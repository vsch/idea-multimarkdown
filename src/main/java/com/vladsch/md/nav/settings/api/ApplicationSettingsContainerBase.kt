// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.ex.EditorEx
import com.vladsch.md.nav.settings.*
import java.util.*
import javax.swing.JPanel

internal abstract class ApplicationSettingsContainerBase(@JvmField val myApplicationSettings: MdApplicationSettings, @JvmField val myProfileSynchronizer: RenderingProfileSynchronizer) : MdSettingsComponent<MdApplicationSettingsHolder>, Disposable {
    protected abstract val mainFormPanel: JPanel?

    private var myInitialShow = true

    private val myTextEditorUpdates = HashMap<CustomizableEditorTextField, String>()
    private val myTextEditorUpdateRunnable = arrayOf<Runnable?>(null)

    protected abstract fun updateFormOnReshow(isInitialShow: Boolean)

    protected open fun initializeOnFormCreate() {
    }

    protected open fun initialFormShow() {
    }

    protected open fun disposeResources() {
    }

    final override fun dispose() {
        disposeResources()
    }

    protected open fun onCssSettingsChanged(cssSettings: MdCssSettings) {
    }

    protected fun onFormCreated() {
        initializeOnFormCreate()

        // DEPRECATED: this is only needed to update editors editable state on older IDEA's
        // IMPORTANT: validate if this is still needed for 2019 and 2020
        if (mainFormPanel == null) {
            // must be not the last child form, it will be initialized when the last child of the main form initializes
            myDelayedInitPanels.add(this)
        } else {
            myDelayedInitPanels.add(this)
            val panelsToHook = myDelayedInitPanels.toTypedArray()

            for (formPanel in panelsToHook) {
                if (formPanel.mainFormPanel != null) {
                    myDelayedInitPanels.remove(formPanel)

                    //                    ImageUtils.initAntiAliasing(mainFormPanel)

                    formPanel.mainFormPanel!!.addPropertyChangeListener { event ->
                        if (event.propertyName == "ancestor" && event.oldValue == null && event.newValue != null) {
                            ApplicationManager.getApplication().invokeLater {
                                val initialShow = myInitialShow
                                if (initialShow) {
                                    myInitialShow = false
                                    initialFormShow()
                                }
                                updateFormOnReshow(initialShow)
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateCustomizableTextFieldEditorText(editorTextField: CustomizableEditorTextField, text: String) {
        //editorTextField.setPlacement(canEdit);
        myTextEditorUpdates.put(editorTextField, text)
        val updates = myTextEditorUpdates
        val updateRunnable = myTextEditorUpdateRunnable

        if (updateRunnable[0] == null) {
            updateRunnable[0] = Runnable {
                for ((key, value) in updates) {
                    val editor = key.editor as EditorEx?
                    if (editor != null && !editor.isDisposed) {
                        val canEdit = editor.document.isWritable
                        editor.document.setReadOnly(false)
                        key.text = value
                        editor.document.setReadOnly(!canEdit)
                    }
                }

                updates.clear()
                updateRunnable[0] = null
            }

            ApplicationManager.getApplication().invokeLater(updateRunnable[0]!!)
        }
    }

    // need to get editor field text but if there is a pending update we need to take the value of that update
    fun getCustomizableTextFiledEditorText(editorTextField: CustomizableEditorTextField): String {
        return myTextEditorUpdates[editorTextField] ?: editorTextField.text
    }

    companion object {
        // no locking is used because this is only modified on the EDT
        private val myDelayedInitPanels = ArrayList<ApplicationSettingsContainerBase>()
    }
}
