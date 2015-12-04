/*
 * Copyright 2013 Square, Inc.
 * No license information provided
 *
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is based on code from https://github.com/jawspeak/intellij-plugin-copy-and-open-github-url
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.util

import com.intellij.openapi.vfs.VirtualFile
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

/**

 */
class GitHubVcsRoot protected constructor(gitHubBaseUrl: String, basePath: String) {
    val baseUrl: String
    val basePath: String
    val mainRepoBaseDir: String
    val isWiki: Boolean

    init {
        // strip out username if the url contains @ from URL
        // regex: ^(https?://)(?:[^@]*\Q@\E)(.*)$
        // replace with $1$2
        var _gitHubBaseUrl = gitHubBaseUrl
        val atPos = _gitHubBaseUrl.indexOf('@', 7)
        if ((_gitHubBaseUrl.startsWith("http://") || _gitHubBaseUrl.startsWith("https://")) && atPos > 0) {
            val prefixPos = if (_gitHubBaseUrl.startsWith("http://")) "http://".length else "https://".length
            _gitHubBaseUrl = _gitHubBaseUrl.substring(0, prefixPos) + _gitHubBaseUrl.substring(atPos + 1)
            //logger.info("cleaned username from url " + gitHubBaseUrl + " -> " + _gitHubBaseUrl);
        }

        this.baseUrl = _gitHubBaseUrl.suffixWith('/')
        this.basePath = basePath.suffixWith('/')
        this.isWiki = FileRef(this.basePath + "Home.md").isUnderWikiDir
        this.mainRepoBaseDir = if (this.isWiki) PathInfo(this.basePath).path else this.basePath
    }

    fun getRelativePath(path: String?): String? {
        if (path != null && path.startsWith(basePath)) {
            return path.substring(basePath.length)
        }
        return null
    }

    fun urlForVcsRemote(relativeFilePath: String, branchOrTag: String?, gitHubLink: String?): String {
        return urlForVcsRemote(relativeFilePath, null, branchOrTag, gitHubLink)
    }

    fun urlForVcsRemote(virtualFile: VirtualFile, withExtension: Boolean, anchor: String?, branchOrTag: String?, gitHubLink: String?): String? {
        val pathInfo = PathInfo(virtualFile.path)
        val relativePath = getRelativePath(if (withExtension) pathInfo.filePath else pathInfo.filePathNoExt)
        return if (relativePath == null) null else urlForVcsRemote(relativePath, anchor, branchOrTag, gitHubLink)
    }

    fun urlForVcsRemote(fileRef: FileRef, withExtension: Boolean, anchor: String?, branchOrTag: String?, gitHubLink: String?): String? {
        var relativePath: String? = if (!fileRef.isUnderWikiDir || withExtension) getRelativePath(fileRef.filePath) else fileRef.fileNameNoExt
        if (isWiki && relativePath != null && relativePath == "Home") relativePath = ""
        return if (relativePath == null) null else urlForVcsRemote(relativePath, anchor, branchOrTag, gitHubLink)
    }

    fun urlForVcsRemote(relativeFilePath: String, anchor: String?, branchOrTag: String?, gitHubLink: String?): String {
        var relativeFilePath = relativeFilePath
        var branchOrTag = branchOrTag
        var gitHubLink = gitHubLink
        if (isWiki && relativeFilePath.startsWith("../../wiki")) {
            relativeFilePath = relativeFilePath.removeStart("../../wiki")
        }

        if (branchOrTag == null || branchOrTag.isEmpty()) branchOrTag = "master"
        if (gitHubLink == null || gitHubLink.isEmpty()) gitHubLink = "blob"

        return baseUrl + (if (isWiki) "wiki/" else "$gitHubLink/$branchOrTag/") + LinkRef.urlEncode(relativeFilePath.removeStart("./")) + anchor.prefixWith('#', false)
    }

