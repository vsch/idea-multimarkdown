// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.LighterASTTokenNode;
import com.intellij.psi.stubs.ILightStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.CharTable;
import com.vladsch.md.nav.MdLanguage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FlexmarkExampleOptionsStubElementType extends ILightStubElementType<FlexmarkExampleOptionsStub, FlexmarkExampleOptions> {
    public FlexmarkExampleOptionsStubElementType(@NotNull String debugName) {
        super(debugName, MdLanguage.INSTANCE);
    }

    @Override
    public FlexmarkExampleOptions createPsi(@NotNull final FlexmarkExampleOptionsStub stub) {
        return new FlexmarkExampleOptionsImpl(stub, this);
    }

    @NotNull
    @Override
    public FlexmarkExampleOptionsStub createStub(@NotNull final FlexmarkExampleOptions psi, final StubElement parentStub) {
        return new FlexmarkExampleOptionsStubImpl(parentStub);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "flexmark.example.options";
    }

    @Override
    public void serialize(@NotNull final FlexmarkExampleOptionsStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
    }

    @NotNull
    @Override
    public FlexmarkExampleOptionsStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        return new FlexmarkExampleOptionsStubImpl(parentStub);
    }

    @Override
    public void indexStub(@NotNull final FlexmarkExampleOptionsStub stub, @NotNull final IndexSink sink) {
    }

    @NotNull
    @Override
    public FlexmarkExampleOptionsStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        return new FlexmarkExampleOptionsStubImpl(parentStub);
    }

    public static String intern(@NotNull CharTable table, @NotNull LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
