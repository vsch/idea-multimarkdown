// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleConstraints;
import com.intellij.psi.codeStyle.CodeStyleDefaults;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.util.misc.Pair;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesExtension;
import com.vladsch.md.nav.language.api.MdTrailingSpacesCodeStyleOption;
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.TrailingSpacesType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MdCodeStyleSettings extends CustomCodeStyleSettings {
    protected static final String SETTINGS_TAG_NAME = "MarkdownNavigatorCodeStyleSettings";
    final public static MdCodeStyleSettings DEFAULT = new MdCodeStyleSettings();
    public static final int USE_DEFAULT_RIGHT_MARGIN_VALUE = -2;

    public interface Holder {
        @NotNull
        MdCodeStyleSettings getStyleSettings();

        void setStyleSettings(@NotNull MdCodeStyleSettings styleSettings);
    }

    public int TRAILING_SPACES_OPTION_1 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_2 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_3 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_4 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_5 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_6 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_7 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_8 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_9 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_10 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_11 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_12 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_13 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_14 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_15 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_16 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_17 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_18 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_19 = TrailingSpacesType.KEEP_ALL.intValue;
    public int TRAILING_SPACES_OPTION_20 = TrailingSpacesType.KEEP_ALL.intValue;

    String TRAILING_SPACES_ID_1 = "";
    String TRAILING_SPACES_ID_2 = "";
    String TRAILING_SPACES_ID_3 = "";
    String TRAILING_SPACES_ID_4 = "";
    String TRAILING_SPACES_ID_5 = "";
    String TRAILING_SPACES_ID_6 = "";
    String TRAILING_SPACES_ID_7 = "";
    String TRAILING_SPACES_ID_8 = "";
    String TRAILING_SPACES_ID_9 = "";
    String TRAILING_SPACES_ID_10 = "";
    String TRAILING_SPACES_ID_11 = "";
    String TRAILING_SPACES_ID_12 = "";
    String TRAILING_SPACES_ID_13 = "";
    String TRAILING_SPACES_ID_14 = "";
    String TRAILING_SPACES_ID_15 = "";
    String TRAILING_SPACES_ID_16 = "";
    String TRAILING_SPACES_ID_17 = "";
    String TRAILING_SPACES_ID_18 = "";
    String TRAILING_SPACES_ID_19 = "";
    String TRAILING_SPACES_ID_20 = "";

    static class TrailingSpacesGetters {
        final static public TrailingSpacesGetters INSTANCE = new TrailingSpacesGetters();

        TrailingSpacesGetters() {
        }

        @SuppressWarnings("unchecked") final Function<MdCodeStyleSettings, Pair<String, Integer>>[] ourGetters = new Function[] {
// @formatter:off
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_1, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_1),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_2, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_2),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_3, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_3),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_4, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_4),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_5, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_5),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_6, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_6),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_7, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_7),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_8, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_8),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_9, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_9),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_10, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_10),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_11, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_11),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_12, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_12),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_13, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_13),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_14, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_14),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_15, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_15),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_16, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_16),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_17, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_17),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_18, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_18),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_19, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_19),
                (s)-> new Pair<>(((MdCodeStyleSettings)s).TRAILING_SPACES_ID_20, ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_20),

// @formatter:on
        };

        @SuppressWarnings("unchecked") final BiConsumer<MdCodeStyleSettings, Pair<String, Integer>>[] ourSetters = new BiConsumer[] {
// @formatter:off
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_1 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_1 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_2 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_2 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_3 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_3 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_4 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_4 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_5 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_5 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_6 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_6 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_7 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_7 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_8 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_8 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_9 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_9 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_10 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_10 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_11 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_11 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_12 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_12 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_13 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_13 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_14 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_14 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_15 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_15 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_16 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_16 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_17 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_17 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_18 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_18 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_19 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_19 = ((Pair<String, Integer>)p).getSecond(); },
                (s,p)-> {((MdCodeStyleSettings)s).TRAILING_SPACES_ID_20 = ((Pair<String, Integer>)p).getFirst(); ((MdCodeStyleSettings)s).TRAILING_SPACES_OPTION_20 = ((Pair<String, Integer>)p).getSecond(); },

