// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.parser.api.HtmlPurpose;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.settings.MdCssSettings;
import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdPreviewSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileHolder;
import com.vladsch.md.nav.settings.api.MdSettingsExtension;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenderingOptions implements MdRenderingProfileHolder {
    private final MdRenderingProfile myRenderingProfile;
    ParserPurpose myParserPurpose;
    HtmlPurpose myHtmlPurpose;
    @Nullable MdLinkResolver myLinkResolver;
    private final MdRenderingProfileHolder myHolder;

    public RenderingOptions(@NotNull final ParserPurpose parserPurpose, @NotNull final HtmlPurpose htmlPurpose, @NotNull final MdRenderingProfile renderingProfile, @Nullable final MdLinkResolver linkResolver) {
        myParserPurpose = parserPurpose;
        myHtmlPurpose = htmlPurpose;
        myRenderingProfile = renderingProfile;
        myLinkResolver = linkResolver;
        myHolder = myRenderingProfile;
    }

    @NotNull
    public MdRenderingProfile getRenderingProfile() {
        return myRenderingProfile;
    }

    public ParserPurpose getParserPurpose() {
        return myParserPurpose;
    }

    public HtmlPurpose getHtmlPurpose() {
        return myHtmlPurpose;
    }

    @Nullable
    public MdLinkResolver getLinkResolver() {
        return myLinkResolver;
    }

    public boolean haveExtensions(int mask) {
        return (myRenderingProfile.getParserSettings().getPegdownFlags() & mask) != 0;
    }

    public boolean haveOptions(long mask) {
        return (myRenderingProfile.getParserSettings().getOptionsFlags() & mask) != 0L;
    }

    @Override
    public void setRenderingProfile(@NotNull final MdRenderingProfile renderingProfile) {myHolder.setRenderingProfile(renderingProfile);}

    @Override
    @NotNull
    public MdRenderingProfile getResolvedProfile(@NotNull final MdRenderingProfile parentProfile) {return myHolder.getResolvedProfile(parentProfile);}

    @Override
    public void groupNotifications(@NotNull final Runnable runnable) {myHolder.groupNotifications(runnable);}

    @Override
    @NotNull
    public <T extends MdSettingsExtension<T>> T getExtension(@NotNull final DataKey<T> key) {return myHolder.getExtension(key);}

    @Override
    public <T extends MdSettingsExtension<T>> void setExtension(@NotNull final T value) {myHolder.setExtension(value);}

    @Override
    @NotNull
    public MdPreviewSettings getPreviewSettings() {return myHolder.getPreviewSettings();}

    @Override
    public void setPreviewSettings(@NotNull final MdPreviewSettings previewSettings) {myHolder.setPreviewSettings(previewSettings);}

    @Override
    @NotNull
    public MdParserSettings getParserSettings() {return myHolder.getParserSettings();}

    @Override
    public void setParserSettings(@NotNull final MdParserSettings parserSettings) {myHolder.setParserSettings(parserSettings);}

    @Override
    @NotNull
    public MdHtmlSettings getHtmlSettings() {return myHolder.getHtmlSettings();}

    @Override
    public void setHtmlSettings(@NotNull final MdHtmlSettings htmlSettings) {myHolder.setHtmlSettings(htmlSettings);}

    @Override
    @NotNull
    public MdCssSettings getCssSettings() {return myHolder.getCssSettings();}

    @Override
    public void setCssSettings(@NotNull final MdCssSettings cssSettings) {myHolder.setCssSettings(cssSettings);}

    @Override
    @NotNull
    public MdCodeStyleSettings getStyleSettings() {return myHolder.getStyleSettings();}

    @Override
    public void setStyleSettings(@NotNull MdCodeStyleSettings styleSettings) {myHolder.setStyleSettings(styleSettings);}
}
