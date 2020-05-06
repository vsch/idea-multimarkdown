// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum DocumentIconTypes implements ComboBoxAdaptable<DocumentIconTypes> {
    MARKDOWN_NAVIGATOR(0, MdBundle.message("document-icon.markdown-navigator")),
    MARKDOWN(1, MdBundle.message("document-icon.markdown")),
    MARKDOWN_NAVIGATOR_WIKI(2, MdBundle.message("document-icon.markdown-navigator-wiki")),
    MARKDOWN_WIKI(3, MdBundle.message("document-icon.markdown-wiki")),
    ;

    public final @NotNull String displayName;
    public final int intValue;

    DocumentIconTypes(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<DocumentIconTypes> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(MARKDOWN_NAVIGATOR));

    @NotNull
    @Override
    public ComboBoxAdapter<DocumentIconTypes> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public DocumentIconTypes[] getValues() { return values(); }
}
