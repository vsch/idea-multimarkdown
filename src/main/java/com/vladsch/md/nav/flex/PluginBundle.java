// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class PluginBundle {

    @NonNls
    private static final String BUNDLE_NAME = "com.vladsch.md.nav.flex.localization.strings";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private PluginBundle() {
    }

    public static ResourceBundle getBundle() {
        return BUNDLE;
    }

    public static String getString(String key, Object... params) {
        return AbstractBundle.message(BUNDLE, key, params);
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return AbstractBundle.message(BUNDLE, key, params);
    }

    public static String messageOrBlank(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return AbstractBundle.messageOrDefault(BUNDLE, key, "", params);
    }
}
