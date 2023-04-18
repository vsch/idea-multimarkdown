// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.vladsch.plugin.util.CancelableJobScheduler;
import com.vladsch.plugin.util.ui.ColorIterable;
import com.vladsch.plugin.util.ui.highlight.TextRangeHighlightProviderBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdTextRangeHighlightProviderImpl extends TextRangeHighlightProviderBase<Object> {
    public MdTextRangeHighlightProviderImpl(@NotNull Object settings) {
        super(settings);
    }

    @Nullable
    @Override
    protected CancelableJobScheduler getCancellableJobScheduler() {
        //return MdCancelableJobScheduler.getInstance();
        return null;
    }

    @Override
    protected void subscribeSettingsChanged() {

    }

    @NotNull
    @Override
    protected ColorIterable getColors(@NotNull final Object settings) {
        return new ColorIterable(false);
    }
}
