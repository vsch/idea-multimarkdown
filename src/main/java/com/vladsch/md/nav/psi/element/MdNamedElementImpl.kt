// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiReference
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.plugin.util.maxLimit

abstract class MdNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdNamedElement {

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is MdPsiVisitor)
            visitor.visitNamedElement(this)
        else
            super.accept(visitor)
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun getName(): String {
        return text ?: ""
    }

    override fun setName(newName: String): PsiElement {
        return setName(newName, MdNamedElement.REASON_FILE_RENAMED)
    }

    override fun getNameIdentifier(): PsiElement? {
        return if (this !is MdLinkText) this else null //MultiMarkdownPsiImplUtil.getNameIdentifier(this);
    }

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(range: TextRange, newContent: String): MdNamedElement {
        if (!range.equalsToRange(0, textLength)) {
            throw IncorrectOperationException()
        }
        return handleContentChange(newContent)
    }

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(newContent: String): MdNamedElement {
        return setName(newContent, MdNamedElement.REASON_FILE_RENAMED) as MdNamedElement
    }

    override fun getLocationString(): String? {
        return if (text == null) "" else text.substring(0, text.length.maxLimit(50))
    }

    override fun getPresentableText(): String {
        return displayName
    }

    override fun getPresentation(): ItemPresentation {
        return MdPsiImplUtil.getPresentation(this)
    }

    override fun isRenameAvailable(): Boolean {
        return this !is MdLinkText
    }

    /**
     * Returns the reference from this PSI element to another PSI element (or elements), if one exists.
     * If the element has multiple associated references (see [.getReferences]
     * for an example), returns the first associated reference.

     * @return the reference instance, or null if the PSI element does not have any
     * * associated references.
     * *
     * @see com.intellij.psi.search.searches.ReferencesSearch
     */
    override fun getReference(): PsiReference? {
        if (this !is MdLinkText) {
            return createReference(TextRange(0, node.textLength), false)
        }
        return null
    }

    override fun getExactReference(): PsiReference? {
        if (this !is MdLinkText) {
            return createReference(TextRange(0, node.textLength), true)
        }
        return null
    }

    companion object {
        private val LOG = Logger.getInstance(MdNamedElementImpl::class.java)
    }
}
