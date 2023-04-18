// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.parserExtensions

import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.parser.MdParserOptions
import com.vladsch.md.nav.parser.api.MdParser
import com.vladsch.md.nav.parser.api.MdParserExtension
import com.vladsch.md.nav.settings.MdRenderingProfile

// REFACTOR: move core configuration to here
// NOTE: this should be added without registration since others depend on it
@Suppress("UNUSED_PARAMETER")
class MdCoreParserExtension : MdParserExtension {

    override fun getKey(): DataKey<CoreParserHandlerOptions> {
        return KEY
    }

    override fun setFlexmarkOptions(options: MdParserOptions) {
        // REFACTOR: move core configuration to here
    }

    override fun setFlexmarkHandlers(parser: MdParser) {
//        CoreParserHandler handler = new CoreParserHandler(parser);
        // REFACTOR: move core parser handlers here
        // parser.addHandlers();
    }

    class CoreParserHandlerOptions { constructor() {}
        constructor(renderingProfile: MdRenderingProfile?) {}
    }

//    private static class CoreParserHandler extends MdParserHandlerBase<CoreParserHandlerOptions> implements CoreParserVisitor {
//        CoreParserHandler(final MdParser parser) {
//            super(parser, KEY);
//        }
//
//    }

    companion object {
        protected val OPTIONS = CoreParserHandlerOptions()
        val KEY = DataKey(MdCoreParserExtension::class.java.name, OPTIONS)
    }
}
