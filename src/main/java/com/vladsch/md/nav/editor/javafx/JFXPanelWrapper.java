// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.javafx;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.awt.Dimension;

public class JFXPanelWrapper extends JFXPanel {
    public JFXPanelWrapper() {
        Platform.setImplicitExit(false);
    }

    /**
     * This override fixes the situation of using multiple JFXPanels with jbtabs/splitters when some of them are not showing. On getMinimumSize there is no layout manager nor peer so the result could be #size() which is incorrect.
     *
     * @return zero size
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }
}
