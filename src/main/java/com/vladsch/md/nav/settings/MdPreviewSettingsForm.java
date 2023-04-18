// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.containers.ContainerUtil;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider;
import com.vladsch.md.nav.editor.split.SplitFileEditor;
import com.vladsch.md.nav.editor.swing.SwingHtmlPanelProvider;
import com.vladsch.md.nav.editor.util.HtmlPanelProvider;
import com.vladsch.md.nav.settings.api.MdSettingsComponent;
import com.vladsch.md.nav.settings.api.SettingsFormImpl;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import static com.vladsch.md.nav.editor.split.SplitFileEditor.SplitEditorLayout;
import static com.vladsch.md.nav.editor.split.SplitFileEditor.SplitEditorPreviewType;

public class MdPreviewSettingsForm extends SettingsFormImpl {
    JPanel myMainPanel;
    ComboBox<HtmlPanelProvider.Info> myPreviewProvider;
    private ComboBox<SplitFileEditor.SplitEditorLayout> myDefaultSplitLayout;
    private ComboBox<SplitEditorPreviewType> myDefaultSplitPreview;
    JBCheckBox myUseGrayscaleRendering;
    JSpinner myZoomFactor;
    JSpinner myMaxImageWidth;
    private JLabel myUseGrayscaleRenderingLabel;
    private JLabel myPageZoomLabel;
    private JLabel myMaxImgWidthLabel;
    private JBLabel myDefaultSplitLayoutLabel;
    private JBLabel myDefaultSplitPreviewLabel;
    JBCheckBox mySynchronizePreviewPosition;
    JComboBox<String> myHighlightPreviewType;
    JSpinner myHighlightFadeOut;
    private JLabel myHighlightFadeOutLabel;
    private JLabel myHighlightPreviewTypeLabel;
    JBCheckBox myHighlightOnTyping;
    private JLabel myHighlightOnTypingLabel;
    JBCheckBox mySynchronizeSourcePositionOnClick;
    JBCheckBox myVerticallyAlignSourceAndPreviewSyncPosition;
    JBCheckBox myShowSearchHighlightsInPreview;
    JBCheckBox myShowSelectionInPreview;
    JBCheckBox myLastLayoutSetsDefault;
    private JLabel mySynchronizePreviewPositionLabel;
    private JLabel mySynchronizeSourcePositionOnClickLabel;
    private JLabel myVerticallyAlignSourceAndPreviewSyncPositionLabel;
    private JLabel myShowSearchHighlightsInPreviewLabel;
    private JLabel myShowSelectionInPreviewLabel;
    private JPanel myExtensionsPanel;
    private CollectionComboBoxModel<SplitFileEditor.SplitEditorLayout> mySplitLayoutModel;
    private CollectionComboBoxModel<SplitEditorPreviewType> mySplitPreviewModel;

    private CollectionComboBoxModel<HtmlPanelProvider.Info> myPreviewPanelModel;

    private boolean settingsLoaded = false;
    HtmlPanelProvider.Info myLastPanelProvider = getPanelProvider().getINFO();

    private final SettingsComponents<MdPreviewSettings> components;
    boolean myInUpdate = false;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    @Override
    protected void disposeResources() {

    }

