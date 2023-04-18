// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdVerbatimLanguageImpl extends ASTWrapperPsiElement implements MdVerbatimLanguage {
    public MdVerbatimLanguageImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public String getVerbatimLanguage() {
        if (!isValid()) return null;

        //final PsiElement element = MultiMarkdownPsiImplUtil.findChildByType(this, MultiMarkdownTypes.VERBATIM_LANG);
        return getText().trim();
    }

    @Nullable
    @Override
    public PsiElement setVerbatimLanguage(@Nullable String verbatimLanguageName) {
        if (!isValid()) return null;

        //final PsiElement element = MultiMarkdownPsiImplUtil.findChildByType(this, MultiMarkdownTypes.VERBATIM_LANG);
        return MdPsiImplUtil.setLanguage((MdVerbatim) getParent(), verbatimLanguageName, null);
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                if (!isValid()) return null;
                return "Code fence language";
            }

            @Nullable
            @Override
            public String getLocationString() {
                if (!isValid()) return null;
                // create a shortened version that is still good to look at
                return MdPsiImplUtil.truncateStringForDisplay(getText(), 50, false, true, true);
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return MdIcons.getDocumentIcon();
            }
        };
    }
}
