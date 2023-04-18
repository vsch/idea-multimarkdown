// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package icons;

import com.intellij.ui.JBColor;
import com.vladsch.md.nav.settings.DocumentIconTypes;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.plugin.util.HelpersKt;
import com.vladsch.plugin.util.ui.Helpers;

import javax.swing.Icon;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class MdIcons {
    public static final String MULTIMARKDOWN = "Markdown";
    public static final String WIKIPAGE = "WikiPage";
    public static final String FLEXMARK = "FlexmarkSpec";
    public static final String[] MARKDOWN_CATEGORY = new String[] { MULTIMARKDOWN };
    public static final String[] WIKIPAGE_CATEGORY = new String[] { WIKIPAGE };
    public static final String[] FLEXMARK_SPEC_CATEGORY = new String[] { FLEXMARK };

    public static class Document {
        public static final Icon WIKI_OVERLAY = load("/icons/svg/overlay-markdown.svg");
        public static final Icon WIKI_OVERLAY_ALT = load("/icons/svg/overlay-markdown1.svg");
        public static final Icon MARKDOWN_NAVIGATOR_LOGO = load("/icons/svg/pluginIcon.svg");
        public static final Icon FILE = load("/icons/svg/application-markdown.svg");
        public static final Icon HIDDEN_FILE = load("/icons/svg/application-markdown-hidden.svg");
        public static final Icon MULTI_FILE = load("/icons/svg/application-markdown-multi.svg");
        public static final Icon FILE_ALT = load("/icons/svg/application-markdown1.svg");
        public static final Icon HIDDEN_FILE_ALT = load("/icons/svg/application-markdown1-hidden.svg");
        public static final Icon MULTI_FILE_ALT = load("/icons/svg/application-markdown1-multi.svg");
        public static final Icon WIKI = load("/icons/svg/application-wikipage.svg");
        public static final Icon HIDDEN_WIKI = load("/icons/svg/application-wikipage-hidden.svg");
        public static final Icon MULTI_WIKI = load("/icons/svg/application-wikipage-multi.svg");
        public static final Icon WIKI_ALT = load("/icons/svg/application-wikipage1.svg");
        public static final Icon HIDDEN_WIKI_ALT = load("/icons/svg/application-wikipage1-hidden.svg");
        public static final Icon MULTI_WIKI_ALT = load("/icons/svg/application-wikipage1-multi.svg");
        public static final Icon FLEXMARK_SPEC = MdIcons.load("/icons/svg/application-flexmark-spec.svg");
    }

    public static class Element {
        public static final Icon REFERENCE = load("/icons/svg/application-reference.svg");
        public static final Icon HIDDEN_REFERENCE = load("/icons/svg/application-reference-hidden.svg");
        public static final Icon MULTI_REFERENCE = load("/icons/svg/application-reference-multi.svg");
        public static final Icon HEADER = load("/icons/svg/application-header.svg");
        public static final Icon HIDDEN_HEADER = load("/icons/svg/application-header-hidden.svg");
        public static final Icon MULTI_HEADER = load("/icons/svg/application-header-multi.svg");
        public static final Icon ENUMERATED_REFERENCE = load("/icons/svg/application-enumerated-reference.svg");
        public static final Icon HIDDEN_ENUMERATED_REFERENCE = load("/icons/svg/application-enumerated-reference-hidden.svg");
        public static final Icon MULTI_ENUMERATED_REFERENCE = load("/icons/svg/application-enumerated-reference-multi.svg");
        public static final Icon FOOTNOTE = load("/icons/svg/application-footnote.svg");
        public static final Icon HIDDEN_FOOTNOTE = load("/icons/svg/application-footnote-hidden.svg");
        public static final Icon MULTI_FOOTNOTE = load("/icons/svg/application-footnote-multi.svg");
        public static final Icon ANCHOR = load("/icons/svg/application-anchor.svg");
        public static final Icon HIDDEN_ANCHOR = load("/icons/svg/application-anchor-hidden.svg");
        public static final Icon MULTI_ANCHOR = load("/icons/svg/application-anchor-multi.svg");
        public static final Icon ATTRIBUTE_ID_VALUE = load("/icons/svg/application-attribute-id-value.svg");
        public static final Icon HIDDEN_ATTRIBUTE_ID_VALUE = load("/icons/svg/application-attribute-id-value-hidden.svg");
        public static final Icon MULTI_ATTRIBUTE_ID_VALUE = load("/icons/svg/application-attribute-id-value-multi.svg");
        public static final Icon BLOCK_QUOTE = load("/icons/svg/application-block-quote.svg");
        public static final Icon ASIDE_BLOCK = load("/icons/svg/application-aside-block.svg");
        public static final Icon IMAGE = load("/icons/svg/application-image.svg");
        public static final Icon LINK = load("/icons/svg/application-link.svg");
        public static final Icon TABLE = load("/icons/svg/application-table.svg");
        public static final Icon BULLET_LIST = load("/icons/svg/application-bullet-list.svg");
        public static final Icon ORDERED_LIST = load("/icons/svg/application-ordered-list.svg");
        public static final Icon LIST_ITEM = load("/icons/svg/application-list-item.svg");
        public static final Icon ABBREVIATION = load("/icons/svg/application-abbreviation.svg");
        public static final Icon MACRO = load("/icons/svg/application-macro.svg");
        public static final Icon HIDDEN_MACRO = load("/icons/svg/application-macro-hidden.svg");
        public static final Icon MULTI_MACRO = load("/icons/svg/application-macro-multi.svg");
        public static final Icon DEFINITION_LIST = load("/icons/svg/application-definition-list.svg");
        public static final Icon DEFINITION_TERM = load("/icons/svg/application-definition-term.svg");
        public static final Icon JEKYLL = load("/icons/svg/application-jekyll.svg");
        public static final Icon SECTION = load("/icons/svg/application-spec-section.svg");
        public static final Icon SECTION_FAIL = load("/icons/svg/application-spec-section-fail.svg");
    }

    public static class Structure {
        public static final Icon COMPLETE_TASK_LIST_ITEM = load("/icons/svg/application-done-task-list-item.svg");
        public static final Icon INCOMPLETE_TASK_LIST_ITEM = load("/icons/svg/application-undone-task-list-item.svg");
        public static final Icon INCOMPLETE_HIGH_TASK_LIST_ITEM = load("/icons/svg/application-undone-high-task-list-item.svg");
        public static final Icon INCOMPLETE_LOW_TASK_LIST_ITEM = load("/icons/svg/application-undone-low-task-list-item.svg");
        public static final Icon EMPTY_TASK_LIST_ITEM = load("/icons/svg/application-empty-task-list-item.svg");
        public static final Icon SHOW_TASK_BADGE = load("/icons/svg/application-show-task-badge.svg");
        public static final Icon UNDONE_TASK_BADGE = load("/icons/svg/application-undone-task-badge.svg");
        public static final JBColor UNDONE_TASK_BADGE_TEXT = new JBColor(Color.WHITE, new Color(0xF2F2F2));
        public static final Icon CONTAINS_UNDONE_TASK_BADGE = load("/icons/svg/application-contains-undone-task-badge.svg");
        public static final JBColor CONTAINS_UNDONE_TASK_BADGE_TEXT = new JBColor(Color.WHITE, new Color(0x333333));
        public static final Icon DONE_TASK_BADGE = load("/icons/svg/application-done-task-badge.svg");
        public static final JBColor DONE_TASK_BADGE_TEXT = new JBColor(Color.WHITE, new Color(0x333333));
        public static final Icon CONTAINS_DONE_TASK_BADGE = load("/icons/svg/application-done-task-badge.svg");
        public static final JBColor CONTAINS_DONE_TASK_BADGE_TEXT = new JBColor(Color.WHITE, new Color(0x333333));
        public static final Icon EMPTY_TASK_BADGE = load("/icons/svg/application-empty-task-badge.svg");
        public static final JBColor EMPTY_TASK_BADGE_TEXT = new JBColor(new Color(0x6E6E6E), new Color(0xB3B3B3));
        public static final Icon CONTAINS_EMPTY_TASK_BADGE = load("/icons/svg/application-empty-task-badge.svg");
        public static final JBColor CONTAINS_EMPTY_TASK_BADGE_TEXT = new JBColor(new Color(0x6E6E6E), new Color(0xB3B3B3));
        public static final Icon SORT_INCOMPLETE_FIRST = load("/icons/svg/application-sort-incomplete-first.svg");
    }

    public static class Misc {
        public static final Icon JEKYLL_ADD = load("/icons/svg/application-jekyll_add.svg");
        public static final Icon LINK_MAP_NOT_USED = load("/icons/svg/application-link-map-not-used.svg");
        public static final Icon SAVE = load("/icons/svg/application-save.svg");
        public static final Icon ADD = load("/icons/svg/application-add.svg");
        public static final Icon DELETE = load("/icons/svg/application-delete.svg");
        public static final Icon COLLAPSIBLE_OPEN = load("/icons/svg/misc-collapsible-open.svg");
        public static final Icon COLLAPSIBLE_CLOSED = load("/icons/svg/misc-collapsible-closed.svg");
    }

    public static final HashMap<Icon, String> iconNamesMap = new HashMap<>();

    /**
     * For Tests
     *
     * @return get icon to name mapping
     */
    public static HashMap<Icon, String> getIconNamesMap() {
        if (iconNamesMap.isEmpty()) {
            iconNamesMap.put(Document.WIKI_OVERLAY, "MdIcons.Document.WIKI_OVERLAY");
            iconNamesMap.put(Document.WIKI_OVERLAY_ALT, "MdIcons.Document.WIKI_OVERLAY_ALT");
            iconNamesMap.put(Document.MARKDOWN_NAVIGATOR_LOGO, "MdIcons.Document.MARKDOWN_NAVIGATOR_LOGO");
            iconNamesMap.put(Document.FILE, "MdIcons.Document.FILE");
            iconNamesMap.put(Document.HIDDEN_FILE, "MdIcons.Document.HIDDEN_FILE");
            iconNamesMap.put(Document.MULTI_FILE, "MdIcons.Document.MULTI_FILE");
            iconNamesMap.put(Document.FILE_ALT, "MdIcons.Document.FILE_ALT");
            iconNamesMap.put(Document.HIDDEN_FILE_ALT, "MdIcons.Document.HIDDEN_FILE_ALT");
            iconNamesMap.put(Document.MULTI_FILE_ALT, "MdIcons.Document.MULTI_FILE_ALT");
            iconNamesMap.put(Document.WIKI, "MdIcons.Document.WIKI");
            iconNamesMap.put(Document.HIDDEN_WIKI, "MdIcons.Document.HIDDEN_WIKI");
            iconNamesMap.put(Document.MULTI_WIKI, "MdIcons.Document.MULTI_WIKI");
            iconNamesMap.put(Document.WIKI_ALT, "MdIcons.Document.WIKI_ALT");
            iconNamesMap.put(Document.HIDDEN_WIKI_ALT, "MdIcons.Document.HIDDEN_WIKI_ALT");
            iconNamesMap.put(Document.MULTI_WIKI_ALT, "MdIcons.Document.MULTI_WIKI_ALT");
            iconNamesMap.put(Document.FLEXMARK_SPEC, "MdIcons.Document.FLEXMARK_SPEC");

            iconNamesMap.put(Element.REFERENCE, "MdIcons.Element.REFERENCE");
            iconNamesMap.put(Element.HIDDEN_REFERENCE, "MdIcons.Element.HIDDEN_REFERENCE");
            iconNamesMap.put(Element.MULTI_REFERENCE, "MdIcons.Element.MULTI_REFERENCE");
            iconNamesMap.put(Element.HEADER, "MdIcons.Element.HEADER");
            iconNamesMap.put(Element.HIDDEN_HEADER, "MdIcons.Element.HIDDEN_HEADER");
            iconNamesMap.put(Element.MULTI_HEADER, "MdIcons.Element.MULTI_HEADER");
            iconNamesMap.put(Element.ENUMERATED_REFERENCE, "MdIcons.Element.ENUMERATED_REFERENCE");
            iconNamesMap.put(Element.HIDDEN_ENUMERATED_REFERENCE, "MdIcons.Element.HIDDEN_ENUMERATED_REFERENCE");
            iconNamesMap.put(Element.MULTI_ENUMERATED_REFERENCE, "MdIcons.Element.MULTI_ENUMERATED_REFERENCE");
            iconNamesMap.put(Element.FOOTNOTE, "MdIcons.Element.FOOTNOTE");
            iconNamesMap.put(Element.HIDDEN_FOOTNOTE, "MdIcons.Element.HIDDEN_FOOTNOTE");
            iconNamesMap.put(Element.MULTI_FOOTNOTE, "MdIcons.Element.MULTI_FOOTNOTE");
            iconNamesMap.put(Element.ANCHOR, "MdIcons.Element.ANCHOR");
            iconNamesMap.put(Element.HIDDEN_ANCHOR, "MdIcons.Element.HIDDEN_ANCHOR");
            iconNamesMap.put(Element.MULTI_ANCHOR, "MdIcons.Element.MULTI_ANCHOR");
            iconNamesMap.put(Element.ATTRIBUTE_ID_VALUE, "MdIcons.Element.ATTRIBUTE_ID_VALUE");
            iconNamesMap.put(Element.HIDDEN_ATTRIBUTE_ID_VALUE, "MdIcons.Element.HIDDEN_ATTRIBUTE_ID_VALUE");
            iconNamesMap.put(Element.MULTI_ATTRIBUTE_ID_VALUE, "MdIcons.Element.MULTI_ATTRIBUTE_ID_VALUE");
            iconNamesMap.put(Element.BLOCK_QUOTE, "MdIcons.Element.BLOCK_QUOTE");
            iconNamesMap.put(Element.ASIDE_BLOCK, "MdIcons.Element.ASIDE_BLOCK");
            iconNamesMap.put(Element.IMAGE, "MdIcons.Element.IMAGE");
            iconNamesMap.put(Element.LINK, "MdIcons.Element.LINK");
            iconNamesMap.put(Element.TABLE, "MdIcons.Element.TABLE");
            iconNamesMap.put(Element.BULLET_LIST, "MdIcons.Element.BULLET_LIST");
            iconNamesMap.put(Element.ORDERED_LIST, "MdIcons.Element.ORDERED_LIST");
            iconNamesMap.put(Element.LIST_ITEM, "MdIcons.Element.LIST_ITEM");
            iconNamesMap.put(Element.ABBREVIATION, "MdIcons.Element.ABBREVIATION");
            iconNamesMap.put(Element.MACRO, "MdIcons.Element.MACRO");
            iconNamesMap.put(Element.HIDDEN_MACRO, "MdIcons.Element.HIDDEN_MACRO");
            iconNamesMap.put(Element.MULTI_MACRO, "MdIcons.Element.MULTI_MACRO");
            iconNamesMap.put(Element.DEFINITION_LIST, "MdIcons.Element.DEFINITION_LIST");
            iconNamesMap.put(Element.DEFINITION_TERM, "MdIcons.Element.DEFINITION_TERM");
            iconNamesMap.put(Element.JEKYLL, "MdIcons.Element.JEKYLL");
            iconNamesMap.put(Element.SECTION, "MdIcons.Element.SECTION");
            iconNamesMap.put(Element.SECTION_FAIL, "MdIcons.Element.SECTION_FAIL");

            iconNamesMap.put(Structure.COMPLETE_TASK_LIST_ITEM, "MdIcons.Structure.COMPLETE_TASK_LIST_ITEM");
            iconNamesMap.put(Structure.INCOMPLETE_TASK_LIST_ITEM, "MdIcons.Structure.INCOMPLETE_TASK_LIST_ITEM");
            iconNamesMap.put(Structure.INCOMPLETE_HIGH_TASK_LIST_ITEM, "MdIcons.Structure.INCOMPLETE_HIGH_TASK_LIST_ITEM");
            iconNamesMap.put(Structure.INCOMPLETE_LOW_TASK_LIST_ITEM, "MdIcons.Structure.INCOMPLETE_LOW_TASK_LIST_ITEM");
            iconNamesMap.put(Structure.EMPTY_TASK_LIST_ITEM, "MdIcons.Structure.EMPTY_TASK_LIST_ITEM");
            iconNamesMap.put(Structure.SHOW_TASK_BADGE, "MdIcons.Structure.SHOW_TASK_BADGE");
            iconNamesMap.put(Structure.UNDONE_TASK_BADGE, "MdIcons.Structure.UNDONE_TASK_BADGE");
            iconNamesMap.put(Structure.CONTAINS_UNDONE_TASK_BADGE, "MdIcons.Structure.CONTAINS_UNDONE_TASK_BADGE");
            iconNamesMap.put(Structure.DONE_TASK_BADGE, "MdIcons.Structure.DONE_TASK_BADGE");
            iconNamesMap.put(Structure.CONTAINS_DONE_TASK_BADGE, "MdIcons.Structure.CONTAINS_DONE_TASK_BADGE");
            iconNamesMap.put(Structure.EMPTY_TASK_BADGE, "MdIcons.Structure.EMPTY_TASK_BADGE");
            iconNamesMap.put(Structure.CONTAINS_EMPTY_TASK_BADGE, "MdIcons.Structure.CONTAINS_EMPTY_TASK_BADGE");
            iconNamesMap.put(Structure.SORT_INCOMPLETE_FIRST, "MdIcons.Structure.SORT_INCOMPLETE_FIRST");

            iconNamesMap.put(Misc.JEKYLL_ADD, "MdIcons.Misc.JEKYLL_ADD");
            iconNamesMap.put(Misc.LINK_MAP_NOT_USED, "MdIcons.Misc.LINK_MAP_NOT_USED");
            iconNamesMap.put(Misc.SAVE, "MdIcons.Misc.SAVE");
            iconNamesMap.put(Misc.ADD, "MdIcons.Misc.ADD");
            iconNamesMap.put(Misc.DELETE, "MdIcons.Misc.DELETE");

            iconNamesMap.put(Admonition.ABSTRACT, "MdIcons.Admonition.ABSTRACT");
            iconNamesMap.put(Admonition.BUG, "MdIcons.Admonition.BUG");
            iconNamesMap.put(Admonition.DANGER, "MdIcons.Admonition.DANGER");
            iconNamesMap.put(Admonition.EXAMPLE, "MdIcons.Admonition.EXAMPLE");
            iconNamesMap.put(Admonition.FAIL, "MdIcons.Admonition.FAIL");
            iconNamesMap.put(Admonition.FAQ, "MdIcons.Admonition.FAQ");
            iconNamesMap.put(Admonition.INFO, "MdIcons.Admonition.INFO");
            iconNamesMap.put(Admonition.NOTE, "MdIcons.Admonition.NOTE");
            iconNamesMap.put(Admonition.QUOTE, "MdIcons.Admonition.QUOTE");
            iconNamesMap.put(Admonition.SUCCESS, "MdIcons.Admonition.SUCCESS");
            iconNamesMap.put(Admonition.TIP, "MdIcons.Admonition.TIP");
            iconNamesMap.put(Admonition.WARNING, "MdIcons.Admonition.WARNING");

            iconNamesMap.put(IntentionActions.MultiMarkdown, "MdIcons.IntentionActions.MultiMarkdown`");
            iconNamesMap.put(IntentionActions.WikiPage, "MdIcons.IntentionActions.WikiPage`");
            iconNamesMap.put(IntentionActions.Headers, "MdIcons.IntentionActions.Headers`");
            iconNamesMap.put(IntentionActions.Links, "MdIcons.IntentionActions.Links`");
            iconNamesMap.put(IntentionActions.References, "MdIcons.IntentionActions.References`");
            iconNamesMap.put(IntentionActions.TaskLists, "MdIcons.IntentionActions.TaskLists`");
            iconNamesMap.put(IntentionActions.TOC, "MdIcons.IntentionActions.TOC`");
            iconNamesMap.put(IntentionActions.RefAnchorExplorer, "MdIcons.IntentionActions.RefAnchorExplorer`");

            iconNamesMap.put(LinkTypes.Web, "MdIcons.LinkTypes.Web");
            iconNamesMap.put(LinkTypes.Ftp, "MdIcons.LinkTypes.Ftp");
            iconNamesMap.put(LinkTypes.Mail, "MdIcons.LinkTypes.Mail");
            iconNamesMap.put(LinkTypes.GitHub, "MdIcons.LinkTypes.GitHub");
            iconNamesMap.put(LinkTypes.Unknown, "MdIcons.LinkTypes.Unknown");

            iconNamesMap.put(EditorActions.Bold, "MdIcons.EditorActions.Bold");
            iconNamesMap.put(EditorActions.Italic, "MdIcons.EditorActions.Italic");
            iconNamesMap.put(EditorActions.Strike_through, "MdIcons.EditorActions.Strike_through");
            iconNamesMap.put(EditorActions.Underline, "MdIcons.EditorActions.Underline");
            iconNamesMap.put(EditorActions.Superscript, "MdIcons.EditorActions.Superscript");
            iconNamesMap.put(EditorActions.Subscript, "MdIcons.EditorActions.Subscript");
            iconNamesMap.put(EditorActions.Code_span, "MdIcons.EditorActions.Code_span");
            iconNamesMap.put(EditorActions.Header_level_down, "MdIcons.EditorActions.Header_level_down");
            iconNamesMap.put(EditorActions.Header_level_up, "MdIcons.EditorActions.Header_level_up");
            iconNamesMap.put(EditorActions.List_indent, "MdIcons.EditorActions.List_indent");
            iconNamesMap.put(EditorActions.List_unindent, "MdIcons.EditorActions.List_unindent");
            iconNamesMap.put(EditorActions.List_tight, "MdIcons.EditorActions.List_tight");
            iconNamesMap.put(EditorActions.List_loose, "MdIcons.EditorActions.List_loose");
            iconNamesMap.put(EditorActions.List_bullet_items, "MdIcons.EditorActions.List_bullet_items");
            iconNamesMap.put(EditorActions.List_ordered_items, "MdIcons.EditorActions.List_ordered_items");
            iconNamesMap.put(EditorActions.List_task_items, "MdIcons.EditorActions.List_task_items");
            iconNamesMap.put(EditorActions.List_toggle_item_done, "MdIcons.EditorActions.List_toggle_item_done");
            iconNamesMap.put(EditorActions.Header_toggle_type, "MdIcons.EditorActions.Header_toggle_type");
            iconNamesMap.put(EditorActions.Link, "MdIcons.EditorActions.Link");
            iconNamesMap.put(EditorActions.Reformat_element, "MdIcons.EditorActions.Reformat_element");
            iconNamesMap.put(EditorActions.Reformat_document, "MdIcons.EditorActions.Reformat_document");
            iconNamesMap.put(EditorActions.Insert_table, "MdIcons.EditorActions.Insert_table");
            iconNamesMap.put(EditorActions.Insert_table_row, "MdIcons.EditorActions.Insert_table_row");
            iconNamesMap.put(EditorActions.Insert_table_column, "MdIcons.EditorActions.Insert_table_column");
            iconNamesMap.put(EditorActions.Insert_table_column_right, "MdIcons.EditorActions.Insert_table_column_right");
            iconNamesMap.put(EditorActions.Delete_table_row, "MdIcons.EditorActions.Delete_table_row");
            iconNamesMap.put(EditorActions.Delete_table_column, "MdIcons.EditorActions.Delete_table_column");
            iconNamesMap.put(EditorActions.AutoFormat_table, "MdIcons.EditorActions.AutoFormat_table");
            iconNamesMap.put(EditorActions.Wrap_on_typing, "MdIcons.EditorActions.Wrap_on_typing");
            iconNamesMap.put(EditorActions.Balloon_test, "MdIcons.EditorActions.Balloon_test");
            iconNamesMap.put(EditorActions.Debug_text_bounds, "MdIcons.EditorActions.Debug_text_bounds");
            iconNamesMap.put(EditorActions.Quote_add, "MdIcons.EditorActions.Quote_add");
            iconNamesMap.put(EditorActions.Quote_remove, "MdIcons.EditorActions.Quote_remove");
            iconNamesMap.put(EditorActions.Toggle_use_char_width, "MdIcons.EditorActions.Toggle_use_char_width");
            iconNamesMap.put(EditorActions.Open_file_tester, "MdIcons.EditorActions.Open_file_tester");
            iconNamesMap.put(EditorActions.Select_file_tester, "MdIcons.EditorActions.Select_file_tester");
            iconNamesMap.put(EditorActions.Show_text_hex, "MdIcons.EditorActions.Show_text_hex");
            iconNamesMap.put(EditorActions.Copy_jira, "MdIcons.EditorActions.Copy_jira");
            iconNamesMap.put(EditorActions.Copy_you_track, "MdIcons.EditorActions.Copy_you_track");
            iconNamesMap.put(EditorActions.Copy_html_mime, "MdIcons.EditorActions.Copy_html_mime");
            iconNamesMap.put(EditorActions.Export_all_html, "MdIcons.EditorActions.Export_all_html");
            iconNamesMap.put(EditorActions.Export_html, "MdIcons.EditorActions.Export_html");
            iconNamesMap.put(EditorActions.Preferences_size, "MdIcons.EditorActions.Preferences_size");
            iconNamesMap.put(EditorActions.Export_pdf, "MdIcons.EditorActions.Export_pdf");
            iconNamesMap.put(EditorActions.Exception_test, "MdIcons.EditorActions.Exception_test");
            iconNamesMap.put(EditorActions.Plugin_list, "MdIcons.EditorActions.Plugin_list");
            iconNamesMap.put(EditorActions.Menu_copy, "MdIcons.EditorActions.Menu_copy");
            iconNamesMap.put(EditorActions.Menu_misc, "MdIcons.EditorActions.Menu_misc");
            iconNamesMap.put(EditorActions.Menu_table, "MdIcons.EditorActions.Menu_table");
            iconNamesMap.put(EditorActions.Menu_list, "MdIcons.EditorActions.Menu_list");
            iconNamesMap.put(EditorActions.Yandex_document, "MdIcons.EditorActions.Yandex_document");
            iconNamesMap.put(EditorActions.Move_table_column_left, "MdIcons.EditorActions.Move_table_column_left");
            iconNamesMap.put(EditorActions.Move_table_column_right, "MdIcons.EditorActions.Move_table_column_right");
            iconNamesMap.put(EditorActions.Copy_exported_html_mime, "MdIcons.EditorActions.Copy_exported_html_mime");
            iconNamesMap.put(EditorActions.Copy_without_soft_breaks, "MdIcons.EditorActions.Copy_without_soft_breaks");
            iconNamesMap.put(EditorActions.Toggle_suspend_license, "MdIcons.EditorActions.Toggle_suspend_license");
            iconNamesMap.put(EditorActions.Transpose_table, "MdIcons.EditorActions.Transpose_table");
            iconNamesMap.put(EditorActions.Sort_table, "MdIcons.EditorActions.Sort_table");

            iconNamesMap.put(Layout.Editor_only, "MdIcons.Layout.Editor_only");
            iconNamesMap.put(Layout.Editor_preview, "MdIcons.Layout.Editor_preview");
            iconNamesMap.put(Layout.Preview_only, "MdIcons.Layout.Preview_only");
            iconNamesMap.put(Layout.Cycle_editor_preview, "MdIcons.Layout.Cycle_editor_preview");
            iconNamesMap.put(Layout.Toggle_editor_preview, "MdIcons.Layout.Toggle_editor_preview");
            iconNamesMap.put(Layout.Html_preview, "MdIcons.Layout.Html_preview");
            iconNamesMap.put(Layout.Html_modified, "MdIcons.Layout.Html_modified");
            iconNamesMap.put(Layout.Html_unmodified, "MdIcons.Layout.Html_unmodified");
            iconNamesMap.put(Layout.Cycle_html, "MdIcons.Layout.Cycle_html");
            iconNamesMap.put(Layout.Print_preview, "MdIcons.Layout.Print_preview");
            iconNamesMap.put(Layout.Debug_preview, "MdIcons.Layout.Debug_preview");
            iconNamesMap.put(Layout.Debug_break, "MdIcons.Layout.Debug_break");
            iconNamesMap.put(Layout.Debug_breakInjection, "MdIcons.Layout.Debug_breakInjection");
            iconNamesMap.put(Layout.Split_preview_horizontal, "MdIcons.Layout.Split_preview_horizontal");
        }

        return iconNamesMap;
    }

    public static final HashMap<Icon, Icon> multiMap = new HashMap<Icon, Icon>();
    public static final HashMap<Icon, Icon> hiddenMap = new HashMap<Icon, Icon>();
    static {
        multiMap.put(getDocumentIcon(), Document.MULTI_FILE);
        multiMap.put(Document.WIKI, Document.MULTI_WIKI);
        multiMap.put(Document.FILE_ALT, Document.MULTI_FILE_ALT);
        multiMap.put(Document.WIKI_ALT, Document.MULTI_WIKI_ALT);
        multiMap.put(FlexmarkIcons.Element.FLEXMARK_SPEC, FlexmarkIcons.Element.MULTI_FLEXMARK_SPEC);
        multiMap.put(FlexmarkIcons.Element.SPEC_EXAMPLE, FlexmarkIcons.Element.MULTI_SPEC_EXAMPLE);
        multiMap.put(Element.REFERENCE, Element.MULTI_REFERENCE);
        multiMap.put(Element.HEADER, Element.MULTI_HEADER);
        multiMap.put(Element.FOOTNOTE, Element.MULTI_FOOTNOTE);
        multiMap.put(Element.ANCHOR, Element.MULTI_ANCHOR);
        multiMap.put(Element.ATTRIBUTE_ID_VALUE, Element.MULTI_ATTRIBUTE_ID_VALUE);
        multiMap.put(FlexmarkIcons.Element.SPEC_EXAMPLE_ERRORS, FlexmarkIcons.Element.MULTI_SPEC_EXAMPLE_ERRORS);
        multiMap.put(Element.ENUMERATED_REFERENCE, Element.MULTI_ENUMERATED_REFERENCE);
        multiMap.put(Element.MACRO, Element.MULTI_MACRO);

        hiddenMap.put(getDocumentIcon(), Document.HIDDEN_FILE);
        hiddenMap.put(Document.WIKI, Document.HIDDEN_WIKI);
        hiddenMap.put(Document.FILE_ALT, Document.HIDDEN_FILE_ALT);
        hiddenMap.put(Document.WIKI_ALT, Document.HIDDEN_WIKI_ALT);
        hiddenMap.put(FlexmarkIcons.Element.FLEXMARK_SPEC, FlexmarkIcons.Element.HIDDEN_FLEXMARK_SPEC);
        hiddenMap.put(FlexmarkIcons.Element.SPEC_EXAMPLE, FlexmarkIcons.Element.HIDDEN_SPEC_EXAMPLE);
        hiddenMap.put(Element.REFERENCE, Element.HIDDEN_REFERENCE);
        hiddenMap.put(Element.HEADER, Element.HIDDEN_HEADER);
        hiddenMap.put(Element.FOOTNOTE, Element.HIDDEN_FOOTNOTE);
        hiddenMap.put(Element.ANCHOR, Element.HIDDEN_ANCHOR);
        hiddenMap.put(Element.ATTRIBUTE_ID_VALUE, Element.HIDDEN_ATTRIBUTE_ID_VALUE);
        hiddenMap.put(Element.ENUMERATED_REFERENCE, Element.HIDDEN_ENUMERATED_REFERENCE);
        hiddenMap.put(Element.MACRO, Element.HIDDEN_MACRO);
    }
    public static Icon getFileIcon(DocumentIconTypes type) {
        switch (type) {
            default:
            case MARKDOWN_NAVIGATOR:
                return Document.FILE;
            case MARKDOWN:
                return Document.FILE_ALT;
            case MARKDOWN_NAVIGATOR_WIKI:
                return Document.WIKI;
            case MARKDOWN_WIKI:
                return Document.WIKI_ALT;
        }
    }

    public static Icon getDocumentIcon() {
        return getFileIcon(MdApplicationSettings.getInstance().getDocumentSettings().getDocumentIconType());
    }

    public static Icon getWikiPageIcon() {
        return getFileIcon(MdApplicationSettings.getInstance().getDocumentSettings().getWikiIconType());
    }

    static Icon load(String path) {
        return Helpers.load(path, MdIcons.class);
    }

    public static Icon getMultiIcon(Icon icon) {
        return multiMap.getOrDefault(icon, icon);
    }

    public static Icon getHiddenIcon(Icon icon) {
        return hiddenMap.getOrDefault(icon, icon);
    }

    public static class Admonition {
        public static final Icon ABSTRACT = load("/icons/svg/admonition-abstract.svg");
        public static final Icon BUG = load("/icons/svg/admonition-bug.svg");
        public static final Icon DANGER = load("/icons/svg/admonition-danger.svg");
        public static final Icon EXAMPLE = load("/icons/svg/admonition-example.svg");
        public static final Icon FAIL = load("/icons/svg/admonition-fail.svg");
        public static final Icon FAQ = load("/icons/svg/admonition-faq.svg");
        public static final Icon INFO = load("/icons/svg/admonition-info.svg");
        public static final Icon NOTE = load("/icons/svg/admonition-note.svg");
        public static final Icon QUOTE = load("/icons/svg/admonition-quote.svg");
        public static final Icon SUCCESS = load("/icons/svg/admonition-success.svg");
        public static final Icon TIP = load("/icons/svg/admonition-tip.svg");
        public static final Icon WARNING = load("/icons/svg/admonition-warning.svg");

        public static Map<String, Icon> ADM_ICON_MAP = new HashMap<>();
        static {
            ADM_ICON_MAP.put("abstract", ABSTRACT);
            ADM_ICON_MAP.put("bug", BUG);
            ADM_ICON_MAP.put("danger", DANGER);
            ADM_ICON_MAP.put("example", EXAMPLE);
            ADM_ICON_MAP.put("fail", FAIL);
            ADM_ICON_MAP.put("faq", FAQ);
            ADM_ICON_MAP.put("info", INFO);
            ADM_ICON_MAP.put("note", NOTE);
            ADM_ICON_MAP.put("quote", QUOTE);
            ADM_ICON_MAP.put("success", SUCCESS);
            ADM_ICON_MAP.put("tip", TIP);
            ADM_ICON_MAP.put("warning", WARNING);
        }
    }

    public static class IntentionActions {
        public static final Icon MultiMarkdown = load("/icons/intention_actions/MultiMarkdown.png"); // 16x16
        public static final Icon WikiPage = load("/icons/intention_actions/WikiPage.png"); // 16x16
        public static final Icon Headers = load("/icons/intention_actions/Headers.png"); // 16x16
        public static final Icon Links = load("/icons/intention_actions/Links.png"); // 16x16
        public static final Icon References = load("/icons/intention_actions/References.png"); // 16x16
        public static final Icon TaskLists = load("/icons/intention_actions/TaskLists.png"); // 16x16
        public static final Icon TOC = load("/icons/intention_actions/TOC.png"); // 16x16
        public static final Icon RefAnchorExplorer = load("/icons/intention_actions/RefAnchorExplorer.png"); // 16x16

        private static final HashMap<String, Icon> intentionActionsMap = new HashMap<String, Icon>();
        static {
            intentionActionsMap.put(MULTIMARKDOWN, MultiMarkdown);
            intentionActionsMap.put(WIKIPAGE, WikiPage);
            //intentionActionsMap.put(MULTIMARKDOWN + "/Links", MultiMarkdown);
            //intentionActionsMap.put(MULTIMARKDOWN + "/Headers", MultiMarkdown);
            intentionActionsMap.put(MULTIMARKDOWN + "/Links", Links);
            intentionActionsMap.put(MULTIMARKDOWN + "/Wiki Links", WikiPage);
            intentionActionsMap.put(MULTIMARKDOWN + "/Headers", Headers);
            intentionActionsMap.put(MULTIMARKDOWN + "/References", References);
            intentionActionsMap.put(MULTIMARKDOWN + "/Lists", TaskLists);
            intentionActionsMap.put(MULTIMARKDOWN + "/Table of Contents", TOC);
        }
        public static Icon getCategoryIcon(String[] category) {
            String categoryName = HelpersKt.splice(category, "/");
            if (intentionActionsMap.containsKey(categoryName)) {
                return intentionActionsMap.get(categoryName);
            }
            return MultiMarkdown;
        }
    }

    public static class LinkTypes {
        public static final Icon CustomUri = load("/icons/svg/application-custom-link.svg"); // 16x16
        public static final Icon Ftp = load("/icons/svg/application-ftplink.svg"); // 16x16
        public static final Icon GitHub = load("/icons/svg/application-githublink.svg"); // 16x16
        public static final Icon JetBrains = load("/icons/svg/application-jetbrains-link.svg"); // 16x16
        public static final Icon Mail = load("/icons/svg/application-maillink.svg"); // 16x16
        public static final Icon Unknown = load("/icons/svg/application-unknown.svg"); // 16x16
        public static final Icon Upsource = load("/icons/svg/application-upsource-link.svg"); // 16x16
        public static final Icon Web = load("/icons/svg/application-weblink.svg"); // 17x17

        private static final HashMap<String, Icon> intentionActionsMap = new HashMap<String, Icon>();
    }

    public static class EditorActions {
        public static final Icon Bold = load("/icons/editor_actions/Bold.png"); // 16x16
        public static final Icon Italic = load("/icons/editor_actions/Italic.png"); // 16x16
        public static final Icon Strike_through = load("/icons/editor_actions/Strike_through.png"); // 16x16
        public static final Icon Underline = load("/icons/editor_actions/Underline.png"); // 16x16
        public static final Icon Superscript = load("/icons/editor_actions/Superscript.png"); // 16x16
        public static final Icon Subscript = load("/icons/editor_actions/Subscript.png"); // 16x16
        public static final Icon Code_span = load("/icons/editor_actions/Code_span.png"); // 16x16
        public static final Icon Header_level_down = load("/icons/editor_actions/Header_level_down.png"); // 16x16
        public static final Icon Header_level_up = load("/icons/editor_actions/Header_level_up.png"); // 16x16
        public static final Icon List_indent = load("/icons/editor_actions/List_indent.png"); // 16x16
        public static final Icon List_unindent = load("/icons/editor_actions/List_unindent.png"); // 16x16
        public static final Icon List_tight = load("/icons/editor_actions/List_tight.png"); // 16x16
        public static final Icon List_loose = load("/icons/editor_actions/List_loose.png"); // 16x16
        public static final Icon List_bullet_items = load("/icons/editor_actions/List_bullet_items.png"); // 16x16
        public static final Icon List_ordered_items = load("/icons/editor_actions/List_ordered_items.png"); // 16x16
        public static final Icon List_task_items = load("/icons/editor_actions/List_task_items.png"); // 16x16
        public static final Icon List_toggle_item_done = load("/icons/editor_actions/List_toggle_item_done.png"); // 16x16
        public static final Icon Header_toggle_type = load("/icons/editor_actions/Header_toggle_type.png"); // 16x16
        public static final Icon Link = load("/icons/editor_actions/Link.png"); // 16x16
        public static final Icon Reformat_element = load("/icons/editor_actions/Reformat_element.png"); // 16x16
        public static final Icon Reformat_document = load("/icons/editor_actions/Reformat_document.png"); // 16x16
        public static final Icon Insert_table = load("/icons/editor_actions/Insert_table.png"); // 16x16
        public static final Icon Insert_table_row = load("/icons/editor_actions/Insert_table_row.png"); // 16x16
        public static final Icon Insert_table_column = load("/icons/editor_actions/Insert_table_column.png"); // 16x16
        public static final Icon Insert_table_column_right = load("/icons/editor_actions/Insert_table_column_right.png"); // 16x16
        public static final Icon Delete_table_row = load("/icons/editor_actions/Delete_table_row.png"); // 16x16
        public static final Icon Delete_table_column = load("/icons/editor_actions/Delete_table_column.png"); // 16x16
        public static final Icon AutoFormat_table = load("/icons/editor_actions/AutoFormat_table.png"); // 16x16
        public static final Icon Wrap_on_typing = load("/icons/editor_actions/Wrap_on_typing.png"); // 16x16
        public static final Icon Balloon_test = load("/icons/editor_actions/Balloon_tester.png"); // 16x16
        public static final Icon Debug_text_bounds = load("/icons/svg/editor-actions-text-bounds.svg"); // 16x16
        public static final Icon Quote_add = load("/icons/editor_actions/Quote_add.png"); // 16x16
        public static final Icon Quote_remove = load("/icons/editor_actions/Quote_remove.png"); // 16x16
        public static final Icon Toggle_use_char_width = load("/icons/editor_actions/Toggle_use_char_width.png"); // 16x16
        public static final Icon Open_file_tester = load("/icons/editor_actions/Open_file_tester.png"); // 16x16
        public static final Icon Select_file_tester = load("/icons/editor_actions/Select_file_tester.png"); // 16x16
        public static final Icon Show_text_hex = load("/icons/editor_actions/Show_text_hex.png"); // 16x16
        public static final Icon Copy_jira = load("/icons/editor_actions/Copy_jira.png"); // 16x16
        public static final Icon Copy_you_track = load("/icons/editor_actions/Copy_you_track.png"); // 16x16
        public static final Icon Copy_html_mime = load("/icons/editor_actions/Copy_html_mime.png"); // 16x16
        public static final Icon Export_all_html = load("/icons/editor_actions/Export_all_html.png"); // 16x16
        public static final Icon Export_html = load("/icons/editor_actions/Export_html.png"); // 16x16
        public static final Icon Preferences_size = load("/icons/editor_actions/Preferences_size.png"); // 16x16
        public static final Icon Export_pdf = load("/icons/editor_actions/Export_pdf.png"); // 16x16
        public static final Icon Exception_test = load("/icons/editor_actions/Exception_test.png"); // 16x16
        public static final Icon Plugin_list = load("/icons/editor_actions/Plugin_list.png"); // 16x16
        public static final Icon Menu_copy = load("/icons/editor_actions/Menu_copy.png"); // 16x16
        public static final Icon Menu_misc = load("/icons/editor_actions/Menu_misc.png"); // 16x16
        public static final Icon Menu_table = load("/icons/editor_actions/Menu_table.png"); // 16x16
        public static final Icon Menu_list = load("/icons/editor_actions/Menu_list.png"); // 16x16
        public static final Icon Yandex_document = load("/icons/editor_actions/Yandex_document.png"); // 16x16
        public static final Icon Move_table_column_left = load("/icons/editor_actions/Move_table_column_left.png"); // 16x16
        public static final Icon Move_table_column_right = load("/icons/editor_actions/Move_table_column_right.png"); // 16x16
        public static final Icon Copy_exported_html_mime = load("/icons/editor_actions/Copy_exported_html_mime.png"); // 16x16
        public static final Icon Copy_without_soft_breaks = load("/icons/editor_actions/Copy_without_soft_breaks.png"); // 16x16
        public static final Icon Toggle_suspend_license = load("/icons/svg/editor_action-suspend-license.svg"); // 16x16
        public static final Icon Style_settings = load("/icons/svg/style-settings.svg"); // 16x16
        public static final Icon Transpose_table = load("/icons/editor_actions/Transpose_table.png"); // 16x16
        public static final Icon Sort_table = load("/icons/editor_actions/Sort_table.png"); // 16x16
    }

    public static class Layout {
        public static final Icon Editor_only = load("/icons/layout/Editor_only.png"); // 16x16
        public static final Icon Editor_preview = load("/icons/layout/Editor_preview.png"); // 16x16
        public static final Icon Preview_only = load("/icons/layout/Preview_only.png"); // 16x16
        public static final Icon Cycle_editor_preview = load("/icons/layout/Cycle_editor_preview.png"); // 16x16
        public static final Icon Toggle_editor_preview = load("/icons/layout/Toggle_editor_preview.png"); // 16x16
        public static final Icon Html_preview = load("/icons/layout/Html_preview.png"); // 16x16
        public static final Icon Html_modified = load("/icons/layout/Html_modified.png"); // 16x16
        public static final Icon Html_unmodified = load("/icons/layout/Html_unmodified.png"); // 16x16
        public static final Icon Cycle_html = load("/icons/layout/Cycle_html.png"); // 16x16
        public static final Icon Print_preview = load("/icons/layout/Print_preview.png"); // 16x16
        public static final Icon Debug_preview = load("/icons/layout/Debug_preview.png"); // 16x16
        public static final Icon Debug_break = load("/icons/layout/Debug_break.png"); // 16x16
        public static final Icon Debug_breakInjection = load("/icons/layout/Debug_breakInjection.png"); // 16x16
        public static final Icon Split_preview_horizontal = load("/icons/layout/Split_preview_horizontal.png"); // 16x16
    }
}
