/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.documentation;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@link com.intellij.lang.documentation.DocumentationProvider} implementation for Markdown.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.10
 */
public class MarkdownDocumentationProvider implements DocumentationProvider {

    @Nullable @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Nullable @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Nullable @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        return null;
    }

    @Nullable @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return null;
    }

    @Nullable @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }
}
