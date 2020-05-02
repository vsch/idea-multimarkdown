/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.md.nav

import com.intellij.ProjectTopics
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.codeInsight.lookup.impl.LookupImpl
import com.intellij.ide.startup.StartupManagerEx
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.EmptyEditorHighlighter
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import com.intellij.openapi.roots.ModuleRootEvent
import com.intellij.openapi.roots.ModuleRootListener
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.*
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.FileContentUtilCore
import com.intellij.util.messages.Topic
import com.vladsch.md.nav.editor.MdPreviewFileEditorProvider
import com.vladsch.md.nav.editor.MdSplitEditor
import com.vladsch.md.nav.editor.api.MdEditorCustomizationProvider
import com.vladsch.md.nav.highlighter.MdSyntaxHighlighter
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.element.MdNamedElement
import com.vladsch.md.nav.settings.*
import com.vladsch.md.nav.util.EditorWindowKey
import com.vladsch.md.nav.util.MdCancelableJobScheduler
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.md.nav.vcs.MdLinkResolverManager
import com.vladsch.plugin.util.AwtRunnable
import com.vladsch.plugin.util.DelayedRunner
import com.vladsch.plugin.util.LazyFunction
import com.vladsch.plugin.util.removeIf
import org.jetbrains.annotations.NonNls
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.*
import java.util.function.Function
import kotlin.collections.ArrayList

// FIX: make this a service
class MdProjectComponent(val project: Project) : ProjectComponent {

    val creation = System.currentTimeMillis()
    private val time: Long get() = System.currentTimeMillis() - creation
    var isLookupActive: Boolean = false
        private set
    var isPostStartup = false
        private set

    @Suppress("UseExpressionBody", "UNUSED_PARAMETER")
    fun println(text: String) {
    }

    init {
        println("ProjectComponent created $time")
    }

    var refactoringRenameFlags = MdNamedElement.RENAME_NO_FLAGS
        private set

    private var refactoringRenameFlagsStack = IntArray(10)
    private var refactoringRenameStack = 0

    // here we store image serials: 0 means file has original content
    // every time the file content changes this number is incremented.
    // this way modified images are reloaded by JavaFX WebView by attaching the serial after
    // the file name as a query parameter to make WebView load it as a new image
    private var imageFilesChangedPending = false

    private val runWhenProjectClosed = DelayedRunner()

    private var reloadEditorsPending = false
    private var reopenChangedEditorsPending = false
    private var reopenRefusedByUser = HashSet<String>()

    @Messages.YesNoResult
    fun showReopenFileTypeChangedEditorsDialog(title: String = MdBundle.message("reopen.changed-type.title")): Int {
        val message = MdBundle.message("update.editor-reopen.changed-type.message")
        return Messages.showYesNoDialog(message, title, MdBundle.message("update.editor-reopen.changed-type.yes.label"), MdBundle.message("update.editor-reopen.changed-type.no-thanks.label"), Messages.getQuestionIcon())
    }

