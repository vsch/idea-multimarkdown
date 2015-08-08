/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
* Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
package com.vladsch.idea.multimarkdown.file;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link FileTypeFactory} responsible for registering {@link MarkdownFileType} with the system.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownFileTypeFactory extends FileTypeFactory {

    /**
     * Register extensions with the file type declared for Markdown.
     *
     * @param fileTypeConsumer the {@link FileTypeConsumer} to register extensions with.
     * @see MarkdownFileType#DEFAULT_ASSOCIATED_EXTENSIONS
     */
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        for (int i = 0; i < MarkdownFileType.DEFAULT_ASSOCIATED_EXTENSIONS.length; i++) {
            fileTypeConsumer.consume(MarkdownFileType.INSTANCE, MarkdownFileType.DEFAULT_ASSOCIATED_EXTENSIONS[i]);
        }
    }
}
