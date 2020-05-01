// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.inspections;

public class SubInspectionDescriptor<T> {
    final public T options;
    final public String description;
    final public String propertyName;

    public SubInspectionDescriptor(String propertyName, String description) {
        this(propertyName, description, null);
    }

    public SubInspectionDescriptor(String propertyName, String description, T options) {
        this.propertyName = propertyName;
        this.description = description;
        this.options = options;
    }
}
