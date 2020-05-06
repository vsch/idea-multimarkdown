// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.psi.util.MdTypes

class MdAutoLinkStubElementType(debugName: String) : MdLinkElementStubElementType<MdAutoLink, MdAutoLinkStub>(debugName) {
    override fun createPsi(stub: MdAutoLinkStub) = MdAutoLinkImpl(stub, this)
    override fun getExternalId(): String = "markdown.link-element.auto-link"
    override fun createStub(parentStub: StubElement<PsiElement>, linkRefWithAnchorText: String): MdAutoLinkStub = MdAutoLinkStubImpl(parentStub, linkRefWithAnchorText)
    override fun getLinkRefTextType(): IElementType? = null
    override fun getLinkRefAnchorMarkerType(): IElementType? = MdTypes.AUTO_LINK_ANCHOR_MARKER
    override fun getLinkRefAnchorType(): IElementType? = MdTypes.AUTO_LINK_ANCHOR
}
