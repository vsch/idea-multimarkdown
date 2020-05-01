// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.psi.util.MdTypes

class MdImageLinkStubElementType(debugName: String) : MdLinkElementStubElementType<MdImageLink, MdImageLinkStub>(debugName) {
    override fun createPsi(stub: MdImageLinkStub) = MdImageLinkImpl(stub, this)
    override fun getExternalId(): String = "markdown.link-element.image-link"
    override fun createStub(parentStub: StubElement<PsiElement>, linkRefWithAnchorText: String): MdImageLinkStub = MdImageLinkStubImpl(parentStub, linkRefWithAnchorText)
    override fun getLinkRefTextType(): IElementType = MdTypes.IMAGE_LINK_REF
    override fun getLinkRefAnchorMarkerType(): IElementType? = null
    override fun getLinkRefAnchorType(): IElementType? = null
}
