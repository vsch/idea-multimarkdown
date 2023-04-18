// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.spellchecking;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.spellchecker.quickfixes.SpellCheckerQuickFix;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import com.vladsch.md.nav.parser.LexParserState;
import com.vladsch.md.nav.parser.MdParserDefinition;
import com.vladsch.md.nav.psi.element.MdElementType;
import com.vladsch.md.nav.psi.element.MdNamedElement;
import com.vladsch.md.nav.psi.util.MdTokenSets;
import com.vladsch.md.nav.psi.util.MdTokenType;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.spellchecking.api.MdSpellcheckingIdentifierTokenizer;
import com.vladsch.md.nav.util.MiscUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

import static com.vladsch.md.nav.psi.util.MdTypes.*;
import static com.vladsch.plugin.util.psi.PsiUtils.isTypeOf;

public class MdSpellcheckingStrategy extends SpellcheckingStrategy {
    protected static final MdSpellcheckingIdentifierTokenizer[] EXTENSIONS = MdSpellcheckingIdentifierTokenizer.EXTENSIONS.getValue();
    private static Boolean useOldApi = null;

    final public static TokenSet NO_SPELL_CHECK_SET = TokenSet.orSet(MdTokenSets.WHITESPACE_SET, TokenSet.create(
            MdParserDefinition.MARKDOWN_FILE,
            //ABBREVIATED_TEXT,
            //ABBREVIATION,
            ANCHOR,
            ANCHOR_ID,
            //ANCHOR_LINK,
            ANCHOR_MARK,
            ASIDE_BLOCK,
            ATTRIBUTE,
            ATTRIBUTE_ID_VALUE,
            ATTRIBUTE_NAME,
            ATTRIBUTE_NAME_CLASS,
            ATTRIBUTE_NAME_ID,
            ATTRIBUTE_VALUE,
            ATTRIBUTE_VALUE_CLOSE,
            ATTRIBUTE_VALUE_OPEN,
            ATTRIBUTE_VALUE_SEP,
            ATTRIBUTES,
            ATTRIBUTES_CLOSE,
            ATTRIBUTES_OPEN,
            //ATX_HEADER,
            ATX_HEADER,
            //AUTO_LINK,
            AUTO_LINK,
            AUTO_LINK_ANCHOR_MARKER,
            AUTO_LINK_CLOSE,
            AUTO_LINK_ELEM,
            AUTO_LINK_OPEN,
            ADMONITION_INFO,
            BLANK_LINE,
            BLOCK_QUOTE,
            //BOLD,
            //BOLD_ITALIC,
            BOLD_MARKER,
            BULLET_LIST,
            BULLET_LIST_ITEM,
            BULLET_LIST_ITEM_MARKER,
            //CODE,
            //COMMENT,
            COMMENT_CLOSE,
            COMMENT_OPEN,
            //DEFINITION,
            DEFINITION_MARKER,
            //DEFINITION_TERM,
            EMOJI,
            EMOJI_ID,
            EMOJI_ID,
            EMOJI_MARKER,
            EMOJI_MARKER,
            ENUM_REF_FORMAT,
            ENUM_REF_FORMAT_CLOSE,
            ENUM_REF_FORMAT_OPEN,
            ENUM_REF_FORMAT_TYPE,
            ENUM_REF_ID,
            ENUM_REF_LINK,
            ENUM_REF_LINK_CLOSE,
            ENUM_REF_LINK_OPEN,
            ENUM_REF_TEXT,
            ENUM_REF_TEXT_CLOSE,
            ENUM_REF_TEXT_OPEN,
            EOL,
            //EXPLICIT_LINK,
            GITLAB_BLOCK_QUOTE,
            GITLAB_MATH,
            GITLAB_MATH_MARKER,
            GITLAB_MATH_TEXT,
            HEADER_ATX_MARKER,
            //HEADER_LEVEL_2,
            //HEADER_LEVEL_3,
            //HEADER_LEVEL_4,
            //HEADER_LEVEL_5,
            //HEADER_LEVEL_6,
            HEADER_SETEXT_MARKER,
            HRULE_TEXT,
            //HTML_BLOCK,
            HTML_ENTITY,
            //IMAGE,
            IMAGE_LINK_REF,
            IMAGE_LINK_REF_CLOSE,
            IMAGE_LINK_REF_OPEN,
            IMAGE_LINK_REF_TEXT_CLOSE,
            IMAGE_LINK_REF_TEXT_OPEN,
            IMAGE_LINK_REF_TITLE_MARKER,
            IMAGE_LINK_REF_TITLE_MARKER,
            //INLINE_HTML,
            //ITALIC,
            ITALIC_MARKER,
            LINK_REF,
            LINK_REF_ANCHOR,
            LINK_REF_ANCHOR_MARKER,
            LINK_REF_CLOSE,
            LINK_REF_OPEN,
            LINK_REF_TEXT_CLOSE,
            LINK_REF_TEXT_OPEN,
            LINK_REF_TITLE_MARKER,
            MAIL_LINK,
            ORDERED_LIST,
            ORDERED_LIST_ITEM,
            ORDERED_LIST_ITEM_MARKER,
            PARAGRAPH_BLOCK,
            //QUOTE,
            //REFERENCE,
            REFERENCE,
            REFERENCE_ANCHOR,
            REFERENCE_ANCHOR_MARKER,
            REFERENCE_END,
            //REFERENCE_IMAGE,
            REFERENCE_IMAGE,
            REFERENCE_IMAGE_REFERENCE_CLOSE,
            REFERENCE_IMAGE_REFERENCE_CLOSE2,
            REFERENCE_IMAGE_REFERENCE_OPEN,
            REFERENCE_IMAGE_REFERENCE_OPEN2,
            REFERENCE_IMAGE_TEXT_CLOSE,
            REFERENCE_IMAGE_TEXT_OPEN,
            //REFERENCE_LINK,
            REFERENCE_LINK,
            REFERENCE_LINK_REF,
            REFERENCE_LINK_REFERENCE_OPEN,
            REFERENCE_LINK_REFERENCE_OPEN2,
            REFERENCE_LINK_TEXT_CLOSE,
            REFERENCE_LINK_TEXT_OPEN,
            REFERENCE_TEXT_CLOSE,
            REFERENCE_TEXT_OPEN,
            REFERENCE_TITLE_MARKER,
            SETEXT_HEADER,
            //SETEXT_HEADER_LEVEL_1,
            //SETEXT_HEADER_LEVEL_2,
            SIM_TOC,
            SIM_TOC_CLOSE,
            SIM_TOC_CONTENT,
            SIM_TOC_HEADER_MARKERS,
            SIM_TOC_KEYWORD,
            SIM_TOC_OPEN,
            SIM_TOC_OPTION,
            //SIM_TOC_TITLE,
            SIM_TOC_TITLE_MARKER,
            //SMARTS,
            SPECIAL_TEXT,
            //STRIKETHROUGH,
            //STRIKETHROUGH_BOLD,
            //STRIKETHROUGH_BOLD_ITALIC,
            //STRIKETHROUGH_ITALIC,
            STRIKETHROUGH_MARKER,
            SUBSCRIPT_MARKER,
            SUPERSCRIPT_MARKER,
            TABLE,
            TABLE_BODY,
            //TABLE_CAPTION,
            //TABLE_CELL_REVEN_CEVEN,
            //TABLE_CELL_REVEN_CODD,
            //TABLE_CELL_RODD_CEVEN,
            //TABLE_CELL_RODD_CODD,
            //TABLE_HDR_CELL_REVEN_CEVEN,
            //TABLE_HDR_CELL_REVEN_CODD,
            //TABLE_HDR_CELL_RODD_CEVEN,
            //TABLE_HDR_CELL_RODD_CODD,
            TABLE_HDR_ROW_EVEN,
            TABLE_HDR_ROW_ODD,
            TABLE_ROW_EVEN,
            TABLE_ROW_ODD,
            TABLE_SEP_COLUMN_EVEN,
            TABLE_SEP_COLUMN_ODD,
            TABLE_SEP_ROW_ODD,
            TABLE_SEPARATOR,
            TASK_DONE_ITEM,
            TASK_DONE_ITEM_MARKER,
            TASK_ITEM,
            TASK_ITEM_MARKER,
            //TEXT,
            UNDERLINE_MARKER,
            //VERBATIM,
            VERBATIM,
            VERBATIM_LANG,
            WIKI_LINK,
            WIKI_LINK_CLOSE,
            WIKI_LINK_OPEN,
            //WIKI_LINK_REF,
            WIKI_LINK_REF_ANCHOR,
            WIKI_LINK_SEPARATOR,
            //WIKI_LINK_TITLE,

            // tail end place holder
            TokenType.WHITE_SPACE
    ));

