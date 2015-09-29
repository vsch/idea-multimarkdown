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
package com.vladsch.idea.multimarkdown.language;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownNamesValidator implements NamesValidator {

    // markdown has no keywords
    @Override public boolean isKeyword(@NotNull String name, Project project) {
        return false;
    }

    // identifiers are all named elements that can be renamed, unfortunately we don't get any context at this point so we have to assume anything goes
    @Override public boolean isIdentifier(@NotNull String name, Project project) {
        //List<MultiMarkdownFile> list = MultiMarkdownPlugin.getProjectComponent(project).findRefLinkMarkdownFiles(name, MARKDOWN_FILE | WANT_WIKI_REF | WIKI_REF | ALLOW_INACCESSIBLE_WIKI_REF);
        //return list.size() > 0;
        return true;
    }
}
