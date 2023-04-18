// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.psi.reference.MdPsiReference;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import icons.MdEmojiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdEmojiIdImpl extends MdNamedElementImpl implements MdEmojiId {
    public MdEmojiIdImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MdPsiReference createReference(@NotNull TextRange textRange, final boolean exactReference) {
        return null;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return MdEmojiIcons.getEmojiIcon(getName());
    }

    @Override
    public PsiElement setName(@NotNull String newName, int reason) {
        MdEmoji emojiElement = MdPsiImplUtil.setEmojiName((MdEmojiImpl) getParent(), newName);
        return emojiElement.getEmojiIdentifier();
    }

    @Override
    public String toString() {
        return "EMOJI_ID '" + getName() + "' " + super.hashCode();
    }
}
