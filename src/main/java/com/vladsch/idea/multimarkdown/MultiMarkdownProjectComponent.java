/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vladsch.idea.multimarkdown;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.util.FileContentUtil;
import com.intellij.util.containers.HashMap;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.util.GitHubRepo;
import com.vladsch.idea.multimarkdown.util.ListenerNotifier;
import com.vladsch.idea.multimarkdown.util.ReferenceChangeListener;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class MultiMarkdownProjectComponent implements ProjectComponent {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownProjectComponent.class);

    private final static int LISTENER_ADDED = 0;
    private final static int SYMBOL_REF_CHANGED = 1;

    private HashMap<String, GitHubRepo> gitHubRepos = null;

    private Project project;
    protected MultiMarkdownGlobalSettingsListener globalSettingsListener;
    protected int refactoringRenameFlags = MultiMarkdownNamedElement.RENAME_NO_FLAGS;

    protected int[] refactoringRenameFlagsStack = new int[10];
    protected int refactoringRenameStack = 0;
    protected boolean needReparseOnDumbModeExit = false;
    protected boolean needReparsePsiOnDumbModeExit = false;

    private final HashMap<String, ElementNamespace> elementNamespaces = new HashMap<String, ElementNamespace>();
    private final ListenerNotifier<ReferenceChangeListener> allNamespacesNotifier = new ListenerNotifier<ReferenceChangeListener>();
    private boolean needAllSpacesNotification;

    private class ElementNamespace {
        final String namespace;
        final HashMap<String, MultiMarkdownNamedElement> symbolTable = new HashMap<String, MultiMarkdownNamedElement>();
        final ListenerNotifier<ReferenceChangeListener> notifier = new ListenerNotifier<ReferenceChangeListener>();
        final HashMap<MultiMarkdownNamedElement, String> rootElements = new HashMap<MultiMarkdownNamedElement, String>();

        public ElementNamespace(String namespace) {
            this.namespace = namespace;
        }

        MultiMarkdownNamedElement getSymbol(@NotNull MultiMarkdownNamedElement element, @NotNull String name) {
            String oldName = null;
            boolean log = false;
            MultiMarkdownNamedElement refElement = element;

            synchronized (this) {
                if (rootElements.containsKey(element)) {
                    if (!rootElements.get(element).equals(name)) {
                        // root element's name changed, inform listeners that they need to remap references
                        oldName = rootElements.get(element);
                        if (log) logger.info("root element " + element + " renamed from '" + oldName + "' to '" + name + "'");
                        symbolTable.remove(oldName);
                        if (!symbolTable.containsKey(name)) {
                            // still root element but under a new name
                            symbolTable.put(name, element);
                            rootElements.put(element, name);
                            if (log) logger.info(" old root " + element + " now under new name");
                        } else {
                            // no longer root element
                            rootElements.remove(element);
                            refElement = symbolTable.get(name);
                            if (log) logger.info("removed old root element " + element + " now referencing " + refElement);
                        }
                    }
                } else {
                    if (!symbolTable.containsKey(name)) {
                        // new root element
                        symbolTable.put(name, element);
                        rootElements.put(element, name);
                        //logger.info("new root for " + namespace + " element " + element);
                    } else {
                        refElement = symbolTable.get(name);
                        assert refElement != element;
                    }
                }
            }

            if (oldName != null) {
                // do the notifications that the reference symbol for oldName has changed
                if (log) logger.info("notifiying listeners of " + namespace + " ref changed to '" + oldName + "'");
                final String finalOldName = oldName;
                notifier.notifyListeners(new ListenerNotifier.RunnableNotifier<ReferenceChangeListener>() {
                    @Override
                    public boolean notify(ReferenceChangeListener listener) {
                        listener.referenceChanged(finalOldName);
                        return false;
                    }
                });
                needAllSpacesNotification = true;
            }

            return refElement;
        }

        void notifyRefsInvalidated() {
            notifier.notifyListeners(new ListenerNotifier.RunnableNotifier<ReferenceChangeListener>() {
                @Override
                public boolean notify(ReferenceChangeListener listener) {
                    listener.referenceChanged(null);
                    return false;
                }
            });
        }

        void addListener(ReferenceChangeListener listener) {
            notifier.addListener(listener);
        }

        public void removeListener(ReferenceChangeListener listener) {
            notifier.removeListener(listener);
        }
    }

    @NotNull
    public MultiMarkdownNamedElement getMissingLinkElement(@NotNull final MultiMarkdownNamedElement element, @NotNull final String namespace, @NotNull final String name) {
        ElementNamespace elementNamespace;
        synchronized (elementNamespaces) {
            if (!elementNamespaces.containsKey(namespace)) {
                elementNamespaces.put(namespace, elementNamespace = new ElementNamespace(namespace));
            } else {
                elementNamespace = elementNamespaces.get(namespace);
            }
        }

        MultiMarkdownNamedElement symbol = elementNamespace.getSymbol(element, name);

        if (needAllSpacesNotification) allNamespacesNotifier.notifyListeners(new ListenerNotifier.RunnableNotifier<ReferenceChangeListener>() {
            @Override
            public boolean notify(ReferenceChangeListener listener) {
                listener.referenceChanged(name);
                return false;
            }
        });

        return symbol;
    }

    protected void reparseMarkdown() {
        reparseMarkdown(false);
    }

    protected void reparseMarkdown(final boolean reparseFilePsi) {
        if (!project.isDisposed()) {
            if (DumbService.isDumb(project)) {
                needReparseOnDumbModeExit = true;
                needReparsePsiOnDumbModeExit = reparseFilePsi;
                return;
            }

            // reparse all open markdown editors
            VirtualFile[] files = FileEditorManager.getInstance(project).getOpenFiles();

            clearNamespaces();

            PsiManager psiManager = PsiManager.getInstance(project);
            if (reparseFilePsi) {
                ArrayList<VirtualFile> fileList = new ArrayList<VirtualFile>(files.length);

                for (VirtualFile file : files) {
                    PsiFile psiFile = psiManager.findFile(file);
                    if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                        fileList.add(file);
                    }
                }
                FileContentUtil.reparseFiles(fileList);
            } else {
                DaemonCodeAnalyzer instance = DaemonCodeAnalyzer.getInstance(project);
                for (VirtualFile file : files) {
                    PsiFile psiFile = psiManager.findFile(file);
                    if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                        instance.restart(psiFile);
                    }
                }
            }
        }
    }

    private void clearNamespaces() {
        for (ElementNamespace elementNamespace : elementNamespaces.values()) {
            elementNamespace.notifyRefsInvalidated();
        }
        allNamespacesNotifier.notifyListeners(new ListenerNotifier.RunnableNotifier<ReferenceChangeListener>() {
            @Override
            public boolean notify(ReferenceChangeListener listener) {
                listener.referenceChanged(null);
                return false;
            }
        });
        elementNamespaces.clear();
    }

    public void addListener(@NotNull String namespace, @NotNull ReferenceChangeListener listener) {
        ElementNamespace elementNamespace;

        synchronized (elementNamespaces) {
            if (!elementNamespaces.containsKey(namespace)) {
                elementNamespaces.put(namespace, elementNamespace = new ElementNamespace(namespace));
            } else {
                elementNamespace = elementNamespaces.get(namespace);
            }
        }

        elementNamespace.addListener(listener);
    }

    public void removeListener(@NotNull String namespace, @NotNull ReferenceChangeListener listener) {
        if (elementNamespaces.containsKey(namespace)) {
            elementNamespaces.get(namespace).removeListener(listener);
        }
    }

    public void addListener(@NotNull ReferenceChangeListener listener) {
        allNamespacesNotifier.addListener(listener);
    }

    public void removeListener(@NotNull ReferenceChangeListener listener) {
        allNamespacesNotifier.removeListener(listener);
    }

    public int getRefactoringRenameFlags() {
        return refactoringRenameFlags;
    }

    public int getRefactoringRenameFlags(int defaultFlags) {
        return refactoringRenameFlags == MultiMarkdownNamedElement.RENAME_NO_FLAGS ? defaultFlags : refactoringRenameFlags;
    }

    public void pushRefactoringRenameFlags(int refactoringReason) {
        this.refactoringRenameFlagsStack[refactoringRenameStack++] = this.refactoringRenameFlags;
        this.refactoringRenameFlags = refactoringReason;
    }

    public void popRefactoringRenameFlags() {
        if (refactoringRenameStack > 0) {
            refactoringRenameFlags = refactoringRenameFlagsStack[--refactoringRenameStack];
        }
    }

    public GitHubRepo getGithubRepo(@Nullable String baseDirectoryPath) {
        if (baseDirectoryPath == null) baseDirectoryPath = project.getBasePath();

        if (gitHubRepos == null) {
            gitHubRepos = new HashMap<String, GitHubRepo>();
        }

        if (!gitHubRepos.containsKey(baseDirectoryPath)) {
            GitHubRepo gitHubRepo = GitHubRepo.getGitHubRepo(baseDirectoryPath, project.getBasePath());
            gitHubRepos.put(baseDirectoryPath, gitHubRepo);
        }

        return gitHubRepos.get(baseDirectoryPath);
    }

    public MultiMarkdownProjectComponent(final Project project) {
        this.project = project;

        // Listen to settings changes
        MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
                reparseMarkdown(true);
            }
        });

        project.getMessageBus().connect(project).subscribe(DumbService.DUMB_MODE, new DumbService.DumbModeListener() {
            @Override
            public void enteredDumbMode() {
            }

            @Override
            public void exitDumbMode() {
                // need to re-evaluate class link accessibility
                if (project.isDisposed()) return;

                if (needReparseOnDumbModeExit) {
                    final boolean reparseFilePsi = needReparsePsiOnDumbModeExit;
                    needReparseOnDumbModeExit = false;
                    needReparsePsiOnDumbModeExit = false;
                    reparseMarkdown(reparseFilePsi);
                }
            }
        });

        MessageBusConnection connect = getProject().getMessageBus().connect();
        connect.subscribe(RefactoringEventListener.REFACTORING_EVENT_TOPIC, new RefactoringEventListener() {
            @Override
            public void refactoringStarted(@NotNull String refactoringId, @Nullable RefactoringEventData beforeData) {
                //logger.info("refactoring started on " + this.hashCode());
            }

            @Override
            public void refactoringDone(@NotNull String refactoringId, @Nullable RefactoringEventData afterData) {
                //logger.info("refactoring done on " + this.hashCode());
            }

            @Override
            public void conflictsDetected(@NotNull String refactoringId, @NotNull RefactoringEventData conflictsData) {
                //logger.info("refactoring conflicts on " + this.hashCode());
            }

            @Override
            public void undoRefactoring(@NotNull String refactoringId) {
                //logger.info("refactoring undo on " + this.hashCode());
                clearNamespaces();
            }
        });
    }

    public Project getProject() {
        return project;
    }

    public boolean isUnderVcs(VirtualFile virtualFile) {
        FileStatus status = FileStatusManager.getInstance(project).getStatus(virtualFile);
        String id = status.getId();
        boolean fileStatus = status.equals(FileStatus.DELETED) || status.equals(FileStatus.ADDED) || status.equals(FileStatus.UNKNOWN) || status.equals(FileStatus.IGNORED)
                || id.startsWith("IGNORE");
        //logger.info("isUnderVcs " + (!fileStatus) + " for file " + virtualFile + " status " + status);
        return !fileStatus;
    }

    public void projectOpened() {
        final MessageBus messageBus = project.getMessageBus();

        messageBus.connect().subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, new VcsListener() {
            @Override
            public void directoryMappingChanged() {
                gitHubRepos = null;
            }
        });

        messageBus.connect().subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED_IN_PLUGIN, new VcsListener() {
            @Override
            public void directoryMappingChanged() {
                gitHubRepos = null;
            }
        });
    }

    public void projectClosed() {

    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return this.getClass().getName();
    }

    public void initComponent() {

    }

    public void disposeComponent() {

    }
}
