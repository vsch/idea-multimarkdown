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

public class ThreadSafeMirrorCache<C> {
    protected C cache;
    protected ThreadSafeCacheListener<ThreadSafeMirrorCache<C>> mainCacheListener;

    public @NotNull C getCache() {
        return cache;
    }

    // this will only be called for the derived MainFileList instances
    ThreadSafeMirrorCache() {
        cache = null;
    }

    public ThreadSafeMirrorCache(final @NotNull ThreadSafeMainCache<C> mainCache) {
        cache = null;

        mainCache.addListener(mainCacheListener = new ThreadSafeCacheListener<ThreadSafeMirrorCache<C>>() {
            @Override
            public void updateCache(ThreadSafeMirrorCache<C> updatedCache) {
                cache = updatedCache.getCache();
            }

            @Override
            public void updateDone() {

            }
        });
    }
}
