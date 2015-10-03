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
import com.vladsch.idea.multimarkdown.util.FileReferenceList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.MARKDOWN_FILE;
import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.WIKI_REF;

public class MultiMarkdownLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof MultiMarkdownWikiPageRef) {
            MultiMarkdownFile[] markdownFiles = MultiMarkdownPlugin.getProjectComponent(element.getProject()).getFileReferenceListQuery()
                    .matchWikiRef((MultiMarkdownWikiPageRef) element)
                    .getAccessibleWikiPageFiles()
                    ;

            if (markdownFiles.length > 0) {
                MultiMarkdownFile file = markdownFiles[0];
                ArrayList<MultiMarkdownFile> markdownTargets = new ArrayList<MultiMarkdownFile>();

                Iterator<? super RelatedItemLineMarkerInfo> iterator = result.iterator();
                boolean skipTarget = false;
                while (iterator.hasNext()) {
                    RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = (RelatedItemLineMarkerInfo<PsiElement>) iterator.next();

                    PsiElement lineMarkerElement = lineMarkerInfo.getElement();
                    if (lineMarkerElement instanceof MultiMarkdownWikiPageRef) {
                        String lineMarkerFileName = ((MultiMarkdownWikiPageRef) lineMarkerElement).getFileName();
                        String fileName = ((MultiMarkdownWikiPageRef) element).getFileName();
                        if (lineMarkerFileName.equals(fileName)) {
                            //skipTarget = true;
                            break;
                        }
                    }
                }

                if (!skipTarget) {
                    NavigationGutterIconBuilder<PsiElement> builder =
                            NavigationGutterIconBuilder.create(file.isWikiPage() ? MultiMarkdownIcons.WIKI : MultiMarkdownIcons.FILE)
                                    .setTarget(file)
                                    .setTooltipText(MultiMarkdownBundle.message("linemarker.navigate-to", ((MultiMarkdownWikiPageRef) element).getFileName()));
                    result.add(builder.createLineMarkerInfo(element));
                }
            }
        }
    }
}
