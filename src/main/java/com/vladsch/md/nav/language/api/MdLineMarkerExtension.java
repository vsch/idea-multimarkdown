// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.codeInsight.daemon.GutterIconDescriptor;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface MdLineMarkerExtension {
    ExtensionPointName<MdLineMarkerExtension> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.lineMarkerExtension");
    MdExtensions<MdLineMarkerExtension> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdLineMarkerExtension[0]);

    interface LineMarkerProvider {
        void collectFileRefMarkers(@NotNull PsiElement leafElement, @NotNull PsiElement element, @Nullable ResolveResult[] resolveResults);

        boolean collectReferencingMarkers(@NotNull PsiElement leafElement, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result, @Nullable String navigationText);
    }

    @NotNull
    GutterIconDescriptor.Option[] getOptions();

    boolean collectNavigationMarkers(@NotNull PsiElement leafElement, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result, @NotNull LineMarkerProvider provider);
}
