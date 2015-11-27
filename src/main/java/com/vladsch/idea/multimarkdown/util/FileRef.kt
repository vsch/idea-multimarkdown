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

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin

open class FileRef(fullPath: String, private val _virtualFile: VirtualFile?) : PathInfo(fullPath) {
    protected val mainRepoDirEnd: Int
    protected val wikiHomeDirEnd: Int

    constructor(virtualFile: VirtualFile) : this(virtualFile.path, virtualFile)
    constructor(psiFile: PsiFile) : this(psiFile.virtualFile)
    constructor(psiElement: PsiElement) : this(psiElement.containingFile)
    constructor(fullPath: String) : this(fullPath, null)

    init {
        // gitHub wiki home will be like ..../dirname/dirname.wiki
        var wikiHomeDirEnd = this.fullPath.lastIndexOf(WIKI_HOME_EXTENSION + "/", nameStart)
        var mainRepoDirEnd = 0

        if (wikiHomeDirEnd >= nameStart || wikiHomeDirEnd < 0) wikiHomeDirEnd = 0

        if (wikiHomeDirEnd > 0) {
            val wikiHomeDirStart = this.fullPath.lastIndexOf('/', wikiHomeDirEnd)
            if (wikiHomeDirStart > 1) {
                val wikiHomeDirName = this.fullPath.substring(wikiHomeDirStart + 1, wikiHomeDirEnd)
                // now previous to start has to be the same directory
                val mainRepoDirStart = this.fullPath.lastIndexOf('/', wikiHomeDirStart - 1)
                if (mainRepoDirStart >= 0) {
                    val mainRepoDirName = this.fullPath.substring(mainRepoDirStart + 1, wikiHomeDirStart)
                    if (mainRepoDirName == wikiHomeDirName) {
                        mainRepoDirEnd = wikiHomeDirStart
                        wikiHomeDirEnd += WIKI_HOME_EXTENSION.length
                    } else {
                        wikiHomeDirEnd = 0
                    }
                } else {
                    wikiHomeDirEnd = 0
                }
            } else {
                wikiHomeDirEnd = 0
            }
        }

        this.wikiHomeDirEnd = wikiHomeDirEnd
        this.mainRepoDirEnd = mainRepoDirEnd

    }

    val isUnderWikiDir: Boolean
        get() = wikiHomeDirEnd > 0

    val isWikiPage: Boolean
        get() = isUnderWikiDir && isWikiPageExt

    val isWikiHomePage: Boolean
        get() = isWikiPage && WIKI_HOME_FILENAME == fileNameNoExt

    val wikiDir: String
        get() = if (wikiHomeDirEnd <= 0) EMPTY_STRING else fullPath.substring(0, wikiHomeDirEnd)

    //    val isWikiDir: Boolean
    //        get() {
    //            if (wikiHomeDirEnd > 0 || !fileName.endsWith(WIKI_HOME_EXTENSION)) return false
    //            // gitHub wiki home will be like ..../dirname/dirname.wiki
    //            return PathInfo(path).fileName == fileNameNoExt
    //        }

    val mainRepoDir: String
        get() {
            assert(isUnderWikiDir, { "mainRepo related values are only valid if isUnderWikiDir is true" })
            return if (mainRepoDirEnd <= 0) EMPTY_STRING else fullPath.substring(0, mainRepoDirEnd)
        }

    val filePathFromWikiDir: String
        get() = if (wikiHomeDirEnd <= 0) fullPath else fullPath.substring(wikiHomeDirEnd + 1)

    val filePathFromMainRepoDir: String
        get() {
            assert(isUnderWikiDir, { "mainRepo related values are only valid if isUnderWikiDir is true" })
            return if (mainRepoDirEnd <= 0) fullPath else fullPath.substring(mainRepoDirEnd + 1)
        }

    val pathFromWikiDir: String
        get() = if (wikiHomeDirEnd <= 0) path else fullPath.substring(wikiHomeDirEnd + 1, nameStart)

    val pathFromMainRepoDir: String
        get() {
            assert(isUnderWikiDir, { "mainRepo related values are only valid if isUnderWikiDir is true" })
            return if (mainRepoDirEnd <= 0) path else fullPath.substring(mainRepoDirEnd + 1, nameStart)
        }

    val upDirectoriesToWikiHome: Int by lazy {
        if (wikiHomeDirEnd <= 0 || wikiHomeDirEnd == fullPath.length) 0
        else {
            var pos = wikiHomeDirEnd
            var upDirs = 0
            while (pos < fullPath.length) {
                pos = fullPath.indexOf('/', pos)
                if (pos < 0) break;

                upDirs++
                pos++
            }

            upDirs
        }
    }

    override fun append(vararg parts: String): FileRef = PathInfo.appendParts(fullPath, *parts, construct = ::FileRef)
    override fun append(parts: Collection<String>): FileRef = PathInfo.appendParts(fullPath, parts, ::FileRef)
    override fun append(parts: Sequence<String>): FileRef = PathInfo.appendParts(fullPath, parts, ::FileRef)

    override val virtualFile by lazy { _virtualFile ?: VirtualFileManager.getInstance().findFileByUrl("file:" + fullPath) }

    val exists: Boolean
        get() = virtualFile?.exists() as Boolean

    override fun projectFileRef(project: Project): ProjectFileRef? {
        val myVirtualFile = virtualFile
        return if (myVirtualFile == null) null else ProjectFileRef(myVirtualFile, project);
    }
}

open class ProjectFileRef(virtualFile: VirtualFile, val project: Project, private val _psiFile:PsiFile?) : FileRef(virtualFile.path, virtualFile) {

    constructor(psiFile: PsiFile) : this(psiFile.virtualFile, psiFile.project, psiFile)
    constructor(virtualFile: VirtualFile, project: Project) : this(virtualFile, project, null)

    val psiFile: PsiFile? by lazy { _psiFile ?: PsiManager.getInstance(project).findFile(virtualFile) }

    // TODO: change this to resolve to the repo type for this particular file
    val gitHubRepo: GitHubRepo? by lazy {
        MultiMarkdownPlugin.getProjectComponent(project)?.getGitHubRepo(path)
    }

    val isUnderVcs: Boolean
        get() {
            val myVirtualFile = virtualFile ?: return false
            val status = FileStatusManager.getInstance(project).getStatus(myVirtualFile)
            val id = status.id
            val fileStatus = status == FileStatus.DELETED || status == FileStatus.ADDED || status == FileStatus.UNKNOWN || status == FileStatus.IGNORED || id.startsWith("IGNORE")
            return !fileStatus
        }

    val gitHubRepoPath: String?
        get() = gitHubRepo?.basePath

    override fun projectFileRef(project: Project): ProjectFileRef? = this
}