    public MdPreviewSettingsForm(RenderingProfileSynchronizer profileSynchronizer) {
        super(profileSynchronizer);

        components = new SettingsComponents<MdPreviewSettings>() {
            @Override
            protected Settable<MdPreviewSettings>[] createComponents(@NotNull MdPreviewSettings i) {
                //noinspection unchecked
                return new Settable[] {
                        component(HighlightPreviewType.ADAPTER, myHighlightPreviewType, i::getHighlightPreviewType, i::setHighlightPreviewType),
                        component(myHighlightFadeOut, i::getHighlightFadeOut, i::setHighlightFadeOut),
                        component(myHighlightOnTyping, i::getHighlightOnTyping, i::setHighlightOnTyping),
                        component(myUseGrayscaleRendering, i::getUseGrayscaleRendering, i::setUseGrayscaleRendering),
                        component(myZoomFactor, i::getZoomFactor, i::setZoomFactor),
                        component(myMaxImageWidth, i::getMaxImageWidth, i::setMaxImageWidth),
                        component(mySynchronizePreviewPosition, i::getSynchronizePreviewPosition, i::setSynchronizePreviewPosition),
                        component(mySynchronizeSourcePositionOnClick, i::getSynchronizeSourcePositionOnClick, i::setSynchronizeSourcePositionOnClick),
                        component(myVerticallyAlignSourceAndPreviewSyncPosition, i::getVerticallyAlignSourceAndPreviewSyncPosition, i::setVerticallyAlignSourceAndPreviewSyncPosition),
                        component(myShowSearchHighlightsInPreview, i::getShowSearchHighlightsInPreview, i::setShowSearchHighlightsInPreview),
                        component(myShowSelectionInPreview, i::getShowSelectionInPreview, i::setShowSelectionInPreview),
                        component(myLastLayoutSetsDefault, i::getLastLayoutSetsDefault, i::setLastLayoutSetsDefault),
                };
            }
        };

        myPageZoomLabel.setText(MdBundle.message("settings.zoom.1.label", MdPreviewSettings.MIN_ZOOM_FACTOR, MdPreviewSettings.MAX_ZOOM_FACTOR));
        myHighlightFadeOutLabel.setText(MdBundle.message("settings.preview.form.sync-highlight-fadeout.label", "(" + MdPreviewSettings.MIN_HIGHLIGHT_FADEOUT + "-" + MdPreviewSettings.MAX_HIGHLIGHT_FADEOUT + ")"));

        ActionListener actionListener = e -> {
            if (!myInUpdate) updateOptionalSettings();
        };

        myHighlightPreviewType.addActionListener(actionListener);
        mySynchronizePreviewPosition.addActionListener(actionListener);

        mySynchronizePreviewPositionLabel.setText(MdBundle.message("settings.preview.form.sync-position.label"));
        mySynchronizeSourcePositionOnClickLabel.setText(MdBundle.message("settings.preview.form.sync-position-on-click.label"));
        myShowSelectionInPreviewLabel.setText(MdBundle.message("settings.preview.form.show-selection-in-preview.label"));
        myShowSearchHighlightsInPreviewLabel.setText(MdBundle.message("settings.preview.form.show-search-in-preview.label"));

        onFormCreated();
    }

    private void createUIComponents() {
        final List<HtmlPanelProvider.Info> providerInfos = getPreviewPanelProviders();
        myPreviewPanelModel = new CollectionComboBoxModel<>(providerInfos, myLastPanelProvider);
        myPreviewProvider = new ComboBox<>(myPreviewPanelModel);

        final List<SplitFileEditor.SplitEditorLayout> layoutList = getSplitLayoutList();
        mySplitLayoutModel = new CollectionComboBoxModel<>(layoutList, layoutList.get(0));
        myDefaultSplitLayout = new ComboBox<>(mySplitLayoutModel);

        final List<SplitEditorPreviewType> previewList = getSplitPreviewList();
        mySplitPreviewModel = new CollectionComboBoxModel<>(previewList, previewList.get(0));
        myDefaultSplitPreview = new ComboBox<>(mySplitPreviewModel);

        myHighlightPreviewType = HighlightPreviewType.ADAPTER.createComboBox();
        final SpinnerNumberModel fadeOutModel = new SpinnerNumberModel(MdPreviewSettings.DEFAULT_HIGHLIGHT_FADEOUT, MdPreviewSettings.MIN_HIGHLIGHT_FADEOUT, MdPreviewSettings.MAX_HIGHLIGHT_FADEOUT, 1);
        myHighlightFadeOut = new JSpinner(fadeOutModel);

        final SpinnerNumberModel model = new SpinnerNumberModel(MdPreviewSettings.DEFAULT_ZOOM_FACTOR, MdPreviewSettings.MIN_ZOOM_FACTOR, MdPreviewSettings.MAX_ZOOM_FACTOR, 0.01);
        myZoomFactor = new JSpinner(model);
        JSpinner.NumberEditor decimalFormat = new JSpinner.NumberEditor(myZoomFactor, "0.00");
        myZoomFactor.setEditor(decimalFormat);

        myPreviewProvider.addItemListener(e -> {
            final Object item = e.getItem();
            if (e.getStateChange() != ItemEvent.SELECTED || !(item instanceof HtmlPanelProvider.Info)) {
                return;
            }

            final HtmlPanelProvider provider = HtmlPanelProvider.Companion.getFromInfoOrDefault((HtmlPanelProvider.Info) item);
            final HtmlPanelProvider.AvailabilityInfo availability = provider.isAvailable();

            if (!availability.checkAvailability(myMainPanel)) {
                myPreviewProvider.setSelectedItem(myLastPanelProvider);
            } else {
                myLastPanelProvider = (HtmlPanelProvider.Info) item;
                if (!myInUpdate) updateOptionalSettings();
            }
        });

        myExtensionsPanel = getExtensionsPanel();
    }

