// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.text;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.CaretAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.actions.styling.util.MdActionUtil;
import com.vladsch.md.nav.editor.split.SplitFileEditor;
import icons.MdIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static javax.swing.SwingUtilities.isEventDispatchThread;

public class MdLineSelectionFakePsiElement extends PsiElementBase implements PsiNamedElement, ItemPresentation {
    public static final Pattern LINE_SELECTION_ANCHOR = Pattern.compile("L(\\d+)(?:-(?:L(\\d+)?)?)?");
    final PsiFile myElement;
    final int myStartLine;
    final int myEndLine;

    public MdLineSelectionFakePsiElement(final PsiFile element, final int startLine, final int endLine) {
        myElement = element;
        myStartLine = startLine;
        myEndLine = endLine;
    }

    @Override
    public boolean isPhysical() {
        return true;
    }

    @Override
    public PsiElement getParent() {
        return myElement;
    }

    public int getStartLine() {
        return myStartLine;
    }

    public int getEndLine() {
        return myEndLine;
    }

    /**
     * @return null to disable default navigation of the IDE and use our navigate
     */
    @Override
    public PsiFile getContainingFile() {
        return null; // myElement.getContainingFile(); //myElement;
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return this;
    }

    @Nullable
    @Override
    public String getText() {
        return myElement.getVirtualFile().getPath() + ":" + (myStartLine + 1) + (myEndLine > -1 ? "-" + (myEndLine + 1) : "");
    }

    @Override
    public int getTextOffset() {
        return getStartLineOffset(myElement, myStartLine);
    }

    public int getTargetLineCount() {
        if (myElement.isValid()) {
            final Editor editor = getEditor(myElement);
            if (editor != null) {
                return editor.getDocument().getLineCount();
            } else {
                return getLineCount(myElement.getOriginalFile());
            }
        }
        return 0;
    }

    @Override
    public void navigate(final boolean requestFocus) {
        if (myElement.isValid()) {
            myElement.navigate(true);
            final Editor editor = getEditor(myElement);
            if (editor != null) {
                final Document document = editor.getDocument();
                int startOffset = document.getLineStartOffset(myStartLine);
                if (startOffset >= 0) {
                    editor.getCaretModel().moveToOffset(startOffset);

                    if (myEndLine >= 0 && myEndLine >= myStartLine) {
                        int endOffset = myEndLine + 1 < document.getLineCount() ? document.getLineStartOffset(myEndLine + 1) : document.getTextLength();
                        if (endOffset > document.getTextLength()) {
                            endOffset = document.getTextLength();
                        }
                        editor.getSelectionModel().setSelection(startOffset, endOffset);
                    }

                    scrollToCaret(editor);
                }
            }
        }
    }

    @Nullable
    @Override
    public Icon getIcon(final boolean open) {
        return MdIcons.Element.ANCHOR;
    }

