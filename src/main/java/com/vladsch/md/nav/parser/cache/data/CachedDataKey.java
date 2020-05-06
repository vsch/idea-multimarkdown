// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data;

import com.vladsch.md.nav.parser.cache.data.transaction.CachedTransactionContext;
import org.jetbrains.annotations.NotNull;

public abstract class CachedDataKey<H extends CachedDataOwner, T> {
    protected final @NotNull String name;

    /**
     * Creates a CachedDataKey with non-null data value and factory
     * <p>
     * Use this constructor to ensure that factory is never called with null data holder value
     *
     * @param name key name
     */
    public CachedDataKey(@NotNull String name) {
        this.name = name;
    }

    /**
     * Validate data in this cached data key
     *
     * @return true if data is valid
     */
    public abstract boolean isValid(@NotNull T value);

    @NotNull
    public abstract T compute(@NotNull CachedTransactionContext<H> context);

    @NotNull
    public String getName() {
        return name;
    }

    public boolean validate(@NotNull Object value) {
        //noinspection unchecked
        return isValid((T) value);
    }

    @Override
    public String toString() {
        // factory applied to null in constructor, no sense doing it again here
        return "CachedDataKey(" + getName() + ")";
    }
}
