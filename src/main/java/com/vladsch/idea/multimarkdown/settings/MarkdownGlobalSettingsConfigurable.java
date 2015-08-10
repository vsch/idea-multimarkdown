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
package com.vladsch.idea.multimarkdown.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.vladsch.idea.multimarkdown.MarkdownIcons;
import com.vladsch.idea.multimarkdown.MarkdownLanguage;
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

    /** The settings storage object. */
    protected MarkdownGlobalSettings globalSettings;

    /** The settings UI form. */
    protected MarkdownSettingsPanel settingsPanel;

    /** Build a new instance of {@link MarkdownGlobalSettingsConfigurable}. */
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
               || settingsPanel.parsingTimeoutSpinner == null || globalSettings.getParsingTimeout() != (Integer) settingsPanel.parsingTimeoutSpinner.getValue()
               || settingsPanel.abbreviationsCheckBox == null || globalSettings.isAbbreviations() != settingsPanel.abbreviationsCheckBox.isSelected()
               || settingsPanel.autoLinksCheckBox == null || globalSettings.isAutoLinks() != settingsPanel.autoLinksCheckBox.isSelected()
               || settingsPanel.wikiLinksCheckBox == null || globalSettings.isWikiLinks() != settingsPanel.wikiLinksCheckBox.isSelected()
               || settingsPanel.definitionsCheckBox == null || globalSettings.isDefinitions() != settingsPanel.definitionsCheckBox.isSelected()
               || settingsPanel.fencedCodeBlocksCheckBox == null || globalSettings.isFencedCodeBlocks() != settingsPanel.fencedCodeBlocksCheckBox.isSelected()
               || settingsPanel.hardWrapsCheckBox == null || globalSettings.isHardWraps() != settingsPanel.hardWrapsCheckBox.isSelected()
               || settingsPanel.taskListsCheckBox == null || globalSettings.isTaskLists() != settingsPanel.taskListsCheckBox.isSelected()
               || settingsPanel.quotesCheckBox == null || globalSettings.isQuotes() != settingsPanel.quotesCheckBox.isSelected()
               || settingsPanel.smartsCheckBox == null || globalSettings.isSmarts() != settingsPanel.smartsCheckBox.isSelected()
               || settingsPanel.suppressHTMLBlocksCheckBox == null || globalSettings.isSuppressHTMLBlocks() != settingsPanel.suppressHTMLBlocksCheckBox.isSelected()
               || settingsPanel.suppressInlineHTMLCheckBox == null || globalSettings.isSuppressInlineHTML() != settingsPanel.suppressInlineHTMLCheckBox.isSelected()
               || settingsPanel.tablesCheckBox == null || globalSettings.isTables() != settingsPanel.tablesCheckBox.isSelected()
               || settingsPanel.strikethroughCheckBox == null || globalSettings.isStrikethrough() != settingsPanel.strikethroughCheckBox.isSelected()
               || settingsPanel.updateDelaySpinner == null || globalSettings.getUpdateDelay() != (Integer)settingsPanel.updateDelaySpinner.getValue()
               || settingsPanel.maxImgWidthSpinner == null || globalSettings.getMaxImgWidth() != (Integer)settingsPanel.maxImgWidthSpinner.getValue()
               || settingsPanel.textCustomCss == null || !globalSettings.getCustomCss().equals(settingsPanel.textCustomCss.getText())
                ;
    }

    /** Apply modifications to the settings done in the UI. */
    public void apply() {
        if (settingsPanel != null) {
            globalSettings.startGroupNotifications();
            globalSettings.setParsingTimeout((Integer) settingsPanel.parsingTimeoutSpinner.getValue());
            globalSettings.setAbbreviations(settingsPanel.abbreviationsCheckBox != null && settingsPanel.abbreviationsCheckBox.isSelected());
            globalSettings.setAutoLinks(settingsPanel.autoLinksCheckBox != null && settingsPanel.autoLinksCheckBox.isSelected());
            globalSettings.setWikiLinks(settingsPanel.wikiLinksCheckBox != null && settingsPanel.wikiLinksCheckBox.isSelected());
            globalSettings.setDefinitions(settingsPanel.definitionsCheckBox != null && settingsPanel.definitionsCheckBox.isSelected());
            globalSettings.setFencedCodeBlocks(settingsPanel.fencedCodeBlocksCheckBox != null && settingsPanel.fencedCodeBlocksCheckBox.isSelected());
            globalSettings.setHardWraps(settingsPanel.hardWrapsCheckBox != null && settingsPanel.hardWrapsCheckBox.isSelected());
            globalSettings.setTaskLists(settingsPanel.taskListsCheckBox != null && settingsPanel.taskListsCheckBox.isSelected());
            globalSettings.setQuotes(settingsPanel.quotesCheckBox != null && settingsPanel.quotesCheckBox.isSelected());
            globalSettings.setSmarts(settingsPanel.smartsCheckBox != null && settingsPanel.smartsCheckBox.isSelected());
            globalSettings.setSuppressHTMLBlocks(settingsPanel.suppressHTMLBlocksCheckBox != null && settingsPanel.suppressHTMLBlocksCheckBox.isSelected());
            globalSettings.setSuppressInlineHTML(settingsPanel.suppressInlineHTMLCheckBox != null && settingsPanel.suppressInlineHTMLCheckBox.isSelected());
            globalSettings.setTables(settingsPanel.tablesCheckBox != null && settingsPanel.tablesCheckBox.isSelected());
            globalSettings.setStrikethrough(settingsPanel.strikethroughCheckBox != null && settingsPanel.strikethroughCheckBox.isSelected());
            globalSettings.setUpdateDelay((Integer) settingsPanel.updateDelaySpinner.getValue());
            globalSettings.setMaxWidth((Integer) settingsPanel.maxImgWidthSpinner.getValue());
            globalSettings.setCustomCss(settingsPanel.textCustomCss.getText());
            globalSettings.endGroupNotifications();
        }
    }

    /** Reset UI with settings values. */
    public void reset() {
        if (settingsPanel != null) {
            if (settingsPanel.parsingTimeoutSpinner != null) settingsPanel.parsingTimeoutSpinner.setValue(globalSettings.getParsingTimeout());
            if (settingsPanel.abbreviationsCheckBox != null) settingsPanel.abbreviationsCheckBox.setSelected(globalSettings.isAbbreviations());
            if (settingsPanel.autoLinksCheckBox != null) settingsPanel.autoLinksCheckBox.setSelected(globalSettings.isAutoLinks());
            if (settingsPanel.wikiLinksCheckBox != null) settingsPanel.wikiLinksCheckBox.setSelected(globalSettings.isWikiLinks());
            if (settingsPanel.definitionsCheckBox != null) settingsPanel.definitionsCheckBox.setSelected(globalSettings.isDefinitions());
            if (settingsPanel.fencedCodeBlocksCheckBox != null) settingsPanel.fencedCodeBlocksCheckBox.setSelected(globalSettings.isFencedCodeBlocks());
            if (settingsPanel.hardWrapsCheckBox != null) settingsPanel.hardWrapsCheckBox.setSelected(globalSettings.isHardWraps());
            if (settingsPanel.taskListsCheckBox != null) settingsPanel.taskListsCheckBox.setSelected(globalSettings.isTaskLists());
            if (settingsPanel.quotesCheckBox != null) settingsPanel.quotesCheckBox.setSelected(globalSettings.isQuotes());
            if (settingsPanel.smartsCheckBox != null) settingsPanel.smartsCheckBox.setSelected(globalSettings.isSmarts());
            if (settingsPanel.suppressHTMLBlocksCheckBox != null) settingsPanel.suppressHTMLBlocksCheckBox.setSelected(globalSettings.isSuppressHTMLBlocks());
            if (settingsPanel.suppressInlineHTMLCheckBox != null) settingsPanel.suppressInlineHTMLCheckBox.setSelected(globalSettings.isSuppressInlineHTML());
            if (settingsPanel.tablesCheckBox != null) settingsPanel.tablesCheckBox.setSelected(globalSettings.isTables());
            if (settingsPanel.strikethroughCheckBox != null) settingsPanel.strikethroughCheckBox.setSelected(globalSettings.isStrikethrough());
            if (settingsPanel.updateDelaySpinner != null) settingsPanel.updateDelaySpinner.setValue(globalSettings.getUpdateDelay());
            if (settingsPanel.maxImgWidthSpinner != null) settingsPanel.maxImgWidthSpinner.setValue(globalSettings.getMaxImgWidth());
            if (settingsPanel.textCustomCss != null) settingsPanel.textCustomCss.setText(globalSettings.getCustomCss());
        }
    }

    /** Dispose UI resources. */
    public void disposeUIResources() {
        settingsPanel = null;
    }
}
