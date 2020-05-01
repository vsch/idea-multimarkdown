// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface CachedDataOwner {
    /**
     * Project for the host
     *
     * @return project
     */
    @NotNull
    Project getProject();

    /**
     * Get container for this host
     *
     * @return container
     */
    @NotNull
    CachedDataSet getCachedData();
}
