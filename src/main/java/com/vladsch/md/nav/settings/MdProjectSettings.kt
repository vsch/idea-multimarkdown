// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Disposer
import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.MdProjectComponent
import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.settings.api.MdSettingsExtension
import javax.swing.UIManager

class MdProjectSettings constructor(val project: Project?) : ComponentItemHolder()
    , MdRenderingProfileHolder
    , LafManagerListener
    , Disposable {

    private val myRenderingProfile: MdRenderingProfile

    val projectOrDefault: Project get() = project ?: ProjectManager.getInstance().defaultProject

    init {
        val profile = MdRenderingProfile()
        if (project != null) {
            profile.setProjectProfile(project)
        }
        myRenderingProfile = profile
    }

    private var groupNotifications = 0
    private var havePendingSettingsChanged = false

    private var isLastLAFWasDarcula = isDarcula

    val isDarcula: Boolean get() = isDarcula(LafManager.getInstance().currentLookAndFeel)

    override fun lookAndFeelChanged(source: LafManager) {
        val newLookAndFeel = source.currentLookAndFeel
        val isNewLookAndFeelDarcula = isDarcula(newLookAndFeel)

        if (isNewLookAndFeelDarcula == isLastLAFWasDarcula) {
            return
        }

        notifyOnSettingsChanged()
        isLastLAFWasDarcula = isNewLookAndFeelDarcula
    }

    init {
        @Suppress("IncorrectParentDisposable")
        Disposer.register(project ?: ProjectManager.getInstance().defaultProject, this)

        val settingsConnection = ApplicationManager.getApplication().messageBus.connect(this)
        settingsConnection.subscribe(LafManagerListener.TOPIC, this)

        // let rendering profile add the unwrapped items which correspond to all settings and extensions
        myRenderingProfile.addUnwrappedItems(this, this)

        ApplicationManager.getApplication().invokeLater { notifyOnSettingsChanged() }
    }

    override fun dispose() {
    }

    fun validateLoadedSettings() {
        renderingProfile.validateLoadedSettings()
    }

    override var renderingProfile: MdRenderingProfile
        get() = myRenderingProfile
        set(settings) {
            myRenderingProfile.copyFrom(settings, true)
            notifyOnSettingsChanged()
        }

    override fun getResolvedProfile(parentProfile: MdRenderingProfile): MdRenderingProfile {
        return renderingProfile
    }

    override fun <T : MdSettingsExtension<T>> getExtension(key: DataKey<T>): T {
        return myRenderingProfile.getExtension(key)
    }

    override fun <T : MdSettingsExtension<T>> setExtension(value: T) {
        myRenderingProfile.setExtension(value)
    }

    override var previewSettings: MdPreviewSettings
        get() = myRenderingProfile.previewSettings
        set(value) {
            myRenderingProfile.previewSettings = value

            notifyOnSettingsChanged()
        }

    override var parserSettings: MdParserSettings
        get() = myRenderingProfile.parserSettings
        set(settings) {
            myRenderingProfile.parserSettings = settings
            notifyOnSettingsChanged()

            val project = project
            if (project != null) {
                MdProjectComponent.getInstance(project).reparseMarkdown(true)
            }
        }

    override var htmlSettings: MdHtmlSettings
        get() = myRenderingProfile.htmlSettings
        set(settings) {
            myRenderingProfile.htmlSettings = settings

            notifyOnSettingsChanged()
        }

    override var cssSettings: MdCssSettings
        get() = myRenderingProfile.cssSettings
        set(settings) {
            myRenderingProfile.cssSettings = settings

            notifyOnSettingsChanged()
        }

    override fun getStyleSettings(): MdCodeStyleSettings {
        return MdCodeStyleSettings.getInstance(project ?: ProjectManager.getInstance().defaultProject)
    }

    override fun setStyleSettings(styleSettings: MdCodeStyleSettings) {
        MdCodeStyleSettings.getInstance(project ?: ProjectManager.getInstance().defaultProject).copyFrom(styleSettings)
    }

    override fun groupNotifications(runnable: Runnable) {
        startGroupChangeNotifications()
        try {
            runnable.run()
        } finally {
            endGroupChangeNotifications()
        }
    }

    private fun startGroupChangeNotifications() {
        if (groupNotifications == 0) {
            havePendingSettingsChanged = false
        }
        groupNotifications++
    }

    private fun endGroupChangeNotifications() {
        assert(groupNotifications > 0) { "endGroupNotifications called when groupNotifications is $groupNotifications" }
        if (groupNotifications > 0) {
            groupNotifications--
            if (groupNotifications == 0) {
                if (havePendingSettingsChanged) notifyOnSettingsChanged()
            }
        }
    }

    fun notifySettingsChanged() {
        notifyOnSettingsChanged()
    }

    private fun notifyOnSettingsChanged() {
        if (groupNotifications > 0) havePendingSettingsChanged = true
        else {
            val project = project
            if (project == null) {
                val defaultProject = ProjectManager.getInstance().defaultProject
                defaultProject.messageBus.syncPublisher(ProjectSettingsChangedListener.TOPIC).onSettingsChange(defaultProject, this)
            } else if (!project.isDisposed) {
                project.messageBus.syncPublisher(ProjectSettingsChangedListener.TOPIC).onSettingsChange(project, this)
            }
        }
    }

    companion object {
        @JvmStatic
        fun isDarcula(laf: UIManager.LookAndFeelInfo?): Boolean {
            return laf?.name?.contains("Darcula") ?: false
        }

        @JvmStatic
        fun getInstance(project: Project): MdProjectSettings {
            val settingsManager = MdProjectSettingsManager.getInstance(project)
            return settingsManager.projectSettings
        }
    }
}
