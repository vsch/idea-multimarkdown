/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.vladsch.idea.multimarkdown.language.MultiMarkdownReference;
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
            // these are always missing
            //logger.info("getting dummy Reference" + " for " + myElement + " named: " + myElement.getMissingElementNamespace() + name);
            MultiMarkdownNamedElement missingLinkElement = getMissingLinkElement(name);

            //if (missingLinkElement == myElement) {
            //    logger.info("dummy Reference" + " for " + myElement + " is itself");
            //}

            List<ResolveResult> results = new ArrayList<ResolveResult>();
            //logger.info("setting dummy Reference" + " for " + myElement + " named: " + myElement.getMissingElementNamespace() + name);
            results.add(new PsiElementResolveResult(missingLinkElement));
            return results.toArray(new ResolveResult[results.size()]);
        }

        return EMPTY_RESULTS;
    }
}
