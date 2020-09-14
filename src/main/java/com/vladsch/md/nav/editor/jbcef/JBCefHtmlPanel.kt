// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.jbcef

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefJSQuery
import com.intellij.ui.jcef.JCEFHtmlPanel
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ArrayUtil
import com.vladsch.boxed.json.BoxedJsObject
import com.vladsch.boxed.json.BoxedJsString
import com.vladsch.boxed.json.BoxedJsValue
import com.vladsch.boxed.json.BoxedJson
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.javafx.webview.debugger.JfxScriptStateProvider
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.editor.HtmlPanelHost
import com.vladsch.md.nav.editor.PreviewEditorState
import com.vladsch.md.nav.editor.api.MdPreviewCustomizationProvider
import com.vladsch.md.nav.editor.javafx.WebViewDocumentLoaded
import com.vladsch.md.nav.editor.util.HtmlPanel
import com.vladsch.md.nav.editor.util.HtmlResource
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import com.vladsch.md.nav.settings.*
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.plugin.util.SemanticVersion
import com.vladsch.plugin.util.debugOne
import com.vladsch.plugin.util.ifElse
import com.vladsch.plugin.util.suffixWith
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefContextMenuParams
import org.cef.callback.CefMenuModel
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefLoadHandlerAdapter
import org.jdom.Text
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import javax.swing.JComponent

// Instantiated by reflection
@SuppressWarnings("unused")
class JBCefHtmlPanel(project: Project, htmlPanelHost: HtmlPanelHost) : HtmlPanel(project, htmlPanelHost) {

    private val myInstance: Int = getInstance(myHtmlPanelHost.getVirtualFile())
    private val myPanel: JCEFHtmlPanel

    init {
        val path = myHtmlPanelHost.getVirtualFile().path
        val uri = PathInfo.fileURIPrefix(path) + path
        myPanel = object : JCEFHtmlPanel(uri) {
            override fun createDefaultContextMenuHandler(): DefaultCefContextMenuHandler {
                return object : DefaultCefContextMenuHandler(true) {
                    override fun onBeforeContextMenu(browser: CefBrowser, frame: CefFrame, params: CefContextMenuParams, model: CefMenuModel) {
                        model.clear()
                        super.onBeforeContextMenu(browser, frame, params, model)
                    }
                }
            }
        }
    }

    private val myWebView: CefBrowser get() = myPanel.cefBrowser
    private val myWebViewFxRunner = JBCefRunner(myPanel)
    private var myInlineCss: String? = null
    private var myCssUris = ArrayUtil.EMPTY_STRING_ARRAY
    private var myLastRawHtml = ""
    private var myLastPageUrl = ""

    //    private var myHtmlPanelHost: HtmlPanelHost? = null
    private var myScrollTag: String = ""
    private var myScrollAttribute: String = ""
    private var myScrollReference: String = ""
    private var myOnTypingUpdate = AtomicBoolean(false)
    private var myState = PreviewEditorState()
    private var myVerticalLocation: Float? = null
    private var myDebugFileSerial = 0
    private var myAllowContextMenu = AtomicBoolean(true)
    private var myHtmlFile = ""
    private var myPageReloadTriggered = false
    private var myScriptState = BoxedJson.of()

    //    internal val myInitActions = ArrayList<Runnable>()
    private var myInitialHtml: String = ""

    private val isWebViewInitialized: Boolean
        get() = true

    private val myJSBridge: JSBridge
    private val myCefLoadHandler: CefLoadHandler?
    //    private val myPanelWrapper: JComponent = JPanel(BorderLayout())

    override fun dispose() {
        Disposer.dispose(myWebViewFxRunner)

        if (myCefLoadHandler != null) myPanel.jbCefClient.removeLoadHandler(myCefLoadHandler, myWebView);
        Disposer.dispose(myJSBridge);

        if (myHtmlFile.isNotEmpty()) {
            val file = File(myHtmlFile)
            if (file.exists()) {
                file.delete()
            }
            myHtmlFile = ""
        }
    }

