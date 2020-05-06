// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.vcs

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.VcsListener
import com.intellij.openapi.vfs.*
import com.intellij.util.Alarm
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.MdProjectComponent
import com.vladsch.md.nav.MdRepoChangeListener
import com.vladsch.md.nav.settings.MdProjectSettings
import com.vladsch.md.nav.settings.ProfileManagerChangeListener
import com.vladsch.md.nav.settings.RenderingProfileManager
import com.vladsch.md.nav.settings.ProjectSettingsChangedListener
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.md.nav.vcs.MdLinkResolver.ProjectResolver
import com.vladsch.md.nav.vcs.api.MdOnProjectSettingsChangedActivity
import com.vladsch.md.nav.vcs.api.MdOnProjectSettingsChangedActivityProvider
import com.vladsch.plugin.util.*
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryChangeListener
import git4idea.repo.GitRepositoryManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import kotlin.collections.ArrayList

// FIX: make this a service
class MdLinkResolverManager(override val project: Project) : Disposable, ProjectResolver {

    companion object {
        private val LOG = com.intellij.openapi.diagnostic.Logger.getInstance("com.vladsch.md.nav.vcs")
        private val NULL = LazyFunction<Project, MdLinkResolverManager>(Function { t -> MdLinkResolverManager(t) })
        private val DUMMY_REPO = Object()

        @JvmStatic
        fun getInstance(project: Project): MdLinkResolverManager {
            return if (project.isDefault) NULL.getValue(project)
            else {
                // DEPRECATED: added 2019.08, when available change to
//                project.getComponent(MdLinkResolverManager::class.java)
                return ServiceManager.getService(project, MdLinkResolverManager::class.java)
            }
        }
    }

    private val projectDirectories = ArrayList<VirtualFile>()
    private var rescanProjectDirectories = true
    private val onProjectSettingsChangedActivities: ArrayList<MdOnProjectSettingsChangedActivity> = ArrayList()
    private val inProjectSettingsChangedActivity = ConcurrentHashMap<MdOnProjectSettingsChangedActivity, Long>()
    private val projectSettingsChangedAlarm = Alarm(this)

    @Suppress("PrivatePropertyName")
    private val VCS_MAPS_LOCK = Object()

    // this one has real roots
    private var gitHubVcsRoots = ConcurrentHashMap<String, GitHubVcsRoot>()

    // this has all paths inquired for vcs since last update
    private var gitHubVcsRepos = ConcurrentHashMap<String, Any>()

    private fun clearVcsMaps(newVcsMap: ConcurrentHashMap<String, GitHubVcsRoot>) {
        synchronized(VCS_MAPS_LOCK) {
            gitHubVcsRoots = newVcsMap
            gitHubVcsRepos = ConcurrentHashMap()
        }
    }

    fun <T : MdOnProjectSettingsChangedActivity> getActivity(klass: Class<out T>): T? {
        @Suppress("UNCHECKED_CAST")
        return onProjectSettingsChangedActivities.find { klass.isInstance(it) } as? T
    }

    fun getProjectDirectories(): ArrayList<VirtualFile> {
        if (rescanProjectDirectories && !project.isDisposed) {
            val allVersionedRoots = ProjectLevelVcsManager.getInstance(project).allVersionedRoots
            allVersionedRoots.sortByDescending { it.name }
            val dirsToVisit = mutableListOf<VirtualFile>(*allVersionedRoots)
            val dirsVisited = mutableSetOf<VirtualFile>()
            val triedDirs = HashSet<VirtualFile>() // don't list roots in the completion list
            triedDirs.addAll(allVersionedRoots)
            val projectDirectories = ArrayList<VirtualFile>()
            projectDirectories.clear()

            val baseDir = project.getProjectBaseDirectory()
            if (baseDir != null) dirsToVisit.add(baseDir)

            while (dirsToVisit.isNotEmpty()) {
                val parentDir = dirsToVisit.removeAt(0)
                if (!parentDir.isValid) continue

                if (!dirsVisited.contains(parentDir)) {
                    dirsVisited.add(parentDir)

                    if (!triedDirs.contains(parentDir)) {
                        triedDirs.add(parentDir)
                        projectDirectories.add(parentDir)
                    }

                    parentDir.children.filterTo(dirsToVisit) { it.isDirectory && it.isInLocalFileSystem && !dirsVisited.contains(it) && !it.name.startsWith(".") }
                }
            }

            synchronized(this.projectDirectories) {
                this.projectDirectories.clear()
                this.projectDirectories.addAll(projectDirectories)
                rescanProjectDirectories = false
            }
        }

        return ArrayList(projectDirectories)
    }

