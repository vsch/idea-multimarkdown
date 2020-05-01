// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "FlexmarkExt.ModuleBuildProperties")
public class FlexmarkExtModuleBuildConfiguration implements PersistentStateComponent<FlexmarkExtModuleBuildConfiguration.Options>, FlexmarkModuleOptions.Holder {
    private final Module myModule;
    //private final ConfigFileContainer myPluginXmlContainer;
    //private VirtualFilePointer myManifestFilePointer;
    //private boolean myUseUserManifest = false;
    //@NonNls private static final String META_INF = "META-INF";
    //@NonNls private static final String PLUGIN_XML = "plugin.xml";

    @NotNull private FlexmarkModuleOptions myOptions = new FlexmarkModuleOptions();

    public FlexmarkExtModuleBuildConfiguration(@NotNull Module module) {
        myModule = module;
        //myPluginXmlContainer = ConfigFileFactory.getInstance().createSingleFileContainer(myModule.getProject(), PluginDescriptorConstants.META_DATA);
        //Disposer.register(module, myPluginXmlContainer);
    }

    @Nullable
    public static FlexmarkExtModuleBuildConfiguration getInstance(@NotNull Module module) {
        return ModuleType.is(module, FlexmarkExtModuleType.getInstance()) ? ModuleServiceManager.getService(module, FlexmarkExtModuleBuildConfiguration.class) : null;
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

