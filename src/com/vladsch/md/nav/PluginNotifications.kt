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
import com.intellij.notification.*
import com.intellij.openapi.project.Project
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.xml.util.XmlStringUtil
import com.vladsch.plugin.util.wrapWith

object PluginNotifications {

    val NOTIFICATION_GROUP_ISSUES: NotificationGroup = NotificationGroup("Markdown Navigator Alerts", NotificationDisplayType.STICKY_BALLOON, true, null)
    val NOTIFICATION_GROUP_UPDATE: NotificationGroup = NotificationGroup("Markdown Navigator Update", NotificationDisplayType.STICKY_BALLOON, true, null)
    val NOTIFICATION_GROUP_UPDATE_TEST: NotificationGroup = NotificationGroup("Markdown Navigator Update Test", NotificationDisplayType.BALLOON, true, null)
    val NOTIFICATION_GROUP_LICENSE: NotificationGroup = NotificationGroup("Markdown Navigator License", NotificationDisplayType.STICKY_BALLOON, true, null)
    val NOTIFICATION_GROUP_HTML_EXPORT: NotificationGroup = NotificationGroup("Markdown Navigator HTML Export", NotificationDisplayType.BALLOON, true, null)
    val NOTIFICATION_GROUP_INFORM: NotificationGroup = NotificationGroup("Markdown Navigator Action Information", NotificationDisplayType.BALLOON, true, null)
    val NOTIFICATION_GROUP_DEFAULT: NotificationGroup = NOTIFICATION_GROUP_ISSUES

    fun applyHtmlColors(htmlText: String): String {
        val isDarkUITheme = UIUtil.isUnderDarcula()
        val enhColor = if (isDarkUITheme) "#B0A8E6" else "#6106A5"
        val buyColor = if (isDarkUITheme) "#F0A8D4" else "#C02080"
        val specialsColor = if (isDarkUITheme) "#A4EBC5" else "#04964F"
        return htmlText.replace("[[ENHANCED]]", enhColor).replace("[[BUY]]", buyColor).replace("[[SPECIALS]]", specialsColor)
    }

    fun processDashStarList(featureList: String, titleHtml: String? = null, enhAttr: String = "ENHANCED"): String {
        val features = featureList.split('\n').fold("") { accum, elem ->
            val item = elem.trim()
            accum + (
                if (item.startsWith('*')) item.removePrefix("*").trim().wrapWith("<span style=\"color: [[$enhAttr]]\">", "</span>")
                else item.removePrefix("-").trim()
                ).wrapWith("<li>", "</li>")
        }.wrapWith((if (titleHtml != null && !titleHtml.isEmpty()) """<h4 style="margin: 0; font-size: ${JBUI.scale(10)}px">$titleHtml</h4>""" else "") + """
<ul style="margin-left: ${JBUI.scale(10)}px;">
""", "</ul>")
        return applyHtmlColors(features)
    }

    fun createNotification(message: String,
        title: String = MdBundle.message(MdPlugin.productId),
        notificationType: NotificationType = NotificationType.INFORMATION,
        issueNotificationGroup: NotificationGroup = NOTIFICATION_GROUP_DEFAULT,
        listener: NotificationListener? = null
    ): Notification {

        //        val listener = NotificationListener { notification, hyperlinkEvent ->
        //            //notification.expire();
        //            if (hyperlinkEvent.url == null) {
        //                val link = hyperlinkEvent.description
        //                when (link) {
        //                    ":DISABLE" -> {
        //                        settings.licenseSettings.showFeatureUpdates = false
        //                        notification.expire()
        //                    }
        //                    ":BUY" -> BrowserUtil.browse (href + "product/multimarkdown/buy")
        //                    ":TRY" -> BrowserUtil.browse (href + "product/multimarkdown/try")
        //                    ":SPECIALS" -> BrowserUtil.browse (href + "product/multimarkdown/specials")
        //                    ":FEATURES" -> BrowserUtil.browse (href + "product/multimarkdown")
        //                }
        //            } else {
        //                BrowserUtil.browse (hyperlinkEvent.url.toString())
        //            }
        //        }

        val basicListener = listener ?: NotificationListener { _, hyperlinkEvent ->
            //notification.expire();
            if (hyperlinkEvent.url != null) {
                BrowserUtil.browse(hyperlinkEvent.url.toString())
            }
        }

        return issueNotificationGroup.createNotification(title, XmlStringUtil.wrapInHtml(message), notificationType, basicListener)
    }

    fun makeNotification(message: String,
        title: String = MdBundle.message(MdPlugin.productId),
        notificationType: NotificationType = NotificationType.INFORMATION,
        issueNotificationGroup: NotificationGroup = NOTIFICATION_GROUP_DEFAULT,
        project: Project? = null,
        listener: NotificationListener? = null
    ) {
        createNotification(message, title, notificationType, issueNotificationGroup, listener).notify(project)
    }
}
