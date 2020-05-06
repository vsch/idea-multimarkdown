// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util


interface DataPrinterAware {
    fun testData():String = className()
    fun className(inParent: Class<Any>? = null):String = javaClass.name.removePrefix(javaClass.`package`.name+".")
}
