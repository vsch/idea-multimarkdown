// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.looping;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.vladsch.flexmark.tree.iteration.IterationConditions;
import com.vladsch.flexmark.tree.iteration.TreeIterator;
import com.vladsch.flexmark.tree.iteration.ValueIterationAdapter;
import com.vladsch.flexmark.tree.iteration.ValueIterationAdapterImpl;
import com.vladsch.flexmark.tree.iteration.ValueIterationFilter;
import com.vladsch.md.nav.psi.util.MdTokenSets;
import com.vladsch.plugin.util.psi.PsiIterator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class MdPsiIterator<T extends PsiElement> extends PsiIterator<T> {
    public MdPsiIterator(@NotNull final PsiElement element, @NotNull ValueIterationAdapter<? super PsiElement, T> adapter, @NotNull TreeIterator<PsiElement> treeIterator) {
        super(element, adapter, treeIterator);
    }

    // *******************************************************
    //
    // Need Subclass Constructors
    //
    // *******************************************************

    @NotNull
    public MdPsiIterator<T> getModifiedCopy(final PsiElement element, final ValueIterationAdapter<? super PsiElement, T> adapter, final TreeIterator<PsiElement> treeIterator) {
        return new MdPsiIterator<>(element, adapter, treeIterator);
    }

    @NotNull
    public <F extends PsiElement> MdPsiIterator<F> getModifiedCopyF(final PsiElement element, final ValueIterationAdapter<? super PsiElement, F> adapter, final TreeIterator<PsiElement> treeIterator) {
        return new MdPsiIterator<>(element, adapter, treeIterator);
    }

    // *******************************************************
    //
    // Need Overrides with cast to sub-class
    //
    // *******************************************************

    @NotNull
    @Override
    public MdPsiIterator<T> reversed() {
        return (MdPsiIterator<T>) super.reversed();
    }

    @NotNull
    @Override
    public MdPsiIterator<T> recursive() {
        return (MdPsiIterator<T>) super.recursive();
    }

    @NotNull
    @Override
    public MdPsiIterator<T> nonRecursive() {
        return (MdPsiIterator<T>) super.nonRecursive();
    }

    @NotNull
    @Override
    public MdPsiIterator<T> recursive(final boolean recursive) {
        return (MdPsiIterator<T>) super.recursive(recursive);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> nonRecursive(final boolean nonRecursive) {
        return (MdPsiIterator<T>) super.nonRecursive(nonRecursive);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> recurse(@NotNull final Predicate<? super PsiElement> predicate) {
        return (MdPsiIterator<T>) super.recurse(predicate);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> recurse(@NotNull final Class clazz) {
        return (MdPsiIterator<T>) super.recurse(clazz);
    }

    @NotNull
    @Override
    public <F extends PsiElement> MdPsiIterator<T> recurse(@NotNull final Class<F> clazz, @NotNull final Predicate<? super F> predicate) {
        return (MdPsiIterator<T>) super.recurse(clazz, predicate);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> noRecurse(@NotNull final Predicate<? super PsiElement> predicate) {
        return (MdPsiIterator<T>) super.noRecurse(predicate);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> noRecurse(@NotNull final Class clazz) {
        return (MdPsiIterator<T>) super.noRecurse(clazz);
    }

    @NotNull
    @Override
    public <F extends PsiElement> MdPsiIterator<T> noRecurse(@NotNull final Class<F> clazz, @NotNull final Predicate<? super F> predicate) {
        return (MdPsiIterator<T>) super.noRecurse(clazz, predicate);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> filterFalse() {
        return (MdPsiIterator<T>) super.filterFalse();
    }

    @NotNull
    @Override
    public MdPsiIterator<T> aborted() {
        return (MdPsiIterator<T>) super.aborted();
    }

    @NotNull
    @Override
    public MdPsiIterator<T> filterOut(@NotNull final Predicate<? super PsiElement> predicate) {
        return (MdPsiIterator<T>) super.filterOut(predicate);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> filterOut(@NotNull final Class clazz) {
        return (MdPsiIterator<T>) super.filterOut(clazz);
    }

    @NotNull
    @Override
    public <F extends PsiElement> MdPsiIterator<T> filterOut(@NotNull final Class<F> clazz, @NotNull final Predicate<? super F> predicate) {
        return (MdPsiIterator<T>) super.filterOut(clazz, predicate);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> filter(@NotNull final Predicate<? super PsiElement> predicate) {
        return (MdPsiIterator<T>) super.filter(predicate);
    }

    @NotNull
    @Override
    public MdPsiIterator<T> acceptFilter(@NotNull final ValueIterationFilter<? super T> filter) {
        return (MdPsiIterator<T>) super.acceptFilter(filter);
    }

    // *******************************************************
    //
    // Mapping Functions
    //
    // *******************************************************

    @NotNull
    @Override
    public <F extends PsiElement> MdPsiIterator<F> filter(@NotNull final Class<F> clazz) {
        return (MdPsiIterator<F>) super.filter(clazz);
    }

    @NotNull
    @Override
    public <F extends PsiElement> MdPsiIterator<F> filter(@NotNull final Class<F> clazz, @NotNull final Predicate<? super F> predicate) {
        return (MdPsiIterator<F>) super.filter(clazz, predicate);
    }

    @NotNull
    @Override
    public <F extends PsiElement> MdPsiIterator<F> adapt(@NotNull final Function<? super T, F> adapter) {
        return (MdPsiIterator<F>) super.adapt(adapter);
    }

    @NotNull
    @Override
    public <F extends PsiElement> MdPsiIterator<F> adapt(@NotNull final ValueIterationAdapter<? super T, F> adapter) {
        return (MdPsiIterator<F>) super.adapt(adapter);
    }

    // *******************************************************
    //
    // PsiLooping specific
    //
    // *******************************************************

    @NotNull
    public MdPsiIterator<T> recurse(@NotNull TokenSet tokenSet) {
        return (MdPsiIterator<T>) super.recurse(tokenSet);
    }

    @NotNull
    public MdPsiIterator<T> filterOut(@NotNull TokenSet tokenSet) {
        return (MdPsiIterator<T>) super.filterOut(tokenSet);
    }

    @NotNull
    public MdPsiIterator<T> filter(@NotNull TokenSet tokenSet) {
        return (MdPsiIterator<T>) super.filter(tokenSet);
    }

    @NotNull
    public MdPsiIterator<T> filterOutLeafPsi() {
        return (MdPsiIterator<T>) super.filterOutLeafPsi();
    }

    // *******************************************************
    //
    // Markdown Navigator specific
    //
    // *******************************************************

    @NotNull
    public MdPsiIterator<T> filterOutWhitespace() {
        return filterOut(MdTokenSets.WHITESPACE_EOL_BLANK_LINE_SET);
    }

    @NotNull
    public MdPsiIterator<T> filterBlockElements() {
        return filter(MdTokenSets.BLOCK_ELEMENT_SET);
    }

    @NotNull
    public MdPsiIterator<T> filterCanContainTasksOrHeaders() {
        return filter(MdTokenSets.CAN_CONTAIN_TASKS_OR_HEADERS);
    }

    @NotNull
    public MdPsiIterator<T> recurseCanContainTasksOrHeaders() {
        return recurse(MdTokenSets.CAN_CONTAIN_TASKS_OR_HEADERS);
    }

    @NotNull
    public MdPsiIterator<T> filterOutNonTextBlocks() {
        return filterOut(MdTokenSets.NON_TEXT_BLOCK_ELEMENTS);
    }

    // *******************************************************
    //
    // Static Factories
    //
    // *******************************************************

    public static MdPsiIterator<PsiElement> of(final @NotNull PsiElement element, final @NotNull TreeIterator<PsiElement> treeIterator) {
        return new MdPsiIterator<>(element, ValueIterationAdapterImpl.of(), treeIterator);
    }

    public static MdPsiIterator<PsiElement> of(final @NotNull PsiElement element, final @NotNull IterationConditions<PsiElement> constraints) {
        return of(element, new TreeIterator<>(constraints));
    }

    public static MdPsiIterator<PsiElement> of(final @NotNull PsiElement element, final @NotNull IterationConditions<PsiElement> constraints, final @NotNull Predicate<? super PsiElement> filter) {
        return of(element, new TreeIterator<>(constraints, filter));
    }

    public static MdPsiIterator<PsiElement> of(final @NotNull PsiElement element, final @NotNull IterationConditions<PsiElement> constraints, final @NotNull Predicate<? super PsiElement> filter, final @NotNull Predicate<? super PsiElement> recursion) {
        return of(element, new TreeIterator<>(constraints, filter, recursion));
    }
}
