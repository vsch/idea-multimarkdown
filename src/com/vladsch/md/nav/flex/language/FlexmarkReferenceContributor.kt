// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.language

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils

class FlexmarkReferenceContributor : PsiReferenceContributor() {
    companion object {
        private val EMPTY_REFERENCES = arrayOf<PsiReference>()
    }

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    if (element is PsiLiteralExpression) {
                        val value = element.value

                        if (value is String) {
                            val specResource = FlexmarkPsiImplUtils.getSpecResourceDefinitionOrNull(element)
                            if (specResource != null) {
                                val reference = PsiReferenceSpecResourceLiteral(element, specResource.textRange)
                                return arrayOf(reference)
                            } else {
                                // see if this one is an option definition which overrides one in a super class, then it refers to the super class
                                val definition = FlexmarkPsiImplUtils.getFlexmarkExampleOptionDefinition(element, null)
                                if (definition != null) {
                                    val psiClassData = FlexmarkSpecTestCaseCachedData.getData(FlexmarkPsiImplUtils.getElementPsiClass(element))
                                    if (psiClassData != null) {
                                        val resolvedLiterals = psiClassData.getResolvedOptionDefinitions(false)
                                        val resolvedLiteral = resolvedLiterals[definition.optionName]
                                        if (resolvedLiteral != null) {
                                            // this reference resolves to example options and overridden super class options
                                            return arrayOf(PsiReferenceOptionDefinitionOverride(definition.literalExpressionElement, definition.rangeInElement))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return EMPTY_REFERENCES
                }
            }
        )
    }
}