// @formatter:on
        };
    }

    public static MdCodeStyleSettings getCustomSettings(CodeStyleSettings settings) {
        return settings.getCustomSettings(MdCodeStyleSettings.class);
    }

    protected MdCodeStyleSettings(String tagName, CodeStyleSettings settings) {
        super(tagName, settings);
        afterLoaded();
    }

    public MdCodeStyleSettings(CodeStyleSettings settings) {
        this(SETTINGS_TAG_NAME, settings);
    }

    // CAUTION: used for de-serialization and tests, do not use in code
    public MdCodeStyleSettings() {
        this(SETTINGS_TAG_NAME, new CodeStyleSettings(false));
    }

    @SuppressWarnings("deprecation")
    @NotNull
    public static MdCodeStyleSettings getInstance(@NotNull Project project) {
        MdCodeStyleSettings settings;

        try {
            settings = CodeStyle.getSettings(project).getCustomSettings(MdCodeStyleSettings.class);
        } catch (NoClassDefFoundError ignored) {
            // DEPRECATED: replacement CodeStyle#getSettings appeared in 2017-11-09
            //    change when 2017.1 is no longer supported use CodeStyle
            settings = com.intellij.psi.codeStyle.CodeStyleSettingsManager.getSettings(project).getCustomSettings(MdCodeStyleSettings.class);
        }
        settings.loadRightMargin();
        return settings;
    }

    protected void loadRightMargin() {
        if (!isBasicPlugin()) {
            CommonCodeStyleSettings commonCodeStyleSettings = getContainer().getCommonSettings(MdLanguage.INSTANCE);

            if (RIGHT_MARGIN != USE_DEFAULT_RIGHT_MARGIN_VALUE) {
                commonCodeStyleSettings.RIGHT_MARGIN = RIGHT_MARGIN;
            } else {
                RIGHT_MARGIN = commonCodeStyleSettings.RIGHT_MARGIN;
            }
        } else if (KEEP_TRAILING_SPACES == TrailingSpacesType.KEEP_LINE_BREAK.intValue) {
            // Fix defaults for basic plugin
            KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_ALL.intValue;
        }
    }

    public static boolean isBasicPlugin() {
        // KLUDGE: enhanced plugin test
        return MdBlockPrefixProvider.PROVIDER.getValue() == MdBlockPrefixProvider.DEFAULT.getValue();
    }

    @SuppressWarnings("deprecation")
    @NotNull
    public static MdCodeStyleSettings getInstance(@NotNull PsiFile psiFile) {
        MdCodeStyleSettings settings;

        try {
            settings = CodeStyle.getCustomSettings(psiFile, MdCodeStyleSettings.class);
        } catch (NoClassDefFoundError ignored) {
            // DEPRECATED: replacement CodeStyle#getSettings appeared in 2017-11-09
            //    change when 2017.1 is no longer supported use CodeStyle
            settings = com.intellij.psi.codeStyle.CodeStyleSettingsManager.getSettings(psiFile.getProject()).getCustomSettings(MdCodeStyleSettings.class);
        }
        settings.loadRightMargin();
        return settings;
    }

    public boolean isWrapOnTyping() {
        // turn off IDE handled wrap on typing
        getContainer().getCommonSettings(MdLanguage.INSTANCE).WRAP_ON_TYPING = WrapOnTyping.NO_WRAP.intValue;
        return WRAP_ON_TYPING == WrapOnTyping.WRAP.intValue || WRAP_ON_TYPING == WrapOnTyping.DEFAULT.intValue && getContainer().WRAP_WHEN_TYPING_REACHES_RIGHT_MARGIN;
    }

    public void setWrapOnTyping(boolean value) {
        // turn off IDE handled wrap on typing
        getContainer().getCommonSettings(MdLanguage.INSTANCE).WRAP_ON_TYPING = WrapOnTyping.NO_WRAP.intValue;
        WRAP_ON_TYPING = value ? WrapOnTyping.WRAP.intValue : WrapOnTyping.NO_WRAP.intValue;
    }

    public int getRightMargin() {
        return RIGHT_MARGIN == -2 ? getContainer().getRightMargin(MdLanguage.INSTANCE) : RIGHT_MARGIN;
    }

    @NotNull
    public String getSoftMargins() {
        List<Integer> softMargins = getContainer().getSoftMargins(MdLanguage.INSTANCE);
        return Utils.splice(softMargins.stream().map(String::valueOf).toArray(value -> new String[value]), ", ");
    }

    public void setSoftMargins(@NotNull String value) {
        String[] softMargins = value.split(",");
        List<Integer> integerList = new ArrayList<>();
        for (String softMargin : softMargins) {
            Integer integer = SequenceUtils.parseIntOrNull(softMargin.trim());
            if (integer != null) integerList.add(integer);
        }

        getContainer().setSoftMargins(MdLanguage.INSTANCE, integerList);
    }

    public static final int DEFAULT_KEEP_BLANK_LINES = 2;
    public static final int MIN_KEEP_BLANK_LINES = 1;
    public static final int MAX_KEEP_BLANK_LINES = 5;

    public static final int DEFAULT_CODE_FENCE_MARKER_LENGTH = 3;
    public static final int MIN_CODE_FENCE_MARKER_LENGTH = 3;
    public static final int MAX_CODE_FENCE_MARKER_LENGTH = 6;

    public static final int DEFAULT_RIGHT_MARGIN = 72;
    public static final int MIN_RIGHT_MARGIN = 0;
    public static final int MAX_RIGHT_MARGIN = CodeStyleConstraints.MAX_RIGHT_MARGIN;

    /**
     * @deprecated do not use directly, use getters and setters
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public int WRAP_ON_TYPING = WrapOnTyping.DEFAULT.intValue;

    public boolean USE_ACTUAL_CHAR_WIDTH = true;

    public int INDENT_SIZE = CodeStyleDefaults.DEFAULT_INDENT_SIZE;
    public int TAB_SIZE = CodeStyleDefaults.DEFAULT_TAB_SIZE;
    public boolean USE_TAB_CHARACTER = false;
    public boolean SMART_TABS = false;

    // Attributes
    public int ATTRIBUTES_SPACES = DiscretionaryText.AS_IS.intValue;

    public DiscretionaryText ATTRIBUTES_SPACES() { return DiscretionaryText.ADAPTER.get(ATTRIBUTES_SPACES); }

    public int ATTRIBUTE_VALUE_QUOTES = AttributeValueQuotesType.AS_IS.intValue;

    public AttributeValueQuotesType ATTRIBUTE_VALUE_QUOTES() { return AttributeValueQuotesType.ADAPTER.get(ATTRIBUTE_VALUE_QUOTES); }

    public int ATTRIBUTE_ID = AttributeImplicitIdType.AS_IS.intValue;

    public AttributeImplicitIdType ATTRIBUTE_ID() { return AttributeImplicitIdType.ADAPTER.get(ATTRIBUTE_ID); }

    public int ATTRIBUTE_CLASS = AttributeImplicitClassType.AS_IS.intValue;

    public AttributeImplicitClassType ATTRIBUTE_CLASS() { return AttributeImplicitClassType.ADAPTER.get(ATTRIBUTE_CLASS); }

    public int ATTRIBUTE_EQUAL_SPACE = DiscretionaryText.AS_IS.intValue;

    public DiscretionaryText ATTRIBUTE_EQUAL_SPACE() { return DiscretionaryText.ADAPTER.get(ATTRIBUTE_EQUAL_SPACE); }

    public boolean ATTRIBUTES_COMBINE_CONSECUTIVE = false;
    public boolean ATTRIBUTES_SORT = false;

    // headers
    public boolean SETEXT_HEADER_EQUALIZE_MARKER = true;
    public int HEADING_PREFERENCE = HeadingStyleType.AS_IS.intValue;

    public HeadingStyleType HEADING_PREFERENCE() { return HeadingStyleType.ADAPTER.get(HEADING_PREFERENCE); }

    public int ATX_HEADER_TRAILING_MARKER = TrailingMarkerEqualizeOptions.AS_IS.intValue;

    public TrailingMarkerEqualizeOptions ATX_HEADER_TRAILING_MARKER() { return TrailingMarkerEqualizeOptions.ADAPTER.get(ATX_HEADER_TRAILING_MARKER); }

    public int SPACE_AFTER_ATX_MARKER = DiscretionaryText.ADD.intValue;

    public DiscretionaryText SPACE_AFTER_ATX_MARKER() { return DiscretionaryText.ADAPTER.get(SPACE_AFTER_ATX_MARKER); }

    public boolean SMART_EDIT_ATX_HEADER = true;
    public boolean SMART_EDIT_SETEXT_HEADER = true;
    public boolean SMART_ENTER_SETEXT_HEADER = true;
    public boolean SMART_ENTER_ATX_HEADER = true;

    public boolean ESCAPE_SPECIAL_CHARS_ON_WRAP = true;
    public boolean ESCAPE_NUMBERED_LEAD_IN_ON_WRAP = true;
    public boolean UNESCAPE_SPECIAL_CHARS_ON_WRAP = true;

    public int KEEP_BLANK_LINES = DEFAULT_KEEP_BLANK_LINES;
    public int KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_LINE_BREAK.getIntValue();

    public TrailingSpacesType KEEP_TRAILING_SPACES() { return TrailingSpacesType.ADAPTER.get(KEEP_TRAILING_SPACES); }

    public int CODE_KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_ALL.getIntValue();

    public TrailingSpacesType CODE_KEEP_TRAILING_SPACES() { return TrailingSpacesType.ADAPTER.get(CODE_KEEP_TRAILING_SPACES); }

    public boolean PARA_WRAP_TEXT = true;
    public int FORMAT_WITH_SOFT_WRAP = FormatWithSoftWrap.ADAPTER.getDefault().getIntValue();

    public FormatWithSoftWrap FORMAT_WITH_SOFT_WRAP() { return FormatWithSoftWrap.ADAPTER.get(FORMAT_WITH_SOFT_WRAP); }

    public int TASK_ITEM_CONTINUATION = TaskItemContinuationType.ALIGN_TO_FIRST.getIntValue();

    public TaskItemContinuationType TASK_ITEM_CONTINUATION() { return TaskItemContinuationType.ADAPTER.get(TASK_ITEM_CONTINUATION); }

    // list formatting
    public boolean LIST_ADD_BLANK_LINE_BEFORE = false;
    public int LIST_ALIGN_NUMERIC = ElementAlignmentType.NONE.getIntValue();

    public ElementAlignmentType LIST_ALIGN_NUMERIC() { return ElementAlignmentType.ADAPTER.get(LIST_ALIGN_NUMERIC); }

    public boolean LIST_RENUMBER_ITEMS = true;
    public int TASK_LIST_ITEM_CASE = TaskListItemCaseType.AS_IS.getIntValue();

    public TaskListItemCaseType TASK_LIST_ITEM_CASE() { return TaskListItemCaseType.ADAPTER.get(TASK_LIST_ITEM_CASE); }

    public int TASK_LIST_ITEM_PLACEMENT = TaskListItemPlacementType.AS_IS.getIntValue();

    public TaskListItemPlacementType TASK_LIST_ITEM_PLACEMENT() { return TaskListItemPlacementType.ADAPTER.get(TASK_LIST_ITEM_PLACEMENT); }

    public int BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.ANY.getIntValue();

    public BulletListItemMarkerType BULLET_LIST_ITEM_MARKER() { return BulletListItemMarkerType.ADAPTER.get(BULLET_LIST_ITEM_MARKER); }

    public int NEW_BULLET_LIST_ITEM_MARKER = BulletListItemMarkerType.DASH.getIntValue();

    public BulletListItemMarkerType NEW_BULLET_LIST_ITEM_MARKER() { return BulletListItemMarkerType.ADAPTER.get(NEW_BULLET_LIST_ITEM_MARKER); }

    public int LIST_SPACING = ListSpacingType.AS_IS.getIntValue();

    public ListSpacingType LIST_SPACING() { return ListSpacingType.ADAPTER.get(LIST_SPACING); }

    public boolean LIST_RESET_FIRST_ITEM_NUMBER = false;
    public int LIST_ORDERED_TASK_ITEM_PRIORITY = TaskItemPriorityType.ADAPTER.getDefault().intValue;

    public TaskItemPriorityType LIST_ORDERED_TASK_ITEM_PRIORITY() { return TaskItemPriorityType.ADAPTER.get(LIST_ORDERED_TASK_ITEM_PRIORITY); }

    // verbatim and code fence
    public boolean VERBATIM_MINIMIZE_INDENT = false;
    public boolean CODE_FENCE_MINIMIZE_INDENT = false;
    public boolean CODE_FENCE_MATCH_CLOSING_MARKER = false;
    public boolean CODE_FENCE_SPACE_BEFORE_INFO = false;
    public int CODE_FENCE_MARKER_TYPE = CodeFenceMarkerType.ANY.getIntValue();

    public CodeFenceMarkerType CODE_FENCE_MARKER_TYPE() { return CodeFenceMarkerType.ADAPTER.get(CODE_FENCE_MARKER_TYPE); }

    public int CODE_FENCE_MARKER_LENGTH = DEFAULT_CODE_FENCE_MARKER_LENGTH;

    // definition marker
    public int DEFINITION_MARKER_TYPE = DefinitionMarkerType.ANY.getIntValue();

    public DefinitionMarkerType DEFINITION_MARKER_TYPE() { return DefinitionMarkerType.ADAPTER.get(DEFINITION_MARKER_TYPE); }

    public int DEFINITION_MARKER_SPACES = 3;

    // block quote
    public int BLOCK_QUOTE_MARKERS = BlockQuoteMarkerOptions.AS_IS.intValue;

    public BlockQuoteMarkerOptions BLOCK_QUOTE_MARKERS() { return BlockQuoteMarkerOptions.ADAPTER.get(BLOCK_QUOTE_MARKERS); }

    public int ABBREVIATIONS_PLACEMENT = ElementPlacementType.AS_IS.intValue;
    public int FOOTNOTE_PLACEMENT = ElementPlacementType.AS_IS.intValue;
    public int MACRO_PLACEMENT = ElementPlacementType.AS_IS.intValue;
    public int REFERENCE_PLACEMENT = ElementPlacementType.AS_IS.intValue;
    public int ENUMERATED_REFERENCE_FORMAT_PLACEMENT = ElementPlacementType.AS_IS.intValue;

    public ElementPlacementType ABBREVIATIONS_PLACEMENT() { return ElementPlacementType.ADAPTER.get(ABBREVIATIONS_PLACEMENT); }

    public ElementPlacementType FOOTNOTE_PLACEMENT() { return ElementPlacementType.ADAPTER.get(FOOTNOTE_PLACEMENT); }

    public ElementPlacementType MACRO_PLACEMENT() { return ElementPlacementType.ADAPTER.get(MACRO_PLACEMENT); }

    public ElementPlacementType REFERENCE_PLACEMENT() { return ElementPlacementType.ADAPTER.get(REFERENCE_PLACEMENT); }

    public ElementPlacementType ENUMERATED_REFERENCE_FORMAT_PLACEMENT() { return ElementPlacementType.ADAPTER.get(ENUMERATED_REFERENCE_FORMAT_PLACEMENT); }

    public int ABBREVIATIONS_SORT = ElementPlacementSortType.AS_IS.getIntValue();
    public int FOOTNOTE_SORT = ElementPlacementSortType.AS_IS.getIntValue();
    public int MACRO_SORT = ElementPlacementSortType.AS_IS.getIntValue();
    public int REFERENCE_SORT = ElementPlacementSortType.AS_IS.getIntValue();
    public int ENUMERATED_REFERENCE_FORMAT_SORT = ElementPlacementSortType.AS_IS.getIntValue();

    public ElementPlacementSortType ABBREVIATIONS_SORT() { return ElementPlacementSortType.ADAPTER.get(ABBREVIATIONS_SORT); }

    public ElementPlacementSortType FOOTNOTE_SORT() { return ElementPlacementSortType.ADAPTER.get(FOOTNOTE_SORT); }

    public ElementPlacementSortType MACRO_SORT() { return ElementPlacementSortType.ADAPTER.get(MACRO_SORT); }

    public ElementPlacementSortType REFERENCE_SORT() { return ElementPlacementSortType.ADAPTER.get(REFERENCE_SORT); }

    public ElementPlacementSortType ENUMERATED_REFERENCE_FORMAT_SORT() { return ElementPlacementSortType.ADAPTER.get(ENUMERATED_REFERENCE_FORMAT_SORT); }

    //tables
    public boolean SMART_EDIT_TABLES = false;
    public boolean SMART_EDIT_TABLE_SEPARATOR_LINE = true;
    public boolean TABLE_LEAD_TRAIL_PIPES = true;
    public boolean TABLE_SPACE_AROUND_PIPE = true;
    public boolean TABLE_ADJUST_COLUMN_WIDTH = true;
    public boolean TABLE_APPLY_COLUMN_ALIGNMENT = true;
    public boolean TABLE_FILL_MISSING_COLUMNS = true;
    public boolean TABLE_TRIM_CELLS = false;
    public int TABLE_LEFT_ALIGN_MARKER = DiscretionaryText.ADD.intValue;

    public DiscretionaryText TABLE_LEFT_ALIGN_MARKER() { return DiscretionaryText.ADAPTER.get(TABLE_LEFT_ALIGN_MARKER); }

    public int TABLE_CAPTION = TableCaptionActionType.AS_IS.intValue;
    public int TABLE_CAPTION_SPACES = DiscretionaryText.AS_IS.intValue;

    public DiscretionaryText TABLE_CAPTION_SPACES() { return DiscretionaryText.ADAPTER.get(TABLE_CAPTION_SPACES); }

    public int KEEP_AT_START_IMAGE_LINKS = KeepAtStartOfLine.JEKYLL.getIntValue();
    public int KEEP_AT_START_EXPLICIT_LINK = KeepAtStartOfLine.JEKYLL.getIntValue();

    public KeepAtStartOfLine KEEP_AT_START_IMAGE_LINKS() { return KeepAtStartOfLine.ADAPTER.get(KEEP_AT_START_IMAGE_LINKS); }

    public KeepAtStartOfLine KEEP_AT_START_EXPLICIT_LINK() { return KeepAtStartOfLine.ADAPTER.get(KEEP_AT_START_EXPLICIT_LINK); }

    // table of contents
    public int TOC_UPDATE_ON_DOC_FORMAT = TocGenerateOnFormatType.ADAPTER.getDefault().getIntValue();

    public TocGenerateOnFormatType TOC_UPDATE_ON_DOC_FORMAT() { return TocGenerateOnFormatType.ADAPTER.get(TOC_UPDATE_ON_DOC_FORMAT); }

    public boolean TOC_FORMAT_ON_SAVE = TocGenerateOnSaveType.ADAPTER.getDefault().getIntValue() == TocGenerateOnSaveType.FORMAT.getIntValue();

    public TocGenerateOnSaveType TOC_FORMAT_ON_SAVE() { return TocGenerateOnSaveType.ADAPTER.get(TOC_FORMAT_ON_SAVE); }

    public int TOC_HEADING_LEVELS = MdTocOptions.DEFAULT_LEVELS;
    public boolean TOC_GENERATE_HTML = false;
    public boolean TOC_GENERATE_TEXT_ONLY = false;
    public boolean TOC_GENERATE_NUMBERED_LIST = false;

    public int TOC_GENERATE_STRUCTURE = TocGenerateStructureType.ADAPTER.getDefault().getIntValue();

    public TocGenerateStructureType TOC_GENERATE_STRUCTURE() { return TocGenerateStructureType.ADAPTER.get(TOC_GENERATE_STRUCTURE); }

    public int TOC_TITLE_LEVEL = MdTocOptions.DEFAULT_TITLE_LEVEL;
    @NotNull public String TOC_TITLE = MdTocOptions.DEFAULT_TITLE;

    // -2 means disabled
    public int RIGHT_MARGIN = USE_DEFAULT_RIGHT_MARGIN_VALUE;

    // get enum types, for Kotlin
    // @formatter:off
    /* getAbbreviationsPlacementType */             public ElementPlacementType         getAbbreviationsPlacementType()               { return ABBREVIATIONS_PLACEMENT(); }
    /* getAbbreviationsSortType */                  public ElementPlacementSortType     getAbbreviationsSortType()                    { return ABBREVIATIONS_SORT(); }
    /* getAttributeEqualSpaceType */                public DiscretionaryText            getAttributeEqualSpaceType()                  { return ATTRIBUTE_EQUAL_SPACE(); }
    /* getAttributesSpacesType */                   public DiscretionaryText            getAttributesSpacesType()                     { return ATTRIBUTES_SPACES(); }
    /* getAttributeValueQuotesType */               public AttributeValueQuotesType     getAttributeValueQuotesType()                 { return ATTRIBUTE_VALUE_QUOTES(); }
    /* getBlockQuoteFirstLineMarkersType */         public BlockQuoteMarkerOptions      getBlockQuoteMarkersType()                    { return BLOCK_QUOTE_MARKERS(); }
    /* getBulletListItemMarkerType */               public BulletListItemMarkerType     getBulletListItemMarkerType()                 { return BULLET_LIST_ITEM_MARKER(); }
    /* getCodeFenceMarkerType */                    public CodeFenceMarkerType          getCodeFenceMarkerType()                      { return CODE_FENCE_MARKER_TYPE(); }
    /* getDefinitionMarkerType */                   public DefinitionMarkerType         getDefinitionMarkerType()                     { return DEFINITION_MARKER_TYPE(); }
    /* getCodeKeepTrailingSpacesType */             public TrailingSpacesType           getCodeKeepTrailingSpacesType()               { return CODE_KEEP_TRAILING_SPACES(); }
    /* getEnumeratedReferenceFormatPlacementType */ public ElementPlacementType         getEnumeratedReferenceFormatPlacementType()   { return ENUMERATED_REFERENCE_FORMAT_PLACEMENT(); }
    /* getEnumeratedReferenceFormatSortType */      public ElementPlacementSortType     getEnumeratedReferenceFormatSortType()        { return ENUMERATED_REFERENCE_FORMAT_SORT(); }
    /* getFootnotePlacementType */                  public ElementPlacementType         getFootnotePlacementType()                    { return FOOTNOTE_PLACEMENT(); }
    /* getFootnoteSortType */                       public ElementPlacementSortType     getFootnoteSortType()                         { return FOOTNOTE_SORT(); }
    /* getFormatWithSoftWrapType */                 public FormatWithSoftWrap           getFormatWithSoftWrapType()                   { return FORMAT_WITH_SOFT_WRAP(); }
    /* getHeadingPreferenceType */                  public HeadingStyleType             getHeadingPreferenceType()                    { return HEADING_PREFERENCE(); }
    /* getKeepAtStartExplicitLinkType */            public KeepAtStartOfLine            getKeepAtStartExplicitLinkType()              { return KEEP_AT_START_EXPLICIT_LINK(); }
    /* getKeepAtStartImageLinksType */              public KeepAtStartOfLine            getKeepAtStartImageLinksType()                { return KEEP_AT_START_IMAGE_LINKS(); }
    /* getKeepTrailingSpacesType */                 public TrailingSpacesType           getKeepTrailingSpacesType()                   { return KEEP_TRAILING_SPACES(); }
    /* getListSpacingType */                        public ListSpacingType              getListSpacingType()                          { return LIST_SPACING(); }
    /* getMacroSortType */                          public ElementPlacementSortType     getMacroSortType()                            { return MACRO_SORT(); }
    /* getNewBulletListItemMarkerType */            public BulletListItemMarkerType     getNewBulletListItemMarkerType()              { return NEW_BULLET_LIST_ITEM_MARKER(); }
    /* getParaContinuationAlignmentType */          public TaskItemContinuationType       getParaContinuationAlignmentType()          { return TASK_ITEM_CONTINUATION(); }
    /* getReferencePlacementType */                 public ElementPlacementType         getReferencePlacementType()                   { return REFERENCE_PLACEMENT(); }
    /* getReferenceSortType */                      public ElementPlacementSortType     getReferenceSortType()                        { return REFERENCE_SORT(); }
    /* getTableLeftAlignMarkerType */               public DiscretionaryText            getTableLeftAlignMarkerType()                 { return TABLE_LEFT_ALIGN_MARKER(); }
    /* getTaskListItemCaseType */                   public TaskListItemCaseType         getTaskListItemCaseType()                     { return TASK_LIST_ITEM_CASE(); }
    /* getTaskListItemPlacementType */              public TaskListItemPlacementType    getTaskListItemPlacementType()                { return TASK_LIST_ITEM_PLACEMENT(); }
    /* getTocGenerateStructureType */               public TocGenerateStructureType     getTocGenerateStructureType()                 { return TOC_GENERATE_STRUCTURE(); }
    /* getTocUpdateOnDocFormatType */               public TocGenerateOnFormatType      getTocUpdateOnDocFormatType()                 { return TOC_UPDATE_ON_DOC_FORMAT(); }
    // @formatter:on

    public MdTocOptions getTocSettings() {
        final TocGenerateStructureType structureType = TocGenerateStructureType.ADAPTER.get(TOC_GENERATE_STRUCTURE);

        return new MdTocOptions(
                TOC_UPDATE_ON_DOC_FORMAT,
                TOC_FORMAT_ON_SAVE,
                TOC_HEADING_LEVELS,
                TOC_GENERATE_HTML,
                TOC_GENERATE_TEXT_ONLY,
                TOC_GENERATE_NUMBERED_LIST,
                TOC_TITLE_LEVEL,
                TOC_TITLE,
                TocGenerateStructureType.ADAPTER.get(TOC_GENERATE_STRUCTURE).asTocListType()
        );
    }

    public void setTocSettings(MdTocOptions.AsMutable options) {
        setTocSettings(options.toImmutable());
    }

    public void setTocSettings(MdTocOptions options) {
        TOC_UPDATE_ON_DOC_FORMAT = options.onFormat;
        TOC_FORMAT_ON_SAVE = options.isFormatOnSave;
        TOC_HEADING_LEVELS = options.levels;
        TOC_GENERATE_HTML = options.isHtml;
        TOC_GENERATE_TEXT_ONLY = options.isTextOnly;
        TOC_GENERATE_NUMBERED_LIST = options.isNumbered;
        TOC_GENERATE_STRUCTURE = TocGenerateStructureType.get(options.listType).getIntValue();
        TOC_TITLE_LEVEL = options.titleLevel;
        TOC_TITLE = options.title;
    }

    public int keepAtStartOfLine(IElementType elementType) {
        if (elementType == MdTypes.IMAGE || elementType == MdTypes.IMAGE_LINK_REF_TEXT_OPEN)
            return KEEP_AT_START_IMAGE_LINKS;
        if (elementType == MdTypes.EXPLICIT_LINK || elementType == MdTypes.LINK_REF_TEXT_OPEN)
            return KEEP_AT_START_EXPLICIT_LINK;
        return KeepAtStartOfLine.NONE.getIntValue();
    }

    public boolean keepAtStartOfLine(IElementType elementType, boolean hasJekyllFrontMatter) {
        int keepAtStart = keepAtStartOfLine(elementType);
        if (keepAtStart == KeepAtStartOfLine.NONE.intValue) return false;
        if (keepAtStart == KeepAtStartOfLine.ALL.intValue) return true;
        return hasJekyllFrontMatter;
    }

    public Map<String, Integer> getTrailingSpacesOptions() {
        HashMap<String, Integer> options = new HashMap<>();
        Function<MdCodeStyleSettings, Pair<String, Integer>>[] ourGetters = TrailingSpacesGetters.INSTANCE.ourGetters;
        for (Function<MdCodeStyleSettings, Pair<String, Integer>> getter : ourGetters) {
            Pair<String, Integer> option = getter.apply(this);
            if (option != null && option.getFirst() != null && option.getFirst().isEmpty()) {
                options.put(option.getFirst(), option.getSecond());
            }
        }
        return options;
    }

    @Override
    protected void afterLoaded() {
        // need to order them into extension order
        HashMap<String, Integer> currentOptions = new HashMap<>(getTrailingSpacesOptions());
        LinkedHashMap<String, Integer> orderedOptions = new LinkedHashMap<>();

        for (MdTrailingSpacesCodeStyleOption styleOption : MdStripTrailingSpacesExtension.getOptions()) {
            if (currentOptions.containsKey(styleOption.getOptionId())) {
                orderedOptions.put(styleOption.getOptionId(), currentOptions.get(styleOption.getOptionId()));
                currentOptions.remove(styleOption.getOptionId());
            } else {
                orderedOptions.put(styleOption.getOptionId(), TrailingSpacesType.KEEP_ALL.intValue);
            }
        }

        // save unused values, maybe plugin disabled or not installed
        orderedOptions.putAll(currentOptions);

        int i = 0;
        BiConsumer<MdCodeStyleSettings, Pair<String, Integer>>[] ourSetters = TrailingSpacesGetters.INSTANCE.ourSetters;
        Function<MdCodeStyleSettings, Pair<String, Integer>>[] ourGetters = TrailingSpacesGetters.INSTANCE.ourGetters;

        for (Map.Entry<String, Integer> entry : orderedOptions.entrySet()) {
            if (i >= ourGetters.length) {
                throw new IllegalStateException(String.format("Maximum of trailing spaces custom options is %d, got option at index %d", ourGetters.length, i));
            }
            ourSetters[i++].accept(this, new Pair<>(entry.getKey(), entry.getValue()));
        }

        for (; i < ourGetters.length; i++) {
            ourSetters[i++].accept(this, new Pair<>("", 0));
        }

        loadRightMargin();
    }

    @NotNull
    public TrailingSpacesType getTrailingSpacesOption(@NotNull String id) {
        Function<MdCodeStyleSettings, Pair<String, Integer>>[] ourGetters = TrailingSpacesGetters.INSTANCE.ourGetters;
        for (Function<MdCodeStyleSettings, Pair<String, Integer>> getter : ourGetters) {
            Pair<String, Integer> option = getter.apply(this);
            if (option != null && option.getFirst() != null && option.getFirst().equals(id)) {
                return TrailingSpacesType.ADAPTER.get(option.getSecond());
            }
        }
        return TrailingSpacesType.KEEP_ALL;
    }

    public void setTrailingSpacesOption(@NotNull String id, TrailingSpacesType option) {
        getTrailingSpacesOptions().put(id, option.intValue);
    }

    public void setTrailingSpacesOption(@NotNull String id, int option) {
        setTrailingSpacesOption(id, TrailingSpacesType.ADAPTER.get(option));
    }

    public boolean isKeepLineBreakSpaces() {
        return KEEP_TRAILING_SPACES != TrailingSpacesType.KEEP_NONE.intValue;
    }

    public void setKeepTrailingSpaces(boolean value) {
        if (value) {
            KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_LINE_BREAK.intValue;
        } else {
            KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_NONE.intValue;
        }
    }

    public boolean getKeepVerbatimTrailingSpaces() {
        return CODE_KEEP_TRAILING_SPACES != TrailingSpacesType.KEEP_NONE.intValue;
    }

    public void setKeepVerbatimTrailingSpaces(boolean value) {
        if (value) {
            CODE_KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_ALL.intValue;
        } else {
            CODE_KEEP_TRAILING_SPACES = TrailingSpacesType.KEEP_NONE.intValue;
        }
    }

    public void copyFrom(MdCodeStyleSettings other) {
        copySettings(other, this);
    }

    public static void copySettings(MdCodeStyleSettings srcSettings, MdCodeStyleSettings dstSettings) {
        dstSettings.TRAILING_SPACES_OPTION_1 = srcSettings.TRAILING_SPACES_OPTION_1;
        dstSettings.TRAILING_SPACES_OPTION_2 = srcSettings.TRAILING_SPACES_OPTION_2;
        dstSettings.TRAILING_SPACES_OPTION_3 = srcSettings.TRAILING_SPACES_OPTION_3;
        dstSettings.TRAILING_SPACES_OPTION_4 = srcSettings.TRAILING_SPACES_OPTION_4;
        dstSettings.TRAILING_SPACES_OPTION_5 = srcSettings.TRAILING_SPACES_OPTION_5;
        dstSettings.TRAILING_SPACES_OPTION_6 = srcSettings.TRAILING_SPACES_OPTION_6;
        dstSettings.TRAILING_SPACES_OPTION_7 = srcSettings.TRAILING_SPACES_OPTION_7;
        dstSettings.TRAILING_SPACES_OPTION_8 = srcSettings.TRAILING_SPACES_OPTION_8;
        dstSettings.TRAILING_SPACES_OPTION_9 = srcSettings.TRAILING_SPACES_OPTION_9;
        dstSettings.TRAILING_SPACES_OPTION_10 = srcSettings.TRAILING_SPACES_OPTION_10;
        dstSettings.TRAILING_SPACES_OPTION_11 = srcSettings.TRAILING_SPACES_OPTION_11;
        dstSettings.TRAILING_SPACES_OPTION_12 = srcSettings.TRAILING_SPACES_OPTION_12;
        dstSettings.TRAILING_SPACES_OPTION_13 = srcSettings.TRAILING_SPACES_OPTION_13;
        dstSettings.TRAILING_SPACES_OPTION_14 = srcSettings.TRAILING_SPACES_OPTION_14;
        dstSettings.TRAILING_SPACES_OPTION_15 = srcSettings.TRAILING_SPACES_OPTION_15;
        dstSettings.TRAILING_SPACES_OPTION_16 = srcSettings.TRAILING_SPACES_OPTION_16;
        dstSettings.TRAILING_SPACES_OPTION_17 = srcSettings.TRAILING_SPACES_OPTION_17;
        dstSettings.TRAILING_SPACES_OPTION_18 = srcSettings.TRAILING_SPACES_OPTION_18;
        dstSettings.TRAILING_SPACES_OPTION_19 = srcSettings.TRAILING_SPACES_OPTION_19;
        dstSettings.TRAILING_SPACES_OPTION_20 = srcSettings.TRAILING_SPACES_OPTION_20;
        dstSettings.TRAILING_SPACES_ID_1 = srcSettings.TRAILING_SPACES_ID_1;
        dstSettings.TRAILING_SPACES_ID_2 = srcSettings.TRAILING_SPACES_ID_2;
        dstSettings.TRAILING_SPACES_ID_3 = srcSettings.TRAILING_SPACES_ID_3;
        dstSettings.TRAILING_SPACES_ID_4 = srcSettings.TRAILING_SPACES_ID_4;
        dstSettings.TRAILING_SPACES_ID_5 = srcSettings.TRAILING_SPACES_ID_5;
        dstSettings.TRAILING_SPACES_ID_6 = srcSettings.TRAILING_SPACES_ID_6;
        dstSettings.TRAILING_SPACES_ID_7 = srcSettings.TRAILING_SPACES_ID_7;
        dstSettings.TRAILING_SPACES_ID_8 = srcSettings.TRAILING_SPACES_ID_8;
        dstSettings.TRAILING_SPACES_ID_9 = srcSettings.TRAILING_SPACES_ID_9;
        dstSettings.TRAILING_SPACES_ID_10 = srcSettings.TRAILING_SPACES_ID_10;
        dstSettings.TRAILING_SPACES_ID_11 = srcSettings.TRAILING_SPACES_ID_11;
        dstSettings.TRAILING_SPACES_ID_12 = srcSettings.TRAILING_SPACES_ID_12;
        dstSettings.TRAILING_SPACES_ID_13 = srcSettings.TRAILING_SPACES_ID_13;
        dstSettings.TRAILING_SPACES_ID_14 = srcSettings.TRAILING_SPACES_ID_14;
        dstSettings.TRAILING_SPACES_ID_15 = srcSettings.TRAILING_SPACES_ID_15;
        dstSettings.TRAILING_SPACES_ID_16 = srcSettings.TRAILING_SPACES_ID_16;
        dstSettings.TRAILING_SPACES_ID_17 = srcSettings.TRAILING_SPACES_ID_17;
        dstSettings.TRAILING_SPACES_ID_18 = srcSettings.TRAILING_SPACES_ID_18;
        dstSettings.TRAILING_SPACES_ID_19 = srcSettings.TRAILING_SPACES_ID_19;
        dstSettings.TRAILING_SPACES_ID_20 = srcSettings.TRAILING_SPACES_ID_20;

        dstSettings.ABBREVIATIONS_PLACEMENT = srcSettings.ABBREVIATIONS_PLACEMENT;
        dstSettings.ABBREVIATIONS_SORT = srcSettings.ABBREVIATIONS_SORT;
        dstSettings.ATTRIBUTE_CLASS = srcSettings.ATTRIBUTE_CLASS;
        dstSettings.ATTRIBUTE_EQUAL_SPACE = srcSettings.ATTRIBUTE_EQUAL_SPACE;
        dstSettings.ATTRIBUTE_ID = srcSettings.ATTRIBUTE_ID;
        dstSettings.ATTRIBUTE_VALUE_QUOTES = srcSettings.ATTRIBUTE_VALUE_QUOTES;
        dstSettings.ATTRIBUTES_COMBINE_CONSECUTIVE = srcSettings.ATTRIBUTES_COMBINE_CONSECUTIVE;
        dstSettings.ATTRIBUTES_SORT = srcSettings.ATTRIBUTES_SORT;
        dstSettings.ATTRIBUTES_SPACES = srcSettings.ATTRIBUTES_SPACES;
        dstSettings.ATX_HEADER_TRAILING_MARKER = srcSettings.ATX_HEADER_TRAILING_MARKER;
        dstSettings.BLOCK_QUOTE_MARKERS = srcSettings.BLOCK_QUOTE_MARKERS;
        dstSettings.BULLET_LIST_ITEM_MARKER = srcSettings.BULLET_LIST_ITEM_MARKER;
        dstSettings.CODE_FENCE_MARKER_LENGTH = srcSettings.CODE_FENCE_MARKER_LENGTH;
        dstSettings.CODE_FENCE_MARKER_TYPE = srcSettings.CODE_FENCE_MARKER_TYPE;
        dstSettings.CODE_FENCE_MATCH_CLOSING_MARKER = srcSettings.CODE_FENCE_MATCH_CLOSING_MARKER;
        dstSettings.CODE_FENCE_MINIMIZE_INDENT = srcSettings.CODE_FENCE_MINIMIZE_INDENT;
        dstSettings.CODE_FENCE_SPACE_BEFORE_INFO = srcSettings.CODE_FENCE_SPACE_BEFORE_INFO;
        dstSettings.CODE_KEEP_TRAILING_SPACES = srcSettings.CODE_KEEP_TRAILING_SPACES;
        dstSettings.DEFINITION_MARKER_SPACES = srcSettings.DEFINITION_MARKER_SPACES;
        dstSettings.DEFINITION_MARKER_TYPE = srcSettings.DEFINITION_MARKER_TYPE;
        dstSettings.ENUMERATED_REFERENCE_FORMAT_PLACEMENT = srcSettings.ENUMERATED_REFERENCE_FORMAT_PLACEMENT;
        dstSettings.ENUMERATED_REFERENCE_FORMAT_SORT = srcSettings.ENUMERATED_REFERENCE_FORMAT_SORT;
        dstSettings.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP = srcSettings.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP;
        dstSettings.ESCAPE_SPECIAL_CHARS_ON_WRAP = srcSettings.ESCAPE_SPECIAL_CHARS_ON_WRAP;
        dstSettings.FOOTNOTE_PLACEMENT = srcSettings.FOOTNOTE_PLACEMENT;
        dstSettings.FOOTNOTE_SORT = srcSettings.FOOTNOTE_SORT;
        dstSettings.FORMAT_WITH_SOFT_WRAP = srcSettings.FORMAT_WITH_SOFT_WRAP;
        dstSettings.HEADING_PREFERENCE = srcSettings.HEADING_PREFERENCE;
        dstSettings.INDENT_SIZE = srcSettings.INDENT_SIZE;
        dstSettings.KEEP_AT_START_EXPLICIT_LINK = srcSettings.KEEP_AT_START_EXPLICIT_LINK;
        dstSettings.KEEP_AT_START_IMAGE_LINKS = srcSettings.KEEP_AT_START_IMAGE_LINKS;
        dstSettings.KEEP_BLANK_LINES = srcSettings.KEEP_BLANK_LINES;
        dstSettings.KEEP_TRAILING_SPACES = srcSettings.KEEP_TRAILING_SPACES;
        dstSettings.LIST_ADD_BLANK_LINE_BEFORE = srcSettings.LIST_ADD_BLANK_LINE_BEFORE;
        dstSettings.LIST_ALIGN_NUMERIC = srcSettings.LIST_ALIGN_NUMERIC;
        dstSettings.LIST_ORDERED_TASK_ITEM_PRIORITY = srcSettings.LIST_ORDERED_TASK_ITEM_PRIORITY;
        dstSettings.LIST_RENUMBER_ITEMS = srcSettings.LIST_RENUMBER_ITEMS;
        dstSettings.LIST_RESET_FIRST_ITEM_NUMBER = srcSettings.LIST_RESET_FIRST_ITEM_NUMBER;
        dstSettings.LIST_SPACING = srcSettings.LIST_SPACING;
        dstSettings.MACRO_PLACEMENT = srcSettings.MACRO_PLACEMENT;
        dstSettings.MACRO_SORT = srcSettings.MACRO_SORT;
        dstSettings.NEW_BULLET_LIST_ITEM_MARKER = srcSettings.NEW_BULLET_LIST_ITEM_MARKER;
        dstSettings.PARA_WRAP_TEXT = srcSettings.PARA_WRAP_TEXT;
        dstSettings.REFERENCE_PLACEMENT = srcSettings.REFERENCE_PLACEMENT;
        dstSettings.REFERENCE_SORT = srcSettings.REFERENCE_SORT;
        dstSettings.RIGHT_MARGIN = srcSettings.RIGHT_MARGIN;
        dstSettings.SETEXT_HEADER_EQUALIZE_MARKER = srcSettings.SETEXT_HEADER_EQUALIZE_MARKER;
        dstSettings.SMART_EDIT_ATX_HEADER = srcSettings.SMART_EDIT_ATX_HEADER;
        dstSettings.SMART_EDIT_SETEXT_HEADER = srcSettings.SMART_EDIT_SETEXT_HEADER;
        dstSettings.SMART_EDIT_TABLE_SEPARATOR_LINE = srcSettings.SMART_EDIT_TABLE_SEPARATOR_LINE;
        dstSettings.SMART_EDIT_TABLES = srcSettings.SMART_EDIT_TABLES;
        dstSettings.SMART_ENTER_ATX_HEADER = srcSettings.SMART_ENTER_ATX_HEADER;
        dstSettings.SMART_ENTER_SETEXT_HEADER = srcSettings.SMART_ENTER_SETEXT_HEADER;
        dstSettings.SMART_TABS = srcSettings.SMART_TABS;
        dstSettings.SPACE_AFTER_ATX_MARKER = srcSettings.SPACE_AFTER_ATX_MARKER;
        dstSettings.TAB_SIZE = srcSettings.TAB_SIZE;
        dstSettings.TABLE_ADJUST_COLUMN_WIDTH = srcSettings.TABLE_ADJUST_COLUMN_WIDTH;
        dstSettings.TABLE_APPLY_COLUMN_ALIGNMENT = srcSettings.TABLE_APPLY_COLUMN_ALIGNMENT;
        dstSettings.TABLE_CAPTION = srcSettings.TABLE_CAPTION;
        dstSettings.TABLE_CAPTION_SPACES = srcSettings.TABLE_CAPTION_SPACES;
        dstSettings.TABLE_FILL_MISSING_COLUMNS = srcSettings.TABLE_FILL_MISSING_COLUMNS;
        dstSettings.TABLE_LEAD_TRAIL_PIPES = srcSettings.TABLE_LEAD_TRAIL_PIPES;
        dstSettings.TABLE_LEFT_ALIGN_MARKER = srcSettings.TABLE_LEFT_ALIGN_MARKER;
        dstSettings.TABLE_SPACE_AROUND_PIPE = srcSettings.TABLE_SPACE_AROUND_PIPE;
        dstSettings.TABLE_TRIM_CELLS = srcSettings.TABLE_TRIM_CELLS;
        dstSettings.TASK_ITEM_CONTINUATION = srcSettings.TASK_ITEM_CONTINUATION;
        dstSettings.TASK_LIST_ITEM_CASE = srcSettings.TASK_LIST_ITEM_CASE;
        dstSettings.TASK_LIST_ITEM_PLACEMENT = srcSettings.TASK_LIST_ITEM_PLACEMENT;
        dstSettings.TOC_FORMAT_ON_SAVE = srcSettings.TOC_FORMAT_ON_SAVE;
        dstSettings.TOC_GENERATE_HTML = srcSettings.TOC_GENERATE_HTML;
        dstSettings.TOC_GENERATE_NUMBERED_LIST = srcSettings.TOC_GENERATE_NUMBERED_LIST;
        dstSettings.TOC_GENERATE_STRUCTURE = srcSettings.TOC_GENERATE_STRUCTURE;
        dstSettings.TOC_GENERATE_TEXT_ONLY = srcSettings.TOC_GENERATE_TEXT_ONLY;
        dstSettings.TOC_HEADING_LEVELS = srcSettings.TOC_HEADING_LEVELS;
        dstSettings.TOC_TITLE = srcSettings.TOC_TITLE;
        dstSettings.TOC_TITLE_LEVEL = srcSettings.TOC_TITLE_LEVEL;
        dstSettings.TOC_UPDATE_ON_DOC_FORMAT = srcSettings.TOC_UPDATE_ON_DOC_FORMAT;
        dstSettings.UNESCAPE_SPECIAL_CHARS_ON_WRAP = srcSettings.UNESCAPE_SPECIAL_CHARS_ON_WRAP;
        dstSettings.USE_ACTUAL_CHAR_WIDTH = srcSettings.USE_ACTUAL_CHAR_WIDTH;
        dstSettings.USE_TAB_CHARACTER = srcSettings.USE_TAB_CHARACTER;
        dstSettings.VERBATIM_MINIMIZE_INDENT = srcSettings.VERBATIM_MINIMIZE_INDENT;
        dstSettings.WRAP_ON_TYPING = srcSettings.WRAP_ON_TYPING;

        if (!compareEqual(dstSettings, srcSettings)) {
            String diff = getDiff(dstSettings, srcSettings);
        }

        // sync what is needed
        dstSettings.afterLoaded();
    }

    public static String getDiff(@NotNull MdCodeStyleSettings o1, @NotNull MdCodeStyleSettings o2) {
        StringBuilder sb = new StringBuilder();

        if (o1.TRAILING_SPACES_OPTION_1 != o2.TRAILING_SPACES_OPTION_1) sb.append("TRAILING_SPACES_OPTION_1 (").append(o1.TRAILING_SPACES_OPTION_1).append(" != ").append(o2.TRAILING_SPACES_OPTION_1).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_2 != o2.TRAILING_SPACES_OPTION_2) sb.append("TRAILING_SPACES_OPTION_2 (").append(o1.TRAILING_SPACES_OPTION_2).append(" != ").append(o2.TRAILING_SPACES_OPTION_2).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_3 != o2.TRAILING_SPACES_OPTION_3) sb.append("TRAILING_SPACES_OPTION_3 (").append(o1.TRAILING_SPACES_OPTION_3).append(" != ").append(o2.TRAILING_SPACES_OPTION_3).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_4 != o2.TRAILING_SPACES_OPTION_4) sb.append("TRAILING_SPACES_OPTION_4 (").append(o1.TRAILING_SPACES_OPTION_4).append(" != ").append(o2.TRAILING_SPACES_OPTION_4).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_5 != o2.TRAILING_SPACES_OPTION_5) sb.append("TRAILING_SPACES_OPTION_5 (").append(o1.TRAILING_SPACES_OPTION_5).append(" != ").append(o2.TRAILING_SPACES_OPTION_5).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_6 != o2.TRAILING_SPACES_OPTION_6) sb.append("TRAILING_SPACES_OPTION_6 (").append(o1.TRAILING_SPACES_OPTION_6).append(" != ").append(o2.TRAILING_SPACES_OPTION_6).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_7 != o2.TRAILING_SPACES_OPTION_7) sb.append("TRAILING_SPACES_OPTION_7 (").append(o1.TRAILING_SPACES_OPTION_7).append(" != ").append(o2.TRAILING_SPACES_OPTION_7).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_8 != o2.TRAILING_SPACES_OPTION_8) sb.append("TRAILING_SPACES_OPTION_8 (").append(o1.TRAILING_SPACES_OPTION_8).append(" != ").append(o2.TRAILING_SPACES_OPTION_8).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_9 != o2.TRAILING_SPACES_OPTION_9) sb.append("TRAILING_SPACES_OPTION_9 (").append(o1.TRAILING_SPACES_OPTION_9).append(" != ").append(o2.TRAILING_SPACES_OPTION_9).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_10 != o2.TRAILING_SPACES_OPTION_10) sb.append("TRAILING_SPACES_OPTION_10 (").append(o1.TRAILING_SPACES_OPTION_10).append(" != ").append(o2.TRAILING_SPACES_OPTION_10).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_11 != o2.TRAILING_SPACES_OPTION_11) sb.append("TRAILING_SPACES_OPTION_11 (").append(o1.TRAILING_SPACES_OPTION_11).append(" != ").append(o2.TRAILING_SPACES_OPTION_11).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_12 != o2.TRAILING_SPACES_OPTION_12) sb.append("TRAILING_SPACES_OPTION_12 (").append(o1.TRAILING_SPACES_OPTION_12).append(" != ").append(o2.TRAILING_SPACES_OPTION_12).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_13 != o2.TRAILING_SPACES_OPTION_13) sb.append("TRAILING_SPACES_OPTION_13 (").append(o1.TRAILING_SPACES_OPTION_13).append(" != ").append(o2.TRAILING_SPACES_OPTION_13).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_14 != o2.TRAILING_SPACES_OPTION_14) sb.append("TRAILING_SPACES_OPTION_14 (").append(o1.TRAILING_SPACES_OPTION_14).append(" != ").append(o2.TRAILING_SPACES_OPTION_14).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_15 != o2.TRAILING_SPACES_OPTION_15) sb.append("TRAILING_SPACES_OPTION_15 (").append(o1.TRAILING_SPACES_OPTION_15).append(" != ").append(o2.TRAILING_SPACES_OPTION_15).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_16 != o2.TRAILING_SPACES_OPTION_16) sb.append("TRAILING_SPACES_OPTION_16 (").append(o1.TRAILING_SPACES_OPTION_16).append(" != ").append(o2.TRAILING_SPACES_OPTION_16).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_17 != o2.TRAILING_SPACES_OPTION_17) sb.append("TRAILING_SPACES_OPTION_17 (").append(o1.TRAILING_SPACES_OPTION_17).append(" != ").append(o2.TRAILING_SPACES_OPTION_17).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_18 != o2.TRAILING_SPACES_OPTION_18) sb.append("TRAILING_SPACES_OPTION_18 (").append(o1.TRAILING_SPACES_OPTION_18).append(" != ").append(o2.TRAILING_SPACES_OPTION_18).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_19 != o2.TRAILING_SPACES_OPTION_19) sb.append("TRAILING_SPACES_OPTION_19 (").append(o1.TRAILING_SPACES_OPTION_19).append(" != ").append(o2.TRAILING_SPACES_OPTION_19).append(")\n");
        if (o1.TRAILING_SPACES_OPTION_20 != o2.TRAILING_SPACES_OPTION_20) sb.append("TRAILING_SPACES_OPTION_20 (").append(o1.TRAILING_SPACES_OPTION_20).append(" != ").append(o2.TRAILING_SPACES_OPTION_20).append(")\n");
        if (!o1.TRAILING_SPACES_ID_1.equals(o2.TRAILING_SPACES_ID_1)) sb.append("TRAILING_SPACES_ID_1 (").append(o1.TRAILING_SPACES_ID_1).append(" != ").append(o2.TRAILING_SPACES_ID_1).append(")\n");
        if (!o1.TRAILING_SPACES_ID_2.equals(o2.TRAILING_SPACES_ID_2)) sb.append("TRAILING_SPACES_ID_2 (").append(o1.TRAILING_SPACES_ID_2).append(" != ").append(o2.TRAILING_SPACES_ID_2).append(")\n");
        if (!o1.TRAILING_SPACES_ID_3.equals(o2.TRAILING_SPACES_ID_3)) sb.append("TRAILING_SPACES_ID_3 (").append(o1.TRAILING_SPACES_ID_3).append(" != ").append(o2.TRAILING_SPACES_ID_3).append(")\n");
        if (!o1.TRAILING_SPACES_ID_4.equals(o2.TRAILING_SPACES_ID_4)) sb.append("TRAILING_SPACES_ID_4 (").append(o1.TRAILING_SPACES_ID_4).append(" != ").append(o2.TRAILING_SPACES_ID_4).append(")\n");
        if (!o1.TRAILING_SPACES_ID_5.equals(o2.TRAILING_SPACES_ID_5)) sb.append("TRAILING_SPACES_ID_5 (").append(o1.TRAILING_SPACES_ID_5).append(" != ").append(o2.TRAILING_SPACES_ID_5).append(")\n");
        if (!o1.TRAILING_SPACES_ID_6.equals(o2.TRAILING_SPACES_ID_6)) sb.append("TRAILING_SPACES_ID_6 (").append(o1.TRAILING_SPACES_ID_6).append(" != ").append(o2.TRAILING_SPACES_ID_6).append(")\n");
        if (!o1.TRAILING_SPACES_ID_7.equals(o2.TRAILING_SPACES_ID_7)) sb.append("TRAILING_SPACES_ID_7 (").append(o1.TRAILING_SPACES_ID_7).append(" != ").append(o2.TRAILING_SPACES_ID_7).append(")\n");
        if (!o1.TRAILING_SPACES_ID_8.equals(o2.TRAILING_SPACES_ID_8)) sb.append("TRAILING_SPACES_ID_8 (").append(o1.TRAILING_SPACES_ID_8).append(" != ").append(o2.TRAILING_SPACES_ID_8).append(")\n");
        if (!o1.TRAILING_SPACES_ID_9.equals(o2.TRAILING_SPACES_ID_9)) sb.append("TRAILING_SPACES_ID_9 (").append(o1.TRAILING_SPACES_ID_9).append(" != ").append(o2.TRAILING_SPACES_ID_9).append(")\n");
        if (!o1.TRAILING_SPACES_ID_10.equals(o2.TRAILING_SPACES_ID_10)) sb.append("TRAILING_SPACES_ID_10 (").append(o1.TRAILING_SPACES_ID_10).append(" != ").append(o2.TRAILING_SPACES_ID_10).append(")\n");
        if (!o1.TRAILING_SPACES_ID_11.equals(o2.TRAILING_SPACES_ID_11)) sb.append("TRAILING_SPACES_ID_11 (").append(o1.TRAILING_SPACES_ID_11).append(" != ").append(o2.TRAILING_SPACES_ID_11).append(")\n");
        if (!o1.TRAILING_SPACES_ID_12.equals(o2.TRAILING_SPACES_ID_12)) sb.append("TRAILING_SPACES_ID_12 (").append(o1.TRAILING_SPACES_ID_12).append(" != ").append(o2.TRAILING_SPACES_ID_12).append(")\n");
        if (!o1.TRAILING_SPACES_ID_13.equals(o2.TRAILING_SPACES_ID_13)) sb.append("TRAILING_SPACES_ID_13 (").append(o1.TRAILING_SPACES_ID_13).append(" != ").append(o2.TRAILING_SPACES_ID_13).append(")\n");
        if (!o1.TRAILING_SPACES_ID_14.equals(o2.TRAILING_SPACES_ID_14)) sb.append("TRAILING_SPACES_ID_14 (").append(o1.TRAILING_SPACES_ID_14).append(" != ").append(o2.TRAILING_SPACES_ID_14).append(")\n");
        if (!o1.TRAILING_SPACES_ID_15.equals(o2.TRAILING_SPACES_ID_15)) sb.append("TRAILING_SPACES_ID_15 (").append(o1.TRAILING_SPACES_ID_15).append(" != ").append(o2.TRAILING_SPACES_ID_15).append(")\n");
        if (!o1.TRAILING_SPACES_ID_16.equals(o2.TRAILING_SPACES_ID_16)) sb.append("TRAILING_SPACES_ID_16 (").append(o1.TRAILING_SPACES_ID_16).append(" != ").append(o2.TRAILING_SPACES_ID_16).append(")\n");
        if (!o1.TRAILING_SPACES_ID_17.equals(o2.TRAILING_SPACES_ID_17)) sb.append("TRAILING_SPACES_ID_17 (").append(o1.TRAILING_SPACES_ID_17).append(" != ").append(o2.TRAILING_SPACES_ID_17).append(")\n");
        if (!o1.TRAILING_SPACES_ID_18.equals(o2.TRAILING_SPACES_ID_18)) sb.append("TRAILING_SPACES_ID_18 (").append(o1.TRAILING_SPACES_ID_18).append(" != ").append(o2.TRAILING_SPACES_ID_18).append(")\n");
        if (!o1.TRAILING_SPACES_ID_19.equals(o2.TRAILING_SPACES_ID_19)) sb.append("TRAILING_SPACES_ID_19 (").append(o1.TRAILING_SPACES_ID_19).append(" != ").append(o2.TRAILING_SPACES_ID_19).append(")\n");
        if (!o1.TRAILING_SPACES_ID_20.equals(o2.TRAILING_SPACES_ID_20)) sb.append("TRAILING_SPACES_ID_20 (").append(o1.TRAILING_SPACES_ID_20).append(" != ").append(o2.TRAILING_SPACES_ID_20).append(")\n");

