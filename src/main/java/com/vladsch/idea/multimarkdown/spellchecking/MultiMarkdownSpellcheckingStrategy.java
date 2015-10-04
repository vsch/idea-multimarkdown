/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package com.vladsch.idea.multimarkdown.spellchecking;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.spellchecker.quickfixes.SpellCheckerQuickFix;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import com.vladsch.idea.multimarkdown.parser.MultiMarkdownParserDefinition;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownElementType;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownTokenType;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*;

/**
 * {@link SpellcheckingStrategy} for Markdown.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.9
 */
public class MultiMarkdownSpellcheckingStrategy extends SpellcheckingStrategy {
    private static final Logger logger = Logger.getLogger(MultiMarkdownSpellcheckingStrategy.class);
    private static TokenSet NO_SPELL_CHECK_SET = TokenSet.create(
            MultiMarkdownParserDefinition.MULTIMARKDOWN_FILE,
            NONE,
            TokenType.WHITE_SPACE,
            //ABBREVIATION,
            //ABBREVIATED_TEXT,
            ANCHOR_LINK,
            //AUTO_LINK,
            //BLOCK_QUOTE,
            //BOLD,
            BOLD_MARKER,
            //BOLDITALIC,
            BULLET_LIST,
            //CODE,
            //DEFINITION,
            DEFINITION_LIST,
            DEFINITION_TERM,
            //EXPLICIT_LINK,
            //HEADER_LEVEL_1,
            //SETEXT_HEADER_LEVEL_1,
            //HEADER_LEVEL_2,
            //SETEXT_HEADER_LEVEL_2,
            //HEADER_LEVEL_3,
            //HEADER_LEVEL_4,
            //HEADER_LEVEL_5,
            //HEADER_LEVEL_6,
            HRULE,
            //HTML_BLOCK,
            //IMAGE,
            //INLINE_HTML,
            //ITALIC,
            ITALIC_MARKER,
            LIST_ITEM,
            MAIL_LINK,
            ORDERED_LIST,
            //QUOTE,
            //REFERENCE,
            //REFERENCE_IMAGE,
            //REFERENCE_LINK,
            SMARTS,
            SPECIAL_TEXT,
            //STRIKETHROUGH,
            //STRIKETHROUGH_BOLD,
            //STRIKETHROUGH_ITALIC,
            //STRIKETHROUGH_BOLDITALIC,
            STRIKETHROUGH_MARKER,
            TABLE,
            TABLE_BODY,
            //TABLE_CAPTION,
            //TABLE_CELL_REVEN_CEVEN,
            //TABLE_CELL_REVEN_CODD,
            //TABLE_CELL_RODD_CEVEN,
            //TABLE_CELL_RODD_CODD,
            TABLE_COLUMN,
            TABLE_HEADER,
            TABLE_ROW_EVEN,
            TABLE_ROW_ODD,
            TASK_ITEM,
            TASK_DONE_ITEM,
            TASK_ITEM_MARKER,
            TASK_DONE_ITEM_MARKER,
            //TEXT,
            //VERBATIM,
            WIKI_LINK_OPEN,
            WIKI_LINK_SEPARATOR,
            WIKI_LINK_CLOSE,
            //WIKI_LINK_REF,
            //WIKI_LINK_TITLE,
            //COMMENT,
            WIKI_LINK
    );

    protected static Tokenizer IDENTIFIER_TOKENIZER = new MultiMarkdownIdentifierTokenizer();

    @NotNull @Override
    public Tokenizer getTokenizer(PsiElement element) {
        if (element.getNode().getElementType() instanceof MultiMarkdownTokenType) {
            if (element instanceof MultiMarkdownNamedElement) {
                //logger.info("identifier tokenizer for " + element.toString());
                return TEXT_TOKENIZER;
            }

            if (NO_SPELL_CHECK_SET.contains(element.getNode().getElementType())) {
                //logger.info("empty tokenizer for " + element.toString());
                return EMPTY_TOKENIZER;
            }
            // here we can return custom tokenizers if needed

            //logger.info("text tokenizer for " + element.toString());
            return TEXT_TOKENIZER;
        }
        return super.getTokenizer(element);
    }

    @Override
    public SpellCheckerQuickFix[] getRegularFixes(PsiElement element,
            int offset,
            @NotNull TextRange textRange,
            boolean useRename,
            String wordWithTypo) {

        SpellCheckerQuickFix[] fixes = getDefaultRegularFixes(useRename, wordWithTypo);

        if (element instanceof MultiMarkdownWikiPageRef && useRename) {
            fixes[0] = new TypoRenameToQuickFix(wordWithTypo);
        }

        return fixes;
    }
}
