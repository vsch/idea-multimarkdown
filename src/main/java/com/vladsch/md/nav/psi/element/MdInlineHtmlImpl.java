// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.parser.MdFactoryContext;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.psi.util.MdTypes;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdInlineHtmlImpl extends ASTWrapperPsiElement implements MdInlineHtml {
    public MdInlineHtmlImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public ASTNode getContentElement() {
        return getNode();
    }

    @NotNull
    @Override
    public String getContent() {
        ASTNode content = getNode().findChildByType(MdTypes.INLINE_HTML);
        return content != null ? content.getText() : "";
    }

    @NotNull
    @Override
    public PsiElement setContent(@NotNull String blockText) {
        return MdPsiImplUtil.setContent(this, blockText);
    }

    @NotNull
    @Override
    public TextRange getContentRange(boolean inDocument) {
        return inDocument ? getTextRange() : new TextRange(0, getTextLength());
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                if (!isValid()) return null;
                return "Inline HTML";
            }

            @Nullable
            @Override
            public String getLocationString() {
                if (!isValid()) return null;
                // create a shortened version that is still good to look at
                return MdPsiImplUtil.truncateStringForDisplay(getText(), 50, false, true, true);
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return MdIcons.getDocumentIcon();
            }
        };
    }

    @Override
    public boolean isValidHost() {
        return isValid();
    }

    @Override
    public MdPsiLanguageInjectionHost updateText(@NotNull String text) {
        return ElementManipulators.handleContentChange(this, text);
    }

    @NotNull
    @Override
    public LiteralTextEscaper<? extends MdPsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new LiteralTextEscaper<MdPsiLanguageInjectionHost>(this) {
            @Override
            public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
                outChars.append(rangeInsideHost.substring(myHost.getText()));
                return true;
            }

            @Override
            public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
                return rangeInsideHost.getStartOffset() + offsetInDecoded;
            }

            @NotNull
            @Override
            public TextRange getRelevantTextRange() {
                return getContentRange();
            }

            @Override
            public boolean isOneLine() {
                return true;
            }
        };
    }

    @NotNull
    public static String getElementText(@NotNull MdFactoryContext factoryContext, @NotNull String content) {
        return content;
    }
}
