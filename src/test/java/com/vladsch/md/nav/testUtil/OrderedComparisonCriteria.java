/*
 * Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.internal.ArrayComparisonFailure;

import java.lang.reflect.Array;

public abstract class OrderedComparisonCriteria<T> extends TypedComparisonCriteria<T> {
    public OrderedComparisonCriteria() {
        super();
    }

    @Override
    public void arrayEquals(String message, Object expecteds, Object actuals) throws ArrayComparisonFailure {
        if (expecteds != actuals) {
            String header = message == null ? "" : message + "\n: ";
            int expectedsLength = this.assertArraysAreSameLength(expecteds, actuals, header);

            for (int i = 0; i < expectedsLength; ++i) {
                Object expected = Array.get(expecteds, i);
                Object actual = Array.get(actuals, i);
                if (this.isArray(expected) && this.isArray(actual)) {
                    try {
                        this.arrayEquals(message, expected, actual);
                    } catch (ArrayComparisonFailure var10) {
                        //var10.addDimension(i);
                        //throw var10;
                        throw new ComparisonFailure(header + "array differed first at element [" + i + "]\n", TestCaseUtils.arrayAsString(expected), TestCaseUtils.arrayAsString(actual));
                    }
                } else {
                    try {
                        this.assertElementsEqual(expected, actual);
                    } catch (AssertionError var11) {
                        //throw new ArrayComparisonFailure(header, var11, i);
                        throw new ComparisonFailure(header + "array differed first at element [" + i + "]\n", TestCaseUtils.arrayAsString(expecteds), TestCaseUtils.arrayAsString(actuals));
                    }
                }
            }
        }
    }

    private boolean isArray(Object expected) {
        return expected != null && expected.getClass().isArray();
    }

    private int assertArraysAreSameLength(Object expecteds, Object actuals, String header) {
        if (expecteds == null) {
            Assert.fail(header + "expected array was null");
        }

        if (actuals == null) {
            Assert.fail(header + "actual array was null");
        }

        int actualsLength = Array.getLength(actuals);
        int expectedsLength = Array.getLength(expecteds);
        if (actualsLength != expectedsLength) {
            throw new ComparisonFailure(header + "array lengths differed, expected.length=" + expectedsLength + " actual.length=" + actualsLength, TestCaseUtils.arrayAsString(expecteds), TestCaseUtils.arrayAsString(actuals));
        }

        return expectedsLength;
    }
}
