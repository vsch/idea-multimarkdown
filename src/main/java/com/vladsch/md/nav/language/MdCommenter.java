// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

public class MdCommenter implements Commenter {
    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return "<!--";
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return "-->";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return "<!--";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return "-->";
    }
}
