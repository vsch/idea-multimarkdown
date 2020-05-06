// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.aside.AsideExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.escaped.character.EscapedCharacterExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.SubscriptExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.jira.converter.JiraConverterExtension;
import com.vladsch.flexmark.parser.ListOptions;
import com.vladsch.flexmark.parser.MutableListOptions;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.KeepType;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.misc.Extension;
import com.vladsch.flexmark.youtrack.converter.YouTrackConverterExtension;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.MdResourceResolverImpl;
import com.vladsch.md.nav.parser.api.HtmlPurpose;
import com.vladsch.md.nav.parser.api.MdParserExtension;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.parser.flexmark.MdNavigatorDiagramExtension;
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension;
import com.vladsch.md.nav.settings.HighlightPreviewType;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdPreviewSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.ParserOptions;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vladsch.md.nav.parser.Extensions.*;
import static com.vladsch.md.nav.parser.MdLexParser.COMMONMARK_LISTS;
import static com.vladsch.md.nav.parser.MdLexParser.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA;
import static com.vladsch.md.nav.parser.MdLexParser.GFM_TABLE_RENDERING;
import static com.vladsch.md.nav.parser.MdLexParser.GITHUB_LISTS;
import static com.vladsch.md.nav.parser.MdLexParser.HEADER_ID_NON_ASCII_TO_LOWERCASE;
import static com.vladsch.md.nav.parser.MdLexParser.HEADER_ID_NO_DUPED_DASHES;
import static com.vladsch.md.nav.parser.MdLexParser.HEADER_ID_REF_TEXT_TRIM_TRAILING_SPACES;
import static com.vladsch.md.nav.parser.MdLexParser.SPACE_IN_LINK_URLS;
import static com.vladsch.md.nav.parser.api.HtmlPurpose.EXPORT;
import static com.vladsch.md.nav.parser.api.HtmlPurpose.RENDER;
import static com.vladsch.md.nav.parser.api.ParserPurpose.HTML_MIME;
import static com.vladsch.md.nav.parser.api.ParserPurpose.JAVAFX;
import static com.vladsch.md.nav.parser.api.ParserPurpose.JIRA;
import static com.vladsch.md.nav.parser.api.ParserPurpose.PARSER;
import static com.vladsch.md.nav.parser.api.ParserPurpose.YOU_TRACK;

public class PegdownOptionsAdapter {

    final private MdParserOptions options;
    private @Nullable Integer myPegdownExtensions = null;
    private @Nullable Long myParserOptions = null;

    public PegdownOptionsAdapter() {
        this(null);
    }

    public PegdownOptionsAdapter(@Nullable DataHolder dataSet) {
        options = new MdParserOptions(dataSet);
    }

    public PegdownOptionsAdapter(int pegdownExtensions, long parserOptions) {
        this(null);
        myPegdownExtensions = pegdownExtensions;
        myParserOptions = parserOptions;
    }

    public PegdownOptionsAdapter withExtensions(Extension... extensions) {
        options.addExtensions(extensions);
        return this;
    }

    public static DataHolder flexmarkOptions(
            int pegdownExtensions,
            long parserOptions,
            @NotNull MdRenderingProfile renderingProfile
    ) {
        PegdownOptionsAdapter optionsAdapter = new PegdownOptionsAdapter(pegdownExtensions, parserOptions);
        return optionsAdapter.getFlexmarkOptions(PARSER, RENDER, null, renderingProfile);
    }

    public boolean haveExtensions(int mask) {
        return myPegdownExtensions != null && (myPegdownExtensions & mask) != 0;
    }

    public boolean haveOptions(long mask) {
        return myParserOptions != null && (myParserOptions & mask) != 0L;
    }

    public DataHolder getFlexmarkOptions(
            @NotNull ParserPurpose parserPurpose,
            @NotNull HtmlPurpose htmlPurpose,
            @Nullable MdLinkResolver linkResolver,
            @NotNull MdRenderingProfile useRenderingProfile
    ) {
        return getFlexmarkParserOptions(parserPurpose, htmlPurpose, linkResolver, useRenderingProfile).getOptions();
    }

