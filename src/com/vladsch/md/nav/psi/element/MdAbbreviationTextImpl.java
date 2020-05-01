// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class MdAbbreviationTextImpl extends ASTWrapperPsiElement implements MdAbbreviationText {
    public MdAbbreviationTextImpl(@NotNull ASTNode node) {
        super(node);
    }
}
