/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
package com.vladsch.idea.multimarkdown.language;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import com.vladsch.idea.multimarkdown.parser.MultiMarkdownLexer;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLink;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownWikiLinkImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.COMMENT;
import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.WIKI_LINK_REF;

public class MultiMarkdownFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return /**
         * Creates a new instance of the words scanner.
         *
         * @param lexer              the lexer used for breaking the text into tokens.
         * @param identifierTokenSet the set of token types which represent identifiers.
         * @param commentTokenSet    the set of token types which represent comments.
         * @param literalTokenSet    the set of token types which represent literals.
         * @param skipCodeContextTokenSet the set of token types which should not be considered as code context.
         */

                new DefaultWordsScanner(new MultiMarkdownLexer(),
                        TokenSet.EMPTY,
                        TokenSet.create(COMMENT),
                        TokenSet.create(WIKI_LINK_REF),
                        //TokenSet.create(TEXT),
                        TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof MultiMarkdownWikiPageRef) {
            return "wiki page ref";
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof MultiMarkdownNamedElement) {
            String name = ((MultiMarkdownNamedElement) element).getName();
            return name != null ? name : "";
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof MultiMarkdownWikiPageRef) {
            String name = ((MultiMarkdownWikiPageRef) element).getName();
            return (name == null ? "" : name);
        } else {
            return "";
        }
    }
}
