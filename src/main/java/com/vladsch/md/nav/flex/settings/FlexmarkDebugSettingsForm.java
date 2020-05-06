// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.settings;

import com.intellij.ui.components.JBCheckBox;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdApplicationSettingsHolder;
import com.vladsch.md.nav.settings.RenderingProfileSynchronizer;
import com.vladsch.md.nav.settings.api.ApplicationSettingsContainer;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.ActionListener;

public class FlexmarkDebugSettingsForm extends ApplicationSettingsContainer {
    private JPanel myMainPanel;
    JBCheckBox myEnableFlexmarkFeatures;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    private final SettingsComponents<FlexmarkDebugSettings> components;

    public FlexmarkDebugSettingsForm(MdApplicationSettings settings, RenderingProfileSynchronizer profileSynchronizer) {
        super(settings, profileSynchronizer);

        components = new SettingsComponents<FlexmarkDebugSettings>() {
            @Override
            protected Settable<FlexmarkDebugSettings>[] createComponents(@NotNull FlexmarkDebugSettings i) {
                //noinspection unchecked
                return new Settable[] {
                        // Enhanced
                        notrace("EnableFlexmarkFeatures", component(myEnableFlexmarkFeatures, i::getEnableFlexmarkFeatures, i::setEnableFlexmarkFeatures)),
                };
            }
        };

        ActionListener actionListener = event -> updateOptionalSettings();

        myEnableFlexmarkFeatures.addActionListener(actionListener);

        onFormCreated();

        updateOptionalSettings();
    }

    private void createUIComponents() {

    }

    public void updateFormOnReshow(boolean isInitialShow) {
        //myFlexmarkSpecExampleRenderHtml.setEnabled(!FlexmarkSpecExampleRenderingType.ADAPTER.get(myFlexmarkSpecExampleRendering).isDefault());
    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void updateOptionalSettings() {

    }

    @Override
    public void reset(@NotNull MdApplicationSettingsHolder settings) {
        FlexmarkDebugSettings debugSettings = settings.getDebugSettings().getExtension(FlexmarkDebugSettings.KEY);
        components.reset(debugSettings);
        updateOptionalSettings();
    }

    public boolean isModified(@NotNull MdApplicationSettingsHolder settings) {
        FlexmarkDebugSettings debugSettings = settings.getDebugSettings().getExtension(FlexmarkDebugSettings.KEY);
        return components.isModified(debugSettings);
    }

    public void apply(@NotNull MdApplicationSettingsHolder settings) {
        FlexmarkDebugSettings debugSettings = settings.getDebugSettings().getExtension(FlexmarkDebugSettings.KEY);
        components.apply(debugSettings);
    }
}
