// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.vladsch.md.nav.psi.element.MdFile;

public interface MdElementCompletion {
    boolean getWantElement(PsiElement element, PsiElement elementPos, CompletionParameters parameters, ProcessingContext context);

    /**
     * @param context    completion initialization context
     * @param element    non leaf element at position
     * @param elementPos element at position
     *
     * @return true if no other completion extensions should modify the replacement range or be included in completions
     */
    default boolean duringCompletion(CompletionInitializationContext context, PsiElement element, PsiElement elementPos) {
        return false;
    }

    boolean addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultSet, PsiElement element, MdFile containingFile);

    Logger LOG = Logger.getInstance("com.vladsch.md.nav.language.completion");
    Logger LOG_TRACE = Logger.getInstance("com.vladsch.md.nav.language.completion-detailed");
}
