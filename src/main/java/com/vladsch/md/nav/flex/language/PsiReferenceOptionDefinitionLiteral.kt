// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language

import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption
import com.vladsch.md.nav.flex.psi.FlexmarkPsi
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.util.PsiMap
import com.vladsch.md.nav.util.Result
import com.vladsch.plugin.util.ifElse
import icons.FlexmarkIcons
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

open class PsiReferenceOptionDefinitionLiteral constructor(element: PsiLiteralExpression, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    constructor(element: PsiLiteralExpression) : this(element, TextRange(1, element.text.length - 1))

    override fun isSoft(): Boolean {
        return false
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return arrayOf()
    }

    override fun resolve(): PsiElement? {
        return null
    }

    override fun getVariants(): Array<Any> {
        val variants: MutableList<LookupElement> = ArrayList()
        val testCaseData = FlexmarkSpecTestCaseCachedData.getData(myElement.containingFile).psiClassData[FlexmarkPsiImplUtils.getElementPsiClass(element)]
        if (testCaseData != null) {
            val specFiles = testCaseData.getResolvedSpecFiles().keys
            val fileOptions = PsiMap { HashMap<PsiFile, HashSet<String>>() }
            val optionLiterals = testCaseData.optionLiterals
            val containingClass = FlexmarkPsiImplUtils.getElementPsiClass(myElement)
            if (containingClass != null) {
                val containingClassSupers = HashSet<PsiClass>()
                FlexmarkPsiImplUtils.collectRenderingTestCaseSupers(containingClass) { t: PsiClass, _: Int -> containingClassSupers.add(t) }
                val resolvedOptionLiterals = testCaseData.getResolvedOptionDefinitions(true)

                for (mdFile in specFiles) {
                    FlexmarkPsiImplUtils.forAllOptions(mdFile) { option: FlexmarkExampleOption ->
                        val optionName: String = option.optionName

                        if (optionName.isNotEmpty() && !optionLiterals.containsKey(optionName) && !FlexmarkPsi.isBuiltInFlexmarkOption(optionName)) {
                            if (fileOptions.computeIfAbsent(option.containingFile) { HashSet() }.add(optionName)) {
                                // NOTE: undefined options get a priority boost
                                val isDefinedOption = testCaseData.specResourceLiteral == null || resolvedOptionLiterals.containsKey(optionName)
                                val priority = isDefinedOption.ifElse(10000.0, 20000.0)

                                variants.add(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(optionName)
                                    .withIcon(isDefinedOption.ifElse(option.getIcon(0), FlexmarkIcons.Element.SPEC_EXAMPLE_ERRORS))
                                    .withTypeText(option.presentableText)
                                    , priority))
                            }
                        }

                        Result.VOID()
                    }
                }
            }
        }

        return variants.toTypedArray()
    }
}
