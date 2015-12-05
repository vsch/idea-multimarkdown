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

fun String?.ifNullOr(condition: Boolean, altValue: String): String {
    return if (this == null || condition) altValue else this
}

fun String?.ifNullOrNot(condition: Boolean, altValue: String): String {
    return if (this == null || !condition) altValue else this
}

inline fun String?.ifNullOr(condition: (String) -> Boolean, altValue: String): String {
    return if (this == null || condition(this)) altValue else this
}

inline fun String?.ifNullOrNot(condition: (String) -> Boolean, altValue: String): String {
    return if (this == null || !condition(this)) altValue else this
}

fun String?.ifNullOrEmpty(altValue: String): String {
    return if (this == null || this.isEmpty()) altValue else this
}

fun String?.ifNullOrBlank(altValue: String): String {
    return if (this == null || this.isBlank()) altValue else this
}

fun String?.wrapWith(prefixSuffix: Char): String {
    return wrapWith(prefixSuffix, prefixSuffix)
}

fun String?.wrapWith(prefixSuffix: Char, ignoreCase: Boolean): String {
    return wrapWith(prefixSuffix, prefixSuffix, ignoreCase)
}

fun String?.wrapWith(prefix: Char, suffix: Char): String {
    return prefixWith(prefix, false).suffixWith(suffix, false)
}

fun String?.wrapWith(prefix: Char, suffix: Char, ignoreCase: Boolean): String {
    return prefixWith(prefix, ignoreCase).suffixWith(suffix, ignoreCase)
}

fun String?.wrapWith(prefixSuffix: String): String {
    return wrapWith(prefixSuffix, prefixSuffix)
}

fun String?.wrapWith(prefixSuffix: String, ignoreCase: Boolean): String {
    return wrapWith(prefixSuffix, prefixSuffix, ignoreCase)
}

fun String?.wrapWith(prefix: String, suffix: String): String {
    return prefixWith(prefix, false).suffixWith(suffix, false)
}

fun String?.wrapWith(prefix: String, suffix: String, ignoreCase: Boolean): String {
    return prefixWith(prefix, ignoreCase).suffixWith(suffix, ignoreCase)
}

fun String?.suffixWith(suffix: Char): String {
    return suffixWith(suffix, false)
}

fun String?.suffixWith(suffix: Char, ignoreCase: Boolean): String {
    if (this != null && !isEmpty() && !endsWith(suffix, ignoreCase)) return plus(suffix)
    return orEmpty()
}

fun String?.suffixWith(suffix: String): String {
    return suffixWith(suffix, false)
}

fun String?.suffixWith(suffix: String, ignoreCase: Boolean): String {
    if (this != null && !isEmpty() && suffix.isNotEmpty() && !endsWith(suffix, ignoreCase)) return plus(suffix)
    return orEmpty()
}

fun String?.prefixWith(prefix: Char): String {
    return prefixWith(prefix, false)
}

fun String?.prefixWith(prefix: Char, ignoreCase: Boolean): String {
    if (this != null && !isEmpty() && !startsWith(prefix, ignoreCase)) return prefix.plus(this.orEmpty())
    return orEmpty()
}

fun String?.prefixWith(prefix: String): String {
    return prefixWith(prefix, false)
}

