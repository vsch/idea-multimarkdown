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
 */
package com.vladsch.idea.multimarkdown.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathDistance implements Comparable<PathDistance> {
    final protected String path;
    final protected MultiMarkdownFile file;
    final protected double distance;

    public static String[] loadLinkRefsStrings(List<MultiMarkdownFile> files, VirtualFile inFile, int searchFlags) {
        ArrayList<PathDistance> pathList = new ArrayList<PathDistance>(files.size());
        for (MultiMarkdownFile file : files) {
            String ref = file.getWikiPageRef(inFile, searchFlags);
            if (ref != null) {
                pathList.add(new PathDistance(ref));
            }
        }


        PathDistance[] paths = pathList.toArray(new PathDistance[pathList.size()]);
        Arrays.sort(paths);

        String[] result = new String[paths.length];
        int index = 0;
        for (PathDistance path : paths) {
            result[index++] = path.getPath();
        }

        return result;
    }

    public static PathDistance[] loadLinkRefsPaths(List<MultiMarkdownFile> files, VirtualFile inFile, int searchFlags) {
        ArrayList<PathDistance> pathList = new ArrayList<PathDistance>(files.size());
        for (MultiMarkdownFile file : files) {
            String ref = file.getWikiPageRef(inFile, searchFlags);
            if (ref != null) {
                pathList.add(new PathDistance(ref));
            }
        }


        PathDistance[] result = pathList.toArray(new PathDistance[pathList.size()]);
        Arrays.sort(result);
        return result;
    }

    public String getPath() {
        return path;
    }

    public MultiMarkdownFile getFile() {
        return file;
    }

    public double getDistance() {
        return distance;
    }

    public PathDistance() {
        path = "";
        distance = 10000.0;
        file = null;
    }

    public PathDistance(String path) {
        this.path = path;
        this.distance = MultiMarkdownProjectComponent.getLinkDistance(path);
        this.file = null;
    }

    public PathDistance(MultiMarkdownFile file, String path) {
        this.path = path;
        this.distance = MultiMarkdownProjectComponent.getLinkDistance(path);
        this.file = file;
    }

    @Override
    public int compareTo(@NotNull PathDistance o) {
        if (this.distance == o.distance) return path.toLowerCase().compareTo(o.path.toLowerCase());
        if (this.distance < o.distance) return -1;
        return 1;
    }

    public boolean isValid() {
        return path.length() > 0;
    }
}
