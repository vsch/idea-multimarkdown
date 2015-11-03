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
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;
import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*;

public class MultiMarkdownPsiImplUtil {
    private static final Logger logger = Logger.getLogger(MultiMarkdownPsiImplUtil.class);

    final public static LinkRefElementTypes WIKI_LINK_ELEMENT = new LinkRefElementTypes(WIKI_LINK, WIKI_LINK_REF, WIKI_LINK_TEXT, WIKI_LINK_REF_ANCHOR);
    final public static LinkRefElementTypes EXPLICIT_LINK_ELEMENT = new LinkRefElementTypes(EXPLICIT_LINK, LINK_REF, LINK_REF_TEXT, LINK_REF_ANCHOR, LINK_REF_TITLE);

    static class LinkRefElementTypes {
        @NotNull public final IElementType parentType;
        @NotNull public final IElementType linkRefType;
        @NotNull public final IElementType textType;
        @NotNull public final IElementType anchorType;
        @Nullable public final IElementType titleType;

        public LinkRefElementTypes(@NotNull IElementType parentType, @NotNull IElementType linkRefType, @NotNull IElementType textType, @NotNull IElementType anchorType) {
            this(parentType, linkRefType, textType, anchorType, null);
        }

        public LinkRefElementTypes(@NotNull IElementType parentType, @NotNull IElementType linkRefType, @NotNull IElementType textType, @NotNull IElementType anchorType, @Nullable IElementType titleType) {
            this.parentType = parentType;
            this.linkRefType = linkRefType;
            this.textType = textType;
            this.anchorType = anchorType;
            this.titleType = titleType;
        }
    }

    public static LinkRefElementTypes getNamedElementTypes(@Nullable PsiElement element) {
        if (element instanceof MultiMarkdownExplicitLink
                || element instanceof MultiMarkdownLinkRef
                || element instanceof MultiMarkdownLinkRefText
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

        PsiElement parent = element.getParent();
        String linkRef = getElementText(elementTypes.parentType, parent, elementTypes.linkRefType, null, null);
        String title = null;
        String text = null;

        IElementType elementType = element.getNode().getElementType();
        if (elementType == elementTypes.linkRefType) {
            linkRef = newName;

            if ((renameFlags & RENAME_KEEP_PATH) != 0 && element.getText().contains("/")) {
                // keep the old path
                String path = new FilePathInfo(element.getText()).getPath();
                String name = new FilePathInfo(newName).getFileName();
                linkRef = path + name;
            }

            if ((renameFlags & RENAME_KEEP_NAME) != 0) {
                // keep the old name
                String path = new FilePathInfo(newName).getPath();
                String name = new FilePathInfo(element.getText()).getFileName();
                linkRef = path + name;
            }

            // preserve anchor
            if ((renameFlags & RENAME_KEEP_ANCHOR) != 0) {
                String anchorText = getElementText(elementTypes.parentType, parent, elementTypes.anchorType, "#", null);
                if (!anchorText.isEmpty()) {
                    linkRef = FilePathInfo.linkRefNoAnchor(newName) + anchorText;
                }
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
        } else if (elementTypes.parentType == EXPLICIT_LINK) {
            newLink = MultiMarkdownElementFactory.createExplicitLink(element.getProject(), linkRef, text, title);
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
            String anchorText = getElementText(WIKI_LINK, element.getParent(), WIKI_LINK_REF_ANCHOR, "#", null);
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageTitleNode.getText(), pageRefNode.getText() + anchorText);
            element.replace(wikiLink);
        }

        return element;
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

