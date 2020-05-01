// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

interface Bag<T, V> extends Set<T> {
    @NotNull
    V getCountOf(T element);

    int getCountOf(@NotNull Collection<T> elements);

    int getCountOfAll();
}
