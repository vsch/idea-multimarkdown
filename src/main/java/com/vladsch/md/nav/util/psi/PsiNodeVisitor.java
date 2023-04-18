// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.psi;

import com.intellij.psi.PsiElement;

import java.util.Collection;

/**
 * PsiElement visitor that visits all children by default and allows configuring node handlers to handle specific classes.
 * <p>
 * Can be used to only process certain nodes. If you override a method and want visiting to descend into children,
 * call {@link #visitChildren}.
 */
public class PsiNodeVisitor extends PsiNodeAdaptedVisitor<PsiVisitHandler<? extends PsiElement>> {
    public PsiNodeVisitor(PsiVisitHandler<?>... handlers) {
        super(handlers);
    }

    public PsiNodeVisitor(PsiVisitHandler<?>[]... handlers) {
        super(handlers);
    }

    public PsiNodeVisitor(Collection<PsiVisitHandler<?>> handlers) {
        super(handlers);
    }

    public void visitChildren(PsiElement parent) {
        PsiElement node = parent.getFirstChild();
        while (node != null) {
            // A subclass of this visitor might modify the node, resulting in getNext returning a different node or no
            // node after visiting it. So get the next node before visiting.
            PsiElement next = node.getNextSibling();
            visit(node);
            node = next;
        }
    }

    public void visit(PsiElement node) {
        PsiVisitHandler<?> handler = getHandler(node);
        if (handler != null) {
            handler.visit(node);
        } else {
            visitChildren(node);
        }
    }

    public void visitNodeOnly(PsiElement node) {
        PsiVisitHandler<?> handler = getHandler(node);
        if (handler != null) {
            handler.visit(node);
        }
    }
}
