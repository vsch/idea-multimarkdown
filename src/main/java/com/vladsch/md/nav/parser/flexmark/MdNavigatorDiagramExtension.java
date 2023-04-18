// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.intellij.openapi.project.Project;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.md.nav.parser.api.MdFencedCodeImageConverter;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MdNavigatorDiagramExtension implements /*Parser.ParserExtension,*/ HtmlRenderer.HtmlRendererExtension {
    @NotNull
    static public MdNavigatorDiagramExtension create() {
        return new MdNavigatorDiagramExtension();
    }

    /**
     * Used to allow fenced code converters to adjust flexmark options
     *
     * @param options          flexmark options to check/modify
     * @param renderingProfile rendering profile
     */
    public static void addRenderingProfileOptions(@NotNull final MutableDataHolder options, @NotNull MdRenderingProfile renderingProfile) {
        Project project = renderingProfile.getProject();
        if (project != null) {
            HashMap<String, String> codeConversions = renderingProfile.getHtmlSettings().getFencedCodeConversions();
            MdFencedCodeImageConversionManager conversionManager = MdFencedCodeImageConversionManager.getInstance(project);
            for (Map.Entry<String, String> entry : codeConversions.entrySet()) {
                MdFencedCodeImageConverter converter = conversionManager.getImageConverter(entry.getKey(), entry.getValue());
                if (converter != null) {
                    converter.addRenderingOptions(entry.getKey(), entry.getValue(), options, renderingProfile);
                }
            }
        }
    }

    @Override
    public void rendererOptions(@NotNull final MutableDataHolder options) {

    }

    @Override
    public void extend(@NotNull HtmlRenderer.Builder rendererBuilder, String rendererType) {
        switch (rendererType) {
            case "HTML":
            case "JIRA":
            case "YOUTRACK":
                rendererBuilder.nodeRendererFactory(new MdNavigatorDiagramNodeRenderer.Factory());
                break;
        }
    }
}
