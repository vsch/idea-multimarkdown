// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.todo;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.id.LexerBasedIdIndexer;
import com.vladsch.md.nav.parser.MdLexParser;
import org.jetbrains.annotations.NotNull;

public class MdIdIndexer extends LexerBasedIdIndexer {

    public static Lexer createIndexingLexer(OccurrenceConsumer consumer) {
        return new MdFilterLexer(MdLexParser.createLexer(null), consumer);
    }

    @NotNull
    @Override
    public Lexer createLexer(@NotNull final OccurrenceConsumer consumer) {
        return createIndexingLexer(consumer);
    }
}
