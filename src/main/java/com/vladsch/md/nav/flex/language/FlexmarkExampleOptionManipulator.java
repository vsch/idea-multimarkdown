// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption;
import org.jetbrains.annotations.NotNull;

public class FlexmarkExampleOptionManipulator implements ElementManipulator<FlexmarkExampleOption> {
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
    public FlexmarkExampleOption handleContentChange(@NotNull FlexmarkExampleOption element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, newContent);
    }

    @Override
    public FlexmarkExampleOption handleContentChange(@NotNull FlexmarkExampleOption element, String newContent) throws IncorrectOperationException {
        return (FlexmarkExampleOption) element.setName(newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull FlexmarkExampleOption element) {
        return new TextRange(0, element.getOptionName().length());
    }
}
