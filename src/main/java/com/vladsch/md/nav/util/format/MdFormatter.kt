// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.format.TrackedOffset
import com.vladsch.flexmark.util.misc.CharPredicate.HASH
import com.vladsch.flexmark.util.misc.CharPredicate.SPACE
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.flexmark.util.sequence.builder.SequenceBuilder
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.editor.api.MdFormatCustomizationProvider
import com.vladsch.md.nav.editor.api.MdPreviewCustomizationProvider
import com.vladsch.md.nav.language.DiscretionaryText
import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.language.TrailingMarkerEqualizeOptions
import com.vladsch.md.nav.parser.PegdownOptionsAdapter
import com.vladsch.md.nav.parser.api.HtmlPurpose
import com.vladsch.md.nav.parser.api.ParserPurpose
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.settings.ListIndentationType
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.toBased

class MdFormatter {
    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.util.format")

        @JvmStatic
        fun formatFile(mdFile: MdFile): SequenceBuilder {
            val charSequence = BasedSequence.of(mdFile.text)
            val editContext = PsiEditAdjustment(mdFile, charSequence)

            val resolver = GitHubLinkResolver(mdFile)
            val parserOptions = PegdownOptionsAdapter().getFlexmarkOptions(ParserPurpose.PARSER, HtmlPurpose.RENDER, resolver, editContext.renderingProfile)

            val formatOptionsAdapter = FlexmarkFormatOptionsAdapter(editContext, editContext.charSequence.startOffset, editContext.charSequence.endOffset)
            val formatOptions = formatOptionsAdapter.formatOptions
            return formatFile(null, parserOptions, formatOptions, charSequence, null)
        }

        @JvmStatic
        fun formatFile(mdFile: MdFile, editor: Editor, charSequence: CharSequence?, trackedOffsets: List<TrackedOffset>?): SequenceBuilder {
            val useCharSequence = if (charSequence != null) BasedSequence.of(charSequence) else BasedSequence.of(editor.document.immutableCharSequence)
            val editContext = PsiEditAdjustment(mdFile, useCharSequence, editor)

            val resolver = GitHubLinkResolver(mdFile)
            val parserOptions = PegdownOptionsAdapter().getFlexmarkOptions(ParserPurpose.PARSER, HtmlPurpose.RENDER, resolver, editContext.renderingProfile)

            val formatOptionsAdapter = FlexmarkFormatOptionsAdapter(editContext, editContext.charSequence.startOffset, editContext.charSequence.endOffset)
            val formatOptions = formatOptionsAdapter.formatOptions
            return formatFile(null, parserOptions, formatOptions, useCharSequence, trackedOffsets)
        }

        /**
         * NOTE: Formatter.RESTORE_TRACKED_SPACES is not set or cleared here
         */
        @JvmStatic
        fun formatFile(documentOptions: DataHolder?, parserOptions: DataHolder, formatOptions: DataHolder, charSequence: BasedSequence, trackedOffsets: List<TrackedOffset>?): SequenceBuilder {

            val useParserOptions = parserOptions.toMutable()
            useParserOptions.set(Parser.BLANK_LINES_IN_AST, true)

            // Allow customizations

            // Allow customizations
            for (provider in MdFormatCustomizationProvider.EXTENSIONS.value) {
                provider.customizeParserOptions(useParserOptions)
            }

            val parseSequence = charSequence.toString().toBased()
            val document = Parser.builder(useParserOptions).build().parse(parseSequence)

            if (trackedOffsets != null) {
                document.set(Formatter.TRACKED_OFFSETS, trackedOffsets)
                document.set(Formatter.TRACKED_SEQUENCE, charSequence)
            }

            documentOptions?.setIn(document)

            val formatter = Formatter.builder(formatOptions).build()
            val builder = parseSequence.builder
            // NOTE: format adapter will change max blank lines for flexmark spec files
            try {
                formatter.render(document, builder, Formatter.MAX_TRAILING_BLANK_LINES.get(formatOptions))
            } catch (e: IllegalAccessException) {
                MdPreviewCustomizationProvider.textErrorReport("formatFile", e, "Markdown", document.chars.toString())
                LOG.error("formatFile error", e)
            }
            return builder
        }

        @JvmStatic
        fun listNeedsBlankLineBefore(element: MdList, indentationType: ListIndentationType?, assumeNoBlankLineBefore: Boolean): Boolean {
            if (assumeNoBlankLineBefore || !MdPsiImplUtil.isPrecededByBlankLine(element)) {
                var prevSibling = MdPsiImplUtil.getBlockElement(MdPsiImplUtil.prevNonWhiteSpaceSibling(element))
                if (prevSibling !is MdListDelimiter || prevSibling !== element.parent) {
                    var parent = element.parent
                    while (prevSibling == null) {
                        if (parent is PsiFile) {
                            prevSibling = null
                            break
                        }
                        prevSibling = MdPsiImplUtil.getBlockElement(parent.prevSibling)
                        parent = parent.parent
                    }

                    if (prevSibling is MdListDelimiter) {
                        while (prevSibling is MdListDelimiter) {
                            if (parent is PsiFile) {
                                break
                            }

                            prevSibling = parent
                            parent = parent.parent
                        }
                        prevSibling = prevSibling?.prevSibling
                    }

                    if (prevSibling != null && prevSibling !is MdListItem) {
                        val prevIsItemParagraph = prevSibling is MdParagraph
                            && prevSibling.parent is MdListItem
                            && MdPsiImplUtil.isFirstIndentedBlockPrefix(prevSibling, false)
                        val firstItem = element.children[0]
                        val isBlankLine = prevSibling is MdBlankLine

                        return when (indentationType
                            ?: MdRenderingProfileManager.getProfile(element.containingFile).parserSettings.parserListIndentationType) {
                            ListIndentationType.COMMONMARK -> {
                                // if prev is not item paragraph or atx heading and first item is not bullet item or ordered item with start number 1
                                if (firstItem is MdOrderedListItem) {
                                    val matches = firstItem.actualTextPrefix(true).matches("^\\s*1(?:\\.|\\)).*".toRegex())
                                    prevSibling !is MdAtxHeader &&
                                        prevSibling !is MdList &&
                                        !isBlankLine &&
                                        !(prevIsItemParagraph || matches)
                                } else {
                                    false
                                }
                            }
                            ListIndentationType.GITHUB -> {
                                // if prev is not item paragraph or atx heading or item has no space following it
                                firstItem is MdOrderedListItem && prevSibling !is MdAtxHeader && !isBlankLine && !prevIsItemParagraph
                            }
                            else -> !prevIsItemParagraph && !isBlankLine
                        }
                    }
                }
            }
            return false
        }

