// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data;

import com.vladsch.md.nav.parser.cache.data.dependency.DataDependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.ReentrantLock;

public interface CachedDataHolder {
    /**
     * Invalidate the key if it was valid
     *
     * @param dataKey key
     *
     * @return return true if key was valid before
     */
    boolean remove(@NotNull CachedDataKey<?, ?> dataKey);

    /**
     * Test if key depends on dependent
     *
     * @param dataKey    key
     * @param dependency dependent
     *
     * @return true if it does, null dependents and keys which are not in the data set always report false
     */
    boolean isDependent(CachedDataKey<?, ?> dataKey, @Nullable DataDependency dependency);

    long getVersion(@NotNull CachedDataKey<?, ?> dataKey);

    /**
     * Test if cached data set is empty
     *
     * @return true if empty
     */
    boolean isEmpty();

    ReentrantLock getKeyLock(@NotNull CachedDataKey<?, ?> dataKey);

    /**
     * @param dataKey data key
     *
     * @return valid value or null
     */
    @Nullable
    Object getOrNull(@NotNull CachedDataKey<?, ?> dataKey);

    @NotNull
    DataDependency getDependency(CachedDataKey<?, ?> dataKey);
}
