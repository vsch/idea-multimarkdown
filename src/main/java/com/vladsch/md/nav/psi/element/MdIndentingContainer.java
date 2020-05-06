// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdIndentingContainer extends MdPsiElement {
    /**
     * Test whether the passed element is the text block of the indenting composite for determining whether the first line is subject to item prefix or line prefix.
     *
     * @param element element to test
     *
     * @return true if is the first indenting child
     */
    boolean isFirstItemBlock(@NotNull PsiElement element);

    @Nullable
    default PsiElement getFirstItemBlock() {
        PsiElement firstItemBlock = getFirstChild();
        while (firstItemBlock != null && !isFirstItemBlock(firstItemBlock)) {
            firstItemBlock = firstItemBlock.getNextSibling();
        }

        return firstItemBlock;
    }

    /**
     * Test whether need to add parent prefix to child's first line, happens for empty list items with block element child instead of text
     * <p>
     * Block Quotes should return true for their first item block since their prefix is added to first child items
     *
     * @param element element to test
     *
     * @return true if item prefix should be added to the child item
     */
    boolean isFirstItemBlockPrefix(@NotNull PsiElement element);
    // {
    //     ASTNode itemMarker = getLastPrefixMarker();
    //     if (itemMarker != null) {
    //         ASTNode treeNext = itemMarker.getTreeNext();
    //         if (treeNext != null) {
    //             return treeNext.getElementType() != MultiMarkdownTypes.EOL;
    //         }
    //     }
    //     return true;
    // }
}
