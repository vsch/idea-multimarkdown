// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.collection.iteration.ArrayIterable;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.DataKeyBase;
import com.vladsch.flexmark.util.data.DataValueFactory;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.data.MutableDataSetter;
import com.vladsch.flexmark.util.data.NullableDataKey;
import com.vladsch.flexmark.util.misc.Extension;
import com.vladsch.md.nav.parser.api.HtmlPurpose;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MdParserOptions implements MutableDataHolder {
    final private LinkedHashMap<Class<? extends Extension>, Supplier<? extends Extension>> myExtensionMap = new LinkedHashMap<>();
    final private @NotNull MutableDataSet myOptions;
    @NotNull private MdRenderingProfile myRenderingProfile = MdRenderingProfile.getDEFAULT();
    @NotNull ParserPurpose myParserPurpose = ParserPurpose.PARSER;
    @NotNull HtmlPurpose myHtmlPurpose = HtmlPurpose.RENDER;
    @Nullable MdLinkResolver myLinkResolver = null;

    public MdParserOptions() {
        this(null);
    }

    public MdParserOptions(@Nullable DataHolder options) {
        myOptions = new MutableDataSet(options);
        reloadExtensions();
    }

    @NotNull
    public MdRenderingProfile getRenderingProfile() {
        return myRenderingProfile;
    }

    @NotNull
    public ParserPurpose getParserPurpose() {
        return myParserPurpose;
    }

    @NotNull
    public HtmlPurpose getHtmlPurpose() {
        return myHtmlPurpose;
    }

    @Nullable
    public MdLinkResolver getLinkResolver() {
        return myLinkResolver;
    }

    public void setExtensionOptions(@NotNull final ParserPurpose parserPurpose, @NotNull final HtmlPurpose htmlPurpose, @NotNull final MdRenderingProfile renderingProfile, @Nullable final MdLinkResolver linkResolver) {
        myParserPurpose = parserPurpose;
        myHtmlPurpose = htmlPurpose;
        myRenderingProfile = renderingProfile;
        myLinkResolver = linkResolver;
    }

    public boolean haveExtensions(int mask) {
        return (myRenderingProfile.getParserSettings().getPegdownFlags() & mask) != 0;
    }

    public boolean haveOptions(long mask) {
        return (myRenderingProfile.getParserSettings().getOptionsFlags() & mask) != 0L;
    }

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    MdParserOptions addExtensions(Iterable<Extension> extensions) {
        for (Extension extension : extensions) {
            if (!myExtensionMap.containsKey(extension.getClass())) {
                myExtensionMap.put(extension.getClass(), () -> extension);
            }
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    MdParserOptions addExtensions(Extension... extensions) {
        return addExtensions(new ArrayIterable<>(extensions));
    }

    @NotNull
    private MdParserOptions reloadExtensions() {
        // load Parser.EXTENSIONS if defined
        if (myOptions.contains(Parser.EXTENSIONS)) {
            addExtensions(Parser.EXTENSIONS.get(myOptions));
            myOptions.remove(Parser.EXTENSIONS);
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends Extension> MdParserOptions addExtension(Class<T> aClass, Supplier<T> supplier) {
        if (!myExtensionMap.containsKey(aClass)) {
            myExtensionMap.put(aClass, supplier);
        }
        return this;
    }

    @Nullable
    public <T extends Extension> Supplier<T> removeExtension(Class<T> aClass) {
        //noinspection unchecked
        return (Supplier<T>) myExtensionMap.remove(aClass);
    }

    public boolean containsExtension(Class<? extends Extension> aClass) {
        return myExtensionMap.containsKey(aClass);
    }

    @NotNull
    public List<Extension> getExtensions() {
        ArrayList<Extension> extensions = new ArrayList<>(myExtensionMap.size());
        for (Supplier<? extends Extension> supplier : myExtensionMap.values()) {
            extensions.add(supplier.get());
        }
        return extensions;
    }

    private void validateKey(DataKeyBase<?> key) {
        if (key == Parser.EXTENSIONS) {
            throw new IllegalStateException("Use of Parser.EXTENSIONS is not supported, use dedicated methods for extension manipulation");
        }
    }

    @NotNull
    public <T> MdParserOptions set(@NotNull final DataKey<T> key, @NotNull final T value) {
        validateKey(key);
        myOptions.set(key, value);
        return this;
    }

    @NotNull
    public <T> MdParserOptions set(@NotNull final NullableDataKey<T> key, @Nullable final T value) {
        validateKey(key);
        myOptions.set(key, value);
        return this;
    }

    @NotNull
    @Override
    public MdParserOptions remove(@NotNull final DataKeyBase<?> key) {
        validateKey(key);
        myOptions.remove(key);
        return this;
    }

    @Override
    public boolean contains(@NotNull final DataKeyBase<?> key) {
        validateKey(key);
        return myOptions.contains(key);
    }

    @NotNull
    public MdParserOptions setFrom(@NotNull final MutableDataSetter dataSetter) {
        myOptions.setFrom(dataSetter);
        return reloadExtensions();
    }

    @NotNull
    public MdParserOptions setAll(@NotNull final DataHolder other) {
        myOptions.setAll(other);
        return reloadExtensions();
    }

    @NotNull
    public MdParserOptions setIn(@NotNull final MutableDataHolder dataHolder) {
        myOptions.setIn(dataHolder);
        return reloadExtensions();
    }

    @Override
    public Object getOrCompute(@NotNull DataKeyBase<?> key, @NotNull DataValueFactory<?> factory) {
        validateKey(key);
        return myOptions.getOrCompute(key, factory);
    }

    @NotNull
    public MdParserOptions toMutable() {
        return this;
    }

    @NotNull
    public DataHolder toImmutable() {return myOptions.toImmutable();}

    @NotNull
    public MdParserOptions clear() {
        myOptions.clear();
        return reloadExtensions();
    }

    @NotNull
    public Map<? extends DataKeyBase<?>, Object> getAll() {return myOptions.getAll();}

    @NotNull
    public Collection<? extends DataKeyBase<?>> getKeys() {return myOptions.getKeys();}

    @NotNull
    public DataHolder getOptions() {
        myOptions.set(Parser.EXTENSIONS, getExtensions());
        return myOptions.toImmutable();
    }
}
