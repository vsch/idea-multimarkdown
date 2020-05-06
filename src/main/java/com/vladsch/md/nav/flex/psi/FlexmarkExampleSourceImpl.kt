// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.psi.util.MdPsiBundle

class FlexmarkExampleSourceImpl(node: ASTNode) : FlexmarkExampleSectionImpl(node), FlexmarkExampleSource {
    override fun getFlexmarkExampleParams(example: FlexmarkExample, content: String?): FlexmarkExampleParams {
        return FlexmarkExampleParams(example).withSource(content)
    }

    override fun getSectionDescription(): String = "Flexmark spec example source block"

    override fun getSectionIndex(): Int = 1

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("flexmark-source")
    }
}
