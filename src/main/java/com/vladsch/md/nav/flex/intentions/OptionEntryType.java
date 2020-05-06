// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.intentions;

import java.awt.Color;

public class OptionEntryType {
    public final OptionType optionType;
    public final boolean isSelected;
    public final Color color;
    public final String toolTip;

    public OptionEntryType(OptionType optionType, boolean isSelected, Color color, String toolTip) {
        this.optionType = optionType;
        this.isSelected = isSelected;
        this.color = color;
        this.toolTip = toolTip;
    }
}
