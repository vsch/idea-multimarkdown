// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.find.EditorSearchSession
import com.intellij.find.FindModel
import com.intellij.find.SearchReplaceComponent
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.SelectionEvent
import com.intellij.openapi.editor.event.SelectionListener
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.util.Alarm
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.md.nav.*
import com.vladsch.md.nav.editor.api.MdPreviewCustomizationProvider
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.jbcef.JBCefHtmlPanelProvider
import com.vladsch.md.nav.editor.resources.JavaFxHtmlCssProvider
import com.vladsch.md.nav.editor.split.SplitFileEditor
import com.vladsch.md.nav.editor.split.SplitPreviewChangeListener
import com.vladsch.md.nav.editor.swing.SwingHtmlPanelProvider
import com.vladsch.md.nav.editor.text.TextHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlGeneratorProvider
import com.vladsch.md.nav.editor.util.HtmlPanel
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlPanelProvider.AvailabilityInfo
import com.vladsch.md.nav.settings.*
import com.vladsch.md.nav.settings.MdProjectSettings.Companion.getInstance
import com.vladsch.md.nav.util.*
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.TimeIt
import com.vladsch.plugin.util.debug
import java.awt.BorderLayout
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.beans.PropertyChangeListener
import java.lang.reflect.Method
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.swing.JComponent
import javax.swing.JPanel

abstract class PreviewFileEditorBase constructor(protected val myProject: Project, protected val myFile: VirtualFile) : UserDataHolderBase(), FileEditor, HtmlPanelHost {

    private val myHtmlPanelWrapper: JPanel = JPanel(BorderLayout())
    private var myPanel: HtmlPanel? = null
    protected var myLastPanelProviderInfo: HtmlPanelProvider.Info? = null
    protected val myDocument: Document? = FileDocumentManager.getInstance().getDocument(myFile)
    private val myDocumentAlarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
    private val mySwingAlarm = Alarm(Alarm.ThreadToUse.SWING_THREAD, this)
    private var myLastHtmlOrRefreshRequest: Runnable? = null
    private var myLastScrollOffset: Int = 0
    private var myLastScrollLineOffsets: Range = Range.of(0, 0)
    private var myLastOffsetVertical: Float? = null
    protected var myLastRenderedHtml: String = ""
    private var myLastRenderedUrl = ""
    private var myLastUpdatedModificationStamp = 0L

    protected var myRenderingProfile: MdRenderingProfile
    private var mySplitEditorLayout: SplitFileEditor.SplitEditorLayout

    init {
        myRenderingProfile = MdRenderingProfileManager.getInstance(myProject).getRenderingProfile(myFile)
        mySplitEditorLayout = myRenderingProfile.previewSettings.splitEditorLayout
    }

    protected var myHtmlTagRanges: List<TagRange> = ArrayList()
    private var inSettingsChange: Boolean = false
    protected var mySplitEditorPreviewType: SplitFileEditor.SplitEditorPreviewType = myRenderingProfile.previewSettings.splitEditorPreviewType
    private var myPreviewEditorState: PreviewEditorState = PreviewEditorState()

    protected var myLastHtmlProviderInfo: HtmlGeneratorProvider.Info? = null
    protected var myFirstEditorCounterpart: FileEditor? = null
    private var gotFirstEditor = false
    protected var myDocumentIsModified: Boolean = false
    private val panelSetupTimeoutMs: Long = 10L
    private var renderingDelayMs: Long = MdApplicationSettings.instance.documentSettings.typingUpdateDelay.toLong()
    protected var myEditor: Editor? = null
    private val mySearchReplaceListener: SearchReplaceComponent.Listener
    private val myFindModelObserver: FindModel.FindModelObserver
    private val myComponentListener: ComponentListener
    private val mySelectionListener: SelectionListener
    private var myShowingSearch = false
    protected var myHighlightEnabled: Boolean = true
    private var myEditorListenerRemoved = false

    init {
        renderingLogger.debug { "initialized rendering profile: '${myRenderingProfile.profileName}' panel info: ${myRenderingProfile.previewSettings.htmlPanelProviderInfo.providerId}, css: ${myRenderingProfile.cssSettings.cssProviderInfo.providerId}" }
    }

    fun print() {
        myPanel?.print()
    }

    fun canPrint(): Boolean = myPanel?.canPrint() ?: false

