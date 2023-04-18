// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.parser.api.MdTypeFactoryRegistry;
import com.vladsch.md.nav.psi.api.MdTypeFactory;
import com.vladsch.md.nav.psi.element.MdCompositeImpl;
import com.vladsch.md.nav.psi.util.MdTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class MdTypeFactoryRegistryImpl implements MdTypeFactoryRegistry {
    private static final Logger LOG = getInstance("com.vladsch.md.nav.parser");

    private static final MdTypeFactory[] ourFactories = MdTypeFactory.EXTENSIONS.getValue();

    HashMap<Class<? extends PsiElement>, IElementType> myElementTypeMap = new HashMap<>();
    HashMap<IElementType, Function<ASTNode, ? extends PsiElement>> myTypeFactoryMap = new HashMap<>();
    HashMap<Class<? extends PsiElement>, Class<? extends PsiElement>> myReplacementMap = new HashMap<>();

    public MdTypeFactoryRegistryImpl() {
        // load basic types
        MdTypes.addTypeFactories(this);

        // now handle extensions
        for (MdTypeFactory factory : ourFactories) {
            factory.addTypeFactories(this);
        }
    }

    @Override
    public boolean isTypeFactoryDefined(@NotNull final Class<? extends PsiElement> psiElementClass) {
        return myElementTypeMap.containsKey(psiElementClass);
    }

    @NotNull
    @Override
    public Set<Class<? extends PsiElement>> getAllTypeFactoryElements() {
        return myElementTypeMap.keySet();
    }

    @NotNull
    @Override
    public <T> Set<Class<T>> getAllTypeFactoryElementsFor(@NotNull Class<T> elementClass) {
        HashSet<Class<T>> classes = new HashSet<>();
        Class<?> resolved = getCurrentFactoryClassOrNull(elementClass);
        if (resolved != null) {
            //noinspection unchecked
            classes.add((Class<T>) resolved);
        } else {
            for (Class<?> klass : myElementTypeMap.keySet()) {
                if (elementClass.isAssignableFrom(klass)) {
                    //noinspection unchecked
                    classes.add((Class<T>) klass);
                }
            }
        }
        return classes;
    }

    @Override
    public <B extends PsiElement, K extends B> void addTypeFactory(@NotNull IElementType elementType, @NotNull Class<B> psiBaseElementClass, @NotNull Class<K> psiElementClass, @NotNull Function<ASTNode, K> factory) {
        addTypeFactory(elementType, psiElementClass, factory);

        IElementType existingType = myElementTypeMap.get(psiBaseElementClass);
        if (existingType != null && existingType != elementType) {
            throw new IllegalStateException("IElementType/PsiClass mapping must be one to one or must use replaceTypeFactory for " + elementType + "base psi class " + psiBaseElementClass.getSimpleName());
        }

        addReplacedClass(psiBaseElementClass, psiElementClass);
        myElementTypeMap.put(psiBaseElementClass, elementType);
    }

    @Override
    public <K extends PsiElement> void addTypeFactory(@NotNull IElementType elementType, @NotNull Class<K> psiElementClass, @NotNull Function<ASTNode, K> factory) {
        myTypeFactoryMap.put(elementType, factory);

        IElementType existingType = myElementTypeMap.get(psiElementClass);
        if (existingType != null && existingType != elementType) {
            throw new IllegalStateException("Element Type/PsiClass mapping must be one to one or must use replaceTypeFactory for " + elementType + "base psi class " + psiElementClass.getSimpleName());
        }

        myElementTypeMap.put(psiElementClass, elementType);
    }

    @Override
    public <B extends PsiElement, K extends B> void replaceTypeFactory(@NotNull Class<B> oldPsiElementClass, @NotNull Class<K> psiElementClass, @NotNull Function<ASTNode, K> factory) {
        // resolve base class to final remapped class
        Class<? extends PsiElement> resolvedOldPsiElementClass = addReplacedClass(oldPsiElementClass, psiElementClass);

        IElementType elementType = myElementTypeMap.get(resolvedOldPsiElementClass);
        if (elementType == null) throw new IllegalStateException("IElementType for psiClass: " + resolvedOldPsiElementClass + " is not defined");
        addTypeFactory(elementType, psiElementClass, factory);
    }

    private <B extends PsiElement, K extends B> Class<? extends PsiElement> addReplacedClass(@NotNull Class<B> oldPsiElementClass, @NotNull Class<K> psiElementClass) {
        Class<? extends PsiElement> resolvedOldPsiElementClass = oldPsiElementClass;
        if (oldPsiElementClass != psiElementClass) {
            while (myReplacementMap.containsKey(resolvedOldPsiElementClass)) {
                Class<? extends PsiElement> oldResolvedOldPsiElementClass = myReplacementMap.get(resolvedOldPsiElementClass);

                // remap to new class
                myReplacementMap.put(resolvedOldPsiElementClass, psiElementClass);
                resolvedOldPsiElementClass = oldResolvedOldPsiElementClass;
            }

            myReplacementMap.put(resolvedOldPsiElementClass, psiElementClass);
        }
        return resolvedOldPsiElementClass;
    }

    @NotNull
    @Override
    public <K extends PsiElement> Class<K> getCurrentFactoryClass(@NotNull Class<? extends K> psiElementClass) {
        //noinspection unchecked
        Class<K> resolvedClass = (Class<K>) myReplacementMap.getOrDefault(psiElementClass, psiElementClass);

        if (!PsiFile.class.isAssignableFrom(resolvedClass)) {
            // check if this class has an element type defined for this class is defined
            IElementType elementType = myElementTypeMap.get(resolvedClass);
            if (elementType == null) LOG.warn("Handler should not be for " + resolvedClass.getSimpleName() + ".class, no IElementType is defined for this class");
        }

        return resolvedClass;
    }

    @Nullable
    @Override
    public Class<?> getCurrentFactoryClassOrNull(@NotNull Class<?> psiElementClass) {
        return myReplacementMap.get(psiElementClass);
    }

    public PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        final Function<ASTNode, ? extends PsiElement> function = myTypeFactoryMap.get(type);
        if (function != null) {
            return function.apply(node);
        }

        LOG.warn("Markdown IElementType: " + type.toString() + " has no dedicated type factory. Creating MdCompositeImpl");
//        System.out.println("Markdown IElementType: " + type.toString() + " has no dedicated type factory. Mapped to MdCompositeImpl");
        return new MdCompositeImpl(node);
    }
}
