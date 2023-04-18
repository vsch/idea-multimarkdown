// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util;

public interface MdCompletionCharFiltering {
    boolean isAddToPrefixChar(char c);

    boolean isSelectAndFinishChar(char c, boolean isAutoPopup);

    MdCompletionCharFiltering ALL_ADD_PREFIX = new MdCompletionCharFiltering() {
        @Override
        public boolean isAddToPrefixChar(final char c) {
            return true;
        }

        @Override
        public boolean isSelectAndFinishChar(final char c, final boolean isAutoPopup) {
            return false;
        }
    };
}
