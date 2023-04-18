// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

public class TextMapMatch {
    final public static TextMapMatch NULL = new TextMapMatch(0, 0, "", 0, 0, "");

    final private int myMatchStart;
    final private int myMatchEnd;
    final private String myMatchedText;
    final private int myReplacedStart; // in match text index coordinates
    final private int myReplacedEnd; // in match text index coordinates
    final private String myReplacedText;

    public TextMapMatch(int matchStart, int matchEnd, String matchedText, int replacedStart, int replacedEnd, String replacedText) {
        myMatchStart = matchStart;
        myMatchEnd = matchEnd;
        myMatchedText = matchedText;
        myReplacedStart = replacedStart;
        myReplacedEnd = replacedEnd;
        myReplacedText = replacedText;
    }

    public int getReplacedStart() {
        return myReplacedStart;
    }

    public int getReplacedEnd() {
        return myReplacedEnd;
    }

    public int getMatchStart() {
        return myMatchStart;
    }

    public int getMatchEnd() {
        return myMatchEnd;
    }

    public String getMatchedText() {
        return myMatchedText;
    }

    public String getReplacedText() {
        return myReplacedText;
    }
}