    fun debug(startStop: Boolean) {
        myPanel?.debug(startStop)
    }

    fun canDebug(): Boolean = myPanel?.canDebug() ?: false

    fun isDebuggerEnabled(): Boolean = myPanel?.isDebuggerEnabled() ?: false
    fun isDebugging(): Boolean = myPanel?.isDebugging() ?: false
    fun isDebugBreakOnLoad(): Boolean = myPanel?.isDebugBreakOnLoad() ?: false
    fun debugBreakOnLoad(debugBreakOnLoad: Boolean, debugBreakInjectionOnLoad: Boolean) {
        myPanel?.debugBreakOnLoad(debugBreakOnLoad, debugBreakInjectionOnLoad)
    }

    override fun getRenderingProfile(): MdRenderingProfile = myRenderingProfile

    override fun isHighlightEnabled(): Boolean {
        return myHighlightEnabled /*&& myIsLicensed*/ && myRenderingProfile.previewSettings.highlightPreviewTypeEnum != HighlightPreviewType.NONE
    }

    open fun canHighlight(): Boolean {
        return true
    }

    override fun toggleTask(pos: String) {
        // pos is start-end
        val taskOffset = pos.toIntOrNull() ?: return
        if (myProject.isDisposed) return

        ApplicationManager.getApplication().invokeLater {
            if (myProject.isDisposed) return@invokeLater

            WriteCommandAction.runWriteCommandAction(myProject) {
                if (myProject.isDisposed) return@runWriteCommandAction

                val textLength = myDocument?.textLength ?: return@runWriteCommandAction
                val charSequence = myDocument.charsSequence
                if (taskOffset > 0 && taskOffset + 2 < textLength && charSequence[taskOffset - 1] == '[' && charSequence[taskOffset + 1] == ']') {
                    myDocument.replaceString(taskOffset, taskOffset + 1, if (charSequence[taskOffset] == ' ') "x" else " ")
                }
            }
        }
    }

    fun monitorDocument() {
        if (myDocumentAlarm.isDisposed) return

        var alternatePageUrlEnabled = false
        for (provider in MdPreviewCustomizationProvider.EXTENSIONS.value) {
            if (provider.isAlternateUrlEnabled(myRenderingProfile)) {
                alternatePageUrlEnabled = true
                break
            }
        }

        if (!alternatePageUrlEnabled) return

        myDocumentAlarm.cancelAllRequests()
        myDocumentAlarm.addRequest({
            ApplicationManager.getApplication().invokeLater {
                val editorCounterpart = myFirstEditorCounterpart
                if (myDocumentIsModified) {
                    if (editorCounterpart != null) {
                        if (!editorCounterpart.isModified) {
                            detailLogger.debug { "clearing document modified flag" }
                            myDocumentIsModified = false
                            updateHtml()
                        }
                    }
                    monitorDocument()
                }
            }
        }, if (myDocumentIsModified) MODIFIED_DOCUMENT_TIMEOUT_MS else UNMODIFIED_DOCUMENT_TIMEOUT_MS)
    }

    private fun previewSettingsValidation() {
        if (myRenderingProfile.previewSettings.htmlPanelProviderInfo == SwingHtmlPanelProvider.INFO) {
            if (myRenderingProfile.cssSettings.cssProviderInfo == JavaFxHtmlCssProvider.INFO) {
                // need to change the stylesheets
                renderingLogger.debug { "previewSettingsValidation: changing JavaFx CSS to swing, '${myRenderingProfile.profileName}' panel info: ${myRenderingProfile.previewSettings.htmlPanelProviderInfo.providerId}, css: ${myRenderingProfile.cssSettings.cssProviderInfo.providerId}" }
                myRenderingProfile = myRenderingProfile.changeToProvider(JavaFxHtmlPanelProvider.INFO, SwingHtmlPanelProvider.INFO)
                renderingLogger.debug { "previewSettingsValidation: changed JavaFx CSS to swing, '${myRenderingProfile.profileName}' panel info: ${myRenderingProfile.previewSettings.htmlPanelProviderInfo.providerId}, css: ${myRenderingProfile.cssSettings.cssProviderInfo.providerId}" }
            }
        }
    }

