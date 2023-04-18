// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;

public class MdAutoLinkAnchorImpl extends MdLinkAnchorImpl implements MdAutoLinkAnchor {
    public MdAutoLinkAnchorImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "AUTO_LINK_REF_ANCHOR '" + getName() + "' " + super.hashCode();
    }
}
