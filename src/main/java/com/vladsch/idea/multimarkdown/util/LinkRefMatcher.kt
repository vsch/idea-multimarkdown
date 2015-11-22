/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
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

import kotlin.text.Regex
import kotlin.text.RegexOption

class LinkRefMatcher(val linkRef: LinkRef, projectBasePath: String? = null, val wantLooseMatch: Boolean = false) {

    val projectBasePath = PathInfo.cleanFullPath(projectBasePath)
    val subDirPattern = "(?:.+/)?"

    private fun matchExt(ext: String?): String {
        return matchPathText(ext.startWith('.'))
    }

    protected fun extensionPattern(useDefaultExt: Boolean, addAnchorExt:Boolean, isOptional: Boolean): String {
        val typeExtensions = if (useDefaultExt) linkRef.linkExtensions else arrayOf()
        var hadExtension = false
        var extensionPattern = ""
        var linkRefExt = if (!linkRef.hasExt) "" else if (linkRef !is WikiLinkRef) linkRef.ext.replace("%23", "#") else linkRef.ext
        val anchorPattern = if (addAnchorExt && linkRef.hasAnchor) matchPathText(linkRef.anchorText, true) else ""

        for (ext in typeExtensions) {
            if (!extensionPattern.isEmpty()) extensionPattern += "|"
            if (!ext.isEmpty()) {
                extensionPattern += matchExt(ext)
                if (ext.equals(linkRefExt, ignoreCase = true)) hadExtension = true
            }
        }

        if (!hadExtension && linkRef.hasExt && linkRef !is WikiLinkRef) {
            if (!extensionPattern.isEmpty()) extensionPattern += "|"
            extensionPattern += matchExt(linkRef.ext)
        }

        if (anchorPattern.isNotEmpty()) {
            if (!extensionPattern.isEmpty()) extensionPattern += "|"
            extensionPattern += anchorPattern
        }

        if (extensionPattern.isNotEmpty()) {
            extensionPattern = "(?:$extensionPattern)"
            if (isOptional) extensionPattern + "?"
        }

        return extensionPattern
    }

    fun matchPathText(pathText: String?, isOptional: Boolean = false, emptyMatchesAll: Boolean = false): String {
        if (pathText == null || pathText.isEmpty()) return if (emptyMatchesAll) "(?:.*)" else ""

        val suffix = if (isOptional) "\\E)?" else "\\E"
        val prefix = if (isOptional) "(?:\\Q" else "\\Q"
        return when {
            linkRef is WikiLinkRef -> prefix + pathText.replace("-| ".toRegex(), "\\\\E(?:-| )\\\\Q") + suffix
            else -> prefix + pathText.replace("%23", "#") + suffix
        }
    }

    fun patternRegex(looseMatch: Boolean = wantLooseMatch): Regex? {
        return patternText(looseMatch)?.toRegex(RegexOption.IGNORE_CASE)
    }

