// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.inspections.emoji;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.vladsch.flexmark.ext.emoji.internal.EmojiReference;
import com.vladsch.flexmark.ext.emoji.internal.EmojiShortcuts;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.inspections.LocalInspectionToolBase;
import com.vladsch.md.nav.inspections.ProblemDescriptors;
import com.vladsch.md.nav.psi.element.MdEmoji;
import com.vladsch.md.nav.psi.element.MdEmojiId;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.settings.EmojiShortcutsType;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Character.isDigit;

public class InvalidEmojiShortcutInspection extends LocalInspectionToolBase {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (file.getFileType() != MdFileType.INSTANCE || !file.isValid() || isIgnoreFencedCodeContent(file)) {
            return null;
        }

        final Project project = file.getProject();
        final MdRenderingProfile renderingProfile = MdRenderingProfileManager.getInstance(project).getRenderingProfile(file);
        final ProblemDescriptors problems = new ProblemDescriptors();

        MdPsiImplUtil.findChildrenOfAnyType((MdFile) file, false, false, false, element -> {
            final MdEmojiId identifier = element.getEmojiIdentifier();
            if (identifier != null) {
                String emojiShortcut = identifier.getName();
                if (!emojiShortcut.isEmpty() /*&& emojiShortcut.indexOf(' ') == -1*/) {
                    EmojiReference.Emoji shortcut = EmojiShortcuts.getEmojiFromShortcut(emojiShortcut);

                    final EmojiShortcutsType shortcutsType = renderingProfile.getParserSettings().getEmojiShortcutsType();
                    String emojiIconFile = shortcut == null ? null : shortcutsType.flexmarkType.getPreferred(shortcut.emojiCheatSheetFile, shortcut.githubFile);

                    if (emojiIconFile == null) {
                        boolean allDigits = true;
                        int iMax = emojiShortcut.length();
                        for (int i = 0; i < iMax; i++) {
                            if (!isDigit(emojiShortcut.charAt(i))) allDigits = false;
                        }

                        if (!allDigits || iMax != 2) {
                            problems.add(manager.createProblemDescriptor(element,
                                    MdBundle.message(shortcutsType.flexmarkType.isGitHub ? "annotation.emoji.0.non-gfm-shortcut" : "annotation.emoji.0.non-ecs-shortcut", "':" + emojiShortcut + ":'"),
                                    true,
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                    isOnTheFly,
                                    problems.fixes()));
                        }
                    }
                }
            }
        }, MdEmoji.class);

        return problems.done();
    }
}
