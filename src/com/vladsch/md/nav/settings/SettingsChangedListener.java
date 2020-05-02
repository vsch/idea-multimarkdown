// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

public interface SettingsChangedListener {
    Topic<SettingsChangedListener> TOPIC = Topic.create("MarkdownNavigator.ApplicationSettingsChanged", SettingsChangedListener.class, Topic.BroadcastDirection.NONE);

    void onSettingsChange(@NotNull MdApplicationSettings settings);
}
