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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MultiMarkdownPlugin implements ApplicationComponent {
    private static final Logger logger = org.apache.log4j.Logger.getLogger("com.vladsch.idea.multimarkdown");
    private MultiMarkdownGlobalSettingsListener globalSettingsListener;

    private Project project;
    private PluginClassLoader myClassLoader;

    private String urlDefaultFxCss;
    private String urlDarculaFxCss;
    private String urlCustomFxCss;
    private String urlCustomFont;
    private File fileCustomFxCss;

    public String getUrlDefaultFxCss() {
        return urlDefaultFxCss;
    }

    public String getUrlDarculaFxCss() {
        return urlDarculaFxCss;
    }

    public String getUrlCustomFont() {
        return urlCustomFont;
    }

    public String getUrlCustomFxCss() {
        return urlCustomFxCss;
    }

    public MultiMarkdownPlugin() {
        myClassLoader = null;
        final MultiMarkdownGlobalSettings settings = MultiMarkdownGlobalSettings.getInstance();
        getClassLoader();

        // now we make temp copies of the fx css files with the font reference replaced to tasklists.png, which is really a .ttf file
        // but for some reason WebView won't accept a copy of the file to the temp dir and IDEA won't include .ttf files in resources
        // directory, but will if the file is .png, ok we live with that, WebView takes the file with a .png extension and correctly figures
        // out that it is .ttf
        Boolean useOldPreview = settings.useOldPreview.getValue();

        // Listen to settings changes
        urlCustomFont = null;
        fileCustomFxCss = null;
        urlCustomFxCss = null;
        urlDefaultFxCss = null;
        urlDarculaFxCss = null;
        globalSettingsListener = null;

        if (!useOldPreview) {
            urlCustomFont = createCustomFontUrl(useOldPreview);
            urlDefaultFxCss = createTempCopy(useOldPreview, MultiMarkdownPlugin.class.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_STYLESHEET_LIGHT), "default-fx.css");
            urlDarculaFxCss = createTempCopy(useOldPreview, MultiMarkdownPlugin.class.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_STYLESHEET_DARK), "darcula-fx.css");

            try {
                fileCustomFxCss = createTempCopy(useOldPreview, settings.customFxCss.getValue(), "custom-fx.css");
                urlCustomFxCss = fileCustomFxCss.toURI().toURL().toExternalForm();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fileCustomFxCss != null) {
                MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
                    public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
                        try {
                            updateTempCopy(fileCustomFxCss, newSettings.customFxCss.getValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    protected String createCustomFontUrl(boolean useOldPreview) {
        if (!useOldPreview) {
            String fontPath = getClass().getResource("/com/vladsch/idea/multimarkdown/taskitems.ttf").toExternalForm();
            File file = new File(fontPath);

            if (file.exists()) {
                return fontPath;
            }
        }
        return null;
    }

    protected String createTempCopy(boolean useOldPreview, URL resourceFile, String suffix) {
        if (!useOldPreview && urlCustomFont != null) {
            try {
                File cssTempFile = createTempCopy(useOldPreview, Resources.toString(resourceFile, Charsets.UTF_8), suffix);
                return cssTempFile.toURI().toURL().toExternalForm();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resourceFile.toExternalForm();
    }

    protected File createTempCopy(boolean useOldPreview, String cssText, String suffix) throws IOException {
        File cssTempFile = File.createTempFile("multimarkdown", suffix);
        cssTempFile.deleteOnExit();
        updateTempCopy(cssTempFile, cssWithCustomFont(cssText));
        return cssTempFile;
    }

    protected void updateTempCopy(File cssTempFile, String cssText) throws IOException {
        FileWriter cssFileWriter = new FileWriter(cssTempFile);
        cssFileWriter.write(cssWithCustomFont(cssText));
        cssFileWriter.close();
    }

    public String cssWithCustomFont(String cssWithFontRef) {
        if (urlCustomFont != null) {
            return cssWithFontRef.replace("'taskitems.ttf'", "'" + urlCustomFont + "'");
        }
        return cssWithFontRef;
    }

    public PluginClassLoader getClassLoader() {
        if (myClassLoader == null) {
            myClassLoader = (PluginClassLoader) getClass().getClassLoader();
            String javaHome = System.getProperties().getProperty("java.home");
            String javaVersion = System.getProperties().getProperty("java.version");
            String libDir = FileUtil.join(javaHome, "lib", "ext");
            String fileName = FileUtil.join(libDir, "jfxrt.jar");
            File file = new File(fileName);
            if (!file.exists()) {
                logger.warn("JavaFX library jfxrt.jar not found in the current jre: " + javaHome + ", version " + javaVersion);
                logger.warn("MultiMarkdown HTML Preview will use a more limited implementation.");
                MultiMarkdownGlobalSettings.getInstance().useOldPreview.setValue(true);
            } else if (!file.exists() || "1.8.0_u51".compareTo(javaVersion.substring(0, Math.min(javaVersion.length(), "1.8.0_u51".length()))) < 0) {
                logger.warn("JavaFX library jfxrt.jar was found in the current jre: " + javaHome + ", but version " + javaVersion + " is not supported, max version is 1.8.0_u51");
                logger.warn("MultiMarkdown HTML Preview will use a more limited implementation.");
                MultiMarkdownGlobalSettings.getInstance().useOldPreview.setValue(true);
            } else {
                logger.info("JavaFX library jfxrt.jar found in " + libDir + " the current jre: " + javaHome + ", version " + javaVersion);
                ArrayList<String> libs = new ArrayList<String>(1);
                libs.add(libDir);
                myClassLoader.addLibDirectories(libs);
            }
        }
        return myClassLoader;
    }

    public void initComponent() {
        // empty
    }

    public void disposeComponent() {
        // empty
        if (globalSettingsListener != null) {
            MultiMarkdownGlobalSettings.getInstance().removeListener(globalSettingsListener);
        }
    }

    @NotNull
    public String getComponentName() {
        return this.getClass().getName();
    }

    public static MultiMarkdownPlugin getInstance() {
        return ApplicationManager.getApplication().getComponent(MultiMarkdownPlugin.class);
    }
}
