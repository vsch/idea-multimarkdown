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
 */
package com.vladsch.idea.multimarkdown.settings;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.ui.EditorTextField;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

public class Settings {
    private static final Logger logger = Logger.getLogger(Settings.class);

    final protected ArrayList<Setting> settings = new ArrayList<Setting>(50);
    @Nullable final protected SettingsNotifier notifier;

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public Settings(@Nullable SettingsNotifier notifier) {
        this.notifier = notifier;
    }

    public BooleanSetting BooleanSetting(Boolean initialValue, String persistName, int flags) {
        return new BooleanSetting(initialValue, persistName, flags, false, false);
    }

    public BooleanSetting BooleanSetting(Boolean initialValue, String persistName, int flags, boolean isLicensedFeature) {
        return new BooleanSetting(initialValue, persistName, flags, isLicensedFeature, false);
    }

    public BooleanSetting LocalBooleanSetting(Boolean initialValue, String persistName, int flags) {
        return new BooleanSetting(initialValue, persistName, flags, false, true);
    }

    public BooleanSetting LocalBooleanSetting(Boolean initialValue, String persistName, int flags, boolean isLicensedFeature) {
        return new BooleanSetting(initialValue, persistName, flags, isLicensedFeature, true);
    }

    public ElementSetting ElementSetting(Element initialValue, String persistName) {
        return new ElementSetting(initialValue, persistName, false, false);
    }

    public ElementSetting LocalElementSetting(Element initialValue, String persistName) {
        return new ElementSetting(initialValue, persistName, false, true);
    }

    public IntegerSetting IntegerSetting(Integer initialValue, String persistName) {
        return new IntegerSetting(initialValue, persistName);
    }

    public DoubleSetting DoubleSetting(Double initialValue, String persistName) {
        return new DoubleSetting(initialValue, persistName);
    }

    public StringSetting StringSetting(String initialValue, String persistName) {
        return new StringSetting(initialValue, persistName, false, false);
    }

    public StringSetting StringSetting(String initialValue, String persistName, boolean isLicensedFeature) {
        return new StringSetting(initialValue, persistName, isLicensedFeature, false);
    }

    public StringSetting LocalStringSetting(String initialValue, String persistName) {
        return new StringSetting(initialValue, persistName, false, true);
    }

    public StringSetting LocalStringSetting(String initialValue, String persistName, boolean isLicensedFeature) {
        return new StringSetting(initialValue, persistName, isLicensedFeature, true);
    }

    public FailedBuildSetting FailedBuildSetting(String initialValue, String persistName) {
        return new FailedBuildSetting(initialValue, persistName, false, true);
    }

    public FailedBuildSetting FailedBuildSetting(String initialValue, String persistName, boolean withJdk) {
        return new FailedBuildSetting(initialValue, persistName, withJdk, true);
    }

    //public Element getState(String name) {
    //    final Element element = new Element(name);
    //    for (Setting setting : settings) {
    //        setting.saveState(element);
    //    }
    //    return element;
    //}

    public void getState(Element element, Boolean isRoamingDisabled) {
        for (Setting setting : settings) {
            if (isRoamingDisabled == null || setting.isNonRoaming == isRoamingDisabled) {
                setting.saveState(element);
            }
        }
    }

    public Element getState(String name, ComponentProvider provider, Boolean isRoamingDisabled) {
        final Element element = new Element(name);
        for (Setting setting : settings) {
            if (isRoamingDisabled == null || setting.isNonRoaming == isRoamingDisabled) {
                String value = (String) provider.getComponent(setting.persistName);
                //noinspection unchecked
                setting.setValue(value == null ? setting.getDefaultValue() : setting.fromString(value));
                setting.saveState(element);
            }
        }
        return element;
    }

    public void loadState(@NotNull Element element, Boolean isRoamingDisabled) {
        if (notifier != null) notifier.startGroupNotifications();
        for (Setting setting : settings) {
            if (isRoamingDisabled == null || setting.isNonRoaming == isRoamingDisabled) {
                setting.loadState(element);
            }
        }
        if (notifier != null) notifier.endGroupNotifications();
    }

