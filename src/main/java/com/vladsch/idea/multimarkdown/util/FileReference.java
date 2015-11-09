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
package com.vladsch.idea.multimarkdown.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileReference extends FilePathInfo {
    private static final Logger logger = Logger.getLogger(FileReference.class);

    protected final Project project;
    protected VirtualFile virtualFile;

    public FileReference(@NotNull String filePath) {
        super(filePath);
        this.project = null;
        this.virtualFile = null;
    }

    public FileReference(@NotNull FilePathInfo filePath) {
        super(filePath);
        this.project = null;
        this.virtualFile = null;
    }

    public FileReference(@NotNull FilePathInfo filePath, Project project) {
        super(filePath);
        this.project = project;
        this.virtualFile = null;
        this.virtualFile = null;
    }

    public FileReference(@NotNull String filePath, Project project) {
        super(filePath);
        this.project = project;
        this.virtualFile = null;
    }

    public FileReference(@NotNull VirtualFile virtualFile, Project project) {
        super(virtualFile.getPath());
        this.project = project;
        this.virtualFile = virtualFile;
    }

    public FileReference(@NotNull PsiFile psiFile) {
        super(psiFile.getVirtualFile().getPath());
        this.project = psiFile.getProject();
        this.virtualFile = psiFile.getVirtualFile();
    }

    public FileReference(@NotNull FileReference other) {
        super(other);
        this.project = other.project;
        this.virtualFile = other.virtualFile;
    }

    @Nullable
    @Override
    public FileReference resolveLinkRef(@Nullable String linkRef, boolean convertGitHubWikiHome) {
        return resolveLinkRef(linkRef, convertGitHubWikiHome, false);
    }

    @Nullable
    @Override
    public FileReference resolveLinkRefWithAnchor(@Nullable String linkRef, boolean convertGitHubWikiHome) {
        return resolveLinkRef(linkRef, convertGitHubWikiHome, true);
    }

    @Nullable
    @Override
    public FileReference resolveLinkRef(@Nullable String linkRef) {
        return resolveLinkRef(linkRef, false, false);
    }

    @Nullable
    @Override
    public FileReference resolveLinkRefWithAnchor(@Nullable String linkRef) {
        return resolveLinkRef(linkRef, false, true);
    }

    @Nullable
    @Override
    public FileReference resolveLinkRefToWikiPage(@Nullable String linkRef) {
        return resolveLinkRef(linkRef, true, false);
    }

    @Nullable
    @Override
    public FileReference resolveLinkRefWithAnchorToWikiPage(@Nullable String linkRef) {
        return resolveLinkRef(linkRef, true, true);
    }

    protected class MarkdownGitHubLinkResolver extends FilePathInfo.LinkRefResolver {
        MarkdownGitHubLinkResolver(@NotNull String lastPart) {
            super("..", "..", lastPart);
        }

        @Override
        boolean isMatched(FilePathInfo currentPath, String[] linkRefParts, int part) {
            return project != null && !currentPath.isWikiHome() && super.isMatched(currentPath, linkRefParts, part);
        }

        @Override
        FilePathInfo computePath(FilePathInfo currentPath) {
            // if this is not a wiki home and what comes next is ../../{githubword} then we can replace is a subdirectory with current dir name with .wiki added
            assert project != null;

            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
            if (projectComponent != null) {
                GitHubRepo gitHubRepo = projectComponent.getGithubRepo(currentPath.getFullFilePath());
                if (gitHubRepo != null) {
                    try {
                        String url = gitHubRepo.githubBaseUrl();
                        return new FilePathInfo(url).append(matchParts[matchParts.length - 1]);
                    } catch (RuntimeException ignored) {
                        logger.info("Can't resolve GitHub url", ignored);
                    }
                }
            }
            return currentPath;
        }
    }

    protected class MarkdownGitHubWikiExternalLinkResolver extends FilePathInfo.LinkRefResolver {
        MarkdownGitHubWikiExternalLinkResolver(@NotNull String lastPart) {
            super("..", "..", lastPart);
        }

        @Override
        boolean isMatched(FilePathInfo currentPath, String[] linkRefParts, int part) {
            return project != null && !currentPath.isWikiHome() && super.isMatched(currentPath, linkRefParts, part);
        }

        @Override
        FilePathInfo computePath(FilePathInfo currentPath) {
            // if this is not a wiki home and what comes next is ../../wiki then we can replace is a subdirectory with current dir name with .wiki added
            assert project != null;

            FilePathInfo wikiPath = currentPath.append(currentPath.getFileName() + WIKI_HOME_EXTENTION);
            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
            if (projectComponent != null) {
                GitHubRepo gitHubRepo = projectComponent.getGithubRepo(wikiPath.getFullFilePath());
                if (gitHubRepo != null) {
                    try {
                        String url = gitHubRepo.repoUrlFor("/");
                        return new FilePathInfo(url);
                    } catch (RuntimeException ignored) {
                        logger.info("Can't resolve GitHub url", ignored);
                    }
                }
            }
            return currentPath;
        }
    }

    protected final MarkdownGitHubWikiExternalLinkResolver markdownGitHubWikiLinkResolver = new MarkdownGitHubWikiExternalLinkResolver(GITHUB_WIKI_HOME_DIRNAME);
    protected final MarkdownGitHubLinkResolver markdownGitHubIssuesLinkResolver = new MarkdownGitHubLinkResolver(GITHUB_ISSUES_NAME);
    protected final MarkdownGitHubLinkResolver markdownGitHubPullsLinkResolver = new MarkdownGitHubLinkResolver(GITHUB_PULLS_NAME);
    protected final MarkdownGitHubLinkResolver markdownGitHubPulseLinkResolver = new MarkdownGitHubLinkResolver(GITHUB_PULSE_NAME);
    protected final MarkdownGitHubLinkResolver markdownGitHubGraphsLinkResolver = new MarkdownGitHubLinkResolver(GITHUB_GRAPHS_NAME);

    @Nullable
    public GitHubRepo getGitHubRepo() {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        return projectComponent != null ? projectComponent.getGithubRepo(getPath()) : null;
    }

    @Nullable
    public String getGitHubRepoPath() {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        GitHubRepo gitHubRepo = projectComponent != null ? projectComponent.getGithubRepo(getPath()) : null;

        return gitHubRepo != null ? endWith(gitHubRepo.getBasePath(), '/') : null;
    }

    @NotNull
    public String getGitHubRepoPath(@NotNull String defaultPath) {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        GitHubRepo gitHubRepo = projectComponent != null ? projectComponent.getGithubRepo(getPath()) : null;

        return endWith(gitHubRepo != null ? gitHubRepo.getBasePath() : defaultPath, '/');
    }

    @Nullable
    @Override
    protected FileReference resolveLinkRef(@Nullable String linkRef, boolean convertLinkRefs, boolean withAnchor, LinkRefResolver... linkRefResolvers) {
        FilePathInfo resolvedPathInfo = super.resolveLinkRef(linkRef, convertLinkRefs, withAnchor, linkRefResolvers);
        return resolvedPathInfo != null ? new FileReference(resolvedPathInfo, this.project) : null;
    }

    @Nullable
    protected FileReference resolveExternalLinkRef(@Nullable String linkRef, boolean withAnchor, LinkRefResolver... linkRefResolvers) {
        FilePathInfo resolvedPathInfo = super.resolveLinkRef(linkRef, true, withAnchor, appendResolvers(linkRefResolvers, markdownGitHubIssuesLinkResolver, markdownGitHubPullsLinkResolver, markdownGitHubPulseLinkResolver, markdownGitHubGraphsLinkResolver));
        return resolvedPathInfo != null && resolvedPathInfo.isExternalReference() ? new FileReference(resolvedPathInfo, this.project) : null;
    }

    @Nullable
    public FileReference resolveExternalLinkRef(@Nullable String linkRef, boolean resolveWikiLinks, boolean withAnchor) {
        return resolveExternalLinkRef(linkRef, withAnchor, resolveWikiLinks ? markdownGitHubWikiLinkResolver : (LinkRefResolver) null);
    }

    @NotNull
    @Override
    public FileReference withExt(@Nullable String ext) {
        return new FileReference(super.withExt(ext), project);
    }

    private boolean fileExists() {
        return getVirtualFile() != null;
    }

    public Project getProject() {
        return project;
    }

    @Nullable
    public VirtualFile getVirtualFile() {
        if (virtualFile == null) {
            virtualFile = FileReference.getVirtualFile(getFilePath());
        }
        return virtualFile != null && virtualFile.getPath().equals(getFilePath()) ? virtualFile : null;
    }

    @Nullable
    public VirtualFile getVirtualFileWithAnchor() {
        if (virtualFile == null) {
            virtualFile = FileReference.getVirtualFile(getFilePathWithAnchor());
        }
        return virtualFile != null && virtualFile.getPath().equals(getFilePathWithAnchor()) ? virtualFile : null;
    }

    @Nullable
    public VirtualFile getVirtualParent() {
        return virtualFile != null ? virtualFile.getParent() : FileReference.getVirtualFile(getPath());
    }

    @Nullable
    public PsiFile getPsiFile() {
        VirtualFile virtualFile = getVirtualFile();
        return virtualFile != null ? FileReference.getPsiFile(virtualFile, project) : null;
    }

    @Nullable
    public PsiFile getPsiFileWithAnchor() {
        VirtualFile virtualFile = getVirtualFileWithAnchor();
        return virtualFile != null ? FileReference.getPsiFile(virtualFile, project) : null;
    }

    @Nullable
    public MultiMarkdownFile getMultiMarkdownFile() {
        PsiFile file;
        VirtualFile virtualFile = getVirtualFile();
        return virtualFile != null && (file = FileReference.getPsiFile(virtualFile, project)) instanceof MultiMarkdownFile ? (MultiMarkdownFile) file : null;
    }

    @Nullable
    public MultiMarkdownFile getMultiMarkdownFileWithAnchor() {
        PsiFile file;
        VirtualFile virtualFile = getVirtualFileWithAnchor();
        return virtualFile != null && (file = FileReference.getPsiFile(virtualFile, project)) instanceof MultiMarkdownFile ? (MultiMarkdownFile) file : null;
    }

    @Nullable
    public static VirtualFile getVirtualFile(@NotNull String sourcePath) {
        return VirtualFileManager.getInstance().findFileByUrl("file://" + sourcePath);
    }

    @Nullable
    public static PsiFile getPsiFile(@NotNull VirtualFile file, @NotNull Project project) {
        return PsiManager.getInstance(project).findFile(file);
    }

    @Override
    public int compareTo(FilePathInfo o) {
        return !(o instanceof FileReference) || project == ((FileReference) o).project ? super.compareTo(o) : -1;
    }

    @Override
    public String toString() {
        return "FileReference(" +
                innerString() +
                ")";
    }

    @Override
    public String innerString() {
        return super.innerString() +
                "project = '" + (project == null ? "null" : project.getName()) + "', " +
                "";
    }

    public boolean canRenameFileTo(@NotNull final String newName) {
        if (project != null) {
            if (equivalent(false, false, getFileName(), newName)) return true;

            // not just changing file name case
            final VirtualFile virtualFile = getVirtualFileWithAnchor();
            final VirtualFile parent = virtualFile != null ? virtualFile.getParent() : null;
            if (parent != null) {
                if (parent.findChild(newName) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canCreateFile() {
        final String newName = getFileName();
        return canCreateFile(newName);
    }

    public boolean canCreateFile(@NotNull final String newName) {
        if (project != null) {
            final VirtualFile parent = getVirtualParent();
            if (parent != null) {
                if (parent.findChild(newName) == null) {
                    return true;
                }
            }
        }
        return false;
    }
}
