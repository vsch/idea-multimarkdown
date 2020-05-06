// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.vladsch.md.nav.settings.api.MdRenderingProfileManagerFactory;
import com.vladsch.plugin.util.LazyComputable;
import com.vladsch.plugin.util.LazyFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MdRenderingProfileManager implements RenderingProfileManager, Disposable {
    private static final Logger LOG = Logger.getInstance("#" + MdRenderingProfileManager.class.getName());

    final private static LazyFunction<Project, MdRenderingProfileManager> NULL = new LazyFunction<>(MdRenderingProfileManager::new);
    final private static LazyComputable<MdRenderingProfileManagerFactory> ourFactory = new LazyComputable<>(() -> {
        for (MdRenderingProfileManagerFactory managerFactory : MdRenderingProfileManagerFactory.EXTENSIONS.getValue()) {
            return managerFactory;
        }

        return project -> {
            if (project.isDefault()) {
                return NULL.getValue(project);
            }
            // DEPRECATED: added 2019.08, when available change to
//            return project.getService(MdRenderingProfileManager.class);
            return ServiceManager.getService(project, MdRenderingProfileManager.class);
        };
    });

    @NotNull
    public static RenderingProfileManager getInstance(@Nullable Project project) {
        Project useProject = project == null ? ProjectManager.getInstance().getDefaultProject() : project;
        return ourFactory.getValue().getInstance(useProject);
    }

    @NotNull
    public static MdRenderingProfile getProfile(@NotNull PsiFile file) {
        return getInstance(file.getProject()).getRenderingProfile(file);
    }

    @NotNull
    public static MdRenderingProfile getProfile(@NotNull Project project, @NotNull VirtualFile file) {
        return getInstance(project).getRenderingProfile(file);
    }

    @NotNull
    public static MdRenderingProfile getProfile(@NotNull Project project) {
        return getInstance(project).getDefaultRenderingProfile();
    }

    private int groupNotifications = 0;
    private boolean havePendingSettingsChanged = false;
    @NotNull final protected Project myProject;
    final private HashMap<VirtualFile, MdRenderingProfile> myCustomProfiles = new HashMap<>();

    @Override
    public void dispose() {
        myCustomProfiles.clear();
    }

    public MdRenderingProfileManager(@NotNull Project project) {
        myProject = project;
    }

    @Override
    final public void groupNotifications(Runnable runnable) {
        startGroupChangeNotifications();
        try {
            runnable.run();
        } finally {
            endGroupChangeNotifications();
        }
    }

    protected void startGroupChangeNotifications() {
        if (groupNotifications == 0) {
            havePendingSettingsChanged = false;
        }
        groupNotifications++;
    }

    protected void endGroupChangeNotifications() {
        assert groupNotifications > 0 : "endGroupNotifications called when groupNotifications is " + groupNotifications;

        groupNotifications--;
        if (groupNotifications == 0) {
            if (havePendingSettingsChanged) notifyOnSettingsChanged();
        }
    }

    protected void notifyOnSettingsChanged() {
        if (groupNotifications > 0) havePendingSettingsChanged = true;
        else {
            myProject.getMessageBus().syncPublisher(ProfileManagerChangeListener.TOPIC).onSettingsChange(this);
        }
    }

    protected void notifyOnSettingsLoaded() {
        if (groupNotifications > 0) havePendingSettingsChanged = true;
        else {
            myProject.getMessageBus().syncPublisher(ProfileManagerChangeListener.TOPIC).onSettingsLoaded(this);
        }
    }

    @Override
    public void copyFrom(RenderingProfileManager other) {
        havePendingSettingsChanged = false;
        notifyOnSettingsLoaded();
        notifyOnSettingsChanged();
    }

    @Override
    public boolean hasAnyProfiles() {
        return false;
    }

    @Override
    final public void registerRenderingProfile(@NotNull final VirtualFile virtualFile, @NotNull final MdRenderingProfile renderingProfile) {
        if (virtualFile.isValid()) myCustomProfiles.put(virtualFile, renderingProfile);
        clearInvalidFiles();
    }

    @Override
    final public void unregisterRenderingProfile(@NotNull final VirtualFile virtualFile) {
        myCustomProfiles.remove(virtualFile);
        clearInvalidFiles();
    }

    private void clearInvalidFiles() {
        if (!myCustomProfiles.isEmpty()) {
            synchronized (myCustomProfiles) {
                HashMap<VirtualFile, MdRenderingProfile> keep = null;
                for (VirtualFile file : myCustomProfiles.keySet()) {
                    if (file != null && file.isValid()) {
                        if (keep == null) keep = new HashMap<>();
                        keep.put(file, myCustomProfiles.get(file));
                    } else {
                        LOG.warn("Custom profile for virtual file " + file + " was not unregistered");
                    }
                }
                myCustomProfiles.clear();
                if (keep != null) myCustomProfiles.putAll(keep);
            }
        }
    }

    @Override
    final public void registerRenderingProfile(@NotNull final PsiFile psiFile, @NotNull final MdRenderingProfile renderingProfile) {
        forVirtualFileOf(psiFile, (virtualFile -> registerRenderingProfile(virtualFile, renderingProfile)));
    }

    @Override
    final public void unregisterRenderingProfile(@NotNull final PsiFile psiFile) {
        forVirtualFileOf(psiFile, this::unregisterRenderingProfile);
    }

    @Nullable
    final protected MdRenderingProfile getCustomRenderingProfile(@Nullable VirtualFile virtualFile) {
        return virtualFile == null || !virtualFile.isValid() ? null : myCustomProfiles.get(virtualFile);
    }

    @Override
    @NotNull
    public MdRenderingProfile getRenderingProfile(@Nullable VirtualFile virtualFile) {
        MdRenderingProfile profile = getCustomRenderingProfile(virtualFile);
        return profile == null ? getDefaultRenderingProfile() : profile;
    }

    @NotNull
    @Override
    public MdRenderingProfile getDefaultRenderingProfile() {
        return MdProjectSettings.getInstance(myProject).getRenderingProfile();
    }

    @Nullable
    final protected MdRenderingProfile getCustomRenderingProfile(@Nullable PsiFile psiFile) {
        MdRenderingProfile[] renderingProfiles = { null };
        forVirtualFileOf(psiFile, virtualFile -> {
            if (renderingProfiles[0] == null) renderingProfiles[0] = getCustomRenderingProfile(virtualFile);
        });

        return renderingProfiles[0];
    }

    @Override
    @NotNull
    public MdRenderingProfile getRenderingProfile(@Nullable PsiFile psiFile) {
        MdRenderingProfile profile = getCustomRenderingProfile(psiFile);
        return profile == null ? getDefaultRenderingProfile() : profile;
    }

    @Override
    @Nullable
    public MdRenderingProfile getRenderingProfile(@Nullable String name) {
        return null;
    }

    @Override
    @NotNull
    final public Project getProject() {
        return myProject;
    }

    @Override
    public void replaceProfile(String displayName, MdRenderingProfile renderingProfile) {
        // not implemented
    }
}
