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
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownExplicitLink;
import com.vladsch.idea.multimarkdown.util.PathInfo;
import com.vladsch.idea.multimarkdown.util.StringUtilKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiMarkdownExplicitLinkImpl extends MultiMarkdownLinkElementImpl implements MultiMarkdownExplicitLink {
    public static String getElementText(@NotNull String linkRefWithAnchor, @Nullable String linkText, @Nullable String linkTitle) {
        if (linkText == null || linkText.isEmpty()) linkText = new PathInfo(linkRefWithAnchor).getFileNameNoExt();
        return "[" + linkText + "](" + MultiMarkdownLinkElementImpl.getElementLinkRefWithAnchor(linkRefWithAnchor) + StringUtilKt.wrapWith(linkTitle, " '", "'") + ")";
    }

    public static String getElementText(@NotNull String linkRef, @Nullable String linkText, @Nullable String linkAnchor, @Nullable String linkTitle) {
        return getElementText(linkRef + StringUtilKt.prefixWith(linkAnchor, '#'), linkText, linkTitle);
    }

    public MultiMarkdownExplicitLinkImpl(ASTNode node) {
        super(node);
    }
}
