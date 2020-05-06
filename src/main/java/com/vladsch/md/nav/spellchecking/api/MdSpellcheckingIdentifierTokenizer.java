// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.spellchecking.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.quickfixes.RenameTo;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdSpellcheckingIdentifierTokenizer {
    ExtensionPointName<MdSpellcheckingIdentifierTokenizer> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.spellcheckingIdentifierTokenizer");
    MdExtensions<MdSpellcheckingIdentifierTokenizer> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdSpellcheckingIdentifierTokenizer[0]);

    /**
     * Return non-null to stop checking
     *
     * @param element   element
     * @param tokenizer default tokenizer
     *
     * @return non-null to stop search or null to keep trying other extensions
     */
    @Nullable
    Tokenizer<?> getIdentifierTokenizer(@NotNull PsiElement element, @NotNull Tokenizer<?> tokenizer);

    /**
     * Provide rename quick fix
     *
     * @param wordWithTypo string of word with typo
     *
     * @return rename to quick fix or null
     */
    @Nullable
    RenameTo getRenameQuickFix(@NotNull String wordWithTypo);
}
