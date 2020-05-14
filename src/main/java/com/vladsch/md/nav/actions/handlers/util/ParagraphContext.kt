// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.ReverseRegEx.util.RegExMatcher
import com.vladsch.ReverseRegEx.util.RegExPattern
import com.vladsch.ReverseRegEx.util.ReversePattern
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.format.CharWidthProvider
import com.vladsch.flexmark.util.format.TrackedOffset
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.misc.CharPredicate.SPACE
import com.vladsch.flexmark.util.misc.CharPredicate.SPACE_TAB
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.BasedSequence.NULL
import com.vladsch.flexmark.util.sequence.SequenceUtils
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.actions.api.MdElementContextInfoProvider
import com.vladsch.md.nav.parser.PegdownOptionsAdapter
import com.vladsch.md.nav.parser.api.HtmlPurpose
import com.vladsch.md.nav.parser.api.ParserPurpose
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.util.BlockQuotePrefix
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.ListIndentationType
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.util.format.FlexmarkFormatOptionsAdapter
import com.vladsch.md.nav.util.format.MdFormatter
import com.vladsch.plugin.util.minLimit
import com.vladsch.plugin.util.toBased

open class ParagraphContext(val context: CaretContextInfo, val paragraphInfo: ParagraphInfo) {
    val paragraph: BasedSequence get() = paragraphInfo.paragraph
    val startOffset: Int get() = paragraphInfo.startOffset
    val endOffset: Int get() = paragraphInfo.endOffset
    var firstPrefix: CharSequence = paragraphInfo.firstPrefix
    val prefix: CharSequence get() = paragraphInfo.prefix
    val indent: Int? get() = paragraphInfo.indent
    val replaceStartLinePrefix: CharSequence? = paragraphInfo.replaceStartLinePrefix
    val char: Char? = paragraphInfo.char
    val isDeleted: Boolean get() = paragraphInfo.isDeleted
    val stopElement: PsiElement? = paragraphInfo.stopElement
    val caretDelta: Int get() = paragraphInfo.caretDelta
    val prefixChanged: Boolean get() = paragraphInfo.prefixChanged

    open fun withOffset(caretDelta: Int): ParagraphContext? {
        // here we create a new paragraph context but offsetting by the changed prefix
        val newContext = CaretContextInfo.subContext(context, context.caretOffset + caretDelta)
        val newInfo = ParagraphInfo(paragraphInfo)
            .withStartOffset(startOffset + caretDelta)
            .withEndOffset(endOffset + caretDelta)
        return ParagraphContext(newContext, newInfo)
    }

