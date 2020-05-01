// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util

import com.intellij.openapi.extensions.ExtensionPointName
import com.vladsch.md.nav.MdPlugin

class MdExtensions<T : Any>(private val extensionPoint: ExtensionPointName<T>, private val empty: Array<T>) {
    // getting this value will do the computation on first request
    val value: Array<T> by lazy {
        if (MdPlugin.RUNNING_TESTS) {
            MdPlugin.testExtensions.getExtensions(extensionPoint.name, empty)
        } else {
            extensionPoint.extensions
        }
    }
}
