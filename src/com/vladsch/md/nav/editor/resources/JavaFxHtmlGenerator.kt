// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.resources

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.data.MutableDataHolder
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.Escaping
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.md.nav.editor.PreviewFileEditorBase
import com.vladsch.md.nav.editor.util.HtmlGenerator
import com.vladsch.md.nav.parser.Extensions
import com.vladsch.md.nav.parser.PegdownOptionsAdapter
import com.vladsch.md.nav.parser.api.HtmlPurpose
import com.vladsch.md.nav.parser.api.ParserPurpose
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.vcs.MdLinkResolver
import com.vladsch.plugin.util.TimeIt
import com.vladsch.plugin.util.plusAssign
import com.vladsch.plugin.util.suffixWith
import java.util.function.Consumer
import java.util.function.Supplier

class JavaFxHtmlGenerator(linkResolver: MdLinkResolver, renderingProfile: MdRenderingProfile) : HtmlGenerator(linkResolver, renderingProfile) {
    protected var tagRanges: List<TagRange> = listOf()

    override val pegdownFlags: Int
        get() = (this.renderingProfile.parserSettings.pegdownFlags and Extensions.EXTANCHORLINKS_WRAP.inv()) or Extensions.MULTI_LINE_IMAGE_URLS

    override val htmlTagRanges: List<TagRange>
        get() = tagRanges

    override fun toHtml(
        file: PsiFile?,
        charSequence: CharSequence,
        htmlPurpose: HtmlPurpose,
        dataContext: DataContext?,
        exportMap: MutableMap<String, String>?,
        optionsAdjuster: Consumer<MutableDataHolder>?
    ): String {
        val forHtmlExport: Boolean = htmlPurpose.isExport

        @Suppress("NAME_SHADOWING")
        var dataContext = dataContext
        val project = linkResolver.project ?: ProjectManager.getInstance().defaultProject
        val parserSettings = renderingProfile.parserSettings

        val parserOptions = parserSettings.optionsFlags
        val addOns = if (!forHtmlExport || pegdownFlags and Extensions.ANCHORLINKS != 0) Extensions.EXTANCHORLINKS else 0
        val optionAdapter = PegdownOptionsAdapter(pegdownFlags or addOns, parserOptions)
        val options = optionAdapter.getFlexmarkOptions(ParserPurpose.JAVAFX, htmlPurpose, linkResolver, renderingProfile).toMutable()

        options.set(MdNavigatorExtension.RENDERING_PROFILE, Supplier { renderingProfile })

        // add the custom rendering keys that may be defined in the script resources, ie. Prism.js needs language- prefix in code class
        addDataKeys(options)

        if (renderingProfile.htmlSettings.noParaTags) {
            options.set(HtmlRenderer.NO_P_TAGS_USE_BR, true)
        }

        if (forHtmlExport && dataContext != null) {
            dataContext = addHtmlExportData(project, options, dataContext, exportMap)
        }

        optionsAdjuster?.accept(options)

        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        val document: Document = TimeIt.logTimedValue(PreviewFileEditorBase.LOG, "JavaFxHtmlGenerator::toHtml - parse document") {
            parser.parse(BasedSequence.of(charSequence))
        }

        // see if document has includes
        var useDocument = document
        var useRenderer = renderer
        TimeIt.logTime(PreviewFileEditorBase.LOG, "JavaFxHtmlGenerator::toHtml - processIncludes") {
            useDocument = processIncludes(parser, renderer, document, file)
            if (useDocument !== document) {
                // options need to be adjusted to match new document
                useRenderer = HtmlRenderer.builder(useDocument).build()
            }
        }

        val html = TimeIt.logTimedValue(PreviewFileEditorBase.LOG, "JavaFxHtmlGenerator::toHtml - render document") {
            useRenderer.render(useDocument)
        }

        val postProcessedHtml = postProcessHtml(html)

        tagRanges = HtmlRenderer.TAG_RANGES[useDocument]

        return makeHtmlPage(postProcessedHtml, forHtmlExport, dataContext, exportMap)
    }

    override fun makeHtmlPage(html: String, forHtmlExport: Boolean, dataContext: DataContext?, exportMap: MutableMap<String, String>?): String {
        @Suppress("NAME_SHADOWING")
        val dataContext = dataContext ?: SimpleDataContext.getProjectContext(linkResolver.project)
        val fileRef = linkResolver.containingFile
        val result = StringBuilder(html.length + 1000)

        if (renderingProfile.htmlSettings.addDocTypeHtml) result += "<!DOCTYPE html>\n"
        result += "<html>\n<head>\n<meta charset=\"UTF-8\">\n"

        // add all standard header top stuff
        addHeadTopHtml(result, null, forHtmlExport, dataContext, exportMap)

        val noCssNoScripts = noCssNoScripts(forHtmlExport)
        if (!noCssNoScripts) {
            result += "<title>${Escaping.escapeHtml(fileRef.fileName, true)}</title>\n"
        }

        addHeadBottomHtml(result, null, forHtmlExport, dataContext, exportMap)

        result += "</head>\n<body>\n"

        addBodyTopHtml(result, null, forHtmlExport, dataContext, exportMap)

        var gitHubHref = adjustPageRef(linkResolver.projectResolver.getVcsRoot(fileRef)?.urlForVcsRemote(fileRef, !fileRef.isWikiPage, "", null, null), forHtmlExport) ?: ""

        val gitHubClose: String
        if (fileRef.isWikiPage) {
            if (!noCssNoScripts) {
                if (renderingProfile.htmlSettings.addPageHeader) {
                    gitHubHref = """<a href="$gitHubHref" name="wikipage" id="wikipage" class="anchor"><span class="octicon octicon-link"></span>"""
                    gitHubClose = "</a>"

                    result += """<div class="wiki-container">
                <h1>$gitHubHref$gitHubClose${Escaping.escapeHtml(fileRef.fileNameNoExt.replace('-', ' '), true)}</h1>
                """
                }
                result += """
                <article class="wiki-body">
                """
            }
        } else {
            if (!noCssNoScripts) {
                if (renderingProfile.htmlSettings.addPageHeader) {
                    gitHubHref = """<a href="$gitHubHref" name="markdown-page" id="markdown-page" class="page-anchor">"""
                    gitHubClose = "</a>"

                    result += "<div class=\"container\">\n<div id=\"readme\" class=\"boxed-group\">\n<h3>\n   $gitHubHref<span class=\"bookicon octicon-book\"></span>\n$gitHubClose  ${fileRef.fileName}\n</h3>\n"
                }
                result += "<article class=\"markdown-body\">\n"
            }
        }

        result += html.suffixWith('\n')

        if (!noCssNoScripts) {
            result += "</article>\n"
            if (renderingProfile.htmlSettings.addPageHeader) {
                result += "</div>\n"
                result += "</div>\n"
            }
        }

        addBodyBottomHtml(result, null, forHtmlExport, dataContext, exportMap)

        result += "</body>\n</html>\n"

        return result.toString()
    }
}
