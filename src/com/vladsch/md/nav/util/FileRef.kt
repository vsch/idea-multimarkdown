// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.vladsch.md.nav.vcs.GitHubVcsRoot
import com.vladsch.md.nav.vcs.MdLinkResolverManager

open class FileRef(fullPath: String, private val _virtualFile: VirtualFile?) : PathInfo(fullPath) {
    protected val _mainRepoDirEnd: Int
    protected val _wikiHomeDirEnd: Int

    // these are only set if the fileRef is from a multiResolve result
    // set by resolver to true if this file matched with link anchor
    //var isAnchorMatched:Boolean = false, not being set because no code uses this value yet

    // set by resolver to true if this resolves to raw file instead of pretty rendered file
    var isRawFile = false

    constructor(virtualFile: VirtualFile) : this(virtualFile.path, virtualFile)

    constructor(psiFile: PsiFileSystemItem) : this(psiFile.virtualFile)

    constructor(psiElement: PsiElement) : this(psiElement.containingFile.originalFile)

    constructor(fileRef: FileRef) : this(fileRef._fullPath, fileRef._virtualFile)

    constructor(fullPath: String) : this(fullPath, null)

    init {
        // gitHub wiki home will be like ..../dirname/dirname.wiki
        var wikiHomeDirEnd = this._fullPath.lastIndexOf(WIKI_HOME_DIR_EXTENSION + "/", _nameStart)
        var mainRepoDirEnd = 0

        if (wikiHomeDirEnd >= _nameStart || wikiHomeDirEnd < 0) wikiHomeDirEnd = 0

        if (wikiHomeDirEnd > 0) {
            val wikiHomeDirStart = this._fullPath.lastIndexOf('/', wikiHomeDirEnd)
            if (wikiHomeDirStart > 1) {
                val wikiHomeDirName = this._fullPath.substring(wikiHomeDirStart + 1, wikiHomeDirEnd)
                // now previous to start has to be the same directory
                val mainRepoDirStart = this._fullPath.lastIndexOf('/', wikiHomeDirStart - 1)
                if (mainRepoDirStart >= 0) {
                    val mainRepoDirName = this._fullPath.substring(mainRepoDirStart + 1, wikiHomeDirStart)
                    if (mainRepoDirName == wikiHomeDirName) {
                        mainRepoDirEnd = wikiHomeDirStart
                        wikiHomeDirEnd += WIKI_HOME_DIR_EXTENSION.length
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

        this._wikiHomeDirEnd = wikiHomeDirEnd
        this._mainRepoDirEnd = mainRepoDirEnd
    }

    val isUnderWikiDir: Boolean
        get() = _wikiHomeDirEnd > 0

    val isWikiPage: Boolean
        get() = isUnderWikiDir && isWikiPageExt

    val isWikiHomePage: Boolean
        get() = isWikiPage && WIKI_HOME_FILENAME == fileNameNoExt

    val wikiDir: String
        get() = if (_wikiHomeDirEnd <= 0) EMPTY_STRING else _fullPath.substring(0, _wikiHomeDirEnd)

    //    val isWikiDir: Boolean
    //        get() {
    //            if (wikiHomeDirEnd > 0 || !fileName.endsWith(WIKI_HOME_EXTENSION)) return false
    //            // gitHub wiki home will be like ..../dirname/dirname.wiki
    //            return PathInfo(path).fileName == fileNameNoExt
    //        }

    val mainRepoDir: String
        get() {
            assert(isUnderWikiDir, { "mainRepo related values are only valid if isUnderWikiDir is true" })
            return if (_mainRepoDirEnd <= 0) EMPTY_STRING else _fullPath.substring(0, _mainRepoDirEnd)
        }

    val filePathFromWikiDir: String
        get() = if (_wikiHomeDirEnd <= 0) _fullPath else _fullPath.substring(_wikiHomeDirEnd + 1)

    val filePathFromMainRepoDir: String
        get() {
            assert(isUnderWikiDir, { "mainRepo related values are only valid if isUnderWikiDir is true" })
            return if (_mainRepoDirEnd <= 0) _fullPath else _fullPath.substring(_mainRepoDirEnd + 1)
        }

    val pathFromWikiDir: String
        get() = if (_wikiHomeDirEnd <= 0) path else _fullPath.substring(_wikiHomeDirEnd + 1, _nameStart)

    val pathFromMainRepoDir: String
        get() {
            assert(isUnderWikiDir, { "mainRepo related values are only valid if isUnderWikiDir is true" })
            return if (_mainRepoDirEnd <= 0) path else _fullPath.substring(_mainRepoDirEnd + 1, _nameStart)
        }

    val upDirectoriesToWikiHome: Int by lazy {
        if (_wikiHomeDirEnd <= 0 || _wikiHomeDirEnd == _fullPath.length) 0
        else {
            var pos = _wikiHomeDirEnd
            var upDirs = 0
            while (pos < _fullPath.length) {
                pos = _fullPath.indexOf('/', pos)
                if (pos < 0) break

                upDirs++
                pos++
            }

            upDirs
        }
    }

    override fun append(vararg parts: String): FileRef = PathInfo.appendParts(_fullPath, *parts, construct = ::FileRef)
    override fun append(parts: Collection<String>): FileRef = PathInfo.appendParts(_fullPath, parts, ::FileRef)
    override fun append(parts: Sequence<String>): FileRef = PathInfo.appendParts(_fullPath, parts, ::FileRef)

    override val virtualFile by lazy { _virtualFile ?: super.virtualFile }

    val exists: Boolean
        get() = virtualFile?.exists() as Boolean

    open fun psiFile(project: Project): PsiFile? {
        val virtualFileCopy = virtualFile
        return if (virtualFileCopy == null) null else PsiManager.getInstance(project).findFile(virtualFileCopy)
    }

    open fun psiFileSystemItem(project: Project): PsiFileSystemItem? {
        val virtualFileCopy = virtualFile
        return if (virtualFileCopy == null) null else
            if (virtualFileCopy.isDirectory) PsiManager.getInstance(project).findFile(virtualFileCopy)
            else PsiManager.getInstance(project).findFile(virtualFileCopy)
    }

    override fun projectFileRef(project: Project): ProjectFileRef? {
        val myVirtualFile = virtualFile
        return if (myVirtualFile == null) null else ProjectFileRef(myVirtualFile, project)
    }
}

open class ProjectFileRef(_virtualFile: VirtualFile, val project: Project, private val _psiFile: PsiFileSystemItem?) : FileRef(_virtualFile) {

    constructor(psiFile: PsiFileSystemItem) : this(psiFile.virtualFile, psiFile.project, psiFile)

    constructor(virtualFile: VirtualFile, project: Project) : this(virtualFile, project, null)

    override val virtualFile = _virtualFile

    val psiFile: PsiFile? by lazy { _psiFile as? PsiFile ?: PsiManager.getInstance(project).findFile(virtualFile) }
    val psiFileSystemItem: PsiFileSystemItem? by lazy {
        _psiFile ?: if (virtualFile.isDirectory) PsiManager.getInstance(project).findDirectory(virtualFile)
        else PsiManager.getInstance(project).findFile(virtualFile)
    }

    // FIX: change this to resolve to the repo type for this particular file
    val gitHubVcsRoot: GitHubVcsRoot? by lazy {
        MdLinkResolverManager.getInstance(project).getGitHubRepo(path)
    }

    val isUnderVcs: Boolean
        get() {
            val myVirtualFile = virtualFile
            val status = FileStatusManager.getInstance(project).getStatus(myVirtualFile)
            val id = status.id
            val fileStatus = status == FileStatus.DELETED || status == FileStatus.ADDED || status == FileStatus.UNKNOWN || status == FileStatus.IGNORED || id.startsWith("IGNORE")
            return !fileStatus
        }

    val gitHubBasePath: String?
        get() = gitHubVcsRoot?.basePath

    val gitHubBaseUrl: String?
        get() = gitHubVcsRoot?.baseUrl

    override fun psiFile(project: Project): PsiFile? = if (project === this.project && this.psiFile is PsiFile) this.psiFile as PsiFile else super.psiFile(project)
    override fun psiFileSystemItem(project: Project): PsiFileSystemItem? = if (project === this.project) this.psiFile else super.psiFile(project)
    override fun projectFileRef(project: Project): ProjectFileRef? = if (project === this.project) this else super.projectFileRef(project)
}

