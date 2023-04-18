// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.inspections

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdFileType
import com.vladsch.md.nav.MdProjectComponent
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.settings.*

class JekyllFrontMatterFoundNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>(), DumbAware {

    override fun getKey(): Key<EditorNotificationPanel> {
        return KEY
    }

    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project:Project): EditorNotificationPanel? {
        if (file.fileType !== MdFileType.INSTANCE) {
            return null
        }

        if (MdApplicationSettings.instance.wasShownSettings.jekyllFrontMatter) {
            return null
        }

        if (fileEditor !is TextEditor) {
            return null
        }

        val profileManager = MdRenderingProfileManager.getInstance(project)
        val renderingProfile = profileManager.getRenderingProfile(file)
        val parserSettings = renderingProfile.parserSettings
        if (parserSettings.optionsFlags and ParserOptions.JEKYLL_FRONT_MATTER.flags != 0L || parserSettings.optionsFlags and ParserOptions.FLEXMARK_FRONT_MATTER.flags != 0L) {
            return null
        }

        // see if the file has jekyll front matter
        val document = FileDocumentManager.getInstance().getDocument(file) ?: return null
        val jekyllFrontMatterOffset = MdFile.frontMatterOffset(document.charsSequence, true, false)
        if (jekyllFrontMatterOffset <= 0) return null

        val panel = EditorNotificationPanel()
        panel.setText(MdBundle.message("editor.jekyll-front-matter.is.available"))

        panel.createActionLabel(MdBundle.message("editor.jekyll-front-matter.enable")) {
            //MarkdownRenderingProfile newProfile = new MarkdownRenderingProfile(renderingProfile);
            val newParserSettings = MdParserSettings(
                parserSettings.pegdownFlags,
                parserSettings.optionsFlags or ParserOptions.JEKYLL_FRONT_MATTER.flags,
                parserSettings.gitHubSyntaxChange,
                parserSettings.emojiShortcuts,
                parserSettings.emojiImages
            )

            if (renderingProfile.profileName.isEmpty()) {
                MdProjectSettings.getInstance(project).parserSettings = newParserSettings
            } else {
                renderingProfile.parserSettings = newParserSettings
                profileManager.replaceProfile(renderingProfile.profileName, renderingProfile)
            }

            MdProjectComponent.getInstance(project).reparseMarkdown(true)
            EditorNotifications.updateAll()
        }

        panel.createActionLabel(MdBundle.message("editor.dont.show.again")) {
            MdApplicationSettings.instance.wasShownSettings.jekyllFrontMatter = true
            EditorNotifications.updateAll()
        }
        return panel
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("editor.jekyll-front-matter.is.available")
    }
}
