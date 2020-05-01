// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.vladsch.md.nav.psi.util.MdPsiBundle

open class FlexmarkExampleHtmlImpl(node: ASTNode) : FlexmarkExampleSectionImpl(node), FlexmarkExampleHtml {
    override fun getFlexmarkExampleParams(example: FlexmarkExample, content: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(example).withHtml(content)
    }

    override fun getSectionDescription(): String = "Flexmark spec example HTML block"

    override fun getSectionIndex(): Int = 2

    override fun getVerbatimLanguageRange(inDocument: Boolean): TextRange {
        val offsetInParent = if (inDocument) node.startOffset else 0
        return TextRange(offsetInParent, offsetInParent)
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("flexmark-html")
    }
}
