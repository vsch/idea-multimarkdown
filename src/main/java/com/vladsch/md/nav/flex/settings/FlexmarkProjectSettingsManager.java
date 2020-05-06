// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.vladsch.md.nav.settings.ComponentItemHolder;
import com.vladsch.md.nav.settings.MdProjectSettingsManager;
import com.vladsch.md.nav.settings.TagItemHolder;
import com.vladsch.md.nav.settings.UnwrappedSettings;
import com.vladsch.plugin.util.LazyFunction;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = FlexmarkProjectSettingsManager.STATE_ELEMENT_NAME, storages = {
        @Storage(value = MdProjectSettingsManager.MARKDOWN_NAVIGATOR_XML),
})
public class FlexmarkProjectSettingsManager implements PersistentStateComponent<Element> {
    public static final String STATE_ELEMENT_NAME = "FlexmarkProjectSettings";
    final private Project myProject;

    public FlexmarkProjectSettingsManager(@NotNull Project project) {
        myProject = project;
        // NOTE: cannot use get instance for project manager because it will create an infinite loop
        FlexmarkHtmlSettings flexmarkHtmlSettings = ServiceManager.getService(myProject, MdProjectSettingsManager.class).getProjectSettings().getHtmlSettings().getExtension(FlexmarkHtmlSettings.KEY);
        flexmarkHtmlSettings.copyFrom(new FlexmarkHtmlSettings());
    }

    final private static LazyFunction<Project, FlexmarkProjectSettingsManager> NULL = new LazyFunction<>(FlexmarkProjectSettingsManager::new);

    @NotNull
    public static FlexmarkProjectSettingsManager getInstance(@NotNull Project project) {
        if (project.isDefault()) return NULL.getValue(project);
            // DEPRECATED: added 2019.08, when available change to
//        project.getService(MdHistoryManager.class);
        else return ServiceManager.getService(project, FlexmarkProjectSettingsManager.class);
    }

    @Nullable
    @Override
    public Element getState() {
        // NOTE: cannot use get instance for project manager because it will create an infinite loop
        FlexmarkHtmlSettings flexmarkHtmlSettings = ServiceManager.getService(myProject, MdProjectSettingsManager.class).getProjectSettings().getHtmlSettings().getExtension(FlexmarkHtmlSettings.KEY);
        TagItemHolder settings = new ComponentItemHolder().addItems(new UnwrappedSettings<>(flexmarkHtmlSettings));
        Element state = settings.saveState(null);
        return state;
    }

    @Override
    public void loadState(@NotNull Element state) {
        // NOTE: cannot use get instance for project manager because it will create an infinite loop
        FlexmarkHtmlSettings flexmarkHtmlSettings = ServiceManager.getService(myProject, MdProjectSettingsManager.class).getProjectSettings().getHtmlSettings().getExtension(FlexmarkHtmlSettings.KEY);
        TagItemHolder settings = new ComponentItemHolder().addItems(new UnwrappedSettings<>(flexmarkHtmlSettings));
        settings.loadState(state);
        //noinspection UnnecessaryReturnStatement
        return;
    }
}
