// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MdNamesValidator implements NamesValidator {
    // markdown has no keywords
    @Override
    public boolean isKeyword(@NotNull String name, Project project) {
        return false;
    }

    // identifiers are all named elements that can be renamed, unfortunately we don't get any context at this point so we have to assume anything goes
    @Override
    public boolean isIdentifier(@NotNull String name, Project project) {
        return true;
    }
}
