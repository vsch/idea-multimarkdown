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
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownNonRoamingGlobalSettings
import com.vladsch.idea.multimarkdown.util.wrapWith
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
        val nonRoamingSettings = MultiMarkdownNonRoamingGlobalSettings.getInstance()
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
        // RELEASE : change to true
        val showOnce = true
        val settings = MultiMarkdownGlobalSettings.getInstance()

        if (agent.isValidLicense && agent.isValidActivation) {
            license_features = agent.licenseFeatures
            if (init) {
                if (showOnce) {
                    settings.wasShownLicensedAvailable.value = true
                } else
                    notifyLicensedUpdate(showOnce)

                settings.licenseType.value = agent.licenseType
                settings.showLicenseExpired.value = true
                val expiresIn = agent.licenseExpiringIn

                if (expiresIn <= 7) {
                    val licenseType = MultiMarkdownBundle.message("settings.license-type-" + agent.licenseType)
                    notifyLicenseExpiration(!showOnce, licenseType, agent.licenseExpiration, expiresIn)
                } else if (!showOnce) {
                    val licenseType = MultiMarkdownBundle.message("settings.license-type-" + agent.licenseType)
                    notifyLicenseExpiration(!showOnce, licenseType, agent.licenseExpiration, expiresIn)
                    notifyLicenseExpiration(!showOnce, licenseType, agent.licenseExpiration, 2)
                    notifyLicenseExpiration(!showOnce, licenseType, agent.licenseExpiration, -1)
                    notifyLicenseExpiration(!showOnce, "trial", agent.licenseExpiration, 2)
                    notifyLicenseExpiration(!showOnce, "trial", agent.licenseExpiration, 1)
                    notifyLicenseExpiration(!showOnce, "trial", agent.licenseExpiration, -1)
                    //                    notifyLicenseExpiration(!showOnce, "subscription", agent.licenseExpiration, 2)
                    //                    notifyLicenseExpiration(!showOnce, "subscription", agent.licenseExpiration, 1)
                    //                    notifyLicenseExpiration(!showOnce, "subscription", agent.licenseExpiration, -1)
                }
            }
        } else {
            license_features = 0
            if (init) {
                if (!settings.licenseType.value.isEmpty()) {
                    notifyLicenseExpiration(!showOnce, settings.licenseType.value, "", -1)
                }
                notifyLicensedUpdate(showOnce)
            }
        }

        if (init) {
            notifyFeatureUpdate(!showOnce, productVersion)
        }
    }

    protected fun applyHtmlColors(htmlText: String, settings: MultiMarkdownGlobalSettings): String {
        val enhColor = if (settings.isDarkUITheme) "#B0A8E6" else "#6106A5"
        val buyColor = if (settings.isDarkUITheme) "#F0A8D4" else "#C02080"
        val specialsColor = if (settings.isDarkUITheme) "#A4EBC5" else "#04964F"
        return htmlText.replace("[[ENHANCED]]", enhColor).replace("[[BUY]]", buyColor).replace("[[SPECIALS]]", specialsColor)
    }

    protected fun notifyFeatureUpdate(debug: Boolean, currentVersion: String) {
        val settings = MultiMarkdownGlobalSettings.getInstance()
        val showNotification = settings.showFeatureUpdates

        if (showNotification.value && settings.lastFeatureUpdateVersion.value != currentVersion) {
            val lastFeatureUpdateVersion = settings.lastFeatureUpdateVersion.value
            val notificationType = NotificationType.INFORMATION
            if (!debug) settings.lastFeatureUpdateVersion.value = currentVersion

            val issueNotificationGroup = NotificationGroup(MultiMarkdownGlobalSettings.NOTIFICATION_GROUP_UPDATE, NotificationDisplayType.STICKY_BALLOON, true, null)

            // collect features based on latest version
            val featureList = """
- Warning if GitHub will render wiki pages as raw text not HTML
- Plugin preferences now split between local and shared
- Inspections for common errors in wiki link resolution
- HTML Text tab now has all links as URI's
* Github fork and raw links are now recognized by the plugin
* Link color, image border in preview when file not on GitHub
* Change link address between relative &amp; absolute
* Change between wiki &amp; explicit link
* Inspections for common errors in explicit and image links
* Absolute links to project files now validated
* Navigation line markers for absolute links to project files
* Absolute link completions for https:// &amp; file:// links.
* Completions now work with non-markdown files
* Refactoring changes wiki to explicit links if target moved from wiki
* Refactoring preserves link format: relative, https:// or file://
"""
            val features = featureList.split('\n').fold("") { accum, elem ->
                val item = elem.trim()
                accum +
                (
                        if (item.startsWith('*')) item.removePrefix("*").trim().wrapWith("<span style=\"color: [[ENHANCED]]\">", "</span>")
                        else item.removePrefix("-").trim()
                ).wrapWith("<li>", "</li>")
            }.wrapWith("""
<h4 style="margin: 0;">New Features: Basic &amp; Enhanced / <span style="color: [[ENHANCED]]">Enhanced Edition only</span></h4>
<ul style="margin-left: 10px;">
""", "</ul>")

            val href = (if (!debug) "http://vladsch.com" else "http://vladsch.dev") + "/"
            val title = MultiMarkdownBundle.message("plugin.feature.notification.title", productName, currentVersion)
            val licenseBased = if (isLicensed) MultiMarkdownBundle.message("plugin.feature.notification.licensed") else MultiMarkdownBundle.message("plugin.feature.notification.unlicensed")
            val message = applyHtmlColors(MultiMarkdownBundle.message("plugin.feature.notification.message", features, licenseBased), settings)

            val listener = NotificationListener { notification, hyperlinkEvent ->
                //notification.expire();
                if (hyperlinkEvent.url == null) {
                    val link = hyperlinkEvent.description
                    when (link) {
                        ":DISABLE" -> {
                            showNotification.value = false
                            notification.expire()
                        }
                        ":BUY" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/buy")
                        ":TRY" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/try")
                        ":SPECIALS" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/specials")
                        ":FEATURES" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown")
                    }
                } else {
                    MultiMarkdownPathResolver.openLink(hyperlinkEvent.url.toString())
                }
            }

            issueNotificationGroup.createNotification(title, XmlStringUtil.wrapInHtml(message), notificationType, listener).notify(null)
        }
    }

    protected fun notifyLicenseExpiration(debug: Boolean, licenseType: String, expiration: String, expiresIn: Int) {
        val settings = MultiMarkdownGlobalSettings.getInstance()
        val showLicenseNotification = if (expiresIn < 0) settings.showLicenseExpired else settings.showLicenseExpiration

        if (showLicenseNotification.value) {
            var notification = if (expiresIn < 0) "license-expired" else "license-expiring"
            val expires =
                    when {
                        expiresIn <= 0 -> MultiMarkdownBundle.message("settings.notification.license-has-expired-" + licenseType)
                        expiresIn == 1 -> MultiMarkdownBundle.message("settings.notification.license-expires-tomorrow-" + licenseType)
                        else -> MultiMarkdownBundle.message("settings.notification.license-expires-" + licenseType, expiration, MultiMarkdownBundle.message("settings.notification.license-expires-in-days", expiresIn))
                    }

            val notificationType =
                    when {
                        expiresIn <= 0 -> NotificationType.WARNING
                        else -> NotificationType.INFORMATION
                    }

            val issueNotificationGroup = NotificationGroup(MultiMarkdownGlobalSettings.NOTIFICATION_GROUP_LICENSE, NotificationDisplayType.STICKY_BALLOON, true, null)

            val href = (if (!debug) "http://vladsch.com" else "http://vladsch.dev") + "/"
            val title = MultiMarkdownBundle.message("plugin.$notification.notification.title", productName)
            val message = applyHtmlColors(MultiMarkdownBundle.message("plugin.$notification.notification.message", MultiMarkdownBundle.message("settings.license-type-" + licenseType), expires), settings)

            val listener = NotificationListener { notification, hyperlinkEvent ->
                //notification.expire();
                if (hyperlinkEvent.url == null) {
                    val link = hyperlinkEvent.description
                    when (link) {
                        ":DISABLE" -> {
                            showLicenseNotification.value = false
                            notification.expire()
                            if (expiresIn <= 0 && !debug) {
                                settings.licenseType.value = ""
                            }
                        }
                        ":BUY" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/buy")
                        ":TRY" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/try")
                        ":SPECIALS" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/specials")
                        ":FEATURES" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown")
                    }
                } else {
                    MultiMarkdownPathResolver.openLink(hyperlinkEvent.url.toString())
                }
            }

            issueNotificationGroup.createNotification(title, XmlStringUtil.wrapInHtml(message), notificationType, listener).notify(null)
        }
    }

    protected fun notifyLicensedUpdate(showOnce: Boolean) {
        val settings = MultiMarkdownGlobalSettings.getInstance()

        if (!showOnce || !settings.wasShownLicensedAvailable.value) {
            if (showOnce) settings.wasShownLicensedAvailable.value = true
            val issueNotificationGroup = NotificationGroup(MultiMarkdownGlobalSettings.NOTIFICATION_GROUP_UPDATE, NotificationDisplayType.STICKY_BALLOON, true, null)

            val href = (if (showOnce) "http://vladsch.com" else "http://vladsch.dev") + "/"
            val title = MultiMarkdownBundle.message("plugin.licensed-available.notification.title", productName)
            val message = applyHtmlColors(MultiMarkdownBundle.message("plugin.licensed-available.notification.message"), settings)
            val listener = NotificationListener { notification, hyperlinkEvent ->
                //notification.expire();
                if (hyperlinkEvent.url == null) {
                    val link = hyperlinkEvent.description
                    when (link) {
                        ":DISABLE" -> {
                            settings.wasShownLicensedAvailable.value = false
                            notification.expire()
                        }
                        ":BUY" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/buy")
                        ":TRY" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/try")
                        ":SPECIALS" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown/specials")
                        ":FEATURES" -> MultiMarkdownPathResolver.openLink(href + "product/multimarkdown")
                    }
                } else {
                    MultiMarkdownPathResolver.openLink(hyperlinkEvent.url.toString())
                }
            }

            issueNotificationGroup.createNotification(title, XmlStringUtil.wrapInHtml(message), NotificationType.INFORMATION, listener).notify(null)
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
        val instance: MultiMarkdownPlugin
            get() = ApplicationManager.getApplication().getComponent(MultiMarkdownPlugin::class.java) ?: MultiMarkdownPlugin()

        @JvmStatic
        fun getProjectComponent(project: Project): MultiMarkdownProjectComponent? {
            return project.getComponent(MultiMarkdownProjectComponent::class.java)
        }
    }
}
