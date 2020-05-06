// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

abstract class InjectHtmlResource(val htmlPlacement: HtmlPlacement, val isByScript: Boolean) {
    abstract fun htmlText(resourceUrl: String?): String
    abstract fun resourceURL(): String?
}