        @JvmStatic
        fun listItemNeedsSpaceAfterMarker(element: MdListItem, indentationType: ListIndentationType): Boolean {
            val prefix = element.actualTextPrefix(false)
            return when (indentationType) {
                ListIndentationType.COMMONMARK -> {
                    false
                }
                ListIndentationType.GITHUB -> {
                    !prefix.endsWith(" ") && !prefix.endsWith("\t")
                }
                else -> {
                    !prefix.endsWith(" ") && !prefix.endsWith("\t")
                }
            }
        }

        @JvmStatic
        fun formatAtxHeader(headerText: CharSequence, styleSettings: MdCodeStyleSettings, deletePos: Int): BasedSequence {
            val headerChars = headerText.toBased()
            var headerLine = headerChars.trimEnd()
            val trimmedEnd = headerChars.length - headerLine.length
            val leadMarkers = headerChars.countLeading(HASH)

            if (leadMarkers > 0) {
                if (styleSettings.SPACE_AFTER_ATX_MARKER == DiscretionaryText.ADD.intValue) {
                    if (leadMarkers >= headerLine.length || headerLine[leadMarkers] != ' ') {
                        headerLine = headerLine.insert(leadMarkers, " ")
                    }
                } else if (styleSettings.SPACE_AFTER_ATX_MARKER == DiscretionaryText.REMOVE.intValue) {
                    if (leadMarkers < headerLine.length && headerLine[leadMarkers] == ' ') {
                        val leadSpaces = headerLine.countLeading(SPACE, leadMarkers)
                        headerLine = headerLine.delete(leadMarkers, leadMarkers + leadSpaces)
                    }
                }

                val trailMarkers = headerLine.countTrailing(HASH)

                if (styleSettings.ATX_HEADER_TRAILING_MARKER == TrailingMarkerEqualizeOptions.ADD.intValue ||
                    styleSettings.ATX_HEADER_TRAILING_MARKER == TrailingMarkerEqualizeOptions.EQUALIZE.intValue && trailMarkers > 0) {
                    if (styleSettings.ATX_HEADER_TRAILING_MARKER == TrailingMarkerEqualizeOptions.EQUALIZE.intValue && deletePos > headerLine.length - trailMarkers) {
                        // remove all
                        if (trailMarkers > 0) {
                            val trailingSpaces = headerLine.countTrailing(SPACE, headerLine.length - trailMarkers - 1)
                            headerLine = headerLine.delete(headerLine.length - trailMarkers - trailingSpaces, headerLine.length)
                        }
                    } else {
                        if (trailMarkers < leadMarkers) {
                            headerLine = headerLine.insert(headerLine.length, RepeatedSequence.repeatOf('#', leadMarkers - trailMarkers))
                        } else if (trailMarkers > leadMarkers) {
                            headerLine = headerLine.delete(headerLine.length - (trailMarkers - leadMarkers), headerLine.length)
                        }

                        val trailingSpaces = headerLine.countTrailing(SPACE, headerLine.length - leadMarkers - 1)
                        //println("getting '$headerLine'.countLeading(' ', index = $leadMarkers)")
                        val leadSpaces = headerLine.countLeading(SPACE, leadMarkers)

                        if (trailingSpaces < leadSpaces) {
                            headerLine = headerLine.insert(headerLine.length - leadMarkers, RepeatedSequence.ofSpaces(leadSpaces - trailingSpaces))
                        } else if (trailingSpaces > leadSpaces) {
                            headerLine = headerLine.delete(headerLine.length - leadMarkers - trailingSpaces + leadSpaces, headerLine.length - leadMarkers)
                        }
                    }
                } else if (styleSettings.ATX_HEADER_TRAILING_MARKER == DiscretionaryText.REMOVE.intValue) {
                    if (trailMarkers > 0) {
                        val trailingSpaces = headerLine.countTrailing(SPACE, headerLine.length - trailMarkers - 1)
                        headerLine = headerLine.delete(headerLine.length - trailMarkers - trailingSpaces, headerLine.length)
                    }
                }
            }

            if (trimmedEnd > 0) headerLine = headerLine.append(headerChars.subSequence(headerChars.length - trimmedEnd, headerChars.length))
            return headerLine
        }
    }
}
