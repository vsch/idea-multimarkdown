// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MdAttributes extends MdComposite {

    @Nullable
    MdAttributeIdValue getIdValueAttribute();

    @NotNull
    MdAttributes setAttributes(Map<String, String> attributes);

    @NotNull
    Map<String, String> getAttributes();
}
