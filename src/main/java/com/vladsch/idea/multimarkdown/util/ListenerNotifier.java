/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashSet;

public class ListenerNotifier<L> {
    final protected HashSet<WeakReference<L>> listeners = new HashSet<WeakReference<L>>();
    public HashSet<WeakReference<L>> getListeners() {
        return listeners;
    }

    public interface RunnableNotifier<L> {
        boolean notify(L listener);
    }

    public ListenerNotifier() {

    }

    public void addListener(@NotNull final L listener) {
        addListener(listener, null);
    }

    public void addListener(@NotNull final L listener, @Nullable RunnableNotifier<L> runnableNotifier) {
        synchronized (listeners) {
            removeListener(listener);
            listeners.add(new WeakReference<L>(listener));

            // the delegate should check for necessary conditions for listener update
            if (runnableNotifier != null) runnableNotifier.notify(listener);
        }
    }

    public void removeListener(@NotNull final L listener) {
        removeListener(listener, null);
    }

    public void removeListener(@NotNull final L listener, @Nullable RunnableNotifier<L> runnableNotifier) {
        synchronized (listeners) {
            WeakReference[] listenerList = listeners.toArray(new WeakReference[listeners.size()]);

            for (WeakReference listenerRef : listenerList) {
                if (listenerRef.get() == null || listenerRef.get() == listener) {
                    listeners.remove(listenerRef);
                }
            }

            if (runnableNotifier != null) notifyListeners(runnableNotifier);
        }
    }

    public void notifyListeners(@NotNull RunnableNotifier<L> runnableNotifier) {
        synchronized (listeners) {
            WeakReference[] listenerList = listeners.toArray(new WeakReference[listeners.size()]);

            L listener;
            for (WeakReference listenerRef : listenerList) {
                if ((listener = (L) listenerRef.get()) != null && runnableNotifier.notify(listener)) break;
            }
        }
    }
}
