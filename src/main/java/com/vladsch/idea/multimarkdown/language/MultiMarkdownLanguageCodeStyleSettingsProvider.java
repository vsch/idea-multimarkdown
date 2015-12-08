/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.language;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
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
