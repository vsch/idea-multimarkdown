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
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

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

    public MultiMarkdownProjectComponent(final Project project) {
        this.project = project;
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

    public FileReferenceListQuery getFileReferenceListQuery() {
        return new FileReferenceListQuery(getFileReferenceList());
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

        @Override
        public void afterCacheUpdate(Object... params) {
            // all threads have updated lists, invalidate references to files and restart parsing to the links are updated for validity

            // we just schedule a later run in the dispatch thread
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

        public void updateCache(final ThreadSafeMainCache.CacheUpdater<FileReferenceList> notifyWhenDone, final Object... params) {
            final FileReferenceList.Builder builder = new FileReferenceList.Builder();

            final Project project = projectComponent.project;
            final ProjectFileIndex projectFileIndex = ProjectFileIndex.SERVICE.getInstance(project);

            // run the list update in a separate thread
            if (project.isDisposed()) return;

            Executors.newCachedThreadPool().submit(new Runnable() {
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

                                    // only add the file only if it is part of the project source or under a .wiki parent
                                    if (projectFileIndex.isExcluded(file) || projectFileIndex.isInLibrarySource(file)) {
                                        // skip this one
                                        return false;
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
                            logger.info(String.format("Updated file list: scanned[%d], images[%d], cached:  projectRefs[%d],  imageRefs[%d],  markdownRefs[%d], wikiRefs[%d]", scanned[0], scanned[1], fileList.getFileReferences().length, counts[0], counts[1], counts[2]));
                        }
                    });
                }
            });
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

    ///*
    //
    //Crap below is to be moved out into separate implementation class(es)
    //
    // */
    //
    //// call this function with the file name without an extension
    //@Nullable
    //public static String fileNameToWikiRef(@Nullable String fileName) {
    //    return fileNameToWikiRef(fileName, true);
    //}
    //
    //@Nullable
    //public static String fileNameToWikiRef(@Nullable String fileName, boolean removeExtension) {
    //    if (fileName == null) return null;
    //    int pathPos = fileName.lastIndexOf('/');
    //    if (pathPos < 0) pathPos = 0;
    //    int dotPos;
    //    int endPos = !removeExtension ? fileName.length() :
    //            (dotPos = fileName.substring(pathPos).lastIndexOf('.')) < 0 ? dotPos : dotPos + pathPos;
    //    if (endPos <= 0) endPos = fileName.length();
    //    return fileName.substring(0, endPos).replace('-', ' ');
    //}
    //
    //public static String wikiPageRefToFileName(String name) {
    //    return wikiPageRefToFileName(name, false);
    //}
    //
    //@Nullable
    //public static String wikiPageRefToFileName(@Nullable String wikiPageRef, boolean addExtension) {
    //    return wikiPageRef == null ? null : wikiPageRef.replace(' ', '-') + (addExtension ? WIKI_PAGE_EXTENSION : "");
    //}
    //
    //public static boolean isWikiPage(VirtualFile file) {
    //    return isWikiPage(file, 0);
    //}
    //
    //public static boolean isWikiPage(VirtualFile file, int searchSettings) {
    //    boolean isWikiFile = false;
    //    if (file != null && file.getFileType() instanceof MultiMarkdownFileType) {
    //        int pos = file.getPath().lastIndexOf(".wiki/");
    //        if (pos >= 0) {
    //            // maybe
    //            if ((searchSettings & SPACE_DASH_EQUIVALENT) != 0 || !file.getPath().substring(pos + 6, file.getPath().length()).contains(" ")) {
    //                isWikiFile = true;
    //            }
    //        }
    //    }
    //    return isWikiFile;
    //}
    //
    //public static boolean isWikiPage(PsiFile file) {
    //    return isWikiPage(file.getVirtualFile(), 0);
    //}
    //
    //public static boolean isWikiPage(PsiFile file, int searchSettings) {
    //    return isWikiPage(file.getVirtualFile(), searchSettings);
    //}
    //
    //@Nullable
    //public static String getLinkRef(VirtualFile toFile, VirtualFile inFile, int searchFlags) {
    //    boolean wantWikiRef = (searchFlags & WANT_WIKI_REF) != 0;
    //    if (inFile == null) {
    //        return wantWikiRef ? fileNameToWikiRef(toFile.getNameWithoutExtension()) :
    //                ((searchFlags & SPACE_DASH_EQUIVALENT) != 0 ? toFile.getNameWithoutExtension() : toFile.getName());
    //    } else {
    //        String linkRef = wantWikiRef ? toFile.getNameWithoutExtension() :
    //                ((searchFlags & SPACE_DASH_EQUIVALENT) != 0 ? toFile.getNameWithoutExtension() : toFile.getName());
    //        String path = toFile.getPath();
    //        String sourcePath = inFile.getPath();
    //        String pathPrefix = "";
    //
    //        String[] targetParts = path.split("/");
    //        String[] sourceParts = sourcePath.split("/");
    //        int iMax = Math.min(targetParts.length - 1, sourceParts.length - 1);
    //        int i;
    //        for (i = 1; i < iMax; i++) {
    //            if (!targetParts[i].equals(sourceParts[i])) break;
    //        }
    //
    //        // used up the common prefix, now for every source we need to add ../, if we hit the parent.wiki directory
    //        // then it cannot possibly we a proper reference. All wiki pages have to stay withing the parent wiki directory
    //        iMax = sourceParts.length - 1;
    //        for (int j = i; j < iMax; j++) {
    //            if ((searchFlags & ALLOW_INACCESSIBLE_WIKI_REF) == 0 && wantWikiRef && sourceParts[j].endsWith(".wiki")) {
    //                return null;
    //            }
    //
    //            pathPrefix += "../";
    //        }
    //
    //        // used up the common prefix, now for every target we need to add the part/
    //        // but if we encounter one with .wiki extension then it is not wiki page accessible
    //        iMax = targetParts.length - 1;
    //        for (; i < iMax; i++) {
    //            if ((searchFlags & ALLOW_INACCESSIBLE_WIKI_REF) == 0 && wantWikiRef && targetParts[i].endsWith(".wiki")) {
    //                return null;
    //            }
    //
    //            pathPrefix += targetParts[i] + "/";
    //        }
    //
    //        linkRef = pathPrefix + linkRef;
    //        return wantWikiRef ? fileNameToWikiRef(linkRef, false) : linkRef;
    //    }
    //}
    //
    //public static double getLinkDistance(@NotNull String linkRef) {
    //    String[] targetParts = linkRef.split("/");
    //    int before = 0;
    //    int after = 0;
    //    for (String part : targetParts) {
    //        if (part.equals("..")) {
    //            before++;
    //        } else if (part.equals(".")) {
    //
    //        } else {
    //            after++;
    //        }
    //    }
    //    double scale = (after > 999 ? 10000 : (after > 99 ? 1000 : (after > 9 ? 100 : (after > 0 ? 10 : 1))));
    //    return (double) before + ((double) after) / scale;
    //}
    //
    //@Nullable
    //public static String getWikiPageRef(VirtualFile toFile, VirtualFile inFile) {
    //    return getWikiPageRef(toFile, inFile, 0);
    //}
    //
    //@Nullable
    //public static String getWikiPageRef(VirtualFile toFile, VirtualFile inFile, int searchFlags) {
    //    return getLinkRef(toFile, inFile, searchFlags | WANT_WIKI_REF);
    //}
    //
    //// refLink is either a file name with extension, or a wikiPageRef or null if any will do
    //// if null then as long as inFile can link to toFile return true, otherwise the link text should match what the getLinkRef returns
    //public static boolean isLinkRefToFile(@Nullable String refLink, VirtualFile toFile, VirtualFile inFile, int searchFlags) {
    //    String toFileRefLink = getLinkRef(toFile, inFile, (searchFlags & ~WANT_WIKI_REF) | SPACE_DASH_EQUIVALENT); // we want file name without extension
    //    String refLinkFileName = (searchFlags & WIKI_REF) != 0 ? wikiPageRefToFileName(refLink) : refLink;
    //
    //    if (toFileRefLink != null && (refLink == null || (toFileRefLink.equals(refLinkFileName)))) {
    //        if ((!toFile.getName().contains(" ") || (searchFlags & SPACE_DASH_EQUIVALENT) != 0 && refLinkFileName == null) && ((searchFlags & WIKI_REF) == 0 || refLink == null || !refLink.contains("-"))) {
    //            return true;
    //        }
    //    }
    //
    //    if (refLinkFileName != null && toFileRefLink != null && (searchFlags & SPACE_DASH_EQUIVALENT) != 0 && toFileRefLink.length() == refLinkFileName.length()) {
    //        int iMax = toFileRefLink.length();
    //        for (int i = 0; i < iMax; i++) {
    //            if (!(Character.toLowerCase(toFileRefLink.charAt(i)) == Character.toLowerCase(refLinkFileName.charAt(i)) || (toFileRefLink.charAt(i) == ' ' || toFileRefLink.charAt(i) == '-') && (refLinkFileName.charAt(i) == ' ' || refLinkFileName.charAt(i) == '-'))) {
    //                return false;
    //            }
    //        }
    //        return true;
    //    }
    //
    //    return false;
    //}
    //
    //protected MultiMarkdownFile[] findWikiFiles(boolean wikiPagesOnly) {
    //    return wikiPagesOnly ? mirrorFileList.get().getCache().wikiFiles : mirrorFileList.get().getCache().markdownFileRefs;
    //}
    //
    //protected VirtualFile[] findProjectFiles(boolean imagesOnly) {
    //    return imagesOnly ? mirrorFileList.get().getCache().imageFileRefs : mirrorFileList.get().getCache().projectFiles;
    //}
    //
    //@Nullable
    //public List<VirtualFile> findRefLinkFiles(@Nullable String refLink, VirtualFile inFile, int searchFlags) {
    //    ArrayList<VirtualFile> result = null;
    //
    //    if ((searchFlags & (MARKDOWN_FILE_ONLY | WIKIPAGE_FILE)) == 0) {
    //        VirtualFile[] files = findProjectFiles((searchFlags & IMAGE_FILE) != 0);
    //
    //        for (VirtualFile file : files) {
    //            if (((searchFlags & INCLUDE_SELF) != 0 || inFile == null || !inFile.getPath().equals(file.getPath())) && isLinkRefToFile(refLink, file, inFile, searchFlags)) {
    //                if (result == null) result = new ArrayList<VirtualFile>();
    //                result.add(file);
    //            }
    //        }
    //    } else {
    //        MultiMarkdownFile[] files = findWikiFiles((searchFlags & MARKDOWN_FILE_ONLY) == 0);
    //        for (MultiMarkdownFile markdownFile : files) {
    //            VirtualFile file = markdownFile.getVirtualFile();
    //            if (((searchFlags & INCLUDE_SELF) != 0 || inFile == null || !inFile.getPath().equals(file.getPath())) && isLinkRefToFile(refLink, file, inFile, searchFlags)) {
    //                if (result == null) result = new ArrayList<VirtualFile>();
    //                result.add(file);
    //            }
    //        }
    //    }
    //    return result;
    //}
    //
    //@Nullable
    //public List<MultiMarkdownFile> findRefLinkMarkdownFiles(@Nullable String refLink, int searchFlags) {
    //    MultiMarkdownFile psiFile = null;
    //    return findRefLinkMarkdownFiles(refLink, psiFile, searchFlags);
    //}
    //
    //public
    //@Nullable
    //List<MultiMarkdownFile> findRefLinkMarkdownFiles(@Nullable String refLink, @Nullable VirtualFile inFile, int searchFlags) {
    //    MultiMarkdownFile psiFile = null;
    //    if (inFile != null) {
    //        psiFile = (MultiMarkdownFile) PsiManager.getInstance(project).findFile(inFile);
    //    }
    //    return findRefLinkMarkdownFiles(refLink, psiFile, searchFlags);
    //}
    //
    //public
    //@Nullable
    //List<MultiMarkdownFile> findRefLinkMarkdownFiles(@Nullable String refLink, @Nullable MultiMarkdownFile inFile, int searchFlags) {
    //    List<MultiMarkdownFile> result = null;
    //
    //    if ((searchFlags & (MARKDOWN_FILE_ONLY | WIKIPAGE_FILE)) == 0) {
    //        return null;
    //    } else {
    //        MultiMarkdownFile[] files =
    //                (searchFlags & MARKDOWN_FILE_ONLY) == 0 ? findWikiFiles(true) : findWikiFiles(false);
    //        VirtualFile virtualInFile = inFile != null ? inFile.getVirtualFile() : null;
    //
    //        for (MultiMarkdownFile file : files) {
    //            VirtualFile virtualFile = file.getVirtualFile();
    //            if ((searchFlags & INCLUDE_SELF) != 0 || virtualInFile == null || !virtualInFile.getPath().equals(virtualFile.getPath())) {
    //                if (isLinkRefToFile(refLink, virtualFile, virtualInFile, searchFlags)) {
    //                    if (result == null) result = new ArrayList<MultiMarkdownFile>();
    //                    result.add(file);
    //                }
    //            }
    //        }
    //    }
    //    return result;
    //}
}
