// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.template;

import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MarkdownTemplateContextType extends TemplateContextType {

    protected MarkdownTemplateContextType(
            @NotNull @NonNls String id,
            @NotNull String presentableName,
            @Nullable Class<? extends TemplateContextType> baseContextType
    ) {
        super(id, presentableName, baseContextType);
    }

    @Override
    public boolean isInContext(@NotNull final PsiFile file, final int offset) {
        if (PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(MdLanguage.INSTANCE)) {
            PsiElement element = file.findElementAt(offset);
            return isInContext(element);
        }

        return false;
    }

    protected abstract boolean isInContext(@Nullable PsiElement element);

    public static class Generic extends MarkdownTemplateContextType {
        public Generic() {
            super("MARKDOWN", "Markdown", EverywhereContextType.class);
        }

        @Override
        protected boolean isInContext(@Nullable PsiElement element) {
            return true;
        }
    }

    public static class BlankLine extends MarkdownTemplateContextType {
        public BlankLine() {
            super("BLANK_LINE", "Blank line", Generic.class);
        }

        @Override
        protected boolean isInContext(@Nullable PsiElement element) {
            return element == null || MdPsiImplUtil.isBlankLine(element);
        }
    }
}