    override fun getGitHubRepo(path: String?): GitHubVcsRoot? {
        if (project.isDisposed) return null
        val projectBasePath: String = project.basePath ?: return null

        var vcsRoots: ConcurrentHashMap<String, GitHubVcsRoot>?
        var vcsRepos: ConcurrentHashMap<String, Any>?

        synchronized(VCS_MAPS_LOCK) {
            vcsRoots = gitHubVcsRoots
            vcsRepos = gitHubVcsRepos
        }

        val vcsRootMap = vcsRoots ?: return null
        val vcsRepoMap = vcsRepos ?: return null

        val findPath = path ?: projectBasePath
        var gitHubVcsRoot: GitHubVcsRoot? = null

        val cached = vcsRepoMap[findPath]

        if (cached != null) {
            return cached as? GitHubVcsRoot
        }

        for (gitHubVcs in vcsRootMap.values) {
            if ((gitHubVcsRoot == null || gitHubVcs.basePath.length > gitHubVcsRoot.basePath.length) && findPath.startsWith(gitHubVcs.basePath.suffixWith('/'))) {
                MdProjectComponent.LOG.debug { "tentative gitHubRepo ${gitHubVcs.basePath} for $findPath" }
                gitHubVcsRoot = gitHubVcs
            }
        }

        vcsRepoMap[findPath] = gitHubVcsRoot ?: DUMMY_REPO
        MdProjectComponent.LOG.debug { "gitHubRepo $path for ${gitHubVcsRoot?.basePath}" }
        return gitHubVcsRoot
    }

    private fun getGitHubRepoFromURL(url: String): GitHubVcsRoot? {
        if (project.isDisposed) return null

        var vcsRoots: ConcurrentHashMap<String, GitHubVcsRoot>?
        var vcsRepos: ConcurrentHashMap<String, Any>?

        synchronized(VCS_MAPS_LOCK) {
            vcsRoots = gitHubVcsRoots
            vcsRepos = gitHubVcsRepos
        }

        val vcsRootMap = vcsRoots ?: return null
        val vcsRepoMap = vcsRepos ?: return null

        val useUrl = url.removeAnyPrefix("http://", "https://")
        val findPath = PathInfo(useUrl).path
        val cached = vcsRepoMap[findPath]
        if (cached != null) {
            return cached as? GitHubVcsRoot
        }

        var gitHubVcsRoot: GitHubVcsRoot? = null
        for (gitHubVcs in vcsRootMap.values) {
            if (gitHubVcs.baseUrl != null && (gitHubVcsRoot == null || gitHubVcs.basePath.length > gitHubVcsRoot.basePath.length)) {
                val suffixWith = gitHubVcs.baseUrl.removeAnyPrefix("http://", "https://").suffixWith('/')

                if (gitHubVcs.isWiki) {
                    if (useUrl.startsWith(suffixWith + "wiki/")) {
                        MdProjectComponent.LOG.debug { "tentative gitHubRepo ${gitHubVcs.basePath} for URL $url" }
                        gitHubVcsRoot = gitHubVcs
                    }
                } else {
                    if (useUrl.startsWith(suffixWith)) {
                        MdProjectComponent.LOG.debug { "tentative gitHubRepo ${gitHubVcs.basePath} for URL $url" }
                        gitHubVcsRoot = gitHubVcs
                    }
                }
            }
        }

        vcsRepoMap[findPath] = gitHubVcsRoot ?: DUMMY_REPO
        MdProjectComponent.LOG.debug { "gitHubRepo $url for ${gitHubVcsRoot?.basePath}" }
        return gitHubVcsRoot
    }

    fun isUnderVcs(virtualFile: VirtualFile): Boolean {
        val status = FileStatusManager.getInstance(project).getStatus(virtualFile)
        val id = status.id
        val fileStatus = status == FileStatus.DELETED || status == FileStatus.ADDED || status == FileStatus.UNKNOWN || status == FileStatus.IGNORED || id.startsWith("IGNORE")
        return !fileStatus
    }

