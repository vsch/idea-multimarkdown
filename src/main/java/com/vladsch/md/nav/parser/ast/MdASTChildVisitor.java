// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.ast;

public class MdASTChildVisitor implements MdASTVisitor {
    @Override
    public void visitNode(MdASTNode node) {
        for (MdASTNode child : node.getChildren()) {
            if (child instanceof MdASTCompositeNode) {
                visitNode(child);
            }
        }
    }
}
