// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.ide.DataManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.ex.Settings
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.Topic
import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.parser.flexmark.MdFencedCodeImageConversionManager
import com.vladsch.md.nav.settings.api.MdRenderingProfileSynchronizerExtension
import com.vladsch.md.nav.settings.api.MdSettingsExtension
import com.vladsch.plugin.util.LazyFunction
import java.util.function.BiConsumer
import java.util.function.Function

class RenderingProfileSynchronizer constructor(val project: Project) : MdRenderingProfileHolder, Disposable {
    private var _renderingProfileHolder: MdRenderingProfileHolder = MdProjectSettings.getInstance(project)
    private var _workingRenderingProfile: MdRenderingProfile = MdRenderingProfile(_renderingProfileHolder.renderingProfile)

    init {
        Disposer.register(project, this)
    }

    var renderingProfileHolder: MdRenderingProfileHolder
        get() {
            return _renderingProfileHolder
        }
        set(value) {
            _renderingProfileHolder = value
            _workingRenderingProfile = MdRenderingProfile(_renderingProfileHolder.renderingProfile)
        }

    private val workingRenderingProfile: MdRenderingProfile
        get() = _workingRenderingProfile

    private val messageBusHolder: MessageBusHolder = MessageBusCreator.createHolder()
    val messageBus: MessageBus get() = messageBusHolder.messageBus

    private var groupNotifications = 0
    private var myFromPanelProvider: HtmlPanelProvider? = null
    private var myToPanelProvider: HtmlPanelProvider? = null

    private val myExtensions = HashMap<Class<*>, Any>()

    fun <T> getExtension(extensionClass: Class<T>): T {
        val extension = myExtensions[extensionClass]
        @Suppress("UNCHECKED_CAST")
        return extension as T
    }

    init {
        for (extension in MdRenderingProfileSynchronizerExtension.EXTENSIONS.value) {
            val extensionData = extension.createExtensionData(this)
            myExtensions[extensionData.javaClass] = extensionData
        }

        project.messageBus.connect(this).subscribe(ProjectSettingsChangedListener.TOPIC, ProjectSettingsChangedListener { project1, settings ->
            if (project1 === project && workingRenderingProfile != settings.renderingProfile) {
                if (workingRenderingProfile.getName().isEmpty()) {
                    // copy everything
                    renderingProfile = settings.renderingProfile
                } else if (!workingRenderingProfile.havePreviewSettings) {
                    // only inform that provider changed
                    updatePanelProvider(settings.previewSettings.htmlPanelProvider, false)
                }
            }
        })
    }

    override fun dispose() {
        Disposer.dispose(messageBusHolder)
    }

    fun apply() {
        if (isModified) {
            renderingProfileHolder.renderingProfile = workingRenderingProfile

            if (!isProjectRenderingProfile) {
                // inform project of the change
                MdProjectSettings.getInstance(project).notifySettingsChanged()
            }
        }
    }

    fun reset() {
        if (isModified) {
            if (renderingProfileHolder.previewSettings.htmlPanelProviderInfo == workingRenderingProfile.previewSettings.htmlPanelProviderInfo) {
                workingRenderingProfile.copyFrom(renderingProfileHolder.renderingProfile, true)
            } else {
                // copy all the provider independent stuff by making a copy of application settings provider
                workingRenderingProfile.copyFrom(workingRenderingProfile.changeToProvider(workingRenderingProfile.previewSettings.htmlPanelProviderInfo, renderingProfileHolder.previewSettings.htmlPanelProviderInfo), true)
            }
        }
    }

    val isModified: Boolean
        get() = workingRenderingProfile != renderingProfileHolder.renderingProfile

    val panelProvider: HtmlPanelProvider
        get() = workingRenderingProfile.previewSettings.htmlPanelProvider

    val panelProviderInfo: HtmlPanelProvider.Info
        get() = workingRenderingProfile.previewSettings.htmlPanelProviderInfo

    override fun groupNotifications(runnable: Runnable) {
        startGroupChangeNotifications()
        try {
            runnable.run()
        } finally {
            endGroupChangeNotifications()
        }
    }

    fun notifyCssSettings(settings: MdRenderingProfileHolder) {
        workingRenderingProfile.cssSettings = settings.cssSettings
        notifyCssSettingsChange(workingRenderingProfile)
    }

    private fun startGroupChangeNotifications() {
        if (groupNotifications == 0) {
            myFromPanelProvider = null
            myToPanelProvider = null
        }
        groupNotifications++
    }

