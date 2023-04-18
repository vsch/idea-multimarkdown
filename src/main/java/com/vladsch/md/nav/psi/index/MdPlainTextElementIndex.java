// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.index;

import com.intellij.psi.stubs.StubIndexKey;
import com.vladsch.md.nav.psi.MdPlainText;
import org.jetbrains.annotations.NotNull;

public class MdPlainTextElementIndex extends MdStubIndexExtension<MdPlainText> {
    public static final StubIndexKey<String, MdPlainText> KEY = StubIndexKey.createIndexKey("markdown.plain-text-element.index");
    private static final MdPlainTextElementIndex ourInstance = new MdPlainTextElementIndex();

    public static MdPlainTextElementIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    public StubIndexKey<String, MdPlainText> getKey() {
        return KEY;
    }
}
