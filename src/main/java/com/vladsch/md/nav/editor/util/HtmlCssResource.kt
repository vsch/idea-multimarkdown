// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.plugin.util.ifElse
import java.util.*

open class HtmlCssResource(
    override val providerInfo: HtmlCssResourceProvider.Info      // provider info
    , val darkCss: String                           // resource path for dark scheme stylesheet
    , val lightCss: String                          // resource path for light scheme stylesheet
    , val layoutCss: String?                        // resource path for layout stylesheet, optional
) : HtmlResource() {

    open val darkCssUrl: String = resourceFileUrl(darkCss, javaClass)
    open val lightCssUrl: String = resourceFileUrl(lightCss, javaClass)
    open val layoutCssUrl: String? = if (layoutCss == null) null else resourceFileUrl(layoutCss, javaClass)

    open fun darkCssUrl(parentDir: String): String {
        return resourceFileUrl(darkCss, javaClass)
    }

    open fun lightCssUrl(parentDir: String): String {
        return resourceFileUrl(lightCss, javaClass)
    }

    open fun layoutCssUrl(parentDir: String): String? {
        return if (layoutCss == null) null else resourceFileUrl(layoutCss, javaClass)
    }

    open val isByScript: Boolean
        get() {
            val parentInfo = HtmlCssResourceProvider.getFromId(providerInfo.providerId)?.HAS_PARENT ?: true
            return parentInfo
        }

    override fun injectHtmlResource(
        project: Project,
        applicationSettings: MdApplicationSettings,
        renderingProfile: MdRenderingProfile,
        injections: ArrayList<InjectHtmlResource?>,
        forHtmlExport: Boolean,
        dataContext: DataContext
    ) {
        val isDarkTheme = renderingProfile.cssSettings.isDarkTheme
        injectCssUrl(injections, true, false, isByScript,
            getInjectionUrl(
                project,
                isDarkTheme.ifElse(darkCssUrl, lightCssUrl),
                isDarkTheme.ifElse(darkCss, lightCss),
                renderingProfile,
                forHtmlExport,
                dataContext
            )
        )
        injectCssUrl(injections, true, true, isByScript, getInjectionUrl(project, layoutCssUrl, layoutCss, renderingProfile, forHtmlExport, dataContext))
    }
}
