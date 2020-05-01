// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.intellij.util.LocalTimeCounter;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.settings.TrailingSpacesType;
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesExtension;
import com.vladsch.md.nav.language.api.MdTrailingSpacesCodeStyleOption;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;

public class MdLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    private final static Logger LOG = Logger.getInstance(MdLanguageCodeStyleSettingsProvider.class.getName());

    @NonNls
    protected static final String SAMPLE_MARKDOWN_DOCUMENT_PATH = "/com/vladsch/md/nav/samples/sample-markdown-document.md";

    private static String loadSampleMarkdownDocument(final String documentPath) {
        try {
            final InputStream resourceAsStream = MdPlugin.class.getResourceAsStream(documentPath);
            return FileUtil.loadTextAndClose(new InputStreamReader(resourceAsStream));
        } catch (Exception e) {
            LOG.error("Failed loading sample Markdown document " + documentPath, e);
        }
        return MdBundle.message("colors.sample-loading-error");
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    // DEPRECATED: replacement override customizeSettings() appeared in 2018-09-04
    //    change when 183.2207 is lowest supported version
    public CommonCodeStyleSettings getDefaultCommonSettings() {
        CommonCodeStyleSettings commonSettings = new CommonCodeStyleSettings(getLanguage());

        try {
            commonSettings.WRAP_ON_TYPING = WrapOnTyping.NO_WRAP.intValue;
        } catch (NoSuchFieldError ignored) {
        }

        commonSettings.RIGHT_MARGIN = 72;
        CommonCodeStyleSettings.IndentOptions indentOptions = commonSettings.initIndentOptions();
        indentOptions.TAB_SIZE = 4;
        indentOptions.INDENT_SIZE = 4;
        indentOptions.CONTINUATION_INDENT_SIZE = 4;
        indentOptions.KEEP_INDENTS_ON_EMPTY_LINES = false;
        indentOptions.USE_TAB_CHARACTER = false;
        indentOptions.USE_RELATIVE_INDENTS = false;
        indentOptions.SMART_TABS = false;
        return commonSettings;
    }

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.INDENT_SETTINGS) {
            consumer.showStandardOptions("INDENT_SIZE", "USE_TAB_CHARACTER", "TAB_SIZE");
        } else if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showStandardOptions("RIGHT_MARGIN");
            consumer.showCustomOption(MdCodeStyleSettings.class,
                    "WRAP_ON_TYPING",
                    "Wrap on typing",
                    null,
                    CodeStyleSettingsCustomizable.WRAP_ON_TYPING_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_ON_TYPING_VALUES);
            consumer.showCustomOption(MdCodeStyleSettings.class,
                    "FORMAT_WITH_SOFT_WRAP",
                    "Disable wrap on typing when soft wraps enabled",
                    null,
                    new String[] { FormatWithSoftWrap.DISABLED.displayName, FormatWithSoftWrap.ENABLED.displayName, FormatWithSoftWrap.INFINITE_MARGIN.displayName },
                    new int[] { FormatWithSoftWrap.DISABLED.intValue, FormatWithSoftWrap.ENABLED.intValue, FormatWithSoftWrap.INFINITE_MARGIN.intValue }
            );
            consumer.showCustomOption(MdCodeStyleSettings.class,
                    "CODE_KEEP_TRAILING_SPACES",
                    "Fenced/Indented Code",
                    "Keep Trailing Spaces",
                    new String[] { TrailingSpacesType.KEEP_ALL.displayName, TrailingSpacesType.KEEP_NONE.displayName },
                    new int[] { TrailingSpacesType.KEEP_ALL.intValue, TrailingSpacesType.KEEP_NONE.intValue }
            );

            int i = 1;
            for (MdTrailingSpacesCodeStyleOption option : MdStripTrailingSpacesExtension.getOptions()) {
                consumer.showCustomOption(MdCodeStyleSettings.class,
                        "TRAILING_SPACES_OPTION_" + i++,
                        option.getOptionName(),
                        "Keep Trailing Spaces",
                        TrailingSpacesType.getDisplayNames(option.getExcludedOptions()),
                        TrailingSpacesType.getOptionValues(option.getExcludedOptions())
                );
            }
            
            consumer.showCustomOption(MdCodeStyleSettings.class,
                    "KEEP_TRAILING_SPACES",
                    "Everything Else",
                    "Keep Trailing Spaces",
                    new String[] { TrailingSpacesType.KEEP_ALL.displayName, TrailingSpacesType.KEEP_NONE.displayName },
                    new int[] { TrailingSpacesType.KEEP_ALL.intValue, TrailingSpacesType.KEEP_NONE.intValue }
            );
        }
    }

    @Nullable
    @Override
    public PsiFile createFileFromText(final Project project, final String text) {
        return PsiFileFactory.getInstance(project)
                .createFileFromText("a.md", MdFileType.INSTANCE, text, LocalTimeCounter.currentTime(), false);
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return MdLanguage.INSTANCE;
    }

    @Nullable
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor(this);
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return loadSampleMarkdownDocument(SAMPLE_MARKDOWN_DOCUMENT_PATH);
    }
}
