// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.psi.util.MdTypes

class MdExplicitLinkStubElementType(debugName: String) : MdLinkElementStubElementType<MdExplicitLink, MdExplicitLinkStub>(debugName) {
    override fun createPsi(stub: MdExplicitLinkStub) = MdExplicitLinkImpl(stub, this)
    override fun getExternalId(): String = "markdown.link-element.explicit-link"
    override fun createStub(parentStub: StubElement<PsiElement>, linkRefWithAnchorText: String): MdExplicitLinkStub = MdExplicitLinkStubImpl(parentStub, linkRefWithAnchorText)
    override fun getLinkRefTextType(): IElementType = MdTypes.LINK_REF
    override fun getLinkRefAnchorMarkerType(): IElementType? = MdTypes.LINK_REF_ANCHOR_MARKER
    override fun getLinkRefAnchorType(): IElementType? = MdTypes.LINK_REF_ANCHOR
}
