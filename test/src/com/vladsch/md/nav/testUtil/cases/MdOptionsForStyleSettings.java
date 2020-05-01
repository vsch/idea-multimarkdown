/*
 * Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.md.nav.testUtil.cases;

import com.vladsch.flexmark.test.util.TestUtils;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.language.*;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.settings.TrailingSpacesType;
import com.vladsch.plugin.test.util.cases.SpecTest;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface MdOptionsForStyleSettings extends MdOptionsForParserSettings {
    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdOptionsForParserSettings.getOptionsMap());

                // style settings
                optionsMap.put("bullet-any", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.NEW_BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.ANY.intValue));
                optionsMap.put("bullet-dash", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.NEW_BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.DASH.intValue));
                optionsMap.put("bullet-asterisk", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.NEW_BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.ASTERISK.intValue));
                optionsMap.put("bullet-plus", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.NEW_BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.PLUS.intValue));
                optionsMap.put("tasks-done-any", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_LIST_ITEM_CASE = TaskListItemCaseType.AS_IS.intValue));
                optionsMap.put("tasks-done-upper", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_LIST_ITEM_CASE = TaskListItemCaseType.UPPERCASE.intValue));
                optionsMap.put("tasks-done-lower", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_LIST_ITEM_CASE = TaskListItemCaseType.LOWERCASE.intValue));

                // trailing spaces options
                optionsMap.put("trailing-keep-none", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_NONE.intValue));
                optionsMap.put("trailing-keep-all", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_ALL.intValue));
                optionsMap.put("trailing-keep-break", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_LINE_BREAK.intValue));
                optionsMap.put("code-keep-none", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_NONE.intValue));
                optionsMap.put("code-keep-all", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_ALL.intValue));
                optionsMap.put("code-keep-break", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_LINE_BREAK.intValue));

                // wrapping options
                optionsMap.put("wrap", new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.setWrapOnTyping(true)));
                optionsMap.put("no-wrap", new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.setWrapOnTyping(false)));

                // style settings boolean
                optionsMap.put("style-use-actual-char-width", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.USE_ACTUAL_CHAR_WIDTH = true));
                optionsMap.put("style-use-tab-character", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.USE_TAB_CHARACTER = true));
                optionsMap.put("style-smart-tabs", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_TABS = true));
                optionsMap.put("style-attributes-combine-consecutive", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTES_COMBINE_CONSECUTIVE = true));
                optionsMap.put("style-setext-header-equalize-marker", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SETEXT_HEADER_EQUALIZE_MARKER = true));
                optionsMap.put("style-smart-edit-atx-header", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_EDIT_ATX_HEADER = true));
                optionsMap.put("style-smart-edit-setext-header", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_EDIT_SETEXT_HEADER = true));
                optionsMap.put("style-smart-enter-setext-header", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_ENTER_SETEXT_HEADER = true));
                optionsMap.put("style-smart-enter-atx-header", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_ENTER_ATX_HEADER = true));
                optionsMap.put("style-escape-special-chars-on-wrap", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ESCAPE_SPECIAL_CHARS_ON_WRAP = true));
                optionsMap.put("style-escape-numbered-lead-in-on-wrap", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP = true));
                optionsMap.put("style-unescape-special-chars-on-wrap", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.UNESCAPE_SPECIAL_CHARS_ON_WRAP = true));
                optionsMap.put("style-para-wrap-text", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.PARA_WRAP_TEXT = true));
                optionsMap.put("style-list-add-blank-line-before", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_ADD_BLANK_LINE_BEFORE = true));
                optionsMap.put("style-list-renumber-items", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_RENUMBER_ITEMS = true));
                optionsMap.put("style-list-reset-first-item-number", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_RESET_FIRST_ITEM_NUMBER = true));
                optionsMap.put("style-verbatim-minimize-indent", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.VERBATIM_MINIMIZE_INDENT = true));
                optionsMap.put("style-code-fence-minimize-indent", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_MINIMIZE_INDENT = true));
                optionsMap.put("style-code-fence-match-closing-marker", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_MATCH_CLOSING_MARKER = true));
                optionsMap.put("style-code-fence-space-before-info", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_SPACE_BEFORE_INFO = true));
                optionsMap.put("style-smart-edit-tables", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_EDIT_TABLES = true));
                optionsMap.put("style-smart-edit-table-separator-line", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_EDIT_TABLE_SEPARATOR_LINE = true));
                optionsMap.put("style-table-lead-trail-pipes", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_LEAD_TRAIL_PIPES = true));
                optionsMap.put("style-table-space-around-pipe", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_SPACE_AROUND_PIPE = true));
                optionsMap.put("style-table-adjust-column-width", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_ADJUST_COLUMN_WIDTH = true));
                optionsMap.put("style-table-apply-column-alignment", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_APPLY_COLUMN_ALIGNMENT = true));
                optionsMap.put("style-table-fill-missing-columns", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_FILL_MISSING_COLUMNS = true));
                optionsMap.put("style-table-trim-cells", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_TRIM_CELLS = true));
                optionsMap.put("style-toc-format-on-save", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_FORMAT_ON_SAVE = true));
                optionsMap.put("style-toc-generate-html", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_HTML = true));
                optionsMap.put("style-toc-generate-text-only", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_TEXT_ONLY = true));
                optionsMap.put("style-toc-generate-numbered-list", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_NUMBERED_LIST = true));

                // style settings boolean:off
                optionsMap.put("style-no-use-actual-char-width", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.USE_ACTUAL_CHAR_WIDTH = false));
                optionsMap.put("style-no-use-tab-character", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.USE_TAB_CHARACTER = false));
                optionsMap.put("style-no-smart-tabs", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_TABS = false));
                optionsMap.put("style-no-attributes-combine-consecutive", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTES_COMBINE_CONSECUTIVE = false));
                optionsMap.put("style-no-setext-header-equalize-marker", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SETEXT_HEADER_EQUALIZE_MARKER = false));
                optionsMap.put("style-no-smart-edit-atx-header", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_EDIT_ATX_HEADER = false));
                optionsMap.put("style-no-smart-edit-setext-header", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_EDIT_SETEXT_HEADER = false));
                optionsMap.put("style-no-smart-enter-setext-header", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_ENTER_SETEXT_HEADER = false));
                optionsMap.put("style-no-smart-enter-atx-header", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_ENTER_ATX_HEADER = false));
                optionsMap.put("style-no-escape-special-chars-on-wrap", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ESCAPE_SPECIAL_CHARS_ON_WRAP = false));
                optionsMap.put("style-no-escape-numbered-lead-in-on-wrap", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP = false));
                optionsMap.put("style-no-unescape-special-chars-on-wrap", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.UNESCAPE_SPECIAL_CHARS_ON_WRAP = false));
                optionsMap.put("style-no-para-wrap-text", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.PARA_WRAP_TEXT = false));
                optionsMap.put("style-no-list-add-blank-line-before", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_ADD_BLANK_LINE_BEFORE = false));
                optionsMap.put("style-no-list-renumber-items", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_RENUMBER_ITEMS = false));
                optionsMap.put("style-no-list-reset-first-item-number", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_RESET_FIRST_ITEM_NUMBER = false));
                optionsMap.put("style-no-verbatim-minimize-indent", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.VERBATIM_MINIMIZE_INDENT = false));
                optionsMap.put("style-no-code-fence-minimize-indent", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_MINIMIZE_INDENT = false));
                optionsMap.put("style-no-code-fence-match-closing-marker", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_MATCH_CLOSING_MARKER = false));
                optionsMap.put("style-no-code-fence-space-before-info", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_SPACE_BEFORE_INFO = false));
                optionsMap.put("style-no-smart-edit-tables", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_EDIT_TABLES = false));
                optionsMap.put("style-no-smart-edit-table-separator-line", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SMART_EDIT_TABLE_SEPARATOR_LINE = false));
                optionsMap.put("style-no-table-lead-trail-pipes", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_LEAD_TRAIL_PIPES = false));
                optionsMap.put("style-no-table-space-around-pipe", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_SPACE_AROUND_PIPE = false));
                optionsMap.put("style-no-table-adjust-column-width", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_ADJUST_COLUMN_WIDTH = false));
                optionsMap.put("style-no-table-apply-column-alignment", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_APPLY_COLUMN_ALIGNMENT = false));
                optionsMap.put("style-no-table-fill-missing-columns", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_FILL_MISSING_COLUMNS = false));
                optionsMap.put("style-no-table-trim-cells", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_TRIM_CELLS = false));
                optionsMap.put("style-no-toc-format-on-save", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_FORMAT_ON_SAVE = false));
                optionsMap.put("style-no-toc-generate-html", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_HTML = false));
                optionsMap.put("style-no-toc-generate-text-only", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_TEXT_ONLY = false));

                // style placement
                optionsMap.put("style-place-abbreviations-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_PLACEMENT = ElementPlacementType.AS_IS.intValue));
                optionsMap.put("style-place-footnote-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_PLACEMENT = ElementPlacementType.AS_IS.intValue));
                optionsMap.put("style-place-macro-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_PLACEMENT = ElementPlacementType.AS_IS.intValue));
                optionsMap.put("style-place-reference-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_PLACEMENT = ElementPlacementType.AS_IS.intValue));
                optionsMap.put("style-place-enumerated-reference-format-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_PLACEMENT = ElementPlacementType.AS_IS.intValue));
                optionsMap.put("style-place-abbreviations-document-top", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_PLACEMENT = ElementPlacementType.DOCUMENT_TOP.intValue));
                optionsMap.put("style-place-footnote-document-top", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_PLACEMENT = ElementPlacementType.DOCUMENT_TOP.intValue));
                optionsMap.put("style-place-macro-document-top", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_PLACEMENT = ElementPlacementType.DOCUMENT_TOP.intValue));
                optionsMap.put("style-place-reference-document-top", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_PLACEMENT = ElementPlacementType.DOCUMENT_TOP.intValue));
                optionsMap.put("style-place-enumerated-reference-format-document-top", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_PLACEMENT = ElementPlacementType.DOCUMENT_TOP.intValue));
                optionsMap.put("style-place-abbreviations-group-with-first", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_PLACEMENT = ElementPlacementType.GROUP_WITH_FIRST.intValue));
                optionsMap.put("style-place-footnote-group-with-first", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_PLACEMENT = ElementPlacementType.GROUP_WITH_FIRST.intValue));
                optionsMap.put("style-place-macro-group-with-first", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_PLACEMENT = ElementPlacementType.GROUP_WITH_FIRST.intValue));
                optionsMap.put("style-place-reference-group-with-first", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_PLACEMENT = ElementPlacementType.GROUP_WITH_FIRST.intValue));
                optionsMap.put("style-place-enumerated-reference-format-group-with-first", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_PLACEMENT = ElementPlacementType.GROUP_WITH_FIRST.intValue));
                optionsMap.put("style-place-abbreviations-group-with-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_PLACEMENT = ElementPlacementType.GROUP_WITH_LAST.intValue));
                optionsMap.put("style-place-footnote-group-with-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_PLACEMENT = ElementPlacementType.GROUP_WITH_LAST.intValue));
                optionsMap.put("style-place-macro-group-with-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_PLACEMENT = ElementPlacementType.GROUP_WITH_LAST.intValue));
                optionsMap.put("style-place-reference-group-with-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_PLACEMENT = ElementPlacementType.GROUP_WITH_LAST.intValue));
                optionsMap.put("style-place-enumerated-reference-format-group-with-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_PLACEMENT = ElementPlacementType.GROUP_WITH_LAST.intValue));
                optionsMap.put("style-place-abbreviations-document-bottom", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_PLACEMENT = ElementPlacementType.DOCUMENT_BOTTOM.intValue));
                optionsMap.put("style-place-footnote-document-bottom", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_PLACEMENT = ElementPlacementType.DOCUMENT_BOTTOM.intValue));
                optionsMap.put("style-place-macro-document-bottom", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_PLACEMENT = ElementPlacementType.DOCUMENT_BOTTOM.intValue));
                optionsMap.put("style-place-reference-document-bottom", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_PLACEMENT = ElementPlacementType.DOCUMENT_BOTTOM.intValue));
                optionsMap.put("style-place-enumerated-reference-format-document-bottom", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_PLACEMENT = ElementPlacementType.DOCUMENT_BOTTOM.intValue));

                // style sort
                optionsMap.put("style-sort-abbreviations-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_SORT = ElementPlacementSortType.AS_IS.intValue));
                optionsMap.put("style-sort-footnote-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_SORT = ElementPlacementSortType.AS_IS.intValue));
                optionsMap.put("style-sort-macro-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_SORT = ElementPlacementSortType.AS_IS.intValue));
                optionsMap.put("style-sort-reference-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_SORT = ElementPlacementSortType.AS_IS.intValue));
                optionsMap.put("style-sort-enumerated-reference-format-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_SORT = ElementPlacementSortType.AS_IS.intValue));
                optionsMap.put("style-sort-abbreviations-sort", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_SORT = ElementPlacementSortType.SORT.intValue));
                optionsMap.put("style-sort-footnote-sort", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_SORT = ElementPlacementSortType.SORT.intValue));
                optionsMap.put("style-sort-macro-sort", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_SORT = ElementPlacementSortType.SORT.intValue));
                optionsMap.put("style-sort-reference-sort", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_SORT = ElementPlacementSortType.SORT.intValue));
                optionsMap.put("style-sort-enumerated-reference-format-sort", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_SORT = ElementPlacementSortType.SORT.intValue));
                optionsMap.put("style-sort-abbreviations-sort-unused-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_SORT = ElementPlacementSortType.SORT_UNUSED_LAST.intValue));
                optionsMap.put("style-sort-footnote-sort-unused-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_SORT = ElementPlacementSortType.SORT_UNUSED_LAST.intValue));
                optionsMap.put("style-sort-macro-sort-unused-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_SORT = ElementPlacementSortType.SORT_UNUSED_LAST.intValue));
                optionsMap.put("style-sort-reference-sort-unused-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_SORT = ElementPlacementSortType.SORT_UNUSED_LAST.intValue));
                optionsMap.put("style-sort-enumerated-reference-format-sort-unused-last", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_SORT = ElementPlacementSortType.SORT_UNUSED_LAST.intValue));
                optionsMap.put("style-sort-abbreviations-sort-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_SORT = ElementPlacementSortType.SORT_DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-footnote-sort-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_SORT = ElementPlacementSortType.SORT_DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-macro-sort-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_SORT = ElementPlacementSortType.SORT_DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-reference-sort-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_SORT = ElementPlacementSortType.SORT_DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-enumerated-reference-format-sort-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_SORT = ElementPlacementSortType.SORT_DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-abbreviations-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ABBREVIATIONS_SORT = ElementPlacementSortType.DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-footnote-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FOOTNOTE_SORT = ElementPlacementSortType.DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-macro-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.MACRO_SORT = ElementPlacementSortType.DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-reference-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.REFERENCE_SORT = ElementPlacementSortType.DELETE_UNUSED.intValue));
                optionsMap.put("style-sort-enumerated-reference-format-delete-unused", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ENUMERATED_REFERENCE_FORMAT_SORT = ElementPlacementSortType.DELETE_UNUSED.intValue));

                // DiscretionaryText
                optionsMap.put("style-attributes-spaces-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTES_SPACES = DiscretionaryText.AS_IS.intValue));
                optionsMap.put("style-attribute-equal-space-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_EQUAL_SPACE = DiscretionaryText.AS_IS.intValue));
                optionsMap.put("style-space-after-atx-marker-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SPACE_AFTER_ATX_MARKER = DiscretionaryText.AS_IS.intValue));
                optionsMap.put("style-table-left-align-marker-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_LEFT_ALIGN_MARKER = DiscretionaryText.AS_IS.intValue));
                optionsMap.put("style-table-caption-spaces-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_CAPTION_SPACES = DiscretionaryText.AS_IS.intValue));
                optionsMap.put("style-attributes-spaces-add", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTES_SPACES = DiscretionaryText.ADD.intValue));
                optionsMap.put("style-attribute-equal-space-add", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_EQUAL_SPACE = DiscretionaryText.ADD.intValue));
                optionsMap.put("style-space-after-atx-marker-add", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SPACE_AFTER_ATX_MARKER = DiscretionaryText.ADD.intValue));
                optionsMap.put("style-table-left-align-marker-add", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_LEFT_ALIGN_MARKER = DiscretionaryText.ADD.intValue));
                optionsMap.put("style-table-caption-spaces-add", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_CAPTION_SPACES = DiscretionaryText.ADD.intValue));
                optionsMap.put("style-attributes-spaces-remove", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTES_SPACES = DiscretionaryText.REMOVE.intValue));
                optionsMap.put("style-attribute-equal-space-remove", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_EQUAL_SPACE = DiscretionaryText.REMOVE.intValue));
                optionsMap.put("style-space-after-atx-marker-remove", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.SPACE_AFTER_ATX_MARKER = DiscretionaryText.REMOVE.intValue));
                optionsMap.put("style-table-left-align-marker-remove", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_LEFT_ALIGN_MARKER = DiscretionaryText.REMOVE.intValue));
                optionsMap.put("style-table-caption-spaces-remove", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_CAPTION_SPACES = DiscretionaryText.REMOVE.intValue));

                // enum options
                optionsMap.put("style-attribute-value-quotes-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_VALUE_QUOTES = AttributeValueQuotesType.AS_IS.intValue));
                optionsMap.put("style-attribute-value-quotes-no-quotes-single-preferred", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_VALUE_QUOTES = AttributeValueQuotesType.NO_QUOTES_SINGLE_PREFERRED.intValue));
                optionsMap.put("style-attribute-value-quotes-no-quotes-double-preferred", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_VALUE_QUOTES = AttributeValueQuotesType.NO_QUOTES_DOUBLE_PREFERRED.intValue));
                optionsMap.put("style-attribute-value-quotes-single-preferred", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_VALUE_QUOTES = AttributeValueQuotesType.SINGLE_PREFERRED.intValue));
                optionsMap.put("style-attribute-value-quotes-double-preferred", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_VALUE_QUOTES = AttributeValueQuotesType.DOUBLE_PREFERRED.intValue));
                optionsMap.put("style-attribute-value-quotes-single-quotes", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_VALUE_QUOTES = AttributeValueQuotesType.SINGLE_QUOTES.intValue));
                optionsMap.put("style-attribute-value-quotes-double-quotes", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATTRIBUTE_VALUE_QUOTES = AttributeValueQuotesType.DOUBLE_QUOTES.intValue));

                optionsMap.put("style-block-quote-first-line-markers-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.BLOCK_QUOTE_MARKERS = BlockQuoteMarkerOptions.AS_IS.intValue));
                optionsMap.put("style-block-quote-first-line-markers-add-compact", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.BLOCK_QUOTE_MARKERS = BlockQuoteMarkerOptions.ADD_COMPACT.intValue));
                optionsMap.put("style-block-quote-first-line-markers-add-compact-with-space", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.BLOCK_QUOTE_MARKERS = BlockQuoteMarkerOptions.ADD_COMPACT_WITH_SPACE.intValue));
                optionsMap.put("style-block-quote-first-line-markers-add-spaced", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.BLOCK_QUOTE_MARKERS = BlockQuoteMarkerOptions.ADD_SPACED.intValue));

                optionsMap.put("style-bullet-list-item-marker-any", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.ANY.intValue));
                optionsMap.put("style-bullet-list-item-marker-dash", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.DASH.intValue));
                optionsMap.put("style-bullet-list-item-marker-asterisk", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.ASTERISK.intValue));
                optionsMap.put("style-bullet-list-item-marker-plus", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.PLUS.intValue));

                optionsMap.put("style-code-fence-marker-type-any", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_MARKER_TYPE = CodeFenceMarkerType.ANY.intValue));
                optionsMap.put("style-code-fence-marker-type-backTick", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_MARKER_TYPE = CodeFenceMarkerType.BACK_TICK.intValue));
                optionsMap.put("style-code-fence-marker-type-tilde", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.CODE_FENCE_MARKER_TYPE = CodeFenceMarkerType.TILDE.intValue));

                optionsMap.put("style-para-continuation-align-to-first", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_ITEM_CONTINUATION = TaskItemContinuationType.ALIGN_TO_FIRST.intValue));
                optionsMap.put("style-para-continuation-align-to-item", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_ITEM_CONTINUATION = TaskItemContinuationType.ALIGN_TO_ITEM.intValue));

                optionsMap.put("style-definition-marker-type-any", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.DEFINITION_MARKER_TYPE = DefinitionMarkerType.ANY.intValue));
                optionsMap.put("style-definition-marker-type-colon", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.DEFINITION_MARKER_TYPE = DefinitionMarkerType.COLON.intValue));
                optionsMap.put("style-definition-marker-type-tilde", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.DEFINITION_MARKER_TYPE = DefinitionMarkerType.TILDE.intValue));

                optionsMap.put("style-list-align-numeric-none", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_ALIGN_NUMERIC = ElementAlignmentType.NONE.intValue));
                optionsMap.put("style-list-align-numeric-left-align", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_ALIGN_NUMERIC = ElementAlignmentType.LEFT_ALIGN.intValue));
                optionsMap.put("style-list-align-numeric-right-align", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_ALIGN_NUMERIC = ElementAlignmentType.RIGHT_ALIGN.intValue));

                optionsMap.put("style-list-ordered-task-item-priority-low", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_ORDERED_TASK_ITEM_PRIORITY = TaskItemPriorityType.LOW.intValue));
                optionsMap.put("style-list-ordered-task-item-priority-normal", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_ORDERED_TASK_ITEM_PRIORITY = TaskItemPriorityType.NORMAL.intValue));
                optionsMap.put("style-list-ordered-task-item-priority-high", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_ORDERED_TASK_ITEM_PRIORITY = TaskItemPriorityType.HIGH.intValue));

                optionsMap.put("style-format-with-soft-wrap-disabled", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FORMAT_WITH_SOFT_WRAP = FormatWithSoftWrap.DISABLED.intValue));
                optionsMap.put("style-format-with-soft-wrap-enabled", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FORMAT_WITH_SOFT_WRAP = FormatWithSoftWrap.ENABLED.intValue));
                optionsMap.put("style-format-with-soft-wrap-infinite-margin", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.FORMAT_WITH_SOFT_WRAP = FormatWithSoftWrap.INFINITE_MARGIN.intValue));

                optionsMap.put("style-heading-preference-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.HEADING_PREFERENCE = HeadingStyleType.AS_IS.intValue));
                optionsMap.put("style-heading-preference-atx-preferred", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.HEADING_PREFERENCE = HeadingStyleType.ATX_PREFERRED.intValue));
                optionsMap.put("style-heading-preference-setext-preferred", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.HEADING_PREFERENCE = HeadingStyleType.SETEXT_PREFERRED.intValue));

                optionsMap.put("style-keep-at-start-image-links-jekyll", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_AT_START_IMAGE_LINKS = KeepAtStartOfLine.JEKYLL.intValue));
                optionsMap.put("style-keep-at-start-image-links-none", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_AT_START_IMAGE_LINKS = KeepAtStartOfLine.NONE.intValue));
                optionsMap.put("style-keep-at-start-image-links-all", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_AT_START_IMAGE_LINKS = KeepAtStartOfLine.ALL.intValue));

                optionsMap.put("style-keep-at-start-explicit-link-jekyll", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_AT_START_EXPLICIT_LINK = KeepAtStartOfLine.JEKYLL.intValue));
                optionsMap.put("style-keep-at-start-explicit-link-none", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_AT_START_EXPLICIT_LINK = KeepAtStartOfLine.NONE.intValue));
                optionsMap.put("style-keep-at-start-explicit-link-all", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.KEEP_AT_START_EXPLICIT_LINK = KeepAtStartOfLine.ALL.intValue));

                optionsMap.put("style-list-spacing-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_SPACING = ListSpacingType.AS_IS.intValue));
                optionsMap.put("style-list-spacing-loosen", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_SPACING = ListSpacingType.LOOSEN.intValue));
                optionsMap.put("style-list-spacing-tighten", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_SPACING = ListSpacingType.TIGHTEN.intValue));
                optionsMap.put("style-list-spacing-loose", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_SPACING = ListSpacingType.LOOSE.intValue));
                optionsMap.put("style-list-spacing-tight", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.LIST_SPACING = ListSpacingType.TIGHT.intValue));

                optionsMap.put("style-table-caption-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_CAPTION = TableCaptionActionType.AS_IS.intValue));
                optionsMap.put("style-table-caption-add", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_CAPTION = TableCaptionActionType.ADD.intValue));
                optionsMap.put("style-table-caption-remove-empty", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_CAPTION = TableCaptionActionType.REMOVE_EMPTY.intValue));
                optionsMap.put("style-table-caption-remove", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TABLE_CAPTION = TableCaptionActionType.REMOVE.intValue));

                optionsMap.put("style-task-list-item-placement-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_LIST_ITEM_PLACEMENT = TaskListItemPlacementType.AS_IS.intValue));
                optionsMap.put("style-task-list-item-placement-incomplete-first", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_LIST_ITEM_PLACEMENT = TaskListItemPlacementType.INCOMPLETE_FIRST.intValue));
                optionsMap.put("style-task-list-item-placement-incomplete-nested-first", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_LIST_ITEM_PLACEMENT = TaskListItemPlacementType.INCOMPLETE_NESTED_FIRST.intValue));
                optionsMap.put("style-task-list-item-placement-complete-to-non-task", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_LIST_ITEM_PLACEMENT = TaskListItemPlacementType.COMPLETE_TO_NON_TASK.intValue));
                optionsMap.put("style-task-list-item-placement-complete-nested-to-non-task", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TASK_LIST_ITEM_PLACEMENT = TaskListItemPlacementType.COMPLETE_NESTED_TO_NON_TASK.intValue));

                optionsMap.put("style-toc-update-on-doc-format-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_UPDATE_ON_DOC_FORMAT = TocGenerateOnFormatType.AS_IS.intValue));
                optionsMap.put("style-toc-update-on-doc-format-update", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_UPDATE_ON_DOC_FORMAT = TocGenerateOnFormatType.UPDATE.intValue));
                optionsMap.put("style-toc-update-on-doc-format-remove", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_UPDATE_ON_DOC_FORMAT = TocGenerateOnFormatType.REMOVE.intValue));

                optionsMap.put("style-toc-generate-structure-hierarchy", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_STRUCTURE = TocGenerateStructureType.HIERARCHY.intValue));
                optionsMap.put("style-toc-generate-structure-flat", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_STRUCTURE = TocGenerateStructureType.FLAT.intValue));
                optionsMap.put("style-toc-generate-structure-flat-reversed", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_STRUCTURE = TocGenerateStructureType.FLAT_REVERSED.intValue));
                optionsMap.put("style-toc-generate-structure-sorted", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_STRUCTURE = TocGenerateStructureType.SORTED.intValue));
                optionsMap.put("style-toc-generate-structure-sorted-reversed", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.TOC_GENERATE_STRUCTURE = TocGenerateStructureType.SORTED_REVERSED.intValue));

                optionsMap.put("style-atx-header-trailing-marker-as-is", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATX_HEADER_TRAILING_MARKER = TrailingMarkerEqualizeOptions.AS_IS.intValue));
                optionsMap.put("style-atx-header-trailing-marker-add", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATX_HEADER_TRAILING_MARKER = TrailingMarkerEqualizeOptions.ADD.intValue));
                optionsMap.put("style-atx-header-trailing-marker-equalize", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATX_HEADER_TRAILING_MARKER = TrailingMarkerEqualizeOptions.EQUALIZE.intValue));
                optionsMap.put("style-atx-header-trailing-marker-remove", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.ATX_HEADER_TRAILING_MARKER = TrailingMarkerEqualizeOptions.REMOVE.intValue));

                // Integer options
                optionsMap.put("margin", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::marginOption)));
                optionsMap.put("style-right-margin", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::marginOption)));
                optionsMap.put("style-indent-size", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::indentSizeOption)));
                optionsMap.put("style-tab-size", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::tabSizeOption)));
                optionsMap.put("style-keep-blank-lines", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::keepBlankLinesOption)));
                optionsMap.put("style-code-fence-marker-length", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::codeFenceMarkerLengthOption)));
                optionsMap.put("style-definition-marker-spaces", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::definitionMarkerSpacesOption)));
                optionsMap.put("style-toc-heading-levels", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::tocHeadingLevelsOption)));
                optionsMap.put("style-toc-title-level", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customIntOption(option, params, MdOptionsForStyleSettings::tocTitleLevelOption)));

                // string options
                optionsMap.put("style-toc-title", new MutableDataSet().set(SpecTest.CUSTOM_OPTION, (option, params) -> TestUtils.customStringOption(option, params, MdOptionsForStyleSettings::tocTitleOption)));
            }
            return optionsMap;
        }
    }

    static DataHolder tocTitleOption(@Nullable String params) {
        if (params != null) {
            return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.TOC_TITLE = params);
        }

        throw new IllegalStateException("'type' option requires non-empty text argument");
    }

    static DataHolder marginOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.RIGHT_MARGIN = value);
    }

    static DataHolder indentSizeOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.INDENT_SIZE = value);
    }

    static DataHolder tabSizeOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.TAB_SIZE = value);
    }

    static DataHolder keepBlankLinesOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.KEEP_BLANK_LINES = value);
    }

    static DataHolder codeFenceMarkerLengthOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.CODE_FENCE_MARKER_LENGTH = value);
    }

    static DataHolder definitionMarkerSpacesOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.DEFINITION_MARKER_SPACES = value);
    }

    static DataHolder tocHeadingLevelsOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.TOC_HEADING_LEVELS = value);
    }

    static DataHolder tocTitleLevelOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.TOC_TITLE_LEVEL = value);
    }
}
