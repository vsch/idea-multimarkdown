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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.language.MultiMarkdownReference;
import org.jetbrains.annotations.NotNull;

public interface MultiMarkdownNamedElement extends PsiNameIdentifierOwner, Navigatable {
    int RENAME_NO_FLAGS = -1;

    int RENAME_KEEP_NOTHING = 0;
    int RENAME_KEEP_PATH = 1;
    int RENAME_KEEP_ANCHOR = 2;
    int RENAME_KEEP_NAME = 4;
    int RENAME_KEEP_TITLE = 8;

    int REASON_FILE_RENAMED = RENAME_KEEP_PATH | RENAME_KEEP_ANCHOR;
    int REASON_FILE_MOVED = RENAME_KEEP_ANCHOR;

    String getDisplayName();

    // this one will only change the name part, not the path part of the link
    @Override
    PsiElement setName(@NotNull String newName);

    // this one will preserve the path and only change the name unless fileMoved is true
    PsiElement setName(@NotNull String newName, int reason);

    @NotNull
    String getMissingElementNamespace();

    @Override
    PsiElement getNameIdentifier();

    ItemPresentation getPresentation();

    MultiMarkdownNamedElement handleContentChange(@NotNull TextRange range, String newContent) throws IncorrectOperationException;
    MultiMarkdownNamedElement handleContentChange(String newContent) throws IncorrectOperationException;

    MultiMarkdownReference createReference(@NotNull TextRange textRange);

    boolean isInplaceRenameAvailable(PsiElement context);
    boolean isMemberInplaceRenameAvailable(PsiElement context);
}