    open fun adjustParagraph(adjustCaret: Boolean): Boolean {
        if (context.editor.caretModel.caretCount == 1 && startOffset < endOffset && endOffset <= context.document.textLength) {
            val caretOffset = context.caretOffset + caretDelta
            val paragraphSeq: BasedSequence = paragraph

            val trackedOffset = TrackedOffset.track(caretOffset, char, context.isDeleteOp)
            val trackedOffsets = listOf(trackedOffset)

            var startOffset = startOffset
            var firstPrefix = firstPrefix

            if (replaceStartLinePrefix != null && replaceStartLinePrefix != prefix) {
                // replace the first line prefix
                val firstLineStart = context.offsetLineStart(startOffset)
                startOffset = firstLineStart ?: startOffset
                firstPrefix = prefix
            }

            val renderingProfile = context.renderingProfile
            val parserSettings = renderingProfile.parserSettings
            val optionsAdapter = PegdownOptionsAdapter()
            val formatOptionsAdapter = FlexmarkFormatOptionsAdapter(context, paragraphSeq.startOffset, paragraphSeq.endOffset)

            val parserOptions = optionsAdapter.getFlexmarkOptions(ParserPurpose.PARSER, HtmlPurpose.RENDER, null, renderingProfile)

            val formatOptions = formatOptionsAdapter.formatOptions
                .set(Formatter.MAX_BLANK_LINES, 0)
                .set(Formatter.MAX_TRAILING_BLANK_LINES, if (paragraphSeq.endsWithEOL()) 0 else -1)

            val documentOptions = MutableDataSet()
                .set(Formatter.DOCUMENT_FIRST_PREFIX, firstPrefix)
                .set(Formatter.RESTORE_TRACKED_SPACES, true)
                .set(Formatter.DOCUMENT_PREFIX, prefix)

            val documentFormatted = MdFormatter.formatFile(documentOptions, parserOptions, formatOptions, paragraphSeq, trackedOffsets)
            val trackedIndex = trackedOffset.index
            val formattedSeq = documentFormatted.toSequence(paragraphSeq)

            if (context.file.getUserData(MdFile.FORMAT_RESULT) != null) {
                context.file.putUserData(MdFile.FORMAT_RESULT, formattedSeq)
            }

            // FIX: use segment information to do incremental update
            val originalChars = context.charSequence.subSequence(startOffset, this.endOffset)
            val diffInfo = getDiffInfo(originalChars, formattedSeq, false)
            if (diffInfo.isEqual || diffInfo.lastCharDiff) {
                //loggerParaAdj.debug { "already formatted, skipping" }
                if (char == null) {
                    context.showTooltip(MdBundle.message("tooltip.document.format.no-changes")) { }
                }
                return false
            }

            val replaceChars = if (diffInfo.firstDiff > 0 || diffInfo.lastDiff > 0) formattedSeq.subSequence(diffInfo.firstDiff, formattedSeq.length - diffInfo.lastDiff) else formattedSeq
            context.document.replaceString(startOffset + diffInfo.firstDiff, endOffset - diffInfo.lastDiff, replaceChars)

            if (adjustCaret && trackedOffset.isResolved) {
                context.editor.caretModel.currentCaret.moveToOffset(startOffset + trackedIndex)
                return true
            }

            if (!adjustCaret) return true
        }
        return false
    }

