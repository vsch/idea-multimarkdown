// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language.injection;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.psi.PsiElement;
import com.intellij.psi.injection.ReferenceInjector;
import com.vladsch.md.nav.flex.psi.FlexmarkFrontMatterBlock;
import com.vladsch.md.nav.psi.element.MdHtmlBlockImpl;
import com.vladsch.md.nav.psi.element.MdImageMultiLineUrlContentImpl;
import com.vladsch.md.nav.psi.element.MdJekyllFrontMatterBlockImpl;
import com.vladsch.md.nav.psi.element.MdPsiLanguageInjectionHost;
import com.vladsch.md.nav.psi.element.MdVerbatim;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

// NOTE: Needs to be registered in the plugin.xml
public class MdLanguageInjector implements MultiHostInjector {

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        // diagnostic/3922, Invalid PsiElement
        // diagnostic/4131, File is not writable: VirtualFileWindow, PhpStorm
        if (!context.isValid() || context.getContainingFile().getVirtualFile() != null && !context.getContainingFile().getVirtualFile().isWritable()) {
            return;
        }

        if (context instanceof MdVerbatim) {
            if (!MdApplicationSettings.getInstance().getDocumentSettings().getVerbatimLangInjections()) return;

            MdVerbatim verbatimElement = (MdVerbatim) context;
            final String fenceLanguage = verbatimElement.getVerbatimLanguage();
            Language language = findLangForInjection(verbatimElement);
            if (language == null && !fenceLanguage.isEmpty()) {
                language = guessLanguageByFenceLangOrPlainText(fenceLanguage);
            }

            if (language == null) return;

            String prefix = verbatimElement.getContentPrefix();
            String suffix = verbatimElement.getContentSuffix();

            if (LanguageParserDefinitions.INSTANCE.forLanguage(language) == null) {
                ReferenceInjector injector = ReferenceInjector.findById(language.getID());
                if (injector == null) {
                    return;
                }
            }

            registrar.startInjecting(language);
            registrar.addPlace(prefix, suffix,
                    verbatimElement,
                    verbatimElement.getContentRange(false));

            registrar.doneInjecting();
        } else if (context instanceof MdHtmlBlockImpl) {
            if (!MdApplicationSettings.getInstance().getDocumentSettings().getHtmlLangInjections()) return;

            MdHtmlBlockImpl htmlBlock = (MdHtmlBlockImpl) context;

            if (htmlBlock.getFirstChild() == null) {
                return;
            }

            Language language = guessLanguageByFenceLangOrPlainText("html");
            if (language == null) return;

            registrar.startInjecting(language);

            registrar.addPlace(null,
                    null,
                    htmlBlock,
                    htmlBlock.getContentRange());

            registrar.doneInjecting();
        } else if (context instanceof MdJekyllFrontMatterBlockImpl) {
            if (!MdApplicationSettings.getInstance().getDocumentSettings().getHtmlLangInjections()) return;

            MdJekyllFrontMatterBlockImpl htmlBlock = (MdJekyllFrontMatterBlockImpl) context;

            if (htmlBlock.getFirstChild() == null) {
                return;
            }

            Language language = guessLanguageByFenceLangOrPlainText("yaml");
            if (language == null) return;

            registrar.startInjecting(language);

            registrar.addPlace(null,
                    null,
                    htmlBlock,
                    htmlBlock.getContentRange());

            registrar.doneInjecting();
        } else if (context instanceof FlexmarkFrontMatterBlock) {
            if (!MdApplicationSettings.getInstance().getDocumentSettings().getHtmlLangInjections()) return;

            FlexmarkFrontMatterBlock htmlBlock = (FlexmarkFrontMatterBlock) context;

            if (htmlBlock.getFirstChild() == null) {
                return;
            }

            Language language = guessLanguageByFenceLangOrPlainText("yaml");
            if (language == null) return;

            registrar.startInjecting(language);

            registrar.addPlace(null,
                    null,
                    htmlBlock,
                    htmlBlock.getContentRange());

            registrar.doneInjecting();
        } else if (context instanceof MdImageMultiLineUrlContentImpl) {
            if (!MdApplicationSettings.getInstance().getDocumentSettings().getMultiLineImageUrlInjections()) return;

            MdImageMultiLineUrlContentImpl multiLineUrl = (MdImageMultiLineUrlContentImpl) context;

            String contentLanguage = multiLineUrl.getContentLanguage();
            Language language = guessLanguageByFenceLangOrPlainText(contentLanguage == null ? "text" : contentLanguage);
            if (language == null) return;

            registrar.startInjecting(language);

            registrar.addPlace(null,
                    null,
                    multiLineUrl,
                    multiLineUrl.getContentRange(false));

            registrar.doneInjecting();
        }
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(MdPsiLanguageInjectionHost.class);
    }

    @Nullable
    protected Language findLangForInjection(@NotNull MdVerbatim element) {
        final String fenceLanguage = element.getVerbatimLanguage();
        return guessLanguageByFenceLang(fenceLanguage);
    }

    @Nullable
    static Language guessLanguageByFenceLang(@NotNull String langName) {
        return LanguageGuesser.INSTANCE.guessLanguage(langName);
    }

    @Nullable
    static Language guessLanguageByFenceLangOrPlainText(@NotNull String langName) {
        Language language = LanguageGuesser.INSTANCE.guessLanguage(langName);
        if (language == null) {
            language = guessLanguageByFenceLang("text");
        }
        return language;
    }
}
