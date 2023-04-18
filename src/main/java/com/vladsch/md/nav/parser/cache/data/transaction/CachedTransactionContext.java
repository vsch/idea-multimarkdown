// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.transaction;

import com.vladsch.md.nav.parser.cache.data.CachedDataHolder;
import com.vladsch.md.nav.parser.cache.data.CachedDataKey;
import com.vladsch.md.nav.parser.cache.data.CachedDataOwner;
import com.vladsch.md.nav.parser.cache.data.dependency.DataDependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CachedTransactionContext<H extends CachedDataOwner> extends CachedDataHolder {
    @Override
    boolean isDependent(CachedDataKey<?, ?> dataKey, @Nullable DataDependency dependency);

    @Override
    boolean isEmpty();

    @Nullable
    @Override
    Object getOrNull(@NotNull CachedDataKey<?, ?> dataKey);

    void addDependency(@NotNull Object dependency);

    @NotNull
    H getDataOwner();

    @NotNull
    <T> T get(@NotNull CachedDataKey<H, T> dataKey);
}
