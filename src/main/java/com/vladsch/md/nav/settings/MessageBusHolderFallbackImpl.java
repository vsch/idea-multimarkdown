// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MessageBusHolderFallbackImpl implements MessageBusHolder {
    private @Nullable MessageBus myMessageBus;

    public MessageBusHolderFallbackImpl() {
        try {
            Class<?> messageBusFactory = Class.forName("com.intellij.util.messages.MessageBusFactory");
            Method method = messageBusFactory.getMethod("newMessageBus", Object.class);
            myMessageBus = (MessageBus) method.invoke(null, this);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Cannot create fallback message bus");
        }
    }

    @NotNull
    @Override
    public MessageBus getMessageBus() {
        if (myMessageBus == null) throw new IllegalStateException("Message bus is already disposed");
        return myMessageBus;
    }

    @Override
    public void dispose() {
        if (myMessageBus != null) {
            Disposer.dispose(myMessageBus);
            myMessageBus = null;
        }
    }
}
