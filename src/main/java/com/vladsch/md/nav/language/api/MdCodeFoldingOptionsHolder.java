// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.openapi.util.Getter;
import com.intellij.openapi.util.Setter;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public interface MdCodeFoldingOptionsHolder {
    /**
     * Adds check box with given {@code title}.
     * Initial checkbox value is obtained from {@code getter}.
     * After the apply, the value from the check box is written back to model via {@code setter}.
     */
    void addCheckBox(@NotNull String title, @NotNull Getter<Boolean> getter, @NotNull Setter<Boolean> setter);

    <V> void component(@NotNull JComponent component, @NotNull Getter<? extends V> beanGetter, @NotNull Setter<? super V> beanSetter, @NotNull Getter<? extends V> componentGetter, @NotNull Setter<? super V> componentSetter);
}
