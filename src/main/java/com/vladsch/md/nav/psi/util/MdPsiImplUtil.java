// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.util;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.LineAppendable;
import com.vladsch.flexmark.util.sequence.LineInfo;
import com.vladsch.flexmark.util.sequence.RepeatedSequence;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import com.vladsch.md.nav.actions.handlers.util.ParagraphContext;
import com.vladsch.md.nav.actions.handlers.util.PostEditAdjust;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleAst;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleHtml;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleSource;
import com.vladsch.md.nav.flex.psi.FlexmarkFrontMatterBlock;
import com.vladsch.md.nav.parser.Extensions;
import com.vladsch.md.nav.parser.LexParserState;
import com.vladsch.md.nav.parser.MdFactoryContext;
import com.vladsch.md.nav.parser.cache.MdCachedFileElements;
import com.vladsch.md.nav.parser.cache.SourcedElementConsumer;
import com.vladsch.md.nav.psi.element.*;
import com.vladsch.md.nav.settings.ListIndentationType;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.Result;
import com.vladsch.md.nav.util.WikiLinkRef;
import com.vladsch.plugin.util.image.ImageUtils;
import com.vladsch.plugin.util.psi.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.vladsch.md.nav.psi.element.MdNamedElement.RENAME_ELEMENT_HANDLES_EXT;
import static com.vladsch.md.nav.psi.element.MdNamedElement.RENAME_KEEP_ANCHOR;
import static com.vladsch.md.nav.psi.element.MdNamedElement.RENAME_KEEP_NAME;
import static com.vladsch.md.nav.psi.element.MdNamedElement.RENAME_KEEP_PATH;
import static com.vladsch.md.nav.psi.element.MdNamedElement.RENAME_KEEP_TEXT;
import static com.vladsch.md.nav.psi.element.MdNamedElement.RENAME_KEEP_TITLE;
import static com.vladsch.md.nav.psi.util.MdTokenSets.BLOCK_ELEMENT_SET;
import static com.vladsch.md.nav.psi.util.MdTokenSets.BLOCK_QUOTE_LIKE_MARKER_SET;
import static com.vladsch.md.nav.psi.util.MdTokenSets.NON_TEXT_INLINE_ELEMENTS;
import static com.vladsch.md.nav.psi.util.MdTokenSets.WHITESPACE_BLANK_LINE_SET;
import static com.vladsch.md.nav.psi.util.MdTokenSets.WHITESPACE_EOL_BLANK_LINE_SET;
import static com.vladsch.md.nav.psi.util.MdTokenSets.WHITESPACE_EOL_SET;
import static com.vladsch.md.nav.psi.util.MdTokenSets.WHITESPACE_SET;
import static com.vladsch.md.nav.psi.util.MdTypes.*;
import static com.vladsch.plugin.util.psi.PsiUtils.isTypeOf;

public class MdPsiImplUtil {
    private static final Logger LOG = Logger.getInstance(MdPsiImplUtil.class);
    final protected static int EXTENSION_STRIP = 1;
    final protected static int EXTENSION_KEEP_OLD = 2;
    final protected static int EXTENSION_USE_NEW_IF_OLD_HAS = 3;
    final protected static int EXTENSION_USE_NEW = 4;

    final public static LinkRefElementTypes WIKI_LINK_ELEMENT = new LinkRefElementTypes(WIKI_LINK, WIKI_LINK_REF, WIKI_LINK_TEXT, WIKI_LINK_REF_ANCHOR, EXTENSION_STRIP);
    final public static LinkRefElementTypes EXPLICIT_LINK_ELEMENT = new LinkRefElementTypes(
            EXPLICIT_LINK,
            LINK_REF,
            LINK_REF_TEXT,
            LINK_REF_ANCHOR,
            LINK_REF_TITLE,
            EXTENSION_USE_NEW_IF_OLD_HAS
    );

    final public static LinkRefElementTypes AUTO_LINK_ELEMENT = new LinkRefElementTypes(
            AUTO_LINK,
            AUTO_LINK_REF,
            null,
            AUTO_LINK_ANCHOR,
            null,
            EXTENSION_USE_NEW
    );

    final public static LinkRefElementTypes REFERENCE_ELEMENT = new LinkRefElementTypes(
            REFERENCE,
            REFERENCE_LINK_REF,
            REFERENCE_TEXT,
            REFERENCE_ANCHOR,
            REFERENCE_TITLE,
            EXTENSION_USE_NEW_IF_OLD_HAS
    );
    final public static LinkRefElementTypes IMAGE_LINK_ELEMENT = new LinkRefElementTypes(IMAGE, IMAGE_LINK_REF, IMAGE_LINK_REF_TEXT, null, IMAGE_LINK_REF_TITLE, EXTENSION_USE_NEW);
    final public static LinkRefElementTypes JEKYLL_INCLUDE_ELEMENT = new LinkRefElementTypes(JEKYLL_INCLUDE_TAG_ELEM, JEKYLL_INCLUDE_TAG_LINK_REF, null, null, EXTENSION_USE_NEW);

    final public static ItemPresentation NULL_PRESENTATION = getPresentation(null, null, null);
    public static final String SPACE_HASH = "# ";
    public static final CharPredicate SPACE_HASH_SET = CharPredicate.anyOf(SPACE_HASH);
    public static final String DECIMAL_DIGITS = "0123456789";
    public static final CharPredicate DECIMAL_DIGITS_SET = CharPredicate.anyOf(DECIMAL_DIGITS);

    @NotNull
    public static List<MdReferenceElement> getReferenceElements(
            @NotNull MdFile file,
            @NotNull IElementType elementType,
            @Nullable String elementName,
            boolean wantOuterFile
    ) {
        Class<? extends MdReferenceElement> elementClass;

        if (elementType == REFERENCE) elementClass = MdReference.class;
        else if (elementType == FOOTNOTE) elementClass = MdFootnote.class;
        else if (elementType == MACRO) elementClass = MdMacro.class;
        else if (elementType == ABBREVIATION) elementClass = MdAbbreviation.class;
        else if (elementType == ATTRIBUTE_ID_VALUE) elementClass = MdAttributeIdValue.class;
        else if (elementType == ENUM_REF_ID || elementType == ENUM_REF_FORMAT) elementClass = MdEnumeratedReferenceFormat.class;
        else {
            throw new IllegalArgumentException("Cannot convert IElementType " + elementType + " to PsiElement class");
        }

        String normalizeReferenceId;

        if (elementClass == MdReference.class) normalizeReferenceId = MdReferenceImpl.normalizeReferenceText(elementName);
        else if (elementClass == MdFootnote.class) normalizeReferenceId = MdFootnoteImpl.normalizeReferenceText(elementName);
        else if (elementClass == MdMacro.class) normalizeReferenceId = MdMacroImpl.normalizeReferenceText(elementName);
        else if (elementClass == MdAbbreviation.class) normalizeReferenceId = MdAbbreviationImpl.normalizeReferenceText(elementName);
        else if (elementClass == MdAttributeIdValue.class) normalizeReferenceId = MdAttributeIdValueImpl.normalizeReferenceText(elementName);
        else //noinspection ConstantConditions
            if (elementClass == MdEnumeratedReferenceFormat.class) normalizeReferenceId = MdEnumeratedReferenceFormatImpl.normalizeReferenceText(elementName);
            else {
                throw new IllegalArgumentException("Cannot normalize reference id for " + elementClass);
            }

        ArrayList<MdReferenceElement> referenceList = new ArrayList<>();

        // FIX: need to resolve markdown rules first vs last duplicate reference
        String finalNormalizeReferenceId = normalizeReferenceId;
        findChildrenOfAnyType(file, wantOuterFile, true, false, (referenceElement) -> {
            if (elementName == null || (referenceElement.normalizeReferenceId(referenceElement.getReferenceId()).equals(finalNormalizeReferenceId))) {
                // NOTE: now the default is KeepType.FIRST
                referenceList.add(referenceElement);
            }
        }, elementClass);

        return referenceList;
    }

    @NotNull
    public static List<MdHeaderElement> getHeaderElements(
            @NotNull MdFile file,
            @Nullable String anchorReferenceId,
            boolean wantOuterFile
    ) {
        ArrayList<MdHeaderElement> validatedElements = new ArrayList<>();
        findChildrenOfAnyType(file, wantOuterFile, true, false, element -> {
            if ((anchorReferenceId == null || element.isReferenceFor(anchorReferenceId))) {
                validatedElements.add(element);
            }
        }, MdHeaderElement.class);

        return validatedElements;
    }

    @NotNull
    public static MdHeaderElement setHeaderLevel(
            MdHeaderElementImpl element,
            int level,
            final int trailingAttributes,
            @NotNull PsiEditContext editContext
    ) {
        if (!element.isValid()) return element;
        String text = element.getHeaderText();

        MdHeaderElement newElement = null;
        BlockPrefixes prefixes = getBlockPrefixes(element, null, editContext);

        if (element instanceof MdAtxHeaderImpl) {
            MdHeaderText headerTextElement = element.getHeaderTextElement();
            if (headerTextElement != null) {
                ASTNode trailText = headerTextElement.getNode().getTreeNext();
                boolean hasTrailMarker = false;
                while (trailText != null) {
                    if (trailText.textContains('#')) {
                        hasTrailMarker = true;
                        break;
                    }
                    trailText = trailText.getTreeNext();
                }

                newElement = MdElementFactory.INSTANCE.createAtxHeader(new MdFactoryContext(element), text, level, hasTrailMarker, prefixes.getChildPrefix());
            }
        } else {
            newElement = MdElementFactory.INSTANCE.createSetextHeader(new MdFactoryContext(element), text, level, prefixes.getChildPrefix(), trailingAttributes);
        }

        if (newElement != null) {
            return (MdHeaderElement) element.replace(newElement);
        }
        return element;
    }

    @NotNull
    public static MdHeaderElement setHeaderText(
            MdHeaderElementImpl element,
            @NotNull String text,
            int trailingAttributes,
            @NotNull PsiEditContext editContext
    ) {
        if (!element.isValid()) return element;

        MdHeaderText textElement = element.getHeaderTextElement();
        if (textElement != null) {
            int level = element.getHeaderLevel();
            BlockPrefixes prefixes = getBlockPrefixes(element, null, editContext);

            MdHeaderElement newElement;
            if (element instanceof MdAtxHeaderImpl) {
                ASTNode trailText = textElement.getNode().getTreeNext();
                boolean hasMarker = trailText != null && trailText.textContains('#');
                newElement = MdElementFactory.INSTANCE.createAtxHeader(new MdFactoryContext(element), text, level, hasMarker, prefixes.getChildPrefix());
            } else {
                newElement = MdElementFactory.INSTANCE.createSetextHeader(new MdFactoryContext(element), text, level, prefixes.getChildPrefix(), trailingAttributes);
            }

            if (newElement != null) {
                PsiElement newTextElement = newElement.getHeaderTextElement();
                if (newTextElement != null) {
                    textElement.replace(newTextElement);

                    if (element instanceof MdSetextHeaderImpl && element.getHeaderMarkerNode() != null && newElement.getHeaderMarkerNode() != null) {
                        // grab the new marker too
                        element.getNode().replaceChild(element.getHeaderMarkerNode(), newElement.getHeaderMarkerNode());
                    }
                }
            }
        }
        return element;
    }

