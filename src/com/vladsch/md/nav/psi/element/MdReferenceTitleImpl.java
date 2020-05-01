// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;

public class MdReferenceTitleImpl extends MdLinkTitleImpl implements MdReferenceTitle {
    public MdReferenceTitleImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "REFERENCE_TITLE '" + getName() + "' " + super.hashCode();
    }
}
