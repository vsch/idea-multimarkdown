// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class FlexmarkExampleOptionLiteralManipulator implements ElementManipulator<PsiLiteralExpression> {
    /**
     * Changes the element's text to a new value
     *
     * @param element    element to be changed
     * @param range      range within the element
     * @param newContent new element text
     *
     * @return changed element
     *
     * @throws IncorrectOperationException if something goes wrong
     */
    @Override
    public PsiLiteralExpression handleContentChange(@NotNull PsiLiteralExpression element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, newContent);
    }

    @Override
    public PsiLiteralExpression handleContentChange(@NotNull PsiLiteralExpression element, String newContent) throws IncorrectOperationException {
        PsiExpression expressionFromText = JavaPsiFacade.getElementFactory(element.getProject()).createExpressionFromText("\"" + newContent + "\"", element.getContext());
        element.replace(expressionFromText);
        return element;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull PsiLiteralExpression element) {
        return new TextRange(1, element.getTextLength() - 1);
    }
}
