// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings.api

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.settings.*
import java.util.*
import javax.swing.JPanel
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener

abstract class SettingsFormBase(@JvmField protected val myProfileSynchronizer: RenderingProfileSynchronizer) : MdSettableFormBase<MdRenderingProfileHolder>, MdSettingsComponent<MdRenderingProfileHolder>, Disposable {
    protected abstract val mainFormPanel: JPanel?

    @JvmField
    protected var myInitialShow = true

    @JvmField
    protected val myProject: Project = myProfileSynchronizer.project

    protected val panelProvider: HtmlPanelProvider get() = myProfileSynchronizer.panelProvider
    protected val panelProviderInfo: HtmlPanelProvider.Info get() = myProfileSynchronizer.panelProvider.INFO

    @JvmField
    protected var myLastPanelProviderInfo: HtmlPanelProvider.Info = panelProviderInfo

    private val myTextEditorUpdates = HashMap<EditorTextField, String>()
    private val myTextEditorUpdateRunnable = arrayOf<Runnable?>(null)

    protected abstract fun updatePanelProviderDependentComponents(fromProvider: HtmlPanelProvider, toProvider: HtmlPanelProvider, isInitialShow: Boolean)
    protected abstract fun updateFormOnReshow(isInitialShow: Boolean)

    protected open fun initializeOnFormCreate() {
    }

    protected open fun initialFormShow() {
    }

    protected open fun onCssSettingsChanged(settings: MdRenderingProfileHolder) {
    }

    protected fun onFormCreated() {
        val synchronizerMessageBus = myProfileSynchronizer.messageBus.connect(this)
        synchronizerMessageBus.subscribe<RenderingProfileSynchronizer.PanelProviderChanged>(RenderingProfileSynchronizer.PANEL_PROVIDER_TOPIC, object : RenderingProfileSynchronizer.PanelProviderChanged {
            override fun updatePanelProvider(fromProvider: HtmlPanelProvider, toProvider: HtmlPanelProvider) {
                myLastPanelProviderInfo = toProvider.INFO
                updatePanelProviderDependentComponents(fromProvider, toProvider, false)
            }
        })

        synchronizerMessageBus.subscribe<RenderingProfileSynchronizer.RenderingProfileChanged>(RenderingProfileSynchronizer.RENDERING_PROFILE_TOPIC, object : RenderingProfileSynchronizer.RenderingProfileChanged {
            override fun updateRenderingProfile(renderingProfile: MdRenderingProfile) {
                reset(renderingProfile)
            }
        })

        synchronizerMessageBus.subscribe<RenderingProfileSynchronizer.CssSettingsChanged>(RenderingProfileSynchronizer.CSS_SETTINGS_TOPIC, object : RenderingProfileSynchronizer.CssSettingsChanged {
            override fun updateCssSettings(settings: MdRenderingProfileHolder) {
                onCssSettingsChanged(settings)
            }
        })

        initializeOnFormCreate()

        // DEPRECATED: this is only needed to update editors editable state on older IDEA's
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

                                    val provider = HtmlPanelProvider.getFromInfoOrDefault(myLastPanelProviderInfo)
                                    updatePanelProviderDependentComponents(provider, provider, true)
                                }
                                updateFormOnReshow(initialShow)
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateEditorTextFieldText(editorTextField: EditorTextField, text: String) {
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

    fun createCustomizableTextFieldEditor(editorCustomizationListener: CustomizableEditorTextField.EditorCustomizationListener, fileTypeExt: String): CustomizableEditorTextField {
        return MdSettableFormBase.createCustomizableTextFieldEditor(editorCustomizationListener, fileTypeExt, myProject)
    }

    // need to get editor field text but if there is a pending update we need to take the value of that update
    fun getEditorTextFieldText(editorTextField: EditorTextField): String {
        return myTextEditorUpdates[editorTextField] ?: editorTextField.text
    }

    companion object {
        private val LOG = Logger.getInstance(SettingsFormBase::class.java)

        // no locking is used because this is only modified on the EDT
        private val myDelayedInitPanels = ArrayList<SettingsFormBase>()

        @JvmStatic
        val hyperLinkListenerBrowseUrl: HyperlinkListener
            get() = HyperlinkListener { e ->
                if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    val href = e.url
                    if (href != null) {
                        BrowserUtil.browse(href)
                    }
                }
            }
    }
}
