// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.ListenerDescriptor;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class MessageBusHolderImpl implements MessageBusHolder, MessageBusOwner {
    private @Nullable MessageBus myMessageBus;
    private boolean myIsDisposed = false;

    public MessageBusHolderImpl() {
        try {
            Class<?> messageBusFactory = Class.forName("com.intellij.util.messages.MessageBusFactory");
            Method method = messageBusFactory.getMethod("newMessageBus", MessageBusOwner.class);
            myMessageBus = (MessageBus) method.invoke(null, this);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Cannot create new message bus");
        }
    }

    @NotNull
    @Override
    public Object createListener(@NotNull ListenerDescriptor descriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDisposed() {
        return myIsDisposed;
    }

    @NotNull
    @Override
    public MessageBus getMessageBus() {
        if (myIsDisposed) throw new IllegalStateException("Message bus is already disposed");
        assert myMessageBus != null;
        return myMessageBus;
    }

    @Override
    public void dispose() {
        if (myMessageBus != null) {
            Disposer.dispose(myMessageBus);
            myMessageBus = null;
        }
        myIsDisposed = true;
    }
}
