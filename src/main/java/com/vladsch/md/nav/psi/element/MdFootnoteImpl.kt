// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.vladsch.flexmark.util.sequence.LineAppendable
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.BlockPrefixes
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.format.LinePrefixMatcher
import com.vladsch.md.nav.util.format.SpacePrefixMatcher
import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.rangeLimit
import icons.MdIcons
import javax.swing.Icon

class MdFootnoteImpl(node: ASTNode) : MdReferenceElementImpl(node), MdFootnote, MdIndentingComposite, MdStructureViewPresentableItem {

    override fun getFootnoteTextElement(): MdFootnoteText? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.FOOTNOTE_TEXT) as MdFootnoteText?
    }

    override fun getFootnoteText(): String {
        val footnoteText = footnoteTextElement

        return if (footnoteText == null) "" else footnoteText.text
    }

    override fun getReferenceDisplayName(): String {
        return REFERENCE_DISPLAY_NAME
    }

    override fun getReferenceType(): IElementType {
        return MdTypes.FOOTNOTE
    }

    override fun getReferenceIdentifier(): MdFootnoteId? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.FOOTNOTE_ID) as MdFootnoteId?
    }

    override fun getReferencingElementText(): String? {
        return "[^$referenceId]"
    }

    override fun isReferenceFor(refElement: MdReferencingElement?): Boolean {
        return refElement is MdFootnoteRef && isReferenceFor(refElement.referenceId)
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.FOOTNOTE
    }

    override fun normalizeReferenceId(referenceId: String?): String {
        return normalizeReferenceText(referenceId)
    }

    override fun isTextStart(node: ASTNode): Boolean {
        return node.elementType == MdTypes.FOOTNOTE_TEXT
    }

    override fun contentIndent(): Int {
        return MdIndentingCompositeImpl.contentIndent(this)
    }

    override fun isEmptyText(): Boolean {
        return MdIndentingCompositeImpl.isEmptyItemText(this)
    }

    override fun removeLinePrefix(lines: LineAppendable, indentColumns: IntArray, isFirstChild: Boolean, editContext: PsiEditContext) {
        MdIndentingCompositeImpl.removeLinePrefix(lines, indentColumns, false, editContext, getPrefixMatcher(editContext), 0)
    }

    override fun getPrefixMatcher(editContext: PsiEditContext): LinePrefixMatcher {
        return SpacePrefixMatcher.maxSpaces(4)
    }

    override fun actualItemPrefix(editContext: PsiEditContext): CharSequence {
        return MdIndentingCompositeImpl.actualTextPrefix(this, editContext, true)
    }

    override fun actualTextPrefix(editContext: PsiEditContext, addTrailingSpace: Boolean): CharSequence {
        return MdIndentingCompositeImpl.actualTextPrefix(this, editContext, true)
    }

    override fun actualTextPrefix(addTrailingSpace: Boolean): CharSequence {
        return MdIndentingCompositeImpl.actualTextPrefix(this, true)
    }

    override fun itemPrefixes(parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
        val prefixes = parentPrefixes ?: MdPsiImplUtil.getBlockPrefixes(parent, parentPrefixes, editContext)
        //val actualItemPrefix = actualItemPrefix(editContext).asString()
        val actualTextPrefix = actualTextPrefix(editContext, true)
        val childIndent = MdIndentingCompositeImpl.indentSpaces(actualTextPrefix.length.rangeLimit(4, 7))
        return prefixes.append(MdPsiImplUtil.isFirstIndentedBlockPrefix(this, false), actualTextPrefix, " ".repeat(continuationIndent(actualTextPrefix.length, childIndent.length, editContext)), childIndent, " ".repeat(continuationIndent(childIndent.length, childIndent.length, editContext)))
    }

    private fun continuationIndent(firstLineIndent: Int, parentIndent: Int, editContext: PsiEditContext): Int {
        return MdIndentingCompositeImpl.continuationIndent(firstLineIndent, parentIndent, editContext)
    }

    override fun isFirstItemBlock(element: PsiElement): Boolean {
        return element is MdFootnoteText && MdPsiImplUtil.findChildByType(this, MdTypes.FOOTNOTE_TEXT) === element
    }

    override fun isFirstItemBlockPrefix(element: PsiElement): Boolean {
        // if there is an EOL between item marker and element, ie empty item but markers not on same line
        val itemMarker = node.findChildByType(MdTypes.FOOTNOTE_CLOSE)
        if (itemMarker != null && itemMarker.treeNext?.elementType == MdTypes.EOL) {
            return false
        }
        return true
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getPresentableText(): String {
        //        return PsiBundle.message("footnote")
        val textElement = MdPsiImplUtil.findChildTextBlock(this) ?: return "$referenceId:"
        val text = MdPsiImplUtil.getNodeText(textElement, true, false)
        val length = text.length.maxLimit(100)
        val eolPos = text.indexOf('\n')
        val result = text.substring(0, if (eolPos < 0) length else eolPos.maxLimit(length))
        return "$referenceId: $result"
    }

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText && node.text.isNotEmpty()) {
            val truncateStringForDisplay = MdPsiImplUtil.truncateStringForDisplay(node.text, settings.maxBreadcrumbText, false, true, true)
            if (truncateStringForDisplay.isNotEmpty()) return truncateStringForDisplay
        }
        return MdPsiBundle.message("footnote")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return MdPsiImplUtil.findChildTextBlock(this)
    }

    companion object {
        val REFERENCE_DISPLAY_NAME: String = MdBundle.message("reference.type.footnote")

        @Suppress("UNUSED_PARAMETER")
        @JvmStatic
        fun getElementText(factoryContext: MdFactoryContext, referenceId: String, text: String): String {
            return getElementPrefix(referenceId) + text + "\n"
        }

        private fun getElementPrefix(referenceId: String): String {
            return "[^$referenceId]: "
        }

        @JvmStatic
        fun normalizeReferenceText(referenceId: String?): String {
            return referenceId ?: ""
        }
    }
}
