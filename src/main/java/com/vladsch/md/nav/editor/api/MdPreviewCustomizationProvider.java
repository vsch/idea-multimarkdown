// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.misc.Pair;
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanel;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.util.MdExtensions;
import com.vladsch.md.nav.util.Want;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import java.util.concurrent.atomic.AtomicBoolean;

public interface MdPreviewCustomizationProvider {
    ExtensionPointName<MdPreviewCustomizationProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.previewCustomizationProvider");
    MdExtensions<MdPreviewCustomizationProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdPreviewCustomizationProvider[0]);

    boolean isAlternateUrlEnabled(@NotNull MdRenderingProfile renderingProfile);

    @Nullable
    Want.Options[] getLinkOptions(@NotNull MdRenderingProfile renderingProfile);

    static void textErrorReport(String source, Throwable e, String textType, String text) {
        for (MdPreviewCustomizationProvider handler : EXTENSIONS.getValue()) {
            if (handler.createErrorReport(source, e, textType, text)) break;
        }
    }

    static void  parserErrorReport(int pegdownExtensions, long parserOptions, @NotNull DataHolder options, Throwable e, String text) {
        for (MdPreviewCustomizationProvider handler : EXTENSIONS.getValue()) {
            if (handler.createParserErrorReport(pegdownExtensions,parserOptions, options, e, text)) break;
        }
    }

    /**
     * Return alternate URL to use for displaying the page preview
     *
     * @return null to display rendered markdown or URL of page
     */
    @Nullable
    String getAlternatePageURL(@NotNull Project project, @NotNull VirtualFile file, MdRenderingProfile renderingProfile);

    boolean canLaunchExternalLink(@NotNull String myLastRenderedUrl, @NotNull String href, @NotNull MdRenderingProfile renderingProfile);

    // REFACTOR: move error related methods to Md error handler extension point
    /**
     * Generate error report for exception in HTML panel
     * @param message message
     * @param e exception
     * @param textType attachment name
     * @param text attachment
     * @return true if handled
     */
    boolean createErrorReport(String message, Throwable e, String textType, String text);

    boolean createParserErrorReport(int pegdownExtensions, long parserOptions, @NotNull DataHolder options, Throwable e, String text);

    boolean canDebugPreview(@NotNull Project project);

    void adjustImageItem(Node item, long serial);

    boolean launchDebugger(@NotNull Project project, @NotNull JavaFxHtmlPanel.JSBridge jsBridge, AtomicBoolean changingState);

    @Nullable
    Pair<String, String> getPageFileURL(@NotNull Project project, int instance, int serial, @NotNull String html);
}
