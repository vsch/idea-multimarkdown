// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.LighterASTTokenNode;
import com.intellij.psi.impl.source.tree.LightTreeUtil;
import com.intellij.psi.stubs.ILightStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.CharTable;
import com.intellij.util.io.StringRef;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.flex.psi.index.FlexmarkExampleOptionIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FlexmarkExampleOptionStubElementType extends ILightStubElementType<FlexmarkExampleOptionStub, FlexmarkExampleOption> {
    public FlexmarkExampleOptionStubElementType(@NotNull String debugName) {
        super(debugName, MdLanguage.INSTANCE);
    }

    @Override
    public FlexmarkExampleOption createPsi(@NotNull final FlexmarkExampleOptionStub stub) {
        return new FlexmarkExampleOptionImpl(stub, this);
    }

    @NotNull
    @Override
    public FlexmarkExampleOptionStub createStub(@NotNull final FlexmarkExampleOption psi, final StubElement parentStub) {
        String optionText = psi.getText();
        return new FlexmarkExampleOptionStubImpl(parentStub, optionText);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "flexmark.example.option";
    }

    @Override
    public void serialize(@NotNull final FlexmarkExampleOptionStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getOptionText());
    }

    @NotNull
    @Override
    public FlexmarkExampleOptionStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        final StringRef optionText = dataStream.readName();
        return new FlexmarkExampleOptionStubImpl(parentStub, optionText == null ? "" : optionText.getString());
    }

    @Override
    public void indexStub(@NotNull final FlexmarkExampleOptionStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(FlexmarkExampleOptionIndex.KEY, stub.getOptionName());
    }

    @NotNull
    @Override
    public FlexmarkExampleOptionStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        String optionText = LightTreeUtil.toFilteredString(tree, node, null);
        return new FlexmarkExampleOptionStubImpl(parentStub, optionText);
    }

    public static String intern(@NotNull CharTable table, @NotNull LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
