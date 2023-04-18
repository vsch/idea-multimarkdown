// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.intellij.openapi.extensions.ExtensionPointName
import com.vladsch.md.nav.settings.Item
import com.vladsch.md.nav.settings.StateHolder

abstract class HtmlCssResourceProvider : HtmlResourceProvider() {

    abstract override val INFO: HtmlCssResourceProvider.Info
    abstract val cssResource: HtmlCssResource

    class Info(providerId: String, name: String, factory: () -> Item<StateHolder>) : HtmlResourceProvider.Info(providerId, name, factory) {
        constructor(providerId: String, name: String) : this(providerId, name, { Info() })
        constructor(other: Info) : this(other.providerId, other.name)
        constructor() : this("", "")
    }

    override fun isSupportedSetting(settingName: String): Boolean {
        return false
    }

    companion object : HtmlProviderCompanion<HtmlCssResourceProvider>() {
        override val EP_NAME: ExtensionPointName<HtmlCssResourceProvider> = ExtensionPointName.create("com.vladsch.idea.multimarkdown.html.css.provider")
    }
}
