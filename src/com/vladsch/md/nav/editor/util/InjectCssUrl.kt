// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

class InjectCssUrl(htmlPlacement: HtmlPlacement, isByScript: Boolean, val resourceURL: String) : InjectHtmlResource(htmlPlacement, isByScript) {
    override fun htmlText(resourceUrl: String?): String = "<link rel=\"stylesheet\" href=\"${resourceUrl ?: resourceURL}\">\n"
    override fun resourceURL(): String? = resourceURL
}
