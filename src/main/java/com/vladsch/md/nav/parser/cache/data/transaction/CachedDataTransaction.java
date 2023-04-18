// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.transaction;

import com.vladsch.md.nav.parser.cache.data.CachedDataKey;
import com.vladsch.md.nav.parser.cache.data.CachedDataOwner;
import com.vladsch.md.nav.parser.cache.data.CachedDataSet;
import com.vladsch.md.nav.parser.cache.data.dependency.DataDependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.ReentrantLock;

abstract class CachedDataTransaction<H extends CachedDataOwner, T> implements CachedTransactionContext<H> {
    final @NotNull H myDataOwner;
    final @NotNull CachedDataKey<H, T> myDataKey;
    final @NotNull CachedDataSet myCachedData;
    final @NotNull StackTraceElement[] myStackTraceElements;

    public CachedDataTransaction(@NotNull H dataOwner, @NotNull CachedDataSet cachedData, @NotNull CachedDataKey<H, T> dataKey) {
        myDataOwner = dataOwner;
        myCachedData = cachedData;
        myDataKey = dataKey;
        myStackTraceElements = Thread.currentThread().getStackTrace();
    }

    @NotNull
    @Override
    public H getDataOwner() {
        return myDataOwner;
    }

    @NotNull
    public StackTraceElement[] getStackTraceElements() {
        return myStackTraceElements;
    }

    @NotNull
    public CachedDataKey<H, T> getDataKey() {
        return myDataKey;
    }

    // pass through to cached data

    @Override
    public boolean remove(@NotNull CachedDataKey<?, ?> dataKey) {
        return myCachedData.remove(dataKey);
    }

    @Override
    public long getVersion(@NotNull CachedDataKey<?, ?> dataKey) {
        return myCachedData.getVersion(dataKey);
    }

    @Override
    public ReentrantLock getKeyLock(@NotNull CachedDataKey<?, ?> dataKey) {
        return myCachedData.getKeyLock(dataKey);
    }

    @NotNull
    @Override
    public DataDependency getDependency(CachedDataKey<?, ?> dataKey) {
        return myCachedData.getDependency(dataKey);
    }

    @Override
    public boolean isDependent(CachedDataKey<?, ?> dataKey, @Nullable DataDependency dependency) {
        return myCachedData.isDependent(dataKey, dependency);
    }

    @Override
    public boolean isEmpty() {
        return myCachedData.isEmpty();
    }

    @Nullable
    @Override
    public Object getOrNull(@NotNull CachedDataKey<?, ?> dataKey) {
        return myCachedData.getOrNull(dataKey);
    }
}
