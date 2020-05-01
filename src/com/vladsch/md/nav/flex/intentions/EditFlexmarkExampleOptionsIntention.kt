// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.intentions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.JBColor
import com.intellij.util.IncorrectOperationException
import com.vladsch.flexmark.util.collection.OrderedMap
import com.vladsch.md.nav.flex.PluginBundle
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.highlighter.MdHighlighterColors
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.flex.psi.FlexmarkExample
import com.vladsch.md.nav.flex.psi.FlexmarkExampleParams
import com.vladsch.md.nav.flex.psi.FlexmarkPsi.FLEXMARK_OPTION_EMBED_TIMED
import com.vladsch.md.nav.flex.psi.FlexmarkPsi.FLEXMARK_OPTION_FAIL
import com.vladsch.md.nav.flex.psi.FlexmarkPsi.FLEXMARK_OPTION_FILE_EOL
import com.vladsch.md.nav.flex.psi.FlexmarkPsi.FLEXMARK_OPTION_IGNORE
import com.vladsch.md.nav.flex.psi.FlexmarkPsi.FLEXMARK_OPTION_NO_FILE_EOL
import com.vladsch.md.nav.flex.psi.FlexmarkPsi.FLEXMARK_OPTION_TIMED
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings
import com.vladsch.md.nav.util.PsiElementPredicate
import java.awt.Color
import java.util.*

