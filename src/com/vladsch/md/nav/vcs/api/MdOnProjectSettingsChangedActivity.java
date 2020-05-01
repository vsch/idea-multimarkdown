// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.vcs.api;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface MdOnProjectSettingsChangedActivity extends Disposable {
    /**
     * Perform activity on project settings change
     * <p>
     * On first load settings change call will be done after VCS roots are updated and link resolvers are ready
     *
     * @param project            project
     * @param firstLoad          true if this is the first call after project open settings loaded, false if settings changed after project opened
     * @param completionRunnable
     */
    void projectSettingsChanged(@NotNull Project project, boolean firstLoad, Runnable completionRunnable);
}
