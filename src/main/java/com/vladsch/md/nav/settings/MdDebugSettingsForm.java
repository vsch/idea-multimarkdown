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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;

public class MdDebugSettingsForm extends ApplicationSettingsContainer {
    private JPanel myMainPanel;
    JBCheckBox myDebugFormatText;
    JBCheckBox myCombinationColors;
    JBCheckBox myGenerateParserExceptions;
    JSpinner myPreferencesDialogWidth;
    JSpinner myPreferencesDialogHeight;
    JSpinner myPreferencesDialogMenuSplit;
    JLabel myPreferencesDialogWidthLabel;
    JLabel myPreferencesDialogHeightLabel;
    JBCheckBox myRemoteInvalidUntilFetched;
    JBCheckBox myOutputRemoteFetchExceptions;
    JBCheckBox myReloadEditorsOnFileTypeChange;
    JBCheckBox myReinitializeEditorsOnSettingsChange;
    JBCheckBox myCacheSvgForSwing;
    JBCheckBox myTaskItemImages;
    JBCheckBox myShowSizePreferencesDialog;
    JBCheckBox myShowTextHexDialog;
    JBCheckBox myUseFileLinkCache;
    private JPanel myExtensionsPanel;

    @Nullable private ActionListener myUpdateListener;
    private final SettingsComponents<MdDebugSettings> components;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    public MdDebugSettingsForm(MdApplicationSettings settings, RenderingProfileSynchronizer profileSynchronizer) {
        super(settings, profileSynchronizer);

        components = new SettingsComponents<MdDebugSettings>() {
            @Override
            protected Settable<MdDebugSettings>[] createComponents(@NotNull MdDebugSettings i) {
                //noinspection unchecked
                return new Settable[] {
                        notrace("CacheSvgForSwing", component(myCacheSvgForSwing, i::getCacheSvgForSwing, i::setCacheSvgForSwing)),
                        notrace("CombinationColors", component(myCombinationColors, i::getDebugCombinationColors, i::setDebugCombinationColors)),
                        notrace("DebugFormatText", component(myDebugFormatText, i::getDebugFormatText, i::setDebugFormatText)),
                        notrace("GenerateParserExceptions", component(myGenerateParserExceptions, i::getGenerateParserExceptions, i::setGenerateParserExceptions)),
                        notrace("OutputRemoteFetchExceptions", component(myOutputRemoteFetchExceptions, i::getOutputRemoteFetchExceptions, i::setOutputRemoteFetchExceptions)),
                        notrace("PreferencesDialogHeight", component(myPreferencesDialogHeight, i::getPreferencesDialogHeight, i::setPreferencesDialogHeight)),
                        notrace("PreferencesDialogMenuSplit", component(myPreferencesDialogMenuSplit, i::getPreferencesDialogMenuSplit, i::setPreferencesDialogMenuSplit)),
                        notrace("PreferencesDialogWidth", component(myPreferencesDialogWidth, i::getPreferencesDialogWidth, i::setPreferencesDialogWidth)),
                        notrace("ReinitializeEditorsOnSettingsChange", component(myReinitializeEditorsOnSettingsChange, i::getReinitializeEditorsOnSettingsChange, i::setReinitializeEditorsOnSettingsChange)),
                        notrace("ReloadEditorsOnFileTypeChange", component(myReloadEditorsOnFileTypeChange, i::getReloadEditorsOnFileTypeChange, i::setReloadEditorsOnFileTypeChange)),
                        notrace("RemoteInvalidUntilFetched", component(myRemoteInvalidUntilFetched, i::getRemoteInvalidUntilFetched, i::setRemoteInvalidUntilFetched)),
                        notrace("TaskItemImages", component(myTaskItemImages, i::getTaskItemImages, i::setTaskItemImages)),
                        notrace("ShowSizePreferencesDialog", component(myShowSizePreferencesDialog, i::getShowSizePreferencesDialog, i::setShowSizePreferencesDialog)),
                        notrace("ShowTextHexDialog", component(myShowTextHexDialog, i::getShowTextHexDialog, i::setShowTextHexDialog)),
                        notrace("UseFileLinkCache", component(myUseFileLinkCache, i::getUseFileLinkCache, i::setUseFileLinkCache)),
                };
            }
        };

        myShowSizePreferencesDialog.addActionListener((event) -> updateOptionalSettings());

        onFormCreated();

        updateOptionalSettings();
    }

    private void createUIComponents() {
        myExtensionsPanel = getExtensionsPanel();

        myUpdateListener = e -> updateFormOnReshow(false);

        final SpinnerNumberModel widthSizeModel = new SpinnerNumberModel(1000, 500, 2000, 100);
        myPreferencesDialogWidth = new JSpinner(widthSizeModel);
        myPreferencesDialogWidth.setEditor(new JSpinner.NumberEditor(myPreferencesDialogWidth, "0"));
        final SpinnerNumberModel heightSizeModel = new SpinnerNumberModel(1000, 500, 2000, 100);
        myPreferencesDialogHeight = new JSpinner(heightSizeModel);
        myPreferencesDialogHeight.setEditor(new JSpinner.NumberEditor(myPreferencesDialogHeight, "0"));

        final SpinnerNumberModel splitModel = new SpinnerNumberModel(0.23, 0.1, 100, 0.001);
        myPreferencesDialogMenuSplit = new JSpinner(splitModel);
        myPreferencesDialogMenuSplit.setEditor(new JSpinner.NumberEditor(myPreferencesDialogMenuSplit, "##0.000"));
    }

    public void updateFormOnReshow(boolean isInitialShow) {

    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void updateOptionalSettings() {
        boolean preferencesEnabled = myShowSizePreferencesDialog.isEnabled() && myShowSizePreferencesDialog.isSelected();
        myPreferencesDialogWidthLabel.setEnabled(preferencesEnabled);
        myPreferencesDialogHeightLabel.setEnabled(preferencesEnabled);
        myPreferencesDialogWidth.setEnabled(preferencesEnabled);
        myPreferencesDialogHeight.setEnabled(preferencesEnabled);
        updateExtensionsOptionalSettings();
    }

    @Override
    public void reset(@NotNull final MdApplicationSettingsHolder settings) {
        MdDebugSettings debugSettings = settings.getDebugSettings();
        components.reset(debugSettings);
        resetExtensions(settings);

        updateOptionalSettings();
        ApplicationManager.getApplication().invokeLater(() -> updateFormOnReshow(false), ModalityState.any());
        if (myUpdateListener != null) myUpdateListener.actionPerformed(null);
    }

    @Override
    public void apply(@NotNull final MdApplicationSettingsHolder settings) {
        MdDebugSettings debugSettings = settings.getDebugSettings();
        components.apply(debugSettings);
        applyExtensions(settings);
    }

    @Override
    public boolean isModified(@NotNull final MdApplicationSettingsHolder settings) {
        MdDebugSettings debugSettings = settings.getDebugSettings();
        return components.isModified(debugSettings) || isModifiedExtensions(settings);
    }
}
