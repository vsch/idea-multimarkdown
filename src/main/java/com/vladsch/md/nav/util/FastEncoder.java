// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import java.util.Random;

/**
 * Static class holding simple HTML encoding logic.
 */
public class FastEncoder {

    private FastEncoder() {}

    public static String encode(String string) {
        if (!string.isEmpty()) {
            for (int i = 0; i < string.length(); i++) {
                if (encode(string.charAt(i)) != null) {
                    // we have at least one character that needs encoding, so do it one by one
                    StringBuilder sb = new StringBuilder();
                    for (i = 0; i < string.length(); i++) {
                        char c = string.charAt(i);
                        String encoded = encode(c);
                        if (encoded != null) sb.append(encoded);
                        else sb.append(c);
                    }
                    return sb.toString();
                }
            }
            return string;
        } else return "";
    }

    public static void encode(String string, StringBuilder sb) {
        if (!string.isEmpty()) {
            for (int i = 0; i < string.length(); i++) {
                if (encode(string.charAt(i)) != null) {
                    // we have at least one character that needs encoding, so do it one by one
                    for (i = 0; i < string.length(); i++) {
                        char c = string.charAt(i);
                        String encoded = encode(c);
                        if (encoded != null) sb.append(encoded);
                        else sb.append(c);
                    }
                    return;
                }
            }
            sb.append(string);
        }
    }

    public static String encode(char c) {
        switch (c) {
            case '&':
                return "&amp;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case '\'':
                return "&#39;";
        }
        return null;
    }

    public static Random random = new Random(0x2626);

    public static String obfuscate(String email) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);
            switch (random.nextInt(5)) {
                case 0:
                case 1:
                    sb.append("&#").append((int) c).append(';');
                    break;
                case 2:
                case 3:
                    sb.append("&#x").append(Integer.toHexString(c)).append(';');
                    break;
                case 4:
                    String encoded = encode(c);
                    if (encoded != null) sb.append(encoded);
                    else sb.append(c);
            }
        }
        return sb.toString();
    }
}
