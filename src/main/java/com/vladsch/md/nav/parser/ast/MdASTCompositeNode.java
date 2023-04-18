// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.ast;

import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

public class MdASTCompositeNode implements MdASTNode {
    private final int startOffset;
    private final int endOffset;
    private final IElementType elementType;
    private final ArrayList<MdASTNode> children = new ArrayList<MdASTNode>();

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
        return children;
    }

    public MdASTCompositeNode(IElementType elementType, int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.elementType = elementType;
    }

    public MdASTCompositeNode(IElementType elementType, int startOffset, int endOffset, List<MdASTNode> children) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.elementType = elementType;

        this.children.addAll(children);
    }

    public void add(MdASTNode child) {
        children.add(child);
    }

    public void add(int index, MdASTNode child) {
        children.add(index, child);
    }

    @Override
    public void accept(MdASTVisitor visitor) {
        visitor.visitNode(this);
    }

    @Override
    public void acceptChildren(MdASTVisitor visitor) {
        for (MdASTNode child : children) {
            child.accept(visitor);
        }
    }

    @Override
    public String toString() {
        //StringBuilder sb = new StringBuilder();
        //for (MarkdownASTNode child : children) {
        //    if (sb.length() != 0) sb.append(", ");
        //    sb.append(child.toString());
        //}
        return elementType + "[" + startOffset + "," + endOffset + ")" + ": children[" + children.size() + "]";
    }
}
