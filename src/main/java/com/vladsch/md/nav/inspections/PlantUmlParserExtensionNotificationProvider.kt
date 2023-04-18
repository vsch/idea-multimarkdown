// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.inspections

import com.intellij.openapi.util.Key
import com.intellij.ui.EditorNotificationPanel
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.parser.flexmark.MdFencedCodeImageConversionManager
import com.vladsch.md.nav.parser.flexmark.MdFencedCodePlantUmlConverter
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.ParserOptions

class PlantUmlParserExtensionNotificationProvider : FencedCodeRenderingExtensionNotificationProviderBase() {
    override fun getKey(): Key<EditorNotificationPanel> {
        return KEY
    }

    override fun getPanelText(): String = MdBundle.message("editor.have-plantuml-references.name")

    override fun getPanelLabel(): String = MdBundle.message("editor.have-plantuml-references.enable")

    override var wasShown: Boolean
        get() = MdApplicationSettings.instance.wasShownSettings.plantUmlExtensionAvailable
        set(value) {
            MdApplicationSettings.instance.wasShownSettings.plantUmlExtensionAvailable = value
        }

//    override fun haveNeededParserSettings(parserSettings: MdParserSettings): Boolean = parserSettings.anyExtensions(PegdownExtensions.FENCED_CODE_BLOCKS)

    override fun getDefaultVariant(info: String): String = MdFencedCodePlantUmlConverter.EMBEDDED

    override fun getInfoStrings(): Array<String> = MdFencedCodePlantUmlConverter.INFO_STRINGS

    override fun adjustRenderingProfile(conversionManager: MdFencedCodeImageConversionManager, renderingProfile: MdRenderingProfile, fencedCodeTypes: Set<String>) {
        val newParserSettings = renderingProfile.parserSettings
        newParserSettings.optionsFlags = newParserSettings.optionsFlags or ParserOptions.GITLAB_MATH_EXT.flags
        super.adjustRenderingProfile(conversionManager, renderingProfile, fencedCodeTypes)
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("editor.have-plantuml-references.name")
    }
}
