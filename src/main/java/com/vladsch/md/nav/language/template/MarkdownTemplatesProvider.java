// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;

// DEPRECATED: 2019-10-25
//    DefaultLiveTemplateEP
public class MarkdownTemplatesProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[] { "liveTemplates/Markdown", "liveTemplates/surround" };
    }

    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return null;
    }
}
