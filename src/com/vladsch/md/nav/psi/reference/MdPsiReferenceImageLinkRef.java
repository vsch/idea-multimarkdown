// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.reference;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.vladsch.md.nav.psi.element.MdImageLinkRef;
import org.jetbrains.annotations.NotNull;

public class MdPsiReferenceImageLinkRef extends MdPsiReferenceLinkRef {
    public MdPsiReferenceImageLinkRef(@NotNull MdImageLinkRef element, @NotNull TextRange textRange, boolean exactReference) {
        super(element, textRange, exactReference);
    }
}
