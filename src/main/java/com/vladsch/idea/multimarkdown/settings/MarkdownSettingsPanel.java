
/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.settings;

import com.google.common.io.Resources;
import com.vladsch.idea.multimarkdown.editor.MarkdownPreviewEditor;
import org.apache.commons.codec.Charsets;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class MarkdownSettingsPanel {

    public JSpinner parsingTimeoutSpinner;
    public JCheckBox smartsCheckBox;
    public JCheckBox quotesCheckBox;
    public JCheckBox abbreviationsCheckBox;
    public JCheckBox hardWrapsCheckBox;
    public JCheckBox autoLinksCheckBox;
    public JCheckBox wikiLinksCheckBox;
    public JCheckBox tablesCheckBox;
    public JCheckBox definitionsCheckBox;
    public JCheckBox fencedCodeBlocksCheckBox;
    public JCheckBox suppressHTMLBlocksCheckBox;
    public JCheckBox suppressInlineHTMLCheckBox;
    public JCheckBox strikethroughCheckBox;
    public JSpinner updateDelaySpinner;
    public JSpinner maxImgWidthSpinner;
    public JTextArea textCustomCss;
    public JPanel customCssPanel;
    public JButton btnResetCss;
    public JButton btnLoadDefault;
    public JCheckBox taskListsCheckBox;
    public JCheckBox headerSpaceCheckBox;
    public JCheckBox showHtmlTextCheckBox;
    public JCheckBox showHtmlTextAsModifiedCheckBox;
    public JCheckBox anchorLinksCheckBox;
    public JCheckBox forceListParaCheckBox;
    public JCheckBox relaxedHRulesCheckBox;
    public JComboBox htmlThemeComboBox;
    public JCheckBox enableTrimSpacesCheckBox;
    private JCheckBox todoCommentsCheckBox;

    public JPanel panel;

    // need this so that we dont try to access components before they are created
    public JComponent getComponent(String name) {
        if (name.equals("parsingTimeoutSpinner")) return parsingTimeoutSpinner;
        if (name.equals("smartsCheckBox")) return smartsCheckBox;
        if (name.equals("quotesCheckBox")) return quotesCheckBox;
        if (name.equals("abbreviationsCheckBox")) return abbreviationsCheckBox;
        if (name.equals("hardWrapsCheckBox")) return hardWrapsCheckBox;
        if (name.equals("autoLinksCheckBox")) return autoLinksCheckBox;
        if (name.equals("wikiLinksCheckBox")) return wikiLinksCheckBox;
        if (name.equals("tablesCheckBox")) return tablesCheckBox;
        if (name.equals("definitionsCheckBox")) return definitionsCheckBox;
        if (name.equals("fencedCodeBlocksCheckBox")) return fencedCodeBlocksCheckBox;
        if (name.equals("suppressHTMLBlocksCheckBox")) return suppressHTMLBlocksCheckBox;
        if (name.equals("suppressInlineHTMLCheckBox")) return suppressInlineHTMLCheckBox;
        if (name.equals("strikethroughCheckBox")) return strikethroughCheckBox;
        if (name.equals("updateDelaySpinner")) return updateDelaySpinner;
        if (name.equals("maxImgWidthSpinner")) return maxImgWidthSpinner;
        if (name.equals("textCustomCss")) return textCustomCss;
        if (name.equals("customCssPanel")) return customCssPanel;
        if (name.equals("btnResetCss")) return btnResetCss;
        if (name.equals("btnLoadDefault")) return btnLoadDefault;
        if (name.equals("taskListsCheckBox")) return taskListsCheckBox;
        if (name.equals("headerSpaceCheckBox")) return headerSpaceCheckBox;
        if (name.equals("showHtmlTextCheckBox")) return showHtmlTextCheckBox;
        if (name.equals("showHtmlTextAsModifiedCheckBox")) return showHtmlTextAsModifiedCheckBox;
        if (name.equals("anchorLinksCheckBox")) return anchorLinksCheckBox;
        if (name.equals("forceListParaCheckBox")) return forceListParaCheckBox;
        if (name.equals("relaxedHRulesCheckBox")) return relaxedHRulesCheckBox;
        if (name.equals("htmlThemeComboBox")) return htmlThemeComboBox;
        if (name.equals("enableTrimSpacesCheckBox")) return enableTrimSpacesCheckBox;
        //if (name.equals("todoCommentsCheckBox")) return todoCommentsCheckBox;
        return null;
    }

    public JPanel settingsPanel;
    public JPanel extensionsPanel;

    private JLabel suppressInlineHTMLDescriptionLabel;
    private JLabel suppressHTMLBlocksDescriptionLabel;
    private JLabel fencedCodeBlocksDescriptionLabel;
    private JLabel definitionsDescriptionLabel;
    private JLabel tablesDescriptionLabel;
    private JLabel autoLinksDescriptionLabel;
    private JLabel wikiLinksDescriptionLabel;
    private JLabel hardWarpsDescriptionLabel;
    private JLabel abbreviationsDescriptionLabel;
    private JLabel quotesDescriptionLabel;
    private JLabel smartsDescriptionLabel;
    private JLabel strikethroughDescriptionLabel;
    private JLabel parsingTimeoutDescriptionLabel;

    protected void showHtmlTextStateChanged() {
        if (showHtmlTextAsModifiedCheckBox != null) {
            boolean checked = showHtmlTextCheckBox.isSelected();
            showHtmlTextAsModifiedCheckBox.setEnabled(checked);
        }
    }

    public MarkdownSettingsPanel() {
        btnResetCss.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                textCustomCss.setText("");
            }
        });

        btnLoadDefault.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                try {
                    textCustomCss.setText(Resources.toString(MarkdownPreviewEditor.class.getResource(
                            htmlThemeComboBox.getSelectedIndex() == 0
                                    ? MarkdownPreviewEditor.PREVIEW_STYLESHEET_PATH0
                                    : MarkdownPreviewEditor.PREVIEW_STYLESHEET_PATH1), Charsets.UTF_8));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        showHtmlTextCheckBox.addPropertyChangeListener(new PropertyChangeListener() {
            @Override public void propertyChange(PropertyChangeEvent evt) {
                showHtmlTextStateChanged();
            }
        });

        showHtmlTextCheckBox.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                showHtmlTextStateChanged();
            }
        });
    }
}
