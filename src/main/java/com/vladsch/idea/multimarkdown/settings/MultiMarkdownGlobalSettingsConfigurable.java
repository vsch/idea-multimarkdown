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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.ui.EditorTextField;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

public class MultiMarkdownGlobalSettingsConfigurable implements SearchableConfigurable {
    protected MultiMarkdownGlobalSettings globalSettings;

    protected MultiMarkdownSettingsPanel settingsPanel;

    @NotNull
    public String getId() {
        return MultiMarkdownFileType.INSTANCE.getName();
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
        componentSettings.add(new CheckBoxComponent("useCustomCssCheckBox", globalSettings.useCustomCss));
        componentSettings.add(new CheckBoxComponent("quotesCheckBox", globalSettings.quotes));
        componentSettings.add(new CheckBoxComponent("useOldPreviewCheckBox", globalSettings.useOldPreview));
        componentSettings.add(new CheckBoxComponent("enableFirebugCheckBox", globalSettings.enableFirebug));
        componentSettings.add(new CheckBoxComponent("useHighlightJsCheckBox", globalSettings.useHighlightJs));
        componentSettings.add(new CheckBoxComponent("includesHljsCssCheckBox", globalSettings.includesHljsCss));
        componentSettings.add(new CheckBoxComponent("includesLayoutCssCheckBox", globalSettings.includesLayoutCss));
        componentSettings.add(new CheckBoxComponent("includesColorsCssCheckBox", globalSettings.includesColorsCss));
        componentSettings.add(new CheckBoxComponent("githubWikiLinksCheckBox", globalSettings.githubWikiLinks));
        componentSettings.add(new CheckBoxComponent("footnotesCheckBox", globalSettings.footnotes));
        componentSettings.add(new SpinnerIntegerComponent("updateDelaySpinner", globalSettings.updateDelay));
        componentSettings.add(new SpinnerIntegerComponent("maxImgWidthSpinner", globalSettings.maxImgWidth));
        componentSettings.add(new SpinnerIntegerComponent("parsingTimeoutSpinner", globalSettings.parsingTimeout));
        componentSettings.add(new TabbedPaneIntegerComponent("tabbedPane", globalSettings.tabbedPaneIndex));
        componentSettings.add(new SpinnerDoubleComponent("pageZoomSpinner", globalSettings.pageZoom));
        //componentSettings.add(new ComboBoxComponent("htmlThemeComboBox", globalSettings.htmlTheme));
        componentSettings.add(new ListComponent("htmlThemeList", globalSettings.htmlTheme));
        componentSettings.add(new EditorTextFieldComponent("textCustomCss", globalSettings.customCss, false));
        componentSettings.add(new EditorTextFieldComponent("textCustomCss", globalSettings.customFxCss, true));
        componentSettings.add(new SettingsComponentState("textCustomCss", globalSettings.customCssEditorState, false));
        componentSettings.add(new SettingsComponentState("textCustomCss", globalSettings.customFxCssEditorState, true));
        componentSettings.add(new TextAreaComponent("licenseTextArea", globalSettings.licenseCode));
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

        if (settingsPanel.haveCustomizableEditor) {
            reset();
        }

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

        runInDispatchThread(new Runnable() {
            @Override
            public void run() {
                applyRaw();
            }
        }, false);
    }

    protected void applyRaw() {
        if (settingsPanel == null) return;

        globalSettings.startGroupNotifications();
        for (ComponentSetting componentSetting : componentSettings) {
            componentSetting.setValue();
        }
        // vsch: hardwire github wiki links
        globalSettings.githubWikiLinks.setValue(true);
        globalSettings.endGroupNotifications();
    }

    protected void runInDispatchThread(Runnable runnable, boolean wait) {
        if (ApplicationManager.getApplication().isDispatchThread()) {
            runnable.run();
        } else if (wait) {
            ApplicationManager.getApplication().invokeLater(runnable, ModalityState.any());
        } else {
            ApplicationManager.getApplication().invokeLater(runnable, ModalityState.any());
        }
    }

    public void reset() {
        runInDispatchThread(new Runnable() {
            @Override
            public void run() {
                resetRaw();
            }
        }, false);
    }

