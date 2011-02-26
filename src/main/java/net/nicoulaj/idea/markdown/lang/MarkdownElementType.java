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
package net.nicoulaj.idea.markdown.lang;

import com.intellij.psi.tree.IElementType;
import net.nicoulaj.idea.markdown.file.MarkdownFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

/**
 * TODO Add Javadoc comment.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownElementType extends IElementType {

    /**
     * TODO Add Javadoc comment.
     *
     * @param debugName TODO Add Javadoc comment.
     */
    public MarkdownElementType(@NotNull @NonNls String debugName) {
        super(debugName, MarkdownFileType.LANGUAGE);
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @Override
    @SuppressWarnings({"HardCodedStringLiteral"})
    public String toString() {
        return MessageFormat.format("Markdown:{0}", super.toString());
    }
}
