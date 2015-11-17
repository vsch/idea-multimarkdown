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

public class WormValueCache<T> {
    protected T value;
    protected Loader<T> valueLoader;

    interface Loader<T> {
        T load();
    }

    public WormValueCache(Loader<T> valueLoader) {
        this.valueLoader = valueLoader;
    }

    public T get() {
        if (valueLoader != null) {
            value = valueLoader.load();
            valueLoader = null;
        }
        return value;
    }
}
