// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.intellij.profile.codeInspection.ui.table.ThreeStateCheckBoxRenderer;
import com.intellij.util.ui.ThreeStateCheckBox;
import com.vladsch.md.nav.util.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SimpleThreeStateTableColumn<T> extends SimpleTableColumn<T, Boolean> {
    private boolean myThirdStateEnabled;
    private ThreeStateCheckBoxRenderer myRenderer;
    private ThreeStateCheckBoxRenderer myEditor;

    public SimpleThreeStateTableColumn(String header, final int width, @NotNull Function<T, Boolean> getter, @Nullable BiConsumer<T, Boolean> setter) {
        super(header, width, getter, setter);
        myThirdStateEnabled = false;
    }

    public SimpleThreeStateTableColumn(String header, final int width, @NotNull Field<T, Boolean> field) {
        super(header, width, field);
    }

    public boolean isThirdStateEnabled() {
        return myThirdStateEnabled;
    }

    public void setThirdStateEnabled(final boolean thirdStateEnabled) {
        myThirdStateEnabled = thirdStateEnabled;
        if (myEditor != null) myEditor.setThirdStateEnabled(thirdStateEnabled);
        if (myRenderer != null) myRenderer.setThirdStateEnabled(thirdStateEnabled);
    }

    @Override
    public final Class<ThreeStateCheckBox.State> getColumnClass() {
        return ThreeStateCheckBox.State.class;
    }

    @Nullable
    @Override
    public TableCellRenderer getRenderer(final T t) {
        if (myRenderer == null) {
            myRenderer = new ThreeStateCheckBoxRenderer();
            myRenderer.setThirdStateEnabled(myThirdStateEnabled);
        }
        return myRenderer;
    }

    @Override
    public TableCellEditor getEditor(final T entry) {
        if (myEditor == null) {
            myEditor = new ThreeStateCheckBoxRenderer();
            myEditor.setThirdStateEnabled(myThirdStateEnabled);
        }
        return myEditor;
    }
}
