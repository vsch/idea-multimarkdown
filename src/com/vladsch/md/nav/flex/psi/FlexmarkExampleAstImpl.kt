// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.psi.util.MdPsiBundle

open class FlexmarkExampleAstImpl(node: ASTNode) : FlexmarkExampleSectionImpl(node), FlexmarkExampleAst {
    override fun getLanguageNode(): ASTNode? {
        return null
    }

    override fun getFlexmarkExampleParams(example: FlexmarkExample, content: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(example).withAst(content)
    }

    override fun getSectionDescription(): String = "Flexmark spec example HTML block"

    override fun getSectionIndex(): Int = 3

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("flexmark-ast")
    }
}
