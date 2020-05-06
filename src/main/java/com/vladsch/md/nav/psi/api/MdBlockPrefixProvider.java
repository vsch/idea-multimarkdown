// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import com.vladsch.md.nav.psi.element.MdIndentingComposite;
import com.vladsch.md.nav.psi.util.BlockPrefixes;
import com.vladsch.md.nav.psi.util.BlockQuotePrefix;
import com.vladsch.md.nav.psi.util.MdBlockPrefixProviderImpl;
import com.vladsch.md.nav.util.MdExtensions;
import com.vladsch.plugin.util.LazyComputable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public interface MdBlockPrefixProvider {
    ExtensionPointName<MdBlockPrefixProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.blockPrefixProvider");
    MdExtensions<MdBlockPrefixProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdBlockPrefixProvider[0]);

    @NotNull
    BlockPrefixes blockPrefixes(
            @NotNull MdIndentingComposite element,
            @Nullable BlockPrefixes parentPrefixes,
            @NotNull PsiEditContext editContext
    );

    int continuationIndent(int firstLineTextIndent, int parentTextIndent, @NotNull PsiEditContext editContext);

    @NotNull
    CharSequence adjustTaskItemPrefix(@NotNull CharSequence prefix, @NotNull PsiEditContext editContext);

    int orderedTaskItemPriority(@NotNull PsiEditContext editContext);

    @NotNull
    CharSequence adjustOrderedItemPrefix(
            int listItemOffset,
            int ordinal,
            int listItems,
            @NotNull CharSequence actualItemPrefix,
            @NotNull CharSequence actualTextPrefix,
            @Nullable Boolean listRenumberItems,
            @Nullable Integer listAlignNumeric,
            @NotNull PsiEditContext editContext
    );

    @NotNull
    CharSequence adjustBulletItemPrefix(@NotNull CharSequence prefix, boolean isTaskItem, @NotNull PsiEditContext editContext);

    @NotNull
    BlockQuotePrefix getBlockQuotePrefix(boolean isItem, @NotNull CharSequence childPrefix, @NotNull CharSequence childContPrefix, boolean noChildItems);

    @NotNull
    CharSequence getTaskItemPrefix(@NotNull PsiEditContext editContext);

    @NotNull
    CharSequence getTaskBulletItemPrefix(@NotNull PsiEditContext editContext);

    @NotNull
    CharSequence getBulletItemPrefix(@NotNull PsiEditContext editContext);

    @Nullable
    Icon getIcon(@NotNull PsiElement element);

    // *******************************
    // Static Helpers
    // *******************************
    LazyComputable<MdBlockPrefixProviderImpl> DEFAULT = new LazyComputable<>(MdBlockPrefixProviderImpl::new);

    LazyComputable<MdBlockPrefixProvider> PROVIDER = new LazyComputable<>(() -> {
        for (MdBlockPrefixProvider provider : EXTENSIONS.getValue()) {
            return provider;
        }

        // use default implementation
        return DEFAULT.getValue();
    });

    @NotNull
    static BlockPrefixes getBlockPrefixes(
            @NotNull MdIndentingComposite element,
            @Nullable BlockPrefixes parentPrefixes,
            @NotNull PsiEditContext editContext
    ) {
        return PROVIDER.getValue().blockPrefixes(element, parentPrefixes, editContext);
    }

    static int getContinuationIndent(int firstLineTextIndent, int parentTextIndent, @NotNull PsiEditContext editContext) {
        return PROVIDER.getValue().continuationIndent(firstLineTextIndent, parentTextIndent, editContext);
    }

    @NotNull
    static CharSequence getAdjustedBulletItemPrefix(@NotNull CharSequence prefix, boolean isTaskItem, @NotNull PsiEditContext editContext) {
        return PROVIDER.getValue().adjustBulletItemPrefix(prefix, isTaskItem, editContext);
    }

    @NotNull
    static CharSequence getAdjustedOrderedItemPrefix(
            int listItemOffset,
            int ordinal,
            int listItems,
            @NotNull CharSequence actualItemPrefix,
            @NotNull CharSequence actualTextPrefix,
            @Nullable Boolean LIST_RENUMBER_ITEMS,
            @Nullable Integer LIST_ALIGN_NUMERIC,
            @NotNull PsiEditContext editContext
    ) {
        return PROVIDER.getValue().adjustOrderedItemPrefix(listItemOffset, ordinal, listItems, actualItemPrefix, actualTextPrefix, LIST_RENUMBER_ITEMS, LIST_ALIGN_NUMERIC, editContext);
    }

    @NotNull
    static CharSequence getAdjustedTaskItemPrefix(@NotNull CharSequence prefix, @NotNull PsiEditContext editContext) {
        return PROVIDER.getValue().adjustTaskItemPrefix(prefix, editContext);
    }

    static int getOrderedTaskItemPriority(@NotNull PsiEditContext editContext) {
        return PROVIDER.getValue().orderedTaskItemPriority(editContext);
    }

    @NotNull
    static BlockQuotePrefix createBlockQuotePrefix(boolean isItem, @NotNull CharSequence childPrefix, @NotNull CharSequence childContPrefix, boolean noChildItems) {
        return PROVIDER.getValue().getBlockQuotePrefix(isItem, childPrefix, childContPrefix, noChildItems);
    }

    @NotNull
    static CharSequence taskItemPrefix(@NotNull PsiEditContext editContext) {
        return PROVIDER.getValue().getTaskItemPrefix(editContext);
    }

    @NotNull
    static CharSequence bulletItemPrefix(@NotNull PsiEditContext editContext) {
        return PROVIDER.getValue().getBulletItemPrefix(editContext);
    }

    @NotNull
    static CharSequence taskBulletItemPrefix(@NotNull PsiEditContext editContext) {
        return PROVIDER.getValue().getTaskBulletItemPrefix(editContext);
    }

    @Nullable
    static Icon elementIcon(@NotNull PsiElement element) {
        return PROVIDER.getValue().getIcon(element);
    }
}
