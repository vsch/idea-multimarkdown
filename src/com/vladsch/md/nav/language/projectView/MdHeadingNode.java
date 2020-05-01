// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.ValidateableNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.vladsch.md.nav.psi.element.MdHeaderElement;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class MdHeadingNode extends ProjectViewNode<MdHeaderElement> implements Navigatable, ValidateableNode {
    private final int myIndex;

    public MdHeadingNode(Project project, @NotNull MdHeaderElement value, @NotNull ViewSettings viewSettings, int index) {
        super(project, value, viewSettings);
        myIndex = index;
    }

    @Override
    public boolean contains(@NotNull final VirtualFile file) {
        return false;
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        return Collections.emptyList();
    }

    @Override
    protected void update(@NotNull final PresentationData presentation) {
        if (getValue() == null || !getValue().isValid()) {
            setValue(null);
        } else {
            //presentation.setPresentableText(getValue().getName());
            presentation.setPresentableText("######".substring(0, getValue().getHeaderLevel()) + " " + getValue().getHeaderText());
            presentation.setIcon(MdIcons.Element.HEADER);
        }
    }

    @Nullable
    @Override
    public Comparable<?> getSortKey() {
        return myIndex;
    }

    @Override
    public boolean isValid() {
        return getValue() != null && getValue().isValid();
    }

    @Override
    public boolean canNavigateToSource() {
        return getValue() != null && getValue().canNavigateToSource();
    }

    @Override
    public boolean canNavigate() {
        return getValue() != null && getValue().canNavigate();
    }

    @Override
    public void navigate(final boolean requestFocus) {
        if (getValue() != null) {
            getValue().navigate(requestFocus);
        }
    }

    @Override
    public int getWeight() {
        return 70;
    }

    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }
}
