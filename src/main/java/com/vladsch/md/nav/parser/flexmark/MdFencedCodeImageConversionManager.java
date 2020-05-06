// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.messages.MessageBusConnection;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider;
import com.vladsch.md.nav.parser.api.MdFencedCodeImageConverter;
import com.vladsch.md.nav.parser.api.MdImageFencedCode;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.ProjectSettingsChangedListener;
import com.vladsch.plugin.util.LazyFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class MdFencedCodeImageConversionManager {
    final private static LazyFunction<Project, MdFencedCodeImageConversionManager> NULL = new LazyFunction<>(MdFencedCodeImageConversionManager::new);

    @NotNull
    public static MdFencedCodeImageConversionManager getInstance(@NotNull Project project) {
        if (project.isDefault()) {
            Project defaultProject = ProjectManager.getInstance().getDefaultProject();
            return NULL.getValue(defaultProject);
        } else {
            // DEPRECATED: added 2019.08, when available change to
            //   return project.getService(MdRenderingProfileManager.class);
            return ServiceManager.getService(project, MdFencedCodeImageConversionManager.class);
        }
    }

    @NotNull final private Project myProject;
    @NotNull final HashMap<String, LinkedHashMap<String, MdFencedCodeImageConverter>> infoVariantMap = new HashMap<>();

    public MdFencedCodeImageConversionManager(@NotNull Project project) {
        myProject = project;

//        // NOTE: if any global settings affect fenced code image rendering, then subscribe and clear        
//        MessageBusConnection applicationMessageBusConnection = ApplicationManager.getApplication().getMessageBus().connect(project);
//        applicationMessageBusConnection.subscribe(SettingsChangedListener.TOPIC, settings -> infoVariantMap.clear());

        MessageBusConnection projectBusConnection = project.getMessageBus().connect(project);
        projectBusConnection.subscribe(ProjectSettingsChangedListener.TOPIC, (project1, settings) -> infoVariantMap.clear());
    }

    @NotNull
    public Set<String> getConversionVariants(@NotNull String info, boolean includeAll, boolean onlyUrlConversions) {
        initializeConverterMap();

        LinkedHashMap<String, MdFencedCodeImageConverter> variants = infoVariantMap.get(info);
        if (variants != null) {
            variants = new LinkedHashMap<>(variants);
            if (!includeAll) {
                ArrayList<String> toRemove = new ArrayList<>();
                for (Map.Entry<String, MdFencedCodeImageConverter> entry : variants.entrySet()) {
                    if (!entry.getValue().hasUrlConversion(info, entry.getKey(), onlyUrlConversions)) {
                        toRemove.add(entry.getKey());
                    }
                }
                for (String variant : toRemove) {
                    variants.remove(variant);
                }
            }
        }

        return variants == null ? Collections.emptySet() : variants.keySet();
    }

    public boolean updateCssSettings(@NotNull MdRenderingProfile renderingProfile) {
        return updateCssSettings(renderingProfile, null);
    }
    
    public boolean updateCssSettings(@NotNull MdRenderingProfile renderingProfile, @Nullable Predicate<String> infoPredicate) {
        HashMap<HtmlScriptResourceProvider.Info, Boolean> scriptProviders = new HashMap<>();
        for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
            Map<HtmlScriptResourceProvider.Info, Boolean> requiredScriptsProviders = converter.getRequiredScriptsProviders(renderingProfile, infoPredicate);
            for (Map.Entry<HtmlScriptResourceProvider.Info, Boolean> entry : requiredScriptsProviders.entrySet()) {
                HtmlScriptResourceProvider.Info info = entry.getKey();
                boolean isNeeded = entry.getValue() != null && entry.getValue();
                // NOTE: add only if it is included or does not exist in the map. 
                //  Multiple converters can share and set required script
                //  and it will be turned off iff none mark it as required.
                if (isNeeded || !scriptProviders.containsKey(info)) {
                    scriptProviders.put(info, isNeeded);
                }
            }
        }

        boolean isModified = false;
        ArrayList<HtmlScriptResourceProvider.Info> scriptProvidersInfo = renderingProfile.getCssSettings().getHtmlScriptProvidersInfo();
        for (Map.Entry<HtmlScriptResourceProvider.Info, Boolean> entry : scriptProviders.entrySet()) {
            HtmlScriptResourceProvider.Info info = entry.getKey();
            boolean isNeeded = entry.getValue() != null && entry.getValue();
            if (isNeeded && !scriptProvidersInfo.contains(info)) {
                scriptProvidersInfo.add(info);
                isModified = true;
            } else if (!isNeeded && scriptProvidersInfo.contains(info)) {
                scriptProvidersInfo.remove(info);
                isModified = true;
            }
        }
        return isModified;
    }

    @NotNull
    public Set<String> getConversionDisplayTexts(@NotNull String info, boolean includeAll, boolean onlyUrlConversions) {
        initializeConverterMap();

        LinkedHashSet<String> variants = new LinkedHashSet<>();
        for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
            String[] infoStrings = converter.getInfoStrings();
            for (String infoString : infoStrings) {
                if (info.equals(infoString)) {
                    String[] conversionVariants = converter.getVariantDisplayTexts(infoString);
                    if (!includeAll) {
                        String[] conversionTypes = converter.getConversionVariants(infoString);
                        int iMax = conversionTypes.length;
                        for (int i = 0; i < iMax; i++) {
                            if (converter.hasUrlConversion(info, conversionTypes[i], onlyUrlConversions)) {
                                variants.add(conversionVariants[i]);
                            }
                        }
                    } else {
                        variants.addAll(Arrays.asList(conversionVariants));
                    }
                }
            }
        }

        return variants;
    }

    @NotNull
    public Set<String> getInfoStrings() {
        initializeConverterMap();

        return infoVariantMap.keySet();
    }

    @Nullable
    public MdFencedCodeImageConverter getImageConverter(@NotNull String info, @NotNull String conversionVariant) {
        initializeConverterMap();

        HashMap<String, MdFencedCodeImageConverter> variantMap = infoVariantMap.get(info);
        if (variantMap != null) {
            return variantMap.get(conversionVariant);
        }

        return null;
    }

    @NotNull
    public List<MdFencedCodeImageConverter> getImageConverters(@NotNull String info) {
        initializeConverterMap();

        HashMap<String, MdFencedCodeImageConverter> variantMap = infoVariantMap.get(info);
        if (variantMap != null) {
            return new ArrayList<>(variantMap.values());
        }

        return Collections.emptyList();
    }

    @Nullable
    public String getConversionVariantDisplayText(@NotNull String info, @NotNull String conversionVariant) {
        initializeConverterMap();

        HashMap<String, MdFencedCodeImageConverter> variantMap = infoVariantMap.get(info);
        if (variantMap != null) {
            MdFencedCodeImageConverter converter = variantMap.get(conversionVariant);
            if (converter != null) {
                String[] variants = converter.getConversionVariants(info);
                String[] displayTexts = converter.getVariantDisplayTexts(info);
                assert variants.length == displayTexts.length : info + ": " + "Number of variants=" + variants.length + " != number of display strings=" + displayTexts.length;
                int iMax = variants.length;
                for (int i = 0; i < iMax; i++) {
                    if (conversionVariant.equals(variants[i])) return displayTexts[i];
                }
            } else {
                int tmp = 0;
            }
        }

        return null;
    }

    @Nullable
    public MdFencedCodeImageConverter getImageToFencedCodeConverter(@NotNull String url, boolean isMultiline, @Nullable Attributes attributes) {
        for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
            if (converter.canConvertFromImageUrl(url, isMultiline, attributes)) {
                return converter;
            }
        }
        return null;
    }

    @Nullable
    public String getConversionVariantDescription(@NotNull String info, @NotNull String conversionVariant) {
        initializeConverterMap();

        HashMap<String, MdFencedCodeImageConverter> variantMap = infoVariantMap.get(info);
        if (variantMap != null) {
            MdFencedCodeImageConverter converter = variantMap.get(conversionVariant);
            if (converter != null) {
                String[] variants = converter.getConversionVariants(info);
                String[] displayTexts = converter.getVariantDescriptions(info);
                assert variants.length == displayTexts.length : "Number of variants=" + variants.length + " != number of description strings=" + displayTexts.length;
                int iMax = variants.length;
                for (int i = 0; i < iMax; i++) {
                    if (conversionVariant.equals(variants[i])) return displayTexts[i];
                }
            } else {
                int tmp = 0;
            }
        }

        return null;
    }

    @Nullable
    public String getConversionVariant(@NotNull String info, @NotNull String displayText) {
        initializeConverterMap();

        HashMap<String, MdFencedCodeImageConverter> variantMap = infoVariantMap.get(info);
        if (variantMap != null) {
            for (Map.Entry<String, MdFencedCodeImageConverter> entry : variantMap.entrySet()) {
                if (displayText.equals(getConversionVariantDisplayText(info, entry.getKey()))) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    @Nullable
    public MdImageFencedCode convertFromImageUrl(@NotNull String url, boolean isMultiline, @Nullable Attributes attributes) {
        for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
            MdImageFencedCode imageFencedCode = converter.convertFromImageUrl(url, isMultiline, attributes);
            if (imageFencedCode != null) return imageFencedCode;
        }
        return null;
    }

    public boolean canConvertFromImageUrl(@NotNull String url, boolean isMultiline, @Nullable Attributes attributes) {
        for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
            if (converter.canConvertFromImageUrl(url, isMultiline, attributes)) return true;
        }
        return false;
    }

    private void initializeConverterMap() {
        if (infoVariantMap.isEmpty()) {
            // initialize the map
            synchronized (infoVariantMap) {
                if (infoVariantMap.isEmpty()) {
                    // initialize the map
                    for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
                        String[] infoStrings = converter.getInfoStrings();
                        for (String infoString : infoStrings) {
                            HashMap<String, MdFencedCodeImageConverter> variantMap = infoVariantMap.computeIfAbsent(infoString, info1 -> new LinkedHashMap<>());
                            String[] conversionVariants = converter.getConversionVariants(infoString);
                            for (String variant : conversionVariants) {
                                if (!variantMap.containsKey(variant)) {
                                    // give it to this converter
                                    variantMap.put(variant, converter);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
