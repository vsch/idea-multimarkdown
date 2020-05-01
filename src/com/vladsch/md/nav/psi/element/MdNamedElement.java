// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.psi.reference.MdPsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdNamedElement extends MdPsiElement, PsiNameIdentifierOwner, NavigationItem, MdStructureViewPresentableItem {
    int RENAME_NO_FLAGS = -1;

    int RENAME_KEEP_NOTHING = 0;
    int RENAME_KEEP_PATH = 1;
    int RENAME_KEEP_ANCHOR = 2;
    int RENAME_KEEP_NAME = 4;
    int RENAME_KEEP_TEXT = 8;
    int RENAME_KEEP_TITLE = 16;     // link and image refs only
    int RENAME_KEEP_RENAMED_TEXT = 32;  //not implemented
    int RENAME_KEEP_EXT = 64;
    int RENAME_ELEMENT_HANDLES_EXT = 128;
    int RENAME_KEEP_URI_FORMAT = 256;
    int RENAME_DROP_ANCHOR = 512;

    int REASON_BIND_TO_FILE = RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TITLE | RENAME_KEEP_URI_FORMAT;
    int REASON_FILE_RENAMED = RENAME_KEEP_PATH | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TEXT | RENAME_KEEP_TITLE | RENAME_ELEMENT_HANDLES_EXT;
    int REASON_FILE_MOVED = RENAME_KEEP_ANCHOR | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TEXT | RENAME_KEEP_TITLE | RENAME_ELEMENT_HANDLES_EXT;

    int RENAME_CHANGE_LINK_TEXT = RENAME_KEEP_TEXT | RENAME_KEEP_ANCHOR | RENAME_KEEP_TITLE;

    @NotNull
    String getDisplayName();

    // this one will only change the name part, not the path part of the link
    @Override
    PsiElement setName(@NotNull String newName);

    // this one will preserve the path and only change the name unless fileMoved is true
    PsiElement setName(@NotNull String newName, int reason);

    @Override
    PsiElement getNameIdentifier();

    @Nullable
    ItemPresentation getPresentation();

    MdNamedElement handleContentChange(@NotNull TextRange range, @NotNull String newContent) throws IncorrectOperationException;

    MdNamedElement handleContentChange(@NotNull String newContent) throws IncorrectOperationException;

    @Nullable
    MdPsiReference createReference(@NotNull TextRange textRange, final boolean exactReference);

    default boolean isInplaceRenameAvailable(@Nullable PsiElement context) {
        return false;
    }

    default boolean isMemberInplaceRenameAvailable(@Nullable PsiElement context) {
        return true;
    }

    default boolean isRenameAvailable() {
        return true;
    }

    @Nullable
    PsiReference getExactReference();

    @NotNull
    @Override
    String getName();
}
