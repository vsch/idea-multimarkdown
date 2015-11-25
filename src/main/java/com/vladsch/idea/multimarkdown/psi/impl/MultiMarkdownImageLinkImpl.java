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
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownImageLink;
import com.vladsch.idea.multimarkdown.util.PathInfo;
import com.vladsch.idea.multimarkdown.util.StringUtilKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiMarkdownImageLinkImpl extends MultiMarkdownLinkElementImpl implements MultiMarkdownImageLink {
    public static String getElementText(@NotNull String linkRefWithAnchor, @Nullable String linkText, @Nullable String linkTitle) {
        return "!" + MultiMarkdownExplicitLinkImpl.getElementText(linkRefWithAnchor, linkText, linkTitle);
    }

    public static String getElementText(@NotNull String linkRef, @Nullable String linkText, @Nullable String linkAnchor, @Nullable String linkTitle) {
        return getElementText(linkRef + StringUtilKt.startWith(linkAnchor, '#'), linkText, linkTitle);
    }

    public MultiMarkdownImageLinkImpl(ASTNode node) {
        super(node);
    }
}
