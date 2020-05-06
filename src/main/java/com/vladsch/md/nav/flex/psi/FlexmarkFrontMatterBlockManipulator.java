// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class FlexmarkFrontMatterBlockManipulator implements ElementManipulator<FlexmarkFrontMatterBlock> {
    /**
     * Changes the element's text to a new value
     *
     * @param element    element to be changed
     * @param range      range within the element
     * @param newContent new element text
     *
     * @return changed element
     *
     * @throws IncorrectOperationException if something goes wrong
     */
    @Override
    public FlexmarkFrontMatterBlock handleContentChange(@NotNull FlexmarkFrontMatterBlock element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, newContent);
    }

    @Override
    public FlexmarkFrontMatterBlock handleContentChange(@NotNull FlexmarkFrontMatterBlock element, String newContent) throws IncorrectOperationException {
        return (FlexmarkFrontMatterBlock) element.setContent(newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull FlexmarkFrontMatterBlock element) {
        return element.getContentRange();
    }
}
