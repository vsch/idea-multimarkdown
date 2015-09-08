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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.Language;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import com.sun.webkit.dom.HTMLImageElementImpl;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.LinkRenderer;
import org.pegdown.ParsingTimeoutException;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.codec.binary.Base64;

public class MultiMarkdownFxPreviewEditor extends UserDataHolderBase implements FileEditor {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownFxPreviewEditor.class);

    private static final Logger LOGGER = Logger.getInstance(MultiMarkdownFxPreviewEditor.class);

    public static final String PREVIEW_EDITOR_NAME = MultiMarkdownBundle.message("multimarkdown.preview-tab-name");

    public static final String TEXT_EDITOR_NAME = MultiMarkdownBundle.message("multimarkdown.html-tab-name");

    /** The {@link Component} used to render the HTML preview. */
    protected final JPanel jEditorPane;
    private WebView webView;
    private WebEngine webEngine;
    private final JFXPanel jfxPanel;
    private String scrollOffset = null;
    private RootNode astRoot = null;
    private AnchorPane anchorPane;

    /** The {@link JBScrollPane} allowing to browse {@link #jEditorPane}. */
    protected final JBScrollPane scrollPane;

    /** The {@link Document} previewed in this editor. */
    protected final Document document;
    //private final EditorTextField myTextViewer;
    private final EditorImpl myTextViewer;

    private boolean isReleased = false;

    protected MultiMarkdownGlobalSettingsListener globalSettingsListener;

    /** The {@link PegDownProcessor} used for building the document AST. */
    private ThreadLocal<PegDownProcessor> processor = initProcessor();

    private boolean isActive = false;

    private boolean isRawHtml = false;

    private boolean isEditorTabVisible = true;

    private Project project;

    private LinkRenderer linkRendererNormal;
    private LinkRenderer linkRendererModified;
    private String pageScript = null;
    private boolean needStyleSheetUpdate;

    public static boolean isShowModified() {
        return MultiMarkdownGlobalSettings.getInstance().showHtmlTextAsModified.getValue();
    }

    public static int getParsingTimeout() {
        return MultiMarkdownGlobalSettings.getInstance().parsingTimeout.getValue();
    }

    public static int getUpdateDelay() {
        return MultiMarkdownGlobalSettings.getInstance().updateDelay.getValue();
    }

    public static boolean isTaskLists() {
        return MultiMarkdownGlobalSettings.getInstance().taskLists.getValue();
    }

    public static boolean isDarkTheme() {
        return MultiMarkdownGlobalSettings.getInstance().isDarkUITheme();
    }

    public static String getCustomCss() {
        return MultiMarkdownGlobalSettings.getInstance().customFxCss.getValue();
    }

    public static boolean isShowHtmlText() {
        return MultiMarkdownGlobalSettings.getInstance().showHtmlText.getValue();
    }

    /** Init/reinit thread local {@link PegDownProcessor}. */
    private static ThreadLocal<PegDownProcessor> initProcessor() {
        return new ThreadLocal<PegDownProcessor>() {
            @Override protected PegDownProcessor initialValue() {
                // ISSUE: #7 worked around, disable pegdown TaskList HTML rendering, they don't display well in Darcula.
                return new PegDownProcessor(MultiMarkdownGlobalSettings.getInstance().getExtensionsValue() /*& ~Extensions.TASKLISTITEMS*/, getParsingTimeout());
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

    protected void checkNotifyUser() {
        //final Project project = this.project;
        //final MultiMarkdownGlobalSettings settings = MultiMarkdownGlobalSettings.getInstance();
        //
        //settings.startSuspendNotifications();
        //if (settings.isDarkUITheme() && (settings.iconBullets.getValue() || settings.iconTasks.getValue()) && true && !settings.wasShownDarkBug.getValue()) {
        //    // notify the user that the Icons for Tasks and Bullets will be turned off due to a rendering bug
        //    settings.wasShownDarkBug.setValue(true);
        //    NotificationGroup issueNotificationGroup = new NotificationGroup(MultiMarkdownGlobalSettings.NOTIFICATION_GROUP_ISSUES,
        //            NotificationDisplayType.BALLOON, true, null);
        //
        //    Notification notification = issueNotificationGroup.createNotification("<strong>MultiMarkdown</strong> Plugin Notification",
        //            "<p>An issue with rendering icons when the UI theme is <strong>Darcula</strong> prevents bullet "+
        //                    "and task list items from using these options. " +
        //                    "These settings will be ignored while <strong>Darcula</strong> "+
        //                    "theme is in effect and until the issue is fixed.</p>\n" +
        //                    "<p>&nbsp;</p>\n" +
        //                    "<p>Feel free leave the <em>Bullets with Icons</em> and <em>Tasks with Icons</em> options turned on. "+
        //                    "They will take effect when they no longer adversely affect the display.</p>\n" +
        //                    "",
        //            NotificationType.INFORMATION, null);
        //    notification.setImportant(true);
        //    Notifications.Bus.notify(notification, project);
        //}
        //settings.endSuspendNotifications();
    }

    private class MyJFXPanel extends JFXPanel {
        @Override public void addNotify() {
            super.addNotify();
            //Dimension dimension = getParent().getPreferredSize();
            //anchorPane.resize(dimension.getWidth(), dimension.getHeight());
            return;
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            return;
        }
    }

    /**
     * Build a new instance of {@link MultiMarkdownFxPreviewEditor}.
     *
     * @param project the {@link Project} containing the document
     * @param doc     the {@link Document} previewed in this editor.
     */
    public MultiMarkdownFxPreviewEditor(@NotNull final Project project, @NotNull Document doc, boolean isRawHtml) {
        this.isRawHtml = isRawHtml;
        this.document = doc;
        this.project = project;

        // Listen to the document modifications.
        this.document.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                delayedHtmlPreviewUpdate(false);
            }
        });

        // Listen to settings changes
        MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
                updateEditorTabIsVisible();
                delayedHtmlPreviewUpdate(true);
                checkNotifyUser();
            }
        });

        linkRendererModified = new MultiMarkdownFxLinkRenderer();
        linkRendererNormal = new MultiMarkdownFxLinkRenderer();

        if (isRawHtml) {
            jEditorPane = null;
            scrollPane = null;
            jfxPanel = null;
            webView = null;
            webEngine = null;
            Language language = Language.findLanguageByID("HTML");
            FileType fileType = language != null ? language.getAssociatedFileType() : null;
            Document myDocument = EditorFactory.getInstance().createDocument("");
            myTextViewer = (EditorImpl) EditorFactory.getInstance().createViewer(myDocument, project);
            if (fileType != null) myTextViewer.setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileType));
        } else {
            // Setup the editor pane for rendering HTML.
            myTextViewer = null;
            jEditorPane = new JPanel(new BorderLayout(), true);

            PluginClassLoader pluginClassLoader = MultiMarkdownPlugin.getInstance(project).getClassLoader();
            jfxPanel = new MyJFXPanel(); // initializing javafx
            jEditorPane.add(jfxPanel, BorderLayout.CENTER);
            Platform.setImplicitExit(false);

            // create a temp file for the custorm stuff
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    webView = new WebView();
                    webEngine = webView.getEngine();
                    setStyleSheet();
                    anchorPane = new AnchorPane();
                    AnchorPane.setTopAnchor(webView, 0.0);
                    AnchorPane.setBottomAnchor(webView, 0.0);
                    AnchorPane.setLeftAnchor(webView, 0.0);
                    AnchorPane.setRightAnchor(webView, 0.0);
                    anchorPane.getChildren().add(webView);
                    //Dimension dimension = jEditorPane.getPreferredSize();
                    //anchorPane.resize(dimension.getWidth(), dimension.getHeight());
                    jfxPanel.setScene(new Scene(anchorPane));
                    //jfxPanel.setScene(new Scene(webView));

                    // TODO: add zoom control to the page using popups or actions
                    webView.setZoom(1.0);

                    addStateChangeListener();
                }
            });

            //scrollPane = new JBScrollPane(jEditorPane);
            scrollPane = null;

            // Add a custom link listener which can resolve local link references.
            //jEditorPane.addHyperlinkListener(new MultiMarkdownLinkListener(jEditorPane, project, document));
            //jEditorPane.setEditable(false);

            // Set the editor pane caret position to top left, and do not let it reset it
            //jEditorPane.getCaret().setMagicCaretPosition(new Point(0, 0));
            //((DefaultCaret) jEditorPane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }

        checkNotifyUser();
    }

    protected void addStateChangeListener() {
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    EventListener listener = new EventListener() {
                        @Override public void handleEvent(org.w3c.dom.events.Event evt) {
                            evt.stopPropagation();
                            evt.preventDefault();
                            Element link = (Element) evt.getCurrentTarget();
                            org.w3c.dom.Document doc = webEngine.getDocument();
                            String href = link.getAttribute("href");
                            if (href.charAt(0) == '#') {
                                // tries to go to an anchor
                                String hrefName = href.substring(1);
                                // scroll it into view
                                try {
                                    JSObject result = (JSObject) webEngine.executeScript("(function () {\n" +
                                            "    var elemTop = 0;\n" +
                                            "    var elems = '';\n" +
                                            "    var elem = window.document.getElementById('" + hrefName + "');\n" +
                                            "    if (!elem) {\n" +
                                            "        var elemList = window.document.getElementsByTagName('a');\n" +
                                            "        for (a in elemList) {\n" +
                                            "            var aElem = elemList[a]\n" +
                                            "            if (aElem.hasOwnProperty('name') && aElem.name == '" + hrefName + "') {\n" +
                                            "                elem = aElem;\n" +
                                            "                break;\n" +
                                            "            }\n" +
                                            "        }\n" +
                                            "    }\n" +
                                            "    if (elem) {\n" +
                                            "        while (elem && elem.tagName !== 'HTML') {\n" +
                                            "            elems += ',' + elem.tagName + ':' + elem.offsetTop\n" +
                                            "            if (elem.offsetTop) {\n" +
                                            "                elemTop += elem.offsetTop;\n" +
                                            "                break;\n" +
                                            "            }\n" +
                                            "            elem = elem.parentNode\n" +
                                            "        }\n" +
                                            "    }\n" +
                                            "    return { elemTop: elemTop, elems: elems, found: !!elem };\n" +
                                            "})()" +
                                            "");
                                    int elemTop = (Integer) result.getMember("elemTop");
                                    boolean elemFound = (Boolean) result.getMember("found");
                                    String parentList = (String) result.getMember("elems");
                                    logger.trace(parentList);
                                    if (elemFound) webEngine.executeScript("window.scroll(0, " + elemTop + ")");
                                } catch (JSException ex) {
                                    String error = ex.toString();
                                    logger.error("JSException on script", ex);
                                }
                            } else {
                                // TODO: we should really handle all of them
                                if (Desktop.isDesktopSupported()) {
                                    try {
                                        Desktop.getDesktop().browse(new URI(href));
                                    } catch (URISyntaxException ex) {
                                        // invalid URI, just log
                                        logger.error("URISyntaxException on '" + href + "'" + ex.toString());
                                    } catch (IOException ex) {
                                        logger.error("IOException on '" + href + "'" + ex.toString());
                                    }
                                }
                            }
                        }
                    };

                    org.w3c.dom.Document doc = webEngine.getDocument();
                    Element el = doc.getElementById("a");
                    NodeList nodeList = doc.getElementsByTagName("a");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        ((EventTarget) nodeList.item(i)).addEventListener("click", listener, false);
                    }

                    // see if we need to change img tag src to a resource, if the src is relative
                    nodeList = doc.getElementsByTagName("img");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        HTMLImageElementImpl imgNode = (HTMLImageElementImpl) nodeList.item(i);
                        String src = imgNode.getSrc();
                        if (src.charAt(0) == '#') {
                            // it is resource based, get name and provide the right src
                            if ("#bullet".equals(src)) {
                                // provide bullet, just for fun
                                imgNode.setWidth("12");
                                imgNode.setHeight("12");
                                imgNode.setSrc(getClass().getResource(UIUtil.isRetina() ? "/com/vladsch/idea/multimarkdown/bullet@2x.png.png" : "/com/vladsch/idea/multimarkdown/bullet.png").toExternalForm());
                            } else if ("#opentask".equals(src)) {
                                // provide bullet, just for fun
                                imgNode.setWidth("12");
                                imgNode.setHeight("12");
                                imgNode.setSrc(getClass().getResource(UIUtil.isRetina() ? "/com/vladsch/idea/multimarkdown/opentask@2x.png" : "/com/vladsch/idea/multimarkdown/opentask.png").toExternalForm());
                            } else if ("#closedtask".equals(src)) {
                                // provide bullet, just for fun
                                imgNode.setWidth("12");
                                imgNode.setHeight("12");
                                imgNode.setSrc(getClass().getResource(UIUtil.isRetina() ? "/com/vladsch/idea/multimarkdown/closedtask@2x.png" : "/com/vladsch/idea/multimarkdown/closedtask.png").toExternalForm());
                            }
                        } else if (!src.startsWith("http://") && !src.startsWith("https://") && !src.startsWith("file://")) {
                            // relative to document, change it to absolute file://
                            VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                            VirtualFile parent = file == null ? null : file.getParent();
                            final VirtualFile localImage = parent == null ? null : parent.findFileByRelativePath(src);
                            try {
                                if (localImage != null && localImage.exists()) {
                                    imgNode.setSrc(String.valueOf(new File(localImage.getPath()).toURI().toURL()));
                                }
                            } catch (MalformedURLException e) {
                                logger.error("MalformedURLException", e);
                            }
                        }
                        int tmp = 0;
                    }

                    JSObject jsobj = (JSObject) webEngine.executeScript("window");
                    jsobj.setMember("java", new JSBridge());
                    if (pageScript != null && pageScript.length() > 0) {
                        webEngine.executeScript(pageScript);
                    }

                    // restore scroll if we had it
                    if (scrollOffset != null) {
                        webEngine.executeScript(scrollOffset);
                        scrollOffset = null;
                    }
                }
            }
        });
    }

    // call backs from JavaScript will be handled by the bridge
    public static class JSBridge {
        public JSBridge() {
        }
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
                            needStyleSheetUpdate = true;
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

        String cssURL = getClass().getResource(MultiMarkdownGlobalSettings.getInstance().getCssFilePath()).toExternalForm();
        if (MultiMarkdownGlobalSettings.getInstance().useCustomCss()) {
            cssURL = "data:css/text;charset=utf-8;base64," + Base64.encodeBase64URLSafeString(MultiMarkdownGlobalSettings.getInstance().getCssText().getBytes(Charset.forName("utf-8")));
        }
        needStyleSheetUpdate = false;
        webEngine.setUserStyleSheetLocation(cssURL);
    }

    /**
     * Get the {@link Component} to display as this editor's UI.
     *
     * @return a scrollable {@link JEditorPane}.
     */
    @NotNull
    public JComponent getComponent() {
        //return scrollPane != null ? scrollPane : myTextViewer.getComponent();
        return jEditorPane != null ? jEditorPane : myTextViewer.getComponent();
    }

    /**
     * Get the component to be focused when the editor is opened.
     *
     * @return {@link #scrollPane}
     */
    @Nullable
    public JComponent getPreferredFocusedComponent() {
        //return scrollPane != null ? scrollPane : myTextViewer.getComponent();
        return jEditorPane != null ? jEditorPane : myTextViewer.getContentComponent();
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
     * Just returns {@link FileEditorState#INSTANCE} as {@link MultiMarkdownFxPreviewEditor} is stateless.
     *
     * @param level the level.
     *
     * @return {@link FileEditorState#INSTANCE}
     *
     * @see #setState(FileEditorState)
     */
    @NotNull
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return FileEditorState.INSTANCE;
    }

    /**
     * Set the state of the editor.
     * <p/>
     * Does not do anything as {@link MultiMarkdownFxPreviewEditor} is stateless.
     *
     * @param state the new state.
     *
     * @see #getState(FileEditorStateLevel)
     */
    public void setState(@NotNull FileEditorState state) {
    }

    /**
     * Indicates whether the document content is modified compared to its file.
     *
     * @return {@code false} as {@link MultiMarkdownFxPreviewEditor} is read-only.
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

    protected void updateRawHtmlText(final String htmlTxt) {
        final DocumentEx myDocument = myTextViewer.getDocument();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        myDocument.replaceString(0, myDocument.getTextLength(), htmlTxt);
                        final CaretModel caretModel = myTextViewer.getCaretModel();
                        if (caretModel.getOffset() >= myDocument.getTextLength()) {
                            caretModel.moveToOffset(myDocument.getTextLength());
                        }
                    }
                }, null, null, UndoConfirmationPolicy.DEFAULT, myDocument);
            }
        });
    }

    private String markdownToHtml(boolean modified) {
        if (astRoot == null) {
            return "<strong>Parser timed out</strong>";
        } else {
            return modified ? new MultiMarkdownToHtmlSerializer(linkRendererModified).toHtml(astRoot) : new ToHtmlSerializer(linkRendererNormal).toHtml(astRoot);
        }
    }

    private void parseMarkdown(String markdownSource) {
        try {
            astRoot = processor.get().parseMarkdown(markdownSource.toCharArray());
        } catch (ParsingTimeoutException e) {
            astRoot = null;
        }
    }

    private void updateHtmlContent(boolean force) {
        if (updateDelayTimer != null) {
            updateDelayTimer.cancel();
            updateDelayTimer = null;
        }

        if (previewIsObsolete && isEditorTabVisible && (isActive || force)) {
            try {
                parseMarkdown(document.getText());
                final String html = makeHtmlPage(markdownToHtml(true));
                final String htmlTxt = isShowModified() ? html : markdownToHtml(false);
                if (isRawHtml) {
                    //myTextViewer.setText(htmlTxt);
                    updateRawHtmlText(htmlTxt);
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: add option to enable/disable keeping scroll position on update
                            JSObject scrollPos = (JSObject) webEngine.executeScript("({ x: window.pageXOffset, y: window.pageYOffset })");
                            scrollOffset = "window.scroll(" + scrollPos.getMember("x") + ", " + scrollPos.getMember("y") + ")";
                            if (needStyleSheetUpdate) {
                                setStyleSheet();
                            }
                            webEngine.loadContent(html);
                        }
                    });
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
                //        if (editors.length > i && editors[i+1] instanceof MultiMarkdownPreviewEditor) {
                //            // update its html too
                //            MultiMarkdownPreviewEditor htmlEditor = (MultiMarkdownPreviewEditor)editors[i+1];
                //            boolean showModified = MultiMarkdownGlobalSettings.getInstance().isShowHtmlTextAsModified();
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

    protected String makeHtmlPage(String html) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        String result = "" +
                "<body>\n" +
                "<div class=\"container\">\n" +
                "<div id=\"readme\" class=\"boxed-group\">\n" +
                "<h3>\n" +
                "   <span class=\"bookicon octicon-book\"></span>\n" +
                "  " + file.getName() + "\n" +
                "</h3>\n" +
                "<article class=\"markdown-body\">\n" +
                "";
        result += html;
        result += "</article>\n";
        result += "</div>\n";
        result += "</div>\n";
        result += "</body>\n";
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
     * @return {@code null} as {@link MultiMarkdownFxPreviewEditor} does not require highlighting.
     */
    @Nullable
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    /**
     * Get the current location.
     *
     * @return {@code null} as {@link MultiMarkdownFxPreviewEditor} is not navigable.
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
        if (!isReleased) {
            isReleased = true;
            if (jEditorPane != null) {
                jEditorPane.removeAll();
                //jEditorPane.removeNotify();
            }

            if (globalSettingsListener != null) {
                MultiMarkdownGlobalSettings.getInstance().removeListener(globalSettingsListener);
                globalSettingsListener = null;
            }

            if (myTextViewer != null) {
                final Application application = ApplicationManager.getApplication();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!myTextViewer.isDisposed()) {
                            EditorFactory.getInstance().releaseEditor(myTextViewer);
                        }
                    }
                };

                if (application.isUnitTestMode() || application.isDispatchThread()) {
                    runnable.run();
                } else {
                    application.invokeLater(runnable);
                }
            }

            project = null;
            Disposer.dispose(this);
        }
    }
}
