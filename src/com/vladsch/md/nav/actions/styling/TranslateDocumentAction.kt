// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.formatter.RenderPurpose
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.md.nav.parser.Extensions
import com.vladsch.md.nav.parser.PegdownOptionsAdapter
import com.vladsch.md.nav.parser.api.HtmlPurpose
import com.vladsch.md.nav.parser.api.ParserPurpose
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.util.translator.yandex.YandexTranslator
import com.vladsch.md.nav.vcs.GitHubLinkResolver

class TranslateDocumentAction : AnAction() {
    override fun isDumbAware(): Boolean {
        return false
    }

    override fun update(e: AnActionEvent) {
        val documentSettings = MdApplicationSettings.instance.documentSettings
        if (documentSettings.showTranslateDocument) {
            MdActionUtil.getConditionBuilder(e, this)
                .and(documentSettings.yandexKey.isNotEmpty()
                    , "No Yandex.Translate API key"
                    , "Translation is disabled when Yandex.Translate API key is not set (Languages & Frameworks > Markdown)"
                )
                .and(documentSettings.yandexToLanguage.isNotEmpty()
                    , "To language not defined"
                    , "Translation is disabled when To language is not set (Languages & Frameworks > Markdown)"
                )
                .and(documentSettings.yandexFromLanguage.isNotEmpty()
                    , "From language not defined"
                    , "Translation is disabled when From language is not set (Languages & Frameworks > Markdown)"
                )
                .and(documentSettings.yandexFromLanguage != documentSettings.yandexToLanguage
                    , "From language = To Language"
                    , "Translation is disabled when From language the same as To language (Languages & Frameworks > Markdown)"
                )
                .done()
        } else {
            e.presentation.isVisible = false
        }
        super.update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val documentSettings = MdApplicationSettings.instance.documentSettings
        if (project != null ||
            documentSettings.yandexKey.isNotEmpty() ||
            documentSettings.yandexToLanguage.isNotEmpty() ||
            documentSettings.yandexFromLanguage.isNotEmpty() ||
            documentSettings.yandexFromLanguage != documentSettings.yandexToLanguage
        ) {
            MdActionUtil.getProjectEditorPsiFile(e)?.let { (project, editor, psiFile) ->
                val text = editor.document.text
                val renderingProfile = MdRenderingProfileManager.getProfile(psiFile)
                val parserSettings = renderingProfile.parserSettings
                val parserOptions = parserSettings.optionsFlags
                val addOns = 0
                val pegdownFlags = (renderingProfile.parserSettings.pegdownFlags and Extensions.EXTANCHORLINKS_WRAP.inv()) or Extensions.MULTI_LINE_IMAGE_URLS
                val optionAdapter = PegdownOptionsAdapter(pegdownFlags or addOns, parserOptions)
                val options = optionAdapter.getFlexmarkOptions(ParserPurpose.JAVAFX, HtmlPurpose.RENDER, GitHubLinkResolver(psiFile), renderingProfile).toMutable()

                options.set(Parser.BLANK_LINES_IN_AST, true)
                    .set(Parser.HTML_FOR_TRANSLATOR, true)
                    .set(Parser.PARSE_INNER_HTML_COMMENTS, true)
                    .set(Formatter.MAX_TRAILING_BLANK_LINES, 0)

                val debugPrint = false
                val simulate = false

                ProgressManager.getInstance().run(object : Task.Modal(project, MdBundle.message("translation.progress.title"), true) {
                    val totalCount = 100 + 5
                    var count = 0

                    fun updateProgress(indicator: ProgressIndicator, message: String?, status: String? = null): Boolean {
                        indicator.isIndeterminate = false
                        if (message != null) {
                            indicator.text = message
                        }

                        indicator.text2 = status ?: ""

                        if (indicator.isCanceled) {
                            return true
                        }

                        indicator.fraction = count.toDouble() / totalCount.toDouble()
                        return false
                    }

                    override fun run(indicator: ProgressIndicator) {
                        if (updateProgress(indicator, MdBundle.message("translation.progress.parsing"))) return
                        val parser = Parser.builder(options).build()
                        val formatter = Formatter.builder(options).build()

                        // 1. Parse the document to get markdown AST
                        val document = parser.parse(BasedSequence.of(text))
                        count++

                        // 2. Format the document to get markdown strings for translation
                        if (updateProgress(indicator, MdBundle.message("translation.progress.extracting"))) return
                        val handler = formatter.translationHandler
                        formatter.translationRender(document, handler, RenderPurpose.TRANSLATION_SPANS)
                        count++

                        // 3. Get the strings to be translated from translation handler
                        val translatingTexts = handler.translatingTexts

                        if (translatingTexts.isEmpty()) {
                            indicator.stop()
                            ApplicationManager.getApplication().invokeLater {
                                Messages.showMessageDialog(
                                    editor.contentComponent,
                                    MdBundle.message("translation.nothing-to-translate.message"),
                                    MdBundle.message("translation.message-box.title"),
                                    Messages.getInformationIcon())
                            }
                            return
                        }

                        // 4. Have the strings translated by your translation service of preference
                        val translator = YandexTranslator(documentSettings.yandexKey)
                        var i = 0
                        val iMax = translatingTexts.size
                        var translatingList = ArrayList<String>()
                        var accumulatedTextLength = 0
                        val maxTextLen = translator.maxTextLength / 2
                        val maxStrings = 20
                        val translatingChunks = ArrayList<List<String>>()
                        var totalStrings: Int = 0
                        var totalChars = 0
                        while (i < iMax) {
                            val translating = translatingTexts[i++]
                            accumulatedTextLength += translating.length
                            if (accumulatedTextLength >= maxTextLen || translatingList.size >= maxStrings) {
                                totalStrings += translatingList.size
                                translatingChunks.add(translatingList)
                                translatingList = ArrayList()
                                if (debugPrint) System.out.println("Chunking ${translatingList.size} strings, total chars $accumulatedTextLength")
                                accumulatedTextLength = translating.length
                            }

                            totalChars += translating.length
                            translatingList.add(translating)
                        }

                        if (translatingList.isNotEmpty()) {
                            totalStrings += translatingList.size
                            translatingChunks.add(translatingList)
                        }

                        // do the translation in chunks
                        val maxChunks = translatingChunks.size
                        var chunkCount = 0
                        val translatedTexts = ArrayList<CharSequence>(translatingTexts.size)
                        val countCopy = count
                        var stringCount = 0
                        var charCount = 0

                        for (translateList in translatingChunks) {
                            chunkCount++
                            count = countCopy + (100.0 * chunkCount / maxChunks).toInt()
                            stringCount += translateList.size
                            var chars = 0
                            translateList.forEach { chars += it.length }
                            charCount += chars

                            val status = String.format("%d/%d strings - %d/%d chars", stringCount, totalStrings, charCount, totalChars)
                            if (updateProgress(indicator, MdBundle.message("translation.progress.translating", chunkCount, chars), status)) return

                            if (debugPrint) System.out.println("Translating ${translateList.size} strings")

                            val translatedList =
                                if (simulate) ArrayList<String>(translateList)
                                else translator.translate(translateList, documentSettings.yandexFromLanguage, documentSettings.yandexToLanguage, documentSettings.translateAutoDetect)

                            if (simulate) {
                                Thread.sleep(1000)
                            }

                            if (debugPrint) System.out.println("Received ${translatedList.size} strings")

                            if (translatedList.size != translateList.size) {
                                indicator.stop()
                                ApplicationManager.getApplication().invokeLater {
                                    Messages.showMessageDialog(
                                        editor.contentComponent,
                                        MdBundle.message("translation.wrong-count.message", translateList.size, translatedList.size),
                                        MdBundle.message("translation.message-box.title"),
                                        Messages.getInformationIcon())
                                }
                                return
                            }

                            for (translated in translatedList) {
                                val fixedPeriods = translated.replace("\n.", ".\n")
                                if (debugPrint) System.out.println("Adding translation \n$fixedPeriods\n")
                                translatedTexts.add(fixedPeriods)
                            }
                        }

                        count += 100
                        if (updateProgress(indicator, MdBundle.message("translation.progress.substitute"))) return

                        if (translatedTexts.isEmpty()) {
                            indicator.stop()
                            ApplicationManager.getApplication().invokeLater {
                                Messages.showMessageDialog(
                                    editor.contentComponent,
                                    MdBundle.message("translation.received-nothing.message"),
                                    MdBundle.message("translation.message-box.title"),
                                    Messages.getInformationIcon())
                            }
                            return
                        }

                        // 5. Set the translated strings in the translation handler
                        handler.setTranslatedTexts(translatedTexts)

                        // 6. Generate markdown with placeholders for non-translating string and out of context translations
                        // the rest will already contain translated text
                        val partial = formatter.translationRender(document, handler, RenderPurpose.TRANSLATED_SPANS)
                        count++

                        if (updateProgress(indicator, MdBundle.message("translation.progress.parsing-translated"))) return
                        // 7. Parse the document with placeholders
                        val partialDoc = parser.parse(partial)
                        count++

                        if (updateProgress(indicator, MdBundle.message("translation.progress.resolving"))) return
                        // 8. Generate the final translated markdown
                        val translatedText = formatter.translationRender(partialDoc, handler, RenderPurpose.TRANSLATED)

                        indicator.stop()

                        if (translatedText != null && !translatedText.isBlank() && translatedText != text) {
                            ApplicationManager.getApplication().invokeLater {
                                WriteCommandAction.runWriteCommandAction(project, Runnable {
                                    editor.document.replaceString(0, editor.document.textLength, translatedText)
                                })
                            }
                        } else {
                            // inform user if translated text was empty or equal to current text
                            ApplicationManager.getApplication().invokeLater {
                                Messages.showMessageDialog(
                                    editor.contentComponent,
                                    if (translatedText.isEmpty()) MdBundle.message("translation.translated-empty.message")
                                    else MdBundle.message("translation.translated-same.message"),
                                    MdBundle.message("translation.message-box.title"),
                                    Messages.getInformationIcon())
                            }
                        }
                    }
                })
            }
        }
    }
}
