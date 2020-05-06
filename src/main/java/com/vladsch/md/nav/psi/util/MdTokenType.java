// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.util;

import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.util.DataPrinterAware;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdTokenType extends IElementType implements DataPrinterAware {
    public MdTokenType(@NotNull @NonNls String debugName) {
        super(debugName, MdLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String testData() {
        return "MdTokenType." + super.toString();
    }

    @NotNull
    @Override
    public String className(@Nullable Class<Object> inParent) {
        return DataPrinterAware.DefaultImpls.className(this, null);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
