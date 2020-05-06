// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.editor;

import java.io.File;

public interface FileGenerator<T> {
    void generateFile(final CharSequence content, final File file, final T param);

    String cacheSignatureId();
}