    companion object {
        private val logger = org.apache.log4j.Logger.getLogger(GitHubVcsRoot::class.java)
        private val INI_CATEGORY = Pattern.compile("\\[\\s*(\\w+)[\\s'\"]+(\\w+)[\\s'\"]+\\]")
        private val URL_VALUE = Pattern.compile("\\s*url\\s*=\\s*([^\\s]*)\\.git")
        private val GIT_CONFIG = "config"

        fun create(gitHubBaseUrl: String, basePath: String): GitHubVcsRoot {
            return GitHubVcsRoot(gitHubBaseUrl, basePath)
        }

        private fun getGitPath(filePath: String): String? {
            val filePathInfo = PathInfo(filePath).append(".git")
            val gitFile = File(filePathInfo.filePath)
            var gitPath: String? = null

            if (gitFile.exists()) {
                if (gitFile.isFile) {
                    var reader: BufferedReader? = null
                    try {
                        reader = BufferedReader(FileReader(gitFile))
                        var line: String
                        while (true) {
                            line = reader.readLine()
                            if (line == null) break

                            // gitdir: ../.git/modules/laravel-translation-manager.isWiki
                            if (line.startsWith("gitdir:")) {
                                line = line.substring("gitdir:".length).trim { it <= ' ' }
                                val lineInfo = PathInfo(line)
                                val configInfo = if (lineInfo.isRelative) filePathInfo.append(line) else lineInfo
                                gitPath = configInfo.filePath
                                break
                            }
                        }
                    } catch (ignored: IOException) {
                        logger.info("Could not read " + gitFile, ignored)
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close()
                            } catch (ignored: IOException) {
                            }

                        }
                    }
                } else {
                    return filePathInfo.filePath
                }
            }
            return gitPath
        }

        private fun getBaseUrl(gitConfigFile: File): String? {
            var baseUrl: String? = null

            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(FileReader(gitConfigFile))
                var line: String
                var inRemoteOriginSection = false

                while (true) {
                    line = reader.readLine()
                    if (line == null) break

                    if (line.matches("\\s*#".toRegex())) continue
                    var matcher = INI_CATEGORY.matcher(line)
                    if (matcher.matches()) {
                        inRemoteOriginSection = "remote" == matcher.group(1) && "origin" == matcher.group(2)
                        continue
                    }
                    matcher = URL_VALUE.matcher(line)
                    if (inRemoteOriginSection && matcher.matches()) {
                        baseUrl = "https://" + matcher.group(1).replace("git://|git@|https://".toRegex(), "").replace(":".toRegex(), "/")

                        if (baseUrl.endsWith(PathInfo.WIKI_HOME_DIR_EXTENSION)) {
                            val baseUrlInfo = FileRef(baseUrl)
                            baseUrl = baseUrlInfo.filePathNoExt
                        }
                        break
                    }
                }
            } catch (ignored: IOException) {
                logger.info("No remote origin in " + gitConfigFile, ignored)
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (ignored: IOException) {
                    }

                }
            }
            return baseUrl
        }

        fun getGitHubVcsRoot(path: String?, basePath: String?): GitHubVcsRoot? {
            if (path == null || basePath == null) return null

            var nextPath: String = path
            do {
                val pathInfo = PathInfo(nextPath)

                val gitPath = getGitPath(pathInfo.filePath)
                if (gitPath != null) {
                    val gitConfigFile = File(gitPath, GIT_CONFIG)
                    if (gitConfigFile.exists() && gitConfigFile.isFile) {
                        val baseUrl = getBaseUrl(gitConfigFile)
                        if (baseUrl != null) {
                            return GitHubVcsRoot(baseUrl, pathInfo.filePath)
                        }

                        // this sub-module does not have a remote.
                        return null
                    }
                }

                nextPath = pathInfo.path
            } while (!nextPath.isEmpty() && nextPath != "/" && !nextPath.equals(basePath, ignoreCase = true))

            return null
        }
    }
}
