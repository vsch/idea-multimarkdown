// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.BundleBase;
import com.intellij.reference.SoftReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

public class WebHelpTopics extends BundleBase {
    @NonNls
    public static final String BUNDLE = "com.vladsch.md.nav.localization.web-help-topics";

    static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return message(getBundle(), key, params);
    }

    static Reference<ResourceBundle> ourBundle = null;

    static ResourceBundle getBundle() {
        ResourceBundle bundle = SoftReference.dereference(ourBundle);

        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }
}