class EditFlexmarkExampleOptionsIntention : FlexIntention() {
    private val highlighterColors = MdHighlighterColors.getInstance()

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        refactor(project, editor, element as FlexmarkExample)
    }

    override fun getElementPredicate(): PsiElementPredicate {
        return PsiElementPredicate { element ->
            element is FlexmarkExample
        }
    }

    override fun isAvailableIn(file: PsiFile): Boolean {
        return super.isAvailableIn(file) && MdApplicationSettings.instance.debugSettings.getExtension(FlexmarkDebugSettings.KEY).enableFlexmarkFeatures
    }

    protected fun getColor(editor: Editor, optionType: OptionType): Color {
        return when (optionType) {
            OptionType.IGNORE -> editor.colorsScheme.getAttributes(highlighterColors.FLEXMARK_EXAMPLE_OPTION_IGNORE_ATTR_KEY).foregroundColor
            OptionType.FAIL -> editor.colorsScheme.getAttributes(highlighterColors.FLEXMARK_EXAMPLE_OPTION_FAIL_ATTR_KEY).foregroundColor
            OptionType.BUILTIN -> editor.colorsScheme.getAttributes(highlighterColors.FLEXMARK_EXAMPLE_OPTION_BUILT_IN_ATTR_KEY).foregroundColor
            OptionType.DISABLED -> editor.colorsScheme.getAttributes(CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES).foregroundColor
            OptionType.KNOWN -> editor.colorsScheme.getAttributes(highlighterColors.FLEXMARK_EXAMPLE_OPTION_ATTR_KEY).foregroundColor
            OptionType.UNKNOWN -> {
                val errorAttribute = editor.colorsScheme.getAttributes(CodeInsightColors.ERRORS_ATTRIBUTES)
                errorAttribute?.foregroundColor ?: errorAttribute?.effectColor ?: errorAttribute?.errorStripeColor ?: JBColor.RED
            }
        }
    }

    @Throws(IncorrectOperationException::class)
    fun refactor(project: Project, editor: Editor, exampleElement: FlexmarkExample) {
        ApplicationManager.getApplication().invokeLater {
            // lets get the link text
            if (exampleElement.isValid) {
                val optionsMap = OrderedMap<String, OptionEntryType>()
                val factoryContext = MdFactoryContext(exampleElement)

                val optionDataKeys = HashMap<String, String>()
                val definitions = FlexmarkPsiImplUtils.getOptionDefinitions(exampleElement.mdFile, true)
                for (definition in definitions) {
                    val option = definition.optionName
                    if (!optionDataKeys.contains(option)) {
                        val element = definition.element
                        val dataKeys = FlexmarkPsiImplUtils.getFlexmarkExampleOptionDataKeys(element)
                        var paramText: String = ""
                        if (dataKeys != null) {
                            for (dataKey in dataKeys) {
                                val split = dataKey.substring(1, dataKey.length - 1).split(",".toRegex(), 2).toTypedArray()
                                var paramKey = split[0].trim { it <= ' ' } + " = "
                                if (split.size > 1) paramKey += split[1].trim { it <= ' ' }
                                if (paramText.isNotEmpty()) paramText += ", "
                                paramText += paramKey
                            }
                        }
                        optionDataKeys[option] = paramText
                    }
                }

                optionDataKeys[FLEXMARK_OPTION_IGNORE.optionName] = PluginBundle.message("refactoring.flexmark.example.option-ignore.description")
                optionDataKeys[FLEXMARK_OPTION_FAIL.optionName] = PluginBundle.message("refactoring.flexmark.example.option-fail.description")
                optionDataKeys[FLEXMARK_OPTION_EMBED_TIMED.optionName] = PluginBundle.message("refactoring.flexmark.example.option-embed-timed.description")
                optionDataKeys[FLEXMARK_OPTION_TIMED.optionName] = PluginBundle.message("refactoring.flexmark.example.option-timed.description")
                // NOTE: this one is not an option to use in spec example but set in data holder
//                optionDataKeys[FLEXMARK_OPTION_TIMED_ITERATIONS.optionName] = PluginBundle.message("refactoring.flexmark.example.option-timed-iterations.description")
                optionDataKeys[FLEXMARK_OPTION_NO_FILE_EOL.optionName] = PluginBundle.message("refactoring.flexmark.example.option-no-file-eol.description")
                optionDataKeys[FLEXMARK_OPTION_FILE_EOL.optionName] = PluginBundle.message("refactoring.flexmark.example.option-file-eol.description")

                val options = exampleElement.optionsList?.options?.toMutableList() ?: ArrayList<String>()
                for (option in options) {
                    val optionType = optionType(option, optionDataKeys)
                    optionsMap[option] = OptionEntryType(optionType, true, getColor(editor, optionType), optionDataKeys[option])
                }

                for (option in optionDataKeys) {
                    val optionType = optionType(option.key, optionDataKeys)
                    optionsMap.computeIfMissing(option.key) { OptionEntryType(optionType, false, getColor(editor, optionType), option.value) }
                }

                optionsMap.computeIfMissing(FLEXMARK_OPTION_IGNORE.optionName) { OptionEntryType(OptionType.IGNORE, false, getColor(editor, OptionType.IGNORE), optionDataKeys[FLEXMARK_OPTION_IGNORE.optionName]) }
                optionsMap.computeIfMissing(FLEXMARK_OPTION_FAIL.optionName) { OptionEntryType(OptionType.FAIL, false, getColor(editor, OptionType.FAIL), optionDataKeys[FLEXMARK_OPTION_FAIL.optionName]) }
                optionsMap.computeIfMissing(FLEXMARK_OPTION_EMBED_TIMED.optionName) { OptionEntryType(OptionType.BUILTIN, false, getColor(editor, OptionType.BUILTIN), optionDataKeys[FLEXMARK_OPTION_EMBED_TIMED.optionName]) }
                optionsMap.computeIfMissing(FLEXMARK_OPTION_TIMED.optionName) { OptionEntryType(OptionType.BUILTIN, false, getColor(editor, OptionType.BUILTIN), optionDataKeys[FLEXMARK_OPTION_TIMED.optionName]) }
                optionsMap.computeIfMissing(FLEXMARK_OPTION_NO_FILE_EOL.optionName) { OptionEntryType(OptionType.BUILTIN, false, getColor(editor, OptionType.BUILTIN), optionDataKeys[FLEXMARK_OPTION_NO_FILE_EOL.optionName]) }
                optionsMap.computeIfMissing(FLEXMARK_OPTION_FILE_EOL.optionName) { OptionEntryType(OptionType.BUILTIN, false, getColor(editor, OptionType.BUILTIN), optionDataKeys[FLEXMARK_OPTION_FILE_EOL.optionName]) }

                val doRefactor = ExampleOptionsRefactoringDialog(editor.component, optionsMap)
                if (doRefactor.showAndGet()) {
                    val editedOptions = doRefactor.options
                    if (options != editedOptions) { //options.containsAll(options) && editedOptions.containsAll(options)) {
                        WriteCommandAction.runWriteCommandAction(project) {
                            // create the reference then the rest of the elements
                            val newExample = MdElementFactory.createFlexmarkExample(factoryContext, FlexmarkExampleParams(exampleElement).withOptions(editedOptions.toList()))
                            if (newExample != null) {
                                exampleElement.replace(newExample)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun optionType(option: String?, optionDataKeys: HashMap<String, String>): OptionType {
        val optionType =
            when {
                option == FLEXMARK_OPTION_IGNORE.optionName -> OptionType.IGNORE
                option == FLEXMARK_OPTION_FAIL.optionName -> OptionType.FAIL
                option == FLEXMARK_OPTION_EMBED_TIMED.optionName -> OptionType.BUILTIN
                option == FLEXMARK_OPTION_TIMED.optionName -> OptionType.BUILTIN
                option == FLEXMARK_OPTION_NO_FILE_EOL.optionName -> OptionType.BUILTIN
                option == FLEXMARK_OPTION_FILE_EOL.optionName -> OptionType.BUILTIN
                optionDataKeys.contains(option) -> OptionType.KNOWN
                else -> {
                    if (option != null && option.startsWith("-")) OptionType.DISABLED
                    else OptionType.UNKNOWN
                }
            }
        return optionType
    }
}

