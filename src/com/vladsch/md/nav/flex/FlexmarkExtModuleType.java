// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex;

import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import icons.FlexmarkIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class FlexmarkExtModuleType extends JavaModuleType {
    private static final String ID = "FLEXMARK_JAVA_EXTENSION";

    public FlexmarkExtModuleType() {
        super(ID);
    }

    public static FlexmarkExtModuleType getInstance() {
        return (FlexmarkExtModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public FlexmarkExtModuleBuilder createModuleBuilder() {
        return new FlexmarkExtModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        //noinspection DialogTitleCapitalization
        return PluginBundle.message("flexmark-extension-module-type.name");
    }

    @NotNull
    @Override
    public String getDescription() {
        return PluginBundle.message("flexmark-extension-module-type.description");
    }

    @NotNull
    @Override
    public Icon getNodeIcon(boolean b) {
        return FlexmarkIcons.EXTENSION;
    }
}
