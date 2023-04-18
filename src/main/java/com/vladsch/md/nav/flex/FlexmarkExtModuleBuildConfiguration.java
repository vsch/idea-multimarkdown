// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "FlexmarkExt.ModuleBuildProperties")
public class FlexmarkExtModuleBuildConfiguration implements PersistentStateComponent<FlexmarkExtModuleBuildConfiguration.Options>, FlexmarkModuleOptions.Holder {
    @NotNull private FlexmarkModuleOptions myOptions = new FlexmarkModuleOptions();

    public FlexmarkExtModuleBuildConfiguration() {
        
    }

    @Nullable
    public static FlexmarkExtModuleBuildConfiguration getInstance(@NotNull Module module) {
        Project project = module.getProject();
        return ModuleType.is(module, FlexmarkExtModuleType.getInstance()) ? project.getService(FlexmarkExtModuleBuildConfiguration.class) : null;
    }

    @NotNull
    public Options getOptions() {
        return new Options(myOptions);
    }

    public void setOptions(@NotNull FlexmarkModuleOptions options) {
        myOptions = new Options(options);
    }

    @Override
    public void loadState(@NotNull Options state) {
        setOptions(state);
    }

    @Nullable
    @Override
    public Options getState() {
        return getOptions();
    }

    static class Options extends FlexmarkModuleOptions {
        public Options(FlexmarkModuleOptions other) {
            super(other);
        }

        public Options() {
        }
    }
}

