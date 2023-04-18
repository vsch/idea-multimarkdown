// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MdEnumeratedReferenceId extends MdReferencingElementReference {
    @NotNull
    String getTypeText();

    MdEnumeratedReferenceId setType(@NotNull String newName, int reason);

    @NotNull
    Pair<List<String>, List<TextRange>> getTypeList();
}
