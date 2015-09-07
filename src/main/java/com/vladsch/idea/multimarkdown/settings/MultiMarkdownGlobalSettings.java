/*
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
package com.vladsch.idea.multimarkdown.settings;

import com.google.common.io.Resources;
import com.intellij.ide.ui.UISettings;
import com.intellij.ide.ui.UISettingsListener;
import com.intellij.notification.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.codec.Charsets;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.Extensions;

import java.io.IOException;

@State(
        name = "MultiMarkdownSettings",
        storages = @Storage(id = "other", file = "$APP_CONFIG$/multimarkdown.xml")
)
public class MultiMarkdownGlobalSettings implements PersistentStateComponent<Element>, Disposable {

    final public static int HTML_THEME_DEFAULT = 0;
    final public static int HTML_THEME_DARCULA = 1;
    final public static int HTML_THEME_UI = 2;   // follow the Appearance setting of the application UI

    @NonNls
    public static final String PREVIEW_STYLESHEET_LIGHT = "/com/vladsch/idea/multimarkdown/default.css";

    public static final String PREVIEW_STYLESHEET_DARK = "/com/vladsch/idea/multimarkdown/darcula.css";

    public static final String NOTIFICATION_GROUP_ISSUES = "MultiMarkdown Alerts";

    /** A set of listeners to this object state changes. */
    @Override public void dispose() {

    }

    protected final SettingsNotifierImpl<MultiMarkdownGlobalSettings> notifier = new SettingsNotifierImpl<MultiMarkdownGlobalSettings>(this);
    protected final Settings settings = new Settings(notifier);

    public MultiMarkdownGlobalSettings() {
        // register to settings change on behalf of our listeners. If the UI changes and htmlTheme is Application UI then notify them
        UISettings.getInstance().addUISettingsListener(new UISettingsListener() {
            @Override public void uiSettingsChanged(UISettings source) {
                if (htmlTheme.getValue() == HTML_THEME_UI) {
                    notifier.notifyListeners();
                }
            }
        }, this);
    }

    public static MultiMarkdownGlobalSettings getInstance() {
        return ServiceManager.getService(MultiMarkdownGlobalSettings.class);
    }

    // these self-add to the list of settings
    final public Settings.BooleanSetting abbreviations = settings.BooleanSetting(false, "abbreviations", Extensions.ABBREVIATIONS);
    final public Settings.BooleanSetting anchorLinks = settings.BooleanSetting(true, "anchorLinks", Extensions.ANCHORLINKS);
    final public Settings.BooleanSetting autoLinks = settings.BooleanSetting(true, "autoLinks", Extensions.AUTOLINKS);
    final public Settings.BooleanSetting definitions = settings.BooleanSetting(true, "definitions", Extensions.DEFINITIONS);
    final public Settings.BooleanSetting enableTrimSpaces = settings.BooleanSetting(false, "enableTrimSpaces", 0);
    final public Settings.BooleanSetting fencedCodeBlocks = settings.BooleanSetting(true, "fencedCodeBlocks", Extensions.FENCED_CODE_BLOCKS);
    final public Settings.BooleanSetting forceListPara = settings.BooleanSetting(false, "forceListPara", Extensions.FORCELISTITEMPARA);
    final public Settings.BooleanSetting hardWraps = settings.BooleanSetting(true, "hardWraps", Extensions.HARDWRAPS);
    final public Settings.BooleanSetting headerSpace = settings.BooleanSetting(false, "headerSpace", Extensions.ATXHEADERSPACE);
    final public Settings.BooleanSetting quotes = settings.BooleanSetting(false, "quotes", Extensions.QUOTES);
    final public Settings.BooleanSetting relaxedHRules = settings.BooleanSetting(true, "relaxedHRules", Extensions.RELAXEDHRULES);
    final public Settings.BooleanSetting showHtmlText = settings.BooleanSetting(true, "showHtmlText", 0);
    final public Settings.BooleanSetting showHtmlTextAsModified = settings.BooleanSetting(false, "showHtmlTextAsModified", 0);
    final public Settings.BooleanSetting smarts = settings.BooleanSetting(false, "smarts", Extensions.SMARTS);
    final public Settings.BooleanSetting strikethrough = settings.BooleanSetting(true, "strikethrough", Extensions.STRIKETHROUGH);
    final public Settings.BooleanSetting suppressHTMLBlocks = settings.BooleanSetting(false, "suppressHTMLBlocks", Extensions.SUPPRESS_HTML_BLOCKS);
    final public Settings.BooleanSetting suppressInlineHTML = settings.BooleanSetting(false, "suppressInlineHTML", Extensions.SUPPRESS_INLINE_HTML);
    final public Settings.BooleanSetting tables = settings.BooleanSetting(true, "tables", Extensions.TABLES);
    final public Settings.BooleanSetting taskLists = settings.BooleanSetting(true, "taskLists", Extensions.TASKLISTITEMS);
    final public Settings.BooleanSetting wikiLinks = settings.BooleanSetting(true, "wikiLinks", Extensions.WIKILINKS);
    final public Settings.BooleanSetting todoComments = settings.BooleanSetting(false, "todoComments", 0);
    final public Settings.BooleanSetting useCustomCss = settings.BooleanSetting(false, "useCustomCss", 0);
    final public Settings.IntegerSetting htmlTheme = settings.IntegerSetting(HTML_THEME_UI, "htmlTheme");
    final public Settings.IntegerSetting maxImgWidth = settings.IntegerSetting(900, "maxImgWidth");
    final public Settings.IntegerSetting parsingTimeout = settings.IntegerSetting(10000, "parsingTimeout");
    final public Settings.IntegerSetting updateDelay = settings.IntegerSetting(1000, "updateDelay");
    final public Settings.StringSetting customCss = settings.StringSetting("", "customCss");
    final public Settings.ElementSetting customCssEditorState = settings.ElementSetting(null, "customCssEditorState");
    final public Settings.BooleanSetting wasShownDarkBug = settings.BooleanSetting(false, "wasShownDarkBug", 0);

    public Element getState() {
        Element multiMarkdownSettings = settings.getState("MultiMarkdownSettings");
        return multiMarkdownSettings;
    }

    public void loadState(@NotNull Element element) {
        settings.loadState(element);
    }

    public boolean isDarkHtmlPreview(int htmlTheme) {
        return (htmlTheme == HTML_THEME_DARCULA
                || htmlTheme == HTML_THEME_UI && UIUtil.isUnderDarcula());
    }

    public boolean isDarkUITheme() {
        return UIUtil.isUnderDarcula();
    }

    public @NotNull String getCssFilePath(int htmlTheme) {
        return isDarkHtmlPreview(htmlTheme) ? PREVIEW_STYLESHEET_DARK : PREVIEW_STYLESHEET_LIGHT;
    }

    public @NotNull java.net.URL getCssFileURL(int htmlTheme) {
        return MultiMarkdownGlobalSettings.class.getResource(getCssFilePath(htmlTheme));
    }

    public @NotNull String getCssFileText(int htmlTheme) {
        String htmlText = "";
        try {
            htmlText = Resources.toString(getCssFileURL(htmlTheme), Charsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return htmlText;
    }

    public @NotNull String getCssFilePath() {
        return getCssFilePath(htmlTheme.getValue());
    }

    public @NotNull java.net.URL getCssFileURL() {
        return getCssFileURL(htmlTheme.getValue());
    }

    public @NotNull String getCssFileText() {
        return getCssFileText(htmlTheme.getValue());
    }

    public @NotNull String getCssText() {
        return useCustomCss() ? customCss.getValue() : getCssFileText(htmlTheme.getValue());
    }

    public boolean useCustomCss() {
        return useCustomCss.getValue() && customCss.getValue().trim().length() != 0;
    }

    public int getExtensionsValue() {
        return settings.getExtensionsValue() | Extensions.EXTANCHORLINKS;
    }

    public void addListener(@NotNull final SettingsListener<MultiMarkdownGlobalSettings> listener) {
        notifier.addListener(listener);
    }

    public void removeListener(@NotNull final SettingsListener<MultiMarkdownGlobalSettings> listener) {
        notifier.removeListener(listener);
    }

    public void startGroupNotifications() {
        notifier.startGroupNotifications();
    }

    public void endGroupNotifications() {
        notifier.endGroupNotifications();
    }
    public void startSuspendNotifications() {
        notifier.startSuspendNotifications();
    }

    public void endSuspendNotifications() {
        notifier.endSuspendNotifications();
    }
}