    public MdParserOptions getFlexmarkParserOptions(
            @NotNull ParserPurpose parserPurpose,
            @NotNull HtmlPurpose htmlPurpose,
            @Nullable MdLinkResolver linkResolver,
            @NotNull MdRenderingProfile renderingProfile
    ) {
        if (myParserOptions != null) renderingProfile.getParserSettings().setOptionsFlags(myParserOptions);
        if (myPegdownExtensions != null) renderingProfile.getParserSettings().setPegdownFlags(myPegdownExtensions);

        RenderingOptions renderingOptions = new RenderingOptions(parserPurpose, htmlPurpose, renderingProfile, linkResolver);
        for (MdParserExtension extension : MdParserExtension.EXTENSIONS.getValue()) {
            extension.setRenderingOptions(renderingOptions);
        }

        myPegdownExtensions = renderingProfile.getParserSettings().getPegdownFlags();
        myParserOptions = renderingProfile.getParserSettings().getOptionsFlags();

        MdPreviewSettings previewSettings = renderingProfile.getPreviewSettings();
        MdHtmlSettings htmlSettings = renderingProfile.getHtmlSettings();
        boolean forHtmlExport = htmlPurpose == EXPORT;

//            myOptions.clear();
        if (haveOptions(MdLexParser.JIRA_CONVERSION))
            parserPurpose = JIRA;

        if (haveOptions(MdLexParser.YOU_TRACK_CONVERSION))
            parserPurpose = YOU_TRACK;

        // don't unescape html entities
        options.set(HtmlRenderer.UNESCAPE_HTML_ENTITIES, false);
        options.set(Parser.SPACE_IN_LINK_URLS, haveOptions(SPACE_IN_LINK_URLS));

        if (parserPurpose == JIRA) {
            options.addExtension(JiraConverterExtension.class, JiraConverterExtension::create);
        } else if (parserPurpose == YOU_TRACK) {
            options.addExtension(YouTrackConverterExtension.class, YouTrackConverterExtension::create);
        }

        //options.set(Parser.PARSE_INLINE_ANCHOR_LINKS, true);
        options.set(Parser.PARSE_INNER_HTML_COMMENTS, true);
        options.set(Parser.INDENTED_CODE_NO_TRAILING_BLANK_LINES, true);
        //options.set(Parser.PARSE_GITHUB_ISSUE_MARKER, true);
        options.set(HtmlRenderer.SUPPRESS_HTML_BLOCKS, haveExtensions(SUPPRESS_HTML_BLOCKS));
        options.set(HtmlRenderer.SUPPRESS_INLINE_HTML, haveExtensions(SUPPRESS_INLINE_HTML));

        // add default extensions in pegdown
        if (parserPurpose != JIRA) options.addExtension(EscapedCharacterExtension.class, EscapedCharacterExtension::create);

        if (parserPurpose != JIRA && parserPurpose != YOU_TRACK && haveExtensions(ASIDE)) {
            options.addExtension(AsideExtension.class, AsideExtension::create);
            // NOTE: aside will follow block quote options by default
            //options.set(AsideExtension.EXTEND_TO_BLANK_LINE, true);
            //if (purpose == PARSER) options.set(AsideExtension.IGNORE_BLANK_LINE, true);
        }

        // setup list options: Fixed, CommonMark or GitHub, with GitHub docs and GitHub comments
        if (haveOptions(GITHUB_LISTS)) {
            // Setup List Options for GitHub profile which is kramdown for documents
            options.setFrom(ParserEmulationProfile.GITHUB_DOC);

            if (!haveOptions(GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA)) {
                // disable list item loose when blank line follows item paragraph and non-list children
                options
                        .set(Parser.LISTS_LOOSE_WHEN_HAS_NON_LIST_CHILDREN, false)
                        .set(Parser.LISTS_LOOSE_WHEN_BLANK_LINE_FOLLOWS_ITEM_PARAGRAPH, false);
            }

            // Setup Block Quote Options
            options.set(Parser.BLOCK_QUOTE_EXTEND_TO_BLANK_LINE, true);
            options.set(Parser.BLOCK_QUOTE_IGNORE_BLANK_LINE, true);
        } else if (haveOptions(COMMONMARK_LISTS)) {
            if (haveOptions(GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA)) {
                // set old github loose rules
                MutableListOptions listOptions = new MutableListOptions()
                        .setAutoLoose(false)
                        .setLooseWhenBlankLineFollowsItemParagraph(true)
                        .setLooseWhenHasLooseSubItem(true)
                        .setLooseWhenHasTrailingBlankLine(true)
                        .setLooseWhenPrevHasTrailingBlankLine(true)
                        .setLooseWhenContainsBlankLine(false)
                        .setLooseWhenHasNonListChildren(true);
                listOptions.setIn(options);
            }
        } else {
            // Setup List Options for Fixed List Indent profile
            options.setFrom(ParserEmulationProfile.FIXED_INDENT);

            // Setup Block Quote Options
            options.set(Parser.BLOCK_QUOTE_EXTEND_TO_BLANK_LINE, true);
            options.set(Parser.BLOCK_QUOTE_IGNORE_BLANK_LINE, true);
        }

        if (parserPurpose == PARSER) {
            //options.set(Parser.LISTS_EMPTY_BULLET_ITEM_INTERRUPTS_ITEM_PARAGRAPH, true);
            // set all bullets interrupt to prevent wrap on typing merging lists
            MutableListOptions listOptions = new MutableListOptions(options);
            ((ListOptions.MutableItemInterrupt) listOptions.getItemInterrupt())
                    .setBulletItemInterruptsParagraph(true)
                    .setOrderedItemInterruptsParagraph(true)
                    .setOrderedNonOneItemInterruptsParagraph(true)
                    .setEmptyBulletItemInterruptsParagraph(true)
                    .setEmptyOrderedItemInterruptsParagraph(true)
                    .setEmptyOrderedNonOneItemInterruptsParagraph(true)
                    .setBulletItemInterruptsItemParagraph(true)
                    .setOrderedItemInterruptsItemParagraph(true)
                    .setOrderedNonOneItemInterruptsItemParagraph(true)
                    .setEmptyBulletItemInterruptsItemParagraph(true)
                    .setEmptyOrderedItemInterruptsItemParagraph(true)
                    .setEmptyOrderedNonOneItemInterruptsItemParagraph(true)
                    .setEmptyBulletSubItemInterruptsItemParagraph(true)
                    .setEmptyOrderedSubItemInterruptsItemParagraph(true)
                    .setEmptyOrderedNonOneSubItemInterruptsItemParagraph(true)
            ;

            listOptions
                    .setItemMarkerSpace(false)
                    .setDelimiterMismatchToNewList(false)
            ;

            options.setFrom(listOptions);

            if (haveExtensions(INTELLIJ_DUMMY_IDENTIFIER)) {
                options.set(Parser.INTELLIJ_DUMMY_IDENTIFIER, true);
            }

            // NOTE: need this or inserting a blank line, like before ENTER handler will
            //   cause a list in block quotes to be split and not properly renumbered.
            //   However, setting this will mess up formatting because block quotes and aside block
            //   will be combined
            //options.set(Parser.BLOCK_QUOTE_IGNORE_BLANK_LINE, true);
        }

        if (haveExtensions(Extensions.MULTI_LINE_IMAGE_URLS)) {
            options.set(Parser.PARSE_MULTI_LINE_IMAGE_URLS, true);
        }

        if (parserPurpose != JIRA && haveExtensions(ABBREVIATIONS)) {
            options.addExtension(AbbreviationExtension.class, AbbreviationExtension::create);
            // NOTE: references last causes arranging unused last to toggle
            options.set(AbbreviationExtension.ABBREVIATIONS_KEEP, KeepType.FIRST);
        }

        if (parserPurpose != JIRA && parserPurpose != YOU_TRACK && parserPurpose != HTML_MIME) {
            if (haveExtensions(ANCHORLINKS | EXTANCHORLINKS | EXTANCHORLINKS_WRAP)) {
                options.addExtension(AnchorLinkExtension.class, AnchorLinkExtension::create);
                if (haveExtensions(EXTANCHORLINKS) || parserPurpose == PARSER && haveExtensions(ANCHORLINKS)) {
                    options.set(AnchorLinkExtension.ANCHORLINKS_WRAP_TEXT, haveExtensions(EXTANCHORLINKS_WRAP) && parserPurpose != PARSER);
                } else if (haveExtensions(ANCHORLINKS)) {
                    options.set(AnchorLinkExtension.ANCHORLINKS_WRAP_TEXT, true);
                }
            }
        }

        if (haveExtensions(AUTOLINKS)) {
            options.addExtension(AutolinkExtension.class, AutolinkExtension::create);
        }

        if (haveExtensions(HTML_DEEP_PARSER)) {
            options.set(Parser.HTML_BLOCK_DEEP_PARSER, true);
        }

        if (haveExtensions(DEFINITIONS)) {
            // not implemented yet, but have placeholder
            options.addExtension(DefinitionExtension.class, DefinitionExtension::create);

            if (haveExtensions(DEFINITION_BREAK_DOUBLE_BLANK_LINE)) {
                options.set(DefinitionExtension.DOUBLE_BLANK_LINE_BREAKS_LIST, true);
            }
        }

        if (haveExtensions(HARDWRAPS)) {
            options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
            options.set(HtmlRenderer.HARD_BREAK, "<br />\n<br />\n");
        }

        if (!haveExtensions(ATXHEADERSPACE)) {
            options.set(Parser.HEADING_NO_ATX_SPACE, true);
        } else {
            if (parserPurpose == PARSER) {
                options.set(Parser.HEADING_NO_EMPTY_HEADING_WITHOUT_SPACE, true);
            }
        }
        options.set(Parser.HEADING_NO_LEAD_SPACE, true);
        options.set(Parser.ESCAPE_HEADING_NO_ATX_SPACE, true);
        options.set(HtmlRenderer.HEADER_ID_GENERATOR_NO_DUPED_DASHES, haveOptions(HEADER_ID_NO_DUPED_DASHES));
        options.set(HtmlRenderer.HEADER_ID_REF_TEXT_TRIM_TRAILING_SPACES, haveOptions(HEADER_ID_REF_TEXT_TRIM_TRAILING_SPACES));

        // Issue: #567, underscores preserved in ids
        options.set(HtmlRenderer.HEADER_ID_GENERATOR_TO_DASH_CHARS, " -");
        options.set(HtmlRenderer.HEADER_ID_GENERATOR_NON_DASH_CHARS, "_");

        if (parserPurpose == PARSER) {
            // 3 for pegdown compatibility, 1 for commonmark, something else for GFM which will take 1 without trailing spaces if in a list, outside a list 1 or 2+ with spaces even if in a list
            options.set(Parser.HEADING_SETEXT_MARKER_LENGTH, 3);
        }

        if (haveExtensions(QUOTES | SMARTS)) {
            // not implemented yet, have placeholder
            options.addExtension(TypographicExtension.class, TypographicExtension::create);
            options.set(TypographicExtension.ENABLE_SMARTS, haveExtensions(SMARTS));
            options.set(TypographicExtension.ENABLE_QUOTES, haveExtensions(QUOTES));
        }

        if (!haveExtensions(RELAXEDHRULES)) {
            options.set(Parser.THEMATIC_BREAK_RELAXED_START, false);
        }

        if (haveExtensions(SUBSCRIPT) && haveExtensions(STRIKETHROUGH)) {
            // first item is loose if second item is loose
            options.addExtension(StrikethroughSubscriptExtension.class, StrikethroughSubscriptExtension::create);
        } else if (haveExtensions(STRIKETHROUGH)) {
            options.addExtension(StrikethroughExtension.class, StrikethroughExtension::create);
        } else if (haveExtensions(SUBSCRIPT)) {
            options.addExtension(SubscriptExtension.class, SubscriptExtension::create);
        }

        if (haveExtensions(SUPERSCRIPT)) {
            options.addExtension(SuperscriptExtension.class, SuperscriptExtension::create);
        }

        if (haveExtensions(INSERTED)) {
            options.addExtension(InsExtension.class, InsExtension::create);
        }

        if (haveExtensions(TABLES)) {
            options.addExtension(TablesExtension.class, TablesExtension::create);
            options.set(TablesExtension.TRIM_CELL_WHITESPACE, false);
            options.set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, false);
            if (parserPurpose != PARSER) {
                options.set(TablesExtension.WITH_CAPTION, !haveOptions(ParserOptions.GFM_TABLE_RENDERING.getFlags()));
            }
        }

