// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.vladsch.md.nav.parser.api.MdFencedCodeImageConverter;
import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.ParserOptions;
import com.vladsch.md.nav.settings.PlantUmlConversionType;
import com.vladsch.md.nav.settings.api.MdRenderingProfileValidator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MdPlantUmlConversionSettingsMigration implements MdRenderingProfileValidator {
    public void validateSettings(@NotNull MdRenderingProfile renderingProfile) {
        MdParserSettings parserSettings = renderingProfile.getParserSettings();
        MdHtmlSettings htmlSettings = renderingProfile.getHtmlSettings();

        if (!htmlSettings.getMigratedPlantUml()) {
            htmlSettings.setMigratedPlantUml(true);
            
            if (parserSettings.anyOptions(ParserOptions.PUML_FENCED_CODE, ParserOptions.PLANTUML_FENCED_CODE)) {
                PlantUmlConversionType conversionType = htmlSettings.getPlantUmlConversionType();
                HashMap<String, String> fencedCodeConversions = htmlSettings.getFencedCodeConversions();

                for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
                    String[] infoStrings = converter.getInfoStrings();
                    for (String infoString : infoStrings) {
                        if (!fencedCodeConversions.containsKey(infoString)) {
                            if (MdFencedCodePlantUmlConverter.PUML_LANGUAGE_INFO.equals(infoString)) {
                                String variant = converter.migrateConversionVariant(infoString, conversionType);
                                if (variant != null) {
                                    fencedCodeConversions.put(infoString, variant);
                                }
                            } else if (MdFencedCodePlantUmlConverter.PLANTUML_LANGUAGE_INFO.equals(infoString)) {
                                String variant = converter.migrateConversionVariant(infoString, conversionType);
                                if (variant != null) {
                                    fencedCodeConversions.put(infoString, variant);
                                }
                            }
                        }
                    }
                }

                //            // remove parser flags and conversion type, these are no longer used
                //            htmlSettings.setPlantUmlConversionType(PlantUmlConversionType.NONE);
                //            parserSettings.setOptionsFlags(parserSettings.getOptionsFlags() & ~(ParserOptions.PUML_FENCED_CODE.getFlags() | ParserOptions.PLANTUML_FENCED_CODE.getFlags()));
            }
        }
    }
}
