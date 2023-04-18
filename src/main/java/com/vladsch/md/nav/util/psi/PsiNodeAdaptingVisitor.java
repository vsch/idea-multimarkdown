// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.psi;

import com.intellij.psi.PsiElement;

/**
 * Intended to be extended with specific visit function(s) and parameters. see {@link PsiVisitor}
 *
 * @param <N> subclass of {@link PsiElement}
 */
public interface PsiNodeAdaptingVisitor<N extends PsiElement> {

}
