// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.inspections;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.vladsch.md.nav.annotator.MdBaseIntentionAction;

import java.util.ArrayList;

public class ProblemDescriptors {
    public static final ProblemDescriptor[] EMPTY_DESCRIPTORS = new ProblemDescriptor[0];
    private final ArrayList<ProblemDescriptor> problems = new ArrayList<>(10);
    private final LocalQuickFixes fixes = new LocalQuickFixes();

    public void add(MdBaseIntentionAction intentionAction) {
        fixes.add(LocalQuickFixIntentionActionAdapter.of(intentionAction));
    }

    /**
     * @param intentionAction action for fix
     *
     * @deprecated (works but only if an editor is open for the file)
     */
    @Deprecated
    public void add(BaseIntentionAction intentionAction) {
        fixes.add(LocalQuickFixIntentionActionAdapter.of(intentionAction));
    }

    public void add(LocalQuickFix quickFix) {
        fixes.add(quickFix);
    }

    public void add(ProblemDescriptor problem) {
        problems.add(problem);
    }

    public LocalQuickFix[] fixes() {
        return fixes.done();
    }

    public ProblemDescriptor[] done() {
        if (problems.isEmpty()) return null;

        ProblemDescriptor[] descriptors = problems.toArray(EMPTY_DESCRIPTORS);
        problems.clear();
        return descriptors;
    }

    public static class LocalQuickFixes {
        final ArrayList<LocalQuickFix> myQuickFixes = new ArrayList<>(10);

        public void add(MdBaseIntentionAction intentionAction) {
            myQuickFixes.add(LocalQuickFixIntentionActionAdapter.of(intentionAction));
        }

        public void add(LocalQuickFix quickFix) {
            myQuickFixes.add(quickFix);
        }

        public LocalQuickFix[] done() {
            final LocalQuickFix[] fixes = myQuickFixes.toArray(new LocalQuickFix[0]);
            myQuickFixes.clear();
            return fixes;
        }
    }
}
