// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.util;

import com.vladsch.md.nav.flex.PluginBundle;
import com.vladsch.md.nav.psi.api.MdTextMapElementTypeProvider;
import com.vladsch.md.nav.psi.util.TextMapElementType;
import icons.FlexmarkIcons;
import org.jetbrains.annotations.NotNull;

public class FlexTextMapElementTypeProvider implements MdTextMapElementTypeProvider {
    final public static TextMapElementType FLEXMARK_FRONT_MATTER = new TextMapElementType("FLEXMARK_FRONT_MATTER", PluginBundle.message("settings.link-map.element-type.flexmark-front-matter.display-name"), PluginBundle.message("settings.link-map.element-type.flexmark-front-matter.banner"), false, false, true, FlexmarkIcons.Element.FLEXMARK_SPEC);

    final static TextMapElementType[] ourBuiltInTypes = {
            FLEXMARK_FRONT_MATTER,
    };

    @NotNull
    @Override
    public TextMapElementType[] getElementTypes() {
        return ourBuiltInTypes;
    }
}
