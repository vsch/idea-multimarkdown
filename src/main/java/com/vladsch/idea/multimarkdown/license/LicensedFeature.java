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
package com.vladsch.idea.multimarkdown.license;

import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(LicensedFeatures.class)
public @interface LicensedFeature {
    enum LicenseType {
        ALL(null, 0xffffffff),
        TRIAL(LicenseAgent.LICENSE_TYPE_TRIAL, 1),
        SUBSCRIPTION(LicenseAgent.LICENSE_TYPE_SUBSCRIPTION, 2),
        LICENSE(LicenseAgent.LICENSE_TYPE_LICENSE, 4);

        private final String type;
        private final int flags;

        LicenseType(String type, int flags) {
            this.type = type;
            this.flags = flags;
        }

        boolean isLicensedFor(@NotNull String type) {
            return this.type == null || this.type.equals(type);
        }

        int getLicenseFlags() {
            return flags;
        }
    }

    LicenseType type() default LicenseType.ALL;
}
