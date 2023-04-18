// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.psi.api.MdTextMapElementTypeProvider;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;

public class BasicTextMapElementTypeProvider implements MdTextMapElementTypeProvider {
    final public static TextMapElementType LINK_ADDRESS = new TextMapElementType("LINK_ADDRESS", MdBundle.message("settings.link-map.element-type.link-ref.display-name"), MdBundle.message("settings.link-map.element-type.link-ref.banner"), false, true, true, MdIcons.Element.LINK);
    final public static TextMapElementType JEKYLL_FRONT_MATTER = new TextMapElementType("JEKYLL_FRONT_MATTER", MdBundle.message("settings.link-map.element-type.jekyll-front-matter.display-name"), MdBundle.message("settings.link-map.element-type.jekyll-front-matter.banner"), false, false, true, MdIcons.Element.JEKYLL);
    final public static TextMapElementType REFERENCE = new TextMapElementType("REFERENCE", MdBundle.message("settings.link-map.element-type.reference.display-name"), MdBundle.message("settings.link-map.element-type.reference.banner"), false, false, false, MdIcons.Element.REFERENCE);
    final public static TextMapElementType FENCED_CODE = new TextMapElementType("FENCED_CODE", MdBundle.message("settings.link-map.element-type.fenced-code.display-name"), MdBundle.message("settings.link-map.element-type.fenced-code.banner"), true, false, true, null);
    final public static TextMapElementType VERBATIM = new TextMapElementType("VERBATIM", MdBundle.message("settings.link-map.element-type.verbatim.display-name"), MdBundle.message("settings.link-map.element-type.verbatim.banner"), false, false, true, null);
    final public static TextMapElementType CODE = new TextMapElementType("CODE", MdBundle.message("settings.link-map.element-type.code.display-name"), MdBundle.message("settings.link-map.element-type.code.banner"), false, false, true, null);
    final public static TextMapElementType HTML_BLOCK = new TextMapElementType("HTML_BLOCK", MdBundle.message("settings.link-map.element-type.html-block.display-name"), MdBundle.message("settings.link-map.element-type.html-block.banner"), false, false, true, MdIcons.Element.ANCHOR);

    final static TextMapElementType[] ourBuiltInTypes = {
            LINK_ADDRESS,
            JEKYLL_FRONT_MATTER,
            REFERENCE,
            FENCED_CODE,
            VERBATIM,
            CODE,
            HTML_BLOCK,
    };

    @NotNull
    @Override
    public TextMapElementType[] getElementTypes() {
        return ourBuiltInTypes;
    }
}
