/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.md.nav;

import com.intellij.ide.IconLayerProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiDirectory;
import com.vladsch.md.nav.settings.DocumentIconTypes;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.vcs.GitHubVcsRoot;
import com.vladsch.md.nav.vcs.MdLinkResolverManager;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;

import javax.swing.Icon;

public class MdWikiDirectoryOverlayProvider implements IconLayerProvider {
    @Override
    public Icon getLayerIcon(@NotNull Iconable element, boolean isLocked) {
        if (!isLocked && element instanceof PsiDirectory) {
            PsiDirectory directory = (PsiDirectory) element;
            String path = directory.getVirtualFile().getPath();
            Project project = directory.getProject();
            @SystemIndependent String basePath = project.getBasePath();
            if (basePath != null) {
                PathInfo projectInfo = new PathInfo(basePath);
                String wikiPath = projectInfo.append(projectInfo.getFileName() + ".wiki").getFilePath();
                if (path.equals(wikiPath)) {
                    MdLinkResolverManager projectComponent = MdLinkResolverManager.getInstance(project);
                    GitHubVcsRoot vcsRoot = projectComponent.getGitHubRepo(path + "/dummy.md");
                    if (vcsRoot != null && vcsRoot.isWiki()) {
                        DocumentIconTypes iconType = MdApplicationSettings.getInstance().getDocumentSettings().getWikiIconType();
                        boolean useAlt = iconType == DocumentIconTypes.MARKDOWN_NAVIGATOR || iconType == DocumentIconTypes.MARKDOWN_NAVIGATOR_WIKI;
                        return useAlt ? MdIcons.Document.WIKI_OVERLAY : MdIcons.Document.WIKI_OVERLAY_ALT;
                    }
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String getLayerDescription() {
        return "Wiki Directory";
    }
}

