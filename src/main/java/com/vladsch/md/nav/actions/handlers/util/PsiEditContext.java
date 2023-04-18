// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.LineAppendable;
import com.vladsch.flexmark.util.sequence.LineAppendableImpl;
import com.vladsch.flexmark.util.sequence.Range;
import com.vladsch.flexmark.util.sequence.builder.SequenceBuilder;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.PegdownExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to help map to/from post edit text offsets from/to pre edit PsiTree offsets
 * <p>
 * Delete op is -ve editOpDelta Insert op is +ve editOpDelta
 * <p>
 * After edit offset is given by editOpOffset, inserted chars are before this offset deleted characters are after this offset. In other words, this offset represents the caret offset after typing operation in case of insert and backspace operation in case of delete.
 */
public interface PsiEditContext {
    @NotNull
    PsiFile getPsiFile();

    @Nullable
    Editor getEditor();

    @NotNull
    BasedSequence getCharSequence();

    boolean getHasFrontMatter();

    @Nullable
    ASTNode getFrontMatterNode();

    @NotNull
    default MdCodeStyleSettings getStyleSettings() {
        return getRenderingProfile().getResolvedStyleSettings();
    }

    @NotNull
    MdRenderingProfile getRenderingProfile();

    int editOpDelta();

    default LineAppendable getLineAppendable() {
        return new LineAppendableImpl(getCharSequence().getBuilder(), 0);
    }

    @NotNull
    default SequenceBuilder getEmptyBuilder() {
        return getCharSequence().getBuilder();
    }

    @NotNull
    default Project getProject() { return getPsiFile().getProject(); }

    @NotNull
    default MdParserSettings getParserSettings() { return getRenderingProfile().getParserSettings(); }

    default boolean isAsideEnabled() { return (getParserSettings().getPegdownFlags() & PegdownExtensions.ASIDE.getFlags()) != 0; }

    @NotNull
    default String getBlockQuoteStyleChars() { return isAsideEnabled() ? ">|" : ">"; }

    @NotNull
    default CharPredicate getBlockQuoteStyleCharsSet() { return CharPredicate.anyOf(getBlockQuoteStyleChars()); }

    @NotNull
    default String getIndentingChars() { return " \t" + getBlockQuoteStyleChars(); }

    @NotNull
    default CharPredicate getIndentingCharsSet() { return CharPredicate.anyOf(getIndentingChars()); }

    default boolean isBlockQuoteStyleChar(@Nullable Character c) { return c != null && getBlockQuoteStyleCharsSet().test(c); }

    default boolean isIndentingChar(@Nullable Character c) { return c != null && getIndentingCharsSet().test(c); }

    default boolean isWhitespaceChar(@Nullable Character c) { return c != null && CharPredicate.SPACE_TAB.test(c); }

    int preEditOffset(int postEditOffset);

    int postEditOffset(int preEditOffset, @NotNull PostEditAdjust adjustInsertAtStart);

    default int postEditNodeStart(ASTNode node, PostEditAdjust adjustInsertAtStart) {
        return postEditOffset(node.getStartOffset(), adjustInsertAtStart);
    }

    default int postEditNodeEnd(ASTNode node, PostEditAdjust adjustInsertAtStart) {
        return postEditOffset(node.getStartOffset() + node.getTextLength(), adjustInsertAtStart);
    }

    default int postEditNodeStart(ASTNode node) {
        return postEditNodeStart(node, PostEditAdjust.NEVER);
    }

    default int postEditNodeEnd(ASTNode node) {
        return postEditNodeEnd(node, PostEditAdjust.ALWAYS);
    }

    boolean isInsertedEditOpRange(int startPostEditOffset, int endPostEditOffset);

    @NotNull
    BasedSequence nodeText(@NotNull ASTNode node);

    @NotNull
    Range nodeRange(@NotNull ASTNode node);

    @NotNull
    BasedSequence elementText(@NotNull PsiElement element);

    @NotNull
    BasedSequence text(int start, int end);
}
