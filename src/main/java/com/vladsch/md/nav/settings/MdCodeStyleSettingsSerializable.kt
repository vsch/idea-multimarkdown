// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.vladsch.md.nav.flex.parser.MdSpecExampleStripTrailingSpacesExtension
import com.vladsch.md.nav.language.MdCodeStyleSettings

/**
 * Used to serialize Code Style Settings when exporting all Project settings
 */
class MdCodeStyleSettingsSerializable(val settings: MdCodeStyleSettings) : StateHolderImpl({ MdCodeStyleSettingsSerializable() }), MdCodeStyleSettings.Holder {

    companion object {
        const val STATE_ELEMENT_NAME: String = "MarkdownNavigatorCodeStyle"
    }

    override fun getStyleSettings(): MdCodeStyleSettings {
        return settings
    }

    override fun setStyleSettings(styleSettings: MdCodeStyleSettings) {
        settings.copyFrom(styleSettings)
    }

    constructor() : this(MdCodeStyleSettings.getInstance())

    override fun getStateHolder(): StateHolder = TagItemHolder(STATE_ELEMENT_NAME).addItems(
        BooleanAttribute("ATTRIBUTES_COMBINE_CONSECUTIVE", { settings.ATTRIBUTES_COMBINE_CONSECUTIVE }, { settings.ATTRIBUTES_COMBINE_CONSECUTIVE = it }),
        BooleanAttribute("CODE_FENCE_MATCH_CLOSING_MARKER", { settings.CODE_FENCE_MATCH_CLOSING_MARKER }, { settings.CODE_FENCE_MATCH_CLOSING_MARKER = it }),
        BooleanAttribute("CODE_FENCE_MINIMIZE_INDENT", { settings.CODE_FENCE_MINIMIZE_INDENT }, { settings.CODE_FENCE_MINIMIZE_INDENT = it }),
        BooleanAttribute("CODE_FENCE_SPACE_BEFORE_INFO", { settings.CODE_FENCE_SPACE_BEFORE_INFO }, { settings.CODE_FENCE_SPACE_BEFORE_INFO = it }),
        BooleanAttribute("ESCAPE_NUMBERED_LEAD_IN_ON_WRAP", { settings.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP }, { settings.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP = it }),
        BooleanAttribute("ESCAPE_SPECIAL_CHARS_ON_WRAP", { settings.ESCAPE_SPECIAL_CHARS_ON_WRAP }, { settings.ESCAPE_SPECIAL_CHARS_ON_WRAP = it }),
        BooleanAttribute("LIST_ADD_BLANK_LINE_BEFORE", { settings.LIST_ADD_BLANK_LINE_BEFORE }, { settings.LIST_ADD_BLANK_LINE_BEFORE = it }),
        BooleanAttribute("LIST_RENUMBER_ITEMS", { settings.LIST_RENUMBER_ITEMS }, { settings.LIST_RENUMBER_ITEMS = it }),
        BooleanAttribute("LIST_RESET_FIRST_ITEM_NUMBER", { settings.LIST_RESET_FIRST_ITEM_NUMBER }, { settings.LIST_RESET_FIRST_ITEM_NUMBER = it }),
        BooleanAttribute("PARA_WRAP_TEXT", { settings.PARA_WRAP_TEXT }, { settings.PARA_WRAP_TEXT = it }),
        BooleanAttribute("SETEXT_HEADER_EQUALIZE_MARKER", { settings.SETEXT_HEADER_EQUALIZE_MARKER }, { settings.SETEXT_HEADER_EQUALIZE_MARKER = it }),
        BooleanAttribute("SMART_EDIT_ATX_HEADER", { settings.SMART_EDIT_ATX_HEADER }, { settings.SMART_EDIT_ATX_HEADER = it }),
        BooleanAttribute("SMART_EDIT_SETEXT_HEADER", { settings.SMART_EDIT_SETEXT_HEADER }, { settings.SMART_EDIT_SETEXT_HEADER = it }),
        BooleanAttribute("SMART_EDIT_TABLE_SEPARATOR_LINE", { settings.SMART_EDIT_TABLE_SEPARATOR_LINE }, { settings.SMART_EDIT_TABLE_SEPARATOR_LINE = it }),
        BooleanAttribute("SMART_EDIT_TABLES", { settings.SMART_EDIT_TABLES }, { settings.SMART_EDIT_TABLES = it }),
        BooleanAttribute("SMART_ENTER_ATX_HEADER", { settings.SMART_ENTER_ATX_HEADER }, { settings.SMART_ENTER_ATX_HEADER = it }),
        BooleanAttribute("SMART_ENTER_SETEXT_HEADER", { settings.SMART_ENTER_SETEXT_HEADER }, { settings.SMART_ENTER_SETEXT_HEADER = it }),
        BooleanAttribute("SMART_TABS", { settings.SMART_TABS }, { settings.SMART_TABS = it }),
        BooleanAttribute("TABLE_ADJUST_COLUMN_WIDTH", { settings.TABLE_ADJUST_COLUMN_WIDTH }, { settings.TABLE_ADJUST_COLUMN_WIDTH = it }),
        BooleanAttribute("TABLE_APPLY_COLUMN_ALIGNMENT", { settings.TABLE_APPLY_COLUMN_ALIGNMENT }, { settings.TABLE_APPLY_COLUMN_ALIGNMENT = it }),
        BooleanAttribute("TABLE_FILL_MISSING_COLUMNS", { settings.TABLE_FILL_MISSING_COLUMNS }, { settings.TABLE_FILL_MISSING_COLUMNS = it }),
        BooleanAttribute("TABLE_LEAD_TRAIL_PIPES", { settings.TABLE_LEAD_TRAIL_PIPES }, { settings.TABLE_LEAD_TRAIL_PIPES = it }),
        BooleanAttribute("TABLE_SPACE_AROUND_PIPE", { settings.TABLE_SPACE_AROUND_PIPE }, { settings.TABLE_SPACE_AROUND_PIPE = it }),
        BooleanAttribute("TABLE_TRIM_CELLS", { settings.TABLE_TRIM_CELLS }, { settings.TABLE_TRIM_CELLS = it }),
        BooleanAttribute("TOC_FORMAT_ON_SAVE", { settings.TOC_FORMAT_ON_SAVE }, { settings.TOC_FORMAT_ON_SAVE = it }),
        BooleanAttribute("TOC_GENERATE_HTML", { settings.TOC_GENERATE_HTML }, { settings.TOC_GENERATE_HTML = it }),
        BooleanAttribute("TOC_GENERATE_NUMBERED_LIST", { settings.TOC_GENERATE_NUMBERED_LIST }, { settings.TOC_GENERATE_NUMBERED_LIST = it }),
        BooleanAttribute("TOC_GENERATE_TEXT_ONLY", { settings.TOC_GENERATE_TEXT_ONLY }, { settings.TOC_GENERATE_TEXT_ONLY = it }),
        BooleanAttribute("UNESCAPE_SPECIAL_CHARS_ON_WRAP", { settings.UNESCAPE_SPECIAL_CHARS_ON_WRAP }, { settings.UNESCAPE_SPECIAL_CHARS_ON_WRAP = it }),
        BooleanAttribute("USE_ACTUAL_CHAR_WIDTH", { settings.USE_ACTUAL_CHAR_WIDTH }, { settings.USE_ACTUAL_CHAR_WIDTH = it }),
        BooleanAttribute("USE_TAB_CHARACTER", { settings.USE_TAB_CHARACTER }, { settings.USE_TAB_CHARACTER = it }),
        BooleanAttribute("VERBATIM_MINIMIZE_INDENT", { settings.VERBATIM_MINIMIZE_INDENT }, { settings.VERBATIM_MINIMIZE_INDENT = it }),

        IntAttribute("ABBREVIATIONS_PLACEMENT", { settings.ABBREVIATIONS_PLACEMENT }, { settings.ABBREVIATIONS_PLACEMENT = it }),
        IntAttribute("ABBREVIATIONS_SORT", { settings.ABBREVIATIONS_SORT }, { settings.ABBREVIATIONS_SORT = it }),
        IntAttribute("ATTRIBUTE_EQUAL_SPACE", { settings.ATTRIBUTE_EQUAL_SPACE }, { settings.ATTRIBUTE_EQUAL_SPACE = it }),
        IntAttribute("ATTRIBUTE_VALUE_QUOTES", { settings.ATTRIBUTE_VALUE_QUOTES }, { settings.ATTRIBUTE_VALUE_QUOTES = it }),
        IntAttribute("ATTRIBUTES_SPACES", { settings.ATTRIBUTES_SPACES }, { settings.ATTRIBUTES_SPACES = it }),
        IntAttribute("ATX_HEADER_TRAILING_MARKER", { settings.ATX_HEADER_TRAILING_MARKER }, { settings.ATX_HEADER_TRAILING_MARKER = it }),
        IntAttribute("BLOCK_QUOTE_MARKERS", { settings.BLOCK_QUOTE_MARKERS }, { settings.BLOCK_QUOTE_MARKERS = it }),
        IntAttribute("BULLET_LIST_ITEM_MARKER", { settings.BULLET_LIST_ITEM_MARKER }, { settings.BULLET_LIST_ITEM_MARKER = it }),
        IntAttribute("CODE_FENCE_MARKER_LENGTH", { settings.CODE_FENCE_MARKER_LENGTH }, { settings.CODE_FENCE_MARKER_LENGTH = it }),
        IntAttribute("CODE_FENCE_MARKER_TYPE", { settings.CODE_FENCE_MARKER_TYPE }, { settings.CODE_FENCE_MARKER_TYPE = it }),
        IntAttribute("CODE_KEEP_TRAILING_SPACES", { settings.CODE_KEEP_TRAILING_SPACES }, { settings.CODE_KEEP_TRAILING_SPACES = it }),
        IntAttribute("DEFINITION_MARKER_SPACES", { settings.DEFINITION_MARKER_SPACES }, { settings.DEFINITION_MARKER_SPACES = it }),
        IntAttribute("DEFINITION_MARKER_TYPE", { settings.DEFINITION_MARKER_TYPE }, { settings.DEFINITION_MARKER_TYPE = it }),
        IntAttribute("ENUMERATED_REFERENCE_FORMAT_PLACEMENT", { settings.ENUMERATED_REFERENCE_FORMAT_PLACEMENT }, { settings.ENUMERATED_REFERENCE_FORMAT_PLACEMENT = it }),
        IntAttribute("ENUMERATED_REFERENCE_FORMAT_SORT", { settings.ENUMERATED_REFERENCE_FORMAT_SORT }, { settings.ENUMERATED_REFERENCE_FORMAT_SORT = it }),
        IntAttribute("FOOTNOTE_PLACEMENT", { settings.FOOTNOTE_PLACEMENT }, { settings.FOOTNOTE_PLACEMENT = it }),
        IntAttribute("FOOTNOTE_SORT", { settings.FOOTNOTE_SORT }, { settings.FOOTNOTE_SORT = it }),
        IntAttribute("FORMAT_WITH_SOFT_WRAP", { settings.FORMAT_WITH_SOFT_WRAP }, { settings.FORMAT_WITH_SOFT_WRAP = it }),
        IntAttribute("HEADING_PREFERENCE", { settings.HEADING_PREFERENCE }, { settings.HEADING_PREFERENCE = it }),
        IntAttribute("INDENT_SIZE", { settings.INDENT_SIZE }, { settings.INDENT_SIZE = it }),
        IntAttribute("KEEP_AT_START_EXPLICIT_LINK", { settings.KEEP_AT_START_EXPLICIT_LINK }, { settings.KEEP_AT_START_EXPLICIT_LINK = it }),
        IntAttribute("KEEP_AT_START_IMAGE_LINKS", { settings.KEEP_AT_START_IMAGE_LINKS }, { settings.KEEP_AT_START_IMAGE_LINKS = it }),
        IntAttribute("KEEP_BLANK_LINES", { settings.KEEP_BLANK_LINES }, { settings.KEEP_BLANK_LINES = it }),
        IntAttribute("KEEP_TRAILING_SPACES", { settings.KEEP_TRAILING_SPACES }, { settings.KEEP_TRAILING_SPACES = it }),
        IntAttribute("LIST_ALIGN_NUMERIC", { settings.LIST_ALIGN_NUMERIC }, { settings.LIST_ALIGN_NUMERIC = it }),
        IntAttribute("LIST_ORDERED_TASK_ITEM_PRIORITY", { settings.LIST_ORDERED_TASK_ITEM_PRIORITY }, { settings.LIST_ORDERED_TASK_ITEM_PRIORITY = it }),
        IntAttribute("LIST_SPACING", { settings.LIST_SPACING }, { settings.LIST_SPACING = it }),
        IntAttribute("MACRO_PLACEMENT", { settings.MACRO_PLACEMENT }, { settings.MACRO_PLACEMENT = it }),
        IntAttribute("MACRO_SORT", { settings.MACRO_SORT }, { settings.MACRO_SORT = it }),
        IntAttribute("NEW_BULLET_LIST_ITEM_MARKER", { settings.NEW_BULLET_LIST_ITEM_MARKER }, { settings.NEW_BULLET_LIST_ITEM_MARKER = it }),
        IntAttribute("TASK_ITEM_CONTINUATION", { settings.TASK_ITEM_CONTINUATION }, { settings.TASK_ITEM_CONTINUATION = it }),
        IntAttribute("REFERENCE_PLACEMENT", { settings.REFERENCE_PLACEMENT }, { settings.REFERENCE_PLACEMENT = it }),
        IntAttribute("REFERENCE_SORT", { settings.REFERENCE_SORT }, { settings.REFERENCE_SORT = it }),
        IntAttribute("RIGHT_MARGIN", { settings.RIGHT_MARGIN }, { settings.RIGHT_MARGIN = it }),
        IntAttribute("SPACE_AFTER_ATX_MARKER", { settings.SPACE_AFTER_ATX_MARKER }, { settings.SPACE_AFTER_ATX_MARKER = it }),
        IntAttribute("TAB_SIZE", { settings.TAB_SIZE }, { settings.TAB_SIZE = it }),
        IntAttribute("TABLE_CAPTION", { settings.TABLE_CAPTION }, { settings.TABLE_CAPTION = it }),
        IntAttribute("TABLE_CAPTION_SPACES", { settings.TABLE_CAPTION_SPACES }, { settings.TABLE_CAPTION_SPACES = it }),
        IntAttribute("TABLE_LEFT_ALIGN_MARKER", { settings.TABLE_LEFT_ALIGN_MARKER }, { settings.TABLE_LEFT_ALIGN_MARKER = it }),
        IntAttribute("TASK_LIST_ITEM_CASE", { settings.TASK_LIST_ITEM_CASE }, { settings.TASK_LIST_ITEM_CASE = it }),
        IntAttribute("TASK_LIST_ITEM_PLACEMENT", { settings.TASK_LIST_ITEM_PLACEMENT }, { settings.TASK_LIST_ITEM_PLACEMENT = it }),
        IntAttribute("TOC_GENERATE_STRUCTURE", { settings.TOC_GENERATE_STRUCTURE }, { settings.TOC_GENERATE_STRUCTURE = it }),
        IntAttribute("TOC_HEADING_LEVELS", { settings.TOC_HEADING_LEVELS }, { settings.TOC_HEADING_LEVELS = it }),
        IntAttribute("TOC_TITLE_LEVEL", { settings.TOC_TITLE_LEVEL }, { settings.TOC_TITLE_LEVEL = it }),
        IntAttribute("TOC_UPDATE_ON_DOC_FORMAT", { settings.TOC_UPDATE_ON_DOC_FORMAT }, { settings.TOC_UPDATE_ON_DOC_FORMAT = it }),
        IntAttribute("WRAP_ON_TYPING", { settings.WRAP_ON_TYPING }, { settings.WRAP_ON_TYPING = it }),
        StringAttribute("TOC_TITLE", { settings.TOC_TITLE }, { settings.TOC_TITLE = it }),

        IntAttribute("FLEXMARK_EXAMPLE_KEEP_TRAILING_SPACES", true, { -2 }, { if (it != -2) settings.setTrailingSpacesOption(MdSpecExampleStripTrailingSpacesExtension.OPTION_ID, it); }),
        HashMapItem<String, Int>("TRAILING_SPACES_OPTIONS",
            { settings.trailingSpacesOptions },
            { settings.trailingSpacesOptions.clear(); settings.trailingSpacesOptions.putAll(it); },
            { key, value -> Pair(key, TrailingSpacesType.ADAPTER.get(value).displayName) },
            { key, value -> Pair(key, TrailingSpacesType.ADAPTER.findEnum(value).intValue) }
        )
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MdCodeStyleSettingsSerializable) return false

        return settings == other.settings
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + settings.hashCode()
        return result
    }
}
