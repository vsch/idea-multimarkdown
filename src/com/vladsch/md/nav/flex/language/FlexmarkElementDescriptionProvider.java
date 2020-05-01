// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.language;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import com.vladsch.md.nav.flex.PluginBundle;
import com.vladsch.md.nav.flex.psi.FakePsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlexmarkElementDescriptionProvider implements ElementDescriptionProvider {
    @Nullable
    @Override
    public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
        if (element instanceof FakePsiLiteralExpression){
            if (location == UsageViewTypeLocation.INSTANCE) return PluginBundle.message("findusages.flexmark.example-option");
            if (location == UsageViewShortNameLocation.INSTANCE) return element.getText();
        }
        return null;
    }
}
