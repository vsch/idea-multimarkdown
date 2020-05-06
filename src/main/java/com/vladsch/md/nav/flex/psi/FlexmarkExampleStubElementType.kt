// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.lang.LighterASTTokenNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import com.intellij.util.CharTable
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.psi.util.MdTypes
import java.io.IOException

class FlexmarkExampleStubElementType(debugName: String) : ILightStubElementType<FlexmarkExampleStub, FlexmarkExample>(debugName, MdLanguage.INSTANCE) {

    override fun createPsi(stub: FlexmarkExampleStub): FlexmarkExample {
        return FlexmarkExampleImpl(stub, this)
    }

    override fun createStub(psi: FlexmarkExample, parentStub: StubElement<PsiElement>): FlexmarkExampleStub {
        return FlexmarkExampleStubImpl(parentStub, psi.sectionNode?.text ?: "", psi.numberNode?.text ?: "")
    }

    override fun getExternalId(): String {
        return "flexmark.example.prop"
    }

    @Throws(IOException::class)
    override fun serialize(stub: FlexmarkExampleStub, dataStream: StubOutputStream) {
        //dataStream.writeName(stub.getKey());
        dataStream.writeName(stub.section)
        dataStream.writeName(stub.number)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<PsiElement>): FlexmarkExampleStub {
        val section = dataStream.readName()?.string ?: ""
        val number = dataStream.readName()?.string ?: ""
        return FlexmarkExampleStubImpl(parentStub, section, number)
    }

    override fun indexStub(stub: FlexmarkExampleStub, sink: IndexSink) {
        //FlexmarkExampleOptions optionsList = stub.getOptionsList();
        //if (optionsList != null) {
        //    for (String option : optionsList.getOptions()) {
        //        sink.occurrence(FlexmarkExampleIndex.KEY, option);
        //    }
        //}
    }

    override fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<PsiElement>): FlexmarkExampleStub {
        //LighterASTNode optionsListNode = LightTreeUtil.firstChildOfType(tree, node, MultiMarkdownTypes.FLEXMARK_EXAMPLE_OPTIONS);
        //String key = intern(tree.getCharTable(), optionsListNode);
        val children = tree.getChildren(node)
        var section: String = ""
        var number: String = ""
        var count = 2
        for (child in children) {
            if (child.tokenType == MdTypes.FLEXMARK_EXAMPLE_SECTION) {
                section = intern(tree.charTable, child)
                if (--count == 0) break
            }
            if (child.tokenType == MdTypes.FLEXMARK_EXAMPLE_SECTION) {
                number = intern(tree.charTable, child)
                if (--count == 0) break
            }
        }
        return FlexmarkExampleStubImpl(parentStub, section, number)
    }

    companion object {

        fun intern(table: CharTable, node: LighterASTNode): String {
            assert(node is LighterASTTokenNode, { node })
            return table.intern((node as LighterASTTokenNode).text).toString()
        }
    }
}
