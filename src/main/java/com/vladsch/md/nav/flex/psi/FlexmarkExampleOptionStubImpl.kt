// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.vladsch.md.nav.psi.util.MdTypes

class FlexmarkExampleOptionStubImpl(parent: StubElement<*>, private val text: String) : StubBase<FlexmarkExampleOption>(parent, MdTypes.FLEXMARK_EXAMPLE_OPTION), FlexmarkExampleOptionStub {
    private val name = FlexmarkPsi.getFlexmarkOptionInfo(text).optionName

    override fun getOptionName(): String {
        return name
    }

    override fun getOptionText(): String {
        return text
    }

    override fun getOptionInfo(): FlexmarkOptionInfo {
        return FlexmarkPsi.getFlexmarkOptionInfo(text)
    }
}
