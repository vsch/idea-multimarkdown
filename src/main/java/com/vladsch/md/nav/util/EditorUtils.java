// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.SelectionModel;

public class EditorUtils {
    public static void scrollToCaret(Editor editor) {
        EditorModificationUtil.scrollToCaret(editor);
    }

    public static void scrollToSelection(Editor editor) {
        ScrollingModel scrollingModel = editor.getScrollingModel();
        SelectionModel caretModel = editor.getSelectionModel();
        scrollingModel.scrollTo(editor.offsetToLogicalPosition(caretModel.getSelectionEnd()), ScrollType.MAKE_VISIBLE);
        scrollingModel.scrollTo(editor.offsetToLogicalPosition(caretModel.getSelectionStart()), ScrollType.MAKE_VISIBLE);
    }

    /**
     * Longest Common Prefix for a set of strings
     *
     * @param s array of strings or null
     *
     * @return longest common prefix
     */
    public static String getLongestCommonPrefix(String... s) {
        if (s == null || s.length == 0) return "";
        if (s.length == 1) return s[0];

        String s0 = s[0];
        int iMax = s0.length();
        int jMax = s.length;

        for (int j = 1; j < jMax; j++) {
            final String sj = s[j];
            if (iMax > sj.length()) iMax = sj.length();
        }

        for (int i = 0; i < iMax; i++) {
            char c = s0.charAt(i);
            for (int j = 1; j < jMax; j++) {
                if (s[j].charAt(i) != c) return s0.substring(0, i);
            }
        }
        return s0.substring(0, iMax);
    }

    public static String getAbbreviatedText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength || maxLength < 6) return text;

        int prefix = maxLength / 2;
        int suffix = maxLength - 3 - prefix;
        return text.substring(0, prefix) + " … " + text.substring(text.length() - suffix);
    }
}


