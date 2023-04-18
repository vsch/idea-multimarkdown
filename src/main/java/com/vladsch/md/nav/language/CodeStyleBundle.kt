// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language

import com.intellij.AbstractBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.util.*

class CodeStyleBundle {
    companion object {
        @JvmStatic
        fun getString(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any): String {
            return AbstractBundle.message(bundle, key, *params)
        }

        @JvmStatic
        fun message(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any): String {
            return AbstractBundle.message(bundle, key, *params)
        }

        @JvmStatic
        fun messageOrBlank(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any): String {
            return AbstractBundle.messageOrDefault(bundle, key, "", *params)
        }

        private var ourBundle: Reference<ResourceBundle>? = null

        @NonNls
        internal const val BUNDLE_NAME = "com.vladsch.md.nav.localization.codestyle"

        @JvmStatic
        val bundle: ResourceBundle
            get() {
                var bundle = com.intellij.reference.SoftReference.dereference(ourBundle)

                if (bundle == null) {
                    bundle = ResourceBundle.getBundle(BUNDLE_NAME)
                    ourBundle = SoftReference(bundle)
                }
                return bundle as ResourceBundle
            }
    }
}
