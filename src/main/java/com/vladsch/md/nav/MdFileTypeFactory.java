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

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import org.jetbrains.annotations.NotNull;

// DEPRECATED: replacement extension point com.intellij.fileType for FileTypeBean appeared in 2019-06-04
@SuppressWarnings("deprecation")
public class MdFileTypeFactory extends com.intellij.openapi.fileTypes.FileTypeFactory {
    final static public String DEFAULT_EXTENSION = "md";

    final static public String[] EXTENSIONS = { DEFAULT_EXTENSION, "mkd", "markdown" };

    static public String[] getExtensions() {
        return EXTENSIONS;
    }

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        for (String extension : EXTENSIONS) {
            fileTypeConsumer.consume(MdFileType.INSTANCE, extension);
        }
    }
}
