/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.language;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.*;

public class MultiMarkdownLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof MultiMarkdownWikiPageRef) {
                Project project = element.getProject();
            List<MultiMarkdownFile> markdownFiles = MultiMarkdownPlugin.getProjectComponent(project)
                    .findRefLinkMarkdownFiles(((MultiMarkdownWikiPageRef) element).getName(), element.getContainingFile().getVirtualFile(), WIKI_REF | MARKDOWN_FILE);
            if (markdownFiles != null && markdownFiles.size() > 0) {
                MultiMarkdownFile file = markdownFiles.get(0);

                    NavigationGutterIconBuilder<PsiElement> builder =
                            NavigationGutterIconBuilder.create(file.isWikiPage() ? MultiMarkdownIcons.WIKI : MultiMarkdownIcons.FILE)
                                    .setTargets(markdownFiles)
                                    .setTooltipText(MultiMarkdownBundle.message("linemarker.navigate-to", ((MultiMarkdownWikiPageRef) element).getFileName()));
                    result.add(builder.createLineMarkerInfo(element));
            }
        }
    }
}
