// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A Map wrapper which understands PsiElement equivalence for keys and treats equivalent PsiElement keys as if they are the same key
 *
 * @param <K> key type
 * @param <V> value type
 */
public class PsiMap<K extends PsiElement, V> implements Map<K, V> {
    @NotNull
    public static <K extends PsiElement, V> PsiMap<K, V> toPsiMap(@NotNull Map<? extends K, ? extends V> other) {
        //noinspection unchecked
        return other instanceof PsiMap<?, ?> ? (PsiMap<K, V>) other : new PsiMap<K, V>(other, HashMap::new);
    }

    @NotNull
    public static <K extends PsiElement, V> PsiMap<K, V> toLinkedPsiMap(@NotNull Map<? extends K, ? extends V> other) {
        return new PsiMap<K, V>(other, LinkedHashMap::new);
    }

    @NotNull
    public static <K extends PsiElement, V> PsiMap<K, V> toPsiMap(@NotNull Collection<Map.Entry<? extends K, ? extends V>> other) {
        return new PsiMap<>(other, HashMap::new);
    }

    @NotNull
    public static <K extends PsiElement, V> PsiMap<K, V> toLinkedPsiMap(@NotNull Collection<Map.Entry<? extends K, ? extends V>> other) {
        return new PsiMap<>(other, LinkedHashMap::new);
    }

    private final @NotNull Map<K, V> myMap;
    private final @NotNull Supplier<Map<K, V>> mySupplier;

    public PsiMap(@NotNull Supplier<Map<K, V>> supplier) {
        mySupplier = supplier;
        myMap = mySupplier.get();
    }

    public PsiMap(@NotNull PsiMap<? extends K, ? extends V> other) {
        //noinspection unchecked
        this(other.myMap, () -> (Map<K, V>) other.mySupplier.get());
    }

    public PsiMap(@NotNull Map<? extends K, ? extends V> other, @NotNull Supplier<Map<K, V>> supplier) {
        this(supplier);
        myMap.putAll(other);
    }

    public PsiMap(@NotNull Collection<Map.Entry<? extends K, ? extends V>> other, @NotNull Supplier<Map<K, V>> supplier) {
        this(supplier);
        putAll(other);
    }

    // clear() { return myMap.;
    // containsKey(Object key) { return myMap.;
    // containsValue(Object value) { return myMap.;
    // entrySet() { return myMap.;
    // get(Object key) { return myMap.;
    // isEmpty() { return myMap.;
    // keySet() { return myMap.;
    // put(K key, V value) { return myMap.;
    // putAll(Map<? extends K, ? extends V> m) { return myMap.;
    // remove(Object key) { return myMap.;
    // size() { return myMap.;
    // values() { return myMap.;

    @Nullable
    public <P> P getKey(P key) {
        if (!(key instanceof PsiElement)) return key;

        //noinspection SuspiciousMethodCalls
        if (myMap.containsKey(key)) {
            return key;
        }

        PsiElement originalKey = MdPsiImplUtil.originalPsi((PsiElement) key);

        if (!(key instanceof PsiFile)) {
            // see if contains equivalent key
            for (Map.Entry<K, V> entry : myMap.entrySet()) {
                K entryKey = entry.getKey();
                if (originalKey.isEquivalentTo(entryKey)) {
                    //noinspection unchecked
                    return (P) entryKey;
                }
            }
        }

        //noinspection unchecked
        return (P) MdPsiImplUtil.originalPsi((PsiElement) key);
    }

    @Override
    public boolean containsKey(Object key) {
        return myMap.containsKey(getKey(key));
    }

    @Override
    @Nullable
    public V get(Object key) {
        return myMap.get(getKey(key));
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        return myMap.put(getKey(key), value);
    }

    @Override
    public V remove(Object key) {
        return myMap.remove(getKey(key));
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            myMap.put(getKey(entry.getKey()), entry.getValue());
        }
    }

    public void putAll(@NotNull Collection<Map.Entry<? extends K, ? extends V>> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m) {
            myMap.put(getKey(entry.getKey()), entry.getValue());
        }
    }

    // pass through methods
    @Override
    public boolean containsValue(Object value) {
        return myMap.containsValue(value);
    }

    @Override
    public int size() {return myMap.size();}

    @Override
    public boolean isEmpty() {return myMap.isEmpty();}

    @Override
    public void clear() {myMap.clear();}

    @NotNull
    @Override
    public Set<K> keySet() {return myMap.keySet();}

    @NotNull
    @Override
    public Collection<V> values() {return myMap.values();}

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {return myMap.entrySet();}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PsiMap)) return false;

        PsiMap<?, ?> map = (PsiMap<?, ?>) o;

        return myMap.equals(map.myMap);
    }

    @Override
    public int hashCode() {
        return myMap.hashCode();
    }

    @Override
    public String toString() {
        return "PsiMap{" + myMap.toString() + '}';
    }
}
