// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.StubBasedPsiElement;
import com.vladsch.md.nav.psi.element.MdComposite;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FlexmarkExampleOptions extends StubBasedPsiElement<FlexmarkExampleOptionsStub>, MdComposite {
    @NotNull
    List<ASTNode> getOptionNodes();

    @NotNull
    List<FlexmarkExampleOption> getOptionElements();

    @NotNull
    List<String> getOptions();

    @NotNull
    List<String> getOptionTexts();

    @NotNull
    List<FlexmarkOptionInfo> getOptionsInfo();

    @NotNull
    String getOptionsString();

    @NotNull
    FlexmarkExampleOptions handleContentChange(@NotNull String newContent);

    boolean isWithIgnore();

    boolean isWithFail();

    boolean isWithErrors();
}
