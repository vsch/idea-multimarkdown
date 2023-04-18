// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.vcs

import com.intellij.openapi.roots.ProjectRootManager
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.util.*
import com.vladsch.plugin.util.*

import java.util.*
import kotlin.text.removePrefix
import kotlin.text.removeSuffix
import kotlin.text.startsWith

open class GitHubLinkMatcher(val projectResolver: MdLinkResolver.ProjectResolver, val originalLinkRef: LinkRef, val exclusionMap: Map<String, String>?) {
    companion object {
        private val LOG = com.intellij.openapi.diagnostic.Logger.getInstance("com.vladsch.md.nav.github.matcher")
    }

    val linkRef: LinkRef =
        originalLinkRef.replaceFilePath(
            originalLinkRef.linkToFile(originalLinkRef.filePath),
            true,
            null
        )

    var fixedPrefix: String = ""
        private set

    private var fixedPrefixPattern: String = ""

    var fileName: String = ""
        private set

    var fileNameExtPattern: String = ""
        private set

    var looseFileNameExtPattern: String = ""
        private set

    var completionFileNameExtPattern: String = ""
        private set

    var linkAllMatch: String? = null
        private set

    // extension set for all file matches
    var linkAllMatchExtensions: List<String>? = null
        private set
    var linkLooseMatch: String? = null
        private set

    // extension set for loose file matches
    var linkLooseMatchExtensions: List<String>? = null
        private set
    var linkCompletionMatch: String? = null
        private set

    // extension set for loose file matches
    var linkCompletionMatchExtensions: List<String>? = null
        private set
    var linkSubAnchorExtMatch: String? = null
        private set
    var linkSubExtMatch: String? = null
        private set
    var linkFileAnchorMatch: String? = null
        private set
    var linkFileMatch: String? = null
        private set

    // if in the main repo then this will be set to the branch or tag from the link
    var branchOrTag: String? = null
        private set

    // set to true if it is a non-file link
    var gitHubLinks: Boolean = false
        private set

    // this will be one of the links including file links: raw, blob, wiki
    var gitHubLink: String? = null
        private set

    // this will be the above github link but with whatever followed the link in the linkRef
    var gitHubLinkWithParams: String? = null
        private set

    // this will be the whatever followed the link in the linkRef
    var gitHubLinkParams: String? = null
        private set

    // set if wiki rules are being matched, ie. match not case sensitive
    // applies only to exact match, loose and completion matches should always be not case sensitive
    var wikiMatchingRules: Boolean = false
        private set

    // effective file extension: either file ext or if the filename starts with . and has no ext then the file name
    var effectiveExt: String? = null
        private set

    // set only after checking isOnlyLooseMatchValid
    var isCompletionMatch: Boolean = false
        private set

    var isOnlyCompletionMatchValid: Boolean = true
        private set

    private fun matchExt(ext: String?): String {
        return if (ext != null && ext.isNotEmpty()) linkTextToFileMatch(ext.prefixWith('.')) else ""
    }

    protected fun extensionPattern(vararg extensions: String, isOptional: Boolean): String {
        val hashSet = HashSet<String>()

        hashSet.addAll(extensions.filter { it.isNotEmpty() }.map { matchExt(it) })
        hashSet.addAll(linkRef.linkExtensions.map { matchExt(it) })
        val extensionPattern = "(?:" + hashSet.reduce(skipEmptySplicer("|")) + ")" + if (isOptional) "?" else ""
        return extensionPattern
    }

    private fun completionExtensionPattern(extension: String): String {
        return "(?:\\Q${extension.prefixWith('.')}\\E[^/]*)"
    }

    private fun linkTextToFileMatch(pathText: String?, isOptional: Boolean = false, emptyMatchesAll: Boolean = false): String {
        if (pathText == null || pathText.isEmpty()) return if (emptyMatchesAll) "(?:.*)" else ""
        return linkRef.linkToFileRegex(pathText.orEmpty()) + if (isOptional) "?" else ""
    }

