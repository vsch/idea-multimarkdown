// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.ide

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.actions.ide.CopyReferenceUtils.getElementsToCopy
import com.vladsch.md.nav.actions.ide.CopyReferenceUtils.getVirtualFileFqn
import com.vladsch.md.nav.actions.ide.CopyReferenceUtils.highlight
import com.vladsch.md.nav.actions.ide.CopyReferenceUtils.setStatusBarText
import com.vladsch.md.nav.settings.MdApplicationSettings
import java.awt.datatransfer.StringSelection
import kotlin.math.max

// CopyPathProvider only available 2019.08.26, so 193+
open class CopySelectionReference : DumbAwareAction() {

    override fun update(e: AnActionEvent) {
        if (isAvailable()) {
            val dataContext = e.dataContext
            val editor = CommonDataKeys.EDITOR.getData(dataContext)
            val project = e.project
            if (project != null) {
                val elements = getElementsToCopy(editor, dataContext)
                val copy = project.let { editor?.let { it1 -> getQualifiedName(it, elements, it1) } }
                if (copy != null) {
                    val text = getText(copy)
                    val description = getDescription(copy)
                    if (text != null) e.presentation.text = text
                    if (description != null) e.presentation.description = description
                    e.presentation.isEnabledAndVisible = true
                    super.update(e)
                    return
                }
            }
        }

        e.presentation.isEnabledAndVisible = false
    }

    fun isAvailable(): Boolean {
//        return ActionManager.getInstance().getAction("CopyPathWithLineNumber") == null
        return MdApplicationSettings.instance.documentSettings.copyPathWithLineNumbers
    }

    fun getDescription(copy: String): String? {
        return MdBundle.message("message.path.to.description", copy)
    }

    fun getText(copy: String): String? {
        return MdBundle.message("message.path.to.label", copy)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = getEventProject(e)
        val dataContext = e.dataContext
        val editor = CommonDataKeys.EDITOR.getData(dataContext)

        val elements = getElementsToCopy(editor, dataContext)
        val copy = project?.let { editor?.let { it1 -> getQualifiedName(it, elements, it1) } }

        CopyPasteManager.getInstance().setContents(StringSelection(copy))
        setStatusBarText(project, MdBundle.message("message.0.path.has.been.copied", copy))

        highlight(editor, project, elements)
    }

    fun getQualifiedName(project: Project, elements: List<PsiElement>, editor: Editor?): String? {
        if (elements.isEmpty()) {
            return getPathToElement(project, editor?.document?.let { FileDocumentManager.getInstance().getFile(it) }, editor)
        }

        val refs = elements.map { element ->
            val virtualFile = if (element is PsiFileSystemItem) element.virtualFile else element.containingFile.virtualFile

            getPathToElement(project, virtualFile ?: return@map null, editor) ?: return@map null
        }.filterNotNull()

        return if (refs.isNotEmpty()) refs.joinToString("\n") else null
    }

    fun getPathToElement(project: Project, virtualFile: VirtualFile?, editor: Editor?): String? {
        return if (virtualFile == null || editor == null || !editor.selectionModel.hasSelection()) null
        else editor.let {
            val startLine = editor.offsetToLogicalPosition(editor.selectionModel.selectionStart).line + 1
            val selectionEnd = editor.selectionModel.selectionEnd
            val endLineOffset = if (selectionEnd > 0 && selectionEnd - 1 < editor.document.textLength && editor.document.charsSequence[selectionEnd - 1] == '\n') 1 else 0
            val endLine = editor.offsetToLogicalPosition(selectionEnd).line + endLineOffset
            getVirtualFileFqn(virtualFile, project, false) + ":" + startLine + "-" + max(endLine - 1, startLine)
        }
    }
}

