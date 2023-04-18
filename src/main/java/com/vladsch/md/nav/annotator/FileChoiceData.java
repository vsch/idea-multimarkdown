// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.annotator;

import com.intellij.openapi.vfs.VirtualFile;

public class FileChoiceData {
    public static FileChoiceData NULL = new FileChoiceData(null, "", "", new AnchorData[0]);

    public AnchorData getAnchorData(final String anchor) {
        for (AnchorData data : anchors) {
            if (data.id.equals(anchor)) {
                return data;
            }
        }
        return AnchorData.NULL;
    }

    public static class AnchorData {
        public static AnchorData NULL = new AnchorData("", 0, 0);

        public final String id;
        public final int textOffset;
        public final int testLength;

        public boolean isNull() {
            return this == NULL;
        }

        public boolean isEmpty() {
            return id.isEmpty();
        }

        public AnchorData(final String id, final int textOffset, final int testLength) {
            this.id = id;
            this.textOffset = textOffset;
            this.testLength = testLength;
        }
    }

    public final VirtualFile virtualFile;
    public final String displayText;
    public final String linkText;
    public final AnchorData[] anchors;

    public boolean isNull() {
        return this == NULL;
    }

    public boolean isEmpty() {
        return virtualFile == null;
    }

    public FileChoiceData(final VirtualFile virtualFile, final String displayText, final String linkText, AnchorData[] anchors) {
        this.virtualFile = virtualFile;
        this.displayText = displayText;
        this.linkText = linkText;
        this.anchors = anchors;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
