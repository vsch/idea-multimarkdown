// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ast.util.BlockVisitor;
import com.vladsch.flexmark.ast.util.BlockVisitorExt;
import com.vladsch.flexmark.ast.util.HtmlInnerVisitor;
import com.vladsch.flexmark.ast.util.HtmlInnerVisitorExt;
import com.vladsch.flexmark.ast.util.InlineVisitor;
import com.vladsch.flexmark.ast.util.InlineVisitorExt;
import com.vladsch.flexmark.ext.abbreviation.Abbreviation;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationBlock;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationVisitor;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationVisitorExt;
import com.vladsch.flexmark.ext.anchorlink.AnchorLink;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkVisitor;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkVisitorExt;
import com.vladsch.flexmark.ext.definition.DefinitionItem;
import com.vladsch.flexmark.ext.definition.DefinitionList;
import com.vladsch.flexmark.ext.definition.DefinitionTerm;
import com.vladsch.flexmark.ext.definition.DefinitionVisitor;
import com.vladsch.flexmark.ext.definition.DefinitionVisitorExt;
import com.vladsch.flexmark.ext.escaped.character.EscapedCharacter;
import com.vladsch.flexmark.ext.escaped.character.EscapedCharacterVisitor;
import com.vladsch.flexmark.ext.escaped.character.EscapedCharacterVisitorExt;
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughVisitor;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughVisitorExt;
import com.vladsch.flexmark.ext.gfm.strikethrough.Subscript;
import com.vladsch.flexmark.ext.gfm.strikethrough.SubscriptVisitor;
import com.vladsch.flexmark.ext.gfm.strikethrough.SubscriptVisitorExt;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListItem;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListItemVisitor;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListItemVisitorExt;
import com.vladsch.flexmark.ext.ins.Ins;
import com.vladsch.flexmark.ext.ins.InsVisitor;
import com.vladsch.flexmark.ext.ins.InsVisitorExt;
import com.vladsch.flexmark.ext.superscript.Superscript;
import com.vladsch.flexmark.ext.superscript.SuperscriptVisitor;
import com.vladsch.flexmark.ext.superscript.SuperscriptVisitorExt;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.tables.TableBody;
import com.vladsch.flexmark.ext.tables.TableCaption;
import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.ext.tables.TableHead;
import com.vladsch.flexmark.ext.tables.TableRow;
import com.vladsch.flexmark.ext.tables.TableSeparator;
import com.vladsch.flexmark.ext.tables.TableVisitor;
import com.vladsch.flexmark.ext.tables.TableVisitorExt;
import com.vladsch.flexmark.ext.typographic.TypographicQuotes;
import com.vladsch.flexmark.ext.typographic.TypographicSmarts;
import com.vladsch.flexmark.ext.typographic.TypographicVisitor;
import com.vladsch.flexmark.ext.typographic.TypographicVisitorExt;
import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.collection.iteration.ReversiblePeekingIterator;
import com.vladsch.flexmark.util.data.DataKeyBase;
import com.vladsch.flexmark.util.data.DataValueFactory;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.misc.Ref;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.Range;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.parser.api.MdParser;
import com.vladsch.md.nav.parser.api.MdParserExtension;
import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import com.vladsch.md.nav.parser.ast.MdASTLeafNode;
import com.vladsch.md.nav.parser.ast.MdASTNode;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.util.SegmentedRange;
import com.vladsch.plugin.util.HelpersKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;

import static com.intellij.openapi.diagnostic.Logger.getInstance;
import static com.vladsch.flexmark.util.misc.Utils.maxLimit;
import static com.vladsch.flexmark.util.misc.Utils.minLimit;
import static com.vladsch.md.nav.psi.util.MdTypes.*;

