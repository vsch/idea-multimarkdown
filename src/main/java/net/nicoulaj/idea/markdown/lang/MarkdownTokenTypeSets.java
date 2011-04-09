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

import com.intellij.psi.tree.TokenSet;

/**
 * Token type sets for the Markdown language.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public interface MarkdownTokenTypeSets extends MarkdownTokenTypes {

    /**
     * Plain text token type set.
     */
    public static final TokenSet PLAIN_TEXT_SET = TokenSet.create(TEXT);

    /**
     * Bold text token type set.
     */
    public static final TokenSet BOLD_SET = TokenSet.create(BOLD);

    /**
     * Italic text token type set.
     */
    public static final TokenSet ITALIC_SET = TokenSet.create(ITALIC);

    /**
     * Link token type set.
     */
    public static final TokenSet LINK_SET = TokenSet.create(LINK);

    /**
     * Image token type set.
     */
    public static final TokenSet IMAGE_SET = TokenSet.create(IMAGE);

    /**
     * Header of level 1 token type set.
     */
    public static final TokenSet HEADER_LEVEL_1_SET = TokenSet.create(HEADER_LEVEL_1);

    /**
     * Header of level 2 token type set.
     */
    public static final TokenSet HEADER_LEVEL_2_SET = TokenSet.create(HEADER_LEVEL_2);

    /**
     * Header of level 3 token type set.
     */
    public static final TokenSet HEADER_LEVEL_3_SET = TokenSet.create(HEADER_LEVEL_3);

    /**
     * Header of level 4 token type set.
     */
    public static final TokenSet HEADER_LEVEL_4_SET = TokenSet.create(HEADER_LEVEL_4);

    /**
     * Header of level 5 token type set.
     */
    public static final TokenSet HEADER_LEVEL_5_SET = TokenSet.create(HEADER_LEVEL_5);

    /**
     * Header of level 6 token type set.
     */
    public static final TokenSet HEADER_LEVEL_6_SET = TokenSet.create(HEADER_LEVEL_6);

    /**
     * Code token type set.
     */
    public static final TokenSet CODE_SET = TokenSet.create(CODE);

    /**
     * Quote token type set.
     */
    public static final TokenSet QUOTE_SET = TokenSet.create(QUOTE);

    /**
     * Table token type set.
     */
    public static final TokenSet TABLE_SET = TokenSet.create(TABLE);

    /**
     * HRule token type set.
     */
    public static final TokenSet HRULE_SET = TokenSet.create(HRULE);
}
