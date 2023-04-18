// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.parserExtensions;

import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterBlock;
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterExtension;
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterVisitor;
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterVisitorExt;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTag;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagBlock;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagVisitor;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagVisitorExt;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.parser.MdParserOptions;
import com.vladsch.md.nav.parser.SyntheticFlexmarkNodes;
import com.vladsch.md.nav.parser.api.MdParser;
import com.vladsch.md.nav.parser.api.MdParserExtension;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;

import static com.vladsch.md.nav.parser.MdLexParser.JEKYLL_FRONT_MATTER;
import static com.vladsch.md.nav.psi.util.MdTypes.*;

public class MdJekyllExtension implements MdParserExtension {
    protected static final JekyllFrontMatterHandlerOptions OPTIONS = new JekyllFrontMatterHandlerOptions();
    final public static DataKey<JekyllFrontMatterHandlerOptions> KEY = new DataKey<>(MdJekyllExtension.class.getName(), OPTIONS);

    @Override
    public DataKey<JekyllFrontMatterHandlerOptions> getKey() {
        return KEY;
    }

    @Override
    public void setFlexmarkOptions(final MdParserOptions options) {
        // set options for use when setting handlers
        if (options.haveOptions(JEKYLL_FRONT_MATTER)) {
            options.addExtension(JekyllFrontMatterExtension.class, JekyllFrontMatterExtension::create)
                    .addExtension(JekyllTagExtension.class, JekyllTagExtension::create)
                    .set(Parser.PARSE_JEKYLL_MACROS_IN_URLS, true)
                    .set(Parser.PARSE_JEKYLL_MACROS_IN_URLS, true)
                    .set(JekyllTagExtension.ENABLE_INLINE_TAGS, false)
                    .set(JekyllTagExtension.EMBED_INCLUDED_CONTENT, false)
            ;
        }
    }

    @Override
    public void setFlexmarkHandlers(@NotNull final MdParser parser) {
        JekyllFrontMatterHandler handler = new JekyllFrontMatterHandler(parser);
        parser.addHandlers(
                JekyllFrontMatterVisitorExt.VISIT_HANDLERS(handler),
                JekyllTagVisitorExt.VISIT_HANDLERS(handler)
        );
    }

    private static class JekyllFrontMatterHandlerOptions {
        public JekyllFrontMatterHandlerOptions() {
        }

        public JekyllFrontMatterHandlerOptions(MdRenderingProfile renderingProfile) {
        }
    }

    private static class JekyllFrontMatterHandler extends MdParserHandlerBase<JekyllFrontMatterHandlerOptions> implements JekyllFrontMatterVisitor, JekyllTagVisitor {
        JekyllFrontMatterHandler(final MdParser parser) {
            super(parser, KEY);
        }

        @Override
        public void visit(final JekyllFrontMatterBlock node) {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, JEKYLL_FRONT_MATTER_BLOCK_ELEM, NONE);
            nodes.addLeaf(node.getOpeningMarker(), JEKYLL_FRONT_MATTER_OPEN);
            nodes.addLeaf(node.getContent(), JEKYLL_FRONT_MATTER_BLOCK);
            nodes.addLeaf(node.getClosingMarker(), JEKYLL_FRONT_MATTER_CLOSE);
            addCompositeTokens(nodes);
        }

        @Override
        public void visit(JekyllTag node) {
            if (node.getTag().matches("include")) {
                SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, JEKYLL_INCLUDE_TAG_ELEM, NONE);
                nodes.addLeaf(node.getOpeningMarker(), JEKYLL_TAG_OPEN);
                nodes.addLeaf(node.getTag(), JEKYLL_TAG_NAME);
                BasedSequence linkRefText = node.getParameters();
                BasedSequence linkRefClose = BasedSequence.NULL;
                if (linkRefText.startsWith("\"") && linkRefText.endsWith("\"")) {
                    nodes.addLeaf(linkRefText.subSequence(0, 1), JEKYLL_LINKREF_OPEN);
                    linkRefClose = linkRefText.subSequence(linkRefText.length() - 1, linkRefText.length());
                    linkRefText = linkRefText.subSequence(1, linkRefText.length() - 1);
                } else if (linkRefText.startsWith("'") && linkRefText.endsWith("'")) {
                    nodes.addLeaf(linkRefText.subSequence(0, 1), JEKYLL_LINKREF_OPEN);
                    linkRefClose = linkRefText.subSequence(linkRefText.length() - 1, linkRefText.length());
                    linkRefText = linkRefText.subSequence(1, linkRefText.length() - 1);
                }
                nodes.addComposite(linkRefText, JEKYLL_INCLUDE_TAG_LINK_REF, MdTypes.JEKYLL_TAG_PARAMETERS);
                if (linkRefClose.isNotNull()) {
                    nodes.addLeaf(linkRefClose, JEKYLL_LINKREF_CLOSE);
                }
                nodes.addLeaf(node.getClosingMarker(), JEKYLL_TAG_CLOSE);
                addCompositeTokens(nodes);
            } else {
                SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, JEKYLL_TAG_BLOCK_ELEM, NONE);
                nodes.addLeaf(node.getOpeningMarker(), JEKYLL_TAG_OPEN);
                nodes.addLeaf(node.getTag(), JEKYLL_TAG_NAME);
                nodes.addLeaf(node.getParameters(), JEKYLL_TAG_PARAMETERS);
                nodes.addLeaf(node.getClosingMarker(), JEKYLL_TAG_CLOSE);
                addCompositeTokens(nodes);
            }
        }

        @Override
        public void visit(final JekyllTagBlock block) {
            visitChildren(block);
        }
    }
}