    private fun layoutSettingValidation() {
        if (mySplitEditorPreviewType == SplitFileEditor.SplitEditorPreviewType.NONE) {
            if (mySplitEditorLayout != SplitFileEditor.SplitEditorLayout.FIRST) {
                mySplitEditorPreviewType = SplitFileEditor.SplitEditorPreviewType.PREVIEW
            }
        }
    }

    init {
        myDocument?.addDocumentListener(object : DocumentListener {

            override fun beforeDocumentChange(e: DocumentEvent) {
                if (!gotFirstEditor) {
                    // time to get our counterpart
                    val splitEditor = getUserData(SplitFileEditor.PARENT_SPLIT_KEY)
                    gotFirstEditor = true
                    myFirstEditorCounterpart = splitEditor?.mainEditor
                }
            }

            override fun documentChanged(e: DocumentEvent) {
                myDocumentIsModified = true
                monitorDocument()
                updateHtml()
            }
        }, this)

        val messageBusConnection = ApplicationManager.getApplication().messageBus.connect(this)
        val projectMessageBusConnection = myProject.messageBus.connect(this)

        val imageFileChangedListener = object : MdProjectComponent.FileChangedListener {
            override fun onFilesChanged() {
                updateHtml()
            }
        }

        projectMessageBusConnection.subscribe(MdProjectComponent.FileChangedListener.TOPIC, imageFileChangedListener)

        val settingsChangedListener = ProjectSettingsChangedListener { project, _ ->
            renderingLogger.debug { "OnProjectSettingsChange updateRenderingProfile: $project, $inSettingsChange, '${myRenderingProfile.profileName}' ${myRenderingProfile.previewSettings.htmlPanelProviderInfo.providerId}" }
            updateRenderingProfile(project)
        }

        messageBusConnection.subscribe(SettingsChangedListener.TOPIC, object : SettingsChangedListener {
            override fun onSettingsChange(settings: MdApplicationSettings) {
                val documentSettings = settings.documentSettings
                renderingDelayMs = documentSettings.typingUpdateDelay.toLong()
                updateGutterIcons()
            }
        })

        projectMessageBusConnection.subscribe(ProjectSettingsChangedListener.TOPIC, settingsChangedListener)
        val profilesChangedListener = object : ProfileManagerChangeListener {
            override fun onSettingsLoaded(manager: RenderingProfileManager) {
                //                renderingLogger.debug { "ProfileManager onSettingsLoaded updateRenderingProfile: ${manager.project}, $inSettingsChange, ${myRenderingProfile.previewSettings.htmlPanelProviderInfo.providerId}" }
                //                updateRenderingProfile(manager.project)
            }

            override fun onSettingsChange(manager: RenderingProfileManager) {
                renderingLogger.debug { "ProfileManager onSettingsChanged updateRenderingProfile: '${myRenderingProfile.profileName}' ${manager.project}, $inSettingsChange, ${myRenderingProfile.previewSettings.htmlPanelProviderInfo.providerId}" }
                updateRenderingProfile(manager.project)
            }
        }
        projectMessageBusConnection.subscribe(ProfileManagerChangeListener.TOPIC, profilesChangedListener)

        val editorBase = this
        val previewChangedListener = SplitPreviewChangeListener { editorPreview, editorLayout, forEditor ->
            if (editorBase === forEditor) {
                //                val updateNeeded = getSplitEditorPreview(mySplitEditorPreviewType, mySplitEditorLayout) != getSplitEditorPreview(editorPreview, editorLayout)
                mySplitEditorPreviewType = editorPreview
                mySplitEditorLayout = editorLayout
                layoutSettingValidation()

                if (!setUpPanel()) updateHtml()
            }
        }
        messageBusConnection.subscribe(SplitFileEditor.PREVIEW_CHANGE, previewChangedListener)

        projectMessageBusConnection.subscribe(MdRepoChangeListener.TOPIC, MdRepoChangeListener {
            ApplicationManager.getApplication().invokeLater {
                updateHtml()
                detailLogger.debug { ("OnRepoChanged()") }
            }
        })

        mySearchReplaceListener = object : SearchReplaceComponent.Listener {
            override fun multilineStateChanged() {
                if (canHighlight() && myRenderingProfile.previewSettings.showSearchHighlightsInPreview) {
                    updateHtml()
                }
            }

            override fun searchFieldDocumentChanged() {
                if (canHighlight() && myRenderingProfile.previewSettings.showSearchHighlightsInPreview) {
                    updateHtml()
                }
            }

            override fun replaceFieldDocumentChanged() {
            }
        }

        myFindModelObserver = FindModel.FindModelObserver {
            if (canHighlight() && myRenderingProfile.previewSettings.showSearchHighlightsInPreview) {
                updateHtml()
            }
        }

        myComponentListener = object : ComponentListener {
            override fun componentMoved(e: ComponentEvent?) {
                checkEditorSearchComponent()
            }

            override fun componentResized(e: ComponentEvent?) {
                checkEditorSearchComponent()
            }

            override fun componentHidden(e: ComponentEvent?) {
            }

            override fun componentShown(e: ComponentEvent?) {
            }
        }

        mySelectionListener = object : SelectionListener {
            override fun selectionChanged(e: SelectionEvent) {
                if (canHighlight() && myRenderingProfile.previewSettings.showSelectionInPreview) {
                    updateHtml()
                }
            }
        }

        setUpPanel()
    }

