// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.language

import com.intellij.codeInsight.daemon.GutterIconDescriptor
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteralExpression
import com.vladsch.md.nav.flex.PluginBundle
import com.vladsch.md.nav.language.MdLineMarkerProvider
import com.vladsch.md.nav.language.api.MdLineMarkerExtension
import com.vladsch.md.nav.language.api.MdLineMarkerExtension.LineMarkerProvider
import com.vladsch.md.nav.flex.parser.FlexmarkSpecCachedData
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData
import com.vladsch.md.nav.flex.psi.FakePsiLiteralExpression
import com.vladsch.md.nav.flex.psi.FlexmarkExample
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOptionDefinition
import com.vladsch.md.nav.flex.psi.FlexmarkPsi
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.PsiMap
import com.vladsch.md.nav.util.toPsiMap
import icons.FlexmarkIcons
import javax.swing.Icon

class MdFlexmarkLineMarkerExtension : MdLineMarkerExtension {
    override fun getOptions(): Array<GutterIconDescriptor.Option> {
        return ourOptions
    }

    override fun collectNavigationMarkers(leafElement: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>, provider: LineMarkerProvider): Boolean {
        if (leafElement.node.elementType !== MdTypes.FLEXMARK_EXAMPLE_OPEN) {
            return false
        }

        if (!SPEC_EXAMPLE.isEnabled) return true;

        val element: FlexmarkExample = MdPsiImplUtil.findAncestorOfType(leafElement, FlexmarkExample::class.java) ?: return false
        val optionsListElement = element.optionsList
        val optionsMap = optionsListElement?.optionElements?.mapNotNull { if (!it.isBuiltIn) it.optionName to it else null }?.toMap() ?: emptyMap()

        val testCaseDefinitionsMap:
            PsiMap<PsiClass, ArrayList<Array<FlexmarkExampleOptionDefinition>>> =
            FlexmarkSpecCachedData.getData(element.mdFile).getElementOptions(optionsMap)

        val definitionElements = ArrayList<PsiElement>()
        val baseClassDefinitions = PsiMap { HashMap<PsiClass, ArrayList<PsiElement>>() }

        testCaseDefinitionsMap.forEach { it ->
            val specResourceLiteral = FlexmarkPsiImplUtils.getResourceSpecLiteralOrNull(it.key)
            if (specResourceLiteral != null) {
                definitionElements.add(FakePsiLiteralExpression(specResourceLiteral, TextRange(1, specResourceLiteral.textLength - 1)))
            } else {
                definitionElements.add(it.key)
            }

            if (it.value.isNotEmpty()) {
                val definitionList = it.value.map { definitions -> definitions[0] }
                val testCaseDefinitions = definitionList.filter { definition -> definition.psiClass.isEquivalentTo(it.key) }.map { definition -> FakePsiLiteralExpression(definition.literalExpressionElement, definition.rangeInElement) }
                definitionElements.addAll(testCaseDefinitions)

                // move all inherited definitions to be shown under their class
                val testCaseBaseDefinitions = definitionList.filter { definition -> !definition.psiClass.isEquivalentTo(it.key) }
                    .map { definition -> it.key to FakePsiLiteralExpression(definition.literalExpressionElement, definition.rangeInElement) }
                    .groupBy { it.first }
                    .map { it.key to it.value.map { it.second } }

                testCaseBaseDefinitions.forEach {
                    baseClassDefinitions.computeIfAbsent(it.first) { ArrayList() }.addAll(it.second)
                }
            }
        }

        baseClassDefinitions.forEach {
            // no need to add the class, the option will take it there
//            definitionElements.add(it.key)
            definitionElements.addAll(it.value)
        }

        val testCaseCount = testCaseDefinitionsMap.size
        val baseCaseCount = baseClassDefinitions.size
        val optionsCount = optionsMap.size
        val testCaseOptionsCountMap = testCaseDefinitionsMap.map { it.key to it.value.size }.toPsiMap()
        val missing = testCaseOptionsCountMap.map { if (FlexmarkSpecTestCaseCachedData.getData(it.key)?.specResourceLiteral != null) optionsCount - it.value else 0 }.sum()
        var disabled = 0
        val countArray = Array(FlexmarkPsi.FLEXMARK_BUILT_IN_OPTION_COUNT) { 0 }

        if (optionsListElement != null) {
            for (optionInfo in optionsListElement.optionsInfo) {
                if (optionInfo.isBuiltIn && !optionInfo.isDisabled) {
                    countArray[optionInfo.index]++
                } else {
                    if (optionInfo.isDisabled) disabled++
                }
            }
        }

        if (definitionElements.size > 0) {
            val icon: Icon = when {
                missing > 0 -> {
                    when (testCaseCount - baseCaseCount) {
                        0 -> if (optionsCount > 1) FlexmarkIcons.Element.MULTI_SPEC_EXAMPLE_ERRORS else FlexmarkIcons.Element.SPEC_EXAMPLE_ERRORS
                        1 -> FlexmarkIcons.Element.FLEXMARK_SPEC_ERRORS
                        else -> FlexmarkIcons.Element.MULTI_FLEXMARK_SPEC_ERRORS
                    }
                }
                countArray[FlexmarkPsi.FLEXMARK_OPTION_IGNORE.index] > 0 -> {
                    when (optionsCount) {
                        0 -> if (testCaseCount - baseCaseCount > 1) FlexmarkIcons.Element.HIDDEN_FLEXMARK_SPEC else FlexmarkIcons.Element.HIDDEN_SPEC_EXAMPLE
                        1 -> FlexmarkIcons.Element.SPEC_EXAMPLE_IGNORED
                        else -> FlexmarkIcons.Element.HIDDEN_SPEC_EXAMPLE
                    }
                }
                countArray[FlexmarkPsi.FLEXMARK_OPTION_FAIL.index] > 0 -> FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_FAIL
                countArray[FlexmarkPsi.FLEXMARK_OPTION_TIMED.index] > 0 -> FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_TIMED
                countArray[FlexmarkPsi.FLEXMARK_OPTION_EMBED_TIMED.index] > 0 -> FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_TIMED
                countArray[FlexmarkPsi.FLEXMARK_OPTION_NO_FILE_EOL.index] > 0 -> FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_NO_FILE_EOL
                countArray[FlexmarkPsi.FLEXMARK_OPTION_FILE_EOL.index] > 0 -> FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_FILE_EOL
                else -> {
                    when (optionsCount) {
                        0 -> if (testCaseCount - baseCaseCount > 1) FlexmarkIcons.Element.MULTI_FLEXMARK_SPEC else FlexmarkIcons.Element.FLEXMARK_SPEC
                        1 -> FlexmarkIcons.Element.SPEC_EXAMPLE
                        else -> FlexmarkIcons.Element.MULTI_SPEC_EXAMPLE
                    }
                }
            }

            val cellRenderer = SpecOptionCellRenderer(optionsCount, testCaseDefinitionsMap)

            val builder = NavigationGutterIconBuilder.create(icon)
                .setCellRenderer(cellRenderer)
                .setTargets(definitionElements)
                .setTooltipText(
                    if (optionsCount > 0 && testCaseCount > 0 || optionsCount == 0)
                        PluginBundle.message("linemarker.flexmark.test-or-option")
                    else
                        if (optionsCount > 0 && testCaseCount > 0) PluginBundle.message("linemarker.flexmark.option")
                        else PluginBundle.message("linemarker.flexmark.test"))

            val useLeafPsiElement = MdPsiImplUtil.getLeafPsiElement(leafElement)
            result.add(builder.createLineMarkerInfo(useLeafPsiElement))
        }
        return true
    }

