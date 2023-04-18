// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.flex.PluginBundle;
import com.vladsch.md.nav.flex.psi.FakePsiLiteralExpression;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.parser.MdLexer;
import com.vladsch.md.nav.psi.element.*;
import com.vladsch.md.nav.psi.util.MdTokenSets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vladsch.md.nav.psi.util.MdTypes.*;

public class MdFindUsagesProvider implements FindUsagesProvider {

    public static final TokenSet IDENTIFIER_TOKEN_SET = TokenSet.create(
            WIKI_LINK_REF, WIKI_LINK_TEXT, WIKI_LINK_REF_ANCHOR,
            LINK_REF, LINK_REF_TEXT, LINK_REF_ANCHOR,// LINK_REF_TITLE,
            FOOTNOTE_ID, FOOTNOTE_REF_ID, REFERENCE_TEXT, REFERENCE_ANCHOR,
            REFERENCE_LINK_REFERENCE, REFERENCE_IMAGE_REFERENCE, FLEXMARK_EXAMPLE_OPTION
    );

    public static final TokenSet SKIP_TOKEN_SET = TokenSet.orSet(MdTokenSets.NON_TEXT_INLINE_ELEMENTS, MdTokenSets.WHITESPACE_EOL_BLANK_LINE_SET);
    public static final TokenSet COMMENT_SET = MdTokenSets.COMMENT_FOR_TODO_SET;
    public static final TokenSet CODE_TOKEN_SET = TokenSet.orSet(MdTokenSets.CODE_SET);
    public static final TokenSet ALL_TOKEN_SET = TokenSet.orSet(IDENTIFIER_TOKEN_SET, COMMENT_SET, CODE_TOKEN_SET);

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        /*
          Creates a new instance of the words scanner.

          @param lexer              the lexer used for breaking the text into tokens.
         * @param identifierTokenSet the set of token types which represent identifiers.
         * @param commentTokenSet    the set of token types which represent comments.
         * @param literalTokenSet    the set of token types which represent literals.
         * @param skipCodeContextTokenSet the set of token types which should not be considered as code context.
         */
        MdLexer lexer = MdLexParser.createLexer(null);

        DefaultWordsScanner wordsScanner = new DefaultWordsScanner(
                lexer,
                IDENTIFIER_TOKEN_SET,
                COMMENT_SET,
                TokenSet.EMPTY,
                TokenSet.EMPTY) {
            @Override
            public int getVersion() {
                return super.getVersion() + 17;
            }
        };

        //wordsScanner.setMayHaveFileRefsInLiterals(false);
        //return new MarkdownWordScanner();
        return wordsScanner;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof MdNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof MdWikiLinkRef) return MdBundle.message("findusages.wikilink.page-ref");
        if (element instanceof MdWikiLinkText) return MdBundle.message("findusages.wikilink.page-title");
        if (element instanceof MdWikiLinkAnchor) return MdBundle.message("findusages.wikilink.page-anchor");
        if (element instanceof MdImageLinkRef) return MdBundle.message("findusages.image.link-ref");
        if (element instanceof MdImageLinkText) return MdBundle.message("findusages.image.link-ref-text");
        if (element instanceof MdImageLinkTitle) return MdBundle.message("findusages.image.link-ref-title");

        if (element instanceof MdVerbatimImpl) return MdBundle.message("findusages.verbatim");
        if (element instanceof MdVerbatimLanguage) return MdBundle.message("findusages.verbatim.language");
        if (element instanceof MdHtmlBlock) return MdBundle.message("findusages.html.block");
        if (element instanceof MdReference) return MdBundle.message("findusages.reference");
        if (element instanceof MdReferenceLinkRef) return MdBundle.message("findusages.reference.link.ref");
        if (element instanceof MdReferenceAnchor) return MdBundle.message("findusages.reference.anchor");
        if (element instanceof MdReferenceIdentifier) return MdBundle.message("findusages.reference.text");
        if (element instanceof MdReferenceTitle) return MdBundle.message("findusages.reference.title");
        if (element instanceof MdReferenceLink) return MdBundle.message("findusages.reference.link");
        if (element instanceof MdReferenceLinkReferenceImpl)
            return MdBundle.message("findusages.reference.link.reference.impl");
        if (element instanceof MdReferencingElementText)
            return MdBundle.message("findusages.referencing.element.text");
        if (element instanceof MdReferenceImage) return MdBundle.message("findusages.reference.image");
        if (element instanceof MdReferenceImageReferenceImpl)
            return MdBundle.message("findusages.reference.image.reference.impl");

        if (element instanceof MdHeaderText) return MdBundle.message("findusages.header.text");

        if (element instanceof MdFootnoteId) return MdBundle.message("findusages.footnote.id");
        if (element instanceof MdFootnoteRefIdImpl) return MdBundle.message("findusages.footnote.refid.impl");

        if (element instanceof MdLinkRef) return MdBundle.message("findusages.explicit.link-ref");
        if (element instanceof MdLinkText) return MdBundle.message("findusages.explicit.link-ref-text");
        if (element instanceof MdLinkAnchor) return MdBundle.message("findusages.explicit.link-ref-anchor");
        if (element instanceof MdLinkTitle) return MdBundle.message("findusages.explicit.link-ref-title");

        // RELEASE: add api and move to flexmark plugin
        if (element instanceof FlexmarkExampleOption) return PluginBundle.message("findusages.flexmark.example-option");
        if (element instanceof FakePsiLiteralExpression) return PluginBundle.message("findusages.flexmark.example-option");
        return element.getClass().getName();
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof MdNamedElement) {
            return ((MdNamedElement) element).getDisplayName();
        } else {
            return element.getClass().getSimpleName();
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof MdNamedElement) {
            return ((MdNamedElement) element).getDisplayName();
        } else {
            return element.getText();
        }
    }
}
