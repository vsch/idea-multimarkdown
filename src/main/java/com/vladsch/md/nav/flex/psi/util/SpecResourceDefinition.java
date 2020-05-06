// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.util;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class SpecResourceDefinition {
    final private @NotNull PsiClass myPsiClass;
    final private @NotNull String mySpecResourceText;
    final private @NotNull PsiLiteralExpression mySpecResourceLiteral;
    final private @NotNull TextRange myTextRange;

    public SpecResourceDefinition(@NotNull PsiClass psiClass, @NotNull PsiLiteralExpression specResourceLiteral, @NotNull String specResourceText, @NotNull TextRange textRange) {
        myPsiClass = psiClass;
        mySpecResourceText = specResourceText;
        mySpecResourceLiteral = specResourceLiteral;
        myTextRange = textRange;
    }

    @NotNull
    public PsiClass getPsiClass() {
        return myPsiClass;
    }

    @NotNull
    public String getSpecResourceText() {
        return mySpecResourceText;
    }

    @NotNull
    public PsiLiteralExpression getSpecResourceLiteral() {
        return mySpecResourceLiteral;
    }

    @NotNull
    public TextRange getTextRange() {
        return myTextRange;
    }

    public boolean isEmptyResourceSpec() {
        return mySpecResourceText.trim().isEmpty();
    }
}
