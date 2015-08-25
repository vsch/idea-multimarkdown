/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.ui.components.JBScrollPane;

import com.vladsch.idea.multimarkdown.MarkdownBundle;
import com.vladsch.idea.multimarkdown.settings.MarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MarkdownGlobalSettingsListener;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.PegDownProcessor;

import javax.swing.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.Timer;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownPreviewEditor extends UserDataHolderBase implements FileEditor {

    private static final Logger LOGGER = Logger.getInstance(MarkdownPreviewEditor.class);

    public static final String PREVIEW_EDITOR_NAME = MarkdownBundle.message("multimarkdown.preview-tab-name");

    public static final String TEXT_EDITOR_NAME = MarkdownBundle.message("multimarkdown.html-tab-name");

    @NonNls
    public static final String PREVIEW_STYLESHEET_PATH0 = "/com/vladsch/idea/multimarkdown/default.css";

    public static final String PREVIEW_STYLESHEET_PATH1 = "/com/vladsch/idea/multimarkdown/darcula.css";

    /** The {@link java.awt.Component} used to render the HTML preview. */
    protected final JEditorPane jEditorPane = new JEditorPane();

    /** The {@link JBScrollPane} allowing to browse {@link #jEditorPane}. */
    protected final JBScrollPane scrollPane = new JBScrollPane(jEditorPane);

    /** The {@link Document} previewed in this editor. */
    protected final Document document;
    private final Document documentHtml;
    private final Editor editor;

    protected MarkdownGlobalSettingsListener globalSettingsListener;

    /** The {@link PegDownProcessor} used for building the document AST. */
    private ThreadLocal<PegDownProcessor> processor = initProcessor();

    private boolean isActive = false;

    private boolean isRawHtml = false;

    private boolean isEditorTabVisible = true;

    private Project project;

    public static boolean isShowModified() {
        return MarkdownGlobalSettings.getInstance().showHtmlTextAsModified.getValue();
    }

    public static int getParsingTimeout() {
        return MarkdownGlobalSettings.getInstance().parsingTimeout.getValue();
    }

    public static int getUpdateDelay() {
        return MarkdownGlobalSettings.getInstance().updateDelay.getValue();
    }

    public static boolean isTaskLists() {
        return MarkdownGlobalSettings.getInstance().taskLists.getValue();
    }

    public static String getCustomCss() {
        return MarkdownGlobalSettings.getInstance().customCss.getValue();
    }

    public static boolean isShowHtmlText() {
        return MarkdownGlobalSettings.getInstance().showHtmlText.getValue();
    }

    /** Init/reinit thread local {@link PegDownProcessor}. */
    private static ThreadLocal<PegDownProcessor> initProcessor() {
        return new ThreadLocal<PegDownProcessor>() {
            @Override protected PegDownProcessor initialValue() {
                return new PegDownProcessor(MarkdownGlobalSettings.getInstance().getExtensionsValue(), getParsingTimeout());
            }
        };
    }

    /** Indicates whether the HTML preview is obsolete and should regenerated from the Markdown {@link #document}. */
    protected boolean previewIsObsolete = true;

    protected Timer updateDelayTimer;

    protected void updateEditorTabIsVisible() {
        if (isRawHtml) {
            isEditorTabVisible = isShowHtmlText();
            getComponent().setVisible(isEditorTabVisible);
        } else {
            isEditorTabVisible = true;
        }
    }

    /**
     * Build a new instance of {@link MarkdownPreviewEditor}.
     *
     * @param project  the {@link Project} containing the document
     * @param document the {@link com.intellij.openapi.editor.Document} previewed in this editor.
     */
    public MarkdownPreviewEditor(@NotNull Project project, @NotNull Document document, boolean isRawHtml) {
        this.isRawHtml = isRawHtml;
        this.document = document;
        this.project = project;

        // Listen to the document modifications.
        this.document.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                delayedHtmlPreviewUpdate(false);
            }
        });

        // Listen to settings changes
        MarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MarkdownGlobalSettings newSettings) {
                updateEditorTabIsVisible();
                delayedHtmlPreviewUpdate(true);
            }
        });

        if (isRawHtml) {
            // TODO: create a standard IntelliJ editor to display HTML so that we get all the goodies for free
            // just as soon as I can figure out how to create a temp VirtualFile to use for the document and make it readonly
            //EditorFactory editorFactory = EditorFactory.getInstance();
            //documentHtml = editorFactory.createDocument("");
            //FileType test = findHtmlFileType();
            //editor = editorFactory.createEditor(document, project, test, true);
            //jEditorPane.add(editor.getComponent());

            //documentHtml.setReadOnly(true);
            documentHtml = null;
            editor = null;
            jEditorPane.setEditable(false);
        } else {
            // Setup the editor pane for rendering HTML.
            documentHtml = null;
            editor = null;
            setStyleSheet();

            // Add a custom link listener which can resolve local link references.
            jEditorPane.addHyperlinkListener(new MarkdownLinkListener(jEditorPane, project, document));
            jEditorPane.setEditable(false);

            // Set the editor pane caret position to top left, and do not let it reset it
            jEditorPane.getCaret().setMagicCaretPosition(new Point(0, 0));
            ((DefaultCaret) jEditorPane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
    }

    protected FileType findHtmlFileType() {
        FileType[] fileTypes = FileTypeManager.getInstance().getRegisteredFileTypes();
        for (FileType fileType : fileTypes) {
            String name = fileType.getName();
            //if (name.equals("HTML")) return fileType;
            if (name.equals("Scratch")) {
                return fileType;
            }
        }
        return fileTypes[0];
    }

    protected void delayedHtmlPreviewUpdate(final boolean fullKit) {
        if (updateDelayTimer != null) {
            updateDelayTimer.cancel();
            updateDelayTimer = null;
        }

        if (!isEditorTabVisible)
            return;

        updateDelayTimer = new Timer();
        updateDelayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        previewIsObsolete = true;

                        if (fullKit) {
                            setStyleSheet();
                            processor.remove();     // make it re-initialize when accessed
                        }

                        updateHtmlContent(true);
                    }
                }, ModalityState.any());
            }
        }, getUpdateDelay());
    }

    protected void setStyleSheet() {
        if (isRawHtml) return;

        MarkdownEditorKit htmlKit = new MarkdownEditorKit(document);

        final StyleSheet style = new StyleSheet();

        if (getCustomCss().equals("")) {
            style.importStyleSheet(MarkdownPreviewEditor.class.getResource(
                    MarkdownGlobalSettings.getInstance().htmlTheme.getValue() == 0 ? PREVIEW_STYLESHEET_PATH0 : PREVIEW_STYLESHEET_PATH1));
        } else {
            try {
                style.loadRules(new StringReader(getCustomCss()), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        htmlKit.setStyleSheet(style);

        jEditorPane.setEditorKit(htmlKit);
    }

    /**
     * Get the {@link java.awt.Component} to display as this editor's UI.
     *
     * @return a scrollable {@link JEditorPane}.
     */
    @NotNull
    public JComponent getComponent() {
        return scrollPane;
    }

    /**
     * Get the component to be focused when the editor is opened.
     *
     * @return {@link #scrollPane}
     */
    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return scrollPane;
    }

    /**
     * Get the editor displayable name.
     *
     * @return editor name
     */
    @NotNull
    @NonNls
    public String getName() {
        return isRawHtml ? TEXT_EDITOR_NAME : PREVIEW_EDITOR_NAME;
    }

    /**
     * Get the state of the editor.
     * <p/>
     * Just returns {@link FileEditorState#INSTANCE} as {@link MarkdownPreviewEditor} is stateless.
     *
     * @param level the level.
     * @return {@link FileEditorState#INSTANCE}
     * @see #setState(com.intellij.openapi.fileEditor.FileEditorState)
     */
    @NotNull
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return FileEditorState.INSTANCE;
    }

    /**
     * Set the state of the editor.
     * <p/>
     * Does not do anything as {@link MarkdownPreviewEditor} is stateless.
     *
     * @param state the new state.
     * @see #getState(com.intellij.openapi.fileEditor.FileEditorStateLevel)
     */
    public void setState(@NotNull FileEditorState state) {
    }

    /**
     * Indicates whether the document content is modified compared to its file.
     *
     * @return {@code false} as {@link MarkdownPreviewEditor} is read-only.
     */
    public boolean isModified() {
        return false;
    }

    /**
     * Indicates whether the editor is valid.
     *
     * @return {@code true} if {@link #document} content is readable.
     */
    public boolean isValid() {
        return true;
    }

    /**
     * Invoked when the editor is selected.
     * <p/>
     * Update the HTML content if obsolete.
     */
    public void selectNotify() {
        isActive = true;
        if (previewIsObsolete) {
            updateHtmlContent(false);
        }
    }

    private void updateHtmlContent(boolean force) {
        if (updateDelayTimer != null) {
            updateDelayTimer.cancel();
            updateDelayTimer = null;
        }

        if (previewIsObsolete && isEditorTabVisible && (isActive || force)) {
            try {
                final String html = processor.get().markdownToHtml(document.getText());
                if (isRawHtml) {
                    final String htmlTxt = isShowModified() ? postProcessHtml(html) : html;
                    jEditorPane.setText(htmlTxt);
                } else {
                    jEditorPane.setText(postProcessHtml(html));
                }
                previewIsObsolete = false;

                // here we can find our HTML Text counterpart but it is better to keep it separate for now
                //VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                //FileEditorManager manager = FileEditorManager.getInstance(project);
                //FileEditor[] editors = manager.getEditors(file);
                //for (int i = 0; i < editors.length; i++)
                //{
                //    if (editors[i] == this)
                //    {
                //        if (editors.length > i && editors[i+1] instanceof MarkdownPreviewEditor) {
                //            // update its html too
                //            MarkdownPreviewEditor htmlEditor = (MarkdownPreviewEditor)editors[i+1];
                //            boolean showModified = MarkdownGlobalSettings.getInstance().isShowHtmlTextAsModified();
                //            htmlEditor.setHtmlContent("<div id=\"multimarkdown-preview\">\n" + (showModified ? procHtml : html) + "\n</div>\n");
                //            break;
                //        }
                //    }
                //}
            } catch (Exception e) {
                LOGGER.error("Failed processing Markdown document", e);
            }
        }
    }

    public void setHtmlContent(String html) {
        jEditorPane.setText(html);
    }

    protected String postProcessHtml(String html) {
        // scan for <table>, </table>, <tr>, </tr> and other tags we modify, this could be done with a custom plugin to pegdown but
        // then it would be more trouble to get un-modified HTML.
        String result = "<div id=\"multimarkdown-preview\">\n";
        Pattern p = Pattern.compile("(<table>|<thead>|<tbody>|<tr>|<hr/>|<del>|</del>|<li>\\[x\\]|<li>\\[ \\]|<li>\\n*\\s*<p>\\[x\\]|<li>\\n*\\s*<p>\\[ \\]|<li>\\n*\\s*<p>)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        int lastPos = 0;
        int rowCount = 0;

        while (m.find()) {
            String found = m.group();
            if (lastPos < m.start(0)) {
                result += html.substring(lastPos, m.start(0));
            }

            if (found.equals("<table>")) {
                rowCount = 0;
                result += found;
            } else if (found.equals("<thead>")) {
                result += found;
            } else if (found.equals("<tbody>")) {
                result += found;
            } else if (found.equals("<tr>")) {
                rowCount++;
                result += "<tr class=\"" + (rowCount == 1 ? "first-child" : (rowCount & 1) != 0 ? "odd-child" : "even-child") + "\">";
            } else if (found.equals("<hr/>")) {
                result += "<div class=\"hr\">&nbsp;</div>";
            } else if (found.equals("<del>")) {
                result += "<span class=\"del\">";
            } else if (found.equals("</del>")) {
                result += "</span>";
            } else {
                boolean taskLists = isTaskLists();
                if (taskLists && found.equals("<li>[x]")) {
                    result += "<li class=\"task\"><input type=\"checkbox\" checked=\"checked\" disabled=\"disabled\">";
                } else if (taskLists && found.equals("<li>[ ]")) {
                    result += "<li class=\"task\"><input type=\"checkbox\" disabled=\"disabled\">";
                } else {
                    // here we have <li>\n*\s*<p>, need to strip out \n*\s* so we can match them easier
                    String foundWithP = found;
                    found = foundWithP.replaceAll("<li>\\n*\\s*<p>", "<li><p>");

                    if (found.equals("<li><p>")) {
                        result += "<li class=\"p\"><p class=\"p\">";
                    } else if (taskLists && found.equals("<li><p>[x]")) {
                        result += "<li class=\"taskp\"><p><input type=\"checkbox\" checked=\"checked\" disabled=\"disabled\">";
                    } else if (taskLists && found.equals("<li><p>[ ]")) {
                        result += "<li class=\"taskp\"><p><input type=\"checkbox\" disabled=\"disabled\">";
                    } else {
                        result += found;
                    }
                }
            }

            lastPos = m.end(0);
        }

        if (lastPos < html.length()) {
            result += html.substring(lastPos);
        }

        result += "\n</div>\n";
        return result;
    }

    /**
     * Invoked when the editor is deselected.
     * <p/>
     * Does nothing.
     */
    public void deselectNotify() {
        isActive = false;
    }

    /**
     * Add specified listener.
     * <p/>
     * Does nothing.
     *
     * @param listener the listener.
     */
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    /**
     * Remove specified listener.
     * <p/>
     * Does nothing.
     *
     * @param listener the listener.
     */
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    /**
     * Get the background editor highlighter.
     *
     * @return {@code null} as {@link MarkdownPreviewEditor} does not require highlighting.
     */
    @Nullable
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    /**
     * Get the current location.
     *
     * @return {@code null} as {@link MarkdownPreviewEditor} is not navigable.
     */
    @Nullable
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    /**
     * Get the structure view builder.
     *
     * @return TODO {@code null} as parsing/PSI is not implemented.
     */
    @Nullable
    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    /** Dispose the editor. */
    public void dispose() {
        if (editor != null) {
            //EditorFactory.getInstance().releaseEditor(editor);
        }

        if (globalSettingsListener != null) {
            MarkdownGlobalSettings.getInstance().removeListener(globalSettingsListener);
            globalSettingsListener = null;
        }
        Disposer.dispose(this);
    }
}
