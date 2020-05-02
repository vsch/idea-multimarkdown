/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.md.nav

import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.notification.impl.NotificationsManagerImpl
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.Anchor
import com.intellij.openapi.actionSystem.Constraints
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.BaseComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.wm.IdeFrame
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.BalloonLayoutData
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.vladsch.md.nav.actions.ide.CopyFileBareNameProvider
import com.vladsch.md.nav.actions.ide.CopyFileNameProvider
import com.vladsch.md.nav.actions.ide.CopyFilePathWithLineNumbersProvider
import com.vladsch.md.nav.actions.ide.CopyUpsourceFilePathWithLineNumbersProvider
import com.vladsch.md.nav.highlighter.MdSyntaxHighlighter
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdDocumentSettings
import com.vladsch.md.nav.settings.SettingsChangedListener
import com.vladsch.md.nav.settings.api.MdExtensionInfoProvider
import com.vladsch.md.nav.util.DynamicNotificationText
import com.vladsch.md.nav.util.MdCancelableJobScheduler
import com.vladsch.md.nav.util.TestExtensionProvider
import com.vladsch.plugin.util.*
import org.jetbrains.jps.service.SharedThreadPool
import java.awt.Color
import java.awt.Point
import java.util.function.Consumer
import javax.swing.UIManager

class MdPlugin : BaseComponent, Disposable {
    private var projectLoaded = false

    private val myNotificationRunnable: DelayedConsumerRunner<Project> = DelayedConsumerRunner()
    var startupDocumentSettings: MdDocumentSettings = MdDocumentSettings()
        private set
//    var startupDebugSettings: MdDebugSettings = MdDebugSettings()
//        private set

    private val restartRequiredChecker = object : AppRestartRequiredCheckerBase<MdApplicationSettings>(MdBundle.message("settings.restart-required.title")) {
        override fun getRestartNeededReasons(settings: MdApplicationSettings): Long {
            val fullHighlightChanged = startupDocumentSettings.fullHighlightCombinations != settings.documentSettings.fullHighlightCombinations
            return fullHighlightChanged.ifElse(1L, 0L)
        }
    }

    override fun initComponent() {
        val appSettings = MdApplicationSettings.instance

        ApplicationManager.getApplication().invokeLater {
            SharedThreadPool.getInstance().submit {
                try {
                    val actionManager = ActionManager.getInstance()

                    // document options for copy path to turn visibility of these on, all are off by default
                    val action0 = CopyUpsourceFilePathWithLineNumbersProvider()
                    actionManager.registerAction("CopyUpsourceFilePathWithLineNumbersProvider", action0)
                    val action1 = CopyFilePathWithLineNumbersProvider()
                    actionManager.registerAction("MdCopyPathWithSelectionLineNumbers", action1)
                    val action2 = CopyFileNameProvider()
                    actionManager.registerAction("MdCopyPathFileName", action2)
                    val action3 = CopyFileBareNameProvider()
                    actionManager.registerAction("MdCopyCopyPathBareName", action3)

                    val referenceAction = actionManager.getAction("CopyFileReference")
                    if (referenceAction is DefaultActionGroup) {
                        referenceAction.add(action0, Constraints(Anchor.AFTER, "CopyPathWithLineNumber"))
                        referenceAction.add(action1, Constraints(Anchor.AFTER, "CopyPathWithLineNumber"))
                        referenceAction.add(action2, Constraints(Anchor.AFTER, "CopyPathWithLineNumber"))
                        referenceAction.add(action3, Constraints(Anchor.AFTER, "CopyPathWithLineNumber"))
                    }
                } catch (e: Throwable) {
                }
            }
        }

        // make a copy so we can inform when it changes
        val documentSettings = appSettings.documentSettings
        startupDocumentSettings = MdDocumentSettings(documentSettings)
//        startupDebugSettings = MdDebugSettings(appSettings.debugSettings)

        LOG.info("Initializing Component: fullHighlights = ${documentSettings.fullHighlightCombinations}")

        // QUERY: do we still need to init default project settings just in case they don't exist?
//        MdProjectSettings.getInstance(ProjectManager.getInstance().defaultProject)

        val settingsChangedListener = SettingsChangedListener { applicationSettings ->
            ApplicationManager.getApplication().invokeLater {
                informRestartIfNeeded(applicationSettings)
            }
        }

        val settingsConnection = ApplicationManager.getApplication().messageBus.connect(this as Disposable)
        settingsConnection.subscribe(SettingsChangedListener.TOPIC, settingsChangedListener)

        @Suppress("DEPRECATION")
        EditorColorsManager.getInstance().addEditorColorsListener(EditorColorsListener {
            MdSyntaxHighlighter.computeMergedAttributes(true)
        }, this)

        // do initialization after first project is loaded
        myNotificationRunnable.addRunnable {
            AwtRunnable.schedule(MdCancelableJobScheduler.getInstance(), "New Features Notification", 1000) {
                // To Show if enhanced plugin is not installed
                val showAvailable = MdExtensionInfoProvider.EP_NAME.extensions.all {
                    if (it is MdEnhancedExtensionInfoProvider) it.showEnhancedPluginAvailable()
                    else true
                }

                if (showAvailable) {
                    notifyLicensedAvailableUpdate(fullProductVersion, MdResourceResolverImpl.instance.getResourceFileContent("/com/vladsch/md/nav/FEATURES.html"))
                }
            }
        }
    }

