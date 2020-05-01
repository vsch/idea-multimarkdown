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
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider
import com.vladsch.md.nav.psi.util.BlockPrefixes
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.format.LinePrefixMatcher
import com.vladsch.md.nav.util.format.SpacePrefixMatcher
import com.vladsch.plugin.util.maxLimit
import icons.MdIcons
import javax.swing.Icon

class MdEnumeratedReferenceFormatImpl(node: ASTNode) : MdReferenceElementImpl(node), MdEnumeratedReferenceFormat, MdStructureViewPresentableItem {

    override fun getFormatTypeText(): String {
        return formatTypeElement?.text ?: ""
    }

    override fun getFormatText(): String {
        return formatTextElement?.text ?: ""
    }

    override fun getFormatTypeElement(): MdEnumeratedReferenceFormatType? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.ENUM_REF_FORMAT_TYPE) as MdEnumeratedReferenceFormatType?
    }

    override fun getFormatTextElement(): MdEnumeratedReferenceFormatText? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.ENUM_REF_FORMAT_TEXT) as MdEnumeratedReferenceFormatText?
    }

    override fun getReferenceDisplayName(): String {
        return REFERENCE_DISPLAY_NAME
    }

    override fun getReferenceType(): IElementType {
        return REFERENCE_TYPE
    }

    override fun getReferencingElementText(): String? {
        return null
    }

    override fun getReferenceIdentifier(): MdEnumeratedReferenceFormatType? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.ENUM_REF_FORMAT_TYPE) as MdEnumeratedReferenceFormatType?
    }

    override fun isReferenceFor(refElement: MdReferencingElement?): Boolean {
        return refElement is MdEnumeratedReferenceBase && isReferenceFor(refElement.referenceId)
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ENUMERATED_REFERENCE
    }

    override fun isReferenced(): Boolean {
        // these we can find in the page, unless this is an included file then we have to find references to this file which are JekyllIncludeLinkRefs
        val typeText = formatTypeText
        if (typeText.isBlank()) return false

        val referenceLinks = MdPsiImplUtil.listChildrenOfAnyType(mdFile, false, true, true, MdEnumeratedReferenceIdImpl::class.java)

        return referenceLinks.any {
            it.typeList.first.contains(typeText)
        }
    }

    override fun normalizeReferenceId(referenceId: String?): String {
        val pos = referenceId?.indexOf(':') ?: -1
        return if (pos > -1) {
            normalizeReferenceText(referenceId?.substring(0, pos))
        } else {
            normalizeReferenceText(referenceId)
        }
    }

    override fun isTextStart(node: ASTNode): Boolean {
        return node.elementType == MdTypes.ENUM_REF_FORMAT_TEXT
    }

    override fun contentIndent(): Int {
        return MdIndentingCompositeImpl.contentIndent(this)
    }

    override fun isEmptyText(): Boolean {
        return MdIndentingCompositeImpl.isEmptyItemText(this)
    }

    override fun removeLinePrefix(lines: LineAppendable, indentColumns: IntArray, isFirstChild: Boolean, editContext: PsiEditContext) {
        MdIndentingCompositeImpl.removeLinePrefix(lines, indentColumns, isFirstChild, editContext, getPrefixMatcher(editContext), 0)
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
        return MdBlockPrefixProvider.getBlockPrefixes(this, parentPrefixes, editContext)
    }

    override fun isFirstItemBlock(element: PsiElement): Boolean {
        return element is MdEnumeratedReferenceFormatText || MdIndentingCompositeImpl.getItemBlock(this) === element
    }

    override fun isFirstItemBlockPrefix(element: PsiElement): Boolean {
        return false
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
        return MdPsiBundle.message("enumerated-reference-format")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return MdPsiImplUtil.findChildTextBlock(this)
    }

    companion object {
        val REFERENCE_DISPLAY_NAME: String = MdBundle.message("reference.type.enumerated-reference-format")
        val REFERENCE_TYPE = MdTypes.ENUM_REF_ID

        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, referenceId: String, text: String): String {
            return getElementPrefix(referenceId) + text + "\n"
        }

        private fun getElementPrefix(referenceId: String): String {
            return "[@$referenceId]: "
        }

        @JvmStatic
        fun normalizeReferenceText(referenceId: String?): String {
            return referenceId ?: ""
        }
    }
}
