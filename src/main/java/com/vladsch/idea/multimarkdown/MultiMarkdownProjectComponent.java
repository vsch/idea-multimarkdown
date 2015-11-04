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

import com.intellij.ProjectTopics;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.util.containers.HashMap;
import com.intellij.util.containers.HashSet;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.util.GitHubRepo;
import com.vladsch.idea.multimarkdown.util.ListenerNotifier;
import com.vladsch.idea.multimarkdown.util.ListenerNotifyDelegate;
import com.vladsch.idea.multimarkdown.util.ReferenceChangeListener;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MultiMarkdownProjectComponent implements ProjectComponent, VirtualFileListener, ListenerNotifyDelegate<ReferenceChangeListener> {
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

    private final HashMap<String, ElementNamespace> elementNamespaces = new HashMap<String, ElementNamespace>();
    private final ListenerNotifier<ReferenceChangeListener> allNamespacesNotifier = new ListenerNotifier<ReferenceChangeListener>(this);
    private boolean needAllSpacesNotification;

    private class ElementNamespace {
        final String namespace;
        final HashMap<String, MultiMarkdownNamedElement> symbolTable = new HashMap<String, MultiMarkdownNamedElement>();
        final ListenerNotifier<ReferenceChangeListener> notifier = new ListenerNotifier<ReferenceChangeListener>(MultiMarkdownProjectComponent.this);
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
                notifier.notifyListeners(SYMBOL_REF_CHANGED, oldName);
                needAllSpacesNotification = true;
            }

            return refElement;
        }

        void notifyRefsInvalidated() {
            notifier.notifyListeners(SYMBOL_REF_CHANGED);
        }

        void addListener(ReferenceChangeListener listener) {
            notifier.addListener(listener, LISTENER_ADDED);
        }

        public void removeListener(ReferenceChangeListener listener) {
            notifier.removeListener(listener);
        }
    }

    @NotNull
    public MultiMarkdownNamedElement getMissingLinkElement(@NotNull final MultiMarkdownNamedElement element, @NotNull final String namespace, @NotNull String name) {
        ElementNamespace elementNamespace;
        synchronized (elementNamespaces) {
            if (!elementNamespaces.containsKey(namespace)) {
                elementNamespaces.put(namespace, elementNamespace = new ElementNamespace(namespace));
            } else {
                elementNamespace = elementNamespaces.get(namespace);
            }
        }

        MultiMarkdownNamedElement symbol = elementNamespace.getSymbol(element, name);
        if (needAllSpacesNotification) allNamespacesNotifier.notifyListeners(MultiMarkdownProjectComponent.SYMBOL_REF_CHANGED);
        return symbol;
    }

    @Override
    public void notify(com.vladsch.idea.multimarkdown.util.ReferenceChangeListener listener, Object... params) {
        if (params.length >= 1) {
            String name = params.length > 1 ? (String) params[1] : null;
            switch ((Integer) params[0]) {
                case LISTENER_ADDED:
                    break;

                case SYMBOL_REF_CHANGED:
                    listener.referenceChanged(name);
                    break;

                default:
                    break;
            }
        }
    }

    protected void reparseMarkdown() {
        if (!project.isDisposed()) {
            if (DumbService.isDumb(project)) {
                needReparseOnDumbModeExit = true;
                return;
            }

            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        @Override
                        public void run() {
                            Collection<MultiMarkdownFile> markdownFiles;
                            HashSet<MultiMarkdownFile> markdownFileHash = new HashSet<MultiMarkdownFile>();

                            // reparse all open markdown editors
                            VirtualFile[] files = FileEditorManager.getInstance(project).getOpenFiles();
                            PsiManager psiManager = PsiManager.getInstance(project);

                            for (VirtualFile file : files) {
                                PsiFile psiFile = psiManager.findFile(file);
                                if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                                    markdownFileHash.add((MultiMarkdownFile) psiFile);
                                }
                            }
                            markdownFiles = markdownFileHash;

                            clearNamespaces();

                            DaemonCodeAnalyzer instance = DaemonCodeAnalyzer.getInstance(project);
                            for (MultiMarkdownFile markdownFile : markdownFiles) {
                                instance.restart(markdownFile);
                            }

                            //FileReferenceList fileList = new FileReferenceListQuery(project).all();
                            //int[] counts = fileList.countByFilter(FileReferenceList.IMAGE_FILE_FILTER, FileReferenceList.MARKDOWN_FILE_FILTER, FileReferenceList.WIKIPAGE_FILE_FILTER);
                            //logger.info(String.format("Updated file list: projectRefs[%d],  imageRefs[%d],  markdownRefs[%d], wikiRefs[%d]", fileList.size(), counts[0], counts[1], counts[2]));
                        }
                    });
                }
            });
        }
    }
    private void clearNamespaces() {
        for (ElementNamespace elementNamespace : elementNamespaces.values()) {
            elementNamespace.notifyRefsInvalidated();
        }
        allNamespacesNotifier.notifyListeners(SYMBOL_REF_CHANGED);
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
        allNamespacesNotifier.addListener(listener, LISTENER_ADDED);
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
                reparseMarkdown();
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
                    needReparseOnDumbModeExit = false;
                    reparseMarkdown();
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
                //notifyMissingLinks(ALL_NAMESPACES);
                //logger.info("refactoring done on " + this.hashCode());
            }

            @Override
            public void conflictsDetected(@NotNull String refactoringId, @NotNull RefactoringEventData conflictsData) {
                //logger.info("refactoring conflicts on " + this.hashCode());
                //notifyMissingLinks(ALL_NAMESPACES);
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
        logger.info("isUnderVcs " + (!fileStatus) + " for file " + virtualFile + " status " + status);
        return !fileStatus;
    }

    // TODO: detect extension change in a file and attach our editors if possible
    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        reparseMarkdown();
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        //reparseMarkdown();
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        reparseMarkdown();
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        reparseMarkdown();
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        reparseMarkdown();
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        reparseMarkdown();
    }

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {
        String s = event.getPropertyName();
        //int tmp = 0;
    }

    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent event) {
        //int tmp = 0;
    }

    // return the files this name from inFile can refer to, wikiPagesOnly is set if the name is a wiki link
    // name could be a wiki page ref or a link -
    // search type could be markdownFileRefs, wikiFiles, imageFileRefs

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
        //int tmp = 0;
    }

    @Override
    public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {
        //int tmp = 0;
    }

    public void projectOpened() {
        // TODO: is this still needed?
        VirtualFileManager.getInstance().addVirtualFileListener(this);
        boolean initialized = project.isInitialized();

        final MessageBus messageBus = project.getMessageBus();

        // TODO: is this still needed?
        messageBus.connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
            public void rootsChanged(ModuleRootEvent event) {
                if (project.isDisposed()) return;
                reparseMarkdown();
            }
        });

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
        VirtualFileManager.getInstance().removeVirtualFileListener(this);
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return this.getClass().getName();
    }

    public void initComponent() {
        // get the file list updated
        //StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
        //    public void run() {
        //        /* initialization code */
        //        mainFileList.updateCache();
        //    }
        //});
        //int tmp = 0;
        //MessageBusConnection connect = project.getMessageBus().connect();
        //connect.subscribe(ProjectLifecycleListener.TOPIC, new ProjectLifecycleListener() {
        //    @Override public void projectComponentsInitialized(Project aProject) {
        //        if (aProject == project) mainFileList.loadList(project);
        //    }
        //
        //    @Override public void beforeProjectLoaded(@NotNull Project project) {
        //
        //    }
        //
        //    @Override public void afterProjectClosed(@NotNull Project project) {
        //
        //    }
        //});
    }

    public void disposeComponent() {
    }
}
