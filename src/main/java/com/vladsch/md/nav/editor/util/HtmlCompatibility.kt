// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

class HtmlCompatibility(
    val providerId: String                            // id of the script, html generator or html panel provider that owns this compatibility instance
    , val htmlLevel: Float?                          // for ScriptProviders and HtmlGeneratorProviders this is min level they need, null means anything, for HtmlPanelProviders this is max level they offer, null means anything offered, 0 means nothing is offered
    , val cssLevel: Float?                           // for ScriptProviders and HtmlGeneratorProviders this is min level they need, for HtmlPanelProviders this is max level they offer
    , val scriptLevel: Float?                        // for ScriptProviders and HtmlGeneratorProviders this is min level they need, for HtmlPanelProviders this is max level they offer
    , val includeProviders: Array<String>             // for ScriptProviders and HtmlGeneratorProviders this lists provider ids to be included, for HtmlPanelProviders this list should include ids of any other panel providers required by this panel for its for operation. All providers in this list must be available and compatible with the script or html generator provider for them to be compatible with the myPanelProvider
    , val excludeProviders: Array<String>      // for ScriptProviders and HtmlGeneratorProviders this lists provider names to be excluded, for HtmlPanelProviders this is a list of script and html generator provider to be excluded
) {

    fun isForRequired(requiredLevel: HtmlCompatibility): Boolean = isCompatibleWith(this, requiredLevel)
    fun isForAvailable(availableLevel: HtmlCompatibility): Boolean = isCompatibleWith(availableLevel, this)

    companion object {
        fun isCompatibleWith(availableLevel: Float?, requiredLevel: Float?): Boolean {
            return requiredLevel == null || availableLevel == null || availableLevel >= requiredLevel
        }

        fun isCompatibleWith(offered: HtmlCompatibility, required: HtmlCompatibility): Boolean {
            // first check incompatibility through exclusion
            if (required.providerId in offered.excludeProviders) return false
            if (required.providerId in offered.includeProviders) return true

            // no short cuts, now check by compatibility
            for (dependsOn in offered.includeProviders) {
                if (dependsOn in required.excludeProviders) return false
                if (dependsOn in required.includeProviders) continue
                if (dependsOn == offered.providerId) continue

                val provider = HtmlPanelProvider.getFromId(dependsOn) ?: continue

                if (!isCompatibleWith(provider.COMPATIBILITY.htmlLevel, required.htmlLevel)) return false
                if (!isCompatibleWith(provider.COMPATIBILITY.scriptLevel, required.scriptLevel)) return false
                if (!isCompatibleWith(provider.COMPATIBILITY.cssLevel, required.cssLevel)) return false
            }

            return isCompatibleWith(offered.htmlLevel, required.htmlLevel)
                && isCompatibleWith(offered.scriptLevel, required.scriptLevel)
                && isCompatibleWith(offered.cssLevel, required.cssLevel)
        }
    }
}
