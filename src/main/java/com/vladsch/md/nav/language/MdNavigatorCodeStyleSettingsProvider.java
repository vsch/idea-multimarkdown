// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.language.api.MdCodeStyleConfigurableProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdNavigatorCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
    public static final String DOCUMENT_FORMAT = "com.vladsch.markdown.navigator.settings.document-format";

    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new MdCodeStyleSettings(settings);
    }

    @Nullable
    @Override
    public Language getLanguage() {
        return MdLanguage.INSTANCE;
    }

    public MdNavigatorCodeStyleSettingsProvider() {
        super();
    }

    @Nullable
    @Override
    public String getConfigurableDisplayName() {
        return MdLanguage.NAME;
    }

    @NotNull
    @Override
    public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings originalSettings) {
        for (MdCodeStyleConfigurableProvider provider : MdCodeStyleConfigurableProvider.EXTENSIONS.getValue()) {
            CodeStyleConfigurable settingsPage = provider.createSettingsPage(settings, originalSettings);
            if (settingsPage != null) return settingsPage;
        }
        return new MdCodeStyleConfigurable(settings, originalSettings);
    }

    private static class MdCodeStyleConfigurable extends CodeStyleAbstractConfigurable {
        MdCodeStyleConfigurable(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
            super(settings, originalSettings, MdLanguage.NAME);
        }

        @Override
        protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
            return new CodeStyleMainPanel(getCurrentSettings(), settings);
        }

        @NotNull
        @Override
        public String getHelpTopic() {
            return DOCUMENT_FORMAT;
        }
    }

    private static class CodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
        public CodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
            super(MdLanguage.INSTANCE, currentSettings, settings);
        }

        /**
         * Initializes all standard tabs: "Tabs and Indents", "Spaces", "Blank Lines" and "Wrapping and Braces" if relevant. For "Tabs and Indents" LanguageCodeStyleSettingsProvider must instantiate its own indent options, for other standard tabs it must return false in usesSharedPreview() method. You can override this method to add your own tabs by calling super.initTabs() and then addTab() methods or selectively add needed tabs with your own implementation.
         *
         * @param settings Code style settings to be used with initialized panels.
         *
         * @see LanguageCodeStyleSettingsProvider
         * @see #addIndentOptionsTab(CodeStyleSettings)
         * @see #addSpacesTab(CodeStyleSettings)
         * @see #addBlankLinesTab(CodeStyleSettings)
         * @see #addWrappingAndBracesTab(CodeStyleSettings)
         */
        protected void initTabs(CodeStyleSettings settings) {
            addIndentOptionsTab(settings);
            addWrappingAndBracesTab(settings);
        }
    }
}
