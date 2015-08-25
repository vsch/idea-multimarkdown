/*
 * Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.todo;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.BaseFilterLexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.search.UsageSearchContext;
import com.vladsch.idea.multimarkdown.parser.MarkdownLexer;

public class MarkdownFilterLexer extends BaseFilterLexer {
    final protected Lexer lexer;

    public MarkdownFilterLexer(final Lexer originalLexer, final OccurrenceConsumer table) {
        super(originalLexer, table);
        lexer = originalLexer;
    }

    @Override
    public void advance() {
        scanWordsInToken(lexer instanceof MarkdownLexer ? UsageSearchContext.IN_COMMENTS : UsageSearchContext.IN_COMMENTS, false, false);
        advanceTodoItemCountsInToken();
        myDelegate.advance();
    }
}
