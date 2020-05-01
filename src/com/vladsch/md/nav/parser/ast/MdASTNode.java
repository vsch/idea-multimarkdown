// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.ast;

import com.intellij.psi.tree.IElementType;

import java.util.List;

public interface MdASTNode {
    int getStartOffset();

    int getEndOffset();

    IElementType getElementType();

    List<MdASTNode> getChildren();

    void accept(MdASTVisitor visitor);

    void acceptChildren(MdASTVisitor visitor);
}
