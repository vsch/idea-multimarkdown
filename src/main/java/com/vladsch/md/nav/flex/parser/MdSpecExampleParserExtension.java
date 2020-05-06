// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.parser;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterBlock;
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterExtension;
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterVisitor;
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterVisitorExt;
import com.vladsch.flexmark.ext.spec.example.*;
import com.vladsch.flexmark.ext.spec.example.internal.RenderAs;
import com.vladsch.flexmark.test.util.ExampleOption;
import com.vladsch.flexmark.test.util.spec.SpecReader;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.parser.MdParserOptions;
import com.vladsch.md.nav.parser.SyntheticFlexmarkNodes;
import com.vladsch.md.nav.parser.api.MdParser;
import com.vladsch.md.nav.parser.api.MdParserExtension;
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension;
import com.vladsch.md.nav.parser.parserExtensions.MdParserHandlerBase;
import com.vladsch.md.nav.flex.psi.FlexmarkOptionInfo;
import com.vladsch.md.nav.flex.psi.FlexmarkPsi;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.ParserOptions;
import com.vladsch.md.nav.flex.settings.FlexmarkHtmlSettings;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

import static com.vladsch.md.nav.psi.util.MdTypes.*;

public class MdSpecExampleParserExtension implements MdParserExtension {
    protected static final SpecExampleHandlerOptions OPTIONS = new SpecExampleHandlerOptions();
    final public static DataKey<SpecExampleHandlerOptions> KEY = new DataKey<>(MdSpecExampleParserExtension.class.getName(), OPTIONS);

    @Override
    public DataKey<SpecExampleHandlerOptions> getKey() {
        return KEY;
    }

    @Override
    public void setFlexmarkOptions(final MdParserOptions options) {
        // set options for use when setting handlers
        if (options.haveOptions(MdLexParser.FLEXMARK_FRONT_MATTER)) {
            MdRenderingProfile renderingProfile = options.getRenderingProfile();
            if (renderingProfile.getParserSettings().anyOptions(ParserOptions.FLEXMARK_FRONT_MATTER)) {
                FlexmarkHtmlSettings flexmarkHtmlSettings = renderingProfile.getHtmlSettings().getExtension(FlexmarkHtmlSettings.KEY);

                SpecExampleHandlerOptions handlerOptions = new SpecExampleHandlerOptions(renderingProfile);
                Map<Integer, String> languagesOnly = flexmarkHtmlSettings.getFlexmarkSectionLanguagesOnly();
                Map<Integer, String> sectionNames = flexmarkHtmlSettings.getFlexmarkSectionNames();
                options.set(KEY, handlerOptions)
                        .addExtension(SpecExampleExtension.class, SpecExampleExtension::create)
                        .addExtension(JekyllFrontMatterExtension.class, JekyllFrontMatterExtension::create)
                        .set(SpecExampleExtension.SPEC_EXAMPLE_SECTION_LANGUAGES, languagesOnly)
                        .set(SpecExampleExtension.SPEC_EXAMPLE_SECTION_NAMES, sectionNames)
                        .set(SpecExampleExtension.SPEC_EXAMPLE_RENDER_AS, flexmarkHtmlSettings.getFlexmarkSpecExampleRenderingType().renderAs)
                        .set(SpecExampleExtension.SPEC_EXAMPLE_RENDER_RAW_HTML, flexmarkHtmlSettings.getFlexmarkSpecExampleRenderHtml());

                Supplier<? extends MdLinkResolver> supplier = MdNavigatorExtension.LINK_RESOLVER.get(options);
                MdLinkResolver resolver = supplier == null ? null: supplier.get();
                boolean productionSpecParserMode = renderingProfile.getParserSettings().anyOptions(MdLexParser.PRODUCTION_SPEC_PARSER);
                boolean testSpecParserMode = !productionSpecParserMode && (ApplicationManager.getApplication().isUnitTestMode());

                if (resolver != null && resolver.getContainingFile() instanceof ProjectFileRef) {
                    PsiFile psiFile = ((ProjectFileRef) resolver.getContainingFile()).getPsiFile();
                    if (psiFile != null) {
                        boolean isInjected = MdPsiImplUtil.isInjectedMdElement(psiFile.getOriginalFile(), false, true);
                        if (isInjected) {
                            testSpecParserMode = true;
                        }
                    }
                }

                if (testSpecParserMode) {
                    options.set(SpecExampleExtension.SPEC_EXAMPLE_BREAK, SpecReader.EXAMPLE_TEST_BREAK)
                            .set(SpecExampleExtension.SPEC_SECTION_BREAK, SpecReader.SECTION_TEST_BREAK)
                            .set(SpecExampleExtension.SPEC_EXAMPLE_RENDER_AS, RenderAs.SECTIONS);
                }
            }
        }
    }

