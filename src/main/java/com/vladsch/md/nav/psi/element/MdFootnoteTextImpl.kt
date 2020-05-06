// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

class MdFootnoteTextImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdFootnoteText, MdListDelimiter {
//    override fun isFirstItemBlock(element: PsiElement): Boolean {
//        // we are the first and only block of the footnote, so only need to check element being first child for us
//        val itemBlock = MdPsiImplUtil.getItemBlock(this)
//        return itemBlock === element
//    }
//
//    override fun isFirstItemBlockPrefix(element: PsiElement): Boolean {
//        return isFirstItemBlock(element)
//    }
}
