// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "FlexmarkModuleOptions",
        storages = @Storage(value = FlexmarkModuleLocalSettings.FLEXMARK_PLUGIN_LOCAL_XML, roamingType = RoamingType.DISABLED))
public class FlexmarkModuleLocalSettings implements PersistentStateComponent<FlexmarkModuleOptions>, FlexmarkModuleOptions.Holder {

    public static final String FLEXMARK_PLUGIN_LOCAL_XML = "flexmark_plugin.local.xml";

    public static FlexmarkModuleLocalSettings getInstance() {
        return ServiceManager.getService(FlexmarkModuleLocalSettings.class);
    }

    @NotNull final private FlexmarkModuleOptions myOptions = new FlexmarkModuleOptions();

    @NotNull
    public FlexmarkModuleOptions getOptions() {
        return new FlexmarkModuleOptions(myOptions);
    }

    public void setOptions(@NotNull FlexmarkModuleOptions options) {
        myOptions.copyFrom(options);
    }

    @Nullable
    @Override
    public FlexmarkModuleOptions getState() {
        return getOptions();
    }

    @Override
    public void loadState(@NotNull FlexmarkModuleOptions state) {
        setOptions(state);
    }
}
