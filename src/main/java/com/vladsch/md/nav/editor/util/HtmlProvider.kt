// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.vladsch.md.nav.settings.*

abstract class HtmlProvider {
    abstract val INFO: HtmlProvider.Info
    abstract val HAS_PARENT: Boolean
    abstract val COMPATIBILITY: HtmlCompatibility
    abstract fun isSupportedSetting(settingName: String): Boolean

    abstract class Info(factory: () -> Item<StateHolder>) : StateHolderImpl(factory) {
        var providerId: String = ""
            protected set

        var name: String = ""
            protected set

        override fun getStateHolder(): StateHolder {
            val ideaMultimarkdownPrefix = "com.vladsch.idea.multimarkdown."
            val mdNavPrefix = "com.vladsch.md.nav."

            return TagItemHolder("provider").addItems(
                StringAttribute("providerId", { providerId }, {
                    // NOTE: need to map change from .idea.multimarkdown. to .md.nav. to preserve css settings
                    providerId = if (it.startsWith(ideaMultimarkdownPrefix)) {
                        mdNavPrefix + it.substring(ideaMultimarkdownPrefix.length)
                    } else it
                }),
                StringAttribute("providerName", { name }, { name = it })
            )
        }

        constructor(providerId: String, name: String, factory: () -> Item<StateHolder>) : this(factory) {
            this.providerId = providerId
            this.name = name
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false

            val info = other as? Info

            if (providerId != info?.providerId) return false
            if (name != info.name) return false
            return true
        }

        override fun hashCode(): Int {
            var result = 0
            result = 31 * result + providerId.hashCode()
            result = 31 * result + name.hashCode()
            return result
        }

        override fun toString(): String {
            return name
        }
    }
}
