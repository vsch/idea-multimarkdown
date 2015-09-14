/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.highlighter;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.io.FileUtil;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.vladsch.idea.multimarkdown.highlighter.MultiMarkdownHighlighterColors.*;

public class MultiMarkdownColorSettingsPage implements ColorSettingsPage {

    private final static Logger LOGGER = Logger.getInstance(MultiMarkdownColorSettingsPage.class.getName());

    protected static final ColorDescriptor[] EMPTY_COLOR_DESCRIPTOR_ARRAY = new ColorDescriptor[]{};

    @NonNls
    protected static final String SAMPLE_MARKDOWN_DOCUMENT_PATH = "/com/vladsch/idea/multimarkdown/sample-document.md";

    protected static final String SAMPLE_MARKDOWN_DOCUMENT = loadSampleMarkdownDocument();

    protected static final List<AttributesDescriptor> attributeDescriptors = new LinkedList<AttributesDescriptor>();

    private void addTextAttributesKey(String name, TextAttributesKey attributesKey) {
        attributeDescriptors.add(new AttributesDescriptor(MultiMarkdownBundle.message("multimarkdown.colorsettings." + name), attributesKey));
    }

    public MultiMarkdownColorSettingsPage() {
        addTextAttributesKey("abbreviation", ABBREVIATION_ATTR_KEY);
        addTextAttributesKey("abbreviated-text", ABBREVIATED_TEXT_ATTR_KEY);
        addTextAttributesKey("anchor-link", ANCHOR_LINK_ATTR_KEY);
        addTextAttributesKey("auto-link", AUTO_LINK_ATTR_KEY);
        addTextAttributesKey("blockquote", BLOCK_QUOTE_ATTR_KEY);
        addTextAttributesKey("bold", BOLD_ATTR_KEY);
        addTextAttributesKey("bold-italic", BOLDITALIC_ATTR_KEY);
        addTextAttributesKey("bold-marker", BOLD_MARKER_ATTR_KEY);
        addTextAttributesKey("bullet-list", BULLET_LIST_ATTR_KEY);
        addTextAttributesKey("comment", COMMENT_ATTR_KEY);
        addTextAttributesKey("code", CODE_ATTR_KEY);
        addTextAttributesKey("definition", DEFINITION_ATTR_KEY);
        addTextAttributesKey("definition-list", DEFINITION_LIST_ATTR_KEY);
        addTextAttributesKey("definition-term", DEFINITION_TERM_ATTR_KEY);
        addTextAttributesKey("explicit-link", EXPLICIT_LINK_ATTR_KEY);
        addTextAttributesKey("header-level-1", HEADER_LEVEL_1_ATTR_KEY);
        addTextAttributesKey("header-level-1-setext", SETEXT_HEADER_LEVEL_1_ATTR_KEY);
        addTextAttributesKey("header-level-2", HEADER_LEVEL_2_ATTR_KEY);
        addTextAttributesKey("header-level-2-setext", SETEXT_HEADER_LEVEL_2_ATTR_KEY);
        addTextAttributesKey("header-level-3", HEADER_LEVEL_3_ATTR_KEY);
        addTextAttributesKey("header-level-4", HEADER_LEVEL_4_ATTR_KEY);
        addTextAttributesKey("header-level-5", HEADER_LEVEL_5_ATTR_KEY);
        addTextAttributesKey("header-level-6", HEADER_LEVEL_6_ATTR_KEY);
        addTextAttributesKey("hrule", HRULE_ATTR_KEY);
        addTextAttributesKey("html-block", HTML_BLOCK_ATTR_KEY);
        addTextAttributesKey("image", IMAGE_ATTR_KEY);
        addTextAttributesKey("inline-html", INLINE_HTML_ATTR_KEY);
        addTextAttributesKey("italic", ITALIC_ATTR_KEY);
        addTextAttributesKey("italic-marker", ITALIC_MARKER_ATTR_KEY);
        addTextAttributesKey("mail-link", MAIL_LINK_ATTR_KEY);
        addTextAttributesKey("ordered-list", ORDERED_LIST_ATTR_KEY);
        addTextAttributesKey("quote", QUOTE_ATTR_KEY);
        addTextAttributesKey("reference", REFERENCE_ATTR_KEY);
        addTextAttributesKey("reference-image", REFERENCE_IMAGE_ATTR_KEY);
        addTextAttributesKey("reference-link", REFERENCE_LINK_ATTR_KEY);
        addTextAttributesKey("smarts", SMARTS_ATTR_KEY);
        addTextAttributesKey("special-text", SPECIAL_TEXT_ATTR_KEY);
        addTextAttributesKey("strikethrough", STRIKETHROUGH_ATTR_KEY);
        addTextAttributesKey("strikethrough-bold", STRIKETHROUGH_BOLD_ATTR_KEY);
        addTextAttributesKey("strikethrough-italic", STRIKETHROUGH_ITALIC_ATTR_KEY);
        addTextAttributesKey("strikethrough-bolditalic", STRIKETHROUGH_BOLDITALIC_ATTR_KEY);
        addTextAttributesKey("strikethrough-marker", STRIKETHROUGH_MARKER_ATTR_KEY);
        addTextAttributesKey("table", TABLE_ATTR_KEY);
        addTextAttributesKey("table-caption", TABLE_CAPTION_ATTR_KEY);
        addTextAttributesKey("table-cell-reven-ceven", TABLE_CELL_REVEN_CEVEN_ATTR_KEY);
        addTextAttributesKey("table-cell-reven-codd", TABLE_CELL_REVEN_CODD_ATTR_KEY);
        addTextAttributesKey("table-cell-rodd-ceven", TABLE_CELL_RODD_CEVEN_ATTR_KEY);
        addTextAttributesKey("table-cell-rodd-codd", TABLE_CELL_RODD_CODD_ATTR_KEY);
        addTextAttributesKey("table-row-even", TABLE_ROW_EVEN_ATTR_KEY);
        addTextAttributesKey("table-row-odd", TABLE_ROW_ODD_ATTR_KEY);
        //addTextAttributesKey("task-item-done", TASK_DONE_ITEM_ATTR_KEY);
        //addTextAttributesKey("task-item", TASK_ITEM_ATTR_KEY);
        addTextAttributesKey("task-item-marker-done", TASK_DONE_ITEM_MARKER_ATTR_KEY);
        addTextAttributesKey("task-item-marker", TASK_ITEM_MARKER_ATTR_KEY);
        addTextAttributesKey("text", TEXT_ATTR_KEY);
        addTextAttributesKey("verbatim", VERBATIM_ATTR_KEY);
        addTextAttributesKey("wiki-link", WIKI_LINK_ATTR_KEY);
    }