    private fun informRestartIfNeeded(applicationSettings: MdApplicationSettings) {
        restartRequiredChecker.informRestartIfNeeded(applicationSettings)
    }

    override fun dispose() {
    }

    fun projectLoaded(project: Project) {
        if (!projectLoaded) {
            projectLoaded = true

            fullProductVersion // load version

            myNotificationRunnable.runAll(project)

            // allow extensions to perform when first project is loaded
            MdExtensionInfoProvider.EP_NAME.extensions.forEach {
                if (it is MdEnhancedExtensionInfoProvider) it.projectLoaded(project)
            }
        }
    }

    private fun addHtmlColors(dynamicText: DynamicNotificationText): DynamicNotificationText {
        val isDarkUITheme = UIUtil.isUnderDarcula()
        val enhColor = if (isDarkUITheme) "#B0A8E6" else "#6106A5"
        val buyColor = if (isDarkUITheme) "#F0A8D4" else "#C02080"
        val specialsColor = if (isDarkUITheme) "#A4EBC5" else "#04964F"
        val linkColor = (UIManager.getColor("link.foreground") ?: Color(0x589df6)).toHtmlString()

        return dynamicText.addText("[[ENHANCED]]") { enhColor }
            .addText("[[BUY]]") { buyColor }
            .addText("[[SPECIALS]]") { specialsColor }
            .addText("[[LINK]]") { linkColor }
    }

    private fun addCommonLinks(dynamicText: DynamicNotificationText): DynamicNotificationText {
        val settings = MdApplicationSettings.instance.getLocalState()
        return dynamicText
            .addLink(":DISABLE_ENHANCED") {
                settings.wasShownSettings.licensedAvailable = true
                it.expire()
            }
            .addLink(":BUY") { BrowserUtil.browse("https://vladsch.com/product/markdown-navigator/buy") }
            .addLink(":TRY") { BrowserUtil.browse("https://vladsch.com/product/markdown-navigator/try") }
            .addLink(":NAME_CHANGE") { BrowserUtil.browse("https://github.com/vsch/idea-multimarkdown/wiki/Settings-Affected-by-Plugin-Name-Change") }
            .addLink(":SPECIALS") { BrowserUtil.browse("https://vladsch.com/product/markdown-navigator/specials") }
            .addLink(":FEATURES") { BrowserUtil.browse("https://vladsch.com/product/markdown-navigator") }
            .addLink(":REFERRALS") { BrowserUtil.browse("https://vladsch.com/product/markdown-navigator/referrals") }
            .addLink(":VERSION") { BrowserUtil.browse("https://github.com/vsch/idea-multimarkdown/blob/master/VERSION.md") }
    }

