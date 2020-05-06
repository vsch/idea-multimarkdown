// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.util.DataPrinterAware;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdElementType extends IElementType implements DataPrinterAware {

    public MdElementType(@NotNull @NonNls String debugName) {
        super(debugName, MdLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String testData() {
        return "MultiMarkdownElementType." + super.toString();
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
