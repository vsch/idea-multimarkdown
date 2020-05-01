// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.plugin.util.maxLimit
import icons.MdIcons
import javax.swing.Icon

class MdMacroImpl(node: ASTNode) : MdReferenceElementImpl(node), MdMacro, MdStructureViewPresentableItem {

    override fun getMacroTextElement(): MdMacroText? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.MACRO_TEXT) as MdMacroText?
    }

    override fun getMacroText(): String {
        val macroText = macroTextElement

        return if (macroText == null) "" else macroText.text
    }

    override fun getReferenceDisplayName(): String {
        return REFERENCE_DISPLAY_NAME
    }

    override fun getReferenceType(): IElementType {
        return REFERENCE_TYPE
    }

    override fun getReferenceIdentifier(): MdMacroId? {
        val id = MdPsiImplUtil.findChildByType(this, MdTypes.MACRO_ID) as MdMacroId?
        return id
    }

    override fun isReferenceFor(refElement: MdReferencingElement?): Boolean {
        return refElement is MdMacroRef && isReferenceFor(refElement.referenceId)
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.MACRO
    }

    override fun normalizeReferenceId(referenceId: String?): String {
        return normalizeReferenceText(referenceId)
    }

    override fun getReferencingElementText(): String? {
        return "<<<$referenceId>>>"
    }

    private fun continuationIndent(firstLineIndent: Int, parentIndent: Int, editContext: PsiEditContext): Int {
        return MdIndentingCompositeImpl.continuationIndent(firstLineIndent, parentIndent, editContext)
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getPresentableText(): String {
        val textElement = MdPsiImplUtil.findChildTextBlock(this) ?: return "$referenceId:"
        val text = MdPsiImplUtil.getNodeText(textElement, true, false)
        val length = text.length.maxLimit(100)
        val eolPos = text.indexOf('\n')
        val result = text.substring(0, if (eolPos < 0) length else eolPos.maxLimit(length))
        return "$referenceId: $result"
    }

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText && !node.text.isEmpty()) {
            val truncateStringForDisplay = MdPsiImplUtil.truncateStringForDisplay(node.text, settings.maxBreadcrumbText, false, true, true)
            if (!truncateStringForDisplay.isEmpty()) return truncateStringForDisplay
        }
        return MdPsiBundle.message("macro")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        val textBlock = MdPsiImplUtil.findChildTextBlock(this)
        return textBlock
    }

    companion object {
        val REFERENCE_DISPLAY_NAME: String = MdBundle.message("reference.type.macro")
        val REFERENCE_TYPE = MdTypes.MACRO!!

        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, referenceId: String, text: String): String {
            return ">>>$referenceId\n$text\n<<<\n"
        }

        @JvmStatic
        fun normalizeReferenceText(referenceId: String?): String {
            return referenceId ?: ""
        }
    }
}
