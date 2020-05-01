// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util;

import com.intellij.util.ui.JBUI;

import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import java.awt.Insets;

/**
 * @author Konstantin Bulenkov
 */
public class JBEmptyBorder extends EmptyBorder {
    public JBEmptyBorder(int top, int left, int bottom, int right) {
        super(JBUI.insets(top, left, bottom, right));
    }

    public JBEmptyBorder(Insets insets) {
        super(JBUI.insets(insets));
    }

    public JBEmptyBorder(int offset) {
        this(offset, offset, offset, offset);
    }

    public JBEmptyBorderUIResource asUIResource() {
        return new JBEmptyBorderUIResource(this);
    }

    public static class JBEmptyBorderUIResource extends JBEmptyBorder implements UIResource {
        public JBEmptyBorderUIResource(JBEmptyBorder border) {
            super(0, 0, 0, 0);
            top = border.top;
            left = border.left;
            bottom = border.bottom;
            right = border.right;
        }
    }
}
