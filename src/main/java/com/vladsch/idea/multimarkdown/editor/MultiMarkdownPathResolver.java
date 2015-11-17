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

    public static boolean canResolveRelativeLink(@Nullable FileReferenceList fileReferenceList, @NotNull FileReference documentFileReference, @Nullable GitHubRepo gitHubRepo, @NotNull String target, boolean isWikiLink, boolean resolveExternal) {
        return resolveRelativeLink(fileReferenceList, documentFileReference, gitHubRepo, target, isWikiLink, resolveExternal) != null;
    }

    @Nullable
    public static FileReference resolveRelativeLink(@Nullable FileReferenceList fileReferenceList, @NotNull FileReference documentFileReference, @Nullable GitHubRepo gitHubRepo, @NotNull String target, boolean isWikiLink, boolean resolveExternal) {
        // need to resolve using same code as links
        if (target.startsWith("./")) target = target.substring(2);

        FilePathInfo linkRefInfo = new FilePathInfo(target);
        FileReferenceList fileList;

        if (fileReferenceList == null && documentFileReference.getProject() == null) return null;

        if (isWikiLink) {
            fileList = (fileReferenceList != null ? new FileReferenceListQuery(fileReferenceList) : new FileReferenceListQuery(documentFileReference.getProject()))
                    .caseInsensitive()
                    .gitHubWikiRules()
                    .ignoreLinkRefExtension(!(linkRefInfo.hasWithAnchorExtWithDot() && linkRefInfo.hasWithAnchorWikiPageExt()))
                    .keepLinkRefAnchor()
                    .linkRefIgnoreSubDirs()
                    .spaceDashEqual()
                    .wantMarkdownFiles()
                    .inSource(documentFileReference)
                    .matchWikiRef(linkRefInfo.getFileNameWithAnchorAsWikiRef())
                    .all()
                    .postMatchFilter(linkRefInfo.getFileNameWithAnchorAsWikiRef(), true)
                    .sorted();
        } else {
            fileList = (fileReferenceList != null ? new FileReferenceListQuery(fileReferenceList) : new FileReferenceListQuery(documentFileReference.getProject()))
                    .caseInsensitive()
                    .gitHubWikiRules()
                    .keepLinkRefAnchor()
                    .linkRefIgnoreSubDirs()
                    .sameGitHubRepo()
                    .wantMarkdownFiles()
                    .inSource(documentFileReference)
                    .matchLinkRef(linkRefInfo.getFilePathWithAnchorNoExt(), false)
                    .all()
                    .postMatchFilter(linkRefInfo.getFullFilePath(), false)
                    .sorted();
        }

        if (fileList.size() > 0) {
            // see if can get github href
            FileReference fileReference = fileList.getAt(0);
            if (!resolveExternal) {
                return fileReference;
            }

            boolean anchorPartOfName = fileReference.isLinkRefAnchorPartOfName(linkRefInfo);
            String anchor = anchorPartOfName ? "" : linkRefInfo.getAnchor();
            boolean withExt = !fileReference.isWikiPage() || (!anchorPartOfName ? linkRefInfo.hasExt() : linkRefInfo.hasWithAnchorExt());

            String gitHubLink = fileReference.getGitHubFileURL(withExt, anchor);

            // does not resolve on GitHub
            if (gitHubLink != null && !isWikiLink && anchorPartOfName) return null;

            return gitHubLink == null ? fileReference : new FileReference(gitHubLink);
        }

        target = linkRefInfo.getFullFilePath();

        if (FilePathInfo.endsWith(target, FilePathInfo.GITHUB_LINKS)) {
            FileReference resolvedTarget = documentFileReference.resolveExternalLinkRef(target, true, false);
            return resolvedTarget;
        }
        return null;
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
    public static String getGitHubDocumentURL(@NotNull Project project, @NotNull Document document, boolean withExtension) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        return getGitHubFileURL(virtualFile, project, withExtension, null);
    }

    @Nullable
    public static String getGitHubFileURL(VirtualFile virtualFile, @NotNull Project project, boolean withExtension, @Nullable String anchor) {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);

        if (projectComponent != null) {
            if (virtualFile != null && projectComponent.isUnderVcs(virtualFile)) {
                GitHubRepo gitHubRepo = projectComponent.getGitHubRepo(virtualFile.getPath());
                if (gitHubRepo != null) {
                    return gitHubRepo.repoUrlFor(virtualFile, withExtension, anchor);
                }
            }
        }
        return null;
    }

    public static void launchExternalLink(@NotNull final Project project, @NotNull final String href) {
        if (FilePathInfo.isExternalReference(href)) {
            openLink(href);
        } else if (href.startsWith("file://")) {
            try {
                URL target = new URL(href);
                VirtualFileSystem virtualFileSystem = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
                final VirtualFile virtualFile = virtualFileSystem == null ? null : virtualFileSystem.findFileByPath(target.getFile());
                // open local file
                if (virtualFile != null) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            FileEditorManager.getInstance(project).openFile(virtualFile, true, true);
                            // TODO: see if we can resolve the #hashSuffix in the file
                            //logger.info("got hash suffixed href: " + href + "#" + hashSuffix);
                        }
                    });
                }
            } catch (MalformedURLException ignored) {
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