    private fun endGroupChangeNotifications() {
        assert(groupNotifications > 0) { "endGroupNotifications called when groupNotifications is $groupNotifications" }
        if (groupNotifications > 0) {
            groupNotifications--
            if (groupNotifications == 0) {
                notifyPanelProviderChange(myFromPanelProvider, myToPanelProvider)
            }
        }
    }

    private fun notifyPanelProviderChange(fromPanelProvider: HtmlPanelProvider?, toPanelProvider: HtmlPanelProvider?) {
        if (fromPanelProvider != null && toPanelProvider != null) {
            if (groupNotifications > 0) {
                myFromPanelProvider = fromPanelProvider
                myToPanelProvider = toPanelProvider
            } else {
                messageBus.syncPublisher(PANEL_PROVIDER_TOPIC).updatePanelProvider(fromPanelProvider, toPanelProvider)
                myFromPanelProvider = null
                myToPanelProvider = null
            }
        }
    }

    private fun notifyCssSettingsChange(settings: MdRenderingProfileHolder) {
        messageBus.syncPublisher(CSS_SETTINGS_TOPIC).updateCssSettings(settings)
    }

    interface PanelProviderChanged {
        fun updatePanelProvider(fromProvider: HtmlPanelProvider, toProvider: HtmlPanelProvider)
    }

    interface CssSettingsChanged {
        fun updateCssSettings(settings: MdRenderingProfileHolder)
    }

    interface RenderingProfileChanged {
        fun updateRenderingProfile(renderingProfile: MdRenderingProfile)
    }

    override var renderingProfile: MdRenderingProfile
        get() = workingRenderingProfile
        set(renderingProfile) {
            val fromPanelProvider = panelProvider
            val toPanelProvider: HtmlPanelProvider

            if (renderingProfile.havePreviewSettings) {
                toPanelProvider = renderingProfile.previewSettings.htmlPanelProvider
            } else {
                toPanelProvider = MdProjectSettings.getInstance(project).renderingProfile.previewSettings.htmlPanelProvider
            }
            workingRenderingProfile.copyFrom(renderingProfile, true)
            if (fromPanelProvider.INFO !== toPanelProvider.INFO) {
                notifyPanelProviderChange(fromPanelProvider, toPanelProvider)
            }
        }

    fun setRenderingProfileAndReset(renderingProfile: MdRenderingProfile) {
        workingRenderingProfile.copyFrom(renderingProfile, true)
        messageBus.syncPublisher(RENDERING_PROFILE_TOPIC).updateRenderingProfile(workingRenderingProfile)
    }

    override fun getResolvedProfile(parentProfile: MdRenderingProfile): MdRenderingProfile {
        return if (workingRenderingProfile.getName().isEmpty()) workingRenderingProfile else workingRenderingProfile.getResolvedProfile(MdProjectSettings.getInstance(project).renderingProfile)
    }

    override // so that all provider dependent fields get updated
    var previewSettings: MdPreviewSettings
        get() = workingRenderingProfile.previewSettings
        set(settings) {
            if (workingRenderingProfile.previewSettings.htmlPanelProviderInfo != settings.htmlPanelProviderInfo) {
                updatePanelProvider(settings.htmlPanelProvider, null)
            }
            workingRenderingProfile.previewSettings = settings
        }

    override var parserSettings: MdParserSettings
        get() = workingRenderingProfile.parserSettings
        set(settings) {
            workingRenderingProfile.parserSettings = settings
        }

    override var cssSettings: MdCssSettings
        get() = workingRenderingProfile.cssSettings
        set(settings) {
            workingRenderingProfile.cssSettings = settings
        }

    override fun getStyleSettings(): MdCodeStyleSettings {
        return workingRenderingProfile.styleSettings
    }

    override fun setStyleSettings(settings: MdCodeStyleSettings) {
        workingRenderingProfile.styleSettings = settings
    }

    override var htmlSettings: MdHtmlSettings
        get() = workingRenderingProfile.htmlSettings
        set(settings) {
            workingRenderingProfile.htmlSettings = settings
        }

    override fun <T : MdSettingsExtension<T>?> getExtension(key: DataKey<T>): T {
        return workingRenderingProfile.getExtension(key)
    }

    override fun <T : MdSettingsExtension<T>?> setExtension(value: T) {
        workingRenderingProfile.setExtension(value)
    }

