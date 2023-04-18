// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.Disposable;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

public interface MessageBusHolder extends Disposable {
    @NotNull
    MessageBus getMessageBus();
}