// @formatter:off

        /*ABBREVIATIONS_PLACEMENT               */ if (o1.ABBREVIATIONS_PLACEMENT != o2.ABBREVIATIONS_PLACEMENT) sb.append("ABBREVIATIONS_PLACEMENT (").append(o1.ABBREVIATIONS_PLACEMENT).append(" != ").append(o2.ABBREVIATIONS_PLACEMENT).append(")\n");
        /*ABBREVIATIONS_SORT                    */ if (o1.ABBREVIATIONS_SORT != o2.ABBREVIATIONS_SORT) sb.append("ABBREVIATIONS_SORT (").append(o1.ABBREVIATIONS_SORT).append(" != ").append(o2.ABBREVIATIONS_SORT).append(")\n");
        /*ATTRIBUTE_CLASS                       */ if (o1.ATTRIBUTE_CLASS != o2.ATTRIBUTE_CLASS) sb.append("ATTRIBUTE_CLASS (").append(o1.ATTRIBUTE_CLASS).append(" != ").append(o2.ATTRIBUTE_CLASS).append(")\n");
        /*ATTRIBUTE_EQUAL_SPACE                 */ if (o1.ATTRIBUTE_EQUAL_SPACE != o2.ATTRIBUTE_EQUAL_SPACE) sb.append("ATTRIBUTE_EQUAL_SPACE (").append(o1.ATTRIBUTE_EQUAL_SPACE).append(" != ").append(o2.ATTRIBUTE_EQUAL_SPACE).append(")\n");
        /*ATTRIBUTE_ID                          */ if (o1.ATTRIBUTE_ID != o2.ATTRIBUTE_ID) sb.append("ATTRIBUTE_ID (").append(o1.ATTRIBUTE_ID).append(" != ").append(o2.ATTRIBUTE_ID).append(")\n");
        /*ATTRIBUTE_VALUE_QUOTES                */ if (o1.ATTRIBUTE_VALUE_QUOTES != o2.ATTRIBUTE_VALUE_QUOTES) sb.append("ATTRIBUTE_VALUE_QUOTES (").append(o1.ATTRIBUTE_VALUE_QUOTES).append(" != ").append(o2.ATTRIBUTE_VALUE_QUOTES).append(")\n");
        /*ATTRIBUTES_COMBINE_CONSECUTIVE        */ if (o1.ATTRIBUTES_COMBINE_CONSECUTIVE != o2.ATTRIBUTES_COMBINE_CONSECUTIVE) sb.append("ATTRIBUTES_COMBINE_CONSECUTIVE (").append(o1.ATTRIBUTES_COMBINE_CONSECUTIVE).append(" != ").append(o2.ATTRIBUTES_COMBINE_CONSECUTIVE).append(")\n");
        /*ATTRIBUTES_SORT                       */ if (o1.ATTRIBUTES_SORT != o2.ATTRIBUTES_SORT) sb.append("ATTRIBUTES_SORT (").append(o1.ATTRIBUTES_SORT).append(" != ").append(o2.ATTRIBUTES_SORT).append(")\n");
        /*ATTRIBUTES_SPACES                     */ if (o1.ATTRIBUTES_SPACES != o2.ATTRIBUTES_SPACES) sb.append("ATTRIBUTES_SPACES (").append(o1.ATTRIBUTES_SPACES).append(" != ").append(o2.ATTRIBUTES_SPACES).append(")\n");
        /*ATX_HEADER_TRAILING_MARKER            */ if (o1.ATX_HEADER_TRAILING_MARKER != o2.ATX_HEADER_TRAILING_MARKER) sb.append("ATX_HEADER_TRAILING_MARKER (").append(o1.ATX_HEADER_TRAILING_MARKER).append(" != ").append(o2.ATX_HEADER_TRAILING_MARKER).append(")\n");
        /*BLOCK_QUOTE_MARKERS                   */ if (o1.BLOCK_QUOTE_MARKERS != o2.BLOCK_QUOTE_MARKERS) sb.append("BLOCK_QUOTE_FIRST_LINE_MARKERS (").append(o1.BLOCK_QUOTE_MARKERS).append(" != ").append(o2.BLOCK_QUOTE_MARKERS).append(")\n");
        /*BULLET_LIST_ITEM_MARKER               */ if (o1.BULLET_LIST_ITEM_MARKER != o2.BULLET_LIST_ITEM_MARKER) sb.append("BULLET_LIST_ITEM_MARKER (").append(o1.BULLET_LIST_ITEM_MARKER).append(" != ").append(o2.BULLET_LIST_ITEM_MARKER).append(")\n");
        /*CODE_FENCE_MARKER_LENGTH              */ if (o1.CODE_FENCE_MARKER_LENGTH != o2.CODE_FENCE_MARKER_LENGTH) sb.append("CODE_FENCE_MARKER_LENGTH (").append(o1.CODE_FENCE_MARKER_LENGTH).append(" != ").append(o2.CODE_FENCE_MARKER_LENGTH).append(")\n");
        /*CODE_FENCE_MARKER_TYPE                */ if (o1.CODE_FENCE_MARKER_TYPE != o2.CODE_FENCE_MARKER_TYPE) sb.append("CODE_FENCE_MARKER_TYPE (").append(o1.CODE_FENCE_MARKER_TYPE).append(" != ").append(o2.CODE_FENCE_MARKER_TYPE).append(")\n");
        /*CODE_FENCE_MATCH_CLOSING_MARKER       */ if (o1.CODE_FENCE_MATCH_CLOSING_MARKER != o2.CODE_FENCE_MATCH_CLOSING_MARKER) sb.append("CODE_FENCE_MATCH_CLOSING_MARKER (").append(o1.CODE_FENCE_MATCH_CLOSING_MARKER).append(" != ").append(o2.CODE_FENCE_MATCH_CLOSING_MARKER).append(")\n");
        /*CODE_FENCE_MINIMIZE_INDENT            */ if (o1.CODE_FENCE_MINIMIZE_INDENT != o2.CODE_FENCE_MINIMIZE_INDENT) sb.append("CODE_FENCE_MINIMIZE_INDENT (").append(o1.CODE_FENCE_MINIMIZE_INDENT).append(" != ").append(o2.CODE_FENCE_MINIMIZE_INDENT).append(")\n");
        /*CODE_FENCE_SPACE_BEFORE_INFO          */ if (o1.CODE_FENCE_SPACE_BEFORE_INFO != o2.CODE_FENCE_SPACE_BEFORE_INFO) sb.append("CODE_FENCE_SPACE_BEFORE_INFO (").append(o1.CODE_FENCE_SPACE_BEFORE_INFO).append(" != ").append(o2.CODE_FENCE_SPACE_BEFORE_INFO).append(")\n");
        /*CODE_KEEP_TRAILING_SPACES             */ if (o1.CODE_KEEP_TRAILING_SPACES != o2.CODE_KEEP_TRAILING_SPACES) sb.append("CODE_KEEP_TRAILING_SPACES (").append(o1.CODE_KEEP_TRAILING_SPACES).append(" != ").append(o2.CODE_KEEP_TRAILING_SPACES).append(")\n");
        /*DEFINITION_MARKER_SPACES              */ if (o1.DEFINITION_MARKER_SPACES != o2.DEFINITION_MARKER_SPACES) sb.append("DEFINITION_MARKER_SPACES (").append(o1.DEFINITION_MARKER_SPACES).append(" != ").append(o2.DEFINITION_MARKER_SPACES).append(")\n");
        /*DEFINITION_MARKER_TYPE                */ if (o1.DEFINITION_MARKER_TYPE != o2.DEFINITION_MARKER_TYPE) sb.append("DEFINITION_MARKER_TYPE (").append(o1.DEFINITION_MARKER_TYPE).append(" != ").append(o2.DEFINITION_MARKER_TYPE).append(")\n");
        /*ENUMERATED_REFERENCE_FORMAT_PLACEMENT */ if (o1.ENUMERATED_REFERENCE_FORMAT_PLACEMENT != o2.ENUMERATED_REFERENCE_FORMAT_PLACEMENT) sb.append("ENUMERATED_REFERENCE_FORMAT_PLACEMENT (").append(o1.ENUMERATED_REFERENCE_FORMAT_PLACEMENT).append(" != ").append(o2.ENUMERATED_REFERENCE_FORMAT_PLACEMENT).append(")\n");
        /*ENUMERATED_REFERENCE_FORMAT_SORT      */ if (o1.ENUMERATED_REFERENCE_FORMAT_SORT != o2.ENUMERATED_REFERENCE_FORMAT_SORT) sb.append("ENUMERATED_REFERENCE_FORMAT_SORT (").append(o1.ENUMERATED_REFERENCE_FORMAT_SORT).append(" != ").append(o2.ENUMERATED_REFERENCE_FORMAT_SORT).append(")\n");
        /*ESCAPE_NUMBERED_LEAD_IN_ON_WRAP       */ if (o1.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP != o2.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP) sb.append("ESCAPE_NUMBERED_LEAD_IN_ON_WRAP (").append(o1.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP).append(" != ").append(o2.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP).append(")\n");
        /*ESCAPE_SPECIAL_CHARS_ON_WRAP          */ if (o1.ESCAPE_SPECIAL_CHARS_ON_WRAP != o2.ESCAPE_SPECIAL_CHARS_ON_WRAP) sb.append("ESCAPE_SPECIAL_CHARS_ON_WRAP (").append(o1.ESCAPE_SPECIAL_CHARS_ON_WRAP).append(" != ").append(o2.ESCAPE_SPECIAL_CHARS_ON_WRAP).append(")\n");
        /*FOOTNOTE_PLACEMENT                    */ if (o1.FOOTNOTE_PLACEMENT != o2.FOOTNOTE_PLACEMENT) sb.append("FOOTNOTE_PLACEMENT (").append(o1.FOOTNOTE_PLACEMENT).append(" != ").append(o2.FOOTNOTE_PLACEMENT).append(")\n");
        /*FOOTNOTE_SORT                         */ if (o1.FOOTNOTE_SORT != o2.FOOTNOTE_SORT) sb.append("FOOTNOTE_SORT (").append(o1.FOOTNOTE_SORT).append(" != ").append(o2.FOOTNOTE_SORT).append(")\n");
        /*FORMAT_WITH_SOFT_WRAP                 */ if (o1.FORMAT_WITH_SOFT_WRAP != o2.FORMAT_WITH_SOFT_WRAP) sb.append("FORMAT_WITH_SOFT_WRAP (").append(o1.FORMAT_WITH_SOFT_WRAP).append(" != ").append(o2.FORMAT_WITH_SOFT_WRAP).append(")\n");
        /*HEADING_PREFERENCE                    */ if (o1.HEADING_PREFERENCE != o2.HEADING_PREFERENCE) sb.append("HEADING_PREFERENCE (").append(o1.HEADING_PREFERENCE).append(" != ").append(o2.HEADING_PREFERENCE).append(")\n");
        /*INDENT_SIZE                           */ if (o1.INDENT_SIZE != o2.INDENT_SIZE) sb.append("INDENT_SIZE (").append(o1.INDENT_SIZE).append(" != ").append(o2.INDENT_SIZE).append(")\n");
        /*KEEP_AT_START_EXPLICIT_LINK           */ if (o1.KEEP_AT_START_EXPLICIT_LINK != o2.KEEP_AT_START_EXPLICIT_LINK) sb.append("KEEP_AT_START_EXPLICIT_LINK (").append(o1.KEEP_AT_START_EXPLICIT_LINK).append(" != ").append(o2.KEEP_AT_START_EXPLICIT_LINK).append(")\n");
        /*KEEP_AT_START_IMAGE_LINKS             */ if (o1.KEEP_AT_START_IMAGE_LINKS != o2.KEEP_AT_START_IMAGE_LINKS) sb.append("KEEP_AT_START_IMAGE_LINKS (").append(o1.KEEP_AT_START_IMAGE_LINKS).append(" != ").append(o2.KEEP_AT_START_IMAGE_LINKS).append(")\n");
        /*KEEP_BLANK_LINES                      */ if (o1.KEEP_BLANK_LINES != o2.KEEP_BLANK_LINES) sb.append("KEEP_BLANK_LINES (").append(o1.KEEP_BLANK_LINES).append(" != ").append(o2.KEEP_BLANK_LINES).append(")\n");
        /*KEEP_TRAILING_SPACES                  */ if (o1.KEEP_TRAILING_SPACES != o2.KEEP_TRAILING_SPACES) sb.append("KEEP_TRAILING_SPACES (").append(o1.KEEP_TRAILING_SPACES).append(" != ").append(o2.KEEP_TRAILING_SPACES).append(")\n");
        /*LIST_ADD_BLANK_LINE_BEFORE            */ if (o1.LIST_ADD_BLANK_LINE_BEFORE != o2.LIST_ADD_BLANK_LINE_BEFORE) sb.append("LIST_ADD_BLANK_LINE_BEFORE (").append(o1.LIST_ADD_BLANK_LINE_BEFORE).append(" != ").append(o2.LIST_ADD_BLANK_LINE_BEFORE).append(")\n");
        /*LIST_ALIGN_NUMERIC                    */ if (o1.LIST_ALIGN_NUMERIC != o2.LIST_ALIGN_NUMERIC) sb.append("LIST_ALIGN_NUMERIC (").append(o1.LIST_ALIGN_NUMERIC).append(" != ").append(o2.LIST_ALIGN_NUMERIC).append(")\n");
        /*LIST_ORDERED_TASK_ITEM_PRIORITY       */ if (o1.LIST_ORDERED_TASK_ITEM_PRIORITY != o2.LIST_ORDERED_TASK_ITEM_PRIORITY) sb.append("LIST_ORDERED_TASK_ITEM_PRIORITY (").append(o1.LIST_ORDERED_TASK_ITEM_PRIORITY).append(" != ").append(o2.LIST_ORDERED_TASK_ITEM_PRIORITY).append(")\n");
        /*LIST_RENUMBER_ITEMS                   */ if (o1.LIST_RENUMBER_ITEMS != o2.LIST_RENUMBER_ITEMS) sb.append("LIST_RENUMBER_ITEMS (").append(o1.LIST_RENUMBER_ITEMS).append(" != ").append(o2.LIST_RENUMBER_ITEMS).append(")\n");
        /*LIST_RESET_FIRST_ITEM_NUMBER          */ if (o1.LIST_RESET_FIRST_ITEM_NUMBER != o2.LIST_RESET_FIRST_ITEM_NUMBER) sb.append("LIST_RESET_FIRST_ITEM_NUMBER (").append(o1.LIST_RESET_FIRST_ITEM_NUMBER).append(" != ").append(o2.LIST_RESET_FIRST_ITEM_NUMBER).append(")\n");
        /*LIST_SPACING                          */ if (o1.LIST_SPACING != o2.LIST_SPACING) sb.append("LIST_SPACING (").append(o1.LIST_SPACING).append(" != ").append(o2.LIST_SPACING).append(")\n");
        /*MACRO_PLACEMENT                       */ if (o1.MACRO_PLACEMENT != o2.MACRO_PLACEMENT) sb.append("MACRO_PLACEMENT (").append(o1.MACRO_PLACEMENT).append(" != ").append(o2.MACRO_PLACEMENT).append(")\n");
        /*MACRO_SORT                            */ if (o1.MACRO_SORT != o2.MACRO_SORT) sb.append("MACRO_SORT (").append(o1.MACRO_SORT).append(" != ").append(o2.MACRO_SORT).append(")\n");
        /*NEW_BULLET_LIST_ITEM_MARKER           */ if (o1.NEW_BULLET_LIST_ITEM_MARKER != o2.NEW_BULLET_LIST_ITEM_MARKER) sb.append("NEW_BULLET_LIST_ITEM_MARKER (").append(o1.NEW_BULLET_LIST_ITEM_MARKER).append(" != ").append(o2.NEW_BULLET_LIST_ITEM_MARKER).append(")\n");
        /*PARA_WRAP_TEXT                        */ if (o1.PARA_WRAP_TEXT != o2.PARA_WRAP_TEXT) sb.append("PARA_WRAP_TEXT (").append(o1.PARA_WRAP_TEXT).append(" != ").append(o2.PARA_WRAP_TEXT).append(")\n");
        /*REFERENCE_PLACEMENT                   */ if (o1.REFERENCE_PLACEMENT != o2.REFERENCE_PLACEMENT) sb.append("REFERENCE_PLACEMENT (").append(o1.REFERENCE_PLACEMENT).append(" != ").append(o2.REFERENCE_PLACEMENT).append(")\n");
        /*REFERENCE_SORT                        */ if (o1.REFERENCE_SORT != o2.REFERENCE_SORT) sb.append("REFERENCE_SORT (").append(o1.REFERENCE_SORT).append(" != ").append(o2.REFERENCE_SORT).append(")\n");
        /*RIGHT_MARGIN                          */ if (o1.RIGHT_MARGIN != o2.RIGHT_MARGIN) sb.append("RIGHT_MARGIN (").append(o1.RIGHT_MARGIN).append(" != ").append(o2.RIGHT_MARGIN).append(")\n");
        /*SETEXT_HEADER_EQUALIZE_MARKER         */ if (o1.SETEXT_HEADER_EQUALIZE_MARKER != o2.SETEXT_HEADER_EQUALIZE_MARKER) sb.append("SETEXT_HEADER_EQUALIZE_MARKER (").append(o1.SETEXT_HEADER_EQUALIZE_MARKER).append(" != ").append(o2.SETEXT_HEADER_EQUALIZE_MARKER).append(")\n");
        /*SMART_EDIT_ATX_HEADER                 */ if (o1.SMART_EDIT_ATX_HEADER != o2.SMART_EDIT_ATX_HEADER) sb.append("SMART_EDIT_ATX_HEADER (").append(o1.SMART_EDIT_ATX_HEADER).append(" != ").append(o2.SMART_EDIT_ATX_HEADER).append(")\n");
        /*SMART_EDIT_SETEXT_HEADER              */ if (o1.SMART_EDIT_SETEXT_HEADER != o2.SMART_EDIT_SETEXT_HEADER) sb.append("SMART_EDIT_SETEXT_HEADER (").append(o1.SMART_EDIT_SETEXT_HEADER).append(" != ").append(o2.SMART_EDIT_SETEXT_HEADER).append(")\n");
        /*SMART_EDIT_TABLE_SEPARATOR_LINE       */ if (o1.SMART_EDIT_TABLE_SEPARATOR_LINE != o2.SMART_EDIT_TABLE_SEPARATOR_LINE) sb.append("SMART_EDIT_TABLE_SEPARATOR_LINE (").append(o1.SMART_EDIT_TABLE_SEPARATOR_LINE).append(" != ").append(o2.SMART_EDIT_TABLE_SEPARATOR_LINE).append(")\n");
        /*SMART_EDIT_TABLES                     */ if (o1.SMART_EDIT_TABLES != o2.SMART_EDIT_TABLES) sb.append("SMART_EDIT_TABLES (").append(o1.SMART_EDIT_TABLES).append(" != ").append(o2.SMART_EDIT_TABLES).append(")\n");
        /*SMART_ENTER_ATX_HEADER                */ if (o1.SMART_ENTER_ATX_HEADER != o2.SMART_ENTER_ATX_HEADER) sb.append("SMART_ENTER_ATX_HEADER (").append(o1.SMART_ENTER_ATX_HEADER).append(" != ").append(o2.SMART_ENTER_ATX_HEADER).append(")\n");
        /*SMART_ENTER_SETEXT_HEADER             */ if (o1.SMART_ENTER_SETEXT_HEADER != o2.SMART_ENTER_SETEXT_HEADER) sb.append("SMART_ENTER_SETEXT_HEADER (").append(o1.SMART_ENTER_SETEXT_HEADER).append(" != ").append(o2.SMART_ENTER_SETEXT_HEADER).append(")\n");
        /*SMART_TABS                            */ if (o1.SMART_TABS != o2.SMART_TABS) sb.append("SMART_TABS (").append(o1.SMART_TABS).append(" != ").append(o2.SMART_TABS).append(")\n");
        /*SPACE_AFTER_ATX_MARKER                */ if (o1.SPACE_AFTER_ATX_MARKER != o2.SPACE_AFTER_ATX_MARKER) sb.append("SPACE_AFTER_ATX_MARKER (").append(o1.SPACE_AFTER_ATX_MARKER).append(" != ").append(o2.SPACE_AFTER_ATX_MARKER).append(")\n");
        /*TAB_SIZE                              */ if (o1.TAB_SIZE != o2.TAB_SIZE) sb.append("TAB_SIZE (").append(o1.TAB_SIZE).append(" != ").append(o2.TAB_SIZE).append(")\n");
        /*TABLE_ADJUST_COLUMN_WIDTH             */ if (o1.TABLE_ADJUST_COLUMN_WIDTH != o2.TABLE_ADJUST_COLUMN_WIDTH) sb.append("TABLE_ADJUST_COLUMN_WIDTH (").append(o1.TABLE_ADJUST_COLUMN_WIDTH).append(" != ").append(o2.TABLE_ADJUST_COLUMN_WIDTH).append(")\n");
        /*TABLE_APPLY_COLUMN_ALIGNMENT          */ if (o1.TABLE_APPLY_COLUMN_ALIGNMENT != o2.TABLE_APPLY_COLUMN_ALIGNMENT) sb.append("TABLE_APPLY_COLUMN_ALIGNMENT (").append(o1.TABLE_APPLY_COLUMN_ALIGNMENT).append(" != ").append(o2.TABLE_APPLY_COLUMN_ALIGNMENT).append(")\n");
        /*TABLE_CAPTION                         */ if (o1.TABLE_CAPTION != o2.TABLE_CAPTION) sb.append("TABLE_CAPTION (").append(o1.TABLE_CAPTION).append(" != ").append(o2.TABLE_CAPTION).append(")\n");
        /*TABLE_CAPTION_SPACES                  */ if (o1.TABLE_CAPTION_SPACES != o2.TABLE_CAPTION_SPACES) sb.append("TABLE_CAPTION_SPACES (").append(o1.TABLE_CAPTION_SPACES).append(" != ").append(o2.TABLE_CAPTION_SPACES).append(")\n");
        /*TABLE_FILL_MISSING_COLUMNS            */ if (o1.TABLE_FILL_MISSING_COLUMNS != o2.TABLE_FILL_MISSING_COLUMNS) sb.append("TABLE_FILL_MISSING_COLUMNS (").append(o1.TABLE_FILL_MISSING_COLUMNS).append(" != ").append(o2.TABLE_FILL_MISSING_COLUMNS).append(")\n");
        /*TABLE_LEAD_TRAIL_PIPES                */ if (o1.TABLE_LEAD_TRAIL_PIPES != o2.TABLE_LEAD_TRAIL_PIPES) sb.append("TABLE_LEAD_TRAIL_PIPES (").append(o1.TABLE_LEAD_TRAIL_PIPES).append(" != ").append(o2.TABLE_LEAD_TRAIL_PIPES).append(")\n");
        /*TABLE_LEFT_ALIGN_MARKER               */ if (o1.TABLE_LEFT_ALIGN_MARKER != o2.TABLE_LEFT_ALIGN_MARKER) sb.append("TABLE_LEFT_ALIGN_MARKER (").append(o1.TABLE_LEFT_ALIGN_MARKER).append(" != ").append(o2.TABLE_LEFT_ALIGN_MARKER).append(")\n");
        /*TABLE_SPACE_AROUND_PIPE               */ if (o1.TABLE_SPACE_AROUND_PIPE != o2.TABLE_SPACE_AROUND_PIPE) sb.append("TABLE_SPACE_AROUND_PIPE (").append(o1.TABLE_SPACE_AROUND_PIPE).append(" != ").append(o2.TABLE_SPACE_AROUND_PIPE).append(")\n");
        /*TABLE_TRIM_CELLS                      */ if (o1.TABLE_TRIM_CELLS != o2.TABLE_TRIM_CELLS) sb.append("TABLE_TRIM_CELLS (").append(o1.TABLE_TRIM_CELLS).append(" != ").append(o2.TABLE_TRIM_CELLS).append(")\n");
        /*TASK_ITEM_CONTINUATION                */ if (o1.TASK_ITEM_CONTINUATION != o2.TASK_ITEM_CONTINUATION) sb.append("PARA_CONTINUATION_ALIGNMENT (").append(o1.TASK_ITEM_CONTINUATION).append(" != ").append(o2.TASK_ITEM_CONTINUATION).append(")\n");
        /*TASK_LIST_ITEM_CASE                   */ if (o1.TASK_LIST_ITEM_CASE != o2.TASK_LIST_ITEM_CASE) sb.append("TASK_LIST_ITEM_CASE (").append(o1.TASK_LIST_ITEM_CASE).append(" != ").append(o2.TASK_LIST_ITEM_CASE).append(")\n");
        /*TASK_LIST_ITEM_PLACEMENT              */ if (o1.TASK_LIST_ITEM_PLACEMENT != o2.TASK_LIST_ITEM_PLACEMENT) sb.append("TASK_LIST_ITEM_PLACEMENT (").append(o1.TASK_LIST_ITEM_PLACEMENT).append(" != ").append(o2.TASK_LIST_ITEM_PLACEMENT).append(")\n");
        /*TOC_FORMAT_ON_SAVE                    */ if (o1.TOC_FORMAT_ON_SAVE != o2.TOC_FORMAT_ON_SAVE) sb.append("TOC_FORMAT_ON_SAVE (").append(o1.TOC_FORMAT_ON_SAVE).append(" != ").append(o2.TOC_FORMAT_ON_SAVE).append(")\n");
        /*TOC_GENERATE_HTML                     */ if (o1.TOC_GENERATE_HTML != o2.TOC_GENERATE_HTML) sb.append("TOC_GENERATE_HTML (").append(o1.TOC_GENERATE_HTML).append(" != ").append(o2.TOC_GENERATE_HTML).append(")\n");
        /*TOC_GENERATE_NUMBERED_LIST            */ if (o1.TOC_GENERATE_NUMBERED_LIST != o2.TOC_GENERATE_NUMBERED_LIST) sb.append("TOC_GENERATE_NUMBERED_LIST (").append(o1.TOC_GENERATE_NUMBERED_LIST).append(" != ").append(o2.TOC_GENERATE_NUMBERED_LIST).append(")\n");
        /*TOC_GENERATE_STRUCTURE                */ if (o1.TOC_GENERATE_STRUCTURE != o2.TOC_GENERATE_STRUCTURE) sb.append("TOC_GENERATE_STRUCTURE (").append(o1.TOC_GENERATE_STRUCTURE).append(" != ").append(o2.TOC_GENERATE_STRUCTURE).append(")\n");
        /*TOC_GENERATE_TEXT_ONLY                */ if (o1.TOC_GENERATE_TEXT_ONLY != o2.TOC_GENERATE_TEXT_ONLY) sb.append("TOC_GENERATE_TEXT_ONLY (").append(o1.TOC_GENERATE_TEXT_ONLY).append(" != ").append(o2.TOC_GENERATE_TEXT_ONLY).append(")\n");
        /*TOC_HEADING_LEVELS                    */ if (o1.TOC_HEADING_LEVELS != o2.TOC_HEADING_LEVELS) sb.append("TOC_HEADING_LEVELS (").append(o1.TOC_HEADING_LEVELS).append(" != ").append(o2.TOC_HEADING_LEVELS).append(")\n");
        /*TOC_TITLE                             */ if (!o1.TOC_TITLE.equals(o2.TOC_TITLE)) sb.append("TOC_TITLE (").append(o1.TOC_TITLE).append(" != ").append(o2.TOC_TITLE).append(")\n");
        /*TOC_TITLE_LEVEL                       */ if (o1.TOC_TITLE_LEVEL != o2.TOC_TITLE_LEVEL) sb.append("TOC_TITLE_LEVEL (").append(o1.TOC_TITLE_LEVEL).append(" != ").append(o2.TOC_TITLE_LEVEL).append(")\n");
        /*TOC_UPDATE_ON_DOC_FORMAT              */ if (o1.TOC_UPDATE_ON_DOC_FORMAT != o2.TOC_UPDATE_ON_DOC_FORMAT) sb.append("TOC_UPDATE_ON_DOC_FORMAT (").append(o1.TOC_UPDATE_ON_DOC_FORMAT).append(" != ").append(o2.TOC_UPDATE_ON_DOC_FORMAT).append(")\n");
        /*UNESCAPE_SPECIAL_CHARS_ON_WRAP        */ if (o1.UNESCAPE_SPECIAL_CHARS_ON_WRAP != o2.UNESCAPE_SPECIAL_CHARS_ON_WRAP) sb.append("UNESCAPE_SPECIAL_CHARS_ON_WRAP (").append(o1.UNESCAPE_SPECIAL_CHARS_ON_WRAP).append(" != ").append(o2.UNESCAPE_SPECIAL_CHARS_ON_WRAP).append(")\n");
        /*USE_ACTUAL_CHAR_WIDTH                 */ if (o1.USE_ACTUAL_CHAR_WIDTH != o2.USE_ACTUAL_CHAR_WIDTH) sb.append("USE_ACTUAL_CHAR_WIDTH (").append(o1.USE_ACTUAL_CHAR_WIDTH).append(" != ").append(o2.USE_ACTUAL_CHAR_WIDTH).append(")\n");
        /*USE_TAB_CHARACTER                     */ if (o1.USE_TAB_CHARACTER != o2.USE_TAB_CHARACTER) sb.append("USE_TAB_CHARACTER (").append(o1.USE_TAB_CHARACTER).append(" != ").append(o2.USE_TAB_CHARACTER).append(")\n");
        /*VERBATIM_MINIMIZE_INDENT              */ if (o1.VERBATIM_MINIMIZE_INDENT != o2.VERBATIM_MINIMIZE_INDENT) sb.append("VERBATIM_MINIMIZE_INDENT (").append(o1.VERBATIM_MINIMIZE_INDENT).append(" != ").append(o2.VERBATIM_MINIMIZE_INDENT).append(")\n");
        /*WRAP_ON_TYPING                        */ if (o1.WRAP_ON_TYPING != o2.WRAP_ON_TYPING) sb.append("WRAP_ON_TYPING (").append(o1.WRAP_ON_TYPING).append(" != ").append(o2.WRAP_ON_TYPING).append(")\n");

