// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.highlighter.MdSyntaxHighlighter;
import com.vladsch.md.nav.parser.LexerData;
import com.vladsch.md.nav.parser.LexerToken;
import com.vladsch.md.nav.parser.MdLexParserManager;
import com.vladsch.md.nav.parser.MdLexemeProcessor;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.settings.SyntaxHighlightingType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.vladsch.flexmark.util.sequence.BasedSequence.EMPTY;

public class MdExternalAnnotator extends ExternalAnnotator<MdExternalAnnotator.ProjectCharSequence, LexerToken[]> {
    private static final Logger LOG = Logger.getInstance("com.vladsch.md.nav.annotator.external");

    private static final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new MdSyntaxHighlighter();
    private static final LexerToken[] LEXER_TOKENS = new LexerToken[0];

    public MdExternalAnnotator() {

    }

    public static class ProjectCharSequence {
        final public Project myProject;
        final public MdRenderingProfile myRenderingProfile;
        final public CharSequence myCharSequence;
        final public String mySource;

        public ProjectCharSequence(Project project, CharSequence charSequence, String source, final MdRenderingProfile renderingProfile) {
            myProject = project;
            myCharSequence = charSequence;
            mySource = source;
            myRenderingProfile = renderingProfile;
        }
    }

    @Nullable
    @Override
    public ProjectCharSequence collectInformation(@NotNull PsiFile file) {
        LOG.debug("collectInformation for " + file + " of type " + file.getFileType());
        if (MdApplicationSettings.getInstance().getDocumentSettings().getSyntaxHighlighting() == SyntaxHighlightingType.ANNOTATOR.getIntValue())
            return new ProjectCharSequence(file.getProject(), BasedSequence.of(file.getText()), file.toString(), MdRenderingProfileManager.getProfile(file));
        return new ProjectCharSequence(file.getProject(), EMPTY, file.toString(), MdRenderingProfileManager.getProfile(file));
    }

    @Nullable
    @Override
    public ProjectCharSequence collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        LOG.debug("collectInformation editor, hasErrors for " + file + " of type " + file.getFileType());
        if (MdApplicationSettings.getInstance().getDocumentSettings().getSyntaxHighlighting() == SyntaxHighlightingType.ANNOTATOR.getIntValue())
            return new ProjectCharSequence(file.getProject(), editor.getDocument().getImmutableCharSequence(), file.toString(), MdRenderingProfileManager.getProfile(file));
        return new ProjectCharSequence(file.getProject(), EMPTY, file.toString(), MdRenderingProfileManager.getProfile(file));
    }

    @Override
    public LexerToken[] doAnnotate(final ProjectCharSequence source) {
        LOG.debug("doAnnotate editor, hasErrors for " + source.mySource);
        if (MdApplicationSettings.getInstance().getDocumentSettings().getSyntaxHighlighting() == SyntaxHighlightingType.ANNOTATOR.getIntValue()) {
            final MdRenderingProfile renderingProfile = source.myRenderingProfile;

            LexerData[] lexerData = new LexerData[] { null };
            ApplicationManager.getApplication().runReadAction(() -> {
                lexerData[0] = MdLexParserManager.parseMarkdown(renderingProfile, source.myCharSequence);
            });
            if (lexerData[0] != null) {
                LexerToken[] tokens = lexerData[0].lexerTokens;
                // parse whitespace and EOL as per lexer
                CharSequence chars = source.myCharSequence;
                MdLexemeProcessor processor = new MdLexemeProcessor(chars, tokens, 0, chars.length(), 0);
                ArrayList<LexerToken> tokenList = new ArrayList<>();
                while (true) {
                    LexerToken token = processor.getLexerToken();
                    if (token == null) break;
                    tokenList.add(token);
                    processor.advance();
                }
                LexerToken[] tokensWithWhiteSpace = tokenList.toArray(LEXER_TOKENS);
                return tokensWithWhiteSpace;
            }
        }
        return LEXER_TOKENS;
    }

    @Override
    public void apply(final @NotNull PsiFile file, final LexerToken[] annotationResult, final @NotNull AnnotationHolder holder) {
        for (final LexerToken token : annotationResult) {
            final TextAttributesKey[] attrs = SYNTAX_HIGHLIGHTER.getTokenHighlights(token.getElementType());
            TextRange range = token.getTextRange();
            // FIX: use new API
            if (attrs.length > 0) holder.createInfoAnnotation(range, null).setTextAttributes(attrs[0]);
        }
    }
}
