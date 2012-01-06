/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static net.nicoulaj.idea.markdown.editor.MarkdownPathResolver.resolveRelativePath;

/**
 * <p>MarkdownViewFactory</p>
 *
 * @author Roger Grantham
 */
public class MarkdownEditorKit extends HTMLEditorKit {

    @Override
    public Object clone() {
        super.clone();
        return new MarkdownEditorKit();
    }

    @Override
    public ViewFactory getViewFactory() {
        return new MarkdownViewFactory();
    }

    private static class MarkdownViewFactory extends HTMLFactory {
        @Override
        public View create(Element elem) {
            final Object tag = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (HTML.Tag.IMG.equals(tag)) {
                final AttributeSet atts = elem.getAttributes();
                final Object src = atts.getAttribute(HTML.Attribute.SRC);
                //final VirtualFile imageTarget = resolveRelativePath(src.toString());
                //final String imagePath = imageTarget.getPath();
                return new MarkdownImageView(elem);
            } else {
                return super.create(elem);
            }
        }
    }

    private static class MarkdownImageView extends ImageView {

        private MarkdownImageView(Element elem) {
            super(elem);
            this.setLoadsSynchronously(true);
        }

        @Override
        public Image getImage() {
            return super.getImage();
        }

        @Override
        public URL getImageURL() {
            URL imageURL = super.getImageURL();
            if (imageURL == null) {
                final String src = (String) getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
                VirtualFile localImage = resolveRelativePath(src);
                try {
                    if (localImage != null && localImage.exists()) {
                        imageURL = new File(localImage.getPath()).toURI().toURL();
                    }
                } catch (MalformedURLException e) {
                    imageURL = null;
                }
            }
            return imageURL;
        }
    }
}
