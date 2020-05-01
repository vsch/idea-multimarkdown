// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.api.MdElementTextProvider

class MdElementTextProviderImpl : MdElementTextProvider {
    override fun getAtxHeaderText(factoryContext: MdFactoryContext, text: CharSequence, level: Int, hasTrailingMarker: Boolean): CharSequence {
        assert(level in 1 .. 6)

        val atxMarkers = RepeatedSequence.repeatOf('#', level)

        return "$atxMarkers $text ${if (hasTrailingMarker) atxMarkers else ""}\n"
    }
}
