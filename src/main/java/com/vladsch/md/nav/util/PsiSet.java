// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A Set wrapper which understands PsiElement equivalence for keys and treats equivalent PsiElement keys as if they are the same key
 *
 * @param <K> key type
 */
public class PsiSet<K extends PsiElement> implements Set<K> {
    @NotNull
    public static <K extends PsiElement> PsiSet<K> toPsiSet(@NotNull Collection<K> other) {
        return other instanceof PsiSet<?> ? (PsiSet<K>) other : new PsiSet<>(other, HashSet::new);
    }

    @NotNull
    public static <K extends PsiElement> PsiSet<K> toLinkedPsiSet(@NotNull Collection<K> other) {
        return new PsiSet<>(other, LinkedHashSet::new);
    }

    private final @NotNull Set<K> mySet;
    private final @NotNull Supplier<Set<K>> mySupplier;

    public PsiSet(@NotNull Supplier<Set<K>> supplier) {
        mySupplier = supplier;
        mySet = mySupplier.get();
    }

    public PsiSet(@NotNull Collection<? extends K> other, @NotNull Supplier<Set<? extends K>> supplier) {
        //noinspection unchecked
        this(() -> (Set<K>) supplier.get());
        mySet.addAll(other);
    }

    @SuppressWarnings({ "unused", "CopyConstructorMissesField" })
    public PsiSet(@NotNull PsiSet<? extends K> other) {
        this(other.mySet, other.mySupplier::get);
        mySet.addAll(other.mySet);
    }

    // add(E e);
    // addAll(Collection<? extends E> c);
    // clear() { return myMap.;
    // contains(Object o);
    // containsAll(Collection<?> c);
    // isEmpty() { return myMap.;
    // iterator();
    // remove(Object o);
    // removeAll(Collection<?> c);
    // retainAll(Collection<?> c);
    // size() { return myMap.;
    // toArray();
    // toArray(T[] a);

    @Nullable
    public <P> P getKey(P key) {
        if (!(key instanceof PsiElement)) return key;

        //noinspection SuspiciousMethodCalls
        if (mySet.contains(key)) {
            return key;
        }

        PsiElement originalKey = MdPsiImplUtil.originalPsi((PsiElement) key);

        if (!(key instanceof PsiFile)) {
            // see if contains equivalent key
            for (K entry : mySet) {
                if (originalKey.isEquivalentTo(entry)) {
                    //noinspection unchecked
                    return (P) entry;
                }
            }
        }

        //noinspection unchecked
        return (P) MdPsiImplUtil.originalPsi((PsiElement) key);
    }

    @Override
    public boolean contains(Object o) {
        return mySet.contains(getKey(o));
    }

    @Override
    public boolean add(K k) {return mySet.add(getKey(k));}

    @Override
    public boolean remove(Object o) {return mySet.remove(getKey(o));}

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            //noinspection SuspiciousMethodCalls
            if (!mySet.contains(getKey(o))) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends K> c) {
        boolean changed = false;
        for (K k : c) {
            if (mySet.add(getKey(k))) changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        ArrayList<K> toRemove = null;
        for (Object k : c) {
            Object key = getKey(k);
            //noinspection SuspiciousMethodCalls
            if (mySet.contains(key)) {
                if (toRemove == null) toRemove = new ArrayList<>();
                //noinspection unchecked
                toRemove.add((K) key);
            }
        }

        if (toRemove != null) {
            for (K key : toRemove) {
                mySet.remove(key);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            //noinspection SuspiciousMethodCalls
            if (mySet.remove(getKey(o))) changed = true;
        }
        return changed;
    }

    // pass through methods
    @NotNull
    @Override
    public Iterator<K> iterator() {return mySet.iterator();}

    @NotNull
    @Override
    public Object[] toArray() {return mySet.toArray();}

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {return mySet.toArray(a);}

    @Override
    public int size() {return mySet.size();}

    @Override
    public boolean isEmpty() {return mySet.isEmpty();}

    @Override
    public void clear() {mySet.clear();}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PsiSet)) return false;

        PsiSet<?> other = (PsiSet<?>) o;

        return mySet.equals(other.mySet);
    }

    @Override
    public int hashCode() {
        return mySet.hashCode();
    }

    @Override
    public String toString() {
        return "PsiSet{" + Arrays.toString(mySet.toArray()) + '}';
    }
}
