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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
     * TODO Add Javadoc comment.
     */
    protected static final ColorDescriptor[] EMPTY_COLOR_DESCRIPTOR_ARRAY = new ColorDescriptor[]{};

    /**
     * TODO Add Javadoc comment.
     */
    @NonNls
    protected static final String SAMPLE_MARKDOWN_DOCUMENT_PATH = "/net/nicoulaj/idea/markdown/sample-document.md";

    /**
     * TODO Add Javadoc comment.
     */
    protected static final String SAMPLE_MARKDOWN_DOCUMENT = loadSampleMarkdownDocument();

    /**
     * TODO Add Javadoc comment.
     */
    protected final Set<AttributesDescriptor> attributeDescriptors = new HashSet<AttributesDescriptor>();

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
                MarkdownBundle.message("markdown.editor.colorsettingspage.bold-text"),
                MarkdownHighlighterColors.BOLD_TEXT_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.italic-text"),
                MarkdownHighlighterColors.ITALIC_TEXT_ATTR_KEY)
        );
        attributeDescriptors.add(new AttributesDescriptor(
                MarkdownBundle.message("markdown.editor.colorsettingspage.link"),
                MarkdownHighlighterColors.LINK_ATTR_KEY)
        );
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @Nullable
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return attributeDescriptors.toArray(new AttributesDescriptor[attributeDescriptors.size()]);
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
        return EMPTY_COLOR_DESCRIPTOR_ARRAY;
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NonNls
    @NotNull
    public String getDemoText() {
        return SAMPLE_MARKDOWN_DOCUMENT;
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    protected static String loadSampleMarkdownDocument() {
        try {
            return FileUtil.loadTextAndClose(new InputStreamReader(MarkdownColorSettingsPage.class.getResourceAsStream(SAMPLE_MARKDOWN_DOCUMENT_PATH)));
        } catch (Exception e) {
            LOGGER.error("Failed loading sample Markdown document", e);
        }
        return MarkdownBundle.message("markdown.editor.colorsettingspage.sample-loading-error");
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public String getDisplayName() {
        return MarkdownBundle.message("markdown.filetype.name");
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public SyntaxHighlighter getHighlighter() {
        return new MarkdownSyntaxHighlighter();
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @Nullable
    public Icon getIcon() {
        return MarkdownIcons.MARKDOWN_ICON;
    }
}
