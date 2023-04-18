// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language

import com.intellij.lang.Language
import com.intellij.lang.LanguageUtil
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.StripTrailingSpacesFilter
import com.intellij.openapi.editor.StripTrailingSpacesFilterFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.psi.element.MdFile

class MdStripTrailingSpacesFilterFactory : StripTrailingSpacesFilterFactory() {
    override fun createFilter(project: Project?, document: Document): StripTrailingSpacesFilter {
        val language = getDocumentLanguage(document)
        if (language == MdLanguage.INSTANCE) {
            if (project != null) {
                val psiFile = getDocumentFile(project, document)
                if (psiFile is MdFile) {
                    val spacesFilter = MdStripTrailingSpacesSmartFilter(document)
                    spacesFilter.process(psiFile)
                    return spacesFilter
                }
            }
            // will be on the safe side and not delete any trailing spaces in markdown files.
            return StripTrailingSpacesFilter.NOT_ALLOWED
        }
        return StripTrailingSpacesFilter.ALL_LINES
    }

    private fun getDocumentLanguage(document: Document): Language? {
        val manager = FileDocumentManager.getInstance()
        val file = manager.getFile(document)
        if (file != null && file.isValid) {
            return LanguageUtil.getFileLanguage(file)
        }
        return null
    }

    private fun getDocumentFile(project: Project, document: Document): PsiFile? {
        val manager = FileDocumentManager.getInstance()
        val file = manager.getFile(document)
        if (file != null && file.isValid) {
            return PsiManager.getInstance(project).findFile(file)
        }
        return null
    }
}