    internal class SpecOptionCellRenderer(private val optionsCount: Int, private val testCaseDefinitionsMap: PsiMap<PsiClass, ArrayList<Array<FlexmarkExampleOptionDefinition>>>) : PsiElementListCellRenderer<PsiElement>() {
        override fun getElementText(psiElement: PsiElement): String {
            if (psiElement is PsiLiteralExpression) {
                if (FlexmarkPsiImplUtils.getSpecResourceLiteralOrNull(psiElement) != null) {
                    return psiElement.getText()
                } else {
                    // assume option, take its contents
                    val text = psiElement.getText()
                    if (text != null) {
                        return text.substring(1, text.length - 1)
                    }
                }
            }

            if (psiElement is PsiClass) {
                return psiElement.name ?: ""
            }

            return psiElement.text
        }

        override fun getIcon(psiElement: PsiElement): Icon {
            if (psiElement is PsiLiteralExpression || psiElement is FakePsiLiteralExpression) {
                val literalElement = if (psiElement is FakePsiLiteralExpression) psiElement.literalExpression else psiElement
                if (FlexmarkPsiImplUtils.getSpecResourceLiteralOrNull(literalElement) != null) {
                    val testCaseClass = FlexmarkPsiImplUtils.getElementPsiClass(literalElement)
                    val definitions = testCaseDefinitionsMap[testCaseClass]
                    if (definitions != null) {
                        return if (definitions.size < optionsCount) FlexmarkIcons.Element.FLEXMARK_SPEC_ERRORS else FlexmarkIcons.Element.FLEXMARK_SPEC
                    }
                } else {
                    // assume option, take its contents
                    return FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE
                }
            }
            return psiElement.getIcon(0)
        }

        override fun getContainerText(psiElement: PsiElement, name: String): String? {
            return psiElement.containingFile.name
        }

        override fun getIconFlags(): Int {
            return Iconable.ICON_FLAG_READ_STATUS
        }
    }

    companion object {
        @JvmField
        val SPEC_EXAMPLE: GutterIconDescriptor.Option = GutterIconDescriptor.Option(MdLineMarkerProvider.ID_PREFIX + ".spec.example", "Flexmark: Spec example", FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE)

        private val ourOptions = arrayOf(
            SPEC_EXAMPLE)
    }
}
