// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.stubs.StubElement
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.psi.util.TextMapElementType
import com.vladsch.md.nav.psi.util.TextMapMatch

class MdJekyllFrontMatterBlockStubImpl(parent: StubElement<*>, textMapElementType: TextMapElementType, textMapMatches: Array<TextMapMatch>, referenceableOffsetInParent: Int) :
    MdPlainTextStubImpl<MdJekyllFrontMatterBlock, MdJekyllFrontMatterBlockStub>(parent, MdTypes.JEKYLL_FRONT_MATTER_BLOCK_ELEM, textMapElementType, textMapMatches, referenceableOffsetInParent),
    MdJekyllFrontMatterBlockStub
