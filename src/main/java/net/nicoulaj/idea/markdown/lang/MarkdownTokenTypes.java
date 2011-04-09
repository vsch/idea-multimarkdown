/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.lang;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

/**
 * Lexer tokens for the Markdown language.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public interface MarkdownTokenTypes extends TokenType {

    /**
     * Plain text token type.
     */
    IElementType TEXT = new MarkdownElementType("TEXT");

    /**
     * Bold text token type.
     */
    IElementType BOLD = new MarkdownElementType("BOLD");

    /**
     * Italic token type.
     */
    IElementType ITALIC = new MarkdownElementType("ITALIC");

    /**
     * Link token type.
     */
    IElementType LINK = new MarkdownElementType("LINK");

    /**
     * Image token type.
     */
    IElementType IMAGE = new MarkdownElementType("IMAGE");

    /**
     * Header of level 1 token type.
     */
    IElementType HEADER_LEVEL_1 = new MarkdownElementType("HEADER_LEVEL_1");

    /**
     * Header of level 2 token type.
     */
    IElementType HEADER_LEVEL_2 = new MarkdownElementType("HEADER_LEVEL_2");

    /**
     * Header of level 3 token type.
     */
    IElementType HEADER_LEVEL_3 = new MarkdownElementType("HEADER_LEVEL_3");

    /**
     * Header of level 4 token type.
     */
    IElementType HEADER_LEVEL_4 = new MarkdownElementType("HEADER_LEVEL_4");

    /**
     * Header of level 5 token type.
     */
    IElementType HEADER_LEVEL_5 = new MarkdownElementType("HEADER_LEVEL_5");

    /**
     * Header of level 6 token type.
     */
    IElementType HEADER_LEVEL_6 = new MarkdownElementType("HEADER_LEVEL_6");

    /**
     * Code token type.
     */
    IElementType CODE = new MarkdownElementType("CODE");

    /**
     * Quote token type.
     */
    IElementType QUOTE = new MarkdownElementType("QUOTE");

    /**
     * Table token type.
     */
    IElementType TABLE = new MarkdownElementType("TABLE");
}