    private fun showFullNotification(project: Project?, notification: Notification) {
        val runnable = Consumer<IdeFrame> { frame ->
            val bounds = frame.component.bounds
            val target = RelativePoint(Point(bounds.x + bounds.width, 0))
            var handled = false
            try {
                val balloon = NotificationsManagerImpl.createBalloon(frame, notification, true, true, BalloonLayoutData.fullContent(), { })
                balloon.show(target, Balloon.Position.atRight)
                handled = true
            } catch (e: NoSuchMethodError) {
            } catch (e: NoClassDefFoundError) {
            } catch (e: NoSuchFieldError) {
            }

            if (!handled) {
                try {
                    val layoutData = BalloonLayoutData()
                    try {
                        layoutData.groupId = ""
                        layoutData.showSettingButton = false
                    } catch (e: NoSuchFieldError) {
                    }

                    layoutData.showFullContent = true
                    val layoutDataRef = Ref(layoutData)
                    val balloon = NotificationsManagerImpl.createBalloon(frame, notification, true, true, layoutDataRef, { })
                    balloon.show(target, Balloon.Position.atRight)
                } catch (e: NoSuchMethodError) {
                    // use normal balloons
                    notification.notify(project)
                } catch (e: NoSuchFieldError) {
                    // use normal balloons
                    notification.notify(project)
                } catch (e: NoClassDefFoundError) {
                    // use normal balloons
                    notification.notify(project)
                }
            }
        }

        if (project != null) {
            if (project.isDisposed) return
            val frame = WindowManager.getInstance().getIdeFrame(project)
            if (frame != null) {
                runnable.accept(frame)
                return
            }
        }

        var delayedRun: (project: Project) -> Unit = {}
        delayedRun = {
            if (!it.isDisposed) {
                val frame = WindowManager.getInstance().getIdeFrame(it)
                if (frame != null) {
                    runnable.accept(frame)
                } else {
                    // reschedule for a bit later
                    AwtRunnable.schedule(MdCancelableJobScheduler.getInstance(), "New Features Notification", 1000) {
                        delayedRun.invoke(it)
                    }
                }
            }
        }

        myNotificationRunnable.addRunnable(delayedRun)
    }

    private fun notifyLicensedAvailableUpdate(currentVersion: String, html: String) {
        val settings = MdApplicationSettings.instance.getLocalState()
        if (settings.wasShownSettings.lastLicensedAvailableVersion != currentVersion) {
            val notificationType = NotificationType.INFORMATION
            settings.wasShownSettings.licensedAvailable = true

            settings.wasShownSettings.lastLicensedAvailableVersion = currentVersion
            val issueNotificationGroup = PluginNotifications.NOTIFICATION_GROUP_UPDATE

            val title = MdBundle.message("plugin.licensed-available.notification.title", productDisplayName, currentVersion)
            val content = html.replace("<ul", String.format("<ul style=\"margin-left: %dpx\"", JBUI.scale(10)))
            val dynamicText = DynamicNotificationText()

            addHtmlColors(dynamicText)
            addCommonLinks(dynamicText)

            val listener = NotificationListener { notification, hyperlinkEvent ->
                if (hyperlinkEvent.url == null) {
                    val link = hyperlinkEvent.description
                    dynamicText.linkAction(notification, link)
                } else {
                    BrowserUtil.browse(hyperlinkEvent.url.toString())
                }
            }

            val messageText = dynamicText.replaceText(content)
            val notification = issueNotificationGroup.createNotification(title, messageText, notificationType, listener) //.notify(null)
            showFullNotification(null, notification)
        }
    }

    override fun disposeComponent() {
        // empty
    }

    override fun getComponentName(): String {
        return this.javaClass.name
    }

    fun getLogger(): Logger {
        return LOG
    }

    interface MdEnhancedExtensionInfoProvider : MdExtensionInfoProvider {
        fun showEnhancedPluginAvailable(): Boolean
        fun projectLoaded(project: Project)
    }

