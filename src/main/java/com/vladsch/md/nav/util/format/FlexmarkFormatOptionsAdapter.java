// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.format.CharWidthProvider;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import com.vladsch.md.nav.editor.api.MdFormatCustomizationProvider;
import com.vladsch.md.nav.language.DiscretionaryText;
import com.vladsch.md.nav.language.FormatWithSoftWrap;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.language.TableCaptionActionType;
import com.vladsch.md.nav.parser.PegdownOptionsAdapter;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class FlexmarkFormatOptionsAdapter {
    final @NotNull PsiEditContext myEditContext;
    final @NotNull MdRenderingProfile myRenderingProfile;
    final @NotNull MdCodeStyleSettings myStyleSettings;
    final @Nullable Editor myEditor;

    @NotNull CharWidthProvider myCharWidthProvider = CharWidthProvider.NULL;

    public FlexmarkFormatOptionsAdapter(@NotNull PsiEditContext editContext, int startOffset, int endOffset) {
        this.myEditContext = editContext;
        this.myRenderingProfile = editContext.getRenderingProfile();
        this.myStyleSettings = editContext.getStyleSettings();
        this.myEditor = editContext.getEditor();
        setCharWidthProvider(startOffset, endOffset);
    }

    private void setCharWidthProvider(int startOffset, int endOffset) {
        myCharWidthProvider = CharWidthProvider.NULL;

        if (myStyleSettings.USE_ACTUAL_CHAR_WIDTH) {
            for (MdFormatCustomizationProvider provider : MdFormatCustomizationProvider.EXTENSIONS.getValue()) {
                CharWidthProvider charWidthProvider = provider.createCharWidthProvider(myEditContext, startOffset, endOffset);
                if (charWidthProvider != null && charWidthProvider != CharWidthProvider.NULL) {
                    myCharWidthProvider = charWidthProvider;
                }
            }
        }
    }

    @NotNull
    public PsiEditContext getEditContext() {
        return myEditContext;
    }

    @NotNull
    public MdRenderingProfile getRenderingProfile() {
        return myRenderingProfile;
    }

    @NotNull
    public MdCodeStyleSettings getStyleSettings() {
        return myStyleSettings;
    }

    @Nullable
    public Editor getEditor() {
        return myEditor;
    }

    @NotNull
    public CharWidthProvider getCharWidthProvider() {
        return myCharWidthProvider;
    }

    @NotNull
    public MutableDataHolder getTableFormatOptions(@Nullable CharSequence tableIndentPrefix) {
        return getFormatOptions()
                .set(TablesExtension.FORMAT_TABLE_INDENT_PREFIX, tableIndentPrefix == null ? "" : tableIndentPrefix.toString());
    }

    @NotNull
    public MutableDataHolder getFormatOptions() {
        MutableDataHolder options = PegdownOptionsAdapter.flexmarkOptions(myRenderingProfile).toMutable();

        // Plugin Only Settings
        // SMART_EDIT_TABLES
        // SMART_EDIT_TABLE_SEPARATOR_LINE
        // INDENT_SIZE
        // TAB_SIZE
        // USE_TAB_CHARACTER
        // SMART_TABS
        // WRAP_ON_TYPING
        // SMART_EDIT_ATX_HEADER
        // SMART_EDIT_SETEXT_HEADER
        // SMART_ENTER_SETEXT_HEADER
        // SMART_ENTER_ATX_HEADER
        // TOC_FORMAT_ON_SAVE
        // NEW_BULLET_LIST_ITEM_MARKER
        // FORMAT_WITH_SOFT_WRAP

        // Passed indirectly
        // USE_ACTUAL_CHAR_WIDTH

        FormatWithSoftWrap formatWithSoftWrap = myStyleSettings.FORMAT_WITH_SOFT_WRAP();
        boolean isUseSoftWraps = myEditor != null && myEditor.getSettings().isUseSoftWraps();
        boolean useInfiniteMargins = isUseSoftWraps && formatWithSoftWrap == FormatWithSoftWrap.INFINITE_MARGIN;

        ASTNode frontMatterNode = myEditContext.getFrontMatterNode();
        boolean hasFrontMatter = frontMatterNode != null && frontMatterNode.getElementType() == MdTypes.JEKYLL_FRONT_MATTER_OPEN;
        boolean hasFlexmarkFrontMatter = frontMatterNode != null && frontMatterNode.getElementType() == MdTypes.FLEXMARK_FRONT_MATTER_OPEN;

        int keepBlankLines = hasFlexmarkFrontMatter ? Math.max(2, myStyleSettings.KEEP_BLANK_LINES) : myStyleSettings.KEEP_BLANK_LINES;
        options
                .set(Formatter.FORMAT_CHAR_WIDTH_PROVIDER, myCharWidthProvider)
                // FEATURE: add separate setting for trailing and max blank lines, also separate flexmark spec example differences
                .set(Formatter.MAX_TRAILING_BLANK_LINES, keepBlankLines)
                .set(Formatter.MAX_BLANK_LINES, keepBlankLines)
                .set(Formatter.FORMATTER_ON_TAG, myStyleSettings.getContainer().FORMATTER_ON_TAG)
                .set(Formatter.FORMATTER_OFF_TAG, myStyleSettings.getContainer().FORMATTER_OFF_TAG)
                .set(Formatter.FORMATTER_TAGS_ENABLED, myStyleSettings.getContainer().FORMATTER_TAGS_ENABLED)
                .set(Formatter.FORMATTER_TAGS_ACCEPT_REGEXP, myStyleSettings.getContainer().FORMATTER_TAGS_ACCEPT_REGEXP)

                // add link markup
                .set(Formatter.LINK_MARKER_COMMENT_PATTERN, Pattern.compile("^\\s*@IGNORE PREVIOUS:.*$"))

                // PARA_WRAP_TEXT
                .set(Formatter.RIGHT_MARGIN, myStyleSettings.PARA_WRAP_TEXT ? (useInfiniteMargins ? 50000 : myRenderingProfile.getRightMargin()) : 0)

                .set(TablesExtension.FORMAT_TABLE_LEAD_TRAIL_PIPES, myStyleSettings.TABLE_LEAD_TRAIL_PIPES)
                .set(TablesExtension.FORMAT_TABLE_SPACE_AROUND_PIPES, myStyleSettings.TABLE_SPACE_AROUND_PIPE)
                .set(TablesExtension.FORMAT_TABLE_ADJUST_COLUMN_WIDTH, myStyleSettings.TABLE_ADJUST_COLUMN_WIDTH)
                .set(TablesExtension.FORMAT_TABLE_APPLY_COLUMN_ALIGNMENT, myStyleSettings.TABLE_APPLY_COLUMN_ALIGNMENT)
                .set(TablesExtension.FORMAT_TABLE_FILL_MISSING_COLUMNS, myStyleSettings.TABLE_FILL_MISSING_COLUMNS)
                .set(TablesExtension.FORMAT_TABLE_CAPTION, TableCaptionActionType.ADAPTER.get(myStyleSettings.TABLE_CAPTION).flexMarkEnum)
                .set(TablesExtension.FORMAT_TABLE_CAPTION_SPACES, DiscretionaryText.ADAPTER.get(myStyleSettings.TABLE_CAPTION_SPACES).flexMarkEnum)
                .set(TablesExtension.FORMAT_TABLE_LEFT_ALIGN_MARKER, DiscretionaryText.ADAPTER.get(myStyleSettings.TABLE_LEFT_ALIGN_MARKER).flexMarkEnum)
                .set(TablesExtension.FORMAT_TABLE_MIN_SEPARATOR_COLUMN_WIDTH, 3)
                .set(TablesExtension.FORMAT_TABLE_MIN_SEPARATOR_DASHES, 3)
                .set(TablesExtension.FORMAT_TABLE_TRIM_CELL_WHITESPACE, myStyleSettings.TABLE_TRIM_CELLS || myStyleSettings.TABLE_APPLY_COLUMN_ALIGNMENT)

                .set(Formatter.LIST_ADD_BLANK_LINE_BEFORE, myStyleSettings.LIST_ADD_BLANK_LINE_BEFORE)
                .set(Formatter.LIST_RENUMBER_ITEMS, myStyleSettings.LIST_RENUMBER_ITEMS)
                .set(Formatter.LIST_BULLET_MARKER, myStyleSettings.BULLET_LIST_ITEM_MARKER().flexMarkEnum)
                .set(Formatter.LIST_ALIGN_NUMERIC, myStyleSettings.LIST_ALIGN_NUMERIC().flexMarkEnum)
                .set(Formatter.LIST_RESET_FIRST_ITEM_NUMBER, myStyleSettings.LIST_RESET_FIRST_ITEM_NUMBER)
                .set(Formatter.LIST_SPACING, myStyleSettings.LIST_SPACING().flexMarkEnum)

                .set(Formatter.SETEXT_HEADING_EQUALIZE_MARKER, myStyleSettings.SETEXT_HEADER_EQUALIZE_MARKER)
                .set(Formatter.ATX_HEADING_TRAILING_MARKER, myStyleSettings.ATX_HEADER_TRAILING_MARKER().flexMarkEnum)
                .set(Formatter.SPACE_AFTER_ATX_MARKER, myStyleSettings.SPACE_AFTER_ATX_MARKER().flexMarkEnum)
                .set(Formatter.HEADING_STYLE, myStyleSettings.HEADING_PREFERENCE().flexMarkEnum)

                .set(Formatter.REFERENCE_PLACEMENT, myStyleSettings.REFERENCE_PLACEMENT().flexMarkEnum)
                .set(Formatter.REFERENCE_SORT, myStyleSettings.REFERENCE_SORT().flexMarkEnum)

                .set(Formatter.ESCAPE_SPECIAL_CHARS, myStyleSettings.ESCAPE_SPECIAL_CHARS_ON_WRAP)
                .set(Formatter.ESCAPE_NUMBERED_LEAD_IN, myStyleSettings.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP)
                .set(Formatter.UNESCAPE_SPECIAL_CHARS, myStyleSettings.UNESCAPE_SPECIAL_CHARS_ON_WRAP)

                .set(Formatter.FENCED_CODE_MARKER_LENGTH, myStyleSettings.CODE_FENCE_MARKER_LENGTH)
                .set(Formatter.FENCED_CODE_MARKER_TYPE, myStyleSettings.CODE_FENCE_MARKER_TYPE().flexMarkEnum)
                .set(Formatter.FENCED_CODE_MATCH_CLOSING_MARKER, myStyleSettings.CODE_FENCE_MATCH_CLOSING_MARKER)
                .set(Formatter.FENCED_CODE_MINIMIZE_INDENT, myStyleSettings.CODE_FENCE_MINIMIZE_INDENT)
                .set(Formatter.FENCED_CODE_SPACE_BEFORE_INFO, myStyleSettings.CODE_FENCE_SPACE_BEFORE_INFO)
                .set(Formatter.INDENTED_CODE_MINIMIZE_INDENT, myStyleSettings.VERBATIM_MINIMIZE_INDENT)
                .set(Formatter.BLOCK_QUOTE_MARKERS, myStyleSettings.BLOCK_QUOTE_MARKERS().flexMarkEnum)

                .set(Formatter.KEEP_EXPLICIT_LINKS_AT_START, myStyleSettings.keepAtStartOfLine(MdTypes.EXPLICIT_LINK, hasFrontMatter))
                .set(Formatter.KEEP_IMAGE_LINKS_AT_START, myStyleSettings.keepAtStartOfLine(MdTypes.IMAGE, hasFrontMatter))

                // Task List Extension
                .set(TaskListExtension.FORMAT_LIST_ITEM_CASE, myStyleSettings.TASK_LIST_ITEM_CASE().flexMarkEnum)
                .set(TaskListExtension.FORMAT_LIST_ITEM_PLACEMENT, myStyleSettings.TASK_LIST_ITEM_PLACEMENT().flexMarkEnum)
                .set(Formatter.LISTS_ITEM_CONTENT_AFTER_SUFFIX, myStyleSettings.TASK_ITEM_CONTINUATION().isAlignToFirst()) // text indents after task item suffix
        ;

        // Allow customizations
        for (MdFormatCustomizationProvider provider : MdFormatCustomizationProvider.EXTENSIONS.getValue()) {
            provider.customizeFormatOptions(this, options);
        }

        return options;
    }
}
