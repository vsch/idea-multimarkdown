// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import com.intellij.psi.PsiElement;
import com.vladsch.flexmark.util.visitor.AstAction;
import org.jetbrains.annotations.NotNull;

/**
 * Node visitor interface
 *
 * @param <N> specific node type
 */
public interface MdVisitor<N extends PsiElement> extends AstAction<N> {
    void visit(@NotNull N element);
}
