/*
 * Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

package com.vladsch.md.nav.testUtil;

import org.junit.internal.ComparisonCriteria;

public abstract class TypedComparisonCriteria<T> extends ComparisonCriteria {
    public TypedComparisonCriteria() {
        super();
    }

    @Override
    protected void assertElementsEqual(Object o1, Object o2) {
        assertElementsAreEqual((T) o1, (T) o2);
    }

    protected abstract void assertElementsAreEqual(T o1, T o2);

    protected abstract boolean elementsAreEqual(T o1, T o2);
}
