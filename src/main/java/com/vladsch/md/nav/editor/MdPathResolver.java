// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.util.PsiNavigateUtil;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdHeaderElement;
import com.vladsch.md.nav.psi.text.MdLineSelectionFakePsiElement;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.vcs.GitHubVcsRoot;
import com.vladsch.md.nav.vcs.MdLinkResolverManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.vladsch.plugin.util.HelpersKt.removePrefix;

public class MdPathResolver {
    private MdPathResolver() {

    }

    public static boolean isWikiDocument(@NotNull final Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        return file != null && new FileRef(file).isWikiPage();
    }

    @Nullable
    public static String getGitHubDocumentURL(@NotNull Project project, @NotNull Document document, boolean withExtension) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        return getGitHubFileURL(virtualFile, project, withExtension, null);
    }

    @Nullable
    public static String getGitHubFileURL(VirtualFile virtualFile, @NotNull Project project, boolean withExtension, @Nullable String anchor) {
        MdLinkResolverManager projectComponent = MdLinkResolverManager.getInstance(project);

        if (virtualFile != null && projectComponent.isUnderVcs(virtualFile)) {
            GitHubVcsRoot gitHubVcsRoot = projectComponent.getGitHubRepo(new PathInfo(virtualFile.getPath()).getPath());
            if (gitHubVcsRoot != null) {
                return gitHubVcsRoot.urlForVcsRemote(virtualFile, withExtension, anchor, null, null);
            }
        }
        return null;
    }

    public static boolean launchExternalLink(@NotNull final Project project, @NotNull final String href) {
        boolean launched = false;

        if (href.startsWith("file://") || href.startsWith("file:/")) {
            try {
                URL target = new URL(href);
                VirtualFileSystem virtualFileSystem = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
                String path = LinkRef.urlDecode(removePrefix(removePrefix(target.getPath(), "file://"), "file:/")); // on windows it needs the stuff after file:// and target.getFile() does not have it
                final VirtualFile virtualFile = virtualFileSystem == null ? null : virtualFileSystem.findFileByPath(path);
                // open local file
                if (virtualFile != null) {
                    final String anchorRef = target.getRef();
                    launched = true;
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (virtualFile.isDirectory()) {
                            // get psi element and navigate to it
                            PsiFileSystemItem psiFileSystemItem = PsiManager.getInstance(project).findDirectory(virtualFile);
                            if (psiFileSystemItem != null) {
                                psiFileSystemItem.navigate(true);
                            }
                        } else {
                            FileEditorManager.getInstance(project).openFile(virtualFile, true, true);
                            if ((anchorRef != null) && !anchorRef.isEmpty()) {
                                PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                                if (psiFile != null) {
                                    MdLineSelectionFakePsiElement lineSelectionElement = MdLineSelectionFakePsiElement.getLineSelectionElement(psiFile, anchorRef);
                                    if (lineSelectionElement != null) {
                                        if (lineSelectionElement.canNavigate()) lineSelectionElement.navigate(true);
                                    } else if (psiFile instanceof MdFile) {
                                        // see if we can resolve the #hashSuffix in the file
                                        List<MdHeaderElement> references = MdPsiImplUtil.getHeaderElements((MdFile) psiFile, anchorRef, true);

                                        // see if there are multiples
                                        if (references.size() > 0) {
                                            // have duplicate
                                            PsiNavigateUtil.navigate(references.get(0));
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            } catch (MalformedURLException ignored) {
            }
        } else if (BrowserUtil.isAbsoluteURL(href)) {
            String hrefUrl = href;
            // HACK: insert idea-ce/file/HEAD if only path provided
            if (href.startsWith("upsource://") && !href.matches("^\\Qupsource://\\E[^/]+\\Q/file/\\E.*$")) {
                hrefUrl = "https://upsource.jetbrains.com/idea-ce/file/HEAD/" + removePrefix(href.substring("upsource://".length()), '/');
            }
            try {
                BrowserUtil.browse(hrefUrl);
            } catch (Exception e) {
                Messages.showErrorDialog(e.getMessage(), "Link Navigation Failure");
            }
            launched = true;
        }

        return launched;
    }
}
