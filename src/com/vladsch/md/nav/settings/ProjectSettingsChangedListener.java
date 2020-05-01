// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

public interface ProjectSettingsChangedListener {
    Topic<ProjectSettingsChangedListener> TOPIC = Topic.create("MarkdownNavigator.ProjectSettingsChanged", ProjectSettingsChangedListener.class);

    void onSettingsChange(@NotNull Project project, @NotNull MdProjectSettings settings);
}