fun String?.prefixWith(prefix: String, ignoreCase: Boolean): String {
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

fun String?.urlDecode(charSet: String? = null): String {
    try {
        return URLDecoder.decode(this, charSet ?: "UTF-8")
    } catch (e: UnsupportedEncodingException) {
        //e.printStackTrace()
        return orEmpty()
    }
}

fun String?.urlEncode(charSet: String? = null): String {
    try {
        return URLEncoder.encode(this, charSet ?: "UTF-8")
    } catch (e: UnsupportedEncodingException) {
        //e.printStackTrace()
        return orEmpty()
    }
}

fun String?.ifEmpty(arg: String): String {
    if (this != null && !this.isEmpty()) return this
    return arg
}

fun String?.ifEmpty(ifEmptyArg: String, ifNotEmptyArg: String): String {
    return if (this == null || this.isEmpty()) ifEmptyArg else ifNotEmptyArg
}

fun String?.ifEmptyNulls(ifEmptyArg: String?, ifNotEmptyArg: String?): String? {
    return if (this == null || this.isEmpty()) ifEmptyArg else ifNotEmptyArg
}

fun String?.ifEmpty(arg: () -> String): String {
    if (this != null && !this.isEmpty()) return this
    return arg()
}

fun String?.ifEmpty(ifEmptyArg: () -> String?, ifNotEmptyArg: () -> String?): String? {
    return if (this == null || this.isEmpty()) ifEmptyArg() else ifNotEmptyArg()
}

fun String?.removeStart(prefix: Char): String {
    if (this != null) {
        return removePrefix(prefix.toString())
    }
    return ""
}

fun String?.removeStart(prefix: String): String {
    if (this != null) {
        return removePrefix(prefix)
    }
    return ""
}

fun String?.removeEnd(prefix: Char): String {
    if (this != null) {
        return removeSuffix(prefix.toString())
    }
    return ""
}

fun String?.removeEnd(prefix: String): String {
    if (this != null) {
        return removeSuffix(prefix)
    }
    return ""
}

fun String?.regexGroup(): String {
    return "(?:" + this.orEmpty() + ")"
}

fun splicer(delimiter: String): (accum: String, elem: String) -> String {
    return { accum, elem -> accum + delimiter + elem }
}

fun skipEmptySplicer(delimiter: String): (accum: String, elem: String) -> String {
    return { accum, elem -> if (elem.isEmpty()) accum else accum + delimiter + elem }
}


fun StringBuilder.regionMatches(thisOffset:Int, other:String, otherOffset:Int, length:Int, ignoreCase: Boolean = false) : Boolean {
    for (i in 0..length-1) {
        if (!this.get(i+thisOffset).equals(other.get(i+otherOffset), ignoreCase)) return false
    }
    return true
}

fun StringBuilder.endsWith(suffix: String, ignoreCase: Boolean = false): Boolean {
    return this.length >= suffix.length && this.regionMatches(this.length - suffix.length, suffix, 0, suffix.length, ignoreCase)
}

fun StringBuilder.startsWith(prefix: String, ignoreCase: Boolean = false): Boolean {
    return this.length >= prefix.length && this.regionMatches(0, prefix, 0, prefix.length, ignoreCase)
}

fun Array<String>.splice(delimiter: String): String {
    val result = StringBuilder(this.size * (delimiter.length + 10))
    var first = true;
    for (elem in this) {
        if (!elem.isEmpty()) {
            if (!first && !elem.startsWith(delimiter) && !result.endsWith(delimiter)) result.append(delimiter)
            else first = false
            result.append(elem.orEmpty())
        }
    }

    return result.toString()
}

fun List<String?>.splice(delimiter: String, skipNullOrEmpty: Boolean = true): String {
    val result = StringBuilder(this.size * (delimiter.length + 10))
    var first = true;
    for (elem in this) {
        if (elem != null && !elem.isEmpty() || !skipNullOrEmpty) {
            if (!first && (!skipNullOrEmpty || !elem.startsWith(delimiter) && !result.endsWith(delimiter))) result.append(delimiter)
            else first = false
            result.append(elem.orEmpty())
        }
    }

    return result.toString()
}

fun Collection<String?>.splice(delimiter: String, skipNullOrEmpty: Boolean = true): String {
    val result = StringBuilder(this.size * (delimiter.length + 10))
    var first = true;
    for (elem in this) {
        if (elem != null && !elem.isEmpty() || !skipNullOrEmpty) {
            if (!first && (!skipNullOrEmpty || !elem.startsWith(delimiter) && !result.endsWith(delimiter))) result.append(delimiter)
            else first = false
            result.append(elem.orEmpty())
        }
    }

    return result.toString()
}

fun Iterator<String>.splice(delimiter: String, skipEmpty: Boolean = true): String {
    val result = StringBuilder(10 * (delimiter.length + 10))
    var first = true;
    for (elem in this) {
        if (!elem.isEmpty() || !skipEmpty) {
            if (!first && (!skipEmpty || !elem.startsWith(delimiter) && !result.endsWith(delimiter))) result.append(delimiter)
            else first = false
            result.append(elem.orEmpty())
        }
    }

    return result.toString()
}

fun String?.appendDelim(delimiter: String, vararg args: String): String {
    return arrayListOf<String?>(this.orEmpty(), *args).splice(delimiter, true)
}


fun <T:Any> Any?.ifNotNull(eval: () -> T?):T? = if (this == null) null else eval()
