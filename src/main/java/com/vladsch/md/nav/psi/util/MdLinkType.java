// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import org.jetbrains.annotations.Nullable;

public enum MdLinkType {
    LINK, IMAGE, WIKI;

    @Nullable
    public static MdLinkType fromOrdinal(int ordinal) {
        if (ordinal < values().length) {
            return values()[ordinal];
        }
        return null;
    }

    @Nullable
    public static MdLinkType fromName(String name) {
        for (MdLinkType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
