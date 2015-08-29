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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.settings;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class SettingsNotifierImpl<D> implements SettingsNotifier {

    protected Set<WeakReference<SettingsListener<D>>> listeners;

    protected final WeakReference<D> delegator;

    private int groupNotifications;
    private int suspendNotifications;

    private boolean needToNotify;

    public SettingsNotifierImpl(D delegator) {
        this.delegator = new WeakReference<D>(delegator);
    }

    public void addListener(@NotNull final SettingsListener<D> listener) {
        if (listeners == null) listeners = new HashSet<WeakReference<SettingsListener<D>>>();
        listeners.add(new WeakReference<SettingsListener<D>>(listener));
    }

    public void removeListener(@NotNull final SettingsListener<D> listener) {
        if (listeners != null) listeners.remove(listener);
    }

    public int startSuspendNotifications() {
        return suspendNotifications++;
    }

    public int endSuspendNotifications() {
        if (suspendNotifications == 0) return 0;
        suspendNotifications--;
        return suspendNotifications;
    }

    public int startGroupNotifications() {
        return groupNotifications++;
    }

    public int endGroupNotifications() {
        if (groupNotifications == 0) return 0;

        if (--groupNotifications == 0) {
            if (needToNotify) {
                needToNotify = false;
                notifyListeners();
            }
        }

        return groupNotifications;
    }

    public void notifyListeners() {
        if (suspendNotifications > 0) return;

        if (groupNotifications > 0) {
            needToNotify = true;
        } else {
            SettingsListener<D> listener;
            if (listeners != null) {
                for (final WeakReference<SettingsListener<D>> listenerRef : listeners) {
                    if ((listener = listenerRef.get()) != null) listener.handleSettingsChanged(delegator.get());
                }
            }
        }
    }
}
