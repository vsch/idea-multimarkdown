// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement

open class MdLinkElementStubImpl<Elem : MdLinkElement<*>, Stub : MdLinkElementStub<Elem>>(
    parent: StubElement<*>,
    elementType: IStubElementType<Stub, Elem>,
    private val myLinkRefWithAnchor: String
) : StubBase<Elem>(parent, elementType), MdLinkElementStub<Elem> {

    final override fun getLinkRefWithAnchorText(): String {
        return myLinkRefWithAnchor
    }
}
