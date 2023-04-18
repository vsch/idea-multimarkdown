// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.vladsch.flexmark.util.visitor.AstActionHandler;
import com.vladsch.flexmark.util.visitor.AstNode;
import com.vladsch.md.nav.psi.element.MdBlockElementWithChildren;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Configurable element visitor handler which does not know anything about element subclasses
 * while allowing easy configuration of custom visitor for nodes of interest to visit.
 * <p>
 * Usage:
 * {@code
 * myVisitor = new MdNodeVisitor(
 * new MdVisitHandler&lt;&gt;(MdFile.class, this::visit),
 * new MdVisitHandler&lt;&gt;(MdExplicit.class, this::visit),
 * new MdVisitHandler&lt;&gt;(SoftLineBreak.class, this::visit),
 * new MdVisitHandler&lt;&gt;(HardLineBreak.class, this::visit)
 * );
 * }
 * <p>
 * Document doc;
 * myVisitor.visit(doc);
 */
@SuppressWarnings("rawtypes")
public class MdNodeVisitor extends AstActionHandler<MdNodeVisitor, PsiElement, MdVisitor<PsiElement>, MdVisitHandler<PsiElement>> implements MdNodeVisitorHandler {
    protected static final MdVisitHandler[] EMPTY_HANDLERS = new MdVisitHandler[0];
    public static final AstNode<PsiElement> AST_ADAPTER = new AstNode<PsiElement>() {
        @Nullable
        @Override
        public PsiElement getFirstChild(@NotNull PsiElement element) {
            return getNextNonLeaf(element.getNode().getFirstChildNode());
        }

        @Nullable
        @Override
        public PsiElement getNext(@NotNull PsiElement element) {
            return getNextNonLeaf(element.getNode().getTreeNext());
        }
    };

    public static final AstNode<PsiElement> AST_BLOCK_ADAPTER = new AstNode<PsiElement>() {
        @Nullable
        @Override
        public PsiElement getFirstChild(@NotNull PsiElement element) {
            return element instanceof MdBlockElementWithChildren ? getNextNonLeaf(element.getNode().getFirstChildNode()) : null;
        }

        @Nullable
        @Override
        public PsiElement getNext(@NotNull PsiElement element) {
            return getNextNonLeaf(element.getNode().getTreeNext());
        }
    };

    @Nullable
    public static PsiElement getNextNonLeaf(@Nullable ASTNode node) {
        while (node instanceof LeafPsiElement) node = node.getTreeNext();
        return node == null ? null : node.getPsi();
    }

    public MdNodeVisitor(boolean blockOnly) {
        super(blockOnly ? AST_BLOCK_ADAPTER : AST_ADAPTER);
    }

    public MdNodeVisitor(boolean blockOnly, @NotNull MdVisitHandler... handlers) {
        super(blockOnly ? AST_BLOCK_ADAPTER : AST_ADAPTER);
        super.addActionHandlers(handlers);
    }

    public MdNodeVisitor(boolean blockOnly, @NotNull MdVisitHandler[]... handlers) {
        super(blockOnly ? AST_BLOCK_ADAPTER : AST_ADAPTER);
        //noinspection unchecked
        super.addActionHandlers(handlers);
    }

    public MdNodeVisitor(boolean blockOnly, @NotNull Collection<MdVisitHandler> handlers) {
        super(blockOnly ? AST_BLOCK_ADAPTER : AST_ADAPTER);
        addHandlers(handlers);
    }

    public MdNodeVisitor() {
        super(AST_ADAPTER);
    }

    public MdNodeVisitor(MdVisitHandler... handlers) {
        super(AST_ADAPTER);
        super.addActionHandlers(handlers);
    }

    public MdNodeVisitor(MdVisitHandler[]... handlers) {
        super(AST_ADAPTER);
        //noinspection unchecked
        super.addActionHandlers(handlers);
    }

    public MdNodeVisitor(@NotNull Collection<MdVisitHandler> handlers) {
        super(AST_ADAPTER);
        addHandlers(handlers);
    }

    @Override
    final public MdNodeVisitor addHandlers(@NotNull Collection<MdVisitHandler> handlers) {
        return addHandlers(handlers.toArray(EMPTY_HANDLERS));
    }

    // needed for backward compatibility with extension handler arrays typed as MdVisitHandler<?>[]
    @Override
    final public MdNodeVisitor addHandlers(MdVisitHandler... handlers) {
        return super.addActionHandlers(handlers);
    }

    @Override
    final public MdNodeVisitor addHandlers(MdVisitHandler[]... handlers) {
        //noinspection unchecked
        return super.addActionHandlers(handlers);
    }

    @Override
    final public MdNodeVisitor addHandler(@NotNull MdVisitHandler handler) {
        //noinspection unchecked
        return super.addActionHandler(handler);
    }

    @Override
    final public void visit(@NotNull PsiElement element) {
        processNode(element, true, this::visit);
    }

    @Override
    final public void visitNodeOnly(@NotNull PsiElement element) {
        processNode(element, false, this::visit);
    }

    @Override
    final public void visitChildren(@NotNull PsiElement parent) {
        processChildren(parent, this::visit);
    }

    private void visit(@NotNull PsiElement element, @NotNull MdVisitor<PsiElement> handler) {
        handler.visit(element);
    }
}