    companion object {
        private const val PLUGIN_ID = "com.vladsch.idea.multimarkdown"
        val LOG: Logger by lazy { Logger.getInstance(PLUGIN_ID) }

        const val siteURL: String = "https://vladsch.com"
        const val productPrefixURL: String = "/product/markdown-navigator"
        const val altProductPrefixURL: String = "/product/multimarkdown"
        const val patchRelease: String = "$siteURL$productPrefixURL/patch-release"
        const val eapRelease: String = "$siteURL$productPrefixURL/eap-release"
        const val altPatchRelease: String = "$siteURL$altProductPrefixURL/patch-release"
        const val altEapRelease: String = "$siteURL$altProductPrefixURL/eap-release"

        const val jbLegacyRelease: String = "https://plugins.jetbrains.com/plugins/LEGACY/7896"
        const val jbLegacyEapRelease: String = "https://plugins.jetbrains.com/plugins/LEGACY-EAP/7896"
        const val jbEapRelease: String = "https://plugins.jetbrains.com/plugins/EAP/7896"

        const val PREVIEW_STYLESHEET_LAYOUT: String = "/com/vladsch/md/nav/layout.css"
        const val PREVIEW_STYLESHEET_LIGHT: String = "/com/vladsch/md/nav/default.css"
        const val PREVIEW_STYLESHEET_DARK: String = "/com/vladsch/md/nav/darcula.css"

        const val PREVIEW_FX_STYLESHEET_LAYOUT: String = "/com/vladsch/md/nav/layout-fx.css"
        const val PREVIEW_FX_STYLESHEET_LIGHT: String = "/com/vladsch/md/nav/default-fx.css"
        const val PREVIEW_FX_STYLESHEET_DARK: String = "/com/vladsch/md/nav/darcula-fx.css"

        const val PREVIEW_FX_JS: String = "/com/vladsch/md/nav/markdown-navigator.js"

        const val PREVIEW_FX_HLJS_STYLESHEET_LIGHT: String = "/com/vladsch/md/nav/hljs-default.css"
        const val PREVIEW_FX_HLJS_STYLESHEET_DARK: String = "/com/vladsch/md/nav/hljs-darcula.css"

        const val PREVIEW_FX_PRISM_JS_STYLESHEET_LIGHT: String = "/com/vladsch/md/nav/prism-default.css"
        const val PREVIEW_FX_PRISM_JS_STYLESHEET_DARK: String = "/com/vladsch/md/nav/prism-darcula.css"

        const val PREVIEW_FX_HIGHLIGHT_JS: String = "/com/vladsch/md/nav/highlight.pack.js"

        const val PREVIEW_FX_PRISM_JS: String = "/com/vladsch/md/nav/prism.pack.js"

        const val PREVIEW_TASKITEMS_FONT: String = "/com/vladsch/md/nav/taskitems.ttf"
        const val PREVIEW_NOTOMONO_REGULAR_FONT: String = "/com/vladsch/md/nav/noto/NotoMono-Regular.ttf"
        const val PREVIEW_NOTOSANS_BOLD_FONT: String = "/com/vladsch/md/nav/noto/NotoSans-Bold.ttf"
        const val PREVIEW_NOTOSANS_BOLDITALIC_FONT: String = "/com/vladsch/md/nav/noto/NotoSans-BoldItalic.ttf"
        const val PREVIEW_NOTOSANS_ITALIC_FONT: String = "/com/vladsch/md/nav/noto/NotoSans-Italic.ttf"
        const val PREVIEW_NOTOSANS_REGULAR_FONT: String = "/com/vladsch/md/nav/noto/NotoSans-Regular.ttf"
        const val PREVIEW_NOTOSERIF_BOLD_FONT: String = "/com/vladsch/md/nav/noto/NotoSerif-Bold.ttf"
        const val PREVIEW_NOTOSERIF_BOLDITALIC_FONT: String = "/com/vladsch/md/nav/noto/NotoSerif-BoldItalic.ttf"
        const val PREVIEW_NOTOSERIF_ITALIC_FONT: String = "/com/vladsch/md/nav/noto/NotoSerif-Italic.ttf"
        const val PREVIEW_NOTOSERIF_REGULAR_FONT: String = "/com/vladsch/md/nav/noto/NotoSerif-Regular.ttf"

        const val PREVIEW_GITHUB_COLLAPSE_MARKDOWN_JS: String = "/com/vladsch/md/nav/github-collapse-markdown.js"
        const val PREVIEW_GITHUB_COLLAPSE_IN_COMMENT_JS: String = "/com/vladsch/md/nav/github-collapse-in-comment.user.original.js"
        const val PREVIEW_GITHUB_COLLAPSE_LIGHT: String = "/com/vladsch/md/nav/github-collapse.css"
        const val PREVIEW_GITHUB_COLLAPSE_DARK: String = "/com/vladsch/md/nav/github-collapse.css"

        const val TASK_ITEM: String = "/icons/svg/application-undone-task-list-item.svg"
        const val TASK_ITEM_DARK: String = "/icons/svg/application-undone-task-list-item_dark.svg"
        const val TASK_ITEM_DONE: String = "/icons/svg/application-done-task-list-item.svg"
        const val TASK_ITEM_DONE_DARK: String = "/icons/svg/application-done-task-list-item_dark.svg"

        const val productId: String = "idea-multimarkdown"
        const val productDisplayName: String = "Markdown Navigator"

        @JvmStatic
        val pluginDescriptor: IdeaPluginDescriptor by lazy {
            val plugins = PluginManagerCore.getPlugins()
            var descriptor: IdeaPluginDescriptor? = null

            for (plugin in plugins) {
                if (PLUGIN_ID == plugin.pluginId.idString) {
                    descriptor = plugin
                }
            }

            descriptor ?: throw IllegalStateException("Unexpected, plugin cannot find its own plugin descriptor")
            //            val pluginId = PluginId.findId(PLUGIN_ID)
            //            val pluginDescriptor = PluginManagerCore.getPlugin(pluginId)
            //            pluginDescriptor
        }

        @JvmStatic
        fun getPluginDescriptor(pluginId: String): IdeaPluginDescriptor? {
            val plugins = PluginManagerCore.getPlugins()
            for (plugin in plugins) {
                if (pluginId == plugin.pluginId.idString) {
                    return plugin
                }
            }
            return null
        }

        @JvmStatic
        val productVersion: String by lazy {
            val pluginDescriptor = pluginDescriptor
            val version = pluginDescriptor.version
            // truncate version to 3 digits and if had more than 3 append .x, that way
            // no separate product versions need to be created
            val parts = version.split(delimiters = *charArrayOf('.'), limit = 4)
            if (parts.size <= 3) {
                version
            } else {
                val newVersion = parts.subList(0, 3).reduce { total, next -> "$total.$next" }
                "$newVersion.x"
            }
        }

        @JvmStatic
        val fullProductVersion: String by lazy {
            val pluginDescriptor = pluginDescriptor
            pluginDescriptor.version
        }

        @JvmStatic
        fun getPluginCustomPath(): String? {
            val variants = arrayOf(PathManager.getHomePath(), PathManager.getPluginsPath())

            for (variant in variants) {
                val path = "$variant/$productId"
                if (LocalFileSystem.getInstance().findFileByPath(path) != null) {
                    return path
                }
            }
            return null
        }

        @JvmStatic
        fun getPluginPath(): String? {
            val variants = arrayOf(PathManager.getPluginsPath())

            for (variant in variants) {
                val path = "$variant/$productId"
                if (LocalFileSystem.getInstance().findFileByPath(path) != null) {
                    return path
                }
            }
            return null
        }

        @JvmStatic
        val instance: MdPlugin
            get() = ApplicationManager.getApplication().getComponent(MdPlugin::class.java) ?: throw IllegalStateException()

        @JvmStatic
        val testExtensions: TestExtensionProvider by lazy { TestExtensionProvider() }
    }
}
