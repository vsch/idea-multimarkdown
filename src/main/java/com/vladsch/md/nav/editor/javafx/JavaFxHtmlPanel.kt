// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.javafx

import com.intellij.ide.IdeEventQueue
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.util.ArrayUtil
import com.sun.javafx.application.PlatformImpl
import com.vladsch.boxed.json.BoxedJsObject
import com.vladsch.boxed.json.BoxedJsValue
import com.vladsch.boxed.json.BoxedJson
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.javafx.webview.debugger.DevToolsDebuggerJsBridge
import com.vladsch.javafx.webview.debugger.JfxDebugProxyJsBridge
import com.vladsch.javafx.webview.debugger.JfxScriptStateProvider
import com.vladsch.javafx.webview.debugger.LogHandler
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.MdProjectComponent
import com.vladsch.md.nav.editor.HtmlPanelHost
import com.vladsch.md.nav.editor.PreviewEditorState
import com.vladsch.md.nav.editor.api.MdPreviewCustomizationProvider
import com.vladsch.md.nav.editor.util.HtmlPanel
import com.vladsch.md.nav.editor.util.HtmlResource
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import com.vladsch.md.nav.settings.*
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.plugin.util.*
import com.vladsch.plugin.util.image.ImageUtils
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.print.PrinterJob
import javafx.scene.Scene
import javafx.scene.text.FontSmoothingType
import javafx.scene.web.WebView
import netscape.javascript.JSException
import netscape.javascript.JSObject
import org.intellij.lang.annotations.Language
import org.jdom.Text
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import java.awt.BorderLayout
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import javax.swing.JComponent
import javax.swing.JPanel

// Instantiated by reflection
@SuppressWarnings("unused")
class JavaFxHtmlPanel(project: Project, htmlPanelHost: HtmlPanelHost) : HtmlPanel(project, htmlPanelHost) {

    private var _myPanel: JFXPanel? = null
    private var _myWebView: WebView? = null
    private var _myJSBridge: JSBridge? = null
    private var myInlineCss: String? = null
    private var myCssUris = ArrayUtil.EMPTY_STRING_ARRAY
    private var myLastRawHtml = ""
    private var myLastPageUrl = ""

    //    private var myHtmlPanelHost: HtmlPanelHost? = null
    private val myInstance: Int = getInstance(myHtmlPanelHost.getVirtualFile())
    private var myScrollTag: String = ""
    private var myScrollAttribute: String = ""
    private var myScrollReference: String = ""
    private val myWebViewFxRunner = WebViewFxRunner()
    private var myOnTypingUpdate = AtomicBoolean(false)
    private var myState = PreviewEditorState()
    private var myVerticalLocation: Float? = null
    private var myDebugFileSerial = 0
    private var myAllowContextMenu = AtomicBoolean(true)
    private var myHtmlFile = ""
    private var myPageReloadTriggered = false
    private var myScriptState = BoxedJson.of()

    //    internal val myInitActions = ArrayList<Runnable>()
    private val myPanelWrapper: JPanel
    private var myInitialHtml: String = ""

    private val myPanel: JFXPanel
        get() = _myPanel!!

    private val myJSBridge: JSBridge
        get() = _myJSBridge!!

    private val myWebView: WebView
        get() = _myWebView!!

    private val isWebViewInitialized: Boolean
        get() = _myPanel != null && _myWebView != null

