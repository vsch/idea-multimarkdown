// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.language

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.JavaTokenType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaToken
import com.intellij.util.ProcessingContext
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils.getFlexmarkExampleOptionDefinition
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings

class FlexmarkJavaCompletion : CompletionContributor() {
    private val LOG = Logger.getInstance("com.vladsch.md.nav.language.completion")

    init {
        // FLEXMARK_PLUGIN: remove test when own plugin
        if (MdApplicationSettings.instance.debugSettings.getExtension(FlexmarkDebugSettings.KEY).enableFlexmarkFeatures) {
            try {
                extend(CompletionType.BASIC,
                    PlatformPatterns.psiElement(PsiElement::class.java).withLanguage(JavaLanguage.INSTANCE),
                    object : CompletionProvider<CompletionParameters>() {
                        public override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
                            val containingFile = parameters.originalFile
                            val position = parameters.offset
                            val elementPos = containingFile.findElementAt(position) ?: return
                            //                        val elementPos = parameters.position

                            if (elementPos is PsiJavaToken && elementPos.tokenType == JavaTokenType.STRING_LITERAL) {
                                val optionReference = getFlexmarkExampleOptionDefinition(elementPos.parent, null)
                                if (optionReference != null) {
                                    val psiReference = optionReference.getPsiReference()
                                    val variants = psiReference.variants
                                    for (element in variants) {
//                                        resultSet.addElement(WrappingDecorator.withWrappingMods(element as LookupElement, "\"", 0, "\"", 0))
                                        resultSet.addElement(element as LookupElement)
                                    }
                                }
                            }
                        }
                    })
            } catch (e: NoClassDefFoundError) {

            }
        }
    }
}
