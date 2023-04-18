// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.psi.util.BasicTextMapElementTypeProvider
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.psi.util.TextMapElementType
import com.vladsch.md.nav.psi.util.TextMapMatch

class MdJekyllFrontMatterBlockStubElementType(debugName: String) : MdPlainTextStubElementType<MdJekyllFrontMatterBlock, MdJekyllFrontMatterBlockStub>(debugName) {

    override fun createPsi(stub: MdJekyllFrontMatterBlockStub): MdJekyllFrontMatterBlock = MdJekyllFrontMatterBlockImpl(stub, this)
    override fun getExternalId(): String = "markdown.plain-text-referenceable.jekyll-front-matter"
    override fun createStub(parentStub: StubElement<*>, textMapType: TextMapElementType, textMapMatches: Array<TextMapMatch>, referenceableOffsetInParent: Int): MdJekyllFrontMatterBlockStub = MdJekyllFrontMatterBlockStubImpl(parentStub, textMapType, textMapMatches, referenceableOffsetInParent)
    override fun getReferenceableTextType(): IElementType = MdTypes.JEKYLL_FRONT_MATTER_BLOCK
    override fun getTextMapType(): TextMapElementType = BasicTextMapElementTypeProvider.JEKYLL_FRONT_MATTER
}
