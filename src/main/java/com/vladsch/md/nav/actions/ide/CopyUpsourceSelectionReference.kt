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
import com.vladsch.md.nav.actions.ide.CopyReferenceUtils.highlight
import com.vladsch.md.nav.actions.ide.CopyReferenceUtils.setStatusBarText
import com.vladsch.md.nav.settings.MdApplicationSettings
import java.awt.datatransfer.StringSelection

// CopyPathProvider only available 2019.08.26, so 193+
open class CopyUpsourceSelectionReference : DumbAwareAction() {

    override fun update(e: AnActionEvent) {
        if (isAvailable(e)) {
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

    fun isAvailable(e: AnActionEvent): Boolean {
        var available = MdApplicationSettings.instance.documentSettings.copyUpsourcePathWithLineNumbers
        if (available) {
            val project = e.project
            if (project != null) {
                available = CopyReferenceUtils.isUpsourceCopyReferenceAvailable(project)
            }
        }
        return available
    }

    fun getDescription(copy: String): String? {
        return MdBundle.message("message.upsource.path.to.description", copy)
    }

    fun getText(copy: String): String? {
        return MdBundle.message("message.upsource.path.to.label", copy)
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
        return if (virtualFile == null || editor == null) null
        else editor.let {
            CopyReferenceUtils.getUpsourceReference(project, virtualFile, editor)
        }
    }
}

