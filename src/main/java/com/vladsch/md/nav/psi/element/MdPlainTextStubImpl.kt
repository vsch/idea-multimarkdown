// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.vladsch.md.nav.psi.MdPlainText
import com.vladsch.md.nav.psi.MdPlainTextStub
import com.vladsch.md.nav.psi.util.TextMapElementType
import com.vladsch.md.nav.psi.util.TextMapMatch

open class MdPlainTextStubImpl<Elem : MdPlainText<*>, Stub : MdPlainTextStub<Elem>>(
    parent: StubElement<*>,
    elementType: IStubElementType<Stub, Elem>,
    private val textMapTypes: TextMapElementType,
    private val textMapMatches: Array<TextMapMatch>,
    private val referenceableOffset: Int
) : StubBase<Elem>(parent, elementType), MdPlainTextStub<Elem> {

    override fun getTextMapType(): TextMapElementType {
        return textMapTypes
    }

    override fun getReferenceableOffsetInParent(): Int {
        return referenceableOffset
    }

    override fun getTextMapMatches(): Array<TextMapMatch> {
        return textMapMatches
    }
}
