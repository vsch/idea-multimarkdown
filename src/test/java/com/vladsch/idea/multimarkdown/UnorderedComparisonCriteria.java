/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown;

import org.junit.Assert;
import org.junit.internal.ArrayComparisonFailure;

import java.lang.reflect.Array;

public abstract class UnorderedComparisonCriteria<T> extends TypedComparisonCriteria<T> {
    public UnorderedComparisonCriteria() {
        super();
    }

    @Override
    public void arrayEquals(String message, Object expecteds, Object actuals) throws ArrayComparisonFailure {
        if (expecteds != actuals) {
            String header = message == null ? "" : message + ": ";
            int expectedsLength = this.assertArraysAreSameLength(expecteds, actuals, header);

            Object[] expectedOrdered = new Object[expectedsLength];
            boolean[] usedUpActuals = new boolean[expectedsLength];
            boolean[] usedUpExpecteds = new boolean[expectedsLength];

            for (int i = 0; i < expectedsLength; i++) {
                for (int j = 0; j < expectedsLength; j++) {
                    if (!usedUpActuals[j] && elementsAreEqual((T)Array.get(expecteds, i), (T)Array.get(actuals,j))) {
                        usedUpActuals[j] = true;
                        usedUpExpecteds[i] = true;
                        expectedOrdered[j] = Array.get(expecteds, i);
                        break;
                    }
                }
            }

            // transfer the rest
            for (int i = 0; i < expectedsLength; i++) {
                if (!usedUpExpecteds[i]) {
                    for (int j = 0; j < expectedsLength; j++) {
                        if (!usedUpActuals[j]) {
                            usedUpActuals[j] = true;
                            usedUpExpecteds[i] = true;
                            expectedOrdered[j] = Array.get(expecteds, i);
                            break;
                        }
                    }
                }
            }

            for (int i = 0; i < expectedsLength; ++i) {
                Object expected = Array.get(expectedOrdered, i);
                Object actual = Array.get(actuals, i);
                if (this.isArray(expected) && this.isArray(actual)) {
                    try {
                        this.arrayEquals(message, expected, actual);
                    } catch (ArrayComparisonFailure var10) {
                        var10.addDimension(i);
                        throw var10;
                    }
                } else {
                    try {
                        this.assertElementsEqual(expected, actual);
                    } catch (AssertionError var11) {
                        throw new ArrayComparisonFailure(header, var11, i);
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
            Assert.fail(header + "array lengths differed, expected.length=" + expectedsLength + " actual.length=" + actualsLength);
        }

        return expectedsLength;
    }

}
