// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.psi.util.MdTypes

class MdReferenceStubElementType(debugName: String) : MdLinkElementStubElementType<MdReference, MdReferenceStub>(debugName) {
    override fun createPsi(stub: MdReferenceStub) = MdReferenceImpl(stub, this)
    override fun getExternalId(): String = "markdown.link-element.reference"
    override fun createStub(parentStub: StubElement<*>, linkRefWithAnchorText: String): MdReferenceStub = MdReferenceStubImpl(parentStub, linkRefWithAnchorText)
    override fun getLinkRefTextType(): IElementType = MdTypes.REFERENCE_LINK_REF
    override fun getLinkRefAnchorMarkerType(): IElementType? = MdTypes.REFERENCE_ANCHOR_MARKER
    override fun getLinkRefAnchorType(): IElementType? = MdTypes.REFERENCE_ANCHOR
}
