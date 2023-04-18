// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.vcs

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.LinkRef
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.plugin.util.nullIf
import com.vladsch.plugin.util.prefixWith
import com.vladsch.plugin.util.suffixWith

import git4idea.GitLocalBranch
import git4idea.GitRemoteBranch
import git4idea.repo.GitRepository
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

/**

 */
class GitHubVcsRoot private constructor(gitHubBaseUrl: String?, basePath: String, val gitRepository: GitRepository?) {

    val baseUrl: String?
    val basePath: String
    val mainRepoBaseDir: String
    val isWiki: Boolean
    var localBranch: GitLocalBranch? = null
    var remoteBranch: GitRemoteBranch? = null

    init {
        // strip out username if the url contains @ from URL
        // regex: ^(https?://)(?:[^@]*\Q@\E)(.*)$
        // replace with $1$2
        var _gitHubBaseUrl = gitHubBaseUrl
        if (_gitHubBaseUrl != null) {
            val atPos = _gitHubBaseUrl.indexOf('@', 7)
            if ((_gitHubBaseUrl.startsWith("http://") || _gitHubBaseUrl.startsWith("https://")) && atPos > 0) {
                val prefixPos = if (_gitHubBaseUrl.startsWith("http://")) "http://".length else "https://".length
                _gitHubBaseUrl = _gitHubBaseUrl.substring(0, prefixPos) + _gitHubBaseUrl.substring(atPos + 1)
            }
        }

        this.baseUrl = _gitHubBaseUrl.suffixWith('/').nullIf("")
        this.basePath = basePath.suffixWith('/')
        this.isWiki = FileRef(this.basePath + "Home.md").isUnderWikiDir
        this.mainRepoBaseDir = if (this.isWiki) PathInfo(this.basePath).path else this.basePath

        if (gitRepository != null) {
            localBranch = gitRepository.currentBranch
            remoteBranch = localBranch?.findTrackedBranch(gitRepository)
        }
    }

    fun getRelativePath(path: String?): String? {
        if (path != null && path.startsWith(basePath)) {
            return path.substring(basePath.length)
        }
        return null
    }

    fun getRemoteBranchName(): String? {
        val remote = remoteBranch
        return remote?.nameForRemoteOperations
    }

    fun getIsRemoteBranchInSync(): Boolean {
//        val local = localBranch
//        val remote = remoteBranch
        return false
    }

    fun onRepositoryChange(gitRepository: GitRepository): Boolean {
        if (this.gitRepository === gitRepository) {
            // FIX: read in the current remote branch for origin
            localBranch = gitRepository.currentBranch
            remoteBranch = localBranch?.findTrackedBranch(gitRepository)
            return true
        }
        return false
    }

