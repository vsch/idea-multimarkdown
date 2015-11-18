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
package com.vladsch.idea.multimarkdown.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MismatchReason {
    enum Severity {
        INFO(0), WEAK_WARNING(1), WARNING(2), ERROR(3);

        int value;

        Severity(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @NotNull public final String id;
    @NotNull public final Severity severity;
    @NotNull public final String fixedLinkRef;
    @NotNull public final String fixedWikiRef;
    @NotNull public final String fixedFilePath;
    @NotNull public final LazyCachedValue<FilePathInfo> fixedFilePathInfo = new LazyCachedValue<FilePathInfo>(new LazyCachedValue.Loader<FilePathInfo>() {
        @Override
        public FilePathInfo load() {
            return new FilePathInfo(fixedFilePath);
        }
    });

    protected MismatchReason(@NotNull String id, @NotNull Severity severity, @Nullable String fixedLinkRef, @Nullable String fixedWikiRef, @Nullable String fixedFilePath) {
        this.id = id;
        this.severity = severity;
        this.fixedLinkRef = fixedLinkRef == null ? "" : fixedLinkRef;
        this.fixedWikiRef = fixedWikiRef == null ? "" : fixedWikiRef;
        this.fixedFilePath = fixedFilePath == null ? "" : fixedFilePath;
    }

    @NotNull
    public FilePathInfo getFixedFilePathInfo() {
        return fixedFilePathInfo.get();
    }
}
