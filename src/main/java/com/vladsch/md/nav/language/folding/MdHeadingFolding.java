// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.folding;

import com.intellij.ui.components.JBCheckBox;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MdHeadingFolding {
    private JLabel myLabel;
    private JPanel myMainPanel;
    private JBCheckBox myHeading1;
    private JBCheckBox myHeading2;
    private JBCheckBox myHeading3;
    private JBCheckBox myHeading4;
    private JBCheckBox myHeading5;
    private JBCheckBox myHeading6;

    JBCheckBox[] myCheckBoxes;

    public MdHeadingFolding() {
        myCheckBoxes = new JBCheckBox[] {
                myHeading1,
                myHeading2,
                myHeading3,
                myHeading4,
                myHeading5,
                myHeading6,
        };

        int i = 1;
        for (JBCheckBox checkBox : myCheckBoxes) {
            checkBox.setText(String.valueOf(i++));
        }
    }

    public int getValue() {
        int iMax = myCheckBoxes.length;
        int value = 0;
        for (int i = 0; i < iMax; i++) {
            if (myCheckBoxes[i].isSelected()) value |= 1 << i;
        }
        return value;
    }

    public void setValue(int value) {
        int iMax = myCheckBoxes.length;
        for (int i = 0; i < iMax; i++) {
            myCheckBoxes[i].setSelected((value & (1 << i)) != 0);
        }
    }

    public JPanel getMainPanel() {
        return myMainPanel;
    }
}
