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
package com.vladsch.idea.multimarkdown;

import com.intellij.ide.FileIconProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.idea.multimarkdown.editor.MultiMarkdownHtmlEditorProvider;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MultiMarkdownFileIconProvider implements FileIconProvider {
    @Nullable @Override public Icon getIcon(@NotNull VirtualFile file, @Iconable.IconFlags int flags, @Nullable Project project) {
        if (MultiMarkdownHtmlEditorProvider.accept(file)) {
            return MultiMarkdownProjectComponent.isWikiPage(file) ? MultiMarkdownIcons.WIKI : MultiMarkdownIcons.FILE;
        }
        return null;
    }
}
