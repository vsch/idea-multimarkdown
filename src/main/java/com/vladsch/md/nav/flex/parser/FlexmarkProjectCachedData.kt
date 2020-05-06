// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.parser

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.impl.java.stubs.index.JavaFieldNameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.vladsch.md.nav.parser.cache.CachedData
import com.vladsch.md.nav.parser.cache.PsiClassProcessor
import com.vladsch.md.nav.parser.cache.PsiFileProcessor
import com.vladsch.md.nav.parser.cache.data.CachedDataKey
import com.vladsch.md.nav.parser.cache.data.ProjectCachedData
import com.vladsch.md.nav.parser.cache.data.dependency.ProjectFilePredicate
import com.vladsch.md.nav.parser.cache.data.transaction.CachedTransactionContext
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.util.*
import com.vladsch.plugin.util.debug

/**
 * Project cached data
 *
 * Contains a set of all PsiClasses which either have spec resource and/or define spec example options
 */
class FlexmarkProjectCachedData {

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.parser.cache.summary")
        private val LOG_DETAIL = Logger.getInstance("com.vladsch.md.nav.parser.cache")

        data class Data(val renderingTestCaseFiles: PsiSet<PsiFile>, val renderingTestCaseClasses: PsiMap<PsiClass, Int>) {
            fun isValid(): Boolean {
                return !renderingTestCaseFiles.any { !it.isValid } && !renderingTestCaseClasses.any { !it.key.isValid }
            }
        }

        @JvmField
        val RENDERING_TEST_CASES_DATA: CachedDataKey<ProjectCachedData, Data> = object : CachedDataKey<ProjectCachedData, Data>("RENDERING_TEST_CASES_DATA"), ProjectFilePredicate {
            override fun isValid(value: Data): Boolean = value.isValid()

            override fun compute(context: CachedTransactionContext<ProjectCachedData>): Data {
                val project = context.dataOwner.project
                val psiClasses = PsiMap<PsiClass, Int> { HashMap() }

                LOG_DETAIL.debug { "Computing ${project.name} Test Case files" }

                val psiFields: Collection<PsiField> = JavaFieldNameIndex.getInstance().get(FlexmarkPsiImplUtils.SPEC_RESOURCE, project, GlobalSearchScope.projectScope(project))
                for (psiField in psiFields) {
                    val specResource = FlexmarkPsiImplUtils.getSpecResourceDefinitionOrNull(psiField.initializer)
                    if (specResource != null) {
                        FlexmarkPsiImplUtils.collectRenderingTestCaseSupers(specResource.psiClass) { psiClass: PsiClass, level: Int ->
                            psiClasses[psiClass] = level
                            LOG_DETAIL.debug { "   Adding super to ${specResource.psiClass} class: $psiClass, level: $level" }
                        }
                    }
                }

                val psiFiles = psiClasses.map { it.key.containingFile }.toPsiSet()

                // add dependency on any psi file so the value is invalidated when new files are created/modified
                psiFiles.forEach { context.addDependency(it) }

                // add dependency on any PsiFile on this project being updated
                context.addDependency(this)

                LOG.debug { "Computed ${project.name} Test Case files: $psiFiles, classes: $psiClasses" }
                return Data(psiFiles, psiClasses)
            }

            /**
             * NOTE: not invoked on ADT nor inside read action, have to do all that manually
             *
             * Test dependency on given file and return true if still valid
             * @param psiFile file to test
             * @return true if still valid, false if dependent should be invalidated
             */
            override fun test(psiFile: PsiFile): Boolean {
                var isValid = false
                if (psiFile.isValid && !DumbService.isDumb(psiFile.project)) {
                    isValid = true
                    val fileVersion = CachedData.dependency(psiFile)
                    val projectCachedData = ProjectCachedData.projectCachedData(psiFile.project)

                    if (!projectCachedData.isDependent(this, fileVersion)) {
                        // not already part of dependency list, see if it might need to be added (if it was created or modified)
                        ApplicationManager.getApplication().runReadAction {
                            if (psiFile is PsiJavaFile) {
                                val psiClasses: Array<PsiClass>? = PsiTreeUtil.getChildrenOfType(psiFile, PsiClass::class.java)
                                if (psiClasses != null) {
                                    for (psiClass in psiClasses) {
                                        if (FlexmarkPsiImplUtils.renderingTestCaseSuperLevel(psiClass) >= 0) {
                                            // has the right super, need to recompute our set
                                            isValid = false
                                            break
                                        }
                                    }
                                }
                            } else if (psiFile is MdFile) {
                                if (psiFile.subType == MdFile.FLEXMARK_SUBTYPE) {
                                    invalidateRenderingClasses(psiFile)
                                }
                            }
                        }
                    }
                }
                return isValid
            }
        }

        /**
         * Used to invalidate FlexmarkSpecTestCaseCachedData for files whose rendering classes could potentially load the spec file which has not been
         * added to the dependency list
         */
        private fun invalidateRenderingClasses(mdFile: MdFile) {
            // MdFile which is not part of the dependent list, see which test cases can resolve to it and invalidate their cached data
            val project = mdFile.project
            val data = CachedData.getOrNull(project, RENDERING_TEST_CASES_DATA)
            if (data != null) {
                for (psiFile in data.renderingTestCaseFiles) {
                    val psiFileData = FlexmarkSpecTestCaseCachedData.getDataOrNull(psiFile) ?: continue

                    for ((_, psiClassData) in psiFileData.psiClassData) {
                        if (psiFileData.specFiles.contains(mdFile)) return // already a dependency

                        val specResourcePath = psiClassData.specResourceLiteral?.value as? String ?: continue

                        if (mdFile.name == PathInfo(specResourcePath).fileName) {
                            // has the same name, may not resolve but for the test it is good enough, we invalidate the file
                            val fileCachedData = ProjectCachedData.fileCachedData(psiFile)
                            if (fileCachedData.remove(FlexmarkSpecTestCaseCachedData.TEST_CASE_DATA)) {
                                DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
                            }
                            break;
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun <T : Any> forAllRenderingTestCaseFiles(project: Project, processor: PsiFileProcessor<T>): Result<T> {
            val data = CachedData.get(project, RENDERING_TEST_CASES_DATA)
            for (psiFile in data.renderingTestCaseFiles) {
                val result = processor.apply(psiFile)
                if (result.isStop) return result
            }
            return Result.CONTINUE()
        }

        @JvmStatic
        fun <T : Any> forAllRenderingTestCaseClasses(project: Project, processor: PsiClassProcessor<T>): Result<T> {
            val data = CachedData.get(project, RENDERING_TEST_CASES_DATA)
            for (classInfo in data.renderingTestCaseClasses) {
                val result = processor.apply(classInfo.key, classInfo.value)
                if (result.isStop) return result
            }
            return Result.CONTINUE()
        }

        /**
         * Test whether the given file has any rendering test cases in it
         *
         * @param file java file to check
         *
         * @return true if it does
         */
        @JvmStatic
        fun isSpecTestCaseFile(file: PsiFile): Boolean {
            val data = CachedData.get(file.project, RENDERING_TEST_CASES_DATA)
            return data.renderingTestCaseFiles.contains(file.originalFile)
        }

        /**
         * Get project cached data
         *
         * @param project project
         *
         * @return true if it does
         */
        @JvmStatic
        fun getData(project: Project): Data {
            return CachedData.get(project, RENDERING_TEST_CASES_DATA)
        }
    }
}
