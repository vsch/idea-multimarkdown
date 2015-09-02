/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
* Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.View;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link HTMLEditorKit} that can display images with paths relative to the document.
 *
 * @author Roger Grantham (https://github.com/grantham)
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @author Vladimir Schneider <vladimir.schneider@gmail.com>
 * @since 0.8
 */
public class MultiMarkdownEditorKit extends HTMLEditorKit {

    /** The document. */
    private final Document document;
    protected float maxWidth;

    protected MultiMarkdownGlobalSettingsListener globalSettingsListener;

    public void setMaxWidth(float maxWidth) { this.maxWidth = maxWidth; }

    public float getMaxWidth() { return maxWidth; }

    /**
     * Build a new instance of {@link MultiMarkdownEditorKit}.
     *
     * @param document the document
     */
    public MultiMarkdownEditorKit(@NotNull Document document) {
        this.document = document;
        maxWidth = MultiMarkdownGlobalSettings.getInstance().maxImgWidth.getValue();

        MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
                maxWidth = MultiMarkdownGlobalSettings.getInstance().maxImgWidth.getValue();
            }
        });
    }

    /**
     * Creates a copy of the editor kit.
     *
     * @return a new {@link MultiMarkdownEditorKit} instance
     */
    @Override
    public Object clone() {
        return new MultiMarkdownEditorKit(document);
    }

    /** {@inheritDoc} */
    @Override
    public ViewFactory getViewFactory() {
        return new MarkdownViewFactory(document, this);
    }

    /**
     * An {@link HTMLFactory} that uses {@link MarkdownImageView} for images.
     *
     * @author Roger Grantham (https://github.com/grantham)
     * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
     * @author Vladimir Schneider <vladimir.schneider@gmail.com>
     * @since 0.8
     */
    private static class MarkdownViewFactory extends HTMLFactory {

        /** The document. */
        private final Document document;
        private MultiMarkdownEditorKit editorKit;

        /**
         * Build a new instance of {@link MarkdownViewFactory}.
         *
         * @param document the document
         */
        private MarkdownViewFactory(Document document, MultiMarkdownEditorKit editorKit) {
            this.document = document;
            this.editorKit = editorKit;
        }

        protected SimpleAttributeSet attributeSet;

        @Override
        public View create(Element elem) {
            if (HTML.Tag.IMG.equals(elem.getAttributes().getAttribute(StyleConstants.NameAttribute))) {
                return new MarkdownImageView(document, elem, editorKit);
                //} else if (HTML.Tag.INPUT.equals(elem.getAttributes().getAttribute(StyleConstants.NameAttribute))) {
                //    return new MarkdownInputView(elem);
            }
            return super.create(elem);
        }
    }

    //protected static class MarkdownInputView extends FormView {
    //
    //    private MarkdownInputView(@NotNull Element elem) {
    //        super(elem);
    //    }
    //
    //    @Override
    //    protected Component createComponent() {
    //        AttributeSet attr = getElement().getAttributes();
    //        HTML.Tag t = (HTML.Tag)
    //                attr.getAttribute(StyleConstants.NameAttribute);
    //        Component c = null;
    //        Object model = attr.getAttribute(StyleConstants.ModelAttribute);
    //        String type = (String) attr.getAttribute(HTML.Attribute.TYPE);
    //
    //        // Remove listeners previously registered in shared model
    //        // when a new UI component is replaced.  See bug 7189299.
    //        if (t == HTML.Tag.INPUT && type.equals("checkbox")) {
    //            String classType = (String) attr.getAttribute(Attribute.CLASS);
    //            boolean isInverted = MultiMarkdownGlobalSettings.getInstance().isInvertedHtmlPreview();
    //            if (classType != null && classType.equals("task-list-item-checkbox")) {
    //                c = super.createComponent();
    //                JCheckBox chk = (JCheckBox) c;
    //                Image openTask = isInverted ? MultiMarkdownIcons.OPEN_TASK_INV : MultiMarkdownIcons.OPEN_TASK;
    //                Icon closedTask = isInverted ? MultiMarkdownIcons.CLOSED_TASK_INV : MultiMarkdownIcons.CLOSED_TASK;
    //                chk.setIcon(openTask);
    //                chk.setDisabledIcon(openTask);
    //                chk.setSelectedIcon(closedTask);
    //                chk.setDisabledSelectedIcon(closedTask);
    //                c.setEnabled(false);
    //            } else if (classType != null && classType.equals("list-item-bullet")) {
    //                c = super.createComponent();
    //                JCheckBox chk = (JCheckBox) c;
    //                Icon bullet = isInverted ? MultiMarkdownIcons.BULLET_INV : MultiMarkdownIcons.BULLET;
    //                chk.setIcon(bullet);
    //                chk.setDisabledIcon(bullet);
    //                chk.setSelectedIcon(bullet);
    //                chk.setDisabledSelectedIcon(bullet);
    //                c.setEnabled(false);
    //            } else {
    //                c = super.createComponent();
    //            }
    //        } else {
    //            c = super.createComponent();
    //        }
    //
    //        return c;
    //    }
    //}

    /**
     * An {@link ImageView} that can resolve the image URL relative to the document.
     *
     * @author Roger Grantham (https://github.com/grantham)
     * @author Vladimir Schneider <vladimir.schneider@gmail.com>
     * @since 0.8
     */
    protected static class MarkdownImageView extends ImageView {

        /** The document. */
        private final Document document;
        private MultiMarkdownEditorKit editorKit;
        private boolean scaled;

        /**
         * Build a new instance of {@link MarkdownImageView}.
         *
         * @param document the document
         * @param elem     the element to create a view for
         */
        private MarkdownImageView(@NotNull Document document, @NotNull Element elem, @NotNull MultiMarkdownEditorKit editorKit) {
            super(elem);

            scaled = false;
            this.document = document;
            this.editorKit = editorKit;
        }

        /**
         * Return a URL for the image source, or null if it could not be determined.
         * <p/>
         * Calls {@link javax.swing.text.html.ImageView#getImageURL()}, tries to resolve the relative if needed.
         *
         * @return a URL for the image source, or null if it could not be determined.
         */
        @Override
        public URL getImageURL() {
            String classType = (String) getElement().getAttributes().getAttribute(Attribute.CLASS);
            boolean isInverted = MultiMarkdownGlobalSettings.getInstance().isInvertedHtmlPreview();
            if (classType != null && classType.equals("task")) {
                return MultiMarkdownIcons.getIconResourceURL(MultiMarkdownIcons.TYPE_TASK, isInverted);
            } else if (classType != null && classType.equals("task-checked")) {
                return MultiMarkdownIcons.getIconResourceURL(MultiMarkdownIcons.TYPE_TASK_CHECKED, isInverted);
            } else if (classType != null && classType.equals("bullet")) {
                return MultiMarkdownIcons.getIconResourceURL(MultiMarkdownIcons.TYPE_BULLET, isInverted);
            } else {
                final String src = (String) getElement().getAttributes().getAttribute(Attribute.SRC);
                if (src != null) {
                    final VirtualFile localImage = MultiMarkdownPathResolver.resolveRelativePath(document, src);
                    try {
                        if (localImage != null && localImage.exists())
                            return new File(localImage.getPath()).toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
                return super.getImageURL();
            }
        }

        float width;
        float height;

        @Override
        public float getPreferredSpan(int axis) {
            if (!scaled) {
                float width = super.getPreferredSpan(View.X_AXIS);
                float height = super.getPreferredSpan(View.Y_AXIS);

                if (width < 0 || height < 0) return super.getPreferredSpan(axis);

                final float maxWidth = editorKit.getMaxWidth();

                if (maxWidth > 0 && width > maxWidth) {
                    scaled = true;
                    this.width = maxWidth;
                    this.height = (int) (height * maxWidth / width);

                    // force refresh of the image size
                    View parent = getParent();
                    super.setParent(null);
                    super.setParent(parent);
                } else {
                    this.width = super.getPreferredSpan(View.X_AXIS);
                    this.height = super.getPreferredSpan(View.Y_AXIS);
                }
            }
            return axis == View.X_AXIS ? this.width : (axis == View.Y_AXIS ? this.height : 0);
        }

        /**
         * Paints the View.
         *
         * @param g the rendering surface to use
         * @param a the allocated region to render into
         *
         * @see View#paint
         */
        @Override
        public void paint(@NotNull Graphics g, @NotNull Shape a) {
            float width = getPreferredSpan(View.X_AXIS);
            float height = getPreferredSpan(View.Y_AXIS);
            final float maxWidth = editorKit.getMaxWidth();

            if (maxWidth > 0 && width > maxWidth) {
                height = height * maxWidth / width;
                width = maxWidth;
            }

            Rectangle rect = (a instanceof Rectangle) ? (Rectangle) a :
                    a.getBounds();
            Rectangle clip = g.getClipBounds();

            if (clip != null) {
                g.clipRect(rect.x, rect.y,
                        rect.width,
                        rect.height);
            }

            Container host = getContainer();
            Image img = getImage();
            if (img != null) {
                if (width > 0 && height > 0) {
                    // Draw the image
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(img, rect.x, rect.y, (int) width, (int) height, null);
                }
            } else {
                Icon icon = getNoImageIcon();
                if (icon != null) {
                    icon.paintIcon(host, g,
                            rect.x, rect.y);
                }
            }
            if (clip != null) {
                // Reset clip.
                g.setClip(clip.x, clip.y, clip.width, clip.height);
            }
        }
    }
}
