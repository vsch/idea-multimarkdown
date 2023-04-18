// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.LinkRef
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.prefixWith
import com.vladsch.plugin.util.wrapWith
import javax.swing.Icon

class MdReferenceImpl(stub: MdReferenceStub?, nodeType: IStubElementType<MdReferenceStub, MdReference>?, node: ASTNode?) :
    MdLinkElementImpl<MdReference, MdReferenceStub>(stub, nodeType, node),
    MdStructureViewPresentableElement, MdStructureViewPresentableItem,
    MdNamedElement,
    MdReference {

    constructor(stub: MdReferenceStub, nodeType: IStubElementType<MdReferenceStub, MdReference>) : this(stub, nodeType, null)
    constructor(node: ASTNode) : this(null, null, node)

    override fun isReferenceFor(referenceId: String?): Boolean {
        return normalizeReferenceId(getReferenceId()) == normalizeReferenceId(referenceId)
    }

    override fun isReferenceFor(refElement: MdReferencingElement?): Boolean {
        return (refElement is MdReferenceLink || refElement is MdReferenceImage) && isReferenceFor(refElement.referenceId)
    }

    override fun getReferenceType(): IElementType {
        return REFERENCE_TYPE
    }

    override fun getExactReference(): PsiReference? {
        return createReference(TextRange(0, node.textLength), true)
    }

    override fun createLinkRef(containingFile: FileRef, linkRefText: String, linkAnchorText: String?, targetRef: FileRef?): LinkRef {
        return LinkRef(containingFile, linkRefText, linkAnchorText, targetRef, false)
    }

    override fun getReferenceDisplayName(): String {
        return REFERENCE_DISPLAY_NAME
    }

    override fun getReferenceIdentifier(): MdReferenceElementIdentifier? {
        return referenceNameElement
    }

    override fun getLinkRefElement(): MdLinkRefElement? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.REFERENCE_LINK_REF) as MdLinkRefElement?
    }

    override fun getLinkAnchorElement(): MdLinkAnchor? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.REFERENCE_ANCHOR) as MdLinkAnchor?
    }

    override fun getLinkTitleElement(): MdLinkTitle? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.REFERENCE_TITLE) as MdLinkTitle?
    }

    override fun getReferenceId(): String {
        val text = linkText
        return text ?: ""
    }

    override fun getReferencingElementText(): String? {
        return "[$referenceId]"
    }

    override fun getLinkTextElement(): MdLinkText? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.REFERENCE_TEXT) as MdLinkText?
    }

    override fun normalizeReferenceId(referenceId: String?): String {
        return normalizeReferenceText(referenceId)
    }

    /*
     * Named element implementation delegated to our Text element
     */

    val referenceNameElement: MdReferenceIdentifier?
        get() {
            var child: PsiElement? = firstChild
            while (child != null && child !is MdReferenceIdentifier) child = child.nextSibling
            return child as? MdReferenceIdentifier?
        }

    override fun setName(newName: String): PsiElement {
        return referenceNameElement?.setName(newName) ?: this
    }

    override fun setName(newName: String, reason: Int): PsiElement {
        return referenceNameElement?.setName(newName, reason) ?: this
    }

    override fun getName(): String {
        return referenceNameElement?.name ?: ""
    }

    override fun getNameIdentifier(): PsiElement? {
        return referenceNameElement
    }

    override fun getPresentation(): ItemPresentation {
        return referenceNameElement?.presentation ?: MdPsiImplUtil.NULL_PRESENTATION
    }

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(range: TextRange, newContent: String): MdRenameElement {
        return referenceNameElement?.handleContentChange(range, newContent) ?: this
    }

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(newContent: String): MdRenameElement {
        return referenceNameElement?.handleContentChange(newContent) ?: this
    }

    override fun isReferenced(): Boolean {
        return MdPsiImplUtil.isElementReferenced(this.mdFile, this)
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference? {
        return null
    }

    override fun isInplaceRenameAvailable(context: PsiElement?): Boolean {
        return referenceNameElement?.isInplaceRenameAvailable(context) ?: false
    }

    override fun isMemberInplaceRenameAvailable(context: PsiElement?): Boolean {
        return referenceNameElement?.isMemberInplaceRenameAvailable(context) ?: false
    }

    override fun getIcon(flags: Int): Icon? {
        val linkRefElement = linkRefElement ?: return null
        val icon = linkRefElement.reference?.resolve()?.getIcon(0)
        if (icon != null) return icon

        val linkRef = MdPsiImplUtil.getLinkRef(linkRefElement) ?: return null
        val resolver = GitHubLinkResolver(this)
        return resolver.getIcon(linkRef)
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getPresentableText(): String? {
        //        return PsiBundle.message("reference")
        //        val anchorText = linkAnchorText
        //        return referenceId + ": " + linkRefText + (anchorText ?: "")
        return referenceId + ": " + linkRefText
    }

    override fun getLocationString(): String? {
        return node.text
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("reference")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun toString(): String {
        return "MdReferenceImpl(referenceId: $referenceId, link: $linkRefText, text: $linkText)"
    }

    companion object {
        @JvmField
        val REFERENCE_DISPLAY_NAME = MdBundle.message("reference.type.reference")

        @JvmField
        val REFERENCE_TYPE = MdTypes.REFERENCE

        private fun getElementText(name: String, linkRefWithAnchor: String, linkTitle: String?): String {
            // add self reference if nothing is given so we can create an element
            return "[" + name + "]: " + getElementLinkRefWithAnchor(if (linkRefWithAnchor.isBlank()) "#" else linkRefWithAnchor) + linkTitle.wrapWith(" \"", "\"") + "\n"
        }

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, name: String, linkRefWithAnchor: String, linkTitle: String?): String {
            return getElementText(name, linkRefWithAnchor, linkTitle)
        }

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, name: String, linkRef: String, linkAnchor: String?, linkTitle: String?): String {
            return getElementText(name, linkRef + linkAnchor.prefixWith('#'), linkTitle)
        }

        @JvmStatic
        fun areIdsEqual(name: String?, name1: String?): Boolean {
            return name != null && name1 != null && normalizeReferenceText(name) == normalizeReferenceText(name1)
        }

        @JvmStatic
        fun normalizeReferenceText(referenceId: String?): String {
            return referenceId?.toLowerCase()?.replace("\\s?\n".toRegex(), " ")?.trim() ?: ""
        }
    }
}
