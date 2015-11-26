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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLinkRef;
import com.vladsch.idea.multimarkdown.util.FileRef;
import com.vladsch.idea.multimarkdown.util.GitHubLinkResolver;
import com.vladsch.idea.multimarkdown.util.LinkRef;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiMarkdownReferenceWikiLinkRef extends MultiMarkdownReference {
    private static final Logger logger = Logger.getLogger(MultiMarkdownReferenceWikiLinkRef.class);

    public MultiMarkdownReferenceWikiLinkRef(@NotNull MultiMarkdownWikiLinkRef element, @NotNull TextRange textRange) {
        super(element, textRange);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length > 0 ? resolveResults[0].getElement() : null;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        if (myElement instanceof MultiMarkdownWikiLinkRef && element instanceof PsiFile) {
            LinkRef linkRef = MultiMarkdownPsiImplUtil.getLinkRef(myElement);
            if (linkRef != null) {
                String linkRefText = new GitHubLinkResolver(myElement).linkAddress(linkRef, new FileRef((PsiFile) element), null, null, null);
                // this will create a new reference and loose connection to this one
                return myElement.setName(linkRefText,  MultiMarkdownNamedElement.REASON_BIND_TO_FILE);
            }
        }
        return super.bindToElement(element);
    }

}
