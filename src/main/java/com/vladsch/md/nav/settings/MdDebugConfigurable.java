// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Disposer;
import com.vladsch.md.nav.MdBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class MdDebugConfigurable implements SearchableConfigurable {
    @Nullable private MdDebugSettingsForm myForm = null;
    private final MdApplicationSettings myApplicationSettings;

    public MdDebugConfigurable() {
        myApplicationSettings = MdApplicationSettings.getInstance();
    }

    @NotNull
    @Override
    public String getId() {
        return "MarkdownNavigator.Settings.Debug";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return MdBundle.message("settings.markdown.debug.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "com.vladsch.markdown.navigator.settings.debug";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        return getForm().getComponent();
    }

    @NotNull
    public MdDebugSettingsForm getForm() {
        if (myForm == null) {
            myForm = new MdDebugSettingsForm(myApplicationSettings, RenderingProfileSynchronizer.getInstance(ProjectManager.getInstance().getDefaultProject()));
        }
        return myForm;
    }

    @Override
    public boolean isModified() {
        return getForm().isModified(myApplicationSettings);
    }

    @Override
    public void apply() throws ConfigurationException {
        myApplicationSettings.groupNotifications(() -> {
            getForm().apply(myApplicationSettings);
            myApplicationSettings.setDebugSettings(myApplicationSettings.getDebugSettings());
        });
    }

    @Override
    public void reset() {
        getForm().reset(myApplicationSettings);
    }

    @Override
    public void disposeUIResources() {
        if (myForm != null) {
            Disposer.dispose(myForm);
            myForm = null;
        }
    }
}
