// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util;

public enum PostEditAdjust {
    NEVER,
    ALWAYS,
    IF_INDENT_CHAR,         // adjust if char is indent type char
    IF_NON_INDENT_CHAR,     // adjust if char is non-indent type char
    IF_WHITESPACE_CHAR,         // adjust if char is space or tab
    IF_NON_WHITESPACE_CHAR,     // adjust if char is not space or tab
}