    @Override
    public void setFlexmarkHandlers(@NotNull final MdParser parser) {
        if (KEY.get(parser).parseFlexmarkFrontMatter) {
            SpecExampleHandler handler = new SpecExampleHandler(parser);
            parser.addHandlers(SpecExampleVisitorExt.VISIT_HANDLERS(handler))
                    .addHandlers(JekyllFrontMatterVisitorExt.VISIT_HANDLERS(handler));
        }
    }

    private static class SpecExampleHandlerOptions {
        final boolean parseFlexmarkFrontMatter;

        public SpecExampleHandlerOptions() {
            parseFlexmarkFrontMatter = false;
        }

        public SpecExampleHandlerOptions(MdRenderingProfile renderingProfile) {
            parseFlexmarkFrontMatter = (renderingProfile.getParserSettings().getOptionsFlags() & MdLexParser.FLEXMARK_FRONT_MATTER) != 0;
        }
    }

    private static class SpecExampleHandler extends MdParserHandlerBase<SpecExampleHandlerOptions> implements SpecExampleVisitor, JekyllFrontMatterVisitor {
        private boolean flexmarkFrontMatterType = false;
        final @Nullable Visitor<Node> myJekyllHandler;

        SpecExampleHandler(final MdParser parser) {
            super(parser, KEY);

            myJekyllHandler = parser.getVisitor(JekyllFrontMatterBlock.class);
        }

        @Override
        public void visit(final JekyllFrontMatterBlock node) {
            if (myOptions.parseFlexmarkFrontMatter && node.getClosingMarker().matches("...")) {
                flexmarkFrontMatterType = true;
                SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, FLEXMARK_FRONT_MATTER_BLOCK_ELEM, NONE);
                nodes.addLeaf(node.getOpeningMarker(), FLEXMARK_FRONT_MATTER_OPEN);
                nodes.addLeaf(node.getContent(), FLEXMARK_FRONT_MATTER_BLOCK);
                nodes.addLeaf(node.getClosingMarker(), FLEXMARK_FRONT_MATTER_CLOSE);
                addCompositeTokens(nodes);
            } else {
                if (myJekyllHandler != null) myJekyllHandler.visit(node);
            }
        }

