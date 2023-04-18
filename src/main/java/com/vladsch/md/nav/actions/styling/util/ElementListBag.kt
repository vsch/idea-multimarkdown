// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling.util

import com.intellij.psi.PsiElement
import com.vladsch.plugin.util.ArrayListBag
import java.util.function.Function

class ElementListBag<T : Any>(mapper: Function<PsiElement, T>) : ArrayListBag<PsiElement, T>(mapper)
