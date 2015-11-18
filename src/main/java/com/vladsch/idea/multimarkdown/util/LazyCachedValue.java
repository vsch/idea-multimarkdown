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

public class LazyCachedValue<T> {
    @Nullable protected T value;
    @NotNull final protected Loader<T> valueLoader;
    protected boolean invalid;

    interface Loader<T> {
        T load();
    }

    public void invalidate() {
        invalid = true;
    }

    public void cache(T value) {
        this.value = value;
        invalid = false;
    }

    public void recompute() {
        cache(valueLoader.load());
    }

    public LazyCachedValue(@NotNull Loader<T> valueLoader) {
        this.valueLoader = valueLoader;
        invalidate();
    }

    public LazyCachedValue(@NotNull Loader<T> valueLoader, @NotNull LazyCacheGroup cacheGroup) {
        this.valueLoader = valueLoader;
        invalidate();
        cacheGroup.add(this);
    }

    public T get() {
        if (invalid) {
            recompute();
        }
        return value;
    }
}
