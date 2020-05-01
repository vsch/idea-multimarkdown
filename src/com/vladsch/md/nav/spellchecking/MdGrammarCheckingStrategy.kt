// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.spellchecking

import com.intellij.grazie.grammar.strategy.GrammarCheckingStrategy
import com.intellij.grazie.grammar.strategy.impl.RuleGroup
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.vladsch.md.nav.parser.LexParserState
import com.vladsch.md.nav.psi.element.MdDefinition
import com.vladsch.md.nav.psi.element.MdDefinitionTerm
import com.vladsch.md.nav.psi.element.MdListItem
import com.vladsch.md.nav.psi.element.MdParagraph
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.plugin.util.psi.isTypeIn

class MdGrammarCheckingStrategy : GrammarCheckingStrategy {
    private val lexerState: LexParserState.State = LexParserState.getInstance().state

    private val contextRoots: TokenSet = TokenSet.orSet(
        TokenSet.create(MdTypes.TEXT_BLOCK),
        MdTokenSets.TABLE_TEXT_SET,
        MdTokenSets.HEADER_TEXT_SET
    )

    private val inlineNonText: TokenSet = TokenSet.create(*lexerState.INLINE_NON_PLAIN_TEXT.toTypedArray())

    override fun isMyContextRoot(element: PsiElement): Boolean {
        val isRoot = element.node.isTypeIn(contextRoots)
//        println("Trying: $element isRoot: $isRoot")
        return isRoot
    }

    override fun isEnabledByDefault(): Boolean = true

    override fun getContextRootTextDomain(root: PsiElement): GrammarCheckingStrategy.TextDomain {
        return GrammarCheckingStrategy.TextDomain.PLAIN_TEXT
    }

    override fun getElementBehavior(root: PsiElement, child: PsiElement): GrammarCheckingStrategy.ElementBehavior {
        return when {
            // FIX: need to have inline code text ignored by marking ABSORB
            isInlineMarker(child) -> GrammarCheckingStrategy.ElementBehavior.STEALTH
            else -> GrammarCheckingStrategy.ElementBehavior.TEXT
        }
    }

    override fun getIgnoredRuleGroup(root: PsiElement, child: PsiElement): RuleGroup? {
        // NOTE: ignore CASING for simple, single line list items
        return if (isSimpleTextItem(root)) RuleGroup.CASING else null
    }

    private fun isInlineMarker(element: PsiElement): Boolean {
        return element.node.isTypeIn(inlineNonText)
    }

    /**
     * @param root root element for the text
     * @return true if this element is simple text for which casing rules should not be applied
     */
    private fun isSimpleTextItem(root: PsiElement): Boolean {
        val isSimpleTextItem = if (root.node.isTypeIn(MdTypes.TEXT_BLOCK) && MdApplicationSettings.instance.documentSettings.grammarIgnoreSimpleTextCasing) {
            var parent = root.parent
            if (parent is MdParagraph) parent = parent.parent
            when (parent) {
                is MdDefinitionTerm -> true
                is MdListItem, is MdDefinition -> {
                    val eolPos = root.text.indexOf('\n')
                    (eolPos == -1 || eolPos == root.textLength - 1) && MdPsiImplUtil.isFirstIndentedBlock(root, false)
                }
                else -> false
            }
        } else false
        return isSimpleTextItem
    }
}
