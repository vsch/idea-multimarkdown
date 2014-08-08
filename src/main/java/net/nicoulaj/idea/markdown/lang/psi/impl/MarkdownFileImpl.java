/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.lang.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import net.nicoulaj.idea.markdown.file.MarkdownFileType;
import net.nicoulaj.idea.markdown.lang.psi.api.MarkdownFile;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.ex.EditorSettingsExternalizable.STRIP_TRAILING_SPACES_NONE;
import static com.intellij.openapi.editor.impl.TrailingSpacesStripper.OVERRIDE_STRIP_TRAILING_SPACES_KEY;

/**
 * Implementation of {@link MarkdownFile}.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownFileImpl extends PsiFileBase implements MarkdownFile {

    /**
     * Build a new instance of {@link MarkdownFileImpl}.
     *
     * @param viewProvider the {@link FileViewProvider} associated with this file.
     */
    public MarkdownFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, MarkdownFileType.LANGUAGE);
    }

    /**
     * Get the file type for the file.
     *
     * @return {@link MarkdownFileType#INSTANCE}
     */
    @NotNull
    public FileType getFileType() {
        return MarkdownFileType.INSTANCE;
    }

    @Override
    public VirtualFile getVirtualFile() {
        final VirtualFile file =  super.getVirtualFile();

        // #138: ignore "strip trailing white space" setting
        if (file != null)
            file.putUserData(OVERRIDE_STRIP_TRAILING_SPACES_KEY, STRIP_TRAILING_SPACES_NONE);

        return file;
    }
}
