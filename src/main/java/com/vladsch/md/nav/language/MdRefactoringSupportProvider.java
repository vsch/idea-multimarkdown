// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.psi.element.MdRenameElement;
import org.jetbrains.annotations.NotNull;

public class MdRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isAvailable(@NotNull PsiElement context) {
        return true;
    }

    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof MdRenameElement && ((MdRenameElement) element).isInplaceRenameAvailable(context);
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof MdRenameElement && ((MdRenameElement) element).isMemberInplaceRenameAvailable(context);
    }
}
