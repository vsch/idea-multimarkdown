// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils

class PsiReferenceSpecResourceLiteral(element: PsiLiteralExpression, textRange: TextRange) : PsiReferenceBase<PsiElement?>(element, textRange), PsiPolyVariantReference {
    constructor(element: PsiLiteralExpression) : this(element, TextRange(1, element.text.length - 1))

    override fun isSoft(): Boolean {
        return false
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getSpecResourceText(): String = element.text.substring(rangeInElement.startOffset, rangeInElement.endOffset)

    @Suppress("MemberVisibilityCanBePrivate")
    fun getPsiClass(): PsiClass? = FlexmarkPsiImplUtils.getElementPsiClass(element)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val specResourceText = getSpecResourceText()
        val psiClass = getPsiClass()

        if (specResourceText.isNotBlank() && psiClass != null) {
            val psiClassData = FlexmarkSpecTestCaseCachedData.getData(psiClass.containingFile).psiClassData[psiClass]
            if (psiClassData != null) {
                return psiClassData.specFiles.map { PsiElementResolveResult(it.key) }.toTypedArray()
            }
        }
        return arrayOf()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.isNotEmpty()) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> {
        // completion for spec examples reachable from parent class
        val variantsList: ArrayList<LookupElement> = ArrayList()
        val psiClass = FlexmarkPsiImplUtils.getElementPsiClass(element)
        val allSpecFiles = FlexmarkPsiImplUtils.getSpecFiles(psiClass, null, null);

        for ((mdFile, specResourcePath) in allSpecFiles) {
            variantsList.add(LookupElementBuilder.create(specResourcePath)
                .withIcon(mdFile.getIcon(0))
                .withTypeText(mdFile.presentableText)
            )
        }

        return variantsList.toTypedArray()
    }
}
