/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.settings;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.lang.Language;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class CssOptionsPanel extends SettingsPanelImpl {
    private EditorTextField textCustomCss;
    private JButton btnLoadDefault;
    private JButton clearCustomCssButton;
    private JButton focusEditorButton;
    private JCheckBox includesColorsCheckBox;
    private JCheckBox includesHljsCssCheckBox;
    private JCheckBox includesLayoutCssCheckBox;
    private JCheckBox useCustomCssCheckBox;
    private JCheckBox useHighlightJsCheckBox;
    private JComponent htmlThemeComboBox;
    private JList htmlThemeList;
    private JPanel customCssPanel;
    private JPanel mainPanel;

    public JComponent getComponent() {
        return mainPanel;
    }

    // need this so that we don't try to access components before they are created
    public
    @Nullable
    Object getComponent(@NotNull String persistName) {
        //if (persistName.equals("htmlThemeComboBox")) return htmlThemeComboBox;
        if (persistName.equals("btnLoadDefault")) return btnLoadDefault;
        if (persistName.equals("clearCustomCssButton")) return clearCustomCssButton;
        if (persistName.equals("htmlThemeList")) return htmlThemeList;
        if (persistName.equals("includesHljsCssCheckBox")) return includesHljsCssCheckBox;
        if (persistName.equals("includesLayoutCssCheckBox")) return includesLayoutCssCheckBox;
        if (persistName.equals("textCustomCss")) return textCustomCss;
        if (persistName.equals("useCustomCssCheckBox")) return useCustomCssCheckBox;
        if (persistName.equals("useHighlightJsCheckBox")) return useHighlightJsCheckBox;
        if (persistName.equals(HAVE_CUSTOM_CSS_BOOLEAN)) return textCustomCss.getText().trim().length() > 0;
        if (persistName.equals(HAVE_CUSTOMIZABLE_EDITOR)) return haveCustomizableEditor ? textCustomCss : null;
        return null;
    }

    protected boolean haveCustomizableEditor;
    final protected MultiMarkdownGlobalSettings settings;

    @Override
    public void updateCustomCssControls(boolean haveCustomCss) {
        useCustomCssCheckBox.setEnabled(haveCustomCss);
        clearCustomCssButton.setEnabled(haveCustomCss);
        if (!haveCustomCss) useCustomCssCheckBox.setSelected(false);
        includesHljsCssCheckBox.setEnabled(useHighlightJsCheckBox.isSelected() && useHighlightJsCheckBox.isEnabled());
        if (haveCustomCss && haveCustomizableEditor)
            focusEditorButton.setEnabled(((CustomizableEditorTextField) textCustomCss).haveSavedState());
    }

    @Override
    public void updateUseOldPreviewControls(boolean useNewPreview) {
        useHighlightJsCheckBox.setEnabled(useNewPreview);
        includesLayoutCssCheckBox.setEnabled(useNewPreview);
        includesColorsCheckBox.setEnabled(useNewPreview);

        btnLoadDefault.setEnabled(!useNewPreview || includesColorsCheckBox.isSelected() || includesHljsCssCheckBox.isSelected() || includesLayoutCssCheckBox.isSelected());

        notifyUpdateCustomCssControls();
    }

    public CssOptionsPanel() {
        settings = MultiMarkdownGlobalSettings.getInstance();

        clearCustomCssButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textCustomCss.setText("");
            }
        });

        btnLoadDefault.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textCustomCss.setText(isUseOldPreview() ? settings.getCssFileText(htmlThemeList.getSelectedIndex(), false)
                        : (includesColorsCheckBox.isSelected() ? settings.getCssFileText(htmlThemeList.getSelectedIndex(), true) : "") +
                        (includesLayoutCssCheckBox.isSelected() ? settings.getLayoutCssFileText() : "") +
                        (includesHljsCssCheckBox.isSelected() && useHighlightJsCheckBox.isSelected() ? settings.getHljsCssFileText(htmlThemeList.getSelectedIndex(), true) : "")
                );
            }
        });

        focusEditorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textCustomCss.requestFocus();
            }
        });

        ItemListener itemListener1 = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                notifyUpdateCustomCssControls();
            }
        };

        useCustomCssCheckBox.addItemListener(itemListener1);
        useHighlightJsCheckBox.addItemListener(itemListener1);

        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                notifyUpdateUseOldPreviewControls(isUseOldPreview());
            }
        };

        includesColorsCheckBox.addItemListener(itemListener);
        includesHljsCssCheckBox.addItemListener(itemListener);
        includesLayoutCssCheckBox.addItemListener(itemListener);
        textCustomCss.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                super.documentChanged(e);
                notifyUpdateCustomCssControls();
            }
        });
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

        CustomizableEditorTextField.EditorCustomizationListener listener = new CustomizableEditorTextField.EditorCustomizationListener() {
            @Override
            public boolean editorCreated(@NotNull EditorEx editor, @NotNull Project project) {
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
