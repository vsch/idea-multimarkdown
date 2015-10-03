/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.INCLUDE_SELF;
import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.MARKDOWN_FILE;

/**
 * Static utilities for resolving resources paths.
 *
 * @author Roger Grantham (https://github.com/grantham)
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.8
 */
public class MultiMarkdownPathResolver {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownPathResolver.class);

    /**
     * Not to be instantiated.
     */
    private MultiMarkdownPathResolver() {
        // no op
    }

    /**
     * Makes a simple attempt to convert the URL into a VirtualFile.
     *
     * @param target url from which a VirtualFile is sought
     * @return VirtualFile or null
     */
    public static VirtualFile findVirtualFile(@NotNull URL target) {
        VirtualFileSystem virtualFileSystem = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
        return virtualFileSystem == null ? null : virtualFileSystem.findFileByPath(target.getFile());
    }

    /**
     * Interprets <var>target</var> as a path relative to the given document.
     *
     * @param document the document
     * @param target   relative path from which a VirtualFile is sought
     * @return VirtualFile or null
     */
    public static VirtualFile resolveRelativePath(@NotNull Document document, @NotNull String target) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        VirtualFile parent = file == null ? null : file.getParent();
        return parent == null ? null : parent.findFileByRelativePath(target);
    }

    /**
     * Interprets <var>target</var> as a class reference.
     *
     * @param project the project to look for files in
     * @param target  from which a VirtualFile is sought
     * @return VirtualFile or null
     */
    public static VirtualFile resolveClassReference(@NotNull final Project project, @NotNull final String target) {
        try {
            if (!DumbService.isDumb(project)) {
                return ApplicationManager.getApplication().runReadAction(new Computable<VirtualFile>() {
                    @Override
                    public VirtualFile compute() {
                        try {
                            final PsiClass classpathResource = JavaPsiFacade.getInstance(project).findClass(target, GlobalSearchScope.projectScope(project));
                            if (classpathResource != null) {
                                return classpathResource.getContainingFile().getVirtualFile();
                            }
                        } catch (NoClassDefFoundError ignored) {
                            // API might not be available on all IntelliJ platform IDEs
                        }
                        return null;
                    }
                });
            }
        } catch (NoClassDefFoundError ignored) {

        }
        return null;
    }

    public static boolean canResolveLink(@NotNull final Project project, @NotNull final Document document, @NotNull final String href) {
        return resolveLink(project, document, href, false, false, false) != null;
    }

    public static Object resolveLink(@NotNull final Project project, @NotNull final Document document, @NotNull final String href) {
        return resolveLink(project, document, href, false, false, false);
    }

    public static boolean isWikiDocument(@NotNull final Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        return file != null && new FilePathInfo(file).isWikiPage();
    }

    public static Object resolveLink(@NotNull final Project project, @NotNull final Document document, @NotNull final String hrefEnc, final boolean openFile, final boolean focusEditor, final boolean searchForOpen) {
        String hrefDec = hrefEnc;
        try {
            hrefDec = URLDecoder.decode(hrefEnc, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int posHash;
        String hash = "";
        if ((posHash = hrefDec.indexOf('#')) > 0) {
            hash = hrefDec.substring(posHash + 1);
            hrefDec = hrefDec.substring(0, posHash);
        }
        final String href = hrefDec;
        final String hashSuffix = hash;

        if (!href.startsWith("http://") && !href.startsWith("ftp://") && !href.startsWith("https://") && !href.startsWith("mailto:")) {
            final Object[] foundFile = { null };
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    VirtualFile virtualTarget = null;

                    if (href.startsWith("file:")) {
                        try {
                            URL target = new URL(href);
                            virtualTarget = findVirtualFile(target);
                        } catch (MalformedURLException e) {
                            //e.printStackTrace();
                        }
                    }

                    // relative path then we can open it.
                    if (virtualTarget == null || !virtualTarget.exists()) {
                        virtualTarget = resolveRelativePath(document, href);
                    }

                    // IMPORTANT: figure out if this should apply only to wiki pages or .md files in general
                    // TODO: add a configuration option for resolving to markdown files if an extension is not provided
                    if (virtualTarget == null) {
                        // if the file has no extension, and a Markdown file exists in the project that has the same
                        FilePathInfo hrefPathInfo = new FilePathInfo(href);
                        if (!hrefPathInfo.hasExt()) {
                            VirtualFile inFile = FileDocumentManager.getInstance().getFile(document);
                            if (inFile != null) {
                                MultiMarkdownFile[] list = MultiMarkdownPlugin.getProjectComponent(project)
                                        .getFileReferenceListQuery()
                                        .matchLinkRefNoExt(href, inFile, project)
                                        .includeSource()
                                        .getMarkdownFiles();

                                if (list.length == 1) {
                                    virtualTarget = list[0].getVirtualFile();
                                }
                            }
                        }
                    }

                    // TODO: add a configuration option for resolving optionally to java classes
                    //if (virtualTarget == null) { // Okay, try as if the link target is a class reference
                    //    virtualTarget = resolveClassReference(project, href);
                    //}

                    foundFile[0] = virtualTarget;
                    if (foundFile[0] != null && openFile) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                FileEditorManager.getInstance(project).openFile((VirtualFile) foundFile[0], focusEditor, searchForOpen);

                                if (hashSuffix.length() > 0) {
                                    // TODO: see if we can resolve the #hashSuffix in the file
                                    //logger.info("got hash suffixed href: " + href + "#" + hashSuffix);
                                }
                            }
                        });
                    }
                }
            };

            Application application = ApplicationManager.getApplication();
            application.runReadAction(runnable);
            return foundFile[0];
        } else {
            if (Desktop.isDesktopSupported()) {
                try {
                    Object foundFile = new URI(hrefEnc);
                    if (openFile) Desktop.getDesktop().browse((URI) foundFile);
                    return foundFile;
                } catch (URISyntaxException ex) {
                    // invalid URI, just log
                    logger.info("URISyntaxException on '" + href + "'" + ex.toString());
                } catch (IOException ex) {
                    logger.info("IOException on '" + href + "'" + ex.toString());
                }
            }
            return null;
        }
    }
}
