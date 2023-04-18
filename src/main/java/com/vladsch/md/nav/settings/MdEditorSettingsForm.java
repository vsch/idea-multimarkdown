// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.ui.components.JBCheckBox;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.highlighter.MdSyntaxHighlighter;
import com.vladsch.md.nav.settings.api.ApplicationSettingsContainer;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;

public class MdEditorSettingsForm extends ApplicationSettingsContainer {
    private JPanel myMainPanel;

    // Enhanced
    JBCheckBox myAsteriskItalics;
    JBCheckBox myCodeLikeStyleToggle;
    JBCheckBox myEnableLineMarkers;
    JBCheckBox myFullHighlightCombinations;
    JBCheckBox myGrammarIgnoreSimpleTextCasing;
    JBCheckBox myHideDisabledButtons;
    JBCheckBox myHideToolbar;
    JLabel myHighlightComboLabel;
    JBCheckBox myHtmlLangInjections;
    JBCheckBox myIconGutters;
    JBCheckBox myJoinStripPrefix;
    JSpinner myMaxBreadcrumbText;
    JBCheckBox myMultiLineImageUrlInjections;
    JBCheckBox myShowBreadcrumbText;
    JBCheckBox mySmartEditAsterisks;
    JBCheckBox mySmartEditBackTicks;
    JBCheckBox mySmartEditTildes;
    JBCheckBox mySmartEditUnderscores;
    JComboBox<String> mySyntaxHighlighting;
    JComboBox<String> myTextSplitLayoutToggle;
    JTextField myToggleStylePunctuations;
    JSpinner myTypingUpdateDelay;
    JBCheckBox myVerbatimLangInjections;
    JBCheckBox myVerticalSplitPreview;

    JBCheckBox myShowTranslateDocument;
    YandexTranslateOptionsForm myYandexTranslateOptionsForm;

    private JPanel myExtensionsPanel;
    JBCheckBox myWrapOnlyOnTypingSpace;

    private final SettingsComponents<MdDocumentSettings> components;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    public MdEditorSettingsForm(MdApplicationSettings settings, RenderingProfileSynchronizer profileSynchronizer) {
        super(settings, profileSynchronizer);

        components = new SettingsComponents<MdDocumentSettings>() {
            @Override
            protected Settable<MdDocumentSettings>[] createComponents(@NotNull MdDocumentSettings i) {
                //noinspection unchecked
                return new Settable[] {
                        // Enhanced
                        notrace("myAsteriskItalics", component(myAsteriskItalics, i::getAsteriskItalics, i::setAsteriskItalics)),
                        notrace("myCodeLikeStyleToggle", component(myCodeLikeStyleToggle, i::getCodeLikeStyleToggle, i::setCodeLikeStyleToggle)),
                        notrace("myFullHighlightCombinations", component(myFullHighlightCombinations, i::getFullHighlightCombinations, i::setFullHighlightCombinations)),
                        notrace("myGrammarIgnoreSimpleTextCasing", component(myGrammarIgnoreSimpleTextCasing, i::getGrammarIgnoreSimpleTextCasing, i::setGrammarIgnoreSimpleTextCasing)),
                        notrace("myHideDisabledButtons", component(myHideDisabledButtons, i::getHideDisabledButtons, i::setHideDisabledButtons)),
                        notrace("myHideToolbar", component(myHideToolbar, i::getHideToolbar, i::setHideToolbar)),
                        notrace("myEnableLineMarkers", component(myEnableLineMarkers, i::getEnableLineMarkers, i::setEnableLineMarkers)),
                        notrace("myHtmlLangInjections", component(myHtmlLangInjections, i::getHtmlLangInjections, i::setHtmlLangInjections)),
                        notrace("myIconGutters", component(myIconGutters, i::getIconGutters, i::setIconGutters)),
                        notrace("myJoinStripPrefix", component(myJoinStripPrefix, i::getJoinStripPrefix, i::setJoinStripPrefix)),
                        notrace("myMaxBreadcrumbText", component(myMaxBreadcrumbText, i::getMaxBreadcrumbText, i::setMaxBreadcrumbText)),
                        notrace("myMultiLineImageUrlInjections", component(myMultiLineImageUrlInjections, i::getMultiLineImageUrlInjections, i::setMultiLineImageUrlInjections)),
                        notrace("myShowBreadcrumbText", component(myShowBreadcrumbText, i::getShowBreadcrumbText, i::setShowBreadcrumbText)),
                        notrace("mySmartEditAsterisks", component(mySmartEditAsterisks, i::getSmartEditAsterisks, i::setSmartEditAsterisks)),
                        notrace("mySmartEditBackTicks", component(mySmartEditBackTicks, i::getSmartEditBackTicks, i::setSmartEditBackTicks)),
                        notrace("mySmartEditTildes", component(mySmartEditTildes, i::getSmartEditTildes, i::setSmartEditTildes)),
                        notrace("mySmartEditUnderscores", component(mySmartEditUnderscores, i::getSmartEditUnderscore, i::setSmartEditUnderscore)),
                        notrace("mySyntaxHighlighting", component(SyntaxHighlightingType.ADAPTER, mySyntaxHighlighting, i::getSyntaxHighlighting, i::setSyntaxHighlighting)),
                        notrace("myTextSplitLayoutToggle", component(TextSplitLayoutToggleType.ADAPTER, myTextSplitLayoutToggle, i::getTextSplitLayoutToggle, i::setTextSplitLayoutToggle)),
                        notrace("myToggleStylePunctuations", component(myToggleStylePunctuations, () -> i.getToggleStylePunctuations().replace(" ", ""), (value) -> i.setToggleStylePunctuations(value.replace(" ", "")))),
                        notrace("myTypingUpdateDelay", component(myTypingUpdateDelay, i::getTypingUpdateDelay, i::setTypingUpdateDelay)),
                        notrace("myVerbatimLangInjections", component(myVerbatimLangInjections, i::getVerbatimLangInjections, i::setVerbatimLangInjections)),
                        notrace("myVerticalSplitPreview", component(myVerticalSplitPreview, i::getVerticalSplitPreview, i::setVerticalSplitPreview)),
                        notrace("ShowTranslateDocument", component(myShowTranslateDocument, i::getShowTranslateDocument, i::setShowTranslateDocument)),
                        notrace("WrapOnlyOnTypingSpace", component(myWrapOnlyOnTypingSpace, i::getWrapOnlyOnTypingSpace, i::setWrapOnlyOnTypingSpace)),
                        notrace("", component(myYandexTranslateOptionsForm, i)),
                };
            }
        };

        ActionListener actionListener = event -> updateOptionalSettings();

        myShowBreadcrumbText.addActionListener(actionListener);
        myFullHighlightCombinations.addActionListener(actionListener);
        myIconGutters.addActionListener(actionListener);
        myShowTranslateDocument.addActionListener(actionListener);
        myEnableLineMarkers.addActionListener(actionListener);

        onFormCreated();
    }

