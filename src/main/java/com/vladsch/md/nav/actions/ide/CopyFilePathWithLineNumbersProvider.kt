// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.ide

import com.intellij.ide.actions.CopyPathProvider
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.actions.ide.CopyReferenceUtils.getVirtualFileFqn
import com.vladsch.md.nav.settings.MdApplicationSettings

// CopyPathProvider only available 2019.08.26, so 193+
open class CopyFilePathWithLineNumbersProvider : CopyPathProvider() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.text = MdBundle.message("message.path.to.selection.with.line.numbers.label")
        e.presentation.isVisible = MdApplicationSettings.instance.documentSettings.copyPathWithLineNumbers
    }

    override fun getPathToElement(project: Project, virtualFile: VirtualFile?, editor: Editor?): String? {
        return if (virtualFile == null || editor == null || !editor.selectionModel.hasSelection()) null
        else editor.let {
            val startLine = editor.offsetToLogicalPosition(editor.selectionModel.selectionStart).line + 1
            val selectionEnd = editor.selectionModel.selectionEnd
            val endLineOffset = if (selectionEnd + 1 < editor.document.textLength && editor.document.charsSequence[selectionEnd] != '\n') 1 else 0
            val endLine = editor.offsetToLogicalPosition(selectionEnd).line + endLineOffset
            getVirtualFileFqn(virtualFile, project, false) + ":" + startLine + "-" + endLine
        }
    }
}
