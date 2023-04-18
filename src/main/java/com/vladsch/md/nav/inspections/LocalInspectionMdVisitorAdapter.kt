// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.inspections

import com.intellij.psi.PsiFile
import com.vladsch.md.nav.flex.psi.FlexmarkExampleAst
import com.vladsch.md.nav.flex.psi.FlexmarkExampleHtml
import com.vladsch.md.nav.flex.psi.FlexmarkExampleSource
import com.vladsch.md.nav.psi.element.MdVerbatim
import com.vladsch.md.nav.psi.util.MdNodeVisitor
import com.vladsch.md.nav.psi.util.MdVisitHandler

abstract class LocalInspectionMdVisitorAdapter(val ignoreFencedCodeContent: Boolean) : MdNodeVisitor() {
    constructor(localInspectionTool: LocalInspectionToolBase) : this(localInspectionTool.isIgnoreFencedCodeContent)

    abstract fun handlers()

    init {
        if (ignoreFencedCodeContent) {
            addHandler(MdVisitHandler(PsiFile::class.java) {
                val context = it.context
                if (!(context is MdVerbatim && context.openMarker.isNotEmpty()) && context !is FlexmarkExampleSource && context !is FlexmarkExampleHtml && context !is FlexmarkExampleAst) {
                    // it is not injected, if so can visit
                    visitChildren(it)
                }
            })
        }

        @Suppress("LeakingThis")
        handlers()
    }
}
