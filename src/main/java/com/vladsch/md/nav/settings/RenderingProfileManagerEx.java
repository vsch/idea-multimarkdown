// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.search.scope.packageSet.NamedScope;
import com.vladsch.md.nav.settings.api.MdRenderingProfileManagerFactory;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface RenderingProfileManagerEx extends RenderingProfileManager {
    String STATE_ELEMENT_NAME = "settings";

    MdRenderingProfileManagerFactory[] ourFactory = { null };

    static MdRenderingProfileManagerFactory getFactory() {
        if (ourFactory[0] != null) return ourFactory[0];

        for (MdRenderingProfileManagerFactory managerFactory : MdRenderingProfileManagerFactory.EXTENSIONS.getValue()) {
            ourFactory[0] = managerFactory;
            return managerFactory;
        }

        throw new IllegalStateException("No RenderingProfileManagerEx factory defined");
    }

    @NotNull
    static RenderingProfileManagerEx getInstance(@Nullable Project project) {
        Project useProject = project == null ? ProjectManager.getInstance().getDefaultProject() : project;
        return (RenderingProfileManagerEx) getFactory().getInstance(useProject);
    }

    Map<String, String> getProfileMapping();

    void setDefaultRenderingProfile(@Nullable MdRenderingProfile renderingProfile);

    @Nullable
    Element getState();

    void loadState(Element state);

    @Override
    @NotNull
    MdRenderingProfile getDefaultRenderingProfile();

    @Nullable
    MdRenderingProfile getPdfRenderingProfile();

    @Nullable
    MdRenderingProfile getResolvedPdfRenderingProfile();

    void setPdfRenderingProfile(@Nullable MdRenderingProfile pdfRenderingProfile);

    @NotNull
    String getPlainTextCompletionScopeName();

    @NotNull
    NamedScope getPlainTextCompletionScope();

    void setPlainTextCompletionScopeName(@Nullable String plainTextCompletionScope);

    void addProfile(MdRenderingProfile renderingProfile);

    void removeProfile(MdRenderingProfile renderingProfile);

    void clearProfiles();

    void mapProfile(String scopeName, String renderingProfileName);

    void unmapProfile(String scopeName);

    Collection<MdRenderingProfile> getRenderingProfiles();
}
