// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data;

import com.intellij.reference.SoftReference;
import com.vladsch.md.nav.parser.cache.data.dependency.DataDependency;
import com.vladsch.md.nav.parser.cache.data.dependency.DataKeyDependency;
import com.vladsch.md.nav.parser.cache.data.dependency.VersionedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CachedDataSet implements CachedDataHolder {
    @NotNull
    public String getDataKeyWithCacheDescription(@NotNull CachedDataKey<?, ?> dataKey) {
        return "{" + getDataKeyDescription(dataKey) + ", " + getName() + "}";
    }

    @NotNull
    public static String getDataKeyDescription(CachedDataKey<?, ?> dataKey) {
        return dataKey.getName();
    }

    private final @NotNull String myName;
    private final @NotNull HashMap<CachedDataKey<?, ?>, SoftReference<VersionedData>> myCachedDataSet = new HashMap<>();
    private final @NotNull HashMap<CachedDataKey<?, ?>, ReentrantLock> myComputingKeyLocks = new HashMap<>();
    private long myTimeStamp = 0;

    public CachedDataSet(@NotNull String name) {
        myName = name;
    }

    public void clearCachedData() {
        synchronized (myCachedDataSet) {
            myTimeStamp++;
            myCachedDataSet.clear();
            myComputingKeyLocks.clear();
        }
    }

    @Override
    public boolean remove(@NotNull CachedDataKey<?, ?> dataKey) {
        boolean removed;

        synchronized (myCachedDataSet) {
            myTimeStamp++;
            removed = myCachedDataSet.remove(dataKey) != null;
        }

        return removed;
    }

    @Override
    public boolean isDependent(@NotNull CachedDataKey<?, ?> dataKey, @Nullable DataDependency dependency) {
        if (dependency != null) {
            VersionedData data;

            synchronized (myCachedDataSet) {
                SoftReference<VersionedData> reference = myCachedDataSet.get(dataKey);
                data = reference != null ? reference.get() : null;
            }

            if (data != null) {
                return data.isDependent(dependency);
            }
        }
        return false;
    }

    @Override
    public long getVersion(@NotNull CachedDataKey<?, ?> dataKey) {
        VersionedData data;

        Object value = getOrNull(dataKey);

        if (value != null) {
            synchronized (myCachedDataSet) {
                SoftReference<VersionedData> reference = myCachedDataSet.get(dataKey);
                data = reference != null ? reference.get() : null;
            }

            if (data != null) {
                return data.version;
            }
        }

        return -1;
    }

    @Override
    public boolean isEmpty() {
        synchronized (myCachedDataSet) {
            return myCachedDataSet.isEmpty();
        }
    }

    @Override
    public ReentrantLock getKeyLock(@NotNull CachedDataKey<?, ?> dataKey) {
        ReentrantLock lock = myComputingKeyLocks.get(dataKey);
        if (lock == null) {
            synchronized (myComputingKeyLocks) {
                lock = myComputingKeyLocks.computeIfAbsent(dataKey, k -> new ReentrantLock());
            }
        }
        return lock;
    }

    @Nullable
    public Object getOrNull(@NotNull CachedDataKey<?, ?> dataKey) {
        VersionedData data;

        synchronized (myCachedDataSet) {
            SoftReference<VersionedData> reference = myCachedDataSet.get(dataKey);
            data = reference != null ? reference.get() : null;
        }

        return data != null && data.isValid() ? data.value : null;
    }

    /**
     * Get Versioned object for data key
     *
     * @param dataKey data key
     *
     * @return versioned object for the data key in this data set
     */
    @NotNull
    @Override
    public DataKeyDependency getDependency(CachedDataKey<?, ?> dataKey) {
        return new DataKeyDependency(this, dataKey);
    }

    /**
     * Called by transaction manager for newly computed cached data
     * inside a transaction and key lock
     *
     * @param dataKey      dataKey
     * @param value        value
     * @param dependencies dependencies
     * @param versions     versions
     */
    public void setValue(@NotNull CachedDataKey<?, ?> dataKey, @NotNull Object value, @NotNull DataDependency[] dependencies, @NotNull long[] versions) {
        synchronized (myCachedDataSet) {
            myTimeStamp++;
            myCachedDataSet.put(dataKey, new SoftReference<>(new VersionedData(myTimeStamp, value, dependencies, versions)));
        }
    }

    @NotNull
    public String getName() {
        return myName;
    }

    public boolean contains(@NotNull CachedDataKey<?, ?> key) {
        synchronized (myCachedDataSet) {
            return myCachedDataSet.containsKey(key);
        }
    }

    @Override
    public String toString() {
        return "CachedDataSet{ " + getName() + '}';
    }
}
