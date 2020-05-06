// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings.api

import com.intellij.openapi.util.Disposer
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.vladsch.md.nav.settings.MdExtensionSpacer
import com.vladsch.md.nav.settings.MdRenderingProfileHolder
import com.vladsch.md.nav.settings.RenderingProfileSynchronizer
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import javax.swing.JPanel

abstract class SettingsFormImpl(myProfileSynchronizer: RenderingProfileSynchronizer) : SettingsFormBase(myProfileSynchronizer), MdSettingsComponent<MdRenderingProfileHolder> {
    private val myExtensionsPanel: JPanel

    @JvmField
    protected val mySettingsExtensions = ArrayList<MdSettingsComponent<MdRenderingProfileHolder>>()
    protected val mySettingsExtensionNames = ArrayList<String>()

    companion object {
        val settingsFormExtensionProviders: Array<MdSettingsFormExtensionProvider<*, *>> by lazy {
            MdSettingsFormExtensionProvider.EP_NAME.extensions.sortedBy { it.extensionName }.toTypedArray()
        }
    }

    init {

        // allow extensions to add theirs
        for (configurableEP in settingsFormExtensionProviders) {
            // pass the container instance to constructor so it can be used for checking availability
            if (configurableEP.isAvailable(this)) {
                @Suppress("UNCHECKED_CAST")
                mySettingsExtensions.add((configurableEP as MdSettingsFormExtensionProvider<MdRenderingProfileHolder, MdRenderingProfileHolder>)
                    .createComponent(myProfileSynchronizer, myProfileSynchronizer, this) as MdSettingsComponent<MdRenderingProfileHolder>)
                mySettingsExtensionNames.add(configurableEP.extensionName)
            }
        }

        val iMax: Int = mySettingsExtensions.size

        if (iMax == 0) {
            myExtensionsPanel = JPanel()
            myExtensionsPanel.isVisible = false
        } else {
            myExtensionsPanel = JPanel(GridLayoutManager(iMax * 2, 1))

            forEachExtension { i, component ->
                val jComponent = component.component
                val extensionSpacer = MdExtensionSpacer(mySettingsExtensionNames[i], jComponent).mainPanel

                val constraintsLabel = GridConstraints(i * 2, 0, 1, 1
                    , GridConstraints.ANCHOR_CENTER
                    , GridConstraints.FILL_HORIZONTAL
                    , GridConstraints.SIZEPOLICY_CAN_GROW or GridConstraints.SIZEPOLICY_CAN_SHRINK
                    , GridConstraints.SIZEPOLICY_CAN_GROW or GridConstraints.SIZEPOLICY_CAN_SHRINK
                    , null, null, null)

                myExtensionsPanel.add(extensionSpacer, constraintsLabel)

                val constraints = GridConstraints(i * 2 + 1, 0, 1, 1
                    , GridConstraints.ANCHOR_CENTER
                    , GridConstraints.FILL_HORIZONTAL
                    , GridConstraints.SIZEPOLICY_CAN_GROW or GridConstraints.SIZEPOLICY_CAN_SHRINK
                    , GridConstraints.SIZEPOLICY_CAN_GROW or GridConstraints.SIZEPOLICY_CAN_SHRINK
                    , null, null, null)

                myExtensionsPanel.add(jComponent, constraints)
            }
        }
    }

    fun getExtensionsPanel(): JPanel = myExtensionsPanel

    fun forEachExtension(consumer: BiConsumer<Int, MdSettingsComponent<MdRenderingProfileHolder>>) {
        for ((index, component) in mySettingsExtensions.withIndex()) {
            consumer.accept(index, component)
        }
    }

    fun forEachExtension(consumer: Consumer<MdSettingsComponent<MdRenderingProfileHolder>>) {
        for (component in mySettingsExtensions) {
            consumer.accept(component)
        }
    }

    fun forEachExtension(consumer: (index: Int, form: MdSettingsComponent<MdRenderingProfileHolder>) -> Unit) {
        forEachExtension(BiConsumer(consumer))
    }

    fun forEachExtension(consumer: (form: MdSettingsComponent<MdRenderingProfileHolder>) -> Unit) {
        forEachExtension(Consumer(consumer))
    }

    fun updateExtensionsOptionalSettings() {
        forEachExtension { component ->
            component.updateOptionalSettings()
        }
    }

    fun isModifiedExtensions(settings: MdRenderingProfileHolder): Boolean {
        var isModified = false
        forEachExtension { component ->
            if (component.isModified(settings)) {
                isModified = true
                return@forEachExtension
            }
        }
        return isModified
    }

    fun applyExtensions(settings: MdRenderingProfileHolder) {
        forEachExtension { component ->
            component.apply(settings)
        }
    }

    fun resetExtensions(settings: MdRenderingProfileHolder) {
        forEachExtension { component ->
            component.reset(settings)
        }
    }

    fun forExtensions(runnable: Runnable) {
        if (mySettingsExtensions.isNotEmpty()) {
            runnable.run()
        }
    }

    fun forExtensions(runnable: () -> Unit) {
        forExtensions(Runnable(runnable))
    }

    protected abstract fun disposeResources()

    final override fun dispose() {
        disposeResources()

        forEachExtension { it -> Disposer.dispose(it) }
        mySettingsExtensions.clear()
    }
}
