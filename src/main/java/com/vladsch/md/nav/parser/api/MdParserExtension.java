// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.api;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.editor.util.HtmlCssResourceProvider;
import com.vladsch.md.nav.editor.util.InjectHtmlResource;
import com.vladsch.md.nav.parser.MdParserOptions;
import com.vladsch.md.nav.parser.RenderingOptions;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface MdParserExtension {
    ExtensionPointName<MdParserExtension> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.parserExtension");
    MdExtensions<MdParserExtension> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdParserExtension[0]);

    /**
     * Used to fudge rendering options as needed
     *
     * @param options rendering options for parsing
     */
    default void setRenderingOptions(RenderingOptions options) {

    }

    /**
     * Key identifying the parser extension
     *
     * @return data key identifying this extension
     */
    DataKey<?> getKey();

    void setFlexmarkOptions(MdParserOptions options);

    void setFlexmarkHandlers(@NotNull MdParser parser);

    default void injectHtmlResource(
            @NotNull HtmlCssResourceProvider.Info forProviderInfo,
            @NotNull Project project,
            @NotNull MdApplicationSettings applicationSettings,
            @NotNull MdRenderingProfile renderingProfile,
            @NotNull ArrayList<InjectHtmlResource> injections,
            @NotNull Boolean forHtmlExport,
            @NotNull DataContext dataContext
    ) {

    }
}
