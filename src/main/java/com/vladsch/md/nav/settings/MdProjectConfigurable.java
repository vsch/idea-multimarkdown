// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MdProjectConfigurable<F extends MdSettableFormBase<MdRenderingProfileHolder>> {
    @NotNull final protected Project myProject;
    @NotNull final protected RenderingProfileSynchronizer myProfileSynchronizer;
    private boolean myFirstReset;
    @Nullable protected F myForm = null;

    public MdProjectConfigurable(@NotNull Project project) {
        myProject = project;
        myProfileSynchronizer = RenderingProfileSynchronizer.getInstance(project);
        myFirstReset = true;
    }

    protected MdRenderingProfileHolder getProfileForReset() {
        if (myFirstReset) {
            myFirstReset = false;
            return myProfileSynchronizer.getRenderingProfile();
        } else {
            return myProfileSynchronizer.getRenderingProfileHolder();
        }
    }

    /**
     * Apply changes in form if one is displayed
     *
     * @param renderingProfile rendering profile
     */
    final public void applyForm(@NotNull MdRenderingProfileHolder renderingProfile) {
        if (myForm != null) {
            myForm.apply(renderingProfile);
        }
    }
}
