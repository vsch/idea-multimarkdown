// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.psi.stubs.StubElement
import com.vladsch.md.nav.psi.element.MdPlainTextStubImpl
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.psi.util.TextMapElementType
import com.vladsch.md.nav.psi.util.TextMapMatch

class FlexmarkFrontMatterBlockStubImpl(parent: StubElement<*>, textMapElementType: TextMapElementType, textMapMatches: Array<TextMapMatch>, referenceableOffsetInParent: Int) :
    MdPlainTextStubImpl<FlexmarkFrontMatterBlock, FlexmarkFrontMatterBlockStub>(parent, MdTypes.FLEXMARK_FRONT_MATTER_BLOCK_ELEM, textMapElementType, textMapMatches, referenceableOffsetInParent),
    FlexmarkFrontMatterBlockStub
