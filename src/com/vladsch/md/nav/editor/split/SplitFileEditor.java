// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.split;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdProjectSettings;
import com.vladsch.md.nav.settings.SettingsChangedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public abstract class SplitFileEditor<E1 extends FileEditor, E2 extends FileEditor> extends UserDataHolderBase implements FileEditor {
    public static final Key<SplitFileEditor<?, ?>> PARENT_SPLIT_KEY = Key.create("parentSplit");
    final public static Topic<SplitPreviewChangeListener> PREVIEW_CHANGE = Topic.create("MarkdownNavigator.SplitEditor.PreviewChange", SplitPreviewChangeListener.class);

    private static final String MY_PROPORTION_KEY = "SplitFileEditor.Proportion";

    @NotNull protected final E1 myMainEditor;
    @NotNull protected final E2 mySecondEditor;
    @NotNull private final JComponent myComponent;
    @NotNull private SplitEditorLayout mySplitEditorLayout;
    @NotNull private SplitEditorPreviewType mySplitEditorPreviewType;
    @NotNull private final MyListenersMultimap myListenersGenerator = new MyListenersMultimap();
    @NotNull JBSplitter mySplitter;
    private SplitEditorToolbar myToolbarWrapper;
    boolean myHideToolbar;

    public SplitFileEditor(@NotNull E1 mainEditor, @NotNull E2 secondEditor) {
        myMainEditor = mainEditor;
        mySecondEditor = secondEditor;

        mySplitEditorLayout = SplitEditorLayout.FIRST;
        mySplitEditorPreviewType = SplitEditorPreviewType.PREVIEW;
        myHideToolbar = MdApplicationSettings.getInstance().getDocumentSettings().getHideToolbar();
        myComponent = createComponent();

        MessageBusConnection settingsConnection = ApplicationManager.getApplication().getMessageBus().connect(this);

        settingsConnection.subscribe(SettingsChangedListener.TOPIC, settings -> {
            final boolean hideToolbar = MdApplicationSettings.getInstance().getDocumentSettings().getHideToolbar();
            if (myHideToolbar != hideToolbar) {
                myHideToolbar = hideToolbar;
                invalidateLayout();
            }

            final boolean verticalSplitPreview = MdApplicationSettings.getInstance().getDocumentSettings().getVerticalSplitPreview();
            if (mySplitter.isVertical() != verticalSplitPreview) {
                mySplitter.setOrientation(verticalSplitPreview);
            }
        });
    }

    @NotNull
    private JComponent createComponent() {
        mySplitter = new JBSplitter(MdApplicationSettings.getInstance().getDocumentSettings().getVerticalSplitPreview(), 0.5f, 0.15f, 0.85f);
        mySplitter.setSplitterProportionKey(MY_PROPORTION_KEY);
        mySplitter.setFirstComponent(myMainEditor.getComponent());
        mySplitter.setSecondComponent(mySecondEditor.getComponent());

        myToolbarWrapper = new SplitEditorToolbar(mySplitter);

        if (myMainEditor instanceof TextEditor) {
            myToolbarWrapper.addGutterToTrack(((EditorGutterComponentEx) ((TextEditor) myMainEditor).getEditor().getGutter()));
        }

        if (mySecondEditor instanceof TextEditor) {
            myToolbarWrapper.addGutterToTrack(((EditorGutterComponentEx) ((TextEditor) mySecondEditor).getEditor().getGutter()));
        }

        final JPanel result = new JPanel(new BorderLayout());
        result.add(myToolbarWrapper, BorderLayout.NORTH);
        result.add(mySplitter, BorderLayout.CENTER);

        myToolbarWrapper.setVisible(!myHideToolbar);
        adjustEditorsVisibility();

        return result;
    }

    public void triggerLayoutChange() {
        final int oldValue = mySplitEditorLayout.ordinal();
        final int N = SplitEditorLayout.values().length;
        final int newValue = (oldValue + N - 1) % N;

        triggerLayoutChange(SplitEditorLayout.values()[newValue]);
    }

    public void triggerLayoutChange(@NotNull SplitFileEditor.SplitEditorLayout newLayout) {
        if (mySplitEditorLayout == newLayout) {
            return;
        }

        boolean needPreviewChange = mySplitEditorLayout == SplitEditorLayout.FIRST || newLayout == SplitEditorLayout.FIRST;

        mySplitEditorLayout = newLayout;

        Project project = ((TextEditor) myMainEditor).getEditor().getProject();
        if (project != null) {
            MdProjectSettings projectSettings = MdProjectSettings.getInstance(project);
            if (projectSettings.getPreviewSettings().getLastLayoutSetsDefault()) {
                projectSettings.getPreviewSettings().setSplitEditorLayout(newLayout);
            }
        }

        if (needPreviewChange) {
            triggerPreviewChange(mySplitEditorPreviewType, true);
        } else {
            invalidateLayout();
        }
    }

    public void triggerPreviewChange() {
        final int oldValue = mySplitEditorPreviewType.ordinal();
        final int N = SplitEditorPreviewType.values().length - SplitEditorPreviewType.UNUSED_CYCLES;
        final int newValue = (oldValue + 1) % N;

        triggerPreviewChange(SplitEditorPreviewType.values()[newValue]);
    }

    public void triggerPreviewChange(@NotNull SplitEditorPreviewType newPreview) {
        triggerPreviewChange(newPreview, false);
    }

    public void triggerPreviewChange(@NotNull SplitEditorPreviewType newPreview, boolean layoutChange) {
        if (!layoutChange) {
            if (mySplitEditorPreviewType == newPreview) {
                return;
            }
            mySplitEditorPreviewType = newPreview;
        }
        invalidateLayout();
    }

    protected void notifyLayoutChange() {
        final SplitEditorPreviewType editorPreview = mySplitEditorPreviewType;
        final SplitEditorLayout editorLayout = mySplitEditorLayout;

        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().getMessageBus().syncPublisher(PREVIEW_CHANGE).updatePreviewType(editorPreview, editorLayout, mySecondEditor);
        });
    }

    @NotNull
    public SplitEditorLayout getCurrentEditorLayout() {
        return mySplitEditorLayout;
    }

    @NotNull
    public SplitEditorPreviewType getCurrentEditorPreview() {
        return mySplitEditorPreviewType;
    }

    void invalidateLayout() {
        adjustEditorsVisibility();
        notifyLayoutChange();

        if (myHideToolbar) {
            myToolbarWrapper.setVisible(false);
        } else {
            myToolbarWrapper.setVisible(true);
            myToolbarWrapper.refresh();
        }
        myComponent.repaint();
    }

    public void takeFocus() {
        JComponent focusComponent;
        if (mySplitEditorLayout.showFirst) {
            focusComponent = myMainEditor.getPreferredFocusedComponent();
            if (focusComponent == null) focusComponent = myMainEditor.getComponent();
        } else if (mySplitEditorLayout.showSecond) {
            focusComponent = mySecondEditor.getPreferredFocusedComponent();
            if (focusComponent == null) focusComponent = mySecondEditor.getComponent();
        } else {
            focusComponent = getPreferredFocusedComponent();
            if (focusComponent == null) focusComponent = myComponent;
        }
        IdeFocusManager.findInstanceByComponent(focusComponent).requestFocus(focusComponent, true);
    }

    void adjustEditorsVisibility() {
        myMainEditor.getComponent().setVisible(mySplitEditorLayout.showFirst);
        mySecondEditor.getComponent().setVisible(mySplitEditorLayout.showSecond);
    }

    @NotNull
    public E1 getMainEditor() {
        return myMainEditor;
    }

    @NotNull
    public E2 getSecondEditor() {
        return mySecondEditor;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return myComponent;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myMainEditor.getPreferredFocusedComponent();
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return new MyFileEditorState(mySplitEditorLayout.name(), mySplitEditorPreviewType.name(), myMainEditor.getState(level), mySecondEditor.getState(level));
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
        if (state instanceof MyFileEditorState) {
            final MyFileEditorState compositeState = (MyFileEditorState) state;
            if (compositeState.getFirstState() != null) {
                myMainEditor.setState(compositeState.getFirstState());
            }
            if (compositeState.getSecondState() != null) {
                mySecondEditor.setState(compositeState.getSecondState());
            }
            if (compositeState.getSplitLayout() != null) {
                mySplitEditorLayout = SplitEditorLayout.valueOf(compositeState.getSplitLayout());
                invalidateLayout();
            }
            if (compositeState.getMySplitPreview() != null) {
                mySplitEditorPreviewType = SplitEditorPreviewType.valueOf(compositeState.getMySplitPreview());
                invalidateLayout();
            }
        }
    }

    @Override
    public boolean isModified() {
        boolean myMainEditorModified = myMainEditor.isModified();
        boolean mySecondEditorModified = mySecondEditor.isModified();
        // RELEASE : comment out assert
        //assert !mySecondEditorModified;
        return myMainEditorModified || mySecondEditorModified;
    }

    @Override
    public boolean isValid() {
        return myMainEditor.isValid() && mySecondEditor.isValid();
    }

    @Override
    public void selectNotify() {
        myMainEditor.selectNotify();
        mySecondEditor.selectNotify();
    }

    @Override
    public void deselectNotify() {
        myMainEditor.deselectNotify();
        mySecondEditor.deselectNotify();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
        myMainEditor.addPropertyChangeListener(listener);
        mySecondEditor.addPropertyChangeListener(listener);

        final DoublingEventListenerDelegate delegate = myListenersGenerator.addListenerAndGetDelegate(listener);
        myMainEditor.addPropertyChangeListener(delegate);
        mySecondEditor.addPropertyChangeListener(delegate);
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
        myMainEditor.removePropertyChangeListener(listener);
        mySecondEditor.removePropertyChangeListener(listener);

        final DoublingEventListenerDelegate delegate = myListenersGenerator.removeListenerAndGetDelegate(listener);
        if (delegate != null) {
            myMainEditor.removePropertyChangeListener(delegate);
            mySecondEditor.removePropertyChangeListener(delegate);
        }
    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return myMainEditor.getBackgroundHighlighter();
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return myMainEditor.getCurrentLocation();
    }

    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder() {
        return myMainEditor.getStructureViewBuilder();
    }

    @Override
    public void dispose() {
        Disposer.dispose(myMainEditor);
        Disposer.dispose(mySecondEditor);
    }

    public SplitEditorLayout getSplitLayout() {
        return mySplitEditorLayout;
    }

    static class MyFileEditorState implements FileEditorState {
        @Nullable
        private final String mySplitLayout;
        @Nullable
        private final String mySplitPreview;
        @Nullable
        private final FileEditorState myFirstState;
        @Nullable
        private final FileEditorState mySecondState;

        public MyFileEditorState(
                @Nullable String splitLayout,
                @Nullable String splitPreview,
                @Nullable FileEditorState firstState,
                @Nullable FileEditorState secondState
        ) {
            mySplitLayout = splitLayout;
            mySplitPreview = splitPreview;
            myFirstState = firstState;
            mySecondState = secondState;
        }

        @Nullable
        public String getSplitLayout() {
            return mySplitLayout;
        }

        @Nullable
        public String getMySplitPreview() {
            return mySplitPreview;
        }

        @Nullable
        public FileEditorState getFirstState() {
            return myFirstState;
        }

        @Nullable
        public FileEditorState getSecondState() {
            return mySecondState;
        }

        @Override
        public boolean canBeMergedWith(FileEditorState otherState, FileEditorStateLevel level) {
            return otherState instanceof MyFileEditorState
                    && (myFirstState == null || myFirstState.canBeMergedWith(((MyFileEditorState) otherState).myFirstState, level))
                    && (mySecondState == null || mySecondState.canBeMergedWith(((MyFileEditorState) otherState).mySecondState, level));
        }
    }

    private class DoublingEventListenerDelegate implements PropertyChangeListener {
        @NotNull
        private final PropertyChangeListener myDelegate;

        DoublingEventListenerDelegate(@NotNull PropertyChangeListener delegate) {
            myDelegate = delegate;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            myDelegate.propertyChange(new PropertyChangeEvent(SplitFileEditor.this, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        }
    }

    private class MyListenersMultimap {
        private final Map<PropertyChangeListener, Pair<Integer, DoublingEventListenerDelegate>> myMap =
                new HashMap<>();

        MyListenersMultimap() {}

        @NotNull
        public DoublingEventListenerDelegate addListenerAndGetDelegate(@NotNull PropertyChangeListener listener) {
            if (!myMap.containsKey(listener)) {
                myMap.put(listener, Pair.create(1, new DoublingEventListenerDelegate(listener)));
            } else {
                final Pair<Integer, DoublingEventListenerDelegate> oldPair = myMap.get(listener);
                myMap.put(listener, Pair.create(oldPair.getFirst() + 1, oldPair.getSecond()));
            }

            //noinspection ReturnOfInnerClass
            return myMap.get(listener).getSecond();
        }

        @Nullable
        public DoublingEventListenerDelegate removeListenerAndGetDelegate(@NotNull PropertyChangeListener listener) {
            final Pair<Integer, DoublingEventListenerDelegate> oldPair = myMap.get(listener);
            if (oldPair == null) {
                return null;
            }

            if (oldPair.getFirst() == 1) {
                myMap.remove(listener);
            } else {
                myMap.put(listener, Pair.create(oldPair.getFirst() - 1, oldPair.getSecond()));
            }
            //noinspection ReturnOfInnerClass
            return oldPair.getSecond();
        }
    }

    public enum SplitEditorLayout {
        FIRST("FIRST", true, false, MdBundle.message("multimarkdown.layout.editor.only")),
        SECOND("SECOND", false, true, MdBundle.message("multimarkdown.layout.preview.only")),
        SPLIT("SPLIT", true, true, MdBundle.message("multimarkdown.layout.editor.and.preview"));

        public final String idName;
        public final boolean showFirst;
        public final boolean showSecond;
        public final String presentationName;

        SplitEditorLayout(
                @NotNull String name,
                boolean showFirst,
                boolean showSecond,
                @NotNull String presentationName
        ) {
            this.idName = name;
            this.showFirst = showFirst;
            this.showSecond = showSecond;
            this.presentationName = presentationName;
        }

        @Override
        public String toString() {
            return presentationName;
        }

        @Nullable
        public static SplitEditorLayout enumConstant(@NotNull String name) {
            for (SplitEditorLayout enumConstant : SplitEditorLayout.values()) {
                if (enumConstant.idName.equals(name)) return enumConstant;
            }
            return null;
        }
    }

    public enum SplitEditorPreviewType {
        PREVIEW("PREVIEW", false, false, MdBundle.message("multimarkdown.preview.rendered")),
        MODIFIED_HTML("MODIFIED_HTML", true, true, MdBundle.message("multimarkdown.preview.html.modified")),
        UNMODIFIED_HTML("UNMODIFIED_HTML", true, false, MdBundle.message("multimarkdown.preview.html.unmodified")),
        NONE("NONE", false, false, MdBundle.message("multimarkdown.preview.none"));

        public static final int UNUSED_CYCLES = 1;
        public final String idName;
        public final boolean showHtmlText;
        public final boolean showModified;
        public final String presentationName;

        SplitEditorPreviewType(String name, boolean showHtmlText, boolean showModified, String presentationName) {
            this.idName = name;
            this.showHtmlText = showHtmlText;
            this.showModified = showModified;
            this.presentationName = presentationName;
        }

        @Override
        public String toString() {
            return presentationName;
        }

        @Nullable
        public static SplitEditorPreviewType enumConstant(@NotNull String name) {
            for (SplitEditorPreviewType enumConstant : SplitEditorPreviewType.values()) {
                if (enumConstant.idName.equals(name)) return enumConstant;
            }
            return null;
        }
    }
}
