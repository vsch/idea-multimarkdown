// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.vladsch.md.nav.MdBundle
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class MdPreviewConfigurable(project: Project) : MdProjectConfigurable<MdPreviewSettingsForm>(project), SearchableConfigurable {
    companion object {
        const val ID: String = "MarkdownNavigator.Settings.Preview"
    }

    override fun getId(): String {
        return ID
    }

    override fun enableSearch(option: String): Runnable? {
        return null
    }

    @Nls
    override fun getDisplayName(): String {
        return MdBundle.message("settings.markdown.preview.name")
    }

    override fun getHelpTopic(): String? {
        return "com.vladsch.markdown.navigator.settings.preview"
    }

    override fun createComponent(): JComponent? {
        return form.component
    }

    override fun isModified(): Boolean {
        return form.isModified(myProfileSynchronizer.renderingProfileHolder) || myProfileSynchronizer.previewSettings != myProfileSynchronizer.renderingProfileHolder.previewSettings
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        form.apply(myProfileSynchronizer)
        myProfileSynchronizer.apply()
    }

    override fun reset() {
        // first reset should take profile settings just in case these were changed by copy from defaults
        myProfileSynchronizer.previewSettings = profileForReset.previewSettings
        form.reset(myProfileSynchronizer)
    }

    override fun disposeUIResources() {
        val form = myForm
        if (form != null) {
            Disposer.dispose(form)
            myForm = null
            myProfileSynchronizer.reset()
        }
    }

    val form: MdPreviewSettingsForm
        get() {
            var form = myForm
            if (form == null) {
                form = MdPreviewSettingsForm(myProfileSynchronizer)
                myForm = form
            }
            return form
        }
}