public class LexParserFlexmarkASTVisitor implements MdParser,
        AbbreviationVisitor,
        AnchorLinkVisitor,
        BlockVisitor,
        DefinitionVisitor,
        EscapedCharacterVisitor,
        HtmlInnerVisitor,
        InlineVisitor,
        InsVisitor,
        StrikethroughVisitor,
        SubscriptVisitor,
        SuperscriptVisitor,
        TableVisitor,
        TaskListItemVisitor,
        TypographicVisitor {
    private static final Logger LOG = getInstance("com.vladsch.md.nav.parser");
    private final LexParserState myLexParserState;

    int tableRows = 0;
    boolean tableHeader = false;
    int rowColumns = 0;
    int rowSepColumns = 0;
    boolean tableSeparator = false;
    ArrayList<SegmentedRange> parentRanges = new ArrayList<>();
    NodeVisitor myVisitor;
    boolean parseHtmlBlocks = !MdApplicationSettings.getInstance().getDocumentSettings().getHtmlLangInjections();
    private final int length;

    protected final ArrayList<LexerToken> tokens = new ArrayList<>(100);

    final private Document myDocument;

    @NotNull final private MdASTCompositeNode rootASTNode;  // current parent to be filled in by children
    @NotNull private MdASTCompositeNode currentASTNode;  // current parent to be filled in by children
    @NotNull private final Stack<MdASTCompositeNode> astNodeStack = new Stack<>();

    final private Map<IElementType, Map<IElementType, IElementType>> COMBINATION_SPLITS;
    final private Map<IElementType, IElementType> TEXT_TOKEN_TYPE_MAP;

    public LexParserFlexmarkASTVisitor(final Document document) {
        this.myDocument = document;
        this.length = document.getEndOffset();
        this.rootASTNode = new MdASTCompositeNode(MdParserDefinition.MARKDOWN_FILE, 0, this.length);
        this.currentASTNode = this.rootASTNode;
        myLexParserState = LexParserState.getInstance();

        LexParserState.State lexParserState = myLexParserState.getState();
        COMBINATION_SPLITS = lexParserState.COMBINATION_SPLITS;
        TEXT_TOKEN_TYPE_MAP = lexParserState.TEXT_TOKEN_TYPE_MAP;

        myVisitor = new NodeVisitor(
                AbbreviationVisitorExt.VISIT_HANDLERS(this),
                AnchorLinkVisitorExt.VISIT_HANDLERS(this),
                BlockVisitorExt.VISIT_HANDLERS(this),
                DefinitionVisitorExt.VISIT_HANDLERS(this),
                EscapedCharacterVisitorExt.VISIT_HANDLERS(this),
                HtmlInnerVisitorExt.VISIT_HANDLERS(this),
                InlineVisitorExt.VISIT_HANDLERS(this),
                InsVisitorExt.VISIT_HANDLERS(this),
                StrikethroughVisitorExt.VISIT_HANDLERS(this),
                SubscriptVisitorExt.VISIT_HANDLERS(this),
                SuperscriptVisitorExt.VISIT_HANDLERS(this),
                TableVisitorExt.VISIT_HANDLERS(this),
                TaskListItemVisitorExt.VISIT_HANDLERS(this),
                TypographicVisitorExt.VISIT_HANDLERS(this)
        );

        for (MdParserExtension extension : MdParserExtension.EXTENSIONS.getValue()) {
            try {
                extension.setFlexmarkHandlers(this);
            } catch (Throwable e) {
                LOG.error("MdParserExtension " + extension.getClass().getCanonicalName() + " caused an exception in setFlexmarkHandlers", e);
            }
        }
    }

    @Nullable
    @Override
    public <N extends Node> Visitor<Node> getVisitor(final Class<N> nodeClass) {
        return myVisitor.getAction(nodeClass);
    }

    @NotNull
    @Override
    public Document getDocument() {
        return myDocument;
    }

    public ArrayList<LexerToken> getTokens() {
        return tokens;
    }

    @NotNull
    public MdASTCompositeNode getRootASTNode() {
        return rootASTNode;
    }

    @Override
    public MdParser addHandlers(final VisitHandler<?>... handlers) {
        myVisitor.addHandlers(handlers);
        return this;
    }

    @Override
    public MdParser addHandlers(final VisitHandler<?>[]... handlers) {
        myVisitor.addHandlers(handlers);
        return this;
    }

    @Override
    public MdParser addHandlers(final Collection<VisitHandler<?>> handlers) {
        myVisitor.addTypedHandlers(handlers);
        return this;
    }

    @Override
    public Object getOrCompute(@NotNull DataKeyBase<?> key, @NotNull DataValueFactory<?> factory) {
        return myDocument.getOrCompute(key, factory);
    }

    public void build() {
        myVisitor.visit(myDocument);
    }

    @Override
    public void visitChildren(Node node) {
        myVisitor.visitChildren(node);
    }

    public void visit(Document node) {
        visitChildren(node);
    }

    @Override
    public void visit(Abbreviation node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, ABBREVIATED, NONE);
        nodes.addComposite(node.getChars(), ABBREVIATED_TEXT, ABBREVIATED_TEXT);
        addCompositeTokens(nodes);
    }

    @Override
    public void visit(AbbreviationBlock node) {
        /*
        openingMarker
        text
        closingMarker
        abbreviation
         */
        includeToTrailingEOL(node);
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, ABBREVIATION, NONE);
        nodes.addLeaf(node.getOpeningMarker(), ABBREVIATION_OPEN);
        if (!node.getText().isBlank()) {
            nodes.addComposite(node.getText(), ABBREVIATION_SHORT_TEXT, ABBREVIATION_SHORT_TEXT);
        }
        nodes.addLeaf(node.getClosingMarker(), ABBREVIATION_CLOSE);
        nodes.addComposite(node.getAbbreviation(), ABBREVIATION_EXPANDED_TEXT, ABBREVIATION_EXPANDED_TEXT);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(AnchorLink node) {

    }

    @Override
    public void visit(AutoLink node) {
        //textOpeningMarker,
        //text,
        //textClosingMarker,
        //linkOpeningMarker,
        //urlOpeningMarker,
        //url,
        //pageRef,
        //anchorMarker,
        //anchorRef,
        //urlClosingMarker,
        //titleOpeningMarker,
        //title,
        //titleClosingMarker,
        //linkClosingMarker

        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, AUTO_LINK, NONE);
        if (node.getOpeningMarker().isNotNull()) nodes.addLeaf(node.getOpeningMarker(), AUTO_LINK_OPEN);
        nodes.addComposite(node.getPageRef(), AUTO_LINK_REF, AUTO_LINK_REF);

        if (node.getAnchorMarker().isNotNull()) {
            nodes.addLeaf(node.getAnchorMarker(), AUTO_LINK_ANCHOR_MARKER);
            nodes.addComposite(node.getAnchorRef(), AUTO_LINK_ANCHOR, AUTO_LINK_ANCHOR);
        }

        if (node.getClosingMarker().isNotNull()) nodes.addLeaf(node.getClosingMarker(), AUTO_LINK_CLOSE);
        addCompositeTokens(nodes);
    }

    @Override
    public void visit(BlockQuote node) {
        // here some children will punch out the block quote's > because they span more than one line and know nothing
        // of indentations. So we have to punch out holes of every child's new line that starts with > and optional space
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, BLOCK_QUOTE, NONE);
        nodes.addLeaf(node.getOpeningMarker().extendByOneOfAny(CharPredicate.SPACE), BLOCK_QUOTE_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(BulletList node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, BULLET_LIST, NONE);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(OrderedList node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, ORDERED_LIST, NONE);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(BulletListItem node) {
        if (node.getFirstChild() == null) {
            // take the eol as part of the item
            includeToTrailingEOL(node);
        }
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, BULLET_LIST_ITEM, NONE);
        nodes.addLeaf(node.getOpeningMarker().extendByOneOfAny(CharPredicate.SPACE), BULLET_LIST_ITEM_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(TaskListItem node) {
        if (node.isOrderedItem()) {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, ORDERED_LIST_ITEM, NONE);
            nodes.addLeaf(node.getOpeningMarker().extendByOneOfAny(CharPredicate.SPACE), ORDERED_LIST_ITEM_MARKER);
            nodes.addLeaf(node.getMarkerSuffix().extendByOneOfAny(CharPredicate.SPACE), node.isItemDoneMarker() ? TASK_DONE_ITEM_MARKER : TASK_ITEM_MARKER);
            addCompositeTokensWithChildren(nodes);
        } else {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, BULLET_LIST_ITEM, NONE);
            nodes.addLeaf(node.getOpeningMarker().extendByOneOfAny(CharPredicate.SPACE), BULLET_LIST_ITEM_MARKER);
            nodes.addLeaf(node.getMarkerSuffix().extendByOneOfAny(CharPredicate.SPACE), node.isItemDoneMarker() ? TASK_DONE_ITEM_MARKER : TASK_ITEM_MARKER);
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(OrderedListItem node) {
        if (node.getFirstChild() == null) {
            // take the eol as part of the item
            includeToTrailingEOL(node);
        }
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, ORDERED_LIST_ITEM, NONE);
        nodes.addLeaf(node.getOpeningMarker().extendByOneOfAny(CharPredicate.SPACE), ORDERED_LIST_ITEM_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(Strikethrough node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, STRIKETHROUGH, STRIKETHROUGH_TEXT);
        nodes.addLeaf(node.getOpeningMarker(), STRIKETHROUGH_MARKER);
        //nodes.addComposite(node.getText(), NONE);
        nodes.addLeaf(node.getClosingMarker(), STRIKETHROUGH_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(Code node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, CODE, NONE);
        nodes.addLeaf(node.getOpeningMarker(), CODE_MARKER);
        nodes.addLeaf(node.getText(), CODE_TEXT);
        nodes.addLeaf(node.getClosingMarker(), CODE_MARKER);
        addCompositeTokens(nodes);
    }

    @Override
    public void visit(Emphasis node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, ITALIC, ITALIC_TEXT);
        nodes.addLeaf(node.getOpeningMarker(), ITALIC_MARKER);
        //nodes.addComposite(node.getText(), TEXT);
        nodes.addLeaf(node.getClosingMarker(), ITALIC_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(Ins node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, UNDERLINE, UNDERLINE_TEXT);
        nodes.addLeaf(node.getOpeningMarker(), UNDERLINE_MARKER);
        //nodes.addComposite(node.getText(), TEXT);
        nodes.addLeaf(node.getClosingMarker(), UNDERLINE_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(Superscript node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, SUPERSCRIPT, SUPERSCRIPT_TEXT);
        nodes.addLeaf(node.getOpeningMarker(), SUPERSCRIPT_MARKER);
        //nodes.addComposite(node.getText(), TEXT);
        nodes.addLeaf(node.getClosingMarker(), SUPERSCRIPT_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(Subscript node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, SUBSCRIPT, SUBSCRIPT_TEXT);
        nodes.addLeaf(node.getOpeningMarker(), SUBSCRIPT_MARKER);
        //nodes.addComposite(node.getText(), TEXT);
        nodes.addLeaf(node.getClosingMarker(), SUBSCRIPT_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(FencedCodeBlock node) {
        /*
        openingMarker
        info
        closingMarker
         */
        includeToTrailingEOL(node);

        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, VERBATIM, NONE);
        nodes.addLeaf(node.getOpeningMarker(), VERBATIM_OPEN);
        nodes.addComposite(node.getInfo().ifNullEmptyAfter(node.getOpeningMarker()), VERBATIM_LANG, VERBATIM_LANG);

        BasedSequence contentChars = node.getContentChars();
        BasedSequence openInfo = Node.spanningChars(node.getOpeningMarker(), node.getInfo());
        boolean trimLead = contentChars.countLeading(CharPredicate.EOL, 0, 1) > 0;
        BasedSequence eol = node.getChars().baseSubSequence(openInfo.getEndOffset(), HelpersKt.maxLimit(openInfo.getEndOffset() + (trimLead ? 2 : 1), node.getChars().getBaseSequence().length()));
        //BasedSequence trimmedContent = removeEOL(contentChars);
        //Node fencedCodeContent = new FencedCodeContent(contentChars);
        //node.appendChild(fencedCodeContent);
        nodes.addLeaf(eol.subSequence(0, HelpersKt.maxLimit(1, eol.length())), EOL);
        nodes.addComposite(contentChars, VERBATIM_CONTENT, VERBATIM_CONTENT);
        nodes.addLeaf(eol.subSequence(HelpersKt.maxLimit(1, eol.length())), EOL);
        nodes.addLeaf(node.getClosingMarker(), VERBATIM_CLOSE);
        nodes.addLeaf(node.getChars().endSequence(1), EOL);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(IndentedCodeBlock node) {
        includeLeadingIndent(node, 4);
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, VERBATIM, NONE);
        nodes.addComposite(node.getChars(), VERBATIM_CONTENT, VERBATIM_CONTENT);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(HardLineBreak node) {
        node.setChars(node.getChars().removeSuffix("\n"));
        addToken(node, LINE_BREAK_SPACES);
    }

    @Override
    public void visit(Heading node) {
        /*
        openingMarker
        text
        closingMarker
         */
        includeToTrailingEOL(node);

        if (node.isSetextHeading()) {
            // chop off after \n
            // split out the setext underline
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, SETEXT_HEADER, NONE);
            BasedSequence headingWithEOL = node.getText().extendByAnyNot(CharPredicate.EOL);
            nodes.addComposite(node.getText().trim(), HEADER_TEXT, HEADER_TEXT);
            nodes.addLeaf(headingWithEOL.trimmedEOL(), EOL);
            nodes.addLeaf(node.getClosingMarker(), HEADER_SETEXT_MARKER);
            addCompositeTokensWithChildren(nodes);
        } else {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, ATX_HEADER, NONE);
            nodes.addLeaf(node.getOpeningMarker(), HEADER_ATX_MARKER);
            nodes.addComposite(node.getText(), HEADER_TEXT, HEADER_TEXT);
            nodes.addLeaf(node.getClosingMarker(), ATX_HEADER);
            nodes.addLeaf(node.getChars().trimmedEOL(), EOL);
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(HtmlEntity node) {
        addToken(node, HTML_ENTITY);
    }

    @Override
    public void visit(HtmlInline node) {
        addToken(node, INLINE_HTML);
    }

    @Override
    public void visit(HtmlCommentBlock node) {
        addCommentComposite(node, new IElementType[] { BLOCK_COMMENT, BLOCK_COMMENT_OPEN, BLOCK_COMMENT_TEXT, BLOCK_COMMENT_CLOSE });
    }

    @Override
    public void visit(HtmlInlineComment node) {
        addCommentComposite(node, new IElementType[] { COMMENT, COMMENT_OPEN, COMMENT_TEXT, COMMENT_CLOSE });
    }

    @Override
    public void visit(HtmlInnerBlockComment node) {
        addCommentComposite(node, new IElementType[] { BLOCK_COMMENT, BLOCK_COMMENT_OPEN, BLOCK_COMMENT_TEXT, BLOCK_COMMENT_CLOSE });
    }

    private void addCommentComposite(final Node node, IElementType[] elementTypes) {
        node.setChars(node.getChars().removeSuffix("\n"));
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, elementTypes[0], NONE);
        // NOTE: CommonMark includes non-indenting whitespace as part of the comment text, we need to trim it off before taking open/close markers off
        BasedSequence nodeChars = node.getChars().trim();
        int length = nodeChars.length();
        // diagnostic/3037, start > end
        int startPos = maxLimit(length, MdPlainTextLexer.HTML_COMMENT_OPEN.length());
        int endPos = minLimit(startPos, length - MdPlainTextLexer.HTML_COMMENT_CLOSE.length());
        nodes.addLeaf(nodeChars.subSequence(0, startPos), elementTypes[1]);
        nodes.addLeaf(nodeChars.subSequence(startPos, endPos), elementTypes[2]);
        nodes.addLeaf(nodeChars.subSequence(endPos), elementTypes[3]);
        addCompositeTokens(nodes);
    }

    @Override
    public void visit(HtmlBlock node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, HTML_BLOCK, NONE);
        BasedSequence chars = node.getChars().removeSuffix("\n");
        if (!node.hasChildren() || !parseHtmlBlocks) {
            nodes.addLeaf(chars, HTML_BLOCK_TEXT);
            addCompositeTokens(nodes);
        } else {
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(HtmlInnerBlock node) {
        addToken(node, HTML_BLOCK_TEXT);
    }

    @Override
    public void visit(Image node) {
        //textOpeningMarker,
        //text,
        //textClosingMarker,
        //linkOpeningMarker,
        //urlOpeningMarker,
        //url,
        //urlClosingMarker,
        //titleOpeningMarker,
        //title,
        //titleClosingMarker,
        //linkClosingMarker

        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, IMAGE, NONE);
        nodes.addLeaf(node.getTextOpeningMarker(), IMAGE_LINK_REF_TEXT_OPEN);
        nodes.addComposite(node.getText(), IMAGE_LINK_REF_TEXT, NONE);
        nodes.addLeaf(node.getTextClosingMarker(), IMAGE_LINK_REF_TEXT_CLOSE);
        nodes.addLeaf(node.getLinkOpeningMarker(), IMAGE_LINK_REF_OPEN);
        if (!node.getUrlContent().isEmpty()) {
            BasedSequence url = node.getUrl().extendByAnyNot(CharPredicate.EOL);
            BasedSequence urlContent = node.getUrlContent().removeSuffix("\n");
            nodes.addComposite(url, IMAGE_LINK_REF, IMAGE_LINK_REF);
            nodes.addComposite(urlContent, IMAGE_URL_CONTENT, IMAGE_URL_CONTENT);
        } else {
            nodes.addComposite(node.getUrl(), IMAGE_LINK_REF, IMAGE_LINK_REF);
        }
        if (node.getTextOpeningMarker().isNotNull()) {
            nodes.addLeaf(node.getTitleOpeningMarker(), IMAGE_LINK_REF_TITLE_MARKER);
            nodes.addComposite(node.getTitle(), IMAGE_LINK_REF_TITLE, IMAGE_LINK_REF_TITLE);
            nodes.addLeaf(node.getTitleClosingMarker(), IMAGE_LINK_REF_TITLE_MARKER);
        }
        nodes.addLeaf(node.getLinkClosingMarker(), IMAGE_LINK_REF_CLOSE);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(ImageRef node) {
          /*
                textOpeningMarker,
                text,
                textClosingMarker,
                referenceOpeningMarker,
                reference,
                referenceClosingMarker
         */
        if (!node.isReferenceTextCombined()) {
            // comes with its own text [text] [reference]
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, REFERENCE_IMAGE, NONE);
            nodes.addLeaf(node.getTextOpeningMarker(), REFERENCE_IMAGE_TEXT_OPEN);
            nodes.addComposite(node.getText(), REFERENCE_IMAGE_TEXT, NONE);
            nodes.addLeaf(node.getTextClosingMarker(), REFERENCE_IMAGE_TEXT_CLOSE);
            nodes.addLeaf(node.getReferenceOpeningMarker(), REFERENCE_IMAGE_REFERENCE_OPEN);
            nodes.addComposite(node.getReference(), REFERENCE_IMAGE_REFERENCE, REFERENCE_IMAGE_REFERENCE_LEAF);
            nodes.addLeaf(node.getReferenceClosingMarker(), REFERENCE_IMAGE_REFERENCE_CLOSE);
            addCompositeTokensWithChildren(nodes);
        } else {
            // just reference key and text [reference]
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, REFERENCE_IMAGE, NONE);
            nodes.addLeaf(node.getReferenceOpeningMarker(), REFERENCE_IMAGE_REFERENCE_OPEN2);
            nodes.addComposite(node.getReference(), REFERENCE_IMAGE_REFERENCE, REFERENCE_IMAGE_REFERENCE_LEAF);
            nodes.addLeaf(node.getReferenceClosingMarker(), REFERENCE_IMAGE_REFERENCE_CLOSE2);
            if (node.isDummyReference()) nodes.addLeaf(node.getDummyReference(), DUMMY_REFERENCE);
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(Link node) {
        //textOpeningMarker,
        //text,
        //textClosingMarker,
        //linkOpeningMarker,
        //urlOpeningMarker,
        //url,
        //pageRef,
        //anchorMarker,
        //anchorRef,
        //urlClosingMarker,
        //titleOpeningMarker,
        //title,
        //titleClosingMarker,
        //linkClosingMarker

        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, EXPLICIT_LINK, NONE);
        nodes.addLeaf(node.getTextOpeningMarker(), LINK_REF_TEXT_OPEN);
        nodes.addComposite(node.getText(), LINK_REF_TEXT, NONE);
        nodes.addLeaf(node.getTextClosingMarker(), LINK_REF_TEXT_CLOSE);
        nodes.addLeaf(node.getLinkOpeningMarker(), LINK_REF_OPEN);
        nodes.addComposite(node.getPageRef(), LINK_REF, LINK_REF);

        if (node.getAnchorMarker().isNotNull()) {
            nodes.addLeaf(node.getAnchorMarker(), LINK_REF_ANCHOR_MARKER);
            nodes.addComposite(node.getAnchorRef(), LINK_REF_ANCHOR, LINK_REF_ANCHOR);
        }

        if (node.getTitleOpeningMarker().isNotNull()) {
            nodes.addLeaf(node.getTitleOpeningMarker(), LINK_REF_TITLE_MARKER);
            nodes.addComposite(node.getTitle(), LINK_REF_TITLE, LINK_REF_TITLE);
            nodes.addLeaf(node.getTitleClosingMarker(), LINK_REF_TITLE_MARKER);
        }

        nodes.addLeaf(node.getLinkClosingMarker(), LINK_REF_CLOSE);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(LinkRef node) {
        /*
                textOpeningMarker,
                text,
                textClosingMarker,
                referenceOpeningMarker,
                reference,
                referenceClosingMarker

         */
        if (!node.isReferenceTextCombined()) {
            // comes with its own text [text] [reference]
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, REFERENCE_LINK, NONE);
            nodes.addLeaf(node.getTextOpeningMarker(), REFERENCE_LINK_TEXT_OPEN);
            nodes.addComposite(node.getText(), REFERENCE_LINK_TEXT, NONE);
            nodes.addLeaf(node.getTextClosingMarker(), REFERENCE_LINK_TEXT_CLOSE);
            nodes.addLeaf(node.getReferenceOpeningMarker(), REFERENCE_LINK_REFERENCE_OPEN);
            nodes.addComposite(node.getReference(), REFERENCE_LINK_REFERENCE, REFERENCE_LINK_REFERENCE_LEAF);
            nodes.addLeaf(node.getReferenceClosingMarker(), REFERENCE_LINK_REFERENCE_CLOSE);
            addCompositeTokensWithChildren(nodes);
        } else {
            // just reference key and text [reference]
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, REFERENCE_LINK, NONE);
            nodes.addLeaf(node.getReferenceOpeningMarker(), REFERENCE_LINK_REFERENCE_OPEN2);
            nodes.addComposite(node.getReference(), REFERENCE_LINK_REFERENCE, REFERENCE_LINK_REFERENCE_LEAF);
            nodes.addLeaf(node.getReferenceClosingMarker(), REFERENCE_LINK_REFERENCE_CLOSE2);
            if (node.isDummyReference()) nodes.addLeaf(node.getDummyReference(), DUMMY_REFERENCE);
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(MailLink node) {
        addToken(node, MAIL_LINK);
    }

    @Override
    public void visit(Reference node) {
        /*
                openingMarker,
                reference,
                closingMarker,
                urlOpeningMarker,
                url,
                pageRef,
                anchorMarker,
                anchorRef,
                urlClosingMarker,
                titleOpeningMarker,
                title,
                titleClosingMarker

         */
        includeToTrailingEOL(node);
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, REFERENCE, NONE);
        nodes.addLeaf(node.getOpeningMarker(), REFERENCE_TEXT_OPEN);
        nodes.addComposite(node.getReference(), REFERENCE_TEXT, REFERENCE_TEXT_LEAF);
        nodes.addLeaf(node.getClosingMarker(), REFERENCE_TEXT_CLOSE);

        nodes.addComposite(node.getPageRef(), REFERENCE_LINK_REF, REFERENCE_LINK_REF);
        if (node.getAnchorMarker().isNotNull()) {
            nodes.addLeaf(node.getAnchorMarker(), REFERENCE_ANCHOR_MARKER);
            nodes.addComposite(node.getAnchorRef(), REFERENCE_ANCHOR, REFERENCE_ANCHOR);
        }

        if (node.getTitleOpeningMarker().isNotNull()) {
            nodes.addLeaf(node.getTitleOpeningMarker(), REFERENCE_TITLE_MARKER);
            nodes.addComposite(node.getTitle(), REFERENCE_TITLE, REFERENCE_TITLE);
            nodes.addLeaf(node.getTitleClosingMarker(), REFERENCE_TITLE_MARKER);
        }
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(Paragraph node) {
        //mergeText(node);
        Block parent = node.getParent();
        boolean isInTightListItem = parent instanceof ParagraphItemContainer && ((ParagraphItemContainer) parent).isParagraphInTightListItem(node);
        if (!isInTightListItem) {
            MdASTCompositeNode paragraphNode = pushCompositeNode(PARAGRAPH_BLOCK, node.getStartOffset(), node.getEndOffset());
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TEXT_BLOCK, NONE);
            addCompositeTokensWithChildren(nodes);
            popCompositeNode(paragraphNode);
        } else {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TEXT_BLOCK, NONE);
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(SoftLineBreak node) {

    }

    @Override
    public void visit(TableBlock node) {
        tableRows = 0;
        rowColumns = 0;
        rowSepColumns = 0;
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE, NONE);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(TableHead node) {
        if (node.getChars().isNotNull()) {
            tableRows = 0;
            tableHeader = true;
            tableSeparator = false;
            includeTrailingEOL(node);
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_HEADER, NONE);
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(TableSeparator node) {
        rowSepColumns = 0;
        tableSeparator = true;
        includeTrailingEOL(node);
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_SEPARATOR, TABLE_SEP_ROW_ODD);
        nodes.addLeaf(node.getChars().trimmedEOL(), EOL);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(TableBody node) {
        if (node.getChars().isNotNull()) {
            tableRows = 0;
            tableHeader = false;
            tableSeparator = false;
            includeTrailingEOL(node);
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_BODY, NONE);
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(TableCaption node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_CAPTION, TABLE_CAPTION_TEXT);
        nodes.addLeaf(node.getOpeningMarker(), TABLE_CAPTION_MARKER);
        //nodes.addComposite(node.getText(), TABLE_CAPTION_TEXT, NONE);
        nodes.addLeaf(node.getClosingMarker(), TABLE_CAPTION_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(TableRow node) {
        includeTrailingEOL(node);
        if (tableSeparator) {
            //SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_ROW, TABLE_SEP_ROW_ODD);
            //addCompositeTokensWithChildren(nodes);
            rowSepColumns = 0;
            visitChildren(node);
        } else {
            tableRows++;
            rowColumns = 0;
            IElementType type = tableHeader ? ((tableRows & 1) != 0 ? TABLE_HDR_ROW_ODD : TABLE_HDR_ROW_EVEN) : (tableRows & 1) != 0 ? TABLE_ROW_ODD : TABLE_ROW_EVEN;
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_ROW, type);
            nodes.addLeaf(node.getChars().trimmedEOL(), EOL);
            addCompositeTokensWithChildren(nodes);
        }
    }

    @Override
    public void visit(TableCell node) {
        if (tableSeparator) {
            rowSepColumns++;
            // expand to include pipe if it is there
            node.setChars(node.getText());
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_CELL, NONE);
            IElementType columnType = (rowSepColumns & 1) != 0 ? TABLE_SEP_COLUMN_ODD : TABLE_SEP_COLUMN_EVEN;
            nodes.addLeaf(node.getOpeningMarker(), NONE);
            nodes.addLeaf(node.getText(), columnType);
            nodes.addLeaf(node.getClosingMarker(), NONE);
            addCompositeTokens(nodes);
        } else {
            rowColumns++;
            node.setChars(node.getText());
            if (tableHeader) {
                IElementType tokenType = (tableRows & 1) != 0 ? ((rowColumns & 1) != 0 ? TABLE_HDR_CELL_RODD_CODD : TABLE_HDR_CELL_RODD_CEVEN)
                        : ((rowColumns & 1) != 0 ? TABLE_HDR_CELL_REVEN_CODD : TABLE_HDR_CELL_REVEN_CEVEN);
                SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_CELL, NONE);
                nodes.addLeaf(node.getOpeningMarker(), NONE);
                nodes.addLeaf(node.getText(), tokenType);
                nodes.addLeaf(node.getClosingMarker(), NONE);
                addCompositeTokensWithChildren(nodes);
            } else {
                IElementType tokenType = (tableRows & 1) != 0 ? ((rowColumns & 1) != 0 ? TABLE_CELL_RODD_CODD : TABLE_CELL_RODD_CEVEN)
                        : ((rowColumns & 1) != 0 ? TABLE_CELL_REVEN_CODD : TABLE_CELL_REVEN_CEVEN);
                SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, TABLE_CELL, NONE);
                nodes.addLeaf(node.getOpeningMarker(), NONE);
                nodes.addLeaf(node.getText(), tokenType);
                nodes.addLeaf(node.getClosingMarker(), NONE);
                addCompositeTokensWithChildren(nodes);
            }
            rowColumns += node.getSpan() - 1;
        }
    }

    @Override
    public void visit(StrongEmphasis node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, BOLD, BOLD_TEXT);
        nodes.addLeaf(node.getOpeningMarker(), BOLD_MARKER);
        //nodes.addComposite(node.getText(), NONE);
        nodes.addLeaf(node.getClosingMarker(), BOLD_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(Text node) {
        IElementType tokenType = processTextAncestors(TEXT);
        addToken(node, tokenType);
    }

    @Override
    public void visit(ThematicBreak node) {
        // we extend to include the EOL
        includeToTrailingEOL(node);
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, HRULE, NONE);
        nodes.addLeaf(node.getChars(), HRULE_TEXT);
        addCompositeTokens(nodes);
    }

    @Override
    public void visit(TypographicQuotes node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, QUOTE, NONE);
        nodes.addLeaf(node.getOpeningMarker(), QUOTE_MARKER);
        nodes.addLeaf(node.getText(), QUOTED_TEXT);
        nodes.addLeaf(node.getClosingMarker(), QUOTE_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void visit(TypographicSmarts node) {
        if (node.getChars().length() == 1) {
            addToken(node, SMARTS);
        } else {
            addToken(node, SMARTS);
        }
    }

    @Override
    public void visit(EscapedCharacter node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, SPECIAL, NONE);
        nodes.addLeaf(node.getOpeningMarker(), SPECIAL_TEXT_MARKER);
        nodes.addLeaf(node.getText(), SPECIAL_TEXT);
        addCompositeTokens(nodes);
    }

    public void visit(DefinitionList node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, DEFINITION_LIST, NONE);
        addCompositeTokensWithChildren(nodes);
    }

    public void visit(DefinitionTerm node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, DEFINITION_TERM_ELEMENT, DEFINITION_TERM);
        addCompositeTokensWithChildren(nodes);
    }

    public void visit(DefinitionItem node) {
        SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, DEFINITION, NONE);
        nodes.addLeaf(node.getOpeningMarker(), DEFINITION_MARKER);
        addCompositeTokensWithChildren(nodes);
    }

    @Override
    public void addCompositeTokens(@NotNull SyntheticFlexmarkNodes nodes) {
        MdASTCompositeNode astCompositeNode = pushCompositeNode(nodes);
        for (SyntheticFlexmarkNodes.SyntheticNode node : nodes.getNodes()) {
            if (node.isComposite()) {
                MdASTCompositeNode compositeNode = pushCompositeNode(node);
                addToken(node.getStartOffset(), node.getEndOffset(), node.getType(), nodes.getNodeType());
                popCompositeNode(compositeNode);
            } else {
                addToken(node.getStartOffset(), node.getEndOffset(), node.getType(), nodes.getNodeType());
            }
        }
        popCompositeNode(astCompositeNode);
    }

    // add synthetic nodes but also visit child nodes and knock out the synthetic nodes with the children
    // if childToVisit is not null then it will be used as the parent of the corresponding list to be punched out by the child list, otherwise the parent of all synthetic nodes will be used
    @Override
    public void addCompositeTokensWithChildren(@NotNull SyntheticFlexmarkNodes nodes) {
        MdASTCompositeNode astCompositeNode = pushCompositeNode(nodes);
        SegmentedRange parentRange = new SegmentedRange(nodes.getNodeStart(), nodes.getNodeEnd(), nodes.getNodeTokenType(), nodes.getNodeType());

        boolean excludeParent = parentRange.getTokenType() != NONE;

        ReversiblePeekingIterator<Node> children = nodes.getNode().getChildren().iterator();
        for (SyntheticFlexmarkNodes.SyntheticNode node : nodes.getNodes()) {
            if (node.getStartOffset() >= node.getEndOffset() && !node.isComposite()) continue;

            while (children.hasNext() && children.peek().getChars().getStartOffset() < node.getStartOffset()) {
                // process the child here
                parentRange = addChildToken(parentRange, children.next(), false);
            }

            SegmentedRange syntheticChildRange = parentRange.intersections(Range.of(node.getStartOffset(), node.getEndOffset()), node.getType(), nodes.getNodeType());
            if (node.isComposite()) {
                MdASTCompositeNode compositeNode = pushCompositeNode(node);
                if (children.hasNext() && children.peek().getChars().getStartOffset() < node.getEndOffset()) {
                    // process the children here, but only if the synthetic range is set to NONE, otherwise override the child node
                    while (children.hasNext() && children.peek().getChars().getStartOffset() < node.getEndOffset()) {
                        syntheticChildRange = addChildToken(syntheticChildRange, children.next(), true);
                    }
                    if (!syntheticChildRange.isEmpty()) addSegmentedToken(syntheticChildRange);
                } else {
                    if (!syntheticChildRange.isEmpty()) addSegmentedToken(syntheticChildRange);
                }
                popCompositeNode(compositeNode);
            } else {
                if (children.hasNext() && children.peek().getChars().getStartOffset() < node.getEndOffset()) {
                    // process the children here, but only if the synthetic range is set to NONE, otherwise override the child node
                    while (children.hasNext() && children.peek().getChars().getStartOffset() < node.getEndOffset()) {
                        syntheticChildRange = addChildToken(syntheticChildRange, children.next(), true);
                    }
                    if (!syntheticChildRange.isEmpty()) addSegmentedToken(syntheticChildRange);
                } else {
                    if (!syntheticChildRange.isEmpty()) addSegmentedToken(syntheticChildRange);
                }
            }
            // exclude the synthetic from the parent
            if (excludeParent) parentRange.exclude(syntheticChildRange);
        }

        final Ref<SegmentedRange> parentRangeRef = new Ref<>(parentRange);
        children.forEachRemaining(node -> parentRangeRef.value = addChildToken(parentRangeRef.value, node, false));
        parentRange = parentRangeRef.value;

        if (excludeParent && !parentRange.isEmpty()) {
            addSegmentedToken(parentRange);
        }
        popCompositeNode(astCompositeNode);
    }

    private void addCompositeChild(
            @NotNull SegmentedRange parentRange,
            @NotNull SyntheticFlexmarkNodes.SyntheticNode childToVisit,
            @NotNull Node node
    ) {
        SegmentedRange childRange = new SegmentedRange(childToVisit.getStartOffset(), childToVisit.getEndOffset(), childToVisit.getType(), childToVisit.getType());
        boolean excludeParent = parentRange.getTokenType() != NONE;
        if (excludeParent) parentRange.exclude(childRange);

        if (childToVisit.isComposite()) {
            MdASTCompositeNode compositeNode = pushCompositeNode(childToVisit);
            childRange = addChildToken(childRange, node, true);
            popCompositeNode(compositeNode);
        } else {
            childRange = addChildToken(childRange, node, true);
        }

        if (!childRange.isEmpty()) addSegmentedToken(childRange);
    }

    protected void addTokenWithChildren(Node node, IElementType tokenType) {
        addTokenWithChildren(node.getStartOffset(), node.getEndOffset(), tokenType, node);
    }

    protected void addTokenWithChildren(
            int startIndex,
            int endIndex,
            @NotNull IElementType tokenType,
            @NotNull Node node
    ) {
        // compensate for missing EOL at end of input causes pegdown to return a range past end of input
        // in this case IDEA ignores the range. :(
        if (endIndex > length) endIndex = length;

        if (startIndex < endIndex) {
            SegmentedRange parentRange = new SegmentedRange(startIndex, endIndex, tokenType, tokenType);
            SegmentedRange segmentedRange = addChildTokens(parentRange, node, true);
            // now add the parent
            if (!segmentedRange.isEmpty()) addSegmentedToken(segmentedRange);
        }
    }

    private SegmentedRange addChildTokens(
            @NotNull SegmentedRange parentRange,
            @NotNull Node node,
            boolean emptyIfNone
    ) {
        if (parentRange.getTokenType() != NONE) {
            parentRanges.add(parentRange);
            visitChildren(node);
            return popRange();
        } else {
            visitChildren(node);
            return emptyIfNone ? SegmentedRange.EMPTY : parentRange;
        }
    }

    SegmentedRange addChildToken(@NotNull SegmentedRange parentRange, @NotNull Node node, boolean emptyIfNone) {
        if (parentRange.getTokenType() != NONE) {
            parentRanges.add(parentRange);
            myVisitor.visit(node);
            return popRange();
        } else {
            myVisitor.visit(node);
            return emptyIfNone ? SegmentedRange.EMPTY : parentRange;
        }
    }

    protected SegmentedRange processAncestors(SegmentedRange childRange) {
        if (parentRanges.size() == 0) return childRange;

        IElementType tokenType = childRange.getTokenType();
        Map<IElementType, IElementType> typeHashMap = COMBINATION_SPLITS.get(tokenType);

        int iMax = parentRanges.size();
        for (int i = iMax; i-- > 0; ) {
            SegmentedRange parentRange = parentRanges.get(i);
            if (parentRange != null && !parentRange.isEmpty()) {
                // do the combination transformations here, these take priority over exclusions
                IElementType combinationType = typeHashMap != null ? typeHashMap.get(parentRange.getTokenType()) : null;
                if (combinationType != null) {
                    // we will create an intersection and make it punch through parents and this range
                    tokenType = combinationType;
                    typeHashMap = COMBINATION_SPLITS.get(tokenType);
                    parentRange.exclude(childRange);
                } else if (parentRange.isExcludedByChild(myLexParserState, childRange)) {
                    parentRange.exclude(childRange);
                } else if (childRange.isExcludedByParent(myLexParserState, parentRange)) {
                    if (childRange.exclude(parentRange).isEmpty()) break;
                }
            }
        }

        childRange = childRange.withTokenType(tokenType);
        return childRange;
    }

    protected IElementType processCombinationAncestors(IElementType tokenType, IElementType defaultType) {
        IElementType resultType = defaultType;
        Map<IElementType, IElementType> typeHashMap = COMBINATION_SPLITS.get(defaultType);
        if (typeHashMap == null) return resultType;

        IElementType combinationType;
        if (tokenType != TEXT) {
            // process the tokenType as if it was the parent because it will be
            combinationType = typeHashMap.get(tokenType);
            if (combinationType != null) {
                // we will create an intersection and make it punch through parents and this range
                resultType = combinationType;
                typeHashMap = COMBINATION_SPLITS.get(resultType);
                if (typeHashMap == null) return resultType;
            }
        }

        int iMax = parentRanges.size();
        if (iMax == 0) return resultType;

        for (int i = iMax; i-- > 0; ) {
            SegmentedRange parentRange = parentRanges.get(i);
            if (parentRange != null) {
                // do the combination transformations here, these take priority over exclusions
                combinationType = typeHashMap.get(parentRange.getTokenType());
                if (combinationType != null) {
                    // we will create an intersection and make it punch through parents and this range
                    resultType = combinationType;
                    typeHashMap = COMBINATION_SPLITS.get(resultType);
                    if (typeHashMap == null) break;
                }
            }
        }

        return resultType;
    }

    protected IElementType processTextAncestors(IElementType defaultType) {
        IElementType resultType = defaultType;
        Map<IElementType, IElementType> typeHashMap = null;
        IElementType combinationType;

        int iMax = parentRanges.size();
        if (iMax == 0) return resultType;

        for (int i = iMax; i-- > 0; ) {
            SegmentedRange parentRange = parentRanges.get(i);
            if (parentRange != null) {
                // do the combination transformations here, these take priority over exclusions
                if (typeHashMap == null) {
                    combinationType = parentRange.getTokenType();
                } else {
                    combinationType = typeHashMap.get(parentRange.getTokenType());
                }

                if (combinationType != null) {
                    resultType = combinationType;
                    typeHashMap = COMBINATION_SPLITS.get(combinationType);
                    if (typeHashMap == null) break;
                }
            }
        }

        if (resultType != null) {
            if (defaultType != TEXT) {
                Map<IElementType, IElementType> defaultTypeMap = COMBINATION_SPLITS.get(defaultType);
                if (defaultTypeMap != null) {
                    resultType = defaultTypeMap.get(resultType);
                }
            }
        }

        return resultType == null ? defaultType : resultType;
    }

    protected void addSegmentedToken(SegmentedRange segmentedRange) {
        if (parentRanges.size() > 0) segmentedRange = processAncestors(segmentedRange);
        IElementType tokenType = segmentedRange.getTokenType();
        if (tokenType == NONE) return;

        for (Range range : segmentedRange.getSegments()) {
            // now exclude from ancestors what is left by the children
            if (!range.isEmpty()) {
                if (TEXT_TOKEN_TYPE_MAP.containsKey(tokenType)) {
                    IElementType combinedType;
                    if (tokenType == TEXT) {
                        combinedType = processTextAncestors(tokenType);
                    } else {
                        combinedType = processCombinationAncestors(tokenType, tokenType);
                    }
                    tokens.add(new LexerToken(range, combinedType, parentRanges.size()));
                } else {
                    tokens.add(new LexerToken(range, tokenType, parentRanges.size()));
                }
            }
        }
    }

    @Override
    public void addToken(Node node, IElementType tokenType) {
        addToken(node.getStartOffset(), node.getEndOffset(), tokenType, tokenType);
    }

    @Override
    public void addToken(int startIndex, int endIndex, IElementType tokenType, IElementType originalTokenType) {
        // compensate for missing EOL at end of input causes pegdown to return a range past end of input
        // in this case IDEA ignores the whole range.
        if (endIndex > length) endIndex = length;

        if (startIndex < endIndex && tokenType != NONE) {
            SegmentedRange range = processAncestors(new SegmentedRange(startIndex, endIndex, tokenType, originalTokenType));
            tokenType = range.getTokenType();
            for (Range subRange : range.getSegments()) {
                if (!subRange.isEmpty()) tokens.add(new LexerToken(subRange, tokenType, parentRanges.size()));
            }
        }
    }

    void addASTNode(MdASTNode node) {
        // see if need to add whitespace
        //int lastEndOffset = currentASTNode.getChildren().size() > 0 ? currentASTNode.getChildren().get(currentASTNode.getChildren().size() - 1).getStartOffset() : currentASTNode.getStartOffset();
        //if (lastEndOffset < node.getStartOffset()) {
        //    // insert a Whitespace node
        //    MarkdownASTNode whiteSpaces = new MarkdownASTLeafNode(NONE, lastEndOffset, node.getStartOffset());
        //    currentASTNode.add(whiteSpaces);
        //}
        currentASTNode.add(node);
    }

    void addLeafNode(@NotNull IElementType elementType, int startOffset, int endOffset) {
        // see if need to add whitespace
        addASTNode(new MdASTLeafNode(elementType, startOffset, endOffset));
    }

    void pushCompositeNode(@NotNull MdASTCompositeNode node) {
        // see if need to add whitespace
        addASTNode(node);
        astNodeStack.push(currentASTNode);
        currentASTNode = node;
    }

    @NotNull
    MdASTCompositeNode pushCompositeNode(@NotNull IElementType elementType, int startOffset, int endOffset) {
        // see if need to add whitespace
        MdASTCompositeNode node = new MdASTCompositeNode(elementType, startOffset, endOffset);
        pushCompositeNode(node);
        return node;
    }

    @NotNull
    @Override
    public MdASTCompositeNode pushCompositeNode(@NotNull SyntheticFlexmarkNodes nodes) {
        return pushCompositeNode(nodes.getNodeType(), nodes.getNodeStart(), nodes.getNodeEnd());
    }

    @NotNull
    protected MdASTCompositeNode pushCompositeNode(@NotNull SyntheticFlexmarkNodes.SyntheticNode node) {
        IElementType type = node.getCompositeType();
        if (type == null) type = node.getType();
        return pushCompositeNode(type, node.getStartOffset(), node.getEndOffset());
    }

    @Override
    public void popCompositeNode(@NotNull MdASTCompositeNode node) {
        // see if need to add whitespace
        assert currentASTNode == node : "PopASTNode does not match current, node " + node + ", current " + currentASTNode;
        assert astNodeStack.size() > 0;

        currentASTNode = astNodeStack.pop();
    }

    SegmentedRange popRange() {
        assert parentRanges.size() > 0;
        return parentRanges.remove(parentRanges.size() - 1);
    }

    void addCompositeNode(@NotNull SyntheticFlexmarkNodes nodes) {
        MdASTCompositeNode astNode = new MdASTCompositeNode(nodes.getNodeType(), nodes.getNodeStart(), nodes.getNodeEnd());

        for (SyntheticFlexmarkNodes.SyntheticNode synthNode : nodes.getNodes()) {
            MdASTLeafNode astLeaf = new MdASTLeafNode(synthNode.getType(), synthNode.getStartOffset(), synthNode.getEndOffset());
            astNode.add(astLeaf);
        }

        currentASTNode.add(astNode);
    }

    void addLeafNode(@NotNull Node node, @NotNull IElementType elementType) {
        MdASTLeafNode astLeaf = new MdASTLeafNode(elementType, node.getStartOffset(), node.getEndOffset());
        currentASTNode.add(astLeaf);
    }

    @Override
    public void includeToTrailingEOL(Node node) {
        node.setChars(node.getChars().extendByAnyNot(CharPredicate.EOL));
    }

    @Override
    public void includeToTrailing(Node node, CharPredicate charSet) {
        node.setChars(node.getChars().extendByAnyNot(charSet));
    }

    @Override
    public void includeTrailingEOL(Node node) {
        includeTrailing(node, CharPredicate.EOL);
    }

    @Override
    public void includeTrailing(Node node, CharPredicate charSet) {
        node.setChars(node.getChars().extendByOneOfAny(charSet));
    }

    @Override
    public void includeLeadingIndent(Node node, int maxColumns) {
        node.setChars(node.getChars().prefixWithIndent(maxColumns));
    }
}
