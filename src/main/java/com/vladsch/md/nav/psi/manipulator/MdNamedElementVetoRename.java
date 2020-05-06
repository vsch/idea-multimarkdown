// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.manipulator;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.psi.element.MdNamedElement;

public class MdNamedElementVetoRename implements Condition<PsiElement> {
    @Override
    public boolean value(final PsiElement element) {
        return element instanceof MdNamedElement && !((MdNamedElement) element).isRenameAvailable();
    }
}
