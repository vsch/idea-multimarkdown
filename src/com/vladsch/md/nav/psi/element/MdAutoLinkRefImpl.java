// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;

public class MdAutoLinkRefImpl extends MdLinkRefImpl implements MdAutoLinkRef {
    public MdAutoLinkRefImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "AUTO_LINK_REF '" + getName() + "' " + super.hashCode();
    }
}
