/*
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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MultiMarkdownPathResolver {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownPathResolver.class);

    private MultiMarkdownPathResolver() {

    }

    public static boolean isWikiDocument(@NotNull final Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        return file != null && new FileRef(file).isWikiPage();
    }

    public static void openLink(@NotNull String href) {
        if (Desktop.isDesktopSupported()) {
            try {
                Object foundFile = new URI(href);
                Desktop.getDesktop().browse((URI) foundFile);
            } catch (URISyntaxException ex) {
                // invalid URI, just log
                logger.info("URISyntaxException on '" + href + "'" + ex.toString());
            } catch (IOException ex) {
                logger.info("IOException on '" + href + "'" + ex.toString());
            }
        }
    }

    @Nullable
    public static String getGitHubDocumentURL(@NotNull Project project, @NotNull Document document, boolean withExtension) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        return getGitHubFileURL(virtualFile, project, withExtension, null);
    }

    @Nullable
    public static String getGitHubFileURL(VirtualFile virtualFile, @NotNull Project project, boolean withExtension, @Nullable String anchor) {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);

        if (projectComponent != null) {
            if (virtualFile != null && projectComponent.isUnderVcs(virtualFile)) {
                GitHubVcsRoot gitHubVcsRoot = projectComponent.getGitHubRepo(new PathInfo(virtualFile.getPath()).getPath());
                if (gitHubVcsRoot != null) {
                    return gitHubVcsRoot.urlForVcsRemote(virtualFile, withExtension, anchor, null, null);
                }
            }
        }
        return null;
    }

    public static void launchExternalLink(@NotNull final Project project, @NotNull final String href) {
        if (PathInfo.isExternal(href)) {
            openLink(href);
        } else if (href.startsWith("file://")) {
            try {
                URL target = new URL(href);
                VirtualFileSystem virtualFileSystem = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
                final VirtualFile virtualFile = virtualFileSystem == null ? null : virtualFileSystem.findFileByPath(target.getFile());
                // open local file
                if (virtualFile != null) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            FileEditorManager.getInstance(project).openFile(virtualFile, true, true);
                            // TODO: see if we can resolve the #hashSuffix in the file
                            //logger.info("got hash suffixed href: " + href + "#" + hashSuffix);
                        }
                    });
                }
            } catch (MalformedURLException ignored) {
            }
        }
    }
}
