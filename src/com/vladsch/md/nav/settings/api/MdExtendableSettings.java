// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.settings.TagItemHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface MdExtendableSettings extends MdSettingsExtensionsHolder {

    @NotNull
    MutableDataSet getExtensions();

    @NotNull
    Set<DataKey<MdSettingsExtension<?>>> getContainedExtensionKeys();

    @NotNull
    default <T extends MdSettingsExtension<T>> T getExtension(@NotNull DataKey<T> key) {
        if (!hasExtensionPoint(key))
            throw new IllegalStateException("Settings Extension for key " + key + " is not registered");
        return key.get(getExtensions());
    }

    default <T extends MdSettingsExtension<T>> void setExtension(@NotNull T value) {
        DataKey<T> key = value.getKey();
        if (!hasExtensionPoint(key)) throw new IllegalStateException("Settings Extension for key " + key + " is not registered");

        getExtensions().set(key, value);
        notifySettingsChanged(key);
    }

    default boolean isExtensionDefault(@NotNull DataKey<MdSettingsExtension<?>> key) {
        if (!getExtensionKeys().contains(key)) throw new IllegalStateException("Settings Extension for key " + key + " is not registered");
        return key.get(getExtensions()).isDefault();
    }

    void validateLoadedSettings();

    Set<DataKey<MdSettingsExtension<?>>> getExtensionKeys();

    Collection<MdSettingsExtensionProvider<?>> getExtensionPoints();

    <T extends MdSettingsExtension<T>> boolean hasExtension(@NotNull DataKey<T> key);

    <T extends MdSettingsExtension<T>> boolean hasExtensionPoint(@NotNull DataKey<T> key);

    void notifyPendingSettingsChanged();

    <T extends MdSettingsExtension<T>> void pendingSettingsChanged(@NotNull DataKey<T> key);

    <T extends MdSettingsExtension<T>> void notifySettingsChanged(@NotNull DataKey<T> key);

    void initializeExtensions(@NotNull MdExtendableSettings container);

    @NotNull
    TagItemHolder addUnwrappedItems(@NotNull Object container, @NotNull TagItemHolder tagItemHolder);
}
