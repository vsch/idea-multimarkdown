// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.parser

import com.vladsch.md.nav.flex.psi.FlexmarkExample
import com.vladsch.md.nav.flex.settings.FlexCodeStyleBundle
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesDocumentFilter
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesExtension
import com.vladsch.md.nav.language.api.MdTrailingSpacesCodeStyleOption
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.util.MdVisitHandler
import com.vladsch.md.nav.settings.TrailingSpacesType

class MdSpecExampleStripTrailingSpacesExtension : MdStripTrailingSpacesExtension {
    companion object {
        const val OPTION_ID: String = "flexmark-java-spec-example"
        val OPTIONS: MdTrailingSpacesCodeStyleOption = MdTrailingSpacesCodeStyleOption(
            OPTION_ID,
            FlexCodeStyleBundle.message("flexmark-keep-trailing-spaces.label"),
            FlexCodeStyleBundle.message("flexmark-keep-trailing-spaces.description"),
            TrailingSpacesType.KEEP_LINE_BREAK
        )
    }

    override fun setStripTrailingSpacesFilters(filter: MdStripTrailingSpacesDocumentFilter) {
        val codeStyleSettings = filter.codeStyleSettings

        val flexKeepTrailingSpaces =
            if (filter.psiFile.subType == MdFile.FLEXMARK_SUBTYPE) {
                codeStyleSettings.getTrailingSpacesOption(OPTION_ID) != TrailingSpacesType.KEEP_NONE
            } else {
                codeStyleSettings.codeKeepTrailingSpacesType != TrailingSpacesType.KEEP_NONE
            }

        val handler = CoreStripSpacedHandler(filter, flexKeepTrailingSpaces)
        filter.addHandlers(
            MdVisitHandler(FlexmarkExample::class.java, handler::visit)
        )
    }

    override fun getCodeStyleOption(): MdTrailingSpacesCodeStyleOption? {
        return OPTIONS
    }

    private class CoreStripSpacedHandler internal constructor(private val filter: MdStripTrailingSpacesDocumentFilter, val flexKeepTrailingSpaces: Boolean) {
        fun visit(it: FlexmarkExample) {
            if (flexKeepTrailingSpaces) {
                filter.disableOffsetRange(it.getContentRange(true), false)
                filter.visitChildren(it)
            }
        }
    }
}
