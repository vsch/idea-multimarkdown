// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import org.jetbrains.annotations.NotNull;

public interface MdOrderedList extends MdList {
    int itemOrdinalOffset(boolean skipFirst, @NotNull PsiEditContext editContext);
}
