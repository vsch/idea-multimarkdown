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
package net.nicoulaj.idea.markdown.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import net.nicoulaj.idea.markdown.MarkdownIcons;
import net.nicoulaj.idea.markdown.MarkdownLanguage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Configuration interface for {@link MarkdownGlobalSettings}.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @see MarkdownGlobalSettings
 * @see MarkdownSettingsPanel
 * @since 0.6
 */
public class MarkdownGlobalSettingsConfigurable implements SearchableConfigurable {

    /**
     * The settings storage object.
     */
    protected MarkdownGlobalSettings globalSettings;

    /**
     * The settings UI form.
     */
    protected MarkdownSettingsPanel settingsPanel;

    /**
     * Build a new instance of {@link MarkdownGlobalSettingsConfigurable}.
     */
    public MarkdownGlobalSettingsConfigurable() {
        globalSettings = MarkdownGlobalSettings.getInstance();
    }

    /**
     * Get the ID of this {@link SearchableConfigurable}.
     *
     * @return {@link MarkdownLanguage#LANGUAGE_NAME}
     */
    @NotNull
    public String getId() {
        return MarkdownLanguage.LANGUAGE_NAME;
    }

    /**
     * Search for a term.
     *
     * @param s the term to search.
     * @return <code>null</code>
     */
    public Runnable enableSearch(String s) {
        return null;
    }

    /**
     * Get the displayable name of this {@link com.intellij.openapi.options.Configurable}.
     *
     * @return {@link #getId()}
     */
    @Nls
    public String getDisplayName() {
        return getId();
    }

    /**
     * Get the icon of this {@link com.intellij.openapi.options.Configurable}.
     *
     * @return {@link MarkdownIcons#MARKDOWN_ICON}
     */
    public Icon getIcon() {
        return MarkdownIcons.MARKDOWN_ICON;
    }

    /**
     * Get the help topic name of this {@link com.intellij.openapi.options.Configurable}.
     *
     * @return {@link #getId()}
     */
    public String getHelpTopic() {
        return getId();
    }

    /**
     * Setup the configuration UI if needed.
     *
     * @return the UI main panel.
     */
    public JComponent createComponent() {
        if (settingsPanel == null) settingsPanel = new MarkdownSettingsPanel();
        reset();
        return settingsPanel.panel;
    }

    /**
     * Whether the settings are modified in the UI compared to the settings.
     *
     * @return true if settings are modified in the UI.
     */
    public boolean isModified() {
        return settingsPanel == null
        || globalSettings.abbreviations != settingsPanel.abbreviationsCheckBox.isSelected()
        || globalSettings.autoLinks != settingsPanel.autoLinksCheckBox.isSelected()
        || globalSettings.definitions != settingsPanel.definitionsCheckBox.isSelected()
        || globalSettings.fencedCodeBlocks != settingsPanel.fencedCodeBlocksCheckBox.isSelected()
        || globalSettings.hardWraps != settingsPanel.hardWrapsCheckBox.isSelected()
        || globalSettings.quotes != settingsPanel.quotesCheckBox.isSelected()
        || globalSettings.smarts != settingsPanel.smartsCheckBox.isSelected()
        || globalSettings.suppressHTMLBlocks != settingsPanel.suppressHTMLBlocksCheckBox.isSelected()
        || globalSettings.suppressInlineHTML != settingsPanel.suppressInlineHTMLCheckBox.isSelected()
        || globalSettings.tables != settingsPanel.tablesCheckBox.isSelected();
    }

    /**
     * Apply modifications to the settings done in the UI.
     */
    public void apply() {
        if (settingsPanel != null) {
            globalSettings.abbreviations = settingsPanel.abbreviationsCheckBox.isSelected();
            globalSettings.autoLinks = settingsPanel.autoLinksCheckBox.isSelected();
            globalSettings.definitions = settingsPanel.definitionsCheckBox.isSelected();
            globalSettings.fencedCodeBlocks = settingsPanel.fencedCodeBlocksCheckBox.isSelected();
            globalSettings.hardWraps = settingsPanel.hardWrapsCheckBox.isSelected();
            globalSettings.quotes = settingsPanel.quotesCheckBox.isSelected();
            globalSettings.smarts = settingsPanel.smartsCheckBox.isSelected();
            globalSettings.suppressHTMLBlocks = settingsPanel.suppressHTMLBlocksCheckBox.isSelected();
            globalSettings.suppressInlineHTML = settingsPanel.suppressInlineHTMLCheckBox.isSelected();
            globalSettings.tables = settingsPanel.tablesCheckBox.isSelected();
        }
    }

    /**
     * Reset UI with settings values.
     */
    public void reset() {
        if (settingsPanel != null) {
            settingsPanel.abbreviationsCheckBox.setSelected(globalSettings.abbreviations);
            settingsPanel.autoLinksCheckBox.setSelected(globalSettings.autoLinks);
            settingsPanel.definitionsCheckBox.setSelected(globalSettings.definitions);
            settingsPanel.fencedCodeBlocksCheckBox.setSelected(globalSettings.fencedCodeBlocks);
            settingsPanel.hardWrapsCheckBox.setSelected(globalSettings.hardWraps);
            settingsPanel.quotesCheckBox.setSelected(globalSettings.quotes);
            settingsPanel.smartsCheckBox.setSelected(globalSettings.smarts);
            settingsPanel.suppressHTMLBlocksCheckBox.setSelected(globalSettings.suppressHTMLBlocks);
            settingsPanel.suppressInlineHTMLCheckBox.setSelected(globalSettings.suppressInlineHTML);
            settingsPanel.tablesCheckBox.setSelected(globalSettings.tables);
        }
    }

    /**
     * Dispose UI resources.
     */
    public void disposeUIResources() {
        settingsPanel = null;
    }
}
