// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.stubs.StubElement;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.LinkRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdLinkElement<Stub extends StubElement<?>> extends MdStubBasedPsiElement<Stub> {
    @NotNull
    String getLinkRefWithAnchorText();

    @Nullable
    MdLinkRefElement getLinkRefElement();

    @NotNull
    String getLinkRefText();

    @Nullable
    String getLinkAnchorText();

    @Nullable
    String getLinkText();

    @Nullable
    String getLinkTitleText();

    @Nullable
    ItemPresentation getPresentation();

    @NotNull
    String getDisplayName();

    @NotNull
    LinkRef getLinkRef();

    @Nullable
    MdImageMultiLineUrlContentImpl getLinkRefUrlContentElement();

    @Nullable
    MdLinkAnchor getLinkAnchorElement();

    @Nullable
    MdLinkTitle getLinkTitleElement();

    @NotNull
    String getMissingElementNameSpace(@NotNull String prefix, boolean addLinkRef);

    @NotNull
    LinkRef createLinkRef(@NotNull FileRef containingFile, @NotNull String linkRefText, @Nullable String linkAnchorText, @Nullable FileRef targetRef);

    @Nullable
    MdLinkText getLinkTextElement();
}
