// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.Alarm;
import com.intellij.util.messages.MessageBusConnection;
import com.vladsch.md.nav.parser.cache.data.dependency.DataKeyDependency;
import com.vladsch.md.nav.parser.cache.data.dependency.ProjectFileDependency;
import com.vladsch.md.nav.parser.cache.data.dependency.ProjectFilePredicate;
import com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger;
import com.vladsch.plugin.util.HelpersKt;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ProjectFileMonitor implements Disposable {
    private static final Logger LOG = Logger.getInstance("com.vladsch.md.nav.parser.cache");
    static final Logger LOG_CACHE_DETAIL = IndentingLogger.LOG_COMPUTE_DETAIL;
    static final Logger LOG_CACHE = IndentingLogger.LOG_COMPUTE_RESULT;

    public static final PsiFile[] EMPTY_PSI_FILES = new PsiFile[0];

    @NotNull
    public static ProjectFileMonitor getInstance(Project project) {
        // DEPRECATED: added 2019.08, when available change to
//        project.getService(ProjectPsiMonitor.class);
        return ServiceManager.getService(project, ProjectFileMonitor.class);
    }

    final @NotNull Project myProject;
    final @NotNull HashSet<ProjectFileDependency> myDataKeyDependencies = new HashSet<>();
    final Alarm myAlarm;

    public ProjectFileMonitor(@NotNull Project project) {
        myProject = project;
        Disposer.register(myProject, this);
        myAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, myProject);

        final Application application = ApplicationManager.getApplication();
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(this);

        messageBusConnection.subscribe(DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC, new DaemonCodeAnalyzer.DaemonListener() {
            /**
             * Fired when the background code analysis is being scheduled for the specified set of files.
             *
             * @param fileEditors The list of files that will be analyzed during the current execution of the daemon.
             */
            @Override
            public void daemonStarting(@NotNull Collection<FileEditor> fileEditors) {
                PsiManager psiManager = PsiManagerEx.getInstance(myProject);
                for (FileEditor fileEditor : fileEditors) {
                    VirtualFile virtualFile = fileEditor.getFile();
                    // ISSUE: diagnostic/4439
                    if (virtualFile != null && !(virtualFile instanceof LightVirtualFile) && virtualFile.isValid()) {
                        PsiFile psiFile = psiManager.findFile(virtualFile);
                        if (psiFile != null) {
                            checkDependencies(true, psiFile);
                        }
                    }
                }
            }
        });

        application.getMessageBus().connect(this).subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                ArrayList<VirtualFile> virtualFiles = new ArrayList<>();
                PsiManager psiManager = PsiManagerEx.getInstance(myProject);
                for (VFileEvent event : events) {
                    VirtualFile virtualFile = event.getFile();
                    if (virtualFile != null && virtualFile.isValid()) {
                        virtualFiles.add(virtualFile);
                    }
                }

                if (!virtualFiles.isEmpty() && !myAlarm.isDisposed()) {
                    myAlarm.addRequest(() -> {
                        if (myProject.isDisposed()) return;

                        for (VirtualFile virtualFile : virtualFiles) {
                            if (virtualFile.isValid()) {
                                PsiFile psiFile = psiManager.findFile(virtualFile);
                                if (psiFile != null) {
                                    checkDependencies(false, psiFile);
                                }
                            }
                        }
                        // NOTE: in tests there should be no delay otherwise the possible rescan
                        //  is triggered in the middle of highlighting for full spec test case
                        //  for release the delay is added just in case there was an external
                        //  jar modification and the jar needs to be re-indexed, otherwise an
                        //  exception that stub is invalid is generated by the IDE.
                    }, application.isUnitTestMode() ? 0 : 100, ModalityState.NON_MODAL);
                }
            }
        });
    }

    @Override
    public void dispose() {
        myDataKeyDependencies.clear();
    }

    public void checkDependencies(boolean fromDaemonCodeAnalyzer, @NotNull PsiFile psiFile) {
        HashSet<ProjectFileDependency> dependencies;

        synchronized (myDataKeyDependencies) {
            dependencies = new HashSet<>(myDataKeyDependencies);
        }

        for (ProjectFileDependency dependency : dependencies) {
            boolean isValid = false;

            DataKeyDependency keyDependency = dependency.getDataKeyDependency();
            if (keyDependency.isValid()) {
                // it is still valid, so we need to invalidate it
                try {
                    if (dependency.getFilePredicate().test(psiFile)) {
                        isValid = true;
                    }
                } catch (Throwable e) {
                    LOG.error("Project file dependency exception " + dependency, e);
                }
            }

            if (!isValid) {
                synchronized (myDataKeyDependencies) {
                    myDataKeyDependencies.remove(dependency);
                }

                dependency.getDataKeyDependency().invalidateDependency();

                PsiFile dependentFile = dependency.getFilePredicate().getDependentFile();
                String onFilePath = psiFile.getVirtualFile().getPath();

                if (dependentFile != null && dependentFile.isValid()) {
                    boolean selfInvalidation = dependentFile.equals(psiFile);
                    if (selfInvalidation) onFilePath = "<self>";

                    String finalOnFilePath = onFilePath;
                    HelpersKt.debug(LOG_CACHE, () -> String.format("ProjectFileMonitor(%s): Removing project file cache monitor for %s on %s", fromDaemonCodeAnalyzer ? "analyzer" : "vfs", dependentFile.getVirtualFile().getPath(), finalOnFilePath));

                    Project project = dependentFile.getProject();
                    if (!project.isDisposed() && dependentFile.isValid() && !selfInvalidation) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            if (!project.isDisposed()) {
                                DaemonCodeAnalyzer.getInstance(project).restart(dependentFile);
                                HelpersKt.debug(LOG_CACHE, () -> String.format("ProjectFileMonitor(%s): Restarted code analyzer for %s on %s", fromDaemonCodeAnalyzer ? "analyzer" : "vfs", dependentFile.getVirtualFile().getPath(), finalOnFilePath));
                            }
                        });
                    }
                } else {
                    String finalOnFilePath1 = onFilePath;
                    HelpersKt.debug(LOG_CACHE, () -> String.format("ProjectFileMonitor(%s): Removing project file cache monitor for %s on %s", fromDaemonCodeAnalyzer ? "analyzer" : "vfs", dependency.getDataKeyDependency(), finalOnFilePath1));
                }
            }
        }
    }

    @NotNull
    public ProjectFileDependency getDependency(@NotNull DataKeyDependency dependency, @NotNull ProjectFilePredicate predicate) {
        ProjectFileDependency fileDependency = new ProjectFileDependency(dependency, predicate);
        synchronized (myDataKeyDependencies) {
            myDataKeyDependencies.add(fileDependency);
        }
        return fileDependency;
    }
}