        @Override
        public void visit(SpecExampleBlock node) {
        /*
        openingMarker
        exampleKeyword
        coordOpeningMarker
        section
        numberSeparator
        number
        coordClosingMarker
        optionsKeyword
        optionsOpeningMarker
        options
        optionsClosingMarker
        source
        htmlSeparator
        html
        astSeparator
        ast
        closingMarker
         */
            includeToTrailingEOL(node);

            if (flexmarkFrontMatterType) {
                SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, FLEXMARK_EXAMPLE, NONE);
                nodes.addLeaf(node.getOpeningMarker(), FLEXMARK_EXAMPLE_OPEN);
                nodes.addLeaf(node.getExampleKeyword(), FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD);
                nodes.addLeaf(node.getCoordOpeningMarker(), FLEXMARK_EXAMPLE_SECTION_OPEN);
                nodes.addLeaf(node.getSection(), FLEXMARK_EXAMPLE_SECTION);
                nodes.addLeaf(node.getNumberSeparator(), FLEXMARK_EXAMPLE_NUMBER_SEPARATOR);
                nodes.addLeaf(node.getNumber(), FLEXMARK_EXAMPLE_NUMBER);
                nodes.addLeaf(node.getCoordClosingMarker(), FLEXMARK_EXAMPLE_SECTION_CLOSE);
                nodes.addLeaf(node.getOptionsKeyword(), FLEXMARK_EXAMPLE_OPTIONS_KEYWORD);
                nodes.addLeaf(node.getOptionsOpeningMarker(), FLEXMARK_EXAMPLE_OPTIONS_OPEN);
                nodes.addLeaf(node.getOptionsClosingMarker(), FLEXMARK_EXAMPLE_OPTIONS_CLOSE);
                nodes.addLeaf(node.getClosingMarker(), FLEXMARK_EXAMPLE_CLOSE);
                addCompositeTokensWithChildren(nodes);
            } else {
                addToken(node, TEXT);
            }
        }

        @Override
        public void visit(SpecExampleOptionsList node) {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, FLEXMARK_EXAMPLE_OPTIONS, NONE);
            addCompositeTokensWithChildren(nodes);
        }

        @Override
        public void visit(SpecExampleOption node) {
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, FLEXMARK_EXAMPLE_OPTION, FLEXMARK_EXAMPLE_OPTION_TYPE);
            FlexmarkOptionInfo flexmarkOptionInfo = FlexmarkPsi.getBuiltInFlexmarkOption(node.getChars());
            if (flexmarkOptionInfo == null) {
                ExampleOption exampleOption = ExampleOption.of(node.getChars());
                if (exampleOption.isCustom) {
                    BasedSequence name = exampleOption.optionName;
                    BasedSequence param = exampleOption.customParams;
                    nodes.addComposite(name, FLEXMARK_EXAMPLE_OPTION_NAME, exampleOption.isDisabled() ? FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME : FLEXMARK_EXAMPLE_OPTION_NAME);
                    nodes.addLeaf(node.getChars().baseSubSequence(name.getEndOffset(), param.getStartOffset()), FLEXMARK_EXAMPLE_OPTION_PARAM_OPEN);
                    nodes.addLeaf(param, FLEXMARK_EXAMPLE_OPTION_PARAM);
                    nodes.addLeaf(node.getChars().baseSubSequence(param.getEndOffset()), FLEXMARK_EXAMPLE_OPTION_PARAM_CLOSE);
                } else {
                    nodes.addComposite(node.getChars(), FLEXMARK_EXAMPLE_OPTION_NAME, exampleOption.isDisabled() ? FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME : FLEXMARK_EXAMPLE_OPTION_NAME);
                }
            } else {
                nodes.addComposite(node.getChars(), FLEXMARK_EXAMPLE_OPTION_NAME, flexmarkOptionInfo.isDisabled() ? FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME : flexmarkOptionInfo.elementType);
            }
            addCompositeTokensWithChildren(nodes);
        }

        @Override
        public void visit(SpecExampleOptionSeparator node) {
            addToken(node, FLEXMARK_EXAMPLE_OPTION_SEPARATOR);
        }

        @Override
        public void visit(SpecExampleSeparator node) {
            addToken(node, FLEXMARK_EXAMPLE_SEPARATOR);
        }

        @Override
        public void visit(SpecExampleSource node) {
            includeTrailingEOL(node);
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, FLEXMARK_EXAMPLE_SOURCE, FLEXMARK_EXAMPLE_SOURCE);
            addCompositeTokensWithChildren(nodes);
        }

        @Override
        public void visit(SpecExampleHtml node) {
            includeTrailingEOL(node);
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, FLEXMARK_EXAMPLE_HTML, FLEXMARK_EXAMPLE_HTML);
            addCompositeTokensWithChildren(nodes);
        }

        @Override
        public void visit(SpecExampleAst node) {
            includeTrailingEOL(node);
            SyntheticFlexmarkNodes nodes = new SyntheticFlexmarkNodes(node, FLEXMARK_EXAMPLE_AST, FLEXMARK_EXAMPLE_AST);
            addCompositeTokensWithChildren(nodes);
        }
    }
}
