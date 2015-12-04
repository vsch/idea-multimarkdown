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
import com.intellij.util.FileContentUtil;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MultiMarkdownProjectComponent implements ProjectComponent, VirtualFileListener, LinkResolver.ProjectResolver {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownProjectComponent.class);

    private final static int LISTENER_ADDED = 0;
    private final static int SYMBOL_REF_CHANGED = 1;
    public static final Object NULL_VCS_ROOT = new Object();

    private ConcurrentHashMap<String, Object> gitHubRepos = null;

    private Project project;
    protected MultiMarkdownGlobalSettingsListener globalSettingsListener;
    protected int refactoringRenameFlags = MultiMarkdownNamedElement.RENAME_NO_FLAGS;

    protected int[] refactoringRenameFlagsStack = new int[10];
    protected int refactoringRenameStack = 0;
    protected boolean needReparseOnDumbModeExit = false;
    protected boolean needReparsePsiOnDumbModeExit = false;

    private final ConcurrentHashMap<String, ElementNamespace> elementNamespaces = new ConcurrentHashMap<String, ElementNamespace>();
    private final ListenerNotifier<ReferenceChangeListener> allNamespacesNotifier = new ListenerNotifier<ReferenceChangeListener>();
    private boolean needAllSpacesNotification;

    private class ElementNamespace {
        final String namespace;
        final ConcurrentHashMap<String, MultiMarkdownNamedElement> symbolTable = new ConcurrentHashMap<String, MultiMarkdownNamedElement>();
        final ListenerNotifier<ReferenceChangeListener> notifier = new ListenerNotifier<ReferenceChangeListener>();
        final ConcurrentHashMap<MultiMarkdownNamedElement, String> rootElements = new ConcurrentHashMap<MultiMarkdownNamedElement, String>();

        public ElementNamespace(String namespace) {
            this.namespace = namespace;
        }

        MultiMarkdownNamedElement getSymbol(@NotNull MultiMarkdownNamedElement element, @NotNull String name) {
            String oldName = null;
            boolean log = false;
            MultiMarkdownNamedElement refElement = element;

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
                    if (refElement == element) {
                        rootElements.put(element, name);
                        logger.info(namespace + name + "not in rootElements but is root in namespace");
                    }
                }
            }

            if (oldName != null) {
                // do the notifications that the reference symbol for oldName has changed
                if (log) logger.info("notifying listeners of " + namespace + " ref changed to '" + oldName + "'");
                final String finalOldName = oldName;
                notifier.notifyListeners(new ListenerNotifier.RunnableNotifier<ReferenceChangeListener>() {
                    @Override
                    public boolean notify(ReferenceChangeListener listener) {
                        listener.referenceChanged(finalOldName);
                        return false;
                    }
                });

                // TODO: validate that this is not needed, a change of a linkref will cause linkrefs referencing
                // to be invalidated but will not invalidate any other elements that depend on the linkref,
                // like its text and anchor siblings
                //needAllSpacesNotification = true;
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
        MultiMarkdownNamedElement symbol;

        if (!elementNamespaces.containsKey(namespace)) {
            elementNamespaces.put(namespace, elementNamespace = new ElementNamespace(namespace));
        } else {
            elementNamespace = elementNamespaces.get(namespace);
        }
        symbol = elementNamespace.getSymbol(element, name);

        if (needAllSpacesNotification) allNamespacesNotifier.notifyListeners(new ListenerNotifier.RunnableNotifier<ReferenceChangeListener>() {
            @Override
            public boolean notify(ReferenceChangeListener listener) {
                listener.referenceChanged(name);
                return false;
            }
        });

        return symbol;
    }

    private void clearNamespaces() {
        elementNamespaces.clear();

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
    }

    protected void reparseMarkdown() {
        reparseMarkdown(false);
    }

    protected void reparseMarkdown(final boolean reparseFilePsi) {
        boolean log = false;

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
                if (log) logger.info("reparse file psi start");
                FileContentUtil.reparseFiles(fileList);
                if (log) logger.info("reparse file psi end");
            } else {
                if (log) logger.info("reparse open file start");
                DaemonCodeAnalyzer instance = DaemonCodeAnalyzer.getInstance(project);
                for (VirtualFile file : files) {
                    PsiFile psiFile = psiManager.findFile(file);
                    if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                        instance.restart(psiFile);
                    }
                }
                if (log) logger.info("reparse open file end");
            }
        }
    }

    public void addListener(@NotNull String namespace, @NotNull ReferenceChangeListener listener) {
        ElementNamespace elementNamespace;

        if (!elementNamespaces.containsKey(namespace)) {
            elementNamespaces.put(namespace, elementNamespace = new ElementNamespace(namespace));
        } else {
            elementNamespace = elementNamespaces.get(namespace);
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

    @Nullable
    public GitHubVcsRoot getGitHubRepo(@Nullable String baseDirectoryPath) {
        if (project.isDisposed()) return null;

        String projectBasePath = project.getBasePath();
        if (projectBasePath == null) return null;

        if (baseDirectoryPath == null) baseDirectoryPath = projectBasePath;

        if (gitHubRepos == null) {
            gitHubRepos = new ConcurrentHashMap<String, Object>();
        }

        // TODO: optimize this to reduce directory scanning for git config by using the VcsRoots defined in the IDE to find roots and only read config for defined roots
        baseDirectoryPath = StringUtilKt.removeEnd(baseDirectoryPath, '/');
        projectBasePath = StringUtilKt.removeEnd(projectBasePath, '/');
        if (!gitHubRepos.containsKey(baseDirectoryPath)) {
            GitHubVcsRoot gitHubVcsRoot = GitHubVcsRoot.Companion.getGitHubVcsRoot(baseDirectoryPath, projectBasePath);
            // add all intervening directories to point to this repo or null if none was found so we don't search for it again
            String gitRootBaseDir = gitHubVcsRoot == null ? projectBasePath : StringUtilKt.removeEnd(gitHubVcsRoot.getBasePath(), '/');
            PathInfo currentBaseDir = new PathInfo(baseDirectoryPath);
            do {
                //logger.info("getGitHubRepo("+baseDirectoryPath+") : Adding vcsRepoRoot: " + gitHubVcsRoot + " for " + currentBaseDir.getFilePath());
                gitHubRepos.put(currentBaseDir.getFilePath(), gitHubVcsRoot != null ? gitHubVcsRoot : NULL_VCS_ROOT);
                if (currentBaseDir.getFilePath().equals(gitRootBaseDir)) break;
                currentBaseDir = new PathInfo(currentBaseDir.getPath());
            } while (!currentBaseDir.isEmpty() && !currentBaseDir.isRoot());
        }

        Object object = gitHubRepos.get(baseDirectoryPath);
        return object instanceof GitHubVcsRoot ? (GitHubVcsRoot) object : null;
    }

    public boolean isUnderVcs(VirtualFile virtualFile) {
        FileStatus status = FileStatusManager.getInstance(project).getStatus(virtualFile);
        String id = status.getId();
        boolean fileStatus = status.equals(FileStatus.DELETED) || status.equals(FileStatus.ADDED) || status.equals(FileStatus.UNKNOWN) || status.equals(FileStatus.IGNORED)
                || id.startsWith("IGNORE");
        //logger.info("isUnderVcs " + (!fileStatus) + " for file " + virtualFile + " status " + status);
        return !fileStatus;
    }

    @Override
    public boolean isUnderVcs(@NotNull FileRef fileRef) {
        ProjectFileRef projectFileRef = fileRef.projectFileRef(project);
        return projectFileRef != null && isUnderVcs(projectFileRef.getVirtualFile());
    }

    @Nullable
    @Override
    public GitHubVcsRoot getVcsRoot(@NotNull FileRef fileRef) {
        return getGitHubRepo(fileRef.getPath());
    }

    @Nullable
    @Override
    public String vcsRootBase(@NotNull FileRef fileRef) {
        GitHubVcsRoot gitHubVcsRoot = getGitHubRepo(fileRef.getPath());
        return gitHubVcsRoot == null ? null : gitHubVcsRoot.getBasePath();
    }

    @NotNull
    @Override
    public String getProjectBasePath() {
        String basePath = project.isDisposed() ? null : project.getBasePath();
        return basePath != null ? basePath : "";
    }

    @Nullable
    @Override
    public String vcsRepoBasePath(@NotNull FileRef fileRef) {
        GitHubVcsRoot gitHubVcsRoot = getGitHubRepo(fileRef.getPath());
        return gitHubVcsRoot == null ? null : gitHubVcsRoot.getMainRepoBaseDir();
    }

    @Nullable
    @Override
    public List<FileRef> projectFileList(@Nullable List<? extends String> fileTypes) {
        assert false: "Should never be called";
        return null;
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

    @NotNull
    public Project getProject() {
        return project;
    }

    public void projectOpened() {
        VirtualFileManager.getInstance().addVirtualFileListener(this);
        boolean initialized = project.isInitialized();

        final MessageBus messageBus = project.getMessageBus();

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

    }

    public void disposeComponent() {

    }

    protected void updateHighlighters() {
        // project files have changed so we need to update the lists and then reparse for link validation
        // We get a call back when all have been updated.
        if (project.isDisposed()) return;
        reparseMarkdown();
    }

    // TODO: detect extension change in a file and attach our editors if possible
    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        updateHighlighters();
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        //updateHighlighters();
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        updateHighlighters();
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        updateHighlighters();
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        updateHighlighters();
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        updateHighlighters();
    }

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {
        //String s = event.getPropertyName();
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
}
