// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.text;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.editor.MdPathResolver;
import com.vladsch.md.nav.vcs.GitHubLinkResolver;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdUrlFakePsiElement extends PsiElementBase implements PsiNamedElement, ItemPresentation {
    final PsiElement myElement;
    final String myUrl;

    public MdUrlFakePsiElement(final PsiElement element, final String url) {
        myElement = element;
        myUrl = url;
    }

    public String getUrl() {
        return myUrl;
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public PsiElement getParent() {
        return myElement;
    }

    /**
     * @return null so default navigation handling is not used. To get real containing file use getParent().getContainingFile().
     */
    @Override
    public PsiFile getContainingFile() {
        return null;
    }

    @Nullable
    @Override
    public String getText() {
        return myUrl;
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    @Override
    public void navigate(final boolean requestFocus) {
        MdPathResolver.launchExternalLink(getProject(), myUrl);
    }

    @Nullable
    @Override
    public Icon getIcon(final boolean open) {
        return GitHubLinkResolver.getIcon(myUrl);
    }

    @Nullable
    @Override
    public Icon getIcon(final int flags) {
        return GitHubLinkResolver.getIcon(myUrl);
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    @NotNull
    public Language getLanguage() {
        return Language.ANY;
    }

    @Override
    @NotNull
    public PsiElement[] getChildren() {
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    @Nullable
    public PsiElement getFirstChild() {
        return null;
    }

    @Override
    @Nullable
    public PsiElement getLastChild() {
        return null;
    }

    @Override
    @Nullable
    public PsiElement getNextSibling() {
        return null;
    }

    @Override
    @Nullable
    public PsiElement getPrevSibling() {
        return null;
    }

    @Override
    @Nullable
    public TextRange getTextRange() {
        return null;
    }

    @Override
    public int getStartOffsetInParent() {
        return 0;
    }

    @Override
    public int getTextLength() {
        return 0;
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }

    @Override
    public int getTextOffset() {
        return 0;
    }

    @Override
    @Nullable
    public PsiElement findElementAt(int offset) {
        return null;
    }

    @Override
    @NotNull
    public char[] textToCharArray() {
        return new char[0];
    }

    @Override
    public boolean textContains(char c) {
        return false;
    }

    @Override
    @Nullable
    public ASTNode getNode() {
        return null;
    }

    @Override
    public String getPresentableText() {
        return getName();
    }

    @Override
    protected final Icon getElementIcon(final int flags) {
        return super.getElementIcon(flags);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiManager getManager() {
        final PsiElement parent = getParent();
        return parent != null ? parent.getManager() : null;
    }
}
