// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.psi;

import com.intellij.psi.PsiElement;

/**
 * intended to be extended with specific handler function. see {@link PsiVisitHandler}
 *
 * @param <N> subclass of {@link PsiElement}
 * @param <A> subclass of {@link PsiNodeAdaptingVisitor}
 */
public abstract class PsiNodeAdaptingVisitHandler<N extends PsiElement, A extends PsiNodeAdaptingVisitor<N>> {
    protected final Class<? extends N> myClass;
    protected final A myAdapter;

    public PsiNodeAdaptingVisitHandler(Class<? extends N> aClass, A adapter) {
        myClass = aClass;
        myAdapter = adapter;
    }

    public Class<? extends N> getNodeType() {
        return myClass;
    }

    public A getNodeAdapter() {
        return myAdapter;
    }

//    // implement whatever function interface is desired for the adapter
//    @Override
//    public void render(Node node, NodeRendererContext context, HtmlWriter html) {
//        //noinspection unchecked
//        myAdapter.render((N) node, context, html);
//    }
//
//    @Override
//    public void render(Node node, NodeFormatterContext context, MarkdownWriter markdown) {
//        //noinspection unchecked
//        myAdapter.render((N) node, context, markdown);
//    }
//    @Override
//    public void render(Node node, DocxRendererContext context) {
//        //noinspection unchecked
//        myAdapter.render((N) node, context);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PsiNodeAdaptingVisitHandler<?, ?> other = (PsiNodeAdaptingVisitHandler<?, ?>) o;

        if (myClass != other.myClass) return false;
        return myAdapter == other.myAdapter;
    }

    @Override
    public int hashCode() {
        int result = myClass.hashCode();
        result = 31 * result + myAdapter.hashCode();
        return result;
    }
}
