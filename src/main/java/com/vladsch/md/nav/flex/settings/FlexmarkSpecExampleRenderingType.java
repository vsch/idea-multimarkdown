// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.settings;

import com.vladsch.flexmark.ext.spec.example.internal.RenderAs;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum FlexmarkSpecExampleRenderingType implements ComboBoxAdaptable<FlexmarkSpecExampleRenderingType> {

    FENCED_CODE(0, RenderAs.FENCED_CODE, FlexCodeStyleBundle.message("flexmark.spec-example.render.fenced-code")),
    SECTIONS(1, RenderAs.SECTIONS, FlexCodeStyleBundle.message("flexmark.spec-example.render.sections")),
    DEFINITION_LIST(2, RenderAs.DEFINITION_LIST, FlexCodeStyleBundle.message("flexmark.spec-example.render.definition-list"));

    public final int intValue;
    public final RenderAs renderAs;
    public final @NotNull String displayName;

    FlexmarkSpecExampleRenderingType(int intValue, RenderAs renderAs, @NotNull String displayName) {
        this.intValue = intValue;
        this.renderAs = renderAs;
        this.displayName = displayName;
    }

    @NotNull
    public static Static<FlexmarkSpecExampleRenderingType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(FENCED_CODE));

    @NotNull
    @Override
    public ComboBoxAdapter<FlexmarkSpecExampleRenderingType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public FlexmarkSpecExampleRenderingType[] getValues() { return values(); }
}
