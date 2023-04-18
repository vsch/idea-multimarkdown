// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi;

import com.intellij.psi.stubs.StubElement;
import com.vladsch.md.nav.psi.util.TextMapElementType;
import com.vladsch.md.nav.psi.util.TextMapMatch;
import org.jetbrains.annotations.NotNull;

public interface MdPlainTextStub<Elem extends MdPlainText> extends StubElement<Elem> {
    @NotNull
    TextMapMatch[] getTextMapMatches();

    @NotNull
    TextMapElementType getTextMapType();

    int getReferenceableOffsetInParent();
}
