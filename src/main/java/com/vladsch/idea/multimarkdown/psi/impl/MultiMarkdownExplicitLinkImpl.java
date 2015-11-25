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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownExplicitLink;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownVisitor;
import com.vladsch.idea.multimarkdown.util.PathInfo;
import com.vladsch.idea.multimarkdown.util.GitHubRepo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiMarkdownExplicitLinkImpl extends ASTWrapperPsiElement implements MultiMarkdownExplicitLink {
    public static String getElementText(@NotNull String name, @Nullable String text, @Nullable String anchor, @Nullable String title) {
        if (text == null || text.isEmpty()) text = new PathInfo(name).getFileNameNoExt();
        return "[" + text + "](" + name.replace("#","%23") + (anchor != null ? anchor : "") + (title != null && title.length() > 0 ? " '" + title + "'" : "") + ")";
    }

    @Override
    @NotNull
    public String getMissingElementNameSpace(@NotNull String prefix, boolean addLinkRef) {
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(getProject());
        PsiFile psiFile = getContainingFile();
        VirtualFile virtualFile = psiFile.getOriginalFile() != null ? psiFile.getOriginalFile().getVirtualFile() : psiFile.getVirtualFile();
        PathInfo filePathInfo = new PathInfo(virtualFile);
        GitHubRepo gitHubRepo = projectComponent != null ? projectComponent.getGitHubRepo(filePathInfo.getPath()) : null;
        String vcsHome = gitHubRepo != null ? gitHubRepo.getBasePath() + "::" : "";

        if (addLinkRef) {
            String pageRef = MultiMarkdownPsiImplUtil.getLinkRefTextWithAnchor(this);
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
        return MultiMarkdownPsiImplUtil.getLinkText(this);
    }

    @Override
    public String getLinkRef() {
        return MultiMarkdownPsiImplUtil.getLinkRefText(this);
    }

    @Override
    public String getTitle() {
        return MultiMarkdownPsiImplUtil.getLinkTitle(this);
    }

    @Override
    public String getLinkRefAnchor() {
        return MultiMarkdownPsiImplUtil.getLinkAnchor(this);
    }
}
