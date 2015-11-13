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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownExplicitLink;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownVisitor;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import com.vladsch.idea.multimarkdown.util.GitHubRepo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiMarkdownExplicitLinkImpl extends ASTWrapperPsiElement implements MultiMarkdownExplicitLink {
    public static String getElementText(@NotNull String name, @Nullable String text, @Nullable String title) {
        if (text == null || text.isEmpty()) text = new FilePathInfo(name).getFileNameNoExt();
        return "[" + text + "](" + name + (title != null && title.length() > 0 ? " '" + title + "'" : "") + ")";
    }

    @Override
    @NotNull
    public String getMissingElementNameSpace(@NotNull String prefix, boolean addLinkRef) {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(getProject());
        FilePathInfo filePathInfo = new FilePathInfo(getContainingFile().getVirtualFile());
        GitHubRepo gitHubRepo = projectComponent != null ? projectComponent.getGitHubRepo(filePathInfo.getPath()) : null;
        String vcsHome = gitHubRepo != null ? gitHubRepo.getBasePath() + "::" : "";

        if (addLinkRef) {
            String pageRef = MultiMarkdownPsiImplUtil.getLinkRefWithAnchor(this);
            if (pageRef.isEmpty()) pageRef = filePathInfo.getFileNameAsWikiRef();
            return prefix + (vcsHome.isEmpty() ? vcsHome : vcsHome + "::") + (pageRef.isEmpty() ? pageRef : pageRef + "::");
        }
        return prefix + (vcsHome.isEmpty() ? vcsHome : vcsHome + "::");
    }

    public MultiMarkdownExplicitLinkImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MultiMarkdownVisitor) visitor.visitElement(this);
        else super.accept(visitor);
    }

    @Override
    public String getDisplayName() {
        return getText();
    }

    @Override
    public String getText() {
        return MultiMarkdownPsiImplUtil.getLinkRefText(this);
    }

    @Override
    public String getLinkRef() {
        return MultiMarkdownPsiImplUtil.getLinkRef(this);
    }

    @Override
    public String getTitle() {
        return MultiMarkdownPsiImplUtil.getLinkRefTitle(this);
    }

    @Override
    public String getLinkRefAnchor() {
        return MultiMarkdownPsiImplUtil.getLinkRefAnchor(this);
    }
}
