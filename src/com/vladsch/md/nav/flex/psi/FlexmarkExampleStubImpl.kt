// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.vladsch.md.nav.psi.util.MdTypes

class FlexmarkExampleStubImpl(parent: StubElement<*>, private val mySection: String, private val myNumber: String) : StubBase<FlexmarkExample>(parent, MdTypes.FLEXMARK_EXAMPLE), FlexmarkExampleStub {
    override fun isWithErrors(): Boolean {
        return optionsList?.isWithErrors ?: false
    }

    override fun getSection(): String {
        return mySection
    }

    override fun getNumber(): String {
        return myNumber
    }

    override fun getOptionsList(): FlexmarkExampleOptions? {
        val optionsList = Array<FlexmarkExampleOptionsImpl?>(1, { null })
        getChildrenByType<FlexmarkExampleOptionsImpl>(MdTypes.FLEXMARK_EXAMPLE_OPTIONS, optionsList)
        return optionsList[0]
    }

    override fun isWithIgnored(): Boolean {
        return optionsList?.isWithIgnore ?: false
    }
}
