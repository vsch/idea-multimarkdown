// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.settings.api.MdApplicationRestartRequiredProvider;
import com.vladsch.plugin.util.ChangedValuePredicate;

import java.util.function.Predicate;

public class MdHighlighterChangedRestartProvider implements MdApplicationRestartRequiredProvider {
    @Override
    public Predicate<MdApplicationSettings> getRestartRequiredPredicate(final MdApplicationSettings startupSettings) {
        return new ChangedValuePredicate<>(startupSettings, settings -> settings.getDocumentSettings().getFullHighlightCombinations());
    }

    public static void setRestartNeededShownFlags(boolean value) {
        MdApplicationRestartManager.getInstance().setRestartNeededShownFlags(MdHighlighterChangedRestartProvider.class, value);
    }
}
