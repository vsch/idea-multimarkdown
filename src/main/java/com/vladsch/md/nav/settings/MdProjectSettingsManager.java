// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
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
    private Element myPendingState;

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

                    for (MdProjectSettingsExtensionProvider provider : MdProjectSettingsExtensionProvider.EXTENSIONS.getValue()) {
                        provider.initializeProjectSettingsService(myProjectSettings);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public Element getState() {
        if (!myProjectSettingsLoaded) {
            LOG.debug("MdProjectSettingsManager.getState delayed");
            return myPendingState;
        } else {
            myPendingState = null;

            if (myProjectSettings.getRenderingProfile().isDefault()) {
                LOG.debug("MdProjectSettingsManager.getState default");
                return new Element("settings");
            } else {
                LOG.debug("MdProjectSettingsManager.getState");
                Element state = myProjectSettings.saveState(null);
                return state;
            }
        }
    }

    @Override
    public void loadState(@NotNull Element state) {
        if (!myProjectSettingsLoaded) {
            // need to save this state so we can return it when state is requested so as not to overwrite extension settings
            myPendingState = state;
        }

        if (state.getChildren().size() > 0) {
            LOG.debug("MdProjectSettingsManager.loadState");
            myProjectSettings.loadState(state);
            ApplicationManager.getApplication().invokeLater(myProjectSettings::validateLoadedSettings);
        } else {
            LOG.debug("MdProjectSettingsManager.loadState default");
            myProjectSettings.getRenderingProfile().setRenderingProfile(MdRenderingProfile.getDEFAULT());
        }
    }
}
