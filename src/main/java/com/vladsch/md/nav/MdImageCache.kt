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
import com.vladsch.plugin.util.plus
import com.vladsch.plugin.util.suffixWith
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

class MdImageCache : Disposable {
    companion object {
        val LOG = com.intellij.openapi.diagnostic.Logger.getInstance("com.vladsch.md.nav.ImageCache")

        var testInstance: MdImageCache? = null

        @JvmStatic
        val instance: MdImageCache
            get() {
                if (ApplicationManager.getApplication() == null || ApplicationManager.getApplication().isUnitTestMode) {
                    if (testInstance == null) {
                        testInstance = MdImageCache()
                    }
                    return testInstance!!
                } else {
                    return ApplicationManager.getApplication().getService(MdImageCache::class.java)
                        ?: throw IllegalStateException()
                }
            }
    }

    val imageMD5PathMap = ConcurrentHashMap<String, String>()
    val cachedImagesMap = ConcurrentHashMap<String, String>()
    var tempDirPath: String = ""
        private set

    val fileWriterLock: Any = Object();

    init {
        initComponent()
    }

    fun isCachedFile(path: String): Boolean {
        return cachedImagesMap.containsKey(path)
    }

    fun fileCount(): Int = imageMD5PathMap.size

    fun fileSize(): Long {
        var size = 0L
        for (file in imageMD5PathMap.values) {
            val tempFile = File(file)
            if (tempFile.exists() && tempFile.isFile) {
                size += tempFile.totalSpace
            }
        }
        return size
    }

    fun clearCache() {
        val values = ArrayList(imageMD5PathMap.values)
        imageMD5PathMap.clear()
        cachedImagesMap.clear()

        for (file in values) {
            val tempFile = File(file)
            if (tempFile.exists() && tempFile.isFile) {
                tempFile.delete()
            }
        }
    }

    private fun initComponent() {
        val tempDir = File(System.getProperty("user.home")).plus(".markdownNavigator").plus("image-cache")
        tempDirPath = tempDir.absolutePath.suffixWith('/')

        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
    }

    override fun dispose() {
    }

    fun getImageFile(md5: String, extension: String, fileWriter: Consumer<File>): File {
        var file: File
        val path = imageMD5PathMap[md5 + extension]
        if (path != null) {
            file = File(path)
            if (file.isFile && file.exists()) return file
        }

        // does not exit, create it
        // FIX: try to use the file name as is, if not conflict then save under the original file name else use temp file with prefix prefix
        file = File.createTempFile("image_", extension, File(tempDirPath))
        imageMD5PathMap[md5 + extension] = file.path
        cachedImagesMap[file.path] = md5 + extension

        try {
            // Only one file writer at a time since a file writer can change user.dir setting while generating a file
            synchronized(fileWriterLock) {
                fileWriter.accept(file)
            }
        } catch (e: Throwable) {
            LOG.error(e)
        }
        return file
    }
}
