/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.editor;

import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.CSS;
import javax.swing.text.html.StyleSheet;
import java.net.URL;

public class MultiMarkdownStyleSheet extends StyleSheet {

    ListPainter myListPainter = null;

    public MultiMarkdownStyleSheet() {
        super();
    }

    ///**
    // * Adds a CSS attribute to the given set.
    // * failed attempt to map images to list item image. Does not work. The image is not used.
    // */
    //@Override
    //public void addCSSAttribute(MutableAttributeSet attr, CSS.Attribute cssKey, String value) {
    //    if (cssKey != null) {
    //        // replace the urls for list style images with our images
    //        URL url = null;
    //
    //        if (cssKey == CSS.Attribute.LIST_STYLE_IMAGE) {
    //            // here we will provide our own urls
    //
    //            if (value != null) {
    //                boolean isInverted = MultiMarkdownGlobalSettings.getInstance().isInvertedHtmlPreview();
    //
    //                if (value.endsWith("/bullet")) {
    //                    url = MultiMarkdownIcons.getIconResourceURL(MultiMarkdownIcons.TYPE_BULLET, isInverted);
    //                } else if (value.endsWith("/task")) {
    //                    url = MultiMarkdownIcons.getIconResourceURL(MultiMarkdownIcons.TYPE_TASK, isInverted);
    //                } else if (value.endsWith("/task-checked")) {
    //                    url = MultiMarkdownIcons.getIconResourceURL(MultiMarkdownIcons.TYPE_TASK_CHECKED, isInverted);
    //                }
    //            }
    //
    //            if (url != null) {
    //                value = url.toString();
    //            }
    //        }
    //    }
    //    super.addCSSAttribute(attr, cssKey, value);
    //}

}
