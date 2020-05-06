// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.ide

import com.intellij.ide.actions.CopyPathProvider
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.settings.MdApplicationSettings
import icons.MdIcons

// CopyPathProvider only available 2019.08.26, so 193+
open class CopyUpsourceFilePathWithLineNumbersProvider : CopyPathProvider() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.text = MdBundle.message("message.upsource.path.to.selection.with.line.numbers.label")
        var available = MdApplicationSettings.instance.documentSettings.copyUpsourcePathWithLineNumbers
        if (available) {
            val project = e.project
            if (project != null) {
                available = CopyReferenceUtils.isUpsourceCopyReferenceAvailable(project)
            }
        }
        e.presentation.isVisible = available
        e.presentation.icon = MdIcons.LinkTypes.Upsource
    }

    override fun getPathToElement(project: Project, virtualFile: VirtualFile?, editor: Editor?): String? {
        return if (virtualFile == null || editor == null) null
        else editor.let {
            CopyReferenceUtils.getUpsourceReference(project, virtualFile, editor)
        }
    }
}
