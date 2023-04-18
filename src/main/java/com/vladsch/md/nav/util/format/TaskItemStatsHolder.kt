// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

class TaskItemStatsHolder {
    internal val rawStats: TaskItemStats = TaskItemStats()
    private var ownSection = true

    val stats: TaskItemStats
        get() {
            finalizeStats()
            return rawStats
        }

    fun finalizeStats() {
        if (ownSection) {
            rawStats.own.copy(rawStats.all)
            ownSection = false
        }
    }
}