    private void createUIComponents() {
        mySyntaxHighlighting = SyntaxHighlightingType.ADAPTER.createComboBox();
        myTextSplitLayoutToggle = TextSplitLayoutToggleType.ADAPTER.createComboBox();

        final SpinnerNumberModel model = new SpinnerNumberModel(MdDocumentSettings.DEFAULT_MAX_BREADCRUMB_TEXT, MdDocumentSettings.MIN_MAX_BREADCRUMB_TEXT, MdDocumentSettings.MAX_MAX_BREADCRUMB_TEXT, 1);
        myMaxBreadcrumbText = new JSpinner(model);

        final SpinnerNumberModel delayModel = new SpinnerNumberModel(MdDocumentSettings.DEFAULT_PREVIEW_DELAY, MdDocumentSettings.MIN_PREVIEW_DELAY, MdDocumentSettings.MAX_PREVIEW_DELAY, 50);
        myTypingUpdateDelay = new JSpinner(delayModel);

        myYandexTranslateOptionsForm = new YandexTranslateOptionsForm(myApplicationSettings.getDocumentSettings());

        myExtensionsPanel = getExtensionsPanel();
    }

    public void updateFormOnReshow(boolean isInitialShow) {

    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void updateOptionalSettings() {
        myMaxBreadcrumbText.setEnabled(myShowBreadcrumbText.isSelected());

        if (myFullHighlightCombinations.isSelected() != MdPlugin.getInstance().getStartupDocumentSettings().getFullHighlightCombinations()) {
            myHighlightComboLabel.setText(String.format("<html><strong></strong>%s</html>", MdBundle.message("settings.application.full-highlight-combinations.restart.label")));
        } else {
            int splitCombos = MdSyntaxHighlighter.getMergedKeys().size();
            int allAttributes = MdSyntaxHighlighter.getAttributes().size();
            myHighlightComboLabel.setText(String.format("%d/%d", splitCombos, allAttributes));
        }

        boolean yandexEnabled = myShowTranslateDocument.isEnabled() && myShowTranslateDocument.isSelected();
        myYandexTranslateOptionsForm.setEnabled(yandexEnabled);

        myEnableLineMarkers.setEnabled(myIconGutters.isEnabled() && myIconGutters.isSelected());

        updateExtensionsOptionalSettings();
    }

    @Override
    public void reset(@NotNull MdApplicationSettingsHolder settings) {
        components.reset(settings.getDocumentSettings());
        resetExtensions(settings);
        updateOptionalSettings();
    }

    public boolean isModified(@NotNull MdApplicationSettingsHolder settings) {
        return components.isModified(settings.getDocumentSettings()) || isModifiedExtensions(settings);
    }

    public void apply(@NotNull MdApplicationSettingsHolder settings) {
        MdDocumentSettings documentSettings = settings.getDocumentSettings();
        settings.setDocumentSettings(documentSettings);
        components.apply(documentSettings);
        applyExtensions(settings);
    }
}
