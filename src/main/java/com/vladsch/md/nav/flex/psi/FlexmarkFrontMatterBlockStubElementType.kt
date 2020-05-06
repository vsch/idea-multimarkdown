// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.flex.psi.util.FlexTextMapElementTypeProvider
import com.vladsch.md.nav.psi.element.MdPlainTextStubElementType
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.psi.util.TextMapElementType
import com.vladsch.md.nav.psi.util.TextMapMatch

class FlexmarkFrontMatterBlockStubElementType(debugName: String) : MdPlainTextStubElementType<FlexmarkFrontMatterBlock, FlexmarkFrontMatterBlockStub>(debugName) {
    override fun createPsi(stub: FlexmarkFrontMatterBlockStub) = FlexmarkFrontMatterBlockImpl(stub, this)
    override fun getExternalId(): String = "markdown.plain-text-referenceable.flexmark-front-matter"
    override fun createStub(parentStub: StubElement<PsiElement>, textMapType: TextMapElementType, textMapMatches: Array<TextMapMatch>, referenceableOffsetInParent: Int): FlexmarkFrontMatterBlockStub = FlexmarkFrontMatterBlockStubImpl(parentStub, textMapType, textMapMatches, referenceableOffsetInParent)
    override fun getReferenceableTextType(): IElementType = MdTypes.FLEXMARK_FRONT_MATTER_BLOCK
    override fun getTextMapType(): TextMapElementType = FlexTextMapElementTypeProvider.FLEXMARK_FRONT_MATTER
}
