// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;

public class MdNavigatorEmojiExtension implements /*Parser.ParserExtension,*/ HtmlRenderer.HtmlRendererExtension {

    @Override
    public void rendererOptions(@NotNull final MutableDataHolder options) {

    }

    public static MdNavigatorEmojiExtension create() {
        return new MdNavigatorEmojiExtension();
    }

    @Override
    public void extend(@NotNull HtmlRenderer.Builder rendererBuilder, String rendererType) {
        switch (rendererType) {
            case "HTML":
            case "JIRA":
            case "YOUTRACK":
                rendererBuilder.nodeRendererFactory(new MdNavigatorEmojiNodeRenderer.Factory());
                break;
        }
    }
}
