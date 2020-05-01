// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.ide;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.codeInsight.daemon.impl.IdentifierUtil;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.ide.actions.QualifiedNameProviderUtil;
import com.intellij.ide.scratch.RootType;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.ContainerUtil;
import com.vladsch.plugin.util.HelpersKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.vladsch.md.nav.settings.MdApplicationSettings.getInstance;

public class CopyReferenceUtils {
    static void highlight(Editor editor, Project project, List<? extends PsiElement> elements) {
        HighlightManager highlightManager = HighlightManager.getInstance(project);
        EditorColorsManager manager = EditorColorsManager.getInstance();
        TextAttributes attributes = manager.getGlobalScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);
        if (elements.size() == 1 && editor != null && project != null) {
            PsiElement element = elements.get(0);
            PsiElement nameIdentifier = IdentifierUtil.getNameIdentifier(element);
            if (nameIdentifier != null) {
                highlightManager.addOccurrenceHighlights(editor, new PsiElement[] { nameIdentifier }, attributes, true, null);
            } else {
                PsiReference reference = TargetElementUtil.findReference(editor, editor.getCaretModel().getOffset());
                if (reference != null) {
                    highlightManager.addOccurrenceHighlights(editor, new PsiReference[] { reference }, attributes, true, null);
                } else if (element != PsiDocumentManager.getInstance(project).getCachedPsiFile(editor.getDocument())) {
                    highlightManager.addOccurrenceHighlights(editor, new PsiElement[] { element }, attributes, true, null);
                }
            }
        }
    }

    @NotNull
    static List<PsiElement> getElementsToCopy(@Nullable final Editor editor, final DataContext dataContext) {
        List<PsiElement> elements = new ArrayList<>();
        if (editor != null) {
            PsiReference reference = TargetElementUtil.findReference(editor);
            if (reference != null) {
                ContainerUtil.addIfNotNull(elements, reference.getElement());
            }
        }

        if (elements.isEmpty()) {
            PsiElement[] psiElements = LangDataKeys.PSI_ELEMENT_ARRAY.getData(dataContext);
            if (psiElements != null) {
                Collections.addAll(elements, psiElements);
            }
        }

        if (elements.isEmpty()) {
            ContainerUtil.addIfNotNull(elements, CommonDataKeys.PSI_ELEMENT.getData(dataContext));
        }

        if (elements.isEmpty() && editor == null) {
            final Project project = CommonDataKeys.PROJECT.getData(dataContext);
            VirtualFile[] files = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
            if (project != null && files != null) {
                for (VirtualFile file : files) {
                    ContainerUtil.addIfNotNull(elements, PsiManager.getInstance(project).findFile(file));
                }
            }
        }

        return ContainerUtil.mapNotNull(elements, element -> element instanceof PsiFile && !((PsiFile) element).getViewProvider().isPhysical()
                ? null
                : adjustElement(element));
    }

    static void setStatusBarText(Project project, String message) {
        if (project != null) {
            final StatusBarEx statusBar = (StatusBarEx) WindowManager.getInstance().getStatusBar(project);
            if (statusBar != null) {
                statusBar.setInfo(message);
            }
        }
    }

    static PsiElement adjustElement(PsiElement element) {
        PsiElement adjustedElement = QualifiedNameProviderUtil.adjustElementToCopy(element);
        return adjustedElement != null ? adjustedElement : element;
    }

    @NotNull
    public static String getVirtualFileFqn(@NotNull VirtualFile virtualFile, @NotNull Project project, boolean relativeToProjectBaseDir) {
        if (!relativeToProjectBaseDir) {
            Module module = ProjectFileIndex.getInstance(project).getModuleForFile(virtualFile, false);
            if (module != null) {
                for (VirtualFile root : ModuleRootManager.getInstance(module).getContentRoots()) {
                    String relativePath = VfsUtilCore.getRelativePath(virtualFile, root);
                    if (relativePath != null) {
                        return relativePath;
                    }
                }
            }
        }

        VirtualFile baseDir = HelpersKt.getProjectBaseDirectory(project);
        if (baseDir != null) {
            String relativePath = VfsUtilCore.getRelativePath(virtualFile, baseDir);
            if (relativePath != null) {
                return relativePath;
            }
        }

        if (!relativeToProjectBaseDir) {
            RootType rootType = RootType.forFile(virtualFile);
            if (rootType != null) {
                VirtualFile scratchRootVirtualFile =
                        VfsUtil.findFileByIoFile(new File(ScratchFileService.getInstance().getRootPath(rootType)), false);
                if (scratchRootVirtualFile != null) {
                    String scratchRelativePath = VfsUtilCore.getRelativePath(virtualFile, scratchRootVirtualFile);
                    if (scratchRelativePath != null) {
                        return scratchRelativePath;
                    }
                }
            }
        }

        return !relativeToProjectBaseDir ? virtualFile.getPath() : "";
    }

    public static boolean isUpsourceCopyReferenceAvailable(@NotNull Project project) {
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        OrderEnumerator orderEntry = projectRootManager.orderEntries();
        final int[] moduleCount = { 0 };
        orderEntry.forEachModule(it -> {
            switch (it.getName()) {
                case "intellij.platform.core":
                case "intellij.platform.core.impl":
                case "intellij.platform.core.ui":
                    moduleCount[0]++;
                    break;
            }
            return moduleCount[0] < 3;
        });

        return moduleCount[0] >= 3;
    }

    public static boolean isUpsourceCopyReferenceAvailable() {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (isUpsourceCopyReferenceAvailable(project)) return true;
        }
        return false;
    }

    public static String getUpsourceReference(@NotNull Project project, @NotNull VirtualFile virtualFile, @NotNull Editor editor) {
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        int selectionStart = caret.getSelectionStart();
        int selectionEnd = caret.getSelectionEnd();
        int caretLine = editor.offsetToLogicalPosition(caret.getOffset()).line + 1;

        String fileFqn = getVirtualFileFqn(virtualFile, project, true);

        if (getInstance().getDocumentSettings().getUseUpsourceURL()) {
            return String.format("https://upsource.jetbrains.com/idea-ce/file/HEAD/%s?nav=%d:%d:focused&line=%d&preview=false", fileFqn, selectionStart, selectionEnd, caretLine);
        } else {
            return String.format("upsource://%s?nav=%d:%d:focused&line=%d&preview=false", fileFqn, selectionStart, selectionEnd, caretLine);
        }
    }
}
