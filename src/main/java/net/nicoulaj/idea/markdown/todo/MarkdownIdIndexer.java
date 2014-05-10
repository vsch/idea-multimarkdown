/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.todo;

import com.intellij.lexer.EmptyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.id.LexerBasedIdIndexer;

/**
 * {@code Id} indexer for Markdown.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.9
 */
public class MarkdownIdIndexer extends LexerBasedIdIndexer {

    public static Lexer createIndexingLexer(OccurrenceConsumer consumer) {
        return new MarkdownFilterLexer(new EmptyLexer(), consumer);
    }

    @Override
    public Lexer createLexer(final OccurrenceConsumer consumer) {
        return createIndexingLexer(consumer);
    }
}
