// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.intentions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.flexmark.util.collection.OrderedSet;
import com.vladsch.md.nav.flex.PluginBundle;
import com.vladsch.plugin.util.HelpersKt;
import com.vladsch.plugin.util.ui.WrapLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExampleOptionsRefactoringDialog extends DialogWrapper {
    JPanel contentPane;
    private JPanel myOptionsPanel;
    private JTextPane myOptionsTextPane;
    private JTextArea myOptionsTextArea;
    private JButton mySelectNoneButton;
    private JButton mySelectAllButton;
    LinkedHashMap<JBCheckBox, String> myCheckBoxOptions = new LinkedHashMap<>();
    OrderedSet<String> myOptions = new OrderedSet<>();
    final Map<String, OptionEntryType> mySetOptions;
    final Font myCheckBoxFont;

    public ExampleOptionsRefactoringDialog(JComponent parent, final Map<String, OptionEntryType> options) {
        super(parent, false);
        mySetOptions = options;
        mySelectAllButton.setVisible(false);
        mySelectNoneButton.setVisible(false);

        init();
        setTitle(PluginBundle.message("refactoring.flexmark.example.edit-options.title"));
        setModal(true);

        ActionListener myUpdateListener = e -> ApplicationManager.getApplication().invokeLater(this::updateOptions);

        Font checkBoxFont = null;

        Set<Map.Entry<String, OptionEntryType>> entries = mySetOptions.entrySet();

        List<Map.Entry<String, OptionEntryType>> stringSorted = HelpersKt.stringSorted(entries, Map.Entry::getKey);

        myOptions.addAll(mySetOptions.keySet());

        for (Map.Entry<String, OptionEntryType> entry : stringSorted) {
            JBCheckBox checkBox = new JBCheckBox(entry.getKey(), entry.getValue().isSelected);
            myCheckBoxOptions.put(checkBox, entry.getKey());

            if (checkBoxFont == null) checkBoxFont = checkBox.getFont();

            if (entry.getValue().toolTip != null) {
                checkBox.setToolTipText(entry.getValue().toolTip);
            }

            checkBox.addActionListener(myUpdateListener);
            checkBox.setForeground(entry.getValue().color);
            myOptionsPanel.add(checkBox);
        }

        myCheckBoxFont = checkBoxFont;
        myOptionsTextPane.setFont(myOptionsTextArea.getFont());
        myOptionsTextArea.setVisible(false);
        updateOptions();

        // not used
        /*
        mySelectNoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                for (JBCheckBox checkBox : myCheckBoxOptions.keySet()) {
                    checkBox.setSelected(false);
                }
                myOptions.clear();
                updateOptions();
            }
        });

        mySelectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                for (JBCheckBox checkBox : myCheckBoxOptions.keySet()) {
                    checkBox.setSelected(true);
                    myOptions.add(myCheckBoxOptions.get(checkBox));
                }
                updateOptions();
            }
        });
        //*/
    }

    String rgbColor(Color color) {
        return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }

    protected void updateOptions() {
        for (JBCheckBox checkBox : myCheckBoxOptions.keySet()) {
            if (checkBox.isSelected()) {
                myOptions.add(myCheckBoxOptions.get(checkBox));
            } else {
                myOptions.remove(myCheckBoxOptions.get(checkBox));
            }

            boolean bold = mySetOptions.get(myCheckBoxOptions.get(checkBox)).isSelected != checkBox.isSelected();
            checkBox.setFont(new Font(myCheckBoxFont.getFontName(), bold ? Font.BOLD : Font.PLAIN, myCheckBoxFont.getSize()));
        }

        String optionsText = "&nbsp;";
        if (myOptions.size() > 0) {
            int i = 0;
            StringBuilder out = new StringBuilder();
            Font font = myOptionsTextArea.getFont();
            out.append("<span style='font-family:").append(font.getFontName()).append(";").append("font-size:").append(font.getSize()).append("pt;'>");
            out.append("options(");
            for (String option : myOptions) {
                if (option.trim().isEmpty()) continue;
                if (i > 0) out.append(", ");

                boolean bold = !mySetOptions.get(option).isSelected;
                if (bold) out.append("<strong>");
                out.append("<span style='color:").append(rgbColor(mySetOptions.get(option).color)).append(";'>");
                out.append(option).append("</span>");
                if (bold) out.append("</strong>");
                i++;
            }
            out.append(")</span>");
            if (i > 0) optionsText = out.toString();
        }
        myOptionsTextPane.setText(optionsText);
    }

    @NotNull
    public OrderedSet<String> getOptions() {
        return myOptions;
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "MarkdownNavigator.ExampleOptionsRefactoringDialog";
    }

    private void createUIComponents() {
        myOptionsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        myOptionsPanel.setVisible(true);
    }

    protected class MyOkAction extends OkAction {
        protected MyOkAction() {
            super();
            putValue(Action.NAME, PluginBundle.message("refactoring.edit.flexmark.example.options.ok.label"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (doValidate() == null) {
                getOKAction().setEnabled(true);
            }
            super.doAction(e);
        }
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        return new Action[] { new MyOkAction(), getCancelAction() };
    }

    protected class MyAction extends OkAction {
        final private Runnable runnable;

        protected MyAction(String name, Runnable runnable) {
            super();
            putValue(Action.NAME, name);
            this.runnable = runnable;
        }

        @Override
        protected void doAction(ActionEvent e) {
            runnable.run();
        }
    }

    //MyAction myOkAndSetAction = new MyAction(PluginBundle.message("debug.show-text-hex.ok-and-set.label"), new Runnable() {
    //    @Override
    //    public void run() {
    //        myText = myOptionsTextArea.getText();
    //        doOKAction();
    //    }
    //});

    @NotNull
    protected Action[] createLeftSideActions() {
        return new Action[] {
                new MyAction(PluginBundle.message("refactoring.edit.flexmark.example.options.select-none.label"), () -> {
                    for (JBCheckBox checkBox : myCheckBoxOptions.keySet()) {
                        checkBox.setSelected(false);
                    }
                    myOptions.clear();
                    updateOptions();
                }),
                new MyAction(PluginBundle.message("refactoring.edit.flexmark.example.options.select-all.label"), () -> {
                    for (JBCheckBox checkBox : myCheckBoxOptions.keySet()) {
                        checkBox.setSelected(true);
                        myOptions.add(myCheckBoxOptions.get(checkBox));
                    }
                    updateOptions();
                }),
        };
    }

    public static boolean showDialog(JComponent parent, final Map<String, OptionEntryType> options) {
        ExampleOptionsRefactoringDialog dialog = new ExampleOptionsRefactoringDialog(parent, options);
        return dialog.showAndGet();
    }

    protected ValidationInfo doValidate(boolean doActionIfValid) {
        return super.doValidate();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return doValidate(false);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myOptionsPanel;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