    companion object {
        const val MARKDOWN_START_LINE_CHAR: Char = SequenceUtils.LS     // https://www.fileformat.info/info/unicode/char/2028/index.htm LINE_SEPARATOR this one is not preserved but will cause a line break if not already at beginning of line

        @JvmStatic
        fun getContext(context: CaretContextInfo): ParagraphContext? {
            return MdElementContextInfoProvider.PROVIDER.value.getParagraphContext(context)
        }

        data class WrapSuspension(val name: String, val isFirstLineOnly: Boolean, val prefixPattern: RegExPattern?, val caretRegion: Boolean, val caretPattern: RegExPattern?)

        fun createContext(context: CaretContextInfo): ParagraphContext? {

            val wrappingContext = context.wrappingContext(context.caretOffset) ?: return null
            if (wrappingContext.formatElement == null) return null

            val wrappingLines = MdPsiImplUtil.linesForWrapping(wrappingContext.formatElement, false, false, true, context)
            if (wrappingLines.lineCount == 0) return null

            val isDeleted = context.isDeleted
            val rightMargin = context.renderingProfile.getRightMargin()
            val startOffset = wrappingContext.startOffset
            val caretColumn = context.caretOffset - context.caretLineStart
            val trailingWhitespace = context.caretLineChars.countTrailing(SPACE_TAB)
            val firstPrefix = if (wrappingContext.firstPrefixStart < wrappingContext.firstPrefixEnd) context.charSequence.subSequence(wrappingContext.firstPrefixStart, wrappingContext.firstPrefixEnd) else NULL

            var caretOffset = 0
            var char = context.char

            if (char == ' ' && isDeleted && context.isBlockQuoteStyleChar(context.beforeCaretChar)) {
                char = context.beforeCaretChar
                caretOffset--;
            }

            val isBlockQuoteStyleChar = context.isBlockQuoteStyleChar(char)

            val firstLineInfo = wrappingLines[0]
            val parserSettings = MdRenderingProfileManager.getProfile(context.file).parserSettings

            // NOTE: can no longer remove lazy continuation prefixes, needed for multiline URL images
//            for (info in wrappingLines) {
//                if (info.index > 0) {
//                    val trimmed = info.text.trimmedStart(SPACE_TAB)
//                    if (trimmed.length > 0) {
//                        wrappingLines.setPrefixLength(info.index, info.prefixLength + trimmed.length)
//                    }
//                }
//            }

            val wrappingCaretLine = (context.caretLine - wrappingContext.firstLine).minLimit(0)
            val removedPrefixLen = if (wrappingCaretLine < wrappingLines.lineCount) wrappingLines[wrappingCaretLine].prefixLength else 0
            val startLineOffset = context.offsetLineStart(startOffset) ?: 0

            val charWidthProvider = CharWidthProvider.NULL
            val rightMarginWidth = charWidthProvider.spaceWidth * rightMargin
            val wrapOnlyOnTypingSpace = MdApplicationSettings.instance.documentSettings.wrapOnlyOnTypingSpace
            val wrapOnlyCaretLineNeedsWrapping = false
            val specialWrappingChars = "-+*:"

            val noWrapAtStart = char != null && specialWrappingChars.indexOf(char) != -1 //MarkdownApplicationSettings.instance.documentSettings.wrapOnlyOnTypingSpace
            if (char != null) {
                // if typing at start of wrapping range: digits followed by . or ) then don't wrap unless margin is exceeded.
                val endOfLine = (context.caretLineEnd - trailingWhitespace).minLimit(startLineOffset)
                val endOfLineWidth = charWidthProvider.getStringWidth(context.charSequence.subSequence(context.caretLineStart, endOfLine))

                if (wrappingCaretLine < wrappingLines.lineCount) {
                    val caretText = wrappingLines[wrappingCaretLine].text
                    val unprefixedColumn = if (wrappingCaretLine == 0) wrappingContext.firstPrefixEnd - context.caretLineStart else wrappingLines[wrappingCaretLine].prefixLength
                    val unprefixedCaret = caretColumn - unprefixedColumn

                    if (unprefixedCaret >= 0 && unprefixedCaret <= caretText.length) {
                        val caretPrefix = wrappingLines[wrappingCaretLine].line.subSequence(0, unprefixedCaret)
                        val patternMap = listOf(
                            WrapSuspension("List Item Marker", false, null, true, ReversePattern.compile("^(?:[+*:-]|\\d+(?:[.)])?)(?:\\s\\[(?:[ a-z]]?)?\\s*\\n?)$")) // list items, including task items
                            , WrapSuspension("Task Item Marker", false, null, true, ReversePattern.compile("^(?:\\[(?:[ a-z]]?)?\\s*\\n?)$")) // task item suffix
                            , WrapSuspension("Atx Heading Marker", false, null, true, ReversePattern.compile("^(?:#{1,6}[ \\t]*)\\n?$")) // setext heading marker
                            , WrapSuspension("Setext Heading Marker", false, null, false, ReversePattern.compile("^(?:[=-]+[ \\t]*)\\n?$")) // setext heading marker
                            , WrapSuspension("Macro Closing Marker", false, null, false, ReversePattern.compile("^(?:[<]{1,3}[ \\t]*)\\n?$")) // macro terminator
                        )

                        for (suspension in patternMap) {
                            if (suspension.isFirstLineOnly && context.caretLine != wrappingContext.firstLine) continue
                            if (suspension.prefixPattern != null) {
                                val matcher: RegExMatcher = suspension.prefixPattern.matcher(caretPrefix)
                                if (!matcher.find()) continue
                            }

                            if (suspension.caretPattern == null) continue

                            val pattern = suspension.caretPattern
                            val matcher: RegExMatcher = pattern.matcher(caretText)

                            if (suspension.caretRegion) {
                                matcher.region(0, unprefixedCaret)
                                matcher.useTransparentBounds(true)
                                matcher.useAnchoringBounds(true)
                            }

                            if (matcher.find()) {
//                                loggerPara.debug {
//                                    "char $char, offset ${context.caretOffset}, startOffset $startOffset, caretColumn $caretColumn, removedPrefixLen $removedPrefixLen, end of line $endOfLineWidth <= $rightMarginWidth, skipping by ${suspension.name}"
//                                }
                                return null
                            }
                        }
                    }
                }

                if (!isDeleted) {
                    val noWrapBefore = context.beforeCaretChars.length >= 2 && specialWrappingChars.indexOf(context.beforeCaretChars[context.beforeCaretChars.length - 2]) != -1 //MarkdownApplicationSettings.instance.documentSettings.wrapOnlyOnTypingSpace
                    if (context.caretOffset >= endOfLine && (char == ' ' && !wrapOnlyOnTypingSpace) && endOfLineWidth <= rightMarginWidth ||
                        (caretColumn - 1 == removedPrefixLen && noWrapAtStart) ||
                        (caretColumn - 2 == removedPrefixLen && noWrapBefore && char == ' ') ||
                        ((caretColumn - 1 >= removedPrefixLen && context.caretOffset > startOffset) &&
                            ((wrapOnlyOnTypingSpace && char != ' ') || (wrapOnlyCaretLineNeedsWrapping && endOfLineWidth <= rightMarginWidth)))
                    ) {
//                        loggerPara.debug {
//                            "char $char, offset ${context.caretOffset}, startOffset $startOffset, caretColumn $caretColumn, removedPrefixLen $removedPrefixLen, end of line $endOfLineWidth <= $rightMarginWidth, skipping"
//                        }
                        return null
                    }
                }
            }

            if (isDeleted && !isBlockQuoteStyleChar) {
                val noWrapBefore = specialWrappingChars.indexOf(context.beforeCaretChar) != -1 //MarkdownApplicationSettings.instance.documentSettings.wrapOnlyOnTypingSpace
                val caretDiff = wrappingContext.endOffset - context.caretOffset
                val caretOffsetWidth = charWidthProvider.getStringWidth(context.charSequence.subSequence(startLineOffset, context.caretOffset.minLimit(startLineOffset)))
                val endOfLineWidth = charWidthProvider.getStringWidth(context.charSequence.subSequence(context.caretLineStart, context.caretLineEnd.minLimit(context.caretLineStart)))
                if ((caretColumn == removedPrefixLen && noWrapAtStart) || (caretColumn - 1 == removedPrefixLen && noWrapBefore && char == ' ') || caretDiff < 1 && caretOffsetWidth <= rightMarginWidth && endOfLineWidth <= rightMarginWidth) {
                    // at very end, we don't need to do anything
                    return null
                }
            }

            var itemPrefixDelta = 0
            var contPrefixDelta = 0
            var blockPrefixes = MdPsiImplUtil.getBlockPrefixes(wrappingContext.formatElement, null, context)
            var addedBlockQuote = false
            val isFirstTextBlock = wrappingContext.mainElement.node.elementType == MdTypes.PARAGRAPH_BLOCK && wrappingContext.mainElement.parent is PsiFile ||
                MdPsiImplUtil.isFirstIndentedBlockPrefix(wrappingContext.formatElement, false) {
                    it is MdListItem || it is MdFootnote || it is MdDefinition
                }

            if (isBlockQuoteStyleChar && context.caretLine == wrappingContext.firstLine) {
                // Need to adjust prefixes because they still reflect the pre-edit prefix
                val beforeCaret: BasedSequence = context.beforeCaretChars.subSequence(0, context.beforeCaretChars.length + caretOffset)
                val bqIndex = beforeCaret.countOfAny(context.blockQuoteStyleCharsSet)

                if (context.editOpDelta < 0) {
                    if (context.caretOffset <= wrappingContext.startOffset && context.caretOffset - context.editOpDelta > wrappingContext.firstPrefixStart) {
                        val removedBq = blockPrefixes.getBlockQuotePrefixAt(bqIndex)
                        blockPrefixes = blockPrefixes.removeBlockQuotePrefixAt(bqIndex)

                        if (bqIndex == 0 && removedBq.prefix is BlockQuotePrefix) {
                            if (blockPrefixes.getBlockQuotePrefixAt(0).prefix !is BlockQuotePrefix) {
                                // need to remove any spaces after we remove the last block quote prefix
                                // assume all the same, otherwise need to go through textToWrapLines and see how many spaces at caretColumn
                                // NOTE: add spaces after > skipped at start of method
                                val delta = context.afterCaretChars.countLeading(CharPredicate.SPACE_EOL) - caretOffset
                                itemPrefixDelta -= delta //(if (isFirstTextBlock) removedBq.itemPrefix else removedBq.itemContPrefix).asBased().countTrailing(" \t")
                                contPrefixDelta -= delta //(if (isFirstTextBlock) removedBq.itemContPrefix else removedBq.childContPrefix).asBased().countTrailing(" \t")
                                caretOffset += delta // adjust caret offset to move past spaces
                            }
                        }
                    }
                } else if (context.editOpDelta > 0) {
                    if (context.caretOffset <= wrappingContext.startOffset + context.editOpDelta &&
                        context.caretOffset - context.editOpDelta >= wrappingContext.firstPrefixStart
                    ) {
                        if (wrappingLines.isNotEmpty) {
                            val leadingIndents = firstLineInfo.text.countLeading(context.indentingCharsSet)
                            if (leadingIndents > 0) {
                                // remove inserted > from text
                                wrappingLines.setPrefixLength(0, firstLineInfo.prefixLength + leadingIndents)
                            }
                        }
                        val blockQuoteIndex = (bqIndex - 1).minLimit(0)
                        blockPrefixes = blockPrefixes.addBlockQuotePrefixAt(blockQuoteIndex, BlockQuotePrefix.create(isFirstTextBlock, char.toString(), char.toString()))
                        addedBlockQuote = blockPrefixes.getBlockQuotePrefixAt(blockQuoteIndex).prefix === blockPrefixes.last()
                    }
                }
            }

            val paragraphChars = wrappingLines.toSequence(0, -1, false).toBased()
            val prefixes = blockPrefixes.finalizePrefixes(context)
            var prefixChanged = true
            var adjustPrefix = parserSettings.parserListIndentationType != ListIndentationType.FIXED

            if (context.isInsertOp && char?.isDigit() != false) {
                val parent = wrappingContext.mainElement
                if (parent is MdOrderedListItemImpl) {
                    adjustPrefix = false
                    if (context.editOpDelta > 0 && caretColumn > 1 && caretColumn < firstPrefix.trimEnd().length) {
                        prefixChanged = true
                        if (MdPsiImplUtil.isFirstIndentedBlock(parent, false)) {
                            adjustPrefix = true
                        }
                    }
                }
            }

            var firstLinePrefix = if (isFirstTextBlock) prefixes.itemPrefix else prefixes.childPrefix
            var contLinePrefix = if (isFirstTextBlock) prefixes.itemContPrefix else prefixes.childContPrefix
            val firstPrefixLength =
                if (isBlockQuoteStyleChar) firstLinePrefix.length
                else firstPrefix.length + if (context.isInsertOp && char == ' ' && context.caretOffset - 1 == wrappingContext.startOffset) 1 else 0

            if (adjustPrefix) {
                if (isDeleted) {
                    if (isBlockQuoteStyleChar) {
                        firstLinePrefix = firstLinePrefix.padStart(firstPrefixLength)
                        contLinePrefix = contLinePrefix.padStart(firstPrefixLength)
                    } else {
                        firstLinePrefix = firstLinePrefix.padEnd(firstPrefixLength + itemPrefixDelta)
                        contLinePrefix = contLinePrefix.padEnd(firstPrefixLength + contPrefixDelta)
                    }
                } else {
                    if (char == ' ' && caretColumn <= firstPrefixLength || isBlockQuoteStyleChar) {
                        // before prefix, pad start
                        firstLinePrefix = firstLinePrefix.padStart(firstPrefixLength)
                        contLinePrefix = contLinePrefix.padStart(firstPrefixLength)
                    } else {
                        firstLinePrefix = firstLinePrefix.padEnd(firstPrefixLength)
                        contLinePrefix = contLinePrefix.padEnd(firstPrefixLength)
                    }
                }
            }

            // NOTE: if the prefixes do not contain non-block quote markers after block quote markers then
            // use the computed prefixes.
            //
            // Otherwise, have to use the actual prefix from the file and compute the continuation by aligning it with computed continuation.
            // ie. replace corresponding prefixes with spaces where they don't exist in computed continuation
            //
            // for example: first line prefix " >> 1. >| * " but computed continuation is "> >   > |  * "
            // this means that 1. in first line prefix has to be replaced with the same number of spaces and used for the continuation with
            // the actual prefix.
            //
            // otherwise, it is possible to re-format the leading block quote prefix to change the indent of a list item
            // however, subsequent list items' block quote prefix will not be changed, causing the alignment to change.
            //
            // This especially affects sub-items since they are sensitive to indentation relative to parent.

            // after this point firstLinePrefix and contLinePrefix have to be the ones used for the paragraph

            if (addedBlockQuote) {
                // move caret if spaces were inserted
                // only if using computed prefixes
                caretOffset += firstLinePrefix.countTrailing(SPACE)
            }

            val paragraphStartOffset = paragraphChars.startOffset
            val paragraphEndOffset = paragraphChars.endOffset
            val startOfLine = context.offsetLineStart(paragraphStartOffset) ?: paragraphStartOffset

            if (context.isInsertOp && !firstPrefix.isBlank && caretColumn < firstPrefix.length + 1 && !isBlockQuoteStyleChar) {
                if (firstLinePrefix.isBlank) {
                    // typing in the prefix of indented paragraph, no
                    return null
                } else if (char != ' ' && firstLinePrefix.indexOfAny(context.blockQuoteStyleChars.toCharArray()) != -1) {
                    // typing in the prefix with block quote like chars and not another block quote char
                    return null
                }
            }

            val paragraphInfo = ParagraphInfo(
                paragraphChars,
                startOfLine,
                paragraphEndOffset,
                firstLinePrefix,
                contLinePrefix,
                null,
                null,
                char,
                isDeleted,
                wrappingContext.mainElement.nextSibling,
                caretOffset,
                prefixChanged
            )

            return ParagraphContext(context, paragraphInfo)
        }
    }
}

