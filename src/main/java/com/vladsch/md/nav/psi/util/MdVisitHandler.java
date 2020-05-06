// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import com.intellij.psi.PsiElement;
import com.vladsch.flexmark.util.visitor.AstHandler;
import com.vladsch.md.nav.parser.MdParserDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * Node visit handler for specific node type
 */
public class MdVisitHandler<N extends PsiElement> extends AstHandler<N, MdVisitor<N>> implements MdVisitor<PsiElement> {
    /**
     * Create node handler where the implementation class and adapter's parameter are of the same class.
     *
     * @param klass   class of adapter node argument
     * @param adapter adapter used for visit generation
     */
    public MdVisitHandler(@NotNull Class<N> klass, @NotNull MdVisitor<N> adapter) {
        super(MdParserDefinition.getCurrentFactoryClass(klass), adapter);
    }

    public void visit(@NotNull PsiElement element) {
        //noinspection unchecked
        getAdapter().visit((N) element);
    }
}
