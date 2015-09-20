/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.settings;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.ui.EditorTextField;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

public class Settings {

    final protected ArrayList<Setting> settings = new ArrayList<Setting>(50);
    @Nullable final protected SettingsNotifier notifier;

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public Settings(@Nullable SettingsNotifier notifier) {
        this.notifier = notifier;
    }

    public BooleanSetting BooleanSetting(Boolean initialValue, String persistName, int flags) {
        return new BooleanSetting(initialValue, persistName, flags);
    }

    public ElementSetting ElementSetting(Element initialValue, String persistName) {
        return new ElementSetting(initialValue, persistName);
    }

    public IntegerSetting IntegerSetting(Integer initialValue, String persistName) {
        return new IntegerSetting(initialValue, persistName);
    }

    public DoubleSetting DoubleSetting(Double initialValue, String persistName) {
        return new DoubleSetting(initialValue, persistName);
    }

    public StringSetting StringSetting(String initialValue, String persistName) {
        return new StringSetting(initialValue, persistName);
    }

    public FailedBuildSetting FailedBuildSetting(String initialValue, String persistName) {
        return new FailedBuildSetting(initialValue, persistName, false);
    }

    public FailedBuildSetting FailedBuildSetting(String initialValue, String persistName, boolean withJdk) {
        return new FailedBuildSetting(initialValue, persistName, withJdk);
    }

    public Element getState(String name) {
        final Element element = new Element(name);
        for (Setting setting : settings) {
            setting.saveState(element);
        }
        return element;
    }

    public Element getState(String name, SettingsProvider provider) {
        final Element element = new Element(name);
        for (Setting setting : settings) {
            String value = (String) provider.getComponent(setting.persistName);
            if (value == null) {
                value = (String) provider.getComponent(setting.persistName);
            }
            setting.setValue(value == null ? setting.getDefaultValue() : setting.fromString(value));
            setting.saveState(element);
        }
        return element;
    }

    public void loadState(@NotNull Element element) {
        if (notifier != null) notifier.startGroupNotifications();
        for (Setting setting : settings) {
            setting.loadState(element);
        }
        if (notifier != null) notifier.endGroupNotifications();
    }

    public boolean isChanged(@NotNull Element element, @NotNull SettingsProvider settingsProvider) {
        for (Settings.Setting setting : settings) {
            if (setting instanceof ElementSetting) {
                ((ElementSetting) setting).isChanged(element);
            } else {
                String storedValue = element.getAttributeValue(setting.persistName);
                String currentValue = (String) settingsProvider.getComponent(setting.persistName);
                if ((storedValue == null) != (currentValue == null)
                        || (storedValue != null && currentValue != null
                        && !setting.fromString(currentValue).equals(setting.fromString(storedValue)))) return true;
            }
        }
        return false;
    }

