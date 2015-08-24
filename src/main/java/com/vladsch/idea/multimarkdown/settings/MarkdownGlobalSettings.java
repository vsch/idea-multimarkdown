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

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.pegdown.Extensions;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@State(
        name = "MultiMarkdownSettings",
        storages = @Storage(id = "other", file = "$APP_CONFIG$/multimarkdown.xml")
)
public class MarkdownGlobalSettings implements PersistentStateComponent<Element> {

    /** A set of listeners to this object state changes. */
    protected Set<WeakReference<MarkdownGlobalSettingsListener>> listeners;

    private int groupNotifications;

    private boolean needToNotify;

    final private ArrayList<Setting> settings = new ArrayList<Setting>(50);

    public static MarkdownGlobalSettings getInstance() {
        return ServiceManager.getService(MarkdownGlobalSettings.class);
    }

    // these self-add to the list of settings
    final public BooleanSetting abbreviations = new BooleanSetting(false, "abbreviations", Extensions.ABBREVIATIONS);
    final public BooleanSetting anchorLinks = new BooleanSetting(false, "anchorLinks", Extensions.ANCHORLINKS);
    final public BooleanSetting autoLinks = new BooleanSetting(false, "autoLinks", Extensions.AUTOLINKS);
    final public BooleanSetting definitions = new BooleanSetting(false, "definitions", Extensions.DEFINITIONS);
    final public BooleanSetting enableTrimSpaces = new BooleanSetting(false, "enableTrimSpaces", 0);
    final public BooleanSetting fencedCodeBlocks = new BooleanSetting(false, "fencedCodeBlocks", Extensions.FENCED_CODE_BLOCKS);
    final public BooleanSetting forceListPara = new BooleanSetting(false, "forceListPara", Extensions.FORCELISTITEMPARA);
    final public BooleanSetting hardWraps = new BooleanSetting(false, "hardWraps", Extensions.HARDWRAPS);
    final public BooleanSetting headerSpace = new BooleanSetting(false, "headerSpace", Extensions.ATXHEADERSPACE);
    final public BooleanSetting quotes = new BooleanSetting(false, "quotes", Extensions.QUOTES);
    final public BooleanSetting relaxedHRules = new BooleanSetting(false, "relaxedHRules", Extensions.RELAXEDHRULES);
    final public BooleanSetting showHtmlText = new BooleanSetting(true, "showHtmlText", 0);
    final public BooleanSetting showHtmlTextAsModified = new BooleanSetting(false, "showHtmlTextAsModified", 0);
    final public BooleanSetting smarts = new BooleanSetting(false, "smarts", Extensions.SMARTS);
    final public BooleanSetting strikethrough = new BooleanSetting(false, "strikethrough", Extensions.STRIKETHROUGH);
    final public BooleanSetting suppressHTMLBlocks = new BooleanSetting(false, "suppressHTMLBlocks", Extensions.SUPPRESS_HTML_BLOCKS);
    final public BooleanSetting suppressInlineHTML = new BooleanSetting(false, "suppressInlineHTML", Extensions.SUPPRESS_INLINE_HTML);
    final public BooleanSetting tables = new BooleanSetting(false, "tables", Extensions.TABLES);
    final public BooleanSetting taskLists = new BooleanSetting(false, "taskLists", 0); //Extensions.TASKLISTITEMS);
    final public BooleanSetting wikiLinks = new BooleanSetting(false, "wikiLinks", Extensions.WIKILINKS);
    final public BooleanSetting todoComments = new BooleanSetting(false, "todoComments", 0);
    final public IntegerSetting htmlTheme = new IntegerSetting(0, "htmlTheme");
    final public IntegerSetting maxImgWidth = new IntegerSetting(900, "maxImgWidth");
    final public IntegerSetting parsingTimeout = new IntegerSetting(10000, "parsingTimeout");
    final public IntegerSetting updateDelay = new IntegerSetting(1000, "updateDelay");
    final public StringSetting customCss = new StringSetting("", "customCss");

    public Element getState() {
        final Element element = new Element("MarkdownSettings");
        for (Setting setting : settings) {
            setting.saveState(element);
        }
        return element;
    }

