// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.util.ui.UIUtil;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.Helpers;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

public class ScriptsTable extends JTable {
    public ScriptsTable(ScriptsTableModel tableModel) {
        super(tableModel);
        //setRunModeColumnRenderer(this.getColumnModel().getColumn(2));

        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.setShowHorizontalLines(true);
        this.setShowVerticalLines(false);
        this.getTableHeader().getColumnModel().getColumn(ScriptsTableModel.SELECTED_COL).setPreferredWidth(100);
        this.getTableHeader().getColumnModel().getColumn(ScriptsTableModel.SELECTED_COL).setMaxWidth(100);
        this.getTableHeader().getColumnModel().getColumn(ScriptsTableModel.NAME_COL).setPreferredWidth(300);
        int height = (int) this.getTableHeader().getPreferredSize().getHeight() + 1;
        this.setRowHeight(height);
        Dimension dimension = new Dimension(300, (tableModel.getRowCount()) * height + 10);
        Dimension dimensionMin = new Dimension(200, (tableModel.getRowCount()) * height + 10);
        this.setPreferredSize(dimension);
        this.setMinimumSize(dimensionMin);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        this.setPreferredScrollableViewportSize(dimension);
        this.setFillsViewportHeight(true);

        this.setCellSelectionEnabled(false);
        this.setDragEnabled(false);
        this.setOpaque(false);
    }

    @Override
    public ScriptsTableModel getModel() {
        return (ScriptsTableModel) super.getModel();
    }

    static class WrappedComponent extends JPanel {
        private final JComponent myComponent;
        private final @NotNull Color myBackgroundColor;
        private final @NotNull String myTooltip;

        public WrappedComponent(JComponent component, @NotNull Color backgroundColor, @NotNull String tooltip) {
            super(new BorderLayout());
            myComponent = component;
            myBackgroundColor = backgroundColor;
            myTooltip = tooltip;
            add(myComponent);
            setBackground(backgroundColor);
        }

        @Override
        public String getToolTipText() {
            return myTooltip;
        }
    }

    @Override
    public String getToolTipText(@NotNull MouseEvent event) {
        return super.getToolTipText(event);
    }

    Color getWarningTableBackground() {
        return Helpers.warningColor(UIUtil.getComboBoxDisabledBackground());
    }

    Color getExperimentalTableBackground() {
        return Helpers.errorColor(UIUtil.getComboBoxDisabledBackground());
    }

    @Override
    public TableCellEditor getCellEditor() {
        return super.getCellEditor();
    }

    @NotNull
    public Component prepareRenderer(@NotNull TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        if (getRowCount() > 0) {
            boolean isEnabled = (Boolean) getModel().getValueAt(row, ScriptsTableModel.ENABLED_COL);
            component.setEnabled(isEnabled);
            Color background = UIUtil.getComboBoxDisabledBackground();
            String message = null;

            if (isEnabled) {
                if (getModel().getExperimentalWarningAt(row)) {
                    background = getExperimentalTableBackground();
                    message = MdBundle.message("settings.preview-experimental-warning.description");
                } else if (getModel().getPerformanceWarningAt(row)) {
                    background = getWarningTableBackground();
                    message = MdBundle.message("settings.preview-performance-warning.description");
                }
            }

            component.setBackground(background);
            if (component instanceof JComponent && isEnabled && message != null) {
                component = new WrappedComponent((JComponent) component, background, message);
            }
        }
        return component;
    }
}
