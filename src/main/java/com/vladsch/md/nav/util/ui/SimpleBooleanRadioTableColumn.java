// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.vladsch.md.nav.util.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SimpleBooleanRadioTableColumn<T> extends SimpleBooleanTableColumn<T> {
    final TableCellRenderer myRenderer = new CellRenderingEditor();
    final TableCellEditor myEditor = new CellRenderingEditor();

    public SimpleBooleanRadioTableColumn(String header, final int width, @NotNull Function<T, Boolean> getter, @Nullable BiConsumer<T, Boolean> setter) {
        super(header, width, getter, setter);
    }

    public SimpleBooleanRadioTableColumn(String header, final int width, @NotNull Field<T, Boolean> field) {
        super(header, width, field);
    }

    @Override
    public final Class<Boolean> getColumnClass() {
        return Boolean.class;
    }

    @Nullable
    @Override
    public TableCellRenderer getRenderer(final T t) {
        return myRenderer;
    }

    @Override
    public TableCellEditor getEditor(final T entry) {
        return myEditor;
    }

    static class CellRenderingEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        final JRadioButton myComponent = new JRadioButton();

        public CellRenderingEditor() {
            myComponent.setOpaque(true);
            myComponent.addActionListener(event -> stopCellEditing());
            myComponent.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            setComponent(table, (Boolean) value, isSelected, hasFocus);
            return myComponent;
        }

        public void setComponent(final JTable table, final Boolean value, final boolean isSelected, final boolean hasFocus) {
            myComponent.setSelected(value);
            myComponent.setFocusPainted(hasFocus);
            myComponent.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            myComponent.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        }

        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
            setComponent(table, (Boolean) value, isSelected, true);
            return myComponent;
        }

        @Override
        public Object getCellEditorValue() {
            return myComponent.isSelected();
        }
    }
}