// @formatter:on

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;

        MdCodeStyleSettings settings = (MdCodeStyleSettings) o;
        return compareEqual(this, settings);
    }

    public static boolean compareEqual(MdCodeStyleSettings o1, MdCodeStyleSettings o2) {
        if (o1 == o2) return true;

        if (o1.TRAILING_SPACES_OPTION_1 != o2.TRAILING_SPACES_OPTION_1) return false;
        if (o1.TRAILING_SPACES_OPTION_2 != o2.TRAILING_SPACES_OPTION_2) return false;
        if (o1.TRAILING_SPACES_OPTION_3 != o2.TRAILING_SPACES_OPTION_3) return false;
        if (o1.TRAILING_SPACES_OPTION_4 != o2.TRAILING_SPACES_OPTION_4) return false;
        if (o1.TRAILING_SPACES_OPTION_5 != o2.TRAILING_SPACES_OPTION_5) return false;
        if (o1.TRAILING_SPACES_OPTION_6 != o2.TRAILING_SPACES_OPTION_6) return false;
        if (o1.TRAILING_SPACES_OPTION_7 != o2.TRAILING_SPACES_OPTION_7) return false;
        if (o1.TRAILING_SPACES_OPTION_8 != o2.TRAILING_SPACES_OPTION_8) return false;
        if (o1.TRAILING_SPACES_OPTION_9 != o2.TRAILING_SPACES_OPTION_9) return false;
        if (o1.TRAILING_SPACES_OPTION_10 != o2.TRAILING_SPACES_OPTION_10) return false;
        if (o1.TRAILING_SPACES_OPTION_11 != o2.TRAILING_SPACES_OPTION_11) return false;
        if (o1.TRAILING_SPACES_OPTION_12 != o2.TRAILING_SPACES_OPTION_12) return false;
        if (o1.TRAILING_SPACES_OPTION_13 != o2.TRAILING_SPACES_OPTION_13) return false;
        if (o1.TRAILING_SPACES_OPTION_14 != o2.TRAILING_SPACES_OPTION_14) return false;
        if (o1.TRAILING_SPACES_OPTION_15 != o2.TRAILING_SPACES_OPTION_15) return false;
        if (o1.TRAILING_SPACES_OPTION_16 != o2.TRAILING_SPACES_OPTION_16) return false;
        if (o1.TRAILING_SPACES_OPTION_17 != o2.TRAILING_SPACES_OPTION_17) return false;
        if (o1.TRAILING_SPACES_OPTION_18 != o2.TRAILING_SPACES_OPTION_18) return false;
        if (o1.TRAILING_SPACES_OPTION_19 != o2.TRAILING_SPACES_OPTION_19) return false;
        if (o1.TRAILING_SPACES_OPTION_20 != o2.TRAILING_SPACES_OPTION_20) return false;
        if (!o1.TRAILING_SPACES_ID_1.equals(o2.TRAILING_SPACES_ID_1)) return false;
        if (!o1.TRAILING_SPACES_ID_2.equals(o2.TRAILING_SPACES_ID_2)) return false;
        if (!o1.TRAILING_SPACES_ID_3.equals(o2.TRAILING_SPACES_ID_3)) return false;
        if (!o1.TRAILING_SPACES_ID_4.equals(o2.TRAILING_SPACES_ID_4)) return false;
        if (!o1.TRAILING_SPACES_ID_5.equals(o2.TRAILING_SPACES_ID_5)) return false;
        if (!o1.TRAILING_SPACES_ID_6.equals(o2.TRAILING_SPACES_ID_6)) return false;
        if (!o1.TRAILING_SPACES_ID_7.equals(o2.TRAILING_SPACES_ID_7)) return false;
        if (!o1.TRAILING_SPACES_ID_8.equals(o2.TRAILING_SPACES_ID_8)) return false;
        if (!o1.TRAILING_SPACES_ID_9.equals(o2.TRAILING_SPACES_ID_9)) return false;
        if (!o1.TRAILING_SPACES_ID_10.equals(o2.TRAILING_SPACES_ID_10)) return false;
        if (!o1.TRAILING_SPACES_ID_11.equals(o2.TRAILING_SPACES_ID_11)) return false;
        if (!o1.TRAILING_SPACES_ID_12.equals(o2.TRAILING_SPACES_ID_12)) return false;
        if (!o1.TRAILING_SPACES_ID_13.equals(o2.TRAILING_SPACES_ID_13)) return false;
        if (!o1.TRAILING_SPACES_ID_14.equals(o2.TRAILING_SPACES_ID_14)) return false;
        if (!o1.TRAILING_SPACES_ID_15.equals(o2.TRAILING_SPACES_ID_15)) return false;
        if (!o1.TRAILING_SPACES_ID_16.equals(o2.TRAILING_SPACES_ID_16)) return false;
        if (!o1.TRAILING_SPACES_ID_17.equals(o2.TRAILING_SPACES_ID_17)) return false;
        if (!o1.TRAILING_SPACES_ID_18.equals(o2.TRAILING_SPACES_ID_18)) return false;
        if (!o1.TRAILING_SPACES_ID_19.equals(o2.TRAILING_SPACES_ID_19)) return false;
        if (!o1.TRAILING_SPACES_ID_20.equals(o2.TRAILING_SPACES_ID_20)) return false;

        if (o1.ABBREVIATIONS_PLACEMENT != o2.ABBREVIATIONS_PLACEMENT) return false;
        if (o1.ABBREVIATIONS_SORT != o2.ABBREVIATIONS_SORT) return false;
        if (o1.ATTRIBUTE_CLASS != o2.ATTRIBUTE_CLASS) return false;
        if (o1.ATTRIBUTE_EQUAL_SPACE != o2.ATTRIBUTE_EQUAL_SPACE) return false;
        if (o1.ATTRIBUTE_ID != o2.ATTRIBUTE_ID) return false;
        if (o1.ATTRIBUTE_VALUE_QUOTES != o2.ATTRIBUTE_VALUE_QUOTES) return false;
        if (o1.ATTRIBUTES_COMBINE_CONSECUTIVE != o2.ATTRIBUTES_COMBINE_CONSECUTIVE) return false;
        if (o1.ATTRIBUTES_SORT != o2.ATTRIBUTES_SORT) return false;
        if (o1.ATTRIBUTES_SPACES != o2.ATTRIBUTES_SPACES) return false;
        if (o1.ATX_HEADER_TRAILING_MARKER != o2.ATX_HEADER_TRAILING_MARKER) return false;
        if (o1.BLOCK_QUOTE_MARKERS != o2.BLOCK_QUOTE_MARKERS) return false;
        if (o1.BULLET_LIST_ITEM_MARKER != o2.BULLET_LIST_ITEM_MARKER) return false;
        if (o1.CODE_FENCE_MARKER_LENGTH != o2.CODE_FENCE_MARKER_LENGTH) return false;
        if (o1.CODE_FENCE_MARKER_TYPE != o2.CODE_FENCE_MARKER_TYPE) return false;
        if (o1.CODE_FENCE_MATCH_CLOSING_MARKER != o2.CODE_FENCE_MATCH_CLOSING_MARKER) return false;
        if (o1.CODE_FENCE_MINIMIZE_INDENT != o2.CODE_FENCE_MINIMIZE_INDENT) return false;
        if (o1.CODE_FENCE_SPACE_BEFORE_INFO != o2.CODE_FENCE_SPACE_BEFORE_INFO) return false;
        if (o1.CODE_KEEP_TRAILING_SPACES != o2.CODE_KEEP_TRAILING_SPACES) return false;
        if (o1.DEFINITION_MARKER_SPACES != o2.DEFINITION_MARKER_SPACES) return false;
        if (o1.DEFINITION_MARKER_TYPE != o2.DEFINITION_MARKER_TYPE) return false;
        if (o1.ENUMERATED_REFERENCE_FORMAT_PLACEMENT != o2.ENUMERATED_REFERENCE_FORMAT_PLACEMENT) return false;
        if (o1.ENUMERATED_REFERENCE_FORMAT_SORT != o2.ENUMERATED_REFERENCE_FORMAT_SORT) return false;
        if (o1.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP != o2.ESCAPE_NUMBERED_LEAD_IN_ON_WRAP) return false;
        if (o1.ESCAPE_SPECIAL_CHARS_ON_WRAP != o2.ESCAPE_SPECIAL_CHARS_ON_WRAP) return false;
        if (o1.FOOTNOTE_PLACEMENT != o2.FOOTNOTE_PLACEMENT) return false;
        if (o1.FOOTNOTE_SORT != o2.FOOTNOTE_SORT) return false;
        if (o1.FORMAT_WITH_SOFT_WRAP != o2.FORMAT_WITH_SOFT_WRAP) return false;
        if (o1.HEADING_PREFERENCE != o2.HEADING_PREFERENCE) return false;
        if (o1.INDENT_SIZE != o2.INDENT_SIZE) return false;
        if (o1.KEEP_AT_START_EXPLICIT_LINK != o2.KEEP_AT_START_EXPLICIT_LINK) return false;
        if (o1.KEEP_AT_START_IMAGE_LINKS != o2.KEEP_AT_START_IMAGE_LINKS) return false;
        if (o1.KEEP_BLANK_LINES != o2.KEEP_BLANK_LINES) return false;
        if (o1.KEEP_TRAILING_SPACES != o2.KEEP_TRAILING_SPACES) return false;
        if (o1.LIST_ADD_BLANK_LINE_BEFORE != o2.LIST_ADD_BLANK_LINE_BEFORE) return false;
        if (o1.LIST_ALIGN_NUMERIC != o2.LIST_ALIGN_NUMERIC) return false;
        if (o1.LIST_ORDERED_TASK_ITEM_PRIORITY != o2.LIST_ORDERED_TASK_ITEM_PRIORITY) return false;
        if (o1.LIST_RENUMBER_ITEMS != o2.LIST_RENUMBER_ITEMS) return false;
        if (o1.LIST_RESET_FIRST_ITEM_NUMBER != o2.LIST_RESET_FIRST_ITEM_NUMBER) return false;
        if (o1.LIST_SPACING != o2.LIST_SPACING) return false;
        if (o1.MACRO_PLACEMENT != o2.MACRO_PLACEMENT) return false;
        if (o1.MACRO_SORT != o2.MACRO_SORT) return false;
        if (o1.NEW_BULLET_LIST_ITEM_MARKER != o2.NEW_BULLET_LIST_ITEM_MARKER) return false;
        if (o1.PARA_WRAP_TEXT != o2.PARA_WRAP_TEXT) return false;
        if (o1.REFERENCE_PLACEMENT != o2.REFERENCE_PLACEMENT) return false;
        if (o1.REFERENCE_SORT != o2.REFERENCE_SORT) return false;
        if (o1.RIGHT_MARGIN != o2.RIGHT_MARGIN) return false;
        if (o1.SETEXT_HEADER_EQUALIZE_MARKER != o2.SETEXT_HEADER_EQUALIZE_MARKER) return false;
        if (o1.SMART_EDIT_ATX_HEADER != o2.SMART_EDIT_ATX_HEADER) return false;
        if (o1.SMART_EDIT_SETEXT_HEADER != o2.SMART_EDIT_SETEXT_HEADER) return false;
        if (o1.SMART_EDIT_TABLE_SEPARATOR_LINE != o2.SMART_EDIT_TABLE_SEPARATOR_LINE) return false;
        if (o1.SMART_EDIT_TABLES != o2.SMART_EDIT_TABLES) return false;
        if (o1.SMART_ENTER_ATX_HEADER != o2.SMART_ENTER_ATX_HEADER) return false;
        if (o1.SMART_ENTER_SETEXT_HEADER != o2.SMART_ENTER_SETEXT_HEADER) return false;
        if (o1.SMART_TABS != o2.SMART_TABS) return false;
        if (o1.SPACE_AFTER_ATX_MARKER != o2.SPACE_AFTER_ATX_MARKER) return false;
        if (o1.TAB_SIZE != o2.TAB_SIZE) return false;
        if (o1.TABLE_ADJUST_COLUMN_WIDTH != o2.TABLE_ADJUST_COLUMN_WIDTH) return false;
        if (o1.TABLE_APPLY_COLUMN_ALIGNMENT != o2.TABLE_APPLY_COLUMN_ALIGNMENT) return false;
        if (o1.TABLE_CAPTION != o2.TABLE_CAPTION) return false;
        if (o1.TABLE_CAPTION_SPACES != o2.TABLE_CAPTION_SPACES) return false;
        if (o1.TABLE_FILL_MISSING_COLUMNS != o2.TABLE_FILL_MISSING_COLUMNS) return false;
        if (o1.TABLE_LEAD_TRAIL_PIPES != o2.TABLE_LEAD_TRAIL_PIPES) return false;
        if (o1.TABLE_LEFT_ALIGN_MARKER != o2.TABLE_LEFT_ALIGN_MARKER) return false;
        if (o1.TABLE_SPACE_AROUND_PIPE != o2.TABLE_SPACE_AROUND_PIPE) return false;
        if (o1.TABLE_TRIM_CELLS != o2.TABLE_TRIM_CELLS) return false;
        if (o1.TASK_ITEM_CONTINUATION != o2.TASK_ITEM_CONTINUATION) return false;
        if (o1.TASK_LIST_ITEM_CASE != o2.TASK_LIST_ITEM_CASE) return false;
        if (o1.TASK_LIST_ITEM_PLACEMENT != o2.TASK_LIST_ITEM_PLACEMENT) return false;
        if (o1.TOC_FORMAT_ON_SAVE != o2.TOC_FORMAT_ON_SAVE) return false;
        if (o1.TOC_GENERATE_HTML != o2.TOC_GENERATE_HTML) return false;
        if (o1.TOC_GENERATE_NUMBERED_LIST != o2.TOC_GENERATE_NUMBERED_LIST) return false;
        if (o1.TOC_GENERATE_STRUCTURE != o2.TOC_GENERATE_STRUCTURE) return false;
        if (o1.TOC_GENERATE_TEXT_ONLY != o2.TOC_GENERATE_TEXT_ONLY) return false;
        if (o1.TOC_HEADING_LEVELS != o2.TOC_HEADING_LEVELS) return false;
        if (o1.TOC_TITLE_LEVEL != o2.TOC_TITLE_LEVEL) return false;
        if (o1.TOC_UPDATE_ON_DOC_FORMAT != o2.TOC_UPDATE_ON_DOC_FORMAT) return false;
        if (o1.UNESCAPE_SPECIAL_CHARS_ON_WRAP != o2.UNESCAPE_SPECIAL_CHARS_ON_WRAP) return false;
        if (o1.USE_ACTUAL_CHAR_WIDTH != o2.USE_ACTUAL_CHAR_WIDTH) return false;
        if (o1.USE_TAB_CHARACTER != o2.USE_TAB_CHARACTER) return false;
        if (o1.VERBATIM_MINIMIZE_INDENT != o2.VERBATIM_MINIMIZE_INDENT) return false;
        if (o1.WRAP_ON_TYPING != o2.WRAP_ON_TYPING) return false;
        return o1.TOC_TITLE.equals(o2.TOC_TITLE);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + TRAILING_SPACES_OPTION_1;
        result = 31 * result + TRAILING_SPACES_OPTION_2;
        result = 31 * result + TRAILING_SPACES_OPTION_3;
        result = 31 * result + TRAILING_SPACES_OPTION_4;
        result = 31 * result + TRAILING_SPACES_OPTION_5;
        result = 31 * result + TRAILING_SPACES_OPTION_6;
        result = 31 * result + TRAILING_SPACES_OPTION_7;
        result = 31 * result + TRAILING_SPACES_OPTION_8;
        result = 31 * result + TRAILING_SPACES_OPTION_9;
        result = 31 * result + TRAILING_SPACES_OPTION_10;
        result = 31 * result + TRAILING_SPACES_OPTION_11;
        result = 31 * result + TRAILING_SPACES_OPTION_12;
        result = 31 * result + TRAILING_SPACES_OPTION_13;
        result = 31 * result + TRAILING_SPACES_OPTION_14;
        result = 31 * result + TRAILING_SPACES_OPTION_15;
        result = 31 * result + TRAILING_SPACES_OPTION_16;
        result = 31 * result + TRAILING_SPACES_OPTION_17;
        result = 31 * result + TRAILING_SPACES_OPTION_18;
        result = 31 * result + TRAILING_SPACES_OPTION_19;
        result = 31 * result + TRAILING_SPACES_OPTION_20;
        result = 31 * result + TRAILING_SPACES_ID_1.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_2.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_3.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_4.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_5.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_6.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_7.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_8.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_9.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_10.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_11.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_12.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_13.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_14.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_15.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_16.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_17.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_18.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_19.hashCode();
        result = 31 * result + TRAILING_SPACES_ID_20.hashCode();

// @formatter:off

        /*ABBREVIATIONS_PLACEMENT               */ result = 31 * result + ABBREVIATIONS_PLACEMENT;
        /*ABBREVIATIONS_SORT                    */ result = 31 * result + ABBREVIATIONS_SORT;
        /*ATTRIBUTE_CLASS                       */ result = 31 * result + ATTRIBUTE_CLASS;
        /*ATTRIBUTE_EQUAL_SPACE                 */ result = 31 * result + ATTRIBUTE_EQUAL_SPACE;
        /*ATTRIBUTE_ID                          */ result = 31 * result + ATTRIBUTE_ID;
        /*ATTRIBUTE_VALUE_QUOTES                */ result = 31 * result + ATTRIBUTE_VALUE_QUOTES;
        /*ATTRIBUTES_COMBINE_CONSECUTIVE        */ result = 31 * result + (ATTRIBUTES_COMBINE_CONSECUTIVE ? 1 : 0);
        /*ATTRIBUTES_SORT                       */ result = 31 * result + (ATTRIBUTES_SORT ? 1 : 0);
        /*ATTRIBUTES_SPACES                     */ result = 31 * result + ATTRIBUTES_SPACES;
        /*ATX_HEADER_TRAILING_MARKER            */ result = 31 * result + ATX_HEADER_TRAILING_MARKER;
        /*BLOCK_QUOTE_MARKERS                   */ result = 31 * result + BLOCK_QUOTE_MARKERS;
        /*BULLET_LIST_ITEM_MARKER               */ result = 31 * result + BULLET_LIST_ITEM_MARKER;
        /*CODE_FENCE_MARKER_LENGTH              */ result = 31 * result + CODE_FENCE_MARKER_LENGTH;
        /*CODE_FENCE_MARKER_TYPE                */ result = 31 * result + CODE_FENCE_MARKER_TYPE;
        /*CODE_FENCE_MATCH_CLOSING_MARKER       */ result = 31 * result + (CODE_FENCE_MATCH_CLOSING_MARKER ? 1 : 0);
        /*CODE_FENCE_MINIMIZE_INDENT            */ result = 31 * result + (CODE_FENCE_MINIMIZE_INDENT ? 1 : 0);
        /*CODE_FENCE_SPACE_BEFORE_INFO          */ result = 31 * result + (CODE_FENCE_SPACE_BEFORE_INFO ? 1 : 0);
        /*CODE_KEEP_TRAILING_SPACES             */ result = 31 * result + CODE_KEEP_TRAILING_SPACES;
        /*DEFINITION_MARKER_SPACES              */ result = 31 * result + DEFINITION_MARKER_SPACES;
        /*DEFINITION_MARKER_TYPE                */ result = 31 * result + DEFINITION_MARKER_TYPE;
        /*ENUMERATED_REFERENCE_FORMAT_PLACEMENT */ result = 31 * result + ENUMERATED_REFERENCE_FORMAT_PLACEMENT;
        /*ENUMERATED_REFERENCE_FORMAT_SORT      */ result = 31 * result + ENUMERATED_REFERENCE_FORMAT_SORT;
        /*ESCAPE_NUMBERED_LEAD_IN_ON_WRAP       */ result = 31 * result + (ESCAPE_NUMBERED_LEAD_IN_ON_WRAP ? 1 : 0);
        /*ESCAPE_SPECIAL_CHARS_ON_WRAP          */ result = 31 * result + (ESCAPE_SPECIAL_CHARS_ON_WRAP ? 1 : 0);
        /*FOOTNOTE_PLACEMENT                    */ result = 31 * result + FOOTNOTE_PLACEMENT;
        /*FOOTNOTE_SORT                         */ result = 31 * result + FOOTNOTE_SORT;
        /*FORMAT_WITH_SOFT_WRAP                 */ result = 31 * result + FORMAT_WITH_SOFT_WRAP;
        /*HEADING_PREFERENCE                    */ result = 31 * result + HEADING_PREFERENCE;
        /*INDENT_SIZE                           */ result = 31 * result + INDENT_SIZE;
        /*KEEP_AT_START_EXPLICIT_LINK           */ result = 31 * result + KEEP_AT_START_EXPLICIT_LINK;
        /*KEEP_AT_START_IMAGE_LINKS             */ result = 31 * result + KEEP_AT_START_IMAGE_LINKS;
        /*KEEP_BLANK_LINES                      */ result = 31 * result + KEEP_BLANK_LINES;
        /*KEEP_TRAILING_SPACES                  */ result = 31 * result + KEEP_TRAILING_SPACES;
        /*LIST_ADD_BLANK_LINE_BEFORE            */ result = 31 * result + (LIST_ADD_BLANK_LINE_BEFORE ? 1 : 0);
        /*LIST_ALIGN_NUMERIC                    */ result = 31 * result + LIST_ALIGN_NUMERIC;
        /*LIST_ORDERED_TASK_ITEM_PRIORITY       */ result = 31 * result + LIST_ORDERED_TASK_ITEM_PRIORITY;
        /*LIST_RENUMBER_ITEMS                   */ result = 31 * result + (LIST_RENUMBER_ITEMS ? 1 : 0);
        /*LIST_RESET_FIRST_ITEM_NUMBER          */ result = 31 * result + (LIST_RESET_FIRST_ITEM_NUMBER ? 1 : 0);
        /*LIST_SPACING                          */ result = 31 * result + LIST_SPACING;
        /*MACRO_PLACEMENT                       */ result = 31 * result + MACRO_PLACEMENT;
        /*MACRO_SORT                            */ result = 31 * result + MACRO_SORT;
        /*NEW_BULLET_LIST_ITEM_MARKER           */ result = 31 * result + NEW_BULLET_LIST_ITEM_MARKER;
        /*PARA_WRAP_TEXT                        */ result = 31 * result + (PARA_WRAP_TEXT ? 1 : 0);
        /*REFERENCE_PLACEMENT                   */ result = 31 * result + REFERENCE_PLACEMENT;
        /*REFERENCE_SORT                        */ result = 31 * result + REFERENCE_SORT;
        /*RIGHT_MARGIN                          */ result = 31 * result + RIGHT_MARGIN;
        /*SETEXT_HEADER_EQUALIZE_MARKER         */ result = 31 * result + (SETEXT_HEADER_EQUALIZE_MARKER ? 1 : 0);
        /*SMART_EDIT_ATX_HEADER                 */ result = 31 * result + (SMART_EDIT_ATX_HEADER ? 1 : 0);
        /*SMART_EDIT_SETEXT_HEADER              */ result = 31 * result + (SMART_EDIT_SETEXT_HEADER ? 1 : 0);
        /*SMART_EDIT_TABLE_SEPARATOR_LINE       */ result = 31 * result + (SMART_EDIT_TABLE_SEPARATOR_LINE ? 1 : 0);
        /*SMART_EDIT_TABLES                     */ result = 31 * result + (SMART_EDIT_TABLES ? 1 : 0);
        /*SMART_ENTER_ATX_HEADER                */ result = 31 * result + (SMART_ENTER_ATX_HEADER ? 1 : 0);
        /*SMART_ENTER_SETEXT_HEADER             */ result = 31 * result + (SMART_ENTER_SETEXT_HEADER ? 1 : 0);
        /*SMART_TABS                            */ result = 31 * result + (SMART_TABS ? 1 : 0);
        /*SPACE_AFTER_ATX_MARKER                */ result = 31 * result + SPACE_AFTER_ATX_MARKER;
        /*TAB_SIZE                              */ result = 31 * result + TAB_SIZE;
        /*TABLE_ADJUST_COLUMN_WIDTH             */ result = 31 * result + (TABLE_ADJUST_COLUMN_WIDTH ? 1 : 0);
        /*TABLE_APPLY_COLUMN_ALIGNMENT          */ result = 31 * result + (TABLE_APPLY_COLUMN_ALIGNMENT ? 1 : 0);
        /*TABLE_CAPTION                         */ result = 31 * result + TABLE_CAPTION;
        /*TABLE_CAPTION_SPACES                  */ result = 31 * result + TABLE_CAPTION_SPACES;
        /*TABLE_FILL_MISSING_COLUMNS            */ result = 31 * result + (TABLE_FILL_MISSING_COLUMNS ? 1 : 0);
        /*TABLE_LEAD_TRAIL_PIPES                */ result = 31 * result + (TABLE_LEAD_TRAIL_PIPES ? 1 : 0);
        /*TABLE_LEFT_ALIGN_MARKER               */ result = 31 * result + TABLE_LEFT_ALIGN_MARKER;
        /*TABLE_SPACE_AROUND_PIPE               */ result = 31 * result + (TABLE_SPACE_AROUND_PIPE ? 1 : 0);
        /*TABLE_TRIM_CELLS                      */ result = 31 * result + (TABLE_TRIM_CELLS ? 1 : 0);
        /*TASK_ITEM_CONTINUATION                */ result = 31 * result + TASK_ITEM_CONTINUATION;
        /*TASK_LIST_ITEM_CASE                   */ result = 31 * result + TASK_LIST_ITEM_CASE;
        /*TASK_LIST_ITEM_PLACEMENT              */ result = 31 * result + TASK_LIST_ITEM_PLACEMENT;
        /*TOC_FORMAT_ON_SAVE                    */ result = 31 * result + (TOC_FORMAT_ON_SAVE ? 1 : 0);
        /*TOC_GENERATE_HTML                     */ result = 31 * result + (TOC_GENERATE_HTML ? 1 : 0);
        /*TOC_GENERATE_NUMBERED_LIST            */ result = 31 * result + (TOC_GENERATE_NUMBERED_LIST ? 1 : 0);
        /*TOC_GENERATE_STRUCTURE                */ result = 31 * result + TOC_GENERATE_STRUCTURE;
        /*TOC_GENERATE_TEXT_ONLY                */ result = 31 * result + (TOC_GENERATE_TEXT_ONLY ? 1 : 0);
        /*TOC_HEADING_LEVELS                    */ result = 31 * result + TOC_HEADING_LEVELS;
        /*TOC_TITLE                             */ result = 31 * result + TOC_TITLE.hashCode();
        /*TOC_TITLE_LEVEL                       */ result = 31 * result + TOC_TITLE_LEVEL;
        /*TOC_UPDATE_ON_DOC_FORMAT              */ result = 31 * result + TOC_UPDATE_ON_DOC_FORMAT;
        /*UNESCAPE_SPECIAL_CHARS_ON_WRAP        */ result = 31 * result + (UNESCAPE_SPECIAL_CHARS_ON_WRAP ? 1 : 0);
        /*USE_ACTUAL_CHAR_WIDTH                 */ result = 31 * result + (USE_ACTUAL_CHAR_WIDTH ? 1 : 0);
        /*USE_TAB_CHARACTER                     */ result = 31 * result + (USE_TAB_CHARACTER ? 1 : 0);
        /*VERBATIM_MINIMIZE_INDENT              */ result = 31 * result + (VERBATIM_MINIMIZE_INDENT ? 1 : 0);
        /*WRAP_ON_TYPING                        */ result = 31 * result + WRAP_ON_TYPING;

// @formatter:on

        return result;
    }
}