    public static int changeHeaderType(
            MdHeaderElement element,
            @Nullable Integer newLevel,
            @NotNull Document document,
            final int caretOffsetInHeadingText,
            @NotNull PsiEditContext editContext
    ) {
        if (!element.isValid()) return caretOffsetInHeadingText;

        if (newLevel == null) newLevel = element.getHeaderLevel();
        BlockPrefixes prefixes = getBlockPrefixes(element, null, editContext);

        CharSequence prefix = prefixes.getChildPrefix();

        if (element instanceof MdAtxHeaderImpl) {
            if (newLevel < 1 || newLevel > 2) return caretOffsetInHeadingText;

            LineAppendable sb = editContext.getLineAppendable();
            PsiElement prevSibling = element.getPrevSibling();
            boolean addBlankLine = !isFirstChildElement(element) && !isPrecededByBlankLine(element) && prevSibling != null && prevSibling.getNode().getElementType() != ORDERED_LIST_ITEM_MARKER && prevSibling.getNode().getElementType() != BULLET_LIST_ITEM_MARKER;
            int textStart = 0;

            if (addBlankLine) {
                sb.append("\n");
            }

            // need to escape leading element openers like bullet list, numbered list, block quote, definition, etc.
            CharSequence headerText = escapeCommonLeadingMarkers(element.getHeaderText(), editContext);
            CharSequence elementText = MdSetextHeaderImpl.Companion.getElementText(new MdFactoryContext(element), headerText, newLevel, element.getTrailingAttributesLength());
            LineAppendable lines = editContext.getLineAppendable().append(elementText).line();

            for (LineInfo info : lines) {
                CharSequence line = info.getLine();
                if (info.index > 0 || addBlankLine) sb.append(prefix);
                if (info.index == 0) {
                    textStart = sb.offsetWithPending();
                }
                sb.append(line).line();
            }

            String newElementText = sb.toString();
            document.replaceString(element.getNode().getStartOffset(), element.getNode().getStartOffset() + element.getNode().getTextLength(), newElementText);
            return element.getNode().getStartOffset() + textStart + Utils.rangeLimit(caretOffsetInHeadingText, 0, newElementText.length());
        } else {
            String headerText = element.getHeaderText();
            headerText = unEscapeCommonLeadingMarkers(headerText.trim().replaceAll("\\s*\n\\s*", " "), editContext).toString();
            CharSequence elementText = MdAtxHeaderImpl.Companion.getElementText(new MdFactoryContext(element), headerText, newLevel, false);

            document.replaceString(element.getNode().getStartOffset(), element.getNode().getStartOffset() + element.getNode().getTextLength(), elementText);
            return element.getNode().getStartOffset() + Utils.rangeLimit(caretOffsetInHeadingText + BasedSequence.of(elementText).countLeading(SPACE_HASH_SET), 0, elementText.length());
        }
    }

    @NotNull
    public static CharSequence unEscapeCommonLeadingMarkers(@NotNull CharSequence line, PsiEditContext editContext) {
//        boolean isCommonMark = parserSettings.getParserListIndentationType() == ListIndentationType.COMMONMARK;
        BasedSequence baseSeq = BasedSequence.of(line);
        int escaped = baseSeq.indexOf('\\');
        if (escaped != -1) {
            BasedSequence unescaped = baseSeq.delete(escaped, escaped + 1);
            if (baseSeq.equals(escapeCommonLeadingMarkers(unescaped, editContext))) {
                // yes, we should un-escape
                return unescaped;
            }
        }
        return line;
    }

    @NotNull
    public static CharSequence escapeCommonLeadingMarkers(@NotNull CharSequence line, PsiEditContext editContext) {
        MdParserSettings parserSettings = editContext.getRenderingProfile().getParserSettings();

        boolean isCommonMark = parserSettings.getParserListIndentationType() == ListIndentationType.COMMONMARK;
        BasedSequence lineSeq = BasedSequence.of(line);
        BasedSequence trimmed = lineSeq.trimmedStart(CharPredicate.SPACE_TAB);

        if (trimmed.length() < 4 && trimmed.indexOf('\t') == -1) {
            // not over indented, can have prefix
            BasedSequence trimmedLine = lineSeq.subSequence(trimmed.length());
            if (!trimmedLine.isEmpty()) {
                switch (trimmedLine.charAt(0)) {
                    case '*':
                    case '-':
                    case '+':
                    case ':':
                        // need at least one space after them, if users add space they will get strange results
                        if (trimmedLine.length() < 2 || trimmedLine.charAt(1) != ' ' && trimmedLine.charAt(1) != '\t') {
                            break;
                        }

                    case '|':
                        if ((parserSettings.getPegdownFlags() & Extensions.ASIDE) == 0) break;
                    case '>':
                    case '#':
                        line = trimmed.toString() + "\\" + trimmedLine.toString();
                        break;

                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        // need to see if ordered list
                        int digits = trimmedLine.countLeading(DECIMAL_DIGITS_SET);
                        if (digits < trimmedLine.length()) {
                            char after = trimmedLine.charAt(digits);
                            if (after == '.' || (after == ')' && isCommonMark)) {
                                // need at least one space after them but we will ignore this to prevent users from adding a space after conversion and
                                // getting strange results
                                if (digits + 1 < trimmedLine.length() && trimmedLine.charAt(digits + 1) != ' ' && trimmedLine.charAt(digits + 1) != '\t') {
                                    break;
                                }
                                line = trimmed.toString() + trimmedLine.subSequence(0, digits).toString() + "\\" + trimmedLine.subSequence(digits).toString();
                            }
                        }
                        break;
                }
            }
        }

        return line;
    }

    public static boolean isElementReferenced(@NotNull MdFile file, @NotNull MdReferenceElement element) {
        String referenceId = element.getReferenceId();
        if (!referenceId.isEmpty()) {
            Set<MdReferenceElement> referenceElements = file.getReferencedElementMap().get(referenceId);
            return referenceElements != null && referenceElements.contains(element);
        }
        return false;
    }

    @NotNull
    public static List<MdRefAnchor> getRefAnchorElements(
            @NotNull MdFile file,
            @Nullable String anchorReferenceId,
            boolean addOuterFile
    ) {
        Collection<MdRefAnchor> elements = listChildrenOfAnyType(file, addOuterFile, true, false, MdRefAnchor.class);
        ArrayList<MdRefAnchor> validatedElements = new ArrayList<>();

        if (!elements.isEmpty()) {
            for (MdRefAnchor headerElement : elements) {
                if ((anchorReferenceId == null || headerElement.isReferenceFor(anchorReferenceId))) {
                    validatedElements.add(headerElement);
                }
            }
        }
        return validatedElements;
    }

    @NotNull
    public static List<MdAttributeIdValue> getIdAttributeValueElements(
            @NotNull MdFile file,
            @Nullable String anchorReferenceId
    ) {
        Collection<MdAttributeIdValue> elements = listChildrenOfAnyType(file, false, true, false, MdAttributeIdValue.class);
        ArrayList<MdAttributeIdValue> validatedElements = new ArrayList<>();

        if (!elements.isEmpty()) {
            for (MdAttributeIdValue headerElement : elements) {
                if ((anchorReferenceId == null || headerElement.isReferenceFor(anchorReferenceId))) {
                    validatedElements.add(headerElement);
                }
            }
        }
        return validatedElements;
    }

    public static boolean isWhitespace(@NotNull IElementType elementType) {
        return PsiUtils.isTypeOf(elementType, WHITESPACE_SET);
    }

    public static boolean isWhitespaceOrEol(@NotNull IElementType elementType) {
        return PsiUtils.isTypeOf(elementType, WHITESPACE_EOL_SET);
    }

    public static boolean isWhitespaceOrEolOrBlankLine(@NotNull IElementType elementType) {
        return PsiUtils.isTypeOf(elementType, WHITESPACE_EOL_BLANK_LINE_SET);
    }

    public static boolean isWhitespaceOrBlankLine(@NotNull IElementType elementType) {
        return PsiUtils.isTypeOf(elementType, WHITESPACE_BLANK_LINE_SET);
    }

    public static boolean isWhitespace(@NotNull ASTNode node) {
        return isWhitespace(node.getElementType());
    }

    public static boolean isWhitespaceOrEol(@NotNull ASTNode node) {
        return isWhitespaceOrEol(node.getElementType());
    }

    public static boolean isWhitespaceOrEolOrBlankLine(@NotNull ASTNode node) {
        return isWhitespaceOrEolOrBlankLine(node.getElementType());
    }

    public static boolean isWhitespaceOrBlankLine(@NotNull ASTNode node) {
        return isWhitespaceOrBlankLine(node.getElementType());
    }

    public static boolean isWhitespace(@NotNull PsiElement element) {
        return isWhitespace(element.getNode());
    }

    public static boolean isWhitespaceOrEol(@NotNull PsiElement element) {
        return isWhitespaceOrEol(element.getNode());
    }

    public static boolean isWhitespaceOrEolOrBlankLine(@NotNull PsiElement element) {
        return isWhitespaceOrEolOrBlankLine(element.getNode());
    }

    public static boolean isWhitespaceOrBlankLine(@NotNull PsiElement element) {
        return isWhitespaceOrBlankLine(element.getNode());
    }

    @Nullable
    public static PsiElement getBlockElement(@Nullable PsiElement element) {
        if (element == null || !element.isValid()) return null;

        while (element != null && !(element instanceof PsiFile) && !isTypeOf(element, BLOCK_ELEMENT_SET)) {
            element = element.getParent();
        }
        return element instanceof PsiFile ? null : element;
    }

    @Nullable
    public static ASTNode nextNonWhiteSpaceSibling(@Nullable ASTNode node) {
        if (node == null) return null;

        do {
            node = node.getTreeNext();
        } while (node != null && isWhitespace(node.getElementType()));
        return node;
    }

    @Nullable
    public static ASTNode prevNonWhiteSpaceSibling(@Nullable ASTNode node) {
        if (node == null) return null;

        do {
            node = node.getTreePrev();
        }
        while (node != null && isWhitespace(node.getElementType()));
        return node;
    }

    @NotNull
    public static PsiElement parentSkipBlockQuote(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();

        while (parent instanceof MdListDelimiter) { parent = parent.getParent(); }
        return parent;
    }

    public static boolean isBlankLine(@Nullable ASTNode node) {
        if (node != null && node.getElementType() == BLANK_LINE) return true;
        if (node != null && node.getElementType() == EOL) {
            // see if the first line of a block quote with nothing but whitespace and EOL
            ASTNode prevNode = prevNonWhiteSpaceSibling(node);
            return PsiUtils.isTypeOf(prevNode, BLOCK_QUOTE_LIKE_MARKER_SET);
        }
        return false;
    }

    @Nullable
    public static PsiElement nextNonWhiteSpaceSibling(@Nullable PsiElement element) {
        if (element == null || !element.isValid()) return null;

        do {
            element = element.getNextSibling();
        }
        while (element != null && element.getNode() != null && isWhiteSpaceOrBlankLine(element));
        return element;
    }

