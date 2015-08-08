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

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.vladsch.idea.multimarkdown.MarkdownBundle;
import com.vladsch.idea.multimarkdown.MarkdownIcons;
import com.vladsch.idea.multimarkdown.MarkdownLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * The {@link LanguageFileType} for Markdown files.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownFileType extends LanguageFileType {

    /** The {@link MarkdownFileType} instance. */
    public static final MarkdownFileType INSTANCE = new MarkdownFileType();

    /** The {@link Language} instance for this {@link com.intellij.openapi.fileTypes.FileType}. */
    public static final Language LANGUAGE = INSTANCE.getLanguage();

    /** The extensions associated by default with this {@link com.intellij.openapi.fileTypes.FileType}. */
    @NonNls
    public static final String[] DEFAULT_ASSOCIATED_EXTENSIONS = {"multimarkdown", "mkd", "md"};

    /** Build a new instance of {@link MarkdownFileType}. */
    public MarkdownFileType() {
        super(new MarkdownLanguage());
    }

    /**
     * Get the name associated with this {@link com.intellij.openapi.fileTypes.FileType}.
     *
     * @return the name as defined by {@link MarkdownBundle}.
     */
    @NotNull
    public String getName() {
        return MarkdownBundle.message("markdown.filetype.name");
    }

    /**
     * Get the description associated with this {@link com.intellij.openapi.fileTypes.FileType}.
     *
     * @return the description as defined by {@link MarkdownBundle}.
     */
    @NotNull
    public String getDescription() {
        return MarkdownBundle.message("markdown.filetype.description");
    }

    /**
     * Get the default extension for this {@link com.intellij.openapi.fileTypes.FileType}.
     *
     * @return the first entry of {@link #DEFAULT_ASSOCIATED_EXTENSIONS}.
     */
    @NotNull
    public String getDefaultExtension() {
        return DEFAULT_ASSOCIATED_EXTENSIONS[0];
    }

    /**
     * Get the icon associated with this {@link com.intellij.openapi.fileTypes.FileType}.
     *
     * @return {@link MarkdownIcons#MARKDOWN_ICON}.
     */
    public Icon getIcon() {
        return MarkdownIcons.MARKDOWN_ICON;
    }
}
