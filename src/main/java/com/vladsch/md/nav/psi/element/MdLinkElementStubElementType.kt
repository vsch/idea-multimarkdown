// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.lang.LighterASTTokenNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IElementType
import com.intellij.util.CharTable
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.psi.index.MdLinkElementIndex
import java.io.IOException

abstract class MdLinkElementStubElementType<Elem : MdLinkElement<*>, Stub : MdLinkElementStub<Elem>>(debugName: String) :
    ILightStubElementType<Stub, Elem>(debugName, MdLanguage.INSTANCE) {

    abstract override fun createPsi(stub: Stub): Elem // = MultiMarkdownWikiLinkImpl(stub, this)
    abstract override fun getExternalId(): String //= "markdown.link-element"
    abstract fun createStub(parentStub: StubElement<*>, linkRefWithAnchorText: String): Stub //= MultiMarkdownWikiLinkStubImpl(parentStub, linkRefWithAnchorText)
    abstract fun getLinkRefTextType(): IElementType? //= MultiMarkdownTypes.WIKI_LINK_REF_TEXT
    abstract fun getLinkRefAnchorMarkerType(): IElementType? //= MultiMarkdownTypes.WIKI_LINK_REF_ANCHOR_MARKER
    abstract fun getLinkRefAnchorType(): IElementType? //= MultiMarkdownTypes.WIKI_LINK_REF_ANCHOR

    override fun createStub(psi: Elem, parentStub: StubElement<*>): Stub {
        return createStub(parentStub, psi.linkRefWithAnchorText)
    }

    @Throws(IOException::class)
    override fun serialize(stub: Stub, dataStream: StubOutputStream) {
        //dataStream.writeName(stub.getKey());
        dataStream.writeName(stub.linkRefWithAnchorText)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<PsiElement>): Stub {
        val linkRefWithAnchorText = dataStream.readName()?.string ?: ""
        return createStub(parentStub, linkRefWithAnchorText)
    }

    override fun indexStub(stub: Stub, sink: IndexSink) {
        sink.occurrence(MdLinkElementIndex.KEY, stub.linkRefWithAnchorText)
    }

    override fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): Stub {
        val children = tree.getChildren(node)
        var linkRefWithAnchorText: String = ""

        var count = 1
        if (getLinkRefAnchorMarkerType() != null) count++
        if (getLinkRefAnchorType() != null) count++

        for (child in children) {
            if (child.tokenType == getLinkRefTextType()) {
                linkRefWithAnchorText += if (child !is LighterASTTokenNode) child.toString() else intern(tree.charTable, child)
                if (--count == 0) break
            }
            if (child.tokenType == getLinkRefAnchorMarkerType()) {
                linkRefWithAnchorText += if (child !is LighterASTTokenNode) child.toString() else intern(tree.charTable, child)
                if (--count == 0) break
            }
            if (child.tokenType == getLinkRefAnchorType()) {
                linkRefWithAnchorText += if (child !is LighterASTTokenNode) child.toString() else intern(tree.charTable, child)
                break
            }
        }
        return createStub(parentStub, linkRefWithAnchorText)
    }

    companion object {
        private fun intern(table: CharTable, node: LighterASTNode): String {
            assert(node is LighterASTTokenNode, { node })
            return table.intern((node as LighterASTTokenNode).text).toString()
        }
    }
}
