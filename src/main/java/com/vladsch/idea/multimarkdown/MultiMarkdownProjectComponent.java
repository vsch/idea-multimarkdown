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
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.openapi.vfs.*;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.util.containers.HashMap;
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

public class MultiMarkdownProjectComponent implements ProjectComponent, VirtualFileListener, ListenerNotifyDelegate<ReferenceChangeListener> {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownProjectComponent.class);

    private final static int LISTENER_ADDED = 0;
    private final static int MISSING_REFS_UPDATED = 1;
    private final static int MISSING_REFS_CHANGED = 2;
    public static final String ALL_NAMESPACES = "";

    private HashMap<String, GithubRepo> githubRepos = null;

    // our listeners that want to know when project files change so they can clear cached references
    private final HashMap<String, ListenerNotifier<ReferenceChangeListener>> notifiers = new HashMap<String, ListenerNotifier<ReferenceChangeListener>>();

    private Project project;
    protected MultiMarkdownGlobalSettingsListener globalSettingsListener;
    protected int refactoringRenameFlags = MultiMarkdownNamedElement.RENAME_NO_FLAGS;

    protected int[] refactoringRenameFlagsStack = new int[10];
    protected int refactoringRenameStack = 0;
    protected boolean needReparseOnDumbModeExit = false;

    private final HashMap<String, HashMap<String, MultiMarkdownNamedElement>> missingLinkNamespaces = new HashMap<String, HashMap<String, MultiMarkdownNamedElement>>();

    public int getRefactoringRenameFlags() {
        return refactoringRenameFlags;
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

    public GithubRepo getGithubRepo(@Nullable String baseDirectoryPath) {
        if (baseDirectoryPath == null) baseDirectoryPath = project.getBasePath();

        if (githubRepos == null) {
            githubRepos = new HashMap<String, GithubRepo>();
        }

        if (!githubRepos.containsKey(baseDirectoryPath)) {
            GithubRepo githubRepo = GithubRepo.getGitHubRepo(baseDirectoryPath, project.getBasePath());
            githubRepos.put(baseDirectoryPath, githubRepo);
        }

        return githubRepos.get(baseDirectoryPath);
    }

    public MultiMarkdownProjectComponent(final Project project) {
        this.project = project;

        // Listen to settings changes
        MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
                updateHighlighters();
            }
        });

        MessageBusConnection connect = getProject().getMessageBus().connect();
        connect.subscribe(RefactoringEventListener.REFACTORING_EVENT_TOPIC, new RefactoringEventListener() {
            @Override
            public void refactoringStarted(@NotNull String refactoringId, @Nullable RefactoringEventData beforeData) {
                // logger.info("refactoring started on " + this.hashCode());
            }

            @Override
            public void refactoringDone(@NotNull String refactoringId, @Nullable RefactoringEventData afterData) {
                //logger.info("refactoring done on " + this.hashCode());
                invalidateAfterRefactoring(ALL_NAMESPACES);
                //logger.info("refactoring done on " + this.hashCode());
            }

            @Override
            public void conflictsDetected(@NotNull String refactoringId, @NotNull RefactoringEventData conflictsData) {
                logger.info("refactoring conflicts on " + this.hashCode());
                invalidateAfterRefactoring(ALL_NAMESPACES);
            }

            @Override
            public void undoRefactoring(@NotNull String refactoringId) {
                // logger.info("refactoring undo on " + this.hashCode());
                invalidateAfterRefactoring(ALL_NAMESPACES);
            }
        });
    }

    protected void notifyListeners(@NotNull String namespace, int notificationType, @Nullable String name) {
        if (ALL_NAMESPACES.equals(namespace)) {
            // all namespaces
            for (ListenerNotifier<ReferenceChangeListener> notifier : notifiers.values()) {
                notifier.notifyListeners(notificationType, name);
            }
        } else if (notifiers.containsKey(namespace)) {
            notifiers.get(namespace).notifyListeners(notificationType, name);

            if (notifiers.containsKey(ALL_NAMESPACES)) {
                notifiers.get(ALL_NAMESPACES).notifyListeners(notificationType, name);
            }
        }
    }

    protected void invalidateAfterRefactoring(@NotNull String namespace) {
        synchronized (missingLinkNamespaces) {
            if (!missingLinkNamespaces.isEmpty()) {
                // logger.info("invalidating after refactoring " + this.hashCode());
                clearMissingLinkElements(namespace);
                notifyListeners(namespace, MISSING_REFS_UPDATED, null);
                //DaemonCodeAnalyzer.getInstance(getProject()).restart(this);
            }
        }
    }

    @NotNull
    public MultiMarkdownNamedElement getMissingLinkElement(@NotNull final MultiMarkdownNamedElement element, @NotNull final String namespace, @NotNull String name) {
        synchronized (missingLinkNamespaces) {
            if (!missingLinkNamespaces.containsKey(namespace)) {
                missingLinkNamespaces.put(namespace, new HashMap<String, MultiMarkdownNamedElement>());
            }

            HashMap<String, MultiMarkdownNamedElement> missingLinks = missingLinkNamespaces.get(namespace);

            // see if this element used to be the one that was referenced by other missing links
            // TODO: this is inefficient, need to optimize
            for (String key : missingLinks.keySet()) {
                if (!key.equals(name) && missingLinks.get(key) == element) {
                    // yes it has, we need to invalidate an rebuild
                    logger.info("Invalidating " + namespace + key + " element " + element + " was root");
                    missingLinks.remove(key);
                    notifyListeners(namespace, MISSING_REFS_CHANGED, key);
                    break;
                }
            }

            if (!missingLinks.containsKey(name)) {
                //logger.info("adding missing element " + element);
                missingLinks.put(name, element);
                return element;
            }

            //logger.info("returning missing ref for element " + element + " to " + missingLinks.get(name));
            return missingLinks.get(name);
        }
    }

    protected void clearMissingLinkElements(@NotNull String namespace) {
        synchronized (missingLinkNamespaces) {
            //logger.info("invalidatingMissingLinks on " + this.hashCode());
            if (ALL_NAMESPACES.equals(namespace)) missingLinkNamespaces.clear();
            else {
                if (missingLinkNamespaces.containsKey(namespace)) {
                    missingLinkNamespaces.get(namespace).clear();
                }
            }
        }
    }

    @Override
    public void notify(com.vladsch.idea.multimarkdown.util.ReferenceChangeListener listener, Object... params) {
        if (params.length >= 1) {
            String name = params.length > 1 ? (String) params[1] : null;
            switch ((Integer) params[0]) {
                case LISTENER_ADDED:
                    break;

                case MISSING_REFS_CHANGED:
                case MISSING_REFS_UPDATED:
                    listener.referencesChanged(name);
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

            final MultiMarkdownFile[] markdownFiles = new FileReferenceListQuery(project).markdownFiles();

            // allow references to invalidate their cached values
            invalidateAfterRefactoring(ALL_NAMESPACES);
            //FileReferenceList fileList = new FileReferenceListQuery(project).all();
            //int[] counts = fileList.countByFilter(FileReferenceList.IMAGE_FILE_FILTER, FileReferenceList.MARKDOWN_FILE_FILTER, FileReferenceList.WIKIPAGE_FILE_FILTER);
            //logger.info(String.format("Updated file list: projectRefs[%d],  imageRefs[%d],  markdownRefs[%d], wikiRefs[%d]", fileList.size(), counts[0], counts[1], counts[2]));

            DaemonCodeAnalyzer instance = DaemonCodeAnalyzer.getInstance(project);
            for (MultiMarkdownFile markdownFile : markdownFiles) {
                instance.restart(markdownFile);
            }
        }
    }

    protected void updateHighlighters() {
        // project files have changed so we need to update the lists and then reparse for link validation
        // We get a call back when all have been updated.
        if (project.isDisposed()) return;
        reparseMarkdown();
    }

    public void addListener(@Nullable String namespace, @NotNull ReferenceChangeListener listener) {
        if (!notifiers.containsKey(namespace)) {
            notifiers.put(namespace, new ListenerNotifier<ReferenceChangeListener>(this));
        }
        notifiers.get(namespace).addListener(listener, LISTENER_ADDED);
    }

    public void removeListener(@NotNull String namespace, @NotNull ReferenceChangeListener listener) {
        if (notifiers.containsKey(namespace)) {
            notifiers.get(namespace).removeListener(listener);
        }
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
                githubRepos = null;
            }
        });

        messageBus.connect().subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED_IN_PLUGIN, new VcsListener() {
            @Override
            public void directoryMappingChanged() {
                githubRepos = null;
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
