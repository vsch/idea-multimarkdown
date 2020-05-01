// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.MdFileType;

public class MdProblemFileHighlightFilter implements Condition<VirtualFile> {
    @Override
    public boolean value(VirtualFile virtualFile) {
        final FileType fileType = virtualFile.getFileType();
        return fileType == MdFileType.INSTANCE;
    }
}