    private fun isUnderVcsSynced(virtualFile: VirtualFile): Boolean {
        val status = FileStatusManager.getInstance(project).getStatus(virtualFile)
        val id = status.id
        val fileStatus = status == FileStatus.DELETED || status == FileStatus.ADDED || status == FileStatus.UNKNOWN || status == FileStatus.IGNORED || id.startsWith("IGNORE") || status == FileStatus.MODIFIED
        // FIX: figure out how to get status of local and remote files
        //        val branchesSynced:Boolean = !fileStatus && (getVcsRoot(FileRef(virtualFile))?.getIsRemoteBranchInSync() ?: false)
        return !fileStatus
    }

    override fun isUnderVcs(fileRef: FileRef): Boolean {
        val projectFileRef = fileRef.projectFileRef(project)
        return projectFileRef != null && isUnderVcs(projectFileRef.virtualFile)
    }

    override fun isUnderVcsSynced(fileRef: FileRef): Boolean {
        val projectFileRef = fileRef.projectFileRef(project)
        return projectFileRef != null && isUnderVcsSynced(projectFileRef.virtualFile)
    }

    override fun getVcsRoot(fileRef: FileRef): GitHubVcsRoot? {
        return getGitHubRepo(fileRef.path)
    }

    override fun getVcsRootForUrl(url: String): GitHubVcsRoot? {
        return getGitHubRepoFromURL(url)
    }

    override fun vcsRootBase(fileRef: FileRef): String? {
        val gitHubVcsRoot = getGitHubRepo(fileRef.path)
        return gitHubVcsRoot?.basePath
    }

    override val projectBasePath: String
        get() {
            val basePath = if (project.isDisposed) null else project.basePath
            return basePath ?: ""
        }

    override fun vcsRepoBasePath(fileRef: FileRef): String? {
        val gitHubVcsRoot = getGitHubRepo(fileRef.path)
        return gitHubVcsRoot?.mainRepoBaseDir
    }

    override fun projectFileList(fileTypes: List<String>?): List<FileRef>? {
        assert(false) { "Should never be called" }
        return null
    }

    private fun updateVcsRoots() {
        if (project.isDisposed) return

        // this should really be the place that creates gitHubVcsRoots
        val gitHubVcsMap = ConcurrentHashMap<String, GitHubVcsRoot>()
        // DEPRECATED: added 2019.08, when available change to
//        val repoManager: GitRepositoryManager = project.getService(GitRepositoryManager::class.java)
        val repoManager: GitRepositoryManager = ServiceManager.getService(project, GitRepositoryManager::class.java)
        for (gitRepository in repoManager.repositories) {
            // create our GitHubVcsRoot
            val gitHubVcsRoot = GitHubVcsRoot.create(gitRepository)
            MdProjectComponent.LOG.debug { "updateVcsRoot: $gitRepository, gitHubVcsRoot: $gitHubVcsRoot" }

            if (gitHubVcsRoot != null) {
                gitHubVcsMap[gitHubVcsRoot.basePath] = gitHubVcsRoot
            }
        }

        clearVcsMaps(gitHubVcsMap)
        rescanProjectDirectories = true
    }

    override fun dispose() {
        onProjectSettingsChangedActivities.clear()
    }

    private fun fileSystemChanged(file: VirtualFile) {
        if (project.isDisposed) return
        if (file.isDirectory) rescanProjectDirectories = true
    }

