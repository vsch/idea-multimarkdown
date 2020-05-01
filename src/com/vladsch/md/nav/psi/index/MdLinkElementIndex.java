// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.index;

import com.intellij.psi.stubs.StubIndexKey;
import com.vladsch.md.nav.psi.element.MdLinkElement;
import org.jetbrains.annotations.NotNull;

public class MdLinkElementIndex extends MdStubIndexExtension<MdLinkElement> {
    public static final StubIndexKey<String, MdLinkElement> KEY = StubIndexKey.createIndexKey("markdown.link-element.index");
    private static final MdLinkElementIndex ourInstance = new MdLinkElementIndex();

    public static MdLinkElementIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    public StubIndexKey<String, MdLinkElement> getKey() {
        return KEY;
    }
}
