// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.md.nav.settings.Item;
import com.vladsch.md.nav.settings.StateHolder;
import com.vladsch.md.nav.settings.TagItemHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdSettingsExtension<T extends MdSettingsExtension<T>> extends Item<StateHolder>, MdSettings, MdOverrideSettings {
    default void resetToDefaults() {
        copyFrom(getDefault());
    }

    @NotNull
    DataKey<T> getKey();

    @NotNull
    T createCopy();

    // FIX: add default implementation: other to save state, this to load state
    void copyFrom(@NotNull T other);

    void addItems(final boolean readOnly, @NotNull TagItemHolder tagItemHolder);

    default boolean isDefault() {
        return this.equals(getDefault());
    }

    @NotNull
    default T getDefault() {
        return getKey().get(null);
    }

    default void validateLoadedSettings() {

    }

    /**
     * Conditional copy. Only copies from other container if the extension has been set
     *
     * @param other container
     */
    default void copyFrom(@Nullable DataSet other) {
        if (other != null && other.contains(getKey())) {
            copyFrom(getKey().get(other));
        }
    }

    default void notifySettingsChanged() {

    }

    /**
     * Return a settings which apply to settings given by toSettings
     * if no changes then return self
     *
     * @param fromSettings original settings
     * @param toSettings   desired settings
     *                     <p>
     *                     modify settings to reflect change
     */
    default void changeToProvider(@Nullable Object fromSettings, @Nullable Object toSettings) {

    }
}
