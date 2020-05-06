// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.vladsch.md.nav.flex.PluginBundle;
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOptionDefinition;
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils;
import com.vladsch.md.nav.flex.psi.util.SpecResourceDefinition;
import com.vladsch.md.nav.util.PsiMap;
import com.vladsch.md.nav.util.Result;
import icons.FlexmarkIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class FlexmarkLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Nullable
    @Override
    public Icon getIcon() {
        return FlexmarkIcons.EXTENSION;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public String getName() {
        return "Flexmark-Java";
    }

// @formatter:off
    public static final Option FLEXMARK_SPEC_FILE = new Option(FlexmarkLineMarkerProvider.class.getName() + ".spec.file"                     , PluginBundle.message("linemarker.flexmark.spec-file")              , FlexmarkIcons.Element.FLEXMARK_SPEC);
    public static final Option FLEXMARK_SPEC_EXAMPLE_OPTION = new Option(FlexmarkLineMarkerProvider.class.getName() + ".spec.example.option" , PluginBundle.message("linemarker.flexmark.spec-example-option")   , FlexmarkIcons.Element.SPEC_EXAMPLE);

// @formatter:on

    final private static Option[] ourOptions = {
            FLEXMARK_SPEC_FILE,
            FLEXMARK_SPEC_EXAMPLE_OPTION,
    };

    @NotNull
    @Override
    public Option[] getOptions() {
        return ourOptions;
    }

    static class SpecOptionCellRenderer extends PsiElementListCellRenderer<PsiElement> {
        final static SpecOptionCellRenderer INSTANCE = new SpecOptionCellRenderer();

        @Override
        public String getElementText(PsiElement psiElement) {
            return psiElement.getText();
        }

        public Icon getIcon(PsiElement psiElement) {
            return psiElement.getIcon(0);
        }

        public String getContainerText(PsiElement psiElement, String name) {
            return psiElement.getContainingFile().getName();
        }

        public int getIconFlags() {
            return Iconable.ICON_FLAG_READ_STATUS;
        }
    }

    @Override
    protected void collectNavigationMarkers(@NotNull final PsiElement element1, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element1 instanceof LeafPsiElement) {
            PsiElement element = element1.getParent();

            if (element instanceof PsiLiteralExpression && element.getFirstChild() == element1) {

                //noinspection ConstantConditions
                do {
                    if (FLEXMARK_SPEC_EXAMPLE_OPTION.isEnabled()) {
                        FlexmarkExampleOptionDefinition definition = FlexmarkPsiImplUtils.getFlexmarkExampleOptionDefinition(element, null);
                        if (definition != null) {
                            FlexmarkSpecTestCaseCachedData.Companion.PsiClassData psiClassData = FlexmarkSpecTestCaseCachedData.getData(definition.getPsiClass());
                            if (psiClassData != null) {
                                ArrayList<FlexmarkExampleOption> options = new ArrayList<>();

                                PsiMap<MdFile, String> files = psiClassData.getResolvedSpecFiles();
                                for (MdFile mdFile : files.keySet()) {
                                    FlexmarkPsiImplUtils.forAllOptions(mdFile, (option) -> {
                                        if (definition.isDefinitionFor(option)) {
                                            options.add(option);
                                        }
                                        return Result.CONTINUE();
                                    });
                                }

                                if (!options.isEmpty()) {
                                    NavigationGutterIconBuilder<PsiElement> builder =
                                            NavigationGutterIconBuilder.create(options.size() > 1 ? FlexmarkIcons.Element.MULTI_SPEC_EXAMPLE : FlexmarkIcons.Element.SPEC_EXAMPLE)
                                                    .setCellRenderer(SpecOptionCellRenderer.INSTANCE)
                                                    .setTargets(options)
                                                    .setTooltipText(PluginBundle.message("linemarker.flexmark.navigate.flex-example"));
                                    result.add(builder.createLineMarkerInfo(element1));
                                }
                            }
                            break;
                        }
                    }

                    if (FLEXMARK_SPEC_FILE.isEnabled()) {
                        SpecResourceDefinition specResource = FlexmarkPsiImplUtils.getSpecResourceDefinitionOrNull(element);
                        if (specResource != null) {
                            FlexmarkSpecTestCaseCachedData.Companion.PsiClassData psiClassData = FlexmarkSpecTestCaseCachedData.getData(specResource.getPsiClass());
                            if (psiClassData != null) {
                                Set<MdFile> files = psiClassData.getSpecFiles().keySet();
                                if (!files.isEmpty()) {
                                    NavigationGutterIconBuilder<PsiElement> builder =
                                            NavigationGutterIconBuilder.create(files.size() > 1 ? FlexmarkIcons.Element.MULTI_FLEXMARK_SPEC : FlexmarkIcons.Element.FLEXMARK_SPEC)
                                                    .setTargets(files)
                                                    .setTooltipText(PluginBundle.message("linemarker.flexmark.navigate.spec-file"));
                                    result.add(builder.createLineMarkerInfo(element1));
                                }
                            }
                            break;
                        }
                    }
                } while (false);
            }
        }
    }
}
