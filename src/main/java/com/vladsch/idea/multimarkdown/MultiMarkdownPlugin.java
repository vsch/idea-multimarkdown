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
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.xml.util.XmlStringUtil;
import com.vladsch.idea.multimarkdown.editor.MultiMarkdownPathResolver;
import com.vladsch.idea.multimarkdown.license.LicenseAgent;
import com.vladsch.idea.multimarkdown.license.LicenseRequest;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettingsListener;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.HyperlinkEvent;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class MultiMarkdownPlugin implements ApplicationComponent {
    private static final Logger logger = org.apache.log4j.Logger.getLogger("com.vladsch.idea.multimarkdown");
    private MultiMarkdownGlobalSettingsListener globalSettingsListener;
    private static int license_features;

    @NotNull
    public static String getProductName() {
        return "idea-multimarkdown";
    }

    @NotNull
    public static String getProductVersion() {
        PluginId pluginId = PluginId.findId("com.vladsch.idea.multimarkdown");
        if (pluginId != null) {
            IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
            if (pluginDescriptor != null) {
                return pluginDescriptor.getVersion();
            }
        }
        return "1.2.x";
    }

    public static boolean areAllLicensed(int licenseFeatures) {
        return (license_features & licenseFeatures) == licenseFeatures;
    }

    public static boolean areSomeLicensed(int licenseFeatures) {
        return (license_features & licenseFeatures) != 0;
    }

    public static boolean isLicensed() {
        return license_features != 0;
    }

    private PluginClassLoader myClassLoader;

    private String urlLayoutFxCss;
    private String urlDefaultFxCss;
    private String urlDarculaFxCss;
    private String urlHljsDefaultFxCss;
    private String urlHljsDarculaFxCss;
    private String urlHighlightJs;
    private String urlCustomFxCss;
    private String urlCustomFont;
    private File fileCustomFxCss;
    private String tempDirPath;
    final private LicenseAgent agent;

    public String getUrlLayoutFxCss() {
        return urlLayoutFxCss;
    }

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

    public String getUrlHljsDefaultFxCss() {
        return urlHljsDefaultFxCss;
    }

    public String getUrlHljsDarculaFxCss() {
        return urlHljsDarculaFxCss;
    }

    public String getUrlHighlightJs() {
        return urlHighlightJs;
    }

    public MultiMarkdownPlugin() {
        //BasicConfigurator.configure();
        //logger.addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
        ConsoleAppender appender = new ConsoleAppender(new PatternLayout("%p %c %x - %m%n"));
        //Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        //while (appenders.hasMoreElements()) {
        //    Appender app = (Appender) appenders.nextElement();
        //    String name = app.getName();
        //}
        logger.addAppender(appender);
        logger.setAdditivity(false);
        logger.setLevel(Level.INFO);

        agent = new LicenseAgent();

        // turn off lcd rendering, will use gray
        //System.setProperty("prism.lcdtext", "false");
        myClassLoader = null;
        final MultiMarkdownGlobalSettings settings = MultiMarkdownGlobalSettings.getInstance();
        getClassLoader();

        license_features = 0;

        // get the tmp directory location
        try {
            File tmpFile = File.createTempFile("multimarkdown_font_file", ".ttf");
            String path = tmpFile.getAbsolutePath();
            tempDirPath = path.substring(0, path.lastIndexOf("multimarkdown_font_file"));
            //noinspection ResultOfMethodCallIgnored
            tmpFile.delete();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        // now we make temp copies of the fx css files with the font reference replaced to tasklists.png, which is really a .ttf file
        // but for some reason WebView won't accept a copy of the file to the temp dir and IDEA won't include .ttf files in resources
        // directory, but will if the file is .png, ok we live with that, WebView takes the file with a .png extension and correctly figures
        // out that it is .ttf

        // Listen to settings changes
        urlCustomFont = null;
        fileCustomFxCss = null;
        urlLayoutFxCss = null;
        urlCustomFxCss = null;
        urlDefaultFxCss = null;
        urlDarculaFxCss = null;
        urlHljsDefaultFxCss = null;
        urlHljsDarculaFxCss = null;
        urlHighlightJs = null;
        globalSettingsListener = null;

        urlCustomFont = createCustomFontUrl();
        urlLayoutFxCss = createTempCopy(MultiMarkdownPlugin.class.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_STYLESHEET_LAYOUT), "layout-fx.css");
        urlDefaultFxCss = createTempCopy(MultiMarkdownPlugin.class.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_STYLESHEET_LIGHT), "default-fx.css");
        urlDarculaFxCss = createTempCopy(MultiMarkdownPlugin.class.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_STYLESHEET_DARK), "darcula-fx.css");
        urlHljsDefaultFxCss = createTempCopy(MultiMarkdownPlugin.class.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_HLJS_STYLESHEET_LIGHT), "hljs-default-fx.css");
        urlHljsDarculaFxCss = createTempCopy(MultiMarkdownPlugin.class.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_HLJS_STYLESHEET_DARK), "hljs-darcula-fx.css");
        urlHighlightJs = createTempCopy(MultiMarkdownPlugin.class.getResource(MultiMarkdownGlobalSettings.PREVIEW_FX_HIGHLIGHT_JS), "highlight.pack.js");

        try {
            fileCustomFxCss = createTempCopy(settings.customFxCss.getValue(), "custom-fx.css");
            urlCustomFxCss = fileCustomFxCss.toURI().toURL().toExternalForm();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
                if (fileCustomFxCss != null) {
                    try {
                        // 1.8u60 caches the css by name, we have to change the name or no refresh is done
                        //updateTempCopy(fileCustomFxCss, newSettings.customFxCss.getValue());
                        //noinspection ResultOfMethodCallIgnored
                        fileCustomFxCss.delete();
                        fileCustomFxCss = createTempCopy(settings.customFxCss.getValue(), "custom-fx.css");
                        urlCustomFxCss = fileCustomFxCss.toURI().toURL().toExternalForm();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // check license information
                loadLicenseActivation(false);
            }
        });
    }

    protected String createCustomFontUrl() {
        // create the freaking thing
        try {
            InputStream fontStream = getClass().getResourceAsStream("/com/vladsch/idea/multimarkdown/taskitems.ttf");
            File tempFontFile = new File(tempDirPath + "multimarkdown_taskitems.ttf");
            //tempFontFile.deleteOnExit();
            logger.info("creating temp font file: " + tempFontFile.getAbsolutePath());

            FileOutputStream fileOutputStream = new FileOutputStream(tempFontFile);
            byte[] buffer = new byte[32768];
            int length;
            while ((length = fontStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.close();
            fontStream.close();

            return tempFontFile.toURI().toURL().toExternalForm();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String createTempCopy(URL resourceFile, String suffix) {
        if (urlCustomFont != null) {
            try {
                File cssTempFile = createTempCopy(Resources.toString(resourceFile, Charsets.UTF_8), suffix);
                return cssTempFile.toURI().toURL().toExternalForm();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resourceFile.toExternalForm();
    }

    protected File createTempCopy(String cssText, String suffix) throws IOException {
        File cssTempFile;
        if ("custom-fx.css".equals(suffix)) {
            cssTempFile = File.createTempFile("multimarkdown", suffix);
            cssTempFile.deleteOnExit();
        } else {
            cssTempFile = new File(tempDirPath + "multimarkdown_" + suffix);
        }
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
            PluginClassLoader pluginClassLoader = (PluginClassLoader) getClass().getClassLoader();
            String javaHome = System.getProperty("java.home");
            String javaVersion = System.getProperty("java.version");
            String libDir = FileUtil.join(javaHome, "lib");
            String libExtDir = FileUtil.join(libDir, "ext");
            String fileName = FileUtil.join(libExtDir, "jfxrt.jar");
            File file = new File(fileName);
            if (!file.exists()) {
                //logger.warn("JavaFX library jfxrt.jar not found in the provided jre: " + javaHome + ", version " + javaVersion);
                logger.info("JavaFX library jfxrt.jar not found in the provided jre: " + javaHome + ", version " + javaVersion);
                logger.info("MultiMarkdown HTML Preview will use JEditorPane with HTMLEditorKit for rendering instead of JavaFX WebView.");
                MultiMarkdownGlobalSettings.getInstance().useOldPreview.setValue(true);
                //} else if (!file.exists() || "1.8.0_u51".compareTo(javaVersion.substring(0, Math.min(javaVersion.length(), "1.8.0_u51".length()))) < 0) {
                //    logger.warn("JavaFX library jfxrt.jar was found in the current jre: " + javaHome + ", but version " + javaVersion + " is not supported, max version is 1.8.0_u51");
                //    logger.warn("MultiMarkdown HTML Preview will use a more limited implementation.");
                //    MultiMarkdownGlobalSettings.getInstance().useOldPreview.setValue(true);
            } else {
                logger.info("JavaFX library jfxrt.jar found in " + libDir + " the current jre: " + javaHome + ", version " + javaVersion);
                ArrayList<String> libs = new ArrayList<String>(1);
                libs.add(libExtDir);
                pluginClassLoader.addLibDirectories(libs);
            }
            myClassLoader = pluginClassLoader;
        }
        return myClassLoader;
    }

    public void initComponent() {
        loadLicenseActivation(true);
    }

    public void loadLicenseActivation(boolean init) {
        MultiMarkdownGlobalSettings globalSettings = MultiMarkdownGlobalSettings.getInstance();
        try {
            globalSettings.startSuspendNotifications();
            String licenseCode = globalSettings.licenseCode.getValue();
            String activationCode = globalSettings.activationCode.getValue();

            if (!agent.licenseCode().equals(licenseCode.trim())) {
                activationCode = "";
            }

            agent.setLicenseActivationCodes(licenseCode, activationCode);
            if (agent.isValidLicense()) {
                if (!agent.isValidActivation()) {
                    agent.setActivationCode(null);

                    // request activation
                    LicenseRequest request = new LicenseRequest(getProductName(), getProductVersion());
                    request.license_code = licenseCode;
                    if (!agent.getLicenseCode(request)) {
                        if (agent.isRemoveLicense()) {
                            agent.setLicenseActivationCodes(null, null);
                        }
                    }
                }
            }

            // update both to the agent's values
            globalSettings.licenseCode.setValue(agent.licenseCode());
            globalSettings.activationCode.setValue(agent.activationCode());
        } finally {
            globalSettings.endSuspendNotifications();
        }

        initLicense(init);
    }

    public static void notifyLicensedUpdate(@Nullable Project project, final boolean showOnce) {
        final MultiMarkdownGlobalSettings settings = MultiMarkdownGlobalSettings.getInstance();

        if (!showOnce || !settings.wasShownLicensedAvailable.getValue()) {
            // notify the user that a new licensed version is available
            if (showOnce) settings.wasShownLicensedAvailable.setValue(true);
            final ApplicationEx app = ApplicationManagerEx.getApplicationEx();

            NotificationGroup issueNotificationGroup = new NotificationGroup(MultiMarkdownGlobalSettings.NOTIFICATION_GROUP_UPDATE, NotificationDisplayType.STICKY_BALLOON, true, null);

            final String href = (showOnce ? "http://vladsch.com" : "http://vladsch.dev") + "/product/multimarkdown/specials";
            String title = MultiMarkdownBundle.message("plugin.updated.notification.title", getProductName());
            final String action = MultiMarkdownBundle.message("plugin.updated.notification.action");
            String message = MultiMarkdownBundle.message("plugin.updated.notification.message", action, href);
            NotificationListener listener = new NotificationListener() {
                @Override
                public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
                    //notification.expire();
                    MultiMarkdownPathResolver.openLink(href);
                }
            };

            issueNotificationGroup.createNotification(title, XmlStringUtil.wrapInHtml(message), NotificationType.INFORMATION, listener).notify(project);
        }
    }

    public void initLicense(boolean init) {// load license information
        boolean showOnce = true;

        if (agent.isValidLicense() && agent.isValidActivation()) {
            license_features = agent.getLicenseFeatures();
            if (init) {
                if (showOnce) MultiMarkdownGlobalSettings.getInstance().wasShownLicensedAvailable.setValue(true);
                else notifyLicensedUpdate(null, showOnce);
            }
        } else {
            license_features = 0;
            if (init) {
                notifyLicensedUpdate(null, showOnce);
            }
        }
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

    public Logger getLogger() {
        return logger;
    }

    // find markdown and wantWikiPages in the project
    public static
    @Nullable
    MultiMarkdownProjectComponent getProjectComponent(Project project) {
        return project.getComponent(MultiMarkdownProjectComponent.class);
    }

    public LicenseAgent getAgent() {
        return agent;
    }
}
