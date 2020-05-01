// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import com.intellij.openapi.updateSettings.impl.UpdateSettings;
import com.vladsch.flexmark.util.sequence.RepeatedSequence;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.settings.UpdateStreamType;
import com.vladsch.md.nav.settings.api.MdExtensionInfoProvider;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MiscUtils {
    public static String getResourceFileContent(String resourcePath) {
        StringWriter writer = new StringWriter();
        getResourceFileContent(writer, resourcePath);
        return writer.toString();
    }

    public static void getResourceFileContent(final StringWriter writer, final String resourcePath) {
        InputStream inputStream = MiscUtils.class.getResourceAsStream(resourcePath);
        try {
            IOUtils.copy(inputStream, writer, "UTF-8");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static String getFileContent(@Nullable File file, @Nullable Charset charset) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            if (fileInputStreamReader.read(bytes) != -1) {
                return charset == null ? new String(bytes) : new String(bytes, charset);
            }
            return null;
        } catch (Throwable e) {
            return null;
        }
    }

    static public <T, R> R firstNonNullResult(final @NotNull T[] extensions, @NotNull Function<T, R> function) {
        for (T extension : extensions) {
            R result = function.apply(extension);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    static public <T, R> void firstNonNullResult(final @NotNull T[] extensions, @NotNull Function<T, R> function, @NotNull Consumer<R> valueConsumer) {
        R result = firstNonNullResult(extensions, function);
        if (result != null) {
            valueConsumer.accept(result);
        }
    }

    static public <T, R> void forNonNullResult(final @NotNull T[] extensions, @NotNull Function<T, R> function, @NotNull Consumer<R> valueConsumer) {
        for (T extension : extensions) {
            R result = function.apply(extension);
            if (result != null) {
                valueConsumer.accept(result);
            }
        }
    }

    static public <T> void forEach(final @NotNull T[] extensions, @NotNull Consumer<T> valueConsumer) {
        for (T extension : extensions) {
            valueConsumer.accept(extension);
        }
    }

    public static String ensureTrailingBlankLines(String text, int blankLines) {
        int trailingBlankLines = 0;

        for (int i = text.length(); i-- > 0; ) {
            char c = text.charAt(i);

            if (c == '\n') {
                if (++trailingBlankLines > blankLines) break;
            } else if (c != ' ' && c != '\t') {
                if (trailingBlankLines > 0) trailingBlankLines--;
                break;
            }
        }

        if (trailingBlankLines < blankLines) {
            // add difference
            return text + RepeatedSequence.repeatOf('\n', blankLines - trailingBlankLines);
        }
        return text;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean containsIgnoreCase(Collection<String> list, String value) {
        for (String listValue : list) {
            if (listValue.equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    public static int getReleaseStream() {
        int releaseStream = 0;
        UpdateSettings settings = UpdateSettings.getInstance();
        List<String> hosts = settings.getStoredPluginHosts();

        for (String host : hosts) {
//            if (host.equalsIgnoreCase(MdPlugin.patchRelease)) {
//                releaseStream = UpdateStreamType.PATCHES.getIntValue();
//                break;
//            }
//            if (host.equalsIgnoreCase(MdPlugin.eapRelease)) {
//                releaseStream = UpdateStreamType.EAP.getIntValue();
//                break;
//            }
//            if (host.equalsIgnoreCase(MdPlugin.altPatchRelease)) {
//                releaseStream = UpdateStreamType.PATCHES.getIntValue();
//                break;
//            }
//            if (host.equalsIgnoreCase(MdPlugin.altEapRelease)) {
//                releaseStream = UpdateStreamType.EAP.getIntValue();
//                break;
//            }
            if (host.equalsIgnoreCase(MdPlugin.jbLegacyRelease)) {
                releaseStream = UpdateStreamType.LEGACY.getIntValue();
                break;
            }
            if (host.equalsIgnoreCase(MdPlugin.jbLegacyEapRelease)) {
                releaseStream = UpdateStreamType.LEGACY_EAP.getIntValue();
                break;
            }
            if (host.equalsIgnoreCase(MdPlugin.jbEapRelease)) {
                releaseStream = UpdateStreamType.EAP.getIntValue();
                break;
            }
        }

        return releaseStream;
    }

    private static void clearReleaseStream() {
        UpdateSettings settings = UpdateSettings.getInstance();
        List<String> hosts = settings.getStoredPluginHosts();
        ArrayList<String> newHosts = new ArrayList<String>();

        ArrayList<String> patchReleases = new ArrayList<>();
        ArrayList<String> eapReleases = new ArrayList<>();

        for (MdExtensionInfoProvider provider : MdExtensionInfoProvider.EXTENSIONS.getValue()) {
            String eapRelease = provider.eapRelease();
            String patchRelease = provider.legacyRelease();
            if (eapRelease != null) eapReleases.add(eapRelease);
            if (patchRelease != null) patchReleases.add(patchRelease);
        }

        for (String host : hosts) {
            if (!host.equalsIgnoreCase(MdPlugin.patchRelease)
                    && !host.equalsIgnoreCase(MdPlugin.eapRelease)
                    && !host.equalsIgnoreCase(MdPlugin.altPatchRelease)
                    && !host.equalsIgnoreCase(MdPlugin.altEapRelease)
                    && !host.equalsIgnoreCase(MdPlugin.jbLegacyRelease)
                    && !host.equalsIgnoreCase(MdPlugin.jbLegacyEapRelease)
                    && !host.equalsIgnoreCase(MdPlugin.jbEapRelease)
                    && !containsIgnoreCase(eapReleases, host)
                    && !containsIgnoreCase(patchReleases, host)
            ) {
                newHosts.add(host);
            }
        }

        hosts.clear();
        hosts.addAll(newHosts);
    }

    public static void setReleaseStream(int updateStream) {
        UpdateSettings settings = UpdateSettings.getInstance();
        List<String> hosts = settings.getStoredPluginHosts();
        ArrayList<String> legacyReleases = new ArrayList<>();
        ArrayList<String> legacyEapReleases = new ArrayList<>();
        ArrayList<String> eapReleases = new ArrayList<>();

        legacyReleases.add(MdPlugin.jbLegacyRelease);
        legacyEapReleases.add(MdPlugin.jbLegacyEapRelease);
        eapReleases.add(MdPlugin.jbEapRelease);

        for (MdExtensionInfoProvider provider : MdExtensionInfoProvider.EXTENSIONS.getValue()) {
            String eapRelease = provider.eapRelease();
            String legacyRelease = provider.legacyRelease();
            String legacyEapRelease = provider.legacyEapRelease();

            if (eapRelease != null) eapReleases.add(eapRelease);
            if (legacyRelease != null) legacyReleases.add(legacyRelease);
            if (legacyEapRelease != null) legacyEapReleases.add(legacyEapRelease);
        }

        clearReleaseStream();

        UpdateStreamType updateStreamType = UpdateStreamType.ADAPTER.findEnum(updateStream);
        List<String> releaseStream = null;

        switch (updateStreamType) {
            case EAP:
                releaseStream = eapReleases;
                break;

            case LEGACY:
                releaseStream = legacyReleases;
                break;

            case LEGACY_EAP:
                releaseStream = legacyEapReleases;
                break;

            default:
            case STABLE:
                break;
        }

        if (releaseStream != null) {
            for (String release : releaseStream) {
                if (!containsIgnoreCase(hosts, release)) {
                    hosts.add(release);
                }
            }
        }
    }
}