    public int getExtensionsValue() {
        int extensionFlags = 0;
        for (Setting setting : settings) {
            extensionFlags |= setting.getExtensionValue();
        }
        return extensionFlags;
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
                if (notifier != null) notifier.notifyListeners();
            }
        }

        public void loadState(Element element) {
            String value = element.getAttributeValue(persistName);
            if (value != null) setValue(fromString(value));
        }

        public void saveState(Element element) {
            element.setAttribute(persistName, value.toString());
        }

        public int getExtensionValue() {
            return 0;
        }

        public boolean isChanged(T that) { return !value.equals(that); }

        abstract public T fromString(String value);

        abstract public T getDefaultValue();
    }

    public class IntegerSetting extends Setting<Integer> {

        public IntegerSetting(Integer initialValue, String persistName) { super(initialValue, persistName); }

        @Override public Integer fromString(String value) { return Integer.parseInt(value); }

        @Override public Integer getDefaultValue() { return 0; }

        public void setValue(JSpinner component) { setValue((Integer) component.getValue()); }

        public void reset(JSpinner component) { component.setValue(value); }

        public boolean isChanged(JSpinner component) { return !value.equals((Integer) component.getValue()); }

        public void setValue(JComboBox component) { setValue((Integer) component.getSelectedIndex()); }

        public void reset(JComboBox component) { component.setSelectedIndex(value); }

        public boolean isChanged(JComboBox component) { return !value.equals((Integer) component.getSelectedIndex()); }

        public void setValue(JTabbedPane component) { setValue((Integer) component.getSelectedIndex()); }

        public void reset(JTabbedPane component) { component.setSelectedIndex(value); }

        public boolean isChanged(JTabbedPane component) { return !value.equals((Integer) component.getSelectedIndex()); }

        public void setValue(JList component) { setValue((Integer) component.getSelectedIndex()); }

        public void reset(JList component) { component.setSelectedIndex(value); }

        public boolean isChanged(JList component) { return !value.equals((Integer) component.getSelectedIndex()); }
    }

    public class DoubleSetting extends Setting<Double> {

        public DoubleSetting(Double initialValue, String persistName) { super(initialValue, persistName); }

        @Override public Double fromString(String value) { return Double.parseDouble(value); }

        @Override public Double getDefaultValue() { return 0.0; }

        public void setValue(JSpinner component) { setValue((Double) component.getValue()); }

        public void reset(JSpinner component) { component.setValue(value); }

        public boolean isChanged(JSpinner component) { return !value.equals((Double) component.getValue()); }
    }

    public class BooleanSetting extends Setting<Boolean> {

        protected int flags;

        public BooleanSetting(Boolean initialValue, String persistName, int flags) {
            super(initialValue, persistName);
            this.flags = flags;
        }

        @Override public Boolean fromString(String value) { return Boolean.parseBoolean(value); }

        @Override public Boolean getDefaultValue() { return false; }

        @Override public int getExtensionValue() { return value ? flags : 0; }

        public void setValue(JCheckBox component) { setValue(component.isSelected()); }

        public void reset(JCheckBox component) { component.setSelected(value); }

        public boolean isChanged(JCheckBox component) { return !value.equals((Boolean) component.isSelected()); }
    }

    public class StringSetting extends Setting<String> {

        public StringSetting(String initialValue, String persistName) { super(initialValue, persistName); }

        @Override public String fromString(String value) {
            return value;
        }

        @Override public String getDefaultValue() {
            return "";
        }

        public void setValue(JTextArea component) { setValue(component.getText()); }

        public void setValue(EditorTextField component) {
            setValue(component.getText());
        }

        public void reset(JTextArea component) { component.setText(value); }

        public void reset(EditorTextField component) {
            component.setText(value);
        }

        public boolean isChanged(JTextArea component) { return !value.equals(component.getText()); }

        public boolean isChanged(EditorTextField component) { return !value.equals(component.getText()); }
    }

    public class FailedBuildSetting extends StringSetting {
        protected final boolean withJdk;

        public FailedBuildSetting(String initialValue, String persistName, boolean withJdk) {
            super(initialValue, persistName);
            this.withJdk = withJdk;
        }

        protected String currentBuild() {
            String ideaBuild = ApplicationInfo.getInstance().getBuild().asString();

            if (withJdk) {
                ideaBuild += ", " + System.getProperty("java.home");
            }

            return ideaBuild;
        }

        public boolean isFailedBuild() {
            String ideaBuild = currentBuild();
            return value != null && value.equals(ideaBuild);
        }

        public <T> T runBuild(FailedBuildRunnable<T> runnable) {
            String ideaBuild = currentBuild();

            if (value == null || !value.equals(ideaBuild)) {
                try {
                    return runnable.runCanFail();
                } catch (Throwable e) {
                    setValue(ideaBuild);
                }
            }

            return runnable.run();
        }
    }

    public class ElementSetting extends Setting<Element> {
        public ElementSetting(Element initialValue, String persistName) {
            super(initialValue != null ? initialValue.clone() : null, persistName);
        }

        @Override
        public void setValue(Element value) {
            super.setValue(value != null ? value.clone() : null);
        }

        @Override public Element fromString(String value) { return null; }

        @Override public Element getDefaultValue() { return null; }

        @Override
        public void loadState(@NotNull Element element) {
            Element child = element.getChild(persistName);
            setValue(child);
        }

        @Override
        public void saveState(@NotNull Element element) {
            if (value != null) {
                element.addContent(value.clone());
            }
        }

        public void setValue(@NotNull ComponentState component) {
            setValue(component.getState(persistName));
        }

        public void reset(@NotNull ComponentState component) {
            component.loadState(value);
        }

        public boolean isChanged(@NotNull ComponentState component) {
            return value == null || component.isChanged(value);
        }
    }
}