    internal fun projectInitialized() {
        @Suppress("DEPRECATION")
        // DEPRECATED: use VFS_CHANGES topic, available since 2017-11-07
        VirtualFileManager.getInstance().addVirtualFileListener(object : VirtualFileListener {
            override fun propertyChanged(event: VirtualFilePropertyEvent) {
                // NOTE: this one fires often when document is modified with writeable property changing
                // fileSystemChanged(event.file)
            }

            override fun contentsChanged(event: VirtualFileEvent) {
                fileSystemChanged(event.file)
            }

            override fun fileCreated(event: VirtualFileEvent) {
                fileSystemChanged(event.file)
            }

            override fun fileDeleted(event: VirtualFileEvent) {
                // issue #776, JavaFx Preview displays cached image for deleted file
                fileSystemChanged(event.file)
            }

            override fun fileMoved(event: VirtualFileMoveEvent) {
                fileSystemChanged(event.file)
            }

            override fun fileCopied(event: VirtualFileCopyEvent) {
                fileSystemChanged(event.file)
            }
        }, project)

        val messageBusConnection = project.messageBus.connect(project)

        messageBusConnection.subscribe(GitRepository.GIT_REPO_CHANGE, GitRepositoryChangeListener { repository ->
            // diagnostic/
            if (project.isDisposed) return@GitRepositoryChangeListener

            // repository changed
            val repoSet = HashSet<GitHubVcsRoot>()
            repoSet.addAll(gitHubVcsRoots.values)

            MdProjectComponent.LOG.debug { "gitRepositoryChanged: $repository" }

            var needReparse = false
            for (gitRepo in repoSet) {
                if (gitRepo.onRepositoryChange(repository)) {
                    needReparse = true
                    break
                }
            }

            if (needReparse) {
                project.messageBus.syncPublisher(MdRepoChangeListener.TOPIC).onRepoChanged()
            }
        })

        messageBusConnection.subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, VcsListener { updateVcsRoots() })
        messageBusConnection.subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED_IN_PLUGIN, VcsListener { updateVcsRoots() })

        ApplicationManager.getApplication().invokeLater({
            if (!project.isDisposed) {
                MdPlugin.instance.projectLoaded(project)
            }
        }, ModalityState.NON_MODAL)

        // Listen to settings changes
        messageBusConnection.subscribe(ProjectSettingsChangedListener.TOPIC, object : ProjectSettingsChangedListener {
            override fun onSettingsChange(project: Project, settings: MdProjectSettings) {
                if (project.isDisposed) return
                onProjectSettingsChanged(false)
            }
        })

        messageBusConnection.subscribe(ProfileManagerChangeListener.TOPIC, object : ProfileManagerChangeListener {
            override fun onSettingsLoaded(manager: RenderingProfileManager) {
            }

            override fun onSettingsChange(manager: RenderingProfileManager) {
                if (project.isDisposed) return
                onProjectSettingsChanged(false)
            }
        })

        // initialize our activities
        for (provider in MdOnProjectSettingsChangedActivityProvider.EXTENSIONS.value) {
            val activity = provider.getProjectSettingsChangedActivity(project)
            onProjectSettingsChangedActivities.add(activity)
            Disposer.register(this, activity)
        }

        // can now update the roots
        updateVcsRoots()
        onProjectSettingsChanged(true)
    }

    private fun onProjectSettingsChanged(firstLoad: Boolean) {
        // NOTE: in unit tests for full spec test repeated option changes re-trigger parse in middle of IDE code handling file changes
        //   to prevent failure this trigger is disabled in unit tests
        if (!project.isDisposed && !ApplicationManager.getApplication().isUnitTestMode) {
            projectSettingsChangedAlarm.cancelAllRequests()

            if (!projectSettingsChangedAlarm.isDisposed) {
                projectSettingsChangedAlarm.addRequest({
                    if (!project.isDisposed) {
                        MdProjectComponent.getInstance(project).reparseMarkdown(true)
                        runOnProjectSettingsChangedActivities(firstLoad)
                    }
                }, 100, ModalityState.NON_MODAL)
            }
        }
    }

    private fun runOnProjectSettingsChangedActivities(firstLoad: Boolean) {
        val opened = firstLoad.ifElse(" Opened", "")
        for (activity in onProjectSettingsChangedActivities) {
            val prevStart = inProjectSettingsChangedActivity[activity]
            if (prevStart == null) {
                LOG.info("On Project$opened Settings Activity $activity started")
                inProjectSettingsChangedActivity[activity] = System.nanoTime()
                activity.projectSettingsChanged(project, firstLoad) { activityDone(opened, activity) }
            } else {
                val endTime = System.nanoTime()
                val runTime = (endTime - prevStart) / 1000000f
                if (firstLoad) {
                    LOG.error("On Project$opened Settings Activity $activity skipped, should not be active, started $runTime ms ago.")
                } else {
                    if (runTime > 1000f) {
                        LOG.error("On Project$opened Settings Activity $activity skipped, started $runTime ms ago still going.")
                    } else {
                        LOG.warn("On Project$opened Settings Activity $activity skipped, started $runTime ms ago still going.")
                    }
                }
            }
        }
    }

    private fun activityDone(opened: String, activity: MdOnProjectSettingsChangedActivity) {
        val endTime = System.nanoTime()
        val startTime = inProjectSettingsChangedActivity.remove(activity)
        if (startTime == null) {
            LOG.error("On Project$opened Settings Activity $activity invoked completion again.")
        } else {
            LOG.info("On Project$opened Settings Activity $activity done in ${(endTime - startTime) / 1000000f} ms")
        }
    }
}
