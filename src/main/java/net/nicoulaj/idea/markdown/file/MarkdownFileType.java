/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.file;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import net.nicoulaj.idea.markdown.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * TODO Add Javadoc comment.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownFileType extends LanguageFileType {

    /**
     * TODO Add Javadoc comment.
     */
    public static final MarkdownFileType MARKDOWN_FILE_TYPE = new MarkdownFileType();

    /**
     * TODO Add Javadoc comment.
     */
    public static final Language LANGUAGE = MARKDOWN_FILE_TYPE.getLanguage();

    /**
     * TODO Add Javadoc comment.
     */
    @NonNls
    public static final String[] DEFAULT_ASSOCIATED_EXTENSIONS = {"markdown", "mkd", "md"};

    /**
     * TODO Add Javadoc comment.
     */
    public MarkdownFileType() {
        super(new MarkdownLanguage());
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public String getName() {
        return MarkdownBundle.message("markdown.filetype.name");
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public String getDescription() {
        return MarkdownBundle.message("markdown.filetype.description");
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public String getDefaultExtension() {
        return DEFAULT_ASSOCIATED_EXTENSIONS[0];
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    public Icon getIcon() {
        return MarkdownIcons.MARKDOWN_ICON;
    }
}
