// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.editorLayout;

import com.vladsch.md.nav.editor.split.SplitFileEditor;

public class ModifiedHtmlTextChangeAction extends BaseChangePreviewAction {
    protected ModifiedHtmlTextChangeAction() {
        super(SplitFileEditor.SplitEditorPreviewType.MODIFIED_HTML);
    }
}
