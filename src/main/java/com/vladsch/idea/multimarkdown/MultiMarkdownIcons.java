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
package com.vladsch.idea.multimarkdown;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class MultiMarkdownIcons {
    private static final boolean useHiDPI = UIUtil.isRetina();

    protected static Image getIconImage(String hiDPI, String normalDPI) {
        try {
            return ImageIO.read(MultiMarkdownIcons.class.getResourceAsStream(useHiDPI ? hiDPI : normalDPI));
        } catch (IOException ex) {
            // TODO: print stack and provide default blank icon
            return null;
        }
    }

    public final static int TYPE_TASK = 0;
    public final static int TYPE_TASK_CHECKED = 1;
    public final static int TYPE_BULLET = 2;

    public static URL getIconResourceURL(int type, boolean isDark) {
        switch (type) {
        case TYPE_TASK:
            return MultiMarkdownIcons.class.getResource(useHiDPI ?
                    (isDark ? "/com/vladsch/idea/multimarkdown/opentask@2x_dark.png" : "/com/vladsch/idea/multimarkdown/opentask@2x.png")
                    : (isDark ? "/com/vladsch/idea/multimarkdown/opentask_dark.png" : "/com/vladsch/idea/multimarkdown/opentask.png")
            );

        case TYPE_TASK_CHECKED:
            return MultiMarkdownIcons.class.getResource(useHiDPI ?
                    (isDark ? "/com/vladsch/idea/multimarkdown/closedtask@2x_dark.png" : "/com/vladsch/idea/multimarkdown/closedtask@2x.png")
                    : (isDark ? "/com/vladsch/idea/multimarkdown/closedtask_dark.png" : "/com/vladsch/idea/multimarkdown/closedtask.png")
            );

        case TYPE_BULLET:
            return MultiMarkdownIcons.class.getResource(useHiDPI ?
                    (isDark ? "/com/vladsch/idea/multimarkdown/bullet@2x_dark.png" : "/com/vladsch/idea/multimarkdown/bullet@2x.png")
                    : (isDark ? "/com/vladsch/idea/multimarkdown/bullet_dark.png" : "/com/vladsch/idea/multimarkdown/bullet.png")
            );

        default:
            return MultiMarkdownIcons.class.getResource(useHiDPI ? "/com/vladsch/idea/multimarkdown/missing@2x.png" : "/com/vladsch/idea/multimarkdown/missing.png");
        }
    }

    public static final Icon FILE = IconLoader.getIcon("/com/vladsch/idea/multimarkdown/multimarkdown.png");
    //public static final Icon OPEN_TASK = IconLoader.getIcon("/com/vladsch/idea/multimarkdown/opentask.png");
    //public static final Icon CLOSED_TASK = IconLoader.getIcon("/com/vladsch/idea/multimarkdown/closedtask.png");
    //public static final Icon BULLET = IconLoader.getIcon("/com/vladsch/idea/multimarkdown/bullet.png");
    //public static final Icon OPEN_TASK_INV = IconLoader.getIcon("/com/vladsch/idea/multimarkdown/opentask_inv.png");
    //public static final Icon CLOSED_TASK_INV = IconLoader.getIcon("/com/vladsch/idea/multimarkdown/closedtask_inv.png");
    //public static final Icon BULLET_INV = IconLoader.getIcon("/com/vladsch/idea/multimarkdown/bullet_inv.png");
}
