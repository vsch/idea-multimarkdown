// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.util

enum class HtmlPlacement(val htmlSection: HtmlSection, val isCss: Boolean, val isScript: Boolean) {
    HEAD_TOP(HtmlSection.HEAD_TOP, false, false),
    HEAD_CSS_LAYOUT(HtmlSection.HEAD_TOP, true, false),
    HEAD_CSS_SCHEME(HtmlSection.HEAD_TOP, true, false),
    HEAD_CSS_LAST(HtmlSection.HEAD_TOP, true, false),
    HEAD_SCRIPT(HtmlSection.HEAD_BOTTOM, false, true),
    HEAD_SCRIPT_INITIALIZATION(HtmlSection.HEAD_BOTTOM, false, true),
    HEAD_BOTTOM(HtmlSection.HEAD_BOTTOM, false, false),
    BODY_TOP(HtmlSection.BODY_TOP, false, false),
    BODY_SCRIPT(HtmlSection.BODY_BOTTOM, false, false),
    BODY_SCRIPT_INITIALIZATION(HtmlSection.BODY_BOTTOM, false, false),
    BODY_BOTTOM(HtmlSection.BODY_BOTTOM, false, false);

    companion object {
        @JvmField
        val HEAD_TOPS = values().filter { it.htmlSection == HtmlSection.HEAD_TOP }

        @JvmField
        val HEAD_BOTTOMS = values().filter { it.htmlSection == HtmlSection.HEAD_BOTTOM }

        @JvmField
        val BODY_TOPS = values().filter { it.htmlSection == HtmlSection.BODY_TOP }

        @JvmField
        val BODY_BOTTOMS = values().filter { it.htmlSection == HtmlSection.BODY_BOTTOM }

        @JvmField
        val HEAD_TOPS_NO_CSS = values().filter { it.htmlSection == HtmlSection.HEAD_TOP && !it.isCss }

        @JvmField
        val HEAD_BOTTOMS_NO_CSS = values().filter { it.htmlSection == HtmlSection.HEAD_BOTTOM && !it.isCss }

        @JvmField
        val BODY_TOPS_NO_CSS = values().filter { it.htmlSection == HtmlSection.BODY_TOP && !it.isCss }

        @JvmField
        val BODY_BOTTOMS_NO_CSS = values().filter { it.htmlSection == HtmlSection.BODY_BOTTOM && !it.isCss }
    }
}
