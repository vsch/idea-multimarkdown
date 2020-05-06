// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx
import com.intellij.openapi.options.ex.Settings
import com.intellij.openapi.project.Project
import com.intellij.ui.ColorUtil
import com.intellij.ui.EditorTextField
import com.intellij.ui.HyperlinkLabel
import com.vladsch.plugin.util.ui.SettableForm
import java.awt.event.ActionListener
import java.util.function.BiConsumer
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener

interface MdSettableFormBase<T> : SettableForm<T> {
    companion object {
        @JvmStatic
        fun getActionListenerBrowseUrl(url: String): ActionListener {
            return ActionListener { BrowserUtil.browse(url) }
        }

        @JvmStatic
        fun updateEditorTextFieldEditable(editorTextField: EditorTextField, canEdit: Boolean) {
            //editorTextField.setPlacement(canEdit);
            ApplicationManager.getApplication().invokeLater {
                val editor = editorTextField.editor as EditorEx?
                if (editor != null && !editor.isDisposed) {
                    try {
                        editor.document.setReadOnly(!canEdit)
                        editor.settings.isCaretRowShown = canEdit
                    } catch (ignored: NoSuchMethodError) {

                    }

                    val baseColor = editor.colorsScheme.defaultBackground

                    if (canEdit) {
                        editor.backgroundColor = baseColor
                    } else {
                        editor.backgroundColor = if (ColorUtil.isDark(baseColor)) ColorUtil.brighter(baseColor, 1) else ColorUtil.darker(baseColor, 1)
                    }
                }
            }
        }

        @JvmStatic
        fun createCustomizableTextFieldEditor(editorCustomizationListener: CustomizableEditorTextField.EditorCustomizationListener, fileTypeExt: String, project: Project?): CustomizableEditorTextField {
            val editorTextField = CustomizableEditorTextField(FileTypeManagerEx.getInstanceEx().getFileTypeByExtension(fileTypeExt), project, "", false)
            editorTextField.setFontInheritedFromLAF(false)
            editorTextField.registerListener(editorCustomizationListener)
            return editorTextField
        }

        @JvmStatic
        fun createHyperlinkLabel(hyperlinkText: String?, handler: BiConsumer<DataContext, Settings>): HyperlinkLabel {
            return createHyperlinkLabel(null, hyperlinkText, null, handler)
        }

        @JvmStatic
        fun createHyperlinkLabel(prefix: String?, hyperlinkText: String?, suffix: String?, handler: BiConsumer<DataContext, Settings>): HyperlinkLabel {
            val hyperlink = HyperlinkLabel()
            hyperlink.isVisible = true
            hyperlink.isEnabled = true

            if (hyperlinkText != null) hyperlink.setHyperlinkText(prefix ?: "", hyperlinkText, suffix ?: "")

            hyperlink.addHyperlinkListener(HyperlinkListener { e: HyperlinkEvent ->
                if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    RenderingProfileSynchronizer.getSettings(handler)
                }
            })
            return hyperlink
        }
    }
}
