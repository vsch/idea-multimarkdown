// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.psi.util.MdTypes

class MdWikiLinkStubElementType(debugName: String) : MdLinkElementStubElementType<MdWikiLink, MdWikiLinkStub>(debugName) {
    override fun createPsi(stub: MdWikiLinkStub) = MdWikiLinkImpl(stub, this)
    override fun getExternalId(): String = "markdown.link-element.wiki"
    override fun createStub(parentStub: StubElement<*>, linkRefWithAnchorText: String): MdWikiLinkStub = MdWikiLinkStubImpl(parentStub, linkRefWithAnchorText)
    override fun getLinkRefTextType(): IElementType = MdTypes.WIKI_LINK_REF
    override fun getLinkRefAnchorMarkerType(): IElementType? = MdTypes.WIKI_LINK_REF_ANCHOR_MARKER
    override fun getLinkRefAnchorType(): IElementType? = MdTypes.WIKI_LINK_REF_ANCHOR
}
