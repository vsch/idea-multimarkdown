// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.intellij.openapi.project.Project;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ext.gitlab.GitLabInlineMath;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.parser.api.MdFencedCodeImage;
import com.vladsch.md.nav.parser.api.MdFencedCodeImageConverter;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * The node renderer that renders all the core nodes (comes last in the order of node renderers).
 */
@SuppressWarnings("WeakerAccess")
public class MdNavigatorDiagramNodeRenderer implements NodeRenderer {
    final public static String MATH_LANGUAGE_INFO = "math";
    final public static String LATEX_LANGUAGE_INFO = "latex";

    final private static String[] MATH_LATEX_LANGUAGE_INFOS = { MATH_LANGUAGE_INFO, LATEX_LANGUAGE_INFO };
    final private static String[] MATH_ONLY_LANGUAGE_INFOS = { MATH_LANGUAGE_INFO };

    public static String[] getMathLanguageInfos(@NotNull Project project) {
        return isLatexMathAlias(project) ? MATH_LATEX_LANGUAGE_INFOS : MATH_ONLY_LANGUAGE_INFOS;
    }

    private static boolean isLatexMathAlias(@NotNull Project project) {
        return !MdFencedCodeImageConversionManager.getInstance(project).getImageConverters(LATEX_LANGUAGE_INFO).isEmpty();
    }

    final private @NotNull Supplier<? extends MdRenderingProfile> myRenderingProfileSupplier;
    final private @NotNull Supplier<? extends MdLinkResolver> myLinkResolverSupplier;
    private MdFencedCodeImageConversionManager myImageConversionManager;

    public MdNavigatorDiagramNodeRenderer(DataHolder options) {
        myLinkResolverSupplier = MdNavigatorExtension.LINK_RESOLVER.get(options);
        myRenderingProfileSupplier = MdNavigatorExtension.RENDERING_PROFILE.get(options);
    }

    @Nullable
    MdFencedCodeImageConversionManager getImageConversionManager() {
        if (myImageConversionManager == null) {
            MdLinkResolver linkResolver = myLinkResolverSupplier.get();
            if (linkResolver != null) {
                Project project = linkResolver.getProject();
                if (project != null) {
                    myImageConversionManager = MdFencedCodeImageConversionManager.getInstance(project);
                }
            }
        }
        return myImageConversionManager;
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> nodeRenderingHandlers = new HashSet<>();

        nodeRenderingHandlers.add(new NodeRenderingHandler<>(GitLabInlineMath.class, MdNavigatorDiagramNodeRenderer.this::render));
        nodeRenderingHandlers.add(new NodeRenderingHandler<>(FencedCodeBlock.class, MdNavigatorDiagramNodeRenderer.this::render));

        return nodeRenderingHandlers;
    }

    void render(FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
        BasedSequence nodeInfo = node.getInfo();
        int pos = nodeInfo.indexOf(' ');
        String infoString = pos == -1 ? nodeInfo.toString() : nodeInfo.subSequence(0, pos).toString();

        MdLinkResolver linkResolver = myLinkResolverSupplier.get();
        MdRenderingProfile renderingProfile = myRenderingProfileSupplier.get();
        MdFencedCodeImageConversionManager imageConversionManager = getImageConversionManager();

        if (imageConversionManager != null && renderingProfile != null && linkResolver != null) {
            String conversionVariant = renderingProfile.getHtmlSettings().getFencedCodeConversions().get(infoString);

            if (conversionVariant != null) {
                MdFencedCodeImageConverter converter = imageConversionManager.getImageConverter(infoString, conversionVariant);
                if (converter != null) {
                    String content = nodeInfo.getBuilder().addAll(node.getContentLines()).toString();
                    MdFencedCodeImage codeImage = converter.imageUrl(content, infoString, conversionVariant, renderingProfile, linkResolver);
                    if (codeImage != null) {
                        renderImageUrl(codeImage.url, node.getChars(), context, html, codeImage.imageAttributes, codeImage.isBlock, codeImage.blockAttributes);
                    } else {
                        converter.defaultRender(infoString, conversionVariant, node, context, html);
                    }
                    return;
                }
            }
        }

        // use default renderer
        context.delegateRender();
    }

