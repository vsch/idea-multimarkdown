// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

import com.intellij.ui.JBColor
import com.intellij.util.ui.EmptyIcon
import icons.MdIcons
import java.awt.Color
import javax.swing.Icon

enum class TaskBadgeType(val icon: Icon, val textColor: Color) {
    NONE(EmptyIcon.ICON_16, JBColor.foreground()),
    UNDONE_TASKS(MdIcons.Structure.UNDONE_TASK_BADGE, MdIcons.Structure.UNDONE_TASK_BADGE_TEXT),
    UNDONE_DESCENDANTS(MdIcons.Structure.CONTAINS_UNDONE_TASK_BADGE, MdIcons.Structure.CONTAINS_UNDONE_TASK_BADGE_TEXT),
    DONE_TASKS(MdIcons.Structure.DONE_TASK_BADGE, MdIcons.Structure.DONE_TASK_BADGE_TEXT),
    DONE_DESCENDANTS(MdIcons.Structure.CONTAINS_DONE_TASK_BADGE, MdIcons.Structure.CONTAINS_DONE_TASK_BADGE_TEXT),
    EMPTY_TASKS(MdIcons.Structure.EMPTY_TASK_BADGE, MdIcons.Structure.EMPTY_TASK_BADGE_TEXT),
    EMPTY_DESCENDANTS(MdIcons.Structure.CONTAINS_EMPTY_TASK_BADGE, MdIcons.Structure.CONTAINS_EMPTY_TASK_BADGE_TEXT)
}
