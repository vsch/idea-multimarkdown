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
