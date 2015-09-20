
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
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.lang.Language;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.util.registry.RegistryValue;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBList;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.editor.MultiMarkdownPreviewEditor;
import org.apache.commons.codec.Charsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class MultiMarkdownSettingsPanel implements SettingsProvider {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownSettingsPanel.class);

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
    //public JTextArea textCustomCss;
    public JPanel customCssPanel;
    public JButton clearCustomCssButton;
    public JButton btnLoadDefault;
    public JCheckBox taskListsCheckBox;
    public JCheckBox headerSpaceCheckBox;
    public JCheckBox showHtmlTextCheckBox;
    public JCheckBox showHtmlTextAsModifiedCheckBox;
    public JCheckBox anchorLinksCheckBox;
    public JCheckBox forceListParaCheckBox;
    public JCheckBox relaxedHRulesCheckBox;
    public JComponent htmlThemeComboBox;
    public JCheckBox enableTrimSpacesCheckBox;
    private EditorTextField textCustomCss;

    public JPanel panel;
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
    private JButton focusEditorButton;
    private JCheckBox useCustomCssCheckBox;
    private JCheckBox useOldPreviewCheckBox;
    private JLabel maxImgWidthLabel;
    private JCheckBox enableFirebugCheckBox;
    private JList htmlThemeList;
    private JEditorPane tippingJarEditorPane;
    private JSpinner pageZoomSpinner;
    private JLabel pageZoomLabel;
    private JTabbedPane tabbedPane;
    private JEditorPane noticesEditorPane;
    private JLabel enableFirebugLabel;
    private JCheckBox useHighlightJsCheckBox;

    // need this so that we dont try to access components before they are created
    public @Nullable Object getComponent(@NotNull String persistName) {
        if (persistName.equals("parsingTimeoutSpinner")) return parsingTimeoutSpinner;
        if (persistName.equals("smartsCheckBox")) return smartsCheckBox;
        if (persistName.equals("quotesCheckBox")) return quotesCheckBox;
        if (persistName.equals("abbreviationsCheckBox")) return abbreviationsCheckBox;
        if (persistName.equals("hardWrapsCheckBox")) return hardWrapsCheckBox;
        if (persistName.equals("autoLinksCheckBox")) return autoLinksCheckBox;
        if (persistName.equals("wikiLinksCheckBox")) return wikiLinksCheckBox;
        if (persistName.equals("tablesCheckBox")) return tablesCheckBox;
        if (persistName.equals("definitionsCheckBox")) return definitionsCheckBox;
        if (persistName.equals("fencedCodeBlocksCheckBox")) return fencedCodeBlocksCheckBox;
        if (persistName.equals("suppressHTMLBlocksCheckBox")) return suppressHTMLBlocksCheckBox;
        if (persistName.equals("suppressInlineHTMLCheckBox")) return suppressInlineHTMLCheckBox;
        if (persistName.equals("strikethroughCheckBox")) return strikethroughCheckBox;
        if (persistName.equals("updateDelaySpinner")) return updateDelaySpinner;
        if (persistName.equals("maxImgWidthSpinner")) return maxImgWidthSpinner;
        if (persistName.equals("textCustomCss")) return textCustomCss;
        if (persistName.equals("clearCustomCssButton")) return clearCustomCssButton;
        if (persistName.equals("btnLoadDefault")) return btnLoadDefault;
        if (persistName.equals("taskListsCheckBox")) return taskListsCheckBox;
        if (persistName.equals("headerSpaceCheckBox")) return headerSpaceCheckBox;
        if (persistName.equals("showHtmlTextCheckBox")) return showHtmlTextCheckBox;
        if (persistName.equals("showHtmlTextAsModifiedCheckBox")) return showHtmlTextAsModifiedCheckBox;
        if (persistName.equals("anchorLinksCheckBox")) return anchorLinksCheckBox;
        if (persistName.equals("forceListParaCheckBox")) return forceListParaCheckBox;
        if (persistName.equals("relaxedHRulesCheckBox")) return relaxedHRulesCheckBox;
        //if (persistName.equals("htmlThemeComboBox")) return htmlThemeComboBox;
        if (persistName.equals("enableTrimSpacesCheckBox")) return enableTrimSpacesCheckBox;
        if (persistName.equals("useCustomCssCheckBox")) return useCustomCssCheckBox;
        if (persistName.equals("useOldPreviewCheckBox")) return useOldPreviewCheckBox;
        if (persistName.equals("enableFirebugCheckBox")) return enableFirebugCheckBox;
        if (persistName.equals("htmlThemeList")) return htmlThemeList;
        if (persistName.equals("pageZoomSpinner")) return pageZoomSpinner;
        if (persistName.equals("tabbedPane")) return tabbedPane;
        if (persistName.equals("useHighlightJsCheckBox")) return useHighlightJsCheckBox;

        return null;
    }

    protected void showHtmlTextStateChanged() {
        if (showHtmlTextAsModifiedCheckBox != null) {
            showHtmlTextAsModifiedCheckBox.setEnabled(showHtmlTextCheckBox.isSelected());
        }
    }

    protected boolean useCustomCSSOriginalState;
    protected boolean haveCustomizableEditor;

    protected void updateCustomCssControls() {
        final Application application = ApplicationManager.getApplication();
        if (haveCustomizableEditor && !((CustomizableEditorTextField) textCustomCss).isPendingTextUpdate()) {
            updateRawCustomCssControls();
        } else {
            application.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateRawCustomCssControls();
                }
            }, application.getCurrentModalityState());
        }
    }

    private void updateRawCustomCssControls() {
        boolean haveCustomCss = textCustomCss.getText().trim().length() > 0;
        useCustomCssCheckBox.setEnabled(haveCustomCss);
        clearCustomCssButton.setEnabled(haveCustomCss);
        if (!haveCustomCss) useCustomCssCheckBox.setSelected(false);
        if (haveCustomCss && haveCustomizableEditor) focusEditorButton.setEnabled(((CustomizableEditorTextField) textCustomCss).haveSavedState());
    }

    private void updateUseOldPreviewControls() {
        boolean useNewPreview = !useOldPreviewCheckBox.isSelected();
        enableFirebugCheckBox.setEnabled(useNewPreview);
        enableFirebugLabel.setEnabled(useNewPreview);
        useHighlightJsCheckBox.setEnabled(useNewPreview);
        pageZoomSpinner.setEnabled(useNewPreview);
        pageZoomLabel.setEnabled(useNewPreview);
        maxImgWidthSpinner.setEnabled(!useNewPreview);
        maxImgWidthLabel.setEnabled(!useNewPreview);
    }

    public MultiMarkdownSettingsPanel() {
        clearCustomCssButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                textCustomCss.setText("");
            }
        });

        btnLoadDefault.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                //String cssFileText = MultiMarkdownGlobalSettings.getInstance().getCssFileText(htmlThemeComboBox.getSelectedIndex());
                //String base64Css = Base64.encodeBase64URLSafeString(MultiMarkdownGlobalSettings.getInstance().getCssText().getBytes(Charset.forName("utf-8")));
                //String cssText = new String(Base64.decodeBase64(base64Css), Charset.forName("utf-8"));
                MultiMarkdownGlobalSettings settings = MultiMarkdownGlobalSettings.getInstance();
                textCustomCss.setText(settings.getCssFileText(htmlThemeList.getSelectedIndex(), settings.isFxHtmlPreview()) +
                        (settings.isFxHtmlPreview() && useHighlightJsCheckBox.isSelected() ? settings.getHljsCssFileText(htmlThemeList.getSelectedIndex(), settings.isFxHtmlPreview()) : ""));
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

        focusEditorButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                textCustomCss.requestFocus();
            }
        });

        useCustomCssCheckBox.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent e) {
                updateCustomCssControls();
            }
        });

        textCustomCss.addDocumentListener(new DocumentAdapter() {
            @Override public void documentChanged(DocumentEvent e) {
                super.documentChanged(e);
                updateCustomCssControls();
            }
        });

        if (MultiMarkdownGlobalSettings.getInstance().fxPreviewFailedBuild.isFailedBuild()) {
            // set it and disable
            useOldPreviewCheckBox.setSelected(true);
            useOldPreviewCheckBox.setEnabled(false);
        }

        updateUseOldPreviewControls();
        useOldPreviewCheckBox.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent e) {
                updateUseOldPreviewControls();
            }
        });

        if (htmlThemeComboBox instanceof ComboBox) {
            ((JComboBox) htmlThemeComboBox).addItemListener(new ItemListener() {
                @Override public void itemStateChanged(ItemEvent e) {
                    if (((JComboBox) htmlThemeComboBox).getSelectedIndex() != htmlThemeList.getSelectedIndex()) {
                        htmlThemeList.setSelectedIndex(((JComboBox) htmlThemeComboBox).getSelectedIndex());
                    }
                }
            });

            htmlThemeList.addListSelectionListener(new ListSelectionListener() {
                @Override public void valueChanged(ListSelectionEvent e) {
                    if (((JComboBox) htmlThemeComboBox).getSelectedIndex() != htmlThemeList.getSelectedIndex()) {
                        ((JComboBox) htmlThemeComboBox).setSelectedIndex(htmlThemeList.getSelectedIndex());
                    }
                }
            });
        }

        tippingJarEditorPane.setText("" +
                "<html>\n" +
                "  <head>\n" +
                "  <style>\n" +
                "     td { text-align: right; margin 0; padding 0 10px !important; }\n" +
                "     td.pic { width: 0; }\n" +
                "     p, table, tr, body, div { margin: 0 !important; padding: 0 !important; }\n" +
                "     table { /*border: 1px solid black;*/ width: 100%; float: right !important; }\n" +
                "  </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "      <table>\n" +
                "        <tr>\n" +
                "          <td><b>If you like my work then please feel free to tip me.<br>I will view it as a show of appreciation and as a reward for my effort.</b></td>\n" +
                "          <td class=\"pic\"><a href=\"http://flattr.com/thing/4603764/vschidea-multimarkdown-on-GitHub\" title=\"Donate monthly to vsch using Flattr\"><img src=\"https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/flattr-tips.png\" border=\"0\" width=\"43\" height=\"53\" alt=\"Donate monthly to vsch using Flattr\" /></a></td>\n" +
                "          <td class=\"pic\"><a href=\"https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=NR7DAGTC8CXLU\" title=\"Donate once-off to vsch using Paypal\"><img src=\"https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/paypal-tips.png\" border=\"0\" width=\"43\" height=\"53\" alt=\"Donate once-off to vsch using Paypal\" /></a></td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "  </body>\n" +
                "</html>\n" +
                "");

        String htmlText = "";
        try {
            htmlText = Resources.toString(getClass().getResource("/com/vladsch/idea/multimarkdown/NOTICE.html"), Charsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        MultiMarkdownPreviewEditor.setStyleSheet(noticesEditorPane);
        noticesEditorPane.setText(htmlText);

        HyperlinkListener listener = new HyperlinkListener() {
            @Override public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    URL href = e.getURL();
                    if (href != null) {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(href.toURI());
                            } catch (URISyntaxException ex) {
                                // invalid URI, just log
                                logger.error("URISyntaxException on '" + href.toString() + "'" + ex.toString());
                            } catch (IOException ex) {
                                logger.error("IOException on '" + href.toString() + "'" + ex.toString());
                            }
                        }
                    }
                }
            }
        };

        tippingJarEditorPane.addHyperlinkListener(listener);
        noticesEditorPane.addHyperlinkListener(listener);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        // create the css themes combobox, make it locale aware
        ArrayList<String> options = new ArrayList<String>(10);
        for (int i = 0; ; i++) {
            String message = MultiMarkdownBundle.messageOrBlank("settings.html-theme-" + (i + 1));
            if (message.isEmpty()) break;
            options.add(message);
        }

        // we use the list to report but combo box if available
        htmlThemeList = new JBList(options);
        htmlThemeList.setSelectedIndex(2);

        htmlThemeComboBox = new JLabel();
        htmlThemeComboBox.setVisible(false);
        haveCustomizableEditor = false;
        try {
            htmlThemeComboBox = new ComboBox(options.toArray(new String[options.size()]));
            ((JComboBox) htmlThemeComboBox).setSelectedIndex(2);
            htmlThemeList.setVisible(false);
            haveCustomizableEditor = true;
        } catch (NoSuchMethodError e) {
            // does not exist, use list box
        } catch (NoClassDefFoundError e) {
            // does not exist, use list box
        }

        // create the CSS text edit control
        Language language = Language.findLanguageByID("CSS");
        final boolean foundCSS = language != null;

        final FileType fileType = language != null && language.getAssociatedFileType() != null ? language.getAssociatedFileType() : StdFileTypes.PLAIN_TEXT;

        // Set zoom to 0.1 increments
        final SpinnerNumberModel model = new SpinnerNumberModel(1.0, 0.2, 5.0, 0.01);
        pageZoomSpinner = new JSpinner(model);
        JSpinner.NumberEditor decimalFormat = new JSpinner.NumberEditor(pageZoomSpinner, "0.00");

        CustomizableEditorTextField.EditorCustomizationListener listener = new CustomizableEditorTextField.EditorCustomizationListener() {
            @Override public boolean editorCreated(@NotNull EditorEx editor, @NotNull Project project) {
                EditorSettings settings = editor.getSettings();
                settings.setRightMarginShown(true);
                //settings.setRightMargin(-1);
                if (foundCSS) settings.setFoldingOutlineShown(true);
                settings.setLineNumbersShown(true);
                if (foundCSS) settings.setLineMarkerAreaShown(true);
                settings.setIndentGuidesShown(true);
                settings.setVirtualSpace(true);

                //settings.setWheelFontChangeEnabled(false);
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);

                FileType fileTypeH = FileTypeManagerEx.getInstanceEx().getFileTypeByExtension(".css");
                FileType highlighterFileType = foundCSS ? fileType : fileTypeH;
                if (highlighterFileType != null && project != null) {
                    editor.setHighlighter(HighlighterFactory.createHighlighter(project, highlighterFileType));
                }

                int lineCursorWidth = 2;
                if (haveCustomizableEditor) {
                    // get the standard caret width from the registry
                    try {
                        RegistryValue value = Registry.get("editor.caret.width");
                        if (value != null) {
                            lineCursorWidth = value.asInteger();
                        }
                    } catch (Exception ex) {
                        // ignore
                    }

                    focusEditorButton.setEnabled(((CustomizableEditorTextField) textCustomCss).haveSavedState(editor));
                }
                settings.setLineCursorWidth(lineCursorWidth);

                return false;
            }
        };

        if (!haveCustomizableEditor) {
            Project project = CustomizableEditorTextField.getAnyProject(null, true);
            Document document = CustomizableEditorTextField.createDocument("", fileType, project, new CustomizableEditorTextField.SimpleDocumentCreator());
            textCustomCss = new CustomizableLanguageEditorTextField(document, project, fileType, false, false);
            textCustomCss.setFontInheritedFromLAF(false);
            ((CustomizableLanguageEditorTextField) textCustomCss).registerListener(listener);
            //focusEditorButton.setEnabled(false);
        } else {
            // we pass a null project because we don't have one, the control will grab any project so that
            // undo works properly in the edit control.
            Project project = CustomizableEditorTextField.getAnyProject(null, true);
            textCustomCss = new CustomizableEditorTextField(fileType, project, "", false);
            textCustomCss.setFontInheritedFromLAF(false);
            ((CustomizableEditorTextField) textCustomCss).registerListener(listener);
        }
    }
}