    final public static TokenSet PARENT_SPELL_CHECK_SET = TokenSet.create(
            BOLD,
            CODE,
            ITALIC,
            QUOTE,
            SMARTS,
            STRIKETHROUGH,
            UNDERLINE,
            SUPERSCRIPT,
            SUBSCRIPT,
            GITLAB_DEL,
            GITLAB_INS
    );

    protected static Tokenizer<?> TEXT_BLOCK_TOKENIZER = new MdTextTokenizer();
    private final List<IElementType> INLINE_NON_PLAIN_TEXT;
    private final TokenSet TYPOGRAPHIC_MARKER_SET;
    private final TokenSet TEXT_SET;
    private final List<IElementType> INLINE_PLAIN_TEXT;
    private final MdApplicationSettings myApplicationSettings;

    public MdSpellcheckingStrategy() {
        LexParserState.State lexParserState = LexParserState.getInstance().getState();

        INLINE_NON_PLAIN_TEXT = lexParserState.INLINE_NON_PLAIN_TEXT;
        TYPOGRAPHIC_MARKER_SET = lexParserState.TYPOGRAPHIC_MARKER_SET;
        TEXT_SET = lexParserState.TEXT_SET;
        INLINE_PLAIN_TEXT = lexParserState.INLINE_PLAIN_TEXT;
        myApplicationSettings = MdApplicationSettings.getInstance();
    }

