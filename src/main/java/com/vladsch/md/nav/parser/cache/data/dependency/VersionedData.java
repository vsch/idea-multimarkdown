// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VersionedData {
    final public long version;
    final public @NotNull Object value;
    final private @NotNull DataDependency[] dependencies;
    final private @NotNull long[] versions;

    public VersionedData(long version, @NotNull Object value, @NotNull DataDependency[] dependencies, @NotNull long[] versions) {
        this.version = version;
        this.value = value;
        this.dependencies = dependencies;
        this.versions = versions;
    }

    void assertNonNullDependencies() {
        for (DataDependency dependency : dependencies) {
            assert dependency != null;
        }
    }

    public boolean isDependent(@Nullable DataDependency dependent) {
        if (dependent == null) return false;
        for (DataDependency item : dependencies) {
            if (item.equals(dependent)) return true;
        }
        return false;
    }

    public boolean isValid() {
        int iMax = dependencies.length;
        for (int i = 0; i < iMax; i++) {
            long version = dependencies[i].getVersion();
            if (version == -1 || version != versions[i]) return false;
        }
        return true;
    }
}
