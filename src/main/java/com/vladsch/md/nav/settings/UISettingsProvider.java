// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.ide.ui.UISettings;

// DEPRECATED: wrapper to make UISettings compatible with 2016.x and 2017.x
public class UISettingsProvider {
    public static UISettings getInstance() {
        return UISettings.getInstance();
    }
}
