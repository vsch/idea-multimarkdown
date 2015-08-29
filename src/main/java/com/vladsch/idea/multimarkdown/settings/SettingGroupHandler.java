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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SettingGroupHandler<M> {
    // this sets the actual value of the model from the settings
    void loadModelValue(@NotNull M model, Settings.Setting[] settings);

    // this puts the model's values in settings
    void saveModelValue(@NotNull M model, Settings.Setting[] settings);

    // this test to see if the saved values are valid
    boolean isSettingValid(@NotNull M model, Settings.Setting[] settings);

    // this returns individual values of the settings in string form
    @Nullable String getModelValue(@NotNull M model, int index);
}
