// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.settings;

import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.settings.MdApplicationLocalSettings;
import com.vladsch.md.nav.settings.MdDebugSettings;
import com.vladsch.md.nav.settings.api.MdSettingsExtensionProvider;
import org.jetbrains.annotations.NotNull;

public class FlexmarkDebugSettingsExtensionProvider implements MdSettingsExtensionProvider<FlexmarkDebugSettings> {
    @NotNull
    @Override
    public FlexmarkDebugSettings create() {
        return new FlexmarkDebugSettings();
    }

    @NotNull
    @Override
    public DataKey<FlexmarkDebugSettings> getKey() {
        return FlexmarkDebugSettings.KEY;
    }

    @Override
    public boolean isAvailableIn(final Object container) {
        return container instanceof MdDebugSettings;
    }

    @Override
    public boolean isLoadedBy(final Object container) {
        return container instanceof MdDebugSettings;
    }

    @Override
    public boolean isSavedBy(final Object container) {
        return container instanceof MdDebugSettings;
    }
}
