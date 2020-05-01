// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

public interface StateHolder {
    @Nullable
    Element saveState(@Nullable Element element);

    void loadState(@Nullable Element element);
}
