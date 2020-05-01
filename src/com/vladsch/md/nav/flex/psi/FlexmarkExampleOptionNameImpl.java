// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.lang.ASTNode;
import com.vladsch.md.nav.psi.element.MdPsiElementImpl;
import org.jetbrains.annotations.NotNull;

public class FlexmarkExampleOptionNameImpl extends MdPsiElementImpl implements FlexmarkExampleOptionName {
    public FlexmarkExampleOptionNameImpl(@NotNull ASTNode node) {
        super(node);
    }
}