    @Nullable
    @Override
    public Icon getIcon(final int flags) {
        return MdIcons.Element.ANCHOR;
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    @NotNull
    public Language getLanguage() {
        return Language.ANY;
    }

    @Override
    @NotNull
    public PsiElement[] getChildren() {
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    @Nullable
    public PsiElement getFirstChild() {
        return null;
    }

    @Override
    @Nullable
    public PsiElement getLastChild() {
        return null;
    }

    @Override
    @Nullable
    public PsiElement getNextSibling() {
        return null;
    }

    @Override
    @Nullable
    public PsiElement getPrevSibling() {
        return null;
    }

    @Override
    @Nullable
    public TextRange getTextRange() {
        return null;
    }

    @Override
    public int getStartOffsetInParent() {
        return 0;
    }

    @Override
    public int getTextLength() {
        return 0;
    }

    @Override
    @Nullable
    public PsiElement findElementAt(int offset) {
        return null;
    }

    @Override
    @NotNull
    public char[] textToCharArray() {
        return new char[0];
    }

    @Override
    public boolean textContains(char c) {
        return false;
    }

    @Override
    @Nullable
    public ASTNode getNode() {
        return null;
    }

    @Override
    public String getPresentableText() {
        return getName();
    }

    @Override
    protected final Icon getElementIcon(final int flags) {
        return super.getElementIcon(flags);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiManager getManager() {
        final PsiElement parent = getParent();
        return parent != null ? parent.getManager() : null;
    }

    @Nullable
    public static MdLineSelectionFakePsiElement getLineSelectionElement(final PsiFile resolved, final String name) {
        MdLineSelectionFakePsiElement result = null;
        final Matcher matcher = MdLineSelectionFakePsiElement.LINE_SELECTION_ANCHOR.matcher(name);

        if (matcher.matches()) {
            String startLine = matcher.group(1);
            String endLine = matcher.group(2);

            int startLineNum = Integer.parseInt(startLine) - 1;
//            int startLineOffset = MdLineSelectionFakePsiElement.getStartLineOffset(resolved, startLineNum);
//            if (startLineOffset >= 0) {
            int endLineNum = endLine == null || endLine.isEmpty() ? -1 : Integer.parseInt(endLine) - 1;

            result = new MdLineSelectionFakePsiElement(resolved, startLineNum, endLineNum);
//            }
        }
        return result;
    }

    public static int getLineCount(final PsiFile file) {
        if (file.isValid()) {
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile != null) {
                final Editor editor = getEditor(file.getProject(), virtualFile);
                if (editor != null) {
                    return editor.getDocument().getLineCount();
                } else {
                    // count EOLs
                    try {
                        final byte[] bytes = virtualFile.contentsToByteArray();
                        String text = new String(bytes, virtualFile.getCharset());
                        int iMax = text.length();
                        int lastPos = 0;
                        int lineCount = 0;
                        while (lastPos < iMax) {
                            int pos = text.indexOf('\n', lastPos);
                            if (pos == -1) break;
                            lineCount++;
                            lastPos = pos + 1;
                        }

                        return lineCount;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return -1;
    }

    public static int getStartLineOffset(final PsiFile file, final int startLine) {
        if (file.isValid()) {
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile != null) {
                if (startLine == 0) return 0;

                final Editor textEditor = getEditor(file);
                if (textEditor != null) {
                    final Document document = textEditor.getDocument();
                    if (startLine >= 0 && startLine < document.getLineCount()) {
                        int startOffset = document.getLineStartOffset(startLine);
                        return startOffset;
                    }
                } else {
                    // count EOLs
                    try {
                        final byte[] bytes = virtualFile.contentsToByteArray();
                        String text = new String(bytes, virtualFile.getCharset());
                        int iMax = text.length();
                        int lastPos = 0;
                        int lineCount = 0;
                        while (lastPos < iMax) {
                            int pos = text.indexOf('\n', lastPos);
                            if (pos == -1) break;
                            lineCount++;

                            if (lineCount == startLine) {
                                return lastPos;
                            }

                            lastPos = pos + 1;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return -1;
    }

    @Nullable
    public static int[] getLinesFromOffsets(final PsiFile file, int... offsets) {
        if (file.isValid()) {
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile != null) {
                int iMax = offsets.length;
                final int[] lines = new int[iMax];
                final Editor textEditor = getEditor(file);
                if (textEditor != null) {
                    final Document document = textEditor.getDocument();
                    final int textLength = document.getTextLength();
                    int i = 0;
                    for (; i < iMax; i++) {
                        final int offset = offsets[i];
                        lines[i] = offset >= 0 && offset < textLength ? document.getLineNumber(offset) : -1;
                    }

                    final int lineCount = document.getLineCount();
                    while (i < iMax) {
                        lines[i++] = offsets[i] = lineCount - 1;
                    }
                } else {
                    // count EOLs
                    try {
                        final byte[] bytes = virtualFile.contentsToByteArray();
                        String text = new String(bytes, virtualFile.getCharset());
                        int lastPos = 0;
                        int lineCount = 0;
                        int i = 0;
                        int textLength = text.length();
                        while (lastPos < textLength) {
                            int pos = text.indexOf('\n', lastPos);
                            if (pos == -1) break;

                            while (i < iMax && offsets[i] <= pos) {
                                lines[i++] = lineCount;
                            }

                            lineCount++;

                            lastPos = pos + 1;
                        }

                        while (i < iMax) {
                            lines[i++] = lineCount;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return lines;
            }
        }
        return null;
    }

    @Nullable
    public static Editor getEditor(final PsiFile file) {
        Editor editor = null;
        if (file.isValid()) {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile != null) {
                editor = getEditor(file.getProject(), virtualFile);
            }
        }
        return editor;
    }

    @Nullable
    static Editor getEditor(final Project project, final VirtualFile virtualFile) {
        if (!isEventDispatchThread()) {
            Editor[] editors = { null };
            getApplication().invokeLater(() -> {
                editors[0] = getEditor(project, virtualFile);
            });

            // HACK: to get around no invokeAndWait while in read action. Getting editors completes in well under 10ms, most times 1 to 3 ms max
            //  so 25ms potential hang once in a blue moon is acceptable
            try {
                Thread.sleep(25);
            } catch (InterruptedException ignored) {
            }

            return editors[0];
        }

        final Editor editor;
        final FileEditorManager editorManager = FileEditorManager.getInstance(project);
        FileEditor fileEditor = editorManager.getSelectedEditor(virtualFile);

        if (fileEditor == null) {
            FileEditor[] fileEditors = editorManager.getAllEditors(virtualFile);
            if (fileEditors.length > 0) {
                fileEditor = fileEditors[0];
            }
        }

        if (fileEditor instanceof SplitFileEditor<?, ?>) {
            editor = MdActionUtil.INSTANCE.getTextEditor((SplitFileEditor<?, ?>) fileEditor, false);
        } else if (fileEditor instanceof TextEditor) {
            editor = ((TextEditor) fileEditor).getEditor();
        } else {
            editor = null;
        }
        return editor;
    }

    /**
     * This method is safe to run both in and out of {@link com.intellij.openapi.editor.CaretModel#runForEachCaret(CaretAction)} context. It scrolls to primary caret in both cases, and, in the former case, avoids performing excessive scrolling in case of large number of carets.
     */
    public static void scrollToCaret(@NotNull Editor editor) {
        if (editor.getCaretModel().getCurrentCaret() == editor.getCaretModel().getPrimaryCaret()) {
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        }
    }
}
