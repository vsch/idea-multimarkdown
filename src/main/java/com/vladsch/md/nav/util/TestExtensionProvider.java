// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import com.intellij.openapi.extensions.ExtensionPointName;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

public class TestExtensionProvider {
    HashMap<String, Supplier<?>> extensionMap = new HashMap<>();

    public <T> TestExtensionProvider addExtensions(ExtensionPointName<T> extensionPointName, Supplier<T[]> supplier) {
        extensionMap.put(extensionPointName.getName(), supplier);
        return this;
    }

    @NotNull
    public <T> T[] getExtensions(String name, T[] defaultValue) {
        Supplier<?> supplier = extensionMap.get(name);
        if (supplier == null)
            return defaultValue;
        else
            //noinspection unchecked
            return (T[]) supplier.get();
    }
}
