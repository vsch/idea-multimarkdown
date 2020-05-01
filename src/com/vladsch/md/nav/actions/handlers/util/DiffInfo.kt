// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util

data class DiffInfo(val isEqual: Boolean, val lastCharDiff: Boolean, val firstDiff: Int, val lastDiff: Int, val lastLineDiffOnly: Boolean)
