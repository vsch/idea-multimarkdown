// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.settings;

import com.vladsch.md.nav.flex.PluginBundle;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdApplicationSettingsHolder;
import com.vladsch.md.nav.settings.MdDebugSettingsForm;
import com.vladsch.md.nav.settings.RenderingProfileSynchronizer;
import com.vladsch.md.nav.settings.api.MdSettingsComponent;
import com.vladsch.md.nav.settings.api.MdSettingsFormExtensionProvider;
import org.jetbrains.annotations.NotNull;

public class FlexmarkDebugSettingsFormProvider implements MdSettingsFormExtensionProvider<MdApplicationSettings, MdApplicationSettingsHolder> {

    @NotNull
    @Override
    public MdSettingsComponent<MdApplicationSettingsHolder> createComponent(final MdApplicationSettings settings, final RenderingProfileSynchronizer profileSynchronizer, final Object parent) {
        return new FlexmarkDebugSettingsForm(settings, profileSynchronizer);
    }

    @NotNull
    @Override
    public String getExtensionName() {
        return PluginBundle.message("product.title");
    }

    @Override
    public boolean isAvailable(@NotNull final Object parent) {
        return parent instanceof MdDebugSettingsForm;
    }
}