    val isProjectRenderingProfile: Boolean
        get() = MdProjectSettings.getInstance(project) == renderingProfileHolder

    fun updatePanelProvider(provider: HtmlPanelProvider, havePreviewSettings: Boolean?) {
        val fromHavePreviewSettings = workingRenderingProfile.havePreviewSettings
        val fromPanelProvider = if (fromHavePreviewSettings) panelProvider else MdProjectSettings.getInstance(project).previewSettings.htmlPanelProvider
        val toHavePreviewSettings = havePreviewSettings ?: fromHavePreviewSettings
        val toPanelProvider = if (toHavePreviewSettings) provider else MdProjectSettings.getInstance(project).previewSettings.htmlPanelProvider

        workingRenderingProfile.havePreviewSettings = toHavePreviewSettings
        if (fromPanelProvider.INFO != toPanelProvider.INFO) {
            workingRenderingProfile.renderingProfile = workingRenderingProfile.changeToProvider(fromPanelProvider.INFO, toPanelProvider.INFO)
            messageBus.syncPublisher(PANEL_PROVIDER_TOPIC).updatePanelProvider(fromPanelProvider, toPanelProvider)
        }
    }

    companion object {
        @JvmField
        val PANEL_PROVIDER_TOPIC: Topic<PanelProviderChanged> = Topic.create("MultiMarkdown.ConfigurablePanelProvider", PanelProviderChanged::class.java, Topic.BroadcastDirection.NONE)

        @JvmField
        val RENDERING_PROFILE_TOPIC: Topic<RenderingProfileChanged> = Topic.create("MultiMarkdown.ConfigurableRenderingProfileChanged", RenderingProfileChanged::class.java, Topic.BroadcastDirection.NONE)

        @JvmField
        val CSS_SETTINGS_TOPIC: Topic<CssSettingsChanged> = Topic.create("MultiMarkdown.CssSettingsChanged", CssSettingsChanged::class.java, Topic.BroadcastDirection.NONE)

        private val NULL: LazyFunction<Project, RenderingProfileSynchronizer> = LazyFunction(Function {
            RenderingProfileSynchronizer(it)
        })

        @JvmStatic
        fun getInstance(project: Project): RenderingProfileSynchronizer {
            return if (project.isDefault) NULL.getValue(project)
            // DEPRECATED: added 2019.08, when available change to
//        project.getService(RenderingProfileSynchronizer.class);
            else ServiceManager.getService(project, RenderingProfileSynchronizer::class.java)
        }

        @JvmStatic
        fun getSettings(handler: BiConsumer<DataContext, Settings>) {
            DataManager.getInstance().dataContextFromFocusAsync
                .onSuccess {
                    val settings: Settings? = Settings.KEY.getData(it)
                    if (settings != null) {
                        handler.accept(it, settings)
                    }
                }
                .onError {}

//            // DEPRECATED : replacement DataManager#getDataContextFromFocusAsync appeared in 2018-02-21
//            //    change to new API when old api is removed
//            //noinspection deprecation
//            val context: DataContext? = DataManager.getInstance().dataContextFromFocus.result
//            if (context != null) {
//                val settings: Settings? = Settings.KEY.getData(context)
//                if (settings != null) {
//                    handler.accept(context, settings)
//                }
//            }
        }

        @JvmStatic
        fun applySettings(settings: Settings, configurableId: String, renderingProfile: MdRenderingProfile) {
            val configurable = settings.find(configurableId) as? MdProjectConfigurable<*>
            configurable?.applyForm(renderingProfile)
        }

        @JvmStatic
        fun updateCssScriptSettings(profileSynchronizer: RenderingProfileSynchronizer) {
            // NOTE: need to update current un-applied settings for validation 
            getSettings(BiConsumer { context: DataContext?, settings: Settings ->
                val renderingProfile = MdRenderingProfile(profileSynchronizer.renderingProfile)
                applySettings(settings, MdPreviewConfigurable.ID, renderingProfile)
                applySettings(settings, MdParserConfigurable.ID, renderingProfile)
                applySettings(settings, MdCssConfigurable.ID, renderingProfile)
                applySettings(settings, MdHtmlConfigurable.ID, renderingProfile)
                applySettings(settings, MdFencedCodeConfigurable.ID, renderingProfile)

                if (MdFencedCodeImageConversionManager.getInstance(profileSynchronizer.project).updateCssSettings(renderingProfile)) {
                    profileSynchronizer.notifyCssSettings(renderingProfile)
                }
            })
        }
    }
}
