// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Disposer
import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.MdProjectComponent
import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.settings.api.MdSettingsExtension
import javax.swing.UIManager

class MdProjectSettings constructor(project: Project?) : ComponentItemHolder()
    , MdRenderingProfileHolder
    , LafManagerListener
    , Disposable {

    private val myProject = project
    private val myRenderingProfile: MdRenderingProfile

    init {
        val profile = MdRenderingProfile()
        if (myProject != null) {
            profile.setProjectProfile(myProject)
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
        // DEPRECATED: replacement appeared in 2019-07-20
        @Suppress("DEPRECATION")
        if (myProject != null) {
            LafManager.getInstance().addLafManagerListener(this, myProject)
        }

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

            val project = myProject
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
        return MdCodeStyleSettings.getInstance(myProject ?: ProjectManager.getInstance().defaultProject)
    }

    override fun setStyleSettings(styleSettings: MdCodeStyleSettings) {
        MdCodeStyleSettings.getInstance(myProject ?: ProjectManager.getInstance().defaultProject).copyFrom(styleSettings)
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
            val project = myProject
            if (project == null) {
                val defaultProject = ProjectManager.getInstance().defaultProject
                defaultProject.messageBus.syncPublisher(ProjectSettingsChangedListener.TOPIC).onSettingsChange(defaultProject, this)
            } else if (!project.isDisposed) {
                project.messageBus.syncPublisher(ProjectSettingsChangedListener.TOPIC).onSettingsChange(project, this)
            }
        }
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.settings.project")

        @JvmStatic
        fun isDarcula(laf: UIManager.LookAndFeelInfo?): Boolean {
            return laf?.name?.contains("Darcula") ?: false
        }

        @JvmStatic
        fun getInstance(project: Project): MdProjectSettings {
            val settingsManager = MdProjectSettingsManager.getInstance(project)
            val settings = settingsManager.projectSettings
            Disposer.register(project, settings)
            settingsManager.isProjectSettingsLoaded = true
            return settings
        }
    }
}
