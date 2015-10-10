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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;

public class MultiMarkdownPsiImplUtil {
    public static String getPageRef(MultiMarkdownWikiLink element) {
        ASTNode pageRefNode = element.getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_REF);
        if (pageRefNode != null) {
            return pageRefNode.getText();
        } else {
            return null;
        }
    }

    public static String getPageTitle(MultiMarkdownWikiLink element) {
        ASTNode pageTitleNode = element.getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_TITLE);
        if (pageTitleNode != null) {
            return pageTitleNode.getText();
        } else {
            return null;
        }
    }

    public static String getName(MultiMarkdownWikiLink element) {
        return getPageRef(element);
    }

    @Nullable
    public static PsiElement findChildByType(@NotNull PsiElement parentElement, @NotNull IElementType childType) {
        PsiElement childElement = null;

        for (PsiElement child : parentElement.getChildren()) {
            if (child.getNode().getElementType() == childType) {
                return child;
            }
        }
        return null;
    }

    public static MultiMarkdownNamedElement setName(MultiMarkdownWikiPageRef element, String newName, int renameFlags) {
        ASTNode pageRefNode = element.getNode();
        String title = null;

        if (pageRefNode != null) {
            if ((renameFlags & RENAME_KEEP_PATH) != 0 && element.getText().contains("/")) {
                // keep the old path
                String path = new FilePathInfo(element.getText()).getPath();
                String name = new FilePathInfo(newName).getFileName();
                newName = path + name;
            }

            if ((renameFlags & RENAME_KEEP_NAME) != 0) {
                // keep the old name
                String path = new FilePathInfo(newName).getPath();
                String name = new FilePathInfo(element.getText()).getFileName();
                newName = path + name;
            }

            // preserve anchor on file move
            if ((renameFlags & RENAME_KEEP_ANCHOR) != 0 && !FilePathInfo.linkRefAnchor(element.getText()).isEmpty()) {
                newName = FilePathInfo.linkRefNoAnchor(newName) + FilePathInfo.linkRefAnchor(element.getText());
            }

            // preserve title on file rename
            if ((renameFlags & RENAME_KEEP_TITLE) != 0 && !FilePathInfo.linkRefAnchor(element.getText()).isEmpty()) {
                ASTNode pageRefTitleNode = element.getParent().getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_TITLE);
                if (pageRefTitleNode != null) {
                    title = pageRefTitleNode.getText();
                }
            }

            if (title != null) {
                MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), newName, title);
                element.getParent().replace(wikiLink);
                MultiMarkdownWikiPageRef newElement = (MultiMarkdownWikiPageRef) findChildByType(wikiLink, MultiMarkdownTypes.WIKI_LINK_REF);
                if (newElement != null) return newElement;
            } else {
                MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), newName);
                MultiMarkdownWikiPageRef newElement = (MultiMarkdownWikiPageRef) findChildByType(wikiLink, MultiMarkdownTypes.WIKI_LINK_REF);
                if (newElement != null) {
                    element.replace(newElement);
                    return newElement;
                }
            }
        }
        return element;
    }

    public static MultiMarkdownNamedElement setName(MultiMarkdownWikiPageTitle element, String newName, int reason) {
        ASTNode pageTitleNode = element.getNode();
        ASTNode pageRefNode = element.getParent().getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_REF);
        if (pageTitleNode != null && pageRefNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageRefNode.getText(), newName);
            MultiMarkdownWikiPageTitle newElement = (MultiMarkdownWikiPageTitle) findChildByType(wikiLink, MultiMarkdownTypes.WIKI_LINK_TITLE);
            if (newElement != null) {
                element.replace(newElement);
                return newElement;
            }
        }
        return element;
    }

    public static MultiMarkdownWikiLink swapWikiLinkRefTitle(MultiMarkdownWikiLink element) {
        ASTNode pageTitleNode = element.getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_TITLE);
        ASTNode pageRefNode = element.getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageTitleNode.getText(), pageRefNode.getText());
            element.replace(wikiLink);
        }

        return element;
    }

    public static MultiMarkdownWikiLink deleteWikiLinkTitle(MultiMarkdownWikiLink element) {
        ASTNode pageTitleNode = element.getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_TITLE);
        ASTNode pageRefNode = element.getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageRefNode.getText());
            element.replace(wikiLink);
        }

        return element;
    }

    public static MultiMarkdownWikiLink deleteWikiLinkRef(MultiMarkdownWikiLink element) {
        ASTNode pageTitleNode = element.getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_TITLE);
        ASTNode pageRefNode = element.getNode().findChildByType(MultiMarkdownTypes.WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageTitleNode.getText());
            element.replace(wikiLink);
        }

        return element;
    }

    public static Document getElementDocument(PsiElement element) {
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

