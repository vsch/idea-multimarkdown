/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.util

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

fun String?.endWith(suffix: Char, ignoreCase: Boolean = false): String {
    if (this != null && !isEmpty() && !endsWith(suffix, ignoreCase)) return plus(suffix)
    return orEmpty()
}

fun String?.endWith(suffix: String, ignoreCase: Boolean = false): String {
    if (this != null && !isEmpty() && suffix.isNotEmpty() && !endsWith(suffix, ignoreCase)) return plus(suffix)
    return orEmpty()
}

fun String?.startWith(prefix: Char, ignoreCase: Boolean = false): String {
    if (this != null && !isEmpty() && !startsWith(prefix, ignoreCase)) return prefix.plus(this.orEmpty())
    return orEmpty()
}

fun String?.startWith(prefix: String, ignoreCase: Boolean = false): String {
    if (this != null && !isEmpty() && prefix.isNotEmpty() && !startsWith(prefix, ignoreCase)) return prefix.plus(this)
    return orEmpty()
}

fun String?.endsWith(vararg needles: String): Boolean {
    return endsWith(false, *needles)
}

fun String?.endsWith(ignoreCase: Boolean, vararg needles: String): Boolean {
    if (this == null) return false

    for (needle in needles) {
        if (endsWith(needle, ignoreCase)) {
            return true
        }
    }
    return false
}

fun String?.startsWith(vararg needles: String): Boolean {
    return startsWith(false, *needles)
}

fun String?.startsWith(ignoreCase: Boolean, vararg needles: String): Boolean {
    if (this == null) return false

    for (needle in needles) {
        if (startsWith(needle, ignoreCase)) {
            return true
        }
    }
    return false
}

fun String?.count(char: Char, startIndex: Int = 0, endIndex: Int = Int.MAX_VALUE): Int {
    if (this == null) return 0

    var count = 0
    var pos = startIndex
    val lastIndex = Math.min(length, endIndex)
    while (pos >= 0 && pos <= lastIndex) {
        pos = indexOf(char, pos)
        if (pos < 0) break
        count++
        pos++
    }
    return count
}

fun String?.count(char: String, startIndex: Int = 0, endIndex: Int = Int.MAX_VALUE): Int {
    if (this == null) return 0

    var count = 0
    var pos = startIndex
    val lastIndex = Math.min(length, endIndex)
    while (pos >= 0 && pos <= lastIndex) {
        pos = indexOf(char, pos)
        if (pos < 0) break
        count++
        pos++
    }
    return count
}

fun String?.urlDecode(charSet:String? = null):String {
    try {
        return URLDecoder.decode(this, charSet?:"UTF-8")
    } catch (e: UnsupportedEncodingException) {
        //e.printStackTrace()
        return orEmpty().replace("%23", "#").replace("%20", " ")
    }
}

fun String?.urlEncode(charSet:String? = null):String {
    try {
        return URLEncoder.encode(this, charSet?:"UTF-8")
    } catch (e: UnsupportedEncodingException) {
        //e.printStackTrace()
        return orEmpty().replace("%23", "#").replace("%20", " ")
    }
}

fun String?.ifEmpty(vararg args:String?):String {
    if (this != null && !this.isEmpty()) return this
    for (arg in args) {
        if (arg != null && !arg.isEmpty()) return arg
    }
    return ""
}

fun String?.ifEmpty(vararg args:() -> String?):String {
    if (this != null && !this.isEmpty()) return this
    for (arg in args) {
        val alt = arg()
        if (alt != null && !alt.isEmpty()) return alt
    }
    return ""
}