    public void loadState(@NotNull Element element) {
        startGroupNotifications();
        for (Setting setting : settings) {
            setting.loadState(element);
        }
        endGroupNotifications();
    }

    public int getExtensionsValue() {
        int pegdownFlags = 0;
        for (Setting setting : settings) {
            pegdownFlags |= setting.selectedPegdownFlags();
        }
        return pegdownFlags;
    }

    public void addListener(@NotNull final MarkdownGlobalSettingsListener listener) {
        if (listeners == null) listeners = new HashSet<WeakReference<MarkdownGlobalSettingsListener>>();
        listeners.add(new WeakReference<MarkdownGlobalSettingsListener>(listener));
    }

    public void removeListener(@NotNull final MarkdownGlobalSettingsListener listener) {
        if (listeners != null) listeners.remove(listener);
    }

    public int startGroupNotifications() {
        return groupNotifications++;
    }

    public int endGroupNotifications() {
        if (groupNotifications == 0) return 0;

        if (--groupNotifications == 0) {
            if (needToNotify) {
                needToNotify = false;
                notifyListeners();
            }
        }

        return groupNotifications;
    }

    protected void notifyListeners() {
        if (groupNotifications > 0) {
            needToNotify = true;
        } else {
            MarkdownGlobalSettingsListener listener;
            if (listeners != null)
                for (final WeakReference<MarkdownGlobalSettingsListener> listenerRef : listeners)
                    if ((listener = listenerRef.get()) != null) listener.handleSettingsChanged(this);
        }
    }

    public abstract class Setting<T> {

        protected T value;

        protected String persistName;

        public Setting(T initialValue, String persistName) {
            value = initialValue;
            this.persistName = persistName;
            settings.add(this);
        }

        public T getValue() { return value; }

        public void setValue(T value) {
            if (this.value != value) {
                this.value = value;
                notifyListeners();
            }
        }

        public void loadState(Element element) {
            String value = element.getAttributeValue(persistName);
            if (value != null) setValue(fromString(value));
        }

        public void saveState(Element element) {
            element.setAttribute(persistName, value.toString());
        }

        public int selectedPegdownFlags() {
            return 0;
        }

        public boolean isChanged(T that) { return !value.equals(that); }

        abstract public T fromString(String value);
    }

    public class IntegerSetting extends Setting<Integer> {

        public IntegerSetting(Integer initialValue, String persistName) { super(initialValue, persistName); }

        @Override public Integer fromString(String value) { return Integer.parseInt(value); }

        public void setValue(JSpinner component) { setValue((Integer) component.getValue()); }

        public void reset(JSpinner component) { component.setValue(value); }

        public boolean isChanged(JSpinner component) { return !value.equals((Integer) component.getValue()); }

        public void setValue(JComboBox component) { setValue((Integer) component.getSelectedIndex()); }

        public void reset(JComboBox component) { component.setSelectedIndex(value); }

        public boolean isChanged(JComboBox component) { return !value.equals((Integer) component.getSelectedIndex()); }
    }

    public class BooleanSetting extends Setting<Boolean> {

        protected int pegdownFlags;

        public BooleanSetting(Boolean initialValue, String persistName, int pegdownFlags) {
            super(initialValue, persistName);
            this.pegdownFlags = pegdownFlags;
        }

        @Override public Boolean fromString(String value) { return Boolean.parseBoolean(value); }

        @Override public int selectedPegdownFlags() { return value ? pegdownFlags : 0; }

        public void setValue(JCheckBox component) { setValue(component.isSelected()); }

        public void reset(JCheckBox component) { component.setSelected(value); }

        public boolean isChanged(JCheckBox component) { return !value.equals((Boolean) component.isSelected()); }
    }

    public class StringSetting extends Setting<String> {

        public StringSetting(String initialValue, String persistName) { super(initialValue, persistName); }

        @Override public String fromString(String value) { return value; }

        public void setValue(JTextArea component) { setValue(component.getText()); }

        public void reset(JTextArea component) { component.setText(value); }

        public boolean isChanged(JTextArea component) { return !value.equals(component.getText()); }
    }
}
