// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor;

import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jetbrains.annotations.NonNls;

import java.util.List;

public class PreviewEditorState implements FileEditorState {
    @NonNls private static final String PREVIEW_PANEL_STATE = "previewPanelState";
    @NonNls private static final String CUSTOM_EDITOR_STATE = "markdownNavigatorState";

    private final Element myElement;

    public PreviewEditorState() {
        myElement = new Element(CUSTOM_EDITOR_STATE);
    }

    public PreviewEditorState(final Element element) {
        List<Content> contentList = element.getContent((Filter<Content>) o -> o instanceof Element && ((Element) o).getName().equals(PREVIEW_PANEL_STATE));

        if (!contentList.isEmpty()) {
            // clone the element because it is immutable, this caused preview highlight to disappear,
            // intermittently because stored settings were loaded and caused exception when trying
            // to save new settings during js scroll/highlight operation
            Element stateElement = (Element) contentList.get(0).clone();
            if (stateElement.getName().equals(PREVIEW_PANEL_STATE)) {
                // wrap it in custom editor state
                Element customEditorState = new Element(CUSTOM_EDITOR_STATE);
                customEditorState.addContent(stateElement);
                myElement = customEditorState;
            } else {
                myElement = stateElement;
            }
        } else {
            myElement = new Element(CUSTOM_EDITOR_STATE);
        }
    }

    public Element getPreviewStateElement() {
        Element jsState = myElement.getChild(PREVIEW_PANEL_STATE);
        if (jsState == null) {
            jsState = new Element(PREVIEW_PANEL_STATE);
            myElement.addContent(jsState);
        }
        return jsState;
    }

    public Element getStateElement() {
        return myElement;
    }

    @Override
    public boolean canBeMergedWith(final FileEditorState otherState, final FileEditorStateLevel level) {
        // FIX: merge elements
        return true;
    }
}
