// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language.injection;

import com.intellij.lang.Language;
import com.intellij.lexer.EmbeddedTokenTypesProvider;
import com.vladsch.md.nav.language.api.MdLanguageProvider;
import com.vladsch.plugin.util.LazyComputable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public enum LanguageGuesser {
    INSTANCE;

    private final LazyComputable<List<EmbeddedTokenTypesProvider>> embeddedTokenTypeProviders =
            new LazyComputable<>(() -> Arrays.asList(EmbeddedTokenTypesProvider.EXTENSION_POINT_NAME.getExtensions()));

    private final LazyComputable<Map<String, Language>> langIdToLanguage = new LazyComputable<>(() -> {
        final HashMap<String, Language> result = new HashMap<>();
        for (Language language : Language.getRegisteredLanguages()) {
            result.put(language.getID().toLowerCase(Locale.US), language);
        }

        if (result.containsKey("bash")) result.put("shell", result.get("bash"));
        if (result.containsKey("javascript")) result.put("js", result.get("javascript"));
        if (result.containsKey("markdown")) result.put("multimarkdown", result.get("markdown"));

        return result;
    });

    @Nullable
    public Language guessLanguage(@NotNull String languageName) {
        HashMap<String, Language> languageMap = new HashMap<>(langIdToLanguage.getValue());
        for (MdLanguageProvider provider : MdLanguageProvider.EXTENSIONS.getValue()) {
            provider.adjustLanguages(languageMap);
        }

        final Language languageFromMap = languageMap.get(languageName.toLowerCase(Locale.US));
        if (languageFromMap != null) {
            return languageFromMap;
        }

        for (MdLanguageProvider provider : MdLanguageProvider.EXTENSIONS.getValue()) {
            Language language = provider.getLanguageFromInfo(languageName, languageMap, embeddedTokenTypeProviders.getValue());
            if (language != null) return language;
        }

        for (EmbeddedTokenTypesProvider provider : embeddedTokenTypeProviders.getValue()) {
            if (provider.getName().equalsIgnoreCase(languageName)) {
                return provider.getElementType().getLanguage();
            }
        }

        return null;
    }

    @NotNull
    public Set<String> getLanguageNames() {

        Map<String, Language> result = langIdToLanguage.getValue();
        for (MdLanguageProvider provider : MdLanguageProvider.EXTENSIONS.getValue()) {
            provider.adjustLanguages(result);
        }

        HashSet<String> list = new HashSet<>();
        for (String languageName : result.keySet()) {
            list.add(languageName.toLowerCase(Locale.US));
        }

        for (EmbeddedTokenTypesProvider provider : embeddedTokenTypeProviders.getValue()) {
            list.add(provider.getName().toLowerCase(Locale.US));
        }

        return list;
    }
}
