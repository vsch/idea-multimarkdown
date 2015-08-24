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
package com.vladsch.idea.multimarkdown.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.psi.MarkdownTypes;
import com.vladsch.idea.multimarkdown.settings.MarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.highlighter.MarkdownSyntaxHighlighter;
import com.vladsch.idea.multimarkdown.settings.MarkdownGlobalSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownAnnotator extends ExternalAnnotator<String, Set<MarkdownAnnotator.HighlightableToken>> {

    private static final Logger LOGGER = Logger.getInstance(MarkdownAnnotator.class);
    private static final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new MarkdownSyntaxHighlighter();
    private MarkdownGlobalSettingsListener globalSettingsListener = null;

    /** Build a new instance of {@link MarkdownAnnotator}. */
    public MarkdownAnnotator() {
        // Listen to global settings changes.
        MarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MarkdownGlobalSettings newSettings) {
            }
        });
    }

    /**
     * Get the text source of the given file.
     *
     * @param file the {@link PsiFile} to process.
     * @return the file text.
     */
    @Nullable @Override
    public String collectInformation(@NotNull PsiFile file) {
        return file.getText();
    }

    /**
     * Collect {@link MarkdownAnnotator.HighlightableToken}s from the given file.
     *
     * @param source the source text to process.
     * @return a {@link Set} of {@link MarkdownAnnotator.HighlightableToken}s that should be used to do the file syntax highlighting.
     */
    @Override
    public Set<HighlightableToken> doAnnotate(final String source) {
        return new HashSet<HighlightableToken>();
    }

    /**
     * Convert collected {@link MarkdownAnnotator.HighlightableToken}s in syntax highlighting annotations.
     *
     * @param file             the source file.
     * @param annotationResult the {@link Set} of {@link MarkdownAnnotator.HighlightableToken}s collected on the file.
     * @param holder           the annotation holder.
     */
    @Override
    public void apply(final @NotNull PsiFile file,
            final Set<HighlightableToken> annotationResult,
            final @NotNull AnnotationHolder holder) {

        for (final HighlightableToken token : annotationResult) {
            final TextAttributesKey[] attrs = SYNTAX_HIGHLIGHTER.getTokenHighlights(token.getElementType());

            if (attrs.length > 0) holder.createInfoAnnotation(token.getRange(), null).setTextAttributes(attrs[0]);
        }
    }

    protected class HighlightableToken {

        protected final TextRange range;
        protected final IElementType elementType;

        public HighlightableToken(final TextRange range, final IElementType elementType) {
            this.range = range;
            this.elementType = elementType;
        }

        public TextRange getRange() { return range; }

        public IElementType getElementType() { return elementType; }
    }
}