    private fun reopenChangedFileTypeEditors() {
        if (ApplicationManager.getApplication().isUnitTestMode) return;

        assert(MdApplicationSettings.instance.debugSettings.reloadEditorsOnFileTypeChange)

        if (!reopenChangedEditorsPending) {
            reopenChangedEditorsPending = true

            ApplicationManager.getApplication().invokeLater({
                if (project.isDisposed) return@invokeLater

                try {
                    val fileEditorManager = FileEditorManagerEx.getInstance(project) as FileEditorManagerEx
                    val files = fileEditorManager.openFiles
                    val reopenFiles = ArrayList<VirtualFile>()
                    for (file in files) {
                        for (editor in fileEditorManager.getAllEditors(file)) {
                            var reOpen = (editor is MdSplitEditor) != MdPreviewFileEditorProvider.acceptFile(project, file)

                            if (!reOpen) {
                                for (provider in MdEditorCustomizationProvider.EXTENSIONS.value) {
                                    if (provider.reloadEditor(editor, project, file)) {
                                        reOpen = true
                                        break
                                    }
                                }
                            }

                            if (reOpen) {
                                reopenFiles.add(file)
                                break
                            }
                        }
                    }

                    if (reopenFiles.isEmpty()) {
                        reopenRefusedByUser.clear()
                    } else if (reopenFiles.any { !reopenRefusedByUser.contains(it.path) }) {
                        if (showReopenFileTypeChangedEditorsDialog() == Messages.YES) {
                            reopenRefusedByUser.clear()
                            reopenFileEditors(files)
                        } else {
                            reopenRefusedByUser.addAll(reopenFiles.map { it.path })
                        }
                    } else {
                        reopenRefusedByUser.removeIf { it1 -> !reopenFiles.any { it.path == it1 } }
                    }
                } finally {
                    reopenChangedEditorsPending = false
                }
            }, ModalityState.NON_MODAL)
        }
    }

    private fun reopenFileEditors(files: Array<out VirtualFile>) {
        val fileEditorManager = FileEditorManagerEx.getInstance(project) as FileEditorManagerEx
        val windows = fileEditorManager.windows
        val dummyVirtualFile: VirtualFile? by lazy { LightVirtualFile("dummy.txt", "\n") }

        for (window in windows) {
            val focusedEditor = window.selectedEditor
            val editors = window.editors

            for (file in files) {
                val editorIndex = editors.indexOfFirst { it.file == file }
                if (editorIndex == -1) continue

                val pinned = window.isFilePinned(file)

                // may need to add a dummy file if there is only one tab so that when we close the file the tab stays open
                val dummy: VirtualFile? = if (window.tabCount == 1) dummyVirtualFile else null

                if (dummy != null) fileEditorManager.openFileWithProviders(dummy, false, window)

                INITIAL_INDEX_KEY.setEditorWindowInitialIndex(file, editorIndex)
                window.closeFile(file, true, false)
                fileEditorManager.openFileWithProviders(file, false, window)

                if (dummy != null) window.closeFile(dummy)

                if (pinned) window.setFilePinned(file, true)

                // clear the index key so it does not keep opening at that tab index by accident
                INITIAL_INDEX_KEY.setEditorWindowInitialIndex(file, null)
            }

            if (focusedEditor != null) {
                // restore focused tab for the window
                window.setSelectedEditor(focusedEditor, false)
            }
        }
    }

    private enum class ReInitType(val intValue: Int, val maskValue: Int = 1 shl intValue) {
        RE_INIT(0),
        SWAP_HIGHLIGHTER(1),
        RE_INIT_SWAP_HIGHLIGHTER(2, RE_INIT.maskValue or SWAP_HIGHLIGHTER.maskValue),
        ;

        val isReInit: Boolean
            get() {
                return (maskValue and RE_INIT.maskValue) != 0
            }

        val isSwapHighlighter: Boolean
            get() {
                return (maskValue and SWAP_HIGHLIGHTER.maskValue) != 0
            }
    }

    private val reinitializeEditorSettings = HashMap<EditorEx, ReInitType>()

