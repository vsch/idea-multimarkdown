// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdVerbatim extends MdIndentingComposite, NavigationItem, MdPsiLanguageInjectionHost, MdBreadcrumbElement, MdBlockElement {
    @Nullable
    ASTNode getOpenMarkerNode();

    @Nullable
    ASTNode getCloseMarkerNode();

    @Nullable
    ASTNode getLanguageNode();

    @NotNull
    String getOpenMarker();

    @NotNull
    String getCloseMarker();

    @NotNull
    String getVerbatimLanguage();

    @NotNull
    default String getInfoString() {
        String info = getVerbatimLanguage().trim();
        int pos = SequenceUtils.indexOfAny(info, CharPredicate.SPACE_TAB);
        return pos < 0 ? info : info.substring(0, pos);
    }

    @NotNull
    PsiElement setVerbatimLanguage(@Nullable String verbatimLanguageName);

    @NotNull
    TextRange getVerbatimLanguageRange(boolean inDocument);

    @NotNull
    CharSequence getContentCharSequence();

    @NotNull
    String getLeadMarkerPrefix();

    @Nullable
    String getContentPrefix();

    @Nullable
    String getContentSuffix();
}
