// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.settings;

import com.intellij.ui.components.JBCheckBox;
import com.vladsch.md.nav.editor.util.HtmlPanelProvider;
import com.vladsch.md.nav.settings.EmojiImagesType;
import com.vladsch.md.nav.settings.EmojiShortcutsType;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdParserSettingsForm;
import com.vladsch.md.nav.settings.MdRenderingProfileHolder;
import com.vladsch.md.nav.settings.ParserOptions;
import com.vladsch.md.nav.settings.PegdownExtensions;
import com.vladsch.md.nav.settings.RenderingProfileSynchronizer;
import com.vladsch.md.nav.settings.api.MdParserSettingsComponent;
import com.vladsch.md.nav.settings.api.SettingsFormImpl;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Map;

public class FlexmarkParserSettingsForm extends SettingsFormImpl implements MdParserSettingsComponent<MdRenderingProfileHolder> {
    private JPanel myMainPanel;
    JBCheckBox myFlexmarkFrontMatter;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    private final SettingsComponents<MdParserSettings> components;

    public FlexmarkParserSettingsForm(RenderingProfileSynchronizer profileSynchronizer) {
        super(profileSynchronizer);

        components = new SettingsComponents<MdParserSettings>() {
            @Override
            protected Settable<MdParserSettings>[] createComponents(@NotNull MdParserSettings i) {
                //noinspection unchecked
                return new Settable[] {
                };
            }
        };

        onFormCreated();
    }

    @Override
    public void addPegdownExtensionCheckboxes(Map<JBCheckBox, PegdownExtensions> checkBoxPegdownMap) {

    }

    @Override
    public void addParserOptionCheckboxes(Map<JBCheckBox, ParserOptions> checkBoxParserMap) {
        checkBoxParserMap.put(myFlexmarkFrontMatter, ParserOptions.FLEXMARK_FRONT_MATTER);
    }

    @Nullable
    @Override
    public MdParserSettingsForm.ParserProfile getProfile(int pegdownOptions, long parserOptions, EmojiShortcutsType emojiShortcutsType, EmojiImagesType emojiImagesType) {
        return null;
    }

    @Nullable
    @Override
    public MdParserSettingsForm.ParserProfileItem getParserProfileItem(MdParserSettingsForm.ParserProfile parserProfile) {
        return null;
    }

    @Nullable
    @Override
    public MdParserSettingsForm.ParserProfileItem[] getParserProfileItems() {
        return null;
    }

    @Override
    public void updateOptionalSettings() {

    }

    private void createUIComponents() {

    }

    @Override
    protected void updatePanelProviderDependentComponents(@NotNull HtmlPanelProvider fromProvider, @NotNull HtmlPanelProvider toProvider, boolean isInitialShow) {

    }

    public void updateFormOnReshow(boolean isInitialShow) {

    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void reset(@NotNull final MdRenderingProfileHolder settings) {
        components.reset(settings.getParserSettings());
    }

    @Override
    public void apply(@NotNull final MdRenderingProfileHolder settings) {
        components.apply(settings.getParserSettings());
    }

    @Override
    public boolean isModified(@NotNull final MdRenderingProfileHolder settings) {
        return components.isModified(settings.getParserSettings());
    }

    @Override
    protected void disposeResources() {

    }
}
