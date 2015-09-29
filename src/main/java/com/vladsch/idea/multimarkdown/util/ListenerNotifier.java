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

import java.lang.ref.WeakReference;
import java.util.HashSet;

public class ListenerNotifier<L> {
    final protected HashSet<WeakReference<L>> listeners = new HashSet<WeakReference<L>>();
    protected final WeakReference<ListenerNotifyDelegate<L>> updateDelegate;

    public Object getListeners() {
        return listeners;
    }

    public ListenerNotifier(@NotNull ListenerNotifyDelegate<L> updateDelegate) {
        this.updateDelegate = new WeakReference<ListenerNotifyDelegate<L>>(updateDelegate);
    }

    public void addListener(@NotNull final L listener, Object... params) {
        synchronized (listeners) {
            removeListener(listener);
            listeners.add(new WeakReference<L>(listener));

            // the delegate should check for necessary conditions for listener update
            ListenerNotifyDelegate<L> notifyDelegate = updateDelegate.get();
            if (notifyDelegate != null) notifyDelegate.notify(listener, params);
        }
    }

    public void removeListener(@NotNull final L listener) {
        synchronized (listeners) {
            WeakReference[] listenerList = listeners.toArray(new WeakReference[listeners.size()]);

            for (final WeakReference listenerRef : listenerList) {
                if (listenerRef.get() == null || listenerRef.get() == listener) {
                    listeners.remove(listenerRef);
                }
            }
        }
    }

    public void notifyListeners(Object... params) {
        synchronized (listeners) {
            ListenerNotifyDelegate<L> notifyDelegate = updateDelegate.get();
            if (notifyDelegate != null) {
                L listener;
                for (final WeakReference<L> listenerRef : listeners) {
                    if ((listener = listenerRef.get()) != null) notifyDelegate.notify(listener, params);
                }
            }
        }
    }
}