    init {
        if (useNewAPI == null) {
            val schemeManagerClass = Class.forName("com.intellij.ide.IdeEventQueue")
            useNewAPI = try {
                schemeManagerClass.getMethod("unsafeNonblockingExecute", Runnable::class.java)
                true
            } catch (e: NoSuchMethodException) {
                false
            }
        }

        if (useNewAPI!!) {
            myPanelWrapper = JPanel(BorderLayout())
            myPanelWrapper.background = JBColor.background()

            LogHandler.LOG_HANDLER = object : LogHandler() {
                override fun trace(message: String) = LOG_JFX_DEBUGGER.debug(message)

                override fun trace(message: String, t: Throwable) = LOG_JFX_DEBUGGER.debug(message, t as Throwable?)

                override fun trace(t: Throwable) = LOG_JFX_DEBUGGER.debug(t)

                override fun isTraceEnabled(): Boolean = LOG_JFX_DEBUGGER.isTraceEnabled

                override fun debug(message: String) = LOG_JFX_DEBUGGER.debug(message)

                override fun debug(message: String, t: Throwable) = LOG_JFX_DEBUGGER.debug(message, t as Throwable?)

                override fun debug(t: Throwable) = LOG_JFX_DEBUGGER.debug(t)

                override fun error(message: String) = LOG_JFX_DEBUGGER.error(message)

                override fun error(message: String, t: Throwable) = LOG_JFX_DEBUGGER.error(message, t)

                override fun error(t: Throwable) = LOG_JFX_DEBUGGER.error(t)

                override fun info(message: String) = LOG_JFX_DEBUGGER.info(message)

                override fun info(message: String, t: Throwable) = LOG_JFX_DEBUGGER.info(message, t)

                override fun info(t: Throwable) = LOG_JFX_DEBUGGER.info(t)

                override fun isDebugEnabled(): Boolean = LOG_JFX_DEBUGGER.isDebugEnabled()

                override fun warn(message: String) = LOG_JFX_DEBUGGER.warn(message)

                override fun warn(message: String, t: Throwable) = LOG_JFX_DEBUGGER.warn(message, t)

                override fun warn(t: Throwable) = LOG_JFX_DEBUGGER.warn(t)
            }

            ApplicationManager.getApplication().invokeLater {
                runFX {
                    PlatformImpl.startup {
                        val webView = WebView()
                        // diagnostic/2858
                        if (Disposer.isDisposed(this)) return@startup

                        updateViewOptions(webView, MdProjectSettings.getInstance(myProject).previewSettings)
                        webView.isContextMenuEnabled = false

                        myWebViewFxRunner.setWorker(webView.engine.loadWorker)
                        _myWebView = webView
                        _myJSBridge = JSBridge(this, webView, myInstance, object : JfxScriptStateProvider {
                            override fun getState(): BoxedJsObject {
                                return myScriptState
                            }

                            override fun setState(state: BoxedJsObject) {
                                // nothing to do, state already mutable
                                //return myScriptState
                            }
                        })

                        val scene = Scene(webView)

                        ApplicationManager.getApplication().invokeLater {
                            runFX {
                                val jfxPanel = JFXPanelWrapper()
                                _myPanel = jfxPanel

                                if (!Disposer.isDisposed(this)) {
                                    Platform.runLater { jfxPanel.scene = scene }

                                    subscribeSettingChanges(webView)

                                    myWebViewFxRunner.panelInitialized()

                                    if (!myInitialHtml.isEmpty()) {
                                        setHtml(myInitialHtml)
                                        myInitialHtml = ""
                                    }

                                    myPanelWrapper.add(jfxPanel, BorderLayout.CENTER)
                                    myPanelWrapper.repaint()
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // 2016.3 compatible
            myPanelWrapper = JPanel(BorderLayout())
            myPanelWrapper.background = JBColor.background()

            val jfxPanel = JFXPanelWrapper()
            _myPanel = jfxPanel

            PlatformImpl.startup {
                val webView = WebView()
                // implied by diagnostic/2858
                if (Disposer.isDisposed(this)) return@startup

                updateViewOptions(webView, MdProjectSettings.getInstance(myProject).previewSettings)
                webView.isContextMenuEnabled = false
                myWebViewFxRunner.setWorker(webView.engine.loadWorker)
                _myWebView = webView
                _myJSBridge = JSBridge(this, webView, myInstance, object : JfxScriptStateProvider {
                    override fun getState(): BoxedJsObject {
                        return myScriptState
                    }

                    override fun setState(state: BoxedJsObject) {
                        // nothing to do, state already mutable
                        //return myScriptState
                    }
                })

                val scene = Scene(webView)
                jfxPanel.scene = scene

                ApplicationManager.getApplication().invokeLater {
                    if (!Disposer.isDisposed(this)) {

                        subscribeSettingChanges(webView)
                        myWebViewFxRunner.panelInitialized()

                        if (!myInitialHtml.isEmpty()) {
                            setHtml(myInitialHtml)
                            myInitialHtml = ""
                        }

                        myPanelWrapper.add(jfxPanel, BorderLayout.CENTER)
                        myPanelWrapper.repaint()
                    }
                }
            }
        }
    }

    private fun subscribeSettingChanges(webView: WebView) {
        // subscribe to application level settings changes,
        // subscribe to project settings notifications
        val messageBusConnection = ApplicationManager.getApplication().messageBus.connect(this)
        messageBusConnection.subscribe(SettingsChangedListener.TOPIC, SettingsChangedListener {
            Platform.runLater {
                updateViewOptions(webView, MdProjectSettings.getInstance(myProject).previewSettings)
            }
        })

        val projectMessageBusConnection = myProject.messageBus.connect(this)
//        projectMessageBusConnection.subscribe(ProjectSettingsChangedListener.TOPIC, ProjectSettingsChangedListener { _, settings ->
//            Platform.runLater {
//                updateViewOptions(webView, settings.previewSettings)
//            }
//        })

        val profilesChangedListener = object : ProfileManagerChangeListener {
            override fun onSettingsLoaded(manager: RenderingProfileManager) {
            }

            override fun onSettingsChange(manager: RenderingProfileManager) {
                Platform.runLater {
                    updateViewOptions(webView, myHtmlPanelHost.getRenderingProfile().previewSettings)
                }
            }
        }
        projectMessageBusConnection.subscribe(ProfileManagerChangeListener.TOPIC, profilesChangedListener)
    }

    private fun updateViewOptions(view: WebView, previewSettings: MdPreviewSettings) {
        if (myProject.isDisposed) return

        val typeToSet: FontSmoothingType =
            if (previewSettings.useGrayscaleRendering) {
                FontSmoothingType.GRAY
            } else {
                FontSmoothingType.LCD
            }

        val fontSmoothingTypeProperty = view.fontSmoothingTypeProperty()
        if (fontSmoothingTypeProperty.value != typeToSet) {
            fontSmoothingTypeProperty.value = typeToSet
        }

        val zoom = previewSettings.zoomFactor * MdApplicationSettings.instance.documentSettings.zoomFactor
        if (view.zoom != zoom) {
            view.zoom = zoom
        }
    }

    override fun setState(state: PreviewEditorState) {
        val version = state.previewStateElement.getAttribute("version")?.value ?: ""

        if (SemanticVersion(version).isEarlierThan("2.4.0.50")) {
            state.previewStateElement.children.clear() // state is not compatible, dump it
        }

        myState = state

        val scriptState = myState.previewStateElement.getChild(SCRIPT_STATE_NAME) ?: org.jdom.Element(SCRIPT_STATE_NAME)
        val content = scriptState.content
        val jsState: BoxedJsObject

        if (content != null && content.size > 0 && content[0] is Text) {
            jsState = BoxedJson.boxedFrom(content[0].value)
        } else {
            jsState = BoxedJsValue.HAD_MISSING_OBJECT
        }

        myScriptState = jsState.isValid.ifElse(jsState, BoxedJson.of())
    }

    override fun getState(): PreviewEditorState {
        myState.previewStateElement.setAttribute("version", MdPlugin.fullProductVersion)
        myState.previewStateElement.removeChild(SCRIPT_STATE_NAME)

        if (myScriptState != null && !myScriptState.isEmpty()) {
            val scriptState = org.jdom.Element(SCRIPT_STATE_NAME)
            myState.previewStateElement.addContent(scriptState)
            scriptState.addContent(myScriptState.toString())
        }
        return myState
    }

    override val component: JComponent
        get() = myPanelWrapper

    override fun setCSS(inlineCss: String?, fileUris: Array<String>) {
        myInlineCss = inlineCss
        myCssUris = fileUris
        setHtml(myLastRawHtml)
    }

    override fun setHtml(html: String): Boolean {
        if (myProject.isDisposed) return true
        if (_myPanel == null || _myWebView == null || _myJSBridge == null) {
            // not initialized yet, save for later
            myInitialHtml = html
            return true
        }

        @Suppress("NAME_SHADOWING")
        var html = html
        val documentPath = myHtmlPanelHost.getVirtualFile().parent?.path?.suffixWith('/')
        val systemPath = if (documentPath != null) FileUtil.toSystemDependentName(documentPath) else null
        val fileUriPrefix = PathInfo.fileURIPrefix(documentPath)

        // adjust relative scripts to absolute relative to document path
        html = SCRIPT_REPLACE_PATTERN.replace(html) { matchResult ->
            val values = matchResult.groupValues
            if (!PathInfo.isURI(values[2]) && PathInfo.isRelative(values[2])) {
                // change it to absolute
                values[1] + fileUriPrefix + systemPath + values[2] + values[3]
            } else {
                matchResult.value
            }
        }

        /*
                // now that links are not resolved before rendering, need to leave them unmodified for
                // resolving when clicked
                html = LINK_REPLACE_PATTERN.replace(html) { matchResult ->
                    val values = matchResult.groupValues
                    if (!PathInfo.isURI(values[2]) && PathInfo.isRelative(values[2])) {
                        // change it to absolute
                        if (values[2].startsWith('#')) {
                            // local, no change
                            values[1] + values[2] + values[3]
                        } else {
                            values[1] + fileUriPrefix + systemPath + values[2] + values[3]
                        }
                    } else {
                        matchResult.value
                    }
                }
        */

        val previewSettings = myHtmlPanelHost.getRenderingProfile().previewSettings
        val synchronizePreview = previewSettings.synchronizePreviewPosition
        val resourcePath = synchronizePreview.ifElse("/com/vladsch/md/nav/synchronize-preview.js", "/com/vladsch/md/nav/scroll-preview.js")

        val scrollScriptUrl = "<script src=\"${getInjectedScriptUrl(resourcePath)}\"></script>\n"
        // if debugger is connected we use injected scripts not links
        val result: String
        if (isDebugging()) {
            result = prepareHtml(html, null, null, null, scrollScriptUrl)
        } else {
            val helperScript = "<script src=\"${getJsBridgeHelperScriptUrl()}\"></script>\n"
            val stateScript = "\n<script>\n${myJSBridge.stateString}</script>"
            result = prepareHtml(html, null, helperScript, stateScript, scrollScriptUrl)
        }

        myLastRawHtml = result
        val lastPageUrl = myLastPageUrl
        // if url was set nothing will make it display the page until content changes
        // so this is now done by creating a new preview
        myLastPageUrl = ""

        LOG.debug { "[$myInstance] updating content, last page url $lastPageUrl" }
        myWebViewFxRunner.schedule("JavaFxHtml::setHtml.loadContent", WebViewFxRunner.Type.LOADER) {
            if (!myProject.isDisposed) {
                val htmlPanelHost = myHtmlPanelHost
                updateViewOptions(myWebView, htmlPanelHost.getRenderingProfile().previewSettings)

                var alternatePage = false
                for (handler in MdPreviewCustomizationProvider.EXTENSIONS.value) {
                    val pageFileUrl = handler.getPageFileURL(myProject, myInstance, myDebugFileSerial + 1, result)
                    if (pageFileUrl != null) {
                        myDebugFileSerial++;
                        pageReloading()
                        myHtmlFile = pageFileUrl.first
                        myWebView.engine.load(pageFileUrl.second)
                        LOG.debug { "[$myInstance] updated content" }
                        alternatePage = true
                        break
                    }
                }

                if (!alternatePage) {
                    pageReloading()
                    myWebView.engine.loadContent(result)
                    LOG.debug { "[$myInstance] updated content" }
                }

                myWebViewFxRunner.schedule("JavaFxHtml::setHtml.preparePage", WebViewFxRunner.Type.INITIALIZER) {
                    preparePage()
                }
            }
        }

        return true
    }

    internal fun getJsBridgeHelperScriptUrl(): String = getInjectedScriptUrl("/com/vladsch/md/nav/markdown-navigator.js")

    internal fun getInjectedScriptUrl(initResourcePath: String): String {
        return HtmlResource.getInjectionUrl(myProject,
            HtmlResource.resourceFileUrl(initResourcePath, javaClass),
            initResourcePath,
            myHtmlPanelHost.getRenderingProfile(),
            false,
            null
        )!!
    }

    override fun setPageUrl(url: String): Boolean? {
        if (myProject.isDisposed || url.isBlank()) return true

        myLastRawHtml = ""
        myLastPageUrl = url

        LOG.debug { "[$myInstance] updating url: $url" }
        myWebViewFxRunner.schedule("JavaFxHtml::setPageUrl", WebViewFxRunner.Type.LOADER) {
            if (!myProject.isDisposed) {
                updateViewOptions(myWebView, myHtmlPanelHost.getRenderingProfile().previewSettings)

                pageReloading()
                myWebView.engine.load(url)
                LOG.debug { "[$myInstance] updated url: $url" }
            }
        }
        return true
    }

    private fun pageReloading() {
        myJSBridge.pageReloading()
    }

    fun launchExternalLink(href: String): Boolean {
        if (myProject.isDisposed) return true
        return myHtmlPanelHost.launchExternalLink(href)
    }

    fun scrollToReference(onLoad: Boolean) {
        val previewSettings = myHtmlPanelHost.getRenderingProfile().previewSettings
        if (previewSettings.synchronizePreviewPosition /*&& MultiMarkdownPlugin.isLicensed*/) {
            myWebViewFxRunner.schedule("JavaFxHtml::scrollToReference", if (onLoad) WebViewFxRunner.Type.INITIALIZER else WebViewFxRunner.Type.PAGE_INTERACTION) {
                if (!myProject.isDisposed) {
                    if (onLoad) {
                        myWebViewFxRunner.schedule("JavaFxHtml::scrollToReference onLoad dummy", WebViewFxRunner.Type.PAGE_INTERACTION, { })
                        myWebViewFxRunner.setInitialized()
                    }

                    val highlightEnabled = myHtmlPanelHost.isHighlightEnabled()
                    val onTypingUpdate = myOnTypingUpdate.get()
                    myOnTypingUpdate.set(false)
                    val highlightOnTyping = previewSettings.highlightOnTyping
                    val highlightFadeOut = previewSettings.highlightFadeOut * 1000
                    val s = "scrollToSourcePosition($myVerticalLocation,'$myScrollTag','$myScrollAttribute','$myScrollReference',$highlightEnabled,$onTypingUpdate,$highlightOnTyping,$highlightFadeOut);"
                    LOG.debugOne(loggerScroll) { "[$myInstance] executing scroll: '$s'" }
                    try {
                        myWebView.engine.executeScript(s)
                    } catch (ex: JSException) {
                        LOG.debugOne(loggerScroll, e = ex) { "[$myInstance] JSException on script" }
                    }
                }
            }
        } else {
            if (onLoad) {
                myWebViewFxRunner.setInitialized()
            }
        }
    }

    override fun scrollToMarkdownSrcOffset(offset: Int, lineOffsets: Range, verticalLocation: Float?, tagRanges: List<TagRange>, onLoadUpdate: Boolean, onTypingUpdate: Boolean) {
        // now we have the best possible match from all the elements, we can find the tag and scroll it into view
        var bestTagRange: TagRange? = null
        val tagPriorities = mapOf(
            "a" to 1,
            "span" to 2,
            "img" to 3
        )

        for (tagRange in tagRanges) {
            if (tagRange.doesContain(offset)) {
                if (bestTagRange == null || tagRange.span < bestTagRange.span || tagRange.span <= bestTagRange.span && (tagPriorities[tagRange.tag]
                        ?: 0) > (tagPriorities[bestTagRange.tag] ?: 0)) {
                    bestTagRange = tagRange
                }
            } else if (bestTagRange == null && lineOffsets.doesOverlap(tagRange)) {
                bestTagRange = tagRange
            }
        }

        LOG.debugOne(loggerScroll) { "scrollToMarkdownSrcOffset($offset, $lineOffsets, $verticalLocation, tagRanges, $onLoadUpdate, $onTypingUpdate) bestFit: ${bestTagRange?.tag} $bestTagRange" }

        val findBestTagRange = bestTagRange ?: return

        myScrollTag = findBestTagRange.tag
        myScrollAttribute = MdNavigatorExtension.SOURCE_POSITION_ATTRIBUTE_NAME
        myScrollReference = "${findBestTagRange.start}-${findBestTagRange.end}"
        myOnTypingUpdate.set(myOnTypingUpdate.get() || onTypingUpdate)
        myVerticalLocation = verticalLocation

        if (onLoadUpdate) {
            // we will get a page update so we cancel the current page first and then schedule the scroll
            LOG.debugOne(loggerScroll) { "scrollToMarkdownSrcOffset: fxRunner cancelAll, prep for load" }
            myWebViewFxRunner.cancelAll("scrollToMarkdownSrcOffset prep for load")
        } else {
            myPageReloadTriggered = false
            scrollToReference(onTypingUpdate)
        }
    }

    override fun dispose() {
        if (_myJSBridge != null) {
            if (myJSBridge.isDebuggerEnabled) {
                myJSBridge.stopDebugServer { }
            }
        }

        Disposer.dispose(myWebViewFxRunner)

        if (myHtmlFile.isNotEmpty()) {
            val file = File(myHtmlFile)
            if (file.exists()) {
                file.delete()
            }
            myHtmlFile = ""
        }
    }

    /*
        // from: https://stackoverflow.com/questions/38391522/javafx-webview-context-menu/38413661
        private fun createContextMenu(webView: WebView) {

            val reload = MenuItem("reload")
            reload.setOnAction({ e -> webView.engine.reload() }
            )
            val contextMenu = ContextMenu(reload)
            webView.setOnMousePressed { e ->
                if (e.button == MouseButton.SECONDARY) {
                    println(webView.engine.executeScript("document.elementFromPoint("
                            + e.x
                            + "," + e.y + ").tagName;"))
                    val `object` = webView.engine.executeScript("document.elementFromPoint("
                            + e.x
                            + "," + e.y + ");") as JSObject
                    contextMenu.show(webView, e.screenX, e.screenY)
                } else {
                    contextMenu.hide()
                }
            }
        }
    */

    private fun preparePage() {
        if (myProject.isDisposed) return

        // connect JsBridge
        myJSBridge.connectJsBridge()

        val lastPageUrl = myLastPageUrl

        val doc = myWebView.engine.document
        if (doc != null) {
            if (myAllowContextMenu.get()) {
                myWebView.contextMenuEnabledProperty().set(true)
            } else {
                (doc.documentElement as EventTarget).addEventListener("contextmenu", { evt -> evt.preventDefault() }, false)
            }

            val listener = EventListener { evt ->
                if (myProject.isDisposed) return@EventListener

                val link = evt.currentTarget as Element
                val href = link.getAttribute("href")
                if (href[0] == '#') {
                    if (!lastPageUrl.isEmpty()) {
                        // we're on github, process as normal
                        return@EventListener
                    }

                    evt.stopPropagation()
                    evt.preventDefault()

                    if (href.length != 1) {
                        // tries to go to an anchor
                        val hrefName = href.substring(1)
                        // scroll it into view
                        try {
                            @Language("JavaScript")
                            val func = """
(function (hrefName) {
    let elemTop = 0;
    let elements = '';
    let elem = window.document.getElementById(hrefName);
    if (!elem) {
        let elemList = window.document.getElementsByName(hrefName);
        for (let a in elemList) {
            if (!elemList.hasOwnProperty(a)) continue;
            elem = elemList[a];
            break;
        }
    }
    if (elem) {
        while (elem && elem.tagName !== 'HTML') {
            elements += ',' + elem.tagName + ':' + elem.offsetTop;
            if (elem.offsetTop) {
                elemTop += elem.offsetTop;
                break;
            }
            elem = elem.parentNode
        }
    }
    return { elemTop: elemTop, elements: elements, found: !!elem };
})
"""
                            val result: JSObject = myWebView.engine.executeScript("$func('$hrefName');") as JSObject
                            val elemTop = result.getMember("elemTop") as Int
                            val elemFound = result.getMember("found") as Boolean

                            if (elemFound) myWebView.engine.executeScript("window.scroll(0, $elemTop)")
                        } catch (ex: JSException) {
                            //                            val error = ex.toString()
                            LOG.debugOne(loggerScroll, e = ex) { "[$myInstance] JSException on script" }
                        }
                    }
                } else {
                    // NOTE: some links can cause the JavaFx to crash so we disable this feature with bundled JVM on Mac, if they are missing the needed libraries
                    if (launchExternalLink(href)) {
                        evt.stopPropagation()
                        evt.preventDefault()
                    }
                }
            }

            val documentPath = myHtmlPanelHost.getVirtualFile().parent?.path?.suffixWith('/')
            val fileUriPrefix = if (documentPath != null && documentPath.length > 1 && documentPath[1] == ':') "file:/" else MdNavigatorExtension.FILE_URI_PREFIX
            val projectComponent = if (documentPath != null) MdProjectComponent.getInstance(myProject) else null

            val nodeList = doc.getElementsByTagName("a")
            for (i in 0 until nodeList.length) {
                val item = nodeList.item(i)
                (item as EventTarget).addEventListener("click", listener, false)
            }

            val taskListener = EventListener { evt ->
                if (myProject.isDisposed) return@EventListener

                val span = evt.currentTarget as Element
                val listItem = span.parentNode as Element
                val taskOffset = listItem.getAttribute("task-offset")
                if (taskOffset != null && taskOffset.isNotEmpty()) {
                    evt.stopPropagation()
                    evt.preventDefault()

                    myHtmlPanelHost.toggleTask(taskOffset)
                }
            }

            val spanList = doc.getElementsByTagName("span")
            for (i in 0 until spanList.length) {
                val item = spanList.item(i) as Element
                val spanClass = item.getAttribute("class")
                if (spanClass == "task-item-closed" || spanClass == "task-item-open") {
                    (item as EventTarget).addEventListener("click", taskListener, false)
                }
            }

            if (myHtmlPanelHost.getRenderingProfile().previewSettings.synchronizeSourcePositionOnClick) {
                val clickListener = EventListener { evt ->
                    if (myProject.isDisposed) return@EventListener

                    if (myJSBridge.jsEventHandledBy != null) {
                        loggerScroll.debugOne(LOG) { "onClick: sync source to preview, default prevented by: ${myJSBridge.jsEventHandledBy}" }
                        myJSBridge.clearJSEventHandledBy()
                        return@EventListener
                    } else {
                        loggerScroll.debugOne(LOG) { "onClick: sync source to preview" }
                    }

                    var element: Node? = evt.target as Node
                    while (element != null) {
                        val srcPos = element.attributes?.getNamedItem("md-pos")
                        if (srcPos !== null) {
                            val startPos = srcPos.nodeValue.split('-', limit = 2)[0].toInt()
                            myHtmlPanelHost.synchronizeCaretPos(startPos)
                            break
                        }
                        element = element.parentNode as? Element
                    }
                }

                (doc as EventTarget).addEventListener("click", clickListener, false)
            }

            if (documentPath != null && projectComponent != null) {
                val images = doc.getElementsByTagName("img")

                for (i in 0 until images.length) {
                    val item: Node = images.item(i)
                    val src = item.attributes?.getNamedItem("src")
                    if (src != null) {
                        val imageSrc = src.nodeValue
                        if (!PathInfo.isURI(imageSrc) && PathInfo.isRelative(imageSrc) && !ImageUtils.isEncodedImage(imageSrc)) {
                            // change it to absolute
                            val imagePath = documentPath + imageSrc
                            val serial = projectComponent.getFileSerial(imagePath)
                            val serialQuery = if (serial > 0) "?$serial" else ""

                            src.nodeValue = fileUriPrefix + imagePath + serialQuery

                            for (handler in MdPreviewCustomizationProvider.EXTENSIONS.value) {
                                handler.adjustImageItem(item, serial)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun debug(startStop: Boolean) {
        if (_myJSBridge != null && !ourDebuggerChangingState.getAndSet(true)) {
            var handled = false
            if (myJSBridge.isDebuggerEnabled != startStop) {
                if (!myJSBridge.isDebuggerEnabled) {
                    for (handler in MdPreviewCustomizationProvider.EXTENSIONS.value) {
                        if (handler.launchDebugger(myProject, myJSBridge, ourDebuggerChangingState)) {
                            handled = true
                            break
                        }
                    }
                } else {
                    // need to stop
                    handled = true
                    myJSBridge.stopDebugServer { shutDown: Boolean? ->
                        // FIX: create notification of server stopped/shutdown
                        ourDebuggerChangingState.set(false)
                        if (shutDown == true) {
                            // server shut down
                        } else if (shutDown == false) {
                            // just disconnected
                        } else {
                            // have not idea, debugger was not enabled.
                        }
                    }
                }
            }

            if (!handled) {
                ourDebuggerChangingState.set(false)
            }
        }
    }

    override fun isDebugging(): Boolean {
        return _myJSBridge?.isDebugging ?: false
    }

    override fun isDebuggerEnabled(): Boolean {
        return (_myJSBridge?.isDebuggerEnabled ?: false) || ourDebuggerChangingState.get()
    }

    override fun isDebugBreakOnLoad(): Boolean {
        return false
    }

    override fun debugBreakOnLoad(breakOnLoad: Boolean, debugBreakInjectionOnLoad: Boolean) {
        _myJSBridge?.reloadPage(breakOnLoad, debugBreakInjectionOnLoad)
    }

    private val canDebug: Boolean
        get() {
            for (handler in MdPreviewCustomizationProvider.EXTENSIONS.value) {
                if (handler.canDebugPreview(myProject)) return true
            }
            return false;
        }

    override fun canDebug(): Boolean {
        return (_myJSBridge?.isDebuggerEnabled ?: false) || canDebug
    }

    override fun print() {
        if (!isWebViewInitialized) return

        myWebViewFxRunner.schedule("JavaFxHtml::print", WebViewFxRunner.Type.PAGE_INTERACTION) {
            val webView = myWebView

            val printerJob = PrinterJob.createPrinterJob()
            if (printerJob != null) {
                if (printerJob.showPrintDialog(myPanel.scene.window)) {
                    ProgressManager.getInstance().run(object : Task.Backgroundable(myProject, MdBundle.message("print.progress"), true, PerformInBackgroundOption.DEAF) {
                        override fun run(indicator: ProgressIndicator) {
                            var count = 1
                            indicator.isIndeterminate = false
                            indicator.text = myHtmlPanelHost.getVirtualFile().name
                            Thread.sleep(200)

                            Platform.runLater {
                                webView.engine.print(printerJob)
                                printerJob.endJob()
                            }

                            while (true) {
                                if (printerJob.jobStatus in arrayListOf(PrinterJob.JobStatus.DONE, PrinterJob.JobStatus.ERROR, PrinterJob.JobStatus.CANCELED)) {
                                    break
                                }
                                if (indicator.isCanceled) {
                                    printerJob.cancelJob()
                                    break
                                }

                                try {
                                    Thread.sleep((200 - count * 10).minLimit(100).toLong())
                                    count++
                                    indicator.fraction = (count - 1.0) / count
                                    // cannot use progress except from JavaFX thread, which makes it quite useless
                                    //                                    indicator.fraction = loadWorker.progress
                                } catch (e: InterruptedException) {
                                    break
                                }
                            }

                            indicator.fraction = 1.0
                            Thread.sleep(300)
                            indicator.stop()
                        }
                    })
                }
            }
        }
    }

    override fun canPrint(): Boolean {
        return true
    }

    internal fun pageReloadStarted() {
        myPageReloadTriggered = true
        myWebViewFxRunner.pageReloadTriggered() // reset state
        if (!myProject.isDisposed) {
            myWebViewFxRunner.schedule("JSBridge::pageReloadStarted.preparePage", WebViewFxRunner.Type.INITIALIZER) {
                preparePage()
            }
        }
    }

    // call backs from JavaScript will be handled by the bridge
    class JSBridge(javaHtmlPanel: JavaFxHtmlPanel, webView: WebView, instance: Int, stateProvider: JfxScriptStateProvider) : DevToolsDebuggerJsBridge(webView, webView.engine, instance, stateProvider, true) {

        private val panel: JavaFxHtmlPanel = javaHtmlPanel
        private val mdNavigatorJSBridge: MdNavigatorJsBridge

        init {
            mdNavigatorJSBridge = MdNavigatorJsBridgeDelegate(MdNavigatorJSBridgeImpl(panel, super.getJfxDebugProxyJsBridge()))
        }

        override fun pageReloadStarted() {
            if (panel.myProject.isDisposed) return
            panel.pageReloadStarted()
        }

        override fun pageLoadComplete() {
            if (panel.myProject.isDisposed) return
            LOG.debug { "[${panel.myInstance}] onDocumentLoaded" }

            // scroll to source it will set initialized
            panel.scrollToReference(true)

            (ApplicationManager.getApplication().messageBus.syncPublisher(WebViewDocumentLoaded.TOPIC) as WebViewDocumentLoaded).onDocumentLoaded(panel.myHtmlPanelHost.getVirtualFile())
        }

        override fun getJsBridgeHelperAsStream(): InputStream {
            var initResource = panel.getJsBridgeHelperScriptUrl()
            val pos = initResource.lastIndexOf('?')
            if (pos > 0) {
                initResource = initResource.substring(0, pos)
            }
            return FileInputStream(File(URI(initResource)))
        }

        // our customized version
        override fun getJfxDebugProxyJsBridge(): JfxDebugProxyJsBridge {
            return mdNavigatorJSBridge
        }

        private class MdNavigatorJSBridgeImpl(private val panel: JavaFxHtmlPanel, delegate: JfxDebugProxyJsBridge) : MdNavigatorJsBridge, JfxDebugProxyJsBridge by delegate {
            override fun toggleTask(pos: String) {
                if (panel.myProject.isDisposed) return
                panel.myHtmlPanelHost.toggleTask(pos)
            }
        }
    }

    companion object {
        internal var ourDebuggerChangingState = AtomicBoolean(false)

        private val LOG = Logger.getInstance("com.vladsch.md.nav.editor.javafx")
        private val LOG_JFX_DEBUGGER = Logger.getInstance("com.vladsch.md.nav.editor.javafx")
        private val loggerScroll = Logger.getInstance("com.vladsch.md.nav.editor.javafx-scroll")
        private var instances: Int = 0
        private val instanceMap = HashMap<String, Int>()

        internal fun getInstance(virtualFile: VirtualFile): Int {
            return instanceMap[virtualFile.path] ?: let {
                instanceMap[virtualFile.path] = ++instances
                instances
            }
        }

        fun runFX(r: Runnable) {
            IdeEventQueue.unsafeNonblockingExecute(r)
        }

        fun runFX(r: () -> Unit) {
            IdeEventQueue.unsafeNonblockingExecute(r)
        }

        val PAGE_SVG_REPLACE_PATTERN: Pattern = Pattern.compile("(<img[^>]+src=\"[^\"]+\\.)svg([^\"]*\"[^>]*>)")
        val PAGE_GIF_REPLACE_PATTERN: Pattern = Pattern.compile("(<img[^>]+src=\"[^\"]+\\.)gif([^\"]*\"[^>]*>)")
        val IMG_SVG_REPLACE_PATTERN: Pattern = Pattern.compile("([^\"]+\\.)svg([^\"]*)")
        val SCRIPT_REPLACE_PATTERN = "(<script[^>]+src=\")([^\"]*)(\"[^>]*>)".toRegex()
        val LINK_REPLACE_PATTERN = "(<(?:a|link)[^>]+href=\")([^\"]*)(\"[^>]*>)".toRegex()
        val SCRIPT_STATE_NAME = "jsState"
        var useNewAPI: Boolean? = null

        @JvmStatic
        fun prepareHtml(html: String, headTopText: String?, headBottomText: String?, bodyTopText: String?, bodyBottomText: String?): String {
            var result = html
            if (MdApplicationSettings.instance.documentSettings.disableGifImages) {
                result = PAGE_GIF_REPLACE_PATTERN.matcher(result).replaceAll("$1_gif_$2")
            }

            if (headTopText != null) {
                result = result.replace("</head>", "$headTopText</head>")
            }
            if (headBottomText != null) {
                result = result.replace("</head>", "$headBottomText</head>")
            }
            if (bodyTopText != null) {
                result = result.replace("<body>", "<body>$bodyTopText")
            }
            if (bodyBottomText != null) {
                result = result.replace("</body>", "$bodyBottomText</body>")
            }
            return result
        }
    }
}
