// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface MdCaretContextInfoHandler {
    ExtensionPointName<MdCaretContextInfoHandler> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.caretContextInfoHandler");
    MdExtensions<MdCaretContextInfoHandler> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdCaretContextInfoHandler[0]);

    /**
     * Prepare context for beforeCharTypedHandler
     *
     * @param context context being initialized
     */
    default void initializeContext(@NotNull CaretContextInfo context, @NotNull Consumer<Runnable> doneHandlerConsumer) {}

    default void beforeCharTypedHandler(@NotNull CaretContextInfo context, @NotNull Consumer<Runnable> doneHandlerConsumer) {}

    default void charTypedHandler(@NotNull CaretContextInfo context, @NotNull Consumer<Runnable> doneHandlerConsumer) {}

    default void beforeBackspaceHandler(@NotNull CaretContextInfo context, @NotNull Consumer<Runnable> doneHandlerConsumer) {}

    default void backspaceHandler(@NotNull CaretContextInfo context, @NotNull Consumer<Runnable> doneHandlerConsumer) {}

    default void beforeEnterHandler(@NotNull CaretContextInfo context, @NotNull Consumer<Runnable> doneHandlerConsumer) {}

    default void enterHandler(@NotNull CaretContextInfo context, @NotNull Consumer<Runnable> doneHandlerConsumer) {}
}
