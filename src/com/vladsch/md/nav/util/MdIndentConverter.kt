// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util

import com.intellij.openapi.util.TextRange
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.plugin.util.toBased
import java.util.*

class MdIndentConverter {
    // verbatim content node
    private val content: BasedSequence
    // offset in parent of the containing verbatim element
    private val startOffsetInParent: Int

    // range within document of original lines without indent removal
    private val originalLines: ArrayList<TextRange>
    // range within document of lines adjusted for editing of fragment
    private val convertedLines: ArrayList<TextRange>

    /**
     * create indent converter from original and unprefixed/unsuffixed lines
     */
    constructor(contentChars: CharSequence, startOffsetInParent: Int, prefixedLines: List<BasedSequence>, unprefixedLines: List<BasedSequence>?, unsuffixedLines: List<BasedSequence>?) {
        this.content = contentChars.toBased()
        assert(content.builder.addAll(prefixedLines).toSequence() == content) { "Content not equal to lines\n" +
            "content: '${content.toVisibleWhitespaceString()}'\n" +
            "  lines: '${content.builder.addAll(prefixedLines).toSequence().toVisibleWhitespaceString()}'" }

        this.startOffsetInParent = startOffsetInParent

        val iMax = prefixedLines.size
        this.originalLines = ArrayList(iMax)
        this.convertedLines = ArrayList(iMax)

        var originalOffset = 0
        for (i in 0 until iMax) {
            val prefixedLine = prefixedLines[i]
            val prefixedLineLength = prefixedLine.length
            assert(prefixedLine == content.subSequence(originalOffset, originalOffset + prefixedLineLength)) { "[$i]: originalOffset: $originalOffset, line: `${prefixedLine.toVisibleWhitespaceString()}` content.subSequence `${content.subSequence(originalOffset, originalOffset + prefixedLineLength).toVisibleWhitespaceString()}`" }

            originalLines.add(TextRange(originalOffset, originalOffset + prefixedLineLength))
            var unprefixedLineLength = prefixedLineLength

            if (unprefixedLines != null && unprefixedLines.size > i) {
                val unprefixedLine = unprefixedLines[i]
                unprefixedLineLength = unprefixedLine.length
                if (prefixedLine.endsWith('\n') && !unprefixedLine.endsWith('\n')) {
                    // add implicit EOL
                    unprefixedLineLength++
                }
            }

            val prefixDiff = prefixedLineLength - unprefixedLineLength

            if (unsuffixedLines != null && unsuffixedLines.size > i) {
                val unsuffixedLine = unsuffixedLines[i]
                unprefixedLineLength = unsuffixedLine.length
            } else {
                if (prefixedLine.endsWith('\n'))
                    unprefixedLineLength--
            }

            val endIndex = originalOffset + prefixDiff + unprefixedLineLength
            assert(endIndex <= originalOffset + prefixedLine.length) { "endIndex: $endIndex > originalOffset: $originalOffset + prefixedLine.length: ${prefixedLine.length}" }
            assert(endIndex <= content.length) { "endIndex: $endIndex > content.length: ${content.length}" }
            convertedLines.add(TextRange(originalOffset + prefixDiff, endIndex))

            // original lines length
            originalOffset += prefixedLineLength
        }
    }

    fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
        var lineCount = 0
        val startOffset = rangeInsideHost.startOffset - startOffsetInParent
        val endOffset = rangeInsideHost.endOffset - startOffsetInParent

        // find the starting line
        while (lineCount < convertedLines.size && startOffset > convertedLines[lineCount].endOffset) {
            lineCount++
        }

        while (lineCount < convertedLines.size) {
            if (convertedLines[lineCount].startOffset >= endOffset) break

            // add the text
            if (convertedLines[lineCount].endOffset <= endOffset) {
                // characters were stripped so we must copy to end of this line then add \n
                outChars.append(content.subSequence(convertedLines[lineCount].startOffset, convertedLines[lineCount].endOffset))
                outChars.append('\n')
            } else {
                outChars.append(content.subSequence(convertedLines[lineCount].startOffset, endOffset))
            }

            if (convertedLines[lineCount].endOffset >= endOffset) {
                break
            }

            lineCount++
        }

        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
        var lineCount = 0
        var decodedOffset = 0
        val endOfContent = startOffsetInParent + originalLines[originalLines.size - 1].endOffset

