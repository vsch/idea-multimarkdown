// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.Disposable;
import com.vladsch.md.nav.settings.MdSettableFormBase;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public interface MdSettingsComponent<T> extends MdSettableFormBase<T>, Disposable {
    @NotNull
    JComponent getComponent();

    void updateOptionalSettings();
}
