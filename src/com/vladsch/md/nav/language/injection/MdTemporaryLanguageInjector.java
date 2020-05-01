// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language.injection;

import com.intellij.lang.Language;
import com.vladsch.md.nav.psi.element.MdVerbatim;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.inject.TemporaryPlacesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// NOTE: Needs to be registered in the plugin-intellilang.xml
public class MdTemporaryLanguageInjector extends MdLanguageInjector {
    @Nullable
    @Override
    protected Language findLangForInjection(@NotNull MdVerbatim element) {
        final TemporaryPlacesRegistry registry = TemporaryPlacesRegistry.getInstance(element.getProject());
        final InjectedLanguage language = registry.getLanguageFor(element, element.getContainingFile());
        if (language != null) {
            return language.getLanguage();
        }
        return null;
    }

    public MdTemporaryLanguageInjector() {
        //Extensions.getRootArea().getExtensionPoint(LanguageInjectionSupport.EP_NAME).registerExtension(new MultiMarkdownTemporaryLanguageSupport(this));
    }
}
