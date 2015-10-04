/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownNamedElementManipulator implements ElementManipulator<MultiMarkdownNamedElement> {
    @Override
    public MultiMarkdownNamedElement handleContentChange(@NotNull MultiMarkdownNamedElement element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        if (!range.equalsToRange(0, element.getTextLength())) {
            throw new IncorrectOperationException();
        }
        return handleContentChange(element, newContent);
    }

    @Override
    public MultiMarkdownNamedElement handleContentChange(@NotNull MultiMarkdownNamedElement element, String newContent) throws IncorrectOperationException {
        return (MultiMarkdownNamedElement) element.handleContentChange(newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull MultiMarkdownNamedElement element) {
        return new TextRange(0, element.getTextLength());
    }
}
