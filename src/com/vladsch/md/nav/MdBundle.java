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

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class MdBundle {
    @NonNls
    // OPTIMIZE: split up this bundle into strings by category.
    //   split out strings used during plugin load into separate bundle
    protected static final String BUNDLE_NAME = "com.vladsch.md.nav.localization.strings";

    protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private MdBundle() {
    }

    public static ResourceBundle getBundle() {
        return BUNDLE;
    }

    public static String getString(String key, Object... params) {
        return AbstractBundle.message(BUNDLE, key, params);
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return AbstractBundle.message(BUNDLE, key, params);
    }

    public static String messageOrBlank(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return AbstractBundle.messageOrDefault(BUNDLE, key, "", params);
    }
}
