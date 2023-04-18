// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.psi.element.MdAttributeIdValue;
import org.jetbrains.annotations.NotNull;

public class MdAttributeIdValueManipulator implements MdElementManipulator<MdAttributeIdValue> {
    @Override
    public MdAttributeIdValue handleContentChange(@NotNull MdAttributeIdValue element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        if (!range.equalsToRange(0, element.getTextLength())) {
            return element.setType(newContent, 0);
        }
        return handleContentChange(element, newContent);
    }

    @Override
    public MdAttributeIdValue handleContentChange(@NotNull MdAttributeIdValue element, String newContent) throws IncorrectOperationException {
        return (MdAttributeIdValue) element.handleContentChange(newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull MdAttributeIdValue element) {
        return new TextRange(0, element.getTextLength());
    }
}
