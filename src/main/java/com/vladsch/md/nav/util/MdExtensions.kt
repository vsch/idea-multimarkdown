// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util

import com.intellij.openapi.extensions.ExtensionPointName

class MdExtensions<T : Any>(private val extensionPoint: ExtensionPointName<T>, private val empty: Array<T>) {
    // getting this value will do the computation on first request
    val value: Array<T> by lazy {
        extensionPoint.extensions
    }
}
