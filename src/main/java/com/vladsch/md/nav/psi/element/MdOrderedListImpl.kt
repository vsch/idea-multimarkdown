// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.SequenceUtils
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.settings.ParserOptions
import icons.MdIcons
import javax.swing.Icon

open class MdOrderedListImpl(node: ASTNode) : MdListImpl(node), MdOrderedList {
    companion object {
        val SPACE_TAB_DOT_PAREN: String = " \t.)"
        val SPACE_TAB_DOT_PAREN_SET = CharPredicate.anyOf(SPACE_TAB_DOT_PAREN)
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ORDERED_LIST
    }

    override fun getPresentableText(): String? {
        return MdPsiBundle.message("ordered-list")
    }

    override fun itemOrdinalOffset(skipFirst: Boolean, editContext: PsiEditContext): Int {
        var offset = 0
        if (editContext.styleSettings.LIST_RENUMBER_ITEMS && !editContext.styleSettings.LIST_RESET_FIRST_ITEM_NUMBER) {
            // see if rendering profile list style is common mark
            val parserSettings = MdRenderingProfileManager.getProfile(containingFile).parserSettings
            if (parserSettings.anyOptions(ParserOptions.COMMONMARK_LISTS)) {
                // need to get the first item prefix number
                var item = firstChild
                var first = skipFirst

                while (item != null) {
                    if (item is MdListItemImpl) {
                        if (!first) break
                        first = false
                    }
                    item = item.nextSibling
                }

                if (item is MdOrderedListItemImpl) {
                    offset = SequenceUtils.parseUnsignedIntOrNull(BasedSequence.of(item.actualItemPrefix(editContext)).trimEnd(SPACE_TAB_DOT_PAREN_SET).toString())?.minus(1) ?: 0
                }
            }
        }
        return offset
    }
}
