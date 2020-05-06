// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.ide

import com.intellij.ide.actions.CopyPathProvider
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.impl.file.PsiPackageBase
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.settings.MdApplicationSettings

// CopyPathProvider only available 2019.08.26, so 193+
open class CopyFileNameProvider : CopyPathProvider() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.text = MdBundle.message("message.file.name.label")
        e.presentation.isVisible = MdApplicationSettings.instance.documentSettings.copyPathFileName
    }

    // First one is for release 2019.3, second is for snapshot.
//    override fun getQualifiedName(project: Project, elements: List<PsiElement>, editor: Editor?): String? {
    override fun getQualifiedName(project: Project, elements: List<PsiElement>, editor: Editor?, dataContext: DataContext): String? {
        val refs = elements
            .mapNotNull {
                getPathToElement(project, (
                    when (it) {
                        is PsiFileSystemItem -> it.virtualFile
                        is PsiPackageBase -> it.directories.firstOrNull()?.virtualFile
                        else -> it.containingFile?.virtualFile
                    }), editor)
            }
//            .ifEmpty { CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext)?.mapNotNull { getPathToElement(project, it, editor) } }
            .orEmpty()
            .filter { !it.isBlank() }

        return if (refs.isNotEmpty()) refs.joinToString("\n") else null
    }

    override fun getPathToElement(project: Project, virtualFile: VirtualFile?, editor: Editor?): String? {
        return when {
            virtualFile == null -> null
            virtualFile.isDirectory -> "${virtualFile.name}/"
            else -> virtualFile.name
        }
    }
}
