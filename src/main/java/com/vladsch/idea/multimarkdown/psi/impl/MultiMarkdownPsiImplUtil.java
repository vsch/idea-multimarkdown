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

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*;
import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;

public class MultiMarkdownPsiImplUtil {
    @NotNull
    public static String getPageRef(@Nullable MultiMarkdownWikiLink element) {
        ASTNode pageRefNode = element == null ? null : element.getNode().findChildByType(WIKI_LINK_REF);
        if (pageRefNode != null) {
            return pageRefNode.getText();
        } else {
            return "";
        }
    }

    @NotNull
    public static
    String getPageRefWithAnchor(@Nullable MultiMarkdownWikiLink element) {
        ASTNode pageRefNode = element == null ? null : element.getNode().findChildByType(WIKI_LINK_REF);
        if (pageRefNode != null) {
            ASTNode pageRefAnchorNode = element.getNode().findChildByType(WIKI_LINK_REF_ANCHOR);
            return pageRefNode.getText() + (pageRefAnchorNode == null ? "" : "#" + pageRefAnchorNode.getText());
        } else {
            return "";
        }
    }

    @NotNull
    public static
    String getPageRefAnchor(@Nullable MultiMarkdownWikiLink element) {
        ASTNode pageRefAnchorNode = element == null ? null : element.getNode().findChildByType(WIKI_LINK_REF_ANCHOR);
        if (pageRefAnchorNode != null) {
            return pageRefAnchorNode.getText();
        } else {
            return "";
        }
    }

    @NotNull
    public static
    String getPageText(@Nullable MultiMarkdownWikiLink element) {
        ASTNode pageTitleNode = element == null ? null : element.getNode().findChildByType(WIKI_LINK_TEXT);
        if (pageTitleNode != null) {
            return pageTitleNode.getText();
        } else {
            return "";
        }
    }

