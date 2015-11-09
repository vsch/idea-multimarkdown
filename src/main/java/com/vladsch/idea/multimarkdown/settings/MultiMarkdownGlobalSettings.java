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
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.ui.UIUtil;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import org.apache.commons.codec.Charsets;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.Extensions;

import java.io.IOException;
import java.net.URL;

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
    public static final String PREVIEW_FX_STYLESHEET_LAYOUT = "/com/vladsch/idea/multimarkdown/layout-fx.css";
    public static final String PREVIEW_FX_STYLESHEET_LIGHT = "/com/vladsch/idea/multimarkdown/default-fx.css";
    public static final String PREVIEW_FX_STYLESHEET_DARK = "/com/vladsch/idea/multimarkdown/darcula-fx.css";
    public static final String PREVIEW_FX_HLJS_STYLESHEET_LIGHT = "/com/vladsch/idea/multimarkdown/hljs-default.css";
    public static final String PREVIEW_FX_HLJS_STYLESHEET_DARK = "/com/vladsch/idea/multimarkdown/hljs-darcula.css";
    public static final String PREVIEW_FX_HIGHLIGHT_JS = "/com/vladsch/idea/multimarkdown/highlight.pack.js";

    public static final String NOTIFICATION_GROUP_ISSUES = "MultiMarkdown Alerts";

    /**
     * A set of listeners to this object state changes.
     */
    @Override
    public void dispose() {

    }

    protected final SettingsNotifierImpl<MultiMarkdownGlobalSettings> notifier = new SettingsNotifierImpl<MultiMarkdownGlobalSettings>(this);
    protected final Settings settings = new Settings(notifier);

    public MultiMarkdownGlobalSettings() {
        // register to settings change on behalf of our listeners. If the UI changes and htmlTheme is Application UI then notify them
        UISettings.getInstance().addUISettingsListener(new UISettingsListener() {
            @Override
            public void uiSettingsChanged(UISettings source) {
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
    final public Settings.BooleanSetting enableFirebug = settings.BooleanSetting(false, "enableFirebug", 0);
    final public Settings.BooleanSetting useHighlightJs = settings.BooleanSetting(true, "useHighlightJs", 0);
    final public Settings.BooleanSetting includesHljsCss = settings.BooleanSetting(false, "includesHljsCss", 0);
    final public Settings.BooleanSetting includesLayoutCss = settings.BooleanSetting(false, "includesLayoutCss", 0);
    final public Settings.BooleanSetting includesColorsCss = settings.BooleanSetting(true, "includesColorsCss", 0);
    final public Settings.BooleanSetting footnotes = settings.BooleanSetting(false, "footnotes", Extensions.FOOTNOTES);
    final public Settings.IntegerSetting htmlTheme = settings.IntegerSetting(HTML_THEME_UI, "htmlTheme");
    final public Settings.IntegerSetting maxImgWidth = settings.IntegerSetting(900, "maxImgWidth");
    final public Settings.IntegerSetting parsingTimeout = settings.IntegerSetting(10000, "parsingTimeout");
    final public Settings.DoubleSetting pageZoom = settings.DoubleSetting(1.0, "pageZoom");
    final public Settings.IntegerSetting updateDelay = settings.IntegerSetting(1000, "updateDelay");
    final public Settings.IntegerSetting tabbedPaneIndex = settings.IntegerSetting(0, "tabbedPaneIndex");
    final public Settings.StringSetting customCss = settings.StringSetting("", "customCss");
    final public Settings.StringSetting customFxCss = settings.StringSetting("", "customFxCss");
    final public Settings.ElementSetting customCssEditorState = settings.ElementSetting(null, "customCssEditorState");
    final public Settings.ElementSetting customFxCssEditorState = settings.ElementSetting(null, "customFxCssEditorState");
    final public Settings.BooleanSetting wasShownDarkBug = settings.BooleanSetting(false, "wasShownDarkBug", 0);
    final public Settings.BooleanSetting useOldPreview = settings.BooleanSetting(false, "useOldPreview", 0);

    // TODO: add this option to pegdown
    final public Settings.BooleanSetting githubWikiLinks = settings.BooleanSetting(true, "githubWikiLinks", 0);

    // when loading of classes fails for some earlier builds these are used to store the build number to avoid retrying until the build changes
    final public Settings.FailedBuildSetting scratchFileServiceFailed = settings.FailedBuildSetting("", "scratchFileServiceFailed");
    final public Settings.FailedBuildSetting lightParserFailedBuild = settings.FailedBuildSetting("", "lightParserFailedBuild");
    final public Settings.FailedBuildSetting fxPreviewFailedBuild = settings.FailedBuildSetting("", "fxPreviewFailedBuild", true);
    //final public Settings.FailedBuildSetting fxPlatformFailedBuild = settings.FailedBuildSetting("", "fxPlatformFailedBuild", true);

    public boolean isFxHtmlPreview() {
        return isFxHtmlPreview;
    }

    public void setIsFxHtmlPreview(boolean isFxHtmlPreview) {
        MultiMarkdownGlobalSettings.isFxHtmlPreview = isFxHtmlPreview;
    }

    static boolean isFxHtmlPreview = false;

    public Element getState() {
        // vsch: hardwire github wiki links
        githubWikiLinks.setValue(true);
        return settings.getState("MultiMarkdownSettings");
    }

    public void loadState(@NotNull Element element) {
        startGroupNotifications();
        settings.loadState(element);
        // vsch: hardwire github wiki links
        githubWikiLinks.setValue(true);
        endGroupNotifications();
    }

    public boolean isDarkHtmlPreview(int htmlTheme) {
        return (htmlTheme == HTML_THEME_DARCULA
                || htmlTheme == HTML_THEME_UI && UIUtil.isUnderDarcula());
    }

    public boolean isDarkHtmlPreview() {
        return isDarkHtmlPreview(htmlTheme.getValue());
    }

    public boolean isDarkUITheme() {
        return UIUtil.isUnderDarcula();
    }

    public
    @NotNull
    String getCustomCssExternalForm(boolean isFxHtmlPreview) {
        String url = MultiMarkdownPlugin.getInstance().getUrlCustomFxCss();
        if (url != null) return url;
        return getColorsCssExternalForm(isFxHtmlPreview);
    }

    @NotNull
    public String getColorsCssExternalForm(boolean isFxHtmlPreview) {
        return isDarkHtmlPreview() ? MultiMarkdownPlugin.getInstance().getUrlDarculaFxCss() : MultiMarkdownPlugin.getInstance().getUrlDefaultFxCss();
    }

    @NotNull
    public String getHljsCssExternalForm(boolean isFxHtmlPreview) {
        return isDarkHtmlPreview() ? MultiMarkdownPlugin.getInstance().getUrlHljsDarculaFxCss() : MultiMarkdownPlugin.getInstance().getUrlHljsDefaultFxCss();
    }

    @NotNull
    public String getLayoutCssExternalForm(boolean isFxHtmlPreview) {
        return MultiMarkdownPlugin.getInstance().getUrlLayoutFxCss();
    }

    @NotNull
    public String getHighlighJsExternalForm(boolean isFxHtmlPreview) {
        return MultiMarkdownPlugin.getInstance().getUrlHighlightJs();
    }

    @Nullable
    public String getUrlCustomFont() {
        return MultiMarkdownPlugin.getInstance().getUrlCustomFont();
    }

    @NotNull
    public String getCssFilePath(int htmlTheme, boolean isFxHtmlPreview) {
        if (isFxHtmlPreview) {
            return isDarkHtmlPreview(htmlTheme) ? PREVIEW_FX_STYLESHEET_DARK : PREVIEW_FX_STYLESHEET_LIGHT;
        } else {
            return isDarkHtmlPreview(htmlTheme) ? PREVIEW_STYLESHEET_DARK : PREVIEW_STYLESHEET_LIGHT;
        }
    }

    @Nullable
    public String getLayoutCssFilePath(boolean isFxHtmlPreview) {
        if (isFxHtmlPreview) {
            return PREVIEW_FX_STYLESHEET_LAYOUT;
        } else {
            return null;
        }
    }

    @Nullable
    public String getHljsCssFilePath(int htmlTheme, boolean isFxHtmlPreview) {
        if (isFxHtmlPreview) {
            return isDarkHtmlPreview(htmlTheme) ? PREVIEW_FX_HLJS_STYLESHEET_DARK : PREVIEW_FX_HLJS_STYLESHEET_LIGHT;
        } else {
            return null;
        }
    }

    @NotNull
    public java.net.URL getCssFileURL(int htmlTheme, boolean isFxHtmlPreview) {
        return MultiMarkdownGlobalSettings.class.getResource(getCssFilePath(htmlTheme, isFxHtmlPreview));
    }

    @Nullable
    public java.net.URL getLayoutCssFileURL() {
        String layoutCssFilePath = getLayoutCssFilePath(isFxHtmlPreview);
        return layoutCssFilePath == null ? null : MultiMarkdownGlobalSettings.class.getResource(layoutCssFilePath);
    }

    @Nullable
    public java.net.URL getHljsCssFileURL(int htmlTheme, boolean isFxHtmlPreview) {
        String hljsCssFilePath = getHljsCssFilePath(htmlTheme, isFxHtmlPreview);
        return hljsCssFilePath == null ? null : MultiMarkdownGlobalSettings.class.getResource(hljsCssFilePath);
    }

    //public @NotNull java.net.URL getFirebugLiteFileURL() throws MalformedURLException {
    //    return MultiMarkdownGlobalSettings.class.getResource("/src/firebug-lite.js");
    //}

    @NotNull
    public String getCssFileText(int htmlTheme, boolean isFxHtmlPreview) {
        String htmlText = "";
        try {
            htmlText = Resources.toString(getCssFileURL(htmlTheme, isFxHtmlPreview), Charsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return htmlText;
    }

    @NotNull
    public String getLayoutCssFileText() {
        String htmlText = "";
        try {
            URL layoutCssFileURL = getLayoutCssFileURL();
            if (layoutCssFileURL != null) htmlText = Resources.toString(layoutCssFileURL, Charsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return htmlText;
    }

    @NotNull
    public String getHljsCssFileText(int htmlTheme, boolean isFxHtmlPreview) {
        String htmlText = "";
        try {
            URL hljsCssFileURL = getHljsCssFileURL(htmlTheme, isFxHtmlPreview);
            if (hljsCssFileURL != null) {
                htmlText = Resources.toString(hljsCssFileURL, Charsets.UTF_8);
            } else {
                htmlText = "";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return htmlText;
    }

    @NotNull
    public String getCssFilePath(boolean isFxHtmlPreview) {
        return getCssFilePath(htmlTheme.getValue(), isFxHtmlPreview);
    }

    @NotNull
    public java.net.URL getCssFileURL(boolean isFxHtmlPreview) {
        return getCssFileURL(htmlTheme.getValue(), isFxHtmlPreview);
    }

    @NotNull
    public String getCssFileText(boolean isFxHtmlPreview) {
        return getCssFileText(htmlTheme.getValue(), isFxHtmlPreview);
    }

    @NotNull
    public String getCssText(boolean isFxHtmlPreview) {
        return useCustomCss(isFxHtmlPreview) ? (isFxHtmlPreview ? customFxCss.getValue() : customCss.getValue()) : getCssFileText(htmlTheme.getValue(), isFxHtmlPreview);
    }

    public boolean useCustomCss(boolean isFxHtmlPreview) {
        return useCustomCss.getValue() && (isFxHtmlPreview ? customFxCss.getValue() : customCss.getValue()).trim().length() != 0;
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
