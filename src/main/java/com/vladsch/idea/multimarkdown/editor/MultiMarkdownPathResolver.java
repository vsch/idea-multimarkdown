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
import com.vladsch.idea.multimarkdown.MultiMarkdownFileTypeFactory;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import com.vladsch.idea.multimarkdown.util.FileReference;
import com.vladsch.idea.multimarkdown.util.GitHubRepo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

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
    @Nullable
    public static String resolveExternalReference(@NotNull Project project, @NotNull Document document, @NotNull String target) {
        FileReference resolvedTarget = null;
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        VirtualFile relativeFile = null;

        if (file != null) {
            FileReference documentFileReference = new FileReference(file.getPath(), project);
            resolvedTarget = documentFileReference.resolveExternalLinkRef(target, true, false);
        }
        return resolvedTarget == null ? null : resolvedTarget.getFilePathWithAnchor();
    }

    /**
     * Interprets <var>target</var> as a path relative to the given document.
     *
     * @param document the document
     * @param target   relative path from which a VirtualFile is sought
     * @return VirtualFile or null
     */
    public static VirtualFile resolveRelativePath(@NotNull Document document, @NotNull String target) {
        VirtualFile relativeFile = null;
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file != null) {
            FileReference documentFileReference = new FileReference(file.getPath());
            VirtualFile parent = file.getParent();
            if (parent != null) {
                FileReference resolvedTarget = documentFileReference.resolveLinkRef(target, false);
                if (resolvedTarget != null && !resolvedTarget.isExternalReference()) {
                    relativeFile = resolvedTarget.getVirtualFile();
                }
                if (relativeFile == null) {
                    resolvedTarget = documentFileReference.resolveLinkRef(target, true);
                    if (resolvedTarget != null && !resolvedTarget.isExternalReference()) {
                        relativeFile = resolvedTarget.getVirtualFile();
                        if (relativeFile == null && resolvedTarget.getExt().isEmpty()) {
                            // try with markdown extensions
                            for (String ext : MultiMarkdownFileTypeFactory.getExtensions()) {
                                FileReference withExt = resolvedTarget.withExt(ext);
                                if (!withExt.isExternalReference()) {
                                    relativeFile = withExt.getVirtualFile();
                                    if (relativeFile != null) break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return relativeFile;
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

    public static boolean canResolveLink(@Nullable final Project project, @NotNull final Document document, @NotNull final String href, boolean resolveLocal) {
        return resolveLink(project, document, href, false, resolveLocal) != null;
    }

    public static boolean isWikiDocument(@NotNull final Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        return file != null && new FilePathInfo(file).isWikiPage();
    }

    public static Object resolveLink(@Nullable final Project project, @NotNull final Document document, @NotNull String hrefEnc, final boolean openFile, final boolean resolveLocal) {
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

        if (!FilePathInfo.isExternalReference(href)) {
            final Object[] foundFile = { null };
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    VirtualFile virtualTarget = null;
                    boolean resolveLocalAnyway = resolveLocal;

                    if (href.startsWith("file:")) {
                        try {
                            URL target = new URL(href);
                            virtualTarget = findVirtualFile(target);
                        } catch (MalformedURLException e) {
                            //e.printStackTrace();
                        }
                    }

                    // relative path then we can open it.
                    if (!resolveLocal && project != null && (virtualTarget == null || !virtualTarget.exists())) {
                        String newHref = resolveExternalReference(project, document, href);
                        // resolve local file if external does not map
                        resolveLocalAnyway = newHref == null || !FilePathInfo.isExternalReference(newHref);
                    }

                    if (resolveLocalAnyway && (virtualTarget == null || !virtualTarget.exists())) {
                        virtualTarget = resolveRelativePath(document, href);
                    }

                    // TODO: add a configuration option for resolving optionally to java classes
                    //if (virtualTarget == null) { // Okay, try as if the link target is a class reference
                    //    virtualTarget = resolveClassReference(project, href);
                    //}

                    foundFile[0] = virtualTarget;
                    if (foundFile[0] != null && project != null && openFile) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                FileEditorManager.getInstance(project).openFile((VirtualFile) foundFile[0], true, true);

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

            if (foundFile[0] == null && project != null) {
                // try link remapping
                String newHref = resolveExternalReference(project, document, href);
                if (newHref == null) return null;
                hrefEnc = hrefDec = newHref;
            } else {
                return foundFile[0];
            }
        }

        if (FilePathInfo.isExternalReference(hrefDec)) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Object foundFile = new URI(hrefEnc);
                    if (openFile) Desktop.getDesktop().browse((URI) foundFile);
                    return foundFile;
                } catch (URISyntaxException ex) {
                    // invalid URI, just log
                    logger.info("URISyntaxException on '" + hrefDec + "'" + ex.toString());
                } catch (IOException ex) {
                    logger.info("IOException on '" + hrefDec + "'" + ex.toString());
                }
            }
        }
        return null;
    }

    @Nullable
    public static Object openLink(@NotNull String href) {
        if (Desktop.isDesktopSupported()) {
            try {
                Object foundFile = new URI(href);
                Desktop.getDesktop().browse((URI) foundFile);
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

    @Nullable
    public static String getGitHubDocumentURL(@NotNull Project project, @NotNull Document document, boolean noExtension) {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        String githubhref = null;
        if (projectComponent != null) {
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

            if (virtualFile != null && projectComponent.isUnderVcs(virtualFile)) {
                GitHubRepo gitHubRepo = projectComponent.getGitHubRepo(virtualFile.getPath());
                if (gitHubRepo != null) {
                    FilePathInfo pathInfo = new FilePathInfo(virtualFile);
                    githubhref = gitHubRepo.repoUrlFor(gitHubRepo.getRelativePath(noExtension ? pathInfo.getFilePathNoExt() : pathInfo.getFilePath()));
                    if (githubhref != null && !FilePathInfo.isExternalReference(githubhref)) {
                        githubhref = null;
                    }
                }
            }
        }
        return githubhref;
    }

    public static void launchExternalLink(@NotNull Project project, @NotNull Document document, @NotNull String href) {
        Object resolved = MultiMarkdownPathResolver.resolveLink(project, document, href, false, true);
        if (resolved != null && resolved instanceof VirtualFile) {
            // can resolve, let see if we need to map it to github
            FilePathInfo pathInfo = new FilePathInfo((VirtualFile) resolved);
            // if it is under git source code control map it to remote
            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
            if (projectComponent != null) {
                if (projectComponent.isUnderVcs((VirtualFile) resolved)) {
                    GitHubRepo gitHubRepo = projectComponent.getGitHubRepo(pathInfo.getPath());
                    if (gitHubRepo != null) {
                        String githubhref = null;
                        FilePathInfo hrefInfo = new FilePathInfo(href);
                        if (hrefInfo.isMarkdownExt() && gitHubRepo.isWiki()) {
                            githubhref = gitHubRepo.repoUrlFor(hrefInfo.getFilePathNoExt() + hrefInfo.getAnchor());
                        } else {
                            githubhref = gitHubRepo.repoUrlFor(hrefInfo.getFilePathWithAnchor());
                        }

                        if (githubhref == null) {
                            githubhref = gitHubRepo.repoUrlFor(gitHubRepo.getRelativePath(gitHubRepo.isWiki() ? pathInfo.getFilePathWithAnchorNoExt() : pathInfo.getFullFilePath()));
                        }

                        if (githubhref != null && FilePathInfo.isExternalReference(githubhref)) {
                            // remap it to external and launch browser
                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().browse((URI) new URI(githubhref));
                                    return;
                                } catch (URISyntaxException ex) {
                                    // invalid URI, just log
                                    logger.info("URISyntaxException on '" + githubhref + "'" + ex.toString());
                                } catch (IOException ex) {
                                    logger.info("IOException on '" + githubhref + "'" + ex.toString());
                                }
                            }
                        }
                    }
                }
            }
        }

        resolveLink(project, document, href, true, !FilePathInfo.isExternalReference(href));
    }
}
