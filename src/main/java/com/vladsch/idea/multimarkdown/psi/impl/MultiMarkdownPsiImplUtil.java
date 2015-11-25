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
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.util.PathInfo;
import com.vladsch.idea.multimarkdown.util.FileReferenceLink;
import com.vladsch.idea.multimarkdown.util.FileReferenceLinkGitHubRules;
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

    static class LinkRefElementTypes {
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
                || element instanceof MultiMarkdownImageLinkRefText
                || element instanceof MultiMarkdownImageLinkRefTitle
                ) return IMAGE_LINK_ELEMENT;
        if (element instanceof MultiMarkdownExplicitLink
                || element instanceof MultiMarkdownLinkRef
                || element instanceof MultiMarkdownLinkRefText
                || element instanceof MultiMarkdownLinkRefTitle
                || element instanceof MultiMarkdownLinkRefAnchor
                ) return EXPLICIT_LINK_ELEMENT;
        if (element instanceof MultiMarkdownWikiLink
                || element instanceof MultiMarkdownWikiPageRef
                || element instanceof MultiMarkdownWikiPageText
                || element instanceof MultiMarkdownWikiPageRefAnchor
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

    @NotNull
    public static String getLinkRef(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null);
    }

    @NotNull
    public static String getLinkRefWithAnchor(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null)
                + getElementText(elementTypes.parentType, element, elementTypes.anchorType, "#", null);
    }

    @NotNull
    public static String getLinkRefAnchor(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.anchorType, null, null);
    }

    @NotNull
    public static String getLinkRefText(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
    }

    @NotNull
    public static String getLinkRefTitle(@Nullable PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        return elementTypes == null ? "" : getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);
    }

    public static MultiMarkdownNamedElement setName(@NotNull MultiMarkdownNamedElement element, @NotNull String newName, int renameFlags) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes == null) return element;

        ASTNode pageRefNode = element.getNode();
        if (pageRefNode == null) return element;

        PathInfo newNameInfo = new PathInfo(newName);

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
                        linkRef = newNameInfo.getFilePathWithAnchorNoExt() + linkRefInfo.getExt();
                        break;

                    case EXTENSION_STRIP:
                        linkRef = newNameInfo.getFilePathWithAnchorNoExt();
                        break;

                    case EXTENSION_USE_NEW_IF_OLD_HAS:
                        linkRef = linkRefInfo.hasExt() ? newNameInfo.getFilePathWithAnchor() : newNameInfo.getFilePathWithAnchorNoExt();
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

    public static MultiMarkdownWikiLink deleteWikiLinkTitle(MultiMarkdownWikiLink element) {
        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageRefNode.getText());
            element.replace(wikiLink);
        }

        return element;
    }

    public static MultiMarkdownWikiLink deleteWikiLinkRef(MultiMarkdownWikiLink element) {
        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageTitleNode.getText());
            element.replace(wikiLink);
        }

        return element;
    }

    public static MultiMarkdownWikiLink swapWikiLinkRefTitle(MultiMarkdownWikiLink element) {
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
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null) {
            String text = getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
            String linkRef = getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null);
            String linkRefAnchor = getElementText(elementTypes.parentType, element, elementTypes.anchorType, "#", null);
            String wikiLinkRef;

            PsiReference reference = element.getReference();
            PsiElement psiFile = reference == null ? null : reference.resolve();

            if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                FileReferenceLinkGitHubRules fileReferenceLink = new FileReferenceLinkGitHubRules(element.getContainingFile(), (PsiFile) psiFile);
                wikiLinkRef = fileReferenceLink.getWikiPageRef();
            } else {
                PathInfo pathInfo = new PathInfo(linkRef.isEmpty() ? element.getContainingFile().getName() : linkRef);
                wikiLinkRef = pathInfo.getFileNameNoExtAsWikiRef().replace("%23","#");
            }

            if (text.equals(wikiLinkRef)) text = null;

            MultiMarkdownWikiLink otherLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), wikiLinkRef + linkRefAnchor, text);
            if (otherLink != null) {
                element.replace(otherLink);
                return otherLink;
            }
        }
        return element;
    }

    public static boolean isWikiLinkEquivalent(PsiElement element) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null) {
            String text = getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
            String linkRef = getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null);
            String linkRefAnchor = getElementText(elementTypes.parentType, element, elementTypes.anchorType, "#", null);
            String linkRefTitle = getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);
            String wikiLinkRef = null;

            PsiReference reference = element.getReference();
            PsiElement psiFile = reference == null ? null : reference.resolve();

            if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                FileReferenceLinkGitHubRules fileReferenceLink = new FileReferenceLinkGitHubRules(element.getContainingFile(), (PsiFile) psiFile);
                wikiLinkRef = fileReferenceLink.getWikiPageRef();
            }
            return wikiLinkRef != null && linkRefTitle.isEmpty() && !(linkRef.contains("%23") && !linkRefAnchor.isEmpty());
        }
        return false;
    }

    @NotNull
    public static PsiElement changeToExplicitLink(PsiElement element, @Nullable PsiFile containingFile) {
        LinkRefElementTypes elementTypes = getNamedElementTypes(element);
        if (elementTypes != null && !(element.getContainingFile() == null && containingFile == null)) {
            String text = getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
            String wikiLinkRef = getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null);
            String linkRefAnchor = getElementText(elementTypes.parentType, element, elementTypes.anchorType, "#", null);
            String linkRefTitle = getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);
            String linkRef;

            PsiElement linkRefElem = findChildByType(element, elementTypes.linkRefType);
            PsiReference reference = linkRefElem != null ? linkRefElem.getReference() : null;
            PsiElement psiFile = reference == null ? null : reference.resolve();

            if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                FileReferenceLink fileReferenceLink = new FileReferenceLink(containingFile != null ? containingFile : element.getContainingFile(), (PsiFile) psiFile);
                linkRef = fileReferenceLink.isWikiPage() ? fileReferenceLink.getNoPrefixLinkRefWithAnchorNoExt() : fileReferenceLink.getLinkRef();
                if (linkRef.contains("#")) linkRefAnchor = "";
                if (text.isEmpty()) {
                    text = fileReferenceLink.getWikiPageRefWithAnchor();
                }
            } else {
                linkRef = elementTypes == WIKI_LINK_ELEMENT ?  PathInfo.wikiRefAsFileNameNoExt(wikiLinkRef) : wikiLinkRef;

                if (text.isEmpty()) {
                    // TODO: use suggestion for default text
                    text = elementTypes == WIKI_LINK_ELEMENT ? wikiLinkRef + linkRefAnchor : wikiLinkRef;
                }
            }

            MultiMarkdownExplicitLink otherLink = MultiMarkdownElementFactory.createExplicitLink(element.getProject(), linkRef, text, linkRefAnchor, linkRefTitle);
            //MultiMarkdownImageLink otherLink = MultiMarkdownElementFactory.createImageLink(element.getProject(), linkRef + linkRefAnchor, text, null);
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
        if (elementTypes != null && !(element.getContainingFile() == null && containingFile == null)) {
            String text = getElementText(elementTypes.parentType, element, elementTypes.textType, null, null);
            String wikiLinkRef = getElementText(elementTypes.parentType, element, elementTypes.linkRefType, null, null);
            String linkRefAnchor = getElementText(elementTypes.parentType, element, elementTypes.anchorType, "#", null);
            String linkRefTitle = getElementText(elementTypes.parentType, element, elementTypes.titleType, null, null);
            String linkRef;

            PsiElement linkRefElem = findChildByType(element, elementTypes.linkRefType);
            PsiReference reference = linkRefElem != null ? linkRefElem.getReference() : null;
            PsiElement psiFile = reference == null ? null : reference.resolve();

            if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                FileReferenceLink fileReferenceLink = new FileReferenceLink(containingFile != null ? containingFile : element.getContainingFile(), (PsiFile) psiFile);
                linkRef = fileReferenceLink.isWikiPage() ? fileReferenceLink.getNoPrefixLinkRefWithAnchorNoExt() : fileReferenceLink.getLinkRef();
                if (linkRef.contains("#")) linkRefAnchor = "";
                if (text.isEmpty()) {
                    text = fileReferenceLink.getWikiPageRefWithAnchor();
                }
            } else {
                linkRef = elementTypes == WIKI_LINK_ELEMENT ?  PathInfo.wikiRefAsFileNameNoExt(wikiLinkRef) : wikiLinkRef;

                if (text.isEmpty()) {
                    // TODO: use suggestion for default text
                    text = elementTypes == WIKI_LINK_ELEMENT ? wikiLinkRef + linkRefAnchor : wikiLinkRef;
                }
            }


            //MultiMarkdownExplicitLink otherLink = MultiMarkdownElementFactory.createExplicitLink(element.getProject(), linkRef + linkRefAnchor, text, null);
            //if (otherLink != null) {
            //    element.replace(otherLink);
            //    return otherLink;
            //}
            //MultiMarkdownImageLink otherLink = MultiMarkdownElementFactory.createImageLink(element.getProject(), linkRef + linkRefAnchor, text, null);
            return MultiMarkdownExplicitLinkImpl.getElementText(linkRef, text, linkRefAnchor, null);
        }
        return "";
    }

    @Nullable
    public static PsiElement findChildByType(@NotNull PsiElement parentElement, @NotNull IElementType childType) {
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

