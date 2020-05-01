// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.flex.psi.FakePsiLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class FlexmarkExampleOptionFakeLiteralManipulator implements ElementManipulator<FakePsiLiteralExpression> {
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
    public FakePsiLiteralExpression handleContentChange(@NotNull FakePsiLiteralExpression element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, newContent);
    }

    @Override
    public FakePsiLiteralExpression handleContentChange(@NotNull FakePsiLiteralExpression element, String newContent) throws IncorrectOperationException {
        PsiExpression expressionFromText = JavaPsiFacade.getElementFactory(element.getProject()).createExpressionFromText("\"" + newContent + "\"", element.getContext());
        PsiElement newElement = element.getLiteralExpression().replace(expressionFromText);
        return new FakePsiLiteralExpression((PsiLiteralExpression) newElement, new TextRange(1, newElement.getTextLength() - 1));
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull FakePsiLiteralExpression element) {
        return new TextRange(0, element.getTextLength());
    }
}
