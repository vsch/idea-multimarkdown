// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language.projectView;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdHeaderElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class MdTreeStructureProvider implements TreeStructureProvider, DumbAware {
    private final Project myProject;

    public MdTreeStructureProvider(@NotNull Project project) {
        myProject = project;
    }

    @NotNull
    @Override
    public Collection<AbstractTreeNode<?>> modify(
            @NotNull AbstractTreeNode<?> parent,
            @NotNull Collection<AbstractTreeNode<?>> children,
            ViewSettings settings
    ) {
        if (!(parent.getValue() instanceof MdFile)) return children;
        if (DumbService.isDumb(myProject)) return children;
        if (!settings.isShowMembers()) return children;

        MdFile mdFile = (MdFile) parent.getValue();

        Collection<AbstractTreeNode<?>> result = new LinkedHashSet<>(children);
        List<MdHeaderElement> headers = mdFile.getHeaderElements();
        int i = 0;
        for (MdHeaderElement header : headers) {
            result.add(new MdHeadingNode(myProject, header, settings, i++));
        }

        return result;
    }
}
