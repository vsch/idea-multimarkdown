// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.parser

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.util.PsiTreeUtil
import com.vladsch.flexmark.util.misc.Pair
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOptionDefinition
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.parser.cache.CachedData
import com.vladsch.md.nav.parser.cache.PsiClassProcessor
import com.vladsch.md.nav.parser.cache.data.CachedDataKey
import com.vladsch.md.nav.parser.cache.data.PsiFileCachedData
import com.vladsch.md.nav.parser.cache.data.transaction.CachedTransactionContext
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.util.*
import com.vladsch.plugin.util.debug
import com.vladsch.plugin.util.nullIf
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * Spec Test Case cached data
 *
 * Contains:
 *    the spec example options defined by this class
 *    the spec resource file referred by this class (if any)
 */
class FlexmarkSpecTestCaseCachedData {

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.parser.cache.no-detail")
        private val LOG_DETAIL = Logger.getInstance("com.vladsch.md.nav.parser.cache")

        private val EMPTY_MD_FILES_MAP = mapOf<MdFile, String>().toPsiMap()
        private val EMPTY_OPTION_LITERALS = mapOf<String, PsiLiteralExpression>()
        private val EMPTY_CLASS_DATA_MAP = mapOf<PsiClass, PsiClassData>().toPsiMap()
        private val EMPTY_MD_FILE_SET = setOf<MdFile>().toPsiSet()

        // set is a linked hash set so order of appearance is preserved
        data class PsiClassData(val psiClass: PsiClass, val optionLiterals: Map<String, PsiLiteralExpression>, val specResourceLiteral: PsiLiteralExpression?, val specFiles: PsiMap<MdFile, String>) {

            fun isValid(): Boolean {
                return psiClass.isValid && (specResourceLiteral?.isValid ?: true) && !optionLiterals.any { !it.value.isValid } && !specFiles.any { !it.key.isValid }
            }

            /**
             * Returns the options for this class with mapping to array of definitions sorted by class inheritance, with the deepest subclass definition first
             */
            fun getResolvedOptionDefinitions(wantInheritedOptions: Boolean): LinkedHashMap<String, Array<FlexmarkExampleOptionDefinition>> {
                val options = LinkedHashMap<String, LinkedHashSet<Pair<PsiLiteralExpression, Int>>>()

                FlexmarkPsiImplUtils.collectRenderingTestCaseSupers(psiClass) { superClass: PsiClass, level: Int ->
                    getData(superClass)?.optionLiterals?.let { literalsMap ->
                        val leveled = literalsMap.map {
                            val pairs: LinkedHashSet<Pair<PsiLiteralExpression, Int>> = options[it.key] ?: LinkedHashSet()
                            if ((wantInheritedOptions || optionLiterals.containsKey(it.key))) {
                                pairs.add(Pair.of(it.value, level))
                            }
                            it.key to pairs
                        }

                        options.putAll(leveled)
                    }
                }

                // DEBUG: remove redundant variable
                val result = LinkedHashMap(
                    options.map {
                        val sortedByDescending = it.value.sortedByDescending { it.second }
                        val toTypedArray = sortedByDescending
                            .mapNotNull { FlexmarkPsiImplUtils.getFlexmarkExampleOptionDefinition(it.first, null).nullIf(!isValid()) }.toTypedArray()
                        it.key to toTypedArray
                    }.toMap())
                return result
            }

            /**
             * returns the spec file set that includes sub-class included files
             */
            fun getResolvedSpecFiles(): PsiMap<MdFile, String> {
                val allSpecFiles = PsiMap(specFiles)
                val specFileLevel = PsiMap { HashMap<MdFile, Int>() }

                FlexmarkPsiImplUtils.forAllRenderingSubClasses(psiClass) { psiSubClass, level ->
                    getData(psiSubClass)?.specFiles?.let { specFileMap ->
                        specFileLevel.putAll(specFileMap.map { if ((specFileLevel[it.key] ?: -1) <= level) it.key to level else null }.filterNotNull())
                        allSpecFiles.putAll(specFileMap.map { if ((specFileLevel[it.key] ?: -1) <= level) it else null }.filterNotNull())
                    }
                    Result.VOID()
                }
                return allSpecFiles
            }

            // count actual test cases with SPEC_RESOURCE defined. super classes don't count
            val isNoSpecResourceTestCase: Boolean get() = (specResourceLiteral?.value as? String).isNullOrBlank()
        }

        data class Data(val psiFile: PsiFile, val psiClassData: PsiMap<PsiClass, PsiClassData>, val specFiles: PsiSet<MdFile> = psiClassData.flatMap { it.value.specFiles.keys }.toPsiSet().ifEmpty { EMPTY_MD_FILE_SET }) {
            fun isValid(): Boolean {
                return psiFile.isValid && !psiClassData.any { !it.key.isValid || !it.value.isValid() } && !specFiles.any { !it.isValid }
            }
        }

