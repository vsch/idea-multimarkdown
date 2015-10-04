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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageTitle;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiMarkdownReferenceWikiPageTitle extends MultiMarkdownReference {
    private static final Logger logger = Logger.getLogger(MultiMarkdownReferenceWikiPageTitle.class);
    //private ResolveResult[] incompleteCodeResolveResults;

    public MultiMarkdownReferenceWikiPageTitle(@NotNull MultiMarkdownWikiPageTitle element, @NotNull TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public ResolveResult[] getMultiResolveResults(boolean incompleteCode) {
        String name = myElement.getName();
        if (name != null) {
            MultiMarkdownFile containingFile = (MultiMarkdownFile) myElement.getContainingFile();

            // these are always missing
            //logger.info("getting dummy Reference" + " for " + myElement + " named: " + myElement.getMissingElementNamespace() + name);
            MultiMarkdownNamedElement missingLinkElement = containingFile.getMissingLinkElement(myElement, myElement.getMissingElementNamespace() + name);

            //if (missingLinkElement == myElement) {
            //    logger.info("dummy Reference" + " for " + myElement + " is itself");
            //}

            List<ResolveResult> results = new ArrayList<ResolveResult>();
            //logger.info("setting dummy Reference" + " for " + myElement + " named: " + myElement.getMissingElementNamespace() + name);
            results.add(new PsiElementResolveResult(missingLinkElement));
            containingFile.addListener(fileListListener);
            return results.toArray(new ResolveResult[results.size()]);
        }

        return EMPTY_RESULTS;
    }
}
