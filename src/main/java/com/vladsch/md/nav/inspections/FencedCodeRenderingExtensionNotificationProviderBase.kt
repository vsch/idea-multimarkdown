// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.inspections

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdFileType
import com.vladsch.md.nav.MdProjectComponent
import com.vladsch.md.nav.parser.flexmark.MdFencedCodeImageConversionManager
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.element.MdVerbatim
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.settings.*

abstract class FencedCodeRenderingExtensionNotificationProviderBase : EditorNotifications.Provider<EditorNotificationPanel>(), DumbAware {
    abstract fun getDefaultVariant(info: String): String
    abstract fun getPanelText(): String
    abstract fun getPanelLabel(): String
    abstract fun getInfoStrings(): Array<String>
    abstract var wasShown: Boolean
    protected open fun haveNeededParserSettings(parserSettings: MdParserSettings): Boolean = parserSettings.anyExtensions(PegdownExtensions.FENCED_CODE_BLOCKS)

    protected open fun conversionFilter(conversionManager: MdFencedCodeImageConversionManager, info: String, variant: String?): Boolean {
        return variant != null && conversionFilter(info, variant) && conversionManager.getImageConverter(info, variant) != null
    }

    protected open fun conversionFilter(info: String, variant: String): Boolean {
        return variant != "NONE"
    }

    protected open fun adjustRenderingProfile(conversionManager: MdFencedCodeImageConversionManager, renderingProfile: MdRenderingProfile, fencedCodeTypes: Set<String>) {
        // IMPORTANT: validate FencedCodeRendering for preview type when not JavaFX
        val parserSettings = renderingProfile.parserSettings
        parserSettings.pegdownFlags = parserSettings.pegdownFlags or PegdownExtensions.FENCED_CODE_BLOCKS.flags

        val newHtmlSettings = renderingProfile.htmlSettings
        val fencedCodeConversions = renderingProfile.htmlSettings.fencedCodeConversions
        fencedCodeTypes.forEach {
            val variant = fencedCodeConversions[it]
            val isRendered = conversionFilter(conversionManager, it, variant)
            if (!isRendered) {
                newHtmlSettings.fencedCodeConversions[it] = getDefaultVariant(it)
            }
        }

        conversionManager.updateCssSettings(renderingProfile) { it in getInfoStrings() }
    }

    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project:Project): EditorNotificationPanel? {
        if (wasShown) return null

        if (file.fileType !== MdFileType.INSTANCE || fileEditor !is TextEditor) {
            return null
        }

        val editor = fileEditor.editor as? EditorEx ?: return null

        if (DumbService.isDumb(project)) {
            return null
        }

        val virtualFile = editor.virtualFile
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? MdFile ?: return null
        val profileManager = MdRenderingProfileManager.getInstance(project)
        val renderingProfile = profileManager.getRenderingProfile(psiFile)
        val parserSettings = renderingProfile.parserSettings

        val fencedCodeTypes = MdPsiImplUtil.listChildrenOfAnyType(psiFile, false, false, false, MdVerbatim::class.java).map { it.infoString }.filter { it in getInfoStrings() }.toSet()
        // IMPORTANT: add inline math detection

        if (fencedCodeTypes.isNotEmpty()) {
            val conversionManager = MdFencedCodeImageConversionManager.getInstance(project)
            val fencedCodeConversions = renderingProfile.htmlSettings.fencedCodeConversions

            // IMPORTANT: validate FencedCodeRendering for preview type when not JavaFX
            val haveAll = fencedCodeTypes.all {
                val variant = fencedCodeConversions[it]
                conversionFilter(conversionManager, it, variant)
            }

            if (haveAll && haveNeededParserSettings(parserSettings)) {
                // need to check CSS Settings
                val newRenderingProfile = MdRenderingProfile(renderingProfile)
                if (!conversionManager.updateCssSettings(newRenderingProfile) { it in getInfoStrings() }) return null
            }

            val panel = EditorNotificationPanel()
            panel.text = getPanelText()
            panel.createActionLabel(getPanelLabel()) {
                val newRenderingProfile = MdRenderingProfile(renderingProfile)

                adjustRenderingProfile(conversionManager, newRenderingProfile, fencedCodeTypes)

                if (renderingProfile.profileName.isEmpty()) {
                    MdProjectSettings.getInstance(project).renderingProfile = newRenderingProfile
                } else {
                    renderingProfile.renderingProfile = newRenderingProfile
                    profileManager.replaceProfile(renderingProfile.profileName, newRenderingProfile)
                }

                MdProjectComponent.getInstance(project).reparseMarkdown(true)
                EditorNotifications.updateAll()
            }

            panel.createActionLabel(MdBundle.message("editor.javafx.dont.show.again")) {
                wasShown = true
                EditorNotifications.updateAll()
            }
            return panel
        }

        return null
    }
}
