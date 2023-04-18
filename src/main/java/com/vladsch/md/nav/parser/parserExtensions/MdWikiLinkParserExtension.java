// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.parserExtensions;

import com.vladsch.flexmark.ext.wikilink.WikiLink;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkVisitor;
import com.vladsch.flexmark.ext.wikilink.WikiLinkVisitorExt;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.parser.Extensions;
import com.vladsch.md.nav.parser.MdParserOptions;
import com.vladsch.md.nav.parser.SyntheticFlexmarkNodes;
import com.vladsch.md.nav.parser.api.MdParser;
import com.vladsch.md.nav.parser.api.MdParserExtension;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;

import static com.vladsch.flexmark.ext.wikilink.WikiLinkExtension.LINK_FIRST_SYNTAX;
import static com.vladsch.md.nav.parser.MdLexParser.GITHUB_WIKI_LINKS;
import static com.vladsch.md.nav.parser.api.ParserPurpose.JAVAFX;
import static com.vladsch.md.nav.psi.util.MdTypes.NONE;
import static com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK;
import static com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK_CLOSE;
import static com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK_OPEN;
import static com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK_REF;
import static com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK_REF_ANCHOR;
import static com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK_REF_ANCHOR_MARKER;
import static com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK_SEPARATOR;
import static com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK_TEXT;

public class MdWikiLinkParserExtension implements MdParserExtension {
    protected static final WikiLinkHandlerOptions OPTIONS = new WikiLinkHandlerOptions();
    final public static DataKey<WikiLinkHandlerOptions> KEY = new DataKey<>(MdWikiLinkParserExtension.class.getName(), OPTIONS);

    @Override
    public DataKey<WikiLinkHandlerOptions> getKey() {
        return KEY;
    }

    @Override
    public void setFlexmarkOptions(final MdParserOptions options) {
        // set options for use when setting handlers
        MdRenderingProfile renderingProfile = options.getRenderingProfile();

        if (options.haveExtensions(Extensions.WIKILINKS)) {
            ParserPurpose parserPurpose = options.getParserPurpose();
            MdLinkResolver linkResolver = options.getLinkResolver();
            WikiLinkHandlerOptions handlerOptions = new WikiLinkHandlerOptions(renderingProfile);
            options.set(KEY, handlerOptions)
                    .addExtension(WikiLinkExtension.class, WikiLinkExtension::create)
                    .set(LINK_FIRST_SYNTAX, !handlerOptions.githubWikiLinks)
                    .set(WikiLinkExtension.ALLOW_ANCHORS, true);

            if (parserPurpose == JAVAFX || parserPurpose == ParserPurpose.SWING) {
                options.set(WikiLinkExtension.DISABLE_RENDERING, linkResolver == null || !linkResolver.getContainingFile().isWikiPage())
                        .set(WikiLinkExtension.ALLOW_ANCHORS, true);
            }
        }
    }

    @Override
    public void setFlexmarkHandlers(@NotNull final MdParser parser) {
        WikiLinkHandler handler = new WikiLinkHandler(parser);
        parser.addHandlers(WikiLinkVisitorExt.VISIT_HANDLERS(handler));
    }

    private static class WikiLinkHandlerOptions {
        final boolean githubWikiLinks;

        public WikiLinkHandlerOptions() {
            githubWikiLinks = true;
        }

        public WikiLinkHandlerOptions(MdRenderingProfile renderingProfile) {
            githubWikiLinks = (renderingProfile.getParserSettings().getOptionsFlags() & GITHUB_WIKI_LINKS) != 0;
        }
    }

    private static class WikiLinkHandler extends MdParserHandlerBase<WikiLinkHandlerOptions> implements WikiLinkVisitor {
        WikiLinkHandler(final MdParser parser) {
            super(parser, KEY);

//            if (myOptions.githubWikiLinks == parser.get(LINK_FIRST_SYNTAX)) {
//                Iterable<Extension> extensions = Parser.EXTENSIONS.get(parser);
//                for (Extension extension : extensions) {
//                    if (extension instanceof WikiLinkExtension) {
//                        assert myOptions.githubWikiLinks != parser.get(LINK_FIRST_SYNTAX);
//                    }
//                }
//            }
        }

        @Override
        public void visit(WikiLink node) {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, WIKI_LINK, NONE);

            nodes.addLeaf(node.getOpeningMarker(), WIKI_LINK_OPEN);
            if (node.getTextSeparatorMarker().isNotNull()) {
                if (node.getText().getStartOffset() < node.getPageRef().getStartOffset()) {
                    nodes.addComposite(node.getText(), WIKI_LINK_TEXT, WIKI_LINK_TEXT);

                    nodes.addLeaf(node.getTextSeparatorMarker(), WIKI_LINK_SEPARATOR);

                    nodes.addComposite(node.getPageRef(), WIKI_LINK_REF, MdTypes.WIKI_LINK_REF);
                    if (node.getAnchorMarker().isNotNull()) {
                        nodes.addLeaf(node.getAnchorMarker(), WIKI_LINK_REF_ANCHOR_MARKER);
                        nodes.addComposite(node.getAnchorRef(), WIKI_LINK_REF_ANCHOR, WIKI_LINK_REF_ANCHOR);
                    }
                } else {
                    nodes.addComposite(node.getPageRef(), WIKI_LINK_REF, MdTypes.WIKI_LINK_REF);
                    if (node.getAnchorMarker().isNotNull()) {
                        nodes.addLeaf(node.getAnchorMarker(), WIKI_LINK_REF_ANCHOR_MARKER);
                        nodes.addComposite(node.getAnchorRef(), WIKI_LINK_REF_ANCHOR, WIKI_LINK_REF_ANCHOR);
                    }

                    nodes.addLeaf(node.getTextSeparatorMarker(), WIKI_LINK_SEPARATOR);

                    nodes.addComposite(node.getText(), WIKI_LINK_TEXT, WIKI_LINK_TEXT);
                }
            } else {
                nodes.addComposite(node.getPageRef(), WIKI_LINK_REF, MdTypes.WIKI_LINK_REF);
                if (node.getAnchorMarker().isNotNull()) {
                    nodes.addLeaf(node.getAnchorMarker(), WIKI_LINK_REF_ANCHOR_MARKER);
                    nodes.addComposite(node.getAnchorRef(), WIKI_LINK_REF_ANCHOR, WIKI_LINK_REF_ANCHOR);
                }
            }
            nodes.addLeaf(node.getClosingMarker(), WIKI_LINK_CLOSE);
            addCompositeTokens(nodes);
        }
    }
}