    public static String getName(@Nullable MultiMarkdownWikiLink element) {
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

            // preserve anchor
            if ((renameFlags & RENAME_KEEP_ANCHOR) != 0) {
                String anchorText = getPageRefAnchor((MultiMarkdownWikiLink) element.getParent());
                if (!anchorText.isEmpty()) {
                    newName = FilePathInfo.linkRefNoAnchor(newName) + "#" + anchorText;
                }
            }

            // preserve title
            if ((renameFlags & RENAME_KEEP_TEXT) != 0) {
                ASTNode pageRefTitleNode = element.getParent().getNode().findChildByType(WIKI_LINK_TEXT);
                if (pageRefTitleNode != null) {
                    title = pageRefTitleNode.getText();
                }
            }

            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), newName, title);
            element.getParent().replace(wikiLink);
            MultiMarkdownWikiPageRef newElement = (MultiMarkdownWikiPageRef) findChildByType(wikiLink, WIKI_LINK_REF);
            if (newElement != null) return newElement;
        }
        return element;
    }

    public static MultiMarkdownNamedElement setName(MultiMarkdownWikiPageText element, String newName, int reason) {
        ASTNode pageTitleNode = element.getNode();
        ASTNode pageRefNode = element.getParent().getNode().findChildByType(WIKI_LINK_REF);
        if (pageTitleNode != null && pageRefNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageRefNode.getText(), newName);
            MultiMarkdownWikiPageText newElement = (MultiMarkdownWikiPageText) findChildByType(wikiLink, WIKI_LINK_TEXT);
            if (newElement != null) {
                element.replace(newElement);
                return newElement;
            }
        }
        return element;
    }

    public static MultiMarkdownNamedElement setName(MultiMarkdownWikiPageRefAnchor element, String newName, int reason) {
        //ASTNode pageRefAnchor = element.getNode();
        ASTNode pageTitleNode = element.getParent().getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getParent().getNode().findChildByType(WIKI_LINK_REF);
        if (pageRefNode != null) {
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageRefNode.getText() + "#" + newName, pageTitleNode == null ? null : pageTitleNode.getText());
            MultiMarkdownWikiPageRefAnchor newElement = (MultiMarkdownWikiPageRefAnchor) findChildByType(wikiLink, WIKI_LINK_REF_ANCHOR);
            if (newElement != null) {
                element.replace(newElement);
                return newElement;
            }
        }
        return element;
    }

    public static MultiMarkdownWikiLink swapWikiLinkRefTitle(MultiMarkdownWikiLink element) {
        ASTNode pageTitleNode = element.getNode().findChildByType(WIKI_LINK_TEXT);
        ASTNode pageRefNode = element.getNode().findChildByType(WIKI_LINK_REF);

        if (pageRefNode != null && pageTitleNode != null) {
            String anchorText = getPageRefAnchor((MultiMarkdownWikiLink) element.getParent());
            if (!anchorText.isEmpty()) {
                anchorText = "#" + anchorText;
            }
            MultiMarkdownWikiLink wikiLink = MultiMarkdownElementFactory.createWikiLink(element.getProject(), pageTitleNode.getText(), pageRefNode.getText() + anchorText);
            element.replace(wikiLink);
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
    
    
    /*
     * Explicit Link Utils
     * 
     */

    @NotNull
    public static
    String getLinkRefWithAnchor(@Nullable MultiMarkdownExplicitLink element) {
        ASTNode pageRefNode = element == null ? null : element.getNode().findChildByType(LINK_REF);
        if (pageRefNode != null) {
            ASTNode pageRefAnchorNode = element.getNode().findChildByType(LINK_REF_ANCHOR);
            return pageRefNode.getText() + (pageRefAnchorNode == null ? "" : "#" + pageRefAnchorNode.getText());
        } else {
            return "";
        }
    }

    public static MultiMarkdownNamedElement setName(MultiMarkdownLinkRef element, String newName, int renameFlags) {
        ASTNode pageRefNode = element.getNode();
        String title = null;
        String text = null;

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

            // preserve anchor
            if ((renameFlags & RENAME_KEEP_ANCHOR) != 0) {
                String anchorText = getPageRefAnchor((MultiMarkdownWikiLink) element.getParent());
                if (!anchorText.isEmpty()) {
                    newName = FilePathInfo.linkRefNoAnchor(newName) + "#" + anchorText;
                }
            }

            // preserve text
            if ((renameFlags & RENAME_KEEP_TEXT) != 0) {
                ASTNode pageRefTextNode = element.getParent().getNode().findChildByType(LINK_REF_TEXT);
                if (pageRefTextNode != null) {
                    text = pageRefTextNode.getText();
                }
            }

            if ((renameFlags & RENAME_KEEP_TITLE) != 0) {
                ASTNode pageRefTitleNode = element.getParent().getNode().findChildByType(LINK_REF_TITLE);
                if (pageRefTitleNode != null) {
                    title = pageRefTitleNode.getText();
                }
            }

            MultiMarkdownExplicitLink link = MultiMarkdownElementFactory.createExplicitLink(element.getProject(), newName, text, title);
            element.getParent().replace(link);
            MultiMarkdownLinkRef newElement = (MultiMarkdownLinkRef) findChildByType(link, LINK_REF);
            if (newElement != null) return newElement;
        }
        return element;
    }

    public static MultiMarkdownNamedElement setName(MultiMarkdownLinkRefText element, String newName, int reason) {
        ASTNode pageTextNode = element.getNode();
        ASTNode pageRefNode = element.getParent().getNode().findChildByType(LINK_REF);
        if (pageTextNode != null && pageRefNode != null) {
            MultiMarkdownExplicitLink explicitLink = MultiMarkdownElementFactory.createExplicitLink(element.getProject(), pageRefNode.getText(), newName, null);
            MultiMarkdownLinkRefText newElement = (MultiMarkdownLinkRefText) findChildByType(explicitLink, LINK_REF_TEXT);
            if (newElement != null) {
                element.replace(newElement);
                return newElement;
            }
        }
        return element;
    }

    public static MultiMarkdownNamedElement setName(MultiMarkdownLinkRefAnchor element, String newName, int reason) {
        //ASTNode pageRefAnchor = element.getNode();
        ASTNode pageTextNode = element.getParent().getNode().findChildByType(LINK_REF_TEXT);
        ASTNode pageRefNode = element.getParent().getNode().findChildByType(LINK_REF);
        if (pageRefNode != null) {
            MultiMarkdownExplicitLink explicitLink = MultiMarkdownElementFactory.createExplicitLink(element.getProject(), pageRefNode.getText() + "#" + newName, pageTextNode == null ? null : pageTextNode.getText(), null);
            MultiMarkdownLinkRefAnchor newElement = (MultiMarkdownLinkRefAnchor) findChildByType(explicitLink, LINK_REF_ANCHOR);
            if (newElement != null) {
                element.replace(newElement);
                return newElement;
            }
        }
        return element;
    }

    public static MultiMarkdownNamedElement setName(MultiMarkdownLinkRefTitle element, String newName, int reason) {
        //ASTNode pageRefAnchor = element.getNode();
        ASTNode pageRefNode = element.getParent().getNode().findChildByType(LINK_REF);
        if (pageRefNode != null) {
            MultiMarkdownExplicitLink explicitLink = MultiMarkdownElementFactory.createExplicitLink(element.getProject(), pageRefNode.getText(), "text", newName);
            MultiMarkdownLinkRefTitle newElement = (MultiMarkdownLinkRefTitle) findChildByType(explicitLink, LINK_REF_TITLE);
            if (newElement != null) {
                element.replace(newElement);
                return newElement;
            }
        }
        return element;
    }



    /*
     * Common Utils
     * 
     */

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

    public static String getLinkRefText(@Nullable MultiMarkdownExplicitLink element) {
        ASTNode pageRefNode = element == null ? null : element.getNode().findChildByType(EXPLICIT_LINK);
        if (pageRefNode != null) {
            return pageRefNode.getText();
        } else {
            return "";
        }
    }

    public static String getLinkRef(@Nullable MultiMarkdownExplicitLink element) {
        ASTNode pageRefNode = element == null ? null : element.getNode().findChildByType(LINK_REF);
        if (pageRefNode != null) {
            return pageRefNode.getText();
        } else {
            return "";
        }
    }

    public static String getLinkRefAnchor(@Nullable MultiMarkdownExplicitLink element) {
        ASTNode pageRefNode = element == null ? null : element.getNode().findChildByType(LINK_REF_ANCHOR);
        if (pageRefNode != null) {
            return pageRefNode.getText();
        } else {
            return "";
        }
    }

    public static String getLinkRefTitle(@Nullable MultiMarkdownExplicitLink element) {
        ASTNode pageRefNode = element == null ? null : element.getNode().findChildByType(LINK_REF_TITLE);
        if (pageRefNode != null) {
            return pageRefNode.getText();
        } else {
            return "";
        }
    }
}

