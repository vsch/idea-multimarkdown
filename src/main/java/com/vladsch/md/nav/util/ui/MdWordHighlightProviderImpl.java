// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.vladsch.plugin.util.CancelableJobScheduler;
import com.vladsch.plugin.util.ui.ColorIterable;
import com.vladsch.plugin.util.ui.highlight.WordHighlightProviderBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdWordHighlightProviderImpl extends WordHighlightProviderBase<Object> {
    public MdWordHighlightProviderImpl(@NotNull Object settings) {
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
