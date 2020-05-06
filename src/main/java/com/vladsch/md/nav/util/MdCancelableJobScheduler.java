// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import com.intellij.openapi.components.ServiceManager;
import com.vladsch.plugin.util.CancelableJobScheduler;

public class MdCancelableJobScheduler extends CancelableJobScheduler {
    public static MdCancelableJobScheduler getInstance() {
        return ServiceManager.getService(MdCancelableJobScheduler.class);
    }
}
