// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.vladsch.md.nav.language.injection.LanguageGuesser
import com.vladsch.md.nav.parser.flexmark.MdFencedCodeImageConversionManager
import com.vladsch.md.nav.parser.flexmark.MdNavigatorDiagramNodeRenderer
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.util.MdTypes.VERBATIM_LANG
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.settings.ParserOptions

class CodeFenceLanguageCompletion : MdElementCompletion {
    override fun getWantElement(element: PsiElement, elementPos: PsiElement, parameters: CompletionParameters, context: ProcessingContext): Boolean {
        val elementType = element.node.elementType

        return elementType === VERBATIM_LANG
    }

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet, element: PsiElement, containingFile: MdFile): Boolean {
        for (suggestion in LanguageGuesser.INSTANCE.languageNames) {
            resultSet.addElement(LookupElementBuilder.create(suggestion).withCaseSensitivity(false))
        }

        val profile = MdRenderingProfileManager.getProfile(containingFile)
        val gitLabExt = profile.parserSettings.anyOptions(ParserOptions.GITLAB_EXT)

        if (gitLabExt || profile.parserSettings.anyOptions(ParserOptions.GITLAB_MATH_EXT)) {
            resultSet.addElement(LookupElementBuilder.create(MdNavigatorDiagramNodeRenderer.MATH_LANGUAGE_INFO).withCaseSensitivity(false))
        }

        if (gitLabExt || profile.parserSettings.anyOptions(ParserOptions.GITLAB_MERMAID_EXT)) {
            resultSet.addElement(LookupElementBuilder.create("mermaid").withCaseSensitivity(false))
        }

        for (infoString in MdFencedCodeImageConversionManager.getInstance(element.project).infoStrings) {
            resultSet.addElement(LookupElementBuilder.create(infoString).withCaseSensitivity(false))
        }

        return true
    }
}
