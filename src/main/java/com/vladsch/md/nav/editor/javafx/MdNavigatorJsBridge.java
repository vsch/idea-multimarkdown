// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.javafx;

import com.vladsch.javafx.webview.debugger.JfxDebugProxyJsBridge;

public interface MdNavigatorJsBridge extends JfxDebugProxyJsBridge {
    void toggleTask(String pos);
}
