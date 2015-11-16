/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vladsch.idea.multimarkdown.spellchecking;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.spellchecker.inspections.IdentifierSplitter;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class MultiMarkdownIdentifierTokenizer extends Tokenizer<MultiMarkdownNamedElement> {
    @Override
    public void tokenize(@NotNull MultiMarkdownNamedElement element, TokenConsumer consumer) {
        StringBuilder text = new StringBuilder(element.getTextLength());
        int offset = -1;
        int nodeOffset = -1;
        int firstOffset = -1;
        int lastOffset = -1;

        // extract a range of spell checkable elements, leave out any leading/trailing non-checkable
        // and any non-checkable in the middle replace with spaces
        for (ASTNode astNode : element.getNode().getChildren(null)) {
            if (!MultiMarkdownSpellcheckingStrategy.NO_SPELL_CHECK_SET.contains(astNode.getElementType())) {
                offset = astNode.getStartOffset();
                if (firstOffset < 0) firstOffset = offset;
                else if (lastOffset < offset) appendSpaces(text, offset - lastOffset);
                text.append(astNode.getChars());
                lastOffset = offset + astNode.getTextLength();
            }

            if (nodeOffset < 0) {
                nodeOffset = offset >= 0 ? offset : astNode.getStartOffset();
            }
        }

        if (nodeOffset < 0) {
            // leaf element, take all text, if it is not to be spell checked then should not be here
            String elemText = element.getText();
            consumer.consumeToken(element, elemText, true, 0, TextRange.allOf(elemText), IdentifierSplitter.getInstance());
        } else if (firstOffset >= 0) {
            consumer.consumeToken(element, text.toString(), true, firstOffset - element.getNode().getStartOffset(), TextRange.create(0, lastOffset - firstOffset), IdentifierSplitter.getInstance());
        }
    }

    public interface SpellCheckConsumer {
        void consume(String word, boolean spellCheck);
    }

    // used during spell check corrections to create variations that preserve non-spell checkable text
    public void tokenizeSpellingSuggestions(@NotNull MultiMarkdownNamedElement element, SpellCheckConsumer consumer) {
        // extract a range of spell checkable elements, leave out any leading/trailing non-checkable
        // and any non-checkable in the middle replace with spaces
        boolean hadChildren = false;
        for (ASTNode astNode : element.getNode().getChildren(null)) {
            hadChildren = true;
            consumer.consume(astNode.getText(), !MultiMarkdownSpellcheckingStrategy.NO_SPELL_CHECK_SET.contains(astNode.getElementType()));
        }

        if (!hadChildren) {
            // leaf element, take all text, if it is not to be spell checked then should not be here
            consumer.consume(element.getText(), true);
        }
    }

    private void appendSpaces(StringBuilder text, int i) {
        while (i-- > 0) {
            text.append(' ');
        }
    }
}
