// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.api;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.md.nav.editor.util.InjectHtmlResource;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.util.MdExtensions;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface MdHtmlGeneratorExtension {
    ExtensionPointName<MdHtmlGeneratorExtension> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.htmlGeneratorExtension");
    MdExtensions<MdHtmlGeneratorExtension> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdHtmlGeneratorExtension[0]);

    DataContext addHtmlExportData(Project project, @NotNull MdRenderingProfile renderingProfile, MutableDataHolder options, DataContext dataContext, @Nullable Map<String, String> exportMap);

    /**
     * @param result             where to append HTML
     * @param renderingProfile   rendering profile
     * @param filteredInjections list of HTML injections
     * @param forHtmlExport      true if HTML for export
     * @param dataContext        dataContext
     * @param exportMap          export map or null
     *
     * @return true if handled
     */
    boolean addHtmlInjections(@NotNull StringBuilder result, @NotNull MdRenderingProfile renderingProfile, @NotNull MdLinkResolver linkResolver, @NotNull List<InjectHtmlResource> filteredInjections, boolean forHtmlExport, @NotNull DataContext dataContext, @Nullable Map<String, String> exportMap);

    /**
     * Process included documents
     *
     * @param parser   parser instance
     * @param renderer renderer instance
     * @param document main document node
     * @param file     main psi file
     *
     * @return true if processed
     */
    @NotNull
    Document processIncludes(@NotNull Parser parser, @NotNull HtmlRenderer renderer, @NotNull Document document, @Nullable PsiFile file);

    /**
     * Post process HTML
     *
     * @param html             html
     * @param renderingProfile rendering profile
     *
     * @return return post processed or unmodified HTML
     */
    @NotNull
    String postProcessHtml(@NotNull String html, @NotNull MdRenderingProfile renderingProfile);

    @Nullable
    String adjustPageRef(@Nullable String gitHubPageRef, boolean forHtmlExport, @NotNull MdRenderingProfile renderingProfile);

    @Nullable
    Boolean noStylesheets(boolean forHtmlExport, @NotNull MdRenderingProfile renderingProfile);

    /**
     * Adjust resource URL for generating HTML
     *
     * @param resourcePath     resource path
     * @param resourceUrl      resource URL
     * @param project          project
     * @param forHtmlExport    true if for export
     * @param renderingProfile rendering profile
     * @param dataContext      data context
     *
     * @return null if not adjusting, else return adjusted URL
     */
    @Nullable
    String adjustUrl(String resourcePath, @Nullable String resourceUrl, @NotNull Project project, boolean forHtmlExport, @NotNull MdRenderingProfile renderingProfile, @Nullable DataContext dataContext);
}
