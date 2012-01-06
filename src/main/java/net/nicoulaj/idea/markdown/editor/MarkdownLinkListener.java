/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.ex.http.HttpFileSystem;
import com.intellij.ui.BrowserHyperlinkListener;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static net.nicoulaj.idea.markdown.editor.MarkdownPathResolver.*;

/**
 * <p>MarkdownLinkListener is able to resolve the following types of links and open them in a new editor window:</p>
 * <ul>
 * <li>Local absolute paths e.g. <code>/absolute/path/to/file</code>
 * <li>local relative paths, e.g. <code>./some/relative/file</code>, <code>../../some/other/file</code>
 * <li>references to classes within the project, e.g. <code>net.nicoulaj.idea.markdown.editor.MarkdownLinkListener</code>
 * </ul>
 * <p/>
 * <p>MarkdownLinkListener will attempt to open non-local resources in a web browser.</p>
 *
 * @author Roger Grantham
 */
public class MarkdownLinkListener implements HyperlinkListener {

    final BrowserHyperlinkListener browserLinkListener = new BrowserHyperlinkListener();

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
            // try to get a URL from the event
            URL target = e.getURL();
            if (target == null) {
                try {
                    target = new File(e.getDescription()).toURI().toURL();
                } catch (MalformedURLException e1) {
                    // can't find the link target, give up...
                    return;
                }
            }
            VirtualFileSystem vfs = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
            if (vfs instanceof HttpFileSystem) {
                browserLinkListener.hyperlinkUpdate(e);
            } else {
                final AsyncResult<DataContext> dataContext = DataManager.getInstance().getDataContextFromFocus();
                final Project project = DataKeys.PROJECT.getData(dataContext.getResult());
                VirtualFile virtualTarget = findVirtualFile(target);
                if (virtualTarget == null || !virtualTarget.exists()) {
                    virtualTarget = resolveRelativePath(e.getDescription());
                }

                if (virtualTarget == null) { // Okay, try as if the link target is a class reference
                    virtualTarget = resolveClassReference(e.getDescription());
                }

                if (virtualTarget != null) {
                    FileEditorManager.getInstance(project).openFile(virtualTarget, true);
                }
            }
        }
    }
}
