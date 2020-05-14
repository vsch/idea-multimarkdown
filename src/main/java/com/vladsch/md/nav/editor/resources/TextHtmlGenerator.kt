// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.resources

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.data.MutableDataHolder
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.md.nav.editor.PreviewFileEditorBase
import com.vladsch.md.nav.editor.util.HtmlGenerator
import com.vladsch.md.nav.parser.PegdownOptionsAdapter
import com.vladsch.md.nav.parser.api.HtmlPurpose
import com.vladsch.md.nav.parser.api.ParserPurpose
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.vcs.MdLinkResolver
import com.vladsch.plugin.util.TimeIt
import java.util.function.Consumer
import java.util.function.Supplier

class TextHtmlGenerator(linkResolver: MdLinkResolver, renderingProfile: MdRenderingProfile) : HtmlGenerator(linkResolver, renderingProfile) {
    protected var tagRanges: List<TagRange> = listOf()

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
        val forHtmlExport = htmlPurpose == HtmlPurpose.EXPORT

        @Suppress("NAME_SHADOWING")
        var dataContext = dataContext
        val optionAdapter = PegdownOptionsAdapter()
        val options = optionAdapter.getFlexmarkOptions(ParserPurpose.HTML, htmlPurpose, linkResolver, renderingProfile).toMutable()
        val project = linkResolver.project ?: ProjectManager.getInstance().defaultProject

        options.set(MdNavigatorExtension.RENDERING_PROFILE, Supplier { renderingProfile })

        if (renderingProfile.htmlSettings.noParaTags) {
            options.set(HtmlRenderer.NO_P_TAGS_USE_BR, true)
        }

        if (forHtmlExport && dataContext != null) {
            dataContext = addHtmlExportData(project, options, dataContext, exportMap)
        }

        optionsAdjuster?.accept(options)

        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        val document: Document = TimeIt.logTimedValue(PreviewFileEditorBase.LOG, "TextHtmlGenerator::toHtml - parse document") {
            parser.parse(BasedSequence.of(charSequence))
        }

        // see if document has includes
        var useDocument = document
        var useRenderer = renderer
        TimeIt.logTime(PreviewFileEditorBase.LOG, "TextHtmlGenerator::toHtml - processIncludes") {
            useDocument = processIncludes(parser, renderer, document, file)
            if (useDocument !== document) {
                // options need to be adjusted to match new document
                useRenderer = HtmlRenderer.builder(useDocument).build()
            }
        }

        val html = TimeIt.logTimedValue(PreviewFileEditorBase.LOG, "TextHtmlGenerator::toHtml - render document") {
            useRenderer.render(useDocument)
        }

        val postProcessedHtml = postProcessHtml(html)

        tagRanges = HtmlRenderer.TAG_RANGES[useDocument]

        return makeHtmlPage(postProcessedHtml, forHtmlExport, dataContext, exportMap)
    }

    override fun makeHtmlPage(html: String, forHtmlExport: Boolean, dataContext: DataContext?, exportMap: MutableMap<String, String>?): String {
        return html + "\n"
    }
}
