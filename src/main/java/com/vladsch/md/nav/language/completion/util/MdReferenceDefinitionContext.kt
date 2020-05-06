// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

data class MdReferenceDefinitionContext(val id: String) {
    var isFirstInsert = true
    private var referenceIdMap: HashMap<String, String>? = null

    operator fun get(id: String): String {
        return referenceIdMap?.get(id) ?: id
    }

    operator fun set(id: String, mappedId: String) {
        if (id == mappedId) return

        var referenceIdMap = referenceIdMap
        if (referenceIdMap == null) {
            referenceIdMap = HashMap()
            this.referenceIdMap = referenceIdMap
        }
        referenceIdMap[id] = mappedId
    }
}