    init {
        //        myPanelWrapper.background = JBColor.background()

        updateViewOptions(myWebView, MdProjectSettings.getInstance(myProject).previewSettings)

        myJSBridge = JSBridge(this, object : JfxScriptStateProvider {
            override fun getState(): BoxedJsObject {
                return myScriptState
            }

            override fun setState(state: BoxedJsObject) {
                // nothing to do, state already mutable
                //return myScriptState
            }
        })

        myPanel.jbCefClient.addLoadHandler(object : CefLoadHandlerAdapter() {
            override fun onLoadingStateChange(browser: CefBrowser?, isLoading: Boolean, canGoBack: Boolean, canGoForward: Boolean) {
                myJSBridge.onLoadingStateChange(browser, isLoading, canGoBack, canGoForward)
            }
        }.also { myCefLoadHandler = it }, myWebView)
        //        myCefLoadHandler = null

        subscribeSettingChanges(myWebView)
        myWebViewFxRunner.panelInitialized()

        if (myInitialHtml.isNotEmpty()) {
            setHtml(myInitialHtml)
            myInitialHtml = ""
        }

        //        myPanelWrapper.add(myPanel.component, BorderLayout.CENTER)
        //        myPanelWrapper.repaint()
    }

    private fun subscribeSettingChanges(webView: CefBrowser) {
        // subscribe to application level settings changes,
        // subscribe to project settings notifications
        val messageBusConnection = ApplicationManager.getApplication().messageBus.connect(this)
        messageBusConnection.subscribe(SettingsChangedListener.TOPIC, SettingsChangedListener {
            updateViewOptions(webView, MdProjectSettings.getInstance(myProject).previewSettings)
        })

        val projectMessageBusConnection = myProject.messageBus.connect(this)
        val profilesChangedListener = object : ProfileManagerChangeListener {
            override fun onSettingsLoaded(manager: RenderingProfileManager) {
            }

            override fun onSettingsChange(manager: RenderingProfileManager) {
                updateViewOptions(webView, myHtmlPanelHost.getRenderingProfile().previewSettings)
            }
        }
        projectMessageBusConnection.subscribe(ProfileManagerChangeListener.TOPIC, profilesChangedListener)
    }