    void render(GitLabInlineMath node, NodeRendererContext context, HtmlWriter html) {
        MdRenderingProfile renderingProfile = myRenderingProfileSupplier.get();
        MdLinkResolver linkResolver = myLinkResolverSupplier.get();
        MdFencedCodeImageConversionManager imageConversionManager = getImageConversionManager();

        if (imageConversionManager != null && renderingProfile != null && linkResolver != null) {
            String infoString = "math";
            String conversionVariant = renderingProfile.getHtmlSettings().getFencedCodeConversions().get(infoString);

            if (conversionVariant != null) {
                MdFencedCodeImageConverter converter = imageConversionManager.getImageConverter(infoString, conversionVariant);
                if (converter != null) {
                    String content = node.getText().toString();
                    MdFencedCodeImage codeImage = converter.imageUrl(content, infoString, conversionVariant, renderingProfile, linkResolver);
                    if (codeImage != null) {
                        renderImageUrl(codeImage.url, node.getChars(), context, html, codeImage.imageAttributes, false, null);
                        return;
                    }
                }
            }
        }

        // use default renderer
        context.delegateRender();
    }

    private static void renderImageUrl(@NotNull String url, @NotNull BasedSequence nodeChars, @NotNull NodeRendererContext context, @NotNull HtmlWriter html, @Nullable Attributes imageAttributes, boolean isBlock, @Nullable Attributes blockAttributes) {
        Image imageNode = new Image();
        imageNode.setChars(nodeChars);
        imageNode.setUrl(BasedSequence.of(url));

        //ResolvedLink resolvedLink = context.resolveLink(LinkType.IMAGE, url, false);
        if (isBlock) {
            if (blockAttributes != null) html.attr(blockAttributes);
            html.withAttr().tag("p");
        }
        if (imageAttributes != null) html.attr(imageAttributes);
        context.render(imageNode);
        if (isBlock) html.closeTag("p").line();
    }

    // REFACTOR: move this functionality to MdFencedCodeImageConverter
    final public static Pattern START_UML_PATTERN = Pattern.compile("^@startuml(?:;?\\s*(?:\n|$))");
    final public static Pattern END_UML_PATTERN = Pattern.compile("^@enduml(?:;?\\s*(?:\n|$))");

    static public void appendContentLines(@NotNull List<CharSequence> contentLines, @NotNull final StringBuilder contentText, @Nullable final CharSequence prefix, final boolean wrapInStartEndUml, final boolean suffixSemi) {
        int iMax = contentLines.size();
        CharSequence usePrefix = prefix == null ? "" : prefix;

        for (int i = 0; i < iMax; i++) {
            BasedSequence line = BasedSequence.of(contentLines.get(i));
            if (wrapInStartEndUml) {
                if (i == 0 && !START_UML_PATTERN.matcher(line).find()) {
                    contentText.append(usePrefix);
                    contentText.append("@startuml");
                    if (suffixSemi) contentText.append(";");
                    contentText.append("\n");
                }
            }

            BasedSequence trimmed = line.trimEnd();
            contentText.append(usePrefix);
            contentText.append(trimmed);

            if (suffixSemi && !trimmed.endsWith(";")) {
                contentText.append(';');
            }

            contentText.append('\n');

            if (wrapInStartEndUml) {
                if (i == iMax - 1 && !END_UML_PATTERN.matcher(line).find()) {
                    contentText.append(usePrefix);
                    contentText.append("@enduml");
                    if (suffixSemi) contentText.append(";");
                    contentText.append("\n");
                }
            }
        }
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull final DataHolder options) {
            return new MdNavigatorDiagramNodeRenderer(options);
        }
    }
}
