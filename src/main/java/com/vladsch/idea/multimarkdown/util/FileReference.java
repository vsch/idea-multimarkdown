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
package com.vladsch.idea.multimarkdown.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileReference {

    protected final FilePathInfo filePathInfo;
    protected final Project project;

    public FileReference(@NotNull String filePath, Project project) {
        this.filePathInfo = new FilePathInfo(filePath);
        this.project = project;
    }

    @NotNull
    public FilePathInfo getFilePathInfo() {
        return filePathInfo;
    }

    public Project getProject() {
        return project;
    }

    @Nullable
    public VirtualFile getSourceVirtualFile() {
        return FileReference.getVirtualFile(filePathInfo.getFilePath(), project);
    }

    @Nullable
    public PsiFile getSourcePsiFile() {
        return FileReference.getPsiFile(filePathInfo.getFilePath(), project);
    }

    @Nullable
    public static VirtualFile getVirtualFile(@NotNull String sourcePath, @NotNull Project project) {
        String baseDir = project.getBasePath();
        if (baseDir != null && sourcePath.startsWith(baseDir + "/")) {
            return VirtualFileManager.getInstance().findFileByUrl("file:" + sourcePath);
        }
        return null;
    }

    @Nullable
    public static PsiFile getPsiFile(@NotNull String sourcePath, @NotNull Project project) {
        VirtualFile file = getVirtualFile(sourcePath, project);
        if (file != null) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                return (MultiMarkdownFile) psiFile;
            }
        }
        return null;
    }

    // delegated FilePathInfo methods for convenience

    @NotNull
    public String getPath() {return filePathInfo.getPath();}

    @NotNull
    public String getFileNameNoExtAsWikiRef() {return filePathInfo.getFileNameNoExtAsWikiRef();}

    @NotNull
    public String getFilePathNoExt() {return filePathInfo.getFilePathNoExt();}

    @NotNull
    public String getFilePathNoExtAsWikiRef() {return filePathInfo.getFilePathNoExtAsWikiRef();}

    @NotNull
    public String getFilePath() {return filePathInfo.getFilePath();}

    public boolean hasWikiPageExt() {return filePathInfo.hasWikiPageExt();}

    public boolean isWikiHome() {return filePathInfo.isWikiHome();}

    @NotNull
    public String getFilePathAsWikiRef() {return filePathInfo.getFilePathAsWikiRef();}

    public boolean fileNameContainsSpaces() {return filePathInfo.fileNameContainsSpaces();}

    @NotNull
    public String getExt() {return filePathInfo.getExt();}

    @NotNull
    public String getFileNameNoExt() {return filePathInfo.getFileNameNoExt();}

    public boolean pathContainsSpaces() {return filePathInfo.pathContainsSpaces();}

    @NotNull
    public String getFileNameAsWikiRef() {return filePathInfo.getFileNameAsWikiRef();}

    public boolean isUnderWikiHome() {return filePathInfo.isUnderWikiHome();}

    @NotNull
    public String getFileName() {return filePathInfo.getFileName();}

    @NotNull
    public String getWikiHome() {return filePathInfo.getWikiHome();}

    public boolean containsSpaces() {return filePathInfo.containsSpaces();}

    @NotNull
    public String getPathAsWikiRef() {return filePathInfo.getPathAsWikiRef();}
}
