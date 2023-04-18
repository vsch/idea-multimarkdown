// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.editor.util.HtmlPanelProvider;
import com.vladsch.md.nav.parser.Extensions;
import com.vladsch.md.nav.settings.api.MdParserSettingsComponent;
import com.vladsch.md.nav.settings.api.MdSettingsComponent;
import com.vladsch.md.nav.settings.api.SettingsFormImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MdParserSettingsForm extends SettingsFormImpl {
    private JPanel myMainPanel;
    private JBCheckBox definitionsCheckBox;
    private JBCheckBox tablesCheckBox;
    private JBCheckBox autoLinksCheckBox;
    private JBCheckBox hardWrapsCheckBox;
    private JBCheckBox quotesCheckBox;
    private JBCheckBox smartsCheckBox;
    private JBCheckBox abbreviationsCheckBox;
    private JBCheckBox wikiLinksCheckBox;
    private JBCheckBox strikethroughCheckBox;
    private JBCheckBox taskListsCheckBox;
    private JBCheckBox headerSpaceCheckBox;
    private JBCheckBox suppressInlineHTMLCheckBox;
    private JBCheckBox suppressHTMLBlocksCheckBox;
    private JBCheckBox fencedCodeBlocksCheckBox;
    private JBCheckBox relaxedHRulesCheckBox;
    private JBCheckBox githubWikiLinksCheckBox;
    private JBCheckBox definitionBreakDoubleBlankLine;
    private JBCheckBox htmlDeepParser;
    private ComboBox<ParserProfileItem> myParserProfileComboBox;
    private JBCheckBox subscriptCheckBox;
    private JBCheckBox superscriptCheckBox;
    private JBCheckBox insertedCheckBox;
    private JBCheckBox jekyllFrontMatterCheckBox;
    private JLabel myDebugLabel;
    private JSeparator myDebugSeparator;
    private JBCheckBox emojiShortcutsCheckBox;
    private JBCheckBox githubTablesCheckBox;
    private JBCheckBox gfmLooseBlankLineAfterItemPara;
    private JBCheckBox headerIdNoDupedDashes;
    private JBCheckBox headerIdNonAsciiToLowercase;
    private JBCheckBox spaceInLinkUrls;
    private JBCheckBox headerIdRefTextTrimTrailingSpaces;
    private JComboBox<String> myListIndentationComboBox;
    JComboBox<String> myEmojiShortcutsType;
    JComboBox<String> myEmojiImagesType;
    private JPanel myExtensionsPanel;

    @Nullable private ActionListener myUpdateListener;

    @NotNull private final Map<JBCheckBox, PegdownExtensions> checkBoxPegdownMap = new HashMap<>();
    @NotNull private final Map<JBCheckBox, ParserOptions> checkBoxParserMap = new HashMap<>();

    @NotNull CollectionComboBoxModel<ParserProfileItem> myParserProfileModel = getParserProfileModel();
    ParserProfileItem myParserProfileLastItem = new ParserProfileItem(ParserProfile.GITHUB_DOCS);
    private int inUpdate = 0;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    @Override
    protected void disposeResources() {

    }

    public enum ParserProfile {
        GITHUB_DOCS(Extensions.GITHUB_DOCUMENT_COMPATIBLE, MdParserSettings.GITHUB_DOCUMENT_COMPATIBLE, MdBundle.message("settings.parser.profile.github-documents"), EmojiShortcutsType.GITHUB, EmojiImagesType.IMAGE_ONLY),
        GITHUB_WIKI(Extensions.GITHUB_WIKI_COMPATIBLE, MdParserSettings.GITHUB_WIKI_COMPATIBLE, MdBundle.message("settings.parser.profile.github-wiki"), EmojiShortcutsType.GITHUB, EmojiImagesType.IMAGE_ONLY),
        GITHUB_COMMENTS(Extensions.GITHUB_COMMENT_COMPATIBLE, MdParserSettings.GITHUB_COMMENT_COMPATIBLE, MdBundle.message("settings.parser.profile.github-comments"), EmojiShortcutsType.GITHUB, EmojiImagesType.IMAGE_ONLY),
        GITBOOK_DOCS(Extensions.GITHUB_DOCUMENT_COMPATIBLE | Extensions.FOOTNOTES, MdParserSettings.GITBOOK_DOCUMENT_COMPATIBLE, MdBundle.message("settings.parser.profile.gitbook-documents"), EmojiShortcutsType.GITHUB, EmojiImagesType.IMAGE_ONLY),
        GITLAB_DOCS(Extensions.GITHUB_DOCUMENT_COMPATIBLE, MdParserSettings.GITLAB_DOCUMENT_COMPATIBLE, MdBundle.message("settings.parser.profile.gitlab-documents"), EmojiShortcutsType.GITHUB, EmojiImagesType.IMAGE_ONLY),
        COMMONMARK(MdParserSettings.COMMONMARK_EXTENSIONS, MdParserSettings.COMMONMARK, MdBundle.message("settings.parser.profile.commonmark"), EmojiShortcutsType.EMOJI_CHEAT_SHEET, EmojiImagesType.IMAGE_ONLY),
        CUSTOM(-1, -1, MdBundle.message("settings.parser.profile.custom"), null, null);

        final String displayName;
        final int pegdownExtensions;
        final long parserOptions;
        final @Nullable EmojiShortcutsType emojiShortcutsType;
        final @Nullable EmojiImagesType emojiImagesType;

        final static int PROFILE_PEGDOWN_OPTIONS = ~(Extensions.TOC | MdParserSettings.EXCLUDED_PEGDOWN_EXTENSIONS);
        final static long PROFILE_PARSER_OPTIONS = ~(0x8000000000000000L | ParserOptions.NO_TEXT_ATTRIBUTES.getFlags() | ParserOptions.SIM_TOC_BLANK_LINE_SPACER.getFlags() | MdParserSettings.EXCLUDED_PARSER_OPTIONS);

        public String getDisplayName() {
            return displayName;
        }

        public boolean isMatchedBy(int pegdownOptions, long parserOptions, EmojiShortcutsType emojiShortcutsType, EmojiImagesType emojiImagesType) {
            if (this != CUSTOM) {
                long applicableParserOptions = PROFILE_PARSER_OPTIONS;
                if ((pegdownOptions & Extensions.WIKILINKS) == 0) applicableParserOptions &= ~ParserOptions.GITHUB_WIKI_LINKS.getFlags();
                int currentPegdownFlags = pegdownOptions & (PROFILE_PEGDOWN_OPTIONS & ~Extensions.AUTOLINKS);
                int profilePegdownFlags = this.pegdownExtensions & (PROFILE_PEGDOWN_OPTIONS & ~Extensions.AUTOLINKS);
                boolean pegdownMatch = currentPegdownFlags == profilePegdownFlags;
                boolean parserMatch = (parserOptions & applicableParserOptions) == (this.parserOptions & applicableParserOptions);
                return pegdownMatch && parserMatch && (this.emojiImagesType == null || this.emojiImagesType == emojiImagesType && this.emojiShortcutsType == emojiShortcutsType);
            } else {
                //noinspection PointlessBooleanExpression
                return !(GITHUB_DOCS.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType) ||
                        GITHUB_DOCS.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType) ||
                        GITHUB_DOCS.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType) ||
                        GITBOOK_DOCS.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType) ||
                        GITLAB_DOCS.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType) ||
                        COMMONMARK.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType) ||
                        false);
            }
        }

        ParserProfile(int pegdownExtensions, long parserOptions, String displayName, @Nullable EmojiShortcutsType emojiShortcutsType, @Nullable EmojiImagesType emojiImagesType) {
            this.displayName = displayName;
            this.pegdownExtensions = pegdownExtensions;
            this.parserOptions = parserOptions;
            this.emojiShortcutsType = emojiShortcutsType;
            this.emojiImagesType = emojiImagesType;
        }
    }

    public ParserProfile getProfile(int pegdownOptions, long parserOptions, EmojiShortcutsType emojiShortcutsType, EmojiImagesType emojiImagesType) {
        if (ParserProfile.GITHUB_DOCS.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType)) return ParserProfile.GITHUB_DOCS;
        if (ParserProfile.GITHUB_WIKI.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType)) return ParserProfile.GITHUB_WIKI;
        if (ParserProfile.GITHUB_COMMENTS.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType)) return ParserProfile.GITHUB_COMMENTS;
        if (ParserProfile.COMMONMARK.isMatchedBy(pegdownOptions, parserOptions, emojiShortcutsType, emojiImagesType)) return ParserProfile.COMMONMARK;
        return ParserProfile.CUSTOM;
    }

    public ParserProfileItem getParserProfileItem(ParserProfile parserProfile) {
        if (parserProfile == ParserProfile.COMMONMARK) return ParserProfileItem.COMMONMARK;
        if (parserProfile == ParserProfile.GITHUB_DOCS) return ParserProfileItem.GITHUB_DOCS;
        if (parserProfile == ParserProfile.GITHUB_WIKI) return ParserProfileItem.GITHUB_WIKI;
        if (parserProfile == ParserProfile.GITHUB_COMMENTS) return ParserProfileItem.GITHUB_COMMENTS;

        for (MdSettingsComponent<?> extension : mySettingsExtensions) {
            if (extension instanceof MdParserSettingsComponent<?>) {
                ParserProfileItem item = ((MdParserSettingsComponent<?>) extension).getParserProfileItem(parserProfile);
                if (item != null) return item;
            }
        }

        return ParserProfileItem.CUSTOM;
    }

    public static class ParserProfileItem {
        final public static ParserProfileItem COMMONMARK = new ParserProfileItem(ParserProfile.COMMONMARK);
        final public static ParserProfileItem GITHUB_DOCS = new ParserProfileItem(ParserProfile.GITHUB_DOCS);
        final public static ParserProfileItem GITHUB_WIKI = new ParserProfileItem(ParserProfile.GITHUB_WIKI);
        final public static ParserProfileItem GITHUB_COMMENTS = new ParserProfileItem(ParserProfile.GITHUB_COMMENTS);
        final public static ParserProfileItem GITBOOK_DOCS = new ParserProfileItem(ParserProfile.GITBOOK_DOCS);
        final public static ParserProfileItem GITLAB_DOCS = new ParserProfileItem(ParserProfile.GITLAB_DOCS);
        final public static ParserProfileItem CUSTOM = new ParserProfileItem(ParserProfile.CUSTOM);

        final public @NotNull ParserProfile parserProfile;
        final public @NotNull String displayName;

        @Override
        public String toString() {
            return displayName;
        }

        public ParserProfileItem(@NotNull ParserProfile parserProfile) {
            this.parserProfile = parserProfile;
            this.displayName = parserProfile.getDisplayName();
        }
    }

    List<ParserProfileItem> parserProfileItems() {
        ArrayList<ParserProfileItem> values = new ArrayList<>();
        values.add(ParserProfileItem.GITHUB_DOCS);
        values.add(ParserProfileItem.GITHUB_COMMENTS);
        values.add(ParserProfileItem.GITHUB_WIKI);
        values.add(ParserProfileItem.COMMONMARK);
        values.add(ParserProfileItem.CUSTOM);

        for (MdSettingsComponent<?> extension : mySettingsExtensions) {
            if (extension instanceof MdParserSettingsComponent<?>) {
                ParserProfileItem[] item = ((MdParserSettingsComponent<?>) extension).getParserProfileItems();
                if (item != null) values.addAll(Arrays.asList(item));
            }
        }

        return values;
    }

    public MdParserSettingsForm(RenderingProfileSynchronizer profileSynchronizer) {
        super(profileSynchronizer);

        checkBoxPegdownMap.put(definitionsCheckBox, PegdownExtensions.DEFINITIONS);
        checkBoxPegdownMap.put(tablesCheckBox, PegdownExtensions.TABLES);
        checkBoxPegdownMap.put(autoLinksCheckBox, PegdownExtensions.AUTOLINKS);
        checkBoxPegdownMap.put(hardWrapsCheckBox, PegdownExtensions.HARDWRAPS);
        checkBoxPegdownMap.put(quotesCheckBox, PegdownExtensions.QUOTES);
        checkBoxPegdownMap.put(smartsCheckBox, PegdownExtensions.SMARTS);
        checkBoxPegdownMap.put(abbreviationsCheckBox, PegdownExtensions.ABBREVIATIONS);
        checkBoxPegdownMap.put(wikiLinksCheckBox, PegdownExtensions.WIKILINKS);
        checkBoxPegdownMap.put(strikethroughCheckBox, PegdownExtensions.STRIKETHROUGH);
        checkBoxPegdownMap.put(taskListsCheckBox, PegdownExtensions.TASKLISTITEMS);
        checkBoxPegdownMap.put(headerSpaceCheckBox, PegdownExtensions.ATXHEADERSPACE);
        checkBoxPegdownMap.put(suppressInlineHTMLCheckBox, PegdownExtensions.SUPPRESS_INLINE_HTML);
        checkBoxPegdownMap.put(suppressHTMLBlocksCheckBox, PegdownExtensions.SUPPRESS_HTML_BLOCKS);
        checkBoxPegdownMap.put(fencedCodeBlocksCheckBox, PegdownExtensions.FENCED_CODE_BLOCKS);
        checkBoxPegdownMap.put(relaxedHRulesCheckBox, PegdownExtensions.RELAXEDHRULES);
        checkBoxPegdownMap.put(subscriptCheckBox, PegdownExtensions.SUBSCRIPT);
        checkBoxPegdownMap.put(superscriptCheckBox, PegdownExtensions.SUPERSCRIPT);
        checkBoxPegdownMap.put(insertedCheckBox, PegdownExtensions.INSERTED);
        checkBoxPegdownMap.put(definitionBreakDoubleBlankLine, PegdownExtensions.DEFINITION_BREAK_DOUBLE_BLANK_LINE);
        checkBoxPegdownMap.put(htmlDeepParser, PegdownExtensions.HTML_DEEP_PARSER);

        checkBoxParserMap.put(githubWikiLinksCheckBox, ParserOptions.GITHUB_WIKI_LINKS);
        checkBoxParserMap.put(jekyllFrontMatterCheckBox, ParserOptions.JEKYLL_FRONT_MATTER);
        checkBoxParserMap.put(emojiShortcutsCheckBox, ParserOptions.EMOJI_SHORTCUTS);
        checkBoxParserMap.put(githubTablesCheckBox, ParserOptions.GFM_TABLE_RENDERING);
        checkBoxParserMap.put(gfmLooseBlankLineAfterItemPara, ParserOptions.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA);
        checkBoxParserMap.put(headerIdNoDupedDashes, ParserOptions.HEADER_ID_NO_DUPED_DASHES);
        checkBoxParserMap.put(headerIdNonAsciiToLowercase, ParserOptions.HEADER_ID_NON_ASCII_TO_LOWERCASE);
        checkBoxParserMap.put(spaceInLinkUrls, ParserOptions.SPACE_IN_LINK_URLS);
        checkBoxParserMap.put(headerIdRefTextTrimTrailingSpaces, ParserOptions.HEADER_ID_REF_TEXT_TRIM_TRAILING_SPACES);

        onFormCreated();

        for (MdSettingsComponent<?> extension : mySettingsExtensions) {
            if (extension instanceof MdParserSettingsComponent<?>) {
                ((MdParserSettingsComponent<?>) extension).addPegdownExtensionCheckboxes(checkBoxPegdownMap);
                ((MdParserSettingsComponent<?>) extension).addParserOptionCheckboxes(checkBoxParserMap);
            }
        }

        for (JBCheckBox checkBox : checkBoxPegdownMap.keySet()) {
            checkBox.addActionListener(myUpdateListener);
        }

        for (JBCheckBox checkBox : checkBoxParserMap.keySet()) {
            checkBox.addActionListener(myUpdateListener);
        }
    }

    private EmojiShortcutsType getEmojiShortcutsType() {
        return EmojiShortcutsType.ADAPTER.get(myEmojiShortcutsType);
    }

    private EmojiImagesType getEmojiImagesType() {
        return EmojiImagesType.ADAPTER.get(myEmojiImagesType);
    }

    @Override
    public void updateOptionalSettings() {
        if (inUpdate > 0) return;

        try {
            inUpdate++;

            // set gitlab options
            definitionBreakDoubleBlankLine.setEnabled(definitionsCheckBox.isSelected());
            githubTablesCheckBox.setEnabled(tablesCheckBox.isSelected());
            githubWikiLinksCheckBox.setEnabled(wikiLinksCheckBox.isSelected());

            githubWikiLinksCheckBox.setEnabled(wikiLinksCheckBox.isSelected());
            definitionBreakDoubleBlankLine.setEnabled(definitionsCheckBox.isSelected());
            githubTablesCheckBox.setEnabled(tablesCheckBox.isSelected());

            updateExtensionsOptionalSettings();

            int pegdownExtensions = getPegdownExtensionFlags();
            long parserOptions = getParserOptionFlags();

            ParserProfile profile = getProfile(pegdownExtensions, parserOptions, getEmojiShortcutsType(), getEmojiImagesType());

            ParserProfileItem item = (ParserProfileItem) myParserProfileComboBox.getSelectedItem();
            if (item != null && item.parserProfile != profile) {
                myParserProfileLastItem = getParserProfileItem(profile);
                myParserProfileComboBox.setSelectedItem(myParserProfileLastItem);
                myParserProfileComboBox.repaint();
            }
        } finally {
            inUpdate--;
        }
    }

    @Override
    protected void updatePanelProviderDependentComponents(@NotNull HtmlPanelProvider fromProvider, @NotNull HtmlPanelProvider toProvider, boolean isInitialShow) {

    }

    @Override
    protected void updateFormOnReshow(boolean isInitialShow) {
        updateOptionalSettings();
    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    public long getParserOptionFlags() {
        long parserOptions = 0;
        for (JBCheckBox checkBox : checkBoxParserMap.keySet()) {
            final boolean selected = checkBox.isSelected();
            if (selected) parserOptions |= checkBoxParserMap.get(checkBox).getFlags();
        }

        // encode list indentation into options
        ListIndentationType listIndentationType = ListIndentationType.ADAPTER.findEnum((String) myListIndentationComboBox.getSelectedItem());
        if (listIndentationType == ListIndentationType.COMMONMARK) {
            parserOptions |= ParserOptions.COMMONMARK_LISTS.getFlags();
        } else if (listIndentationType == ListIndentationType.GITHUB) {
            parserOptions |= ParserOptions.GITHUB_LISTS.getFlags();
        }

        return parserOptions;
    }

    public int getPegdownExtensionFlags() {
        int pegdownExtensions = 0;
        for (JBCheckBox checkBox : checkBoxPegdownMap.keySet()) {
            final boolean selected = checkBox.isSelected();
            if (selected) pegdownExtensions |= checkBoxPegdownMap.get(checkBox).getFlags();
        }
        return pegdownExtensions;
    }

    public void updateCheckBoxes(ParserProfile profile) {
        if (profile != ParserProfile.CUSTOM) updateCheckBoxes(profile.pegdownExtensions, profile.parserOptions, true);
    }

    public void updateCheckBoxes(int pegdownExtensions, long parserOptions, boolean fromProfile) {
        if (inUpdate > 0) return;

        try {
            inUpdate++;
            for (JBCheckBox checkBox : checkBoxPegdownMap.keySet()) {
                if (!fromProfile || (checkBoxPegdownMap.get(checkBox).getFlags() & ParserProfile.PROFILE_PEGDOWN_OPTIONS) != 0) {
                    checkBox.setSelected((checkBoxPegdownMap.get(checkBox).getFlags() & pegdownExtensions) != 0);
                }
            }

            for (JBCheckBox checkBox : checkBoxParserMap.keySet()) {
                if (!fromProfile || (checkBoxParserMap.get(checkBox).getFlags() & ParserProfile.PROFILE_PARSER_OPTIONS) != 0L) {
                    checkBox.setSelected((checkBoxParserMap.get(checkBox).getFlags() & parserOptions) != 0L);
                }
            }

            // decode list indentation flags
            myListIndentationComboBox.setSelectedItem(MdParserSettings.listIndentationType(parserOptions).getDisplayName());
        } finally {
            inUpdate--;
        }

        updateOptionalSettings();
    }

    private void createUIComponents() {
        myParserProfileModel = getParserProfileModel();
        myParserProfileComboBox = new ComboBox<>(myParserProfileModel);
        myParserProfileLastItem = (ParserProfileItem) myParserProfileComboBox.getSelectedItem();
        myParserProfileComboBox.addItemListener(e -> {
            final Object item = e.getItem();
            if (e.getStateChange() != ItemEvent.SELECTED || !(item instanceof ParserProfileItem)) {
                return;
            }

            myParserProfileLastItem = (ParserProfileItem) item;
            updateCheckBoxes(myParserProfileLastItem.parserProfile);

            // set emoji options if they are not null for the profile
            EmojiShortcutsType emojiShortcutsType = myParserProfileLastItem.parserProfile.emojiShortcutsType;
            if (emojiShortcutsType != null) {
                EmojiShortcutsType.ADAPTER.setComboBoxSelection(myEmojiShortcutsType, emojiShortcutsType);
                EmojiImagesType.ADAPTER.setComboBoxSelection(myEmojiImagesType, myParserProfileLastItem.parserProfile.emojiImagesType);
            }
        });

        myListIndentationComboBox = ListIndentationType.ADAPTER.createComboBox();
        myListIndentationComboBox.addItemListener(e -> {
            updateCheckBoxes(getPegdownExtensionFlags(), getParserOptionFlags(), false);
            updateOptionalSettings();
        });

        myEmojiShortcutsType = EmojiShortcutsType.ADAPTER.createComboBox();
        myEmojiShortcutsType.addItemListener(e -> updateOptionalSettings());

        myEmojiImagesType = EmojiImagesType.ADAPTER.createComboBox();
        myEmojiImagesType.addItemListener(e -> updateOptionalSettings());

        myUpdateListener = e -> ApplicationManager.getApplication().invokeLater(this::updateOptionalSettings, ModalityState.current());
        myExtensionsPanel = getExtensionsPanel();
    }

    @NotNull
    private CollectionComboBoxModel<ParserProfileItem> getParserProfileModel() {
        return new CollectionComboBoxModel<>(parserProfileItems());
    }

    @Override
    public void reset(@NotNull final MdRenderingProfileHolder settings) {
        reset(settings.getParserSettings());
        resetExtensions(settings);

        final int pegdownExtensions = settings.getParserSettings().getPegdownFlags();
        final long parserOptions = settings.getParserSettings().getOptionsFlags();

        ApplicationManager.getApplication().invokeLater(() -> {
            updateCheckBoxes(pegdownExtensions, parserOptions, false);
            updateOptionalSettings();
        }, ModalityState.any());
    }

    @Override
    public void apply(@NotNull final MdRenderingProfileHolder settings) {
        final MdParserSettings parserSettings = new MdParserSettings(
                getPegdownExtensionFlags(),
                getParserOptionFlags(),
                myProfileSynchronizer.getParserSettings().getGitHubSyntaxChange(),
                getEmojiShortcutsType().intValue,
                getEmojiImagesType().intValue
        );

        settings.setParserSettings(parserSettings);
        applyExtensions(settings);
    }

    @Override
    public boolean isModified(@NotNull final MdRenderingProfileHolder settings) {
        MdRenderingProfile renderingProfile = new MdRenderingProfile();
        apply(renderingProfile);
        return !renderingProfile.getParserSettings().equals(settings.getParserSettings());
    }

    private void reset(@NotNull MdParserSettings settings) {
        final int pegdownExtensions = settings.getPegdownFlags();
        final long parserOptions = settings.getOptionsFlags();

        final ParserProfile profile = getProfile(pegdownExtensions, parserOptions, getEmojiShortcutsType(), getEmojiImagesType());
        myParserProfileLastItem = getParserProfileItem(profile);

        myParserProfileComboBox.setSelectedItem(myParserProfileLastItem);

        EmojiShortcutsType.ADAPTER.setComboBoxSelection(myEmojiShortcutsType, settings.getEmojiShortcutsType());
        EmojiImagesType.ADAPTER.setComboBoxSelection(myEmojiImagesType, settings.getEmojiImagesType());

        if (myUpdateListener != null) myUpdateListener.actionPerformed(null);
    }
}