    fun patternText(looseMatch: Boolean = wantLooseMatch): String? {
        // return a regex that will match most loosely a file path to be used by this link
        assert(projectBasePath.isNotEmpty(), { "projectBasePath cannot be empty" })

        val pattern: String

        // we always match subdirectories for markdown and wiki's, even for exact match since if the destination is a wiki page then no directories will be used
        // image target types have no pattern subdirectories but use exact type
        if (linkRef is WikiLinkRef) {
            // spaces match - and spaces, all subdirectories under Wiki Home match, only WIKI targets accepted, no case sensitivity
            val filenamePattern = matchPathText(linkRef.fileNameNoExt, emptyMatchesAll = true)
            val anchorPattern = matchPathText(linkRef.anchorText, isOptional = true)
            val extensionPattern = extensionPattern(useDefaultExt = true, addAnchorExt = true, isOptional = true)

            pattern = "^" + matchPathText(linkRef.containingFile.wikiDir.endWith('/')) + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$"
        } else {
            // it is assumed that if there is a wiki directory then it is a sub dir of the projectBasePath with the same name and .wiki appended
            // so if we encounter projectBasePath/wiki will will change it to projectBasePath/projectBasePathName.wiki
            // going below the projectBasePath is not supported for now.

            // if the page is a wiki home page then it will be treated as if it is located in the projectBasePath so that its relative links resolve correctly
            // if it is a wiki but no the main page then its prefix is not changed
            var repoPrefixPath = projectBasePath.endWith('/') + "blob/master/"
            var wikiPrefixPath = projectBasePath.endWith('/') + "wiki/"
            var prefixPath: String

            if (linkRef.containingFile.isWikiPage) {
                // wiki repo, files here can be accessed in two ways:
                // 1. markdown without extension, all files are logically located in the root of the wiki regardless of their physical location in the repo and will be rendered to HTML,
                // with one exception for the Home page which is logically located in the main repo directory and wiki/ is aliased to it

                // 2. any file with extension, all files are located relative to their physical location under the wiki repo
                var filePrefixPath = if (linkRef.containingFile.isWikiHomePage && !linkRef.hasExt) PathInfo.append(wikiPrefixPath, "..").filePath else wikiPrefixPath
                prefixPath = PathInfo.append(filePrefixPath, linkRef.path.split('/')).filePath.endWith('/')
            } else {
                // main repo

                // files in the main repo are logically two dirs down blob/branchOrTag/fileName..., files in the repo require no backing out, wiki, pulls, issues, ... do
                // to figure out whether the link is trying for GitHub specifics located at the repo root we will normalize the linkRef path from projectBasePath/blob/master/
                // if after normalization we still have that prefix then the link is not going for Wiki or GitHub specifics and we can just search normal files and remap
                // them later

                // if the prefix changes to projectBasePath/wiki then we will search for Wiki pages ignoring subdirectories if the link has no extension and keep subdirectories
                // if the link has an extension because in the latter case it will map to a raw markdown or image in the wiki repo
                prefixPath = PathInfo.append(repoPrefixPath, linkRef.path.split('/')).filePath.endWith('/')
            }

            var wikiPages = false
            var gitHubLinks = false

            if (prefixPath.endWith('/').startsWith(wikiPrefixPath)) {
                // not going for main repo or links, linking a file in the Wiki Repo
                // if not image and no extension then will match markdown files, ie. WikiPages
                wikiPages = linkRef !is ImageLinkRef && !linkRef.hasExt

                // change it to our physical wiki directory which is right after our project base with same name and .wiki extension
                var pathInfo = PathInfo(projectBasePath)

                prefixPath = pathInfo.append(pathInfo.fileNameNoExt + PathInfo.WIKI_HOME_EXTENSION).filePath.endWith('/') + prefixPath.substring(wikiPrefixPath.length)
            } else if (prefixPath.startsWith(repoPrefixPath)) {
                // linking to files in the main repo
                prefixPath = projectBasePath.endWith('/') + prefixPath.substring(repoPrefixPath.length)
            } else {
                // if in the main project directory then can link to issues, pulls, etc
                // otherwise somewhere before the main repo or in between and nothing will be found
                if (!prefixPath.endWith('/').equals(projectBasePath.endWith('/'), looseMatch)) return null

                // looking for GitHub Links, linkRef name should be one of the GitHub link directories, without wiki since that is taken care of separately
                // already projectBasePath
                gitHubLinks = true
            }

            val filenamePattern = matchPathText(linkRef.fileNameNoExt)
            var extensionPattern = ""
            var anchorPattern = ""

            if (wikiPages) {
                if (looseMatch) anchorPattern = matchPathText(linkRef.anchorText, true)
                extensionPattern = extensionPattern (useDefaultExt = looseMatch, addAnchorExt = looseMatch, isOptional = looseMatch)
            } else {
                // prefix is the file's directory plus any path in the link itself, loose match will search down into the tree, but not up
                // also looseMatch is not particular about extension as long as there is one that is an image extension
                // the file name has to match, no anchor option is used
                if (looseMatch || linkRef.hasExt) extensionPattern = extensionPattern(useDefaultExt = looseMatch, addAnchorExt = false, isOptional = false)
            }

            pattern = "^" + matchPathText(prefixPath.endWith('/')) + (if (wikiPages) subDirPattern else "") + filenamePattern + anchorPattern + extensionPattern + "$"
        }

        return pattern
    }
}
