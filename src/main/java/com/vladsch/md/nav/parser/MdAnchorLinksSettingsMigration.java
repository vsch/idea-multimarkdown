// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.api.MdRenderingProfileValidator;
import org.jetbrains.annotations.NotNull;

public class MdAnchorLinksSettingsMigration implements MdRenderingProfileValidator {
    public void validateSettings(@NotNull MdRenderingProfile renderingProfile) {
        MdHtmlSettings htmlSettings = renderingProfile.getHtmlSettings();
        MdParserSettings parserSettings = renderingProfile.getParserSettings();

        htmlSettings.setAddAnchorLinks(parserSettings.anyExtensions(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS));
        parserSettings.setPegdownFlags(parserSettings.getPegdownFlags() & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS));
    }
}