    public boolean isChanged(@NotNull Element element, @NotNull ComponentProvider componentProvider) {
        for (Settings.Setting setting : settings) {
            if (setting instanceof ElementSetting) {
                ((ElementSetting) setting).isChanged(element);
            } else {
                String storedValue = element.getAttributeValue(setting.persistName);
                String currentValue = (String) componentProvider.getComponent(setting.persistName);
                //noinspection ConstantConditions
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
        private T value;
        final protected T initialValue;
        final protected boolean isLicensedFeature;
        final protected boolean isNonRoaming;

        protected String persistName;

        public Setting(T initialValue, String persistName) {
            this(initialValue, persistName, false, false);
        }

        public Setting(T initialValue, String persistName, boolean isLicensedFeature) {
            this(initialValue, persistName, isLicensedFeature, false);
        }

        public Setting(T initialValue, String persistName, boolean isLicensedFeature, boolean isNonRoaming) {
            this.initialValue = initialValue;
            this.value = initialValue;
            this.persistName = persistName;
            this.isLicensedFeature = isLicensedFeature;
            this.isNonRoaming = isNonRoaming;
            settings.add(this);
        }

        public T getValue() { return (!isLicensedFeature) ? value : initialValue; }

        public void setValue(T value) {
            if (!isLicensedFeature) {
                if (!isEqual(value)) {
                    this.value = value;
                    if (notifier != null) notifier.notifyListeners();
                }
            }
        }

        public boolean isEqual(T value) {
            return this.value == value;
        }

        public void loadState(Element element) {
            if (!isLicensedFeature) {
                String value = element.getAttributeValue(persistName);
                if (value != null) setValue(fromString(value));
            }
        }

        public void saveState(Element element) {
            if (!isLicensedFeature) {
                //logger.info("saving state for " + persistName);
                element.setAttribute(persistName, value.toString());
            }
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

        @Override
        public Integer fromString(String value) { return Integer.parseInt(value); }

        @Override
        public Integer getDefaultValue() { return 0; }

        public void setValue(JSpinner component) { setValue((Integer) component.getValue()); }

        public void reset(JSpinner component) { component.setValue(getValue()); }

        public boolean isChanged(JSpinner component) { return !getValue().equals((Integer) component.getValue()); }

        public void setValue(JComboBox component) { setValue((Integer) component.getSelectedIndex()); }

        public void reset(JComboBox component) { component.setSelectedIndex(getValue()); }

        public boolean isChanged(JComboBox component) { return !getValue().equals((Integer) component.getSelectedIndex()); }

        public void setValue(JTabbedPane component) { setValue((Integer) component.getSelectedIndex()); }

        public void reset(JTabbedPane component) { component.setSelectedIndex(getValue()); }

        public boolean isChanged(JTabbedPane component) { return !getValue().equals((Integer) component.getSelectedIndex()); }

        public void setValue(JList component) { setValue((Integer) component.getSelectedIndex()); }

        public void reset(JList component) { component.setSelectedIndex(getValue()); }

        public boolean isChanged(JList component) { return !getValue().equals((Integer) component.getSelectedIndex()); }
    }

    public class DoubleSetting extends Setting<Double> {

        public DoubleSetting(Double initialValue, String persistName) { super(initialValue, persistName); }

        @Override
        public Double fromString(String text) { return Double.parseDouble(text); }

        @Override
        public Double getDefaultValue() { return 0.0; }

        public void setValue(JSpinner component) { setValue((Double) component.getValue()); }

        public void reset(JSpinner component) { component.setValue(getValue()); }

        public boolean isChanged(JSpinner component) { return !getValue().equals((Double) component.getValue()); }
    }

    public class BooleanSetting extends Setting<Boolean> {
        final protected int flags;

        public BooleanSetting(Boolean initialValue, String persistName, int flags, boolean isLicensedFeature, boolean isNonRoaming) {
            super(initialValue, persistName, isLicensedFeature, isNonRoaming);
            this.flags = flags;
        }

        @Override
        public Boolean fromString(String text) { return Boolean.parseBoolean(text); }

        @Override
        public Boolean getDefaultValue() { return false; }

        @Override
        public int getExtensionValue() { return getValue() ? flags : 0; }

        public void setValue(JCheckBox component) { setValue(component.isSelected()); }

        public void reset(JCheckBox component) { component.setSelected(getValue()); }

        public boolean isChanged(JCheckBox component) { return !getValue().equals((Boolean) component.isSelected()); }
    }

    public class StringSetting extends Setting<String> {
        public StringSetting(String initialValue, String persistName, boolean isLicensedFeature, boolean isNonRoaming) { super(initialValue, persistName, isLicensedFeature, isNonRoaming); }

        @Override
        public String fromString(String text) {
            return text;
        }

        @Override
        public String getDefaultValue() {
            return "";
        }

        @Override
        public boolean isEqual(String text) {
            return getValue().equals(text);
        }

        public void setValue(JTextArea component) { setValue(component.getText()); }

        public void setValue(EditorTextField component) {
            setValue(component.getText());
        }

        public void reset(JTextArea component) { component.setText(getValue()); }

        public void reset(EditorTextField component) {
            component.setText(getValue());
        }

        public boolean isChanged(JTextArea component) { return !getValue().equals(component.getText()); }

        public boolean isChanged(EditorTextField component) { return !getValue().equals(component.getText()); }
    }

    public class FailedBuildSetting extends StringSetting {
        protected final boolean withJdk;

        public FailedBuildSetting(String initialValue, String persistName, boolean withJdk, boolean isNonRoaming) {
            super(initialValue, persistName, false, true);
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
            return getValue() != null && getValue().equals(ideaBuild);
        }

        public <T> T runBuild(FailedBuildRunnable<T> runnable) {
            String ideaBuild = currentBuild();

            if (getValue() == null || !getValue().equals(ideaBuild)) {
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
        public ElementSetting(Element initialValue, String persistName, boolean isLicensedFeature, boolean isNonRoaming) {
            super(initialValue != null ? initialValue.clone() : null, persistName, isLicensedFeature, isNonRoaming);
        }

        @Override
        public void setValue(Element element) {
            super.setValue(element != null ? element.clone() : null);
        }
        @Override
        public Element fromString(String text) { return null; }

        @Override
        public Element getDefaultValue() { return null; }

        @Override
        public void loadState(@NotNull Element element) {
            Element child = element.getChild(persistName);
            setValue(child);
        }

        @Override
        public void saveState(@NotNull Element element) {
            if (getValue() != null) {
                element.addContent(getValue().clone());
            }
        }

        public void setValue(@NotNull ComponentState component) {
            setValue(component.getState(persistName));
        }

        public void reset(@NotNull ComponentState component) {
            component.loadState(getValue());
        }

        public boolean isChanged(@NotNull ComponentState component) {
            return getValue() == null || component.isChanged(getValue());
        }
    }
}
