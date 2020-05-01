// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.psi.MdPlainText;
import com.vladsch.md.nav.psi.MdPlainTextStub;
import com.vladsch.md.nav.psi.util.TextMapMatch;
import org.jetbrains.annotations.NotNull;

public abstract class MdStubPlainTextImpl<T extends MdPlainTextStub<?>> extends StubBasedPsiElementBase<T> implements MdPlainText<T> {
    public MdStubPlainTextImpl(T stub, IElementType nodeType, ASTNode node) {
        super(stub, nodeType, node);
    }

    public MdStubPlainTextImpl(final T stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public MdStubPlainTextImpl(final ASTNode node) {
        super(node);
    }

    @NotNull
    public Language getLanguage() {
        return MdLanguage.INSTANCE;
    }

    @Override
    @NotNull
    public TextMapMatch[] getTextMapMatches() {
        T stub = getStub();
        if (stub != null) {
            return stub.getTextMapMatches();
        }
        PsiFile psiFile = getContainingFile();
        TextMapMatch[] textMapMatches = MdPlainTextStubElementType.getTextMapMatches(psiFile, getTextMapType(), getReferenceableText());
        return textMapMatches;
    }
}
