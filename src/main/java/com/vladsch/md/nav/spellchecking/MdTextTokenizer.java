// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.spellchecking;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.spellchecker.inspections.TextSplitter;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import com.vladsch.flexmark.util.sequence.RepeatedSequence;
import com.vladsch.md.nav.parser.LexParserState;
import com.vladsch.md.nav.psi.util.MdTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.vladsch.md.nav.spellchecking.MdSpellcheckingStrategy.NO_SPELL_CHECK_SET;
import static com.vladsch.plugin.util.psi.PsiUtils.isTypeOf;

public class MdTextTokenizer extends Tokenizer<PsiElement> {

    private final List<IElementType> INLINE_NON_PLAIN_TEXT;

    public MdTextTokenizer() {
        INLINE_NON_PLAIN_TEXT = LexParserState.getInstance().getState().INLINE_NON_PLAIN_TEXT;
    }

    void flatLeafNodes(ASTNode node, List<ASTNode> leafElements) {
        ASTNode astNode = node.getFirstChildNode();
        if (astNode == null) {
            // must be leaf
            leafElements.add(node);
        } else {
            do {
                flatLeafNodes(astNode, leafElements);
                astNode = astNode.getTreeNext();
            } while (astNode != null);
        }
    }

    protected boolean spellCheckedType(IElementType elementType) {
        return elementType == MdTypes.COMMENT_TEXT || elementType == MdTypes.BLOCK_COMMENT_TEXT
                || !(isTypeOf(elementType, NO_SPELL_CHECK_SET) || INLINE_NON_PLAIN_TEXT.contains(elementType));
    }

    protected List<ASTNode> getFlatNodes(@NotNull PsiElement element) {
        ArrayList<ASTNode> leafNodes = new ArrayList<>();
        flatLeafNodes(element.getNode(), leafNodes);
        return leafNodes;
    }

    protected boolean useRename() {
        return false;
    }

    protected List<ASTNode> getSpellCheckableNodes(@NotNull PsiElement element) {
        // need a flat list of leaf elements, not combination nodes
        List<ASTNode> leafNodes = getFlatNodes(element);

        int iMax = leafNodes.size();
        int i = 0;

        // remove leading non-spellchecking nodes
        while (i < iMax) {
            IElementType elementType = leafNodes.get(i).getElementType();
            if (spellCheckedType(elementType)) break;
            i++;
        }

        // remove trailing non-spellchecking nodes
        while (i < iMax) {
            IElementType elementType = leafNodes.get(i).getElementType();
            if (spellCheckedType(elementType)) break;
            iMax--;
        }

        return leafNodes.subList(i, iMax);
    }

    @Override
    public void tokenize(@NotNull PsiElement element, TokenConsumer consumer) {
        StringBuilder text = new StringBuilder(element.getTextLength());

        // extract a range of spell checkable elements, leave out any leading/trailing non-checkable
        // and any non-checkable in the middle replace with spaces
        // to make the spelling error underline align with the text add skipped characters before
        // a new spell-checkable span to compensate for skipped characters
        // FIX: implementing a check for new spellchecking span that matches TextSplitter rules would be best
        List<ASTNode> leafNodes = getSpellCheckableNodes(element);

        if (!leafNodes.isEmpty()) {
            int firstOffset = leafNodes.get(0).getStartOffset();
            int lastOffset = firstOffset;
            int iMax = leafNodes.size();
            int skippedChars = 0;
            boolean newSpellCheckingSpan = true;
            for (ASTNode astNode : leafNodes) {
                IElementType elementType = astNode.getElementType();
                int textLength = astNode.getTextLength();

                // INLINE_NON_PLAIN_TEXT are inline markers
                if (elementType == MdTypes.COMMENT_TEXT || !INLINE_NON_PLAIN_TEXT.contains(elementType)) {
                    if (isTypeOf(elementType, NO_SPELL_CHECK_SET)) {
                        // not spell checked. put spaces for these
                        text.append(RepeatedSequence.repeatOf(' ', textLength + skippedChars));
                        lastOffset += skippedChars;
                        skippedChars = 0;
                        newSpellCheckingSpan = true;
                    } else {
                        CharSequence nodeChars = astNode.getChars();
                        if (!newSpellCheckingSpan && nodeChars.length() > 0) {
                            char c = nodeChars.charAt(0);
                            if (c == ' ' || c == '\t' || c == '\n') {
                                newSpellCheckingSpan = true;
                            }
                        }
                        if (newSpellCheckingSpan && skippedChars > 0) {
                            // can insert skipped characters before our run
                            text.append(RepeatedSequence.repeatOf(' ', skippedChars));
                            lastOffset += skippedChars;
                            skippedChars = 0;
                        }
                        text.append(nodeChars);
                        newSpellCheckingSpan = false;
                        if (nodeChars.length() > 0) {
                            char c = nodeChars.charAt(nodeChars.length() - 1);
                            if (c == ' ' || c == '\t' || c == '\n') {
                                newSpellCheckingSpan = true;
                            }
                        }
                    }
                    lastOffset += textLength;
                } else {
                    skippedChars += textLength;
                }
            }

            consumer.consumeToken(element, text.toString(), useRename(), firstOffset - element.getNode().getStartOffset(), TextRange.create(0, lastOffset - firstOffset), TextSplitter.getInstance());
        }
    }
}
