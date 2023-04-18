// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.psi.element.MdHtmlBlock;
import org.jetbrains.annotations.NotNull;

public class MdHtmlBlockManipulator implements MdElementManipulator<MdHtmlBlock> {
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
    public MdHtmlBlock handleContentChange(@NotNull MdHtmlBlock element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, newContent);
    }

    @Override
    public MdHtmlBlock handleContentChange(@NotNull MdHtmlBlock element, String newContent) throws IncorrectOperationException {
        return (MdHtmlBlock) element.setContent(newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull MdHtmlBlock element) {
        return element.getContentRange();
    }
}
