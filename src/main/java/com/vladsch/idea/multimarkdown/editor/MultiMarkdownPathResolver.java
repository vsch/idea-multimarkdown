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
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.util.*;
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

    public static boolean canResolveRelativeImageLink(@NotNull FileReferenceList fileReferenceList, @NotNull FileReference documentFileReference, @Nullable GitHubRepo gitHubRepo, @NotNull String target, boolean resolveExternal) {
        // need to resolve using same code as links
        FilePathInfo targetInfo = new FilePathInfo(target);
        FileReferenceList fileList = fileReferenceList.query()
                .gitHubWikiRules()
                .wantImageFiles()
                .inSource(documentFileReference)
                .matchLinkRef(targetInfo.getFilePath())
                .first();

        // can just compare if it is the same instance, if not then it was resolved
        if (fileList.size() > 0 && resolveRelativeLink(fileList.getAt(0), documentFileReference, gitHubRepo, targetInfo) != targetInfo) return true;
        return false;
    }

    public static boolean canResolveRelativeLink(@NotNull FileReferenceList fileReferenceList, @NotNull FileReference documentFileReference, @Nullable GitHubRepo gitHubRepo, @NotNull String target, boolean resolveExternal) {
        // need to resolve using same code as links
        FilePathInfo targetInfo = new FilePathInfo(target);
        FileReferenceList fileList = fileReferenceList.query()
                .gitHubWikiRules()
                .wantMarkdownFiles()
                .inSource(documentFileReference)
                .matchLinkRefNoExt(targetInfo.getFilePathNoExt())
                .first();

        // can just compare if it is the same instance, if not then it was resolved
        if (fileList.size() > 0 && resolveRelativeLink(fileList.getAt(0), documentFileReference, gitHubRepo, targetInfo) != targetInfo) return true;

        target = targetInfo.getFullFilePath();

        if (FilePathInfo.endsWith(target, FilePathInfo.GITHUB_LINKS)) {
            FileReference resolvedTarget = documentFileReference.resolveLinkRef(target, false);
            //if (resolvedTarget != null && (resolvedTarget.isExternalReference() || resolvedTarget.getVirtualFile() != null)) return true;
            if (resolvedTarget != null) return true;
            resolvedTarget = documentFileReference.resolveExternalLinkRef(target, true, false);
            if (resolvedTarget != null) return true;
        }
        return false;
    }

    @NotNull
    public static FilePathInfo resolveRelativeLink(@NotNull FileReference targetFileReference, @NotNull FileReference documentFileReference, @Nullable GitHubRepo gitHubRepo, @NotNull FilePathInfo targetInfo) {
        boolean wantExt = !documentFileReference.isWikiPage();
        String newTarget = null;
        FileReferenceLink fileReferenceLink;
        if (gitHubRepo != null) {
            fileReferenceLink = new FileReferenceLinkGitHubRules(documentFileReference, targetFileReference);
            if (!wantExt || fileReferenceLink.isWikiPage()) {
                wantExt = false;
                newTarget = fileReferenceLink.getLinkRefNoExt();
            } else {
                newTarget = fileReferenceLink.getLinkRef();
            }
        } else {
            fileReferenceLink = new FileReferenceLink(documentFileReference, targetFileReference);
            newTarget = fileReferenceLink.getLinkRef();
        }

        // make sure both have matching extensions or no match
        if (wantExt) {
            if (!targetInfo.getExt().equalsIgnoreCase(fileReferenceLink.getExt())) return targetInfo;

            // now if the source and target are not wiki pages then comparison is case sensitive
            if (!documentFileReference.isWikiPage() && !fileReferenceLink.isWikiPage()) {
                if (!targetInfo.getFilePath().equals(fileReferenceLink.getLinkRef())) return targetInfo;
            }
        } else {
            if (targetInfo.hasExt() && fileReferenceLink.isWikiPage()) return targetInfo;

            // now if the source and target are not wiki pages then comparison is case sensitive
            if (!documentFileReference.isWikiPage() && !fileReferenceLink.isWikiPage()) {
                if (!targetInfo.getFileNameNoExt().equals(fileReferenceLink.getLinkRefNoExt())) return targetInfo;
            }
        }

        return new FilePathInfo(newTarget);
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

    public static boolean isWikiDocument(@NotNull final Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        return file != null && new FilePathInfo(file).isWikiPage();
    }

    public static Object resolveLocalLink(@NotNull final Project project, @NotNull final Document document, @NotNull String hrefEnc, final boolean withExternal) {
        if (project.isDisposed()) return null;

        String hrefDec = hrefEnc;
        try {
            hrefDec = URLDecoder.decode(hrefEnc, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        int posHash;
        if ((posHash = hrefDec.indexOf('#')) > 0) {
            hrefDec = hrefDec.substring(0, posHash);
        }

        final String href = hrefDec;

        if (!FilePathInfo.isExternalReference(href)) {
            final Object[] foundFile = { null };
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    VirtualFile virtualTarget = null;

                    if (href.startsWith("file:")) {
                        try {
                            URL target = new URL(href);
                            VirtualFileSystem virtualFileSystem = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
                            virtualTarget = virtualFileSystem == null ? null : virtualFileSystem.findFileByPath(target.getFile());
                        } catch (MalformedURLException ignored) {
                        }
                    }

                    if ((virtualTarget == null || !virtualTarget.exists())) {
                        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                        if (file != null) {
                            FileReference documentFileReference = new FileReference(file.getPath(), project);
                            VirtualFile parent = file.getParent();
                            if (parent != null) {
                                // resolve without wiki links
                                FileReference resolvedTarget = documentFileReference.resolveLinkRef(href, false);
                                virtualTarget = resolvedTarget == null || resolvedTarget.isExternalReference() ? null : resolvedTarget.getVirtualFile();

                                if (virtualTarget == null && resolvedTarget != null) {
                                    // resolve with local wiki links, try standard markdown extensions
                                    resolvedTarget = documentFileReference.resolveLinkRef(href, true);
                                    virtualTarget = resolvedTarget == null || resolvedTarget.isExternalReference() ? null : resolvedTarget.getVirtualFile(FilePathInfo.WIKI_PAGE_EXTENSIONS);

                                    if (virtualTarget == null && resolvedTarget != null && withExternal) {
                                        // resolve with external wiki local links
                                        resolvedTarget = documentFileReference.resolveExternalLinkRef(href, true, false);
                                        if (resolvedTarget != null && resolvedTarget.isExternalReference()) {
                                            foundFile[0] = resolvedTarget.getFullFilePath();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    foundFile[0] = virtualTarget;
                }
            };

            // RELEASE: make sure this works on older versions
            //Application application = ApplicationManager.getApplication();
            //application.runReadAction(runnable);
            runnable.run();
            return foundFile[0];
        }

        return null;
    }

    public static void openLink(@NotNull String href) {
        if (Desktop.isDesktopSupported()) {
            try {
                Object foundFile = new URI(href);
                Desktop.getDesktop().browse((URI) foundFile);
            } catch (URISyntaxException ex) {
                // invalid URI, just log
                logger.info("URISyntaxException on '" + href + "'" + ex.toString());
            } catch (IOException ex) {
                logger.info("IOException on '" + href + "'" + ex.toString());
            }
        }
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

    public static void launchExternalLink(@NotNull final Project project, @NotNull Document document, @NotNull final String href) {
        final Object resolved = resolveLocalLink(project, document, href, true);

        if (resolved != null) {
            if (resolved instanceof VirtualFile) {
                // can resolve, let see if we need to map it to github
                VirtualFile documentFile = FileDocumentManager.getInstance().getFile(document);
                if (documentFile != null) {
                    FileReferenceLink fileReferenceLink = new FileReferenceLink(documentFile, (VirtualFile) resolved, project);
                    FilePathInfo hrefInfo = new FilePathInfo(href);

                    if (fileReferenceLink.isWikiPage() && hrefInfo.hasExt()) {
                        // it is a wiki page and href has extension, it will not resolve on github
                        return;
                    }

                    // if it is under git source code control map it to remote
                    MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
                    if (projectComponent != null) {
                        if (projectComponent.isUnderVcs((VirtualFile) resolved)) {
                            GitHubRepo gitHubRepo = projectComponent.getGitHubRepo(fileReferenceLink.getPath());
                            if (gitHubRepo != null) {
                                String gitHubHref = null;

                                if (fileReferenceLink.isMarkdownExt() && gitHubRepo.isWiki()) {
                                    gitHubHref = gitHubRepo.repoUrlFor(fileReferenceLink.getFileNameNoExt()) + hrefInfo.getAnchor();
                                } else {
                                    gitHubHref = gitHubRepo.repoUrlFor(fileReferenceLink.getLinkRef()) + hrefInfo.getAnchor();
                                }

                                if (FilePathInfo.isExternalReference(gitHubHref)) {
                                    openLink(gitHubHref);
                                    return;
                                }
                            }
                        }
                    }
                }

                // open local file
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        FileEditorManager.getInstance(project).openFile((VirtualFile) resolved, true, true);
                        int posHash;
                        String hashSuffix = null;
                        if ((posHash = href.indexOf('#')) > 0) {
                            hashSuffix = href.substring(posHash + 1);
                        }

                        if (hashSuffix != null && hashSuffix.length() > 0) {
                            // TODO: see if we can resolve the #hashSuffix in the file
                            //logger.info("got hash suffixed href: " + href + "#" + hashSuffix);
                        }
                    }
                });
            } else if (resolved instanceof String) {
                openLink((String) resolved);
            }
        }
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

        if (file != null) {
            FileReference documentFileReference = new FileReference(file.getPath(), project);

            // include wiki links in resolution
            resolvedTarget = documentFileReference.resolveExternalLinkRef(target, true, false);
        }
        return resolvedTarget == null ? null : resolvedTarget.getFilePathWithAnchor();
    }
}
