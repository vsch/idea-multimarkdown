// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.md.nav.settings.api.ApplicationSettingsContainer;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.ActionListener;

public class MdWasShownSettingsForm extends ApplicationSettingsContainer {
    private JPanel myMainPanel;
    JBCheckBox myLicensedAvailable;
    JBCheckBox myJavaFxAvailable;
    JBCheckBox myJbCefAvailable;
    JBCheckBox myJekyllFrontMatter;
    JBCheckBox myUnicodeLineSeparator;
    JBCheckBox myGitHubSyntaxChange;
    JBCheckBox myAutoLinksExtension;
    JBCheckBox myNeedLegacyUpdateChannel;
    JBCheckBox myNeedStandardUpdateChannel;
    JBCheckBox myPlantUmlExtensionAvailable;
    private JPanel myBasicPanel;
    private JPanel myExtensionsPanel;
    private javax.swing.JLabel myShowSizePreferencesDialog;

    @Nullable private ActionListener myUpdateListener;
    private final SettingsComponents<MdWasShownSettings> components;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    public MdWasShownSettingsForm(MdApplicationSettings settings, RenderingProfileSynchronizer profileSynchronizer) {
        super(settings, profileSynchronizer);

        components = new SettingsComponents<MdWasShownSettings>() {
            @Override
            protected Settable<MdWasShownSettings>[] createComponents(@NotNull MdWasShownSettings i) {
                //noinspection unchecked
                return new Settable[] {
// @formatter:off
                    component(myLicensedAvailable, i::getLicensedAvailable, i::setLicensedAvailable),
                    component(myJavaFxAvailable, i::getJavaFxAvailable, i::setJavaFxAvailable),
                    component(myJbCefAvailable, i::getJbCefAvailable, i::setJbCefAvailable),
                    component(myJekyllFrontMatter, i::getJekyllFrontMatter, i::setJekyllFrontMatter),
                    component(myUnicodeLineSeparator, i::getUnicodeLineSeparator, i::setUnicodeLineSeparator),
                    component(myGitHubSyntaxChange, i::getGitHubSyntaxChange, i::setGitHubSyntaxChange),
                    component(myAutoLinksExtension, i::getAutoLinksExtension, i::setAutoLinksExtension),
                    component(myNeedLegacyUpdateChannel, i::getNeedLegacyUpdateChannel, i::setNeedLegacyUpdateChannel),
                    component(myNeedStandardUpdateChannel, i::getNeedStandardUpdateChannel, i::setNeedStandardUpdateChannel),
                    component(myPlantUmlExtensionAvailable, i::getPlantUmlExtensionAvailable, i::setPlantUmlExtensionAvailable),

// @formatter:on
                };
            }
        };

        onFormCreated();
    }

    private void createUIComponents() {
        myExtensionsPanel = getExtensionsPanel();
    }

    public void setBasicPanelVisible(boolean visible) {
        myBasicPanel.setVisible(visible);
    }

    @Override
    public void updateFormOnReshow(boolean isInitialShow) {

    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void updateOptionalSettings() {
        updateExtensionsOptionalSettings();
    }

    @Override
    public void reset(@NotNull final MdApplicationSettingsHolder settings) {
        components.reset(settings.getWasShownSettings());
        resetExtensions(settings);
        updateOptionalSettings();
        ApplicationManager.getApplication().invokeLater(() -> updateFormOnReshow(false), ModalityState.any());
        if (myUpdateListener != null) myUpdateListener.actionPerformed(null);
    }

    @Override
    public void apply(@NotNull final MdApplicationSettingsHolder settings) {
        components.apply(settings.getWasShownSettings());
        applyExtensions(settings);
    }

    @Override
    public boolean isModified(@NotNull final MdApplicationSettingsHolder settings) {
        return components.isModified(settings.getWasShownSettings()) || isModifiedExtensions(settings);
    }

    @Override
    protected void disposeResources() {
        super.disposeResources();
    }
}
