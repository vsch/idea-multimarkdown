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

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingHandlers<M> extends Settings {
    final protected ArrayList<GroupHandler<M>> handlers = new ArrayList<GroupHandler<M>>();
    final protected HashMap<String, HandlerSetting<M>> handlersSettings = new HashMap<String, HandlerSetting<M>>();

    public SettingHandlers(@Nullable SettingsNotifier notifier) {
        super(notifier);
    }

    public Element getState(@Nullable M model, String elementName, SettingsProvider settingsProvider) {
        if (model != null) {
            for (GroupHandler<M> handler : handlers) {
                handler.saveModelValue(model);
            }
        }
        return super.getState(elementName, settingsProvider);
    }

    public void loadState(@Nullable M model, @NotNull Element element) {
        super.loadState(element);
        loadState(model);
    }

    public void loadState(@Nullable M model) {
        if (model != null) {
            for (GroupHandler<M> handler : handlers) {
                handler.loadModelValue(model);
            }
        }
    }

    public @Nullable Object getComponentValue(@Nullable M model, @NotNull String name) {
        if (!handlersSettings.containsKey(name) || model == null) return null;

        HandlerSetting<M> handlerSetting = handlersSettings.get(name);
        return handlerSetting.getHandler().getModelValue(model, handlerSetting.getIndex());
    }

    public GroupHandler<M> newGroupHandler(SettingGroupHandler<M> groupHandler, Settings.Setting... settings) {
        GroupHandler<M> handler1 = new GroupHandlerImpl<M>(groupHandler, settings);
        registerHandler(handler1);
        return handler1;
    }

    public <T> GroupHandler<M> newHandler(SettingHandler<M, T> handler, Settings.Setting<T> setting) {
        GroupHandler<M> handler1 = new HandlerImpl<M, T>(handler, setting);
        registerHandler(handler1);
        return handler1;
    }

    protected void registerHandler(GroupHandler<M> handler) {
        handlers.add(handler);
        for (int i = 0; ; i++) {
            Settings.Setting setting = handler.getSetting(i);
            if (setting == null) break;

            handlersSettings.put(setting.persistName, new HandlerSetting<M>(handler, i));
        }
    }

    protected class HandlerSetting<M> {
        protected final GroupHandler<M> handler;
        protected final int index;

        HandlerSetting(GroupHandler<M> handler, int index) {
            this.handler = handler;
            this.index = index;
        }

        GroupHandler<M> getHandler() {
            return handler;
        }

        public int getIndex() {
            return index;
        }
    }

    public interface GroupHandler<M> {
        // this sets the actual value of the model from the settings
        public void loadModelValue(@Nullable M model);

        // this puts the model's values in settings
        public void saveModelValue(@Nullable M model);

        // this test to see if the saved values are valid
        public boolean isSettingValid(@Nullable M model);

        // this returns individual values of the settings in string form
        @Nullable String getModelValue(@NotNull M model, int index);

        // this is used to iterate settings of this group
        public @Nullable Settings.Setting getSetting(int index);
    }

    private class GroupHandlerImpl<M> implements GroupHandler<M> {
        protected final Settings.Setting[] settings;
        protected final SettingGroupHandler<M> groupHandler;

        GroupHandlerImpl(SettingGroupHandler<M> groupHandler, Settings.Setting... settings) {
            this.settings = settings;
            this.groupHandler = groupHandler;
        }

        public Setting[] getSettings() {
            return settings;
        }

        // this sets the actual value of the model from the settings
        @Override public void loadModelValue(@Nullable M model) {
            if (model != null) {
                if (groupHandler.isSettingValid(model, settings)) groupHandler.loadModelValue(model, settings);
                groupHandler.saveModelValue(model, settings);
            }
        }

        // this puts the model's values in settings
        @Override public void saveModelValue(@Nullable M model) {
            if (model != null) {
                groupHandler.saveModelValue(model, settings);
            }
        }

        // this test to see if the saved values are valid
        @Override public boolean isSettingValid(@Nullable M model) {
            return model == null || groupHandler.isSettingValid(model, settings);
        }

        // this returns individual values of the settings in string form
        @Override public @Nullable String getModelValue(@NotNull M model, int index) {
            return groupHandler.getModelValue(model, index);
        }

        // this is used to iterate settings of this group
        public @Nullable Settings.Setting getSetting(int index) {
            return index >= 0 && index < settings.length ? settings[index] : null;
        }
    }

    private class HandlerImpl<M, T> implements GroupHandler<M> {
        protected final Settings.Setting<T> setting;

        protected final SettingHandler<M, T> handler;

        HandlerImpl(SettingHandler<M, T> handler, Settings.Setting<T> setting) {
            this.setting = setting;
            this.handler = handler;
        }

        // this sets the actual value of the model from the setting
        @Override
        public void loadModelValue(@Nullable M model) {
            if (model != null) {
                T value = setting.getValue();
                if (isValid(model, value)) {
                    loadModelValue(model, value);
                }
                saveModelValue(model);
            }
        }

        // this puts the model's value in setting
        @Override
        public void saveModelValue(@Nullable M model) {
            if (model != null) {
                setting.setValue(getModelValue(model));
            }
        }

        // this test to see if the saved value is valid
        @Override
        public boolean isSettingValid(@Nullable M model) {
            return model == null || isValid(model, setting.getValue());
        }

        // this returns individual values of the settings in string form
        @Override public @Nullable String getModelValue(@NotNull M model, int index) {
            return index == 0 ? String.valueOf(handler.getModelValue(model)) : null;
        }

        // this is used to iterate settings of this group
        @Override public @Nullable Setting getSetting(int index) {
            return index == 0 ? setting : null;
        }

        public void loadModelValue(@NotNull M model, T value) {
            handler.setModelValue(model, value);
        }

        public boolean isValid(@NotNull M model, T value) {
            return handler.isSettingValid(model, value);
        }

        public T getModelValue(@NotNull M model) {
            return handler.getModelValue(model);
        }
    }
}

