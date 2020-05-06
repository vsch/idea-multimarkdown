// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language.folding;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(
        name = "FlexmarkFoldingSettings",
        storages = @Storage("editor.codeinsight.xml")
)
public class MdFlexmarkFoldingSettings implements PersistentStateComponent<MdFlexmarkFoldingSettings> {
    final private static MdFlexmarkFoldingSettings NULL = new MdFlexmarkFoldingSettings();

    @NotNull
    public static MdFlexmarkFoldingSettings getInstance() {
        MdFlexmarkFoldingSettings service = ServiceManager.getService(MdFlexmarkFoldingSettings.class);
        return service == null ? NULL : service;
    }

    public boolean COLLAPSE_FLEXMARK_EXAMPLE = false;
    public boolean COLLAPSE_FLEXMARK_EXAMPLE_AST = true;
    public boolean COLLAPSE_FLEXMARK_EXAMPLE_HTML = false;
    public boolean COLLAPSE_FLEXMARK_EXAMPLE_SOURCE = false;
    public boolean COLLAPSE_FLEXMARK_FRONT_MATTER = false;

    @Override
    public MdFlexmarkFoldingSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull final MdFlexmarkFoldingSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
