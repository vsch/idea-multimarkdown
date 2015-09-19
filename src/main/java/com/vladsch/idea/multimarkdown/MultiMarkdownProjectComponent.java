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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Executors;

public class MultiMarkdownProjectComponent implements ProjectComponent, VirtualFileListener {
    public static int ANY_FILE = 0;
    public static int FILE_REF = 0;

    public static int WIKIPAGE_FILE = 1;
    public static int MARKDOWN_FILE_ONLY = 2;
    public static int IMAGE_FILE = 4;
    public static int WIKI_REF = 8;
    public static int ALLOW_INACCESSIBLE_REF = 16;
    public static int INCLUDE_SELF = 32;
    public static int WANT_WIKI_REF = 64;
    public static int SPACE_DASH_EQUIVALENT = 128;

    public static int MARKDOWN_FILE = MARKDOWN_FILE_ONLY | WIKIPAGE_FILE | ALLOW_INACCESSIBLE_REF;
    public static int ALLOW_INACCESSIBLE_WIKI_REF = ALLOW_INACCESSIBLE_REF | MARKDOWN_FILE;

    private Project project;
    private ThreadLocal<FileList> filesList = loadMarkdownFilesList();

    // this one is updating when files change, the thread local ones get updated by this one
    private final MainFileList projectFileList = new MainFileList();

    private ThreadLocal<FileList> loadMarkdownFilesList() {
        return new ThreadLocal<FileList>() {
            @Override protected FileList initialValue() {
                return new FileList(projectFileList);
            }
        };
    }

