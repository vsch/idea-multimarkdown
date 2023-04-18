// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.parser.MdFactoryContext;
import com.vladsch.md.nav.psi.element.MdElementTextProviderImpl;
import com.vladsch.md.nav.util.MdExtensions;
import com.vladsch.plugin.util.LazyComputable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface MdElementTextProvider {
    ExtensionPointName<MdElementTextProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.elementTextProvider");
    MdExtensions<MdElementTextProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdElementTextProvider[0]);

    LazyComputable<MdElementTextProvider> DEFAULT = new LazyComputable<>(MdElementTextProviderImpl::new);

    static @NotNull
    CharSequence getElementText(@NotNull Function<MdElementTextProvider, CharSequence> extractor) {
        for (MdElementTextProvider provider : EXTENSIONS.getValue()) {
            return extractor.apply(provider);
        }
        return extractor.apply(DEFAULT.getValue());
    }

    @NotNull
    CharSequence getAtxHeaderText(@NotNull MdFactoryContext factoryContext, @NotNull CharSequence text, int level, boolean hasTrailingMarker);
}
