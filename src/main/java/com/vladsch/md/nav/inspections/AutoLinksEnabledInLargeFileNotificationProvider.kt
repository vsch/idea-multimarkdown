// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.inspections

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdFileType
import com.vladsch.md.nav.MdProjectComponent
import com.vladsch.md.nav.psi.element.MdAutoLink
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.settings.*

class AutoLinksEnabledInLargeFileNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>(), DumbAware {
    override fun getKey(): Key<EditorNotificationPanel> {
        return KEY
    }

    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project:Project): EditorNotificationPanel? {
        if (MdApplicationSettings.instance.wasShownSettings.autoLinksExtension) return null

        if (file.fileType !== MdFileType.INSTANCE) {
            return null
        }

        if (fileEditor !is TextEditor) {
            return null
        }

        val editor = fileEditor.editor as? EditorEx ?: return null

        if (DumbService.isDumb(project)) {
            return null
        }

        // see if the file has actual auto-links without <> wrapping and is a large file
        val document = FileDocumentManager.getInstance().getDocument(file) ?: return null
        //        if (document == null /*|| document.lineCount < 500*/) return null

        val virtualFile = editor.virtualFile

        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? MdFile ?: return null
        val profileManager = MdRenderingProfileManager.getInstance(project)
        val renderingProfile = profileManager.getRenderingProfile(psiFile)
        val parserSettings = renderingProfile.parserSettings

        if (!parserSettings.anyExtensions(PegdownExtensions.AUTOLINKS)) return null

        val autoLinks = MdPsiImplUtil.listChildrenOfAnyType(psiFile, false, false, false, MdAutoLink::class.java)
            .filter {
                val linkRefText = it.text
                !linkRefText.startsWith('<') || !linkRefText.endsWith('>')
            }

        val panel = EditorNotificationPanel()
        panel.setText(MdBundle.message("editor.auto-links-enabled-in-large-file.name"))

        if (autoLinks.isNotEmpty()) {
            panel.createActionLabel(MdBundle.message("editor.auto-links-enabled-in-large-file.wrap-all")) {
                //MarkdownRenderingProfile newProfile = new MarkdownRenderingProfile(renderingProfile);
                WriteCommandAction.runWriteCommandAction(project) {
                    val charsSequence = document.charsSequence

                    for (autoLink in autoLinks.reversed()) {
                        val text = autoLink.linkRefText
                        val textRange = autoLink.textRange
                        val endOffset = textRange.endOffset
                        val startOffset = textRange.startOffset
                        if (!text.endsWith('>') && (endOffset >= charsSequence.length || charsSequence[endOffset] != '>')) document.insertString(endOffset, ">")
                        if (!text.startsWith('<') && (startOffset == 0 || charsSequence[startOffset - 1] != '<')) document.insertString(startOffset, "<")
                    }

                    PsiDocumentManager.getInstance(project).commitDocument(document)
                    EditorNotifications.getInstance(project).updateNotifications(file)
                }
            }
        } else {
            panel.createActionLabel(MdBundle.message("editor.auto-links-enabled-in-large-file.disable")) {
                //MarkdownRenderingProfile newProfile = new MarkdownRenderingProfile(renderingProfile);
                WriteCommandAction.runWriteCommandAction(project) {
                    val newParserSettings = MdParserSettings(
                        parserSettings.pegdownFlags and (PegdownExtensions.AUTOLINKS.flags).inv(),
                        parserSettings.optionsFlags,
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
            }
        }

        panel.createActionLabel(MdBundle.message("editor.javafx.dont.show.again")) {
            MdApplicationSettings.instance.wasShownSettings.autoLinksExtension = true
            EditorNotifications.updateAll()
        }
        return panel
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("editor.auto-links-in-large-file.name")
    }
}