        if (haveExtensions(TASKLISTITEMS)) {
            options.addExtension(TaskListExtension.class, TaskListExtension::create);
            options.set(Parser.LISTS_ITEM_CONTENT_AFTER_SUFFIX, false);
        }

        // make GitHub compatible by default
        options.set(HtmlRenderer.HEADER_ID_GENERATOR_NON_ASCII_TO_LOWERCASE, haveOptions(HEADER_ID_NON_ASCII_TO_LOWERCASE));

        // NOTE: references last causes arranging unused last to toggle
        options.set(Parser.REFERENCES_KEEP, KeepType.FIRST);

        if (!haveExtensions(FENCED_CODE_BLOCKS)) {
            // disable fenced code blocks
            options.set(Parser.FENCED_CODE_BLOCK_PARSER, false);
        } else {
            // FIX: this needs to be true for GitHub compatibility
            options.set(Parser.MATCH_CLOSING_FENCE_CHARACTERS, false);
            options.addExtension(MdNavigatorDiagramExtension.class, MdNavigatorDiagramExtension::create);
            MdNavigatorDiagramExtension.addRenderingProfileOptions(options, renderingProfile);
        }

        if (parserPurpose == JAVAFX) {
            // set rendering options for JavaFX
            // set to true if java fx, else false
            options.set(MdNavigatorExtension.USE_IMAGE_SERIALS, renderingProfile.getHtmlSettings().getImageUriSerials());
            options.set(HtmlRenderer.INDENT_SIZE, 2);

            if (haveExtensions(TABLES) && haveOptions(GFM_TABLE_RENDERING)) {
                options.set(TablesExtension.COLUMN_SPANS, false)
                        .set(TablesExtension.MIN_HEADER_ROWS, 1)
                        .set(TablesExtension.MAX_HEADER_ROWS, 1)
                        .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                        .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                        .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
            }

            if (!forHtmlExport && previewSettings.getSynchronizePreviewPosition()) {
                options.set(HtmlRenderer.SOURCE_POSITION_ATTRIBUTE, MdNavigatorExtension.SOURCE_POSITION_ATTRIBUTE_NAME);
                options.set(HtmlRenderer.SOURCE_POSITION_PARAGRAPH_LINES, previewSettings.getHighlightPreviewTypeEnum() == HighlightPreviewType.LINE);
                options.set(HtmlRenderer.INLINE_CODE_SPLICE_CLASS, "line-spliced");
                options.set(Parser.CODE_SOFT_LINE_BREAKS, previewSettings.getHighlightPreviewTypeEnum() == HighlightPreviewType.LINE);

                options.set(HtmlRenderer.SOURCE_WRAP_HTML, true);
            }

            if (haveExtensions(FENCED_CODE_BLOCKS)) {
                options.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "");
            }