    private List<SplitEditorPreviewType> getSplitPreviewList() {
        ArrayList<SplitEditorPreviewType> list = new ArrayList<>();

        // add only the first three
        list.add(SplitEditorPreviewType.PREVIEW);
        list.add(SplitEditorPreviewType.MODIFIED_HTML);
        list.add(SplitEditorPreviewType.UNMODIFIED_HTML);

        return list;
    }

    private List<SplitFileEditor.SplitEditorLayout> getSplitLayoutList() {
        ArrayList<SplitFileEditor.SplitEditorLayout> list = new ArrayList<>();

        // add only the first three
        list.add(SplitEditorLayout.SPLIT);
        list.add(SplitEditorLayout.FIRST);
        list.add(SplitEditorLayout.SECOND);

        return list;
    }

    @NotNull
    private List<HtmlPanelProvider.Info> getPreviewPanelProviders() {
        return ContainerUtil.mapNotNull(HtmlPanelProvider.Companion.getEP_NAME().getExtensions(),
                provider -> {
                    if (provider.isAvailable() == HtmlPanelProvider.AvailabilityInfo.UNAVAILABLE) {
                        return null;
                    }
                    if (provider.getCOMPATIBILITY().getHtmlLevel() == null) {
                        // not a browser, must be text, don't show it for browsers
                        return null;
                    }

                    return provider.getINFO();
                });
    }

    @Override
    protected void updatePanelProviderDependentComponents(@NotNull HtmlPanelProvider fromProvider, @NotNull HtmlPanelProvider toProvider, boolean isInitialShow) {
        if (!myInUpdate) {
            try {
                myInUpdate = true;
                updateOptionalSettings();
            } finally {
                myInUpdate = false;
            }
        }
    }

