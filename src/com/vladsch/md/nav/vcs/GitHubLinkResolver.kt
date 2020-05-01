// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:Suppress("MemberVisibilityCanBePrivate")

package com.vladsch.md.nav.vcs

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.file.exclude.ProjectPlainTextFileTypeManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.vladsch.flexmark.util.sequence.Escaping
import com.vladsch.md.nav.parser.api.MdLinkMapProvider
import com.vladsch.md.nav.parser.cache.MdCachedResolvedLinks
import com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.element.MdLinkElement
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.util.*
import com.vladsch.plugin.util.*
import icons.MdIcons
import java.util.*
import java.util.regex.Pattern
import javax.swing.Icon
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.text.endsWith
import kotlin.text.removePrefix
import kotlin.text.removeSuffix
import kotlin.text.startsWith

class GitHubLinkResolver(projectResolver: ProjectResolver
    , containingFile: FileRef
    , renderingProfile: MdRenderingProfile? = null
    , branchOrTag: String? = null
) : MdLinkResolver(projectResolver, containingFile, branchOrTag) {

    companion object {
        private val LOG_CACHE_DETAIL = IndentingLogger.LOG_COMPUTE_DETAIL

        const val GITHUB_BLOB_NAME: String = "blob"
        const val GITHUB_FORK_NAME: String = "fork"
        const val GITHUB_GRAPHS_NAME: String = "graphs"
        const val GITHUB_ISSUES_NAME: String = "issues"
        const val GITHUB_LABELS_NAME: String = "labels"
        const val GITHUB_MILESTONES_NAME: String = "milestones"
        const val GITHUB_PULLS_NAME: String = "pulls"
        const val GITHUB_PULSE_NAME: String = "pulse"
        const val GITHUB_RAW_NAME: String = "raw"
        const val GITHUB_WIKI_NAME: String = "wiki"

        // NOTE: keep alphabetically sorted. These are not re-sorted after match
        @JvmField
        val GITHUB_LINKS: Array<String> = arrayOf(
            GITHUB_BLOB_NAME,
            GITHUB_FORK_NAME,
            GITHUB_GRAPHS_NAME,
            GITHUB_ISSUES_NAME,
            GITHUB_LABELS_NAME,
            GITHUB_MILESTONES_NAME,
            GITHUB_PULLS_NAME,
            GITHUB_PULSE_NAME,
            GITHUB_RAW_NAME,
            GITHUB_WIKI_NAME
        )

        @JvmField
        val GITHUB_NON_FILE_LINKS: Array<String> = arrayOf(
            GITHUB_FORK_NAME,
            GITHUB_GRAPHS_NAME,
            GITHUB_ISSUES_NAME,
            GITHUB_LABELS_NAME,
            GITHUB_MILESTONES_NAME,
            GITHUB_PULLS_NAME,
            GITHUB_PULSE_NAME
        )

        @JvmField
        val GITHUB_TARGET_LINKS: Array<String> = arrayOf(
            GITHUB_FORK_NAME,
            GITHUB_GRAPHS_NAME,
            GITHUB_ISSUES_NAME,
            GITHUB_LABELS_NAME,
            GITHUB_MILESTONES_NAME,
            GITHUB_PULLS_NAME,
            GITHUB_PULSE_NAME,
            GITHUB_WIKI_NAME
        )

        @JvmStatic
        fun isGitHubLink(link: String): Boolean {
            return link in GITHUB_LINKS
        }

        @JvmStatic
        fun isGitHubNonFileLink(link: String): Boolean {
            return link in GITHUB_NON_FILE_LINKS
        }

        @JvmStatic
        fun wantFlags(linkRef: LinkRef): Int {
            return if (linkRef.isURI && linkRef.isLocal) Want.invoke(Local.URI, Remote.URI)
            else if (linkRef.isURL) Want.invoke(Local.URL, Remote.URL, Links.URL)
            else if (linkRef.isLocal && linkRef.isAbsolute) Want.invoke(Local.ABS, Remote.ABS)
            else Want(Local.REL, Remote.REL, Links.REL)
        }

        @JvmStatic
        fun wantFlagsWithRaw(linkRef: LinkRef): Int {
            return if (linkRef.isLocal && linkRef.isAbsolute) Want.invoke(Local.ABS, Remote.ABS)
            else if (linkRef.isURI && linkRef.isLocal) Want.invoke(Local.URI, Remote.URI)
            else if (linkRef.isURL) Want.invoke(Local.URI, Remote.RAW)
            // FIX: add RAW_REL and change RAW to RAW_URL
            else Want(Local.REL, Remote.REL, Links.REL)
        }

        // FEATURE: add ignored sites to annotation config
        private val IGNORED_SITES = Pattern.compile("^https?://(?:[a-zA-z_-]+\\.)*(?:example\\.com)")

        @JvmStatic
        fun isIgnoredSite(url: String): Boolean {

            return IGNORED_SITES.matcher(url).find()
        }

        @JvmStatic
        fun getIcon(url: String): Icon? {
            @Suppress("NAME_SHADOWING")

            return when {
                url.startsWith("ftp://") -> MdIcons.LinkTypes.Ftp
                url.startsWith("jetbrains://") -> MdIcons.LinkTypes.JetBrains
                url.startsWith("upsource://") -> MdIcons.LinkTypes.Upsource
                url.startsWith("https://upsource.jetbrains.com/") -> MdIcons.LinkTypes.Upsource
                url.startsWith("https://github.com/") || url == "https://github.com" -> MdIcons.LinkTypes.GitHub
                url.startsWith("http://github.com/") || url == "http://github.com" -> MdIcons.LinkTypes.GitHub
                url.startsWith("https://raw.githubusercontent.com/") -> MdIcons.LinkTypes.GitHub
                url.startsWith("http://raw.githubusercontent.com/") -> MdIcons.LinkTypes.GitHub
                url.startsWith("mailto:") -> MdIcons.LinkTypes.Mail
                url.matches("^.+@.+\\.\\+$".toRegex()) -> MdIcons.LinkTypes.Mail
                PathInfo.isURL(url) -> MdIcons.LinkTypes.Web
                PathInfo.isCustomURI(url) -> MdIcons.LinkTypes.CustomUri
                else -> {
                    MdIcons.LinkTypes.Unknown
                }
            }
        }
    }

    fun getIcon(linkRef: LinkRef): Icon? {
        @Suppress("NAME_SHADOWING")
        var linkRef = linkRef

        if (linkRef.isRelative || linkRef.isRepoRelative) {
            // should be a github link or unresolved
            for (link in GITHUB_NON_FILE_LINKS) {
                if (linkRef.filePath.matches("^.*../$link\\b.*$".toRegex())) {
                    val resolved = resolve(linkRef, Want(Local.NONE, Remote.NONE, Links.URL), null)
                    if (resolved is LinkRef) linkRef = resolved
                    break
                }
            }
        }

        return getIcon(linkRef.filePath)
    }

    constructor(virtualFile: VirtualFile, project: Project) : this(MdLinkResolverManager.getInstance(project), FileRef(virtualFile.path, virtualFile), null)
    constructor(virtualFile: VirtualFile, project: Project, renderingProfile: MdRenderingProfile? = null) : this(MdLinkResolverManager.getInstance(project), FileRef(virtualFile.path, virtualFile), renderingProfile)

    constructor(projectFileRef: ProjectFileRef) : this(MdLinkResolverManager.getInstance(projectFileRef.project), projectFileRef, null)
    constructor(projectFileRef: ProjectFileRef, renderingProfile: MdRenderingProfile? = null) : this(MdLinkResolverManager.getInstance(projectFileRef.project), projectFileRef, renderingProfile)

    constructor(psiFile: PsiFile) : this(MdLinkResolverManager.getInstance(psiFile.originalFile.project), FileRef(MdPsiImplUtil.getVirtualFilePath(psiFile), MdPsiImplUtil.getVirtualFile(psiFile)), null)

    constructor(psiFile: PsiFile, renderingProfile: MdRenderingProfile? = null) : this(MdLinkResolverManager.getInstance(psiFile.originalFile.project), FileRef(MdPsiImplUtil.getVirtualFilePath(psiFile), MdPsiImplUtil.getVirtualFile(psiFile)), renderingProfile)

    constructor(psiElement: PsiElement) : this(psiElement.containingFile.originalFile, null)
    constructor(psiElement: PsiElement, renderingProfile: MdRenderingProfile? = null) : this(psiElement.containingFile.originalFile, renderingProfile)

    internal val linkInspector: GitHubLinkInspector by lazy { GitHubLinkInspector(this) }

    private var matcher: GitHubLinkMatcher? = null

    val renderingProfile: MdRenderingProfile by lazy {
        renderingProfile ?: MdRenderingProfileManager.getInstance(project).getRenderingProfile(containingFile.virtualFile)
    }

    private val includeDirsInCompletion: Boolean by lazy {
        var includeDirsInCompletion = false
        for (provider in MdLinkMapProvider.EXTENSIONS.value) {
            val includeDirs = provider.getIncludeDirsInCompletion(renderingProfile)
            if (includeDirs != null) {
                includeDirsInCompletion = includeDirs
                break
            }
        }
        includeDirsInCompletion
    }

    val linkEncodingExclusionMap: Map<String, String>? by lazy {
        var linkExclusionMap: Map<String, String>? = null

        if (project != null) {
            val psiFile = containingFile.psiFile(project)
            if (psiFile != null) {
                for (provider in MdLinkMapProvider.EXTENSIONS.value) {
                    linkExclusionMap = provider.getLinkExclusionMap(renderingProfile)
                    if (linkExclusionMap != null) {
                        break
                    }
                }
            }
        }

        linkExclusionMap
    }

    override fun linkEncodingExclusionMap(): Map<String, String>? {
        return linkEncodingExclusionMap
    }

    override fun renderingProfile(): MdRenderingProfile? {
        return renderingProfile
    }

    fun getAltLinkFormatText(linkElement: MdLinkElement<*>, options: Int, wantShorter: Boolean, nullifyIfSame: Boolean, destResolver: GitHubLinkResolver): String? {
        val linkRef = linkElement.linkRef
        val altLinkFormatText = getAltLinkFormatText(linkRef, options, wantShorter, destResolver)
        return altLinkFormatText.nullIf(nullifyIfSame && altLinkFormatText == linkRef.filePathWithAnchor)
    }

    fun getAltLinkFormatText(linkRef: LinkRef, options: Int, wantShorter: Boolean, destResolver: GitHubLinkResolver): String {
        assertContainingFile(linkRef)

        val altRef = getAltLinkFormat(linkRef, options, wantShorter, destResolver) ?: return linkRef.filePathWithAnchor
        var wantExtension = true

        if (altRef is LinkRef && altRef.targetRef != null && altRef.targetRef.isWikiPage) {
            val anchorInfo = PathInfo(linkRef.anchor.orEmpty())
            if (linkRef.anchor != null && anchorInfo.isWikiPageExt) {
                if (!wasAnchorUsedInMatch(linkRef, altRef.targetRef)) {
                    wantExtension = false
                }
            }
        }

        val linkRefText = if (altRef is LinkRef && !altRef.anchorText.isEmpty() && altRef.anchorText != "#")
            if (wantExtension) altRef.filePathWithAnchor else altRef.filePathNoExtWithAnchor
        else
            if (wantExtension) altRef.filePath else altRef.filePathNoExt

        val mapped = denormalizedLinkRef(linkRefText)
        return mapped
    }

    fun getAltLinkFormat(linkRef: LinkRef, options: Int, wantShorter: Boolean, destResolver: GitHubLinkResolver): PathInfo? {
        assertContainingFile(linkRef)

        var useLinkRef = linkRef
        var altRef = resolve(useLinkRef, options, null) ?: return null

        if (altRef.isFileURI && wantLocalType(options) == Local.URI) {
            return altRef
        } else if (altRef.isURL && wantRemoteType(options) == Remote.URL) {
            return altRef
        }

        if (altRef is LinkRef) {
            if (destResolver !== this && altRef.targetRef != null) {
                // need to map to another target file
                useLinkRef = destResolver.createLinkRefForTarget(linkRef, altRef.targetRef!!, false)
                altRef = destResolver.resolve(useLinkRef, options, null) ?: return altRef
            }
        }

        if (useLinkRef.containingFile.isWikiHomePage && altRef.filePath.startsWith("../")) {
            altRef = PathInfo(altRef.filePath.removePrefix("../"))
        }

        val altLinkText = if (altRef is LinkRef && !altRef.anchorText.isEmpty() && altRef.anchorText != "#") altRef.filePathWithAnchor else altRef.filePath
        if (altLinkText == linkRef.filePathWithAnchor) return altRef
        val oldFormat = wantFlags(useLinkRef)
        if (oldFormat == options && destResolver === this && (!wantShorter || altLinkText.length >= linkRef.filePathWithAnchor.length)) return null
        // it is a reference and it has an image target then only raw should be used
        // here we need to add this to the intention that handles references, it needs to change the linkref
        // if (linkRefInfo.isImageExt) {
        //     val imageLinkRef = ImageLinkRef.from(linkRefInfo)
        //
        //     if (imageLinkRef != null) {
        //         altRef = resolver.resolve(imageLinkRef, Want.invoke(Local.URL, Remote.URL), looseTargetRefs)
        //         if (altRef != null) linkVarieties.put(if (altRef is LinkRef) (altRef as LinkRef).filePathWithAnchor else altRef.filePath, Want.FileType.URL)
        //     }
        // } else {
        //     altRef = resolver.resolve(linkRefInfo, Want.invoke(Local.URL, Remote.URL), looseTargetRefs)
        //     if (altRef != null) linkVarieties.put(if (altRef is LinkRef) (altRef as LinkRef).filePathWithAnchor else altRef.filePath, Want.FileType.URL)
        // }
        return altRef
    }

    fun changeLinkRefContainingFile(linkRef: LinkRef, destinationResolver: GitHubLinkResolver, wikiToLinkRef: Boolean): LinkRef {
        val targetRef = resolve(linkRef, Want(Local.REF, Remote.REF, Links.URL), null) as? FileRef ?: return linkRef
        return destinationResolver.createLinkRefForTarget(linkRef, targetRef, wikiToLinkRef)
    }

    // call on destination file's resolver
    fun createLinkRefForTarget(linkRef: LinkRef, targetRef: FileRef, wikiToLinkRef: Boolean): LinkRef {
        var newLinkRef: LinkRef = linkRef

        if (!linkRef.isURL && !linkRef.isURI) {
            // update the link ref according to the new destination for the file
            var makeLinkRef = false

            newLinkRef = linkRef.withTargetRef(targetRef)

            // now we can change the links based on change of target path and new containing file reference
            if (linkRef is WikiLinkRef) {
                // the file can be moved out of reach, we may need to change the wiki link to explicit when that happens
                if (!wikiToLinkRef && containingFile.isUnderWikiDir && targetRef.isUnderWikiDir && containingFile.wikiDir == targetRef.wikiDir) return newLinkRef // no change, still in same wiki directory

                // change to explicit link
                newLinkRef = LinkRef.from(newLinkRef, linkEncodingExclusionMap)
                makeLinkRef = true
            }

            var denormalizeLink = true
            val movedLink = if (makeLinkRef) {
                if (containingFile.fileName == targetRef.fileName) {
                    // self reference, keep anchor, wiki links don't use directory paths
                    LinkRef(containingFile, "", newLinkRef.anchor, targetRef, false)
                } else {
                    // create new link ref
                    LinkRef(containingFile, "", null, targetRef, false)
                }
            } else if (containingFile.filePath == targetRef.filePath) {
                // self reference
                if (containingFile.isWikiPage) {
                    denormalizeLink = false // self reference in wiki page, ignore path
                    LinkRef(containingFile, "", newLinkRef.anchor, targetRef, false)
                } else {
                    LinkRef(containingFile, "", newLinkRef.anchor, targetRef, false)
                }
            } else if (targetRef.isWikiPage && containingFile.isWikiPage && newLinkRef.fileName == targetRef.fileName) {
                // reference in wiki page, ignore path
                denormalizeLink = false
                LinkRef(containingFile, "", newLinkRef.anchor, targetRef, false)
            } else {
                LinkRef(containingFile, "", newLinkRef.anchor, targetRef, false)
            }

            newLinkRef = if (denormalizeLink) {
                val preservedLinkFormat = preserveLinkFormat(linkRef, movedLink)
                denormalizedLinkRef(preservedLinkFormat)
            } else movedLink
        }
        return newLinkRef
    }

    fun getMatcher(linkRef: LinkRef): GitHubLinkMatcher {
        var matcher_ = matcher
        val normLinkRef = normalizedLinkRef(linkRef)
        if (matcher_ === null || matcher_.originalLinkRef != normLinkRef) {
            matcher_ = GitHubLinkMatcher(projectResolver, normLinkRef, linkEncodingExclusionMap)
            matcher = matcher_
        }
        return matcher_
    }

    fun getLastMatcher(): GitHubLinkMatcher? {
        return matcher
    }

    fun resetLastMatcher() {
        matcher = null
    }

    // TEST: this needs tests to make sure it works
    override fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean?, branchOrTag: String?): Boolean {
        assertContainingFile(linkRef)
        val linkRefText = linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, "", true)
        return linkRef.filePath.equals(linkRefText, ignoreCase = targetRef.isWikiPage && !linkRef.hasExt)
    }

    override fun isResolved(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): Boolean {
        assertContainingFile(linkRef)
        return resolve(linkRef, options, inList) != null
    }

    fun preserveLinkFormat(linkRef: LinkRef, newLinkRef: LinkRef): LinkRef {
        assertContainingFile(newLinkRef)

        // FIX: when cross repository resolution is implemented, try to preserve the format but use one that will resolve if preserving it will not resolve the link
        var flags = wantFlags(linkRef)

        if (newLinkRef.targetRef == null) {
            val resolvedLinkRef = resolve(newLinkRef, flags, null)
            if (resolvedLinkRef is LinkRef) return resolvedLinkRef
        } else {
            // have target, do we have a link path?
            var useLinkRef = newLinkRef
            var useRaw = false

            if (newLinkRef.filePath.isEmpty()) {
                // no, make it from the linkRef but to the new target stored in newLinkRef
                // strip extension from links to markdown files when they move from non-wiki to wiki directory

                // here we need to know if the target file has not gone from non-wiki to wiki status, because it has an extension, in which case we have to preserve it
                // if it went from non-wiki to wiki, either the containing file is a wiki page and the link does not leave the wiki, there is no blob/ or raw/ in it, or the old link would have contained /wiki/ or ended in /wiki, in either case
                // we keep the extension
                val hasExt = if (newLinkRef.targetRef.isUnderWikiDir) wikiLinkHasRealExt(linkRef, newLinkRef.targetRef) else linkRef.hasExt
                useRaw = newLinkRef.targetRef.isRawFile || linkRef.filePath.contains("/raw/")
                    || if (linkRef.containingFile.isWikiPage) hasExt && !linkRef.contains("/blob/")
                else hasExt && (linkRef.filePath.contains("/wiki/") || linkRef.filePath.endsWith("/wiki"))

                val useExtForWiki = useRaw || !newLinkRef.targetRef.isWikiPage

                if (useRaw) flags = wantFlagsWithRaw(linkRef)

                val linkAddress = linkAddress(newLinkRef.replaceFilePath(linkRef.filePath, true), newLinkRef.targetRef, useExtForWiki, null, newLinkRef.anchor, linkRef.filePath.isEmpty())
                useLinkRef = newLinkRef.replaceFilePath(linkAddress, true, false)
            }

            if (linkRef.isURI || linkRef.isAbsolute && linkRef.isLocal) {
                return processMatchOptions(useLinkRef, newLinkRef.targetRef, flags) as LinkRef? ?: useLinkRef
            } else if (useRaw) {
                // here we change ../blob/ to ../raw/
                if (useLinkRef.filePath.contains("../blob/")) {
                    useLinkRef = useLinkRef.replaceFilePath(useLinkRef.filePath.replace("../blob/", "../raw/"), true)
                } else if (!useLinkRef.filePath.startsWith('/') && !newLinkRef.targetRef.isUnderWikiDir && !useLinkRef.containingFile.isWikiPage && !useLinkRef.isURI) {
                    // need to prefix it ../../raw/ but only after the initial ../../, would be nice if I added a comment on when this is needed
                    if (!useLinkRef.filePath.startsWith("../../../")) {
                        val pos = useLinkRef.filePath.lastIndexOf("../")
                        if (pos < 0) {
                            useLinkRef = useLinkRef.replaceFilePath("../../raw/master/" + useLinkRef.filePath, true)
                        } else {
                            useLinkRef = useLinkRef.replaceFilePath(useLinkRef.filePath.substring(0, pos + "../".length) + "../../raw/master/" + useLinkRef.filePath.substring(pos + "../".length), true)
                        }
                    }
                }
            }

            // must be correct as is
            return useLinkRef
        }
        return linkRef
    }

    override fun resolve(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): PathInfo? {
        assertContainingFile(linkRef)
        // FIX: if only want local, then can try to resolve external links to local file refs if they map, for that need to parse the
        //   LinkRef's URL file path and remove the repoPrefix for non-Wiki and wikiRepoPrefix for wiki files, then prefix the result with the corresponding basePath
        var linkRef_ = normalizedLinkRef(linkRef)
        var targetRef: PathInfo = linkRef_
        var opts = options

        if (linkRef_.isSelfAnchor) {
            if (linkRef_ is WikiLinkRef && linkRef_.filePath.isEmpty()) {
                // here it is a pure anchor wiki link, which does not resolve
                if (!wantCompletionMatch(options)) return null
            }

            targetRef = linkRef_.containingFile
            linkRef_ = linkRef_.replaceFilePath(if (linkRef_.hasExt || !linkRef_.containingFile.isUnderWikiDir) targetRef.fileName else targetRef.fileNameNoExt, true, true)
        }

        if (linkRef_.isURI || linkRef_.isAbsolute) {
            val relPath = absoluteToRelativeLink(linkRef_)
            if (!linkRef_.isURI) opts = opts or LINK_REF_WAS_REPO_REL
            else opts = opts or LINK_REF_WAS_URI
            linkRef_ = relPath
        }

        val file = if (project != null) containingFile.psiFile(project) as? MdFile else null
        if (file != null) {
            var cachedLink = MdCachedResolvedLinks.getCachedLink(file, linkRef)
            if (cachedLink == null && linkRef != linkRef_) {
                // try mapped
                cachedLink = MdCachedResolvedLinks.getCachedLink(file, linkRef_)
            }

            if (cachedLink != null) {
                val resolved: PathInfo? = processMatchOptions(linkRef_, cachedLink, opts)
                if (resolved != null) {
                    LOG_CACHE_DETAIL.debug { "Resolved cached link: ${cachedLink.filePath} to ${resolved.filePath}" }
                    // FIX: the cached link may have the wrong address format since this is not saved during caching
                    return resolved
                }
            } else if (MdCachedResolvedLinks.hasCachedLink(file, linkRef)) {
                // this one is not defined so we save time by not resolving
                return null;
            }
        }

        if (!linkRef_.isAbsolute || !linkRef_.isURI || linkRef_.filePath.startsWith("/")) {
            // resolve the relative link as per requested options
            // FIX: use cached resolved links and update values if none were found there
            val matches = getMatchedRefs(linkRef_, null, opts, inList)
            val resolvedRef = (if (matches.isNotEmpty()) matches[0] else null) ?: return null
            if (file != null) {
                MdCachedResolvedLinks.addCachedLink(file, linkRef, resolvedRef)
            }
            return resolvedRef
        }

        if (file != null) {
            MdCachedResolvedLinks.addCachedLink(file, linkRef, targetRef)
        }

        return processMatchOptions(linkRef_, targetRef, opts)
    }

    override fun isAbsoluteUnchecked(linkRef: PathInfo): Boolean {
        if (linkRef.isURL && linkRef is LinkRef) {
            val normalized = absoluteToRelativeLink(linkRef)
            if (!normalized.isURL) return false
        }
        if (linkRef.isFileURI && !linkRef.isURL) {
            // file:// only check if part of module or project
            if (linkRef is LinkRef) {
                if (PathInfo.removeFileUriPrefix(linkRef.path).startsWith(projectResolver.projectBasePath.suffixWith("/"))) {
                    return false
                } else {
                    // use the modules
                    val virtualFile = linkRef.virtualFile
                    val project = projectResolver.project
                    if (virtualFile != null && project != null) {
                        val fileIndex = com.intellij.openapi.roots.ProjectRootManager.getInstance(project).fileIndex
                        val module = fileIndex.getModuleForFile(virtualFile)
                        if (module != null) {
                            // if target ref in module under module root dir then it can be relative
                            return false
                        }
                    }
                }
            }
            return true
        } else {
            if (BrowserUtil.isAbsoluteURL(linkRef.filePath)) {
                return true
            }
        }
        return false
    }

    override fun isExternalUnchecked(linkRef: LinkRef): Boolean {
        val vcsRoot = projectResolver.getVcsRoot(linkRef.containingFile)
        return linkRef.isExternal && (vcsRoot == null || vcsRoot.baseUrl == null || !linkRef.filePath.toLowerCase().startsWith(vcsRoot.baseUrl.toLowerCase()))
    }

    override fun multiResolve(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): List<PathInfo> {
        assertContainingFile(linkRef)

        var relLink = normalizedLinkRef(linkRef)
        var opts = options
        if (relLink.isURI || relLink.isAbsolute) {
            val relPath = absoluteToRelativeLink(relLink)
            if (relPath.isURL) {
                return listOf<PathInfo>(relPath)
            } else {
                opts = if (!relLink.isURI) opts or LINK_REF_WAS_REPO_REL
                else opts or LINK_REF_WAS_URI
                relLink = relPath
            }
        }

        return getMatchedRefs(relLink, null, opts, inList)
    }

    // FIX: change this to take an exact resolve list and a loose matched list so that
    // all types of issues could be analyzed, not just based on single target
    override fun inspect(linkRef: LinkRef, targetRef: FileRef, referenceId: Any?): List<InspectionResult> {
        assertContainingFile(linkRef)
        val normLinkRef = normalizedLinkRef(linkRef)
        return linkInspector.inspect(normLinkRef, targetRef, referenceId)
    }

    fun getTargetFileTypes(extensions: List<String>?, completionMatch: Boolean, includeNoExtFiles: Boolean): HashSet<FileType> {
        val typeSet = HashSet<FileType>()
        if (extensions == null || project == null) return typeSet

        val typeManager = FileTypeManager.getInstance() as FileTypeManagerImpl
        val registeredFileTypes = FileTypeRegistry.getInstance().registeredFileTypes
        var allExtensionResolved = true
        val unresolvedExtensions: HashSet<String> = HashSet()

        for (fileType in registeredFileTypes) {
            val typeExtensions = typeManager.getAssociations(fileType)

            outer@
            for (ext in extensions) {
                if (ext.isEmpty()) {
                    allExtensionResolved = false
                    unresolvedExtensions.add(ext)
                    continue
                }

                val bareExt = if (ext[0] == '.') ext.substring(1) else ext
                val extSuffix = ".$bareExt"

                // does not work for nested extensions such as blade.php, will not find it since we are looking for .php
                // val targetFileType = typeManager.getFileTypeByExtension(ext.removePrefix("."))
                // typeSet.add(targetFileType)

                val extensionResolved = false

                for (typeExt in typeExtensions) {
                    val typeText = typeExt.presentableString

                    if (typeText.isEmpty()) {
                        typeSet.add(fileType)
                        break@outer
                    } else {
                        if (typeText.endsWith(extSuffix, ignoreCase = true)) {
                            typeSet.add(fileType)
                            break@outer
                        }

                        if (completionMatch && typeText.length > bareExt.length) {
                            val pos = typeText.lastIndexOf('.')
                            if (pos >= 0 && typeText.substring(pos + 1).startsWith(bareExt, ignoreCase = true)) {
                                typeSet.add(fileType)
                                break@outer
                            }
                        }
                    }
                }

                if (!extensionResolved && ext != "md") {
                    allExtensionResolved = false
                    unresolvedExtensions.add(ext)
                }
            }
        }

        if (includeNoExtFiles || !allExtensionResolved) {
            // these are text
            val targetFileType = typeManager.getFileTypeByExtension("txt")
            typeSet.add(targetFileType)
        }

        return typeSet
    }

    internal class MatchList() : MutableList<PathInfo> {
        private val list = ArrayList<PathInfo>()
        private val matchSet = HashMap<String, PathInfo>()

        override val size: Int
            get() = list.size

        private fun adding(element: PathInfo) {
            matchSet[element.filePath] = element
        }

        private fun addingAll(elements: Collection<PathInfo>) {
            elements.forEach {
                matchSet[it.filePath] = it
            }
        }

        fun getList(): ArrayList<PathInfo> {
            return list
        }

        override fun contains(element: PathInfo): Boolean {
            return matchSet.containsKey(element.filePath)
        }

        override fun containsAll(elements: Collection<PathInfo>): Boolean = list.containsAll(elements)

        override fun get(index: Int): PathInfo = list[index]

        override fun indexOf(element: PathInfo): Int = list.indexOf(element)

        override fun isEmpty(): Boolean = list.isEmpty()

        override fun iterator(): MutableIterator<PathInfo> = list.iterator()

        override fun lastIndexOf(element: PathInfo): Int = list.lastIndexOf(element)

        override fun add(element: PathInfo): Boolean {
            adding(element)
            return list.add(element)
        }

        override fun add(index: Int, element: PathInfo) {
            adding(element)
            list.add(index, element)
        }

        override fun addAll(index: Int, elements: Collection<PathInfo>): Boolean {
            addingAll(elements)
            return list.addAll(index, elements)
        }

        override fun addAll(elements: Collection<PathInfo>): Boolean {
            addingAll(elements)
            return list.addAll(elements)
        }

        override fun clear() {
            matchSet.clear()
            list.clear()
        }

        override fun listIterator(): MutableListIterator<PathInfo> = list.listIterator()

        override fun listIterator(index: Int): MutableListIterator<PathInfo> = list.listIterator(index)

        override fun remove(element: PathInfo): Boolean {
            matchSet.remove(element.filePath)
            return list.remove(element)
        }

        override fun removeAll(elements: Collection<PathInfo>): Boolean {
            elements.forEach { matchSet.remove(it.filePath) }
            return list.removeAll(elements)
        }

        override fun removeAt(index: Int): PathInfo {
            matchSet.remove(list[index].filePath)
            return list.removeAt(index)
        }

        override fun retainAll(elements: Collection<PathInfo>): Boolean {
            matchSet.removeIf { key, value -> !elements.contains(value) }
            return list.retainAll(elements)
        }

        override fun set(index: Int, element: PathInfo): PathInfo {
            matchSet.remove(list[index].filePath)
            adding(element)
            return list.set(index, element)
        }

        override fun subList(fromIndex: Int, toIndex: Int): MutableList<PathInfo> {
            throw NotImplementedError()
        }
    }

    fun getMatchedRefs(linkRef: LinkRef, linkMatcher: GitHubLinkMatcher?, options: Int, fromList: List<PathInfo>?): List<PathInfo> {
        assert(linkRef.isNormalized)

        @Suppress("NAME_SHADOWING")
        val linkMatcher = linkMatcher ?: getMatcher(linkRef)

        // process the files that match the pattern and put them in the list
        var matches = MatchList()

        linkMatcher.computeMatchText(options and LINK_REF_WAS_URI != 0, options and LINK_REF_WAS_REPO_REL != 0)

        val completionMatch = linkMatcher.isCompletionMatch
        if ((linkMatcher.isOnlyCompletionMatchValid || (completionMatch && !linkMatcher.gitHubLinks)) && !wantCompletionMatch(options)) return matches

        val linkLooseMatch = linkMatcher.linkLooseMatch
        val linkCompletionMatch = linkMatcher.linkCompletionMatch
        val linkAllMatch = linkMatcher.linkAllMatch
        if (linkLooseMatch == null || linkAllMatch == null || linkCompletionMatch == null) return matches

        // FIX: need to have a flag or to modify the regex to exclude wiki matches when exact matching in the repo
        val allMatchWiki =
            if (wantLooseMatch(options)) linkLooseMatch.toRegex(RegexOption.IGNORE_CASE)
            else if (wantCompletionMatch(options)) linkCompletionMatch.toRegex(RegexOption.IGNORE_CASE)
            else if (linkMatcher.wikiMatchingRules) linkAllMatch.toRegex(RegexOption.IGNORE_CASE)
            else linkMatcher.linkFileMatch!!.toRegex()

        val allMatchNonWiki =
            if (wantLooseMatch(options)) allMatchWiki
            else if (wantCompletionMatch(options)) allMatchWiki
            else if (linkMatcher.wikiMatchingRules) linkAllMatch.toRegex()
            else allMatchWiki

        val fixedPrefix = linkMatcher.fixedPrefix

        if (!linkMatcher.gitHubLinks) {
            val allExtensions =
                if (wantLooseMatch(options)) linkMatcher.linkLooseMatchExtensions
                else if (wantCompletionMatch(options)) linkMatcher.linkCompletionMatchExtensions
                else linkMatcher.linkAllMatchExtensions

            val rawGitHubLink = linkMatcher.gitHubLink == "raw"

            // see if we have cached files set
            if (fromList == null) {
                // NOTE: cannot use empty extension to search for directories since directories can have extensions
                val includeNoExtFiles = linkRef !is ImageLinkRef && linkRef !is WikiLinkRef
                val targetFileTypes = getTargetFileTypes(allExtensions, wantCompletionMatch(options), includeNoExtFiles)
                if (targetFileTypes.isEmpty() || project == null) {
                    // Only used in testing, runtime uses FileBasedIndex
                    if (project != null || allExtensions == null) return ArrayList(0)

                    // here extensions need to be expanded to include equivalent extensions as done at runtime using IDE code
                    val extensions = hashSetOf(*allExtensions.toTypedArray())

                    if (extensions.contains("md")) {
                        extensions.add("mkd")
                        extensions.add("markdown")
                    }

                    val fileTypes = extensions.toList()
                    val projectFileList = projectResolver.projectFileList(fileTypes)
                    if (projectFileList != null) {
                        for (fileRef in projectFileList) {
                            if (fileRef.filePath.startsWith(fixedPrefix) && fileRef.filePath.matches(if (fileRef.isWikiPage) allMatchWiki else allMatchNonWiki)) {
                                // here we need to test for wiki page links that resolve to raw files, these have to match case sensitive
                                if (allMatchNonWiki === allMatchWiki || !linkMatcher.wikiMatchingRules || !linkRef.hasExt || fileRef.filePath.matches(allMatchNonWiki)) {
                                    val newFileRef = if (rawGitHubLink) FileRef(fileRef) else fileRef
                                    if (rawGitHubLink) newFileRef.isRawFile = true
                                    matches.add(newFileRef)
                                }
                            }
                        }
                    }
                } else {
                    val projectScope = GlobalSearchScope.projectScope(project)
                    val matchPattern = if (allMatchNonWiki === allMatchWiki || !linkMatcher.wikiMatchingRules || !linkRef.hasExt) allMatchWiki else allMatchNonWiki
                    val fileNameNoDot = linkMatcher.fileName
                    val fileNameDot = linkMatcher.fileName + "."
                    var triedQuickMatch: Long = 0
                    var triedPrefixMatch: Long = 0
                    var triedMatch: Long = 0
                    for (type in targetFileTypes) {
                        FileTypeIndex.processFiles(type, { virtualFile ->
                            //println("checking file type: $type, path: ${virtualFile.path}")
                            val fileName = virtualFile.name
                            triedQuickMatch++
                            if (completionMatch || linkMatcher.wikiMatchingRules || fileName.length == fileNameNoDot.length && fileName == fileNameNoDot || fileName.length >= fileNameDot.length && fileName.startsWith(fileNameDot)) {
                                triedPrefixMatch++
                                if (virtualFile.path.startsWith(fixedPrefix)) {
                                    triedMatch++
                                    if (virtualFile.path.matches(matchPattern)) {
                                        val fileRef = ProjectFileRef(virtualFile, project)
                                        val newFileRef = if (rawGitHubLink) FileRef(fileRef) else fileRef
                                        if (rawGitHubLink) newFileRef.isRawFile = true
                                        matches.add(newFileRef)
                                    }
                                }
                            }
                            true
                        }, projectScope)

                        if (includeNoExtFiles && type == PlainTextFileType.INSTANCE) {
                            // #741, links
                            // add plain text marked files, these do not show up as original extension or as plain text indexed
                            val projectPlainTextFileTypeManager: ProjectPlainTextFileTypeManager? = ProjectPlainTextFileTypeManager.getInstance(project)
                            if (projectPlainTextFileTypeManager != null) {
                                for (virtualFile in projectPlainTextFileTypeManager.files) {
                                    val fileName = virtualFile.name
                                    triedQuickMatch++
                                    if (completionMatch || linkMatcher.wikiMatchingRules || fileName.length == fileNameNoDot.length && fileName == fileNameNoDot || fileName.length >= fileNameDot.length && fileName.startsWith(fileNameDot)) {
                                        triedPrefixMatch++
                                        if (virtualFile.path.startsWith(fixedPrefix)) {
                                            triedMatch++
                                            if (virtualFile.path.matches(matchPattern)) {
                                                val fileRef = ProjectFileRef(virtualFile, project)
                                                val newFileRef = if (rawGitHubLink) FileRef(fileRef) else fileRef
                                                if (rawGitHubLink) newFileRef.isRawFile = true
                                                matches.add(newFileRef)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // need to try all directories under the project tree and modules because without loose matching does not get any hits
                    if (includeDirsInCompletion && includeNoExtFiles && !rawGitHubLink) {
                        // create a list of directories by removing file name from collected files
                        val projectDirectories = MdLinkResolverManager.getInstance(project).getProjectDirectories()

                        // NOTE: the returned array has nulls so should be checked.
                        for (virtualFile: VirtualFile? in projectDirectories) {
                            virtualFile ?: continue

                            val fileName = virtualFile.name
                            triedQuickMatch++
                            if (completionMatch || linkMatcher.wikiMatchingRules || fileName.length == fileNameNoDot.length && fileName == fileNameNoDot || fileName.length >= fileNameDot.length && fileName.startsWith(fileNameDot)) {
                                triedPrefixMatch++
                                if (virtualFile.path.startsWith(fixedPrefix)) {
                                    triedMatch++
                                    if (virtualFile.path.matches(matchPattern)) {
                                        val fileRef = ProjectFileRef(virtualFile, project)
                                        if (!fileRef.isUnderWikiDir) {
                                            if (!matches.contains(fileRef)) {
                                                matches.add(fileRef)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // filter from a pre-created list, used to get looseMatch, then from that list get exact matches
                for (fileRef in fromList) {
                    // here we can have both fileRefs and linkRefs, we only handle fileRefs, linkRefs are silently dropped
                    if (fileRef is FileRef) {
                        if (fileRef.filePath.startsWith(fixedPrefix) && fileRef.filePath.matches(if (fileRef.isWikiPage) allMatchWiki else allMatchNonWiki)) {
                            // here we need to test for wiki page links that resolve to raw files, these have to match case sensitive
                            if (allMatchNonWiki === allMatchWiki || !linkMatcher.wikiMatchingRules || !fileRef.isWikiPage || !linkRef.hasExt || fileRef.filePath.matches(allMatchNonWiki)) {
                                // if isRawFile is set we cannot reuse it, since in our case it may no longer be raw access
                                if (fileRef.isRawFile == rawGitHubLink) matches.add(fileRef)
                                else if (!rawGitHubLink) matches.add(FileRef(fileRef))
                                else {
                                    val newFileRef = FileRef(fileRef)
                                    newFileRef.isRawFile = true
                                    matches.add(newFileRef)
                                }
                            }
                        }
                    }
                }
            }

            if (matches.size == 0 && linkRef.filePath.isEmpty()) {
                // add its own reference otherwise scratch file self ref link don't resolve because these files are not part of the project
                val fileRef = linkRef.containingFile
                matches.add(fileRef)
            }

            val rawWikiFileRefMatches = HashSet<FileRef>()

            // now we need to weed out the matches that will not work, unless this is a loose match
            if (linkMatcher.wikiMatchingRules) {
                // here some will be case sensitive some not,
                // anchor and ext based matches are to wiki pages
                if (linkRef is WikiLinkRef) {
                    // these match wiki pages, also have priority over the non-pages, ie. if Test.kt and Test.kt.md exists, then the .md will be matched first
                    // not case sensitive: linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"
                    // not case sensitive: linkSubAnchorExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
                    //val pageMatch = (linkMatcher.linkSubExtMatch + "|" + linkMatcher.linkSubAnchorExtMatch).toRegex(RegexOption.IGNORE_CASE)

                    // these match raw file content
                    // case sensitive: linkFileMatch = "^$fixedPrefix$filenamePattern$"
                    // case sensitive: linkFileAnchorMatch = "^$fixedPrefix$filenamePattern$anchorPattern$"
                    val fileOrAnchorMatch = (if (linkMatcher.linkFileAnchorMatch == null) linkMatcher.linkFileMatch else linkMatcher.linkFileMatch + "|" + linkMatcher.linkFileAnchorMatch)?.toRegex()
                    if (fileOrAnchorMatch != null) {
                        for (fileRef in matches) {
                            if (fileRef is FileRef && fileRef.filePath.matches(fileOrAnchorMatch)) {
                                rawWikiFileRefMatches.add(fileRef)
                                if (fileRef.isUnderWikiDir && !fileRef.isMarkdownExt) fileRef.isRawFile = true
                            }
                        }
                    }
                } else {
                    // these match wiki pages, also have priority over the non-pages, ie. if Test.kt and Test.kt.md exists, then the .md will be matched first
                    // not case sensitive: linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"

                    // these match raw file content and images
                    // case sensitive: linkFileMatch = "^$fixedPrefix$filenamePattern$"
                    val fileMatch = linkMatcher.linkFileMatch?.toRegex()
                    if (fileMatch != null) {
                        for (fileRef in matches) {
                            if (fileRef is FileRef) {
                                // it will be raw access if it is under the wiki directory  and has a 'real' extension
                                if (!fileRef.isWikiPageExt || fileRef.filePath.matches(fileMatch)) {
                                    rawWikiFileRefMatches.add(fileRef)
                                    if (fileRef.isUnderWikiDir && !fileRef.isMarkdownExt) fileRef.isRawFile = true
                                }
                            }
                        }
                    }
                }
            } else {
                // case sensitive: linkFileMatch = "^$fixedPrefix$filenamePattern$"
                // these are already set for raw if taken from the raw/ access URL and all are exact matches
                for (fileRef in matches) {
                    if (fileRef is FileRef) {
                        rawWikiFileRefMatches.add(fileRef)
                        if (fileRef.isUnderWikiDir && !fileRef.isMarkdownExt) fileRef.isRawFile = true
                    }
                }
            }

            if (linkRef is ImageLinkRef) {
                // have to remove all that will not resolve, unless loose matching
                val resolved = if (linkRefWasURI(options) || !linkRef.containingFile.isWikiPage) matches else MatchList()
                val unresolved = ArrayList<PathInfo>()
                if (!linkRefWasURI(options) && linkRef.containingFile.isWikiPage) {
                    for (it in matches) {
                        // if it is an image it should only resolve for raw
                        if (it is FileRef) {
                            val resolvedLinkAddress = linkAddress(linkRef, it, null, null, null, true)
                            if (linkRef.filePath.equals(resolvedLinkAddress, ignoreCase = true) || (linkRef.containingFile.isWikiHomePage && linkRef.filePath.equals("wiki/$resolvedLinkAddress", ignoreCase = true))) resolved.add(it)
                            else unresolved.add(it)
                        } else {
                            unresolved.add(it)
                        }
                    }
                }

                val linkFileMatchRegex = linkMatcher.linkFileMatch?.toRegex() ?: linkAllMatch.toRegex()
                resolved.sortWith(Comparator { self, other ->
                    val selfMatch = self.filePath.matches(linkFileMatchRegex)
                    val otherMatch = other.filePath.matches(linkFileMatchRegex)
                    if (selfMatch && !otherMatch) -1
                    else if (!selfMatch && otherMatch) 1
                    else self.compareTo(other)
                })

                if (wantLooseMatch(options) || wantCompletionMatch(options)) {
                    unresolved.sortWith(Comparator { self, other ->
                        val selfMatch = self.filePath.matches(linkFileMatchRegex)
                        val otherMatch = other.filePath.matches(linkFileMatchRegex)
                        if (selfMatch && !otherMatch) -1
                        else if (!selfMatch && otherMatch) 1
                        else self.compareTo(other)
                    })
                    matches = resolved
                    matches.addAll(unresolved)
                } else {
                    matches = resolved
                }
            } else if (matches.size > 1) {
                if (linkMatcher.wikiMatchingRules) {
                    matches.sortWith(Comparator { self, other ->
                        if (self is FileRef && other is FileRef) {
                            if (self in rawWikiFileRefMatches && other !in rawWikiFileRefMatches) 1
                            else if (self !in rawWikiFileRefMatches && other in rawWikiFileRefMatches) -1
                            else if (self in rawWikiFileRefMatches && other in rawWikiFileRefMatches) {
                                if (self.isWikiPageExt && !other.isWikiPageExt) -1
                                else if (!self.isWikiPageExt && other.isWikiPageExt) 1
                                else self.compareTo(other)
                            } else self.compareTo(other)
                        } else self.compareTo(other)
                    })
                } else {
                    matches.sortWith(Comparator { self, other ->
                        if (self is FileRef && other is FileRef) {
                            if (!self.isUnderWikiDir && other.isUnderWikiDir) -1
                            else if (self.isUnderWikiDir && !other.isUnderWikiDir) 1
                            else if (self in rawWikiFileRefMatches && other !in rawWikiFileRefMatches) 1
                            else if (self !in rawWikiFileRefMatches && other in rawWikiFileRefMatches) -1
                            else if (self in rawWikiFileRefMatches && other in rawWikiFileRefMatches) {
                                if (self.isWikiPageExt && !other.isWikiPageExt) -1
                                else if (!self.isWikiPageExt && other.isWikiPageExt) 1
                                else self.compareTo(other)
                            } else self.compareTo(other)
                        } else self.compareTo(other)
                    })
                }
            }

            // here we post process for other than just vanilla fileRef result type && filter out not accessible via repo relative links /
            if (!wantLocalREF(options) || !wantRemoteREF(options) || linkRefWasRepoRel(options)) {
                val postProcessedMatches = MatchList()
                val fileVcsRoot = projectResolver.getVcsRoot(linkRef.containingFile)

                for (fileRef in matches) {
                    if (linkRefWasRepoRel(options)) {
                        if (fileRef is FileRef) {
                            val targetVcsRoot = projectResolver.getVcsRoot(fileRef)
                            if (targetVcsRoot?.basePath != fileVcsRoot?.basePath) continue
                        }
                    }

                    val pathInfo = processMatchOptions(linkRef, fileRef, options)
                    if (pathInfo != null && !postProcessedMatches.contains(pathInfo)) {
                        postProcessedMatches.add(pathInfo)
                    }
                }

                matches = postProcessedMatches
            }
        }

        if (wantLinks(options) && linkRef !is WikiLinkRef && linkRef !is ImageLinkRef) {
            if (linkMatcher.gitHubLinks) {
                //  no need to check for links, the matcher has the link already set and we even pass all the stuff after the link

                val gitHubLinkRef =
                    when (wantLinksType(options)) {
                        Links.ABS -> LinkRef.parseLinkRef(linkRef.containingFile, linkMatcher.gitHubLinkWithParams.prefixWith('/'), null)
                        Links.REL -> {
                            val basePath = projectResolver.getVcsRoot(linkRef.containingFile)?.basePath
                            val containingFilePath =
                                if (linkRef.containingFile.isUnderWikiDir)
                                    if (linkRef.containingFile.isWikiHomePage) linkRef.path
                                    else linkRef.containingFile.filePath
                                else PathInfo.appendParts(linkRef.containingFile.path, "blob", "master", linkRef.containingFile.fileName).filePath

                            val linkAddress = PathInfo.relativePath(containingFilePath, basePath.suffixWith('/') + linkMatcher.gitHubLink, withPrefix = true, blobRawEqual = false)
                            LinkRef.parseLinkRef(linkRef.containingFile, linkAddress + linkMatcher.gitHubLinkParams, null)
                        }
                        Links.URL -> {
                            val remoteUrl = projectResolver.getVcsRoot(linkRef.containingFile)?.baseUrl
                            if (remoteUrl != null) {
                                LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl.suffixWith('/') + linkMatcher.gitHubLinkWithParams, null)
                            } else null
                        }
                        else ->
                            throw IllegalArgumentException("Want.Links can only be REL, ABS or URL got ${wantLinksType(options)}")
                    }
                if (gitHubLinkRef != null) matches.add(gitHubLinkRef)
            } else {
                if (!linkMatcher.isOnlyCompletionMatchValid && linkMatcher.effectiveExt.isNullOrEmpty()) {
                    val vcsRoot = projectResolver.getVcsRoot(linkRef.containingFile)
                    if (vcsRoot != null) {
                        val remoteUrl = vcsRoot.baseUrl
                        val basePath = vcsRoot.basePath.suffixWith('/')
                        val isWikiPage = linkRef.containingFile.isWikiPage
//                        val isWikiHomePage = linkRef.containingFile.isWikiHomePage

                        for (link in GITHUB_NON_FILE_LINKS) {
                            if ((basePath + link).matches(allMatchWiki)) {
                                val gitHubLinkRef =
                                    when (wantLinksType(options)) {
                                        Links.ABS -> if (isWikiPage) null else LinkRef.parseLinkRef(linkRef.containingFile, link.prefixWith('/'), null)
                                        Links.REL -> {
                                            if (isWikiPage)
                                                LinkRef(linkRef.containingFile, "../$link", null, null, false)
                                            else {
                                                val containingFilePath = PathInfo.appendParts(linkRef.containingFile.path, "blob", "master", linkRef.containingFile.fileName).filePath
                                                val linkAddress = PathInfo.relativePath(containingFilePath, basePath + link, withPrefix = true, blobRawEqual = false)
                                                LinkRef.parseLinkRef(linkRef.containingFile, linkAddress, null)
                                            }
                                        }
                                        Links.URL -> LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl.suffixWith('/') + link, null)
                                        else ->
                                            throw IllegalArgumentException("Want.Links can only be REL, ABS or URL got ${wantLinksType(options)}")
                                    }

                                if (gitHubLinkRef != null) matches.add(gitHubLinkRef)
                            }
                        }
                    }
                }
            }
        }

        return matches.getList()
    }

    fun fileRefAsURL(linkRef: LinkRef, targetRef: FileRef, vcsRoot: GitHubVcsRoot?, wantRaw: Boolean?): LinkRef? {
        val gitHubLink = if (wantRaw == true || (wantRaw == null && (linkRef is ImageLinkRef || targetRef.isRawFile))) "raw" else null

        val remoteUrl = vcsRoot?.urlForVcsRemote(targetRef, targetRef.isRawFile || wikiLinkHasRealExt(linkRef, targetRef), linkRef.anchor, branchOrTag, gitHubLink)
        if (remoteUrl != null) {
            // putting an if () in to select parameter crashes the compiler:
            // val urlRef = LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl, targetRef, if (linkRef is ImageLinkRef) ::ImageLinkRef else ::LinkRef)
            val urlRef =
                if (linkRef is ImageLinkRef) LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl, targetRef, ::ImageLinkRef)
                else LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl, targetRef, ::LinkRef)
            assert(urlRef.isExternal) { "expected to get URL, instead got $urlRef" }
            return urlRef
        } else {
            return null
        }
    }

    fun fileRefAsURI(linkRef: LinkRef, targetRef: PathInfo): LinkRef {
        val urlEncoded = urlEncode(targetRef.filePath)
        // windows path needs file:/ the rest file://
        val fullPath = PathInfo.prefixWithFileURI(urlEncoded)
        return if (targetRef is FileRef) linkRef.replaceFilePath(fullPath, targetRef) else linkRef.replaceFilePath(fullPath, true)
    }

    fun fileRefAsABS(linkRef: LinkRef, targetRef: FileRef, vcsRoot: GitHubVcsRoot?): LinkRef? {
        return if (linkRef.containingFile.isUnderWikiDir) {
            null
        } else {
            val containingFileVcsRoot = projectResolver.getVcsRoot(linkRef.containingFile)
            if (vcsRoot?.basePath != containingFileVcsRoot?.basePath || containingFileVcsRoot?.basePath == null) {
                if (vcsRoot == null) {
                    if (containingFileVcsRoot != null) null
                    else {
                        // the path is relative to project or null
                        if (targetRef.filePath.startsWith(projectBasePath.suffixWith('/'), ignoreCase = true)) {
                            val result = linkRef.replaceFilePath(PathInfo.relativePath(projectBasePath.suffixWith('/'), targetRef.filePath, false, false).prefixWith('/'), targetRef, false)
                            result
                        } else null
                    }
                } else {
                    null
                }
            } else {
                val relativePath = vcsRoot?.rootRelativeForVcsRemote(
                    targetRef,
                    targetRef.isRawFile || wikiLinkHasRealExt(linkRef, targetRef),
                    linkRef.anchor,
                    branchOrTag,
                    if (linkRef is ImageLinkRef || targetRef.isRawFile) "raw" else null
                )

                if (relativePath != null && !relativePath.startsWith("../")) {
                    // putting an if () in to select parameter crashes the compiler:
                    // val urlRef = LinkRef.parseLinkRef(linkRef.containingFile, relativePath, targetRef, if (linkRef is ImageLinkRef) ::ImageLinkRef else ::LinkRef)
                    val fullPath = relativePath.prefixWith('/')
                    val urlRef =
                        if (linkRef is ImageLinkRef) LinkRef.parseLinkRef(linkRef.containingFile, fullPath, targetRef, ::ImageLinkRef)
                        else LinkRef.parseLinkRef(linkRef.containingFile, fullPath, targetRef, ::LinkRef)
                    assert(urlRef.isLocal && urlRef.isAbsolute) { "expected to get vcsRoot Relative for ABS, instead got $urlRef" }
                    urlRef
                } else {
                    null
                }
            }
        }
    }

    fun processMatchOptions(linkRef: LinkRef, targetRef: PathInfo, options: Int): PathInfo? {
        if (targetRef is FileRef) {
            val vcsRoot: GitHubVcsRoot? = projectResolver.getVcsRoot(targetRef)
            val isUnderVcs: Boolean = vcsRoot.ifNotNull { projectResolver.isUnderVcs(targetRef) } ?: false

            if (vcsRoot != null && isUnderVcs) {
                // it is a remote reference
                return when (wantRemoteType(options)) {
                    Remote.NONE -> null
                    Remote.URI -> fileRefAsURI(linkRef, targetRef)
                    Remote.URL -> fileRefAsURL(linkRef, targetRef, vcsRoot, null)
                    Remote.RAW -> fileRefAsURL(linkRef, targetRef, vcsRoot, true)
                    Remote.ABS -> fileRefAsABS(linkRef, targetRef, vcsRoot)
                    Remote.REL -> {
                        val newLinkRef = linkRef(linkRef, targetRef, null, null, null)
                        if (PathInfo.isRelative(newLinkRef.filePath)) newLinkRef else null
                    }
                    else -> {
                        assert(wantRemoteType(options) == Remote.REF) { "Not all RemoteTypes are handled, expected Remote.REF got ${wantRemoteType(options).testData()}" }
                        targetRef
                    }
                }
            } else {
                // local file
                return when (wantLocalType(options)) {
                    Local.NONE -> null
                    Local.URI -> fileRefAsURI(linkRef, targetRef)
                    Local.URL -> fileRefAsURL(linkRef, targetRef, vcsRoot, null)
                    Local.RAW -> fileRefAsURL(linkRef, targetRef, vcsRoot, true)
                    Local.ABS -> fileRefAsABS(linkRef, targetRef, vcsRoot)
                    Local.REL -> {
                        val newLinkRef = linkRef(linkRef, targetRef, null, null, null)
                        if (PathInfo.isRelative(newLinkRef.filePath)) newLinkRef else null
                    }
                    else -> {
                        assert(wantLocalType(options) == Local.REF) { "Not all LocalTypes are handled, expected Local.REF got ${wantLocalType(options).testData()}" }
                        targetRef
                    }
                }
            }
        } else if (targetRef is LinkRef && targetRef.isExternal) {
            // see if has the right format
            val vcsRoot = projectResolver.getVcsRootForUrl(targetRef.filePath)
            if (vcsRoot?.baseUrl != null) {
                val linkPath = targetRef.filePath.substring(vcsRoot.baseUrl.length)
                if (linkPath in GITHUB_NON_FILE_LINKS) {
                    val linksType = wantLinksType(options)
                    if (linksType == Links.URL || linksType == Links.ABS) {
                        return linkRef
                    }
                }
            }
        }
        return null
    }

    fun logicalRemotePath(fileRef: FileRef, useWikiPageActualLocation: Boolean, isSourceRef: Boolean, isImageLinkRef: Boolean, branchOrTag: String?): PathInfo {
        val filePathInfo: PathInfo

        if (fileRef.isUnderWikiDir) {
            if (useWikiPageActualLocation && !isSourceRef) filePathInfo = PathInfo(fileRef.path)
            // GitHub Wiki Home Change: No longer true
            //else if (fileRef.isWikiHomePage && isSourceRef && isImageLinkRef) filePathInfo = PathInfo.appendParts(fullPath = fileRef.wikiDir, parts = "..")
            else filePathInfo = PathInfo(fileRef.wikiDir)
        } else {
            val gitHubVcsRoot = projectResolver.getVcsRoot(fileRef)
            if (fileRef.filePath.startsWith(projectBasePath.suffixWith('/'))) {
                val vcsMainRepoBase = (gitHubVcsRoot?.mainRepoBaseDir ?: projectBasePath).suffixWith('/')
                filePathInfo = PathInfo(vcsMainRepoBase + (if (isImageLinkRef || fileRef.isRawFile) "raw/" else "blob/") + (branchOrTag
                    ?: "master").suffixWith('/') + PathInfo.relativePath(vcsMainRepoBase, fileRef.path, withPrefix = false, blobRawEqual = false))
            } else {
                filePathInfo = PathInfo(fileRef.path)
            }
        }
        return filePathInfo
    }

    override fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String?): String? {
        assertContainingFile(linkRef)

        val containingFilePath = logicalRemotePath(containingFile, useWikiPageActualLocation = false, isSourceRef = true, isImageLinkRef = linkRef is ImageLinkRef, branchOrTag = branchOrTag).filePath.suffixWith('/')
        val targetFilePath = logicalRemotePath(targetRef, useWikiPageActualLocation = withExtForWikiPage, isSourceRef = false, isImageLinkRef = linkRef is ImageLinkRef, branchOrTag = branchOrTag).filePath.suffixWith('/')

        val containingFileGitHubVcsRoot = projectResolver.getVcsRoot(containingFile)
        val targetFileGitHubVcsRoot = projectResolver.getVcsRoot(targetRef)

        // if not under VCS and both roots are null then get relative path also
        if (containingFileGitHubVcsRoot == null && targetFileGitHubVcsRoot == null || targetFileGitHubVcsRoot != null && containingFileGitHubVcsRoot != null && (containingFileGitHubVcsRoot.mainRepoBaseDir == targetFileGitHubVcsRoot.mainRepoBaseDir)) {
            val relativePath = PathInfo.relativePath(containingFilePath, targetFilePath, withPrefix = true, blobRawEqual = true)
            return relativePath
        } else {
            return null
        }
    }

    fun linkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val linkRef = LinkRef(containingFile, targetRef.fileNameNoExt, anchor, null, false)
        return linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor, true)
    }

    @Suppress("UNUSED_PARAMETER")
    fun wikiLinkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val fileRef = when (targetRef) {
            is FileRef -> if (project != null) targetRef.projectFileRef(project) else null
            is LinkRef -> targetRef.targetRef
            else -> null
        }

        val fullPath = if (targetRef.isWikiPageExt && withExtForWikiPage != true) targetRef.fileNameNoExt else targetRef.fileName
        val wikiLinkRef = WikiLinkRef(containingFile, LinkRef.urlDecode(fullPath), anchor, targetRef as? FileRef, false)

        if (fileRef?.isUnderWikiDir != true) {
            return ""
        }
        return wikiLinkRef.filePathWithAnchor
    }

    fun imageLinkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val linkRef = ImageLinkRef(containingFile, targetRef.fileNameNoExt, anchor, null, false)
        return linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor, true)
    }

    fun linkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): LinkRef {
        return LinkRef.parseLinkRef(containingFile, linkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::LinkRef)
    }

    fun wikiLinkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): WikiLinkRef {
        return LinkRef.parseLinkRef(containingFile, wikiLinkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::WikiLinkRef) as WikiLinkRef
    }

    fun imageLinkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): ImageLinkRef {
        return LinkRef.parseLinkRef(containingFile, imageLinkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::ImageLinkRef) as ImageLinkRef
    }

    fun linkRef(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean?, branchOrTag: String?, anchor: String?): LinkRef {
        assert(linkRef.isNormalized)

        return when (linkRef) {
            is WikiLinkRef -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor, true), targetRef as? FileRef, ::WikiLinkRef)
            is ImageLinkRef -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor, true), targetRef as? FileRef, ::ImageLinkRef)
            else -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor, true), targetRef as? FileRef, ::LinkRef)
        }
    }

    fun wikiLinkHasRealExt(linkRef: LinkRef, targetRef: PathInfo): Boolean {
        return linkRef.hasExt && linkRef.fileNameNoExt.equals(linkRef.fileToLink(targetRef.fileNameNoExt, linkEncodingExclusionMap), ignoreCase = true)
    }

    @Suppress("NAME_SHADOWING")
    override fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean?, branchOrTag: String?, anchor: String?, reduceToAnchor: Boolean): String {
        assertContainingFile(linkRef)
        val normLinkRef = normalizedLinkRef(linkRef)

        // need  to make sure that the extension in the link is a real extension and not part of the file name, otherwise it will add the .md extension erroneously
        val withExtForWikiPage = withExtForWikiPage ?: wikiLinkHasRealExt(normLinkRef, targetRef)

        if (targetRef is FileRef) {
            var prefix = relativePath(normLinkRef, targetRef, withExtForWikiPage || normLinkRef is ImageLinkRef, branchOrTag)

            if (prefix == null) {
                // only full address will work or relative to containing file if in a module
                if (project != null) {
                    val virtualFile = containingFile.virtualFile
                    val virtualFile1 = targetRef.virtualFile
                    if (virtualFile != null && virtualFile1 != null) {
                        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
                        val module = fileIndex.getModuleForFile(virtualFile)
                        val anchorText = if (anchor == null || anchor.isEmpty()) "" else if (anchor[0] == '#') anchor else "#${LinkRef.urlEncode(anchor, null)}"
                        if (module != null) {
                            // if target ref in module under module root dir then it can be relative
                            val path = PathInfo(module.moduleFilePath).path
                            val projectComponent = MdLinkResolverManager.getInstance(project)
                            val targetVcsRoot = projectComponent.getVcsRoot(targetRef)
                            val containingVcsRoot = projectComponent.getVcsRoot(containingFile)

                            if (targetVcsRoot?.mainRepoBaseDir == containingVcsRoot?.mainRepoBaseDir) {
                                if (virtualFile.path.startsWith(path) && virtualFile1.path.startsWith(path)) {
                                    return urlEncode(PathInfo.relativePath(containingFile.filePath, targetRef.filePath, blobRawEqual = true)) + anchorText
                                }

                                val scope = module.moduleScope
                                if (MdPsiImplUtil.inScope(scope, virtualFile1)) {
                                    return urlEncode(PathInfo.relativePath(containingFile.filePath, targetRef.filePath, blobRawEqual = true)) + anchorText
                                }
                            }
                        }

                        // in this case it takes a full path so we change it to file://
                        if (targetRef.isURI) {
                            return targetRef.filePath + anchorText
                        } else {
                            return "file://" + urlEncode(targetRef.filePath) + anchorText
                        }
                    }
                }
                return targetRef.filePath
            } else {
                if (normLinkRef is WikiLinkRef) {
                    return prefix.suffixWith('/') + normLinkRef.fileToLink(if (withExtForWikiPage) targetRef.fileName else targetRef.fileNameNoExt, linkEncodingExclusionMap) + (anchor
                        ?: if (wasAnchorUsedInMatch(normLinkRef, targetRef)) "" else normLinkRef.anchor).prefixWith("#")
                } else {
                    if (prefix.isNotEmpty() && targetRef.isUnderWikiDir) {
                        // if the prefix starts with the wiki dir change it to the generic wiki used in links
                        val wikiDirName = targetRef.wikiDir.substring(targetRef.mainRepoDir.length + 1).suffixWith('/')
                        if (containingFile.isUnderWikiDir && prefix.startsWith(wikiDirName)) prefix = "wiki/" + prefix.substring(wikiDirName.length)
                        else if (!containingFile.isUnderWikiDir) {
                            val vcsRoot = projectResolver.getVcsRoot(containingFile)
                            val repoBasePath = vcsRoot?.basePath ?: projectBasePath
                            val backDirsToRepoRoot = PathInfo.relativePath(containingFile.path, repoBasePath, withPrefix = true, blobRawEqual = false)
                            val prefixWithWikiDirName = "$backDirsToRepoRoot../../$wikiDirName"
                            if (prefix.startsWith(prefixWithWikiDirName)) prefix = backDirsToRepoRoot + "../../wiki/" + prefix.substring(prefixWithWikiDirName.length)
                        }
                    }

                    val selfRef = isSelfRef(normLinkRef, targetRef, withExtForWikiPage)
                    val optimizedAnchor = (anchor ?: optimizedLinkAnchor(normLinkRef, targetRef, withExtForWikiPage)).prefixWith('#')

                    if (targetRef.isWikiPage) {
                        if (selfRef && reduceToAnchor) return optimizedAnchor
                        else {
                            val fileName = prefix.suffixWith('/') + if (!withExtForWikiPage) (if (targetRef.isWikiHomePage) (if (linkRef.containingFile.isWikiPage) "Home" else "") else targetRef.fileNameNoExt) else targetRef.fileName
                            return normLinkRef.fileToLink(fileName, linkEncodingExclusionMap).removeSuffix("/") + optimizedAnchor
                        }
                    } else {
                        if (selfRef && reduceToAnchor) return optimizedAnchor.ifEmpty(targetRef.fileName)
                        else return normLinkRef.fileToLink(prefix.suffixWith('/') + targetRef.fileName, linkEncodingExclusionMap) + optimizedAnchor
                    }
                }
            }
        } else if (targetRef.isURI) {
            // convert git hub links to relative links
            val vcsRoot = projectResolver.getVcsRoot(normLinkRef.containingFile)
            val remoteUrl = vcsRoot?.baseUrl
            val repoBasePath = vcsRoot?.basePath

            if (remoteUrl != null && repoBasePath != null) {
                assert(remoteUrl.startsWith("http://", "https://")) { "remote vcsRepoBase has to start with http:// or https://, instead got $remoteUrl" }

                if (targetRef.path.startsWith(remoteUrl.suffixWith('/'))) {
                    val fileName = targetRef.filePath.substring(remoteUrl.suffixWith('/').length)
                    if (fileName in GITHUB_NON_FILE_LINKS) {
                        return when {
                            containingFile.isWikiHomePage || containingFile.isUnderWikiDir -> "../$fileName"
                            else -> PathInfo.relativePath(containingFile.path, repoBasePath.suffixWith('/'), withPrefix = true, blobRawEqual = false) + "../../" + fileName
                        }
                    } else {
                        if (fileName.startsWith("wiki/")) {
                            // trying for wiki page
                            val filePath = when {
                                // GitHub Wiki Home Change: No longer true
                                //containingFile.isWikiHomePage && normLinkRef is ImageLinkRef -> fileName
                                containingFile.isWikiHomePage || containingFile.isUnderWikiDir -> fileName.substring("wiki/".length)
                                else -> PathInfo.relativePath(containingFile.path, repoBasePath.suffixWith('/'), withPrefix = true, blobRawEqual = false).suffixWith("/") + "../../" + fileName
                            }
                            return filePath
                        } else {
                            // main repo file, if it starts with blob/something/ or raw/something then we can handle it
                            val repoPrefixPathPattern = ("^([^/]+)\\Q/\\E([^/]+)\\Q/\\E").toRegex()
                            if (fileName.matches(repoPrefixPathPattern)) {
                                val match = repoPrefixPathPattern.find(fileName)
                                if (match != null) {
                                    val oldGitHubLink = match.groups[0]
                                    val oldBranchOrTag = match.groups[1]
                                    // we throw out the branch if one is given to us or if linking from another file in the repo, its branch or tag will be used by GitHub
                                    val fileNamePart = fileName.substring(match.range.endInclusive + 1)
                                    val filePath = when {
                                        // GitHub Wiki Home Change: No longer true
                                        //containingFile.isWikiHomePage -> "$oldGitHubLink/${branchOrTag ?: oldBranchOrTag ?: "master"}/" + fileNamePart
                                        containingFile.isWikiHomePage || containingFile.isUnderWikiDir -> "../$oldGitHubLink/${branchOrTag
                                            ?: oldBranchOrTag ?: "master"}/" + fileNamePart
                                        else -> PathInfo.relativePath(containingFile.path, repoBasePath.suffixWith('/'), withPrefix = true, blobRawEqual = false).suffixWith("/") + fileNamePart
                                    }
                                    return filePath
                                }
                            }
                        }
                    }
                }
            }
        }
        return ""
    }

    fun urlEncode(url: String): String {
        return LinkRef.urlEncode(url, linkEncodingExclusionMap)
    }

    fun absoluteToRelativeLink(linkRef: LinkRef): LinkRef {
        val normLinkRef = normalizedLinkRef(linkRef)
        if (!normLinkRef.isURI && !normLinkRef.isAbsolute) return normLinkRef

        if (normLinkRef.isLocal) {
            if (!normLinkRef.isURI) {
                val vcsRoot = projectResolver.getVcsRoot(linkRef.containingFile)

                // need to suffix projectBasePath with /, vcsRoot does that
                val baseDir = vcsRoot?.fileProjectBaseDirectory(linkRef.containingFile) ?: projectBasePath.suffixWith("/")
                if (!linkRef.containingFile.filePath.startsWith(baseDir)) {
                    return normLinkRef
                }
                val containingFileRelPath = linkRef.containingFile.filePath.substring(baseDir.length)
                val subDirs = containingFileRelPath.count('/')

                // we add .. for every subDir the file is under its base directory so that relative resolves to the right baseDir
                val fullPath = "../".repeat(subDirs) + linkRef.filePath.substring(1)
                val relRef = linkRef.replaceFilePath(fullPath, linkRef.isNormalized)
                return relRef
            } else {
                val targetRef = FileRef(LinkRef.urlDecode(normLinkRef.filePath.removeAnyPrefix("file://", "file:/")))
                val containingFileVcsRoot = projectResolver.getVcsRoot(linkRef.containingFile)
                val targetFileVcsRoot = projectResolver.getVcsRoot(targetRef)

                if (containingFileVcsRoot === targetFileVcsRoot && targetFileVcsRoot != null) {
                    val linkAddress = linkAddress(normLinkRef, targetRef, null, null, "", true)
                    // GitHub Wiki Home Change: No longer true
                    // if (containingFile.isWikiHomePage && linkAddress.startsWith("../")) {
                    //   linkAddress = linkAddress.removePrefix("../")
                    // }
                    val relRefNoAnchor = normLinkRef.replaceFilePath(linkAddress, true)
                    val relRef = normLinkRef.replaceFilePathAndAnchor(relRefNoAnchor.filePath, true, normLinkRef.anchor, linkRef.isNormalized)
                    return relRef
                } else {
                    // keep file reference
                    val relLink = normLinkRef.replaceFilePath(targetRef.filePath, false, linkRef.isNormalized)
                    return relLink
                }
            }
        } else if (normLinkRef.isExternal) {
            // search for VCS root based on the URL!!!
            val gitHubVcsRoot = projectResolver.getVcsRootForUrl(normLinkRef.filePath)
            val containingVcsRoot = projectResolver.getVcsRoot(containingFile)
            if (gitHubVcsRoot != null && containingVcsRoot != null && gitHubVcsRoot.mainRepoBaseDir == containingVcsRoot.mainRepoBaseDir) {
                val gitHubRepoBaseUrl = gitHubVcsRoot.baseUrl
                if (gitHubRepoBaseUrl != null && normLinkRef.filePath.startsWith(gitHubRepoBaseUrl)) {
                    val linkToFile = normLinkRef.linkToFile(normLinkRef.filePath.substring(gitHubRepoBaseUrl.suffixWith('/').length))

                    if (linkToFile.startsWith("blob/") || linkToFile.startsWith("raw/")) {
                        val targetFilePath = gitHubVcsRoot.mainRepoBaseDir.suffixWith('/') + linkToFile
                        val containingFilePath = logicalRemotePath(containingFile, useWikiPageActualLocation = false, isSourceRef = true, isImageLinkRef = normLinkRef is ImageLinkRef, branchOrTag = null).filePath.suffixWith('/')
                        val containingFileVcsRoot = projectResolver.getVcsRoot(linkRef.containingFile)
                        var fullPath: String
                        if (containingFileVcsRoot === gitHubVcsRoot) {
                            fullPath = PathInfo.relativePath(containingFilePath, targetFilePath, blobRawEqual = true)
                            // GitHub Wiki Home Change: No longer true
                            //if (containingFile.isWikiHomePage && fullPath.startsWith("../")) {
                            //    fullPath = fullPath.removePrefix("../")
                            //}
                            val relLink = normLinkRef.replaceFilePath(fullPath, false, linkRef.isNormalized)
                            return relLink
                        } else {
                            fullPath = PathInfo.relativePath(gitHubVcsRoot.basePath, targetFilePath, blobRawEqual = true)
                            fullPath = fullPath.removePrefix("../")
                            fullPath = fullPath.removeAnyPrefix("blob/", "raw/")
                            // remove the next path part which is branch
                            fullPath = fullPath.removePrefixIncluding("/")

                            // now add the vcs base root
                            fullPath = gitHubVcsRoot.basePath.suffixWith('/') + fullPath
                            val relLink = normLinkRef.replaceFilePath(fullPath, false, linkRef.isNormalized)
                            return relLink
                        }
                    } else if (linkToFile.startsWith("wiki/") || linkToFile == "wiki" || linkToFile == "wiki/") {
                        val isWikiHome = linkToFile == "wiki" || linkToFile == "wiki/"
                        val targetFilePath =
                            if (isWikiHome) gitHubVcsRoot.mainRepoBaseDir.suffixWith('/') + "wiki/Home"
                            else gitHubVcsRoot.mainRepoBaseDir.suffixWith('/') + linkToFile
                        val containingFilePath = logicalRemotePath(containingFile, useWikiPageActualLocation = false, isSourceRef = true, isImageLinkRef = normLinkRef is ImageLinkRef, branchOrTag = null).filePath.suffixWith('/')
                        val fullPath = PathInfo.relativePath(containingFilePath, targetFilePath, blobRawEqual = true)
                        val relLink = normLinkRef.replaceFilePath(fullPath, false, linkRef.isNormalized)
                        return relLink
                    } else if (linkToFile.startsWith("issues/") || linkToFile == "issues" || linkToFile == "issues/") {
                        val targetFilePath = gitHubVcsRoot.mainRepoBaseDir.suffixWith('/') + linkToFile
                        val containingFilePath = logicalRemotePath(containingFile, useWikiPageActualLocation = false, isSourceRef = true, isImageLinkRef = normLinkRef is ImageLinkRef, branchOrTag = null).filePath.suffixWith('/')
//                        val containingFileVcsRoot = projectResolver.getVcsRoot(linkRef.containingFile)
                        val fullPath = PathInfo.relativePath(containingFilePath, targetFilePath, blobRawEqual = false)
                        return normLinkRef.replaceFilePath(fullPath, false, linkRef.isNormalized)
                    }
                }
            }
        }
        return linkRef
    }

    fun denormalizedLinkRef(linkRef: LinkRef): LinkRef {
        var mappedLinkRef = linkRef
        // from "https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_source_preview.png"
        // to "https://github.com/vsch/idea-multimarkdown/raw/master/assets/images/ScreenShot_source_preview.png"
        // from "https://raw.githubusercontent.com/wiki/vsch/idea-multimarkdown/img/ScreenShot_source_preview_Large.png?token=AJ0mzve3jxMArvfYq7nKkL1ZaYZbPVxXks5Was-1wA%3D%3D"
        // to "https://github.com/vsch/idea-multimarkdown/wiki/img/ScreenShot_source_preview.png"
        val linkRefText = denormalizedLinkRef(mappedLinkRef.filePath)

        if (linkRefText !== mappedLinkRef.filePath) {
            // was mapped
            mappedLinkRef = mappedLinkRef.replaceFilePath(linkRefText, true, false)
        }

        return mappedLinkRef
    }

    fun denormalizedLinkRef(linkRefText: String): String {
        for (provider in MdLinkMapProvider.EXTENSIONS.value) {
            val url = provider.mapLinkRef(linkRefText, renderingProfile)
            if (url != null) return url
        }
        return linkRefText;
    }

    fun normalizedLinkRef(linkRef: LinkRef): LinkRef {
        if (linkRef.isNormalized) return linkRef

        var mappedLinkRef = linkRef
        // from "https://raw.githubusercontent.com/vsch/idea-multimarkdown/master/assets/images/ScreenShot_source_preview.png"
        // to "https://github.com/vsch/idea-multimarkdown/raw/master/assets/images/ScreenShot_source_preview.png"
        // from "https://raw.githubusercontent.com/wiki/vsch/idea-multimarkdown/img/ScreenShot_source_preview_Large.png?token=AJ0mzve3jxMArvfYq7nKkL1ZaYZbPVxXks5Was-1wA%3D%3D"
        // to "https://github.com/vsch/idea-multimarkdown/wiki/img/ScreenShot_source_preview.png"
        val linkRefText = if (project == null) mappedLinkRef.filePath else {
            var text: String? = null
            val unescapeString = Escaping.unescapeString(mappedLinkRef.filePath)
            for (provider in MdLinkMapProvider.EXTENSIONS.value) {
                text = provider.mapLinkText(unescapeString, renderingProfile)
                if (text != null) break
            }
            text ?: unescapeString
        }
        if (linkRefText !== mappedLinkRef.filePath) {
            // was mapped
            mappedLinkRef = mappedLinkRef.replaceFilePath(linkRefText, true, true)
        }

        if (mappedLinkRef.filePath.startsWith("https://raw.githubusercontent.com/") || mappedLinkRef.filePath.startsWith("http://raw.githubusercontent.com/")) {
            val noRawContentPrefix = mappedLinkRef.filePath.removeAnyPrefix("https://raw.githubusercontent.com/", "http://raw.githubusercontent.com/")
            val linkParts = noRawContentPrefix.split("?", limit = 2)[0].split("/")

            if (linkParts.size >= 3 && (linkParts[0] != "wiki" || linkParts.size >= 4)) {
                val normLinkParts: List<String>

                if (linkParts[0] == "wiki") {
                    // we have a user and repo match
                    normLinkParts = arrayListOf(linkParts[1], linkParts[2], "wiki", *linkParts.subList(3, linkParts.size).toTypedArray())
                } else {
                    // we have a user and repo match
                    normLinkParts = arrayListOf(linkParts[0], linkParts[1], "raw", *linkParts.subList(2, linkParts.size).toTypedArray())
                }

                val changedUrlPath = PathInfo.appendParts("https://github.com", normLinkParts).filePath
                val normalizedLinkRef = mappedLinkRef.replaceFilePath(changedUrlPath, true, true)
                return normalizedLinkRef
            }
        }

        if (!mappedLinkRef.isNormalized) {
            // make a mapped version
            val normalized = mappedLinkRef.replaceFilePath(mappedLinkRef.filePath, true, true)
            return normalized
        }

        return mappedLinkRef
    }

    private fun assertContainingFile(linkRef: LinkRef) {
        assert(linkRef.containingFile.compareTo(containingFile) == 0) { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" }
    }

    private fun assertContainingFile(fileRef: FileRef) {
        assert(fileRef.compareTo(containingFile) == 0) { "fileRef differs from LinkResolver containingFile, need new Resolver for each containing file" }
    }

    fun optimizedLinkAnchor(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean): String {
        val anchorUsedInMatch = wasAnchorUsedInMatch(linkRef, targetRef)
        val selfRef = isSelfRef(linkRef, targetRef, withExtForWikiPage)

        return if (anchorUsedInMatch)
            (if (selfRef) "#" else "")
        else
            (if (selfRef) "#" + linkRef.anchor.orEmpty() else linkRef.anchorText)
    }

    fun isSelfRef(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean): Boolean {
        return if ((targetRef.isWikiPage && withExtForWikiPage) || (!targetRef.isWikiPage && linkRef.hasExt && !linkRef.isImageExt)) linkRef.containingFile.filePathNoExt == targetRef.filePathNoExt
        else linkRef.containingFile.filePath == targetRef.filePath
    }

    fun wasAnchorUsedInMatch(linkRef: LinkRef, targetRef: PathInfo): Boolean {
        return linkRef.hasAnchor && (linkRef.linkToFile(linkRef.fileName) != targetRef.fileName && targetRef.fileName.endsWith(linkRef.anchorText)
            || linkRef.linkToFile(linkRef.fileNameNoExt) != targetRef.fileNameNoExt && targetRef.fileNameNoExt.endsWith(linkRef.anchorText))
    }

    fun equalLinks(fileName: String, wikiLinkAddress: String, ignoreCase: Boolean = true): Boolean {
        return WikiLinkRef.fileAsLink(fileName).equals(WikiLinkRef.fileAsLink(wikiLinkAddress), ignoreCase)
    }
}

