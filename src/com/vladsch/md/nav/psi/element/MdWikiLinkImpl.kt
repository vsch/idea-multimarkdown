// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.ParserOptions
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.LinkRef
import com.vladsch.md.nav.util.WikiLinkRef
import com.vladsch.plugin.util.prefixWith

class MdWikiLinkImpl(stub: MdWikiLinkStub?, nodeType: IStubElementType<MdWikiLinkStub, MdWikiLink>?, node: ASTNode?) :
    MdLinkElementImpl<MdWikiLink, MdWikiLinkStub>(stub, nodeType, node),
    MdWikiLink {

    constructor(stub: MdWikiLinkStub, nodeType: IStubElementType<MdWikiLinkStub, MdWikiLink>) : this(stub, nodeType, null)
    constructor(node: ASTNode) : this(null, null, node)

    override fun createLinkRef(containingFile: FileRef, linkRefText: String, linkAnchorText: String?, targetRef: FileRef?): LinkRef {
        return WikiLinkRef(containingFile, WikiLinkRef.fileAsLink(linkRefText, null), linkAnchorText, targetRef, false)
    }

    override fun getLinkRefElement(): MdWikiLinkRef? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.WIKI_LINK_REF) as MdWikiLinkRef?
    }

    override fun getLinkTextElement(): MdLinkText? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.WIKI_LINK_TEXT) as MdLinkText?
    }

    override fun getLinkAnchorElement(): MdLinkAnchor? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.WIKI_LINK_REF_ANCHOR) as MdLinkAnchor?
    }

    override fun getLinkTitleElement(): MdLinkTitle? {
        return null
    }

    override fun getPresentation(): ItemPresentation {
        return MdPsiImplUtil.getPresentation(breadcrumbInfo, displayName, getIcon(0))
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("wiki-link")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    companion object {
        fun getElementText(factoryContext: MdFactoryContext, linkRefWithAnchor: String, linkText: String?): String {
            val githubWikiLinks = factoryContext.renderingProfile.parserSettings.anyOptions(ParserOptions.GITHUB_WIKI_LINKS.flags)
            return if (githubWikiLinks)
                "[[" + (if (linkText != null && linkText.isNotEmpty() && linkRefWithAnchor != linkText) "$linkText|" else "") + linkRefWithAnchor + "]]"
            else
                "[[" + linkRefWithAnchor + (if (linkText != null && linkText.isNotEmpty() && linkRefWithAnchor != linkText) "|$linkText" else "") + "]]"
        }

        fun getElementText(factoryContext: MdFactoryContext, linkRef: String, linkText: String?, anchor: String?): String {
            return getElementText(factoryContext, linkRef + anchor.prefixWith('#'), linkText)
        }
    }
}
