// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
//
// This code is private property of the copyright holder and cannot be used without
// having obtained a license or prior written permission of the copyright holder.
//
package com.vladsch.md.nav.inspections;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider;
import com.vladsch.md.nav.editor.jbcef.JBCefHtmlPanelProvider;
import com.vladsch.md.nav.editor.util.HtmlPanelProvider;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdPreviewSettings;
import com.vladsch.md.nav.settings.MdProjectSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.settings.RenderingProfileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JbCefCouldBeEnabledNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> implements DumbAware {
    private static final Key<EditorNotificationPanel> KEY = Key.create("Markdown JCEF Preview Could Be Enabled");

    @NotNull
    @Override
    public Key<EditorNotificationPanel> getKey() {
        return KEY;
    }

    @Nullable
    @Override
    public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull final FileEditor fileEditor, @NotNull Project project) {
        if (file.getFileType() != MdFileType.INSTANCE) {
            return null;
        }

        if (MdApplicationSettings.getInstance().getWasShownSettings().getJbCefAvailable()) {
            return null;
        }

        if (!(fileEditor instanceof TextEditor)) {
            return null;
        }

        RenderingProfileManager profileManager = MdRenderingProfileManager.getInstance(project);
        MdRenderingProfile renderingProfile = profileManager.getRenderingProfile(file);

        if (!renderingProfile.isProjectProfile()) {
            return null;
        }

        final MdPreviewSettings oldPreviewSettings = renderingProfile.getPreviewSettings();
        final HtmlPanelProvider.AvailabilityInfo availabilityInfo = new JBCefHtmlPanelProvider().isAvailable();
        if (availabilityInfo == HtmlPanelProvider.AvailabilityInfo.UNAVAILABLE) {
            return null;
        } else if (availabilityInfo == HtmlPanelProvider.AvailabilityInfo.AVAILABLE_NOTUSED) {
            if (oldPreviewSettings.getHtmlPanelProviderInfo().getProviderId().equals(JavaFxHtmlPanelProvider.ID) ||
                    oldPreviewSettings.getHtmlPanelProviderInfo().getProviderId().equals(JBCefHtmlPanelProvider.ID)) {
                return null;
            }
        }

        final EditorNotificationPanel panel = new EditorNotificationPanel();

        panel.setText(MdBundle.message("editor.jbcef.is.available"));

        panel.createActionLabel(MdBundle.message("editor.jbcef.change.to"), () -> {
            final boolean isSuccess = availabilityInfo.checkAvailability(panel);
            if (isSuccess) {
                MdRenderingProfile renderingSettings = MdRenderingProfileManager.getProfile(project, file);
                MdRenderingProfile newSettings = renderingSettings.changeToProvider(renderingSettings.getPreviewSettings().getHtmlPanelProviderInfo(), JBCefHtmlPanelProvider.INFO);
                if (renderingProfile.getName().isEmpty()) {
                    MdProjectSettings.getInstance(project).setRenderingProfile(newSettings);
                } else {
                    profileManager.replaceProfile(renderingProfile.getProfileName(), newSettings);
                }

                EditorNotifications.updateAll();
            } else {
                Logger.getInstance(JbCefCouldBeEnabledNotificationProvider.class).warn("Could not apply JbCef");
            }
        });

        panel.createActionLabel(MdBundle.message("editor.dont.show.again"), () -> {
            MdApplicationSettings.getInstance().getWasShownSettings().setJbCefAvailable(true);
            EditorNotifications.updateAll();
        });
        return panel;
    }
}
