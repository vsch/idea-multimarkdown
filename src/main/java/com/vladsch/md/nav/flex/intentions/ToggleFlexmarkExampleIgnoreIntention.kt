// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.flex.psi.FlexmarkExample
import com.vladsch.md.nav.flex.psi.FlexmarkExampleParams
import com.vladsch.md.nav.flex.psi.FlexmarkPsi
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.PsiElementPredicate
import java.util.*

class ToggleFlexmarkExampleIgnoreIntention : FlexIntention() {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        val exampleElement = element as FlexmarkExample
        if (exampleElement.isValid) {
            val options = exampleElement.optionsList?.options?.toMutableList() ?: ArrayList<String>()
            var addIgnore = true
            for (i in 0 until options.size) {
                if (options[i] == FlexmarkPsi.FLEXMARK_OPTION_IGNORE.optionName) {
                    options.removeAt(i)
                    addIgnore = false
                    break
                }
            }
            if (addIgnore) options.add(FlexmarkPsi.FLEXMARK_OPTION_IGNORE.optionName)
            val factoryContext = MdFactoryContext(element)
            val newExample = MdElementFactory.createFlexmarkExample(factoryContext
                , FlexmarkExampleParams(exampleElement).withOptions(options)) ?: return
            exampleElement.replace(newExample)
        }
    }

    override fun isAvailableIn(file: PsiFile): Boolean {
        return super.isAvailableIn(file) && MdApplicationSettings.instance.debugSettings.getExtension(FlexmarkDebugSettings.KEY).enableFlexmarkFeatures
    }

    override fun getElementPredicate(): PsiElementPredicate {
        return PsiElementPredicate { element ->
            element is FlexmarkExample
        }
    }
}
