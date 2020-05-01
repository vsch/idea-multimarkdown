// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.SoftWrapModel;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.VisibleAreaEvent;
import com.intellij.openapi.editor.event.VisibleAreaListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.impl.SoftWrapModelImpl;
import com.intellij.openapi.editor.impl.softwrap.CompositeSoftWrapPainter;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapDrawingType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Alarm;
import com.intellij.util.messages.MessageBusConnection;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.editor.api.MdColumnVisibleAreaWidthProvider;
import com.vladsch.md.nav.editor.api.MdEditorCustomizationProvider;
import com.vladsch.md.nav.editor.api.MdEditorRangeHighlighter;
import com.vladsch.md.nav.editor.split.SplitFileEditor;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.SettingsChangedListener;
import com.vladsch.md.nav.settings.SoftWrapType;
import com.vladsch.md.nav.util.MiscUtils;
import com.vladsch.plugin.util.HelpersKt;
import com.vladsch.plugin.util.TimeIt;
import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.ArrayList;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class MdSplitEditor extends SplitFileEditor<TextEditor, MdPreviewFileEditor> implements TextEditor {
    private static final Logger LOG = getInstance("com.vladsch.md.nav.editor.split.summary");
    private static final Logger LOG_DETAILS = getInstance("com.vladsch.md.nav.editor.split");

    private final Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);
    private final MdColumnVisibleAreaWidthProvider myAreaWidthProvider;

    private final ArrayList<MdEditorRangeHighlighter> myRangeHighlighters = new ArrayList<>();

    public MdSplitEditor(@NotNull TextEditor mainEditor, @NotNull MdPreviewFileEditor secondEditor) {
        super(mainEditor, secondEditor);

        // add our own width provider
        MdApplicationSettings settings = MdApplicationSettings.getInstance();
        EditorEx editor = (EditorEx) mainEditor.getEditor();
        SoftWrapModel softWrapModel = editor.getSoftWrapModel();

        if (softWrapModel instanceof SoftWrapModelImpl && editor instanceof EditorImpl) {
            myAreaWidthProvider = MiscUtils.firstNonNullResult(MdEditorCustomizationProvider.EXTENSIONS.getValue(), (extension) -> extension.getColumnVisibleAreaWidthProvider(editor));
            if (myAreaWidthProvider != null) ((SoftWrapModelImpl) softWrapModel).getApplianceManager().setWidthProvider(myAreaWidthProvider);
        } else {
            myAreaWidthProvider = null;
        }

        myMainEditor.putUserData(PARENT_SPLIT_KEY, this);
        mySecondEditor.putUserData(PARENT_SPLIT_KEY, this);

        editor.getCaretModel().addCaretListener(new MyCaretListener(this));
        editor.getScrollingModel().addVisibleAreaListener(new MyVisibleAreaListener(this));
        editor.getDocument().addDocumentListener(new MyDocumentChangeListener(this), this);

        MiscUtils.forNonNullResult(MdEditorCustomizationProvider.EXTENSIONS.getValue(), extension -> extension.getEditorRangeHighlighter(editor), myRangeHighlighters::add);

        MessageBusConnection messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect(this);
        messageBusConnection.subscribe(SettingsChangedListener.TOPIC, appSettings -> updateSoftWraps());

        updateSoftWraps();

        if (editor instanceof EditorImpl) {
            MiscUtils.forEach(MdEditorCustomizationProvider.EXTENSIONS.getValue(), (extension) -> extension.customizeMarkdownEditor(editor));
        }
    }

    @Override
    public void dispose() {
        for (MdEditorRangeHighlighter rangeHighlighter : myRangeHighlighters) {
            rangeHighlighter.removeRangeHighlighters();
        }

        if (myAreaWidthProvider != null) {
            Disposer.dispose(myAreaWidthProvider);
        }

        super.dispose();
    }

    void updateSoftWraps() {
        TimeIt.logTime(LOG, "updateSoftWraps", () -> {
            if (myAreaWidthProvider != null) {
                CompositeSoftWrapPainter myPainter = new CompositeSoftWrapPainter((EditorEx) myMainEditor.getEditor());
                int minDrawingWidth = myPainter.getMinDrawingWidth(SoftWrapDrawingType.BEFORE_SOFT_WRAP_LINE_FEED);
                myAreaWidthProvider.setMinDrawingWidth(minDrawingWidth);
                myPainter.reinit();

                int softWraps = myAreaWidthProvider.getSoftWraps();
                boolean haveSoftWraps = myMainEditor.getEditor().getSettings().isUseSoftWraps();
                if (softWraps == SoftWrapType.DISABLED.getIntValue() && haveSoftWraps) {
                    setSoftWraps(false);
                } else if (softWraps == SoftWrapType.ENABLED.getIntValue() && !haveSoftWraps) {
                    setSoftWraps(true);
                } else {
                    setSoftWraps(!haveSoftWraps);
                    setSoftWraps(haveSoftWraps);
                }
            }
            monitorDocument();
        });
    }

    void monitorDocument() {
        if (!myRangeHighlighters.isEmpty()) {
            if (myDocumentAlarm.isDisposed()) return;

            HelpersKt.debug(LOG_DETAILS, () -> "Schedule update highlighters in 100");
            myDocumentAlarm.cancelAllRequests();
            myDocumentAlarm.addRequest(this::updateHighlighters, 100);
        }
    }

    void updateHighlighters() {
        if (!myRangeHighlighters.isEmpty()) {
            TimeIt.logTime(LOG, "Updating highlighters", () -> {
                Editor editor = myMainEditor.getEditor();
                Project project = editor.getProject();
                MdFile file = null;

                if (project != null) {
                    final VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
                    if (virtualFile != null) {
                        final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                        if (psiFile instanceof MdFile) {
                            file = (MdFile) psiFile;
                        }
                    }
                }

                for (MdEditorRangeHighlighter rangeHighlighter : myRangeHighlighters) {
                    rangeHighlighter.removeRangeHighlighters();
                    if (file != null) rangeHighlighter.updateRangeHighlighters(file);
//                    else rangeHighlighter.removeRangeHighlighters();
                }
            });
        }

        final Editor editor = getMainEditor().getEditor();

        if (editor.getCaretModel().getCaretCount() == 1) {
            int offset = editor.getCaretModel().getOffset();
            syncScrollPosition(editor, offset, mySecondEditor);
        }
    }

    private void setSoftWraps(boolean state) {
        TimeIt.logTime(LOG, "setSoftWraps " + state, () -> {
            final Editor editor = getMainEditor().getEditor();

            Point point = editor.getScrollingModel().getVisibleArea().getLocation();
            LogicalPosition anchorPosition = editor.xyToLogicalPosition(point);
            int intraLineShift = point.y - editor.logicalPositionToXY(anchorPosition).y;

            editor.getSettings().setUseSoftWraps(state);

            if (editor instanceof EditorEx) {
                ((EditorEx) editor).reinitSettings();
            }

            editor.getScrollingModel().disableAnimation();
            editor.getScrollingModel().scrollVertically(editor.logicalPositionToXY(anchorPosition).y + intraLineShift);
            editor.getScrollingModel().enableAnimation();
        });
    }

    @NotNull
    @Override
    public String getName() {
        return MdBundle.message("multimarkdown.split-tab-name");
    }

    @NotNull
    @Override
    public Editor getEditor() {
        return getMainEditor().getEditor();
    }

    @Override
    public boolean canNavigateTo(@NotNull Navigatable navigatable) {
        return getMainEditor().canNavigateTo(navigatable);
    }

    @Override
    public void navigateTo(@NotNull Navigatable navigatable) {
        getMainEditor().navigateTo(navigatable);
    }

    public static void syncScrollPosition(
            final Editor editor,
            int offset,
            final MdPreviewFileEditor previewFileEditor
    ) {
        TimeIt.logTime(LOG, "syncScrollPosition", () -> {
            Document document = editor.getDocument();
            // need to find a non-blank and move offset at its position
            int useOffset = offset;
            if (useOffset > document.getTextLength()) {
                useOffset = document.getTextLength();
            }

            CharSequence sequence = document.getCharsSequence();
            int line = document.getLineNumber(useOffset);
            int startOffset;
            int endOffset;
            BasedSequence charSequence;
            int indent;
            int trailing;
            do {
                startOffset = document.getLineStartOffset(line);
                endOffset = document.getLineEndOffset(line);
                charSequence = BasedSequence.of(sequence.subSequence(startOffset, endOffset));
                indent = charSequence.countLeading(CharPredicate.SPACE_TAB);
                trailing = charSequence.countTrailing(CharPredicate.SPACE_TAB);
                if (startOffset + indent < endOffset - trailing) {
                    // not a blank line
                    if (useOffset > endOffset - trailing) {
                        useOffset = endOffset - trailing;
                    }
                    break;
                }
                line--;
            } while (line >= 0);

            if (line < 0) useOffset = 0;

            if (useOffset <= startOffset + indent && useOffset < endOffset - trailing) {
                // already good
            } else {
                int pos = useOffset - startOffset - 1;
                while (pos >= 0) {
                    char c = charSequence.charAt(pos);
                    if (!CharPredicate.SPACE_TAB_EOL.test(c)) {
                        useOffset = startOffset + pos;
                        break;
                    }
                    pos--;
                }
            }
            previewFileEditor.scrollToSrcOffset(useOffset, editor);
        });
    }

    private static class MyCaretListener implements CaretListener {
        final private @NotNull MdSplitEditor mySplitEditor;

        public MyCaretListener(@NotNull MdSplitEditor splitEditor) {
            mySplitEditor = splitEditor;
        }

        @Override
        public void caretAdded(@NotNull CaretEvent e) {
            mySplitEditor.monitorDocument();
        }

        @Override
        public void caretRemoved(@NotNull CaretEvent e) {
            mySplitEditor.monitorDocument();
        }

        @Override
        public void caretPositionChanged(@NotNull CaretEvent e) {
            mySplitEditor.monitorDocument();
        }
    }

    private static class MyVisibleAreaListener implements VisibleAreaListener {
        final private @NotNull MdSplitEditor mySplitEditor;

        public MyVisibleAreaListener(@NotNull MdSplitEditor splitEditor) {
            mySplitEditor = splitEditor;
        }

        @Override
        public void visibleAreaChanged(VisibleAreaEvent e) {
            mySplitEditor.monitorDocument();
        }
    }

    private static class MyDocumentChangeListener implements DocumentListener {
        final private @NotNull MdSplitEditor mySplitEditor;
        private boolean myInBulkUpdate;

        public MyDocumentChangeListener(@NotNull MdSplitEditor splitEditor) {
            mySplitEditor = splitEditor;
        }

        @Override
        public void beforeDocumentChange(@NotNull com.intellij.openapi.editor.event.DocumentEvent event) {

        }

        @Override
        public void documentChanged(@NotNull com.intellij.openapi.editor.event.DocumentEvent event) {
            if (!myInBulkUpdate) {
                mySplitEditor.monitorDocument();
            }
        }

        @Override
        public void bulkUpdateStarting(@NotNull Document document) {
            myInBulkUpdate = true;
        }

        @Override
        public void bulkUpdateFinished(@NotNull Document document) {
            myInBulkUpdate = false;
            mySplitEditor.monitorDocument();
        }
    }
}
