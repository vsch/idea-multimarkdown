// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.split;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.util.ui.JBUI;
import com.vladsch.md.nav.editor.util.JBEmptyBorder;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class SplitEditorToolbar extends JPanel implements Disposable {
    private static final String LEFT_TOOLBAR_GROUP_ID = "MarkdownNavigator.Toolbar.Left";
    private static final String RIGHT_TOOLBAR_GROUP_ID = "MarkdownNavigator.Toolbar.Right";

    private final MySpacingPanel mySpacingPanel;

    private final ActionToolbar myRightToolbar;

    private final List<EditorGutterComponentEx> myGutters = new ArrayList<EditorGutterComponentEx>();

    private final ComponentAdapter myAdjustToGutterListener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            adjustSpacing();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            adjustSpacing();
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            adjustSpacing();
        }
    };

    public SplitEditorToolbar(@NotNull final JComponent targetComponentForActions) {
        super(new GridBagLayout());

        final ActionToolbar leftToolbar = createToolbarFromGroupId(LEFT_TOOLBAR_GROUP_ID);
        leftToolbar.setTargetComponent(targetComponentForActions);
        myRightToolbar = createToolbarFromGroupId(RIGHT_TOOLBAR_GROUP_ID);
        myRightToolbar.setTargetComponent(targetComponentForActions);

        mySpacingPanel = new MySpacingPanel((int) leftToolbar.getComponent().getPreferredSize().getHeight());
        final JPanel centerPanel = new JPanel(new BorderLayout());
        //centerPanel.add(new JLabel("View:"/*MdBundle.message("editor.toolbar.view.label")*/, SwingConstants.RIGHT), BorderLayout.EAST);

        add(mySpacingPanel);
        add(leftToolbar.getComponent());
        add(centerPanel, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.emptyInsets(), 0, 0));
        add(myRightToolbar.getComponent());

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.CONTRAST_BORDER_COLOR));

        addComponentListener(myAdjustToGutterListener);
    }

    public void addGutterToTrack(@NotNull EditorGutterComponentEx gutterComponentEx) {
        myGutters.add(gutterComponentEx);

        gutterComponentEx.addComponentListener(myAdjustToGutterListener);
    }

    public void refresh() {
        adjustSpacing();
        myRightToolbar.updateActionsImmediately();
    }

    void adjustSpacing() {
        EditorGutterComponentEx leftMostGutter = null;
        for (EditorGutterComponentEx gutter : myGutters) {
            if (!gutter.isShowing()) {
                continue;
            }
            if (leftMostGutter == null || leftMostGutter.getX() > gutter.getX()) {
                leftMostGutter = gutter;
            }
        }

        final int spacing;
        if (leftMostGutter == null) {
            spacing = 0;
        } else {
            spacing = leftMostGutter.getWhitespaceSeparatorOffset();
        }
        mySpacingPanel.setSpacing(spacing);

        revalidate();
        repaint();
    }

    @Override
    public void dispose() {
        removeComponentListener(myAdjustToGutterListener);
        for (EditorGutterComponentEx gutter : myGutters) {
            gutter.removeComponentListener(myAdjustToGutterListener);
        }
    }

    @NotNull
    private static ActionToolbar createToolbarFromGroupId(@NotNull String groupId) {
        final ActionManagerEx actionManager = ActionManagerEx.getInstanceEx();

        if (!actionManager.isGroup(groupId)) {
            throw new IllegalStateException(groupId + " should be a group");
        }
        final ActionGroup group = ((ActionGroup) actionManager.getAction(groupId));
        final ActionToolbarImpl editorToolbar =
                ((ActionToolbarImpl) actionManager.createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, group, true, false));
        editorToolbar.setOpaque(false);
        editorToolbar.setBorder(new JBEmptyBorder(0, 2, 0, 2));
        //editorToolbar.setMiniMode(true);
        editorToolbar.setLayoutPolicy(ActionToolbar.AUTO_LAYOUT_POLICY);

        return editorToolbar;
    }

    private static class MySpacingPanel extends JPanel {
        private final int myHeight;

        private int mySpacing;

        public MySpacingPanel(int height) {
            myHeight = height;
            mySpacing = 0;
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(mySpacing, myHeight);
        }

        public void setSpacing(int spacing) {
            mySpacing = spacing;
        }
    }
}