    private fun linkPathCompletionMatch(pathText: String): String {
        val mapped = pathText.removePrefix("/").split("/").filter { !it.isBlank() }.map { "(?:.*\\Q${linkRef.linkToFile(it)}\\E.*)" }
        if (mapped.isEmpty()) return ""

        val prefix = mapped.reduce { total, elem -> "$total/$elem" }
        return if (pathText.startsWith('/')) prefix.wrapWith("(?:", ".*/)") else prefix.wrapWith("(?:.*", ".*/)")
    }

    private fun quoteMatchPattern(pathText: String?, isOptional: Boolean = false, emptyMatchesAll: Boolean = false): String {
        if (pathText == null || pathText.isEmpty()) return if (emptyMatchesAll) "(?:.*)" else ""
        return "(?:\\Q" + pathText + "\\E)" + if (isOptional) "?" else ""
    }

    private fun quoteMatchPrefixPattern(pathText: String?, isOptional: Boolean = false, emptyMatchesAll: Boolean = false): String {
        if (pathText == null || pathText.isEmpty()) return if (emptyMatchesAll) "(?:.*)" else ""
        if (pathText[0] == '/') {
            // root, on windows needs (?:[A-Z]:)? prefix
            return "(?:[A-Z]:)?(?:\\Q" + pathText + "\\E)" + if (isOptional) "?" else ""
        } else {
            return "(?:\\Q" + pathText + "\\E)" + if (isOptional) "?" else ""
        }
    }

    fun patternRegex(looseMatch: Boolean): Regex? {
        computeMatchText(wasURI = false, wasRepoRel = false)
        if (isOnlyCompletionMatchValid && !looseMatch) return null
        return (if (looseMatch) linkLooseMatch else linkAllMatch)?.toRegex(RegexOption.IGNORE_CASE)
    }

