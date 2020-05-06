// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.dependency;

import com.vladsch.md.nav.parser.cache.data.transaction.LogIndenter;
import org.jetbrains.annotations.NotNull;

public interface DataDependencyManager extends LogIndenter, DataDependencyProvider {
    @NotNull
    @Override
    DataDependency getDependency(@NotNull Object dependent);
}
