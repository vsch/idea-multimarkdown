// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.editor.PreviewFileEditorBase
import com.vladsch.md.nav.editor.util.HtmlGenerator
import com.vladsch.md.nav.parser.PegdownOptionsAdapter
import com.vladsch.md.nav.parser.api.HtmlPurpose
import com.vladsch.md.nav.parser.api.ParserPurpose
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.vcs.MdLinkResolver
import com.vladsch.plugin.util.TimeIt
import com.vladsch.plugin.util.plusAssign
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.regex.Pattern

class SwingHtmlGenerator(linkResolver: MdLinkResolver, renderingProfile: MdRenderingProfile) : HtmlGenerator(linkResolver, renderingProfile) {
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
        val options = optionAdapter.getFlexmarkOptions(ParserPurpose.SWING, htmlPurpose, linkResolver, renderingProfile).toMutable()
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

        val document: Document = TimeIt.logTimedValue(PreviewFileEditorBase.LOG, "SwingHtmlGenerator::toHtml - parse document") {
            parser.parse(BasedSequence.of(charSequence))
        }

        // see if document has includes
        var useDocument = document
        var useRenderer = renderer
        TimeIt.logTime(PreviewFileEditorBase.LOG, "SwingHtmlGenerator::toHtml - processIncludes") {
            useDocument = processIncludes(parser, renderer, document, file)
            if (useDocument !== document) {
                // options need to be adjusted to match new document
                useRenderer = HtmlRenderer.builder(useDocument).build()
            }
        }

        val html = TimeIt.logTimedValue(PreviewFileEditorBase.LOG, "SwingHtmlGenerator::toHtml - render document") {
            useRenderer.render(useDocument)
        }

        val postProcessedHtml = postProcessHtml(html)

        tagRanges = HtmlRenderer.TAG_RANGES[useDocument]

        return makeHtmlPage(postProcessedHtml, forHtmlExport, dataContext, exportMap)
    }

    override fun makeHtmlPage(html: String, forHtmlExport: Boolean, dataContext: DataContext?, exportMap: MutableMap<String, String>?): String {
        @Suppress("NAME_SHADOWING")
        val dataContext = dataContext ?: SimpleDataContext.getProjectContext(linkResolver.project!!)
        val file = linkResolver.containingFile
        val regex = "<hr(\\s+md-pos=\"[^\"]+\")?\\s*/?>|<del>|</del>|</p>|<kbd>|</kbd>|<var>|</var>"
        val result = StringBuilder(html.length + 1000)

        result += "<html>\n<head>\n<meta charset=\"UTF-8\">\n"

        addHeadTopHtml(result, false, forHtmlExport, dataContext, exportMap)
        addHeadBottomHtml(result, false, forHtmlExport, dataContext, exportMap)
        result += "</head>\n"

        val isWikiDocument = linkResolver.containingFile.isWikiPage
        var gitHubHref = adjustPageRef(linkResolver.projectResolver.getVcsRoot(file)?.urlForVcsRemote(file, !isWikiDocument, "", null, null), forHtmlExport) ?: ""

        var gitHubClose = ""
        val linkTitle = MdBundle.message("html-generator.document-click.link-title")

        if (gitHubHref.isNotEmpty()) {
            gitHubHref = """<a href="$gitHubHref" name="wikipage" id="wikipage" title="$linkTitle">"""
            gitHubClose = "</a>"
        }

        if (isWikiDocument) {
            result += """<body class="multimarkdown-wiki-preview">
<div class="content">
"""
            addBodyTopHtml(result, false, forHtmlExport, dataContext, exportMap)
            result += """<h1 class="first-child">$gitHubHref${Escaping.escapeHtml(file.fileNameNoExt.replace('-', ' '), true)}$gitHubClose</h1>
"""
        } else {
            if (noCssNoScripts(forHtmlExport)) {
                result += """<body>
"""
            } else {
                result += """<body class="multimarkdown-preview">
"""
            }

            addBodyTopHtml(result, false, forHtmlExport, dataContext, exportMap)

            if (renderingProfile.htmlSettings.addPageHeader) {
                result += """<div class="content">
<div class="page-header">$gitHubHref${Escaping.escapeHtml(file.fileName.replace('-', ' '), true)}$gitHubClose</div>
<div class="hr"></div>
"""
            }
        }

        val p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val m = p.matcher(html)
        var lastPos = 0

        while (m.find()) {
            val found = m.group()
            if (lastPos < m.start(0)) {
                result += html.substring(lastPos, m.start(0))
            }

            if (found == "</p>") {
                result += found
            } else if (found.startsWith("<br")) {
                result += "<br/>\n"
            } else if (found == "/>") {
                result += ">"
            } else if (found.startsWith("<hr")) {
                result += "<div class=\"hr\"${m.group(1)}>&nbsp;</div>"
            } else if (found == "<del>") {
                result += "<span class=\"del\">"
            } else if (found == "</del>") {
                result += "</span>"
            } else if (found == "<kbd>") {
                result += "<span class=\"kbd\">"
            } else if (found == "</kbd>") {
                result += "</span>"
            } else if (found == "<code>") {
                result += "<span class=\"code\">"
            } else if (found == "</code>") {
                result += "</span>"
            } else if (found == "<var>") {
                result += "<span class=\"var\">"
            } else if (found == "</var>") {
                result += "</span>"
            }

            lastPos = m.end(0)
        }

        if (lastPos < html.length) {
            result += html.substring(lastPos)
        }

        if (result.last() != '\n') {
            result += '\n'
        }

        if (renderingProfile.htmlSettings.addPageHeader) {
            result += "</div>\n"
        }

        addBodyBottomHtml(result, false, forHtmlExport, dataContext, exportMap)
        result += "</body>\n</html>\n"

        return result.toString()
    }
}
