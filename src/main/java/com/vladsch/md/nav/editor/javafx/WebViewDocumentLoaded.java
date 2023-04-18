// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.javafx;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

public interface WebViewDocumentLoaded {
    Topic<WebViewDocumentLoaded> TOPIC = Topic.create("WebViewDocumentLoaded", WebViewDocumentLoaded.class, Topic.BroadcastDirection.NONE);

    void onDocumentLoaded(@NotNull VirtualFile virtualFile);
}
