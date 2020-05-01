// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language.folding

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.flex.PluginBundle
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings
import com.vladsch.md.nav.language.api.MdCodeFoldingOptionsHolder
import com.vladsch.md.nav.language.api.MdFoldingBuilderProvider
import com.vladsch.md.nav.language.api.MdFoldingVisitorHandler
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings

class MdFlexmarkFoldingBuilderProvider : MdFoldingBuilderProvider {

    private val settings = MdFlexmarkFoldingSettings.getInstance()

    override fun getExtensionName(): String {
        return PluginBundle.message("product.title")
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean? {
        return when (node.elementType) {
            MdTypes.FLEXMARK_FRONT_MATTER_BLOCK_ELEM -> settings.COLLAPSE_FLEXMARK_FRONT_MATTER
            MdTypes.FLEXMARK_EXAMPLE -> settings.COLLAPSE_FLEXMARK_EXAMPLE
            MdTypes.FLEXMARK_EXAMPLE_SOURCE -> settings.COLLAPSE_FLEXMARK_EXAMPLE_SOURCE
            MdTypes.FLEXMARK_EXAMPLE_HTML -> settings.COLLAPSE_FLEXMARK_EXAMPLE_HTML
            MdTypes.FLEXMARK_EXAMPLE_AST -> settings.COLLAPSE_FLEXMARK_EXAMPLE_AST
            else -> null
        }
    }

    override fun extendFoldingOptions(holder: MdCodeFoldingOptionsHolder) {
        if (MdApplicationSettings.instance.debugSettings.getExtension(FlexmarkDebugSettings.KEY).enableFlexmarkFeatures) {
            holder.addCheckBox(PluginBundle.message("code-folding.flexmark-front-matter"), { settings.COLLAPSE_FLEXMARK_FRONT_MATTER }, { value: Boolean -> settings.COLLAPSE_FLEXMARK_FRONT_MATTER = value })
            holder.addCheckBox(PluginBundle.message("code-folding.flexmark-example"), { settings.COLLAPSE_FLEXMARK_EXAMPLE }, { value: Boolean -> settings.COLLAPSE_FLEXMARK_EXAMPLE = value })

            val sectionFolding = FlexSectionFolding()
            holder.component(sectionFolding.mainPanel, this::getSections, this::setSections, sectionFolding::getValue, sectionFolding::setValue)
        }
    }

    private fun getSections(): Int {
        var value = 0
        val s = settings
        if (s.COLLAPSE_FLEXMARK_EXAMPLE_SOURCE) value = value or 1 shl 0
        if (s.COLLAPSE_FLEXMARK_EXAMPLE_HTML) value = value or 1 shl 1
        if (s.COLLAPSE_FLEXMARK_EXAMPLE_AST) value = value or 1 shl 2
        return value
    }

    private fun setSections(value: Int) {
        val s = settings
        s.COLLAPSE_FLEXMARK_EXAMPLE_SOURCE = value and (1 shl 0) != 0
        s.COLLAPSE_FLEXMARK_EXAMPLE_HTML = value and (1 shl 1) != 0
        s.COLLAPSE_FLEXMARK_EXAMPLE_AST = value and (1 shl 2) != 0
    }

    override fun extendFoldingHandler(handler: MdFoldingVisitorHandler, quick: Boolean) {
        MdFlexmarkFoldingBuilder(handler, quick)
    }
}
