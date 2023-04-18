// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.parser.api.MdTypeFactoryRegistry;
import com.vladsch.md.nav.util.MdExtensions;

public interface MdTypeFactory {
    ExtensionPointName<MdTypeFactory> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.elementTypeFactory");
    MdExtensions<MdTypeFactory> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdTypeFactory[0]);

    void addTypeFactories(MdTypeFactoryRegistry resolver);
}
