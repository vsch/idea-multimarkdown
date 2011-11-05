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
package net.nicoulaj.idea.markdown;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import net.nicoulaj.idea.markdown.file.MarkdownFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * {@link ApplicationComponent} loading Markdown support.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.4
 */
public class MarkdownLoader implements ApplicationComponent {

    /**
     * The component name used by {@link #getComponentName()}.
     */
    @NonNls
    protected static final String COMPONENT_NAME = "markdown.support.loader";

    /**
     * Get the unique name of this component.
     *
     * @return {@link #COMPONENT_NAME}
     */
    @NotNull @NonNls
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    /**
     * Initialize the component.
     * <p/>
     * Registers Markdown file types.
     */
    public void initComponent() {
        // FIXME Deprecated API usage
        FileTypeManager.getInstance().registerFileType(MarkdownFileType.INSTANCE, MarkdownFileType.DEFAULT_ASSOCIATED_EXTENSIONS);
    }

    /**
     * Dispose the component.
     */
    public void disposeComponent() {
    }
}
