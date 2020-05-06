// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.settings;

import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.api.MdSettingsExtensionProvider;
import org.jetbrains.annotations.NotNull;

public class FlexmarkHtmlSettingsExtensionProvider implements MdSettingsExtensionProvider<FlexmarkHtmlSettings> {
    @NotNull
    @Override
    public FlexmarkHtmlSettings create() {
        return new FlexmarkHtmlSettings();
    }

    @NotNull
    @Override
    public DataKey<FlexmarkHtmlSettings> getKey() {
        return FlexmarkHtmlSettings.KEY;
    }

    @Override
    public boolean isAvailableIn(final Object container) {
        return container instanceof MdHtmlSettings;
    }

    @Override
    public boolean isLoadedBy(final Object container) {
        return container instanceof MdHtmlSettings;
    }

    @Override
    public boolean isSavedBy(final Object container) {
        return container instanceof MdRenderingProfile || container instanceof FlexmarkProjectSettingsManager;
    }
}
