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

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.md.nav.util.ResourceResolver
import com.vladsch.plugin.util.debug
import com.vladsch.plugin.util.suffixWith
import org.apache.commons.io.IOUtils
import java.io.*
import java.util.*

class MdResourceResolverImpl : Disposable, ResourceResolver {
    companion object {
        val LOG = com.intellij.openapi.diagnostic.Logger.getInstance("com.vladsch.md.nav.ResourceResolver")

        val resourcesToCopy = arrayOf(
            MdPlugin.PREVIEW_STYLESHEET_LAYOUT,
            MdPlugin.PREVIEW_STYLESHEET_LIGHT,
            MdPlugin.PREVIEW_STYLESHEET_DARK,
            MdPlugin.PREVIEW_FX_STYLESHEET_LAYOUT,
            MdPlugin.PREVIEW_FX_STYLESHEET_LIGHT,
            MdPlugin.PREVIEW_FX_STYLESHEET_DARK,
            MdPlugin.PREVIEW_FX_HLJS_STYLESHEET_LIGHT,
            MdPlugin.PREVIEW_FX_HLJS_STYLESHEET_DARK,
            MdPlugin.PREVIEW_FX_HIGHLIGHT_JS,
            MdPlugin.PREVIEW_GITHUB_COLLAPSE_MARKDOWN_JS,
            MdPlugin.PREVIEW_GITHUB_COLLAPSE_IN_COMMENT_JS,
            MdPlugin.PREVIEW_GITHUB_COLLAPSE_LIGHT,
            MdPlugin.PREVIEW_GITHUB_COLLAPSE_DARK,
            MdPlugin.TASK_ITEM,
            MdPlugin.TASK_ITEM_DARK,
            MdPlugin.TASK_ITEM_DONE,
            MdPlugin.TASK_ITEM_DONE_DARK
        )

        val fontsToCopy = arrayOf(
            MdPlugin.PREVIEW_TASKITEMS_FONT,
            MdPlugin.PREVIEW_NOTOMONO_REGULAR_FONT,
            MdPlugin.PREVIEW_NOTOSANS_BOLD_FONT,
            MdPlugin.PREVIEW_NOTOSANS_BOLDITALIC_FONT,
            MdPlugin.PREVIEW_NOTOSANS_ITALIC_FONT,
            MdPlugin.PREVIEW_NOTOSANS_REGULAR_FONT,
            MdPlugin.PREVIEW_NOTOSERIF_BOLD_FONT,
            MdPlugin.PREVIEW_NOTOSERIF_BOLDITALIC_FONT,
            MdPlugin.PREVIEW_NOTOSERIF_ITALIC_FONT,
            MdPlugin.PREVIEW_NOTOSERIF_REGULAR_FONT
        )

        var testInstance: MdResourceResolverImpl? = null

        @JvmStatic
        val instance: MdResourceResolverImpl
            get() {
                if (ApplicationManager.getApplication() == null || ApplicationManager.getApplication().isUnitTestMode) {
                    if (testInstance == null) {
                        testInstance = MdResourceResolverImpl()
                    }
                    return testInstance!!
                } else {
                    return ApplicationManager.getApplication().getService(MdResourceResolverImpl::class.java)
                        ?: throw IllegalStateException()
                }
            }
    }

    val resourceTempCopiesURLs = HashMap<String, String>()
    val customFonts = HashMap<String, String>()
    var tempDirPath: String? = null
        private set

    init {
        initComponent()
    }

    private fun initComponent() {
        val tempDir = File(File(System.getProperty("user.home")), ".markdownNavigator")
        tempDirPath = tempDir.absolutePath.suffixWith(File.separatorChar)

        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }

        for (fontPath in fontsToCopy) {
            val fileCustomFont = createTempCopyOfResourceFile(fontPath)
            val fontExternalUrl = correctUrl(fileCustomFont.toURI().toURL().toExternalForm())

            val customFontPathInfo = PathInfo(fontPath)

            customFonts.put(customFontPathInfo.fileName, fontExternalUrl)
            resourceTempCopiesURLs.put(fontPath, fontExternalUrl)
        }