    @Suppress("UNUSED_PARAMETER")
    fun computeMatchText(wasURI: Boolean, wasRepoRel: Boolean): Unit {
//         RELEASE : remove false
//        if (false && linkCompletionMatch != null) return
        if (linkCompletionMatch != null) return

        // return a regex that will match most loosely a file path to be used by this link
        val vcsRepo: GitHubVcsRoot?
        val vcsRepoBasePath = if (linkRef.isAbsolute && wasURI || linkRef.filePath.isEmpty()) {
            vcsRepo = null
            "/"
        } else {
            vcsRepo = projectResolver.getVcsRoot(linkRef.containingFile)
            PathInfo.cleanFullPath(with(vcsRepo?.mainRepoBaseDir) {
                if (this == null || this.isEmpty()) {
                    if (linkRef.containingFile.path.startsWith(projectResolver.projectBasePath.suffixWith("/"))) {
                        projectResolver.projectBasePath
                    } else {
                        // use the module path
                        var virtualFile = linkRef.containingFile.virtualFile
                        var modulePath = "/"
                        val project = projectResolver.project
                        if (virtualFile != null && project != null) {
                            val fileIndex = ProjectRootManager.getInstance(project).fileIndex
                            val module = fileIndex.getModuleForFile(virtualFile)
                            if (module != null) {
                                // if target ref in module under module root dir then it can be relative
                                val path = PathInfo(module.moduleFilePath).path
                                if (virtualFile.path.startsWith(path)) {
                                    modulePath = path
                                }
                                val scope = module.moduleScope
                                while (virtualFile != null && MdPsiImplUtil.inScope(scope, virtualFile)) {
                                    try {
                                        if (!virtualFile.isDirectory && virtualFile.path == "/" + virtualFile.name) {
                                            // resource file at root, causes illegal state exception
                                            break
                                        }
                                        modulePath = if (virtualFile.isDirectory) virtualFile.path else virtualFile.parent.path
                                        virtualFile = virtualFile.parent ?: break
                                    } catch (e: Exception) {
                                        LOG.error("GitHubLinkMatcher exception for virtualFile " + virtualFile.path, e)
                                        break
                                    }
                                }
                            }
                        }

                        modulePath
                        //                        "/Volumes/Promise Pegasus/Data/site-sample/src/site"
                    }
                } else this
                //            if (this == null || this.isEmpty()) "/" else this
            }).suffixWith('/')
        }

        assert(vcsRepoBasePath.isNotEmpty()) { "vcsRepoBasePath cannot be empty" }

        // empty path or file name is an extension: .something, so cannot link to . files
        val useLooseMatch = true
        val subDirPattern = "(?:.+/)?"
        var completionMatchOnly = false
        val originalLinkRef = linkRef

        if (linkRef is WikiLinkRef) {
            // spaces match - and spaces, all subdirectories under Wiki Home match, only WIKI targets accepted, no case sensitivity
            val linkRef = linkRef.replaceFilePath(linkRef.filePath.trim())

            fixedPrefix = linkRef.containingFile.wikiDir.suffixWith('/')
            fixedPrefixPattern = quoteMatchPrefixPattern(fixedPrefix)
            isCompletionMatch = (linkRef.filePath.isEmpty() || linkRef.fileNameNoExt.startsWith('.') && !linkRef.hasExt) && !linkRef.hasAnchor
            val filePath = if (isCompletionMatch) "" else linkRef.filePath
//            val filePathNoExt = if (isCompletionMatch) "" else linkRef.filePathNoExt
//            val fileName = if (isCompletionMatch) "" else linkRef.fileName
            val fileNameNoExt = if (isCompletionMatch) "" else linkRef.fileNameNoExt
            val ext = if (linkRef.fileName.startsWith('.') && !linkRef.hasExt) linkRef.fileName else linkRef.ext
            val linkExtensions = if (linkRef.fileName.startsWith('.') && !linkRef.hasExt) arrayOf() else linkRef.linkExtensions
            val emptyMatchesAll = isCompletionMatch
            effectiveExt = ext

            val pureAnchor = linkRef.filePath.isEmpty() && linkRef.anchor != null
            if (useLooseMatch) {
                val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                val looseFilenamePattern = linkTextToFileMatch(if (pureAnchor) linkRef.containingFile.fileNameNoExt else fileNameNoExt, isOptional = false, emptyMatchesAll = false)
                val completionFilenamePattern = linkTextToFileMatch("", isOptional = false, emptyMatchesAll = true)
                val looseExtensionPattern = (extensionPattern(ext, isOptional = true) + extensionPattern(PathInfo(linkRef.anchorText).ext, *linkExtensions, isOptional = true)).regexGroup()
                linkLooseMatch = "^$fixedPrefixPattern$subDirPattern$looseFilenamePattern$looseAnchorPattern$looseExtensionPattern$"
                looseFileNameExtPattern = "^$looseFilenamePattern$looseAnchorPattern$looseExtensionPattern$"
                linkCompletionMatch = "^$fixedPrefixPattern$subDirPattern$completionFilenamePattern$looseAnchorPattern$looseExtensionPattern$"
                completionFileNameExtPattern = "^$completionFilenamePattern$looseAnchorPattern$looseExtensionPattern$"
                linkLooseMatchExtensions = arrayOf(ext, PathInfo(linkRef.anchorText).ext, *linkExtensions).filter { it.isNotEmpty() }
                linkCompletionMatchExtensions = linkLooseMatchExtensions
            }

            completionMatchOnly = isCompletionMatch || !linkRef.containingFile.isWikiPage

            val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)
            // if it has extension then we include it as alternative before default extensions because it may not be an extension but part of the file name
            val defaultExtensions = extensionPattern(*linkExtensions, isOptional = false)
            val extensionPattern = if (ext.isNotEmpty()) (extensionPattern(ext, isOptional = false) + "|" + extensionPattern(ext, isOptional = false) + defaultExtensions).regexGroup() else defaultExtensions
            val filenamePattern = linkTextToFileMatch(filePath, isOptional = false, emptyMatchesAll = emptyMatchesAll)
            fileName = fileNameNoExt
            linkAllMatchExtensions = arrayOf(ext, *linkExtensions).filter { it.isNotEmpty() }

            linkFileMatch = "^$fixedPrefixPattern$filenamePattern$"
            if (anchorPattern.isNotEmpty()) linkFileAnchorMatch = "^$fixedPrefixPattern$filenamePattern$anchorPattern$"
            linkSubExtMatch = "^$fixedPrefixPattern$subDirPattern$filenamePattern$extensionPattern$"
            if (anchorPattern.isNotEmpty()) linkSubAnchorExtMatch = "^$fixedPrefixPattern$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
            fileNameExtPattern = "^$filenamePattern${anchorPattern.suffixWith('?')}$"
            linkAllMatch = "^$fixedPrefixPattern(?:$filenamePattern${anchorPattern.suffixWith('?')}|$subDirPattern$filenamePattern${anchorPattern.suffixWith('?')}$extensionPattern)$"
            wikiMatchingRules = true
        } else {
            // it is assumed that if there is a wiki directory then it is a sub dir of the vcsRepoBasePath with the same name and .wiki appended
            // so if we encounter vcsRepoBasePath/wiki will will change it to vcsRepoBasePath/projectBasePathName.wiki
            // going below the vcsRepoBasePath is not supported for now.

            // if the page is a wiki home page then it will be treated as if it is located in the vcsRepoBasePath so that its relative links resolve correctly
            // however, this must be done for image links but is optional for non-image explicit links which resolve if the page was under the wiki directory
            // if it is a wiki but not the main page or not image link then its prefix is not changed
            var repoLink: String = linkRef.filePath
            val projectDirName = PathInfo(vcsRepoBasePath).fileName
            val noWikiSubDirPattern = if (projectDirName.isEmpty()) "" else "(?!\\Q$projectDirName.wiki\\E)"
            var wikiPrefixIsOptional: Boolean? = null
            var linkRef = linkRef

            val fixedCompletionPrefix = vcsRepoBasePath
            val fixedCompletionPrefixPattern = quoteMatchPrefixPattern(fixedCompletionPrefix)

            val originalWantCompletionMatch = linkRef.anchor == null && (linkRef.filePath.isEmpty() || linkRef.fileNameNoExt.startsWith('.') && !linkRef.hasExt)

            if (originalWantCompletionMatch) {
                fixedPrefix = fixedCompletionPrefix
                fixedPrefixPattern = fixedCompletionPrefixPattern
            } else {
                var prefixPath: String

                if (linkRef.filePath.isEmpty() && linkRef.anchor != null) {
                    linkRef = linkRef.replaceFilePath(linkRef.containingFile.fileName)
                }

                // here we resolve the start of the relative path
                val containingFilePrefixPath =
                    if (linkRef.containingFile.isWikiPage) {
                        // for the home page image links require wiki/ prefix, for the rest it is optional
                        //  if (linkRef.containingFile.isWikiHomePage) {
                        //      if (linkRef is ImageLinkRef) {
                        //          wikiPrefixIsOptional = false
                        //          vcsRepoBasePath
                        //      } else if (linkRef.isImageExt) {
                        //          wikiPrefixIsOptional = false
                        //          if (wasURI || wasRepoRel) {
                        //              vcsRepoBasePath
                        //          } else {
                        //              vcsRepoBasePath + "wiki/"
                        //          }
                        //      } else {
                        //          wikiPrefixIsOptional = true
                        //          vcsRepoBasePath + "wiki/"
                        //      }
                        //  } else vcsRepoBasePath + "wiki/"
                        if (linkRef.containingFile.isWikiHomePage) {
                            wikiPrefixIsOptional = true
                        }

                        vcsRepoBasePath + "wiki/"
                    } else {
                        when {
                            vcsRepo == null -> {
                                val pathRelativeToRepo = if (linkRef.isRelative) linkRef.containingFile.path.substring(vcsRepoBasePath.length) else ""
                                vcsRepoBasePath + pathRelativeToRepo
                            }
                            linkRef.containingFile.path.length < vcsRepoBasePath.length -> {
                                LOG.debug("linkRef.containingFile ${linkRef.containingFile} not under repo $vcsRepoBasePath or project ${projectResolver.projectBasePath}")
                                vcsRepoBasePath + "blob/master/"
                            }
                            else -> {
                                val pathRelativeToRepo = if (linkRef.isRelative) linkRef.containingFile.path.substring(vcsRepoBasePath.length) else ""
                                vcsRepoBasePath + "blob/master/" + pathRelativeToRepo
                            }
                        }
                    }

                // now append the link's path to see where we wind up, but if the link is absolute starting with / and has a subdirectory part then it resolves from vcsRepoBase + blob/master
                prefixPath =
                    if (linkRef.filePath.startsWith("/"))
                        when {
                            vcsRepoBasePath == "/" -> vcsRepoBasePath + linkRef.path.removePrefix("/").suffixWith('/')
                            vcsRepo == null -> vcsRepoBasePath + linkRef.path.removePrefix("/").suffixWith('/')
                            else -> vcsRepoBasePath + "blob/master/" + linkRef.path.removePrefix("/").suffixWith('/')
                        }
                    else
                        PathInfo.appendParts(containingFilePrefixPath, linkRef.path).filePath.suffixWith('/')

                // NOTE: here vcsRepoBasePath and vcsRoot are of the main project, but the link can refer to a module vcs root in a sub-directory
                var destVcsRepo: GitHubVcsRoot? = vcsRepo
                var destVcsRepoBasePath: String = vcsRepoBasePath
                var destProjectDirName: String = projectDirName

                @Suppress("NAME_SHADOWING")
                var wikiPrefixIsOptional = wikiPrefixIsOptional

                if (projectResolver.project != null) {
                    //                    val projectComponent = MultiMarkdownPlugin.getProjectComponent(projectResolver.project!!)
                    //                    val moduleRoot = projectComponent.getGitHubRepo(prefixPath)
                    val moduleRoot = projectResolver.getGitHubRepo(prefixPath)
                    if (moduleRoot != null && moduleRoot != vcsRepo) {
                        destVcsRepo = moduleRoot
                        destVcsRepoBasePath = moduleRoot.basePath
                        destProjectDirName = PathInfo(destVcsRepoBasePath).fileName
                        val suffix = prefixPath.removePrefix(destVcsRepoBasePath)
                        wikiPrefixIsOptional = false // different repo
                        val pos = suffix.indexOf('/')
                        val firstPart = if (pos != -1) suffix.substring(0, pos) else suffix
                        prefixPath = if (GitHubLinkResolver.isGitHubLink(firstPart)) destVcsRepoBasePath + suffix.suffixWith('/') else destVcsRepoBasePath + "blob/master/" + suffix.suffixWith('/')
                    }
                }

                if (linkRef.fileName.isEmpty() && (prefixPath == destVcsRepoBasePath || prefixPath == destVcsRepoBasePath.removeSuffix("/"))) {
                    // main repo link
                    prefixPath = destVcsRepoBasePath
                    gitHubLink = ""
                    gitHubLinks = true
                    fixedPrefix = prefixPath
                    fixedPrefixPattern = quoteMatchPrefixPattern(fixedPrefix)
                } else {
                    if (destVcsRepoBasePath == "/") {
                        // see if this is a wiki dir
                        val fileRef = FileRef(linkRef.filePath)
                        wikiMatchingRules = fileRef.isUnderWikiDir
                        fixedPrefix = ""
                        fixedPrefixPattern = ""
                    } else {
                        prefixPath = if (!prefixPath.startsWith(destVcsRepoBasePath)) {
                            // cannot match it is below the vcs repo
                            ""
                        } else {
                            prefixPath.substring(destVcsRepoBasePath.length).removeSuffix("/")
                        }

                        if (prefixPath.isEmpty() && linkRef.fileName in GitHubLinkResolver.GITHUB_LINKS) {
                            // add these to the path
                            prefixPath += linkRef.fileName
                            linkRef = linkRef.replaceFilePath("")
                        }

                        if (destVcsRepo == null || prefixPath.isEmpty()) {
                            completionMatchOnly = destVcsRepo != null && prefixPath.isEmpty()
                            fixedPrefix = destVcsRepoBasePath.suffixWith('/') + prefixPath.suffixWith('/')
                            fixedPrefixPattern = quoteMatchPrefixPattern(fixedPrefix)
                            repoLink = linkRef.fileName
                        } else {
                            val pathParts = prefixPath.split('/', limit = 3).iterator()

                            // we take the first subdirectory after the prefix
                            val firstSub = pathParts.next()
                            val linkQuery = firstSub.split('?', limit = 2).iterator()
                            val gitHubLink = linkQuery.next()
                            var linkParams = if (linkQuery.hasNext()) "?" + linkQuery.next() else ""
                            var linkParamsSuffix = ""

                            // now we see where we are
                            when {
                                gitHubLink == "" && !linkRef.containingFile.isWikiHomePage -> {
                                    prefixPath = destVcsRepoBasePath
                                    this.gitHubLink = if (linkRef is ImageLinkRef) "raw" else "blob"
                                    this.branchOrTag = "master"
                                    linkParamsSuffix = "master"
                                }
                                gitHubLink == "wiki" || gitHubLink == "" && linkRef.containingFile.isWikiHomePage -> {
                                    if (linkParams.isEmpty() && !pathParts.hasNext() && linkRef.fileNameNoExt.isEmpty()) {
                                        // just a link to wiki, match Home page
                                        linkParams = "Home" + linkParams
                                    }

                                    if (gitHubLink == "" && wikiPrefixIsOptional == false) {
                                        // has to have a wiki prefix or it will not resolve in the wiki
                                        if (!pathParts.hasNext() || pathParts.next() != "wiki") {
                                            // to allow for loose matching
                                            completionMatchOnly = true
                                        }
                                    }

                                    prefixPath = "$destVcsRepoBasePath$destProjectDirName${PathInfo.WIKI_HOME_DIR_EXTENSION}/"
                                    this.gitHubLink = "wiki"
                                    wikiMatchingRules = true
                                }
                                gitHubLink == "blob" || gitHubLink == "raw" -> {
                                    if (pathParts.hasNext()) {
                                        linkParamsSuffix = pathParts.next()
                                    } else {
                                        linkParamsSuffix = "master"
                                        completionMatchOnly = true
                                    }

                                    branchOrTag = linkParamsSuffix
                                    prefixPath = destVcsRepoBasePath
                                    this.gitHubLink = gitHubLink
                                }
                                gitHubLink in GitHubLinkResolver.GITHUB_LINKS -> {
                                    if (linkRef is ImageLinkRef) {
                                        completionMatchOnly = true
                                    }

                                    prefixPath = "$destVcsRepoBasePath$gitHubLink/"
                                    gitHubLinks = true
                                    this.gitHubLink = gitHubLink
                                }
                                else -> {
                                    if (linkRef.containingFile.isWikiHomePage) {
                                        // this one does checks in wiki, but only if it is not an image link
                                        if (linkRef is ImageLinkRef) {
                                            completionMatchOnly = true
                                        }

                                        prefixPath = "$destVcsRepoBasePath$destProjectDirName${PathInfo.WIKI_HOME_DIR_EXTENSION}/"
                                        this.gitHubLink = "wiki"
                                        wikiMatchingRules = true
                                    } else {
                                        // we are trying to resolve a non-existent sub directory
                                        // RELEASE : disable logging
                                        //println("rejecting for only loose match originalURI $linkRefWasURI ${linkRef.filePath}")
                                        completionMatchOnly = true
                                    }
                                }
                            }

                            var splicedParts = pathParts.splice("/", skipEmpty = false)

                            // if we have wiki already handled, and there is a wiki/ in the link then it must be optional or only loose match is possible
                            if (wikiPrefixIsOptional != true && gitHubLink == "wiki" && (splicedParts.startsWith("wiki/") || splicedParts == "wiki")) completionMatchOnly = true

                            // handle the optional wiki prefix for wiki/Home page
                            if (wikiPrefixIsOptional == true || completionMatchOnly) {
                                if (splicedParts.startsWith("wiki/")) splicedParts = splicedParts.substring("wiki/".length)
                                else if (splicedParts == "wiki") splicedParts = ""
                            }

                            val gitHubRepoLink = (linkParams + arrayOf(linkParamsSuffix, splicedParts).reduce(skipEmptySplicer("/")).suffixWith('/') + linkRef.fileName).removePrefix("/")
                            repoLink = (linkParams + splicedParts.suffixWith('/') + linkRef.fileName).removePrefix("/")
                            gitHubLinkParams = (if (!gitHubRepoLink.startsWith("?", "/")) "/" else "") + gitHubRepoLink + linkRef.anchorText
                            gitHubLinkWithParams = this.gitHubLink + gitHubLinkParams
                            fixedPrefix = prefixPath
                            fixedPrefixPattern = quoteMatchPrefixPattern(fixedPrefix)
                        }
                    }
                }
            }

            // from here we need to use repoLink, this is the stuff that comes after fixedPrefix
            linkRef = linkRef.replaceFilePath(fullPath = repoLink)
            isCompletionMatch = originalWantCompletionMatch || linkRef.filePath.isEmpty() && !linkRef.isSelfAnchor || linkRef.fileNameNoExt.startsWith('.') && !linkRef.hasExt
            val filePath = if (isCompletionMatch) "" else linkRef.filePath
            val filePathNoExt = if (isCompletionMatch) "" else linkRef.filePathNoExt
//            val fileName = if (isCompletionMatch) "" else linkRef.fileName
            val fileNameNoExt = if (isCompletionMatch) "" else linkRef.fileNameNoExt
            val ext = if (linkRef.fileName.startsWith('.') && !linkRef.hasExt) linkRef.fileName else linkRef.ext
            val linkExtensions = if (linkRef.fileName.startsWith('.') && !linkRef.hasExt) arrayOf() else linkRef.linkExtensions
            val emptyMatchesAll = isCompletionMatch
            effectiveExt = ext

            if (wikiMatchingRules) {
                // same as wiki link but without the wiki to file name conversion and the anchor matching, anchor needs to be escaped
                // we do anchor matching for loose match identical to wiki links but without the wiki to file conversion

                // if it has extension then we include it as alternative before default extensions because it may not be an extension but part of the file name
                if (useLooseMatch) {
                    val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                    val looseFilenamePattern = linkTextToFileMatch(fileNameNoExt, isOptional = false, emptyMatchesAll = false)
                    val looseFilePrefixPattern = linkTextToFileMatch(if (filePath.isEmpty()) linkRef.containingFile.filePath else linkRef.path)
                    val completionFilenamePattern = "(?:.+)"
                    val completionFilePrefixPattern = if (originalLinkRef.path.isEmpty()) subDirPattern else linkPathCompletionMatch(originalLinkRef.path)
                    val completionExtensionPattern = if (ext.isNotEmpty()) completionExtensionPattern(ext) else extensionPattern(PathInfo(linkRef.anchorText).ext, *linkExtensions, isOptional = true).regexGroup()
                    val looseExtensionPattern = "(?:\\..*)?"
                    linkLooseMatch = "^$fixedPrefixPattern$looseFilePrefixPattern$subDirPattern$looseFilenamePattern$looseAnchorPattern${looseExtensionPattern.suffixWith('?')}$"
                    looseFileNameExtPattern = "^$looseFilenamePattern$looseAnchorPattern${looseExtensionPattern.suffixWith('?')}$"
                    linkCompletionMatch = "^$fixedCompletionPrefixPattern$completionFilePrefixPattern$completionFilenamePattern$looseAnchorPattern$completionExtensionPattern$"
                    completionFileNameExtPattern = "^$completionFilenamePattern$looseAnchorPattern$completionExtensionPattern$"
                    val extensionList = arrayListOf(ext, PathInfo(linkRef.anchorText).ext, *linkExtensions)
                    linkCompletionMatchExtensions = extensionList
                    linkLooseMatchExtensions = extensionList.filter { it.isNotEmpty() }
                }

                val defaultExtensions = extensionPattern(*linkExtensions, isOptional = false)
                val extensionPattern = if (ext.isNotEmpty()) (extensionPattern(ext, isOptional = false) + "|" + extensionPattern(ext, isOptional = false) + defaultExtensions).regexGroup() else defaultExtensions
                val filenamePattern = linkTextToFileMatch(filePath, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                val filenameNoExtPattern = linkTextToFileMatch(filePathNoExt, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)

                fileName = fileNameNoExt
                linkFileMatch = "^$fixedPrefixPattern$filenamePattern$"
                if (anchorPattern.isNotEmpty()) linkFileAnchorMatch = "^$fixedPrefixPattern$filenameNoExtPattern$anchorPattern$"
                if (anchorPattern.isNotEmpty()) linkSubAnchorExtMatch = "^$fixedPrefixPattern$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
                fileNameExtPattern = "^$filenamePattern$"
                linkSubExtMatch = "^$fixedPrefixPattern$subDirPattern$filenamePattern$extensionPattern$"
                linkAllMatchExtensions = arrayOf(ext, *linkExtensions).filter { it.isNotEmpty() }

                // if linkref path is empty then this should match linkFileMatch|linkSubExtMatch|linkSubAnchorExtMatch|fileAnchorMatch
                linkAllMatch = if (vcsRepoBasePath == "/" || linkRef.path.isEmpty()) "$linkFileMatch|$linkSubExtMatch" else linkFileMatch
            } else {
                // regular repo match, we build up all options for looseMatch and later resolution as to what we really matched
                val extensionPattern = if (linkRef.hasExt) extensionPattern(ext, isOptional = false) else extensionPattern(*linkExtensions, isOptional = false)
                val filenamePattern = linkTextToFileMatch(filePath, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                val filenameNoExtPattern = linkTextToFileMatch(filePathNoExt, isOptional = false, emptyMatchesAll = emptyMatchesAll)

                if (useLooseMatch) {
                    val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                    val looseFilenamePattern = linkTextToFileMatch(fileNameNoExt, isOptional = false, emptyMatchesAll = false)
                    val completionFilenamePattern = "(?:.+)"
                    val completionFilePrefixPattern = if (originalLinkRef.path.isEmpty()) subDirPattern else linkPathCompletionMatch(originalLinkRef.path)
                    val completionExtensionPattern = if (ext.isNotEmpty()) completionExtensionPattern(ext) else extensionPattern(PathInfo(linkRef.anchorText).ext, *linkExtensions, isOptional = true)
                    val looseExtensionPattern = "(?:\\..*)?"
                    linkLooseMatch = "^$fixedPrefixPattern$subDirPattern$looseFilenamePattern$looseAnchorPattern${looseExtensionPattern.suffixWith('?')}$"
                    looseFileNameExtPattern = "^$looseFilenamePattern$looseAnchorPattern${looseExtensionPattern.suffixWith('?')}$"
                    linkCompletionMatch = "^$fixedCompletionPrefixPattern$completionFilePrefixPattern$completionFilenamePattern$looseAnchorPattern$completionExtensionPattern$"
                    completionFileNameExtPattern = "^$completionFilenamePattern$looseAnchorPattern$completionExtensionPattern$"
                    val extensionList = arrayListOf(ext, PathInfo(linkRef.anchorText).ext, *linkExtensions)
                    linkCompletionMatchExtensions = extensionList
                    linkLooseMatchExtensions = extensionList //.filter { !it.isEmpty() }

                    //                    println(linkCompletionMatch)
                    val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)
                    if (anchorPattern.isNotEmpty()) linkFileAnchorMatch = "^$fixedPrefixPattern$filenameNoExtPattern$anchorPattern$"
                    if (anchorPattern.isNotEmpty()) linkSubAnchorExtMatch = "^$fixedPrefixPattern$noWikiSubDirPattern$filenameNoExtPattern$anchorPattern$extensionPattern$"
                    linkSubExtMatch = "^$fixedPrefixPattern$noWikiSubDirPattern$filenameNoExtPattern$extensionPattern$"
                }

                // only exact matches for files, the rest are loose matches
                fileName = fileNameNoExt
                linkFileMatch = "^$fixedPrefixPattern$filenamePattern$"
                fileNameExtPattern = "^$filenamePattern$"
                linkAllMatch = linkFileMatch
                linkAllMatchExtensions = if (linkRef.hasExt) arrayListOf(ext) else arrayListOf(*linkExtensions)
            }
        }

        assert(linkCompletionMatch != null)
        assert(linkLooseMatch != null)
        assert(linkAllMatch != null)

        isOnlyCompletionMatchValid = completionMatchOnly
    }
}
