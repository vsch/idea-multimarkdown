// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling.util;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.vladsch.flexmark.util.misc.DelimitedBuilder;
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class DisabledConditionBuilder {
    public static final String ORIGINAL_TOOLTIP = "MARKDOWN_NAVIGATOR_ORIGINAL_TOOLTIP";

    private final AnActionEvent myEvent;
    private final Presentation myPresentation;
    private final AnAction myAction;
    private final String myPlace;
    private final String myOriginalText;
    private final DelimitedBuilder myBuilder;
    private boolean myEnabled;
    private boolean mySelected;
    private boolean myMenuAction;

    public DisabledConditionBuilder(AnActionEvent event, AnAction action) {
        this(event, action, !event.getPlace().equals(ActionPlaces.EDITOR_TOOLBAR));
    }

    public boolean isMenuAction() {
        return myMenuAction;
    }

    private DisabledConditionBuilder(AnActionEvent event, AnAction action, boolean inMenuAction) {
        myEvent = event;
        myPresentation = myEvent.getPresentation();
        myMenuAction = inMenuAction || !event.isFromActionToolbar();

        String originalTooltip = (String) myPresentation.getClientProperty(ORIGINAL_TOOLTIP);
        if (originalTooltip == null) {
            originalTooltip = myPresentation.getText();
            myPresentation.putClientProperty(ORIGINAL_TOOLTIP, originalTooltip != null ? originalTooltip : "");
        }

        myAction = action;
        myPlace = event.getPlace();
        myOriginalText = originalTooltip;
        myBuilder = new DelimitedBuilder(myMenuAction ? ", " : "");
        myEnabled = true;
        mySelected = false;

        myBuilder.append(myOriginalText);
        myBuilder.append(myMenuAction ? " (" : ", disabled by:<ul style='margin:0 0 0 20'>");
        if (!myMenuAction) myBuilder.mark();
    }

    public DisabledConditionBuilder notNull(Object object, CharSequence elementName) {
        return notNull(object, elementName, false, null);
    }

    public DisabledConditionBuilder notNull(Object object, CharSequence elementName, final boolean fullMessage, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        if (object == null) {
            myEnabled = false;
            if (!myMenuAction) myBuilder.append("<li style='margin-top:0;margin-bottom:0'>");
            if (fullMessage) {
                myBuilder.append(elementName);
            } else {
                myBuilder.append(elementName).append(" is null");
            }
            if (!myMenuAction) myBuilder.append("</li>");
            myBuilder.mark();
        } else {
            if (runnable != null) {
                runnable.accept(this);
            }
        }
        return this;
    }

    public boolean isSelected() {
        return mySelected;
    }

    public void setSelected(final boolean selected) {
        mySelected = selected;
    }

    public DisabledConditionBuilder menuAction() {
        myMenuAction = true;
        return this;
    }

    public DisabledConditionBuilder notNull(Project object) {
        return notNull(object, (Consumer<? super DisabledConditionBuilder>) null);
    }

    public DisabledConditionBuilder notNull(Project object, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        return notNull(object, "Project", false, runnable);
    }

    public DisabledConditionBuilder notNull(Editor object) {
        return notNull(object, (Consumer<? super DisabledConditionBuilder>) null);
    }

    public DisabledConditionBuilder notNull(Editor object, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        return notNull(object, "Editor is not showing", true, runnable);
    }

    public DisabledConditionBuilder notNull(FileEditor object) {
        return notNull(object, (Consumer<? super DisabledConditionBuilder>) null);
    }

    public DisabledConditionBuilder notNull(FileEditor object, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        return notNull(object, "FileEditor is not showing", true, runnable);
    }

    public DisabledConditionBuilder notNull(PsiFile object) {
        return notNull(object, (Consumer<? super DisabledConditionBuilder>) null);
    }

    public DisabledConditionBuilder notNull(PsiFile object, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        return notNull(object, "PsiFile", false, runnable);
    }

    public DisabledConditionBuilder notNull(VirtualFile object) {
        return notNull(object, (Consumer<? super DisabledConditionBuilder>) null);
    }

    public DisabledConditionBuilder notNull(VirtualFile object, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        return notNull(object, "File", false, runnable);
    }

    public DisabledConditionBuilder notNull(CaretContextInfo object) {
        return notNull(object, (Consumer<? super DisabledConditionBuilder>) null);
    }

    public DisabledConditionBuilder notNull(CaretContextInfo object, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        return and(object != null, "caret context not found, editor is not showing", runnable);
    }

    public DisabledConditionBuilder isValid(PsiFile object) {
        return isValid(object, null);
    }

    public DisabledConditionBuilder isValid(PsiFile object, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        notNull(object, "PsiFile");
        if (object != null) {
            boolean valid = object.isValid();
            and(valid, "PsiFile is not valid");
            and(runnable);
        }
        return this;
    }

    public DisabledConditionBuilder isValid(VirtualFile object) {
        return isValid(object, null);
    }

    public DisabledConditionBuilder isValid(VirtualFile object, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        notNull(object, "VirtualFile");
        if (object != null) {
            boolean valid = object.isValid();
            and(valid, "VirtualFile is not valid");
            and(runnable);
        }
        return this;
    }

    public DisabledConditionBuilder and(boolean predicate, CharSequence... disabledText) {
        if (!predicate) {
            myEnabled = false;
            for (CharSequence text : disabledText) {
                if (!myMenuAction) myBuilder.append("<li style='margin-top:0;margin-bottom:0'>");
                myBuilder.append(text);
                if (!myMenuAction) myBuilder.append("</li>");
                myBuilder.mark();
            }
        }
        return this;
    }

    public DisabledConditionBuilder and(boolean predicate, CharSequence disabledText, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        if (!predicate) {
            myEnabled = false;
            if (!myMenuAction) myBuilder.append("<li style='margin-top:0;margin-bottom:0'>");
            myBuilder.append(disabledText);
            if (!myMenuAction) myBuilder.append("</li>");
            myBuilder.mark();
        } else if (runnable != null) {
            runnable.accept(this);
        }
        return this;
    }

    public DisabledConditionBuilder and(@Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        if (myEnabled && runnable != null) {
            runnable.accept(this);
        }
        return this;
    }

    public DisabledConditionBuilder isVisible(@Nullable Editor editor) {
        return and(editor != null && editor.getComponent().isVisible(), "Editor is not showing");
    }

    public DisabledConditionBuilder andSingleCaret(@Nullable final Editor editor) {
        return andSingleCaret(editor, null);
    }

    public DisabledConditionBuilder andSingleCaret(@Nullable final Editor editor, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        if (editor != null) {
            boolean predicate = editor.getCaretModel().getCaretCount() == 1;
            and(predicate, "Multiple carets are not supported", runnable);
        }
        return this;
    }

    public DisabledConditionBuilder andNoSelection(@Nullable final Editor editor) {
        return andNoSelection(editor, null);
    }

    public DisabledConditionBuilder andNoSelection(@Nullable final Editor editor, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        if (editor != null) {
            boolean predicate = !editor.getSelectionModel().hasSelection();
            and(predicate, "Selection(s) are not supported", runnable);
        }
        return this;
    }

    public DisabledConditionBuilder andSingleCaretOrNoSelection(@Nullable final Editor editor) {
        return andSingleCaretOrNoSelection(editor, null);
    }

    public DisabledConditionBuilder andSingleCaretOrNoSelection(@Nullable final Editor editor, @Nullable Consumer<? super DisabledConditionBuilder> runnable) {
        if (editor != null) {
            boolean predicate = !editor.getSelectionModel().hasSelection() || editor.getCaretModel().getCaretCount() == 1;
            and(predicate, "Selections with multiple carets are not supported", runnable);
        }
        return this;
    }

    public DisabledConditionBuilder enabledAnd(boolean predicate, CharSequence disabledText) {
        if (myEnabled) {
            and(predicate, disabledText);
        }
        return this;
    }

    public boolean isEnabled() {
        return myEnabled;
    }

    public DisabledConditionBuilder done() {
        return done(null, false);
    }

    public DisabledConditionBuilder done(boolean keepVisible) {
        return done(keepVisible, false);
    }

    public DisabledConditionBuilder doneToggleable() {
        return done(null, true);
    }

    public void doneToggleable(boolean keepVisible) {
        done(keepVisible, true);
    }

    public DisabledConditionBuilder done(Boolean keepVisible, boolean toggleable) {
        boolean showDisabledText = !myAction.displayTextInToolbar();
        if (myEnabled) {
            myPresentation.setText(myOriginalText);
            myPresentation.setEnabled(true);
            myPresentation.setVisible(true);
        } else {
            myBuilder.unmark().append(myMenuAction ? ")" : "</ul>");
            if (!myMenuAction) myBuilder.mark();

            if (showDisabledText) {
                myPresentation.setText(myBuilder.toString(), false);
            } else {
                myPresentation.setText(myOriginalText);
            }
            myPresentation.setEnabled(false);
            myPresentation.setVisible(keepVisible == null ? !MdApplicationSettings.getInstance().getDocumentSettings().getHideDisabledButtons() : keepVisible);
        }

        if (toggleable) myPresentation.putClientProperty(Toggleable.SELECTED_PROPERTY, mySelected);
        return this;
    }

    @Override
    public String toString() { return myBuilder.toString(); }

    public String getAndClear() { return myBuilder.getAndClear(); }

    public DisabledConditionBuilder clear() {
        myBuilder.clear();
        return this;
    }

    public String toStringOrNull() { return myBuilder.toStringOrNull(); }

    public DisabledConditionBuilder mark() {
        myBuilder.mark();
        return this;
    }

    public DisabledConditionBuilder unmark() {
        myBuilder.unmark();
        return this;
    }

    public DisabledConditionBuilder push() {
        myBuilder.push();
        return this;
    }

    public DisabledConditionBuilder push(final String delimiter) {
        myBuilder.push(delimiter);
        return this;
    }

    public DisabledConditionBuilder pop() {
        myBuilder.pop();
        return this;
    }

    public DisabledConditionBuilder append(final char v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final int v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final boolean v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final long v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final float v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final double v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final String v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final String v, final int start, final int end) {
        myBuilder.append(v, start, end);
        return this;
    }

    public DisabledConditionBuilder append(final CharSequence v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final CharSequence v, final int start, final int end) {
        myBuilder.append(v, start, end);
        return this;
    }

    public DisabledConditionBuilder append(final char[] v) {
        myBuilder.append(v);
        return this;
    }

    public DisabledConditionBuilder append(final char[] v, final int start, final int end) {
        myBuilder.append(v, start, end);
        return this;
    }

    public <V> DisabledConditionBuilder appendAll(final V[] v) {
        myBuilder.appendAll(v);
        return this;
    }

    public <V> DisabledConditionBuilder appendAll(final V[] v, final int start, final int end) {
        myBuilder.appendAll(v, start, end);
        return this;
    }

    public <V> DisabledConditionBuilder appendAll(final String delimiter, final V[] v) {
        myBuilder.appendAll(delimiter, v);
        return this;
    }

    public <V> DisabledConditionBuilder appendAll(final String delimiter, final V[] v, final int start, final int end) {
        myBuilder.appendAll(delimiter, v, start, end);
        return this;
    }

    public <V> DisabledConditionBuilder appendAll(final List<? extends V> v) {
        myBuilder.appendAll(v);
        return this;
    }

    public <V> DisabledConditionBuilder appendAll(final List<? extends V> v, final int start, final int end) {
        myBuilder.appendAll(v, start, end);
        return this;
    }

    public <V> DisabledConditionBuilder appendAll(final String delimiter, final List<? extends V> v) {
        myBuilder.appendAll(delimiter, v);
        return this;
    }

    public <V> DisabledConditionBuilder appendAll(
            final String delimiter,
            final List<? extends V> v,
            final int start,
            final int end
    ) {
        myBuilder.appendAll(delimiter, v, start, end);
        return this;
    }
}
