// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.md.nav.editor.util.HtmlCssResourceProvider;
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.util.MdExtensions;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;

public interface MdFencedCodeImageConverter {
    ExtensionPointName<MdFencedCodeImageConverter> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.codeFenceImageConverter");
    MdExtensions<MdFencedCodeImageConverter> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdFencedCodeImageConverter[0]);
    String HTTP_PREFIX = "http://";
    String HTTPS_PREFIX = "https://";

    @NotNull
    static String replaceHttpWithHttps(@NotNull String url) {
        return (url.startsWith(HTTP_PREFIX)) ? HTTPS_PREFIX + url.substring(HTTP_PREFIX.length()) : url;
    }

    /**
     * Return handled info types
     *
     * @return array of info strings this extension can handle
     */
    @NotNull
    String[] getInfoStrings();

    /**
     * Return conversion variants for given info type
     *
     * @param info info string for which to get conversion variants
     *
     * @return array of variant IDs
     */
    @NotNull
    String[] getConversionVariants(@NotNull String info);

    /**
     * Return conversion variants' display strings for given info type
     *
     * @param info info string for which to get conversion variants
     *
     * @return array of variant display strings, in the same order as {@link #getConversionVariants(String)}
     */
    @NotNull
    String[] getVariantDisplayTexts(@NotNull String info);

    /**
     * Return conversion variants' display strings for given info type
     *
     * @param info info string for which to get conversion variants
     *
     * @return array of variant display strings, in the same order as {@link #getConversionVariants(String)}
     */
    @NotNull
    String[] getVariantDescriptions(@NotNull String info);

    /**
     * Migrate conversion variant from old settings to string
     *
     * @param info  info string
     * @param param old setting value
     *
     * @return migrated conversion variant or null of not convertible
     */
    @Nullable
    String migrateConversionVariant(@NotNull String info, @NotNull Object param);

    /**
     * Convert fenced code to Image url if handled, else null
     *
     * @param content          fenced code content
     * @param info             fenced code info string (one of strings reported by {@link #getInfoStrings()}()
     * @param variant          variant (one of variants reported by {@link #getInfoStrings()}() for this info string
     * @param renderingProfile rendering profile used for the file being converted
     * @param resolver         link resolver used for rendering HTML, can be used to convert file references in content to full paths
     *
     * @return image url or null if not enabled
     */
    @Nullable
    MdFencedCodeImage imageUrl(@NotNull String content, @NotNull String info, @NotNull String variant, @NotNull MdRenderingProfile renderingProfile, @NotNull MdLinkResolver resolver);

    /**
     * default rendering of code fence block, only called if {@link #imageUrl(String, String, String, MdRenderingProfile, MdLinkResolver)} returns null
     *
     * @param info              language info which was matched to this converter
     * @param conversionVariant conversion variant
     * @param node              fenced code node
     * @param context           rendering context
     * @param html              html writer
     */
    default void defaultRender(@NotNull String info, String conversionVariant, @NotNull FencedCodeBlock node, @NotNull NodeRendererContext context, @NotNull HtmlWriter html) {
        context.delegateRender();
    }

    /**
     * Test if url can be converted to fenced code
     *
     * @param url         image url
     * @param isMultiline true if URL is a multi-line image URL
     * @param attributes  optional image attributes
     *
     * @return true if this converter can convert the image URL to fenced code (or inline code)
     */
    default boolean canConvertFromImageUrl(@NotNull String url, boolean isMultiline, @Nullable Attributes attributes) {
        return convertFromImageUrl(url, isMultiline, attributes) != null;
    }

    /**
     * Convert url to fenced code
     *
     * @param url         image url
     * @param isMultiline true if URL is a multi-line image URL
     * @param attributes  optional image attributes
     *
     * @return converted image to fenced code (or inline code)
     */
    @Nullable
    MdImageFencedCode convertFromImageUrl(@NotNull String url, boolean isMultiline, @Nullable Attributes attributes);

    /**
     * See if conversion variant has a URL conversion
     *
     * @param info              language info which was matched to this converter
     * @param conversionVariant conversion variant
     * @param remoteOnly        include only remote URLs
     *
     * @return true if it has URL conversion
     */
    boolean hasUrlConversion(@NotNull String info, String conversionVariant, boolean remoteOnly);

    /**
     * Adjust flexmark options for given conversion and rendering profile
     *
     * @param info              language info which was matched to this converter
     * @param conversionVariant conversion variant
     * @param options           flexmark options
     * @param renderingProfile  rendering profile
     */
    default void addRenderingOptions(@NotNull String info, @NotNull String conversionVariant, MutableDataHolder options, MdRenderingProfile renderingProfile) {

    }

    /**
     * Get Required Script Providers based on profile
     *
     * @param renderingProfile rendering profile
     * @param infoPredicate    null for all info strings or predicate to test
     *
     * @return map of handled script provider info to true for include/false for exclude
     */
    default Map<HtmlScriptResourceProvider.Info, Boolean> getRequiredScriptsProviders(@NotNull MdRenderingProfile renderingProfile, @Nullable Predicate<String> infoPredicate) {
        return Collections.emptyMap();
    }

    /**
     * Get Required Css Providers based on profile, only css providers which do not have script provider as parent (ie. bare css)
     *
     * @param renderingProfile rendering profile
     *
     * @return list of required script provider info
     */
    default Map<HtmlCssResourceProvider.Info, Boolean> getRequiredCssProviders(@NotNull MdRenderingProfile renderingProfile) {
        return Collections.emptyMap();
    }

    /**
     * Test if converter is available for given rendering profile (based preview settings, for example)
     *
     * @param renderingProfile rendering profile
     *
     * @return null or empty if available, else reason why not available
     */
    @Nullable
    default String isAvailable(@NotNull String info, @NotNull String conversionVariant, @NotNull MdRenderingProfile renderingProfile) {
        return null;
    }
}