    protected void resetRaw() {
        if (settingsPanel == null) return;

        // vsch: hardwire github wiki links
        globalSettings.githubWikiLinks.setValue(true);

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
                //ex.printStackTrace();
            }
            return false;
        }

        public void setValue() {
            try {
                T component = (T) settingsPanel.getComponent(componentName);
                if (component != null) setValue(component);
            } catch (ClassCastException ex) {
                //ex.printStackTrace();
            }
        }

        public void reset() {
            try {
                T component = (T) settingsPanel.getComponent(componentName);
                if (component != null) reset(component);
            } catch (ClassCastException ex) {
                //ex.printStackTrace();
            }
        }

        abstract public boolean isChanged(T component);

        abstract public void setValue(T component);

        abstract public void reset(T component);
    }

    class EditorTextFieldComponent extends ComponentSetting<EditorTextField, Settings.StringSetting> {
        private final Boolean isFxPreviewState;

        EditorTextFieldComponent(String component, Settings.StringSetting setting) {
            super(component, setting);
            this.isFxPreviewState = null;
        }

        EditorTextFieldComponent(String component, Settings.StringSetting setting, boolean isFxPreviewState) {
            super(component, setting);
            this.isFxPreviewState = isFxPreviewState;
        }

        @Override
        public boolean isChanged(EditorTextField component) {
            if ((component instanceof CustomizableEditorTextField) && (isFxPreviewState == null || MultiMarkdownGlobalSettings.isFxHtmlPreview == isFxPreviewState)) {
                return setting.isChanged(component);
            }
            return false;
        }

        @Override
        public void setValue(EditorTextField component) {
            if ((component instanceof CustomizableEditorTextField) && (isFxPreviewState == null || MultiMarkdownGlobalSettings.isFxHtmlPreview == isFxPreviewState)) {
                setting.setValue(component);
            }
        }

        @Override
        public void reset(EditorTextField component) {
            if ((component instanceof CustomizableEditorTextField) && isFxPreviewState == null || MultiMarkdownGlobalSettings.isFxHtmlPreview == isFxPreviewState) {
                setting.reset(component);
            }
        }
    }

    class TextAreaComponent extends ComponentSetting<JTextArea, Settings.StringSetting> {
        TextAreaComponent(String component, Settings.StringSetting setting) {
            super(component, setting);
        }

        @Override
        public boolean isChanged(JTextArea component) { return setting.isChanged(component); }

        @Override
        public void setValue(JTextArea component) { setting.setValue(component); }

        @Override
        public void reset(JTextArea component) { setting.reset(component); }
    }

    class SettingsComponentState extends ComponentSetting<ComponentState, Settings.ElementSetting> {
        private final Boolean isFxPreviewState;

        SettingsComponentState(String component, Settings.ElementSetting setting) {
            super(component, setting);
            this.isFxPreviewState = null;
        }

        SettingsComponentState(String component, Settings.ElementSetting setting, boolean isFxPreview) {
            super(component, setting);
            this.isFxPreviewState = isFxPreview;
        }

        @Override
        public boolean isChanged(ComponentState component) {
            if (isFxPreviewState == null || MultiMarkdownGlobalSettings.isFxHtmlPreview == isFxPreviewState) {
                return setting.getValue() == null || component.isChanged(setting.getValue());
            }
            return false;
        }

        @Override
        public void setValue(ComponentState component) {
            if (isFxPreviewState == null || MultiMarkdownGlobalSettings.isFxHtmlPreview == isFxPreviewState) {
                setting.setValue(component.getState(setting.persistName));
            }
        }

        @Override
        public void reset(ComponentState component) {
            if (isFxPreviewState == null || MultiMarkdownGlobalSettings.isFxHtmlPreview == isFxPreviewState) {
                if (setting.getValue() != null) component.loadState(setting.getValue());
            }
        }
    }

    class SpinnerIntegerComponent extends ComponentSetting<JSpinner, Settings.IntegerSetting> {
        SpinnerIntegerComponent(String componentName, Settings.IntegerSetting setting) { super(componentName, setting); }

        @Override
        public boolean isChanged(JSpinner component) { return setting.isChanged(component); }

        @Override
        public void setValue(JSpinner component) { setting.setValue(component); }

        @Override
        public void reset(JSpinner component) { setting.reset(component); }
    }

    class TabbedPaneIntegerComponent extends ComponentSetting<JTabbedPane, Settings.IntegerSetting> {
        TabbedPaneIntegerComponent(String componentName, Settings.IntegerSetting setting) { super(componentName, setting); }

        @Override
        public boolean isChanged(JTabbedPane component) { return setting.isChanged(component); }

        @Override
        public void setValue(JTabbedPane component) { setting.setValue(component); }

        @Override
        public void reset(JTabbedPane component) { setting.reset(component); }
    }

    class SpinnerDoubleComponent extends ComponentSetting<JSpinner, Settings.DoubleSetting> {
        SpinnerDoubleComponent(String componentName, Settings.DoubleSetting setting) { super(componentName, setting); }

        @Override
        public boolean isChanged(JSpinner component) { return setting.isChanged(component); }

        @Override
        public void setValue(JSpinner component) { setting.setValue(component); }

        @Override
        public void reset(JSpinner component) { setting.reset(component); }
    }

    class ComboBoxComponent extends ComponentSetting<JComboBox, Settings.IntegerSetting> {
        ComboBoxComponent(String componentName, Settings.IntegerSetting setting) { super(componentName, setting); }

        @Override
        public boolean isChanged(JComboBox component) { return setting.isChanged(component); }

        @Override
        public void setValue(JComboBox component) { setting.setValue(component); }

        @Override
        public void reset(JComboBox component) { setting.reset(component); }
    }

    class ListComponent extends ComponentSetting<JList, Settings.IntegerSetting> {
        ListComponent(String componentName, Settings.IntegerSetting setting) { super(componentName, setting); }

        @Override
        public boolean isChanged(JList component) { return setting.isChanged(component); }

        @Override
        public void setValue(JList component) { setting.setValue(component); }

        @Override
        public void reset(JList component) { setting.reset(component); }
    }

    class CheckBoxComponent extends ComponentSetting<JCheckBox, Settings.BooleanSetting> {
        CheckBoxComponent(String componentName, Settings.BooleanSetting setting) { super(componentName, setting); }

        @Override
        public boolean isChanged(JCheckBox component) { return setting.isChanged(component); }

        @Override
        public void setValue(JCheckBox component) { setting.setValue(component); }

        @Override
        public void reset(JCheckBox component) { setting.reset(component); }
    }
}