        // find the starting line
        while (lineCount < convertedLines.size) {
            val length = convertedLines[lineCount].length

            if (offsetInDecoded <= decodedOffset + length) {
                return startOffsetInParent + convertedLines[lineCount].startOffset + offsetInDecoded - decodedOffset  // return offset into the converted content
            }

            decodedOffset = endOfContent.coerceAtMost(decodedOffset + length + 1) // add 1 for implicit EOL
            lineCount++
        }

        if (decodedOffset + 1 >= offsetInDecoded) {
            // return end of content
            return endOfContent
        }

        return -1
    }

    companion object {
        val FULL_INDENT = "    "

        // NOTE: for fenced prefixed blocks the leading and trailing prefixes are removed when passed to IntelliLang but
        //     here we have to include them because the whole verbatim content will be replaced.
        fun encode(newContent: String, firstIndentPrefix: String, indentPrefix: String, isFenced: Boolean, endLineSuffix: String?, excludeSuffixAfter: String?): String {
            var lastPos = 0
            var pos: Int
            val firstIndent = normalizeIndent(firstIndentPrefix, isFenced)
            val restIndent = normalizeIndent(indentPrefix, isFenced)
            val outChars = StringBuilder(newContent.length + (indentPrefix.length + 20))
            var prefix = firstIndent
            val suffix = endLineSuffix ?: ""
            var subSequence: CharSequence

            while (true) {
                pos = if (lastPos < newContent.length) newContent.indexOf('\n', lastPos) else -1
                if (pos < 0) {
                    if (lastPos < newContent.length) {
                        outChars.append(prefix)

                        subSequence = newContent.subSequence(lastPos, newContent.length)
                        if (!suffix.isEmpty()) subSequence = trimEnd(subSequence, suffix)

                        if (subSequence.length > 0) {
                            outChars.append(subSequence)

                            if (excludeSuffixAfter == null || excludeSuffixAfter.indexOf(subSequence[subSequence.length - 1]) < 0) {
                                outChars.append(suffix)
                            }
                        }
                        outChars.append('\n')
                    }
                    break
                } else {
                    outChars.append(prefix)
                    prefix = restIndent

                    subSequence = newContent.subSequence(lastPos, pos)
                    if (!suffix.isEmpty()) subSequence = trimEnd(subSequence, suffix)

                    if (subSequence.length > 0) {
                        outChars.append(subSequence)
                        if (!suffix.isEmpty() && (excludeSuffixAfter == null || excludeSuffixAfter.indexOf(subSequence[subSequence.length - 1]) < 0)) {
                            outChars.append(suffix)
                        }
                    }

                    outChars.append('\n')
                    lastPos = pos + 1
                }
            }

            //if (isFenced) outChars.append(indent)
            return outChars.toString()
        }

        private fun trimEnd(sequence: CharSequence, stripEndLine: String): CharSequence {
            val iMax = sequence.length
            var lastEnd = iMax
            var i = iMax
            while (i-- > 0) {
                val c = sequence[i]
                if (lastEnd - i - 1 < stripEndLine.length && c == stripEndLine[lastEnd - i - 1]) {
                    // skip strip end line
                    continue
                }

                if (c != ' ' && c != '\t') {
                    return sequence.subSequence(0, i + 1)
                }

                lastEnd = i
            }
            return sequence.subSequence(0, 0)
        }

        internal fun normalizeIndent(indentPrefix: String, isFenced: Boolean): String {
            var normalized = ""
            val iMax = indentPrefix.length

            for (i in 0 .. iMax - 1) {
                val c = indentPrefix[i]
                if (c == '\t') {
                    val count = 4 - i % 4
                    normalized += FULL_INDENT.substring(0, count)
                } else {
                    normalized += c
                }
            }

            return if (isFenced) normalized else normalized + FULL_INDENT
        }
    }
}
