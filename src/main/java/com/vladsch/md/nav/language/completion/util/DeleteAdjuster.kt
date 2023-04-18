// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

import com.intellij.codeInsight.completion.InsertionContext

interface DeleteAdjuster {
    fun getDeletePrefixDelta(charSequence: CharSequence, headOffset: Int, context: InsertionContext): Int
    fun getDeleteSuffixDelta(charSequence: CharSequence, tailOffset: Int, context: InsertionContext): Int
}
