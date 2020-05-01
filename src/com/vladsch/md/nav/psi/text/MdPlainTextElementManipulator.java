// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.text;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class MdPlainTextElementManipulator implements ElementManipulator<MdPlainTextElementImpl> {
    @Override
    public MdPlainTextElementImpl handleContentChange(@NotNull MdPlainTextElementImpl element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        if (!range.equalsToRange(0, element.getTextLength())) {
            throw new IncorrectOperationException();
        }
        return handleContentChange(element, newContent);
    }

    @Override
    public MdPlainTextElementImpl handleContentChange(@NotNull MdPlainTextElementImpl element, String newContent) throws IncorrectOperationException {
        return element.setName(newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull MdPlainTextElementImpl element) {
        return new TextRange(element.getStartOffsetInParent(), element.getStartOffsetInParent() + element.getTextLength());
    }
}
