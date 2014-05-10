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
package net.nicoulaj.idea.markdown.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.ex.http.HttpFileSystem;
import com.intellij.ui.BrowserHyperlinkListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static net.nicoulaj.idea.markdown.editor.MarkdownPathResolver.*;

/**
 * {@link MarkdownLinkListener} is able to resolve the following types of links and open them in a new editor window:
 * <ul>
 * <li>local absolute paths e.g. <code>/absolute/path/to/file</code>
 * <li>local relative paths, e.g. <code>./some/relative/file</code>, <code>../../some/other/file</code>
 * <li>references to classes within the project, e.g. <code>net.nicoulaj.idea.markdown.editor.MarkdownLinkListener</code>
 * </ul>
 * <p/>
 * MarkdownLinkListener will attempt to open non-local resources in a web browser.
 *
 * @author Roger Grantham (https://github.com/grantham)
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.8
 */
public class MarkdownLinkListener implements HyperlinkListener {

    /** The {@link BrowserHyperlinkListener} used for non-local resources. */
    private final BrowserHyperlinkListener browserLinkListener = new BrowserHyperlinkListener();

    /** The editor. */
    private final JEditorPane editor;

    /** The project. */
    private final Project project;

    /** The document. */
    private final Document document;

    /**
     * Build a new instance of {@link MarkdownLinkListener}.
     *
     * @param editor the editor
     * @param project  the project
     * @param document the document
     */
    public MarkdownLinkListener(@NotNull JEditorPane editor, @NotNull Project project, @NotNull Document document) {
        this.editor = editor;
        this.project = project;
        this.document = document;
    }

    /**
     * Handle hypertext link update.
     * <p/>
     * Tries to resolve the target as a local resource, else delegate to {@link #browserLinkListener}.
     *
     * @param e the event responsible for the update
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
            // Try to get a URL from the event.
            URL target = e.getURL();
            if (target == null) {
                if (e.getDescription().startsWith("#")) {
                    editor.scrollToReference(e.getDescription().substring(1));
                    return;
                }
                try {
                    target = new File(e.getDescription()).toURI().toURL();
                } catch (MalformedURLException e1) {
                    // can't find the link target, give up...
                    return;
                }
            }
            if (VirtualFileManager.getInstance().getFileSystem(target.getProtocol()) instanceof HttpFileSystem) {
                browserLinkListener.hyperlinkUpdate(e);
            } else {
                VirtualFile virtualTarget = findVirtualFile(target);
                if (virtualTarget == null || !virtualTarget.exists())
                    virtualTarget = resolveRelativePath(document, e.getDescription());

                try {
                    if (virtualTarget == null) // Okay, try as if the link target is a class reference
                        virtualTarget = resolveClassReference(project, e.getDescription());
                } catch (NoClassDefFoundError silent) {
                    // API might not be available on all IntelliJ platform IDEs
                }

                if (virtualTarget != null)
                    FileEditorManager.getInstance(project).openFile(virtualTarget, true);
            }
        }
    }
}
