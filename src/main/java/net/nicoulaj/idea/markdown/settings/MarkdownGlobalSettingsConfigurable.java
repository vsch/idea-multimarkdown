/*
 * Copyright (c) 2011-2012 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
import java.util.HashSet;
import java.util.Set;

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
                || globalSettings.isAbbreviations() != settingsPanel.abbreviationsCheckBox.isSelected()
                || globalSettings.isAutoLinks() != settingsPanel.autoLinksCheckBox.isSelected()
                || globalSettings.isWikiLinks() != settingsPanel.wikiLinksCheckBox.isSelected()
                || globalSettings.isDefinitions() != settingsPanel.definitionsCheckBox.isSelected()
                || globalSettings.isFencedCodeBlocks() != settingsPanel.fencedCodeBlocksCheckBox.isSelected()
                || globalSettings.isHardWraps() != settingsPanel.hardWrapsCheckBox.isSelected()
                || globalSettings.isQuotes() != settingsPanel.quotesCheckBox.isSelected()
                || globalSettings.isSmarts() != settingsPanel.smartsCheckBox.isSelected()
                || globalSettings.isSuppressHTMLBlocks() != settingsPanel.suppressHTMLBlocksCheckBox.isSelected()
                || globalSettings.isSuppressInlineHTML() != settingsPanel.suppressInlineHTMLCheckBox.isSelected()
                || globalSettings.isTables() != settingsPanel.tablesCheckBox.isSelected();
    }

    /**
     * Apply modifications to the settings done in the UI.
     */
    public void apply() {
        if (settingsPanel != null) {
            globalSettings.setAbbreviations(settingsPanel.abbreviationsCheckBox.isSelected());
            globalSettings.setAutoLinks(settingsPanel.autoLinksCheckBox.isSelected());
            globalSettings.setWikiLinks(settingsPanel.wikiLinksCheckBox.isSelected());
            globalSettings.setDefinitions(settingsPanel.definitionsCheckBox.isSelected());
            globalSettings.setFencedCodeBlocks(settingsPanel.fencedCodeBlocksCheckBox.isSelected());
            globalSettings.setHardWraps(settingsPanel.hardWrapsCheckBox.isSelected());
            globalSettings.setQuotes(settingsPanel.quotesCheckBox.isSelected());
            globalSettings.setSmarts(settingsPanel.smartsCheckBox.isSelected());
            globalSettings.setSuppressHTMLBlocks(settingsPanel.suppressHTMLBlocksCheckBox.isSelected());
            globalSettings.setSuppressInlineHTML(settingsPanel.suppressInlineHTMLCheckBox.isSelected());
            globalSettings.setTables(settingsPanel.tablesCheckBox.isSelected());
        }
    }

    /**
     * Reset UI with settings values.
     */
    public void reset() {
        if (settingsPanel != null) {
            settingsPanel.abbreviationsCheckBox.setSelected(globalSettings.isAbbreviations());
            settingsPanel.autoLinksCheckBox.setSelected(globalSettings.isAutoLinks());
            settingsPanel.wikiLinksCheckBox.setSelected(globalSettings.isWikiLinks());
            settingsPanel.definitionsCheckBox.setSelected(globalSettings.isDefinitions());
            settingsPanel.fencedCodeBlocksCheckBox.setSelected(globalSettings.isFencedCodeBlocks());
            settingsPanel.hardWrapsCheckBox.setSelected(globalSettings.isHardWraps());
            settingsPanel.quotesCheckBox.setSelected(globalSettings.isQuotes());
            settingsPanel.smartsCheckBox.setSelected(globalSettings.isSmarts());
            settingsPanel.suppressHTMLBlocksCheckBox.setSelected(globalSettings.isSuppressHTMLBlocks());
            settingsPanel.suppressInlineHTMLCheckBox.setSelected(globalSettings.isSuppressInlineHTML());
            settingsPanel.tablesCheckBox.setSelected(globalSettings.isTables());
        }
    }

    /**
     * Dispose UI resources.
     */
    public void disposeUIResources() {
        settingsPanel = null;
    }
}
