// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor

import com.intellij.openapi.vfs.VirtualFile
import com.vladsch.md.nav.settings.MdRenderingProfile

interface HtmlPanelHost : ExternalLinkLauncher {
    fun getVirtualFile(): VirtualFile
    fun synchronizeCaretPos(offset: Int)
    fun getRenderingProfile(): MdRenderingProfile
    fun isHighlightEnabled(): Boolean
    fun toggleTask(pos: String)
}

