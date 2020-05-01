// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.todo;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.BaseFilterLexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.tree.IElementType;

import static com.vladsch.md.nav.psi.util.MdTokenSets.COMMENT_FOR_TODO_SET;
import static com.vladsch.plugin.util.psi.PsiUtils.isTypeOf;

public class MdFilterLexer extends BaseFilterLexer {
    public MdFilterLexer(final Lexer originalLexer, final OccurrenceConsumer table) {
        super(originalLexer, table);
    }

    @Override
    public void advance() {
        final IElementType tokenType = myDelegate.getTokenType();
        if (isTypeOf(tokenType, COMMENT_FOR_TODO_SET)) {
            scanWordsInToken(UsageSearchContext.IN_COMMENTS, false, false);
            advanceTodoItemCountsInToken();
        }
        myDelegate.advance();
    }
}
