// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.settings.ListIndentationType
import com.vladsch.md.nav.util.format.LinePrefixMatcher
import com.vladsch.md.nav.util.format.SpacePrefixMatcher
import javax.swing.Icon

class MdDefinitionImpl(node: ASTNode) : MdIndentingCompositeImpl(node), MdDefinition {
    override fun getPrefixMatcher(editContext: PsiEditContext): LinePrefixMatcher {
        return when (editContext.renderingProfile.parserSettings.parserListIndentationType) {
            ListIndentationType.COMMONMARK -> SpacePrefixMatcher.maxSpaces(contentIndent())
            ListIndentationType.GITHUB -> SpacePrefixMatcher.maxSpaces(contentIndent())
            else -> SpacePrefixMatcher.maxSpaces(4)
        }
    }

//    override fun isFirstItemBlockPrefix(element: PsiElement): Boolean {
//        // if there is an EOL between item marker and element, ie empty item but markers not on same line
//        val itemMarker = node.firstChildNode
//        if (itemMarker != null && itemMarker.treeNext?.elementType == MdTypes.EOL) {
//            return false
//        }
//        return true
//    }

    override fun getIcon(flags: Int): Icon? {
        return null //MarkdownIcons.DEFINITION
    }
}
