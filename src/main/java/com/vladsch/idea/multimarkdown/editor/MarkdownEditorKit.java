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
import com.vladsch.idea.multimarkdown.settings.MarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.settings.MarkdownGlobalSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.View;
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
public class MarkdownEditorKit extends HTMLEditorKit {

    /** The document. */
    private final Document document;
    protected float maxWidth;

    protected MarkdownGlobalSettingsListener globalSettingsListener;

    public void setMaxWidth(float maxWidth) { this.maxWidth = maxWidth; }
    public float getMaxWidth() { return maxWidth; }


    /**
     * Build a new instance of {@link MarkdownEditorKit}.
     *
     * @param document the document
     */
    public MarkdownEditorKit(@NotNull Document document) {
        this.document = document;
        maxWidth = MarkdownGlobalSettings.getInstance().maxImgWidth.getValue();

        MarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MarkdownGlobalSettings newSettings) {
                maxWidth = MarkdownGlobalSettings.getInstance().maxImgWidth.getValue();
            }
        });
    }

    /**
     * Creates a copy of the editor kit.
     *
     * @return a new {@link MarkdownEditorKit} instance
     */
    @Override
    public Object clone() {
        return new MarkdownEditorKit(document);
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
        private MarkdownEditorKit editorKit;

        /**
         * Build a new instance of {@link MarkdownViewFactory}.
         *
         * @param document the document
         */
        private MarkdownViewFactory(Document document, MarkdownEditorKit editorKit) {
            this.document = document;
            this.editorKit = editorKit;
        }

        protected SimpleAttributeSet attributeSet;

        @Override
        public View create(Element elem) {
            if (HTML.Tag.IMG.equals(elem.getAttributes().getAttribute(StyleConstants.NameAttribute))) {
                return new MarkdownImageView(document, elem, editorKit);
            }
            return super.create(elem);
        }
    }

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
        private MarkdownEditorKit editorKit;
        private boolean scaled;

        /**
         * Build a new instance of {@link MarkdownImageView}.
         *
         * @param document the document
         * @param elem     the element to create a view for
         */
        private MarkdownImageView(@NotNull Document document, @NotNull Element elem, @NotNull MarkdownEditorKit editorKit) {
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
            final String src = (String) getElement().getAttributes().getAttribute(Attribute.SRC);
            final VirtualFile localImage = MarkdownPathResolver.resolveRelativePath(document, src);
            try {
                if (localImage != null && localImage.exists())
                    return new File(localImage.getPath()).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            return super.getImageURL();
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