    @Nullable
    public static PsiElement nextNonWhiteSpaceOnlySibling(@Nullable PsiElement element) {
        if (element == null || !element.isValid()) return null;

        do {
            element = element.getNextSibling();
        }
        while (element != null && element.getNode() != null && isWhitespace(element));
        return element;
    }

    public static boolean isWhiteSpaceOrBlankLine(@Nullable PsiElement element) {
        return element != null && isWhitespaceOrBlankLine(element.getNode().getElementType());
    }

    @Nullable
    public static PsiElement lastNonWhiteSpaceChild(@Nullable PsiElement element) {
        if (element == null || !element.isValid() || element instanceof MdBlankLine) return null;

        element = element.getLastChild();
        while (element != null && element.getNode() != null && isWhiteSpaceOrBlankLine(element)) {
            element = element.getPrevSibling();
        }
        return element;
    }

    @Nullable
    public static PsiElement prevNonWhiteSpaceSibling(@Nullable PsiElement element) {
        if (element == null || !element.isValid()) return null;

        do {
            element = element.getPrevSibling();
        }
        while (element != null && element.getNode() != null && isWhiteSpaceOrBlankLine(element));
        return element;
    }

    @Nullable
    public static PsiElement prevNonWhiteSpaceOnlySibling(@Nullable PsiElement element) {
        if (element == null || !element.isValid()) return null;

        do {
            element = element.getPrevSibling();
        }
        while (element != null && element.getNode() != null && isWhitespace(element));
        return element;
    }

    public static boolean isBlankLine(@Nullable PsiElement element) {
        return element != null && isBlankLine(element.getNode());
    }

    @Nullable
    public static PsiElement blankLineOrNull(@Nullable PsiElement element) {
        return (element == null || !isBlankLine(element.getNode())) ? null : element;
    }

    public static boolean isFollowedByBlankLine(@NotNull PsiElement element) {
        return followingBlankLine(element) != null;
    }

    @Nullable
    public static PsiElement tailChildBlankLine(@Nullable PsiElement element) {
        if (element == null || !element.isValid() || element instanceof MdBlankLine) return null;

        element = element.getLastChild();
        PsiElement lastBlankLine = null;
        while (element != null && element.getNode() != null && isWhitespaceOrBlankLine(element.getNode().getElementType())) {
            if (isBlankLine(element)) lastBlankLine = element;
            element = element.getPrevSibling();
        }
        return lastBlankLine;
    }

    @Nullable
    public static PsiElement followingBlankLine(@NotNull PsiElement element) {
        if (!element.isValid() || element.getNode() == null) return null;
        int startOffset = element.getNode().getStartOffset();

        PsiElement child = tailChildBlankLine(element);
        if (child != null) return child;

        PsiElement sibling = nextNonWhiteSpaceOnlySibling(element);
        if (isBlankLine(sibling)) return sibling;

        PsiElement parent = element.getParent();
        child = lastNonWhiteSpaceChild(parent);
        if (child == element) {
            PsiElement followingBlankLine = followingBlankLine(parent);
            if (followingBlankLine != null && startOffset < followingBlankLine.getNode().getStartOffset())
                return followingBlankLine;
        }
        return null;
    }

    public static boolean isPrecededByBlankLine(@NotNull PsiElement element) {
        ASTNode node = prevNonWhiteSpaceSibling(element.getNode());
        return PsiUtils.isTypeOf(node, BLOCK_QUOTE_LIKE_MARKER_SET) || precedingBlankLine(element) != null;
    }

    public static boolean endsInBlankLine(@NotNull PsiElement element) {
        return endingBlankLine(element) != null;
    }

    @Nullable
    public static PsiElement endingBlankLine(@Nullable PsiElement element) {
        if (element == null || !element.isValid()) return null;
        if (isBlankLine(element)) return element;

        PsiElement child = element;
        do {
            child = lastNonWhiteSpaceChild(child);
            if (isBlankLine(child)) return child;
        }
        while (child != null);
        return null;
    }

    @Nullable
    public static PsiElement precedingBlankLine(@NotNull PsiElement element) {
        if (!element.isValid() || element.getNode() == null) return null;
        int startOffset = element.getNode().getStartOffset();

        PsiElement sibling = prevNonWhiteSpaceOnlySibling(element);
        if (isBlankLine(sibling)) return sibling;

        if (element.getParent() != null && (element == element.getParent().getFirstChild() || sibling == null)) {
            PsiElement precedingBlankLine = precedingBlankLine(element.getParent());
            return precedingBlankLine == null || startOffset <= precedingBlankLine.getNode().getStartOffset() ? null : precedingBlankLine;
        } else {
            PsiElement blankLine = endingBlankLine(sibling);
            if (blankLine != null && startOffset > blankLine.getNode().getStartOffset()) return blankLine;
        }
        return null;
    }

    @Nullable
    public static PsiElement findChildTextBlock(@NotNull PsiElement parentElement) {
        if (!parentElement.isValid()) return null;

        if (parentElement.getNode().getElementType() == FOOTNOTE) {
            parentElement = findChildByType(parentElement, FOOTNOTE_TEXT);
            if (parentElement == null) return null;
        }

        PsiElement child = parentElement.getFirstChild();
        while (child != null && !(child instanceof MdComposite)) child = child.getNextSibling();

        if (child != null) {
            if (child.getNode().getElementType() == TEXT_BLOCK) {
                return child;
            }
            if (child.getNode().getElementType() == PARAGRAPH_BLOCK && child.getFirstChild().getNode().getElementType() == TEXT_BLOCK) {
                return child.getFirstChild();
            }
        }
        return null;
    }

    public static int childElementOrdinal(@NotNull PsiElement element) {
        if (!element.isValid()) return 0;

        PsiElement parent = element.getParent();
        int ordinal = 0;
        for (PsiElement child : parent.getChildren()) {
            if (child == element) return ordinal;
            if (child.getNode() != null && !isWhitespaceOrEolOrBlankLine(child.getNode().getElementType()))
                ordinal++;
        }
        return -1;
    }

    @Nullable
    public static ASTNode prevLeafElement(@Nullable ASTNode node) {
        if (node == null) return null;

        ASTNode prevLeaf = node.getTreePrev();
        if (prevLeaf == null) {
            prevLeaf = node.getTreeParent().getTreePrev();
        }
        ASTNode lastLeaf = prevLeaf;
        while (prevLeaf != null) {
            lastLeaf = prevLeaf;
            prevLeaf = prevLeaf.getLastChildNode();
        }
        return lastLeaf;
    }

    @Nullable
    public static ASTNode nextLeafElement(@Nullable ASTNode node) {
        if (node == null) return null;

        ASTNode prevLeaf = node.getTreeNext();
        if (prevLeaf == null) {
            prevLeaf = node.getTreeParent().getTreeNext();
        }
        ASTNode lastLeaf = prevLeaf;
        while (prevLeaf != null) {
            lastLeaf = prevLeaf;
            prevLeaf = prevLeaf.getFirstChildNode();
        }
        return lastLeaf;
    }

    @SafeVarargs
    @Nullable
    public static <T extends PsiElement> T findAncestorOfType(PsiElement element, Class<T>... classes) {
        PsiElement parent = element;
        do {
            parent = parent.getParent();
            if (parent == null) break;

            for (Class<T> ancestor : classes) {
                if (ancestor.isInstance(parent)) {
                    //noinspection unchecked
                    return (T) parent;
                }
            }
        }
        while (true);
        return null;
    }

    public static ArrayList<MdAnchorTarget> getAnchorTargets(
            MdFile containingFile,
            String referenceId,
            boolean wantOuterElements
    ) {

        List<MdRefAnchor> anchors = getRefAnchorElements(containingFile, referenceId, wantOuterElements);
        ArrayList<MdAnchorTarget> elements = new ArrayList<>(anchors);

        List<MdHeaderElement> headers = getHeaderElements(containingFile, null, wantOuterElements);
        List<MdAttributeIdValue> attributes = getIdAttributeValueElements(containingFile, null);
        for (MdAttributeIdValue attribute : attributes) {
            final PsiElement ancestor = findAncestorOfType(attribute, MdHeaderElement.class);
            if (ancestor != null) {
                // remove the header whose id was overridden
                headers.remove(ancestor);
            }

            if (referenceId == null || referenceId.equals(attribute.getText())) {
                elements.add(attribute);
            }
        }

        for (MdHeaderElement headerElement : headers) {
            if (referenceId == null || referenceId.equals(headerElement.getAnchorReferenceId())) {
                elements.add(headerElement);
            }
        }
        return elements;
    }

    @NotNull
    public static <P extends PsiElement> P originalPsi(@NotNull P psiElement) {
        if (psiElement instanceof PsiFile) {
            return psiElement;
        } else {
            //noinspection unchecked
            return (P) psiElement.getOriginalElement();
        }
    }

