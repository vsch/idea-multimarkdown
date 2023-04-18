// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MdExtensionSpacer {
    JLabel myLabel;
    private JPanel myMainPanel;
    JComponent myComponent;

    public MdExtensionSpacer(@NotNull String name, @Nullable JComponent component) {
        myComponent = component;

        myLabel.setText(name);

        if (myComponent != null) {
            MouseListener listener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    boolean open = myComponent.isVisible();
                    myComponent.setVisible(!open);
                    myLabel.setIcon(open ? MdIcons.Misc.COLLAPSIBLE_CLOSED : MdIcons.Misc.COLLAPSIBLE_OPEN);
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            };

            myLabel.addMouseListener(listener);
            myLabel.setIcon(MdIcons.Misc.COLLAPSIBLE_OPEN);
        } else {
            myLabel.setIcon(null);
        }
    }

    public JPanel getMainPanel() {
        return myMainPanel;
    }

    public void addContent(@NotNull JComponent component) {
        myMainPanel.add(component, BorderLayout.CENTER);
    }
}
