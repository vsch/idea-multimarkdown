/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package com.vladsch.idea.multimarkdown.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.vladsch.idea.multimarkdown.*;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.editor.ex.EditorSettingsExternalizable.STRIP_TRAILING_SPACES_NONE;
import static com.intellij.openapi.editor.ex.EditorSettingsExternalizable.STRIP_TRAILING_SPACES_WHOLE;

public class MultiMarkdownFile extends PsiFileBase {
    private static final Logger logger = Logger.getLogger(MultiMarkdownFile.class);

    public MultiMarkdownFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, MultiMarkdownLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return MultiMarkdownFileType.INSTANCE;
    }

    @Override
    public Icon getIcon(int flags) {
        //return super.getIcon(flags);
        return isWikiPage() ? MultiMarkdownIcons.WIKI : MultiMarkdownIcons.FILE;
    }

    public boolean isWikiPage() {
        return new FilePathInfo(getVirtualFile()).isWikiPage();
    }

    @Override
    public VirtualFile getVirtualFile() {
        final VirtualFile file = super.getVirtualFile();

        // now it is a user selectable option.
        if (file != null) {
            Key<String> overrideStripTrailingSpacesKey = getOverrideStripTrailingSpacesKey();

            if (overrideStripTrailingSpacesKey != null) {
                file.putUserData(overrideStripTrailingSpacesKey,
                        MultiMarkdownGlobalSettings.getInstance().enableTrimSpaces.getValue() ?
                                STRIP_TRAILING_SPACES_WHOLE : STRIP_TRAILING_SPACES_NONE);
            }
        }

        return file;
    }

    /**
     * Gets OVERRIDE_STRIP_TRAILING_SPACES_KEY from the TrailingSpacesStripper class. Since the package
     * in which the class is located depends on api version, some checks are required.
     *
     * @return a key for "strip trailing white space" setting
     */
    @Nullable
    private static Key<String> getOverrideStripTrailingSpacesKey() {
        final String apiVersion = ApplicationInfo.getInstance().getApiVersion();
        final Pattern apiVersionPattern = Pattern.compile("^[A-Z]*-(\\d+\\..*)$");
        final Matcher matcher = apiVersionPattern.matcher(apiVersion);

        if (!matcher.matches()) {
            return null;
        }

        String buildVersion = matcher.group(1);

        final String classPath;
        if (buildVersion.compareTo("138") >= 0) {
            classPath = "com.intellij.openapi.editor.impl.TrailingSpacesStripper";
        } else {
            classPath = "com.intellij.openapi.fileEditor.impl.TrailingSpacesStripper";
        }

        try {
            //noinspection unchecked
            return (Key<String>) Class.forName(classPath).getDeclaredField("OVERRIDE_STRIP_TRAILING_SPACES_KEY").get(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        if (getVirtualFile() == null) {
            return super.toString();
        }
        return "MultiMarkdownFile : " + getVirtualFile().toString();
    }
}
