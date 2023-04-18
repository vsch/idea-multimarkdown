// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.util.MdExtensions;

import java.util.function.Predicate;

public interface MdApplicationRestartRequiredProvider {
    ExtensionPointName<MdApplicationRestartRequiredProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.applicationRestartRequired");
    MdExtensions<MdApplicationRestartRequiredProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdApplicationRestartRequiredProvider[0]);

    /**
     * @param startupSettings markdown application settings at startup
     *
     * @return predicate which will return true if a setting changed requiring application restart
     */
    Predicate<MdApplicationSettings> getRestartRequiredPredicate(MdApplicationSettings startupSettings);
}
