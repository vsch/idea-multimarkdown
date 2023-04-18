// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.lang.LighterASTTokenNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IElementType
import com.intellij.util.CharTable
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.parser.api.MdLinkMapProvider
import com.vladsch.md.nav.psi.MdPlainText
import com.vladsch.md.nav.psi.MdPlainTextStub
import com.vladsch.md.nav.psi.index.MdPlainTextElementIndex
import com.vladsch.md.nav.psi.index.MdReferenceableTextIndex
import com.vladsch.md.nav.psi.util.TextMapElementType
import com.vladsch.md.nav.psi.util.TextMapMatch
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import java.io.IOException

abstract class MdPlainTextStubElementType<Elem : MdPlainText<*>, Stub : MdPlainTextStub<Elem>>(debugName: String) :
    ILightStubElementType<Stub, Elem>(debugName, MdLanguage.INSTANCE) {

    abstract override fun createPsi(stub: Stub): Elem // = MultiMarkdownWikiLinkImpl(stub, this)
    abstract override fun getExternalId(): String //= "markdown.link-element"
    abstract fun createStub(parentStub: StubElement<*>, textMapType: TextMapElementType, textMapMatches: Array<TextMapMatch>, referenceableOffsetInParent: Int): Stub
    abstract fun getReferenceableTextType(): IElementType
    abstract fun getTextMapType(): TextMapElementType

    override fun createStub(psi: Elem, parentStub: StubElement<*>): Stub {
        val psiFile = psi.containingFile
        val textMapMatches = getTextMapMatches(psiFile, getTextMapType(), psi.referenceableText)
        return createStub(parentStub, getTextMapType(), textMapMatches, getReferenceableOffsetInParent(psi.node, getReferenceableTextType()))
    }

    @Throws(IOException::class)
    override fun serialize(stub: Stub, dataStream: StubOutputStream) {
        dataStream.writeVarInt(stub.referenceableOffsetInParent)

        val matches = stub.textMapMatches
        dataStream.writeVarInt(matches.size)
        for (match in matches) {
            dataStream.writeVarInt(match.replacedStart)
            dataStream.writeVarInt(match.replacedEnd)
            dataStream.writeName(match.replacedText)
        }
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<PsiElement>): Stub {
        val referenceableOffsetInParent = dataStream.readVarInt()

        val count = dataStream.readVarInt()
        if (count == 0) return createStub(parentStub, getTextMapType(), EMPTY_MATCHES, 0)
        val matches = Array<TextMapMatch>(count) { TextMapMatch.NULL }
        for (i in 1 .. count) {
            val startOffset = dataStream.readVarInt()
            val endOffset = dataStream.readVarInt()
            val replacedText = dataStream.readName()?.string ?: ""

            if (startOffset >= endOffset) {
                matches[i - 1] = TextMapMatch.NULL
            } else {
                matches[i - 1] = TextMapMatch(0, 0, "", startOffset, endOffset, replacedText)
            }
        }
        return createStub(parentStub, getTextMapType(), matches, referenceableOffsetInParent)
    }

    override fun indexStub(stub: Stub, sink: IndexSink) {
        val matches = stub.textMapMatches

        // save in element index so we can quickly find these elements for validation purposes and re-indexing
        sink.occurrence(MdPlainTextElementIndex.KEY, stub.textMapType.name)

        // save actual results from matches
        for (match in matches) {
            sink.occurrence(MdReferenceableTextIndex.KEY, match.replacedText)
        }
    }

    override fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): Stub {
        parentStub.getParentStubOfType(PsiFile::class.java)
        var referenceableText: String = ""

        val children = tree.getChildren(node)
        var referenceableOffsetInParent = 0
        for (child in children) {
            if (child.tokenType == getReferenceableTextType()) {
                referenceableText = intern(tree.charTable, child)
                referenceableOffsetInParent = child.startOffset - node.startOffset
                break
            }
        }

        val psiFile = parentStub.getParentStubOfType(MdFile::class.java)
        val textMapMatches = getTextMapMatches(psiFile, getTextMapType(), referenceableText)
        return createStub(parentStub, getTextMapType(), textMapMatches, referenceableOffsetInParent)
    }

    companion object {
        @JvmField
        val EMPTY_MATCHES: Array<TextMapMatch> = emptyArray<TextMapMatch>()

        @JvmStatic
        fun getTextMapMatches(psiFile: PsiFile?, textMapElementType: TextMapElementType, referenceableText: String): Array<TextMapMatch> {
            if (psiFile != null) {
                val renderingProfile: MdRenderingProfile = MdRenderingProfileManager.getProfile(psiFile)
                for (provider in MdLinkMapProvider.EXTENSIONS.value) {
                    val matchList = provider.getMatchList(textMapElementType, referenceableText, renderingProfile)
                    if (matchList != null) {
                        // now use text search/replace to convert to reference that we can resolve
                        return matchList.toTypedArray()
                    }
                }
            }

            return EMPTY_MATCHES
        }

        @JvmStatic
        fun getReferenceableOffsetInParent(parentNode: ASTNode, referenceableTextType: IElementType): Int {
            val contentOffset = parentNode.findChildByType(referenceableTextType)?.startOffset ?: 0
            return contentOffset - parentNode.startOffset
        }

        protected fun intern(table: CharTable, node: LighterASTNode): String {
            assert(node is LighterASTTokenNode) { node }
            return table.intern((node as LighterASTTokenNode).text).toString()
        }
    }
}
