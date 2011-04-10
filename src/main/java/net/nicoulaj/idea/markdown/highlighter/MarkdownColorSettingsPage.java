/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.highlighter;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.io.FileUtil;
import net.nicoulaj.idea.markdown.MarkdownBundle;
import net.nicoulaj.idea.markdown.MarkdownIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Color settings page for the Markdown editor.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownColorSettingsPage implements ColorSettingsPage {

    /**
     * The {@link Logger}.
     */
    private final static Logger LOGGER = Logger.getInstance(MarkdownColorSettingsPage.class.getName());

    /**
     * An empty {@link ColorDescriptor} array.
     *
     * @see #getColorDescriptors()
     */
    protected static final ColorDescriptor[] EMPTY_COLOR_DESCRIPTOR_ARRAY = new ColorDescriptor[]{};

    /**
     * The path to the sample Markdown document shown in the colors settings dialog.
     */
    @NonNls
    protected static final String SAMPLE_MARKDOWN_DOCUMENT_PATH = "/net/nicoulaj/idea/markdown/sample-document.md";

    /**
     * The sample Markdown document shown in the colors settings dialog.
     *
     * @see #loadSampleMarkdownDocument()
     */
    protected static final String SAMPLE_MARKDOWN_DOCUMENT = loadSampleMarkdownDocument();

    /**
     * The set of {@link AttributesDescriptor} defining the configurable options in the dialog.
     *
     * @see #MarkdownColorSettingsPage()
     */
    protected final List<AttributesDescriptor> attributeDescriptors = new LinkedList<AttributesDescriptor>();

    /**
     * Build a new instance of {@link MarkdownColorSettingsPage}.
     */
    public MarkdownColorSettingsPage() {

        // Populate attribute descriptors.
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.plain-text"),
                MarkdownHighlighterColors.PLAIN_TEXT_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.bold"),
                MarkdownHighlighterColors.BOLD_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.italic"),
                MarkdownHighlighterColors.ITALIC_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.link"),
                MarkdownHighlighterColors.LINK_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.image"),
                MarkdownHighlighterColors.IMAGE_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.header-level-1"),
                MarkdownHighlighterColors.HEADER_LEVEL_1_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.header-level-2"),
                MarkdownHighlighterColors.HEADER_LEVEL_2_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.header-level-3"),
                MarkdownHighlighterColors.HEADER_LEVEL_3_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.header-level-4"),
                MarkdownHighlighterColors.HEADER_LEVEL_4_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.header-level-5"),
                MarkdownHighlighterColors.HEADER_LEVEL_5_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.header-level-6"),
                MarkdownHighlighterColors.HEADER_LEVEL_6_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.code"),
                MarkdownHighlighterColors.CODE_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.quote"),
                MarkdownHighlighterColors.QUOTE_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.table"),
                MarkdownHighlighterColors.TABLE_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.hrule"),
                MarkdownHighlighterColors.HRULE_ATTR_KEY)
        );
    }

    /**
     * Get the mapping from special tag names surrounding the regions to be highlighted in the preview text to text
     * attribute keys used to highlight the regions.
     *
     * @return {@code null} as the demo text does not contain any additional highlighting tags.
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
     * Get the text shown in the preview pane.
     *
     * @return {@link #SAMPLE_MARKDOWN_DOCUMENT}
     * @see #SAMPLE_MARKDOWN_DOCUMENT_PATH
     * @see #loadSampleMarkdownDocument()
     */
    @NonNls
    @NotNull
    public String getDemoText() {
        // FIXME Issue #14: Demo text is not highlighted because highlighting is done with an
        // ExternalAnnotator, which is not triggered in color settings pages.
        // return SAMPLE_MARKDOWN_DOCUMENT;
        return " ";
    }

    /**
     * Get the title of the page, shown as text in the dialog tab.
     *
     * @return the name as defined by {@link MarkdownBundle}
     */
    @NotNull
    public String getDisplayName() {
        return MarkdownBundle.message("markdown.filetype.name");
    }

    /**
     * Get the syntax highlighter which is used to highlight the text shown in the preview
     * pane of the page.
     *
     * @return an instance of {@link MarkdownSyntaxHighlighter}
     */
    @NotNull
    public SyntaxHighlighter getHighlighter() {
        return new MarkdownSyntaxHighlighter();
    }

    /**
     * Get the icon for the page, shown in the dialog tab.
     *
     * @return {@link MarkdownIcons#MARKDOWN_ICON}
     */
    @Nullable
    public Icon getIcon() {
        return MarkdownIcons.MARKDOWN_ICON;
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
            return FileUtil.loadTextAndClose(new InputStreamReader(MarkdownColorSettingsPage.class.getResourceAsStream(SAMPLE_MARKDOWN_DOCUMENT_PATH)));
        } catch (Exception e) {
            LOGGER.error("Failed loading sample Markdown document", e);
        }
        return MarkdownBundle.message("markdown.editor.colorsettingspage.sample-loading-error");
    }
}