    @Override
    protected void updateFormOnReshow(boolean isInitialShow) {

    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void updateOptionalSettings() {
        final HtmlPanelProvider provider = myLastPanelProvider != null ? HtmlPanelProvider.Companion.getFromInfoOrDefault(myLastPanelProvider) : getPanelProvider();
        myUseGrayscaleRendering.setEnabled(provider.isSupportedSetting(MdPreviewSettings.USE_GRAYSCALE_RENDERING));
        myUseGrayscaleRenderingLabel.setEnabled(provider.isSupportedSetting(MdPreviewSettings.USE_GRAYSCALE_RENDERING));
        myZoomFactor.setEnabled(provider.isSupportedSetting(MdPreviewSettings.ZOOM_FACTOR));
        myPageZoomLabel.setEnabled(provider.isSupportedSetting(MdPreviewSettings.ZOOM_FACTOR));
        myMaxImageWidth.setEnabled(provider.isSupportedSetting(MdPreviewSettings.MAX_IMAGE_WIDTH));
        myMaxImgWidthLabel.setEnabled(provider.isSupportedSetting(MdPreviewSettings.MAX_IMAGE_WIDTH));

        mySynchronizePreviewPosition.setEnabled(provider.isSupportedSetting(MdPreviewSettings.SYNCHRONIZE_PREVIEW_POSITION));
        mySynchronizePreviewPositionLabel.setEnabled(mySynchronizePreviewPosition.isEnabled() && provider.isSupportedSetting(MdPreviewSettings.SYNCHRONIZE_PREVIEW_POSITION));

        boolean syncPos = mySynchronizePreviewPosition.isEnabled() && mySynchronizePreviewPosition.isSelected() && provider.isSupportedSetting(MdPreviewSettings.SYNCHRONIZE_SOURCE_POSITION);
        mySynchronizeSourcePositionOnClick.setEnabled(syncPos);
        mySynchronizeSourcePositionOnClickLabel.setEnabled(syncPos);
        myVerticallyAlignSourceAndPreviewSyncPosition.setEnabled(syncPos);
        myVerticallyAlignSourceAndPreviewSyncPositionLabel.setEnabled(syncPos);

        myHighlightPreviewType.setEnabled(mySynchronizePreviewPosition.isEnabled()
                && mySynchronizePreviewPosition.isSelected()
                && provider.isSupportedSetting(MdPreviewSettings.FOCUS_HIGHLIGHT_PREVIEW));
        myHighlightPreviewTypeLabel.setEnabled(myHighlightPreviewType.isEnabled());
        myHighlightFadeOut.setEnabled(myHighlightPreviewType.isEnabled() && HighlightPreviewType.ADAPTER.findEnum((String) myHighlightPreviewType.getSelectedItem()) != HighlightPreviewType.NONE);
        myHighlightFadeOutLabel.setEnabled(myHighlightFadeOut.isEnabled());
        myHighlightOnTyping.setEnabled(myHighlightFadeOut.isEnabled());
        myHighlightOnTypingLabel.setEnabled(myHighlightFadeOut.isEnabled());

        for (MdSettingsComponent<MdRenderingProfileHolder> component : mySettingsExtensions) {
            component.updateOptionalSettings();
        }

        updatePanelProvider(provider);

        updateExtensionsOptionalSettings();
    }

    private void updatePanelProvider(HtmlPanelProvider provider) {
        if (settingsLoaded && !myInUpdate) {
            myProfileSynchronizer.updatePanelProvider(provider, null);
        }
    }

    public void updatePanelProvider(boolean havePreviewSettings) {
        if (settingsLoaded && !myInUpdate) {
            final HtmlPanelProvider provider = myLastPanelProvider != null ? HtmlPanelProvider.getFromInfoOrDefault(myLastPanelProvider) : getPanelProvider();
            myProfileSynchronizer.updatePanelProvider(provider, havePreviewSettings);
        }
    }

    @Override
    public void reset(@NotNull MdRenderingProfileHolder settings) {
        MdPreviewSettings previewSettings = settings.getPreviewSettings();
        try {
            myInUpdate = true;
            settingsLoaded = true;

            myLastPanelProvider = previewSettings.getHtmlPanelProviderInfo();
            if (myPreviewPanelModel.contains(myLastPanelProvider)) {
                myPreviewPanelModel.setSelectedItem(myLastPanelProvider);
                myPreviewProvider.setSelectedItem(myLastPanelProvider);
            } else {
                myLastPanelProvider = myPreviewPanelModel.getSelected();
            }

            SplitEditorPreviewType splitEditorPreviewType = previewSettings.getSplitEditorPreviewType();
            mySplitPreviewModel.setSelectedItem(splitEditorPreviewType);

            SplitEditorLayout splitEditorLayout = previewSettings.getSplitEditorLayout();
            mySplitLayoutModel.setSelectedItem(splitEditorLayout);

            components.reset(previewSettings);
            resetExtensions(settings);
            updateOptionalSettings();
        } finally {
            myInUpdate = false;
        }
    }

    @Override
    public void apply(@NotNull final MdRenderingProfileHolder settings) {
        SplitEditorLayout splitLayout = mySplitLayoutModel.getSelected();
        SplitEditorPreviewType splitPreview = mySplitPreviewModel.getSelected();
        HtmlPanelProvider.Info previewProvider = myLastPanelProvider;

        MdPreviewSettings previewSettings = settings.getPreviewSettings();

        if (previewProvider != null && splitLayout != null && splitPreview != null) {
            previewSettings.setSplitEditorLayout(splitLayout);
            previewSettings.setSplitEditorPreviewType(splitPreview);
            previewSettings.setHtmlPanelProviderInfo(previewProvider);
            components.apply(previewSettings);
            applyExtensions(settings);
        }
    }

    @Override
    public boolean isModified(@NotNull final MdRenderingProfileHolder settings) {
        MdRenderingProfile profile = settings.getRenderingProfile();
        MdRenderingProfile renderingProfile = new MdRenderingProfile(profile);
        apply(renderingProfile);
        return !renderingProfile.equals(profile);
    }
}
