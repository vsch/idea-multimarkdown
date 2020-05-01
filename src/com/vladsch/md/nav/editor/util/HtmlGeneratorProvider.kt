// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.intellij.openapi.extensions.ExtensionPointName
import com.vladsch.md.nav.settings.Item
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.StateHolder
import com.vladsch.md.nav.vcs.MdLinkResolver
import java.util.*

abstract class HtmlGeneratorProvider : HtmlProvider() {
    abstract override val INFO: HtmlGeneratorProvider.Info
    abstract fun createHtmlGenerator(linkResolver: MdLinkResolver, renderingProfile: MdRenderingProfile): HtmlGenerator
    abstract val cssProvider: HtmlCssResourceProvider
    abstract val scriptProviders: Array<HtmlScriptResourceProvider>

    val scriptProvidersInfo: Array<HtmlScriptResourceProvider.Info>
        get() {
            val total = ArrayList<HtmlScriptResourceProvider.Info>()
            for (it in scriptProviders) {
                total.add(it.INFO)
            }
            return total.toTypedArray()
        }

    open class Info(providerId: String, name: String, factory: () -> Item<StateHolder>) : HtmlProvider.Info(providerId, name, factory) {
        constructor(providerId: String, name: String) : this(providerId, name, { Info() })
        constructor(other: Info) : this(other.providerId, other.name)
        constructor() : this("", "")
    }

    override fun isSupportedSetting(settingName: String): Boolean {
        return false
    }

    companion object : HtmlProviderCompanion<HtmlGeneratorProvider>() {
        override val EP_NAME: ExtensionPointName<HtmlGeneratorProvider> = ExtensionPointName.create("com.vladsch.idea.multimarkdown.html.generator.provider")
    }
}
