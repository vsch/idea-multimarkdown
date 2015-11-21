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

import java.util.regex.Pattern
import kotlin.text.Regex
import kotlin.text.RegexOption

class LinkRefMatcher(val linkRef: LinkRef, projectBasePath: String? = null, val wantLooseMatch: Boolean = false) {

    val projectBasePath = LinkInfo.cleanFullPath(projectBasePath)
    val subDirPattern = "(?:.+/)?"

    var needLooseMatchForWiki = false

    protected fun extensionPattern(wantLooseMatch: Boolean): String {
        val typeExtensions = linkRef.linkExtensions
        var hadExtension = false
        var extensionPattern = ""
        var linkRefExt = if (!linkRef.hasExt) "" else if (linkRef !is WikiLinkRef) linkRef.ext.replace("%23", "#") else linkRef.ext
        val anchorPattern = if ((wantLooseMatch || linkRef is WikiLinkRef) && linkRef.hasAnchor) matchPathText(linkRef.anchorText, true) else ""

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

        if (!extensionPattern.isEmpty()) extensionPattern = "(?:$extensionPattern)"

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

    fun pattern(looseMatch: Boolean = wantLooseMatch): Pattern {
        return Pattern.compile(patternText(looseMatch), Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
    }

    fun patternRegex(looseMatch: Boolean = wantLooseMatch): Regex {
        return patternText(looseMatch).toRegex(RegexOption.IGNORE_CASE)
    }

    fun patternText(looseMatch: Boolean = wantLooseMatch): String {
        // return a regex that will match most loosely a file path to be used by this link
        val pattern: String

        // we always match subdirectories for markdown and wiki's, even for exact match since if the destination is a wiki page then no directories will be used
        // image target types have no pattern subdirectories but use exact type
        if (linkRef is WikiLinkRef) {
            // spaces match - and spaces, all subdirectories under Wiki Home match, only WIKI targets accepted, no case sensitivity
            val filenamePattern = matchPathText(linkRef.fileNameNoExt, emptyMatchesAll = true)
            val anchorPattern = matchPathText(linkRef.anchorText, isOptional = true)
            val extensionPattern = extensionPattern(true) + "?"

            pattern = "^" + matchPathText(linkRef.containingFile.wikiDir.endWith('/')) + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$"
        } else {
            // this is all other links, no space dash equivalence, %23 in link matches # in file, files under project or gitHubRepoHome will be matched
            // extension pattern is optional added to a list of all extensions for the targetType, with zero or none matching
            // anchor is an optional pattern right after the name so extensions follow, all subdirectories between project or repo home are optional
            // matching is not case sensitive even for exact match because the final could be a wiki page

            // project base, if none then file path
            var prefixPath = if (linkRef is ImageLinkRef) "" else projectBasePath ?: linkRef.containingFile.path
            var subDirPattern = if (!looseMatch) linkRef.path else subDirPattern
            val filenamePattern = "\\Q" + linkRef.fileNameNoExt + "\\E"
            val anchorPattern = if (looseMatch && linkRef.hasAnchor) matchPathText(linkRef.anchorText, true) else ""
            var extensionPattern =
                    // TODO: take the linkRef filePath and append it to containingFile.filePath and see if you are in a wiki or in the repo
                    // based on that we can make a decision of what the destination file will be!!! If it is a wiki then wiki rules will apply
                    // not case sensitive, either with or without an extension, otherwise case sensitive and needs extension

                    // TODO: the best way to resolve the links is to take the path relative to the file's GitHub URL, then map the resulting URL back to
                    // local file system. That way the mapping will be done to Wiki and from Wiki, but also between repositories on GitHub
                    if (linkRef is FileLinkRef && ((!looseMatch && !linkRef.containingFile.isWikiPage) || linkRef.hasExt && !linkRef.isMarkdownExt)) {
                        needLooseMatchForWiki = linkRef.isMarkdownExt && !linkRef.path.isEmpty()
                        matchExt(linkRef.ext)
                    } else {
                        extensionPattern(looseMatch) + "?"
                    }

            pattern = "^" + matchPathText(prefixPath.endWith('/')) + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$"
        }

        return pattern
    }

    private fun matchExt(ext: String?): String {
        return matchPathText(ext.startWith('.'))
    }
}
