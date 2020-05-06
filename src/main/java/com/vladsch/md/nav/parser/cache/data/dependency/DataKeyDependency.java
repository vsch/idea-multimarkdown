// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.dependency;

import com.vladsch.md.nav.parser.cache.data.CachedDataKey;
import com.vladsch.md.nav.parser.cache.data.CachedDataSet;
import org.jetbrains.annotations.NotNull;

/**
 * Versioned dependency
 * <p>
 * NOTE: equals and hashCode for two versioned instances for the same underlying object should result in equivalence between the versioned instances
 * <p>
 * If the underlying dependency is a PsiElement then original psi element should be used for version tracking and comparisons
 * <p>
 * If the underlying dependency is a PsiFile then original original file should be used for version tracking and comparisons
 */
final public class DataKeyDependency implements DataDependency {
    private final @NotNull CachedDataSet myCachedData;
    private final @NotNull CachedDataKey<?, ?> myDataKey;

    public DataKeyDependency(@NotNull CachedDataSet cachedData, @NotNull CachedDataKey<?, ?> dataKey) {
        myCachedData = cachedData;
        myDataKey = dataKey;
    }

    @Override
    public long getVersion() {
        return myCachedData.getVersion(myDataKey);
    }

    public void invalidateDependency() {
        myCachedData.remove(myDataKey);
    }

    public boolean isValid() {
        return myCachedData.getOrNull(myDataKey) != null;
    }

    @NotNull
    public CachedDataSet getCachedData() {
        return myCachedData;
    }

    @NotNull
    public CachedDataKey<?, ?> getDataKey() {
        return myDataKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataKeyDependency)) return false;

        DataKeyDependency that = (DataKeyDependency) o;

        if (!myCachedData.equals(that.myCachedData)) return false;
        return myDataKey.equals(that.myDataKey);
    }

    @Override
    public int hashCode() {
        int result = myCachedData.hashCode();
        result = 31 * result + myDataKey.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DataKeyDependency{" + myDataKey.getName() + ", " + myCachedData.getName() + '}';
    }
}
