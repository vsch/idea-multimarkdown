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

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdFileType extends LanguageFileType {
    public static final MdFileType INSTANCE = new MdFileType();

    private MdFileType() {
        super(MdLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return MdBundle.message("multimarkdown.filetype.name");
    }

    @NotNull
    @Override
    public String getDescription() {
        return MdBundle.message("multimarkdown.filetype.description");
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return MdFileTypeFactory.DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MdIcons.getDocumentIcon();
    }
}
