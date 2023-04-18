// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

public interface MdSettingsExtensionProvider<T extends MdSettingsExtension<T>> {
    ExtensionPointName<MdSettingsExtensionProvider<?>> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.settingsExtension");
    MdExtensions<MdSettingsExtensionProvider<?>> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdSettingsExtensionProvider<?>[0]);

    @NotNull
    T create();

    @NotNull
    DataKey<T> getKey();

    /**
     * Test if should be added to container
     *
     * @param container container inquiring whether to include settings extension
     *
     * @return true if available for container
     */
    boolean isAvailableIn(Object container);

    /**
     * Test if should be loaded by container
     * or if it should load if it is a persistence service class and it is not saved by.
     * <p>
     * If isAvailable and isLoaded by return true for an extendable setting then container.copyFrom() will
     * also copy the extensions. In this case you should be careful not to have the container defaults overwrite
     * the contents of extension.
     * <p>
     * NOTE: having true returned for persistence service for which isSavedBy returns false, means settings can be
     * read from the container but will be saved somewhere else. Useful when migrating settings from one container
     * to another.
     *
     * @param container container inquiring whether to include settings extension
     *
     * @return true if available for container
     */
    default boolean isLoadedBy(Object container) {
        return isSavedBy(container);
    }

    /**
     * Test if container should save extension settings in its state information if extendable settings container
     * or if persistence service class whether settings should be saved.
     * <p>
     * If isSavedBy() returns true then the container will also be used for loading the settings in addition to
     * any other containers for which isLoadedBy() returns true.
     *
     * @param container container inquiring whether to save settings extension
     *
     * @return true if saved with container
     */
    boolean isSavedBy(Object container);
}
