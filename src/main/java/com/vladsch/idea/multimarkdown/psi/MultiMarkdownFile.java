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

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.util.messages.MessageBusConnection;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import com.vladsch.idea.multimarkdown.util.ListenerNotifier;
import com.vladsch.idea.multimarkdown.util.ListenerNotifyDelegate;
import com.vladsch.idea.multimarkdown.util.ProjectFileListListener;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.editor.ex.EditorSettingsExternalizable.STRIP_TRAILING_SPACES_NONE;
import static com.intellij.openapi.editor.ex.EditorSettingsExternalizable.STRIP_TRAILING_SPACES_WHOLE;

public class MultiMarkdownFile extends PsiFileBase implements ListenerNotifyDelegate<MultiMarkdownFile.ReferenceChangeListener> {
    private static final Logger logger = Logger.getLogger(MultiMarkdownFile.class);
    private static final int MISSING_REFS_UPDATED = 1;
    private static final int MISSING_REFS_CHANGED = 2;

    private HashMap<String, MultiMarkdownNamedElement> missingLinks = new HashMap<String, MultiMarkdownNamedElement>();
    private final ProjectFileListListener projectFileListener = new ProjectFileListListener() {
        @Override
        public void projectListsUpdated() {
            invalidateMissingLinkElements();
        }
    };

    public interface ReferenceChangeListener extends ProjectFileListListener {
    }

    private final ListenerNotifier<ReferenceChangeListener> notifier = new ListenerNotifier<ReferenceChangeListener>(this);

    // notification on addListener
    @Override
    public void notify(ReferenceChangeListener listener, Object... params) {
        if (params.length > 0) {
            switch ((Integer) params[0]) {
                case MISSING_REFS_UPDATED:
                    // logger.info("notify missing refs updated listeners" + this.hashCode());
                    listener.projectListsUpdated();
                    break;

                //case MISSING_REFS_CHANGED:
                //    if (params.length > 2) {
                //        MultiMarkdownNamedElement oldElement = (MultiMarkdownNamedElement) params[1];
                //        MultiMarkdownNamedElement newElement = (MultiMarkdownNamedElement) params[2];
                //        logger.info("notify missing refs changed from " + oldElement + " to " + newElement);
                //        listener.namedElementChanged(oldElement, newElement);
                //    }
                //    break;

                default:
                    break;
            }
        }
    }

    public void addListener(@NotNull ReferenceChangeListener listener) {
        notifier.addListener(listener);
    }

    public void removeListener(@NotNull ReferenceChangeListener listener) {
        notifier.removeListener(listener);
    }

    public MultiMarkdownFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, MultiMarkdownLanguage.INSTANCE);
        MultiMarkdownPlugin.getProjectComponent(getProject()).addListener(projectFileListener);

        MessageBusConnection connect = getProject().getMessageBus().connect();
        connect.subscribe(RefactoringEventListener.REFACTORING_EVENT_TOPIC, new RefactoringEventListener() {
            @Override
            public void refactoringStarted(@NotNull String refactoringId, @Nullable RefactoringEventData beforeData) {
                // logger.info("refactoring started on " + this.hashCode());
            }

            @Override
            public void refactoringDone(@NotNull String refactoringId, @Nullable RefactoringEventData afterData) {
                // logger.info("refactoring done on " + this.hashCode());
                invalidateAfterRefactoring();
                //logger.info("refactoring done on " + this.hashCode());
            }

            @Override
            public void conflictsDetected(@NotNull String refactoringId, @NotNull RefactoringEventData conflictsData) {
                // logger.info("refactoring conflicts on " + this.hashCode());
                invalidateAfterRefactoring();
            }

            @Override
            public void undoRefactoring(@NotNull String refactoringId) {
                // logger.info("refactoring undo on " + this.hashCode());
                invalidateAfterRefactoring();
            }
        });
    }

    protected void invalidateAfterRefactoring() {
        if (!missingLinks.isEmpty()) {
            // logger.info("invalidating after refactoring " + this.hashCode());
            invalidateMissingLinkElements();
            notifier.notifyListeners(MISSING_REFS_UPDATED);
            DaemonCodeAnalyzer.getInstance(getProject()).restart(this);
        }
    }

    public MultiMarkdownNamedElement getMissingLinkElement(final MultiMarkdownNamedElement element, String name) {
        // see if this element used to be the one that was referenced by other missing links
        for (String key : missingLinks.keySet()) {
            if (!key.equals(name) && missingLinks.get(key) == element) {
                // yes it has, we need to invalidate an rebuild
                // logger.info("element " + element + " was root");
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // logger.info("invalidating references due to root change " + element);
                        invalidateAfterRefactoring();
                    }
                });

                // just point to itself for now, it will be reset
                return element;
            }
        }

        if (!missingLinks.containsKey(name)) {
            // see if this one had its name edited, so we can remove it from the list and reset
            //logger.info("adding missing element " + element);
            missingLinks.put(name, element);
            return element;
        }

        //logger.info("returning missing ref for element " + element + " to " + missingLinks.get(name));
        return missingLinks.get(name);
    }

    //public void setMissingLinkElement(final MultiMarkdownNamedElement oldElement, final MultiMarkdownNamedElement newElement, String name) {
    //    // see if this element used to be the one that was referenced by other missing links
    //    for (String key : missingLinks.keySet()) {
    //        if ((!key.equals(name) || oldElement != newElement) && (missingLinks.get(key) == oldElement || missingLinks.get(key) == newElement)) {
    //            // yes it has, we need to invalidate an rebuild
    //            logger.info("set missing element" + oldElement + " to " + newElement);
    //            notifier.notifyListeners(MISSING_REFS_CHANGED, oldElement, newElement);
    //        }
    //    }
    //}

    public void invalidateMissingLinkElements() {
        //logger.info("invalidatingMissingLinks on " + this.hashCode());
        missingLinks.clear();
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

    @Override
    public String toString() {
        if (getVirtualFile() == null) {
            return super.toString();
        }
        return "MultiMarkdownFile : " + getVirtualFile().toString();
    }
}
