// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.plugin.util.rangeLimit
import com.vladsch.plugin.util.toBased

// FIX: file.text should not be used directly, instead data key with soft reference for wrapped file text and modification timestamp should be used.
//   If data does not exist or timestamp does not match current then create new wrapped text base chars.

open class PsiEditAdjustment constructor(val caretOffset: Int, val file: PsiFile, chars: CharSequence, val char: Char?, editor: Editor?) : PsiEditContext {
    constructor(psiFile: PsiFile) : this(0, psiFile, psiFile.text, null, null)
    constructor(psiFile: PsiFile, charSequence: CharSequence) : this(0, psiFile, charSequence, null, null)
    constructor(psiFile: PsiFile, charSequence: CharSequence, editor: Editor) : this(0, psiFile, charSequence, null, editor)

    var editOpDelta: Int = 0
        protected set

    var isForceDelete: Boolean = false
    val isEditOp: Boolean get() = editOpDelta != 0
    val isDeleteOp: Boolean get() = editOpDelta < 0 || editOpDelta == 0 && isForceDelete
    val isInsertOp: Boolean get() = editOpDelta > 0
    private val _charSequence: BasedSequence = chars.toBased()

    override fun getPsiFile(): PsiFile = file
    override fun getCharSequence(): BasedSequence = _charSequence
    override fun getHasFrontMatter(): Boolean = frontMatterOffset > 0
    override fun editOpDelta(): Int = editOpDelta
    private val _editor:Editor? = editor
    override fun getEditor(): Editor? = _editor

    val frontMatterOffset: Int by lazy {
        val jekyllFrontMatter = if (this.charSequence.startsWith("---")) this.file.findElementAt(0) else null
        if (jekyllFrontMatter != null && (jekyllFrontMatter.node.elementType == MdTypes.JEKYLL_FRONT_MATTER_OPEN || jekyllFrontMatter.node.elementType == MdTypes.FLEXMARK_FRONT_MATTER_OPEN)) jekyllFrontMatter.textLength + 1 else 0
    }

    override fun getFrontMatterNode(): ASTNode? {
        return if (this.charSequence.startsWith("---")) this.file.findElementAt(0)?.node else null
    }

    private val _renderingProfile: MdRenderingProfile by lazy { MdRenderingProfileManager.getProfile(this.file) }
    override fun getRenderingProfile(): MdRenderingProfile = _renderingProfile

    // NOTE: default implementation in PsiEditContext is fine
//    val project: Project get() = file.project
//    val parserSettings: MdParserSettings by lazy { renderingProfile.parserSettings }
//    val asideEnabled: Boolean get() = parserSettings.pegdownFlags and PegdownExtensions.ASIDE.flags != 0
//    val blockQuoteStyleChars: String by lazy { if (asideEnabled) ">|" else ">" }
//    val blockQuoteStyleCharsSet: CharPredicate by lazy { CharPredicate.anyOf(if (asideEnabled) ">|" else ">") }
//    val indentingChars: String by lazy { " \t$blockQuoteStyleChars" }
//    val indentingCharsSet: CharPredicate by lazy { CharPredicate.anyOf(" \t$indentingChars") }
//    fun isBlockQuoteStyleChar(c: Char?): Boolean = c != null && blockQuoteStyleCharsSet.test(c)
//    fun isIndentingChar(c: Char?): Boolean = c != null && indentingCharsSet.test(c)
//    fun isWhitespaceChar(c: Char?): Boolean = c != null && CharPredicate.SPACE_TAB.test(c)

    fun findElementAt(offset: Int): PsiElement? {
        return file.findElementAt(preEditOffset(offset).rangeLimit(0, charSequence.length))
    }

    override fun text(start: Int, end: Int): BasedSequence {
        if (start in 0 .. end && end <= charSequence.length) {
            return charSequence.subSequence(start, end)
        }
        return BasedSequence.NULL
    }

    /**
     * Adjust after edit text offset to before edit offset in the PsiTree
     */
    override fun preEditOffset(postEditOffset: Int): Int {
        if (editOpDelta < 0) {
            // if before deleted region, no change, assuming that delete was done via backspace
            if (postEditOffset < caretOffset) return postEditOffset

            return postEditOffset - editOpDelta
        } else if (editOpDelta > 0) {
            // if before the inserted region, no change
            if (postEditOffset < caretOffset - editOpDelta) return postEditOffset

            // if in the inserted region then return end of inserted region
            if (postEditOffset < caretOffset) return caretOffset

            return postEditOffset - editOpDelta
        } else {
            // no change
            return postEditOffset
        }
    }

    /**
     * Adjust before edit offset in the PsiTree to post edit offset in the text
     *
     * @param preEditOffset offset to be adjusted for edit op
     * @param adjustInsertAtStart when to adjust if pre-edit offset falls at start of insert range
     */
    override fun postEditOffset(preEditOffset: Int, adjustInsertAtStart: PostEditAdjust): Int {
        //        if (preEditOffset + editOpDelta < caretOffset) return preEditOffset
        //        return preEditOffset + editOpDelta
        when {
            editOpDelta < 0 -> {
                if (preEditOffset < caretOffset) return preEditOffset // if before deleted region, no change, assuming that delete was done via backspace

                if (preEditOffset < caretOffset - editOpDelta) return caretOffset // if in the deleted region then return caretOffset

                return preEditOffset + editOpDelta
            }
            editOpDelta > 0 -> {
                if (preEditOffset < caretOffset - editOpDelta) return preEditOffset // if before inserted region, no change
                val adjustedOffset = preEditOffset + editOpDelta

                if (preEditOffset == caretOffset - editOpDelta) {
                    return when (adjustInsertAtStart) {
                        PostEditAdjust.NEVER -> preEditOffset
                        PostEditAdjust.ALWAYS -> adjustedOffset
                        PostEditAdjust.IF_INDENT_CHAR -> if (isIndentingChar(char)) adjustedOffset else preEditOffset
                        PostEditAdjust.IF_NON_INDENT_CHAR -> if (!isIndentingChar(char)) adjustedOffset else preEditOffset
                        PostEditAdjust.IF_WHITESPACE_CHAR -> if (isWhitespaceChar(char)) adjustedOffset else preEditOffset
                        PostEditAdjust.IF_NON_WHITESPACE_CHAR -> if (!isWhitespaceChar(char)) adjustedOffset else preEditOffset
                    }
                }

                // after insert start, adjust
                return adjustedOffset
            }
            else -> return preEditOffset // no change
        }
    }

    override fun nodeText(node: ASTNode): BasedSequence {
        val len = charSequence.length
        return charSequence.subSequence((postEditNodeStart(node)).rangeLimit(0, len), (postEditNodeEnd(node)).rangeLimit(0, len))
    }

    override fun nodeRange(node: ASTNode): Range {
        val len = charSequence.length
        return Range.of((postEditNodeStart(node)).rangeLimit(0, len), (postEditNodeEnd(node)).rangeLimit(0, len))
    }

    override fun elementText(element: PsiElement): BasedSequence {
        return nodeText(element.node)
    }

    override fun isInsertedEditOpRange(startPostEditOffset: Int, endPostEditOffset: Int): Boolean {
        if (editOpDelta > 0) {
            return startPostEditOffset == caretOffset - editOpDelta && endPostEditOffset == caretOffset
        } else {
            return false
        }
    }
}
