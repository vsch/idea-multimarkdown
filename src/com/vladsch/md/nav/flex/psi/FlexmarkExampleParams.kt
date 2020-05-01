// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.vladsch.md.nav.psi.element.MdVerbatim
import com.vladsch.plugin.util.nullIfEmpty
import com.vladsch.plugin.util.suffixWith

data class FlexmarkExampleParams(val section: String?,
    val number: String?,
    val source: String?,
    val html: String?,
    val ast: String?,
    val options: List<String>?,
    val useTestExample:Boolean = false
) {

    companion object {
        private fun content(element: MdVerbatim?): String? {
            if (element == null) return null
            val contentElement = element.contentElement
            if (contentElement != null) return contentElement.text.suffixWith('\n')
            //            if (element.node.treeNext.elementType == MultiMarkdownTypes.EOL) return "\n"
            return ""
        }
    }

    constructor(example: FlexmarkExample) : this(
        example.section,
        example.number,
        content(example.source),
        content(example.html),
        content(example.ast),
        example.optionsList?.optionTexts.nullIfEmpty()
    )

    fun withCoords(section: String?, number: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source, html, ast, options)
    }

    fun forTest(): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source, html, ast, options, true)
    }

    fun forTest(useTestExample: Boolean): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source, html, ast, options, useTestExample)
    }

    fun withoutCoords(): FlexmarkExampleParams {
        return FlexmarkExampleParams(null, null, source, html, ast, options)
    }

    val haveCoords: Boolean get() = section != null || number != null

    fun withSection(section: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source, html, ast, options)
    }

    fun withNumber(number: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source, html, ast, options)
    }

    fun withSource(source: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source?.suffixWith('\n'), html, ast, options)
    }

    fun withHtml(html: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source, html?.suffixWith('\n'), ast, options)
    }

    fun withAst(ast: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source, html, ast?.suffixWith('\n'), options)
    }

    fun withOptions(options: List<String>?): FlexmarkExampleParams {
        return FlexmarkExampleParams(section, number, source, html, ast, options)
    }

    fun withOptions(options: String?): FlexmarkExampleParams {
        if (options != null) {
            val optionsList = options.split(',').map { it.trim() }.filter { !it.isEmpty() }
            return FlexmarkExampleParams(section, number, source, html, ast, optionsList)
        }
        return FlexmarkExampleParams(section, number, source, html, ast, null)
    }
}
