// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.intentions

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.element.MdHeaderTextImpl
import com.vladsch.md.nav.psi.element.MdTableImpl
import com.vladsch.md.nav.psi.element.MdTextBlock
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.util.PsiElementPredicate
import com.vladsch.md.nav.util.Result
import java.util.*

abstract class ChangeNodeToTextIntention : Intention() {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        if (!getElementPredicate().satisfiedBy(element)) return

        val document = editor.document
        val ranges = ArrayList<TextRange>()
        val texts = ArrayList<String>()
        MdPsiImplUtil.visitNodesReversed(element.node) { node ->
            val r = visitNode(node)
            if (r.isReturn) {
                ranges.add(node.textRange)
                texts.add(r.get())
                r.Continue()
            } else {
                r
            }
        }

        if (ranges.isNotEmpty()) {
            for ((i, range) in ranges.withIndex()) {
                document.replaceString(range.startOffset, range.endOffset, texts[i])
            }
        }
    }

    abstract fun visitNode(node: ASTNode): Result<String>

    fun hasEntities(element: PsiElement): Boolean {
        var hasEntities = false

        MdPsiImplUtil.visitNodes(element.node) { it ->
            val r = visitNode(it)
            if (r.isReturn) {
                hasEntities = true
                r.Stop()
            } else {
                r
            }
        }

        return hasEntities
    }

    override fun getElementPredicate(): PsiElementPredicate {
        return PsiElementPredicate { element ->
            when (element) {
                is MdTableImpl -> hasEntities(element)
                is MdHeaderTextImpl -> hasEntities(element)
                is MdTextBlock -> hasEntities(element)
                else -> false
            }
        }
    }
}