    public MultiMarkdownProjectComponent(final Project project) {
        this.project = project;

        projectFileList.addListener(new ListLoadedListener() {
            @Override public void updateLists(FileList updateLists) {

            }

            @Override public void updateDone() {
                // all threads have updated lists, restart parsing to the links are updated for validity
                final MultiMarkdownFile[] markdownFiles = projectFileList.markdownFiles;

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override public void run() {
                        DaemonCodeAnalyzer instance = DaemonCodeAnalyzer.getInstance(project);
                        for (MultiMarkdownFile markdownFile : markdownFiles) {
                            instance.restart(markdownFile);
                        }
                    }
                });
            }
        });
    }

    protected interface ListLoadedListener extends EventListener {
        // use the new lists
        void updateLists(FileList updateLists);

        // all listeners have been updated
        void updateDone();
    }

    protected static class FileList {
        protected VirtualFile[] projectFiles = new VirtualFile[0];
        protected VirtualFile[] imageFiles = new VirtualFile[0];
        protected MultiMarkdownFile[] markdownFiles = new MultiMarkdownFile[0];
        protected MultiMarkdownFile[] wikiFiles = new MultiMarkdownFile[0];

        // this will only be called for the derived MainFileList instances
        FileList() {
        }

        FileList(final MainFileList mainFileList) {
            if (mainFileList != null) {
                mainFileList.addListener(new ListLoadedListener() {
                    @Override public void updateLists(FileList updatedLists) {
                        projectFiles = updatedLists.projectFiles;
                        imageFiles = updatedLists.imageFiles;
                        markdownFiles = updatedLists.markdownFiles;
                        wikiFiles = updatedLists.wikiFiles;
                    }

                    @Override public void updateDone() {

                    }
                });
            }
        }
    }

    private static class MainFileList extends FileList {
        private boolean isListLoaded = false;
        private boolean isListLoading = false;

        protected final Set<WeakReference<ListLoadedListener>> listeners = new HashSet<WeakReference<ListLoadedListener>>();

        public MainFileList(MainFileList mainFileList) {
            this();
        }

        public MainFileList() {
            super();
        }

        public void addListener(@NotNull final ListLoadedListener listener) {
            synchronized (listeners) {
                removeListener(listener);
                listeners.add(new WeakReference<ListLoadedListener>(listener));

                if (isListLoaded && !isListLoading) {
                    listener.updateLists(this);
                }
            }
        }

        public void removeListener(@NotNull final ListLoadedListener listener) {
            synchronized (listeners) {
                synchronized (listeners) {
                    Object[] listenerList = listeners.toArray(new Object[listeners.size()]);

                    for (final Object listenerObj : listenerList) {
                        WeakReference<ListLoadedListener> listenerRef = (WeakReference<ListLoadedListener>) listenerObj;
                        if (listenerRef.get() == null || listenerRef.get() == listener) {
                            listeners.remove(listenerRef);
                        }
                    }
                }
            }
        }

        public void notifyListeners() {
            ListLoadedListener listener;
            for (final WeakReference<ListLoadedListener> listenerRef : listeners) {
                if ((listener = listenerRef.get()) != null) listener.updateLists(this);
            }
        }

        public void notifyUpdateDone() {
            ListLoadedListener listener;
            for (final WeakReference<ListLoadedListener> listenerRef : listeners) {
                if ((listener = listenerRef.get()) != null) listener.updateDone();
            }
        }

        protected void loadList(final Project project) {
            boolean alreadyLoading = true;

            synchronized (listeners) {
                if (!isListLoading) {
                    isListLoading = true;
                    alreadyLoading = false;
                }
            }

            if (!alreadyLoading) {
                final ArrayList<VirtualFile> projectFiles = new ArrayList<VirtualFile>();
                final ArrayList<VirtualFile> imageFiles = new ArrayList<VirtualFile>();
                final ArrayList<MultiMarkdownFile> markdownFiles = new ArrayList<MultiMarkdownFile>();
                final ArrayList<MultiMarkdownFile> wikiFiles = new ArrayList<MultiMarkdownFile>();
                final MainFileList mainList = this;

                // run the list update in a separate thread
                Executors.newCachedThreadPool().submit(new Runnable() {
                    @Override public void run() {
                        // run the file gathering in a read action
                        ApplicationManager.getApplication().runReadAction(new Runnable() {
                            @Override public void run() {
                                VirtualFile baseDir = project.getBaseDir();
                                VfsUtilCore.visitChildrenRecursively(baseDir, new VirtualFileVisitor() {
                                    @Override public boolean visitFile(@NotNull VirtualFile file) {
                                        // RELEASE: should only add the file only if it is part of the project source or under a .wiki parent
                                        projectFiles.add(file);
                                        if (ImageFileTypeManager.getInstance().isImage(file)) {
                                            imageFiles.add(file);
                                        }

                                        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                                        if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                                            markdownFiles.add((MultiMarkdownFile) psiFile);

                                            if (isWikiPage(psiFile)) {
                                                wikiFiles.add((MultiMarkdownFile) psiFile);
                                            }
                                        }

                                        return super.visitFile(file);
                                    }
                                });

                                synchronized (mainList.listeners) {
                                    mainList.projectFiles = projectFiles.toArray(new VirtualFile[projectFiles.size()]);
                                    mainList.imageFiles = imageFiles.toArray(new VirtualFile[imageFiles.size()]);
                                    mainList.markdownFiles = markdownFiles.toArray(new MultiMarkdownFile[markdownFiles.size()]);
                                    mainList.wikiFiles = wikiFiles.toArray(new MultiMarkdownFile[wikiFiles.size()]);

                                    mainList.isListLoaded = true;
                                    mainList.isListLoading = false;
                                    mainList.notifyListeners();
                                }

                                // now the project can reparse the data
                                mainList.notifyUpdateDone();
                            }
                        });
                    }
                });
            }
        }
    }

    protected MultiMarkdownFile[] findWikiFiles(boolean wikiPagesOnly) {
        return wikiPagesOnly ? filesList.get().wikiFiles : filesList.get().markdownFiles;
    }

    protected VirtualFile[] findProjectFiles(boolean imagesOnly) {
        return imagesOnly ? filesList.get().imageFiles : filesList.get().projectFiles;
    }

    protected void updateHighlighters() {
        // project files have changed so we need to update the lists and then reparse for link validation
        // We get a call back when all have been updated.
        projectFileList.loadList(project);
    }

    // TODO: detect extension change in a file and attach our editors if possible
    @Override public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        updateHighlighters();
    }

    @Override public void contentsChanged(@NotNull VirtualFileEvent event) {
        //updateHighlighters();
    }

    @Override public void fileCreated(@NotNull VirtualFileEvent event) {
        updateHighlighters();
    }

    @Override public void fileDeleted(@NotNull VirtualFileEvent event) {
        updateHighlighters();
    }

    @Override public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        updateHighlighters();
    }

    @Override public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        updateHighlighters();
    }

    @Override public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {
        String s = event.getPropertyName();
        int tmp = 0;
    }

    @Override public void beforeContentsChange(@NotNull VirtualFileEvent event) {
        int tmp = 0;
    }

    @Override public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
        int tmp = 0;
    }

    @Override public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {
        int tmp = 0;
    }

    public void projectOpened() {
        VirtualFileManager.getInstance().addVirtualFileListener(this);
        boolean initialized = project.isInitialized();
    }

    public void projectClosed() {
        VirtualFileManager.getInstance().removeVirtualFileListener(this);
        filesList.remove();     // remove the cached file list
        this.project = null;
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
                projectFileList.loadList(project);
            }
        });
        //int tmp = 0;
        //MessageBusConnection connect = project.getMessageBus().connect();
        //connect.subscribe(ProjectLifecycleListener.TOPIC, new ProjectLifecycleListener() {
        //    @Override public void projectComponentsInitialized(Project aProject) {
        //        if (aProject == project) projectFileList.loadList(project);
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

    // return the files this name from inFile can refer to, wikiPagesOnly is set if the name is a wiki link
    // name could be a wiki page ref or a link -
    // search type could be markdownFiles, wikiFiles, imageFiles

    // call this function with the file name without an extension
    public static @Nullable String fileNameToWikiRef(@Nullable String fileName) {
        return fileName == null ? null : fileName.replace('-', ' ');
    }

    public static String wikiPageRefToFileName(String name) {
        return wikiPageRefToFileName(name, false);
    }

    public static @Nullable String wikiPageRefToFileName(@Nullable String wikiPageRef, boolean addExtension) {
        return wikiPageRef == null ? null : wikiPageRef.replace(' ', '-') + (addExtension ? ".md" : "");
    }

    public static boolean isWikiPage(VirtualFile file) {
        return file != null && file.getPath().contains(".wiki/") && file.getFileType() instanceof MultiMarkdownFileType;
    }

    public static boolean isWikiPage(PsiFile file) {
        return file != null && file.getVirtualFile().getPath().contains(".wiki/") && (file instanceof MultiMarkdownFile);
    }

    public static @Nullable String getLinkRef(VirtualFile toFile, VirtualFile inFile, int searchFlags) {
        boolean wantWikiRef = (searchFlags & WANT_WIKI_REF) != 0;
        if (inFile == null) {
            return wantWikiRef ? fileNameToWikiRef(toFile.getNameWithoutExtension()) : ((searchFlags & SPACE_DASH_EQUIVALENT) != 0 ? toFile.getNameWithoutExtension() : toFile.getName());
        } else {
            String linkRef = wantWikiRef ? toFile.getNameWithoutExtension() : ((searchFlags & SPACE_DASH_EQUIVALENT) != 0 ? toFile.getNameWithoutExtension() : toFile.getName());
            String path = toFile.getPath();
            String sourcePath = inFile.getPath();
            String pathPrefix = "";

            String[] targetParts = path.split("/");
            String[] sourceParts = sourcePath.split("/");
            int iMax = Math.min(targetParts.length - 1, sourceParts.length - 1);
            int i;
            for (i = 1; i < iMax; i++) {
                if (!targetParts[i].equals(sourceParts[i])) break;
            }

            // used up the common prefix, now for every source we need to add ../, if we hit the parent.wiki directory
            // then it cannot possibly we a proper reference. All wiki pages have to stay withing the parent wiki directory
            iMax = sourceParts.length - 1;
            for (int j = i; j < iMax; j++) {
                if ((searchFlags & ALLOW_INACCESSIBLE_WIKI_REF) == 0 && wantWikiRef && sourceParts[j].endsWith(".wiki")) {
                    return null;
                }

                pathPrefix += "../";
            }

            // used up the common prefix, now for every target we need to add the part/
            // but if we encounter one with .wiki extension then it is not wiki page accessible
            iMax = targetParts.length - 1;
            for (; i < iMax; i++) {
                if ((searchFlags & ALLOW_INACCESSIBLE_WIKI_REF) == 0 && wantWikiRef && targetParts[i].endsWith(".wiki")) {
                    return null;
                }

                pathPrefix += targetParts[i] + "/";
            }

            linkRef = pathPrefix + linkRef;
            return wantWikiRef ? fileNameToWikiRef(linkRef) : linkRef;
        }
    }

    public static @Nullable String getWikiPageRef(VirtualFile toFile, VirtualFile inFile) {
        return getWikiPageRef(toFile, inFile, 0);
    }

    public static @Nullable String getWikiPageRef(VirtualFile toFile, VirtualFile inFile, int searchFlags) {
        return getLinkRef(toFile, inFile, searchFlags | WANT_WIKI_REF);
    }

    // refLink is either a file name with extension, or a wikiPageRef or null if any will do
    // if null then as long as inFile can link to toFile return true, otherwise the link text should match what the getLinkRef returns
    public static boolean isLinkRefToFile(@Nullable String refLink, VirtualFile toFile, VirtualFile inFile, int searchFlags) {
        String toFileRefLink = getLinkRef(toFile, inFile, (searchFlags & ~WANT_WIKI_REF) | SPACE_DASH_EQUIVALENT); // we want file name without extension
        String refLinkFileName = (searchFlags & WIKI_REF) != 0 ? wikiPageRefToFileName(refLink) : refLink;

        if (toFileRefLink != null && (refLink == null || (toFileRefLink.equals(refLinkFileName)))) {
            if (!toFile.getName().contains(" ") && ((searchFlags & WIKI_REF) == 0 || refLink == null || !refLink.contains("-"))) {
                return true;
            }
        }

        if (toFileRefLink != null && (searchFlags & SPACE_DASH_EQUIVALENT) != 0 && toFileRefLink.length() == refLinkFileName.length()) {
            int iMax = toFileRefLink.length();
            for (int i = 0; i < iMax; i++) {
                if (!(Character.toLowerCase(toFileRefLink.charAt(i)) == Character.toLowerCase(refLinkFileName.charAt(i))
                        || (toFileRefLink.charAt(i) == ' ' || toFileRefLink.charAt(i) == '-')
                        && (refLinkFileName.charAt(i) == ' ' || refLinkFileName.charAt(i) == '-'))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public @Nullable List<VirtualFile> findRefLinkFiles(@Nullable String refLink, VirtualFile inFile, int searchFlags) {
        ArrayList<VirtualFile> result = null;

        if ((searchFlags & (MARKDOWN_FILE_ONLY | WIKIPAGE_FILE)) == 0) {
            VirtualFile[] files = findProjectFiles((searchFlags & IMAGE_FILE) != 0);

            for (VirtualFile file : files) {
                if (((searchFlags & INCLUDE_SELF) != 0 || inFile == null || !inFile.getPath().equals(file.getPath())) && isLinkRefToFile(refLink, file, inFile, searchFlags)) {
                    if (result == null) result = new ArrayList<VirtualFile>();
                    result.add(file);
                }
            }
        } else {
            MultiMarkdownFile[] files = findWikiFiles((searchFlags & MARKDOWN_FILE_ONLY) == 0);
            for (MultiMarkdownFile markdownFile : files) {
                VirtualFile file = markdownFile.getVirtualFile();
                if (((searchFlags & INCLUDE_SELF) != 0 || inFile == null || !inFile.getPath().equals(file.getPath())) && isLinkRefToFile(refLink, file, inFile, searchFlags)) {
                    if (result == null) result = new ArrayList<VirtualFile>();
                    result.add(file);
                }
            }
        }
        return result;
    }

    public @Nullable List<MultiMarkdownFile> findRefLinkMarkdownFiles(@Nullable String refLink, @Nullable VirtualFile inFile, int searchFlags) {
        MultiMarkdownFile psiFile = null;
        if (inFile != null) {
            psiFile = (MultiMarkdownFile) PsiManager.getInstance(project).findFile(inFile);
        }
        return findRefLinkMarkdownFiles(refLink, psiFile, searchFlags);
    }

    public @Nullable List<MultiMarkdownFile> findRefLinkMarkdownFiles(@Nullable String refLink, @Nullable MultiMarkdownFile inFile, int searchFlags) {
        List<MultiMarkdownFile> result = null;

        if ((searchFlags & (MARKDOWN_FILE_ONLY | WIKIPAGE_FILE)) == 0) {
            return null;
        } else {
            MultiMarkdownFile[] files = (searchFlags & MARKDOWN_FILE_ONLY) == 0 ? findWikiFiles(true) : findWikiFiles(false);
            VirtualFile virtualInFile = inFile != null ? inFile.getVirtualFile() : null;

            for (MultiMarkdownFile file : files) {
                VirtualFile virtualFile = file.getVirtualFile();
                if (((searchFlags & INCLUDE_SELF) != 0 || virtualInFile == null || !virtualInFile.getPath().equals(virtualFile.getPath())) && isLinkRefToFile(refLink, virtualFile, virtualInFile, searchFlags)) {
                    if (result == null) result = new ArrayList<MultiMarkdownFile>();
                    result.add(file);
                }
            }
        }
        return result;
    }
}
