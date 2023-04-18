// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

/**
 * Only used to get around Kotlin's Scala ambitions, insisting on needing to determine parameter T in MdSettingsExtension
 * for a function that does not require knowing what T is.
 */
public interface MdOverrideSettings {
    default boolean isOverrideByDefault() {
        return false;
    }
}
