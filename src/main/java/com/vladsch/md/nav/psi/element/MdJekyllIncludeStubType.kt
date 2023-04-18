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
import com.vladsch.md.nav.psi.util.MdTypes
import java.io.IOException

class MdJekyllIncludeStubType(debugName: String) :
    ILightStubElementType<MdJekyllIncludeStub, MdJekyllInclude>(debugName, MdLanguage.INSTANCE) {

    override fun createPsi(stub: MdJekyllIncludeStub): MdJekyllInclude = MdJekyllIncludeImpl(stub, this, null)
    override fun getExternalId(): String = "markdown.jekyll-include-tag"
    fun createStub(parentStub: StubElement<*>, linkRefWithAnchorText: String): MdJekyllIncludeStub = MdJekyllIncludeStubImpl(parentStub, this, linkRefWithAnchorText)
    fun getLinkRefTextType(): IElementType = MdTypes.JEKYLL_TAG_PARAMETERS

    override fun createStub(psi: MdJekyllInclude, parentStub: StubElement<*>): MdJekyllIncludeStub {
        return createStub(parentStub, psi.linkRefText)
    }

    @Throws(IOException::class)
    override fun serialize(stub: MdJekyllIncludeStub, dataStream: StubOutputStream) {
        //dataStream.writeName(stub.getKey());
        dataStream.writeName(stub.linkRefWithAnchorText)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<PsiElement>): MdJekyllIncludeStub {
        val linkRefText = dataStream.readName()?.string ?: ""
        return createStub(parentStub, linkRefText)
    }

    override fun indexStub(stub: MdJekyllIncludeStub, sink: IndexSink) {
        sink.occurrence(MdLinkElementIndex.KEY, stub.linkRefWithAnchorText)
    }

    override fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): MdJekyllIncludeStub {
        val children = tree.getChildren(node)
        var linkRefText: String = ""
        var count = 1

        for (child in children) {
            if (child.tokenType == getLinkRefTextType()) {
                linkRefText = intern(tree.charTable, child)
                if (--count == 0) break
            }
        }
        return createStub(parentStub, linkRefText)
    }

    companion object {
        fun intern(table: CharTable, node: LighterASTNode): String {
            assert(node is LighterASTTokenNode, { node })
            return table.intern((node as LighterASTTokenNode).text).toString()
        }
    }
}
