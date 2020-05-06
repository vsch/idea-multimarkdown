// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.psi;

import com.intellij.psi.PsiElement;

public class PsiVisitHandler<N extends PsiElement> extends PsiNodeAdaptingVisitHandler<N, PsiVisitor<N>> implements PsiVisitor<N> {
    public PsiVisitHandler(Class<? extends N> aClass, PsiVisitor<N> adapter) {
        super(aClass, adapter);
    }

    @Override
    public void visit(PsiElement node) {
        //noinspection unchecked
        myAdapter.visit((N) node);
    }
}
