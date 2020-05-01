// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.intellij.openapi.application.ApplicationManager;
import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ast.util.AttributeProviderAdapter;
import com.vladsch.flexmark.ast.util.AttributeProvidingHandler;
import com.vladsch.flexmark.ext.emoji.Emoji;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListItem;
import com.vladsch.flexmark.ext.tables.TableHead;
import com.vladsch.flexmark.ext.tables.TableRow;
import com.vladsch.flexmark.ext.wikilink.WikiLink;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.html.Attribute;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.parser.api.MdLinkMapProvider;
import com.vladsch.md.nav.psi.element.MdImageMultiLineUrlContentImpl;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.util.ImageLinkRef;
import com.vladsch.md.nav.util.Links;
import com.vladsch.md.nav.util.Local;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.util.Remote;
import com.vladsch.md.nav.util.Want;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import com.vladsch.plugin.util.image.ImageUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import static com.vladsch.flexmark.ext.gfm.tasklist.internal.TaskListNodeRenderer.TASK_ITEM_PARAGRAPH;
import static com.vladsch.flexmark.ext.wikilink.WikiLinkExtension.WIKI_LINK;
import static com.vladsch.flexmark.html.renderer.AttributablePart.LINK;
import static com.vladsch.flexmark.html.renderer.AttributablePart.NODE;
import static com.vladsch.flexmark.html.renderer.CoreNodeRenderer.LOOSE_LIST_ITEM;
import static com.vladsch.flexmark.html.renderer.CoreNodeRenderer.TIGHT_LIST_ITEM;
import static com.vladsch.flexmark.util.html.Attribute.LINK_STATUS_ATTR;
import static com.vladsch.md.nav.settings.MdApplicationSettings.getInstance;
import static com.vladsch.md.nav.util.PathInfo.isAbsolute;
import static com.vladsch.md.nav.util.PathInfo.isFileURI;

public class FlexmarkAttributeProvider implements AttributeProvider {
    final static public com.vladsch.flexmark.util.data.DataKey<String> FENCED_CODE_PRE_CLASS = new com.vladsch.flexmark.util.data.DataKey<>("FENCED_CODE_PRE_CLASS", "");
    final static public com.vladsch.flexmark.util.data.DataKey<String> INDENTED_CODE_PRE_CLASS = new DataKey<>("INDENTED_CODE_PRE_CLASS", "");
    final static public DataKey<String> IMAGE_STYLE = new com.vladsch.flexmark.util.data.DataKey<>("IMAGE_STYLE", "");
    public static final String TASK_ITEM_OFFSET_ATTRIBUTE = "task-offset";
    final static public com.vladsch.flexmark.util.data.DataKey<Boolean> EMBED_IMAGES = new com.vladsch.flexmark.util.data.DataKey<>("EMBED_IMAGES", true);
    final static public com.vladsch.flexmark.util.data.DataKey<Boolean> EMBED_HTTP_IMAGES = new com.vladsch.flexmark.util.data.DataKey<>("EMBED_HTTP_IMAGES", false);

    @NotNull final protected String missingTargetClass;
    @NotNull final protected String localOnlyTargetClass;
    @NotNull final protected AttributeProviderAdapter nodeAdapter;
    @NotNull final protected String sourcePositionAttribute;
    @NotNull final protected String fencedCodePreTagClass;
    @NotNull final protected String indentedCodePreTagClass;
    @NotNull final protected String imageStyle;
    final protected boolean useSwingAttributes;
    final protected boolean isWikiPage;
    private final boolean embedImages;
    private final boolean embedHttpImages;
    private final boolean htmlExport;
    private boolean isFirstH1 = false;
    private final MdLinkResolver resolver;
    final private boolean showUnresolvedLinks;

