// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.test.util.ExampleOption;
import com.vladsch.flexmark.test.util.TestUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class FlexmarkOptionInfo {
    final public @NotNull ExampleOption exampleOption;
    final public Icon icon;
    final public IElementType elementType;
    final public int index;

    public FlexmarkOptionInfo(@NotNull ExampleOption exampleOption, Icon icon, IElementType elementType, int index) {
        this.exampleOption = exampleOption;
        this.icon = icon;
        this.elementType = elementType;
        this.index = index;
    }

    @NotNull
    public ExampleOption getExampleOption() {
        return exampleOption;
    }

    public Icon getIcon() {
        return icon;
    }

    public IElementType getElementType() {
        return elementType;
    }

    public int getIndex() {
        return index;
    }

    public TextRange getOptionNameRange() {
        ExampleOption exampleOption = ExampleOption.of(getOptionText());
        return new TextRange(exampleOption.optionName.getStartOffset(), exampleOption.optionName.getEndOffset());
    }

    public String getOptionText(@NotNull String optionName) {
        String text = optionName;

        String customParams = getCustomParams();
        if (customParams != null) {
            text = text + "[" + customParams + "]";
        }

        if (isDisabled()) {
            text = TestUtils.DISABLED_OPTION_PREFIX + text;
        }

        return text;
    }

    @NotNull
    public String getOptionText() {return getExampleOption().getOptionText();}

    @NotNull
    public String getOptionName() {return exampleOption.getOptionName();}

    @Nullable
    public String getCustomParams() {return exampleOption.getCustomParams();}

    public boolean isBuiltIn() {return exampleOption.isBuiltIn();}

    public boolean isDisabled() {return exampleOption.isDisabled();}

    public boolean isCustom() {return exampleOption.isCustom();}

    public boolean isValid() {return exampleOption.isValid();}

    public boolean isIgnore() {return exampleOption.isIgnore();}

    public boolean isFail() {return exampleOption.isFail();}

    public boolean isTimed() {return exampleOption.isTimed();}

    public boolean isTimedIterations() {return exampleOption.isTimedIterations();}

    public boolean isEmbedTimed() {return exampleOption.isEmbedTimed();}

    public boolean isFileEol() {return exampleOption.isFileEol();}

    public boolean isNoFileEol() {return exampleOption.isNoFileEol();}
}
