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

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLink;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRefAnchor;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownWikiPageRefAnchorImpl extends MultiMarkdownNamedElementImpl implements MultiMarkdownWikiPageRefAnchor {
    private static final Logger logger = Logger.getLogger(MultiMarkdownWikiPageRefAnchorImpl.class);
    protected static final String MISSING_ELEMENT_NAME_SPACE = "wiki-anchor::";

    @NotNull
    @Override
    public String getMissingElementNamespace() {
        String pageRef = MultiMarkdownPsiImplUtil.getPageRef((MultiMarkdownWikiLink) getParent());
        FilePathInfo filePathInfo = new FilePathInfo(getContainingFile().getVirtualFile());
        String wikiHome = filePathInfo.getWikiHome();
        if (pageRef.isEmpty()) pageRef = filePathInfo.getFileNameAsWikiRef();
        return MISSING_ELEMENT_NAME_SPACE + (wikiHome.isEmpty() ? wikiHome : wikiHome + "::") + (pageRef.isEmpty() ? pageRef : pageRef + "::");
    }

    public MultiMarkdownWikiPageRefAnchorImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MultiMarkdownReference createReference(@NotNull TextRange textRange) {
        return  new MultiMarkdownReference(this, textRange);
    }

    @Override
    public String getDisplayName() {
        return getParent() instanceof MultiMarkdownWikiLink  ? ((MultiMarkdownWikiLink) getParent()).getDisplayName() : getName();
    }

    @Override
    public PsiElement setName(@NotNull String newName, int reason) {
        return MultiMarkdownPsiImplUtil.setName(this, newName, reason);
    }

    @Override
    public boolean isInplaceRenameAvailable(PsiElement context) {
        return false;
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(PsiElement context) {
        return false;
    }

    @Override
    public String toString() {
        return "WIKI_LINK_REF_ANCHOR '" + getName() + "' " + super.hashCode();
    }
}
