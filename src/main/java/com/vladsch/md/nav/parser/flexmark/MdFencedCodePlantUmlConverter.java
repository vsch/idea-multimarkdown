// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.intellij.openapi.util.io.FileUtil;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.Escaping;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.MdImageCache;
import com.vladsch.md.nav.parser.api.MdFencedCodeImage;
import com.vladsch.md.nav.parser.api.MdFencedCodeImageConverter;
import com.vladsch.md.nav.parser.api.MdImageFencedCode;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.PlantUmlConversionType;
import com.vladsch.md.nav.util.Md5Utils;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import net.sourceforge.plantuml.SourceStringReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MdFencedCodePlantUmlConverter implements MdFencedCodeImageConverter {
    final static Pattern START_UML_PATTERN = Pattern.compile("^@start(uml|mindmap|wbs|salt)(?:;?\\s*(?:\n|$))");
    final static Pattern END_UML_PATTERN = Pattern.compile("^@end(uml|mindmap|wbs|salt)(?:;?\\s*(?:\n|$))");
    final public static String PUML_LANGUAGE_INFO = "puml";
    final public static String PLANTUML_LANGUAGE_INFO = "plantuml";
    final public static String[] INFO_STRINGS = { PLANTUML_LANGUAGE_INFO, PUML_LANGUAGE_INFO };
    final public static String PLANT_UML_CACHE_SIGNATURE = "PlantUML:";
    public static final String EMBEDDED = "EMBEDDED";
    final public static String[] CONVERSION_VARIANTS = {
            "NONE",
            EMBEDDED,
            "GRAVIZO_PNG",
            "GRAVIZO_SVG",
    };
    public static final String[] CONVERSION_DISPLAY_STRINGS = {
            MdBundle.message("plant-uml.conversion.none"),
            MdBundle.message("plant-uml.conversion.embedded"),
            MdBundle.message("plant-uml.conversion.gravizo-png"),
            MdBundle.message("plant-uml.conversion.gravizo-svg"),
    };

    public static final String[] CONVERSION_DESCRIPTION_STRINGS = {
            MdBundle.message("plant-uml.conversion.none.description"),
            MdBundle.message("plant-uml.conversion.embedded.description"),
            MdBundle.message("plant-uml.conversion.gravizo-png.description"),
            MdBundle.message("plant-uml.conversion.gravizo-svg.description"),
    };

    private static final Pattern UML_INCLUDE_PATTERN = Pattern.compile("^\\s*!include\\s+(.+)\\s*$", Pattern.MULTILINE);

    @NotNull
    @Override
    public String[] getInfoStrings() {
        return INFO_STRINGS;
    }

    @NotNull
    @Override
    public String[] getConversionVariants(@NotNull String info) {
        return CONVERSION_VARIANTS;
    }

    @NotNull
    @Override
    public String[] getVariantDisplayTexts(@NotNull String info) {
        return CONVERSION_DISPLAY_STRINGS;
    }

    @NotNull
    @Override
    public String[] getVariantDescriptions(@NotNull String info) {
        return CONVERSION_DESCRIPTION_STRINGS;
    }

    @Nullable
    @Override
    public String migrateConversionVariant(@NotNull String info, @NotNull Object param) {
        if (param instanceof PlantUmlConversionType) {
            switch ((PlantUmlConversionType) param) {
                case NONE:
                    return "NONE";
                case EMBEDDED:
                    return EMBEDDED;
                case GRAVIZO_PNG:
                    return "GRAVIZO_PNG";
                case GRAVIZO_SVG:
                    return "GRAVIZO_SVG";
            }
        }
        return null;
    }

    @Override
    public boolean hasUrlConversion(@NotNull String info, String conversionVariant, boolean remoteOnly) {
        PlantUmlConversionType conversionType = PlantUmlConversionType.ADAPTER.findEnumNameOrNull(conversionVariant);
        return conversionType != null && (conversionType.urlPrefix() != null || !remoteOnly && (conversionType == PlantUmlConversionType.EMBEDDED));
    }

    @Nullable
    private PlantUmlConversionType getConversionTypeFromImageUrl(@NotNull String url, boolean isMultiline, @Nullable Attributes attributes) {
        for (PlantUmlConversionType conversionType : PlantUmlConversionType.values()) {
            String urlPrefix = conversionType.urlPrefix();
            if (urlPrefix != null) {
                if (url.startsWith(urlPrefix)) {
                    return conversionType;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canConvertFromImageUrl(@NotNull String url, boolean isMultiline, @Nullable Attributes attributes) {
        String useUrl = MdFencedCodeImageConverter.replaceHttpWithHttps(url);
        return getConversionTypeFromImageUrl(useUrl, isMultiline, attributes) != null;
    }

    @Nullable
    @Override
    public MdImageFencedCode convertFromImageUrl(@NotNull String url, boolean isMultiline, @Nullable Attributes attributes) {
        String useUrl = MdFencedCodeImageConverter.replaceHttpWithHttps(url);
        PlantUmlConversionType conversionType = getConversionTypeFromImageUrl(useUrl, isMultiline, attributes);
        if (conversionType != null) {
            String urlPrefix = conversionType.urlPrefix();
            if (urlPrefix != null) {
                String content = useUrl.substring(urlPrefix.length());
                if (!isMultiline) content = Escaping.percentDecodeUrl(content);
                return new MdImageFencedCode(content, INFO_STRINGS, conversionType.name(), true);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public MdFencedCodeImage imageUrl(@NotNull String content, @NotNull String info, @NotNull String variant, @NotNull MdRenderingProfile renderingProfile, @NotNull MdLinkResolver resolver) {
        PlantUmlConversionType conversionType = PlantUmlConversionType.ADAPTER.findEnumNameOrNull(variant);
        if (conversionType == null) {
            conversionType = PlantUmlConversionType.NONE;
        }

        if (conversionType != PlantUmlConversionType.NONE) {
            boolean embedded = conversionType.isEmbedded();

            if ((info.equals(PUML_LANGUAGE_INFO))) {
                String url = getUrl(content, resolver, conversionType, true, !embedded);
                return new MdFencedCodeImage(url, conversionType.extension, true, null, null);
            } else if (info.equals(PLANTUML_LANGUAGE_INFO)) {
                String url = getUrl(content, resolver, conversionType, true, !embedded);
                return new MdFencedCodeImage(url, conversionType.extension, true, null, null);
            }
        }
        return null;
    }

    @NotNull
    private String getUrl(@NotNull String content, @NotNull MdLinkResolver resolver, PlantUmlConversionType plantUmlFencedCode, boolean wrapInStartEnd, boolean suffixSemi) {
        StringBuilder contentText = new StringBuilder();
        CharSequence[] contentLines = content.split("\n");
        int iMax = contentLines.length;
        CharSequence usePrefix = "";

        String umlType = "uml";

        boolean addStartUml = wrapInStartEnd;
        boolean addEndUml = wrapInStartEnd;

        if (wrapInStartEnd && iMax > 0) {
            Matcher startMatcher = START_UML_PATTERN.matcher(contentLines[0]);
            if (startMatcher.find()) {
                umlType = startMatcher.group(1);
                addStartUml = false;
            }

            Matcher endMatcher = START_UML_PATTERN.matcher(contentLines[iMax - 1]);
            if (endMatcher.find()) {
                if (addStartUml) {
                    umlType = endMatcher.group(1);
                }
                addEndUml = false;
            }
        } else {
            addStartUml = addEndUml = false;
        }

        if (addStartUml) {
            contentText.append(usePrefix);
            contentText.append("@start").append(umlType);
            if (suffixSemi) contentText.append(';');
            contentText.append("\n");
        }

        for (CharSequence contentLine : contentLines) {
            BasedSequence line = BasedSequence.of(contentLine);
            BasedSequence trimmed = line.trimEnd();
            contentText.append(usePrefix);
            contentText.append(trimmed);

            if (suffixSemi && !trimmed.endsWith(";")) {
                contentText.append(';');
            }

            contentText.append('\n');
        }

        if (addEndUml) {
            contentText.append(usePrefix);
            contentText.append("@end").append(umlType);
            if (suffixSemi) contentText.append(';');
            contentText.append("\n");
        }

        String url;

        if (!plantUmlFencedCode.isEmbedded()) {
            // site generated, use URL
            // reverse URL encoding of =, &
            CharSequence contentUml = Escaping.percentEncodeUrl(contentText).replace("+", "%2B").replace("%3D", "=").replace("%26", "&amp;");
            url = plantUmlFencedCode.urlPrefix() + contentUml;
        } else {
            Md5Utils md5 = new Md5Utils();
            CharSequence contentUml = contentText.toString();

            md5.add(contentText);

            Matcher matcher = UML_INCLUDE_PATTERN.matcher(contentText);
            while (matcher.find()) {
                String path = matcher.group(1);
                if (!path.startsWith("<") && !path.endsWith(">")) {
                    File file = PathInfo.isAbsolute(path) ? new File(path) : new File(resolver.getContainingFile().getPath(), path);
                    if (file.exists() && file.isFile()) {
                        md5.add(file);
                    }
                }
            }

            String contentMd5 = md5.getMd5();

            File imageFile = MdImageCache.getInstance().getImageFile(PLANT_UML_CACHE_SIGNATURE + contentMd5, plantUmlFencedCode.extension, file -> {
                File parentDir = new File(resolver.getContainingFile().getFilePath()).getParentFile();
                String savedDir = null;

                if (parentDir != null && parentDir.exists() && parentDir.isDirectory()) {
                    savedDir = System.getProperty("user.dir");
                    String absolutePath = parentDir.getAbsolutePath();
                    System.setProperty("user.dir", absolutePath);
                }

                try {
                    SourceStringReader planUmlReader = new SourceStringReader(contentUml.toString());
                    try {
                        planUmlReader.outputImage(file);
                    } catch (IOException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                } finally {
                    if (savedDir != null) {
                        System.setProperty("user.dir", savedDir);
                    }
                }
            });

            url = "file://" + FileUtil.toSystemIndependentName(imageFile.getPath());
        }
        return url;
    }
}
