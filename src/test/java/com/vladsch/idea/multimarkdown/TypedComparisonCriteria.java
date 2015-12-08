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
 *
 */
package com.vladsch.idea.multimarkdown;

import org.junit.internal.ComparisonCriteria;

public abstract class TypedComparisonCriteria<T> extends ComparisonCriteria {
    public TypedComparisonCriteria() {
        super();
    }

    @Override
    protected void assertElementsEqual(Object o1, Object o2) {
        assertElementsAreEqual((T)o1, (T)o2);
    }

    protected abstract void assertElementsAreEqual(T o1, T o2);
    protected abstract boolean elementsAreEqual(T o1, T o2);
}
