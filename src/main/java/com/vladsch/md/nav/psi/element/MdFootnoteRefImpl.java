// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.parser.MdFactoryContext;
import com.vladsch.md.nav.psi.util.MdTypes;
import org.jetbrains.annotations.NotNull;

public class MdFootnoteRefImpl extends MdReferencingElementImpl implements MdFootnoteRef {
    protected static final String STRING_NAME = "FOOTNOTE_REFERENCE";

    public static String getElementText(@NotNull MdFactoryContext factoryContext, @NotNull String referenceId) {
        return "[^" + referenceId + "]";
    }

    public MdFootnoteRefImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getToStringName() {
        return STRING_NAME;
    }

    @NotNull
    @Override
    public IElementType getReferenceChildType() {
        return MdTypes.FOOTNOTE_REF_ID;
    }

    @NotNull
    @Override
    public IElementType getTextChildType() {
        throw new IllegalStateException("Footnote references do not have a text child type");
    }
}
