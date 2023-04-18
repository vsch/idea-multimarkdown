// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.LayeredIcon
import com.vladsch.plugin.util.AppUtils
import com.vladsch.plugin.util.ifElse
import com.vladsch.plugin.util.ui.TextIcon
import javax.swing.Icon
import javax.swing.JComponent

open class TaskItemStats {
    val own: TaskItemCounts
    val all: TaskItemCounts

    constructor(other: TaskItemStats) : this(other.own, other.all) {}
    constructor(own: TaskItemCounts, all: TaskItemCounts) {
        this.own = TaskItemCounts(own)
        this.all = TaskItemCounts(all)
    }

    constructor() {
        own = TaskItemCounts()
        all = TaskItemCounts()
    }

    fun isEmpty(): Boolean {
        return all.incompleteItems + all.completeItems + all.emptyItems == 0
    }

    fun nullIfEmpty(): TaskItemStats? {
        return if (isEmpty()) null else this
    }

    fun getIcon(project: Project): Icon? {
        return getType()?.let {
            createIconWithTaskCount(WindowManager.getInstance().getStatusBar(project).component, it.first, it.second)
        }
    }

    fun getType(): Pair<TaskBadgeType, Int>? {
        // need badge for number of undone tasks
        if (!isEmpty()) {
            val total: Int
            val type = when {
                own.incompleteItems > 0 -> {
                    total = all.incompleteItems
                    TaskBadgeType.UNDONE_TASKS
                }
                all.incompleteItems > 0 -> {
                    total = all.incompleteItems
                    TaskBadgeType.UNDONE_DESCENDANTS
                }
                own.completeItems > 0 -> {
                    total = all.completeItems
                    TaskBadgeType.DONE_TASKS
                }
                all.completeItems > 0 -> {
                    total = all.completeItems
                    TaskBadgeType.DONE_DESCENDANTS
                }
                own.emptyItems > 0 -> {
                    total = all.emptyItems
                    TaskBadgeType.EMPTY_TASKS
                }
                all.emptyItems > 0 -> {
                    total = all.emptyItems
                    TaskBadgeType.EMPTY_DESCENDANTS
                }
                else -> {
                    total = 0
                    TaskBadgeType.NONE
                }
            }

            return Pair(type, total)
        }
        return null
    }

    companion object {
        val NULL: TaskItemStats by lazy { TaskItemStats() }

        fun createIconWithTaskCount(component: JComponent, taskBadgeType: TaskBadgeType, size: Int): LayeredIcon {
            val icon = LayeredIcon(2)
            icon.setIcon(taskBadgeType.icon, 0)
            if (size > 0) {
                val newOffsetIcons = AppUtils.isAppVersionEqualOrGreaterThan("183", true) && !AppUtils.isAppVersionEqualOrGreaterThan("192", true)
                val vShift = (size < 100).ifElse(newOffsetIcons.ifElse(1, 0), 0)
                val hBase = newOffsetIcons.ifElse(1, -1)
                val hShift = (size < 100).ifElse(hBase, hBase)
                icon.setIcon(TextIcon(component, if (size < 100) size.toString() else "∞", taskBadgeType.textColor), 1, hShift, vShift)
            }
            return icon
        }
    }
}
