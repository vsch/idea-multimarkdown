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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.language.MultiMarkdownReference;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownVisitor;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MultiMarkdownNamedElementImpl extends ASTWrapperPsiElement implements MultiMarkdownNamedElement {
    private static final Logger logger = Logger.getLogger(MultiMarkdownNamedElementImpl.class);
    protected final MultiMarkdownReference reference;

    public MultiMarkdownNamedElementImpl(@NotNull ASTNode node) {
        super(node);
        reference = createReference(new TextRange(0, node.getTextLength()));
    }

    public MultiMarkdownNamedElementImpl(@NotNull ASTNode node, @NotNull TextRange textRange) {
        super(node);
        reference = createReference(textRange);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MultiMarkdownVisitor) ((MultiMarkdownVisitor) visitor).visitNamedElement(this);
        else super.accept(visitor);
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(@NotNull String newName) {
        // logger.info("setting name on " + this + " to " + newName);
        return setName(newName, REASON_FILE_RENAMED);
    }

    @Override
    public PsiElement getNameIdentifier() {
        return this; //MultiMarkdownPsiImplUtil.getNameIdentifier(this);
    }

    @Override
    public ItemPresentation getPresentation() {
        return MultiMarkdownPsiImplUtil.getPresentation(this);
    }

    @Override
    public MultiMarkdownNamedElement handleContentChange(@NotNull TextRange range, String newContent) throws IncorrectOperationException {
        if (!range.equalsToRange(0, getTextLength())) {
            throw new IncorrectOperationException();
        }
        return handleContentChange(newContent);
    }

    @Override
    public MultiMarkdownNamedElement handleContentChange(String newContent) throws IncorrectOperationException {
        return (MultiMarkdownNamedElement)setName(newContent, REASON_FILE_RENAMED);
    }

    /**
     * Returns the reference from this PSI element to another PSI element (or elements), if one exists.
     * If the element has multiple associated references (see {@link #getReferences()}
     * for an example), returns the first associated reference.
     *
     * @return the reference instance, or null if the PSI element does not have any
     * associated references.
     * @see com.intellij.psi.search.searches.ReferencesSearch
     */
    @Override
    @Nullable
    public PsiReference getReference() {
        reference.refreshName();
        return reference;
    }
}
