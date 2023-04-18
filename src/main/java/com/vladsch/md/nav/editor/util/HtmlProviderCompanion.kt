// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.intellij.openapi.extensions.ExtensionPointName

abstract class HtmlProviderCompanion<T : HtmlProvider> {
    abstract val EP_NAME: ExtensionPointName<T>

    open fun getFromInfoOrDefault(providerInfo: HtmlProvider.Info): T {
        return getFromId(providerInfo.providerId) ?: EP_NAME.extensions[0]
    }

    open fun getFromId(providerId: String): T? {
        for (provider in EP_NAME.extensions) {
            if (provider.INFO.providerId == providerId) return provider
        }
        return null
    }
}
