// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseComponent
import com.vladsch.md.nav.settings.api.MdApplicationRestartRequiredProvider
import com.vladsch.plugin.util.AppRestartRequiredChecker
import java.util.function.Predicate

// FIX: make this a service
class MdApplicationRestartManager : BaseComponent, Disposable {

    private val restartProviders = HashMap<Class<out MdApplicationRestartRequiredProvider>, Predicate<MdApplicationSettings>>()

    private val restartRequiredChecker = AppRestartRequiredChecker<MdApplicationSettings>()

    override fun initComponent() {
        val appSettings = MdApplicationSettings.instance

        for (provider in MdApplicationRestartRequiredProvider.EP_NAME.extensions) {
            val restartRequiredPredicate = provider.getRestartRequiredPredicate(appSettings)
            restartProviders[provider.javaClass] = restartRequiredPredicate
            restartRequiredChecker.addRestartNeededPredicate(restartRequiredPredicate)
        }

        val settingsConnection = ApplicationManager.getApplication().messageBus.connect(this as Disposable)
        settingsConnection.subscribe(SettingsChangedListener.TOPIC, SettingsChangedListener { applicationSettings ->
            ApplicationManager.getApplication().invokeLater {
                restartRequiredChecker.informRestartIfNeeded(applicationSettings)
            }
        })
    }

    fun haveRestartNeededShownFlags(predicate: Class<out MdApplicationRestartRequiredProvider>): Boolean {
        return restartRequiredChecker.haveRestartNeededShownFlags(restartProviders[predicate])
    }

    fun setRestartNeededShownFlags(predicate: Class<out MdApplicationRestartRequiredProvider>) {
        restartRequiredChecker.setRestartNeededShownFlags(restartProviders[predicate])
    }

    fun setRestartNeededShownFlags(predicate: Class<out MdApplicationRestartRequiredProvider>, value: Boolean) {
        restartRequiredChecker.setRestartNeededShownFlags(restartProviders[predicate], value)
    }

    override fun dispose() {
        restartProviders.clear()
        restartRequiredChecker.clear()
    }

    override fun getComponentName(): String {
        return this.javaClass.name
    }

    companion object {
        @JvmStatic
        val instance: MdApplicationRestartManager
            get() = ApplicationManager.getApplication().getComponent(MdApplicationRestartManager::class.java) ?: throw IllegalStateException()
    }
}