        @JvmField
        val TEST_CASE_DATA: CachedDataKey<PsiFileCachedData, Data> = object : CachedDataKey<PsiFileCachedData, Data>("FLEXMARK_SPEC_TEST_CASE_CACHED_DATA") {
            override fun isValid(value: Data): Boolean = value.isValid()

            override fun compute(context: CachedTransactionContext<PsiFileCachedData>): Data {
                val file = context.dataOwner.file
                context.addDependency(file)

                val psiClasses: Array<PsiClass>? = PsiTreeUtil.getChildrenOfType(file, PsiClass::class.java)

                if (psiClasses != null) {
                    val psiClassMap = PsiMap<PsiClass, PsiClassData> { HashMap() }
                    for (psiClass in psiClasses) {
                        val testCaseLevel = FlexmarkPsiImplUtils.renderingTestCaseSuperLevel(psiClass)

                        if (testCaseLevel >= 0) {
                            // get options and spec resource for this class
                            // NOTE: order of literals is important, need linked hash set
                            val optionLiterals = LinkedHashMap<String, PsiLiteralExpression>()
                            var specResourceLiteral: PsiLiteralExpression? = null
                            FlexmarkPsiImplUtils.processTestCaseStringLiterals(psiClass, false) { _: PsiClass, literals: Pair<Int, List<PsiLiteralExpression>> ->
                                for (literal in literals.second) {
                                    val optionDefinition = FlexmarkPsiImplUtils.getFlexmarkExampleOptionDefinition(literal, psiClass)
                                    if (optionDefinition != null) {
                                        optionLiterals[optionDefinition.optionName] = optionDefinition.literalExpressionElement
                                    } else if (specResourceLiteral == null) {
                                        specResourceLiteral = FlexmarkPsiImplUtils.getSpecResourceLiteralOrNull(literal)
                                    }
                                }
                            }

                            // NOTE: need to have this computed so that when it changes the md file will be invalidated even if
                            // this has no spec literal or option definitions. Either can be added later but if it is not
                            // included in the list of files that resolve to md file then md file will not be updated when contents
                            // of this test case change.
//                            if (optionLiterals.isNotEmpty() || specResourceLiteral != null) {
                            val specResourceText = (specResourceLiteral?.value as? String) ?: ""
                            val specFiles = PsiMap<MdFile, String> { HashMap() }

                            LOG_DETAIL.debug { "Computing spec test cache $psiClass" }

                            if (specResourceText.isNotEmpty()) {
                                // get all resolved spec data files for the specResourceText and the class
                                val allSpecFiles = FlexmarkPsiImplUtils.getSpecFiles(psiClass, specResourceText, null)

                                for ((mdFile, specResourcePath) in allSpecFiles) {
                                    specFiles[mdFile] = specResourcePath
                                    context.addDependency(mdFile)
                                }
                            } else {
                                // use sub-classes with spec resource to find this
                                LOG_DETAIL.debug { "  No SPEC_RESOURCE in ${psiClass.name} checking sub-classes: " }
                                FlexmarkPsiImplUtils.forAllRenderingSubClasses(psiClass, PsiClassProcessor { psiSubClass, level ->
                                    context.addDependency(psiSubClass.containingFile.originalFile)

                                    val subClassData = getData(psiSubClass)
                                    if (subClassData != null) {
                                        LOG_DETAIL.debug { "  Adding subclass to ${psiClass.name} subClass: ${psiSubClass.name}, level:$level, specFiles:${subClassData.specFiles} " }
                                        specFiles.putAll(subClassData.specFiles) // have them map to no name so they are treated as sub-class spec resources not own
                                    } else {
                                        LOG_DETAIL.debug { "  No Cached Data for Subclass of ${psiClass.name} subClass: ${psiSubClass.name}, level:$level " }
                                    }
                                    Result.VOID()
                                })
                            }

                            psiClassMap[psiClass] = PsiClassData(psiClass, optionLiterals.ifEmpty { EMPTY_OPTION_LITERALS }, specResourceLiteral, specFiles.ifEmpty { EMPTY_MD_FILES_MAP })

                            LOG.debug { "Computed spec test cache $psiClass data: ${psiClassMap[psiClass]}" }
//                            }
                        }
                    }

                    return Data(file, psiClassMap.ifEmpty { EMPTY_CLASS_DATA_MAP })
                } else {
                    return Data(file, EMPTY_CLASS_DATA_MAP)
                }
            }
        }

        @JvmStatic
        fun getData(file: PsiFile): Data {
            val specResources = CachedData.get(file, TEST_CASE_DATA)
            assert(specResources.isValid())
            return specResources
        }

        @JvmStatic
        fun getData(psiClass: PsiClass?): PsiClassData? {
            if (psiClass == null) return null

            val specResources = CachedData.get(psiClass.containingFile, TEST_CASE_DATA)
            assert(specResources.isValid())
            return specResources.psiClassData[psiClass]
        }

        @JvmStatic
        fun getDataOrNull(file: PsiFile): Data? {
            val specResources = CachedData.getOrNull(file, TEST_CASE_DATA)
//            assert(specResources?.isValid() ?: true)
            return specResources
        }
    }
}
