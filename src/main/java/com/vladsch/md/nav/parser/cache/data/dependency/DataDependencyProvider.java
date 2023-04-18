// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.dependency;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DataDependencyProvider {
    ExtensionPointName<DataDependencyProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.dataDependencyProvider");
    MdExtensions<DataDependencyProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new DataDependencyProvider[0]);

    /**
     * NOTE: when a provider supplies a non-null value for a class, it will always be used to create dependencies for objects of that exact class
     *
     * @param dependent dependent
     *
     * @return data dependency for the dependent object or null if don't handle such object type
     */
    @Nullable
    DataDependency getDependency(@NotNull Object dependent);
}
