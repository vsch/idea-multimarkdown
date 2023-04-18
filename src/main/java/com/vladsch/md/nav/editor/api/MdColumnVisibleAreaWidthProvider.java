// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.api;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.impl.softwrap.mapping.SoftWrapApplianceManager;

public interface MdColumnVisibleAreaWidthProvider extends SoftWrapApplianceManager.VisibleAreaWidthProvider, Disposable {
    boolean isInDistractionFreeMode();

    int getPlainSpaceWidth();

    int getMinDrawingWidth();

    void setMinDrawingWidth(int minDrawingWidth);

    int getSoftWraps();
}
