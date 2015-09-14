/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Static utilities for resolving resources paths.
 *
 * @author Roger Grantham (https://github.com/grantham)
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.8
 */
public class MultiMarkdownPathResolver {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownPathResolver.class);

    /** Not to be instantiated. */
    private MultiMarkdownPathResolver() {
        // no op
    }

    /**
     * Makes a simple attempt to convert the URL into a VirtualFile.
     *
     * @param target url from which a VirtualFile is sought
     *
     * @return VirtualFile or null
     */
    public static VirtualFile findVirtualFile(@NotNull URL target) {
        VirtualFileSystem virtualFileSystem = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
        return virtualFileSystem == null ? null : virtualFileSystem.findFileByPath(target.getFile());
    }

    /**
     * Interprets <var>target</var> as a path relative to the given document.
     *
     * @param document the document
     * @param target   relative path from which a VirtualFile is sought
     *
     * @return VirtualFile or null
     */
    public static VirtualFile resolveRelativePath(@NotNull Document document, @NotNull String target) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        VirtualFile parent = file == null ? null : file.getParent();
        return parent == null ? null : parent.findFileByRelativePath(target);
    }

    /**
     * Interprets <var>target</var> as a class reference.
     *
     * @param project the project to look for files in
     * @param target  from which a VirtualFile is sought
     *
     * @return VirtualFile or null
     */
    public static VirtualFile resolveClassReference(@NotNull Project project, @NotNull String target) {
        final PsiClass classpathResource = JavaPsiFacade.getInstance(project).findClass(target, GlobalSearchScope.projectScope(project));
        if (classpathResource != null)
            return classpathResource.getContainingFile().getVirtualFile();
        return null;
    }

    public static boolean resolveLink(@NotNull final Project project, @NotNull final Document document, @NotNull final String href) {
        return resolveLink(project, document, href, false,false,false);
    }

    public static boolean isWikiDocument(@NotNull final Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        while (file != null) {
            file = file.getParent();
            if (file != null && file.getCanonicalPath().endsWith(".wiki")) return true;
        }
        return false;
    }

    public static boolean resolveLink(@NotNull final Project project, @NotNull final Document document, @NotNull final String href, final boolean openFile, final boolean focusEditor, final boolean searchForOpen) {
        if (!href.startsWith("http://") && !href.startsWith("https://") && !href.startsWith("mailto:")) {
            final boolean[] foundFile = {false};
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    VirtualFile virtualTarget = null;

                    if (href.startsWith("file:")) {
                        try {
                            URL target = new URL(href);
                            virtualTarget = findVirtualFile(target);
                        } catch (MalformedURLException e) {
                            //e.printStackTrace();
                        }
                    }

                    if (virtualTarget == null || !virtualTarget.exists()) {
                        virtualTarget = resolveRelativePath(document, href);
                    }

                    try {
                        if (virtualTarget == null) { // Okay, try as if the link target is a class reference
                            virtualTarget = resolveClassReference(project, href);
                        }
                    } catch (NoClassDefFoundError silent) {
                        // API might not be available on all IntelliJ platform IDEs
                    }

                    foundFile[0] = virtualTarget != null;
                    if (foundFile[0] && openFile) {
                        FileEditorManager.getInstance(project).openFile(virtualTarget, focusEditor, searchForOpen);
                    }
                }
            };

            Application application = ApplicationManager.getApplication();
            if (application.isDispatchThread()) {
                runnable.run();
                return foundFile[0];
            } else {
                application.invokeLater(runnable, ModalityState.any());

                // we don't know so we guess?
                return true;
            }
        } else {
            if (Desktop.isDesktopSupported()) {
                try {
                    if (openFile) Desktop.getDesktop().browse(new URI(href));
                    return true;
                } catch (URISyntaxException ex) {
                    // invalid URI, just log
                    logger.info("URISyntaxException on '" + href + "'" + ex.toString());
                } catch (IOException ex) {
                    logger.info("IOException on '" + href + "'" + ex.toString());
                }
            }
            return false;
        }
    }
}
