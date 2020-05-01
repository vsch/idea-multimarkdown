// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.lang.Language;
import com.intellij.lexer.EmbeddedTokenTypesProvider;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface MdLanguageProvider {
    ExtensionPointName<MdLanguageProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.languageProvider");
    MdExtensions<MdLanguageProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdLanguageProvider[0]);

    /**
     * 
     * @param languageName language info string
     * @param languageMap  language name to language map
     * @param embeddedTokenTypeProviders embedded token extension providers
     * @return language info to use for language lookup
     */
    @Nullable
    default Language getLanguageFromInfo(@NotNull String languageName, @NotNull Map<String, Language> languageMap, List<EmbeddedTokenTypesProvider> embeddedTokenTypeProviders) {
        return null;
    }

    /**
     * Return map of additional languages
     *
     * @param result current language map, contains lowercase keys with language values
     */
    void adjustLanguages(@NotNull Map<String, Language> result);
}
