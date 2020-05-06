// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import org.jetbrains.annotations.NotNull;

public class MessageBusCreator {
    @NotNull
    public static MessageBusHolder createHolder() {
        try {
            Class<?> messageBusOwner = Class.forName("com.intellij.util.messages.MessageBusOwner");
            return new MessageBusHolderImpl();
        } catch (Throwable e) {
            return new MessageBusHolderFallbackImpl();
        }
    }
}
