// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

public interface MdFormatElementHandler {
    ExtensionPointName<MdFormatElementHandler> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.formatElementHandler");
    MdExtensions<MdFormatElementHandler> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdFormatElementHandler[0]);

    /**
     * Format element at caret context info if able
     *
     * @param caretContext caret context information
     *
     * @return true if handled, false if not handled
     */
    boolean formatElement(@NotNull CaretContextInfo caretContext);

    /**
     * Stop wrap on typing for default typed char handler if implementing alternative
     *
     * @return true  if implementing alternative typed char handler which implements wrap on typing
     */
    default boolean skipWrapOnTyping() {
        return false;
    }

    /**
     * Stop default backspace handler if implementing alternative
     *
     * @return true  if implementing alternative typed char handler which implements wrap on typing
     */
    default boolean skipBackspaceHandler() {
        return false;
    }
}
