// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.intentions

import com.intellij.BundleBase
import com.vladsch.plugin.util.suffixWith
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.util.*

class IntentionsBundle : BundleBase() {
    companion object {

        @JvmStatic
        fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
            if (key.endsWith(".family.name")) {
                return BundleBase.messageOrDefault(bundle, key, null, *params)
                    ?: BundleBase.message(bundle, key.removeSuffix(".family.name").suffixWith(".name"), *params)
            } else {
                return BundleBase.message(bundle, key, *params)
            }
        }

        private var ourBundle: Reference<ResourceBundle>? = null

        @NonNls
        internal const val BUNDLE = "com.vladsch.md.nav.localization.intentions"

        internal val bundle: ResourceBundle
            get() {
                var bundle = com.intellij.reference.SoftReference.dereference(ourBundle)

                if (bundle == null) {
                    bundle = ResourceBundle.getBundle(BUNDLE)
                    ourBundle = SoftReference<ResourceBundle>(bundle)
                }
                return bundle as ResourceBundle
            }
    }
}