            if (haveExtensions(ANCHORLINKS | EXTANCHORLINKS | EXTANCHORLINKS_WRAP)) {
                options.set(AnchorLinkExtension.ANCHORLINKS_SET_ID, false);
                options.set(AnchorLinkExtension.ANCHORLINKS_ANCHOR_CLASS, "anchor");
                options.set(AnchorLinkExtension.ANCHORLINKS_SET_NAME, true);
                options.set(AnchorLinkExtension.ANCHORLINKS_TEXT_PREFIX, "<span class=\"octicon octicon-link\"></span>");
            }

            if (haveExtensions(TASKLISTITEMS)) {
                options.set(TaskListExtension.ITEM_DONE_MARKER, "<span class=\"task-item-closed\" style=\"cursor:hand\"></span>");
                options.set(TaskListExtension.ITEM_NOT_DONE_MARKER, "<span class=\"task-item-open\" style=\"cursor:hand\"></span>");
            }

            options.set(HtmlRenderer.RENDER_HEADER_ID, true);
        } else if (parserPurpose == ParserPurpose.SWING) {
            // set rendering options for Swing
            options.set(HtmlRenderer.INDENT_SIZE, 2);

            // QUERY: disable because swing does not use it or leave it in
            if (!forHtmlExport && previewSettings.getSynchronizePreviewPosition()) {
                options.set(HtmlRenderer.SOURCE_POSITION_ATTRIBUTE, MdNavigatorExtension.SOURCE_POSITION_ATTRIBUTE_NAME);
                options.set(HtmlRenderer.SOURCE_POSITION_PARAGRAPH_LINES, previewSettings.getHighlightPreviewTypeEnum() == HighlightPreviewType.LINE);
                options.set(HtmlRenderer.INLINE_CODE_SPLICE_CLASS, "line-spliced");
                options.set(Parser.CODE_SOFT_LINE_BREAKS, previewSettings.getHighlightPreviewTypeEnum() == HighlightPreviewType.LINE);
                options.set(HtmlRenderer.SOURCE_WRAP_HTML, true);
            }

            if (haveExtensions(TABLES) && haveOptions(GFM_TABLE_RENDERING)) {
                options.set(TablesExtension.COLUMN_SPANS, false)
                        .set(TablesExtension.MIN_HEADER_ROWS, 1)
                        .set(TablesExtension.MAX_HEADER_ROWS, 1)
                        .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                        .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                        .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
            }

            // set to true if java fx, else false
            options.set(MdNavigatorExtension.USE_IMAGE_SERIALS, true);

            if (haveExtensions(FENCED_CODE_BLOCKS)) {
                options.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "");
            }

            if (haveExtensions(ANCHORLINKS | EXTANCHORLINKS | EXTANCHORLINKS_WRAP)) {
                options.set(AnchorLinkExtension.ANCHORLINKS_SET_ID, false);
                options.set(AnchorLinkExtension.ANCHORLINKS_ANCHOR_CLASS, "");
                options.set(AnchorLinkExtension.ANCHORLINKS_SET_NAME, true);
                options.set(AnchorLinkExtension.ANCHORLINKS_TEXT_PREFIX, "");
            }

            if (haveExtensions(TASKLISTITEMS)) {
                if (MdApplicationSettings.getInstance().getDebugSettings().getTaskItemImages()) {
                    boolean isDark = renderingProfile.getCssSettings().isDarkTheme();
                    String doneMarker = MdResourceResolverImpl.getInstance().resourceFileURL(isDark ? MdPlugin.TASK_ITEM_DONE_DARK : MdPlugin.TASK_ITEM_DONE, MdPlugin.class);
                    String notDoneMarker = MdResourceResolverImpl.getInstance().resourceFileURL(isDark ? MdPlugin.TASK_ITEM_DARK : MdPlugin.TASK_ITEM, MdPlugin.class);

                    options.set(TaskListExtension.ITEM_DONE_MARKER, "<img class='task-img' src='" + doneMarker + "' width='12'>&nbsp;");
                    options.set(TaskListExtension.ITEM_NOT_DONE_MARKER, "<img class='task-img' src='" + notDoneMarker + "' width='12'>&nbsp;");
                    options.set(Parser.LISTS_ITEM_CONTENT_AFTER_SUFFIX, false);
                } else {
                    options.set(TaskListExtension.ITEM_DONE_MARKER, "");
                    options.set(TaskListExtension.ITEM_NOT_DONE_MARKER, "");
                }
            }

            options.set(HtmlRenderer.RENDER_HEADER_ID, true);

            options.set(MdNavigatorExtension.USE_SWING_ATTRIBUTES, true);
            options.set(MdNavigatorExtension.IS_WIKI_PAGE, linkResolver != null && linkResolver.getContainingFile().isWikiPage());
        } else if (parserPurpose == ParserPurpose.HTML || parserPurpose == ParserPurpose.HTML_MIME) {
            // set rendering options for HTML and HTML_MIME
            options.set(MdNavigatorExtension.USE_IMAGE_SERIALS, false);
            options.set(HtmlRenderer.INDENT_SIZE, 2);

            if (haveExtensions(FENCED_CODE_BLOCKS)) {
                options.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "");
            }

            if (haveExtensions(TABLES) && haveOptions(GFM_TABLE_RENDERING)) {
                options.set(TablesExtension.COLUMN_SPANS, false)
                        .set(TablesExtension.MIN_HEADER_ROWS, 1)
                        .set(TablesExtension.MAX_HEADER_ROWS, 1)
                        .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                        .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                        .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
            }

            options.set(HtmlRenderer.RENDER_HEADER_ID, false);
            options.set(HtmlRenderer.GENERATE_HEADER_ID, true);
        }

        if (linkResolver != null) options.set(MdNavigatorExtension.LINK_RESOLVER, () -> linkResolver);

        options.addExtension(MdNavigatorExtension.class, MdNavigatorExtension::create);

        // add extensions last
        options.setExtensionOptions(parserPurpose, htmlPurpose, renderingProfile, linkResolver);
        for (MdParserExtension extension : MdParserExtension.EXTENSIONS.getValue()) {
            extension.setFlexmarkOptions(options);
        }

        return options;
    }
}
