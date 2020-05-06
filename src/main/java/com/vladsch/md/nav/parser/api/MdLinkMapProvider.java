// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.psi.util.TextMapElementType;
import com.vladsch.md.nav.psi.util.TextMapMatch;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.util.MdExtensions;
import com.vladsch.md.nav.util.Want;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

public interface MdLinkMapProvider {
    ExtensionPointName<MdLinkMapProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.linkMapProvider");
    MdExtensions<MdLinkMapProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdLinkMapProvider[0]);

    @Nullable
    String mapRefLink(@NotNull String referenceId, @NotNull MdRenderingProfile renderingProfile);

    @Nullable
    String mapLinkRef(@NotNull String linkRef, @NotNull MdRenderingProfile renderingProfile);

    @Nullable
    String mapLinkText(@NotNull String linkText, @NotNull MdRenderingProfile renderingProfile);

    @Nullable
    String mapFileRef(@NotNull TextMapElementType elementType, @NotNull String text, MdRenderingProfile renderingProfile);

    @Nullable
    ArrayList<TextMapMatch> getMatchList(@NotNull TextMapElementType textMapElementType, @NotNull String referenceableText, @NotNull MdRenderingProfile renderingProfile);

    @Nullable
    Boolean getOpenRemoteLinks(@NotNull MdRenderingProfile renderingProfile);

    @Nullable
    String mapTargetFilePath(@NotNull Project project, @NotNull VirtualFile file);

    @Nullable
    Want.Options.Remotes getRemoteFormat(@NotNull MdRenderingProfile renderingProfile);

    @Nullable
    Want.Options.Locals getLocalFormat(@NotNull MdRenderingProfile renderingProfile);

    @Nullable
    Boolean showUnresolvedLinkRefs(@NotNull MdRenderingProfile renderingProfile);

    @Nullable
    Boolean getIncludeDirsInCompletion(@Nullable MdRenderingProfile renderingProfile);

    @Nullable
    Map<String, String> getLinkExclusionMap(@Nullable MdRenderingProfile renderingProfile);
}
