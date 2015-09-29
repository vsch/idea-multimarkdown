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

public abstract class ThreadSafeCacheUpdater<C> {
    public abstract C newCache();

    /**
     * called to build a new cache and invoke notifyWhenDone.cacheUpdated() with the new cache to be propagated
     * to all listeners
     *
     * @param notifyWhenDone
     * @param params
     */
    public abstract void updateCache(final ThreadSafeMainCache.CacheUpdater<C> notifyWhenDone, final Object... params);

    /**
     * called from within the notifyWhenDone.cacheUpdated() method.
     *
     * called right before starting notifications to mirror listeners, already in synchronized section with
     * the main cache updated and reporting isCacheCurrent() true.
     *
     * so new listeners will be blocked until all existing ones are notified, can do a remove() on the
     * ThreadLocal<> of the cache, so that all threads clear old cache and will be forced to load the new
     * one.
     *
     * When unblocked, the new listeners will load the new cache and continue
     *
     * @param params    what was passed to the updateCache() method
     */
    public abstract void beforeCacheUpdate(final Object... params);

    /**
     * called from within the notifyWhenDone.cacheUpdated() method.
     *
     * called right after all mirror listeners have been notified of the update and from outside the
     * synchronized section can now do whatever notifications are needed that access the cache
     *
     * @param params
     */
    public abstract void afterCacheUpdate(final Object... params);
}
