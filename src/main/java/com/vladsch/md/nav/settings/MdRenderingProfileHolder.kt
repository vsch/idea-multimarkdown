// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings

import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.settings.api.MdSettingsExtensionsHolder

interface MdRenderingProfileHolder :
    MdSettingsExtensionsHolder
    , MdPreviewSettings.Holder
    , MdParserSettings.Holder
    , MdHtmlSettings.Holder
    , MdCssSettings.Holder
    , MdCodeStyleSettings.Holder
//    , MdAnnotatorSettings.Holder
//    , MdHtmlExportSettings.Holder
//    , MdLinkMapSettings.Holder
{

    var renderingProfile: MdRenderingProfile
    fun getResolvedProfile(parentProfile: MdRenderingProfile): MdRenderingProfile
    fun groupNotifications(runnable: Runnable)
}
