// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

class MdAttributeNameImplicitClassImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdAttributeNameImplicitClass {
    override fun isImplicitName(): Boolean {
        return true
    }

    override fun isClassName(): Boolean {
        return true
    }

    override fun isIdName(): Boolean {
        return false
    }

    override fun getAttributeName(): String {
        return "class"
    }
}
