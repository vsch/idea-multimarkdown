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

public class ThreadSafeMainCache<C> extends ThreadSafeMirrorCache<C> {
    private static final int LISTENER_ADDED = 1;
    private static final int LISTS_UPDATED = 2;
    private static final int UPDATE_DONE = 3;

    private CacheProvider<C> cacheFactory;

    private boolean isListLoaded = false;
    private boolean isListLoading = false;
    private ListenerNotifier<CacheUpdateListener<ThreadSafeMirrorCache<C>>> notifier = new ListenerNotifier<CacheUpdateListener<ThreadSafeMirrorCache<C>>>();

    public ThreadSafeMainCache(CacheProvider<C> cacheFactory) {
        super(cacheFactory.newCache());
        this.cacheFactory = cacheFactory;
    }

    protected C getNewCache() {
        return cacheFactory.newCache();
    }

    public boolean cacheIsCurrent() {
        return isListLoaded && !isListLoading;
    }

    public void addListener(@NotNull CacheUpdateListener<ThreadSafeMirrorCache<C>> listener) {
        // used only during addListener processing
        final ThreadSafeMainCache<C> thizz = this;

        notifier.addListener(listener, new ListenerNotifier.RunnableNotifier<CacheUpdateListener<ThreadSafeMirrorCache<C>>>() {
            @Override
            public boolean notify(CacheUpdateListener<ThreadSafeMirrorCache<C>> listener) {
                if (cacheIsCurrent()) {
                    listener.updateCache(thizz);
                }
                return false;
            }
        });
    }

    public void removeListener(@NotNull CacheUpdateListener<ThreadSafeMirrorCache<C>> listener) {
        notifier.removeListener(listener);
    }

    public void notifyListeners() {
        final ThreadSafeMainCache<C> thizz = this;

        notifier.notifyListeners(new ListenerNotifier.RunnableNotifier<CacheUpdateListener<ThreadSafeMirrorCache<C>>>() {
            @Override
            public boolean notify(CacheUpdateListener<ThreadSafeMirrorCache<C>> listener) {
                listener.updateCache(thizz);
                return false;
            }
        });
    }

    public void notifyUpdateDone() {
        notifier.notifyListeners(new ListenerNotifier.RunnableNotifier<CacheUpdateListener<ThreadSafeMirrorCache<C>>>() {
            @Override
            public boolean notify(CacheUpdateListener<ThreadSafeMirrorCache<C>> listener) {
                listener.updateDone();
                return false;
            }
        });
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

            cacheFactory.updateCache(new UpdateDoneListener<C>() {
                @Override
                public void cacheUpdated(@NotNull C newCache) {

                    synchronized (notifier.getListeners()) {
                        mainCache.cache = newCache;

                        mainCache.isListLoaded = true;
                        mainCache.isListLoading = false;

                        cacheFactory.beforeCacheUpdate(params);
                        mainCache.notifyListeners();
                    }

                    // now the can use the data it is consistent across all threads
                    mainCache.notifyUpdateDone();

                    cacheFactory.afterCacheUpdate(params);
                }
            }, params);
        }
    }

    public static interface CacheUpdateListener<C> {
        // use the new cached data
        void updateCache(C updatedCache);

        // all listeners have been updated
        void updateDone();
    }

    public interface UpdateDoneListener<C> {
        void cacheUpdated(@NotNull C newCache);
    }

    static interface CacheProvider<C> {
        C newCache();

        /**
         * called to build a new cache and invoke notifyWhenDone.cacheUpdated() with the new cache to be propagated
         * to all listeners
         *
         * @param notifyWhenDone
         * @param params
         */
        void updateCache(final UpdateDoneListener<C> notifyWhenDone, final Object... params);

        /**
         * called from within the notifyWhenDone.cacheUpdated() method.
         * <p/>
         * called right before starting notifications to mirror listeners, already in synchronized section with
         * the main cache updated and reporting isCacheCurrent() true.
         * <p/>
         * so new listeners will be blocked until all existing ones are notified, can do a remove() on the
         * ThreadLocal<> of the cache, so that all threads clear old cache and will be forced to load the new
         * one.
         * <p/>
         * When unblocked, the new listeners will load the new cache and continue
         *
         * @param params what was passed to the updateCache() method
         */
        void beforeCacheUpdate(final java.lang.Object... params);

        /**
         * called from within the notifyWhenDone.cacheUpdated() method.
         * <p/>
         * called right after all mirror listeners have been notified of the update and from outside the
         * synchronized section can now do whatever notifications are needed that access the cache
         *
         * @param params
         */
        void afterCacheUpdate(final java.lang.Object... params);
    }

}
