// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.text.ImmutableCharSequence;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.editor.api.MdPreviewCustomizationProvider;
import com.vladsch.md.nav.parser.api.HtmlPurpose;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdLexParserManager {
    @Nullable
    public static Document parseFlexmarkDocument(@NotNull MdRenderingProfile renderingProfile, @NotNull final CharSequence buffer, boolean forParser) {
        PegdownOptionsAdapter optionsAdapter = new PegdownOptionsAdapter();
        Document rootNode = null;

        DataHolder options = optionsAdapter.getFlexmarkOptions(forParser ? ParserPurpose.PARSER : ParserPurpose.HTML, HtmlPurpose.RENDER, null, renderingProfile);
        Parser parser = Parser.builder(options).build();
        String exceptionText = null;
        // use an immutable copy so it does not change in the process of being parsed
        final BasedSequence input = buffer instanceof BasedSequence ? (BasedSequence) buffer : BasedSequence.of(ImmutableCharSequence.asImmutable(buffer));

        if (ApplicationManager.getApplication().isUnitTestMode()) {
            rootNode = parser.parse(input);
        } else {
            try {
                rootNode = parser.parse(input);
            } catch (Throwable e) {
                exceptionText = e.getMessage();
                return null;
            }
        }

        return rootNode;
    }

    @NotNull
    public static LexerData parseMarkdown(@NotNull MdRenderingProfile renderingProfile, @NotNull final CharSequence buffer) {
        int pegdownExtensions = renderingProfile.getParserSettings().getPegdownFlags();
        long parserOptions = renderingProfile.getParserSettings().getOptionsFlags();

        PegdownOptionsAdapter optionsAdapter = new PegdownOptionsAdapter();
        Document rootNode = null;

        DataHolder options = optionsAdapter.getFlexmarkOptions(ParserPurpose.PARSER, HtmlPurpose.RENDER, null, renderingProfile);
        Parser parser = Parser.builder(options).build();
        // use an immutable copy so it does not change in the process of being parsed
        final BasedSequence input = BasedSequence.of(ImmutableCharSequence.asImmutable(buffer));

        if (MdApplicationSettings.getInstance().getDebugSettings().getGenerateParserExceptions()) {
            rootNode = parser.parse(input);
        } else {
            try {
                rootNode = parser.parse(input);
            } catch (Throwable e) {
                for (MdPreviewCustomizationProvider provider : MdPreviewCustomizationProvider.EXTENSIONS.getValue()) {
                    if (provider.createParserErrorReport(pegdownExtensions, parserOptions, options, e, buffer.toString())) break;
                }

                return new LexerData(MdLexParser.EMPTY_TOKENS, new MdASTCompositeNode(MdParserDefinition.MARKDOWN_FILE, 0, buffer.length()));
            }
        }

        LexerData lexerData = MdLexParser.parseFlexmarkMarkdown(rootNode);
        return lexerData;
    }
}
