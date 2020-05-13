// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.vladsch.md.nav.settings.api.MdProjectSettingsExtensionProvider;
import com.vladsch.plugin.util.HelpersKt;
import com.vladsch.plugin.util.LazyFunction;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "MarkdownProjectSettings",
        storages = {
                @Storage(value = MdProjectSettingsManager.MARKDOWN_NAVIGATOR_XML)
        })
public class MdProjectSettingsManager implements PersistentStateComponent<Element> {
    private static final Logger LOG = Logger.getInstance("com.vladsch.md.nav.settings");

    public static final String MARKDOWN_NAVIGATOR_XML = "markdown-navigator.xml";

    final private @NotNull Project myProject;
    final private @NotNull MdProjectSettings myProjectSettings;
    private boolean myProjectSettingsLoaded = false;
    private Element myPendingState;

    public MdProjectSettingsManager(@NotNull Project project) {
        myProject = project;
        myProjectSettings = new MdProjectSettings(project.isDefault() ? null : project);
        LOG.debug("MdProjectSettingsManager.constructor:" + myProject.getBasePath() + myProjectSettings.getHashCodeId());
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
                    LOG.debug("MdProjectSettingsManager.ensureExtensionsLoaded:" + myProject.getBasePath() + myProjectSettings.getHashCodeId());
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
            LOG.debug("MdProjectSettingsManager.getState delayed:" + myProject.getBasePath() + myProjectSettings.getHashCodeId());
            return myPendingState;
        } else {
            myPendingState = null;

            if (myProjectSettings.getRenderingProfile().isDefault()) {
                LOG.debug("MdProjectSettingsManager.getState default:" + myProject.getBasePath() + myProjectSettings.getHashCodeId());
                return new Element("settings");
            } else {
                LOG.debug("MdProjectSettingsManager.getState:" + myProject.getBasePath() + myProjectSettings.getHashCodeId());
                Element state = myProjectSettings.saveState(null);
                HelpersKt.debug(LOG, () -> "MdProjectSettingsManager.getState savedState:" + myProject.getBasePath() + myProjectSettings.getHashCodeId()
                        + "\n" + new XMLOutputter().outputString(state));
                return state;
            }
        }
    }

    @Override
    public void loadState(@NotNull Element state) {
        if (!myProjectSettingsLoaded) {
            // need to save this state so we can return it when state is requested so as not to overwrite extension settings
            myPendingState = state.clone();
        }

        if (state.getChildren().size() > 0) {
            HelpersKt.debug(LOG, () -> "MdProjectSettingsManager.loadState:" + myProject.getBasePath() + myProjectSettings.getHashCodeId() + "\n" + new XMLOutputter().outputString(state));

            // NOTE: prevent enhanced and normal settings from being loaded in parallel by different threads
            synchronized (myProjectSettings) {
                myProjectSettings.loadState(state);
            }

            ApplicationManager.getApplication().invokeLater(() -> {
                myProjectSettings.validateLoadedSettings();
                HelpersKt.debug(LOG, () -> {
                    Element savedState = myProjectSettings.saveState(null);
                    return "MdProjectSettingsManager.loadState validated savedState:" + myProject.getBasePath() + myProjectSettings.getHashCodeId() + "\n" + new XMLOutputter().outputString(savedState);
                });
            });

            HelpersKt.debug(LOG, () -> {
                Element savedState = myProjectSettings.saveState(null);
                return "MdProjectSettingsManager.loadState savedState:" + myProject.getBasePath() + myProjectSettings.getHashCodeId() + "\n" + new XMLOutputter().outputString(savedState);
            });
        } else {
            HelpersKt.debug(LOG, () -> "MdProjectSettingsManager.loadState default:" + myProject.getBasePath() + myProjectSettings.getHashCodeId() + "\n" + new XMLOutputter().outputString(state));
            myProjectSettings.getRenderingProfile().setRenderingProfile(MdRenderingProfile.getDEFAULT());
        }
    }
}
