// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.ast;

import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

public class MdASTLeafNode implements MdASTNode {
    private static final List<MdASTNode> EMPTY_LIST = new ArrayList<MdASTNode>(0);

    private final int startOffset;
    private final int endOffset;
    private final IElementType elementType;

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public int getEndOffset() {
        return endOffset;
    }

    @Override
    public IElementType getElementType() {
        return elementType;
    }

    @Override
    public List<MdASTNode> getChildren() {
        return EMPTY_LIST;
    }

    public MdASTLeafNode(IElementType elementType, int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.elementType = elementType;
    }

    @Override
    public void accept(MdASTVisitor visitor) {
        visitor.visitNode(this);
    }

    @Override
    public void acceptChildren(MdASTVisitor visitor) {

    }

    @Override
    public String toString() {
        return elementType + "[" + startOffset + "," + endOffset + ")";
    }
}
