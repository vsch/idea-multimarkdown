// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.annotator;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.psi.element.MdNamedElement;

import java.util.List;

public interface FileDataCreator {
    void fillData(final MdNamedElement element, List<FileChoiceData> fileData);

    default boolean isAvailable(final MdNamedElement element) {
        return element.isValid();
    }

    String getDefaultAnchor();

    default String getFilePrompt() {
        return MdBundle.message("file-choice-with-preview.prompt-anchor.0.label", "%s");
    }
}
