// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.inspections;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LineSeparatorFoundNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> implements DumbAware {
    private static final Key<EditorNotificationPanel> KEY = Key.create("editor.line-separators-found.name");
    public static final char LINE_SEPARATOR_CHAR = '\u2028';

    @NotNull
    @Override
    public Key<EditorNotificationPanel> getKey() {
        return KEY;
    }

    @Nullable
    @Override
    public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull final FileEditor fileEditor, @NotNull Project project) {
        if (MdApplicationSettings.getInstance().getWasShownSettings().getUnicodeLineSeparator()) return null;

        if (file.getFileType() != MdFileType.INSTANCE) {
            return null;
        }

        if (!(fileEditor instanceof TextEditor)) {
            return null;
        }

        // see if the file has line separators
        final Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null) return null;

        final BasedSequence charsSequence = BasedSequence.of(document.getCharsSequence());
        if (charsSequence.indexOf(LINE_SEPARATOR_CHAR) < 0) return null;

        final EditorNotificationPanel panel = new EditorNotificationPanel();
        panel.setText(MdBundle.message("editor.line-separators-found.name"));

        panel.createActionLabel(MdBundle.message("editor.line-separators-found.remove-all"), () -> {
            //MarkdownRenderingProfile newProfile = new MarkdownRenderingProfile(renderingProfile);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                int i = document.getTextLength();
                int startPos = -1;
                while (i-- > 0) {
                    if (charsSequence.charAt(i) == LINE_SEPARATOR_CHAR) {
                        if (startPos == -1) startPos = i + 1;
                    } else if (startPos != -1) {
                        // delete the accumulated range
                        document.deleteString(i + 1, startPos);
                        startPos = -1;
                    }
                }

                EditorNotifications.getInstance(project).updateNotifications(file);
            });
        });

        panel.createActionLabel(MdBundle.message("editor.dont.show.again"), () -> {
                    MdApplicationSettings.getInstance().getWasShownSettings().setUnicodeLineSeparator(true);
                    EditorNotifications.updateAll();
                }
        );
        return panel;
    }
}
