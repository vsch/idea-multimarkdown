// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.language

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiReference
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.Processor
import com.vladsch.md.nav.flex.parser.FlexmarkSpecCachedData
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData
import com.vladsch.md.nav.flex.psi.FakePsiLiteralExpression
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOptionDefinition
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.util.MdPsiImplUtil.inScope
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.PsiMap
import com.vladsch.md.nav.util.PsiSet
import com.vladsch.md.nav.util.Result

class FlexmarkReferenceSearch : QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>(true) {
    override fun processQuery(p: ReferencesSearch.SearchParameters, consumer: Processor<in PsiReference>) {
        if (!MdApplicationSettings.instance.debugSettings.getExtension(FlexmarkDebugSettings.KEY).enableFlexmarkFeatures) return

        val element = p.elementToSearch
        val effectiveSearchScope = p.effectiveSearchScope
        val project = element.project

        if (element is MdFile && element.subType == MdFile.FLEXMARK_SUBTYPE) {
            if (inScope(effectiveSearchScope, element)) {
                FlexmarkPsiImplUtils.forAllRenderingTestCaseClasses(element) { specTestData ->
                    for ((_, psiClassData) in specTestData.psiClassData) {
                        if (psiClassData.specFiles.containsKey(element) && !psiClassData.isNoSpecResourceTestCase) {
                            consumer.process(PsiReferenceSpecResourceLiteral(psiClassData.specResourceLiteral!!))
                        }
                    }

                    Result.VOID()
                }
            }
        } else if (element is PsiLiteralExpression || element is FakePsiLiteralExpression) {
            // PsiLiteralExpression: look for spec example options and psiLiteral overrides
            // FakePsiLiteralExpression: look for literals in subclasses which override it
            val psiLiteral = if (element is FakePsiLiteralExpression) element.literalExpression else element

            val definition = FlexmarkPsiImplUtils.getFlexmarkExampleOptionDefinition(psiLiteral, null)
            if (definition != null) {
                val psiClassData = FlexmarkSpecTestCaseCachedData.getData(definition.psiClass)
                if (psiClassData != null) {
                    val specExampleOptions = FlexmarkPsiImplUtils.findSpecExampleOptions(project, definition.optionName, null)

                    // check for markdown example options referencing this literal option definition
                    for (option in specExampleOptions) {
                        if (inScope(effectiveSearchScope, option.mdFile)) {
                            val reference = option.reference ?: continue
                            consumer.process(reference)
                        }
                    }

                    if (effectiveSearchScope !is LocalSearchScope) {
                        // add all super/subclasses
                        if (element is FakePsiLiteralExpression) {
                            val optionDefinitions = psiClassData.getResolvedOptionDefinitions(false)
                            val definitions = optionDefinitions[definition.optionName]
                            if (definitions != null) {
                                for (definition1 in definitions) {
                                    consumer.process(definition1.getPsiReference())
                                }
                            }
                        }
                    }
                }
            }
        } else if (element is FlexmarkExampleOption) {
            if (!element.isBuiltIn) {
                val testCaseClasses = PsiSet { HashSet<PsiClass>() }

                val testCaseDefinitionsMap:
                    PsiMap<PsiClass, ArrayList<Array<FlexmarkExampleOptionDefinition>>> =
                    FlexmarkSpecCachedData.getData(element.mdFile).getElementOptions(mapOf(element.optionName to element))

                testCaseDefinitionsMap.forEach { psiTestClass: PsiClass, definitions: ArrayList<Array<FlexmarkExampleOptionDefinition>> ->
                    testCaseClasses.add(psiTestClass)

                    if (definitions.isNotEmpty()) {
                        val flexmarkExampleOptionDefinition = definitions[0][0]

                        if (flexmarkExampleOptionDefinition.isDefinitionFor(element)) {
                            if (inScope(effectiveSearchScope, flexmarkExampleOptionDefinition.element.containingFile)) {
                                consumer.process(PsiReferenceOptionDefinitionOverride(flexmarkExampleOptionDefinition.literalExpressionElement))
                            }
                        }
                    }
                }

                if (effectiveSearchScope !is LocalSearchScope) {
                    val specExampleOptions = FlexmarkPsiImplUtils.findSpecExampleOptions(project, element.optionName, null)

                    // check for markdown example options referencing this literal option definition
                    for (option in specExampleOptions) {
                        if (option.isEquivalentTo(element)) continue

                        if (inScope(effectiveSearchScope, option.mdFile)) {
                            val reference = option.reference ?: continue
                            consumer.process(reference)

                            val resolved = reference.resolve()
                            if (resolved is FakePsiLiteralExpression) {
                                // find all sub and super classes of the literal expression and provide references to their option so all get renamed/found
                                val literal = resolved.literalExpression
                                val psiClass = FlexmarkPsiImplUtils.getElementPsiClass(literal)
                                if (psiClass != null) {
                                    val psiClassData = FlexmarkSpecTestCaseCachedData.getData(psiClass)
                                    if (psiClassData != null) {
                                        val optionDefinitions = psiClassData.getResolvedOptionDefinitions(false)
                                        val definitions = optionDefinitions[option.optionName]
                                        if (definitions != null) {
                                            for (definition in definitions) {
                                                consumer.process(definition.getPsiReference())
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
