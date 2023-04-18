// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.transaction;

import com.vladsch.flexmark.util.sequence.RepeatedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LogIndenter {
    @NotNull
    String getLogIndent();

    @NotNull
    String getId(boolean wantReal);

    default long getTimestamp(boolean wantReal) {
        return System.currentTimeMillis();
    }

    @NotNull
    default String indentMessage(@Nullable Object detail, boolean appendEol, boolean wantReal) {
        return indentMessage(getTimestamp(wantReal), getId(wantReal), getLogIndent(), detail, appendEol);
    }

    LogIndenter NULL = new LogIndenter() {
        @NotNull
        @Override
        public String getId(boolean wantReal) {
            return " --";
        }

        @NotNull
        @Override
        public String getLogIndent() {
            return "";
        }
    };

    @NotNull
    static String indentMessage(long timeStamp, @NotNull String id, @NotNull String logIndent, @Nullable Object detail, boolean appendEol) {
        String s = String.format("%04d:", timeStamp % 10000L) + id + "|";

        String lineIndent = logIndent + RepeatedSequence.ofSpaces(s.length()).toString();
        String message = String.valueOf(detail);
        String indented;
        if (message.endsWith("\n")) {
            indented = s + logIndent + message.substring(0, message.length() - 1).replace("\n", "\n" + lineIndent) + "\n";
        } else {
            indented = s + logIndent + message.replace("\n", "\n" + lineIndent) + (appendEol ? "\n" : "");
        }
        return indented;
    }
}
