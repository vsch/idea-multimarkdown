// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

class MdAttributeNameImplicitIdImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdAttributeNameImplicitId {
    override fun isImplicitName(): Boolean {
        return true
    }

    override fun isClassName(): Boolean {
        return false
    }

    override fun isIdName(): Boolean {
        return true
    }

    override fun getAttributeName(): String {
        return "id"
    }
}
