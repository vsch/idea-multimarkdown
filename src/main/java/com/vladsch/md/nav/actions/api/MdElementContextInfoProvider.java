// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.api;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.flexmark.util.format.CharWidthProvider;
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo;
import com.vladsch.md.nav.actions.handlers.util.ListItemContext;
import com.vladsch.md.nav.actions.handlers.util.ParagraphContext;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import com.vladsch.md.nav.actions.handlers.util.WrappingContext;
import com.vladsch.md.nav.psi.element.MdListImpl;
import com.vladsch.md.nav.psi.element.MdListItemImpl;
import com.vladsch.md.nav.util.MdExtensions;
import com.vladsch.plugin.util.LazyComputable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdElementContextInfoProvider {
    ExtensionPointName<MdElementContextInfoProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.elementContextInfoProvider");
    MdExtensions<MdElementContextInfoProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdElementContextInfoProvider[0]);

    LazyComputable<MdElementContextInfoProvider> PROVIDER = new LazyComputable<>(() -> {
        for (MdElementContextInfoProvider provider : EXTENSIONS.getValue()) {
            return provider;
        }

        // use default implementation
        return new MdElementContextInfoProvider() {
            @NotNull
            @Override
            public ListItemContext getListItemContext(@NotNull CaretContextInfo context, @NotNull MdListImpl listElement, @NotNull MdListItemImpl listItemElement, int lineOffset, boolean isEmptyItem, boolean isTaskItem, boolean isItemDone, @NotNull WrappingContext wrappingContext) {
                return new ListItemContext(context, listElement, listItemElement, lineOffset, isEmptyItem, isTaskItem, isItemDone, wrappingContext);
            }

            @NotNull
            @Override
            public CharWidthProvider getCharWidthProvider(@NotNull PsiEditContext editContext, @NotNull Editor editor, int startOffset, int endOffset) {
                return CharWidthProvider.NULL;
            }

            @Nullable
            @Override
            public ParagraphContext getParagraphContext(@NotNull CaretContextInfo context) {
                return ParagraphContext.Companion.createContext(context);
            }
        };
    });

    @NotNull
    ListItemContext getListItemContext(
            @NotNull CaretContextInfo context,
            @NotNull MdListImpl listElement,
            @NotNull MdListItemImpl listItemElement,
            int lineOffset,
            boolean isEmptyItem,
            boolean isTaskItem,
            boolean isItemDone,
            @NotNull WrappingContext wrappingContext
    );

    @Nullable
    ParagraphContext getParagraphContext(@NotNull CaretContextInfo context);

    @NotNull
    CharWidthProvider getCharWidthProvider(@NotNull PsiEditContext editContext, @NotNull Editor editor, int startOffset, int endOffset);
}