    private fun updateRenderingProfile(project: Project) {
        if (project === myProject) {
            renderingLogger.debug { "updateRenderingProfile: $project, $inSettingsChange, '${myRenderingProfile.profileName}' ${myRenderingProfile.previewSettings.htmlPanelProviderInfo.providerId}" }

            if (!inSettingsChange) {
                inSettingsChange = true
                val highlightType = myRenderingProfile.previewSettings.highlightPreviewTypeEnum
                myRenderingProfile = MdRenderingProfileManager.getInstance(myProject).getRenderingProfile(myFile)
                previewSettingsValidation()

                renderingLogger.debug { "after update: '${myRenderingProfile.profileName}' ${myRenderingProfile.previewSettings.htmlPanelProviderInfo.providerId}" }

                val updateHtml = !setUpPanel() || highlightType != myRenderingProfile.previewSettings.highlightPreviewTypeEnum /*&& myIsLicensed*/
                inSettingsChange = false
                if (updateHtml) updateHtml()
            } else {
                renderingLogger.debug { "skipped already in update" }
            }
        }
    }

    private fun updateGutterIcons() {
        val editor = myEditor ?: return
        val documentSettings = MdApplicationSettings.instance.documentSettings
        val enableGutters = documentSettings.iconGutters

        val settings = editor.settings
        if (settings.isLineMarkerAreaShown != enableGutters) {
            settings.isLineMarkerAreaShown = enableGutters
        }
    }

    private fun checkEditorSearchComponent() {
        val editor = myEditor ?: return
        val searchSession = EditorSearchSession.get(editor)
        val showingSearch = searchSession != null

        if (searchSession != null) {
            searchSession.findModel.addObserver(myFindModelObserver)
            searchSession.component.addListener(mySearchReplaceListener)
        }

        if (showingSearch != myShowingSearch) {
            if (myShowingSearch || myRenderingProfile.previewSettings.showSearchHighlightsInPreview) {
                updateHtml()
            }
        }
    }

    fun getSplitEditorPreview(value: SplitFileEditor.SplitEditorPreviewType, layout: SplitFileEditor.SplitEditorLayout): SplitFileEditor.SplitEditorPreviewType {
        return if (layout != SplitFileEditor.SplitEditorLayout.FIRST) value
        else SplitFileEditor.SplitEditorPreviewType.NONE
    }

    override fun synchronizeCaretPos(offset: Int) {
        if (myRenderingProfile.previewSettings.synchronizeSourcePositionOnClick) {
            ApplicationManager.getApplication().invokeLater {
                synchronizeCaretPosRaw(offset)
            }
        }
    }

    private fun synchronizeCaretPosRaw(offset: Int) {
        val textLength = myDocument?.textLength ?: 0
        if (textLength > offset) {
            val fileEditor = FileEditorManager.getInstance(myProject).getSelectedEditor(myFile)
            val textEditor = (fileEditor as? SplitFileEditor<*, *>)?.mainEditor as? TextEditor
            if (fileEditor != null && textEditor != null) {
                textEditor.editor.caretModel.moveToOffset(offset)
                textEditor.editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
                val focusComponent = textEditor.preferredFocusedComponent ?: textEditor.component
                IdeFocusManager.findInstanceByComponent(focusComponent).requestFocus(focusComponent, true)
            }
        }
    }

