// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.highlighter;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.io.FileUtil;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.highlighter.api.MdColorSettingsExtension;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import icons.MdIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class MdColorSettingsPage implements ColorSettingsPage {
    private final static Logger LOG = Logger.getInstance(MdColorSettingsPage.class.getName());

    protected static final ColorDescriptor[] EMPTY_COLOR_DESCRIPTOR_ARRAY = new ColorDescriptor[] { };

    @NonNls
    public static final String SAMPLE_MARKDOWN_DOCUMENT_PATH = "/com/vladsch/md/nav/samples/sample-document.md";

    protected static final LinkedHashMap<String, AttributesDescriptor> attributeDescriptors = new LinkedHashMap<>();
    private static final AttributesDescriptor[] EMPTY_DESCRIPTORS = new AttributesDescriptor[0];

    private void addTextAttributesKey(String name, TextAttributesKey attributesKey) {
        if (!attributeDescriptors.containsKey(name)) {
            attributeDescriptors.put(name, new AttributesDescriptor(name, attributesKey));
        }
    }

    public MdColorSettingsPage() {
        MdHighlighterColors highlighterColors = MdHighlighterColors.getInstance();

        addTextAttributesKey(MdBundle.message("colors.abbreviation"), highlighterColors.ABBREVIATION_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.abbreviation-short"), highlighterColors.ABBREVIATION_SHORT_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.abbreviation-expanded"), highlighterColors.ABBREVIATION_EXPANDED_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.abbreviated-text"), highlighterColors.ABBREVIATED_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.auto-link"), highlighterColors.AUTO_LINK_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.anchor"), highlighterColors.ANCHOR_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.anchor-id"), highlighterColors.ANCHOR_ID_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.emoji-marker"), highlighterColors.EMOJI_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.emoji-id"), highlighterColors.EMOJI_ID_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.block-quote"), highlighterColors.BLOCK_QUOTE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.bold"), highlighterColors.BOLD_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.bold-marker"), highlighterColors.BOLD_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.underline"), highlighterColors.UNDERLINE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.underline-marker"), highlighterColors.UNDERLINE_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.superscript"), highlighterColors.SUPERSCRIPT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.superscript-marker"), highlighterColors.SUPERSCRIPT_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.subscript"), highlighterColors.SUBSCRIPT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.subscript-marker"), highlighterColors.SUBSCRIPT_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.bullet-list"), highlighterColors.BULLET_LIST_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.block-comment"), highlighterColors.BLOCK_COMMENT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.comment"), highlighterColors.COMMENT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.code"), highlighterColors.CODE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.code-marker"), highlighterColors.CODE_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.definition-marker"), highlighterColors.DEFINITION_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.definition-term"), highlighterColors.DEFINITION_TERM_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.link-ref"), highlighterColors.LINK_REF_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.atx-header"), highlighterColors.ATX_HEADER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.setext-header"), highlighterColors.SETEXT_HEADER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.header-text"), highlighterColors.HEADER_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.atx-header-marker"), highlighterColors.HEADER_ATX_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.setext-header-marker"), highlighterColors.HEADER_SETEXT_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.hrule"), highlighterColors.HRULE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.html-block"), highlighterColors.HTML_BLOCK_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.image"), highlighterColors.IMAGE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.inline-html"), highlighterColors.INLINE_HTML_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.html-entity"), highlighterColors.HTML_ENTITY_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.italic"), highlighterColors.ITALIC_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.italic-marker"), highlighterColors.ITALIC_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.jekyll-front-matter-block"), highlighterColors.JEKYLL_FRONT_MATTER_BLOCK_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.jekyll-front-matter-marker"), highlighterColors.JEKYLL_FRONT_MATTER_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.jekyll-tag-marker"), highlighterColors.JEKYLL_TAG_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.jekyll-tag-name"), highlighterColors.JEKYLL_TAG_NAME_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.jekyll-tag-parameters"), highlighterColors.JEKYLL_TAG_PARAMETERS_ATTR_KEY);

        addTextAttributesKey(MdBundle.message("colors.mail-link"), highlighterColors.MAIL_LINK_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.ordered-list"), highlighterColors.ORDERED_LIST_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.quote"), highlighterColors.QUOTE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.quoted-text"), highlighterColors.QUOTED_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.smarts"), highlighterColors.SMARTS_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.special-text"), highlighterColors.SPECIAL_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.special-text-marker"), highlighterColors.SPECIAL_TEXT_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.line-break-spaces"), highlighterColors.LINE_BREAK_SPACES_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.strikethrough"), highlighterColors.STRIKETHROUGH_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.strikethrough-marker"), highlighterColors.STRIKETHROUGH_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table"), highlighterColors.TABLE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-separator"), highlighterColors.TABLE_SEPARATOR_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-caption"), highlighterColors.TABLE_CAPTION_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-caption-marker"), highlighterColors.TABLE_CAPTION_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-cell-reven-ceven"), highlighterColors.TABLE_CELL_REVEN_CEVEN_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-cell-reven-codd"), highlighterColors.TABLE_CELL_REVEN_CODD_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-cell-rodd-ceven"), highlighterColors.TABLE_CELL_RODD_CEVEN_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-cell-rodd-codd"), highlighterColors.TABLE_CELL_RODD_CODD_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-row-even"), highlighterColors.TABLE_ROW_EVEN_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-row-odd"), highlighterColors.TABLE_ROW_ODD_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-header-cell-reven-ceven"), highlighterColors.TABLE_HDR_CELL_REVEN_CEVEN_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-header-cell-reven-codd"), highlighterColors.TABLE_HDR_CELL_REVEN_CODD_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-header-cell-rodd-ceven"), highlighterColors.TABLE_HDR_CELL_RODD_CEVEN_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-header-cell-rodd-codd"), highlighterColors.TABLE_HDR_CELL_RODD_CODD_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-header-row-even"), highlighterColors.TABLE_HDR_ROW_EVEN_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-header-row-odd"), highlighterColors.TABLE_HDR_ROW_ODD_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-column-even"), highlighterColors.TABLE_SEP_COLUMN_EVEN_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.table-column-odd"), highlighterColors.TABLE_SEP_COLUMN_ODD_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.task-item-marker-done"), highlighterColors.TASK_DONE_ITEM_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.task-item-done-text"), highlighterColors.TASK_DONE_ITEM_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.task-item-marker"), highlighterColors.TASK_ITEM_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.text"), highlighterColors.TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.wiki-link-marker"), highlighterColors.WIKI_LINK_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.wiki-link-separator"), highlighterColors.WIKI_LINK_SEPARATOR_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.wiki-link-ref"), highlighterColors.WIKI_LINK_REF_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.wiki-link-ref-anchor"), highlighterColors.WIKI_LINK_REF_ANCHOR_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.wiki-link-ref-anchor-marker"), highlighterColors.WIKI_LINK_REF_ANCHOR_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.wiki-link-text"), highlighterColors.WIKI_LINK_TEXT_ATTR_KEY);

        addTextAttributesKey(MdBundle.message("colors.reference-image"), highlighterColors.REFERENCE_IMAGE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference-link"), highlighterColors.REFERENCE_LINK_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference"), highlighterColors.REFERENCE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.verbatim"), highlighterColors.VERBATIM_ATTR_KEY);

        addTextAttributesKey(MdBundle.message("colors.reference-image-reference"), highlighterColors.REFERENCE_IMAGE_REFERENCE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference-image-text"), highlighterColors.REFERENCE_IMAGE_TEXT_ATTR_KEY);

        addTextAttributesKey(MdBundle.message("colors.explicit-link"), highlighterColors.EXPLICIT_LINK_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference-link-reference"), highlighterColors.REFERENCE_LINK_REFERENCE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference-link-text"), highlighterColors.REFERENCE_LINK_TEXT_ATTR_KEY);

        addTextAttributesKey(MdBundle.message("colors.reference-address"), highlighterColors.REFERENCE_LINK_REF_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference-title"), highlighterColors.REFERENCE_TITLE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference-text"), highlighterColors.REFERENCE_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference-anchor"), highlighterColors.REFERENCE_ANCHOR_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.reference-anchor-marker"), highlighterColors.REFERENCE_ANCHOR_MARKER_ATTR_KEY);

        addTextAttributesKey(MdBundle.message("colors.verbatim-marker"), highlighterColors.VERBATIM_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.verbatim-content"), highlighterColors.VERBATIM_CONTENT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.verbatim-lang"), highlighterColors.VERBATIM_LANG_ATTR_KEY);

        addTextAttributesKey(MdBundle.message("colors.link-ref-text"), highlighterColors.LINK_REF_TEXT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.link-ref-title"), highlighterColors.LINK_REF_TITLE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.link-ref-anchor"), highlighterColors.LINK_REF_ANCHOR_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.link-ref-anchor-marker"), highlighterColors.LINK_REF_ANCHOR_MARKER_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.image-link-ref"), highlighterColors.IMAGE_LINK_REF_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.image-url-content"), highlighterColors.IMAGE_URL_CONTENT_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.image-link-ref-title"), highlighterColors.IMAGE_LINK_REF_TITLE_ATTR_KEY);
        addTextAttributesKey(MdBundle.message("colors.image-alt-text"), highlighterColors.IMAGE_ALT_TEXT_ATTR_KEY);

        for (MdColorSettingsExtension extension : MdColorSettingsExtension.EXTENSIONS.getValue()) {
            extension.addTextAttributes(this::addTextAttributesKey);
        }

        if (MdApplicationSettings.getInstance().getDebugSettings().getDebugCombinationColors()) {
            // add the combination colors
            for (TextAttributesKey key : MdSyntaxHighlighter.getMergedKeys()) {
                addTextAttributesKey(key.getExternalName(), key);
            }
        }
    }

    @Nullable
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    /**
     * Get the set of {@link AttributesDescriptor} defining the configurable options in the dialog.
     *
     * @return {@link #attributeDescriptors} as an array.
     */
    @NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return attributeDescriptors.values().toArray(EMPTY_DESCRIPTORS);
    }

    /**
     * Get the list of descriptors specifying the {@link com.intellij.openapi.editor.colors.ColorKey} instances for which colors are specified in the settings page.
     *
     * @return {@link #EMPTY_COLOR_DESCRIPTOR_ARRAY}
     */
    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
        return EMPTY_COLOR_DESCRIPTOR_ARRAY;
    }

    /**
     * Returns the text shown in the preview pane. If some elements need to be highlighted in the preview text which are not highlighted by the syntax highlighter, they need to be surrounded by XML-like tags, for example: <code>&lt;class&gt;MyClass&lt;/class&gt;</code>. The mapping between the names of the tags and the text attribute keys used for highlighting is defined by the {@link #getAdditionalHighlightingTagToDescriptorMap()} method.
     *
     * @return the text to show in the preview pane.
     */
    @NonNls
    @NotNull
    public String getDemoText() {
        for (MdColorSettingsExtension extension : MdColorSettingsExtension.EXTENSIONS.getValue()) {
            String demoText = extension.getDemoText();
            if (demoText != null) return demoText;
        }
        return loadSampleMarkdownDocument(SAMPLE_MARKDOWN_DOCUMENT_PATH);
    }

    /**
     * Get the title of the page, shown as text in the dialog tab.
     *
     * @return the name as defined by {@link MdBundle}
     */
    @NotNull
    public String getDisplayName() {
        return MdBundle.message("multimarkdown.filetype.name");
    }

    /**
     * Get the syntax highlighter which is used to highlight the text shown in the preview pane of the page.
     *
     * @return an instance of {@link MdSyntaxHighlighter}
     */
    @NotNull
    public SyntaxHighlighter getHighlighter() {
        return new MdSyntaxHighlighter(MdRenderingProfile.getFOR_SAMPLE_DOC(), true, false);
    }

    /**
     * Get the icon for the page, shown in the dialog tab.
     *
     * @return {@link MdIcons.Document#FILE}
     */
    @Nullable
    public Icon getIcon() {
        return MdIcons.getDocumentIcon();
    }

    /**
     * Load the sample text to be displayed in the preview pane.
     */
    private static String loadSampleMarkdownDocument(String path) {
        try {
            return FileUtil.loadTextAndClose(new InputStreamReader(MdColorSettingsPage.class.getResourceAsStream(path)));
        } catch (Exception e) {
            LOG.error("Failed loading sample Markdown document", e);
        }
        return MdBundle.message("colors.sample-loading-error");
    }
}

