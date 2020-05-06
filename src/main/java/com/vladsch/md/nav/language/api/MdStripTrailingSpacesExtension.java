// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;

public interface MdStripTrailingSpacesExtension {
    ExtensionPointName<MdStripTrailingSpacesExtension> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.stripTrailingSpacesExtension");
    MdExtensions<MdStripTrailingSpacesExtension> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdStripTrailingSpacesExtension[0]);

    @NotNull
    static ArrayList<MdTrailingSpacesCodeStyleOption> getOptions() {
        ArrayList<MdTrailingSpacesCodeStyleOption> options = new ArrayList<>();
        for (MdStripTrailingSpacesExtension extension : MdStripTrailingSpacesExtension.EXTENSIONS.getValue()) {
            MdTrailingSpacesCodeStyleOption option = extension.getCodeStyleOption();
            if (option != null) {
                options.add(option);
            }
        }

        options.sort(Comparator.comparing((MdTrailingSpacesCodeStyleOption::getOptionName)));
        return options;
    }

    void setStripTrailingSpacesFilters(@NotNull MdStripTrailingSpacesDocumentFilter filter);

    /**
     * Option to add to code style settings for trailing spaces custom element
     *
     * @return option or null
     */
    @Nullable
    default MdTrailingSpacesCodeStyleOption getCodeStyleOption() {
        return null;
    }
}
