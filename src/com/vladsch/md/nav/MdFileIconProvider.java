/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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
package com.vladsch.md.nav;

import com.intellij.ide.FileIconProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.md.nav.editor.MdPreviewFileEditorProvider;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.util.FileRef;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdFileIconProvider implements FileIconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull VirtualFile file, @Iconable.IconFlags int flags, @Nullable Project project) {
        // diagnostic/4103
        if (project != null && file.getFileType() == MdFileType.INSTANCE) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof MdFile) {
                return psiFile.getIcon(flags);
            }

            if (MdPreviewFileEditorProvider.acceptFile(project, file)) {
                if (new FileRef(file.getPath()).isWikiPage()) return MdIcons.getWikiPageIcon();
                return MdIcons.getDocumentIcon();
            }
        }

        return null;
    }
}
