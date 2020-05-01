// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.vladsch.md.nav.settings.api.MdProjectSettingsExtensionProvider;
import com.vladsch.plugin.util.LazyFunction;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "MarkdownProjectSettings",
        storages = {
                @Storage(value = MdProjectSettingsManager.MARKDOWN_NAVIGATOR_XML)
        })
public class MdProjectSettingsManager implements PersistentStateComponent<Element> {
    public static final String MARKDOWN_NAVIGATOR_XML = "markdown-navigator.xml";

    final private MdProjectSettings myProjectSettings;
    private boolean myProjectSettingsLoaded = false;

    @SuppressWarnings("FieldCanBeLocal")
    private final LazyFunction<Project, Boolean> myExtensionsInitialized = new LazyFunction<>(project -> {
        for (MdProjectSettingsExtensionProvider provider : MdProjectSettingsExtensionProvider.EXTENSIONS.getValue()) {
            provider.initializeProjectSettingsService(project);
        }
        return true;
    });

    public boolean isProjectSettingsLoaded() {
        return myProjectSettingsLoaded;
    }

    public void setProjectSettingsLoaded(final boolean projectSettingsLoaded) {
        myProjectSettingsLoaded = projectSettingsLoaded;
    }

    public MdProjectSettingsManager(@NotNull Project project) {
        myProjectSettings = new MdProjectSettings(project.isDefault() ? null : project);
    }

    public MdProjectSettings getProjectSettings() {
        return myProjectSettings;
    }

    public final static LazyFunction<Project, MdProjectSettingsManager> NULL = new LazyFunction<>(MdProjectSettingsManager::new);

    @NotNull
    public static MdProjectSettingsManager getInstance(@NotNull Project project) {
        MdProjectSettingsManager service;
        if (project.isDefault()) service = NULL.getValue(project);
            // DEPRECATED: added 2019.08, when available change to
//        project.getService(MdProjectSettingsManager.class);
        else service = ServiceManager.getService(project, MdProjectSettingsManager.class);
        service.myExtensionsInitialized.getValue(project);
        return service;
    }

    @Nullable
    @Override
    public Element getState() {
        if (myProjectSettings.getRenderingProfile().isDefault()) {
            return new Element("settings");
        }

        Element state = myProjectSettings.saveState(null);
        return state;
    }

    @Override
    public void loadState(Element state) {
        if (/*!myProject.isDefault() &&*/ state.getChildren().size() > 0) {
            myProjectSettings.loadState(state);
            ApplicationManager.getApplication().invokeLater(myProjectSettings::validateLoadedSettings);
        } else {
            myProjectSettings.getRenderingProfile().setRenderingProfile(MdRenderingProfile.getDEFAULT());
        }
    }
}
