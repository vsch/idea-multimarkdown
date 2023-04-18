// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.intellij.ide.actions.QualifiedNameProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.md.nav.psi.element.MdAnchorTarget;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.util.PathInfo;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MdQualifiedNameProvider implements QualifiedNameProvider {
    @Nullable
    @Override
    public PsiElement adjustElementToCopy(PsiElement element) {
        return MdPsiImplUtil.findAncestorOfType(element, MdAnchorTarget.class);
    }

    @Nullable
    @Override
    public String getQualifiedName(PsiElement element) {
        if (element instanceof MdAnchorTarget) {
            MdAnchorTarget target = (MdAnchorTarget) element;
            String anchorRef = target.getAttributedAnchorReferenceId();
            if (anchorRef != null) {
                // change to name of file/anchor
                PsiFile psiFile = element.getContainingFile();
                VirtualFile virtualFile = psiFile.getVirtualFile();
                if (virtualFile != null) {
                    String filePath = virtualFile.getCanonicalPath();
                    if (filePath != null) {
                        String fileUri = PathInfo.prefixWithFileURI(filePath);

                        return String.format("%s#%s", fileUri, anchorRef);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement qualifiedNameToElement(String fqn, Project project) {
        PathInfo fqnInfo = new PathInfo("file:///" + PathInfo.removeFileUriPrefix(FileUtil.toSystemIndependentName(fqn)));
        if (fqnInfo.isFileURI() && fqnInfo.fileNameContainsAnchor()) {
            String fileNameAnchor = fqnInfo.getFileName();
            int pos = fileNameAnchor.indexOf("#");
            String fileName = fileNameAnchor.substring(0, pos);
            String anchorRef = fileNameAnchor.substring(pos + 1);
            if (!anchorRef.trim().isEmpty()) {
                // see if we can find the file and it is markdown
                VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(fqnInfo.getPath() + fileName);
                if (virtualFile != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                    if (psiFile instanceof MdFile) {
                        // find anchor target for reference
                        List<MdAnchorTarget> targets = ((MdFile) psiFile).getAnchorTargets();
                        for (MdAnchorTarget target : targets) {
                            if (target.isReferenceFor(anchorRef)) {
                                return target;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}

