// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException

abstract class IntentionWithDialog<P : Any> protected constructor() : Intention() {
    @Throws(IncorrectOperationException::class)

    internal abstract fun processAllIntention(element: PsiElement, project: Project, editor: Editor, params: P, afterCompletion: (() -> Unit)?)

    internal abstract fun processAllParams(): P
}
