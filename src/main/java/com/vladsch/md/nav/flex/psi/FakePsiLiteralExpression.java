// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils;
import icons.FlexmarkIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class FakePsiLiteralExpression extends FakePsiElement {
    @SuppressWarnings("NotNullFieldNotInitialized") @NotNull PsiLiteralExpression myElement;
    @SuppressWarnings("NotNullFieldNotInitialized") @NotNull TextRange myTextRangeInParent;
    @SuppressWarnings("NotNullFieldNotInitialized") @NotNull TextRange myTextRange;

    public FakePsiLiteralExpression(@NotNull PsiLiteralExpression element, @NotNull TextRange textRangeInParent) {
        updateElement(element, textRangeInParent);
    }

    private void updateElement(@NotNull PsiLiteralExpression element, @NotNull TextRange textRangeInParent) {
        myElement = element;
        myTextRangeInParent = textRangeInParent;
        myTextRange = textRangeInParent.shiftRight(myElement.getTextOffset());
    }

    @NotNull
    public PsiLiteralExpression getLiteralExpression() {
        return myElement;
    }

    @Override
    public PsiReference getReference() {
        return null;
    }

    @Nullable
    @Override
    public PsiManager getManager() {
        final PsiElement parent = getParent();
        return parent != null ? parent.getManager() : null;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getText();
    }

    @Nullable
    @Override
    public String getLocationString() {
        PsiClass psiClass = FlexmarkPsiImplUtils.getElementPsiClass(myElement);
        return psiClass == null ? getName() : psiClass.getName();
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
////        throw new IncorrectOperationException("Not supported on fake element");
//        PsiExpression fromText = JavaPsiFacade.getInstance(myElement.getProject()).getElementFactory().createExpressionFromText("\"" + name + "\"", myElement.getParent());
//        PsiLiteralExpression psiElement = (PsiLiteralExpression) myElement.replace(fromText);
//        updateElement(psiElement, new TextRange(1, psiElement.getTextLength() - 1));
//        return psiElement;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return MdLanguage.INSTANCE;
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return new PsiElement[0];
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return null;
    }

    @NotNull
    @Override
    public char[] textToCharArray() {
        return getText().toCharArray();
    }

    @Override
    public ASTNode getNode() {
        return myElement.getNode().getFirstChildNode();
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        return another == myElement || (another instanceof FakePsiLiteralExpression && myElement == ((FakePsiLiteralExpression) another).myElement);
    }

    @Override
    public PsiElement getParent() {
        return myElement;
    }

    @Override
    public boolean canNavigate() {
        return super.canNavigate();
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return this;
    }

    @Override
    public void navigate(boolean requestFocus) {
        super.navigate(requestFocus);
    }

    @Override
    public int getStartOffsetInParent() {
        return myTextRangeInParent.getStartOffset();
    }

    @NotNull
    @Override
    public TextRange getTextRange() {
        return myTextRange;
    }

    @Override
    public int getTextLength() {
        return myTextRangeInParent.getLength();
    }

    @Override
    public int getTextOffset() {
        return myTextRange.getStartOffset();
    }

    @NotNull
    @Override
    public TextRange getTextRangeInParent() {
        return myTextRangeInParent;
    }

    @Override
    public PsiFile getContainingFile() {
        return myElement.getContainingFile();
    }

    @NotNull
    @Override
    public String getText() {
        return myElement.getText().substring(myTextRangeInParent.getStartOffset(), myTextRangeInParent.getEndOffset());
    }

    @Nullable
    @Override
    public Icon getIcon(final boolean open) {
        return FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FakePsiLiteralExpression that = (FakePsiLiteralExpression) o;

        return myElement.equals(that.myElement);
    }

    @Override
    public int hashCode() {
        return myElement.hashCode();
    }
}
