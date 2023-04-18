// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.vladsch.flexmark.util.sequence.LineAppendable;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import com.vladsch.md.nav.psi.util.BlockPrefixes;
import com.vladsch.md.nav.util.format.LinePrefixMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdIndentingComposite extends MdComposite, MdIndentingContainer {
    /**
     * Remove any composite block prefixes from child returned text can be used to get child text without parental context and can be used to create these elements at the file level
     * <p>
     * Will remove block quote prefixes, list item indentation, etc.
     *
     * @param lines         child text lines
     * @param indentColumns int array that holds indent columns for lines on input and updated for new indent columns on return
     * @param isFirstChild  if this is the first child of the block
     * @param editContext   edit context for the operation
     */
    void removeLinePrefix(
            @NotNull LineAppendable lines,
            @NotNull int[] indentColumns,
            boolean isFirstChild,
            @NotNull PsiEditContext editContext
    );

    /**
     * prefixes to use on the indented block.
     *
     * @param editContext edit context to use for getting actual text
     *
     * @return pair of prefixes, first is for first line of the first child, second is the the rest of the lines of the first block, and all the lines of the other child blocks.
     */
    @NotNull
    BlockPrefixes itemPrefixes(
            @Nullable BlockPrefixes parentPrefixes,
            @NotNull PsiEditContext editContext
    );

    /**
     * Actual current prefix of the item for those elements that have items
     *
     * @param editContext editContext to use to get the item text
     *
     * @return characterSequence of the item prefix
     */
    @NotNull
    CharSequence actualItemPrefix(@NotNull PsiEditContext editContext);

    /**
     * Prefix before the text block for elements that support this
     *
     * @param editContext      editContext to use to get the item text
     * @param addTrailingSpace true to add trailing space after the prefix
     *
     * @return characterSequence of the item prefix
     */
    @NotNull
    CharSequence actualTextPrefix(@NotNull PsiEditContext editContext, boolean addTrailingSpace);

    /**
     * Prefix before the text block for elements that support this
     *
     * @param addTrailingSpace true to add trailing space after the prefix
     *
     * @return string of the item prefix from Psi
     */
    @NotNull
    CharSequence actualTextPrefix(boolean addTrailingSpace);

    /**
     * Get the content indent offset for this element
     *
     * @return content offset for this element
     */
    int contentIndent();

    /**
     * Return true if the given node is the start of the element's text item content
     *
     * @param node node to test
     *
     * @return true if the node is the start of element's text item content
     */
    boolean isTextStart(@NotNull ASTNode node);

    /**
     * Return true if the element has no item text
     *
     * @return true if the element has no item text
     */
    boolean isEmptyText();

    /**
     * Get LinePrefixMatcher for this element
     *
     * @param editContext edit context for the operation
     *
     * @return line prefix matcher
     */
    @NotNull
    LinePrefixMatcher getPrefixMatcher(@NotNull PsiEditContext editContext);
}
