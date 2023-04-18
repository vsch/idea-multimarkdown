// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api

import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.md.nav.settings.TagItemHolder
import com.vladsch.md.nav.settings.UnwrappedSettings

class MdExtendableSettingsImpl : MdExtendableSettings, MdSettingsExtensionsHolder {
    private val myExtensions = MutableDataSet()
    private var myContainer: MdExtendableSettings = this
    private val myExtensionPoints = LinkedHashMap<DataKey<out MdSettingsExtension<*>>, MdSettingsExtensionProvider<*>>()
    private val myPendingExtensionNotifications = LinkedHashSet<DataKey<out MdSettingsExtension<*>>>()

    companion object {
        val settingsExtensionProviders: Array<MdSettingsExtensionProvider<*>> get() = MdSettingsExtensionProvider.EXTENSIONS.value
    }

    override fun initializeExtensions(container: MdExtendableSettings) {
        myContainer = container

        for (extensionProvider in settingsExtensionProviders) {
            if (extensionProvider.isAvailableIn(container)) {
                myExtensionPoints[extensionProvider.getKey()] = extensionProvider
            }
        }
    }

    override fun getExtensionPoints(): Collection<MdSettingsExtensionProvider<*>> {
        return myExtensionPoints.values
    }

    override fun <T : MdSettingsExtension<T>> hasExtension(key: DataKey<T>): Boolean {
        return myExtensions.contains(key)
    }

    override fun <T : MdSettingsExtension<T>> hasExtensionPoint(key: DataKey<T>): Boolean {
        return myExtensionPoints.contains(key)
    }

    override fun <T : MdSettingsExtension<T>> notifySettingsChanged(key: DataKey<T>) {
        key.get(myExtensions).notifySettingsChanged()
    }

    override fun <T : MdSettingsExtension<T>> pendingSettingsChanged(key: DataKey<T>) {
        myPendingExtensionNotifications.add(key)
    }

    override fun notifyPendingSettingsChanged() {
        myPendingExtensionNotifications.forEach {
            it.get(myExtensions).notifySettingsChanged()
        }
    }

    override fun getExtensions(): MutableDataSet = myExtensions

    override fun getExtensionKeys(): Set<DataKey<MdSettingsExtension<*>>> {
        @Suppress("UNCHECKED_CAST")
        return myExtensionPoints.keys as Set<DataKey<MdSettingsExtension<*>>>
    }

    override fun getContainedExtensionKeys(): Set<DataKey<MdSettingsExtension<*>>> {
        return myExtensions.keys.filterIsInstance<DataKey<MdSettingsExtension<*>>>().toSet()
    }

    fun <T : MdExtendableSettings> copyFrom(other: T) {
        other.extensionKeys.forEach { key ->
            if (!other.containedExtensionKeys.contains(key)) extensions.remove(key)
            else key.get(extensions).copyFrom(other.extensions)
        }
    }

    fun addItems(tagItemHolder: TagItemHolder): TagItemHolder {
        extensionKeys.forEach { key ->
            val extensionProvider = myExtensionPoints[key] ?: return@forEach
            val savedBy = extensionProvider.isSavedBy(myContainer)
            val loadedBy = extensionProvider.isLoadedBy(myContainer)
            if (savedBy || loadedBy) {
                key.get(myExtensions).addItems(!savedBy, tagItemHolder)
            }
        }
        return tagItemHolder
    }

    fun addUnwrappedItems(tagItemHolder: TagItemHolder): TagItemHolder {
        return addUnwrappedItems(myContainer, tagItemHolder)
    }

    override fun validateLoadedSettings() {
        extensionKeys.forEach { key ->
            val extension = key.get(myExtensions)
            extension.validateLoadedSettings()
        }
    }

    override fun addUnwrappedItems(container: Any, tagItemHolder: TagItemHolder): TagItemHolder {
        extensionKeys.forEach { key ->
            val extensionProvider = myExtensionPoints[key] ?: return@forEach
            val savedBy = extensionProvider.isSavedBy(container)
            val loadedBy = extensionProvider.isLoadedBy(container)
            if (savedBy || loadedBy) {
                val settings = key.get(myExtensions)
                tagItemHolder.addItems(UnwrappedSettings<MdSettingsExtension<*>>(!savedBy, settings))

                if (settings is MdExtendableSettings) {
                    settings.addUnwrappedItems(container, tagItemHolder)
                }
            }
        }
        return tagItemHolder
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MdExtendableSettings) return false

        extensionKeys.forEach {
            if (it.get(myExtensions) != it.get(other.extensions)) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 0
        extensionKeys.forEach { result += 31 * result + it.get(myExtensions).hashCode() }
        return result
    }
}
