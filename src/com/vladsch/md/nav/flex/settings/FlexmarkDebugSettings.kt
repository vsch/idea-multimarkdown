// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.settings

import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.settings.BooleanAttribute
import com.vladsch.md.nav.settings.StateHolder
import com.vladsch.md.nav.settings.StateHolderImpl
import com.vladsch.md.nav.settings.TagItemHolder
import com.vladsch.md.nav.settings.api.MdSettingsExtension

class FlexmarkDebugSettings : StateHolderImpl({ FlexmarkDebugSettings() }), MdSettingsExtension<FlexmarkDebugSettings> {

    var enableFlexmarkFeatures: Boolean = false

    override fun createCopy(): FlexmarkDebugSettings {
        val copy = FlexmarkDebugSettings()
        copy.copyFrom(this)
        return copy
    }

    override fun getKey(): DataKey<FlexmarkDebugSettings> = KEY
    override fun getDefault(): FlexmarkDebugSettings = DEFAULT

    override fun copyFrom(other: FlexmarkDebugSettings) {
        this.enableFlexmarkFeatures = other.enableFlexmarkFeatures
    }

    override fun getStateHolder(): StateHolder {
        val tagItemHolder = TagItemHolder("EnhDebugSettings")
        addItems(false, tagItemHolder)
        return tagItemHolder
    }

    override fun addItems(readOnly: Boolean, tagItemHolder: TagItemHolder) {
        tagItemHolder.addItems(
            BooleanAttribute("enableFlexmarkFeatures", readOnly, { enableFlexmarkFeatures }, { enableFlexmarkFeatures = it })
        )
    }

    companion object {
        @JvmField
        val KEY = DataKey("FlexmarkDebugSettings") { FlexmarkDebugSettings() }

        @JvmField
        val DEFAULT = FlexmarkDebugSettings()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlexmarkDebugSettings) return false

        // enhanced
        if (enableFlexmarkFeatures != other.enableFlexmarkFeatures) return false
        return true
    }

    override fun hashCode(): Int {
        var result = 0
        result += 31 * result + enableFlexmarkFeatures.hashCode()
        return result
    }
}
