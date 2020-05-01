// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.help.WebHelpProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdWebHelpTopicProvider extends WebHelpProvider {
    @Nullable
    @Override
    public String getHelpPageUrl(@NotNull final String helpTopicId) {
        return WebHelpTopics.message(helpTopicId);
    }

    @NotNull
    @Override
    public String getHelpTopicPrefix() {
        return getMarkdownHelpTopicPrefix();
    }

    @NotNull
    public static String getMarkdownHelpTopicPrefix() {
        return "com.vladsch.markdown.navigator.";
    }
}
