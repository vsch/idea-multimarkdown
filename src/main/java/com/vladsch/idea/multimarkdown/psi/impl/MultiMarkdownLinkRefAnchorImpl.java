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
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownExplicitLink;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownLinkRefAnchor;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownLinkRefAnchorImpl extends MultiMarkdownNamedElementImpl implements MultiMarkdownLinkRefAnchor {
    private static final Logger logger = Logger.getLogger(MultiMarkdownLinkRefAnchorImpl.class);
    protected static final String MISSING_ELEMENT_NAME_SPACE = "link-anchor::";

    @NotNull
    @Override
    public String getMissingElementNamespace() {
        String linkRef = MultiMarkdownPsiImplUtil.getLinkRef((MultiMarkdownExplicitLink) getParent());
        FilePathInfo filePathInfo = new FilePathInfo(getContainingFile().getVirtualFile());
        return MISSING_ELEMENT_NAME_SPACE + (linkRef.isEmpty() ? linkRef : linkRef + "::");
    }

    public MultiMarkdownLinkRefAnchorImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MultiMarkdownReference createReference(@NotNull TextRange textRange) {
        return  new MultiMarkdownReference(this, textRange);
    }

    @Override
    public String getDisplayName() {
        return getParent() instanceof MultiMarkdownExplicitLink  ? ((MultiMarkdownExplicitLink) getParent()).getDisplayName() : getName();
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
        return "LINK_REF_ANCHOR '" + getName() + "' " + super.hashCode();
    }
}