    public FlexmarkAttributeProvider(LinkResolverContext context) {
        DataHolder options = context.getOptions();
        localOnlyTargetClass = MdNavigatorExtension.LOCAL_ONLY_TARGET_CLASS.get(options);
        missingTargetClass = MdNavigatorExtension.MISSING_TARGET_CLASS.get(options);
        useSwingAttributes = MdNavigatorExtension.USE_SWING_ATTRIBUTES.get(options);
        isWikiPage = MdNavigatorExtension.IS_WIKI_PAGE.get(options);
        sourcePositionAttribute = HtmlRenderer.SOURCE_POSITION_ATTRIBUTE.get(options);
        fencedCodePreTagClass = FENCED_CODE_PRE_CLASS.get(options);
        indentedCodePreTagClass = INDENTED_CODE_PRE_CLASS.get(options);
        imageStyle = IMAGE_STYLE.get(options);
        Supplier<? extends MdLinkResolver> supplier = MdNavigatorExtension.LINK_RESOLVER.get(options);
        resolver = supplier == null ? null : supplier.get();
        embedImages = FlexmarkAttributeProvider.EMBED_IMAGES.get(options);
        embedHttpImages = FlexmarkAttributeProvider.EMBED_HTTP_IMAGES.get(options);
        htmlExport = MdNavigatorExtension.HTML_EXPORT.get(options);

        Boolean showUnresolvedLinkRefs = null;
        if (resolver != null) {
            MdRenderingProfile renderingProfile = resolver.renderingProfile();
            if (renderingProfile != null) {
                for (MdLinkMapProvider provider : MdLinkMapProvider.EXTENSIONS.getValue()) {
                    showUnresolvedLinkRefs = provider.showUnresolvedLinkRefs(renderingProfile);
                    if (showUnresolvedLinkRefs != null) {
                        break;
                    }
                }
            }
        }

        this.showUnresolvedLinks = showUnresolvedLinkRefs != null && showUnresolvedLinkRefs;

        AttributeProviderAdapter adapter = new AttributeProviderAdapter(
                new AttributeProvidingHandler<>(Image.class, this::setImageAttributes),
                new AttributeProvidingHandler<>(ImageRef.class, this::setImageRefAttributes),
                new AttributeProvidingHandler<>(com.vladsch.flexmark.ast.LinkRef.class, this::setLinkRefAttributes),
                new AttributeProvidingHandler<>(Link.class, this::setLinkAttributes),
                new AttributeProvidingHandler<>(FencedCodeBlock.class, this::setFencedCodeAttributes),
                new AttributeProvidingHandler<>(IndentedCodeBlock.class, this::setIndentedCodeAttributes),
                new AttributeProvidingHandler<>(TaskListItem.class, this::setTaskListItemAttributes),
                new AttributeProvidingHandler<>(Emoji.class, this::setEmojiAttributes),
                new AttributeProvidingHandler<>(WikiLink.class, this::setWikiLinkAttributes)
        );

        if (useSwingAttributes) {
            adapter.addHandlers(
                    new AttributeProvidingHandler<>(TableRow.class, this::setTableRowAttributes),
                    new AttributeProvidingHandler<>(Paragraph.class, this::setParagraphAttributes),
                    new AttributeProvidingHandler<>(BulletListItem.class, this::setListItemAttributes),
                    new AttributeProvidingHandler<>(OrderedListItem.class, this::setListItemAttributes)
            );

            if (isWikiPage) {
                isFirstH1 = true;
                adapter.addHandler(new AttributeProvidingHandler<>(Heading.class, this::setHeadingAttributes));
            }
        }

        this.nodeAdapter = adapter;
    }

