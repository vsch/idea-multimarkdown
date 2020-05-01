// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.vladsch.md.nav.util.looping.MdPsiIterator;
import com.vladsch.plugin.util.psi.TreeIteratorConstrains;

public interface MdPsiElement extends PsiElement {
    default MdPsiIterator<PsiElement> childLooping() {
        return MdPsiIterator.of(this, TreeIteratorConstrains.PSI.getIterateChildren());
    }

    default MdPsiIterator<PsiElement> siblingsLooping() {
        return MdPsiIterator.of(this, TreeIteratorConstrains.PSI.getIterateSiblings());
    }

    default MdFile getMdFile() throws PsiInvalidElementAccessException {
        return (MdFile) getContainingFile();
    }
}
