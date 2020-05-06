// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.ide

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.settings.MdApplicationSettings

// CopyPathProvider only available 2019.08.26, so 193+
open class CopyFileBareNameProvider : CopyFileNameProvider() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.text = MdBundle.message("message.file.bare.name.label")
        e.presentation.isVisible = MdApplicationSettings.instance.documentSettings.copyPathBareFileName
    }

    override fun getPathToElement(project: Project, virtualFile: VirtualFile?, editor: Editor?): String? {
        return when {
            virtualFile == null -> null
            virtualFile.isDirectory -> virtualFile.name
            else -> virtualFile.nameWithoutExtension
        }
    }
}
