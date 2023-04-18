// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.parserExtensions;

import com.vladsch.flexmark.ext.zzzzzz.Zzzzzz;
import com.vladsch.flexmark.ext.zzzzzz.ZzzzzzBlock;
import com.vladsch.flexmark.ext.zzzzzz.ZzzzzzVisitor;
import com.vladsch.flexmark.ext.zzzzzz.ZzzzzzVisitorExt;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.parser.MdParserOptions;
import com.vladsch.md.nav.parser.api.MdParser;
import com.vladsch.md.nav.parser.api.MdParserExtension;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;

public class MdZzzzzzParserExtension implements MdParserExtension {
    protected static final ZzzzzzHandlerOptions OPTIONS = new ZzzzzzHandlerOptions();
    final public static DataKey<ZzzzzzHandlerOptions> KEY = new DataKey<>(MdZzzzzzParserExtension.class.getName(), OPTIONS);

    @Override
    public DataKey<ZzzzzzHandlerOptions> getKey() {
        return KEY;
    }

    @Override
    public void setFlexmarkOptions(final MdParserOptions options) {
//        MdRenderingProfile renderingProfile = options.getRenderingProfile();
//        MdHtmlSettings htmlSettings = renderingProfile.getHtmlSettings();
//        MdPreviewSettings previewSettings = renderingProfile.getPreviewSettings();
//        ParserPurpose parserPurpose = options.getParserPurpose();
//        HtmlPurpose htmlPurpose = options.getHtmlPurpose();
//        MdLinkResolver linkResolver = options.getLinkResolver();
//        ZzzzzzHandlerOptions handlerOptions = new ZzzzzzHandlerOptions(renderingProfile);
//        boolean forHtmlExport = htmlPurpose == HtmlPurpose.EXPORT;
//        options.set(KEY, handlerOptions);
    }

    @Override
    public void setFlexmarkHandlers(@NotNull final MdParser parser) {
        ZzzzzzHandler handler = new ZzzzzzHandler(parser);
        parser.addHandlers(ZzzzzzVisitorExt.VISIT_HANDLERS(handler));
    }

    private static class ZzzzzzHandlerOptions {
//        final boolean optionZzzzzzs;

        public ZzzzzzHandlerOptions() {
        }

        public ZzzzzzHandlerOptions(MdRenderingProfile renderingProfile) {
//            optionZzzzzzs = (renderingProfile.getParserSettings().getParserOptionsFlags() & GITHUB_WIKI_LINKS) != 0;
        }
    }

    private static class ZzzzzzHandler extends MdParserHandlerBase<ZzzzzzHandlerOptions> implements ZzzzzzVisitor {
        ZzzzzzHandler(final MdParser parser) {
            super(parser, KEY);
        }

        @Override
        public void visit(final ZzzzzzBlock block) {

        }

        @Override
        public void visit(Zzzzzz node) {

        }
    }
}
