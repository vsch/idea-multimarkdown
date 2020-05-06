// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FlexmarkExtModuleWizardStep extends ModuleWizardStep {
    final protected @Nullable WizardContext myContext;
    final protected @Nullable Disposable myParentDisposable;
    final protected FlexmarkModuleOptions.Holder myOptionsHolder;
    private final FlexmarkExtModuleSettingsForm myForm = null;

    public FlexmarkExtModuleWizardStep(@Nullable WizardContext context, @Nullable Disposable parentDisposable, @NotNull FlexmarkModuleOptions.Holder optionsHolder) {
        myContext = context;
        myParentDisposable = parentDisposable;
        myOptionsHolder = optionsHolder;
    }

    @Override
    public boolean isStepVisible() {
        // FLEXMARK_PLUGIN: remove when own plugin
        return MdApplicationSettings.getInstance().getDebugSettings().getExtension(FlexmarkDebugSettings.KEY).getEnableFlexmarkFeatures(); //FlexmarkExtModuleBuilder.getFlexmarkArchetypeModuleExtension();
    }
}
