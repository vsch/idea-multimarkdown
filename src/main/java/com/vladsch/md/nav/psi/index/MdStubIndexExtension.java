// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.index;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.vladsch.md.nav.parser.MdFileElementType;

public abstract class MdStubIndexExtension<Psi extends PsiElement> extends StringStubIndexExtension<Psi> {
    @Override
    final public int getVersion() {
        return super.getVersion() + MdFileElementType.MD_INDEX_VERSION;
    }
}
