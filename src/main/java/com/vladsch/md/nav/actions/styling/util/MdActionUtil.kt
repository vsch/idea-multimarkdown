// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling.util

import com.intellij.lang.ASTNode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.Couple
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.editor.split.SplitFileEditor
import com.vladsch.md.nav.psi.element.MdFile

object MdActionUtil {
    fun findSplitEditor(e: AnActionEvent): SplitFileEditor<*, *>? {
        val editor = e.getData(PlatformDataKeys.FILE_EDITOR)
        if (editor is SplitFileEditor<*, *>) {
            return editor
        } else {
            return SplitFileEditor.PARENT_SPLIT_KEY.get(editor)
        }
    }

    fun getTextEditor(splitEditor: SplitFileEditor<*, *>, onlyIfVisible: Boolean): Editor? {
        if (splitEditor.mainEditor !is TextEditor) {
            return null
        }
        val mainEditor = splitEditor.mainEditor as TextEditor
        if (onlyIfVisible && !mainEditor.component.isVisible) {
            return null
        }

        return mainEditor.editor
    }

    data class ActionParameters(val project: Project, val editor: Editor, val mdFile: MdFile)

    fun getProjectEditorPsiFile(e: AnActionEvent): ActionParameters? {
        val project = e.project ?: return null
        val editor = findMarkdownEditor(e) ?: return null
        val psiFile = getPsiFile(e) as? MdFile ?: return null
        if (!psiFile.isValid) return null
        return ActionParameters(project, editor, psiFile)
    }

    fun getConditionBuilder(e: AnActionEvent, anAction: AnAction, andThen: ((DisabledConditionBuilder, ActionParameters) -> Unit)? = null): DisabledConditionBuilder {
        return withConditionBuilder(e, DisabledConditionBuilder(e, anAction), andThen)
    }

    fun <T : DisabledConditionBuilder> withConditionBuilder(e: AnActionEvent, builder: T, andThen: ((T, ActionParameters) -> Unit)? = null): T {
        val project = e.project
        val editor = findMarkdownEditor(e)
        val psiFile = getPsiFile(e) as? MdFile
        @Suppress("UNCHECKED_CAST")
        return builder
            .notNull(project)
            .notNull(editor)
            .notNull(psiFile)
            .isValid(psiFile)
            .isVisible(editor)
            .and {
                andThen?.invoke(it as T, ActionParameters(project!!, editor!!, psiFile!!))
            } as T
    }

    fun getPsiFile(e: AnActionEvent): PsiFile? {
        return e.getData(CommonDataKeys.PSI_FILE)
    }

    fun getCommonParentOfType(psiFile: PsiFile, range: TextRange, isNestable: Boolean, elementCondition: Condition<PsiElement>): PsiElement? {
        var elements = getElementsUnderCaretOrSelection(psiFile, range.startOffset, range.endOffset)
        var parentOfType: PsiElement? = if (elements == null) null else getCommonParentOfType(elements.getFirst(), elements.getSecond(), elementCondition)
        if (parentOfType != null || !isNestable || range.startOffset != range.endOffset || range.startOffset == 0) {
            return parentOfType
        }

        // try previous position
        elements = getElementsUnderCaretOrSelection(psiFile, range.startOffset - 1, range.startOffset - 1)
        parentOfType = if (elements == null) null else getCommonParentOfType(elements.getFirst(), elements.getSecond(), elementCondition)
        return parentOfType
    }

    fun isMarkdownMainEditorVisible(e: AnActionEvent): Boolean {
        val splitEditor = findSplitEditor(e)
        if (splitEditor == null) {
            // This fallback is used primarily for testing
            val psiFile = getPsiFile(e)
            return psiFile != null && psiFile.language === MdLanguage.INSTANCE && ApplicationManager.getApplication().isUnitTestMode
        }

        if (splitEditor.mainEditor !is TextEditor) {
            return false
        }
        return splitEditor.mainEditor.component.isVisible
    }

    fun findMarkdownEditor(e: AnActionEvent, onlyIfVisible: Boolean = true): Editor? {
        val splitEditor = findSplitEditor(e)
        if (splitEditor == null) {
            // This fallback is used primarily for testing
            val psiFile = getPsiFile(e)
            if (psiFile != null && psiFile.language === MdLanguage.INSTANCE && ApplicationManager.getApplication().isUnitTestMode) {
                return e.getData(CommonDataKeys.EDITOR)
            } else {
                return null
            }
        }

        if (splitEditor.mainEditor !is TextEditor) {
            return null
        }

        val mainEditor = splitEditor.mainEditor as TextEditor

        return if (!onlyIfVisible || mainEditor.component.isVisible) mainEditor.editor else null
    }

    fun getElementsUnderCaretOrSelection(file: PsiFile, startOffset: Int, endOffset: Int): Couple<PsiElement>? {
        if (startOffset == endOffset) {
            val element = file.findElementAt(startOffset) ?: return null
            return Couple.of(element, element)
        } else {
            val startElement = file.findElementAt(startOffset)
            val endElement = file.findElementAt(endOffset - 1)
            if (startElement == null || endElement == null) {
                return null
            }
            return Couple.of(startElement, endElement)
        }
    }

    fun getCommonParentOfType(element1: PsiElement,
        element2: PsiElement,
        elementType: IElementType): PsiElement? {
        val base = PsiTreeUtil.findCommonParent(element1, element2)

        return PsiTreeUtil.findFirstParent(base, false) { element ->
            val node = element.node
            node != null && node.elementType === elementType
        }
    }

    fun getCommonParentOfType(element1: PsiElement,
        element2: PsiElement,
        condition: Condition<PsiElement>): PsiElement? {
        val base = PsiTreeUtil.findCommonParent(element1, element2) ?: return null

        return if (base.node != null) PsiTreeUtil.findFirstParent(base, false, condition) else null
    }

    fun getPreviousSibling(node: ASTNode, nodeType: IElementType): ASTNode? {
        var sibling: ASTNode? = node

        while (sibling != null && sibling.elementType !== nodeType) sibling = sibling.treePrev
        return sibling
    }

    fun getNextSibling(node: ASTNode, nodeType: IElementType): ASTNode? {
        var sibling: ASTNode? = node

        while (sibling != null && sibling.elementType !== nodeType) sibling = sibling.treeNext
        return sibling
    }
}