    @NotNull
    public static PsiElement changeToWikiLink(MdLinkElement<?> element, boolean keepExtension) {
        if (!element.isValid()) return element;

        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null) {
            String linkText = getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
            LinkRef sourceLinkRef = getLinkRef(elementTypes, element);

            if (sourceLinkRef != null) {
                // only include text if it does not equal the file name
                if (linkText.equals(LinkRef.urlDecode(sourceLinkRef.getFileName()))) linkText = null;

                LinkRef linkRef = WikiLinkRef.from(sourceLinkRef);

                if (linkRef != null) {

                    MdWikiLink otherLink = MdElementFactory.INSTANCE.createWikiLink(
                            new MdFactoryContext(element),
                            keepExtension ? linkRef.getFilePath() : linkRef.getFilePathNoExt(),
                            linkText,
                            linkRef.getAnchor()
                    );
                    if (otherLink != null) {
                        return element.replace(otherLink);
                    }
                }
            }
        }
        return element;
    }

    public static boolean isWikiLinkEquivalent(MdLinkElement<?> element) {
        if (!element.isValid()) return false;

        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null && elementTypes.parentType != REFERENCE) {
            String linkRefTitle = getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);
            if (linkRefTitle.isEmpty()) {
                LinkRef sourceLinkRef = getLinkRef(elementTypes, element);

                if (sourceLinkRef != null) {
                    LinkRef linkRef = WikiLinkRef.from(sourceLinkRef);

                    if (linkRef != null) {
                        return !(sourceLinkRef.pathContains("%23") && sourceLinkRef.getHasAnchor());
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    public static String getTextForChangeToExplicitLink(MdLinkElement<?> element, @Nullable String newLinkRef) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);

        if (elementTypes != null) {
            String linkText = getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
            LinkRef sourceLinkRef = element.getLinkRef();
            String linkRefTitle = getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);

            LinkRef linkRef = LinkRef.from(sourceLinkRef, null);
            if (newLinkRef != null) {
                linkRef = linkRef.replaceFilePath(newLinkRef, false, false);
            }

            if (linkText.isEmpty()) {
                linkText = sourceLinkRef.getFilePath();
            }

            return MdExplicitLinkImpl.Companion.getElementText(new MdFactoryContext(element), linkRef.getFilePath(), linkText, linkRef.getAnchorText(), linkRefTitle);
        }
        return "";
    }

    @NotNull
    public static PsiElement setContent(@NotNull MdInlineGitLabMath element, @NotNull String html) {
        MdInlineGitLabMath newElement = MdElementFactory.INSTANCE.createInlineGitLabMath(new MdFactoryContext(element), html);
        if (newElement != null) {
            element.getNode().replaceAllChildrenToChildrenOf(newElement.getNode());
        }
        return element;
    }

    @NotNull
    public static PsiElement changeToExplicitLink(MdLinkElement<?> element, @Nullable String newLinkRef) {
        if (!element.isValid()) return element;

        String elementText = getTextForChangeToExplicitLink(element, newLinkRef);

        if (!elementText.isEmpty()) {
            MdExplicitLink otherLink = (MdExplicitLink) MdElementFactory.INSTANCE.createElementFromText(
                    new MdFactoryContext(element),
                    elementText,
                    MdExplicitLink.class
            );
            if (otherLink != null) {
                return element.replace(otherLink);
            }
        }
        return element;
    }

    private static class TextNodeVisitor {
        public final StringBuilder charOut = new StringBuilder();
        private final LexParserState.State myLexParserState;
        public final boolean trimTrailingRefLinkText;
        public final boolean forHeadingIdText;

        public TextNodeVisitor(boolean trimTrailingRefLinkText, boolean forHeadingIdText) {
            myLexParserState = LexParserState.getInstance().getState();
            this.trimTrailingRefLinkText = trimTrailingRefLinkText;
            this.forHeadingIdText = forHeadingIdText;
        }

        private void addSpacesToNext(@NotNull ASTNode node) {
            if (forHeadingIdText && !trimTrailingRefLinkText) {
                // need to add trailing spaces to the text 
                int nodeEnd = node.getStartOffset() + node.getTextLength();
                ASTNode nextNode = node.getTreeNext();
                if (nextNode != null) {
                    if (nextNode.getText().startsWith(" ")) {
                        charOut.append(SequenceUtils.trimmedStart(nextNode.getText()));
                    }
                    if (nextNode.getStartOffset() > nodeEnd) {
                        charOut.append(RepeatedSequence.ofSpaces(nextNode.getStartOffset() - nodeEnd));
                    }
                }
            }
        }

        public void visit(@NotNull ASTNode node) {
            //final ASTNode node = element.getNode();
            IElementType type = node.getElementType();

            if (type == REFERENCE_LINK) {
                // this is special
                ASTNode textNode = node.findChildByType(REFERENCE_LINK_TEXT);
                if (textNode != null) {
                    charOut.append(textNode.getText());
                    addSpacesToNext(textNode);
                } else {
                    textNode = node.findChildByType(REFERENCE_LINK_REFERENCE);
                    if (textNode != null) {
                        charOut.append(textNode.getText());
                        addSpacesToNext(textNode);
                    }
                }
            } else if (type == REFERENCE_IMAGE) {
                // these no longer produce text for heading ids
                if (!forHeadingIdText) {
                    ASTNode textNode = node.findChildByType(REFERENCE_IMAGE_TEXT);
                    if (textNode != null) {
                        charOut.append(textNode.getText());
                    } else {
                        textNode = node.findChildByType(REFERENCE_IMAGE_REFERENCE);
                        if (textNode != null) {
                            charOut.append(textNode.getText());
                        }
                    }
                }
            } else if (type == EXPLICIT_LINK) {
                // these
                ASTNode textNode = node.findChildByType(LINK_REF_TEXT);
                if (textNode != null) {
                    charOut.append(textNode.getText());
                }
            } else if (type == IMAGE) {
                // these
                ASTNode textNode = node.findChildByType(IMAGE_LINK_REF_TEXT);
                if (textNode != null) {
                    charOut.append(textNode.getText());
                }
            } else if (type == WIKI_LINK) {
                // these
                ASTNode textNode = node.findChildByType(WIKI_LINK_TEXT);
                if (textNode != null) {
                    charOut.append(textNode.getText());
                } else {
                    textNode = node.findChildByType(MdTypes.WIKI_LINK_REF);
                    if (textNode != null) {
                        charOut.append(textNode.getText());
                    }
                }
            } else if (type == EMOJI) {
                // Emoji shortcut is included in heading text for ID
                if (forHeadingIdText) {
                    ASTNode textNode = node.findChildByType(EMOJI_ID);
                    if (textNode != null) {
                        charOut.append(textNode.getText());
                    }
                }
            } else {
                ASTNode child = node.getFirstChildNode();
                if (child != null) {
                    while (child != null) {
                        visit(child);
                        child = child.getTreeNext();
                    }
                } else {
                    if (PsiUtils.isTypeOf(type, myLexParserState.TYPOGRAPHIC_MARKER_SET)) {
                        charOut.append(node.getChars());
                    } else if (myLexParserState.INLINE_SPECIAL_TEXT.contains(type)) {
                        charOut.append(node.getChars().subSequence(1, node.getTextLength()));
                    } else if (myLexParserState.INLINE_PLAIN_TEXT.contains(type) || type == TEXT || type == HEADER_TEXT) {
                        charOut.append(node.getChars());
                    }
                }
            }
        }
    }

    public static boolean isInPlainText(@NotNull PsiElement element) {
        // move up parent chain and stop if encountering any in NON_TEXT_INLINE_ELEMENTS
        PsiElement context = element;
        while (context != null) {
            ASTNode node = context.getNode();
            if (node == null) break;
            IElementType elementType = node.getElementType();

            if (PsiUtils.isTypeOf(elementType, NON_TEXT_INLINE_ELEMENTS)) {
                return false;
            }

            if (elementType == HEADER_TEXT || elementType == TEXT_BLOCK || elementType == PARAGRAPH_BLOCK) {
                return true;
            }
            context = context.getParent();
        }

        return false;
    }

    @NotNull
    public static String getNodeText(@NotNull PsiElement element, boolean trimTrailingRefLinkText, boolean forHeadingIdText) {
        TextNodeVisitor textNodeVisitor = new TextNodeVisitor(trimTrailingRefLinkText, forHeadingIdText);
        textNodeVisitor.visit(element.getNode());
        return textNodeVisitor.charOut.toString();
    }

    public static class LinkRefElementTypes {
        @NotNull public final IElementType parentType;
        @NotNull public final IElementType linkRefType;
        @Nullable public final IElementType textType;
        @Nullable public final IElementType anchorType;
        @Nullable public final IElementType titleType;
        public final int extensionFlags;

        public LinkRefElementTypes(
                @NotNull IElementType parentType,
                @NotNull IElementType linkRefType,
                @Nullable IElementType textType,
                @Nullable IElementType anchorType,
                int extensionFlags
        ) {
            this(parentType, linkRefType, textType, anchorType, null, extensionFlags);
        }

        public LinkRefElementTypes(
                @NotNull IElementType parentType,
                @NotNull IElementType linkRefType,
                @Nullable IElementType textType,
                @Nullable IElementType anchorType,
                @Nullable IElementType titleType,
                int extensionFlags
        ) {
            this.parentType = parentType;
            this.linkRefType = linkRefType;
            this.textType = textType;
            this.anchorType = anchorType;
            this.titleType = titleType;
            this.extensionFlags = extensionFlags;
        }
    }

    @Nullable
    public static LinkRefElementTypes getNamedElementTypes(@Nullable PsiElement element) {
        if (element instanceof MdJekyllInclude
                || element instanceof MdJekyllIncludeLinkRef
        ) return JEKYLL_INCLUDE_ELEMENT;
        if (element instanceof MdImageLink
                || element instanceof MdImageLinkRef
                || element instanceof MdImageLinkText
                || element instanceof MdImageLinkTitle
        ) return IMAGE_LINK_ELEMENT;
        if (element instanceof MdReference
                || element instanceof MdReferenceLinkRef
                || element instanceof MdReferenceIdentifier
                || element instanceof MdReferenceTitle
                || element instanceof MdReferenceAnchor
        ) return REFERENCE_ELEMENT;
        if (element instanceof MdWikiLink
                || element instanceof MdWikiLinkRef
                || element instanceof MdWikiLinkText
                || element instanceof MdWikiLinkAnchor
        ) return WIKI_LINK_ELEMENT;

        if (element instanceof MdAutoLink
                || element instanceof MdLinkRef && element.getNode().getElementType() == AUTO_LINK_REF
                || element instanceof MdLinkAnchor && element.getNode().getElementType() == AUTO_LINK_ANCHOR
        ) return AUTO_LINK_ELEMENT;

        // NOTE: keep this one last, many others subclass it
        if (element instanceof MdExplicitLink
                || element instanceof MdLinkRef
                || element instanceof MdLinkText
                || element instanceof MdLinkTitle
                || element instanceof MdLinkAnchor
        ) return EXPLICIT_LINK_ELEMENT;
        return null;
    }

    @NotNull
    public static String getElementText(
            IElementType parentType,
            @Nullable PsiElement element,
            @Nullable IElementType elementType,
            @Nullable String prefix,
            @Nullable String suffix
    ) {
        String text = getElementTextOrNull(parentType, element, elementType, prefix, suffix);
        return text == null ? "" : text;
    }

    @Nullable
    public static String getElementTextOrNull(
            IElementType parentType,
            @Nullable PsiElement element,
            @Nullable IElementType elementType,
            @Nullable String prefix,
            @Nullable String suffix
    ) {
        PsiElement parent = element == null || elementType == null ? null : (element.getNode().getElementType() == parentType ? element : element.getParent());
        ASTNode astNode = element == null || elementType == null ? null : parent.getNode().findChildByType(elementType);
        if (astNode == null) return null;
        else {
            String nodeText = astNode.getText();
            if (suffix != null && prefix != null) return prefix + nodeText + suffix;
            if (prefix != null) return prefix + nodeText;
            if (suffix != null) return nodeText + suffix;
            return nodeText;
        }
    }

    @Nullable
    public static LinkRef getLinkRef(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null) {
            return getLinkRef(elementTypes, element);
        }
        return null;
    }

    @Nullable
    public static LinkRef getLinkRef(@NotNull LinkRefElementTypes elementTypes, @NotNull PsiElement element) {
        if (!element.isValid()) return null;

        if (elementTypes.parentType == WIKI_LINK) {
            return LinkRef.parseWikiLinkRef(new FileRef(element), getLinkRefTextWithAnchor(element), null);
        } else if (elementTypes.parentType == JEKYLL_INCLUDE_TAG_ELEM) {
            if (element instanceof MdJekyllIncludeImpl) {
                MdJekyllIncludeLinkRef linkRefElement = ((MdJekyllIncludeImpl) element).getLinkRefElement();
                if (linkRefElement != null) return LinkRef.parseLinkRef(new FileRef(element), linkRefElement.getFileName(), null);
                return null;
            } else {
                return LinkRef.parseLinkRef(new FileRef(element), ((MdJekyllIncludeLinkRefImpl) element).getFileName(), null);
            }
        } else if (elementTypes.parentType == IMAGE || elementTypes.parentType == REFERENCE_IMAGE) {
            return LinkRef.parseImageLinkRef(new FileRef(element), getLinkRefTextWithAnchor(element), null);
        } else {
            return LinkRef.parseLinkRef(new FileRef(element), getLinkRefTextWithAnchor(element), null);
        }
    }

    @NotNull
    public static String getLinkRefText(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null);
    }

    @NotNull
    public static String getLinkRefTextWithAnchor(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null)
                + getElementText(elementTypes.parentType, element, elementTypes.anchorType, "#", null);
    }

    @NotNull
    public static String getLinkAnchorText(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.anchorType, null, null);
    }

    @Nullable
    public static String getLinkAnchor(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? null : getElementText(elementTypes.parentType, element, elementTypes.anchorType, "#", null);
    }

    @Nullable
    public static String getLinkText(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? null : getElementTextOrNull(elementTypes.parentType, element, elementTypes.textType, null, null);
    }

    @NotNull
    public static String getLinkTitle(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);
    }

    @NotNull
    public static PsiElement setContent(@NotNull MdInlineCode element, @NotNull String code) {
        ASTNode markers = element.getNode().getFirstChildNode();
        if (markers != null) {
            MdInlineCode newElement = MdElementFactory.INSTANCE.createInlineCode(new MdFactoryContext(element), code, markers.getText());
            replaceInlineCodeContent(element, newElement);
        }
        return element;
    }

    public static void replaceInlineCodeContent(@NotNull MdInlineCode element, MdInlineCode newElement) {
        if (newElement != null) {
            // check if we have an &nbsp; right before the new element and old element is at start of paragraph, then we insert the &nbsp;
            PsiElement insertElement = null;
            PsiElement prevSibling = newElement.getPrevSibling();
            if (prevSibling instanceof LeafPsiElement && prevSibling.getNode().getElementType() == HTML_ENTITY) {
                // need to insert it into parent before the element
                PsiElement paragraph = getParagraphParent(element);
                if (paragraph != null) {
                    int startOffset = paragraph.getTextOffset();
                    int elementStart = element.getTextOffset();
                    if (elementStart == startOffset || element.getText().substring(startOffset, elementStart).trim().isEmpty()) {
                        // we will add an &nbsp; prevent interpretation as code fence
                        insertElement = prevSibling;
                    }
                }
            }

            // NOTE: replacing the content node leaves the element unchanged and preserves the injection all the time
            //    if the element is changed, injection may be lost and will have to be initiated again by the user.
            element.getNode().replaceAllChildrenToChildrenOf(newElement.getNode());
            if (insertElement != null) {
                element.getParent().addBefore(insertElement, element);
            }
        }
    }

    @Nullable
    public static MdParagraph getParagraphParent(@NotNull MdPsiElement element) {
        PsiElement parent = element.getParent();
        PsiElement paragraph = parent.getParent();
        while (!(paragraph instanceof PsiFile || paragraph instanceof MdParagraph)) paragraph = paragraph.getParent();
        return paragraph instanceof MdParagraph ? (MdParagraph) paragraph : null;
    }

    @NotNull
    public static PsiElement setContent(@NotNull MdInlineHtml element, @NotNull String html) {
        MdInlineHtml newElement = MdElementFactory.INSTANCE.createInlineHtml(new MdFactoryContext(element), html);
        if (newElement != null) {
            element.getNode().replaceAllChildrenToChildrenOf(newElement.getNode());
        }
        return element;
    }

    @NotNull
    public static MdFootnoteImpl setFootnoteName(MdFootnoteImpl element, String name) {
        if (!element.isValid()) return element;
        MdFootnoteId textElement = element.getReferenceIdentifier();
        if (textElement == null) return element;

        MdFootnote newElement;
        newElement = MdElementFactory.INSTANCE.createFootnote(new MdFactoryContext(element), name, element.getFootnoteText());
        if (newElement != null) {
            PsiElement newTextElement = newElement.getReferenceIdentifier();
            if (newTextElement != null) textElement.replace(newTextElement);
        }
        return element;
    }

    @NotNull
    public static MdMacroImpl setMacroName(MdMacroImpl element, String name) {
        if (!element.isValid()) return element;
        MdMacroId textElement = element.getReferenceIdentifier();
        if (textElement == null) return element;

        MdMacro newElement;
        newElement = MdElementFactory.INSTANCE.createMacro(new MdFactoryContext(element), name, element.getMacroText());
        if (newElement != null) {
            PsiElement newTextElement = newElement.getReferenceIdentifier();
            if (newTextElement != null) textElement.replace(newTextElement);
        }
        return element;
    }

    @NotNull
    public static MdAttributeImpl setAttributeIdValueName(MdAttributeImpl element, String value) {
        MdAttribute newElement;
        newElement = MdElementFactory.INSTANCE.createAttribute(new MdFactoryContext(element), "id", value);
        if (newElement != null) {
            return (MdAttributeImpl) element.replace(newElement);
        }
        return element;
    }

    @NotNull
    public static MdAttributeImpl setAttributeIdValueType(MdAttributeImpl element, String value) {
        if (!element.isValid()) return element;
        MdAttributeValue textElement = element.getAttributeValueElement();
        String newValue = value;

        if (textElement != null) {
            String text = textElement.getText();
            int pos = text.indexOf(':');

            if (pos > -1) {
                newValue = value + text.substring(pos);
            }
        }

        MdAttribute newElement;
        newElement = MdElementFactory.INSTANCE.createAttribute(new MdFactoryContext(element), "id", newValue);
        if (newElement != null) {
            return (MdAttributeImpl) element.replace(newElement);
        }
        return element;
    }

    @NotNull
    public static MdEnumeratedReferenceId setEnumeratedReferenceType(
            MdEnumeratedReferenceId element,
            String value
    ) {
        if (!element.isValid()) return element;
        String newValue = value;

        String text = element.getText();
        if (text != null) {
            int pos = text.indexOf(':');

            if (pos > -1) {
                newValue = value + text.substring(pos);
            }
        }

        MdEnumeratedReferenceBase newElement;

        if (element instanceof MdEnumeratedReferenceLink) {
            newElement = MdElementFactory.INSTANCE.createEnumeratedReferenceLink(new MdFactoryContext(element), newValue);
        } else {
            newElement = MdElementFactory.INSTANCE.createEnumeratedReferenceText(new MdFactoryContext(element), newValue);
        }

        if (newElement != null) {
            final MdReferencingElementReference referenceTextElement = newElement.getReferenceIdElement();
            if (referenceTextElement != null) {
                return (MdEnumeratedReferenceId) element.replace(referenceTextElement);
            }
        }
        return element;
    }

    @NotNull
    public static MdEnumeratedReferenceBase setEnumeratedReferenceText(
            MdEnumeratedReferenceBase element,
            String value
    ) {
        if (!element.isValid()) return element;

        MdEnumeratedReferenceBase newElement;

        if (element instanceof MdEnumeratedReferenceLink) {
            newElement = MdElementFactory.INSTANCE.createEnumeratedReferenceLink(new MdFactoryContext(element), value);
        } else {
            newElement = MdElementFactory.INSTANCE.createEnumeratedReferenceText(new MdFactoryContext(element), value);
        }
        if (newElement != null) {
            return (MdEnumeratedReferenceBase) element.replace(newElement);
        }
        return element;
    }

    @NotNull
    public static MdEnumeratedReferenceFormatImpl setEnumeratedReferenceFormatName(
            MdEnumeratedReferenceFormatImpl element,
            String name
    ) {
        if (!element.isValid()) return element;
        MdEnumeratedReferenceFormatType textElement = element.getReferenceIdentifier();
        if (textElement == null) return element;

        MdEnumeratedReferenceFormat newElement;
        newElement = MdElementFactory.INSTANCE.createEnumeratedReferenceFormat(new MdFactoryContext(element), name, element.getFormatText());
        if (newElement != null) {
            PsiElement newTextElement = newElement.getReferenceIdentifier();
            if (newTextElement != null) textElement.replace(newTextElement);
        }
        return element;
    }

    @NotNull
    public static MdAbbreviationImpl setAbbreviationName(
            MdAbbreviationImpl element,
            String name
    ) {
        if (!element.isValid()) return element;
        MdAbbreviationId textElement = element.getReferenceIdentifier();
        if (textElement == null) return element;

        MdAbbreviation newElement;
        newElement = MdElementFactory.INSTANCE.createAbbreviation(new MdFactoryContext(element), name, element.getExpandedText());
        if (newElement != null) {
            PsiElement newTextElement = newElement.getReferenceIdentifier();
            if (newTextElement != null) textElement.replace(newTextElement);
        }
        return element;
    }

    @NotNull
    public static MdRefAnchorImpl setRefAnchorName(MdRefAnchorImpl element, String name) {
        if (!element.isValid()) return element;
        MdRefAnchorId refAnchorId = element.getReferenceIdentifier();
        String anchorText = refAnchorId == null ? null : refAnchorId.getText();

        for (PsiElement anchorId : element.getChildren()) {
            if (anchorId instanceof MdRefAnchorId && (anchorText == null || anchorText.equals(anchorId.getText()))) {
                MdRefAnchorId textElement = (MdRefAnchorId) anchorId;
                MdRefAnchor newElement;
                newElement = MdElementFactory.INSTANCE.createRefAnchor(new MdFactoryContext(element), name, element.getAnchorText());
                if (newElement != null) {
                    PsiElement newTextElement = newElement.getReferenceIdentifier();
                    if (newTextElement != null) textElement.replace(newTextElement);
                }
            }
        }

        return element;
    }

    @NotNull
    public static MdEmoji setEmojiName(MdEmojiImpl element, String name) {
        if (!element.isValid()) return element;
        MdEmojiId textElement = element.getEmojiIdentifier();
        if (textElement == null) return element;

        MdEmoji newElement;
        newElement = MdElementFactory.INSTANCE.createEmoji(new MdFactoryContext(element), name);

        PsiElement newTextElement = newElement == null ? null : newElement.getEmojiIdentifier();
        if (newTextElement != null) textElement.replace(newTextElement);
        return element;
    }

    @NotNull
    public static MdNamedElement setName(
            @NotNull MdNamedElement element,
            @NotNull String newName,
            int renameFlags
    ) {
        if (!element.isValid()) return element;

        if (element instanceof MdReferencingElementReferenceImpl) {
            // this is a reference, we swap in the name?
            MdReferencingElement parent = (MdReferencingElement) element.getParent();
            MdReferencingElement newElement;

            if (parent instanceof MdReferenceLinkImpl) {
                newElement = MdElementFactory.INSTANCE.createReferenceLink(new MdFactoryContext(element), newName, parent.getReferenceText());
            } else if (parent instanceof MdReferenceImageImpl) {
                newElement = MdElementFactory.INSTANCE.createReferenceImage(new MdFactoryContext(element), newName, parent.getReferenceText());
            } else if (parent instanceof MdFootnoteRefImpl) {
                newElement = MdElementFactory.INSTANCE.createFootnoteRef(new MdFactoryContext(element), newName);
            } else if (parent instanceof MdMacroRefImpl) {
                newElement = MdElementFactory.INSTANCE.createMacroRef(new MdFactoryContext(element), newName);
            } else if (parent instanceof MdAbbreviatedTextImpl) {
                newElement = MdElementFactory.INSTANCE.createAbbreviatedText(new MdFactoryContext(element), newName);
            } else if (parent instanceof MdEnumeratedReferenceLink) {
                newElement = MdElementFactory.INSTANCE.createEnumeratedReferenceLink(new MdFactoryContext(element), newName);
            } else if (parent instanceof MdEnumeratedReferenceText) {
                newElement = MdElementFactory.INSTANCE.createEnumeratedReferenceText(new MdFactoryContext(element), newName);
            } else {
                throw new IllegalArgumentException("Don't know how to set name for " + element.getClass());
            }

            if (newElement != null) {
                ASTNode oldNode = parent.getNode().findChildByType(parent.getReferenceChildType());
                assert oldNode != null : "Unexpected, ReferenceElement with null Reference child node";

                ASTNode newNode = newElement.getNode().findChildByType(parent.getReferenceChildType());
                assert newNode != null : "Unexpected, new ReferenceElement with null Reference child node";

                parent.getNode().replaceChild(oldNode, newNode);
                MdNamedElement newReference = (MdNamedElement) findChildByType(parent, parent.getReferenceChildType());
                if (newReference != null) return newReference;
            }
            return element;
        }

        // catch all for all link types
        return setLinkRefName(element, newName, renameFlags, null);
    }

    @NotNull
    public static MdNamedElement setLinkRefName(
            @NotNull MdNamedElement element,
            @NotNull String newName,
            int renameFlags,
            @Nullable MdFile containingFile
    ) {
        if (!element.isValid()) return element;

        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes == null) return element;

        ASTNode pageRefNode = element.getNode();
        if (pageRefNode == null) return element;

        // CAUTION: when the element was replaced, as happens to WikiLinks during FileMoveRefactoring, their containing virtual file is NULL
        LinkRef newNameInfo = LinkRef.parseLinkRef(containingFile != null ? new FileRef(containingFile) : new FileRef(element), newName, null);

        PsiElement parent = element.getParent();
        String linkRef = getElementText(elementTypes.parentType, parent, elementTypes.linkRefType, null, null);
        String title = null;
        String text = null;
        String anchor = null;
        boolean isEncoded = ImageUtils.isEncodedImage(newName) || ImageUtils.isEncodedImage(linkRef);

        IElementType elementType = element.getNode().getElementType();

        if (elementType == elementTypes.linkRefType) {
            anchor = newNameInfo.getAnchor();

            if (elementTypes.extensionFlags != 0 && (renameFlags & RENAME_ELEMENT_HANDLES_EXT) != 0) {
                PathInfo linkRefInfo = new PathInfo(linkRef);

                switch (elementTypes.extensionFlags) {
                    case EXTENSION_KEEP_OLD:
                        linkRef = newNameInfo.getFilePathNoExt() + linkRefInfo.getExtWithDot();
                        break;

                    case EXTENSION_STRIP:
                        linkRef = newNameInfo.getFilePathNoExt();
                        break;

                    case EXTENSION_USE_NEW_IF_OLD_HAS:
                        linkRef = linkRefInfo.getHasExt() ? newNameInfo.getFilePath() : newNameInfo.getFilePathNoExt();
                        break;

                    case EXTENSION_USE_NEW:
                    default:
                        linkRef = newNameInfo.getFilePath();
                        break;
                }
            } else {
                linkRef = newNameInfo.getFilePath();
            }

            if (!isEncoded) {
                if ((renameFlags & RENAME_KEEP_PATH) != 0 && element.getText().contains("/")) {
                    // keep the old path
                    String path = new PathInfo(element.getText()).getPath();
                    String name = new PathInfo(newName).getFileName();
                    linkRef = path + name;
                }

                if ((renameFlags & RENAME_KEEP_NAME) != 0) {
                    // keep the old name
                    String path = newNameInfo.getPath();
                    String name = new PathInfo(element.getText()).getFileName();
                    linkRef = path + name;
                }

                // preserve anchor
                if ((renameFlags & RENAME_KEEP_ANCHOR) != 0 && elementTypes.anchorType != null) {
                    anchor = getElementText(elementTypes.parentType, parent, elementTypes.anchorType, "#", null);
                }
            }

            // preserve text
            if ((renameFlags & RENAME_KEEP_TEXT) != 0) {
                text = getElementText(elementTypes.parentType, parent, elementTypes.textType, null, null);
            }

            // preserve title
            if ((renameFlags & RENAME_KEEP_TITLE) != 0) {
                title = getElementText(elementTypes.parentType, parent, elementTypes.titleType, null, null);
            }
        } else if (elementType == elementTypes.anchorType) {
            linkRef += newName.isEmpty() ? newName : "#" + newName;
        } else if (elementType == elementTypes.textType) {
            text = newName;
        } else if (elementType == elementTypes.titleType) {
            title = newName;
        } else {
            // no such beast
            LOG.debug("MultiMarkdownPsiImplUtil.setName called for unhandled element " + element);
            return element;
        }

        PsiElement newLink = null;
        if (elementTypes.parentType == WIKI_LINK) {
            newLink = MdElementFactory.INSTANCE.createWikiLink(new MdFactoryContext(element), linkRef, text, anchor);
        } else if (elementTypes.parentType == IMAGE) {
            newLink = MdElementFactory.INSTANCE.createImageLink(new MdFactoryContext(element), linkRef, text, title);
        } else if (elementTypes.parentType == AUTO_LINK) {
            boolean wrapInBrackets = element.getParent().getText().startsWith("<");
            newLink = MdElementFactory.INSTANCE.createAutoLink(new MdFactoryContext(element), wrapInBrackets, linkRef, anchor);
        } else if (elementTypes.parentType == EXPLICIT_LINK) {
            newLink = MdElementFactory.INSTANCE.createExplicitLink(new MdFactoryContext(element), linkRef, text, anchor, title);
        } else if (elementTypes.parentType == JEKYLL_INCLUDE_TAG_ELEM) {
            // preserve double quotes
            PsiElement openQuote = element.getPrevSibling();
            PsiElement closeQuote = element.getNextSibling();
            if (openQuote != null && openQuote.getNode().getElementType() == JEKYLL_LINKREF_OPEN) {
                linkRef = openQuote.getText() + linkRef;
            }
            if (closeQuote != null && closeQuote.getNode().getElementType() == JEKYLL_LINKREF_CLOSE) {
                linkRef = linkRef + closeQuote.getText();
            }
            newLink = MdElementFactory.INSTANCE.createJekyllInclude(new MdFactoryContext(element), linkRef);
        } else if (elementTypes.parentType == REFERENCE) {
            if (text == null) text = ((MdReferenceImpl) element.getParent()).getLinkText();
            if (text != null)
                newLink = MdElementFactory.INSTANCE.createReference(new MdFactoryContext(element), text, linkRef, anchor, title);
        }

        if (newLink != null) {
            if (elementType == elementTypes.linkRefType) {
                //element.getParent().replace(newLink);
                ASTNode parentAST = parent.getNode();
                ASTNode linkRefChild = element.getNode();
                ASTNode newLinkRefChild = newLink.getNode().findChildByType(elementType);

                // QUERY: figure out why replacing does not work but adding and removing does?
                if (newLinkRefChild != null) {
                    //parentAST.replaceChild(firstChildNode, newLink.getFirstChild().getNode());
                    parentAST.addChildren(newLinkRefChild, null, linkRefChild);
                    parentAST.removeRange(linkRefChild, null);

                    MdNamedElement newElement = (MdNamedElement) findChildByType(parent, elementType);
                    if (newElement != null) return newElement;
                }
            } else {
                MdNamedElement newElement = (MdNamedElement) findChildByType(newLink, elementType);
                if (newElement != null) {
                    return (MdNamedElement) element.replace(newElement);
                }
            }
        }
        return element;
    }

    @NotNull
    public static PsiElement setLanguage(
            @NotNull MdVerbatim element,
            @Nullable String languageName,
            @Nullable String leadMarkerPrefix
    ) {
        if (!element.isValid()) return element;

        ASTNode parentAST = element.getNode();

        ASTNode markerNode = parentAST.findChildByType(VERBATIM_OPEN);
        assert markerNode != null : "Unexpected, Verbatim Node with null VERBATIM_MARKER child node";

        ASTNode verbatimContentNode = parentAST.findChildByType(VERBATIM_CONTENT);
        assert verbatimContentNode != null : "Unexpected, Verbatim Node with null VERBATIM_MARKER child node";

        ASTNode verbatimLanguageNode = parentAST.findChildByType(VERBATIM_LANG);
        assert verbatimLanguageNode != null : "Unexpected, Verbatim Node with null VERBATIM_LANGUAGE child node";

        if (leadMarkerPrefix == null) {
            leadMarkerPrefix = element.getLeadMarkerPrefix();
        }

        MdVerbatim newElement = MdElementFactory.INSTANCE.createVerbatim(
                new MdFactoryContext(element),
                markerNode.getText(),
                languageName,
                verbatimContentNode.getText(),
                leadMarkerPrefix
        );

        if (newElement != null) {
            ASTNode newNode = newElement.getNode().findChildByType(VERBATIM_LANG);
            assert newNode != null : "Unexpected, new Verbatim Node with null VERBATIM_LANGUAGE child node";

            parentAST.replaceChild(verbatimLanguageNode, newNode);
        }

        return element;
    }

    @NotNull
    public static PsiElement setContent(
            @NotNull MdVerbatim element,
            @Nullable String verbatimContent,
            @Nullable String leadMarkerPrefix
    ) {
        if (!element.isValid()) return element;

        ASTNode parentAST = element.getNode();

        ASTNode markerNode = parentAST.findChildByType(VERBATIM_OPEN);
        ASTNode verbatimContentNode = parentAST.findChildByType(VERBATIM_CONTENT);
        assert verbatimContentNode != null : "Unexpected, Verbatim Node with null VERBATIM_MARKER child node";

        ASTNode verbatimLanguageNode = parentAST.findChildByType(VERBATIM_LANG);
        //assert verbatimLanguageNode != null : "Unexpected, Verbatim Node with null VERBATIM_LANGUAGE child node";

        if (leadMarkerPrefix == null) {
            leadMarkerPrefix = element.getLeadMarkerPrefix();
        }

        MdVerbatim newElement = MdElementFactory.INSTANCE.createVerbatim(
                new MdFactoryContext(element),
                markerNode == null ? null : markerNode.getText(),
                verbatimLanguageNode != null ? verbatimLanguageNode.getText() : null,
                verbatimContent,
                leadMarkerPrefix
        );

        if (newElement != null) {
            ASTNode newNode = newElement.getNode().findChildByType(VERBATIM_CONTENT);
            assert newNode != null : "Unexpected, new Verbatim Node with null VERBATIM_CONTENT child node";

            parentAST.replaceChild(verbatimContentNode, newNode);
        }
        return element;
    }

    @NotNull
    public static PsiElement setContent(
            @NotNull MdImageMultiLineUrlContent element,
            @Nullable String verbatimContent
    ) {
        if (!element.isValid()) return element;

        ASTNode content = element.getNode().findChildByType(IMAGE_URL_CONTENT);
        assert content != null;

        MdImageLink imageLink = (MdImageLink) element.getParent();

        MdImageLink newElement = MdElementFactory.INSTANCE.createImageLink(
                new MdFactoryContext(element),
                element.getContentUrlPrefix() + verbatimContent,
                imageLink.getLinkText(),
                imageLink.getLinkTitleText()
        );

        if (newElement != null) {
            MdImageMultiLineUrlContentImpl linkRefElement = newElement.getLinkRefUrlContentElement();
            ASTNode newNode = linkRefElement == null ? null : linkRefElement.getNode().findChildByType(IMAGE_URL_CONTENT);
            assert newNode != null : "Unexpected, new ImageLink Node with null MULTI_LINE_URL_CONTENT child node";

            element.getNode().replaceChild(content, newNode);
        }
        return element;
    }

    @NotNull
    public static PsiElement setContent(@NotNull MdHtmlBlock element, @Nullable String content) {
        if (!element.isValid()) return element;

        MdHtmlBlock newElement = MdElementFactory.INSTANCE.createHtmlBlock(new MdFactoryContext(element), content);
        if (newElement != null) return element.replace(newElement);
        return element;
    }

    @NotNull
    public static PsiElement setContent(
            @NotNull MdJekyllFrontMatterBlock element,
            @Nullable String content
    ) {
        if (!element.isValid()) return element;

        MdJekyllFrontMatterBlock newElement = MdElementFactory.INSTANCE.createJekyllFrontMatterBlock(new MdFactoryContext(element), content);
        if (newElement != null) return element.replace(newElement);
        return element;
    }

    @NotNull
    public static PsiElement setContent(@NotNull FlexmarkFrontMatterBlock element, @Nullable String content) {
        if (!element.isValid()) return element;

        FlexmarkFrontMatterBlock newElement = MdElementFactory.INSTANCE.createFlexmarkFrontMatterBlock(new MdFactoryContext(element), content);
        if (newElement != null) return element.replace(newElement);
        return element;
    }

    @NotNull
    public static MdWikiLink deleteWikiLinkTitle(MdWikiLink element) {
        if (!element.isValid()) return element;

        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(MdTypes.WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MdWikiLink newElement = MdElementFactory.INSTANCE.createWikiLink(new MdFactoryContext(element), pageRefNode.getText());
            if (newElement != null) return (MdWikiLink) element.replace(newElement);
        }
        return element;
    }

    @NotNull
    public static MdWikiLink deleteWikiLinkRef(MdWikiLink element) {
        if (!element.isValid()) return element;

        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(MdTypes.WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MdWikiLink newElement = MdElementFactory.INSTANCE.createWikiLink(new MdFactoryContext(element), pageTitleNode.getText());
            if (newElement != null) return (MdWikiLink) element.replace(newElement);
        }

        return element;
    }

    @NotNull
    public static MdWikiLink swapWikiLinkRefTitle(MdWikiLink element) {
        if (!element.isValid()) return element;

        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(MdTypes.WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            String anchorText = getElementText(WIKI_LINK, element, WIKI_LINK_REF_ANCHOR, "#", null);
            String pageTitleNodeText = pageTitleNode.getText();

            if (pageTitleNodeText.startsWith("#")) {
                // add this page name
                pageTitleNodeText = WikiLinkRef.fileAsLink(element.getContainingFile().getName(), null) + pageTitleNodeText;
            }

            MdWikiLink newElement = MdElementFactory.INSTANCE.createWikiLink(
                    new MdFactoryContext(element),
                    pageTitleNodeText,
                    pageRefNode.getText() + anchorText,
                    null
            );
            if (newElement != null) return (MdWikiLink) element.replace(newElement);
        }

        return element;
    }

    @Nullable
    public static PsiElement findChildByType(@NotNull PsiElement parentElement, @NotNull IElementType childType) {
        if (!parentElement.isValid()) return null;

        for (PsiElement child : parentElement.getChildren()) {
            if (child.getNode().getElementType() == childType) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement findChildByTypeNoCheck(
            @NotNull PsiElement parentElement,
            @NotNull IElementType childType
    ) {
        //if (!parentElement.isValid()) return null;

        for (PsiElement child : parentElement.getChildren()) {
            if (child.getNode().getElementType() == childType) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement findNestedChildByType(@NotNull PsiElement parentElement, @NotNull IElementType childType) {
        if (!parentElement.isValid()) return null;

        for (PsiElement child : parentElement.getChildren()) {
            if (child.getNode().getElementType() == childType) {
                return child;
            }

            PsiElement nestedChild = findNestedChildByType(child, childType);
            if (nestedChild != null) return nestedChild;
        }
        return null;
    }

    public static ArrayList<ASTNode> collectChildrenOfType(
            @NotNull ASTNode parent,
            boolean nested,
            @Nullable String text,
            int maxCount,
            @Nullable ArrayList<ASTNode> children,
            @NotNull IElementType childType
    ) {
        ASTNode child = parent.getFirstChildNode();
        while (child != null) {
            if (child.getElementType() == childType) {
                if (text == null || child.getText().equals(text)) {
                    if (children == null) children = new ArrayList<>();
                    children.add(child);
                    if (children.size() >= maxCount) break;
                }
            } else if (nested) {
                children = collectChildrenOfType(child, true, text, maxCount, children, childType);
                if (children != null && children.size() >= maxCount) break;
            }
            child = child.getTreeNext();
        }
        return children;
    }

    @Nullable
    public static List<ASTNode> findChildrenByType(
            @NotNull ASTNode parent,
            boolean nested,
            @Nullable String text,
            int maxCount,
            @NotNull IElementType childType
    ) {
        return collectChildrenOfType(parent, nested, text, maxCount, null, childType);
    }

    @NotNull
    public static <T> Result<T> visitNodes(
            @NotNull ASTNode parent,
            NestedConsumer<T, ASTNode> consumer
    ) {
        ASTNode child = parent.getFirstChildNode();
        while (child != null) {
            Result<T> result = consumer.consume(child);
            if (result.isStop()) return result;
            if (!result.isSkip()) {
                result = visitNodes(child, consumer);
                if (result.isSkip()) return result;
            }
            child = child.getTreeNext();
        }
        return Result.CONTINUE();
    }

    @NotNull
    public static <T> Result<T> visitNodesReversed(
            @NotNull ASTNode parent,
            NestedConsumer<T, ASTNode> consumer
    ) {
        ASTNode child = parent.getLastChildNode();
        while (child != null) {
            Result<T> result = consumer.consume(child);
            if (result.isStop()) return result;
            if (!result.isSkip()) {
                result = visitNodesReversed(child, consumer);
                if (result.isSkip()) return result;
            }
            child = child.getTreePrev();
        }
        return Result.CONTINUE();
    }

    public static void insertBlankLineAfter(
            @NotNull Document document,
            @NotNull PsiElement element,
            @Nullable BlockPrefixes parentPrefixes,
            @NotNull PsiEditContext editContext
    ) {
        int endOffset = editContext.postEditNodeEnd(element.getNode());
        BlockPrefixes prefixes = getBlockPrefixes(element, parentPrefixes, editContext).finalizePrefixes(editContext);

        if (endOffset <= editContext.getCharSequence().length() && editContext.getCharSequence().charAt(endOffset - 1) != '\n') {
            // insert two \n
            document.insertString(endOffset, "\n" + prefixes.getChildPrefix() + "\n");
        } else {
            document.insertString(endOffset, prefixes.getChildPrefix() + "\n");
        }
    }

    public static void insertBlankLineBefore(
            @NotNull Document document,
            @NotNull PsiElement element,
            @Nullable BlockPrefixes parentPrefixes,
            @NotNull PsiEditContext editContext
    ) {
        int startOffset = editContext.postEditNodeEnd(element.getNode());
        BlockPrefixes prefixes = getBlockPrefixes(element, parentPrefixes, editContext).finalizePrefixes(editContext);

        if (startOffset > 0 && editContext.getCharSequence().charAt(startOffset - 1) != '\n') {
            // insert two \n
            document.insertString(startOffset, "\n" + prefixes.getChildPrefix() + "\n");
        } else {
            document.insertString(startOffset, prefixes.getChildPrefix() + "\n");
        }
    }

    @NotNull
    public static BlockPrefixes getBlockPrefixes(
            @NotNull PsiElement element,
            @Nullable BlockPrefixes parentPrefixes,
            @NotNull PsiEditContext editContext
    ) {
        PsiElement parent = element;
        if (parent instanceof PsiFile) return BlockPrefixes.EMPTY;

        while (parent != null && !(parent instanceof PsiFile)) {
            if (parent instanceof MdIndentingComposite) {
                MdIndentingComposite indentingComposite = (MdIndentingComposite) parent;
                return indentingComposite.itemPrefixes(parentPrefixes, editContext);
            }
            parent = parent.getParent();
        }

        return BlockPrefixes.EMPTY;
    }

    public static @Nullable
    PsiElement getItemBlock(PsiElement parent) {
        PsiElement child = parent.getFirstChild();

        while (child != null) {
            if (!(child instanceof LeafPsiElement)) return child;
            child = child.getNextSibling();
        }

        return null;
    }

    public static boolean isFirstIndentedBlock(@NotNull PsiElement element, boolean defaultIfNoIndentingParent) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiFile) return false;
        PsiElement child = element;

        while (parent != null && !(parent instanceof PsiFile)) {
            if (parent instanceof MdIndentingContainer) {
                MdIndentingContainer indentingContainer = (MdIndentingContainer) parent;
                return indentingContainer.isFirstItemBlock(child);
            }

            // if not first child of parent then does not matter if parent is first child
            if (getItemBlock(parent) != child) return false;

            child = parent;
            parent = parent.getParent();
        }

        return defaultIfNoIndentingParent;
    }

    public static boolean isFirstIndentedBlockPrefix(@NotNull PsiElement element, boolean defaultIfNoIndentingParent) {
        return isFirstIndentedBlockPrefix(element, defaultIfNoIndentingParent, null);
    }

    public static boolean isFirstIndentedBlockPrefix(@NotNull PsiElement element, boolean defaultIfNoIndentingParent, @Nullable Predicate<PsiElement> predicate) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiFile) return false;
        PsiElement child = element;

        while (parent != null && !(parent instanceof PsiFile)) {
            if (parent instanceof MdIndentingContainer) {
                MdIndentingContainer indentingContainer = (MdIndentingContainer) parent;
                boolean firstItem = indentingContainer.isFirstItemBlock(child) && indentingContainer.isFirstItemBlockPrefix(child);
                if (!firstItem || predicate != null && predicate.test(parent)) return firstItem;
            }

            // if not first child of parent then does not matter if parent is first child
            if (getItemBlock(parent) != child) return false;

            child = parent;
            parent = parent.getParent();
        }

        return defaultIfNoIndentingParent;
    }

    public static boolean isFirstChildElement(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiFile) return false;

        final PsiElement[] children = parent.getChildren();
        return children.length > 0 && children[0] == element;
    }

    @NotNull
    public static ASTNode lastLeafChild(@NotNull ASTNode node) {
        ASTNode lastChild = node.getLastChildNode();
        if (lastChild != null) {
            return lastLeafChild(lastChild);
        }
        return node;
    }

    public static void addLinePrefix(
            @NotNull PsiElement element,
            @NotNull LineAppendable lines,
            final boolean prefixFirstChild,
            @NotNull PsiEditContext editContext
    ) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiFile) return;

        BlockPrefixes prefixes = getBlockPrefixes(element, null, editContext);

        addLinePrefix(lines, prefixes, isFirstIndentedBlock(element, false), prefixFirstChild);
    }

    public static void addLinePrefix(
            @NotNull LineAppendable lines,
            @NotNull BlockPrefixes prefixes,
            final boolean isFirstIndentedChild,
            final boolean prefixFirstChild
    ) {
        CharSequence firstLinePrefix = isFirstIndentedChild ? (prefixFirstChild ? prefixes.getItemPrefix() : "") : prefixes.getChildPrefix();
        CharSequence linePrefix = isFirstIndentedChild ? prefixes.getItemContPrefix() : prefixes.getChildContPrefix();

        addLinePrefix(lines, firstLinePrefix, linePrefix);
    }

    public static void addLinePrefix(
            @NotNull LineAppendable lines,
            @NotNull CharSequence firstLinePrefix,
            @NotNull CharSequence linePrefix
    ) {
        CharSequence addPrefix = firstLinePrefix;

        for (LineInfo info : lines) {
            BasedSequence prefix = info.getPrefix();
            BasedSequence text = info.getText();

            if (text.safeCharAt(0) == ParagraphContext.MARKDOWN_START_LINE_CHAR) {
                // strip out start line char
                text = text.subSequence(1);
            }

            if (text.safeCharAt(text.length() - 1) == ParagraphContext.MARKDOWN_START_LINE_CHAR) {
                // strip out start line char
                text = text.subSequence(0, text.length() - 1);
            }

            if (addPrefix.length() != 0 || text.length() != info.textLength) {
                lines.setLine(info.index, LineAppendable.combinedPrefix(addPrefix, prefix), text);
            }

            addPrefix = linePrefix;
        }
    }

    public static void adjustLinePrefix(
            @NotNull PsiElement element,
            @NotNull LineAppendable lines,
            @NotNull PsiEditContext editContext
    ) {
        PsiElement parent = element.getParent();
        LineAppendable result = lines;

        if (parent instanceof PsiFile) return;

        PsiElement child = element;
        List<MdIndentingComposite> parents = new ArrayList<>();
        List<PsiElement> children = new ArrayList<>();

        while (parent != null && !(parent instanceof PsiFile)) {
            if (parent instanceof MdIndentingComposite) {
                parents.add((MdIndentingComposite) parent);
                children.add(child);
            }
            child = parent;
            parent = parent.getParent();
        }

        int iMax = parents.size();
        if (iMax == 0) return;

        int[] indentColumns = new int[lines.getLineCount()];

        for (int i = iMax; i-- > 0; ) {
            MdIndentingComposite indentingComposite = parents.get(i);
            child = children.get(i);
            indentingComposite.removeLinePrefix(result, indentColumns, indentingComposite.isFirstItemBlockPrefix(child), editContext);
        }
    }

    public static LineAppendable linesForWrapping(
            @NotNull PsiElement element,
            boolean includeFirstLinePrefix,
            boolean includeTailBlankLine,
            boolean addLastLineEOL,
            @NotNull PsiEditContext editContext
    ) {
        LineAppendable textToWrap = editContext.getLineAppendable();
        BasedSequence elementText = editContext.elementText(element);

        int nodeStartOffset = editContext.postEditNodeStart(element.getNode(), PostEditAdjust.ALWAYS);
        final BasedSequence charSequence = editContext.getCharSequence();
        int lineStart = charSequence.lineAt(nodeStartOffset).getStartOffset();

        // NOTE: inserting at start of paragraph causes the character to be added as prefix and as text
        if (elementText.getStartOffset() + 1 == nodeStartOffset) {
//            if (editContext.isIndentingChar(elementText.charAt(0))) {
//                // it is an indenting character, remove it from element text
//                elementText = elementText.subSequence(1);
//            } else {
//                // it is not an indenting character, remove it from the prefix
//                nodeStartOffset--;
//            }
            nodeStartOffset--;
        }

        if (includeFirstLinePrefix || !MdPsiImplUtil.isFirstIndentedBlock(element, true)) {
            // need to add text from node start to start of line
            if (lineStart < nodeStartOffset) {
                textToWrap.append(editContext.getCharSequence().subSequence(lineStart, nodeStartOffset));
            }
        }

        textToWrap.append(elementText);

        if (textToWrap.isNotEmpty()) {
            textToWrap.append(elementText.getEmptySuffix());
            if (addLastLineEOL) textToWrap.line();

            if (!includeTailBlankLine) {
                int trailingBlankLines = textToWrap.getTrailingBlankLines();
                if (trailingBlankLines > 0) {
                    // remove the last line
                    textToWrap.removeExtraBlankLines(Integer.MAX_VALUE, trailingBlankLines - 1);
                }
            }
        }

        // remove prefixes
        adjustLinePrefix(element, textToWrap, editContext);

        return textToWrap;
    }

    @Nullable
    public static Document getElementDocument(@NotNull PsiElement element) {
        return FileDocumentManager.getInstance().getDocument(element.getContainingFile().getVirtualFile());
    }

    @NotNull
    public static ItemPresentation getPresentation(final @NotNull MdStructureViewPresentableItem element) {
        return new MdElementItemPresentation(element);
    }

    @NotNull
    public static ItemPresentation getPresentation(
            final @Nullable String presentableText,
            final @Nullable String locationString,
            final @Nullable Icon icon
    ) {
        return new MdItemPresentation(presentableText, locationString, icon);
    }

    @NotNull
    public static List<PsiElement> findChildrenOfTypes(@NotNull PsiElement parent, @NotNull Class<?>... psiClasses) {
        ArrayList<PsiElement> list = new ArrayList<>();

        PsiElement child = parent.getFirstChild();
        while (child != null) {
            for (Class<?> psiClass : psiClasses) {
                if (psiClass.isInstance(child)) {
                    list.add(child);
                }
            }
            child = child.getNextSibling();
        }
        return list;
    }

    @NotNull
    public static String truncateStringForDisplay(
            String text,
            int maxChars,
            boolean onWordBreak,
            boolean showEtc,
            boolean firstLineOnly
    ) {
        if (maxChars <= 0) return text;
        int useMaxChars = Math.min(maxChars, text.length());
        String truncated = text.substring(0, useMaxChars);
        boolean useShowEtc = text.length() > maxChars && showEtc && !text.substring(useMaxChars).trim().isEmpty();

        if (firstLineOnly) {
            int i = truncated.indexOf('\n');
            if (i >= 0) {
                truncated = truncated.substring(0, i);
                if (i <= maxChars) useShowEtc = false;
            }
        }

        if (onWordBreak) {
            int i = truncated.lastIndexOf(' ');
            if (i > 0) {
                truncated = truncated.substring(0, i);
            }
        }

        // trim off white space at end
        int i = truncated.length();

        while (i > 0) {
            char c = truncated.charAt(i - 1);
            if (!(c == ' ' || c == '\t' || c == '\n')) break;
            i--;
        }

        if (i < truncated.length()) {
            if (truncated.length() > maxChars && i <= maxChars) useShowEtc = false;
            truncated = truncated.substring(0, i);
        }

        return truncated + (useShowEtc ? "…" : "");
    }

    @NotNull
    @SafeVarargs
    public static <T extends PsiElement> List<T> listChildrenOfAnyType(
            @NotNull final MdFile file,
            boolean addOuterFile, boolean addIncludedFiles,
            boolean addIncludingFiles,
            @NotNull final Class<? extends T>... classes
    ) {
        return MdCachedFileElements.listChildrenOfAnyType(file, addOuterFile, addIncludedFiles, addIncludingFiles, classes);
    }

    @SafeVarargs
    public static <T extends PsiElement> void findChildrenOfAnyType(
            @NotNull final MdFile file,
            boolean addOuterFile, boolean addIncludedFiles,
            boolean addIncludingFiles,
            @NotNull Consumer<T> consumer,
            @NotNull final Class<? extends T>... classes
    ) {
        MdCachedFileElements.findChildrenOfAnyType(file, addOuterFile, addIncludedFiles, addIncludingFiles, classes, consumer);
    }

    @NotNull
    @SafeVarargs
    public static <T extends PsiElement> Result<T> findChildrenOfAnyType(
            @NotNull final MdFile file,
            boolean addOuterFile, boolean addIncludedFiles,
            boolean addIncludingFiles,
            @NotNull SourcedElementConsumer<T> consumer,
            @NotNull final Class<? extends T>... classes
    ) {
        return MdCachedFileElements.findChildrenOfAnyType(file, addOuterFile, addIncludedFiles, addIncludingFiles, classes, consumer);
    }

    public static boolean inScope(@NotNull SearchScope searchScope, @Nullable PsiFile psiFile) {
        return psiFile != null && inScope(searchScope, psiFile.getOriginalFile().getVirtualFile());
    }

    public static boolean inScope(@NotNull SearchScope searchScope, @Nullable VirtualFile virtualFile) {
        return virtualFile != null && searchScope.contains(virtualFile);
    }

    public static boolean isInjectedFile(@NotNull PsiFile file) {
        PsiElement context = file.getContext();
        return context instanceof MdPsiElement;
    }

    public static boolean isInjectedInFencedCode(@NotNull PsiFile file) {
        PsiElement context = file.getContext();
        return context instanceof MdVerbatim && !((MdVerbatim) context).getOpenMarker().isEmpty() ||
                context instanceof FlexmarkExampleSource || context instanceof FlexmarkExampleHtml || context instanceof FlexmarkExampleAst;
    }

    public static boolean isInjectedMdElement(@NotNull PsiFile file, boolean testFencedCode, boolean testFlexmarkExample) {
        PsiElement context = file.getContext();
        return testFencedCode && context instanceof MdVerbatim && !((MdVerbatim) context).getOpenMarker().isEmpty() ||
                testFlexmarkExample && (context instanceof FlexmarkExampleSource || context instanceof FlexmarkExampleHtml || context instanceof FlexmarkExampleAst);
    }

    @Nullable
    public static VirtualFile getVirtualFile(@Nullable PsiFile psiFile) {
        VirtualFile virtualFile = null;

        if (psiFile != null) {
            virtualFile = psiFile.getOriginalFile().getVirtualFile();

            if (virtualFile == null) {
                // parse contents light will have virtual file null but view provider virtual file will be light virtual which profile manager can handle
                virtualFile = psiFile.getViewProvider().getVirtualFile();
            }
        }
        return virtualFile;
    }

    @NotNull
    public static String getVirtualFilePath(@Nullable PsiFile psiFile) {
        VirtualFile virtualFile = null;

        if (psiFile != null) {
            virtualFile = psiFile.getOriginalFile().getVirtualFile();

            if (virtualFile == null) {
                // parse contents light will have virtual file null but view provider virtual file will be light virtual which profile manager can handle
                virtualFile = psiFile.getViewProvider().getVirtualFile();
            }
        }
        return virtualFile == null ? "" : virtualFile.getPath();
    }

    @NotNull
    public static PsiElement getLeafPsiElement(@NotNull PsiElement element) {
        if (element instanceof LeafPsiElement) return element;

        // get first leaf psi element from element
        PsiElement element1 = element.getFirstChild();
        while (element1 != null && !(element1 instanceof LeafPsiElement)) element1 = element1.getNextSibling();
        return element1 == null ? element : element1;
    }
}

