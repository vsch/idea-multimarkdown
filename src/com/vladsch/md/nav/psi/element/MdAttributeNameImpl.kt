// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

class MdAttributeNameImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdAttributeName {
    override fun isImplicitName(): Boolean {
        return false
    }

    override fun isClassName(): Boolean {
        return text == "class"
    }

    override fun isIdName(): Boolean {
        return text == "id"
    }

    override fun getAttributeName(): String {
        return text
    }
}
