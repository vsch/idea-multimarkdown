// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
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
    private static final Logger LOG = Logger.getInstance("com.vladsch.md.nav.settings");

    public static final String MARKDOWN_NAVIGATOR_XML = "markdown-navigator.xml";

    final private @NotNull MdProjectSettings myProjectSettings;
    private boolean myProjectSettingsLoaded = false;

    @SuppressWarnings("FieldCanBeLocal")
    private final LazyFunction<MdProjectSettings, Boolean> myExtensionsInitialized = new LazyFunction<>(projectSettings -> {
        for (MdProjectSettingsExtensionProvider provider : MdProjectSettingsExtensionProvider.EXTENSIONS.getValue()) {
            provider.initializeProjectSettingsService(projectSettings);
        }
        return true;
    });

    /**
     * Should not be called other than from service creation.
     *
     * @param project project
     */
    public MdProjectSettingsManager(@NotNull Project project) {
        LOG.debug("MdProjectSettingsManager.constructor");
        myProjectSettings = new MdProjectSettings(project.isDefault() ? null : project);
    }

    @NotNull
    public MdProjectSettings getProjectSettings() {
        return myProjectSettings;
    }

    public final static LazyFunction<Project, MdProjectSettingsManager> NULL = new LazyFunction<>(MdProjectSettingsManager::new);

    @NotNull
    static MdProjectSettingsManager getInstance(@NotNull Project project) {
        MdProjectSettingsManager service;
        if (project.isDefault()) service = NULL.getValue(project);
        else service = project.getService(MdProjectSettingsManager.class);
        service.ensureExtensionsLoaded();
        return service;
    }

    private void ensureExtensionsLoaded() {
        if (!myProjectSettingsLoaded) {
            synchronized (myProjectSettings) {
                if (!myProjectSettingsLoaded) {
                    LOG.debug("MdProjectSettingsManager.ensureExtensionsLoaded");
                    myProjectSettingsLoaded = true;
                    myExtensionsInitialized.getValue(myProjectSettings);
                }
            }
        }
    }

    @Nullable
    @Override
    public Element getState() {
        LOG.debug("MdProjectSettingsManager.getState");

        if (myProjectSettings.getRenderingProfile().isDefault()) {
            return new Element("settings");
        }

        Element state = myProjectSettings.saveState(null);
        return state;
    }

    @Override
    public void loadState(Element state) {
        LOG.debug("MdProjectSettingsManager.loadState");

        if (state.getChildren().size() > 0) {
            myProjectSettings.loadState(state);
            ApplicationManager.getApplication().invokeLater(myProjectSettings::validateLoadedSettings);
        } else {
            myProjectSettings.getRenderingProfile().setRenderingProfile(MdRenderingProfile.getDEFAULT());
        }
    }
}
