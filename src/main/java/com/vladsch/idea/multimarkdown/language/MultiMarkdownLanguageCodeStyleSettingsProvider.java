package com.vladsch.idea.multimarkdown.language;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;

public class MultiMarkdownLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    private final static Logger LOGGER = Logger.getInstance(MultiMarkdownLanguageCodeStyleSettingsProvider.class.getName());

    @NonNls
    protected static final String SAMPLE_MARKDOWN_DOCUMENT_PATH = "/com/vladsch/idea/multimarkdown/sample-code-document.md";

    protected static final String SAMPLE_MARKDOWN_DOCUMENT = loadSampleMarkdownDocument();

    protected static String loadSampleMarkdownDocument() {
        try {
            return FileUtil.loadTextAndClose(new InputStreamReader(MultiMarkdownLanguageCodeStyleSettingsProvider.class.getResourceAsStream(SAMPLE_MARKDOWN_DOCUMENT_PATH)));
        } catch (Exception e) {
            LOGGER.error("Failed loading sample Markdown document", e);
        }
        return MultiMarkdownBundle.message("colorsettings.sample-loading-error");
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return MultiMarkdownLanguage.INSTANCE;
    }

    @Nullable
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new IndentOptionsEditor();
    }

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            //consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS");
            //consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Separator");
        } else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
            //consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");
        }
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return SAMPLE_MARKDOWN_DOCUMENT;
    }
}
