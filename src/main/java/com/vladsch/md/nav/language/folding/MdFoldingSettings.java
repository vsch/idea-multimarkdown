// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.folding;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "MarkdownNavigatorFoldingSettings", storages = @Storage("editor.codeinsight.xml"))
public class MdFoldingSettings implements PersistentStateComponent<MdFoldingSettings> {
    final private static MdFoldingSettings NULL = new MdFoldingSettings();

    @NotNull
    public static MdFoldingSettings getInstance() {
        MdFoldingSettings service = ServiceManager.getService(MdFoldingSettings.class);
        return service == null ? NULL : service;
    }

    public boolean COLLAPSE_CODE_FENCE_BLOCKS = false;
    public boolean COLLAPSE_COMMENTS = false;
    public boolean COLLAPSE_EMBEDDED_IMAGES = true;
    public boolean COLLAPSE_EXPLICIT_LINKS = false;
    public boolean COLLAPSE_HEADINGS_1 = false;
    public boolean COLLAPSE_HEADINGS_2 = false;
    public boolean COLLAPSE_HEADINGS_3 = false;
    public boolean COLLAPSE_HEADINGS_4 = false;
    public boolean COLLAPSE_HEADINGS_5 = false;
    public boolean COLLAPSE_HEADINGS_6 = false;
    public boolean COLLAPSE_IMAGES = true;
    public boolean COLLAPSE_JEKYLL_FRONT_MATTER = false;
    public boolean COLLAPSE_LIST_ITEMS = false;
    public boolean COLLAPSE_MULTILINE_URL_IMAGES = false;
    public boolean COLLAPSE_REFERENCES = false;
    public boolean COLLAPSE_VERBATIM_BLOCKS = false;

    @Override
    public MdFoldingSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull final MdFoldingSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