    private fun updateViewOptions(view: CefBrowser, previewSettings: MdPreviewSettings) {
        if (myProject.isDisposed) return

        //        val typeToSet: FontSmoothingType =
        //            if (previewSettings.useGrayscaleRendering) {
        //                FontSmoothingType.GRAY
        //            } else {
        //                FontSmoothingType.LCD
        //            }
        //
        //        val fontSmoothingTypeProperty = view.fontSmoothingTypeProperty()
        //        if (fontSmoothingTypeProperty.value != typeToSet) {
        //            fontSmoothingTypeProperty.value = typeToSet
        //        }
        //
        // See: https://magpcss.org/ceforum/viewtopic.php?t=11491 for calculation of zoomLevel from scale
        //        val displayScale = JBUIScale.sysScale()
        val scale: Double = previewSettings.zoomFactor * MdApplicationSettings.instance.documentSettings.zoomFactor /** displayScale*/
        val zoomLevel = Math.log(scale) / Math.log(1.2)
        view.zoomLevel = zoomLevel
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
        get() = myPanel.component

    override fun setCSS(inlineCss: String?, fileUris: Array<String>) {
        myInlineCss = inlineCss
        myCssUris = fileUris
        setHtml(myLastRawHtml)
    }

    override fun setHtml(html: String): Boolean {
        if (myProject.isDisposed) return true
        //        if (_myPanel == null || _myWebView == null || _myJSBridge == null) {
        //            // not initialized yet, save for later
        //            myInitialHtml = html
        //            return true
        //        }

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
        myWebViewFxRunner.schedule("JBCefHtmlPanel::setHtml.loadContent", JBCefRunner.Type.LOADER) {
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
                        myPanel.loadURL(pageFileUrl.second)
                        LOG.debug { "[$myInstance] updated content" }
                        alternatePage = true
                        break
                    }
                }

                if (!alternatePage) {
                    pageReloading()
                    val fileUri = fileUriPrefix + myHtmlPanelHost.getVirtualFile().path
                    myPanel.loadHTML(result, fileUri)
                    LOG.debug { "[$myInstance] updated content" }
                }

                myWebViewFxRunner.schedule("JBCefHtmlPanel::setHtml.preparePage", JBCefRunner.Type.INITIALIZER) {
                    preparePage()
                }
            }
        }

        return true
    }

    internal fun getJsBridgeHelperScriptUrl(): String {
        return getInjectedScriptUrl("/com/vladsch/md/nav/markdown-navigator-jcef.js")
    }

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
        myWebViewFxRunner.schedule("JBCefHtmlPanel::setPageUrl", JBCefRunner.Type.LOADER) {
            if (!myProject.isDisposed) {
                updateViewOptions(myWebView, myHtmlPanelHost.getRenderingProfile().previewSettings)

                pageReloading()
                myPanel.loadURL(url)
                LOG.debug { "[$myInstance] updated url: $url" }
            }
        }
        return true
    }

    private fun pageReloading() {
        //myJSBridge.pageReloading()
    }

    fun launchExternalLink(href: String): Boolean {
        if (myProject.isDisposed) return true
        return myHtmlPanelHost.launchExternalLink(href)
    }

    fun scrollToReference(onLoad: Boolean) {
        val previewSettings = myHtmlPanelHost.getRenderingProfile().previewSettings
        if (previewSettings.synchronizePreviewPosition /*&& MultiMarkdownPlugin.isLicensed*/) {
            myWebViewFxRunner.schedule("JBCefHtmlPanel::scrollToReference", if (onLoad) JBCefRunner.Type.INITIALIZER else JBCefRunner.Type.PAGE_INTERACTION) {
                if (!myProject.isDisposed) {
                    if (onLoad) {
                        myWebViewFxRunner.schedule("JBCefHtmlPanel::scrollToReference onLoad dummy", JBCefRunner.Type.PAGE_INTERACTION, { })
                        myWebViewFxRunner.setInitialized()
                    }

                    val highlightEnabled = myHtmlPanelHost.isHighlightEnabled()
                    val onTypingUpdate = myOnTypingUpdate.get()
                    myOnTypingUpdate.set(false)
                    val highlightOnTyping = previewSettings.highlightOnTyping
                    val highlightFadeOut = previewSettings.highlightFadeOut * 1000
                    val s = "scrollToSourcePosition($myVerticalLocation,'$myScrollTag','$myScrollAttribute','$myScrollReference',$highlightEnabled,$onTypingUpdate,$highlightOnTyping,$highlightFadeOut);"
                    LOG.debugOne(loggerScroll) { "[$myInstance] executing scroll: '$s'" }
                    myWebView.executeJavaScript(s, myWebView.url, 0)
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

    /*
        // from: https://stackoverflow.com/questions/38391522/javafx-webview-context-menu/38413661
        private fun createContextMenu(webView: JBCefBrowser) {

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
    }

    override fun print() {
        if (!isWebViewInitialized) return

        myWebViewFxRunner.schedule("JBCefHtmlPanel::print", JBCefRunner.Type.PAGE_INTERACTION) {
            myWebView.print()
        }
    }

    override fun canPrint(): Boolean {
        return true
    }

    internal fun pageReloadStarted() {
        myPageReloadTriggered = true
        myWebViewFxRunner.pageReloadTriggered() // reset state
        if (!myProject.isDisposed) {
            myWebViewFxRunner.schedule("JSBridge::pageReloadStarted.preparePage", JBCefRunner.Type.INITIALIZER) {
                preparePage()
            }
        }
    }

    // call backs from JavaScript will be handled by the bridge
    class JSBridge(javaHtmlPanel: JBCefHtmlPanel, val stateProvider: JfxScriptStateProvider) : CefLoadHandlerAdapter(), Disposable {

        val stateString: String
            get() {
                // output all the state vars
                val sb = StringBuilder()
                sb.append("var markdownNavigator;\n")
                sb.append("(function () {\n")
                try {
                    sb.append("let state = {\n")
                    for (entry in stateProvider.state.entries) {
                        sb.append("  \"").append(entry.key).append("\": ").append(entry.value.toString()).append(",\n")
                    }
                    sb.append("};\n")
                    sb.append("markdownNavigator.getState = function(stateName) {\n")
                    sb.append("  return state[stateName];\n")
                    sb.append("};\n")
                } catch (e: IOException) {
                    LOG.error("appendStateString: exception", e)
                }
                sb.append("})()\n")

                return sb.toString()
            }

        private val panel: JBCefHtmlPanel = javaHtmlPanel
        private val myJSQuerySetState: JBCefJSQuery = JBCefJSQuery.create(panel.myPanel)
        private val myJSQueryGetState: JBCefJSQuery = JBCefJSQuery.create(panel.myPanel)
        private val myJSQueryOpenInBrowser: JBCefJSQuery = JBCefJSQuery.create(panel.myPanel)
        private val myJSQueryToggleTask: JBCefJSQuery = JBCefJSQuery.create(panel.myPanel)
        private val myJSQuerySyncSource: JBCefJSQuery = JBCefJSQuery.create(panel.myPanel)

        init {
            myJSQuerySetState.addHandler { state: String ->
                try {
                    val jsObject = BoxedJson.objectFrom(state)
                    val stateName: BoxedJsString = jsObject.getJsString("stateName")
                    val stateValue: BoxedJsValue = jsObject["stateValue"]
                    if (stateName.isValid) {
                        if (stateValue.isValid && !stateValue.isNull) {
                            stateProvider.state[stateName.string] = stateValue.jsonValue()
                        } else {
                            stateProvider.state.remove(stateName.string)
                        }
                    }
                } catch (ignored: Exception) {
                }
                null
            }

            myJSQueryGetState.addHandler { stateName: String ->
                try {
                    val stateValue: BoxedJsValue = stateProvider.state[stateName]
                    if (stateValue.isValid) {
                        val json = stateValue.toString()
                        return@addHandler JBCefJSQuery.Response(json)
                    }
                } catch (ignored: Exception) {
                }
                null
            }

            myJSQueryOpenInBrowser.addHandler { link: String? ->
                if (link != null) {
                    panel.launchExternalLink(link)
                }
                null
            }

            myJSQueryToggleTask.addHandler { taskOffset: String? ->
                if (taskOffset != null && taskOffset.isNotEmpty()) {
                    panel.myHtmlPanelHost.toggleTask(taskOffset)
                }
                null
            }

            myJSQuerySyncSource.addHandler { srcPos: String? ->
                if (panel.myHtmlPanelHost.getRenderingProfile().previewSettings.synchronizeSourcePositionOnClick) {
                    if (srcPos !== null && srcPos.isNotEmpty()) {
                        val startPos = srcPos.split('-', limit = 2)[0].toInt()
                        panel.myHtmlPanelHost.synchronizeCaretPos(startPos)
                    }
                }
                null
            }
        }

        fun getCefBrowser(): CefBrowser = panel.myWebView

        override fun dispose() {
            Disposer.dispose(myJSQuerySetState);
            Disposer.dispose(myJSQueryGetState);
            Disposer.dispose(myJSQueryOpenInBrowser);
            Disposer.dispose(myJSQueryToggleTask);
            Disposer.dispose(myJSQuerySyncSource);
        }

        override fun onLoadingStateChange(browser: CefBrowser?, isLoading: Boolean, canGoBack: Boolean, canGoForward: Boolean) {
            if (panel.myProject.isDisposed) return

            if (isLoading) {
                LOG.debug { "[${panel.myInstance}] pageLoading" }

                panel.pageReloadStarted()
            } else {
                // loading done
                LOG.debug { "[${panel.myInstance}] onDocumentLoaded" }

                if (getCefBrowser().zoomLevel == 0.0) {
                    // NOTE: zoom needs to be set when page is loading
                    panel.updateViewOptions(getCefBrowser(), panel.myHtmlPanelHost.getRenderingProfile().previewSettings)
                }

                getCefBrowser().executeJavaScript(
                    """var markdownNavigator; 
(function () {    
    if (!markdownNavigator) {
       markdownNavigator = { };
    }
    
    markdownNavigator.setStateString = function(stateString) { ${myJSQuerySetState.inject("stateString")} };
    markdownNavigator.getStateString = function(stateName) { ${myJSQueryGetState.inject("stateName")} };
    markdownNavigator.openLinkInBrowser = function(link) {${myJSQueryOpenInBrowser.inject("link")}};
    markdownNavigator.toggleTask = function(taskOffset) {${myJSQueryToggleTask.inject("taskOffset")}};
    markdownNavigator.synchronizeCaretPos = function(sourceOffset) {${myJSQuerySyncSource.inject("sourceOffset")}};
    
    markdownNavigator.runJsBridge();
})();
""",
                    getCefBrowser().url, 0)
                
                // scroll to source it will set initialized
                panel.scrollToReference(true)

                (ApplicationManager.getApplication().messageBus.syncPublisher(WebViewDocumentLoaded.TOPIC) as WebViewDocumentLoaded).onDocumentLoaded(panel.myHtmlPanelHost.getVirtualFile())
            }
        }

        fun reloadPage(breakOnLoad: Boolean, debugBreakInjectionOnLoad: Boolean) {
            panel.myWebView.reloadIgnoreCache()
        }

        fun stopDebugServer(function: () -> Unit) {
            function()
        }
    }

    companion object {
        internal var ourDebuggerChangingState = AtomicBoolean(false)

        private val LOG = Logger.getInstance("com.vladsch.md.nav.editor.jbcef")
        private val LOG_JFX_DEBUGGER = Logger.getInstance("com.vladsch.md.nav.editor.jbcef")
        private val loggerScroll = Logger.getInstance("com.vladsch.md.nav.editor.jbcef-scroll")
        private var instances: Int = 0
        private val instanceMap = HashMap<String, Int>()

        internal fun getInstance(virtualFile: VirtualFile): Int {
            return instanceMap[virtualFile.path] ?: let {
                instanceMap[virtualFile.path] = ++instances
                instances
            }
        }

        val PAGE_SVG_REPLACE_PATTERN: Pattern = Pattern.compile("(<img[^>]+src=\"[^\"]+\\.)svg([^\"]*\"[^>]*>)")
        val PAGE_GIF_REPLACE_PATTERN: Pattern = Pattern.compile("(<img[^>]+src=\"[^\"]+\\.)gif([^\"]*\"[^>]*>)")
        val IMG_SVG_REPLACE_PATTERN: Pattern = Pattern.compile("([^\"]+\\.)svg([^\"]*)")
        val SCRIPT_REPLACE_PATTERN: Regex = "(<script[^>]+src=\")([^\"]*)(\"[^>]*>)".toRegex()
        val LINK_REPLACE_PATTERN: Regex = "(<(?:a|link)[^>]+href=\")([^\"]*)(\"[^>]*>)".toRegex()
        const val SCRIPT_STATE_NAME: String = "jsState"
        var useNewAPI: Boolean? = null

        private val ourClassUrl: String

        init {
            var url = "about:blank"
            try {
                url = JBCefHtmlPanel::class.java.getResource(JBCefHtmlPanel::class.java.simpleName + ".class").toExternalForm()
            } catch (ignored: Exception) {
            }
            ourClassUrl = url
        }

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
