// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.vladsch.md.nav.MdBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class MdParserConfigurable extends MdProjectConfigurable<MdParserSettingsForm> implements SearchableConfigurable {
    public static final String ID = "MarkdownNavigator.Settings.Parser";

    public MdParserConfigurable(@NotNull Project project) {
        super(project);
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return MdBundle.message("settings.markdown.parser.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "com.vladsch.markdown.navigator.settings.parser";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        return getForm().getComponent();
    }

    @NotNull
    public MdParserSettingsForm getForm() {
        if (myForm == null) {
            myForm = new MdParserSettingsForm(myProfileSynchronizer);
        }
        return myForm;
    }

    @Override
    public boolean isModified() {
        return getForm().isModified(myProfileSynchronizer.getRenderingProfileHolder()) || !myProfileSynchronizer.getPreviewSettings().equals(myProfileSynchronizer.getRenderingProfileHolder().getPreviewSettings());
    }

    @Override
    public void apply() throws ConfigurationException {
        getForm().apply(myProfileSynchronizer);
        myProfileSynchronizer.apply();
    }

    @Override
    public void reset() {
        getForm().reset(getProfileForReset());
    }

    @Override
    public void disposeUIResources() {
        if (myForm != null) {
            Disposer.dispose(myForm);
            myForm = null;
            myProfileSynchronizer.reset();
        }
    }
}
