// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

import com.vladsch.plugin.util.min

/**
 * Prefix matcher used to remove indent and block quote or aside prefixes from content lines
 *
 * This is not a parsing prefix remover. It is only used to remove prefixes from block of lines which were
 * parsed into their parent context. So if any lines are missing the prefix then it is perfectly ok,
 * because this prefix must be optional or the parser would have caught it.
 *
 * @param maxLeadSpaces     maximum allowed leading spaces before marker, ie. allowed non-indenting spaces
 * @param marker            marker string, has no leading, trailing spaces nor embedded tabs
 * @param minTrailSpaces    minimum trailing spaces for prefix match, also minimum spaces which will be removed
 * @param maxTrailSpaces    maximum trailing spaces to remove, these are not discretionary spaces, if they are there after the marker, they will be removed
 *
 * NOTE: if marker is null or empty then maxLeadSpaces must be 0 and
 *       minTrailSpaces, maxTrailSpaces specifies the space range to remove
 */
class PrefixMatcher(marker: String?, val maxLeadSpaces: Int, val minTrailSpaces: Int, val maxTrailSpaces: Int) {

    val marker: String?

    init {
        assert(minTrailSpaces >= 0)

        if (marker == null || marker.isEmpty()) {
            // if no marker then only fixed trailing spaces and no leading spaces allowed
            assert(maxLeadSpaces == 0 && minTrailSpaces <= maxTrailSpaces)
            this.marker = null
        } else {
            assert(maxLeadSpaces >= 0)
            assert(minTrailSpaces <= maxTrailSpaces)

            assert(!marker.startsWith(' '))
            assert(!marker.endsWith(' '))
            assert(marker.indexOf('\t') == -1)

            this.marker = marker
        }
    }

    /**
     * Remove matching prefix from the given lines, lines not matching prefix are left as is
     * convert leading tabs to spaces if leading space removal takes part of the tab
     *
     * Modified line information is returned in the given arrays
     *
     * @param lines                 lines from which to strip prefix, result returned in place
     * @param lineIndexes           index of first char of each line
     * @param lineColumns           column of the first char of each line
     * @param lineExtraSpaces       number of spaces remaining from removed tabs at index/column for the line
     * @param startLine             first line index to process (included)
     * @param endLine               end index for lines to process (excluded)
     */
    fun removePrefix(lines: Array<CharSequence>, lineIndexes: Array<Int>, lineColumns: Array<Int>, lineExtraSpaces: Array<Int>, startLine: Int, endLine: Int) {
        if (maxLeadSpaces == 0 && marker == null) return

        for (i in startLine .. endLine - 1) {
            val line = lines[i]

            if (line.isEmpty()) continue

            var column = lineColumns[i]
            var extraSpaces = lineExtraSpaces[i]
            var index = lineIndexes[i]

            // remove non-indenting leading spaces
            if (extraSpaces > 0 && maxLeadSpaces > 0) {
                val removableSpaces = maxLeadSpaces.min(extraSpaces)
                extraSpaces -= removableSpaces
                column += removableSpaces
            }

            if (marker != null) {
                if (extraSpaces > 0 || !line.startsWith(marker)) continue // no match
                index += marker.length
                column += marker.length
            }

            // check criteria for min/max trailing spaces
            var removableSpaces = maxTrailSpaces
            while (removableSpaces > 0 && index < line.length) {
                if (extraSpaces == 0 && line[index] == '\t') {
                    extraSpaces = 4 - (column % 3)
                    index++
                }

                if (extraSpaces > 0) {
                    val removedSpaces = removableSpaces.min(extraSpaces)
                    removableSpaces -= removedSpaces
                    extraSpaces -= removedSpaces
                    column += removedSpaces
                } else {
                    if (line[index] == ' ') {
                        removableSpaces--
                        index++
                        column++
                    } else {
                        continue  // no match
                    }
                }
            }

            val removedSpaces = maxTrailSpaces - removableSpaces
            if (removedSpaces >= minTrailSpaces) {
                // prefix matched, remove it
                lineIndexes[i] = index
                lineColumns[i] = column
                lineExtraSpaces[i] = extraSpaces
            }
        }
    }

    companion object {
        @JvmField
        val EMPTY = PrefixMatcher(null, 0, 0, 0)

        @JvmField
        val NON_INDENTING_SPACES = PrefixMatcher(null, 0, 0, 3)

        @JvmField
        val BLOCK_QUOTE = PrefixMatcher(">", 3, 0, 1)

        @JvmField
        val ASIDE_BLOCK = PrefixMatcher("|", 3, 0, 1)

        @JvmField
        val CONTENT_INDENT_1 = PrefixMatcher(null, 0, 1, 1)

        @JvmField
        val CONTENT_INDENT_2 = PrefixMatcher(null, 0, 2, 2)

        @JvmField
        val CONTENT_INDENT_3 = PrefixMatcher(null, 0, 3, 3)

        @JvmField
        val CONTENT_INDENT_4 = PrefixMatcher(null, 0, 4, 4)

        @JvmField
        val CONTENT_INDENT_5 = PrefixMatcher(null, 0, 5, 5)

        @JvmField
        val CONTENT_INDENT_6 = PrefixMatcher(null, 0, 6, 6)

        @JvmField
        val CONTENT_INDENT_7 = PrefixMatcher(null, 0, 7, 7)

        @JvmField
        val CONTENT_INDENTS = arrayOf(EMPTY, CONTENT_INDENT_1, CONTENT_INDENT_2, CONTENT_INDENT_3, CONTENT_INDENT_4, CONTENT_INDENT_5, CONTENT_INDENT_6, CONTENT_INDENT_7)

        @JvmStatic
        fun basedOnContentIndent(indent: Int): PrefixMatcher {
            return if (indent < CONTENT_INDENTS.size) CONTENT_INDENTS[indent]
            else PrefixMatcher("", 0, indent, indent)
        }

        /**
         * Call to get contents of lines after prefix(es) have been removed
         *
         * @param lines                 lines from which prefixes were stripped
         * @param lineIndexes           index of first char of each line
         * @param lineColumns           column of the first char of each line
         * @param lineExtraSpaces       number of spaces remaining from removed tabs at index/column for the line
         * @param startLine             first line index to process (included)
         * @param endLine               end index for lines to process (excluded)
         * @param consumer              called for each line with line content after prefix removal
         *                              with (index:Int, prefixSpaces: Int, line: CharSequence, startColumn:Int) where:
         *                              index: index of the line in lines[]
         *                              prefixSpaces: how many spaces to prefix to the line
         *                              line: remaining characters of the line
         *                              startColumn: column position of the first character of the line
         */
        fun contentLines(
            lines: Array<CharSequence>,
            lineIndexes: Array<Int>,
            lineColumns: Array<Int>,
            lineExtraSpaces: Array<Int>,
            startLine: Int,
            endLine: Int,
            consumer: (index: Int, prefixSpaces: Int, line: CharSequence, startColumn: Int) -> Unit
        ) {
            for (i in startLine .. endLine - 1) {
                val line = lines[i]
                consumer.invoke(i, lineExtraSpaces[i], line.subSequence(lineIndexes[i], lines[i].length), lineColumns[i])
            }
        }
    }
}
