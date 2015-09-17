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
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownWikiLinkImpl;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.editor.ex.EditorSettingsExternalizable.STRIP_TRAILING_SPACES_NONE;
import static com.intellij.openapi.editor.ex.EditorSettingsExternalizable.STRIP_TRAILING_SPACES_WHOLE;

public class MultiMarkdownFile extends PsiFileBase {
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

    public static String makeLinkRef(String name) {
        return name.replace('-', ' ');
    }

    public static String makeFileName(String name, boolean addExtension) {
        return name.replace(' ', '-') + (addExtension ? ".md" : "");
    }

    public static String makeFileName(String name) {
        return makeFileName(name,false);
    }

    public static boolean isWikiPage(VirtualFile file) {
        if (file == null) return false;

        String canonicalPath = file.getCanonicalPath();
        return canonicalPath != null && canonicalPath.contains(".wiki/");
    }

    public boolean isWikiPage() {
        return MultiMarkdownFile.isWikiPage(getVirtualFile());
    }

    public boolean isPageReference(String name, VirtualFile inFile) {
        // convert it just in case it has spaces
        if (inFile == null) {
            String wikiLinkName = MultiMarkdownFile.makeLinkRef(getVirtualFile().getCanonicalPath());
            return wikiLinkName.endsWith("/" + name + "." + getVirtualFile().getExtension());
        } else {
            String wikiLinkName = getWikiLinkName(inFile);
            return wikiLinkName.equals(name);
        }
    }

    public String getWikiLinkName() {
        String wikiLinkName = MultiMarkdownFile.makeLinkRef(getVirtualFile().getNameWithoutExtension());
        return wikiLinkName;
    }

    public static String getWikiLinkName(VirtualFile toFile, VirtualFile inFile) {
        String wikiLinkName = toFile.getNameWithoutExtension();
        String canonicalPath = toFile.getCanonicalPath();
        String sourcePath = inFile.getCanonicalPath();
        String pathPrefix = "";
        if (canonicalPath != null && sourcePath != null) {
            String[] targetParts = canonicalPath.split("/");
            String[] sourceParts = sourcePath.split("/");
            int iMax = Math.min(targetParts.length - 1, sourceParts.length - 1);
            int i;
            for (i = 1; i < iMax; i++) {
                if (!targetParts[i].equals(sourceParts[i])) break;
            }

            // used up the common prefix, now for every source we need to add ../
            iMax = sourceParts.length - 1;
            for (int j = i; j < iMax; j++) {
                pathPrefix += "../";
            }

            // used up the common prefix, now for every target we need to add the part/
            iMax = targetParts.length - 1;
            for (; i < iMax; i++) {
                pathPrefix += targetParts[i] + "/";
            }

            wikiLinkName = pathPrefix + wikiLinkName;
        }
        return MultiMarkdownFile.makeLinkRef(wikiLinkName);
    }

    public static String getWikiLinkFileName(VirtualFile toFile, VirtualFile inFile, boolean addExtension) {
        return makeFileName(getWikiLinkName(toFile, inFile), addExtension);
    }

    public String getWikiLinkFileName(VirtualFile inFile, boolean addExtension) {
        return MultiMarkdownFile.getWikiLinkFileName(getVirtualFile(), inFile, addExtension);
    }

    public String getWikiLinkFileName(VirtualFile inFile) {
        return MultiMarkdownFile.getWikiLinkFileName(getVirtualFile(), inFile, true);
    }

    public String getWikiLinkName(VirtualFile inFile) {
        return getWikiLinkName(getVirtualFile(), inFile);
    }

    @Override
    public VirtualFile getVirtualFile() {
        final VirtualFile file = super.getVirtualFile();

        // now it is a user selectable option.
        if (file != null) {
            Key<String> overrideStripTrailingSpacesKey = getOverrideStripTrailingSpacesKey();

            if (overrideStripTrailingSpacesKey != null) {
                file.putUserData(overrideStripTrailingSpacesKey, MultiMarkdownGlobalSettings.getInstance().enableTrimSpaces.getValue() ? STRIP_TRAILING_SPACES_WHOLE : STRIP_TRAILING_SPACES_NONE);
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
        final Pattern apiVersionPattern = Pattern.compile("^[A-Z]+-(\\d+\\.\\d+)$");
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

    ///**
    // * Returns the reference from this PSI element to another PSI element (or elements), if one exists.
    // * If the element has multiple associated references (see {@link #getReferences()}
    // * for an example), returns the first associated reference.
    // *
    // * @return the reference instance, or null if the PSI element does not have any
    // * associated references.
    // *
    // * @see com.intellij.psi.search.searches.ReferencesSearch
    // */
    //@Nullable
    //@Override
    //public PsiReference getReference() {
    //    return null;
    //}
    //
    ///**
    // * Returns all references from this PSI element to other PSI elements. An element can
    // * have multiple references when, for example, the element is a string literal containing
    // * multiple sub-strings which are valid full-qualified class names. If an element
    // * contains only one text fragment which acts as a reference but the reference has
    // * multiple possible targets, {@link PsiPolyVariantReference} should be used instead
    // * of returning multiple references.
    // * <p/>
    // * Actually, it's preferable to call {@link com.intellij.psi.PsiReferenceService#getReferences} instead
    // * as it allows adding references by plugins when the element implements {@link com.intellij.psi.ContributedReferenceHost}.
    // *
    // * @return the array of references, or an empty array if the element has no associated
    // * references.
    // *
    // * @see com.intellij.psi.PsiReferenceService#getReferences
    // * @see com.intellij.psi.search.searches.ReferencesSearch
    // */
    //@Override
    //@NotNull
    //public PsiReference[] getReferences() {
    //    return SharedPsiElementImplUtil.getReferences(this);
    //}

    @Override public String toString() {
        return "MultiMarkdownFile : " + getVirtualFile().toString();
    }
}
