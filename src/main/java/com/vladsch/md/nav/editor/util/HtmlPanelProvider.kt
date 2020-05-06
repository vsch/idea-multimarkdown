// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.vladsch.md.nav.editor.HtmlPanelHost
import com.vladsch.md.nav.settings.Item
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.StateHolder
import com.vladsch.md.nav.vcs.MdLinkResolver
import java.util.*
import javax.swing.JComponent

abstract class HtmlPanelProvider : HtmlProvider() {
    override val HAS_PARENT = false
    abstract override val INFO: HtmlPanelProvider.Info
    abstract val isAvailable: AvailabilityInfo
    abstract fun createHtmlPanel(project: Project, htmlPanelHost: HtmlPanelHost): HtmlPanel

    open class Info(providerId: String, name: String, factory: () -> Item<StateHolder>) : HtmlProvider.Info(providerId, name, factory) {
        constructor(providerId: String, name: String) : this(providerId, name, { Info() })
        constructor(other: Info) : this(other.providerId, other.name)
        constructor() : this("", "")
    }

    abstract class AvailabilityInfo {
        abstract fun checkAvailability(parentComponent: JComponent): Boolean

        // QUERY: don't think so: revert back to checking for availability and downloading for Java9+?
        companion object {

            @JvmField
            val AVAILABLE: AvailabilityInfo = object : AvailabilityInfo() {
                override fun checkAvailability(parentComponent: JComponent): Boolean {
                    return true
                }
            }

            @JvmField
            val UNAVAILABLE: AvailabilityInfo = object : AvailabilityInfo() {
                override fun checkAvailability(parentComponent: JComponent): Boolean {
                    return false
                }
            }

            @JvmField
            val AVAILABLE_NOTUSED: AvailabilityInfo = object : AvailabilityInfo() {
                override fun checkAvailability(parentComponent: JComponent): Boolean {
                    return true
                }
            }
        }
    }

    open fun injectHtmlResource(project: Project, applicationSettings: MdApplicationSettings, renderingProfile: MdRenderingProfile, injections: ArrayList<InjectHtmlResource?>, forHtmlExport: Boolean, dataContext: DataContext) {
    }

    fun injectHtmlResource(linkResolver: MdLinkResolver, renderingProfile: MdRenderingProfile, injections: ArrayList<InjectHtmlResource?>): Unit {
        val dataContext = SimpleDataContext.getSimpleContext(Collections.emptyMap(), null)
        injectHtmlResource(linkResolver.project!!, MdApplicationSettings.instance, renderingProfile, injections, false, dataContext)
    }

    companion object : HtmlProviderCompanion<HtmlPanelProvider>() {
        override val EP_NAME = ExtensionPointName.create<HtmlPanelProvider>("com.vladsch.idea.multimarkdown.html.panel.provider")

        @JvmStatic
        override fun getFromInfoOrDefault(providerInfo: HtmlProvider.Info): HtmlPanelProvider = super.getFromInfoOrDefault(providerInfo)

        @JvmStatic
        override fun getFromId(providerId: String): HtmlPanelProvider? = super.getFromId(providerId)
    }
}