    /**
     * Replace image src attribute if embedding images
     *
     * @param attributes      attributes to be replaced
     * @param resolver        link resolver
     * @param embedImages     embed images
     * @param embedHttpImages embed http images
     */
    public static void embedImage(
            Attributes attributes,
            final MdLinkResolver resolver,
            final boolean embedImages,
            final boolean embedHttpImages
    ) {
        if (embedImages) {
            String src = attributes.getValue("src");
            if (PathInfo.isURL(src)) {
                if (embedHttpImages) {
                    // process gravizo and SVG image URLs
                    final PathInfo urlPath = new PathInfo(src);
                    final String urlPathFilePath = urlPath.getFilePath();
                    final int protocolLength = urlPath.getProtocolURIPrefix() == null ? 0 : urlPath.getProtocolURIPrefix().length();
                    String bareUrl = urlPathFilePath.substring(protocolLength);
                    boolean isUrlPath = urlPath.isURL();
                    boolean isGravizoPng = isUrlPath && bareUrl.startsWith(MdImageMultiLineUrlContentImpl.GRAVIZO_PNG_PREFIX_Q);
                    boolean isGravizoSvg = urlPath.isURI() && bareUrl.startsWith(MdImageMultiLineUrlContentImpl.GRAVIZO_SVG_PREFIX_Q);
                    boolean isCodeCogsSvg = isUrlPath && bareUrl.startsWith(MdImageMultiLineUrlContentImpl.CODECOGS_SVG_PREFIX_Q);
//                    boolean isCodeCogsPng = urlPath.isURL() && bareUrl.startsWith(MdImageLinkRefUrlContentImpl.CODECOGS_PNG_PREFIX_Q);

                    if (isGravizoPng) {
                        // change to SVG because PNG does not load
                        src = src.replace("//g.gravizo.com/g?", "//g.gravizo.com/svg?");
                    }

                    final BufferedImage bufferedImage;
                    if (".svg".equals(urlPath.getExtWithDot()) || isGravizoSvg || isGravizoPng || isCodeCogsSvg) {
                        bufferedImage = ImageUtils.loadSvgImageFromURL(src, 1.0f);
                    } else {
                        bufferedImage = ImageUtils.loadImageFromURL(src);
                    }

                    if (bufferedImage != null) {
                        attributes.replaceValue("src", ImageUtils.base64Encode(bufferedImage));
                    }
                }
            } else if (resolver != null && !ImageUtils.isPossiblyEncodedImage(src)) {
                final PathInfo pathInfo;
                if (isFileURI(src) && isAbsolute(src) && !ApplicationManager.getApplication().isUnitTestMode()) {
                    pathInfo = new PathInfo(PathInfo.removeFileUriPrefix(src));
                } else {
                    pathInfo = resolver.resolve(ImageLinkRef.parseLinkRef(resolver.getContainingFile(), src, null), Want.INSTANCE.invoke(Local.getREF(), Remote.getREF(), Links.getNONE()), null);
                }

                if (pathInfo != null) {
                    if (pathInfo instanceof ProjectFileRef && pathInfo.getVirtualFile() != null) {
                        try {
                            byte[] imageBytes = pathInfo.getVirtualFile().contentsToByteArray();
                            attributes.replaceValue("src", ImageUtils.base64Encode(imageBytes));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        File imageFile = new File(pathInfo.getFilePathNoQuery());
                        if (imageFile.exists()) {
                            final BufferedImage bufferedImage = ImageUtils.loadImageFromFile(imageFile);
                            if (bufferedImage != null) {
                                attributes.replaceValue("src", ImageUtils.base64Encode(bufferedImage));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setAttributes(@NotNull Node node, @NotNull AttributablePart part, @NotNull Attributes attributes) {
        nodeAdapter.setAttributes(node, part, attributes);
    }

    void setTableRowAttributes(TableRow node, AttributablePart part, Attributes attributes) {
        int rowNumber = node.getRowNumber();
        if (rowNumber != 0) {
            String rowType = node.getParent() instanceof TableHead ? "thead-" : "tbody-";
            attributes.replaceValue("class", rowType + (rowNumber == 1 ? "first-child" : (rowNumber & 1) != 0 ? "odd-child" : "even-child"));
        }
    }

    void setParagraphAttributes(Paragraph node, AttributablePart part, Attributes attributes) {
        if (node.getParent() instanceof ListItem && ((ListItem) node.getParent()).isItemParagraph(node)) {
            attributes.replaceValue("class", "p");
        }
    }

    void setListItemAttributes(ListItem node, AttributablePart part, Attributes attributes) {
        attributes.replaceValue("class", part == LOOSE_LIST_ITEM ? "p" : "");
    }

    void setTaskListItemAttributes(TaskListItem node, AttributablePart part, Attributes attributes) {
        if (useSwingAttributes) {
            if (part == TASK_ITEM_PARAGRAPH) {
                attributes.replaceValue("class", "p");
            } else if (part == TIGHT_LIST_ITEM || part == LOOSE_LIST_ITEM) {
                if (getInstance().getDebugSettings().getTaskItemImages()) {
                    if (node.isItemDoneMarker()) attributes.replaceValue("class", part == LOOSE_LIST_ITEM ? "img-dtaskp" : "img-dtask");
                    else attributes.replaceValue("class", part == LOOSE_LIST_ITEM ? "img-taskp" : "img-taski");
                } else {
                    if (node.isItemDoneMarker()) attributes.replaceValue("class", part == LOOSE_LIST_ITEM ? "dtaskp" : "dtask");
                    else attributes.replaceValue("class", part == LOOSE_LIST_ITEM ? "taskp" : "taski");
                }
            }
        } else {
            // need to add source info for click-back
            if (part == LOOSE_LIST_ITEM || part == TIGHT_LIST_ITEM) {
                final BasedSequence markerSuffix = node.getMarkerSuffix();
                attributes.replaceValue(TASK_ITEM_OFFSET_ATTRIBUTE, String.format("%d", (markerSuffix.getStartOffset() + markerSuffix.getEndOffset()) / 2));
            }
        }
    }

    void setHeadingAttributes(Heading node, AttributablePart part, Attributes attributes) {
        if (isFirstH1 && node.getLevel() == 1) {
            isFirstH1 = false;
            attributes.replaceValue("class", "first-child");
        }
    }

    void setWikiLinkAttributes(WikiLink node, AttributablePart part, Attributes attributes) {
        setLinkAttributes(WIKI_LINK, part, attributes);
    }

    void setFencedCodeAttributes(FencedCodeBlock node, AttributablePart part, Attributes attributes) {
        if (!fencedCodePreTagClass.isEmpty() && part == NODE) {
            attributes.addValue("class", fencedCodePreTagClass);
        }
    }

    void setIndentedCodeAttributes(IndentedCodeBlock node, AttributablePart part, Attributes attributes) {
        if (!indentedCodePreTagClass.isEmpty() && part == NODE) {
            attributes.addValue("class", indentedCodePreTagClass);
        }
    }

    void setImageAttributes(Image node, AttributablePart part, Attributes attributes) {
        if (!imageStyle.isEmpty()) {
            attributes.addValue(Attribute.STYLE_ATTR, imageStyle);
        }
        setLinkAttributes(LinkType.IMAGE, part, attributes);
        embedImage(attributes, resolver, embedImages, embedHttpImages);
    }

    void setEmojiAttributes(Emoji node, AttributablePart part, Attributes attributes) {
        //setLinkAttributes(LinkType.IMAGE, part, attributes);
        embedImage(attributes, resolver, embedImages, embedHttpImages);
    }

    void setImageRefAttributes(ImageRef node, AttributablePart part, Attributes attributes) {
        if (!imageStyle.isEmpty()) {
            attributes.addValue(Attribute.STYLE_ATTR, imageStyle);
        }
        setLinkAttributes(LinkType.IMAGE, part, attributes);
        embedImage(attributes, resolver, embedImages, embedHttpImages);
    }

    void setLinkAttributes(Link node, AttributablePart part, Attributes attributes) {
        setLinkAttributes(LinkType.LINK, part, attributes);
    }

    void setLinkRefAttributes(com.vladsch.flexmark.ast.LinkRef node, AttributablePart part, Attributes attributes) {
        setLinkAttributes(LinkType.LINK, part, attributes);
    }

    void setLinkAttributes(LinkType linkType, AttributablePart part, Attributes attributes) {
        if (part == LINK) {
            String linkStatus = attributes.getValue(LINK_STATUS_ATTR);

            String appendTitle = attributes.getValue("title");
            if (!htmlExport && showUnresolvedLinks) {
                if (LinkStatus.NOT_FOUND.isStatus(linkStatus)) {
                    attributes.addValue("class", missingTargetClass);
                } else if (MdNavigatorExtension.LOCAL_ONLY.isStatus(linkStatus)) {
                    attributes.addValue("class", localOnlyTargetClass);
                    appendTitle += " **" + MdBundle.message("annotation.link.not-vcs-target") + "**";
                }
            }

            if (linkType != WIKI_LINK && !appendTitle.isEmpty()) {
                attributes.replaceValue("title", appendTitle);
            }
        }
    }

    public static class Factory extends IndependentAttributeProviderFactory {
        @NotNull
        @Override
        public AttributeProvider apply(@NotNull LinkResolverContext context) {
            return new FlexmarkAttributeProvider(context);
        }
    }
}
