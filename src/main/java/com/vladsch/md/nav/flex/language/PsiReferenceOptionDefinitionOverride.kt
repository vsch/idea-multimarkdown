// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils

class PsiReferenceOptionDefinitionOverride constructor(element: PsiLiteralExpression, textRange: TextRange) : PsiReferenceOptionDefinitionLiteral(element, textRange) {
    constructor(element: PsiLiteralExpression) : this(element, TextRange(1, element.text.length - 1))

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val definition = FlexmarkPsiImplUtils.getFlexmarkExampleOptionDefinition(element, null)
        if (definition != null) {
            val psiLiteral = definition.literalExpressionElement
            val result = ArrayList<ResolveResult>()
            val specExampleOptions = FlexmarkPsiImplUtils.findSpecExampleOptions(element.project, definition.optionName, null)
            for (option in specExampleOptions) {
                val reference = option.reference ?: continue
                val resolvesToLiteral = if (reference is PsiPolyVariantReference) {
                    val multiResolve = reference.multiResolve(false)
                    multiResolve.any { it.isValidResult && it.element?.isEquivalentTo(psiLiteral) ?: false }
                } else {
                    val resolved = reference.resolve()
                    resolved?.isEquivalentTo(psiLiteral) ?: false
                }

                if (resolvesToLiteral) {
                    result.add(PsiElementResolveResult(option))
                }
            }

            // check for overrides
            val psiClassData = FlexmarkSpecTestCaseCachedData.getData(FlexmarkPsiImplUtils.getElementPsiClass(element))
            if (psiClassData != null) {
                val optionDefinitions = psiClassData.getResolvedOptionDefinitions(false)[definition.optionName]
                if (optionDefinitions != null && optionDefinitions.size > 1) {
                    for (i in 1 until optionDefinitions.size) {
                        result.add(PsiElementResolveResult(optionDefinitions[i].element))
                    }
                }
            }
            return result.toTypedArray()
        }
        return arrayOf()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.isNotEmpty()) resolveResults[0].element else null
    }
}
