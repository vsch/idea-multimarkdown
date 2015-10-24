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
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
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
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.sun.webkit.dom.HTMLImageElementImpl;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.util.ReferenceChangeListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.*;
import org.pegdown.ast.RootNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;

import static com.vladsch.idea.multimarkdown.editor.MultiMarkdownPathResolver.isWikiDocument;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

//import com.sun.javafx.scene.layout.region.CornerRadiiConverter;

public class MultiMarkdownFxPreviewEditor extends UserDataHolderBase implements FileEditor, Disposable {
    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownFxPreviewEditor.class);

    public static final String PREVIEW_EDITOR_NAME = MultiMarkdownBundle.message("multimarkdown.preview-tab-name");

    public static final String TEXT_EDITOR_NAME = MultiMarkdownBundle.message("multimarkdown.html-tab-name");

    protected static int instances = 0;

    /**
     * The {@link Component} used to render the HTML preview.
     */
    protected final JPanel jEditorPane;
    protected WebView webView;
    protected WebEngine webEngine;
    protected JFXPanel jfxPanel;
    protected String scrollOffset = null;
    protected RootNode astRoot = null;
    protected AnchorPane anchorPane;

    /**
     * The {@link Document} previewed in this editor.
     */
    protected final Document document;
    protected final boolean isWikiDocument;

    //protected final EditorTextField myTextViewer;
    protected final EditorImpl myTextViewer;

    protected boolean isReleased = false;

    protected MultiMarkdownGlobalSettingsListener globalSettingsListener;
    protected ReferenceChangeListener projectFileListener;

    /**
     * The {@link PegDownProcessor} used for building the document AST.
     */
    //private ThreadLocal<PegDownProcessor> processor = initProcessor();
	private PegDownProcessor processor = null;

	protected boolean isActive = false;

    protected boolean isRawHtml = false;

    protected boolean isEditorTabVisible = true;

    protected Project project;

    protected LinkRenderer linkRendererNormal;
    protected LinkRenderer linkRendererModified;
    protected String pageScript = null;
    protected boolean needStyleSheetUpdate;
    protected boolean htmlWorkerRunning;

    protected String fireBugJS;

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

    /**
     * Init/reinit thread local {@link PegDownProcessor}.
     */
    //protected static ThreadLocal<PegDownProcessor> initProcessor() {
    //    return new ThreadLocal<PegDownProcessor>() {
    //        @Override
    //        protected PegDownProcessor initialValue() {
    //            // ISSUE: #7 worked around, disable pegdown TaskList HTML rendering, they don't display well in Darcula.
    //            return getProcessor();
    //        }
    //    };
    //}

    @NotNull
    private static PegDownProcessor getProcessor() {
        return new PegDownProcessor(MultiMarkdownGlobalSettings.getInstance().getExtensionsValue() /*& ~Extensions.TASKLISTITEMS*/, getParsingTimeout());
    }

    /**
     * Indicates whether the HTML preview is obsolete and should regenerated from the Markdown {@link #document}.
     */
    protected boolean previewIsObsolete = true;

    protected Timer updateDelayTimer;
    protected final int instance = ++instances;

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

    protected void updateLinkRenderer() {
        int options = MultiMarkdownGlobalSettings.getInstance().githubWikiLinks.getValue() ? MultiMarkdownLinkRenderer.GITHUB_WIKI_LINK_FORMAT : 0;
        linkRendererModified = new MultiMarkdownFxLinkRenderer(project, document, "absent", options);
        linkRendererNormal = new MultiMarkdownFxLinkRenderer(options);
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
        this.isWikiDocument = isWikiDocument(document);

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
                if (project.isDisposed()) return;
                processor = null;
                updateEditorTabIsVisible();
                updateLinkRenderer();
                delayedHtmlPreviewUpdate(true);
                checkNotifyUser();
            }
        });

        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        if (projectComponent != null) {
            projectComponent.addListener(MultiMarkdownProjectComponent.ALL_NAMESPACES, projectFileListener = new ReferenceChangeListener() {
                @Override
                public void referencesChanged(@Nullable String name) {
                    if (project.isDisposed()) return;
                    delayedHtmlPreviewUpdate(false);
                }
            });
        }

        project.getMessageBus().connect(this).subscribe(DumbService.DUMB_MODE, new DumbService.DumbModeListener() {
            @Override
            public void enteredDumbMode() {
            }

            @Override
            public void exitDumbMode() {
                // need to re-evaluate class link accessibility
                if (project.isDisposed()) return;
                delayedHtmlPreviewUpdate(false);
            }
        });

        updateLinkRenderer();

        if (isRawHtml) {
            jEditorPane = null;
            jfxPanel = null;
            webView = null;
            webEngine = null;
            Language language = Language.findLanguageByID("HTML");
            FileType fileType = language != null ? language.getAssociatedFileType() : null;
            Document myDocument = EditorFactory.getInstance().createDocument("");
            myTextViewer = (EditorImpl) EditorFactory.getInstance().createViewer(myDocument, project);
            if (fileType != null)
                myTextViewer.setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileType));
        } else {
            // Setup the editor pane for rendering HTML.
            myTextViewer = null;
            jEditorPane = new JPanel(new BorderLayout(), false);
            jfxPanel = new JFXPanel(); // initializing javafx
            jEditorPane.add(jfxPanel, BorderLayout.CENTER);
            Platform.setImplicitExit(false);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (project.isDisposed()) return;

                    webView = new WebView();
                    webEngine = webView.getEngine();

                    anchorPane = new AnchorPane();
                    AnchorPane.setTopAnchor(webView, 0.0);
                    AnchorPane.setLeftAnchor(webView, 0.0);
                    AnchorPane.setBottomAnchor(webView, 0.0);
                    AnchorPane.setRightAnchor(webView, 0.0);
                    anchorPane.getChildren().add(webView);
                    jfxPanel.setScene(new Scene(anchorPane));

                    webEngine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {
                        @Override
                        public WebEngine call(PopupFeatures config) {
                            // return a web engine for the new browser window or null to block popups
                            return null;
                        }
                    });

                    addStateChangeListener();
                }
            });
        }

        checkNotifyUser();
    }

    protected void addStateChangeListener() {
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldState, Worker.State newState) {
                if (project.isDisposed()) return;
                workerStateChanged(observable, oldState, newState);
            }
        });
    }

    protected void workerStateChanged(ObservableValue<? extends Worker.State> observable, Worker.State oldState, Worker.State newState) {
        //logger.info("[" + instance + "] " + "newState: " + newState + ", oldState: " + oldState);
        if (newState == Worker.State.SUCCEEDED) {
            // restore scroll if we had it
            JSObject jsobj = (JSObject) webEngine.executeScript("window");
            jsobj.setMember("java", new JSBridge(this));

            EventListener listener = new EventListener() {
                @Override
                public void handleEvent(org.w3c.dom.events.Event evt) {
                    evt.stopPropagation();
                    evt.preventDefault();

                    if (project.isDisposed()) return;

                    Element link = (Element) evt.getCurrentTarget();
                    org.w3c.dom.Document doc = webEngine.getDocument();
                    final String href = link.getAttribute("href");
                    if (href.charAt(0) == '#') {
                        if (href.length() != 1) {
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
                                //logger.trace(parentList);
                                if (elemFound) webEngine.executeScript("window.scroll(0, " + elemTop + ")");
                            } catch (JSException ex) {
                                String error = ex.toString();
                                logger.info("[" + instance + "] " + "JSException on script", ex);
                            }
                        }
                    } else {
                        MultiMarkdownPathResolver.launchExternalLink(project, document, href);
                    }
                }
            };

            NodeList nodeList;
            org.w3c.dom.Document doc = webEngine.getDocument();
            if (doc != null) {
                ((EventTarget) doc.getDocumentElement()).addEventListener("contextmenu", new EventListener() {
                    @Override
                    public void handleEvent(Event evt) {
                        evt.preventDefault();
                    }
                }, false);

                Element el = doc.getElementById("a");
                nodeList = doc.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    ((EventTarget) nodeList.item(i)).addEventListener("click", listener, false);
                }

                // see if we need to change img tag src to a resource, if the src is relative
                nodeList = doc.getElementsByTagName("img");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    HTMLImageElementImpl imgNode = (HTMLImageElementImpl) nodeList.item(i);
                    String src = imgNode.getSrc();
                    if (!src.startsWith("http://") && !src.startsWith("https://") && !src.startsWith("ftp://") && !src.startsWith("file://")) {
                        // relative to document, change it to absolute file://
                        //VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                        //VirtualFile parent = file == null ? null : file.getParent();
                        //final VirtualFile localImage = parent == null ? null : parent.findFileByRelativePath(src);
                        //final VirtualFile localImage = MultiMarkdownPathResolver.resolveRelativePath(document, src);
                        final VirtualFile localImage = (VirtualFile) MultiMarkdownPathResolver.resolveLink(project, document, src, false, true);
                        if (localImage != null && localImage.exists()) {
                            try {
                                imgNode.setSrc(String.valueOf(new File(localImage.getPath()).toURI().toURL()));
                            } catch (MalformedURLException e) {
                                logger.info("[" + instance + "] " + "MalformedURLException" + localImage.getPath());
                            }
                        } else {
                            String href = MultiMarkdownPathResolver.resolveExternalReference(project, document, src);
                            if (href != null) {
                                imgNode.setSrc(href);
                            }
                        }
                    }
                }
            }

            if (pageScript != null && pageScript.length() > 0) {
                webEngine.executeScript(pageScript);
            }

            // enable debug if it is enabled in settings
            if (MultiMarkdownGlobalSettings.getInstance().enableFirebug.getValue()) {
                enableDebug();
            }

            String scroll = scrollOffset;
            if (scroll != null) {
                try {
                    //webEngine.executeScript("window.java.log('test info')");
                    webEngine.executeScript("" +
                            "window.setTimeout(function () { " +
                            "    window.java.log('before scroll');" +
                            "    " + scroll + ";\n" +
                            "    window.java.log('after scroll');" +
                            "}, 50);");
                } catch (Exception e) {
                    logger.info("[" + instance + "] " + "JSException on script", e);
                }
            }

            try {
                webEngine.executeScript("window.addEventListener('scroll', function() { " +
                        "    window.java.onScroll();" +
                        "    window.setTimeout(function () { " +
                        "        window.java.repaint()" +
                        "    }, 100);" +
                        "})");
            } catch (Exception e) {
                logger.info("[" + instance + "] " + "", e);
            }

            //if (needStyleSheetUpdate) {
            //    setStyleSheet();
            //}
        }

        if (newState == Worker.State.READY || newState == Worker.State.FAILED || newState == Worker.State.SUCCEEDED) {
            htmlWorkerRunning = false;
        }
    }

    // call backs from JavaScript will be handled by the bridge
    public static class JSBridge {
        final MultiMarkdownFxPreviewEditor editor;

        public JSBridge(MultiMarkdownFxPreviewEditor editor) {
            this.editor = editor;
        }

        public void log(String msg) {
            //logger.info("[" + editor.instance + "] " + msg);
        }

        public void repaint() {
            //logger.info("[" + editor.instance + "] " + "before repaint");
            //jEditorPane.invalidate();
            if (editor.project.isDisposed()) return;

            editor.jfxPanel.repaint();
            //logger.info("[" + editor.instance + "] " + "after repaint");
        }

        public void onScroll() {
            if (editor.project.isDisposed()) return;

            JSObject scrollPos = (JSObject) editor.webEngine.executeScript("({ x: window.pageXOffset, y: window.pageYOffset })");
            //logger.info("[" + editor.instance + "] " + "window scrolled to " + scrollPos.getMember("x") + ", " + scrollPos.getMember("y"));
            editor.scrollOffset = "window.scroll(" + scrollPos.getMember("x") + ", " + scrollPos.getMember("y") + ")";
        }
    }

    protected String makeHtmlPage(String html) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        String result = "<head>\n" +
                "";

        // load layout css
        if (!(MultiMarkdownGlobalSettings.getInstance().useCustomCss(true) && MultiMarkdownGlobalSettings.getInstance().includesLayoutCss.getValue())) {
            result += "" +
                    "<link rel=\"stylesheet\" href=\"" + MultiMarkdownGlobalSettings.getInstance().getLayoutCssExternalForm(true) + "\">\n" +
                    "";
        }

        // load colors css
        if (!(MultiMarkdownGlobalSettings.getInstance().useCustomCss(true) && MultiMarkdownGlobalSettings.getInstance().includesColorsCss.getValue())) {
            result += "" +
                    "<link rel=\"stylesheet\" href=\"" + MultiMarkdownGlobalSettings.getInstance().getCssExternalForm(true) + "\">\n" +
                    "";
        }

        // load highlight js script & css
        if (MultiMarkdownGlobalSettings.getInstance().useHighlightJs.getValue()) {
            if (!(MultiMarkdownGlobalSettings.getInstance().useCustomCss(true) && MultiMarkdownGlobalSettings.getInstance().includesHljsCss.getValue())) {
                result += "" +
                        "<link rel=\"stylesheet\" href=\"" + MultiMarkdownGlobalSettings.getInstance().getHljsCssExternalForm(true) + "\">\n" +
                        "";
            }

            result += "" +
                    "<script src=\"" + MultiMarkdownGlobalSettings.getInstance().getHighlighJsExternalForm(true) + "\"></script>\n" +
                    "";
        }

        result += "" +
                "<title>" + escapeHtml(file != null ? file.getName() : "<null>") + "</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "";

        String gitHubHref = MultiMarkdownPathResolver.getGitHubDocumentURL(project, document, isWikiDocument);
        String gitHubClose = "";
        if (isWikiDocument) {
            if (gitHubHref == null) {
                gitHubHref = "";
            } else {
                gitHubHref = "<a href=\"" + gitHubHref + "\" name=\"wikipage\" id=\"wikipage\" class=\"anchor\"><span class=\"octicon octicon-link\"></span>";
                gitHubClose = "</a>";
            }

            result += "" +
                    "<div class=\"wiki-container\">\n" +
                    "<h1>" + gitHubHref + gitHubClose + escapeHtml(file != null ? file.getNameWithoutExtension().replace('-', ' ') : "<null>") + "</h1>\n" +
                    "<article class=\"wiki-body\">\n" +
                    "";
        } else {
            if (gitHubHref == null) {
                gitHubHref = "";
            } else {
                gitHubHref = "<a href=\"" + gitHubHref + "\" name=\"markdown-page\" id=\"markdown-page\" class=\"page-anchor\">";
                gitHubClose = "</a>";
            }
            result += "" +
                    "<div class=\"container\">\n" +
                    "<div id=\"readme\" class=\"boxed-group\">\n" +
                    "<h3>\n" +
                    "   " + gitHubHref + "<span class=\"bookicon octicon-book\"></span>\n" + gitHubClose +
                    "  " + (file != null ? file.getName() : "<null>") + "\n" +
                    "</h3>\n" +
                    "<article class=\"markdown-body\">\n" +
                    "";
        }

        result += html;
        result += "\n</article>\n";
        result += "</div>\n";
        result += "</div>\n";
        //if (fireBugJS != null && fireBugJS.length() > 0 && MultiMarkdownGlobalSettings.getInstance().enableFirebug.getValue()) {
        //    result += "" +
        //            "<script src='" + fireBugJS + "' id='FirebugLite' FirebugLite='4'/>\n" +
        //            "";
        //}
        result += "" +
                "<script>hljs.initHighlightingOnLoad();</script>\n" +
                "</body>\n";
        return result;
    }

    public void enableDebug() {
        //webView.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
        //if (fireBugJS == null) return;

        try {
            //webEngine.executeScript("if (!document.getElementById('FirebugLite')) {\n" +
            //        "    E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;\n" +
            //        "    E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');\n" +
            //        "    E['setAttribute']('id', 'FirebugLite');\n" +
            //        "    E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');\n" +
            //        //"    E['setAttribute']('src', '" + fireBugJS + "');\n" +
            //        "    E['setAttribute']('FirebugLite', '4');\n" +
            //        "    (document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);\n" +
            //        "    E = new Image;\n" +
            //        "    E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');\n" +
            //        //"    E['setAttribute']('src', '" + fireBugJS + "' );\n" +
            //        "}\n");

            webEngine.executeScript("if (!document.getElementById('FirebugLite')) {\n" +
                    "    E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;\n" +
                    "    E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');\n" +
                    "    E['setAttribute']('id', 'FirebugLite');\n" +
                    "    E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');\n" +
                    //"    E['setAttribute']('src', '" + fireBugJS + "');\n" +
                    "    E['setAttribute']('FirebugLite', '4');\n" +
                    "    (document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);\n" +
                    "    E = new Image;\n" +
                    "    E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');\n" +
                    "}\n");
        } catch (JSException ex) {
            String error = ex.toString();
            logger.info("[" + instance + "] " + "JSException on script", ex);
        }
    }

    protected void delayedHtmlPreviewUpdate(final boolean fullKit) {
        if (updateDelayTimer != null) {
            updateDelayTimer.cancel();
            updateDelayTimer = null;
        }

        if (project.isDisposed()) return;

        if (!isEditorTabVisible)
            return;

        updateDelayTimer = new Timer();
        updateDelayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (project.isDisposed()) return;

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (project.isDisposed()) return;

                        previewIsObsolete = true;

                        if (fullKit) {
                            needStyleSheetUpdate = true;
                            //processor.remove();     // make it re-initialize when accessed
                        }
                        updateHtmlContent(isActive || isMyTabSelected());
                    }
                }, ModalityState.any());
            }
        }, getUpdateDelay());
    }

    protected boolean isMyTabSelected() {
        FileEditorManager manager = FileEditorManager.getInstance(project);
        FileEditor[] editors = manager.getSelectedEditors();
        for (FileEditor editor : editors) {
            if (editor == this) return true;
        }
        return false;
    }

    protected MultiMarkdownFxPreviewEditor findCounterpart() {
        // here we can find our HTML Text counterpart and update its HTML at the same time. but it is better to keep it separate for now
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file != null) {
            FileEditorManager manager = FileEditorManager.getInstance(project);
            FileEditor[] editors = manager.getEditors(file);
            for (FileEditor editor : editors) {
                if (editor != this && editor instanceof MultiMarkdownFxPreviewEditor) {
                    return (MultiMarkdownFxPreviewEditor) editor;
                }
            }
        }

        return null;
    }

    //protected void setStyleSheet() {
    //    if (isRawHtml) return;
    //
    //    needStyleSheetUpdate = false;
    //    webEngine.setUserStyleSheetLocation(MultiMarkdownGlobalSettings.getInstance().getCssExternalForm());
    //}

    protected void updateRawHtmlText(final String htmlTxt) {
        final DocumentEx myDocument = myTextViewer.getDocument();

        if (project.isDisposed()) return;

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                if (project.isDisposed()) return;

                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        if (project.isDisposed()) return;

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

    protected String markdownToHtml(boolean modified) {
        if (astRoot == null) {
            return "<strong>Parser timed out</strong>";
        } else {
            if (modified) {
                MultiMarkdownToHtmlSerializer htmlSerializer = new MultiMarkdownToHtmlSerializer(project, document, linkRendererModified);

                if (!isWikiDocument) {
                    htmlSerializer.setFlag(MultiMarkdownToHtmlSerializer.NO_WIKI_LINKS);
                }

                return htmlSerializer.toHtml(astRoot);
            } else {

                return new ToHtmlSerializer(linkRendererNormal).toHtml(astRoot);
            }
        }
    }

    protected void parseMarkdown(String markdownSource) {
        try {
            if (processor == null) {
                processor = getProcessor();
            }
            astRoot = processor.parseMarkdown(markdownSource.toCharArray());
        } catch (ParsingTimeoutException e) {
            astRoot = null;
        }
    }

    protected void updateHtmlContent(boolean force) {
        if (updateDelayTimer != null) {
            updateDelayTimer.cancel();
            updateDelayTimer = null;
        }

        if (previewIsObsolete && isEditorTabVisible && (isActive || force)) {
            try {
                if (isRawHtml) {
                    parseMarkdown(document.getText());
                    final String html = makeHtmlPage(markdownToHtml(true));
                    final String htmlTxt = isShowModified() ? html : markdownToHtml(false);
                    astRoot = null;
                    updateRawHtmlText(htmlTxt);
                } else {
                    if (!htmlWorkerRunning) {
                        htmlWorkerRunning = true;
                        previewIsObsolete = false;

                        parseMarkdown(document.getText());
                        final String html = makeHtmlPage(markdownToHtml(true));
                        astRoot = null;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (project.isDisposed()) return;

                                // TODO: add option to enable/disable keeping scroll position on update
                                Worker.State state = webEngine.getLoadWorker().getState();
                                //logger.info("[" + instance + "] " + "State on update " + state);
                                Double pageZoom = MultiMarkdownGlobalSettings.getInstance().pageZoom.getValue();
                                if (webView.getZoom() != pageZoom) {
                                    //logger.info("[" + instance + "] " + "setZoom(" + pageZoom + ")");
                                    webView.setZoom(pageZoom);
                                }

                                //logger.info("[" + instance + "] " + "loadContent");
                                webEngine.loadContent(html);
                            }
                        });
                    } else {
                        // reschedule the update for later
                        delayedHtmlPreviewUpdate(false);
                    }
                }
            } catch (Exception e) {
                logger.info("[" + instance + "] " + "Failed processing Markdown document", e);
            }
        }
    }

    @NotNull
    public JComponent getComponent() {
        //return scrollPane != null ? scrollPane : myTextViewer.getComponent();
        return jEditorPane != null ? jEditorPane : myTextViewer.getComponent();
    }

    @Nullable
    public JComponent getPreferredFocusedComponent() {
        //return scrollPane != null ? scrollPane : myTextViewer.getComponent();
        return jEditorPane != null ? jEditorPane : myTextViewer.getContentComponent();
    }

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
     * @return {@link FileEditorState#INSTANCE}
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

    /**
     * Dispose the editor.
     */
    public void dispose() {
        if (!isReleased) {
            isReleased = true;

            if (updateDelayTimer != null) {
                updateDelayTimer.cancel();
                updateDelayTimer = null;
            }

            if (jEditorPane != null) {
                jEditorPane.removeAll();
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

            Disposer.dispose(this);
        }
    }
}
