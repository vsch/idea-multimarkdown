// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language;

import com.intellij.codeInsight.highlighting.PairedBraceMatcherAdapter;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.psi.util.MdTypes;
import org.jetbrains.annotations.NotNull;

public class MdBraceMatcher extends PairedBraceMatcherAdapter {

    public MdBraceMatcher() {
        super(new MyPairedBraceMatcher(), MdLanguage.INSTANCE);
    }

    private static class MyPairedBraceMatcher implements PairedBraceMatcher {

        MyPairedBraceMatcher() {}

        @NotNull
        @Override
        public BracePair[] getPairs() {
            return new BracePair[] {
                    new BracePair(MdTypes.VERBATIM_OPEN, MdTypes.VERBATIM_CLOSE, false),
                    new BracePair(MdTypes.JEKYLL_FRONT_MATTER_OPEN, MdTypes.JEKYLL_FRONT_MATTER_CLOSE, true),
                    new BracePair(MdTypes.FLEXMARK_FRONT_MATTER_OPEN, MdTypes.FLEXMARK_FRONT_MATTER_CLOSE, true)
            };
        }

        @Override
        public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, IElementType contextType) {
            return true;
        }

        @Override
        public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
            return openingBraceOffset;
        }
    }
}