open class ParagraphInfo(
    val paragraph: BasedSequence,
    val startOffset: Int,
    val endOffset: Int,
    val firstPrefix: CharSequence,
    val prefix: CharSequence,
    val indent: Int?,
    val replaceStartLinePrefix: CharSequence?,
    val char: Char?,
    val isDeleted: Boolean,
    val stopElement: PsiElement?,
    val caretDelta: Int,
    val prefixChanged: Boolean
) {

    constructor(paragraphInfo: ParagraphInfo) : this(
        paragraphInfo.paragraph,
        paragraphInfo.startOffset,
        paragraphInfo.endOffset,
        paragraphInfo.firstPrefix,
        paragraphInfo.prefix,
        paragraphInfo.indent,
        paragraphInfo.replaceStartLinePrefix,
        paragraphInfo.char,
        paragraphInfo.isDeleted,
        paragraphInfo.stopElement,
        paragraphInfo.caretDelta,
        paragraphInfo.prefixChanged
    )

    open fun withStartOffset(startOffset: Int): ParagraphInfo {
        return ParagraphInfo(
            paragraph,
            startOffset,
            endOffset,
            firstPrefix,
            prefix,
            indent,
            replaceStartLinePrefix,
            char,
            isDeleted,
            stopElement,
            caretDelta,
            prefixChanged
        )
    }

    open fun withEndOffset(endOffset: Int): ParagraphInfo {
        return ParagraphInfo(
            paragraph,
            startOffset,
            endOffset,
            firstPrefix,
            prefix,
            indent,
            replaceStartLinePrefix,
            char,
            isDeleted,
            stopElement,
            caretDelta,
            prefixChanged
        )
    }
}
