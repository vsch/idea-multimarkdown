/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.util.FileRef;
import com.vladsch.idea.multimarkdown.util.LinkRef;
import com.vladsch.idea.multimarkdown.util.PathInfo;
import com.vladsch.idea.multimarkdown.util.WikiLinkRef;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;
import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*;

public class MultiMarkdownPsiImplUtil {
    private static final Logger logger = Logger.getLogger(MultiMarkdownPsiImplUtil.class);
    final protected static int EXTENSION_STRIP = 1;
    final protected static int EXTENSION_KEEP_OLD = 2;
    final protected static int EXTENSION_USE_NEW_IF_OLD_HAS = 3;
    final protected static int EXTENSION_USE_NEW = 4;

    final public static LinkRefElementTypes WIKI_LINK_ELEMENT = new LinkRefElementTypes(WIKI_LINK, WIKI_LINK_REF, WIKI_LINK_TEXT, WIKI_LINK_REF_ANCHOR, EXTENSION_STRIP);
    final public static LinkRefElementTypes EXPLICIT_LINK_ELEMENT = new LinkRefElementTypes(EXPLICIT_LINK, LINK_REF, LINK_REF_TEXT, LINK_REF_ANCHOR, LINK_REF_TITLE, EXTENSION_USE_NEW_IF_OLD_HAS);
    final public static LinkRefElementTypes IMAGE_LINK_ELEMENT = new LinkRefElementTypes(IMAGE, IMAGE_LINK_REF, IMAGE_LINK_REF_TEXT, null, IMAGE_LINK_REF_TITLE, EXTENSION_USE_NEW);

    public static class LinkRefElementTypes {
        @NotNull public final IElementType parentType;
        @NotNull public final IElementType linkRefType;
        @NotNull public final IElementType textType;
        @Nullable public final IElementType anchorType;
        @Nullable public final IElementType titleType;
        public final int extensionFlags;

        public LinkRefElementTypes(@NotNull IElementType parentType, @NotNull IElementType linkRefType, @NotNull IElementType textType, @Nullable IElementType anchorType, int extensionFlags) {
            this(parentType, linkRefType, textType, anchorType, null, extensionFlags);
        }

        public LinkRefElementTypes(@NotNull IElementType parentType, @NotNull IElementType linkRefType, @NotNull IElementType textType, @Nullable IElementType anchorType, @Nullable IElementType titleType, int extensionFlags) {
            this.parentType = parentType;
            this.linkRefType = linkRefType;
            this.textType = textType;
            this.anchorType = anchorType;
            this.titleType = titleType;
            this.extensionFlags = extensionFlags;
        }
    }

    public static LinkRefElementTypes getNamedElementTypes(@Nullable PsiElement element) {
        if (element instanceof MultiMarkdownImageLink
                || element instanceof MultiMarkdownImageLinkRef
                || element instanceof MultiMarkdownImageLinkText
                || element instanceof MultiMarkdownImageLinkTitle
                ) return IMAGE_LINK_ELEMENT;
        if (element instanceof MultiMarkdownExplicitLink
                || element instanceof MultiMarkdownLinkRef
                || element instanceof MultiMarkdownLinkText
                || element instanceof MultiMarkdownLinkTitle
                || element instanceof MultiMarkdownLinkAnchor
                ) return EXPLICIT_LINK_ELEMENT;
        if (element instanceof MultiMarkdownWikiLink
                || element instanceof MultiMarkdownWikiLinkRef
                || element instanceof MultiMarkdownWikiLinkText
                || element instanceof MultiMarkdownWikiLinkAnchor
                ) return WIKI_LINK_ELEMENT;
        return null;
    }

    @NotNull
    public static String getElementText(IElementType parentType, @Nullable PsiElement element, @Nullable IElementType elementType, @Nullable String prefix, @Nullable String suffix) {
        PsiElement parent = element == null || elementType == null ? null : (element.getNode().getElementType() == parentType ? element : element.getParent());
        ASTNode astNode = element == null || elementType == null ? null : parent.getNode().findChildByType(elementType);
        if (astNode == null) return "";
        if (suffix != null && prefix != null) return prefix + astNode.getText() + suffix;
        if (prefix != null) return prefix + astNode.getText();
        if (suffix != null) return astNode.getText() + suffix;
        return astNode.getText();
    }

