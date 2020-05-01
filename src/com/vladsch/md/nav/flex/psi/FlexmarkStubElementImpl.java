// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.MdLanguage;
import org.jetbrains.annotations.NotNull;

public class FlexmarkStubElementImpl<T extends StubElement> extends StubBasedPsiElementBase<T> {
    public FlexmarkStubElementImpl(T stub, IElementType nodeType, ASTNode node) {
        super(stub, nodeType, node);
    }

    public FlexmarkStubElementImpl(final T stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public FlexmarkStubElementImpl(final ASTNode node) {
        super(node);
    }

    @NotNull
    public Language getLanguage() {
        return MdLanguage.INSTANCE;
    }
}