        for (resourcePath in resourcesToCopy) {
            val resourcePathInfo = PathInfo(resourcePath)
            if (resourcePathInfo.ext == "css") {
                resourceTempCopiesURLs.put(resourcePath, createTempCssWithCustomFontExtURL(resourcePath, resourcePathInfo.fileName))
            } else {
                resourceTempCopiesURLs.put(resourcePath, createTempCopyOfResourceExtURL(resourcePath))
            }
        }
    }

    override fun dispose() {
    }

    override fun resourceFileURL(resourcePath: String, resourceClass: Class<*>?): String {
        if (resourcePath.isEmpty()) return resourcePath // dummy path for serialization constructor

        var resourceUrl = resourceTempCopiesURLs[resourcePath]
        if (resourceUrl == null) {
            // not yet loaded
            val resourcePathInfo = PathInfo(resourcePath)
            if (resourcePathInfo.ext == "css") {
                resourceUrl = createTempCssWithCustomFontExtURL(resourcePath, resourcePathInfo.fileName, resourceClass)
                resourceTempCopiesURLs.put(resourcePath, resourceUrl)
            } else {
                resourceUrl = createTempCopyOfResourceExtURL(resourcePath, resourceClass)
                resourceTempCopiesURLs.put(resourcePath, resourceUrl)
            }
        }
        return resourceUrl
    }

    @Throws(IOException::class)
    private fun createCustomFontUrl(): String {
        // create the freaking thing
        return createTempCopyOfResourceExtURL(MdPlugin.PREVIEW_TASKITEMS_FONT)
    }

    @Throws(IOException::class)
    private fun createTempCopyOfResourceExtURL(resourcePath: String, resourceClass: Class<*>? = null): String {
        return correctUrl(createTempCopyOfResourceFile(resourcePath, resourceClass).toURI().toURL().toExternalForm())
    }

    fun correctUrl(url: String): String {
        val changeToUnixURL = tempDirPath?.startsWith('/') ?: true
        if (changeToUnixURL && url.startsWith("file:") && !url.startsWith("file:///") && !url.startsWith("file://")) {
            return "file://" + url.substring("file:".length)
        }
        return url
    }

    @Throws(IOException::class, MissingResourceException::class)
    private fun createTempCopyOfResourceFile(resourcePath: String, resourceClass: Class<*>? = null): File {
        val tempFile =
            if (tempDirPath != null) File(tempDirPath + "multimarkdown_" + PathInfo(resourcePath).fileName)
            else File.createTempFile("multimarkdown_", PathInfo(resourcePath).fileName)

        if (tempDirPath == null) tempFile.deleteOnExit()

        //tempFile.deleteOnExit();
        LOG.debug { "creating temp file: " + tempFile.absolutePath + " of resource " + resourcePath }

        val resourceStream = (resourceClass ?: javaClass).getResourceAsStream(resourcePath)
            ?: throw MissingResourceException("Resource $resourcePath not found", (resourceClass ?: javaClass).name, resourcePath)
        val fileOutputStream = FileOutputStream(tempFile)
        val buffer = ByteArray(32768)
        var length: Int
        while (true) {
            length = resourceStream.read(buffer)
            if (length <= 0) break
            fileOutputStream.write(buffer, 0, length)
        }
        fileOutputStream.close()
        resourceStream.close()

        return tempFile
    }

    @Throws(IOException::class)
    fun getResourceFileContent(resourcePath: String, resourceClass: Class<*>? = null): String {
        val inputStream = (resourceClass ?: javaClass).getResourceAsStream(resourcePath)
        val writer = StringWriter()
        IOUtils.copy(inputStream, writer, "UTF-8")
        inputStream.close()
        return writer.toString()
    }

    @Suppress("UNUSED_PARAMETER")
    @Throws(IOException::class)
    private fun createTempCssWithCustomFontExtURL(resourcePath: String, suffix: String, resourceClass: Class<*>? = null): String {
        return correctUrl(createTempCssWithCustomFont(getResourceFileContent(resourcePath, resourceClass), PathInfo(resourcePath).fileName).toURI().toURL().toExternalForm())
    }

    @Throws(IOException::class)
    private fun createTempCssWithCustomFont(cssText: String, suffix: String): File {
        val cssTempFile: File
        if ("custom-fx.css" == suffix || tempDirPath == null) {
            cssTempFile = File.createTempFile("multimarkdown_", suffix)
            cssTempFile.deleteOnExit()
        } else {
            cssTempFile = File(tempDirPath + "multimarkdown_" + suffix)
        }
        val cssWithCustomFontText = cssWithCustomFont(cssText)
        updateTempCssWithCustomFontCopy(cssTempFile, cssWithCustomFontText)
        return cssTempFile
    }

    @Throws(IOException::class)
    private fun updateTempCssWithCustomFontCopy(cssTempFile: File, cssText: String) {
        val cssFileWriter = FileWriter(cssTempFile)
        cssFileWriter.write(cssWithCustomFont(cssText))
        cssFileWriter.close()
    }

    private fun cssWithCustomFont(cssWithFontRef: String): String {
        var result = cssWithFontRef
        for ((fontName, fontUrl) in customFonts) {
            result = result.replace("'$fontName'", "'$fontUrl'")
        }
        return result
    }
}
