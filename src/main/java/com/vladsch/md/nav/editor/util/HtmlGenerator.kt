// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.data.MutableDataHolder
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.md.nav.editor.api.MdHtmlGeneratorExtension
import com.vladsch.md.nav.parser.api.HtmlPurpose
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.vcs.MdLinkResolver
import com.vladsch.plugin.util.plusAssign
import java.util.*
import java.util.function.Consumer

abstract class HtmlGenerator(val linkResolver: MdLinkResolver, val renderingProfile: MdRenderingProfile) {

    abstract val pegdownFlags: Int
    abstract fun toHtml(file: PsiFile?, charSequence: CharSequence, htmlPurpose: HtmlPurpose, dataContext: DataContext?, exportMap: MutableMap<String, String>? = null, optionsAdjuster: Consumer<MutableDataHolder>?): String
    abstract fun makeHtmlPage(html: String, forHtmlExport: Boolean, dataContext: DataContext?, exportMap: MutableMap<String, String>? = null): String
    abstract val htmlTagRanges: List<TagRange>
    val injections: List<InjectHtmlResource>

    init {
        val rawInjections = ArrayList<InjectHtmlResource?>()
        renderingProfile.injectHtmlResource(linkResolver, rawInjections)
        injections = rawInjections.filterNotNull().sortedBy { it.htmlPlacement.ordinal }
    }

    fun postProcessHtml(html: String): String {
        var useHtml = html
        for (handler in MdHtmlGeneratorExtension.EXTENSIONS.value) {
            useHtml = handler.postProcessHtml(useHtml, renderingProfile)
        }
        return useHtml
    }

    fun addHtmlExportData(project: Project, options: MutableDataHolder, dataContext: DataContext, exportMap: Map<String, String>?): DataContext {
        var useDataContext = dataContext
        for (handler in MdHtmlGeneratorExtension.EXTENSIONS.value) {
            useDataContext = handler.addHtmlExportData(project, renderingProfile, options, useDataContext, exportMap)
        }
        return useDataContext
    }

    fun addHtmlInjections(result: StringBuilder, isByScript: Boolean?, filterList: List<HtmlPlacement>?, forHtmlExport: Boolean, dataContext: DataContext, exportMap: MutableMap<String, String>?) {
        val filteredInjections: List<InjectHtmlResource> = injections.filter {
            if (isByScript == null || it.isByScript == isByScript) {
                if (filterList == null) {
                    true
                } else {
                    var isInArray = false
                    for (placement in filterList) {
                        if (it.htmlPlacement == placement) {
                            isInArray = true
                            break
                        }
                    }

                    isInArray
                }
            } else {
                false
            }
        }

        for (handler in MdHtmlGeneratorExtension.EXTENSIONS.value) {
            if (handler.addHtmlInjections(result, renderingProfile, linkResolver, filteredInjections, forHtmlExport, dataContext, exportMap)) return;
        }

        // use default of appending injections
        for (injection in filteredInjections) {
            result += injection.htmlText(null)
        }
    }

    fun addDataKeys(result: MutableDataHolder) {
        for (resource in renderingProfile.cssSettings.scriptResources) {
            result.setAll(resource)
        }
    }

    open fun addHeadTopHtml(result: StringBuilder, isByScript: Boolean?, forHtmlExport: Boolean, dataContext: DataContext, exportMap: MutableMap<String, String>?) {
        addHtmlInjections(result, isByScript, HtmlPlacement.HEAD_TOPS, forHtmlExport, dataContext, exportMap)
    }

    open fun addHeadBottomHtml(result: StringBuilder, isByScript: Boolean?, forHtmlExport: Boolean, dataContext: DataContext, exportMap: MutableMap<String, String>?) {
        addHtmlInjections(result, isByScript, HtmlPlacement.HEAD_BOTTOMS, forHtmlExport, dataContext, exportMap)
    }

    open fun addBodyTopHtml(result: StringBuilder, isByScript: Boolean?, forHtmlExport: Boolean, dataContext: DataContext, exportMap: MutableMap<String, String>?) {
        addHtmlInjections(result, isByScript, HtmlPlacement.BODY_TOPS, forHtmlExport, dataContext, exportMap)
    }

    open fun addBodyBottomHtml(result: StringBuilder, isByScript: Boolean?, forHtmlExport: Boolean, dataContext: DataContext, exportMap: MutableMap<String, String>?) {
        addHtmlInjections(result, isByScript, HtmlPlacement.BODY_BOTTOMS, forHtmlExport, dataContext, exportMap)
    }

    protected fun processIncludes(parser: Parser, renderer: HtmlRenderer, document: Document, file: PsiFile?): Document {
        for (handler in MdHtmlGeneratorExtension.EXTENSIONS.value) {
            val processed = handler.processIncludes(parser, renderer, document, file)
            if (processed !== document) return processed;
        }

        return document
    }

    protected fun adjustPageRef(gitHubPageRef: String?, forHtmlExport: Boolean): String? {
        @Suppress("NAME_SHADOWING")
        var gitHubPageRef = gitHubPageRef
        for (handler in MdHtmlGeneratorExtension.EXTENSIONS.value) {
            gitHubPageRef = handler.adjustPageRef(gitHubPageRef, forHtmlExport, renderingProfile);
        }
        return gitHubPageRef
    }

    protected fun noCssNoScripts(forHtmlExport: Boolean): Boolean {
        for (handler in MdHtmlGeneratorExtension.EXTENSIONS.value) {
            return handler.noStylesheets(forHtmlExport, renderingProfile) ?: continue;
        }
        return false
    }
}
