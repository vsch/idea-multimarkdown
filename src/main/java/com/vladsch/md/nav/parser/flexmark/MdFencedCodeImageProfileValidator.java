// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.vladsch.md.nav.parser.api.MdFencedCodeImageConverter;
import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.api.MdRenderingProfileValidator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MdFencedCodeImageProfileValidator implements MdRenderingProfileValidator {
    @Override
    public void validateSettings(@NotNull MdRenderingProfile renderingProfile) {
        MdHtmlSettings htmlSettings = renderingProfile.getHtmlSettings();
        HashMap<String, String> fencedCodeConversions = htmlSettings.getFencedCodeConversions();

        for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
            String[] infoStrings = converter.getInfoStrings();
            for (String infoString : infoStrings) {
                if (!fencedCodeConversions.containsKey(infoString)) {
                    String[] variants = converter.getConversionVariants(infoString);
                    if (variants.length > 0) {
                        fencedCodeConversions.put(infoString, variants[0]);
                    }
                }
            }
        }
    }
}