    fun urlForVcsRemote(relativeFilePath: String, branchOrTag: String?, gitHubLink: String?): String? {
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

    fun rootRelativeForVcsRemote(fileRef: FileRef, withExtension: Boolean, anchor: String?, branchOrTag: String?, gitHubLink: String?): String? {
        var relativePath: String? = if (!fileRef.isUnderWikiDir || withExtension) getRelativePath(fileRef.filePath) else fileRef.fileNameNoExt
        if (isWiki && relativePath != null && relativePath == "Home") relativePath = ""
        return if (relativePath == null) null else rootRelativeForVcsRemote(relativePath, anchor, branchOrTag, gitHubLink)
    }

    fun urlForVcsRemote(relativeFilePath: String, anchor: String?, branchOrTag: String?, gitHubLink: String?): String? {
        if (baseUrl == null) return null
        assert(baseUrl.isNotEmpty())

        return baseUrl + rootRelativeForVcsRemoteRaw(relativeFilePath, anchor, (gitHubLink.nullIf("")
            ?: "blob").suffixWith('/') + (branchOrTag.nullIf("") ?: "master").suffixWith('/'))
    }

    @Suppress("UNUSED_PARAMETER")
    fun rootRelativeForVcsRemote(relativeFilePath: String, anchor: String?, branchOrTag: String?, gitHubLink: String?): String {
        val relPath = rootRelativeForVcsRemoteRaw(relativeFilePath, anchor, "")
        return if (relPath.startsWith("wiki/") || relPath == "wiki") return "../../$relPath" else relPath
    }

    @Suppress("NAME_SHADOWING")
    fun rootRelativeForVcsRemoteRaw(relativeFilePath: String, anchor: String?, gitHubLinkAndBranchOrTag: String?): String {
        var relativeFilePath = relativeFilePath
        val gitHubLinkAndBranchOrTag = gitHubLinkAndBranchOrTag ?: "blob/master/"
        if (isWiki && relativeFilePath.startsWith("../../wiki")) {
            relativeFilePath = relativeFilePath.removePrefix("../../wiki")
        }

        return (if (isWiki) "wiki/" else gitHubLinkAndBranchOrTag.suffixWith('/')) + LinkRef.urlEncode(relativeFilePath.removePrefix("./"), null) + anchor.prefixWith('#', false)
    }

    fun fileProjectBaseDirectory(fileRef: FileRef): String {
        if (isWiki && fileRef.isUnderWikiDir) {
            if (fileRef.isWikiHomePage) {
                // wiki home, its base directory is the basePath
                return mainRepoBaseDir
            }
            return basePath
        }
        return basePath
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.github.vcsroot")
        private val INI_CATEGORY = Pattern.compile("\\[\\s*(\\w+)[\\s'\"]+(\\w+)[\\s'\"]+\\]")
        private val URL_VALUE = Pattern.compile("\\s*url\\s*=\\s*([^\\s]*)")
        private const val GIT_CONFIG = "config"

        @JvmStatic
        fun create(gitHubBaseUrl: String, basePath: String): GitHubVcsRoot {
            return GitHubVcsRoot(gitHubBaseUrl, basePath, null)
        }

        @JvmStatic
        fun create(gitRepository: GitRepository): GitHubVcsRoot? {
            val root = gitRepository.root
            val gitDirPath = root.path
            val gitPath = getGitPath(gitDirPath)
            LOG.debug("GitHubVcsRoot($gitDirPath), gitPath: $gitPath")
            if (gitPath != null) {
                val gitConfigFile = File(gitPath, GIT_CONFIG)
                if (gitConfigFile.exists() && gitConfigFile.isFile) {
                    // it is a sub-module need to distinguish it from main repo
                    val baseUrl = getBaseUrl(gitConfigFile)
                    // this repo does not have a remote url
                    val gitVcsRoot = GitHubVcsRoot(baseUrl, gitDirPath, gitRepository)
                    LOG.debug("GitHubVcsRoot($gitDirPath) = ${gitVcsRoot.basePath}")
                    return gitVcsRoot
                }
            }
            return null
        }

        private fun getGitPath(filePath: String): String? {
            val gitFilePathInfo = if (filePath.endsWith("/.git")) PathInfo(filePath) else PathInfo(filePath.suffixWith('/') + ".git")
            val gitFile = File(gitFilePathInfo.filePath)
            var gitPath: String? = null

            if (gitFile.exists()) {
                if (gitFile.isFile) {
                    var reader: BufferedReader? = null
                    try {
                        reader = BufferedReader(FileReader(gitFile))
                        var line: String?
                        while (true) {
                            line = reader.readLine()
                            if (line == null) break

                            // gitdir: ../.git/modules/laravel-translation-manager.isWiki
                            if (line.startsWith("gitdir:")) {
                                line = line.substring("gitdir:".length).trim { it <= ' ' }
                                val lineInfo = PathInfo(line)
                                val filePathInfo = PathInfo(filePath)
                                val configInfo = if (lineInfo.isRelative) filePathInfo.append(line) else lineInfo
                                gitPath = configInfo.filePath
                                break
                            }
                        }
                    } catch (ignored: IOException) {
                        LOG.debug("Could not read $gitFile", ignored)
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close()
                            } catch (ignored: IOException) {
                            }
                        }
                    }
                } else {
                    return gitFilePathInfo.filePath
                }
            }
            return gitPath
        }

        private fun getBaseUrl(gitConfigFile: File): String? {
            var baseUrl: String? = null

            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(FileReader(gitConfigFile))
                var line: String?
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
                        baseUrl = "https://" + matcher.group(1).replace("git://|git@|https://".toRegex(), "").replace(":".toRegex(), "/").removeSuffix(".git")

                        if (baseUrl.endsWith(PathInfo.WIKI_HOME_DIR_EXTENSION)) {
                            val baseUrlInfo = FileRef(baseUrl)
                            baseUrl = baseUrlInfo.filePathNoExt
                        }
                        break
                    }
                }
            } catch (ignored: IOException) {
                LOG.debug("No remote origin in $gitConfigFile", ignored)
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
                        // it is a submodule need to distinguish it from main repo
                        val baseUrl = getBaseUrl(gitConfigFile)
                        // this repo does not have a remote url
                        return GitHubVcsRoot(baseUrl, pathInfo.filePath, null)
                    }
                }

                nextPath = pathInfo.path
            } while (nextPath.isNotEmpty() && nextPath != "/" && !nextPath.equals(basePath, ignoreCase = true))

            return null
        }
    }
}