    private fun reloadMarkdownEditors(reInitMarkdownEditors: Boolean, swapHighlighter: Boolean) {
        if (MdApplicationSettings.instance.debugSettings.reinitializeEditorsOnSettingsChange) {
            if (!reloadEditorsPending) {
                reloadEditorsPending = true

                ApplicationManager.getApplication().invokeLater({
                    if (project.isDisposed) return@invokeLater

                    try {
                        val fileEditorManager = FileEditorManager.getInstance(project)
                        val files = fileEditorManager.openFiles
                        val psiManager = PsiManager.getInstance(project)

                        for (file in files) {
                            val allEditors = fileEditorManager.getAllEditors(file)
                            for (editor in allEditors) {
                                if (editor is MdSplitEditor && (reInitMarkdownEditors || swapHighlighter)) {
                                    if (file.isValid) {
                                        val psiFile = psiManager.findFile(file)
                                        if (psiFile != null) {
                                            val editorEx = editor.editor
                                            if (editorEx.settings.isLineMarkerAreaShown) {
                                                DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
                                                if (editorEx is EditorEx) {
                                                    reinitializeEditorSettings[editorEx] = if (swapHighlighter) ReInitType.RE_INIT_SWAP_HIGHLIGHTER else ReInitType.RE_INIT
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (reinitializeEditorSettings.isNotEmpty()) {
                            AwtRunnable.schedule(MdCancelableJobScheduler.getInstance(), "gutter change", GUTTER_CHANGE_DELAY) {
                                ApplicationManager.getApplication().invokeLater({
                                    reinitializeEditorSettings()
                                }, ModalityState.NON_MODAL)
                            }
                        }
                    } finally {
                        reloadEditorsPending = false
                    }
                }, ModalityState.NON_MODAL)
            }
        }
    }

    @JvmOverloads
    fun reparseMarkdown(reparseFilePsi: Boolean = false) {
        val log = LOG.isDebugEnabled

        if (!project.isDisposed) {
            if (DumbService.isDumb(project)) {
                DumbService.getInstance(project).runWhenSmart {
                    reparseMarkdown(reparseFilePsi)
                }
            } else {
                // re-parse all open markdown editors
                val files = FileEditorManager.getInstance(project).openFiles

                val psiManager = PsiManager.getInstance(project)
                if (reparseFilePsi) {
                    val fileList = ArrayList<PsiFile>(files.size)

                    for (file in files) {
                        if (file.isValid) {
                            val psiFile = psiManager.findFile(file)
                            if (psiFile != null && psiFile is MdFile) {
                                fileList.add(psiFile)
                            }
                        }
                    }

                    //  project.messageBus.syncPublisher(ReparseMarkdownPsiFiles.TOPIC).reparseFiles(fileList)
                    if (log) LOG.debug("reparse file psi start")
                    if (brokenReparseFiles) {
                        val instance = DaemonCodeAnalyzer.getInstance(project)
                        for (file in fileList) {
                            if (file.isValid) {
                                instance.restart(file)
                            }
                        }
                    } else {
                        FileContentUtilCore.reparseFiles(fileList.map { it.virtualFile })
                    }
                    if (log) LOG.debug("reparse file psi end")
                } else {
                    if (log) LOG.debug("reparse open file start")

                    val instance = DaemonCodeAnalyzer.getInstance(project)
                    for (file in files) {
                        if (file.isValid) {
                            val psiFile = psiManager.findFile(file)
                            if (psiFile != null && psiFile is MdFile) {
                                instance.restart(psiFile)
                            }
                        }
                    }
                    if (log) LOG.debug("reparse open file end")
                }

                if (MdApplicationSettings.instance.debugSettings.reloadEditorsOnFileTypeChange) {
                    reopenChangedFileTypeEditors()
                }
            }
        }
    }

    fun pushRefactoringRenameFlags(refactoringReason: Int) {
        this.refactoringRenameFlagsStack[refactoringRenameStack++] = this.refactoringRenameFlags
        this.refactoringRenameFlags = refactoringReason
    }

    fun popRefactoringRenameFlags() {
        if (refactoringRenameStack > 0) {
            refactoringRenameFlags = refactoringRenameFlagsStack[--refactoringRenameStack]
        }
    }

    override fun projectClosed() {
        println("projectClosed $time")
        reinitializeEditorSettings.clear()
        runWhenProjectClosed.runAll()
    }

    private fun propertyChange(evt: PropertyChangeEvent) {
        if (LookupManager.PROP_ACTIVE_LOOKUP == evt.propertyName) {
            var newEditor: Editor? = null
            if (evt.newValue is LookupImpl) {
                val lookup = evt.newValue as LookupImpl
                newEditor = lookup.editor
            }

            isLookupActive = newEditor != null
        }
    }

    private fun fileContentChanged(file: VirtualFile) {
        val changedFile = file.path
        if (PathInfo(changedFile).isImageExt) {
            if (!imageFilesChangedPending) {
                imageFilesChangedPending = true

                if (!project.isDisposed) {
                    ApplicationManager.getApplication().invokeLater {
                        if (!project.isDisposed) {
                            imageFilesChangedPending = false
                            project.messageBus.syncPublisher(FileChangedListener.TOPIC).onFilesChanged()
                        }
                    }
                }
            }
        }
    }

    override fun projectOpened() {
        println("projectOpened $time")

        @Suppress("DEPRECATION")
        // DEPRECATED: use VFS_CHANGES topic, available since 2017-11-07
        VirtualFileManager.getInstance().addVirtualFileListener(object : VirtualFileListener {
            override fun propertyChanged(event: VirtualFilePropertyEvent) {
                // NOTE: this also one fires often when document is modified with writeable property changing
                if (!event.file.isDirectory) {
//                    System.out.println("PropertyChanged: ${event.propertyName}: old: ${event.oldValue} new: ${event.newValue}")
                    if (event.propertyName == "name" && (event.oldValue == event.newValue || PathInfo(event.oldValue as String).ext != PathInfo(event.newValue as String).ext)) {
                        // this is file type change when no name changes or PSI is re-parsed
                        reloadMarkdownEditors(reInitMarkdownEditors = false, swapHighlighter = true)

                        if (MdApplicationSettings.instance.debugSettings.reloadEditorsOnFileTypeChange) {
                            reopenChangedFileTypeEditors()
                        }
                    }
                }
            }

            override fun contentsChanged(event: VirtualFileEvent) {
                if (project.isDisposed) return
                if (event.file.isDirectory) return

                fileContentChanged(event.file)
            }

            override fun fileCreated(event: VirtualFileEvent) {
                updateHighlighters()
                if (!event.file.isDirectory) fileContentChanged(event.file)
            }

            override fun fileDeleted(event: VirtualFileEvent) {
                updateHighlighters()
                if (!event.file.isDirectory) fileContentChanged(event.file)
            }

            override fun fileMoved(event: VirtualFileMoveEvent) {
                updateHighlighters()
            }

            override fun fileCopied(event: VirtualFileCopyEvent) {
                updateHighlighters()
            }
        }, project)

        // add lookup manager listener
        LookupManager.getInstance(project).addPropertyChangeListener(PropertyChangeListener { evt -> this@MdProjectComponent.propertyChange(evt) }, project)

        val messageBusConnection = project.messageBus.connect(project)

        messageBusConnection.subscribe(ProjectTopics.PROJECT_ROOTS, object : ModuleRootListener {
            override fun rootsChanged(event: ModuleRootEvent) {
                if (project.isDisposed) return
                reparseMarkdown(false)
            }
        })

        messageBusConnection.subscribe(MdRepoChangeListener.TOPIC, MdRepoChangeListener {
            ApplicationManager.getApplication().invokeLater({
                reparseMarkdown(true)
            }, ModalityState.NON_MODAL)
        })

        ApplicationManager.getApplication().invokeLater({
            if (!project.isDisposed) {
                MdPlugin.instance.projectLoaded(project)
            }
        }, ModalityState.NON_MODAL)

        val applicationMessageBusConnection = ApplicationManager.getApplication().messageBus.connect(project)
        val settingsChangeReloadReparseHandler = SettingsChangeReloadReparseHandler()
        applicationMessageBusConnection.subscribe(SettingsChangedListener.TOPIC, settingsChangeReloadReparseHandler)

        applicationMessageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
            }

            override fun fileOpenedSync(source: FileEditorManager, file: VirtualFile, editors: Pair<Array<FileEditor>, Array<FileEditorProvider>>) {
            }

            override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
            }

            override fun selectionChanged(event: FileEditorManagerEvent) {
                if (project.isDisposed) return

                if (MdApplicationSettings.instance.debugSettings.reinitializeEditorsOnSettingsChange) {
                    AwtRunnable.schedule(MdCancelableJobScheduler.getInstance(), "gutter change", GUTTER_CHANGE_DELAY) {
                        reinitializeEditorSettings()
                    }
                } else {
                    reinitializeEditorSettings.clear()
                }
            }
        })

        StartupManagerEx.getInstance(project).registerPostStartupActivity {
            if (project.isDisposed) return@registerPostStartupActivity

            // FIX: have this registered in plugin.xml instead
            // NOTE: line below fires before vcs roots are initialized.
//        (ProjectLevelVcsManagerImpl.getInstance(project) as ProjectLevelVcsManagerImpl).addInitializationRequest(VcsInitObject.AFTER_COMMON) { projectInitialized() }
//        StartupManager.getInstance(project).registerPostStartupActivity { projectInitialized() }
            MdLinkResolverManager.getInstance(project).projectInitialized()

            isPostStartup = true
            settingsChangeReloadReparseHandler.onSettingsChange(MdApplicationSettings.instance)
//            reloadMarkdownEditors(reInitMarkdownEditors = false, swapHighlighter = true)

            EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryListener {
                override fun editorReleased(event: EditorFactoryEvent) {
                    if (project.isDisposed) return
                    reinitializeEditorSettings.removeIf { it -> it.isDisposed || it === event.editor }
                }

                // NOTE: now editors are created with the correct highlighter. Swapping is only needed for diff view because it requests editor highlighter for the project file for both repository and project file
                override fun editorCreated(event: EditorFactoryEvent) {
                    if (project.isDisposed) return

                    val editor = event.editor
                    if (editor is EditorEx) {
                        if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("editorEx created $editor ${editor.virtualFile} isWritable: ${editor.document.isWritable}")
                        AwtRunnable.schedule(MdCancelableJobScheduler.getInstance(), "$editor", 100, ModalityState.any()) {
                            swapEditorHighlighter(editor)
                        }
                    } else {
                        if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("editor created ${event.editor}, skipping not editorEx")
                    }
                }
            }, project)
        }
    }

    inner class SettingsChangeReloadReparseHandler : SettingsChangedListener {
        private var documentSettings: MdDocumentSettings = MdDocumentSettings(MdPlugin.instance.startupDocumentSettings)

        //        private var debugSettings: MdDebugSettings = MdPlugin.instance.startupDebugSettings
        private var inReopenEditors = false

        override fun onSettingsChange(settings: MdApplicationSettings) {
            if (project.isDisposed) return

            if (!inReopenEditors && isPostStartup) {
                val oldDocumentSettings = documentSettings
                val newDocumentSettings = settings.documentSettings

                // check for settings changes needing editor reloading
                var reloadMarkdown = false
                var swapHighlighter = false
                var reparseMarkdown = false

                inReopenEditors = true
                try {
                    if (settings.debugSettings.reinitializeEditorsOnSettingsChange) {
                        if (oldDocumentSettings.syntaxHighlighting != newDocumentSettings.syntaxHighlighting) {
                            swapHighlighter = true
                        }

                        if (oldDocumentSettings.htmlLangInjections != newDocumentSettings.htmlLangInjections ||
                            oldDocumentSettings.grammarIgnoreSimpleTextCasing != newDocumentSettings.grammarIgnoreSimpleTextCasing ||
                            oldDocumentSettings.verbatimLangInjections != newDocumentSettings.verbatimLangInjections ||
                            oldDocumentSettings.multiLineImageUrlInjections != newDocumentSettings.multiLineImageUrlInjections
                        ) {
                            reparseMarkdown = true
                        }

                        if (newDocumentSettings.iconGutters) {
                            if (oldDocumentSettings.enableLineMarkers != newDocumentSettings.enableLineMarkers) {
                                reloadMarkdown = true
                            }
                        }
                    }

                    if (!reloadMarkdown) {
                        for (provider in MdEditorCustomizationProvider.EXTENSIONS.value) {
                            if (provider.reloadMarkdown(oldDocumentSettings, newDocumentSettings)) {
                                reloadMarkdown = true
                                break
                            }
                        }
                    }

                    if (!reparseMarkdown) {
                        for (provider in MdEditorCustomizationProvider.EXTENSIONS.value) {
                            if (provider.reparseMarkdown(oldDocumentSettings, newDocumentSettings)) {
                                reparseMarkdown = true
                                break
                            }
                        }
                    }

                    if (reloadMarkdown || swapHighlighter) {
                        reloadMarkdownEditors(reloadMarkdown, swapHighlighter)
                    }

                    if (reparseMarkdown) {
                        reparseMarkdown(false)
                    }

                    documentSettings = MdDocumentSettings(settings.documentSettings)
//                    debugSettings = settings.debugSettings
                } finally {
                    inReopenEditors = false
                }
            }
        }
    }

    private fun reinitializeEditorSettings() {
        if (project.isDisposed) {
            reinitializeEditorSettings.clear()
            return
        }

        assert(MdApplicationSettings.instance.debugSettings.reinitializeEditorsOnSettingsChange)

        val fileEditorManager = FileEditorManager.getInstance(project)

        for (newEditor in fileEditorManager.selectedEditors) {
            var editor: Editor? = when (newEditor) {
                is MdSplitEditor -> newEditor.editor
                else -> null
            }

            if (editor == null) {
                for (provider in MdEditorCustomizationProvider.EXTENSIONS.value) {
                    editor = provider.getEditorEx(newEditor);
                    if (editor != null) {
                        break
                    }
                }
            }

            if (editor is EditorEx && reinitializeEditorSettings.contains(editor)) {
                val reInitType = reinitializeEditorSettings.remove(editor) ?: ReInitType.RE_INIT

                if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("reinitializeType $reInitType for $editor")

                if (reInitType.isSwapHighlighter) {
                    swapEditorHighlighter(editor)
                }

                if (reInitType.isReInit) {
                    editor.reinitSettings()
                }
            }

            reinitializeEditorSettings.removeIf { it -> it.isDisposed }
        }
    }

    private fun swapEditorHighlighter(editor: EditorEx) {
        if (project.isDisposed) {
            reinitializeEditorSettings.clear()
            return
        }

        if (editor.isDisposed) {
            return
        }

        val virtualFile = editor.virtualFile ?: return
        if (virtualFile.isValid && virtualFile.fileType == MdFileType.INSTANCE) {
            val editorHighlighter = editor.highlighter
            var swapHighlighter = false
            val documentSettings = MdApplicationSettings.instance.documentSettings
            val forAnnotator = documentSettings.syntaxHighlighting == SyntaxHighlightingType.ANNOTATOR.intValue && editor.document.isWritable

            if (editorHighlighter is LexerEditorHighlighter) {
                val syntaxHighlighter = editorHighlighter.syntaxHighlighter
                if (syntaxHighlighter is MdSyntaxHighlighter && !syntaxHighlighter.forSampleDoc) {
                    val isPlainTextLexer = syntaxHighlighter.highlightingLexer.javaClass.simpleName == "MdPlainTextLexer"
                    swapHighlighter = if (forAnnotator) !isPlainTextLexer else isPlainTextLexer
                    if (!swapHighlighter && LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("swapEditorHighlighter lexer already ${syntaxHighlighter.highlightingLexer.javaClass.simpleName} $editor $virtualFile")
                } else {
                    if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("swapEditorHighlighter not mdHighlighter or for SampleDoc $editor $virtualFile")
                }
            } else if (editorHighlighter is EmptyEditorHighlighter) {
                // this one needs a delay
                if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("swapEditorHighlighter EmptyEditorHighlighter highlighter $editor $virtualFile $editorHighlighter")
            } else {
                if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("swapEditorHighlighter not lexer highlighter $editor $virtualFile $editorHighlighter")
            }

            if (swapHighlighter) {
                val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
                try {
                    // here we change the highlighter to correct one to reflect file's rendering profile
                    val renderingProfile =
                        if (psiFile != null) MdRenderingProfileManager.getProfile(psiFile)
                        else MdRenderingProfileManager.getInstance(project).defaultRenderingProfile

                    val mdSyntaxHighlighter = MdSyntaxHighlighter(renderingProfile, false, forAnnotator)
                    val highlighter = LexerEditorHighlighter(mdSyntaxHighlighter, editor.colorsScheme)
                    highlighter.setText(editor.document.immutableCharSequence)
                    editor.highlighter = highlighter

                    if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("swapEditorHighlighter in $editor to ${mdSyntaxHighlighter.highlightingLexer.javaClass.simpleName} for $virtualFile")

                    // now need to re-parse to get rid of wrong highlighting
                    if (psiFile != null) {
                        val daemonCodeAnalyzer = DaemonCodeAnalyzer.getInstance(project)
                        daemonCodeAnalyzer.restart(psiFile)
                    }
                } catch (e: Throwable) {
                    if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug(e)
                }
            }
        } else {
            if (LOG_EDITOR.isDebugEnabled) LOG_EDITOR.debug("swapEditorHighlighter not markdown $editor $virtualFile")
        }
    }

    @NonNls
    override fun getComponentName(): String {
        return this.javaClass.name
    }

    override fun initComponent() {
        println("initComponent $time")
        val messageBusConnection = project.messageBus.connect(project)

        messageBusConnection.subscribe(ProjectLifecycleListener.TOPIC, object : ProjectLifecycleListener {
            override fun afterProjectClosed(project: Project) {
                println("afterProjectClosed $time")
            }

            override fun projectComponentsInitialized(project: Project) {
                println("projectComponentsInitialized $time")
            }

            override fun beforeProjectLoaded(project: Project) {
                println("beforeProjectLoaded $time")
            }
        })
    }

    override fun disposeComponent() {
        println("disposeComponent $time")
    }

    private fun updateHighlighters(reparseFilePsi: Boolean = false) {
        // project files have changed so we need to update the lists and then reparse for link validation
        // We get a call back when all have been updated.
        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                reparseMarkdown(reparseFilePsi)
            }
        }
    }

    fun getFileSerial(filePath: String): Long {
        val virtualFile = VirtualFileManager.getInstance().findFileByUrl("file:/$filePath")
        return virtualFile?.timeStamp ?: 0L
    }

    interface FileChangedListener {
        fun onFilesChanged()

        companion object {
            val TOPIC = Topic.create("ImageFileChanged", FileChangedListener::class.java, Topic.BroadcastDirection.NONE)
        }
    }

    companion object {
        internal val LOG = Logger.getInstance("com.vladsch.md.nav.project")
        internal val LOG_EDITOR = Logger.getInstance("com.vladsch.md.nav.project.editors")
        var brokenReparseFiles = false
            private set

        val INITIAL_INDEX_KEY: EditorWindowKey by lazy {
            EditorWindowKey()
        }

        private const val GUTTER_CHANGE_DELAY = 500

        private val NULL = LazyFunction<Project, MdProjectComponent>(Function { t -> MdProjectComponent(t) })

        @JvmStatic
        fun getInstance(project: Project): MdProjectComponent {
            return if (project.isDefault) NULL.getValue(project)
            else project.getComponent(MdProjectComponent::class.java)
        }
    }
}

