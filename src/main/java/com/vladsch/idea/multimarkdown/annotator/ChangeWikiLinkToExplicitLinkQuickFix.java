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
package com.vladsch.idea.multimarkdown.annotator;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.license.LicensedFeature;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLink;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import org.jetbrains.annotations.NotNull;

@LicensedFeature
class ChangeWikiLinkToExplicitLinkQuickFix extends BaseIntentionAction {
    private MultiMarkdownWikiLink wikiLinkElement;

    ChangeWikiLinkToExplicitLinkQuickFix(MultiMarkdownWikiLink wikiLinkElement) {
        this.wikiLinkElement = wikiLinkElement;
    }

    @NotNull
    @Override
    public String getText() {
        return MultiMarkdownBundle.message("quickfix.wikilink.to-explicit-link");
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return MultiMarkdownBundle.message("quickfix.wikilink.family-name");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                changeToExplicitLink(project, editor, wikiLinkElement);
            }
        });
    }

    private void changeToExplicitLink(final Project project, final Editor editor, final MultiMarkdownWikiLink wikiLinkElement) {
        new WriteCommandAction.Simple(project) {
            @Override
            public void run() {
                // change the element using text, until I can figure out why any operations on ExplicitLinks cause exceptions.
                //MultiMarkdownPsiImplUtil.changeToExplicitLink(wikiLinkElement);
                final Document document = editor.getDocument();
                String text = MultiMarkdownPsiImplUtil.getExplicitLinkTextFromWikiLink(wikiLinkElement);
                int pos = wikiLinkElement.getTextOffset();
                document.replaceString(pos, pos + wikiLinkElement.getTextLength(), text);
            }
        }.execute();
    }
}
