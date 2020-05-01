// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.text

import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.psi.MdPlainText

/**
 * Implements FakePsiElement for text based references to other elements
 */
class MdPlainTextElementImpl(parent: MdPlainText<*>, startOffset: Int, endOffset: Int, referenceableOffsetInParent: Int) : FakePsiElement() {

    private val myParent = parent
    private val myParentNode: ASTNode get() = myParent.node
    private val myReferenceableOffsetInParent = referenceableOffsetInParent
    private val myStartOffset = startOffset
    private val myEndOffset = endOffset
    private val myFileStartOffset: Int get() = myStartOffset + myParent.node.startOffset
    private val myFileEndOffset: Int get() = myEndOffset + myParent.node.startOffset
    private val myASTNode: ASTNode = MyAstNode()
    //    private val myTextLength:Int get() = myEndOffset - myStartOffset

    override fun getParent(): PsiElement {
        return myParent
    }

    override fun getText(): String {
        return myParentNode.text.substring(myFileStartOffset, myFileEndOffset)
    }

    override fun getStartOffsetInParent(): Int {
        return myReferenceableOffsetInParent
    }

    override fun getLanguage(): Language {
        return MdLanguage.INSTANCE
    }

    override fun getTextRange(): TextRange {
        return TextRange(myFileStartOffset, myFileEndOffset)
    }

    override fun canNavigate(): Boolean {
        return true
    }

    override fun getContainingFile(): PsiFile {
        return myParent.containingFile
    }

    override fun getContext(): PsiElement? {
        return myParent
    }

    override fun isValid(): Boolean {
        return myParent.isValid && myParent.node == myParentNode && myParentNode.startOffset <= myFileStartOffset && myParentNode.startOffset + myParentNode.textLength >= myFileEndOffset
    }

    override fun replace(newElement: PsiElement): PsiElement {
        if (newElement is MdPlainTextElementImpl) {
            val newParent = myParent.replace(newElement.myParent)
            if (newParent != myParent) {
                return newElement
            }
            return this
        }
        return super.replace(newElement)
    }

    override fun getTextLength(): Int {
        return myEndOffset - myStartOffset
    }

    override fun getTextOffset(): Int {
        return myFileStartOffset
    }

    override fun isPhysical(): Boolean {
        return myParent.isPhysical
    }

    override fun getNode(): ASTNode? {
        return myASTNode
    }

    override fun delete() {
        name = ""
    }

    override fun setName(name: String): MdPlainTextElementImpl {
        // change the file text and find element at offset
        val newParent = myParent.replaceReferenceableText(name, myStartOffset, myEndOffset)
        if (newParent !== myParent && newParent is MdPlainText<*>) {
            // create a new us
            return MdPlainTextElementImpl(newParent, myStartOffset, myStartOffset + name.length, myReferenceableOffsetInParent)
        }
        return this
    }

    override fun textContains(c: Char): Boolean {
        return myASTNode.chars.contains(c)
    }

    override fun getReferences(): Array<out PsiReference> {
        return PsiReference.EMPTY_ARRAY
    }

    override fun getNavigationElement(): PsiElement {
        return this
    }

    override fun getReference(): PsiReference? {
        return null
    }

    override fun getName(): String {
        return text
    }

    override fun isWritable(): Boolean {
        return myParent.isWritable
    }

    override fun navigate(requestFocus: Boolean) {
        PsiNavigationSupport.getInstance().createNavigatable(project, containingFile.virtualFile, myFileStartOffset).navigate(requestFocus)
    }

    // ASTNode
    inner class MyAstNode : ASTNode {

        override fun getTextLength(): Int {
            return this@MdPlainTextElementImpl.textLength
        }

        override fun getText(): String {
            return this@MdPlainTextElementImpl.text
        }

        override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
            this@MdPlainTextElementImpl.putUserData(key, value)
        }

        override fun <T : Any?> getUserData(key: Key<T>): T? {
            return this@MdPlainTextElementImpl.getUserData(key)
        }

        override fun textContains(c: Char): Boolean {
            return this@MdPlainTextElementImpl.textContains(c)
        }

        override fun getTextRange(): TextRange {
            return this@MdPlainTextElementImpl.textRange
        }

        override fun <T : Any?> getCopyableUserData(key: Key<T>): T? {
            return this@MdPlainTextElementImpl.getCopyableUserData(key)
        }

        override fun <T : Any?> putCopyableUserData(key: Key<T>, value: T) {
            return this@MdPlainTextElementImpl.putCopyableUserData(key, value)
        }

        override fun getChildren(filter: TokenSet?): Array<out ASTNode> {
            return EMPTY_NODES
        }

        override fun addLeaf(leafType: IElementType, leafText: CharSequence, anchorBefore: ASTNode?) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getLastChildNode(): ASTNode? {
            return null
        }

        override fun getElementType(): IElementType {
            return myParentNode.elementType
        }

        override fun getTreeParent(): ASTNode {
            return myParent.node
        }

        override fun getChars(): CharSequence {
            return myParent.node.chars.subSequence(myFileStartOffset, myFileEndOffset)
        }

        override fun removeRange(firstNodeToRemove: ASTNode, firstNodeToKeep: ASTNode?) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun replaceAllChildrenToChildrenOf(anotherParent: ASTNode) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun findLeafElementAt(offset: Int): ASTNode? {
            return null
        }

        override fun getStartOffset(): Int {
            return myFileStartOffset
        }

        override fun getTreeNext(): ASTNode? {
            return null
        }

        override fun replaceChild(oldChild: ASTNode, newChild: ASTNode) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun clone(): Any {
            val newParentNode = myParent.node.clone() as ASTNode
            val newParent = newParentNode.psi
            return MdPlainTextElementImpl(newParent as MdPlainText<*>, myStartOffset, myEndOffset, myReferenceableOffsetInParent)
        }

        override fun copyElement(): ASTNode {
            val newParentNode = myParent.node.copyElement()
            val newParent = newParentNode.psi
            return MdPlainTextElementImpl(newParent as MdPlainText<*>, myStartOffset, myEndOffset, myReferenceableOffsetInParent).myASTNode
        }

        override fun addChildren(firstChild: ASTNode, firstChildToNotAdd: ASTNode?, anchorBefore: ASTNode?) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun findChildByType(type: IElementType): ASTNode? {
            return null
        }

        override fun findChildByType(type: IElementType, anchor: ASTNode?): ASTNode? {
            return null
        }

        override fun findChildByType(typesSet: TokenSet): ASTNode? {
            return null
        }

        override fun findChildByType(typesSet: TokenSet, anchor: ASTNode?): ASTNode? {
            return null
        }

        override fun getFirstChildNode(): ASTNode? {
            return null
        }

        override fun removeChild(child: ASTNode) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getPsi(): PsiElement {
            return this@MdPlainTextElementImpl
        }

        override fun <T : PsiElement?> getPsi(clazz: Class<T>): T {
            LOG.assertTrue(clazz.isInstance(myParent), "unexpected psi class. expected: " + clazz + " got: " + myParent.javaClass)
            @Suppress("UNCHECKED_CAST")
            return myParent as T
        }

        override fun addChild(child: ASTNode) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addChild(child: ASTNode, anchorBefore: ASTNode?) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getTreePrev(): ASTNode? {
            return null
        }
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.psi.impl.text")

        @Suppress("CAST_NEVER_SUCCEEDS", "RemoveExplicitTypeArguments")
        @JvmField
        val EMPTY_NODES = Array<ASTNode>(0, { null as ASTNode })
    }
}
