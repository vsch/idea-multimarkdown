// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language.folding;

import com.intellij.ui.components.JBCheckBox;
import com.vladsch.md.nav.flex.PluginBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class FlexSectionFolding {
    private JLabel myLabel;
    private JPanel myMainPanel;
    private JBCheckBox mySection1;
    private JBCheckBox mySection2;
    private JBCheckBox mySection3;
    private JBCheckBox mySection4;
    private JBCheckBox mySection5;
    private JBCheckBox mySection6;

    JBCheckBox[] myCheckBoxes;

    public FlexSectionFolding() {
        myCheckBoxes = new JBCheckBox[] {
                mySection1,
                mySection2,
                mySection3,
                mySection4,
                mySection5,
                mySection6,
        };

        String[] labels = new String[] {
                PluginBundle.message("code-folding.flexmark-example-source"),
                PluginBundle.message("code-folding.flexmark-example-html"),
                PluginBundle.message("code-folding.flexmark-example-ast"),
                null,
                null,
                null,
        };

        int i = 0;
        for (JBCheckBox checkBox : myCheckBoxes) {
            String label = labels[i];
            if (label != null) checkBox.setText(label);
            else checkBox.setVisible(false);
            i++;
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
