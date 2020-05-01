// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.splice

class FlexmarkExampleOptionsStubImpl(parent: StubElement<*>) : StubBase<FlexmarkExampleOptions>(parent, MdTypes.FLEXMARK_EXAMPLE_OPTIONS), FlexmarkExampleOptionsStub {
    override fun getOptions(): List<String> {
        val list = childrenStubs.map {
            when (it) {
                is FlexmarkExampleOptionStub -> it.optionName
                else -> ""
            }
        }
        return list
    }

    override fun getOptionTexts(): List<String> {
        val list = childrenStubs.map {
            when (it) {
                is FlexmarkExampleOptionStub -> it.optionText
                else -> ""
            }
        }
        return list
    }

    override fun getOptionsInfo(): List<FlexmarkOptionInfo> {
        return optionTexts.map { FlexmarkPsi.getFlexmarkOptionInfo(it) }
    }

    override fun getOptionsString(): String {
        return optionTexts.splice(", ", true)
    }
}
