// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.codeInspection.ui.OptionAccessor;
import com.intellij.psi.PsiFile;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.plugin.util.TestUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public abstract class LocalInspectionToolBase extends LocalInspectionTool implements OptionAccessor {
    final static private String IGNORE_FENCED_CODE_CONTENT = "ignoreFencedCodeContent";

    public boolean ignoreFencedCodeContent = true;

    final public boolean isIgnoreFencedCodeContent() {
        return ignoreFencedCodeContent;
    }

    final public boolean isIgnoreFencedCodeContent(@NotNull PsiFile file) {
        return ignoreFencedCodeContent && MdPsiImplUtil.isInjectedInFencedCode(file);
    }

    protected void getTestOptions(@NotNull PsiFile file) {
        // options will be flipped from defaults
        String[] options = file.getUserData(TestUtils.TEST_INTENTION_OPTIONS);

        if (options != null) {
            for (String option : options) {
                boolean value = getOption(option);
                setOption(option, !value);
            }
        }
    }

    @Override
    public boolean getOption(String optionName) {
        switch (optionName) {
            case IGNORE_FENCED_CODE_CONTENT:
                return ignoreFencedCodeContent;
        }
        return false;
    }

    @Override
    public void setOption(String optionName, boolean optionValue) {
        switch (optionName) {
            case IGNORE_FENCED_CODE_CONTENT:
                ignoreFencedCodeContent = optionValue;
                break;
        }
    }

    @NotNull
    public JComponent createdOptionsPanel(@NotNull MultipleCheckboxOptionsPanel optionsPanel) {
        return optionsPanel;
    }

    @NotNull
    @Override
    final public JComponent createOptionsPanel() {
        final MultipleCheckboxOptionsPanel optionsPanel = new MultipleCheckboxOptionsPanel((OptionAccessor) this);
        optionsPanel.addCheckbox(InspectionsBundle.message("inspection.ignore-fenced-code-content"), IGNORE_FENCED_CODE_CONTENT);
        return createdOptionsPanel(optionsPanel);
    }
}
