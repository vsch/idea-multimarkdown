/*
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
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LanguageTextField;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

public class MultiMarkdownGlobalSettingsConfigurable implements SearchableConfigurable {
    protected MultiMarkdownGlobalSettings globalSettings;

    protected MultiMarkdownSettingsPanel settingsPanel;

    @NotNull
    public String getId() {
        return MultiMarkdownLanguage.NAME;
    }

    final private ArrayList<ComponentSetting> componentSettings = new ArrayList<ComponentSetting>(50);

    public MultiMarkdownGlobalSettingsConfigurable() {
        globalSettings = MultiMarkdownGlobalSettings.getInstance();
        componentSettings.add(new CheckBoxComponent("abbreviationsCheckBox", globalSettings.abbreviations));
        componentSettings.add(new CheckBoxComponent("anchorLinksCheckBox", globalSettings.anchorLinks));
        componentSettings.add(new CheckBoxComponent("autoLinksCheckBox", globalSettings.autoLinks));
        componentSettings.add(new CheckBoxComponent("definitionsCheckBox", globalSettings.definitions));
        componentSettings.add(new CheckBoxComponent("fencedCodeBlocksCheckBox", globalSettings.fencedCodeBlocks));
        componentSettings.add(new CheckBoxComponent("forceListParaCheckBox", globalSettings.forceListPara));
        componentSettings.add(new CheckBoxComponent("hardWrapsCheckBox", globalSettings.hardWraps));
        componentSettings.add(new CheckBoxComponent("headerSpaceCheckBox", globalSettings.headerSpace));
        componentSettings.add(new CheckBoxComponent("relaxedHRulesCheckBox", globalSettings.relaxedHRules));
        componentSettings.add(new CheckBoxComponent("showHtmlTextAsModifiedCheckBox", globalSettings.showHtmlTextAsModified));
        componentSettings.add(new CheckBoxComponent("showHtmlTextCheckBox", globalSettings.showHtmlText));
        componentSettings.add(new CheckBoxComponent("smartsCheckBox", globalSettings.smarts));
        componentSettings.add(new CheckBoxComponent("strikethroughCheckBox", globalSettings.strikethrough));
        componentSettings.add(new CheckBoxComponent("enableTrimSpacesCheckBox", globalSettings.enableTrimSpaces));
        componentSettings.add(new CheckBoxComponent("suppressHTMLBlocksCheckBox", globalSettings.suppressHTMLBlocks));
        componentSettings.add(new CheckBoxComponent("suppressInlineHTMLCheckBox", globalSettings.suppressInlineHTML));
        componentSettings.add(new CheckBoxComponent("tablesCheckBox", globalSettings.tables));
        componentSettings.add(new CheckBoxComponent("taskListsCheckBox", globalSettings.taskLists));
        componentSettings.add(new CheckBoxComponent("wikiLinksCheckBox", globalSettings.wikiLinks));
        componentSettings.add(new CheckBoxComponent("iconBulletsCheckBox", globalSettings.iconBullets));
        //componentSettings.add(new CheckBoxComponent("todoCommentsCheckBox", globalSettings.todoComments));
        componentSettings.add(new CheckBoxComponent("quotesCheckBox", globalSettings.quotes));
        componentSettings.add(new SpinnerComponent("updateDelaySpinner", globalSettings.updateDelay));
        componentSettings.add(new SpinnerComponent("maxImgWidthSpinner", globalSettings.maxImgWidth));
        componentSettings.add(new SpinnerComponent("parsingTimeoutSpinner", globalSettings.parsingTimeout));
        componentSettings.add(new ComboBoxComponent("htmlThemeComboBox", globalSettings.htmlTheme));
        //componentSettings.add(new TextAreaComponent("textCustomCss", globalSettings.customCss));
        componentSettings.add(new EditorTextFieldComponent("textCustomCss", globalSettings.customCss));
    }

    public Runnable enableSearch(String s) {
        return null;
    }

    @Nls
    public String getDisplayName() {
        return getId();
    }

    public Icon getIcon() {
        return MultiMarkdownIcons.FILE;
    }

    public String getHelpTopic() {
        return getId();
    }

    public JComponent createComponent() {
        if (settingsPanel == null) settingsPanel = new MultiMarkdownSettingsPanel();
        reset();
        return settingsPanel.panel;
    }

    public boolean isModified() {
        if (settingsPanel == null) return true;
        for (ComponentSetting componentSetting : componentSettings) {
            if (componentSetting.isChanged()) return true;
        }
        return false;
    }

    public void apply() {
        if (settingsPanel == null) return;

        globalSettings.startGroupNotifications();
        for (ComponentSetting componentSetting : componentSettings) {
            componentSetting.setValue();
        }
        globalSettings.endGroupNotifications();
    }

    public void reset() {
        if (settingsPanel == null) return;
        for (ComponentSetting componentSetting : componentSettings) {
            componentSetting.reset();
        }
    }

    public void disposeUIResources() {
        settingsPanel = null;
    }

    abstract class ComponentSetting<T, S> {
        String componentName;

        S setting;

        ComponentSetting(String componentName, S setting) {
            this.componentName = componentName;
            this.setting = setting;
        }

        public boolean isChanged() {
            try {
                T component = (T) settingsPanel.getComponent(componentName);
                if (component != null) return isChanged(component);
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            }
            return false;
        }

        public void setValue() {
            try {
                T component = (T) settingsPanel.getComponent(componentName);
                if (component != null) setValue(component);
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            }
        }

        public void reset() {
            try {
                T component = (T) settingsPanel.getComponent(componentName);
                if (component != null) reset(component);
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            }
        }

        abstract public boolean isChanged(T component);

        abstract public void setValue(T component);

        abstract public void reset(T component);
    }

    class TextAreaComponent extends ComponentSetting<JTextArea, MultiMarkdownGlobalSettings.StringSetting> {
        TextAreaComponent(String component, MultiMarkdownGlobalSettings.StringSetting setting) { super(component, setting); }

        @Override public boolean isChanged(JTextArea component) { return setting.isChanged(component); }

        @Override public void setValue(JTextArea component) { setting.setValue(component); }

        @Override public void reset(JTextArea component) { setting.reset(component); }
    }

    class EditorTextFieldComponent extends ComponentSetting<EditorTextField, MultiMarkdownGlobalSettings.StringSetting> {
        EditorTextFieldComponent(String component, MultiMarkdownGlobalSettings.StringSetting setting) { super(component, setting); }

        @Override public boolean isChanged(EditorTextField component) { return setting.isChanged(component); }

        @Override public void setValue(EditorTextField component) { setting.setValue(component); }

        @Override public void reset(EditorTextField component) { setting.reset(component); }
    }

    class SpinnerComponent extends ComponentSetting<JSpinner, MultiMarkdownGlobalSettings.IntegerSetting> {
        SpinnerComponent(String componentName, MultiMarkdownGlobalSettings.IntegerSetting setting) { super(componentName, setting); }

        @Override public boolean isChanged(JSpinner component) { return setting.isChanged(component); }

        @Override public void setValue(JSpinner component) { setting.setValue(component); }

        @Override public void reset(JSpinner component) { setting.reset(component); }
    }

    class ComboBoxComponent extends ComponentSetting<JComboBox, MultiMarkdownGlobalSettings.IntegerSetting> {
        ComboBoxComponent(String componentName, MultiMarkdownGlobalSettings.IntegerSetting setting) { super(componentName, setting); }

        @Override public boolean isChanged(JComboBox component) { return setting.isChanged(component); }

        @Override public void setValue(JComboBox component) { setting.setValue(component); }

        @Override public void reset(JComboBox component) { setting.reset(component); }
    }

    class CheckBoxComponent extends ComponentSetting<JCheckBox, MultiMarkdownGlobalSettings.BooleanSetting> {
        CheckBoxComponent(String componentName, MultiMarkdownGlobalSettings.BooleanSetting setting) { super(componentName, setting); }

        @Override public boolean isChanged(JCheckBox component) { return setting.isChanged(component); }

        @Override public void setValue(JCheckBox component) { setting.setValue(component); }

        @Override public void reset(JCheckBox component) { setting.reset(component); }
    }
}
