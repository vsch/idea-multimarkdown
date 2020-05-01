// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.parserExtensions;

import com.vladsch.flexmark.ext.emoji.Emoji;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.emoji.EmojiImageType;
import com.vladsch.flexmark.ext.emoji.EmojiShortcutType;
import com.vladsch.flexmark.ext.emoji.EmojiVisitor;
import com.vladsch.flexmark.ext.emoji.EmojiVisitorExt;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.editor.api.MdHtmlGeneratorExtension;
import com.vladsch.md.nav.parser.MdParserOptions;
import com.vladsch.md.nav.parser.SyntheticFlexmarkNodes;
import com.vladsch.md.nav.parser.api.HtmlPurpose;
import com.vladsch.md.nav.parser.api.MdParser;
import com.vladsch.md.nav.parser.api.MdParserExtension;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.parser.flexmark.MdNavigatorEmojiExtension;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;

import static com.vladsch.md.nav.parser.MdLexParser.EMOJI_SHORTCUTS;
import static com.vladsch.md.nav.parser.api.HtmlPurpose.EXPORT;
import static com.vladsch.md.nav.parser.api.ParserPurpose.SWING;
import static com.vladsch.md.nav.psi.util.MdTypes.EMOJI;
import static com.vladsch.md.nav.psi.util.MdTypes.EMOJI_ID;
import static com.vladsch.md.nav.psi.util.MdTypes.EMOJI_MARKER;
import static com.vladsch.md.nav.psi.util.MdTypes.NONE;

public class MdEmojiParserExtension implements MdParserExtension {
    protected static final EmojiHandlerOptions OPTIONS = new EmojiHandlerOptions();
    final public static DataKey<EmojiHandlerOptions> KEY = new DataKey<>(MdEmojiParserExtension.class.getName(), OPTIONS);

    public static String emojiInstallDirectory(boolean inTest) {
        String pluginInstallDir = inTest ? "" : MdPlugin.getPluginPath();
        if (pluginInstallDir == null) pluginInstallDir = "";

        if (pluginInstallDir.length() > 2 && pluginInstallDir.charAt(1) == ':') {
            // windows
            pluginInstallDir = "file:/" + pluginInstallDir + "/emojis/";
        } else {
            // unix or mac
            pluginInstallDir = "file://" + pluginInstallDir + "/emojis/";
        }

        return pluginInstallDir;
    }

    @Override
    public DataKey<EmojiHandlerOptions> getKey() {
        return KEY;
    }

    @Override
    public void setFlexmarkOptions(final MdParserOptions options) {
        if (options.haveOptions(EMOJI_SHORTCUTS)) {
            ParserPurpose parserPurpose = options.getParserPurpose();
            HtmlPurpose htmlPurpose = options.getHtmlPurpose();
            MdRenderingProfile renderingProfile = options.getRenderingProfile();

            EmojiImageType emojiImageType = renderingProfile.getParserSettings().getEmojiImagesType().flexmarkType;
            EmojiShortcutType emojiShortcutType = renderingProfile.getParserSettings().getEmojiShortcutsType().flexmarkType;
            options.addExtension(EmojiExtension.class, EmojiExtension::create)
                    .addExtension(MdNavigatorEmojiExtension.class, MdNavigatorEmojiExtension::create)
                    .set(EmojiExtension.ROOT_IMAGE_PATH, emojiInstallDirectory(MdPlugin.RUNNING_TESTS))
                    .set(EmojiExtension.USE_SHORTCUT_TYPE, emojiShortcutType)
                    .set(EmojiExtension.USE_IMAGE_TYPE, emojiImageType);

            Boolean noStylesheets = null;
            for (MdHtmlGeneratorExtension extension : MdHtmlGeneratorExtension.EXTENSIONS.getValue()) {
                noStylesheets = extension.noStylesheets(htmlPurpose == EXPORT, renderingProfile);
                if (noStylesheets != null) break;
            }

            if (noStylesheets != null && noStylesheets) {
                // set these manually
                options.set(EmojiExtension.ATTR_ALIGN, "absmiddle")
                        .set(EmojiExtension.ATTR_IMAGE_SIZE, "20")
                        .set(EmojiExtension.ATTR_IMAGE_CLASS, "");
            } else if (parserPurpose != SWING) {
                options.set(EmojiExtension.ATTR_ALIGN, "")
                        .set(EmojiExtension.ATTR_IMAGE_SIZE, "")
                        .set(EmojiExtension.ATTR_IMAGE_CLASS, "emoji");
            }
        }
    }

    @Override
    public void setFlexmarkHandlers(@NotNull final MdParser parser) {
        EmojiHandler handler = new EmojiHandler(parser);
        parser.addHandlers(EmojiVisitorExt.VISIT_HANDLERS(handler));
    }

    private static class EmojiHandlerOptions {
        public EmojiHandlerOptions() {
        }

        public EmojiHandlerOptions(MdRenderingProfile renderingProfile) {
        }
    }

    private static class EmojiHandler extends MdParserHandlerBase<EmojiHandlerOptions> implements EmojiVisitor {
        EmojiHandler(final MdParser parser) {
            super(parser, KEY);
        }

        @Override
        public void visit(Emoji node) {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, EMOJI, NONE);
            nodes.addLeaf(node.getOpeningMarker(), EMOJI_MARKER);
            nodes.addComposite(node.getText(), EMOJI_ID, EMOJI_ID);
            nodes.addLeaf(node.getClosingMarker(), EMOJI_MARKER);
            addCompositeTokens(nodes);
        }
    }
}
