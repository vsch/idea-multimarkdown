// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.psi.stubs.StubElement
import com.vladsch.md.nav.psi.util.MdTypes

class MdExplicitLinkStubImpl(parent: StubElement<*>, linkRefWithAnchor: String) :
    MdLinkElementStubImpl<MdExplicitLink, MdExplicitLinkStub>(parent, MdTypes.EXPLICIT_LINK, linkRefWithAnchor),
    MdExplicitLinkStub
