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
package com.vladsch.idea.multimarkdown.settings;

import com.vladsch.idea.multimarkdown.license.LicensedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MarkdownExtensionsPanel extends SettingsPanelImpl {
    private JCheckBox abbreviationsCheckBox;
    private JCheckBox anchorLinksCheckBox;
    private JCheckBox autoLinksCheckBox;
    private JCheckBox definitionsCheckBox;
    private JCheckBox fencedCodeBlocksCheckBox;
    private JCheckBox footnotesCheckBox;
    private JCheckBox forceListParaCheckBox;
    private JCheckBox githubWikiLinksCheckBox;
    private JCheckBox hardWrapsCheckBox;
    private JCheckBox headerSpaceCheckBox;
    private JCheckBox quotesCheckBox;
    private JCheckBox relaxedHRulesCheckBox;
    private JCheckBox smartsCheckBox;
    private JCheckBox strikethroughCheckBox;
    private JCheckBox suppressHTMLBlocksCheckBox;
    private JCheckBox suppressInlineHTMLCheckBox;
    private JCheckBox tablesCheckBox;
    private JCheckBox taskListsCheckBox;
    private JCheckBox wikiLinksCheckBox;
    private JLabel githubWikiLinksLabel;
    private JPanel mainPanel;

    @LicensedFeature
    private JCheckBox tocCheckBox;
    private JLabel tocLabel;

    // need this so that we don't try to access components before they are created
    @Nullable
    public Object getComponent(@NotNull String persistName) {
        if (persistName.equals("abbreviationsCheckBox")) return abbreviationsCheckBox;
        if (persistName.equals("anchorLinksCheckBox")) return anchorLinksCheckBox;
        if (persistName.equals("autoLinksCheckBox")) return autoLinksCheckBox;
        if (persistName.equals("definitionsCheckBox")) return definitionsCheckBox;
        if (persistName.equals("fencedCodeBlocksCheckBox")) return fencedCodeBlocksCheckBox;
        if (persistName.equals("footnotesCheckBox")) return footnotesCheckBox;
        if (persistName.equals("footnotesCheckBox")) return footnotesCheckBox;
        if (persistName.equals("forceListParaCheckBox")) return forceListParaCheckBox;
        if (persistName.equals("githubWikiLinksCheckBox")) return githubWikiLinksCheckBox;
        if (persistName.equals("githubWikiLinksCheckBox")) return githubWikiLinksCheckBox;
        if (persistName.equals("hardWrapsCheckBox")) return hardWrapsCheckBox;
        if (persistName.equals("headerSpaceCheckBox")) return headerSpaceCheckBox;
        if (persistName.equals("quotesCheckBox")) return quotesCheckBox;
        if (persistName.equals("relaxedHRulesCheckBox")) return relaxedHRulesCheckBox;
        if (persistName.equals("smartsCheckBox")) return smartsCheckBox;
        if (persistName.equals("strikethroughCheckBox")) return strikethroughCheckBox;
        if (persistName.equals("suppressHTMLBlocksCheckBox")) return suppressHTMLBlocksCheckBox;
        if (persistName.equals("suppressInlineHTMLCheckBox")) return suppressInlineHTMLCheckBox;
        if (persistName.equals("tablesCheckBox")) return tablesCheckBox;
        if (persistName.equals("taskListsCheckBox")) return taskListsCheckBox;
        if (persistName.equals("tocCheckBox")) return tocCheckBox;
        if (persistName.equals("wikiLinksCheckBox")) return wikiLinksCheckBox;

        return null;
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    public MarkdownExtensionsPanel() {
        // we don't change these
        githubWikiLinksCheckBox.setEnabled(false);
        //githubWikiLinksLabel.setVisible(false);

        // TODO: fix pegdown TOC extension and enable it here
        tocCheckBox.setVisible(false);
        tocLabel.setVisible(false);
    }

    @Override
    public void updateLicensedControls(boolean isLicensed) {
        if (!isLicensed) tocCheckBox.setSelected(false);
        tocCheckBox.setEnabled(isLicensed);
    }
}
