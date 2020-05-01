// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.todo;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;
import org.jetbrains.annotations.NotNull;

public class MdTodoIndexer extends LexerBasedTodoIndexer {
    @NotNull
    @Override
    public Lexer createLexer(@NotNull OccurrenceConsumer consumer) {
        return MdIdIndexer.createIndexingLexer(consumer);
    }
}