    @NotNull
    @Override
    public Tokenizer<?> getTokenizer(PsiElement element) {
        IElementType elementType = element.getNode().getElementType();

        if (elementType instanceof MdTokenType || elementType instanceof MdElementType) {
            // don't spell check individual TEXT elements or the inline emphasis ones, let the parent element be spell checked as a whole so the words are not broken
            if (elementType != COMMENT_TEXT
                    && elementType != BLOCK_COMMENT_TEXT
                    && elementType != CODE
                    && !(isTypeOf(elementType, TEXT_SET) || INLINE_PLAIN_TEXT.contains(elementType))
                    &&
                    (INLINE_NON_PLAIN_TEXT.contains(elementType)
                            || isTypeOf(elementType, TYPOGRAPHIC_MARKER_SET) // these are markers
                            || isTypeOf(elementType, NO_SPELL_CHECK_SET)
                            || isTypeOf(elementType, PARENT_SPELL_CHECK_SET))) {
                return EMPTY_TOKENIZER;
            }

            // here we can return custom tokenizer if needed
            PsiElement namedAncestor = getNamedAncestor(element);

            if (namedAncestor != null) {
                if (namedAncestor == element) {
                    Tokenizer<?>[] tokenizerArr = { TEXT_BLOCK_TOKENIZER };
                    MiscUtils.firstNonNullResult(EXTENSIONS,
                            (Function<MdSpellcheckingIdentifierTokenizer, Tokenizer<?>>) tokenizer -> tokenizer.getIdentifierTokenizer(element, tokenizerArr[0]),
                            tokenizer -> tokenizerArr[0] = tokenizer
                    );
                    return tokenizerArr[0];
                } else {
                    return EMPTY_TOKENIZER;
                }
            }

            if (elementType == MdTypes.HTML_BLOCK) {
                if (myApplicationSettings.getDocumentSettings().getHtmlLangInjections()) {
                    return EMPTY_TOKENIZER;
                }
            }

            if (elementType == MdTypes.HTML_BLOCK_TEXT) {
                return EMPTY_TOKENIZER;
            }

            // These do not work!!! Need to disable inner comments for HTML blocks when injecting HTML language
            // if have language injections for HTML blocks then HTML language will process the comment
            //if (elementType == COMMENT && element.getParent().getNode().getElementType() == HTML_BLOCK) {
            //    if (MarkdownApplicationSettings.getInstance().getDocumentSettings().getHtmlLangInjections()) {
            //        return EMPTY_TOKENIZER;
            //    }
            //}

            return TEXT_BLOCK_TOKENIZER;
        }
        return super.getTokenizer(element);
    }

    PsiElement getNamedAncestor(PsiElement element) {
        while (element != null) {
            if (element instanceof MdNamedElement) {
                return element;
            }

            if (element instanceof PsiFile) {
                return null;
            }

            element = element.getParent();
        }
        return null;
    }

    @Override
    // FIX: remove old api references, now they are all the same for a given version
    public LocalQuickFix[] getRegularFixes(PsiElement element, @NotNull TextRange textRange, boolean useRename, String wordWithTypo) {
        LocalQuickFix[] fixes = new SpellCheckerQuickFix[0];
        if (useOldApi == null) {
            try {
                //fixes = getDefaultRegularFixes(useRename, wordWithTypo, element);
                Class<?> superClass = Class.forName("com.intellij.spellchecker.tokenizer.SpellcheckingStrategy");
                Method getDefaultRegularFixesMethod = superClass.getMethod("getDefaultRegularFixes", Boolean.TYPE, String.class, PsiElement.class);
                fixes = (LocalQuickFix[]) getDefaultRegularFixesMethod.invoke(null, useRename, wordWithTypo, element);
                useOldApi = false;
            } catch (Exception ignored) {
                try {
                    fixes = getDefaultRegularFixes(useRename, wordWithTypo, element, textRange);
                    useRename = true;
                    useOldApi = true;
                } catch (NoSuchMethodError e) {
                    useRename = false;
                    useOldApi = false;
                }
            }
        }

        if (useOldApi) {
            fixes = getDefaultRegularFixes(useRename, wordWithTypo, element, textRange);
        } else {
            try {
                Class<?> superClass = Class.forName("com.intellij.spellchecker.tokenizer.SpellcheckingStrategy");
                Method getDefaultRegularFixesMethod = superClass.getMethod("getDefaultRegularFixes", Boolean.TYPE, String.class, PsiElement.class);
                fixes = (LocalQuickFix[]) getDefaultRegularFixesMethod.invoke(null, useRename, wordWithTypo, element);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                useRename = false;
                useOldApi = false;
            }
        }

        LocalQuickFix[] finalFixes = fixes;
        if (useRename && finalFixes.length > 0) {
            MiscUtils.firstNonNullResult(EXTENSIONS,
                    (Function<MdSpellcheckingIdentifierTokenizer, SpellCheckerQuickFix>) tokenizer -> tokenizer.getRenameQuickFix(wordWithTypo),
                    quickFix -> finalFixes[0] = quickFix
            );
            return finalFixes;
        } else {
            return finalFixes;
        }
    }
}