    override fun getComponent(): JComponent {
        return myHtmlPanelWrapper
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return myPanel?.component
    }

    override fun getName(): String {
        return "Markdown HTML Preview"
    }

    override fun getVirtualFile(): VirtualFile {
        return myFile
    }

    override fun getState(level: FileEditorStateLevel): FileEditorState {
        val htmlPanel = myPanel
        if (htmlPanel != null) {
            myPreviewEditorState = htmlPanel.getState()
        }
        return myPreviewEditorState
    }

    override fun setState(state: FileEditorState) {
        if (state is PreviewEditorState) {
            myPreviewEditorState = state
            myPanel?.setState(myPreviewEditorState)
        }
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun selectNotify() {
        if (myProject.isDisposed) return
        updateHtml()
    }

    private fun setUpPanel(): Boolean {
        val newPanelProvider = retrievePanelProvider() ?: return false

        if (!mySwingAlarm.isDisposed) mySwingAlarm.addRequest({
            val newPanel = newPanelProvider.createHtmlPanel(myProject, this)
            val oldPanel = myPanel
            if (oldPanel != null) {
                myHtmlPanelWrapper.remove(oldPanel.component)
                Disposer.dispose(oldPanel)
            }
            myPanel = newPanel
            myHtmlPanelWrapper.add(newPanel.component, BorderLayout.CENTER)
            //newPanel.setHtmlPanelHost(this)
            newPanel.setState(myPreviewEditorState)
            myHtmlPanelWrapper.repaint()
            updateHtml()
        }, /*if (myPanel == null) 1L else */panelSetupTimeoutMs, ModalityState.stateForComponent(myHtmlPanelWrapper))

        return true
    }

    private fun retrievePanelProvider(): HtmlPanelProvider? {
        var providerInfo = when (getSplitEditorPreview(mySplitEditorPreviewType, mySplitEditorLayout)) {
            SplitFileEditor.SplitEditorPreviewType.PREVIEW -> myRenderingProfile.previewSettings.htmlPanelProviderInfo
            SplitFileEditor.SplitEditorPreviewType.NONE -> myRenderingProfile.previewSettings.htmlPanelProviderInfo
            else -> TextHtmlPanelProvider.INFO
        }

        if (providerInfo == myLastPanelProviderInfo) {
            return null
        }

        var provider = HtmlPanelProvider.getFromInfoOrDefault(providerInfo)

        if (provider.isAvailable !== AvailabilityInfo.AVAILABLE && provider.isAvailable !== AvailabilityInfo.AVAILABLE_NOTUSED) {
            val unavailableProviderInfo = providerInfo

            if (providerInfo === unavailableProviderInfo) {
                // change to available provider
                val jbCefProvider = HtmlPanelProvider.getFromInfoOrDefault(JBCefHtmlPanelProvider.INFO)
                providerInfo = if (jbCefProvider.isAvailable !== AvailabilityInfo.UNAVAILABLE) jbCefProvider.INFO
                else MdPreviewSettings.DEFAULT.htmlPanelProviderInfo
            }

            val newSettings = myRenderingProfile.changeToProvider(null, providerInfo)
            providerInfo = newSettings.previewSettings.htmlPanelProviderInfo

            PluginNotifications.makeNotification(MdBundle.message("editor.preview.file.no-javafx.message", unavailableProviderInfo.name),
                MdBundle.message("editor.preview.file.no-javafx.title"), project = myProject)

            val profileManager = RenderingProfileManagerEx.getInstance(myProject)
            val renderingProfile = profileManager.getRenderingProfile(myFile)

            if (renderingProfile.previewSettings.htmlPanelProviderInfo == unavailableProviderInfo) {
                try {
                    inSettingsChange = true
                    if (renderingProfile.getName().isEmpty()) {
                        getInstance(myProject).renderingProfile = newSettings
                    } else {
                        profileManager.replaceProfile(newSettings.profileName, newSettings)
                    }
                } finally {
                    inSettingsChange = false
                }
            }

            myRenderingProfile = newSettings
            provider = newSettings.previewSettings.htmPanelProvider
        }

        myLastPanelProviderInfo = providerInfo
        return provider
    }

    private val panelGuaranteed: HtmlPanel
        get() {
            return myPanel ?: throw IllegalStateException("Panel is guaranteed to be not null now")
        }

    abstract fun makeHtmlPage(pattern: Pattern?): String

    private fun updateHtml() {
        if (getSplitEditorPreview(mySplitEditorPreviewType, mySplitEditorLayout) == SplitFileEditor.SplitEditorPreviewType.NONE) return

        if (!myFile.isValid || myDocument == null) {
            return
        }

        myShowingSearch = false
        var highlightRanges: Pattern? = null

        if (myEditor != null) {
            if (myRenderingProfile.previewSettings.showSearchHighlightsInPreview) {
                val searchSession = EditorSearchSession.get(myEditor)
                if (searchSession != null) {
                    val findModel = searchSession.findModel
                    val searchText = searchSession.component.searchTextComponent.text
                    val isRegex = findModel.isRegularExpressions
                    val isCaseSensitive = findModel.isCaseSensitive
                    val isWord = findModel.isWholeWordsOnly
                    if (searchText.isNotEmpty()) {
                        try {
                            val pattern = if (isRegex) {
                                Pattern.compile(searchText, if (isCaseSensitive) 0 else Pattern.CASE_INSENSITIVE)
                            } else {
                                if (isWord) {
                                    val wordStart = searchText[0].isJavaIdentifierPart() && searchText[0] != '$'
                                    val wordEnd = searchText[searchText.length - 1].isJavaIdentifierPart() && searchText[searchText.length - 1] != '$'
                                    Pattern.compile("${if (wordStart) "\\b" else ""}\\Q$searchText\\E${if (wordEnd) "\\b" else ""}", if (isCaseSensitive) 0 else Pattern.CASE_INSENSITIVE)
                                } else {
                                    Pattern.compile("\\Q$searchText\\E", if (isCaseSensitive) 0 else Pattern.CASE_INSENSITIVE)
                                }
                            }

                            highlightRanges = pattern
                        } catch (e: PatternSyntaxException) {

                        }
                    }
                }
                myShowingSearch = searchSession != null
            }
        }

        if (!mySwingAlarm.isDisposed) {
            val lastHtmlOrRefreshRequest = myLastHtmlOrRefreshRequest
            if (lastHtmlOrRefreshRequest != null) {
                mySwingAlarm.cancelRequest(lastHtmlOrRefreshRequest)
            }

            val nextHtmlOrRefreshRequest = Runnable {
                updateHtmlRunner(highlightRanges)
            }

            myLastHtmlOrRefreshRequest = nextHtmlOrRefreshRequest
            val stateForComponent = ModalityState.stateForComponent(component)
            mySwingAlarm.addRequest(nextHtmlOrRefreshRequest, renderingDelayMs, stateForComponent)
        }
    }

    open fun scrollToSrcOffset(offset: Int, editor: Editor) {
        val document = editor.document
        if (myEditor == null) {
            myEditor = editor
            val editorComponent = editor.contentComponent
            editorComponent.addComponentListener(myComponentListener)
            editor.selectionModel.addSelectionListener(mySelectionListener)

            ApplicationManager.getApplication().invokeLater {
                if (!editor.isDisposed) updateGutterIcons()
            }
        }

        val offsetVertical: Float?
        val lineOffsets: Range?

        if (myRenderingProfile.previewSettings.verticallyAlignSourceAndPreviewSyncPosition) {
            // get the vertical position as % of editor content height
            syncLogger.debug { "getting editor search info" }
            val searchHeight: Int
            val searchSession = EditorSearchSession.get(editor)
            searchHeight = searchSession?.component?.height ?: 0
            val scrollingModel = editor.scrollingModel
            val scrollOffset = scrollingModel.verticalScrollOffset
            val scrollHeight = scrollingModel.visibleArea.height - searchHeight
            val pos = editor.offsetToVisualPosition(offset)
            val lineNumber = editor.document.getLineNumber(offset)
            val lineStart = editor.document.getLineStartOffset(lineNumber)
            val lineEnd = editor.document.getLineEndOffset(lineNumber)
            lineOffsets = Range.of(lineStart, lineEnd)
            val offsetVerticalPos = editor.visualPositionToXY(pos).y - scrollOffset
            offsetVertical = if (scrollHeight > 0) offsetVerticalPos * 100f / scrollHeight else 50f
        } else {
            syncLogger.debug { "no editor search info" }
            offsetVertical = null
            lineOffsets = Range.of(offset, offset)
        }

        if (myRenderingProfile.previewSettings.synchronizePreviewPosition) {
            if (document.modificationStamp == myLastUpdatedModificationStamp && myPanel != null) {
                TimeIt.logTime(syncLogger, "scrollToMarkdownSrcOffset($offset, myHtmlTagRanges, false, false))") {
                    myLastScrollOffset = offset
                    myLastScrollLineOffsets = lineOffsets
                    myLastOffsetVertical = offsetVertical
                    panelGuaranteed.scrollToMarkdownSrcOffset(offset, lineOffsets, offsetVertical, myHtmlTagRanges, onLoadUpdate = false, onTypingUpdate = false)
                }
            } else {
                // save it for the page load update
                syncLogger.debug { "caching till page load: scrollToMarkdownSrcOffset($offset, myHtmlTagRanges, false, false))" }
                myLastScrollOffset = offset
                myLastScrollLineOffsets = lineOffsets
                myLastOffsetVertical = offsetVertical
            }
        }
    }

    private fun updateHtmlRunner(highlightRanges: Pattern?) {
        // diagnostic/2940
        if (myProject.isDisposed) return

        myLastHtmlOrRefreshRequest = null

        // set time stamp to eliminate scrolling updates for earlier or later caret moves
        myLastUpdatedModificationStamp = myDocument?.modificationStamp ?: 0

        val lastRenderedUrl = myLastRenderedUrl
        myLastRenderedUrl = ""

        if (panelGuaranteed.setPageUrl("") != null && !myDocumentIsModified) {
            var url: String? = null

            for (provider in MdPreviewCustomizationProvider.EXTENSIONS.value) {
                val altUrl = provider.getAlternatePageURL(myProject, myFile, myRenderingProfile)
                if (altUrl != null) {
                    url = altUrl
                    break
                }
            }

            if (url != null) {
                detailLogger.debug { "GitHub preview url: $url" }
                //val urlBase = "https://github.com"
                myLastRenderedUrl = url
                myLastRenderedHtml = ""
                panelGuaranteed.setPageUrl(url)

                detailLogger.debug { "GitHub preview: $url" }
            }
        }

        if (myLastRenderedUrl.isBlank()) {
            var currentHtml = ""
            TimeIt.logTime(LOG, "makeHtmlPage() ") {
                currentHtml = makeHtmlPage(highlightRanges)
            }
            detailLogger.debug { "JavaFx preview, last URL = $lastRenderedUrl" }

            myLastRenderedHtml = currentHtml
            if (!lastRenderedUrl.isBlank()) {
                // first one needs to be blank to reset the URL, or it won't render
                setUpPanel()
                detailLogger.debug { "JavaFx recreating preview to clear URL" }
            } else {
                myPanel ?: return

                if (myRenderingProfile.previewSettings.synchronizePreviewPosition) {
                    val lastActionId: String? = getLastActionId()
                    panelGuaranteed.scrollToMarkdownSrcOffset(myLastScrollOffset, myLastScrollLineOffsets, myLastOffsetVertical, myHtmlTagRanges, true, lastActionId == null || lastActionId in arrayOf("EditorBackSpace"))
                }

                TimeIt.logTime(LOG, "Update") {
                    if (panelGuaranteed.setHtml(currentHtml)) {
                        detailLogger.debug { "JavaFx preview, updated" }
                    } else {
                        detailLogger.debug { "JavaFx preview, rescheduled" }
                    }
                }
            }
        }
    }

    // return true if handled
    override fun launchExternalLink(href: String): Boolean {
        @Suppress("NAME_SHADOWING")
        var href = href
        var hrefInfo = PathInfo(href)

        if (!myLastRenderedUrl.isBlank() || !hrefInfo.isURI) {
            // here we translate to files so that the file is opened, then it will be viewed on GitHub
            ApplicationManager.getApplication().runReadAction {
                val resolver = GitHubLinkResolver(myFile, myProject)

                if (myLastRenderedUrl.isBlank()) {
                    // regular link resolve
                    var options = arrayOf(Local.URI, Remote.URI, Links.URL)

                    for (provider in MdPreviewCustomizationProvider.EXTENSIONS.value) {
                        val useLinkOptions = provider.getLinkOptions(myRenderingProfile)
                        if (useLinkOptions != null) {
                            options = useLinkOptions
                            break
                        }
                    }

                    val pathInfo = resolver.resolve(LinkRef.parseLinkRef(resolver.containingFile, href, null), Want(*options))
                    if (pathInfo != null) {
                        val pos = href.indexOf('#')
                        val anchorRef = if (pos == -1) "" else href.substring(pos)

                        hrefInfo = pathInfo
                        href = pathInfo.filePath + anchorRef
                    }
                    detailLogger.debug { "link click resolved: $href, resolved: ${pathInfo?.filePath}" }
                } else {
                    // external page url resolve
                    val url = if (href.startsWith("/")) "https://github.com$href" else href
                    val pathInfo = resolver.resolve(LinkRef.parseLinkRef(resolver.containingFile, url, null), Want(Local.URI, Remote.URI, Links.URL))
                    if (pathInfo != null) {
                        val pos = href.indexOf('#')
                        val anchorRef = if (pos == -1) "" else href.substring(pos)

                        hrefInfo = pathInfo
                        href = pathInfo.filePath + anchorRef
                    }

                    detailLogger.debug { "GitHub preview link: $href, url: $url, resolved: ${pathInfo?.filePath}" }
                }
            }
        }

        for (provider in MdPreviewCustomizationProvider.EXTENSIONS.value) {
            if (!provider.canLaunchExternalLink(myLastRenderedUrl, href, myRenderingProfile)) return false
        }

        MdPathResolver.launchExternalLink(myProject, href)
        return true
    }

    override fun deselectNotify() {
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? {
        return null
    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun getStructureViewBuilder(): StructureViewBuilder? {
        return null
    }

    override fun dispose() {
        val panel = myPanel
        if (panel != null) {
            Disposer.dispose(panel)
        }

        val editor = myEditor
        if (editor != null && !myEditorListenerRemoved) {
            myEditorListenerRemoved = true
            val editorComponent = editor.contentComponent
            editorComponent.removeComponentListener(myComponentListener)
            editor.selectionModel.removeSelectionListener(mySelectionListener)
        }
    }

    companion object {

        val LOG = Logger.getInstance("com.vladsch.md.nav.editor.htmlPrep")
        val syncLogger = Logger.getInstance("com.vladsch.md.nav.editor.sync-details")
        private val detailLogger = Logger.getInstance("com.vladsch.md.nav.editor.htmlPrep-details")
        val renderingLogger = Logger.getInstance("com.vladsch.md.nav.editor.htmlPrep-rendering")

        private var lastActionInitialized = false
        private var lastActionIdMethod: Method? = null
        private var editorLastActionTrackerInstance: Any? = null

        val MODIFIED_DOCUMENT_TIMEOUT_MS = 1500L
        val UNMODIFIED_DOCUMENT_TIMEOUT_MS = 5000L

        fun getLastActionId(): String? {
            if (!lastActionInitialized) {
                lastActionInitialized = true

                var editorLastActionTrackerClass: Class<*>? = null

                try {
                    editorLastActionTrackerClass = Class.forName("com.intellij.openapi.editor.impl.EditorLastActionTrackerImpl")
                } catch (e: ClassNotFoundException) {
                }

                if (editorLastActionTrackerClass == null) {
                    try {
                        editorLastActionTrackerClass = Class.forName("com.intellij.openapi.editor.impl.EditorLastActionTracker")
                    } catch (e: ClassNotFoundException) {
                    }
                }

                if (editorLastActionTrackerClass != null) {
                    try {
                        val instanceMethod: Method = editorLastActionTrackerClass.getMethod("getInstance")
                        lastActionIdMethod = editorLastActionTrackerClass.getMethod("getLastActionId")

                        editorLastActionTrackerInstance = instanceMethod.invoke(null)
                    } catch (e: Throwable) {
                    }
                }
            }

            if (lastActionIdMethod == null || editorLastActionTrackerInstance == null) {
                return null
            } else {
                return lastActionIdMethod!!.invoke(editorLastActionTrackerInstance) as? String
            }
        }

        private val TEST_HTML: String by lazy {
            MdResourceResolverImpl.instance.getResourceFileContent("/com/vladsch/md/nav/test_rendering.html")
        }
    }
}
