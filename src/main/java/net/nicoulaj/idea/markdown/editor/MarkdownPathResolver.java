/*
 * Copyright (c) 2011-2012 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

/**
 * Static utilities for resolving resources paths.
 *
 * @author Roger Grantham (https://github.com/grantham)
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.8
 */
public class MarkdownPathResolver {

    /**
     * Not to be instantiated.
     */
    private MarkdownPathResolver() {
        // no op
    }

    /**
     * Makes a simple attempt to convert the URL into a VirtualFile.
     *
     * @param target url from which a VirtualFile is sought
     * @return VirtualFile or null
     */
    public static VirtualFile findVirtualFile(@NotNull URL target) {
        return VirtualFileManager.getInstance().getFileSystem(target.getProtocol()).findFileByPath(target.getFile());
    }

    /**
     * Interprets <var>target</var> as a path relative to the given document.
     *
     * @param document the document
     * @param target   relative path from which a VirtualFile is sought
     * @return VirtualFile or null
     */
    public static VirtualFile resolveRelativePath(@NotNull Document document, @NotNull String target) {
        return FileDocumentManager.getInstance().getFile(document).getParent().findFileByRelativePath(target);
    }

    /**
     * Interprets <var>target</var> as a class reference.
     *
     * @param project the project to look for files in
     * @param target  from which a VirtualFile is sought
     * @return VirtualFile or null
     */
    public static VirtualFile resolveClassReference(@NotNull Project project, @NotNull String target) {
        final PsiClass classpathResource = JavaPsiFacade.getInstance(project).findClass(target, GlobalSearchScope.projectScope(project));
        if (classpathResource != null)
            return classpathResource.getContainingFile().getVirtualFile();
        return null;
    }
}
