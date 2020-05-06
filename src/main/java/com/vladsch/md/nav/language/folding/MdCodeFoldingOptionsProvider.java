// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.folding;

import com.intellij.application.options.editor.CodeFoldingOptionsProvider;
import com.intellij.openapi.options.BeanConfigurable;
import com.intellij.openapi.util.Getter;
import com.intellij.openapi.util.Setter;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.language.api.MdCodeFoldingOptionsHolder;
import com.vladsch.md.nav.language.api.MdFoldingBuilderProvider;
import com.vladsch.md.nav.settings.MdExtensionSpacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.Arrays;

public class MdCodeFoldingOptionsProvider extends BeanConfigurable<MdFoldingSettings> implements CodeFoldingOptionsProvider {
    public MdCodeFoldingOptionsProvider() {
        super(MdFoldingSettings.getInstance(), MdBundle.message("language.name"));
        MdFoldingSettings settings = MdFoldingSettings.getInstance();

        checkBox(MdBundle.message("code-folding.code-fence.blocks"), () -> settings.COLLAPSE_CODE_FENCE_BLOCKS, value -> settings.COLLAPSE_CODE_FENCE_BLOCKS = value);
        checkBox(MdBundle.message("code-folding.verbatim.blocks"), () -> settings.COLLAPSE_VERBATIM_BLOCKS, value -> settings.COLLAPSE_VERBATIM_BLOCKS = value);
        checkBox(MdBundle.message("code-folding.references"), () -> settings.COLLAPSE_REFERENCES, value -> settings.COLLAPSE_REFERENCES = value);
        checkBox(MdBundle.message("code-folding.links"), () -> settings.COLLAPSE_EXPLICIT_LINKS, value -> settings.COLLAPSE_EXPLICIT_LINKS = value);
        checkBox(MdBundle.message("code-folding.images"), () -> settings.COLLAPSE_IMAGES, value -> settings.COLLAPSE_IMAGES = value);
        checkBox(MdBundle.message("code-folding.multi-line-url.images"), () -> settings.COLLAPSE_MULTILINE_URL_IMAGES, value -> settings.COLLAPSE_MULTILINE_URL_IMAGES = value);
        checkBox(MdBundle.message("code-folding.embedded.images"), () -> settings.COLLAPSE_EMBEDDED_IMAGES, value -> settings.COLLAPSE_EMBEDDED_IMAGES = value);
        checkBox(MdBundle.message("code-folding.jekyll-front-matter"), () -> settings.COLLAPSE_JEKYLL_FRONT_MATTER, value -> settings.COLLAPSE_JEKYLL_FRONT_MATTER = value);

        MdHeadingFolding headingFolding = new MdHeadingFolding();
        component(headingFolding.getMainPanel(), this::getHeadings, this::setHeadings, headingFolding::getValue, headingFolding::setValue);

        checkBox(MdBundle.message("code-folding.collapse-list-items"), () -> settings.COLLAPSE_LIST_ITEMS, value -> settings.COLLAPSE_LIST_ITEMS = value);
        checkBox(MdBundle.message("code-folding.comments"), () -> settings.COLLAPSE_COMMENTS, value -> settings.COLLAPSE_COMMENTS = value);

        // allow extensions to add theirs
        MdFoldingBuilderProvider[] extensions = MdFoldingBuilderProvider.EXTENSIONS.getValue();
        ArrayList<MdFoldingBuilderProvider> sortedExtensions = new ArrayList<>(Arrays.asList(extensions));
        sortedExtensions.sort((provider1, provider2) -> {
            String name1 = provider1.getExtensionName();
            String name2 = provider2.getExtensionName();
            return name1.isEmpty() && name2.isEmpty() ? 0 : name1.isEmpty() ? 1 : name2.isEmpty() ? -1 : name1.compareTo(name2);
        });

        for (MdFoldingBuilderProvider provider : sortedExtensions) {
            final boolean[] first = { true };
            provider.extendFoldingOptions(new MdCodeFoldingOptionsHolder() {
                @Override
                public void addCheckBox(@NotNull String title, @NotNull Getter<Boolean> getter, @NotNull Setter<Boolean> setter) {
                    if (first[0]) {
                        addExtensionSeparator();
                        first[0] = false;
                    }
                    MdCodeFoldingOptionsProvider.this.checkBox(title, getter, setter);
                }

                @Override
                public <V> void component(@NotNull JComponent component, @NotNull Getter<? extends V> beanGetter, @NotNull Setter<? super V> beanSetter, @NotNull Getter<? extends V> componentGetter, @NotNull Setter<? super V> componentSetter) {
                    if (first[0]) {
                        addExtensionSeparator();
                        first[0] = false;
                    }
                    MdCodeFoldingOptionsProvider.this.component(component, beanGetter, beanSetter, componentGetter, componentSetter);
                }

                void addExtensionSeparator() {
                    String name = provider.getExtensionName();
                    addSeparator(name.isEmpty() ? "Unnamed Extension" : name);
                }
            });
        }
    }

    int getHeadings() {
        int value = 0;
        MdFoldingSettings s = MdFoldingSettings.getInstance();

        if (s.COLLAPSE_HEADINGS_1) value |= 1 << 0;
        if (s.COLLAPSE_HEADINGS_2) value |= 1 << 1;
        if (s.COLLAPSE_HEADINGS_3) value |= 1 << 2;
        if (s.COLLAPSE_HEADINGS_4) value |= 1 << 3;
        if (s.COLLAPSE_HEADINGS_5) value |= 1 << 4;
        if (s.COLLAPSE_HEADINGS_6) value |= 1 << 5;
        return value;
    }

    void setHeadings(int value) {
        MdFoldingSettings s = MdFoldingSettings.getInstance();
        s.COLLAPSE_HEADINGS_1 = (value & (1 << 0)) != 0;
        s.COLLAPSE_HEADINGS_2 = (value & (1 << 1)) != 0;
        s.COLLAPSE_HEADINGS_3 = (value & (1 << 2)) != 0;
        s.COLLAPSE_HEADINGS_4 = (value & (1 << 3)) != 0;
        s.COLLAPSE_HEADINGS_5 = (value & (1 << 4)) != 0;
        s.COLLAPSE_HEADINGS_6 = (value & (1 << 5)) != 0;
    }

    static final Getter<Boolean> dummyGetter = () -> true;
    static final Setter<Boolean> dummySetter = (value) -> {
    };

    void addSeparator(@Nullable String name) {
        component(new MdExtensionSpacer(name == null ? "" : name, null).getMainPanel(), dummyGetter, dummySetter, dummyGetter, dummySetter);
    }
}
