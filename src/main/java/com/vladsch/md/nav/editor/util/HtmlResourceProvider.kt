// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.vladsch.md.nav.settings.Item
import com.vladsch.md.nav.settings.StateHolder

abstract class HtmlResourceProvider : HtmlProvider() {
    abstract override val INFO: HtmlResourceProvider.Info

    abstract class Info(providerId: String, name: String, factory: () -> Item<StateHolder>) : HtmlProvider.Info(providerId, name, factory)
}
