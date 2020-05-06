// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.text.ImmutableCharSequence;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.editor.api.MdPreviewCustomizationProvider;
import com.vladsch.md.nav.parser.api.HtmlPurpose;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class MdLexParserManager {
    public static int LEXER_PEGDOWN_AND_MASK = ~0;
    public static int LEXER_PEGDOWN_OR_MASK = Extensions.MULTI_LINE_IMAGE_URLS;
    public static int PARSER_PEGDOWN_AND_MASK = ~Extensions.HARDWRAPS;
    public static int PARSER_PEGDOWN_OR_MASK = Extensions.MULTI_LINE_IMAGE_URLS | Extensions.INTELLIJ_DUMMY_IDENTIFIER;

    public static Document parseFlexmarkDocument(@NotNull MdRenderingProfile renderingProfile, @NotNull final CharSequence buffer, boolean forParser) {
        return parseFlexmarkDocument(renderingProfile, buffer, null, forParser);
    }

    public static Document parseFlexmarkDocument(@NotNull MdRenderingProfile renderingProfile, @NotNull final CharSequence buffer, @Nullable Integer pegdownExtensions, boolean forParser) {
        int andMask = forParser ? PARSER_PEGDOWN_AND_MASK : LEXER_PEGDOWN_AND_MASK;
        int orMask = forParser ? PARSER_PEGDOWN_OR_MASK : LEXER_PEGDOWN_OR_MASK;
        return parseFlexmarkDocument(renderingProfile, buffer, pegdownExtensions, andMask, orMask);
    }

    public static Document parseFlexmarkDocument(@NotNull MdRenderingProfile renderingProfile, @NotNull final CharSequence buffer, @Nullable Integer pegdownExtensions, int andMask, int orMask) {
        int pegdownExtensionFlags = renderingProfile.getParserSettings().getPegdownFlags();
        long parserOptionsFlags = renderingProfile.getParserSettings().getOptionsFlags();

        return parseFlexmarkDocument(buffer, pegdownExtensions, andMask, orMask, pegdownExtensionFlags, parserOptionsFlags, renderingProfile);
    }

    @Nullable
    public static Document parseFlexmarkDocument(@NotNull final CharSequence buffer, @Nullable Integer pegdownExtensions, int andMask, int orMask, int pegdownExtensionFlags, long parserOptionsFlags, @NotNull MdRenderingProfile renderingProfile) {
        int actualPegdownExtensions = ((pegdownExtensions != null ? pegdownExtensions : pegdownExtensionFlags) & andMask) | orMask;
        long actualParserOptions = parserOptionsFlags;

        PegdownOptionsAdapter optionsAdapter = new PegdownOptionsAdapter(actualPegdownExtensions, actualParserOptions);
        Document rootNode = null;

        DataHolder options = optionsAdapter.getFlexmarkOptions(ParserPurpose.PARSER, HtmlPurpose.RENDER, null, renderingProfile);
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
        return parseMarkdown(renderingProfile, buffer, null, null);
    }

    /**
     * @param renderingProfile
     * @param buffer
     * @param pegdownExtensions
     * @param parserOptions
     */
    @NotNull
    public static LexerData parseMarkdown(@NotNull MdRenderingProfile renderingProfile, @NotNull final CharSequence buffer, @Nullable Integer pegdownExtensions, @Nullable Long parserOptions) {
        int actualPegdownExtensions = ((pegdownExtensions != null ? pegdownExtensions : renderingProfile.getParserSettings().getPegdownFlags()) & PARSER_PEGDOWN_AND_MASK) | PARSER_PEGDOWN_OR_MASK;
        long actualParserOptions = parserOptions != null ? parserOptions : renderingProfile.getParserSettings().getOptionsFlags();

        PegdownOptionsAdapter optionsAdapter = new PegdownOptionsAdapter(actualPegdownExtensions, actualParserOptions);
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
                    if (provider.createParserErrorReport(actualPegdownExtensions, actualParserOptions, options, e, buffer.toString())) break;
                }

                return new LexerData(MdLexParser.EMPTY_TOKENS, new MdASTCompositeNode(MdParserDefinition.MARKDOWN_FILE, 0, buffer.length()));
            }
        }

        LexerData lexerData = MdLexParser.parseFlexmarkMarkdown(rootNode, actualPegdownExtensions, actualParserOptions);
        return lexerData;
    }
}
