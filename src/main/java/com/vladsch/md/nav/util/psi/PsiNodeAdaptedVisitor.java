// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.psi;

import com.intellij.psi.PsiElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Intended to be extended by specific type of node visitor
 *
 * @param <H> subclass of {@link PsiNodeAdaptingVisitHandler}
 */
public abstract class PsiNodeAdaptedVisitor<H extends PsiNodeAdaptingVisitHandler<?, ?>> {
    private final Map<Class<?>, H> myCustomHandlersMap = new HashMap<Class<?>, H>();

    // Usage:
    //myVisitor = new NodeVisitor(
    //        new PsiNodeAdaptingVisitHandler<>(Text.class, this::visit),
    //        new PsiNodeAdaptingVisitHandler<>(HtmlEntity.class, this::visit),
    //        new PsiNodeAdaptingVisitHandler<>(SoftLineBreak.class, this::visit),
    //        new PsiNodeAdaptingVisitHandler<>(HardLineBreak.class, this::visit)
    //);
    public PsiNodeAdaptedVisitor(H... handlers) {
        addHandlers(handlers);
    }

    public PsiNodeAdaptedVisitor(H[]... handlers) {
        addHandlers(handlers);
    }

    public PsiNodeAdaptedVisitor(Collection<H> handlers) {
        addHandlers(handlers);
    }

    public PsiNodeAdaptedVisitor<H> addHandlers(H... handlers) {
        for (H handler : handlers) {
            myCustomHandlersMap.put(handler.getNodeType(), handler);
        }
        return this;
    }

    public PsiNodeAdaptedVisitor<H> addHandlers(H[]... handlers) {
        for (H[] moreVisitors : handlers) {
            for (H handler : moreVisitors) {
                myCustomHandlersMap.put(handler.getNodeType(), handler);
            }
        }
        return this;
    }

    public PsiNodeAdaptedVisitor<H> addHandlers(Collection<H> handlers) {
        for (H handler : handlers) {
            myCustomHandlersMap.put(handler.getNodeType(), handler);
        }
        return this;
    }

    public H getHandler(PsiElement node) {
        return myCustomHandlersMap.get(node.getClass());
    }

    public H getHandler(Class<?> nodeClass) {
        return myCustomHandlersMap.get(nodeClass);
    }

    public Set<Class<?>> getNodeClasses() {
        return myCustomHandlersMap.keySet();
    }
}
