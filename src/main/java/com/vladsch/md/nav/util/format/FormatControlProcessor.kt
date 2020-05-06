// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import com.vladsch.md.nav.psi.element.MdComment
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.plugin.util.psi.isTypeIn

// DEPRECATED: replacement CodeStyle#getSettings appeared in 2017-11-09
@Suppress("DEPRECATION", "MemberVisibilityCanBePrivate")
class FormatControlProcessor(val psiFile: PsiFile) {

    private var myFormatterOff = false

    // DEPRECATED: when no longer supporting 2016.3 can change CodeStyleSettingsManager.getSettings to CodeStyle.getSettings
    // class appeared in the API on 2017.11.09

    @Suppress("unused")
    val settings: CodeStyleSettings by lazy { CodeStyleSettingsManager.getSettings(psiFile.project) }

    val isFormattingOff: Boolean get() = myFormatterOff
    val isFormattingRegion: Boolean get() = !myFormatterOff
    val formatterOnTag: String = settings.FORMATTER_ON_TAG
    val formatterOffTag: String = settings.FORMATTER_OFF_TAG
    val formatterTagsEnabled = settings.FORMATTER_TAGS_ENABLED
    val formatterRegExEnabled = settings.FORMATTER_TAGS_ACCEPT_REGEXP
    val formatterOnPattern = settings.formatterOnPattern
    val formatterOffPattern = settings.formatterOffPattern
    var justTurnedOffFormatting = false
    private set

    private fun isFormatterOffTag(commentText: CharSequence?): Boolean? {
        val text = commentText?.toString()?.trim() ?: return null

        if (formatterRegExEnabled && formatterOffPattern != null && formatterOnPattern != null) {
            if (formatterOnPattern.matcher(text).matches()) {
                return false
            } else if (formatterOffPattern.matcher(text).matches()) {
                return true
            }
        } else if (formatterTagsEnabled) {
            if (text == formatterOnTag) {
                return false
            } else if (text == formatterOffTag) {
                return true
            }
        }
        return null
    }

    fun initializeFrom(element: PsiElement) {
        myFormatterOff = element is PsiFile && !isFormattingRegion(element.textOffset)
    }

    fun processFormatControl(element: PsiElement) {
        justTurnedOffFormatting = false

        if ((element is MdComment) && formatterTagsEnabled) {
            // could be formatter control
            val formatterOff = myFormatterOff
            myFormatterOff = isFormatterOffTag(element.commentText) ?: return
            if (!formatterOff && myFormatterOff) justTurnedOffFormatting = true
        }
    }

    fun isFormattingRegion(offset: Int): Boolean {
        // find the first HTML comment with a formatter directive
        if (!formatterTagsEnabled || offset == 0) return true

        var node = psiFile.node.lastChildNode
        while (node != null) {
            if (node.startOffset + node.textLength <= offset) {
                break
            }
            node = node.treePrev
        }

        while (node != null) {
            if (node.isTypeIn(MdTokenSets.COMMENT_FOR_COMMENT_SET)) {
                val formatterOff = isFormatterOffTag(node.getChildren(MdTokenSets.COMMENT_FOR_TODO_SET).lastOrNull()?.text)
                if (formatterOff != null) return !formatterOff
            }
            node = node.treePrev
        }
        return true
    }
}
