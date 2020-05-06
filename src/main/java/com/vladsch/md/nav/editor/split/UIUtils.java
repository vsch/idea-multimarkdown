// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.split;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.NotNullProducer;
import org.jetbrains.annotations.NotNull;

import javax.swing.UIManager;
import java.awt.Color;

public class UIUtils {
    @SuppressWarnings({ "HardCodedStringLiteral" })
    public static boolean isUnderIntelliJLaF() {
        return UIManager.getLookAndFeel().getName().contains("IntelliJ");
    }

    public static final Color CONTRAST_BORDER_COLOR = new JBColor(new NotNullProducer<Color>() {
        final Color color = new JBColor(0x9b9b9b, 0x282828);

        @NotNull
        @Override
        public Color produce() {
            if (SystemInfo.isMac && isUnderIntelliJLaF()) {
                return Gray.xC9;
            }
            return color;
        }
    });
}
