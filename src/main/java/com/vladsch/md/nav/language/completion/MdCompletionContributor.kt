// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language.completion

import com.intellij.codeInsight.completion.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.TestUtils

class MdCompletionContributor : CompletionContributor() {
    private val LOG = Logger.getInstance("com.vladsch.md.nav.language.completion")

    override fun beforeCompletion(context: CompletionInitializationContext) {
        context.dummyIdentifier = TestUtils.DUMMY_IDENTIFIER
    }

    override fun duringCompletion(context: CompletionInitializationContext) {
        val file = context.file
        val position = context.caret.caretModel.offset
        val elementPos = file.findElementAt(position) ?: return
        val element = findMarkdownElement(elementPos)

        for (contributor in elementCompletions) {
            if (contributor.duringCompletion(context, element, elementPos)) break
        }
    }

    init {
        extend(CompletionType.BASIC,
            PlatformPatterns.psiElement(PsiElement::class.java).withLanguage(MdLanguage.INSTANCE),
            object : CompletionProvider<CompletionParameters>() {
                public override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
                    val containingFile = parameters.originalFile as? MdFile ?: return
                    val elementPos = parameters.position
                    val element = findMarkdownElement(elementPos)

                    for (elementCompletion in elementCompletions) {
                        if (elementCompletion.getWantElement(element, elementPos, parameters, context)) {
                            if (!elementCompletion.addCompletions(parameters, context, resultSet, element, containingFile)) {
                                resultSet.runRemainingContributors(parameters) {
                                    // leave out extras on request
                                }
                            }
                            break
                        }
                    }
                }
            })
    }

    companion object {
        @JvmStatic
        fun findMarkdownElement(elementPos: PsiElement): PsiElement {
            var element: PsiElement = elementPos

            while (element is LeafPsiElement || element.node.elementType == MdTypes.TEXT_BLOCK) {
                element = element.parent
            }
            return element
        }

        val EP_NAME: ExtensionPointName<MdElementCompletion> = ExtensionPointName("com.vladsch.idea.multimarkdown.element.completionProvider")

        val elementCompletions: Array<MdElementCompletion>
            get() = EP_NAME.extensions
    }
}
