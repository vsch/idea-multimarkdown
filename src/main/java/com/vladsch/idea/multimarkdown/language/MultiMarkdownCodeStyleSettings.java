package com.vladsch.idea.multimarkdown.language;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class MultiMarkdownCodeStyleSettings extends CustomCodeStyleSettings {
    public MultiMarkdownCodeStyleSettings(CodeStyleSettings settings) {
        super("MultiMarkdownCodeStyleSettings", settings);
    }
}
