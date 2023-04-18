// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.ext.attributes.AttributeValueQuotes;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum AttributeValueQuotesType implements ComboBoxAdaptable<AttributeValueQuotesType> {
    AS_IS(0, CodeStyleBundle.message("attribute-value.quotes.as-is"), AttributeValueQuotes.AS_IS),
    NO_QUOTES_SINGLE_PREFERRED(1, CodeStyleBundle.message("attribute-value.quotes.no-quotes-single-preferred"), AttributeValueQuotes.NO_QUOTES_SINGLE_PREFERRED),
    NO_QUOTES_DOUBLE_PREFERRED(2, CodeStyleBundle.message("attribute-value.quotes.no-quotes-double-preferred"), AttributeValueQuotes.NO_QUOTES_DOUBLE_PREFERRED),
    SINGLE_PREFERRED(3, CodeStyleBundle.message("attribute-value.quotes.single-preferred"), AttributeValueQuotes.SINGLE_PREFERRED),
    DOUBLE_PREFERRED(4, CodeStyleBundle.message("attribute-value.quotes.double-preferred"), AttributeValueQuotes.DOUBLE_PREFERRED),
    SINGLE_QUOTES(5, CodeStyleBundle.message("attribute-value.quotes.single-quotes"), AttributeValueQuotes.SINGLE_QUOTES),
    DOUBLE_QUOTES(6, CodeStyleBundle.message("attribute-value.quotes.double-quotes"), AttributeValueQuotes.DOUBLE_QUOTES),
    ;

    public final @NotNull String displayName;
    public final int intValue;
    public final AttributeValueQuotes flexMarkEnum;

    AttributeValueQuotesType(int intValue, @NotNull String displayName, @NotNull AttributeValueQuotes flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<AttributeValueQuotesType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    public String quotesFor(String text, final String defaultQuotes) {
        return flexMarkEnum.quotesFor(text, defaultQuotes);
    }

    @NotNull
    @Override
    public ComboBoxAdapter<AttributeValueQuotesType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public AttributeValueQuotesType[] getValues() { return values(); }
}
