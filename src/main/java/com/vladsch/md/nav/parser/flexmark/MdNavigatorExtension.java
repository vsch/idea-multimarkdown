// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.NullableDataKey;
import com.vladsch.flexmark.util.sequence.Range;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class MdNavigatorExtension implements /*Parser.ParserExtension,*/ HtmlRenderer.HtmlRendererExtension {
    final static public DataKey<Supplier<? extends MdLinkResolver>> LINK_RESOLVER = new DataKey<>("LINK_RESOLVER", (Supplier<? extends MdLinkResolver>) () -> null);
    final static public DataKey<Supplier<? extends MdRenderingProfile>> RENDERING_PROFILE = new DataKey<>("RENDERING_PROFILE", (Supplier<? extends MdRenderingProfile>) () -> null);
    final static public NullableDataKey<Map<String, String>> HTML_IMAGE_FILE_MAP = new NullableDataKey<>("HTML_IMAGE_FILE_MAP");
    final static public NullableDataKey<String> HTML_EXPORT_PATH = new NullableDataKey<>("HTML_EXPORT_PATH");
    final static public DataKey<String> LOCAL_ONLY_TARGET_CLASS = new DataKey<>("LOCAL_ONLY_TARGET_CLASS", "local-only");
    final static public DataKey<String> MISSING_TARGET_CLASS = new DataKey<>("MISSING_TARGET_CLASS", "absent");
    final static public DataKey<Boolean> USE_IMAGE_SERIALS = new DataKey<>("USE_IMAGE_SERIALS", true);
    final static public DataKey<Boolean> USE_SWING_ATTRIBUTES = new DataKey<>("USE_SWING_ATTRIBUTES", false);
    final static public DataKey<Boolean> IS_WIKI_PAGE = new DataKey<>("IS_WIKI_PAGE", false);
    final static public DataKey<Boolean> LINK_TO_EXPORTED_HTML = new DataKey<>("LINK_TO_EXPORTED_HTML", false);
    final static public DataKey<Boolean> HTML_EXPORT = new DataKey<>("HTML_EXPORT", false);
    final static public LinkStatus LOCAL_ONLY = new LinkStatus("LOCAL_ONLY");
    final static public String FILE_URI_PREFIX = "file://";
    final public static String SOURCE_POSITION_ATTRIBUTE_NAME = "md-pos";
    final static public NullableDataKey<Map<Range, String>> HIGHLIGHT_RANGES = new NullableDataKey<>("HIGHLIGHT_RANGES");

    static public MdNavigatorExtension create() {
        return new MdNavigatorExtension();
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
                rendererBuilder.linkResolverFactory(new FlexmarkLinkResolver.Factory());
                rendererBuilder.attributeProviderFactory(new FlexmarkAttributeProvider.Factory());
                if (HIGHLIGHT_RANGES.get(rendererBuilder) != null) {
                    rendererBuilder.nodeRendererFactory(new MdNavigatorNodeRenderer.Factory());
                }
                break;
        }
    }
}
