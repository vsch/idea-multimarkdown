/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static net.nicoulaj.idea.markdown.editor.MarkdownPathResolver.resolveRelativePath;

/**
 * {@link HTMLEditorKit} that can display images with paths relative to the document.
 *
 * @author Roger Grantham (https://github.com/grantham)
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.8
 */
public class MarkdownEditorKit extends HTMLEditorKit {

    /** The document. */
    private final Document document;

    /**
     * Build a new instance of {@link MarkdownEditorKit}.
     *
     * @param document the document
     */
    public MarkdownEditorKit(@NotNull Document document) {
        this.document = document;
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
        return new MarkdownViewFactory(document);
    }

    /**
     * An {@link HTMLFactory} that uses {@link MarkdownImageView} for images.
     *
     * @author Roger Grantham (https://github.com/grantham)
     * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
     * @since 0.8
     */
    private static class MarkdownViewFactory extends HTMLFactory {

        /** The document. */
        private final Document document;

        /**
         * Build a new instance of {@link MarkdownViewFactory}.
         *
         * @param document the document
         */
        private MarkdownViewFactory(Document document) {
            this.document = document;
        }

        @Override
        public View create(Element elem) {
            if (HTML.Tag.IMG.equals(elem.getAttributes().getAttribute(StyleConstants.NameAttribute)))
                return new MarkdownImageView(document, elem);
            return super.create(elem);
        }
    }

    /**
     * An {@link ImageView} that can resolve the image URL relative to the document.
     *
     * @author Roger Grantham (https://github.com/grantham)
     * @since 0.8
     */
    private static class MarkdownImageView extends ImageView {

        /** The document. */
        private final Document document;

        /**
         * Build a new instance of {@link MarkdownImageView}.
         *
         * @param document the document
         * @param elem     the element to create a view for
         */
        private MarkdownImageView(@NotNull Document document, @NotNull Element elem) {
            super(elem);
            this.document = document;
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
            final String src = (String) getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
            final VirtualFile localImage = resolveRelativePath(document, src);
            try {
                if (localImage != null && localImage.exists())
                    return new File(localImage.getPath()).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            return super.getImageURL();
        }
    }
}
