/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown

import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.plugins.cl.PluginClassLoader
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationManagerEx
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.xml.util.XmlStringUtil
import com.vladsch.idea.multimarkdown.editor.MultiMarkdownPathResolver
import com.vladsch.idea.multimarkdown.license.LicenseAgent
import com.vladsch.idea.multimarkdown.license.LicenseRequest
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import java.io.*
import java.net.URL
import java.util.*

class MultiMarkdownPlugin : ApplicationComponent {
    private var globalSettingsListener: MultiMarkdownGlobalSettingsListener? = null

    private var myClassLoader: PluginClassLoader? = null

    var urlLayoutFxCss: String? = null
        private set
    var urlDefaultFxCss: String? = null
        private set
    var urlDarculaFxCss: String? = null
        private set
    var urlHljsDefaultFxCss: String? = null
        private set
    var urlHljsDarculaFxCss: String? = null
        private set
    var urlHighlightJs: String? = null
        private set
    var urlCustomFxCss: String? = null
        private set
    var urlCustomFont: String? = null
        private set

    private var fileCustomFxCss: File? = null
    private var tempDirPath: String? = null
    val agent: LicenseAgent

    init {
        //BasicConfigurator.configure();
        //logger.addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
        val appender = ConsoleAppender(PatternLayout("%p %c %x - %m%n"))
        //Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        //while (appenders.hasMoreElements()) {
        //    Appender app = (Appender) appenders.nextElement();
        //    String name = app.getName();
        //}
        logger.addAppender(appender)
        logger.additivity = false
        logger.level = Level.INFO

        agent = LicenseAgent()

        // turn off lcd rendering, will use gray
        //System.setProperty("prism.lcdtext", "false");
        myClassLoader = null
        val settings = MultiMarkdownGlobalSettings.getInstance()
        classLoader

        license_features = 0

        // get the tmp directory location
        try {
            val tmpFile = File.createTempFile("multimarkdown_font_file", ".ttf")
            val path = tmpFile.absolutePath
            tempDirPath = path.substring(0, path.lastIndexOf("multimarkdown_font_file"))
            //noinspection ResultOfMethodCallIgnored
            tmpFile.delete()
        } catch (e: IOException) {
            //e.printStackTrace();
        }

        // now we make temp copies of the fx css files with the font reference replaced to tasklists.png, which is really a .ttf file
        // but for some reason WebView won't accept a copy of the file to the temp dir and IDEA won't include .ttf files in resources
        // directory, but will if the file is .png, ok we live with that, WebView takes the file with a .png extension and correctly figures
        // out that it is .ttf

        // Listen to settings changes
        urlCustomFont = null
        fileCustomFxCss = null
        urlLayoutFxCss = null
        urlCustomFxCss = null
        urlDefaultFxCss = null
        urlDarculaFxCss = null
        urlHljsDefaultFxCss = null
        urlHljsDarculaFxCss = null
        urlHighlightJs = null
        globalSettingsListener = null

        urlCustomFont = createCustomFontUrl()
        urlLayoutFxCss = createTempCopy(MultiMarkdownPlugin::class.java.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_STYLESHEET_LAYOUT), "layout-fx.css")
        urlDefaultFxCss = createTempCopy(MultiMarkdownPlugin::class.java.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_STYLESHEET_LIGHT), "default-fx.css")
        urlDarculaFxCss = createTempCopy(MultiMarkdownPlugin::class.java.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_STYLESHEET_DARK), "darcula-fx.css")
        urlHljsDefaultFxCss = createTempCopy(MultiMarkdownPlugin::class.java.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_HLJS_STYLESHEET_LIGHT), "hljs-default-fx.css")
        urlHljsDarculaFxCss = createTempCopy(MultiMarkdownPlugin::class.java.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_HLJS_STYLESHEET_DARK), "hljs-darcula-fx.css")
        urlHighlightJs = createTempCopy(MultiMarkdownPlugin::class.java.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_HIGHLIGHT_JS), "highlight.pack.js")

        try {
            fileCustomFxCss = createTempCopy(settings.customFxCss.value, "custom-fx.css")
            urlCustomFxCss = fileCustomFxCss!!.toURI().toURL().toExternalForm()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        globalSettingsListener = MultiMarkdownGlobalSettingsListener {
            if (fileCustomFxCss != null) {
                try {
                    // 1.8u60 caches the css by name, we have to change the name or no refresh is done
                    //updateTempCopy(fileCustomFxCss, newSettings.customFxCss.getValue());
                    //noinspection ResultOfMethodCallIgnored
                    fileCustomFxCss!!.delete()
                    fileCustomFxCss = createTempCopy(settings.customFxCss.value, "custom-fx.css")
                    urlCustomFxCss = fileCustomFxCss!!.toURI().toURL().toExternalForm()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            // check license information
            loadLicenseActivation(false)
        }

        MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener as MultiMarkdownGlobalSettingsListener)
    }

    protected fun createCustomFontUrl(): String? {
        // create the freaking thing
        try {
            val fontStream = javaClass.getResourceAsStream("/com/vladsch/idea/multimarkdown/taskitems.ttf")
            val tempFontFile = File(tempDirPath!! + "multimarkdown_taskitems.ttf")
            //tempFontFile.deleteOnExit();
            logger.info("creating temp font file: " + tempFontFile.absolutePath)

            val fileOutputStream = FileOutputStream(tempFontFile)
            val buffer = ByteArray(32768)
            var length: Int
            while (true) {
                length = fontStream.read(buffer)
                if (length <= 0) break;
                fileOutputStream.write(buffer, 0, length)
            }
            fileOutputStream.close()
            fontStream.close()

            return tempFontFile.toURI().toURL().toExternalForm()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    protected fun createTempCopy(resourceFile: URL, suffix: String): String {
        if (urlCustomFont != null) {
            try {
                val cssTempFile = createTempCopy(Resources.toString(resourceFile, Charsets.UTF_8), suffix)
                return cssTempFile.toURI().toURL().toExternalForm()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return resourceFile.toExternalForm()
    }

    @Throws(IOException::class)
    protected fun createTempCopy(cssText: String, suffix: String): File {
        val cssTempFile: File
        if ("custom-fx.css" == suffix) {
            cssTempFile = File.createTempFile("multimarkdown", suffix)
            cssTempFile.deleteOnExit()
        } else {
            cssTempFile = File(tempDirPath + "multimarkdown_" + suffix)
        }
        updateTempCopy(cssTempFile, cssWithCustomFont(cssText))
        return cssTempFile
    }

    @Throws(IOException::class)
    protected fun updateTempCopy(cssTempFile: File, cssText: String) {
        val cssFileWriter = FileWriter(cssTempFile)
        cssFileWriter.write(cssWithCustomFont(cssText))
        cssFileWriter.close()
    }

    fun cssWithCustomFont(cssWithFontRef: String): String {
        if (urlCustomFont != null) {
            return cssWithFontRef.replace("'taskitems.ttf'", "'$urlCustomFont'")
        }
        return cssWithFontRef
    }

    //logger.warn("JavaFX library jfxrt.jar not found in the provided jre: " + javaHome + ", version " + javaVersion);
    //} else if (!file.exists() || "1.8.0_u51".compareTo(javaVersion.substring(0, Math.min(javaVersion.length(), "1.8.0_u51".length()))) < 0) {
    //    logger.warn("JavaFX library jfxrt.jar was found in the current jre: " + javaHome + ", but version " + javaVersion + " is not supported, max version is 1.8.0_u51");
    //    logger.warn("MultiMarkdown HTML Preview will use a more limited implementation.");
    //    MultiMarkdownGlobalSettings.getInstance().useOldPreview.setValue(true);
    val classLoader: PluginClassLoader
        get() {
            if (myClassLoader == null) {
                val pluginClassLoader = javaClass.classLoader as PluginClassLoader
                val javaHome = System.getProperty("java.home")
                val javaVersion = System.getProperty("java.version")
                val libDir = FileUtil.join(javaHome, "lib")
                val libExtDir = FileUtil.join(libDir, "ext")
                val fileName = FileUtil.join(libExtDir, "jfxrt.jar")
                val file = File(fileName)
                if (!file.exists()) {
                    logger.info("JavaFX library jfxrt.jar not found in the provided jre: $javaHome, version $javaVersion")
                    logger.info("MultiMarkdown HTML Preview will use JEditorPane with HTMLEditorKit for rendering instead of JavaFX WebView.")
                    MultiMarkdownGlobalSettings.getInstance().useOldPreview.value = true
                } else {
                    logger.info("JavaFX library jfxrt.jar found in $libDir the current jre: $javaHome, version $javaVersion")
                    val libs = ArrayList<String>(1)
                    libs.add(libExtDir)
                    pluginClassLoader.addLibDirectories(libs)
                }
                myClassLoader = pluginClassLoader
            }
            return myClassLoader as PluginClassLoader
        }

    override fun initComponent() {
        loadLicenseActivation(true)
    }

    fun loadLicenseActivation(init: Boolean) {
        val globalSettings = MultiMarkdownGlobalSettings.getInstance()
        try {
            globalSettings.startSuspendNotifications()
            val licenseCode = globalSettings.licenseCode.value
            var activationCode = globalSettings.activationCode.value

            if (agent.licenseCode() != licenseCode.trim { it <= ' ' }) {
                activationCode = ""
            }

            agent.setLicenseActivationCodes(licenseCode, activationCode)
            if (agent.isValidLicense) {
                if (!agent.isValidActivation) {
                    agent.setActivationCode(null)

                    // request activation
                    val request = LicenseRequest(productName, productVersion)
                    request.license_code = licenseCode
                    if (!agent.getLicenseCode(request)) {
                        if (agent.isRemoveLicense) {
                            agent.setLicenseActivationCodes(null, null)
                        }
                    }
                }
            }

            // update both to the agent's values
            globalSettings.licenseCode.value = agent.licenseCode()
            globalSettings.activationCode.value = agent.activationCode()
        } finally {
            globalSettings.endSuspendNotifications()
        }

        initLicense(init)
    }

    fun initLicense(init: Boolean) {
        // load license information
        val showOnce = true

        if (agent.isValidLicense && agent.isValidActivation) {
            license_features = agent.licenseFeatures
            if (init) {
                if (showOnce)
                    MultiMarkdownGlobalSettings.getInstance().wasShownLicensedAvailable.value = true
                else
                    notifyLicensedUpdate(null, showOnce)
            }
        } else {
            license_features = 0
            if (init) {
                notifyLicensedUpdate(null, showOnce)
            }
        }
    }

    override fun disposeComponent() {
        // empty
    }

    override fun getComponentName(): String {
        return this.javaClass.name
    }

    fun getLogger(): Logger {
        return logger
    }

    companion object {
        private val logger = org.apache.log4j.Logger.getLogger("com.vladsch.idea.multimarkdown")
        private var license_features: Int = 0

        @JvmStatic
        val productName: String
            get() = "idea-multimarkdown"

        @JvmStatic
        val productVersion: String
            get() {
                try {
                    val pluginId = PluginId.findId("com.vladsch.idea.multimarkdown")
                    if (pluginId != null) {
                        val pluginDescriptor = PluginManager.getPlugin(pluginId)
                        if (pluginDescriptor != null) {
                            val version = pluginDescriptor.version
                            // truncate version to 3 digits and if had more than 3 append .x, that way
                            // no separate product versions need to be created
                            val parts = version.split(delimiters = '.', limit = 4)
                            if (parts.size <= 3) {
                                return version
                            }

                            val newVersion = parts.subList(0, 3).reduce { total, next -> total + "." + next }
                            return newVersion + ".x"
                        }
                    }
                } catch (ignored: NoSuchMethodError) {
                    logger.info("PluginId.findId() method not supported in this product version.")
                }

                return "1.2.x"
            }

        @JvmStatic
        fun areAllLicensed(licenseFeatures: Int): Boolean {
            return (license_features and licenseFeatures) == licenseFeatures
        }

        @JvmStatic
        fun areSomeLicensed(licenseFeatures: Int): Boolean {
            return (license_features and licenseFeatures) != 0
        }

        @JvmStatic
        val isLicensed: Boolean
            get() = license_features != 0

        @JvmStatic
        fun notifyLicensedUpdate(project: Project?, showOnce: Boolean) {
            val settings = MultiMarkdownGlobalSettings.getInstance()

            if (!showOnce || !settings.wasShownLicensedAvailable.value) {
                // notify the user that a new licensed version is available
                if (showOnce) settings.wasShownLicensedAvailable.value = true
                val app = ApplicationManagerEx.getApplicationEx()

                val issueNotificationGroup = NotificationGroup(MultiMarkdownGlobalSettings.NOTIFICATION_GROUP_UPDATE, NotificationDisplayType.STICKY_BALLOON, true, null)

                val href = (if (showOnce) "http://vladsch.com" else "http://vladsch.dev") + "/product/multimarkdown/specials"
                val title = MultiMarkdownBundle.message("plugin.updated.notification.title", productName)
                val action = MultiMarkdownBundle.message("plugin.updated.notification.action")
                val message = MultiMarkdownBundle.message("plugin.updated.notification.message", action, href)
                val listener = NotificationListener { notification, hyperlinkEvent ->
                    //notification.expire();
                    MultiMarkdownPathResolver.openLink(href)
                }

                issueNotificationGroup.createNotification(title, XmlStringUtil.wrapInHtml(message), NotificationType.INFORMATION, listener).notify(project)
            }
        }

        @JvmStatic
        val instance: MultiMarkdownPlugin
            get() = ApplicationManager.getApplication().getComponent(MultiMarkdownPlugin::class.java) ?: MultiMarkdownPlugin()

        @JvmStatic
        fun getProjectComponent(project: Project): MultiMarkdownProjectComponent? {
            return project.getComponent(MultiMarkdownProjectComponent::class.java)
        }
    }
}