    @Nullable
    public static LinkRef getLinkRef(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null) {
            return getLinkRef(elementTypes, element);
        }
        return null;
    }

    @Nullable
    public static LinkRef getLinkRef(@NotNull LinkRefElementTypes elementTypes, @NotNull PsiElement element) {
        if (!element.isValid()) return null;

        if (elementTypes.parentType == WIKI_LINK) {
            return LinkRef.parseWikiLinkRef(new FileRef(element), getLinkRefTextWithAnchor(element), null);
        } else if (elementTypes.parentType == IMAGE || elementTypes.parentType == REFERENCE_IMAGE) {
            return LinkRef.parseImageLinkRef(new FileRef(element), getLinkRefTextWithAnchor(element), null);
        } else {
            return LinkRef.parseLinkRef(new FileRef(element), getLinkRefTextWithAnchor(element), null);
        }
    }

    @NotNull
    public static String getLinkRefText(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null);
    }

    @NotNull
    public static String getLinkRefTextWithAnchor(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null)
                + getElementText(elementTypes.parentType, element, elementTypes.anchorType, "#", null);
    }

    @NotNull
    public static String getLinkAnchor(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.anchorType, null, null);
    }

    @NotNull
    public static String getLinkText(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
    }

    @NotNull
    public static String getLinkTitle(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);
    }

    @NotNull
    public static MultiMarkdownNamedElement setName(@NotNull MultiMarkdownNamedElement element, @NotNull String newName, int renameFlags) {
        if (!element.isValid()) return element;

        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes == null) return element;

        ASTNode pageRefNode = element.getNode();
        if (pageRefNode == null) return element;

        LinkRef newNameInfo = LinkRef.parseLinkRef(new FileRef(element.getContainingFile().getVirtualFile().getPath()), newName, null);

        PsiElement parent = element.getParent();
        String linkRef = getElementText(elementTypes.parentType, parent, elementTypes.linkRefType, null, null);
        String title = null;
        String text = null;
        String anchor = newNameInfo.getAnchor();

        IElementType elementType = element.getNode().getElementType();

        if (elementType == elementTypes.linkRefType) {
            if (elementTypes.extensionFlags != 0 && (renameFlags & RENAME_ELEMENT_HANDLES_EXT) != 0) {
                PathInfo linkRefInfo = new PathInfo(linkRef);

                switch (elementTypes.extensionFlags) {
                    case EXTENSION_KEEP_OLD:
                        linkRef = newNameInfo.getFilePathNoExt() + linkRefInfo.getExtWithDot();
                        break;

                    case EXTENSION_STRIP:
                        linkRef = newNameInfo.getFilePathNoExt();
                        break;

                    case EXTENSION_USE_NEW_IF_OLD_HAS:
                        linkRef = linkRefInfo.getHasExt() ? newNameInfo.getFilePath() : newNameInfo.getFilePathNoExt();
                        break;

                    case EXTENSION_USE_NEW:
                    default:
                        linkRef = newNameInfo.getFilePath();
                        break;
                }
            } else {
                linkRef = newName;
            }

            if ((renameFlags & RENAME_KEEP_PATH) != 0 && element.getText().contains("/")) {
                // keep the old path
                String path = new PathInfo(element.getText()).getPath();
                String name = new PathInfo(newName).getFileName();
                linkRef = path + name;
            }

            if ((renameFlags & RENAME_KEEP_NAME) != 0) {
                // keep the old name
                String path = new PathInfo(newName).getPath();
                String name = new PathInfo(element.getText()).getFileName();
                linkRef = path + name;
            }

            // preserve anchor
            if ((renameFlags & RENAME_KEEP_ANCHOR) != 0 && elementTypes.anchorType != null) {
                anchor = getElementText(elementTypes.parentType, parent, elementTypes.anchorType, "#", null);
            }

            // preserve text
            if ((renameFlags & RENAME_KEEP_TEXT) != 0) {
                text = getElementText(elementTypes.parentType, parent, elementTypes.textType, null, null);
            }

            // preserve title
            if ((renameFlags & RENAME_KEEP_TITLE) != 0) {
                title = getElementText(elementTypes.parentType, parent, elementTypes.titleType, null, null);
            }
        } else if (elementType == elementTypes.anchorType) {
            linkRef += newName.isEmpty() ? newName : "#" + newName;
        } else if (elementType == elementTypes.textType) {
            text = newName;
        } else if (elementType == elementTypes.titleType) {
            title = newName;
        } else {
            // no such beast
            logger.info("MultiMarkdownPsiImplUtil.setName called for unhandled element " + element);
            return element;
        }

        PsiElement newLink = null;
        if (elementTypes.parentType == WIKI_LINK) {
            newLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), linkRef, text);
        } else if (elementTypes.parentType == IMAGE) {
            newLink = MultiMarkdownElementFactory.createImageLink(element.getProject(), linkRef, text, title);
        } else if (elementTypes.parentType == EXPLICIT_LINK) {
            newLink = MultiMarkdownElementFactory.createExplicitLink(element.getProject(), linkRef, text, anchor, title);
        }

        if (newLink != null) {
            if (elementType == elementTypes.linkRefType) {
                //element.getParent().replace(newLink);
                ASTNode parentAST = parent.getNode();
                ASTNode firstChildNode = parentAST.getFirstChildNode();
                parentAST.addChildren(newLink.getFirstChild().getNode(), null, firstChildNode);
                parentAST.removeRange(firstChildNode, null);

                MultiMarkdownNamedElement newElement = (MultiMarkdownNamedElement) findChildByType(parent, elementType);
                if (newElement != null) return newElement;
            } else {
                MultiMarkdownNamedElement newElement = (MultiMarkdownNamedElement) findChildByType(newLink, elementType);
                if (newElement != null) {
                    element.replace(newElement);
                    return newElement;
                }
            }
        }
        return element;
    }

    @NotNull
    public static MultiMarkdownWikiLink deleteWikiLinkTitle(MultiMarkdownWikiLink element) {
        if (!element.isValid()) return element;

        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageRefNode.getText());
            element.replace(wikiLink);
        }

        return element;
    }

    @NotNull
    public static MultiMarkdownWikiLink deleteWikiLinkRef(MultiMarkdownWikiLink element) {
        if (!element.isValid()) return element;

        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageTitleNode.getText());
            element.replace(wikiLink);
        }

        return element;
    }

    @NotNull
    public static MultiMarkdownWikiLink swapWikiLinkRefTitle(MultiMarkdownWikiLink element) {
        if (!element.isValid()) return element;

        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            String anchorText = getElementText(WIKI_LINK, element, WIKI_LINK_REF_ANCHOR, "#", null);
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageTitleNode.getText(), pageRefNode.getText() + anchorText);
            element.replace(wikiLink);
        }

        return element;
    }

    @NotNull
    public static PsiElement changeToWikiLink(PsiElement element) {
        if (!element.isValid()) return element;

        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null) {
            String linkText = getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
            LinkRef sourceLinkRef = getLinkRef(elementTypes, element);

            if (sourceLinkRef != null) {
                LinkRef linkRef = WikiLinkRef.from(sourceLinkRef);

                if (linkRef != null) {
                    if (linkText.equals(linkRef.getFilePath())) linkText = null;

                    MultiMarkdownWikiLink otherLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), linkRef.getFilePathWithAnchor(), linkText);
                    if (otherLink != null) {
                        element.replace(otherLink);
                        return otherLink;
                    }
                }
            }
        }
        return element;
    }

    public static boolean isWikiLinkEquivalent(PsiElement element) {
        if (!element.isValid()) return false;

        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null) {
            String linkRefTitle = getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);
            if (linkRefTitle.isEmpty()) {
                LinkRef sourceLinkRef = getLinkRef(elementTypes, element);

                if (sourceLinkRef != null) {
                    LinkRef linkRef = WikiLinkRef.from(sourceLinkRef);

                    if (linkRef != null) {
                        return !(sourceLinkRef.pathContains("%23") && sourceLinkRef.getHasAnchor());
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    public static PsiElement changeToExplicitLink(PsiElement element, @Nullable PsiFile containingFile) {
        if (!element.isValid()) return element;

        String elementText = getTextForChangeToExplicitLink(element, containingFile);

        if (!elementText.isEmpty()) {
            MultiMarkdownExplicitLink otherLink = (MultiMarkdownExplicitLink) MultiMarkdownElementFactory.createElementFromText(element.getProject(), elementText);
            if (otherLink != null) {
                element.replace(otherLink);
                return otherLink;
            }
        }
        return element;
    }

    @NotNull
    public static String getTextForChangeToExplicitLink(PsiElement element, @Nullable PsiFile containingFile) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);

        if (containingFile == null) containingFile = element.getContainingFile();

        if (elementTypes != null && containingFile != null) {
            String linkText = getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
            LinkRef sourceLinkRef = getLinkRef(elementTypes, element);
            String linkRefTitle = getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);

            if (sourceLinkRef != null) {
                LinkRef linkRef = LinkRef.from(sourceLinkRef);

                if (linkRef != null) {
                    if (linkText.isEmpty()) {
                        // TODO: use suggestion for default text
                        linkText = !linkRefTitle.isEmpty() ? linkRefTitle : WikiLinkRef.fileAsLink(linkRef.linkToFile(linkRef.getFileNameNoExt()));
                    }
                    return MultiMarkdownExplicitLinkImpl.getElementText(linkRef.getFilePath(), linkText, linkRef.getAnchorText(), linkRefTitle);
                }
            }
        }
        return "";
    }

    @Nullable
    public static PsiElement findChildByType(@NotNull PsiElement parentElement, @NotNull IElementType childType) {
        if (!parentElement.isValid()) return null;

        for (PsiElement child : parentElement.getChildren()) {
            if (child.getNode().getElementType() == childType) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    public static Document getElementDocument(@NotNull PsiElement element) {
        return FileDocumentManager.getInstance().getDocument(element.getContainingFile().getVirtualFile());
    }

    public static ItemPresentation getPresentation(final MultiMarkdownNamedElement element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getDisplayName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                PsiFile containingFile = element.getContainingFile();
                return containingFile == null ? null : containingFile.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return MultiMarkdownIcons.FILE;
            }
        };
    }
}

