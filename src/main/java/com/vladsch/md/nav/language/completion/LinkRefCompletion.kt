// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.language.api.MdLinkRefCompletionExtension
import com.vladsch.md.nav.language.completion.util.WrappingDecorator
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.element.MdReferenceImageImpl
import com.vladsch.md.nav.psi.element.MdReferenceImpl
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes.AUTO_LINK_REF
import com.vladsch.md.nav.psi.util.MdTypes.IMAGE_LINK_REF
import com.vladsch.md.nav.psi.util.MdTypes.JEKYLL_INCLUDE_TAG_LINK_REF
import com.vladsch.md.nav.psi.util.MdTypes.LINK_REF
import com.vladsch.md.nav.psi.util.MdTypes.REFERENCE_LINK_REF
import com.vladsch.md.nav.psi.util.MdTypes.WIKI_LINK_REF
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.settings.ParserOptions
import com.vladsch.md.nav.util.*
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.TestUtils
import com.vladsch.plugin.util.debug
import com.vladsch.plugin.util.suffixWith
import com.vladsch.plugin.util.toBased
import com.vladsch.plugin.util.ui.Helpers
import icons.MdIcons
import javax.swing.Icon

class LinkRefCompletion : MdElementCompletion {
    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.language.completion")
        private val itemLogger = Logger.getInstance("com.vladsch.md.nav.language.completion.link-ref-items")
    }

    override fun getWantElement(element: PsiElement, elementPos: PsiElement, parameters: CompletionParameters, context: ProcessingContext): Boolean {
        val elementType = element.node.elementType
        return elementType === IMAGE_LINK_REF || elementType === JEKYLL_INCLUDE_TAG_LINK_REF || elementType === AUTO_LINK_REF || elementType === LINK_REF || elementType === WIKI_LINK_REF || elementType === REFERENCE_LINK_REF
        //|| elementType === IMAGE_LINK_REF_CLOSE || elementType === LINK_REF_CLOSE || elementType === WIKI_LINK_CLOSE || elementType === REFERENCE_LINK_REFERENCE_CLOSE
    }

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet, element: PsiElement, containingFile: MdFile): Boolean {
        val elementType = element.node.elementType

        // FIX: clean up this hack-ball, the information is available from parameters.
        val linkRefText = MdPsiImplUtil.getLinkRefText(element)
        val containingFileRef = ProjectFileRef(containingFile)
        val resolver = GitHubLinkResolver(containingFileRef)
        val errorColor = Helpers.errorColor()

        // see if it is issues completion
        val linkRefCleanText = linkRefText.replace(TestUtils.DUMMY_IDENTIFIER, "")
        if (elementType === LINK_REF || elementType === REFERENCE_LINK_REF) {
            val textWithPos = element.text
            val cursorPos = textWithPos.indexOf(TestUtils.DUMMY_IDENTIFIER)
            if (cursorPos >= 0) {
                val beforeCursor = textWithPos.substring(0, cursorPos).toBased().trimEnd(CharPredicate.DECIMAL_DIGITS).toString()

                for (extension in MdLinkRefCompletionExtension.EXTENSIONS.value) {
                    if (extension.overrideCompletion(beforeCursor, parameters, context, resultSet, element, containingFile)) {
                        return true
                    }
                }
            }
        }

        if (linkRefCleanText == ".") return true

        var linksType = if (elementType !== WIKI_LINK_REF) Links.ABS else Links.NONE
        var localType = if (elementType !== WIKI_LINK_REF) Local.ABS else Local.REL
        var remoteType = if (elementType !== WIKI_LINK_REF) Remote.ABS else Remote.REL
        val linkRef: LinkRef
        var fullPath: String
        var uriPrefix = ""
        var userPrefix = ""
        var linksPrefix = ""
        var fixedIncludePath = false

        val shortcut = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction(IdeActions.ACTION_CODE_COMPLETION))
        if (shortcut.isNotEmpty()) {
            if (elementType != WIKI_LINK_REF) {
                resultSet.addLookupAdvertisement(MdBundle.message("completions.linkref.advertise.next.0.invocation", shortcut))
            } else if (parameters.invocationCount > 1) {
                if (parameters.invocationCount > 2) {
                    resultSet.addLookupAdvertisement(MdBundle.message("completions.wikilinkref.advertise.next.0.invocation2", shortcut))
                } else {
                    resultSet.addLookupAdvertisement(MdBundle.message("completions.wikilinkref.advertise.next.0.invocation", shortcut))
                }
            }
        }

        // we don't allow auto popup for links, too many errors
        if (parameters.isAutoPopup) return true

        // Need to use only the part before cursor for directory prefix, but without the .. or protocol prefixes
        val textWithPos = linkRefText
        val cursorPos = textWithPos.indexOf(TestUtils.DUMMY_IDENTIFIER)
        var beforeCursor = if (cursorPos > 0) textWithPos.substring(0, cursorPos).trim { it <= ' ' } else ""
        var afterCursor = if (cursorPos < textWithPos.length) textWithPos.substring(cursorPos + 1) else ""
        var useUri = ""

        for (url in PathInfo.URI_PREFIXES) {
            if (linkRefCleanText.startsWith(url)) {
                val prefixLen = url.length
                if (cursorPos < prefixLen) {
                    beforeCursor = ""
                } else {
                    beforeCursor = textWithPos.substring(prefixLen, cursorPos)
                    useUri = url
                }

                if (PathInfo.isExternal(url)) {
                    // remove domain
                    val domainEnd = textWithPos.indexOf('/', prefixLen + TestUtils.DUMMY_IDENTIFIER.length)
                    afterCursor = if (domainEnd < 0) "" else textWithPos.substring(domainEnd + TestUtils.DUMMY_IDENTIFIER.length)
                    if (!PathInfo.isURL(url)) useUri = ""
                }

                break
            }
        }

        // need to remove leading ../ and github links that are file targets raw/ and blob/, and branch after them but keep the before after split
        var pos: Int
        var startPos = 0
        val combined = beforeCursor + afterCursor
        var skipNext = false
        while (startPos < combined.length) {
            pos = combined.indexOf("/", startPos)
            if (pos <= 0) break

            if (skipNext) {
                startPos = pos + 1
                skipNext = false
            } else {
                var skip = 0
                when (combined.substring(startPos, pos)) {
                    ".." -> skip = 3
                    GitHubLinkResolver.GITHUB_BLOB_NAME -> {
                        skip = GitHubLinkResolver.GITHUB_BLOB_NAME.length + 1
                        skipNext = true
                    }
                    GitHubLinkResolver.GITHUB_RAW_NAME -> {
                        skip = GitHubLinkResolver.GITHUB_RAW_NAME.length + 1
                        skipNext = true
                    }
                }

                if (skip == 0) break
                startPos += skip
            }
        }

        if (startPos > 0) {
            if (startPos < beforeCursor.length) beforeCursor = beforeCursor.substring(startPos)
            else {
                afterCursor = afterCursor.substring(startPos - beforeCursor.length)
                beforeCursor = ""
            }
        }

        LOG.debug("cleanText: $linkRefCleanText, useUri: $useUri, beforeCursor: $beforeCursor, afterCursor: $afterCursor")

        var useForCompletion = ""
        val cleanTextInfo = PathInfo(beforeCursor + afterCursor)
        if (afterCursor.contains("/")) useForCompletion = useForCompletion.suffixWith("/")
        var useExtension = ""

        if (cleanTextInfo.hasExt) useExtension = cleanTextInfo.ext
        else if (cleanTextInfo.fileName.startsWith('.')) useExtension = cleanTextInfo.fileName

        if (useForCompletion.endsWith('/')) useForCompletion += "dummy"
        else useForCompletion = ""
        useForCompletion += useExtension.toBased().prefixOnceWith(".")

        fullPath = useForCompletion

        LOG.debug("cleanText: $linkRefCleanText, useUri: $useUri, fullPath: $fullPath, useExt: $useExtension")

        val invocationCount = (parameters.invocationCount - 1) % (if (containingFile.isWikiPage) 3 else 4)

        if (elementType !== WIKI_LINK_REF) {
            if (elementType == JEKYLL_INCLUDE_TAG_LINK_REF) {
                fixedIncludePath = MdRenderingProfileManager.getProfile(containingFile).parserSettings.optionsFlags and ParserOptions.GITBOOK_URL_ENCODING.flags == 0L
                if (fixedIncludePath) {
                    if (fullPath.isEmpty()) fullPath = ".html"
                    userPrefix = ""
                    linksType = Links.ABS
                    localType = Local.ABS
                    remoteType = Remote.ABS
                } else {
                    linksType = Links.REL
                    localType = Local.REL
                    remoteType = Remote.REL
                }
            } else if (fullPath.startsWith('/') && useUri.isEmpty()) {
                linksType = Links.REL
                localType = Local.ABS
                remoteType = Remote.ABS
                fullPath = fullPath.toBased().removePrefix("/").toString()
            } else if (PathInfo.isFileURI(useUri)) {
                linksType = Links.URL
                localType = Local.URI
                remoteType = Remote.URI

                // WINDOWS HANDLING
                if (fullPath.length > 1 && fullPath[1] == ':' && Character.isLetter(fullPath[0])) {
                    fullPath = fullPath.substring(2)
                }
                userPrefix = "file://"
                linksPrefix = "https://.../"
            } else if (PathInfo.isURI(useUri) && PathInfo.isExternal(useUri)) {
                linksType = Links.URL
                localType = Local.URL
                remoteType = Remote.URL
                userPrefix = "https://"
                linksPrefix = "https://.../"
            } else {
                // FEATURE: make this configurable, in terms of order and what types are included
                var useInvocationCount = invocationCount
                for (extension in MdLinkRefCompletionExtension.EXTENSIONS.value) {
                    useInvocationCount = (useInvocationCount + extension.invocationCountAdjustment()) % 4
                }

                when (if (containingFile.isWikiPage && useInvocationCount >= 1) {
                    // repo relative is not available
                    useInvocationCount + 1
                } else {
                    useInvocationCount
                }) {
                    0 -> {
                        linksType = Links.REL
                        localType = Local.REL
                        remoteType = Remote.REL
                    }

                    1 -> {
                        linksType = Links.REL
                        localType = Local.ABS
                        remoteType = Remote.ABS
                    }

                    2 -> {
                        linksType = Links.URL
                        localType = Local.URL
                        remoteType = Remote.URL
                        uriPrefix = "https://.../"
                        linksPrefix = "https://.../"
                    }

                    3 -> {
                        linksType = Links.URL
                        localType = Local.URI
                        remoteType = Remote.URI
                        uriPrefix = "file://.../"
                        linksPrefix = "https://.../"
                    }

                    else -> {
                    }
                }
            }
        }

        LOG.debug("completion path: $fullPath, invocation ordinal: $invocationCount, uriPrefix: $uriPrefix, linksPrefix: $linksPrefix")

        var needImage = false

        if (!needImage || elementType === REFERENCE_LINK_REF) {
            // need to know if it has image refs referencing it
            val reference = element.parent as? MdReferenceImpl
            if (reference != null) {
                for (refElement in containingFile.referencingElements) {
                    if (refElement is MdReferenceImageImpl) {
                        if (reference.isReferenceFor(refElement.referenceId)) {
                            needImage = true
                            break
                        }
                    }
                }
            }
        }

        linkRef = if (elementType === WIKI_LINK_REF) {
            WikiLinkRef(containingFileRef, fullPath, null, null, false)
        } else if (elementType === JEKYLL_INCLUDE_TAG_LINK_REF) {
            LinkRef(containingFileRef, fullPath, null, null, false)
        } else {
            if (elementType === IMAGE_LINK_REF || (elementType === REFERENCE_LINK_REF && needImage)) {
                ImageLinkRef(containingFileRef, fullPath, null, null, false)
            } else {
                LinkRef(containingFileRef, fullPath, null, null, false)
            }
        }

        var gitHubRepoPath = resolver.projectResolver.vcsRepoBasePath(linkRef.containingFile)
        if (gitHubRepoPath == null) gitHubRepoPath = resolver.projectBasePath.suffixWith("/")

        val matchedFiles = resolver.multiResolve(linkRef, Want.invoke(localType, remoteType, linksType, Match.COMPLETION), null)
        val repoName = PathInfo(gitHubRepoPath).fileName
        val repoWiki = repoName + PathInfo.WIKI_HOME_DIR_EXTENSION + "/"

        LOG.debug("matched files count: " + matchedFiles.size)

        for (pathInfo in matchedFiles) {
            val originalLinkAddress = pathInfo.filePath

            if (elementType === JEKYLL_INCLUDE_TAG_LINK_REF && fixedIncludePath) {
                if (!originalLinkAddress.startsWith("/_includes/")) {
                    continue
                }
            }

            var mappedLinkAddress = resolver.denormalizedLinkRef(originalLinkAddress)
            var linkAddress = originalLinkAddress
            var itemPrefix = if (uriPrefix.isNotEmpty()) uriPrefix else userPrefix
            val fileRef: FileRef?
            var linkRefFileName = linkAddress
            val icon: Icon?
            val lookupElement: LookupElement

            fileRef = if (pathInfo is FileRef) {
                pathInfo
            } else {
                (pathInfo as LinkRef).targetRef
            }

            itemLogger.debug { "start linkAddress: $linkAddress, linkRefFileName: $linkRefFileName, itemPrefix: $itemPrefix, userPrefix: $userPrefix" }

            if (fileRef != null) {
                // remove directories from completions
                val virtualFile = fileRef.virtualFile
                if (virtualFile != null && virtualFile.isDirectory) {
                    continue
                }

                if (linkRefFileName.isEmpty() || linkRefFileName == "#") {
                    linkRefFileName = PathInfo.relativePath(gitHubRepoPath, fileRef.filePath, withPrefix = false, blobRawEqual = false)
                    if (linkAddress.isEmpty()) linkAddress = "#"
                } else {
                    linkRefFileName = PathInfo.relativePath(gitHubRepoPath, fileRef.filePath, withPrefix = false, blobRawEqual = false)
                    if (linkRefFileName.toBased().prefixOnceWith("/").equals(linkAddress)) linkRefFileName = linkAddress
                }

                icon = when {
                    fileRef.isWikiPage -> MdIcons.getWikiPageIcon()
                    fileRef.isMarkdownExt -> MdIcons.getDocumentIcon()
                    else -> {
                        val project = resolver.project
                        val psiFile = if (project == null) null else fileRef.psiFileSystemItem(project)

                        psiFile?.getIcon(0)
                    }
                }
            } else {
                // must be a github link
                icon = MdIcons.LinkTypes.GitHub
                linkRefFileName = pathInfo.fileName

                // add a / to issues so we can do completion right away for the number
                if (linkRefFileName == GitHubLinkResolver.GITHUB_ISSUES_NAME) {
                    mappedLinkAddress = mappedLinkAddress.suffixWith("/")
                }
                itemPrefix = linksPrefix
            }

            itemLogger.debug { "link/ref linkAddress: $linkAddress, linkRefFileName: $linkRefFileName, itemPrefix: $itemPrefix, userPrefix: $userPrefix" }

            if (itemPrefix.isEmpty() && linkAddress.startsWith("../")) {
                // remove all except one and use the removed as prefix
                linkRefFileName = linkAddress
                while (linkRefFileName.startsWith("../")) {
                    itemPrefix += "../"
                    linkRefFileName = linkRefFileName.substring("../".length)
                }

                if (itemPrefix.length > "../.../".length) {
                    itemPrefix = "../.../"
                }
            }

            if (fileRef != null) {
                if (fileRef.isUnderWikiDir && linkRefFileName.startsWith(repoWiki)) {
                    // remove the repo part and leave wiki
                    linkRefFileName = GitHubLinkResolver.GITHUB_WIKI_NAME + "/" + linkRefFileName.substring(repoWiki.length)
                } else if (fileRef.virtualFile?.isDirectory == true) {
                    linkRefFileName += "/" // add trailing slash to dirs
                    linkAddress += "/"
                }
            }

            if (userPrefix.isNotEmpty()) {
                linkRefFileName = userPrefix + linkRefFileName
                itemPrefix = ""
            }

            itemLogger.debug { "finish linkAddress: $linkAddress, linkRefFileName: $linkRefFileName, itemPrefix: $itemPrefix, userPrefix: $userPrefix" }

            if (userPrefix.isNotEmpty() || itemPrefix.isNotEmpty() || linkAddress != linkRefFileName || elementType === JEKYLL_INCLUDE_TAG_LINK_REF && fixedIncludePath) {
                // can swap them and add with prefix
                var lookupElementBuilder = LookupElementBuilder.create(linkRefFileName).withCaseSensitivity(false).withTypeText(itemPrefix, false)
                if (icon != null) lookupElementBuilder = lookupElementBuilder.withIcon(icon)
                if (linkRef is WikiLinkRef && linkAddress.contains("/")) {
                    lookupElementBuilder = lookupElementBuilder.withItemTextForeground(errorColor)
                }
                lookupElement = if (elementType === JEKYLL_INCLUDE_TAG_LINK_REF && fixedIncludePath) {
                    val prefixString = mappedLinkAddress.removePrefix("/_includes/")
                    WrappingDecorator.withPrefixMods(lookupElementBuilder, prefixString, linkRefFileName.length)
                } else {
                    if (mappedLinkAddress == originalLinkAddress) {
                        WrappingDecorator.withPrefixMods(lookupElementBuilder, linkAddress, linkRefFileName.length)
                    } else {
                        WrappingDecorator.withPrefixMods(lookupElementBuilder, mappedLinkAddress, linkRefFileName.length)
                    }
                }
            } else {
                var lookupElementBuilder = LookupElementBuilder.create(linkAddress).withCaseSensitivity(false)
                if (icon != null) lookupElementBuilder = lookupElementBuilder.withIcon(icon)

                if (linkRef is WikiLinkRef && linkAddress.contains("/")) {
                    lookupElementBuilder = lookupElementBuilder.withItemTextForeground(errorColor)
                }

                lookupElement = WrappingDecorator.withPrefixMods(lookupElementBuilder, mappedLinkAddress, linkAddress.length)
            }

            resultSet.addElement(AutoCompletionPolicy.NEVER_AUTOCOMPLETE.applyPolicy(lookupElement))
        }
        return true
    }
}
