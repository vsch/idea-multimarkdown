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
 */
package com.vladsch.idea.multimarkdown.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLink;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.util.PathInfo;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownWikiPageRefImpl extends MultiMarkdownNamedElementImpl implements MultiMarkdownWikiPageRef {
    private static final Logger logger = Logger.getLogger(MultiMarkdownWikiPageRefImpl.class);
    protected static final String MISSING_ELEMENT_NAME_SPACE = "wiki-ref::";

    @NotNull
    @Override
    public String getMissingElementNamespace() {
        assert getParent() instanceof MultiMarkdownWikiLink;
        return ((MultiMarkdownWikiLink) getParent()).getMissingElementNameSpace(MISSING_ELEMENT_NAME_SPACE, false);
    }

    public MultiMarkdownWikiPageRefImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MultiMarkdownReference createReference(@NotNull TextRange textRange) {
        return new MultiMarkdownReferenceWikiPageRef(this, textRange);
    }

    @Override
    public String getDisplayName() {
        return getParent() instanceof MultiMarkdownWikiLink  ? ((MultiMarkdownWikiLink) getParent()).getDisplayName() : getFileName();
    }

    @Override
    public String getFileName() {
        return PathInfo.wikiRefAsFileNameWithExt(new PathInfo(getName() == null ? "" : getName()).getFileName());
    }

    @Override
    public String getFileNameWithAnchor() {
        String anchorText = MultiMarkdownPsiImplUtil.getLinkRefAnchor(getParent());
        PathInfo pathInfo = new PathInfo((getName() == null ? "" : getName()) + (anchorText.isEmpty() ? anchorText : "#" + anchorText));
        return PathInfo.wikiRefAsFileNameWithExt(pathInfo.getFileName()) + pathInfo.getAnchor();
    }

    @Override
    public String getNameWithAnchor() {
        return MultiMarkdownPsiImplUtil.getLinkRefWithAnchor(getParent());
    }

    @Override
    public MultiMarkdownNamedElement handleContentChange(String newContent) throws IncorrectOperationException {
        String newName = new PathInfo(newContent).getFileNameNoExtAsWikiRef();
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(getProject());
        if (projectComponent == null) return this;

        return (MultiMarkdownNamedElement) setName(newName, projectComponent.getRefactoringRenameFlags(REASON_FILE_RENAMED));
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(PsiElement context) {
        return false;
    }

    @Override
    public boolean isInplaceRenameAvailable(PsiElement context) {
        return false;
    }

    @Override
    public PsiElement setName(@NotNull String newName, int renameFlags) {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(getProject());
        if (projectComponent == null) return this;

        if (projectComponent.getRefactoringRenameFlags() != RENAME_NO_FLAGS) renameFlags = projectComponent.getRefactoringRenameFlags();
        else if (reference.isResolveRefMissing()) renameFlags &= ~RENAME_KEEP_ANCHOR;

        return MultiMarkdownPsiImplUtil.setName(this, newName, renameFlags);
    }

    @Override
    public String toString() {
        return "WIKI_LINK_REF '" + getName() + "' " + super.hashCode();
    }
}
