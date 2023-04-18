// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.api;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public interface MdTypeFactoryRegistry {
    boolean isTypeFactoryDefined(@NotNull Class<? extends PsiElement> psiElementClass);

    <K extends PsiElement> void addTypeFactory(@NotNull IElementType elementType, @NotNull Class<K> psiElementClass, @NotNull Function<ASTNode, K> factory);

    @NotNull
    Set<Class<? extends PsiElement>> getAllTypeFactoryElements();

    <B extends PsiElement, K extends B> void addTypeFactory(@NotNull IElementType elementType, @NotNull Class<B> psiBaseElementClass, @NotNull Class<K> psiElementClass, @NotNull Function<ASTNode, K> factory);

    <B extends PsiElement, K extends B> void replaceTypeFactory(@NotNull Class<B> oldPsiElementClass, @NotNull Class<K> psiElementClass, @NotNull Function<ASTNode, K> factory);

    @NotNull
    <K extends PsiElement> Class<K> getCurrentFactoryClass(@NotNull Class<? extends K> psiElementClass);

    @Nullable
    <T> Set<Class<T>> getAllTypeFactoryElementsFor(@NotNull Class<T> elementClass);

    @Nullable
    Class<?> getCurrentFactoryClassOrNull(@NotNull Class<?> psiElementClass);
}
