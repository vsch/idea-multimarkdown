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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.*;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownProjectComponent implements ProjectComponent, VirtualFileListener, ListenerNotifyDelegate<ProjectFileListListener> {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownProjectComponent.class);

    public final static int ANY_FILE = 0;
    public final static int FILE_REF = 0;

    public final static int WIKIPAGE_FILE = 1;
    public final static int MARKDOWN_FILE_ONLY = 2;
    public final static int IMAGE_FILE = 4;
    public final static int WIKI_REF = 8;
    public final static int ALLOW_INACCESSIBLE_REF = 16;
    public final static int INCLUDE_SELF = 32;
    public final static int WANT_WIKI_REF = 64;
    public final static int SPACE_DASH_EQUIVALENT = 128;
    public final static int MARKDOWN_FILE = MARKDOWN_FILE_ONLY | WIKIPAGE_FILE | ALLOW_INACCESSIBLE_REF;
    public final static int ALLOW_INACCESSIBLE_WIKI_REF = ALLOW_INACCESSIBLE_REF | MARKDOWN_FILE;

    protected final static int LISTENER_ADDED = 0;
    protected final static int UPDATE_DONE = 1;

    private static final String WIKI_PAGE_EXTENSION = ".md";

    // this one is updating when files change, the thread local ones get updated by this one
    private final ThreadSafeMainCache<FileReferenceList> mainFileList = new ThreadSafeMainCache<FileReferenceList>(new MainFileListUpdater(this));
    private ThreadLocal<ThreadSafeMirrorCache<FileReferenceList>> mirrorFileList = loadMarkdownFilesList();

    // our listeners that want to know when project files change so they can clear cached references
    private final ListenerNotifier<ProjectFileListListener> projectFileListNotifier = new ListenerNotifier<ProjectFileListListener>(this);

    private Project project;
    private boolean hadGithubLinks = MultiMarkdownGlobalSettings.getInstance().githubWikiLinks.getValue();
    protected MultiMarkdownGlobalSettingsListener globalSettingsListener;
    protected int refactoringReason;

    public int getRefactoringReason() {
        return refactoringReason;
    }

    public void setRefactoringReason(int refactoringReason) {
        this.refactoringReason = refactoringReason;
    }

    public MultiMarkdownProjectComponent(final Project project) {
        this.project = project;

        // Listen to settings changes
        MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
                if (hadGithubLinks != newSettings.githubWikiLinks.getValue()) {
                    // need to reparse everything
                    hadGithubLinks = newSettings.githubWikiLinks.getValue();
                    updateHighlighters();
                }
            }
        });
    }

    private ThreadLocal<ThreadSafeMirrorCache<FileReferenceList>> loadMarkdownFilesList() {
        return new ThreadLocal<ThreadSafeMirrorCache<FileReferenceList>>() {
            @Override
            protected ThreadSafeMirrorCache<FileReferenceList> initialValue() {
                return new ThreadSafeMirrorCache<FileReferenceList>(mainFileList);
            }
        };
    }

    @Override
    public void notify(ProjectFileListListener listener, Object... params) {
        if (params.length == 1) {
            switch ((Integer) params[0]) {
                case LISTENER_ADDED:
                    if (mainFileList.cacheIsCurrent()) {
                        listener.projectListsUpdated();
                    }
                    break;

                case UPDATE_DONE:
                    listener.projectListsUpdated();
                    break;

                default:
                    break;
            }
        }
    }

    public void addListener(@NotNull ProjectFileListListener listener) {
        projectFileListNotifier.addListener(listener, LISTENER_ADDED);
    }

    public void removeListener(@NotNull ProjectFileListListener listener) {
        projectFileListNotifier.removeListener(listener);
    }

    public Project getProject() {
        return project;
    }

    public FileReferenceList getFileReferenceList() {
        return mirrorFileList.get().getCache();
    }

    private static class MainFileListUpdater extends ThreadSafeCacheUpdater<FileReferenceList> {
        protected final MultiMarkdownProjectComponent projectComponent;

        public MainFileListUpdater(MultiMarkdownProjectComponent projectComponent) {
            this.projectComponent = projectComponent;
        }

        public FileReferenceList newCache() {
            return new FileReferenceList();
        }

        @Override
        public void beforeCacheUpdate(Object... params) {
            // this cleans up all the threads and forces them to load a fresh cache on access
            projectComponent.mirrorFileList.remove();
        }

        protected void reparseMarkdown() {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final Project project = projectComponent.project;

                    if (!project.isDisposed()) {
                        final MultiMarkdownFile[] fileList = projectComponent.getFileReferenceList().getMarkdownFiles();

                        // allow references to invalidate their cached values
                        projectComponent.projectFileListNotifier.notifyListeners(UPDATE_DONE);

                        DaemonCodeAnalyzer instance = DaemonCodeAnalyzer.getInstance(project);
                        for (MultiMarkdownFile markdownFile : fileList) {
                            instance.restart(markdownFile);
                        }
                    }
                }
            });
        }

        @Override
        public void afterCacheUpdate(Object... params) {
            // all threads have updated lists, invalidate references to files and restart parsing to the links are updated for validity

            // we just schedule a later run in the dispatch thread
            reparseMarkdown();
        }

        public void updateCache(final ThreadSafeMainCache.CacheUpdater<FileReferenceList> notifyWhenDone, final Object... params) {
            final FileReferenceList.Builder builder = new FileReferenceList.Builder();

            final Project project = projectComponent.project;
            final ProjectFileIndex projectFileIndex = ProjectFileIndex.SERVICE.getInstance(project);

            // run the list update in a separate thread
            if (project.isDisposed()) return;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // run the file gathering in a read action
                    if (project.isDisposed()) return;

                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        @Override
                        public void run() {
                            if (project.isDisposed()) return;

                            VirtualFile baseDir = project.getBaseDir();
                            final int[] scanned = new int[2];
                            VfsUtilCore.visitChildrenRecursively(baseDir, new VirtualFileVisitor() {
                                @Override
                                public boolean visitFile(@NotNull VirtualFile file) {
                                    if (project.isDisposed()) return false;

                                    scanned[0]++;

                                    // these don't exist in 133.1711
                                    try {
                                        // only add the file only if it is part of the project source or under a .wiki parent
                                        if (projectFileIndex.isExcluded(file) || projectFileIndex.isInLibrarySource(file)) {
                                            // skip this one
                                            return false;
                                        }
                                    } catch (NoSuchMethodError ignored) {
                                    }

                                    if (projectFileIndex.isInSource(file)) {
                                        //projectFiles.add(file);
                                        FileReference fileReference = new FileReference(file, project);
                                        builder.add(fileReference);
                                    }

                                    return super.visitFile(file);
                                }
                            });

                            if (project.isDisposed()) return;

                            final FileReferenceList fileList = new FileReferenceList(builder);

                            notifyWhenDone.cacheUpdated(fileList);

                            int[] counts = fileList.countByFilter(FileReferenceList.IMAGE_FILE_FILTER, FileReferenceList.MARKDOWN_FILE_FILTER, FileReferenceList.WIKIPAGE_FILE_FILTER);
                            logger.info(String.format("Updated file list: scanned[%d] cached:  projectRefs[%d],  imageRefs[%d],  markdownRefs[%d], wikiRefs[%d]", scanned[0], fileList.getFileReferences().length, counts[0], counts[1], counts[2]));
                        }
                    });
                }
            }).start();
        }
    }

    protected void updateHighlighters() {
        // project files have changed so we need to update the lists and then reparse for link validation
        // We get a call back when all have been updated.
        if (project.isDisposed()) return;

        mainFileList.updateCache();
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
        VirtualFileManager.getInstance().addVirtualFileListener(this);
        boolean initialized = project.isInitialized();

        project.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
            public void rootsChanged(ModuleRootEvent event) {
                if (project.isDisposed()) return;

                mainFileList.updateCache();
            }
        });
    }

    public void projectClosed() {
        VirtualFileManager.getInstance().removeVirtualFileListener(this);
        mirrorFileList.remove();     // remove the cached file list
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return this.getClass().getName();
    }

    public void initComponent() {
        // get the file list updated
        StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
            public void run() {
                /* initialization code */
                mainFileList.updateCache();
            }
        });
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
