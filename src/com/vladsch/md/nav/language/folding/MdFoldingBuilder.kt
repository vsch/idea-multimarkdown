// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.language.api.MdFoldingBuilderProvider
import com.vladsch.md.nav.psi.element.MdPsiElement
import com.vladsch.md.nav.psi.util.MdTypes.*
import com.vladsch.plugin.util.image.ImageUtils

class MdFoldingBuilder private constructor(val settings: MdFoldingSettings) : FoldingBuilderEx() {
    constructor() : this(MdFoldingSettings.getInstance())

    private val defaultPlaceHolderText = MdBundle.message("code-folding.default.placeholder")

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        if (root !is MdPsiElement) {
            return EMPTY_DESCRIPTORS
        }

        val descriptors = ArrayList<FoldingDescriptor>()
        val inner = MdFoldingVisitor(root, document, descriptors, quick, defaultPlaceHolderText)
        inner.buildFoldingRegions(root)

        return descriptors.toArray(arrayOfNulls<FoldingDescriptor>(descriptors.size))
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        return defaultPlaceHolderText
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        LOG.debug { "isCollapsedByDefault(" + node.elementType + ") node.parent " + node.treeParent }

        val type = node.elementType

        return when (type) {
            VERBATIM -> if (node.findChildByType(VERBATIM_LANG) != null) settings.COLLAPSE_CODE_FENCE_BLOCKS else settings.COLLAPSE_VERBATIM_BLOCKS
            REFERENCE_LINK_REF -> settings.COLLAPSE_REFERENCES
            LINK_REF -> settings.COLLAPSE_EXPLICIT_LINKS
            IMAGE_LINK_REF -> if (ImageUtils.isPossiblyEncodedImage(node.text)) settings.COLLAPSE_EMBEDDED_IMAGES else settings.COLLAPSE_IMAGES
            IMAGE_URL_CONTENT -> settings.COLLAPSE_MULTILINE_URL_IMAGES
            JEKYLL_FRONT_MATTER_BLOCK_ELEM -> settings.COLLAPSE_JEKYLL_FRONT_MATTER
            ATX_HEADER -> when ((node.findChildByType(HEADER_ATX_MARKER))?.textLength ?: 0) {
                1 -> settings.COLLAPSE_HEADINGS_1
                2 -> settings.COLLAPSE_HEADINGS_2
                3 -> settings.COLLAPSE_HEADINGS_3
                4 -> settings.COLLAPSE_HEADINGS_4
                5 -> settings.COLLAPSE_HEADINGS_5
                6 -> settings.COLLAPSE_HEADINGS_6
                else -> false
            }
            SETEXT_HEADER -> when ((node.findChildByType(HEADER_SETEXT_MARKER))?.text?.get(0)) {
                '=' -> settings.COLLAPSE_HEADINGS_1
                '-' -> settings.COLLAPSE_HEADINGS_2
                else -> false
            }
            BULLET_LIST_ITEM -> settings.COLLAPSE_LIST_ITEMS
            ORDERED_LIST_ITEM -> settings.COLLAPSE_LIST_ITEMS
            COMMENT -> settings.COLLAPSE_COMMENTS
            BLOCK_COMMENT -> settings.COLLAPSE_COMMENTS
            else -> {
                // allow extensions to add their settings/nodes
                for (provider in MdFoldingBuilderProvider.EXTENSIONS.value) {
                    provider.isCollapsedByDefault(node)?.let { return it }
                }
                false
            }
        }
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.language.folding")
        private val EMPTY_DESCRIPTORS = arrayOf<FoldingDescriptor>()
    }
}
