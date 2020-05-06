// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.vladsch.md.nav.parser.MdFileElementType;
import org.jetbrains.annotations.NotNull;

public class MdFileStub extends PsiFileStubImpl<MdFile> {
    final public static IStubFileElementType<MdFileStub> MD_STUB_TYPE = MdFileElementType.INSTANCE;

    public MdFileStub(final MdFile file) {
        super(file);
    }

    @NotNull
    @Override
    public IStubFileElementType<MdFileStub> getType() {
        return MD_STUB_TYPE;
    }
}
