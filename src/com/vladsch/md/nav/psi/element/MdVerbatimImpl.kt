// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.plugin.util.ifElse
import com.vladsch.plugin.util.suffixWith

open class MdVerbatimImpl(node: ASTNode) : MdVerbatimBaseImpl(node) {

    override fun setVerbatimLanguage(verbatimLanguage: String?): PsiElement {
        //final PsiElement element = MultiMarkdownPsiImplUtil.findChildByType(this, MultiMarkdownTypes.VERBATIM_LANG);
        MdPsiImplUtil.setLanguage(this, verbatimLanguage, null)
        return this
    }

    override fun isPrefixedContent(): Boolean {
        return true
    }

    override fun getBreadcrumbInfo(): String {
        return if (verbatimLanguage.isEmpty()) MdPsiBundle.message("verbatim") else MdPsiBundle.message("fenced-code")
    }

    override fun getBreadcrumbTooltip(): String? {
        return verbatimLanguage
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun isTerminatedContent(): Boolean {
        return this.openMarkerNode == null || this.closeMarkerNode != null
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, marker: String?, languageName: String?, verbatimContent: String?, leadMarkerPrefix: String?): String {
            return getElementText(false, marker, marker, languageName, verbatimContent, leadMarkerPrefix, null)
        }

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, spaceBeforeLanguage: Boolean, openMarker: String?, closeMarker: String?, languageName: String?, verbatimContent: String?, leadMarkerPrefix: String?, trailMarkerPrefix: String?): String {
            return getElementText(spaceBeforeLanguage, openMarker, closeMarker, languageName, verbatimContent, leadMarkerPrefix, trailMarkerPrefix)
        }

        @Suppress("NAME_SHADOWING")
        private fun getElementText(spaceBeforeLanguage: Boolean, openMarker: String?, closeMarker: String?, languageName: String?, verbatimContent: String?, leadMarkerPrefix: String?, trailMarkerPrefix: String?): String {
            val openMarker = openMarker ?: ""
            val closeMarker = closeMarker ?: ""
            val languageName = languageName ?: ""
            val verbatimContent = verbatimContent ?: "\n"
            val leadMarkerPrefix = leadMarkerPrefix ?: ""
            val trailMarkerPrefix = trailMarkerPrefix ?: ""

            if (openMarker.isEmpty()) {
                return verbatimContent.suffixWith("\n")
            } else {
                val languageInfo = languageName.trim { it <= ' ' }
                val prefix = leadMarkerPrefix + openMarker + (spaceBeforeLanguage && languageInfo != "").ifElse(" ", "") + languageInfo + "\n"
                if (verbatimContent.startsWith(leadMarkerPrefix + openMarker + languageInfo)) {
                    return verbatimContent
                } else {
                    val result = prefix + verbatimContent.suffixWith("\n") + trailMarkerPrefix + closeMarker.suffixWith("\n")
                    return result
                }
            }
        }
    }
}
