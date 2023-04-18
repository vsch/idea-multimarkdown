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

public class MdHtmlConfigurable extends MdProjectConfigurable<MdHtmlSettingsForm> implements SearchableConfigurable {
    public static final String ID = "MarkdownNavigator.Settings.Html";

    private MdHtmlConfigurable(@NotNull Project project) {
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
        return MdBundle.message("settings.markdown.html.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "com.vladsch.markdown.navigator.settings.html-generation";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        return getForm().getComponent();
    }

    @NotNull
    public MdHtmlSettingsForm getForm() {
        if (myForm == null) {
            myForm = new MdHtmlSettingsForm(myProfileSynchronizer, false);
        }
        return myForm;
    }

    @Override
    public boolean isModified() {
        return getForm().isModified(myProfileSynchronizer.getRenderingProfileHolder()) || !myProfileSynchronizer.getHtmlSettings().equals(myProfileSynchronizer.getRenderingProfileHolder().getHtmlSettings());
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
