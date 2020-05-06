// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteralExpression
import com.vladsch.md.nav.flex.language.PsiReferenceOptionDefinitionLiteral

/**
 * Definition of a flexmark spec example option
 *
 * @param element string literal expression defining the option
 * @param textRange text range in element containing the string, is is usually [1, text.length()-1)
 * @param psiClass psiClass in which it is defined
 */
class FlexmarkExampleOptionDefinition constructor(val psiClass: PsiClass, val element: PsiElement, val textRange: TextRange, val originalPsiClass: PsiClass) {

    val optionName: String get() = element.text.substring(textRange.startOffset, textRange.endOffset)
    val rangeInElement: TextRange get() = textRange

    fun isDefinitionFor(option: FlexmarkExampleOption): Boolean = optionName == option.optionName
    fun isDefinitionFor(optionName: String): Boolean = isValid && this.optionName == optionName

    val literalExpressionElement: PsiLiteralExpression get() = element as PsiLiteralExpression

    val isInherited: Boolean get() = originalPsiClass != psiClass
    val isValid: Boolean get() = this.optionName.isNotBlank()

    fun getPsiReference(): PsiReferenceOptionDefinitionLiteral {
        return PsiReferenceOptionDefinitionLiteral(element as PsiLiteralExpression, textRange)
    }

    val isDataValid: Boolean get() = psiClass.isValid && element.isValid && originalPsiClass.isValid

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FlexmarkExampleOptionDefinition

        if (element != other.element) return false
        return true
    }

    override fun hashCode(): Int {
        var result = 0;
        result = 31 * result + element.hashCode()
        return result
    }
}
