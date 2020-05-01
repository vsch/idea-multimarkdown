// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.parser

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiClass
import com.vladsch.md.nav.parser.cache.CachedData
import com.vladsch.md.nav.parser.cache.data.CachedDataKey
import com.vladsch.md.nav.parser.cache.data.dependency.RestartableProjectFileDependency
import com.vladsch.md.nav.parser.cache.data.transaction.CachedTransactionContext
import com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOptionDefinition
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.util.PsiMap
import com.vladsch.md.nav.util.PsiSet
import com.vladsch.md.nav.util.Result
import com.vladsch.plugin.util.debug

/**
 * Spec File cached data
 *
 * Contains:
 *    the line marker data for the file
 */
class FlexmarkSpecCachedData {

    companion object {
        val LOG_CACHE_DETAIL: Logger = IndentingLogger.LOG_COMPUTE_DETAIL
        val LOG_CACHE: Logger = IndentingLogger.LOG_COMPUTE_RESULT

        data class SpecOptionDefinitionsData(val testCaseDefinitionsMap: Map<PsiClass, ArrayList<Array<FlexmarkExampleOptionDefinition>>>) {
            fun isValid(): Boolean {
                return !testCaseDefinitionsMap.any { !it.key.isValid || !it.value.any { !it.any { !it.isDataValid } } }
            }

            fun getElementOptions(optionsMap: Map<String, FlexmarkExampleOption>): PsiMap<PsiClass, ArrayList<Array<FlexmarkExampleOptionDefinition>>> {
                val testCaseClasses = PsiSet { HashSet<PsiClass>() }
                val testCaseDefinitionsMap = PsiMap { HashMap<PsiClass, ArrayList<Array<FlexmarkExampleOptionDefinition>>>() }

                for ((psiTestClass, definitions) in this.testCaseDefinitionsMap) {
                    testCaseClasses.add(psiTestClass)

                    for (definitionsArray in definitions) {
                        if (optionsMap.containsKey(definitionsArray[0].optionName)) {
                            testCaseDefinitionsMap.computeIfAbsent(psiTestClass) { ArrayList() }.add(definitionsArray)
                        }
                    }
                }

                // Add test case class if no options in the example matched its definition. The spec resource containing item should always be there
                for (psiClass in testCaseClasses) {
                    if (!testCaseDefinitionsMap.containsKey(psiClass)) testCaseDefinitionsMap[psiClass] = arrayListOf()
                }

                return testCaseDefinitionsMap
            }
        }

        @JvmField
        val SPEC_OPTION_DEFINITIONS: CachedDataKey<MdFile, SpecOptionDefinitionsData> = object : CachedDataKey<MdFile, SpecOptionDefinitionsData>("FLEXMARK_SPEC_OPTION_DEFINITIONS") {
            override fun isValid(value: SpecOptionDefinitionsData): Boolean = value.isValid()

            override fun compute(context: CachedTransactionContext<MdFile>): SpecOptionDefinitionsData {
                val file: MdFile = context.dataOwner
                context.addDependency(file)

                val testCaseClasses = PsiSet { HashSet<PsiClass>() }
                val testCaseDefinitionsMap = PsiMap { HashMap<PsiClass, ArrayList<Array<FlexmarkExampleOptionDefinition>>>() }

                FlexmarkPsiImplUtils.forOptionDefinitions(file, null, true) { psiTestClass, definitions ->
                    // NOTE: psiTestClass is only test classes which define this mdFile as their spec resource
                    //    definitions[0] is the definition used for the option, [1...] if present are super class overridden definitions
                    testCaseClasses.add(psiTestClass)
                    testCaseDefinitionsMap.computeIfAbsent(psiTestClass) { ArrayList() }.add(definitions)

                    context.addDependency(psiTestClass.containingFile)

                    Result.VOID()
                }

                // Add test case class if no options in the example matched its definition. The spec resource containing item should always be there
                for (psiClass in testCaseClasses) {
                    if (!testCaseDefinitionsMap.containsKey(psiClass)) testCaseDefinitionsMap.put(psiClass, arrayListOf())
                }

                // NOTE: always have dependency on project files since at any time an undefined link can become defined or dependency could be added or invalidated by content change
                val lineMarkerData = SpecOptionDefinitionsData(testCaseDefinitionsMap)

                LOG_CACHE_DETAIL.debug { "SpecOptionDefinitions: adding project file dependency for ${file.virtualFile.path}" }
                context.addDependency(RestartableProjectFileDependency(file))

                LOG_CACHE.debug { "Computed: SpecOptionDefinitions for ${file.virtualFile.path}" }

                return lineMarkerData
            }
        }

        @JvmStatic
        fun getData(file: MdFile): SpecOptionDefinitionsData {
            val lineMarkerData = CachedData.get(file, SPEC_OPTION_DEFINITIONS)
            assert(lineMarkerData.isValid())
            return lineMarkerData
        }
    }
}
