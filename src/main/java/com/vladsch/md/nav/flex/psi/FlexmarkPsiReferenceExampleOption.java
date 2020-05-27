// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.ResolveResult;
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData;
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdRenameElement;
import com.vladsch.md.nav.psi.reference.MdPsiReference;
import com.vladsch.md.nav.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FlexmarkPsiReferenceExampleOption extends MdPsiReference {

    public FlexmarkPsiReferenceExampleOption(@NotNull FlexmarkExampleOption element, @NotNull TextRange textRange, boolean exactReference) {
        super(element, textRange, exactReference);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return getMultiResolveResults(incompleteCode);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    /**
     * Default implementation resolves to missing element reference by namespace of the referencing element
     *
     * @param incompleteCode code is incomplete
     *
     * @return resolve results
     */
    @NotNull
    protected ResolveResult[] getMultiResolveResults(boolean incompleteCode) {
        FlexmarkExampleOption element = (FlexmarkExampleOption) getElement();
        MdFile mdFile = element.getMdFile();
        ArrayList<ResolveResult> results = new ArrayList<>();
        HashSet<FlexmarkExampleOptionDefinition> definitionSet = new HashSet<>();

        TextRange textRange = getRangeInElement();
        FlexmarkPsiImplUtils.forOptionDefinitions(mdFile, element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset()), true, (psiClass, definitions) -> {
            FlexmarkExampleOptionDefinition definition = definitions[0];
            if (!definitionSet.contains(definition) && definition.isDefinitionFor(element)) {
                //                results.add(new PsiElementResolveResult(definitions.getLiteralExpressionElement()));
                definitionSet.add(definition);
                FakePsiLiteralExpression fakePsiElement = new FakePsiLiteralExpression(definition.getLiteralExpressionElement(), definition.getTextRange());
                results.add(new PsiElementResolveResult(fakePsiElement));
            }
            return Result.CONTINUE();
        });

        if (results.isEmpty()) {
            // not defined, allow navigation to the spec resource of the test class
            FlexmarkPsiImplUtils.forAllRenderingTestCaseClasses(mdFile, specTestData -> {
                for (FlexmarkSpecTestCaseCachedData.Companion.PsiClassData psiClassData : specTestData.getPsiClassData().values()) {
                    if (psiClassData.getSpecFiles().containsKey(mdFile) && !psiClassData.isNoSpecResourceTestCase()) {
                        PsiLiteralExpression literal = psiClassData.getSpecResourceLiteral();
                        if (literal != null) {
                            results.add(new PsiElementResolveResult(psiClassData.getPsiClass()));
                        }
                    }
                }

                return Result.VOID();
            });
        }

        return results.isEmpty() ? EMPTY_RESULTS : results.toArray(EMPTY_RESULTS);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        // NOTE: find all Java Test Cases which define this option for this spec file
        MdRenameElement element = getElement();
        List<LookupElement> variants = new ArrayList<>();
        HashMap<PsiFile, HashSet<String>> fileOptions = new HashMap<>();

        FlexmarkExampleOptions parent = (FlexmarkExampleOptions) myElement.getParent();
        List<String> options = parent.getOptions();
        HashSet<String> haveOptions = new HashSet<>(options);
        // NOTE: the element passed in is created for completion with dummy identifier in the text. Need to use original file otherwise nothing is returned.
        MdFile mdFile = (MdFile) element.getMdFile().getOriginalFile();

        FlexmarkPsiImplUtils.forOptionDefinitions(mdFile, null, true, (psiClass, definitions) -> {
            FlexmarkExampleOptionDefinition definition = definitions[0];
            String optionName = definition.getOptionName();
            if (!haveOptions.contains(optionName)) {
                List<String> dataKeys = FlexmarkPsiImplUtils.getFlexmarkExampleOptionDataKeys(definition.getLiteralExpressionElement());
                String paramText = null;
                if (dataKeys != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String dataKey : dataKeys) {
                        String[] split = dataKey.substring(1, dataKey.length() - 1).split(",", 2);
                        String paramKey = split[0].trim() + " = ";
                        if (split.length > 1) paramKey += split[1].trim();
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(paramKey);
                    }
                    paramText = sb.toString();
                }

                if (!fileOptions.containsKey(mdFile)) {
                    fileOptions.put(mdFile, new HashSet<>());
                }

                if (!fileOptions.get(mdFile).contains(optionName)) {
                    fileOptions.get(mdFile).add(optionName);
                    variants.add(LookupElementBuilder.create(optionName)
                            .withIcon(mdFile.getIcon(0))
                            .withTypeText(paramText != null ? paramText : mdFile.getName())
                    );
                }
            }
            return Result.CONTINUE();
        });

        for (FlexmarkOptionInfo optionInfo : FlexmarkPsi.FLEXMARK_OPTIONS_INFO) {
            if (optionInfo == null) continue;

            if (!haveOptions.contains(optionInfo.getOptionName())) {
                LookupElementBuilder builder = LookupElementBuilder.create(optionInfo.getOptionName());
                final Icon icon = optionInfo.icon;
                builder = builder.withIcon(icon);
                variants.add(builder);
            }
        }
        return variants.toArray();
    }
}
