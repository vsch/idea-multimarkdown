// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.settings.api.MdApplicationSettingsExtensionProvider
import com.vladsch.md.nav.settings.api.MdExtendableSettings
import com.vladsch.md.nav.settings.api.MdExtendableSettingsImpl
import com.vladsch.md.nav.settings.api.MdSettingsExtension
import com.vladsch.plugin.util.LazyRunnable
import com.vladsch.plugin.util.ListenersRunner
import com.vladsch.plugin.util.ui.SettableForm
import org.jdom.Element
import org.jetbrains.annotations.TestOnly
import javax.swing.UIManager

class MdApplicationSettings(val isUnitTestMode: Boolean) :
    MdApplicationSettingsHolder
    , LafManagerListener
    , Disposable {

    constructor() : this(false)

    private val mySharedState = SharedState()
    private val myLocalState = LocalState()

    private var groupNotifications = 0
    private var havePendingSettingsChanged = false

    private var isLastLAFWasDarcula = isDarcula

    private val documentSettingsUpdaters = ListenersRunner<SettableForm<MdApplicationSettingsHolder>>()

    val isDarcula: Boolean get() = !isUnitTestMode && isDarcula(LafManager.getInstance().currentLookAndFeel)

    // used to get current document settings which may not have been applied yet
    fun addDocumentSettingsUpdater(updater: SettableForm<MdApplicationSettingsHolder>) {
        documentSettingsUpdaters.addListener(updater)
    }

    fun removeDocumentSettingsUpdater(updater: SettableForm<MdApplicationSettingsHolder>) {
        documentSettingsUpdaters.removeListener(updater)
    }

    fun getUpdatedDocumentSettings(): MdApplicationSettingsHolder {
        var documentSettings = MdDocumentSettings(documentSettings)
        val applicationSettings = object : MdApplicationSettingsHolder,
            MdWasShownSettings.Holder by this
            , MdDocumentSettings.Holder
            , MdDebugSettings.Holder by this {
            override var documentSettings: MdDocumentSettings
                get() = documentSettings
                set(value) {
                    documentSettings = MdDocumentSettings(value)
                }

            override fun <T : MdSettingsExtension<T>> getExtension(key: DataKey<T>): T {
                return this@MdApplicationSettings.getExtension(key)
            }

            override fun <T : MdSettingsExtension<T>> setExtension(value: T) {
                this@MdApplicationSettings.setExtension(value)
            }
        }
        documentSettingsUpdaters.fire { it.apply(applicationSettings) }
        return applicationSettings
    }

    fun resetDocumentSettings(settings: MdApplicationSettingsHolder) {
        documentSettingsUpdaters.fire { it.reset(settings) }
    }

    override fun lookAndFeelChanged(source: LafManager) {
        val newLookAndFeel = source.currentLookAndFeel
        val isNewLookAndFeelDarcula = isDarcula(newLookAndFeel)

        if (isNewLookAndFeelDarcula == isLastLAFWasDarcula) {
            return
        }

        notifyOnSettingsChangedRaw()
        isLastLAFWasDarcula = isNewLookAndFeelDarcula
    }

    init {
        if (!isUnitTestMode) {
            val messageBus = ApplicationManager.getApplication().messageBus
            val settingsConnection = messageBus.connect(messageBus)

            try {
                settingsConnection.subscribe(LafManagerListener.TOPIC, this)
            } catch (ignored: NoSuchFieldError) {
                // DEPRECATED: replacement appeared in 2019-07-20
                @Suppress("DEPRECATION")
                LafManager.getInstance().addLafManagerListener(this)
            }

            ApplicationManager.getApplication().invokeLater { notifyOnSettingsChangedRaw() }
        }
    }

    override fun dispose() {
    }

    fun getSharedState(): SharedState {
        return mySharedState
    }

    fun getLocalState(): LocalState {
        return myLocalState
    }

    override fun <T : MdSettingsExtension<T>> getExtension(key: DataKey<T>): T {
        if (myLocalState.hasExtensionPoint(key)) {
            return myLocalState.getExtension(key)
        }
        return mySharedState.getExtension(key)
    }

    override fun <T : MdSettingsExtension<T>> setExtension(value: T) {
        val key = value.key
        if (myLocalState.hasExtensionPoint(key)) {
            return myLocalState.setExtension(value)
        }
        return mySharedState.setExtension(value)
    }

    override var documentSettings: MdDocumentSettings
        get() = myLocalState.documentSettings
        set(settings) {
            myLocalState.documentSettings.copyFrom(settings)
            notifyOnSettingsChangedRaw()
        }

    override var wasShownSettings: MdWasShownSettings
        get() = myLocalState.wasShownSettings
        set(settings) {
            myLocalState.wasShownSettings.copyFrom(settings)
            notifyOnSettingsChangedRaw()
        }

    override var debugSettings: MdDebugSettings
        get() = myLocalState.debugSettings
        set(settings) {
            myLocalState.debugSettings.copyFrom(settings)
            notifyOnSettingsChangedRaw()
        }

    fun groupNotifications(runnable: Runnable) {
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
                myLocalState.notifyPendingSettingsChanged()
                mySharedState.notifyPendingSettingsChanged()
                if (havePendingSettingsChanged) notifyOnSettingsChangedRaw()
            }
        }
    }

    fun notifyOnSettingsChangedRaw() {
        if (groupNotifications > 0) havePendingSettingsChanged = true
        else ApplicationManager.getApplication().messageBus.syncPublisher(SettingsChangedListener.TOPIC).onSettingsChange(this)
    }

    class SharedState(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : ComponentItemHolder(), MdExtendableSettings by mySettingsExtensions {
        init {
            initializeExtensions(this)
            mySettingsExtensions.addUnwrappedItems(addItems(

            ))
        }

        @TestOnly
        internal fun copyFrom(other: SharedState) {
            this.mySettingsExtensions.copyFrom(other)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SharedState

            return mySettingsExtensions.equals(other)
        }

        override fun hashCode(): Int {
            val result = mySettingsExtensions.hashCode()
            return result
        }
    }

    class LocalState(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : ComponentItemHolder(), MdExtendableSettings by mySettingsExtensions {
        val documentSettings = MdDocumentSettings()
        val wasShownSettings = MdWasShownSettings()
        val debugSettings = MdDebugSettings()

        init {
            initializeExtensions(this)

            // need to add extensions of extendable settings in case they saved by rendering profile
            // one caveat is that if the main settings are loaded then the extensions need to be copied from
            // the original otherwise these will load into a discarded object and not have any values

            val tagItemHolder = mySettingsExtensions.addUnwrappedItems(addItems(
                UnwrappedSettings(documentSettings),
                UnwrappedSettings(debugSettings),
                UnwrappedSettings(wasShownSettings)
            ))

            documentSettings.addUnwrappedItems(this, tagItemHolder)
            debugSettings.addUnwrappedItems(this, tagItemHolder)
            wasShownSettings.addUnwrappedItems(this, tagItemHolder)
        }

        override fun loadState(element: Element?) {
            super.loadState(element)

            documentSettings.migrateSettings(this)
        }

        @TestOnly
        internal fun copyFrom(other: LocalState) {
            documentSettings.copyFrom(other.documentSettings)
            wasShownSettings.copyFrom(other.wasShownSettings)
            debugSettings.copyFrom(other.debugSettings)
            this.mySettingsExtensions.copyFrom(other)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LocalState

            if (documentSettings != other.documentSettings) return false
            if (wasShownSettings != other.wasShownSettings) return false
            if (debugSettings != other.debugSettings) return false

            return mySettingsExtensions == other
        }

        override fun hashCode(): Int {
            var result = mySettingsExtensions.hashCode()
            result = 31 * result + documentSettings.hashCode()
            result = 31 * result + wasShownSettings.hashCode()
            result = 31 * result + debugSettings.hashCode()
            return result
        }
    }

    @TestOnly
    fun copyFrom(other: MdApplicationSettings) {
        assert(isUnitTestMode)
        myLocalState.copyFrom(other.myLocalState)
        mySharedState.copyFrom(other.mySharedState)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MdApplicationSettings

        if (mySharedState != other.mySharedState) return false
        if (myLocalState != other.myLocalState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mySharedState.hashCode()
        result = 31 * result + myLocalState.hashCode()
        return result
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.settings")

        @JvmStatic
        fun isDarcula(laf: UIManager.LookAndFeelInfo?): Boolean {
            return laf?.name?.contains("Darcula") ?: false
        }

        private val dummyInstance: MdApplicationSettings by lazy { MdApplicationSettings(true) }

        private val componentsLoaded = LazyRunnable(Runnable {
            // NOTE: our shared and local state is provided by separate services,
            //  these need to be instantiated so their state is loaded and will be saved.
            //  The same for application settings extension services.

            MdApplicationSharedSettings.getInstance()
            MdApplicationLocalSettings.getInstance()

            for (extension in MdApplicationSettingsExtensionProvider.EP_NAME.extensions) {
                extension.initializeApplicationSettingsService()
            }
        })

        @JvmStatic
        val instance: MdApplicationSettings
            get() {
                val application: Application? = ApplicationManager.getApplication()
                return if (application?.isUnitTestMode != false) {
                    dummyInstance
                } else {
                    val service = ServiceManager.getService(MdApplicationSettings::class.java)
                    componentsLoaded.hasRun
                    Disposer.register(ApplicationManager.getApplication().messageBus, service)
                    service
                }
            }
    }
}
