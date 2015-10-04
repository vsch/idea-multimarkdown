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
package com.vladsch.idea.multimarkdown.language;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    public MultiMarkdownReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters p, @NotNull Processor<PsiReference> consumer) {
        final PsiElement refElement = p.getElementToSearch();

        String text = null;
        if (refElement instanceof MultiMarkdownFile) {
            text = new FilePathInfo(((MultiMarkdownFile) refElement).getVirtualFile()).getFileNameNoExtAsWikiRef();
        }
        if (StringUtil.isNotEmpty(text)) {
            final SearchScope searchScope = p.getEffectiveSearchScope();
            p.getOptimizer().searchWord(text, searchScope, refElement.getLanguage().isCaseSensitive(), refElement);
        }
    }
}
