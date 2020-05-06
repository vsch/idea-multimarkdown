// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.vladsch.flexmark.ext.emoji.Emoji;
import com.vladsch.flexmark.ext.emoji.internal.EmojiOptions;
import com.vladsch.flexmark.ext.emoji.internal.EmojiResolvedShortcut;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.data.DataHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The node renderer that renders all the core nodes (comes last in the order of node renderers).
 */
@SuppressWarnings("WeakerAccess")
public class MdNavigatorEmojiNodeRenderer implements NodeRenderer {
    private final EmojiOptions myOptions;

    public MdNavigatorEmojiNodeRenderer(com.vladsch.flexmark.util.data.DataHolder options) {
        myOptions = new EmojiOptions(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        return new HashSet<>(Collections.singletonList(new NodeRenderingHandler<>(Emoji.class, this::render)));
    }

    private void render(Emoji node, NodeRendererContext context, HtmlWriter html) {
        final EmojiResolvedShortcut shortcut = EmojiResolvedShortcut.getEmojiText(node, myOptions.useShortcutType, myOptions.useImageType, myOptions.rootImagePath);

        if (shortcut.emoji == null || shortcut.emojiText == null) {
            // output as text
            html.text(":");
            context.renderChildren(node);
            html.text(":");
        } else {
            if (shortcut.isUnicode) {
                html.text(shortcut.emojiText);
            } else {
                // always use our images
                ResolvedLink resolvedLink = context.resolveLink(LinkType.IMAGE, myOptions.rootImagePath + shortcut.emoji.shortcut + ".png", null);

                html.attr("src", resolvedLink.getUrl());
                html.attr("alt", shortcut.alt);
                if (!myOptions.attrImageSize.isEmpty()) html.attr("height", myOptions.attrImageSize).attr("width", myOptions.attrImageSize);
                if (!myOptions.attrAlign.isEmpty()) html.attr("align", myOptions.attrAlign);
                if (!myOptions.attrImageClass.isEmpty()) html.attr("class", myOptions.attrImageClass);
                html.withAttr(resolvedLink);
                html.tagVoid("img");
            }
        }
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull final DataHolder options) {
            return new MdNavigatorEmojiNodeRenderer(options);
        }
    }
}
