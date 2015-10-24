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
import com.vladsch.idea.multimarkdown.language.MultiMarkdownReference;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageTitle;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownWikiPageTitleImpl extends MultiMarkdownNamedElementImpl implements MultiMarkdownWikiPageTitle {
    private static final Logger logger = Logger.getLogger(MultiMarkdownWikiPageTitleImpl.class);
    protected static final String MISSING_ELEMENT_NAME_SPACE = "wiki-title::";

    @NotNull
    @Override
    public String getMissingElementNamespace() {
        return MISSING_ELEMENT_NAME_SPACE;
    }

    public MultiMarkdownWikiPageTitleImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MultiMarkdownReference createReference(@NotNull TextRange textRange) {
        return  new MultiMarkdownReferenceWikiPageTitle(this, textRange);
    }

    @Override
    public PsiElement setName(@NotNull String newName, int reason) {
        String oldName = getName();
        //logger.info("setting name on " + this + " to " + newName);
        MultiMarkdownNamedElement element = MultiMarkdownPsiImplUtil.setName(this, newName, reason);
        //logger.info("element rename change " + this + " to " + element);
        //reference.notifyNamedElementChange(this, element);
        //reference.invalidateResolveResults();
        return element;
    }

    @Override
    public boolean isInplaceRenameAvailable(PsiElement context) {
        return false;
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(PsiElement context) {
        return false;
    }

    @Override
    public String toString() {
        return "WIKI_LINK_TITLE '" + getName() + "' " + super.hashCode();
    }
}
