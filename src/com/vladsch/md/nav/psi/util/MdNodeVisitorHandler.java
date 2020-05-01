// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface MdNodeVisitorHandler extends MdVisitor<PsiElement> {
    MdNodeVisitor addHandlers(@NotNull Collection<MdVisitHandler> handlers);

    // needed for backward compatibility with extension handler arrays typed as MdVisitHandler<?>[]
    MdNodeVisitor addHandlers(@NotNull MdVisitHandler... handlers);

    MdNodeVisitor addHandlers(@NotNull MdVisitHandler[]... handlers);

    MdNodeVisitor addHandler(@NotNull MdVisitHandler handler);

    void visitNodeOnly(@NotNull PsiElement element);

    void visitChildren(@NotNull PsiElement parent);
}
