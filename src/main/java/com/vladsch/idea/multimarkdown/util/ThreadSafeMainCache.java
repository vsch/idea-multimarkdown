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

public class ThreadSafeMainCache<C> extends ThreadSafeMirrorCache<C> implements ListenerNotifyDelegate<ThreadSafeCacheListener<ThreadSafeMirrorCache<C>>> {
    private static final int LISTENER_ADDED = 1;
    private static final int LISTS_UPDATED = 2;
    private static final int UPDATE_DONE = 3;

    private ThreadSafeCacheUpdater<C> cacheFactory;

    private boolean isListLoaded = false;
    private boolean isListLoading = false;
    private ListenerNotifier<ThreadSafeCacheListener<ThreadSafeMirrorCache<C>>> notifier = new ListenerNotifier<ThreadSafeCacheListener<ThreadSafeMirrorCache<C>>>(this);

    public ThreadSafeMainCache(ThreadSafeCacheUpdater<C> cacheFactory) {
        super(cacheFactory.newCache());
        this.cacheFactory = cacheFactory;
    }

    protected C getNewCache() {
        return cacheFactory.newCache();
    }

    public boolean cacheIsCurrent() {
        return isListLoaded && !isListLoading;
    }

    public void addListener(@NotNull ThreadSafeCacheListener<ThreadSafeMirrorCache<C>> listener) {
        // used only during addListener processing
        notifier.addListener(listener, LISTENER_ADDED);
    }

    @Override
    public void notify(ThreadSafeCacheListener<ThreadSafeMirrorCache<C>> listener, Object... params) {
        if (params.length == 1) {
            switch ((Integer) params[0]) {
                case LISTENER_ADDED:
                    if (cacheIsCurrent()) {
                        listener.updateCache(this);
                    }
                    break;

                case  LISTS_UPDATED:
                    listener.updateCache(this);
                    break;

                case UPDATE_DONE:
                    listener.updateDone();
                    break;

                default:
                    break;
            }
        }
    }

    public void removeListener(@NotNull ThreadSafeCacheListener<ThreadSafeMirrorCache<C>> listener) {
        notifier.removeListener(listener);
    }

    public void notifyListeners() {
        notifier.notifyListeners(LISTS_UPDATED);
    }

    public void notifyUpdateDone() {
        notifier.notifyListeners(UPDATE_DONE);
    }

    public interface CacheUpdater<C> {
        void cacheUpdated(@NotNull C newCache);
    }

    public void updateCache(final Object... params) {
        boolean alreadyLoading = true;

        synchronized (notifier.getListeners()) {
            if (!isListLoading) {
                isListLoading = true;
                alreadyLoading = false;
            }
        }

        if (!alreadyLoading) {
            final ThreadSafeMainCache<C> mainCache = this;

            cacheFactory.updateCache(new CacheUpdater<C>() {
                @Override
                public void cacheUpdated(@NotNull C newCache) {

                    synchronized (notifier.getListeners()) {
                        mainCache.cache = newCache;

                        mainCache.isListLoaded = true;
                        mainCache.isListLoading = false;

                        cacheFactory.beforeCacheUpdate(params);
                        mainCache.notifyListeners();
                    }

                    // now the can use the data it is consistent accross all threads
                    mainCache.notifyUpdateDone();

                    cacheFactory.afterCacheUpdate();
                }
            }, params);
        }
    }
}