    /**
     * Get the mapping from special tag names surrounding the regions to be highlighted in the preview text to text
     * attribute keys used to highlight the regions.
     *
     * If some elements need to be highlighted in
     * the preview text which are not highlighted by the syntax highlighter, they need to be
     * surrounded by XML-like tags, for example: <code>&lt;class&gt;MyClass&lt;/class&gt;</code>.
     * The mapping between the names of the tags and the text attribute keys used for highlighting
     * is defined by the {@link #getAdditionalHighlightingTagToDescriptorMap()} method.
     *
     * Returns the mapping from special tag names surrounding the regions to be highlighted
     * in the preview text (see {@link #getDemoText()}) to text attribute keys used to
     * highlight the regions.
     *
     * @return the mapping from tag names to text attribute keys, or null if the demo text
     * does not contain any additional highlighting tags.
     */
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
        return attributeDescriptors.toArray(new AttributesDescriptor[attributeDescriptors.size()]);
    }

    /**
     * Get the list of descriptors specifying the {@link com.intellij.openapi.editor.colors.ColorKey} instances for which
     * colors are specified in the settings page.
     *
     * @return {@link #EMPTY_COLOR_DESCRIPTOR_ARRAY}
     */
    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
        return EMPTY_COLOR_DESCRIPTOR_ARRAY;
    }

    /**
     * Returns the text shown in the preview pane. If some elements need to be highlighted in
     * the preview text which are not highlighted by the syntax highlighter, they need to be
     * surrounded by XML-like tags, for example: <code>&lt;class&gt;MyClass&lt;/class&gt;</code>.
     * The mapping between the names of the tags and the text attribute keys used for highlighting
     * is defined by the {@link #getAdditionalHighlightingTagToDescriptorMap()} method.
     *
     * @return the text to show in the preview pane.
     */
    @NonNls
    @NotNull
    public String getDemoText() {
        return SAMPLE_MARKDOWN_DOCUMENT;
    }

    /**
     * Get the title of the page, shown as text in the dialog tab.
     *
     * @return the name as defined by {@link MultiMarkdownBundle}
     */
    @NotNull
    public String getDisplayName() {
        return MultiMarkdownBundle.message("multimarkdown.filetype.name");
    }

    /**
     * Get the syntax highlighter which is used to highlight the text shown in the preview
     * pane of the page.
     *
     * @return an instance of {@link MultiMarkdownSyntaxHighlighter}
     */
    @NotNull
    public SyntaxHighlighter getHighlighter() {
        return new MultiMarkdownSyntaxHighlighter(true);
    }

    /**
     * Get the icon for the page, shown in the dialog tab.
     *
     * @return {@link MultiMarkdownIcons#FILE}
     */
    @Nullable
    public Icon getIcon() {
        return MultiMarkdownIcons.FILE;
    }

    /**
     * Load the sample text to be displayed in the preview pane.
     *
     * @return the text loaded from {@link #SAMPLE_MARKDOWN_DOCUMENT_PATH}
     * @see #getDemoText()
     * @see #SAMPLE_MARKDOWN_DOCUMENT_PATH
     * @see #SAMPLE_MARKDOWN_DOCUMENT
     */
    protected static String loadSampleMarkdownDocument() {
        try {
            return FileUtil.loadTextAndClose(new InputStreamReader(MultiMarkdownColorSettingsPage.class.getResourceAsStream(SAMPLE_MARKDOWN_DOCUMENT_PATH)));
        } catch (Exception e) {
            LOGGER.error("Failed loading sample Markdown document", e);
        }
        return MultiMarkdownBundle.message("multimarkdown.colorsettings.sample-loading-error");
    }
}

