// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.vladsch.md.nav.settings.Item;
import com.vladsch.md.nav.settings.StateHolder;

public interface MdSettings extends Item<StateHolder> {
    void resetToDefaults();
}
