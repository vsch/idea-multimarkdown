/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.util

import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin
import com.vladsch.idea.multimarkdown.TestUtils.compareOrderedLists
import com.vladsch.idea.multimarkdown.parser.LexerToken
import com.vladsch.idea.multimarkdown.parser.MultiMarkdownLexParser
import com.vladsch.idea.multimarkdown.printData
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.pegdown.Extensions
import org.pegdown.ast.RootNode
import java.util.*

@RunWith(value = Parameterized::class)
class LexParserTest constructor(val rowId: Int, val markdownInput: String, val rootNode: RootNode, val lexerTokens: Array<LexerToken>) {
    val currentChars = markdownInput.toCharArray()

    @Test
    fun testBasic() {
        MultiMarkdownPlugin.testing()
        var lexParser = MultiMarkdownLexParser()
        val actual = lexParser.parseMarkdown(rootNode, currentChars, MultiMarkdownLexParser.GITHUB_WIKI_LINKS)
        accumulateTokenData(rowId, lexerTokens, actual)
        compareOrderedLists(null, lexerTokens, actual)
    }

//    @Test
    fun testBasicTiming() {
        // used to do rough
        MultiMarkdownPlugin.testing()
        for (i in 1..100000) {
            val lexParser = MultiMarkdownLexParser()
            val actual = lexParser.parseMarkdown(rootNode, currentChars, MultiMarkdownLexParser.GITHUB_WIKI_LINKS)
        }
    }

    companion object {
        val accumulatedData = ArrayList<Array<Any?>>()

        fun accumulateTokenData(rowId: Int, expected: Array<LexerToken>, actual: Array<LexerToken>) {
            var equal = expected.size == actual.size
            if (equal) {
                for (i in expected.indices) {
                    if (expected[i].compareTo(actual[i]) != 0) {
                        equal = false
                        break
                    }
                }
            }

            if (!equal) {
                accumulatedData.add(arrayOf(rowId, actual))
                printAccum()
            }
        }

        fun printAccum() {
            val header = arrayOf("rowID", "lexerTokens")
            val stringers = mapOf<String, (Array<Any?>) -> String>(
                    //                        Pair("pegdownOptions", { row -> stringPegDownOptions(row[1] as Int) }),
                    Pair("lexerTokens", { row -> stringLexerTokens(row[1] as Array<LexerToken>) })
            )
            printData(accumulatedData, header, stringers)
        }

        val optionsMap = mapOf<Int, String>(
                Pair(Extensions.SMARTS, "SMARTS"),
                Pair(Extensions.QUOTES, "QUOTES"),
                Pair(Extensions.SMARTYPANTS, "SMARTYPANTS"),
                Pair(Extensions.ABBREVIATIONS, "ABBREVIATIONS"),
                Pair(Extensions.HARDWRAPS, "HARDWRAPS"),
                Pair(Extensions.AUTOLINKS, "AUTOLINKS"),
                Pair(Extensions.TABLES, "TABLES"),
                Pair(Extensions.DEFINITIONS, "DEFINITIONS"),
                Pair(Extensions.FENCED_CODE_BLOCKS, "FENCED_CODE_BLOCKS"),
                Pair(Extensions.WIKILINKS, "WIKILINKS"),
                Pair(Extensions.STRIKETHROUGH, "STRIKETHROUGH"),
                Pair(Extensions.ANCHORLINKS, "ANCHORLINKS"),
                Pair(Extensions.ALL, "ALL"),
                Pair(Extensions.SUPPRESS_HTML_BLOCKS, "SUPPRESS_HTML_BLOCKS"),
                Pair(Extensions.SUPPRESS_INLINE_HTML, "SUPPRESS_INLINE_HTML"),
                Pair(Extensions.SUPPRESS_ALL_HTML, "SUPPRESS_ALL_HTML"),
                Pair(Extensions.ATXHEADERSPACE, "ATXHEADERSPACE"),
                Pair(Extensions.FORCELISTITEMPARA, "FORCELISTITEMPARA"),
                Pair(Extensions.RELAXEDHRULES, "RELAXEDHRULES"),
                Pair(Extensions.TASKLISTITEMS, "TASKLISTITEMS"),
                Pair(Extensions.EXTANCHORLINKS, "EXTANCHORLINKS"),
                Pair(Extensions.EXTANCHORLINKS_WRAP, "EXTANCHORLINKS_WRAP"),
                Pair(Extensions.FOOTNOTES, "FOOTNOTES"),
                Pair(Extensions.TOC, "TOC"),
                Pair(Extensions.INTELLIJ_DUMMY_IDENTIFIER, "INTELLIJ_DUMMY_IDENTIFIER"),
                Pair(Extensions.ALL_OPTIONALS, "ALL_OPTIONALS"),
                Pair(Extensions.ALL_WITH_OPTIONALS, "ALL_WITH_OPTIONALS")
        )

        val sortedOptionsMap by lazy {
            optionsMap.toSortedMap(Comparator { s, o -> if (s == o) 0 else if (s and o == s) 1 else if (s and o == o) -1 else optionsMap[s]?.compareTo(optionsMap[o] ?: "") ?: 0 })
        }

        fun stringPegDownOptions(options: Int): String {
            val optionsList = arrayListOf<String>()

            if (options == 0) {
                optionsList.add("NONE")
            } else {
                var remainingOptions = options
                for (option in sortedOptionsMap) {
                    if (remainingOptions and option.key != 0) {
                        remainingOptions = remainingOptions and option.key.inv()
                        optionsList.add(option.value)
                        if (remainingOptions == 0) break
                    }
                }
            }
            return optionsList.splice(" or ")
        }

        fun stringLexerTokens(lexerTokens: Array<LexerToken>?): String {
            if (lexerTokens == null) {
                return "null"
            } else {
                val resultsList = arrayListOf<String>()
                for (lexerToken in lexerTokens) {
                    resultsList.add(lexerToken.testData())
                }
                return "arrayOf<LexerToken>(" + resultsList.splice(", ") + ")"
            }
        }

        //val markdownInput: String, val pegdownOptions: Int, val lexerTokens: Array<MultiMarkdownLexParser.LexerToken>
        @Parameterized.Parameters(name = "{index}: markdownInput = {1}, rootNode = {2}, lexerTokens = {3}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val data = LexParser_md_RootNode.data()
            val amendedData = ArrayList<Array<Any?>>()
            val cleanData = false
            val lexerMap = HashMap<Int, Int>()

            var i = 0
            for (row in lexerTokenData) {
                lexerMap.put(row[0] as Int, i)
                i++
            }

            i = 0
            for (row in data) {
                val amendedRow = Array<Any?>(row.size + 2, { null })
                System.arraycopy(row, 0, amendedRow, 1, row.size)
                amendedRow[0] = i
                amendedRow[row.size + 1] = if (lexerMap.containsKey(i)) lexerTokenData[lexerMap[i] as Int][1] else arrayOf<LexerToken>()
                amendedData.add(amendedRow)
                i++
            }

            if (cleanData) {
                val header = arrayOf("markdownInput", "pegdownOptions")
                val stringers = mapOf<String, (Array<Any?>) -> String>(
                        //                        Pair("pegdownOptions", { row -> stringPegDownOptions(row[1] as Int) }),
                        Pair("lexerTokens", { row -> stringLexerTokens(row[2] as Array<LexerToken>) })
                )
                printData(data, header, stringers)
            }

            return amendedData
        }

        val lexerTokenData = arrayListOf<Array<Any?>>(
                /*      arrayOf<Any?>("rowID", "lexerTokens") */
                /*  0 */arrayOf<Any?>(0, arrayOf<LexerToken>(LexerToken(0, 2, WIKI_LINK_OPEN, -1), LexerToken(2, 3, WIKI_LINK_REF, -1), LexerToken(3, 5, WIKI_LINK_CLOSE, -1))) /*  0 */,
                /*  1 */arrayOf<Any?>(1, arrayOf<LexerToken>(LexerToken(0, 2, WIKI_LINK_OPEN, -1), LexerToken(2, 3, WIKI_LINK_REF_ANCHOR_MARKER, -1), LexerToken(3, 5, WIKI_LINK_CLOSE, -1))) /*  1 */,
                /*  2 */arrayOf<Any?>(2, arrayOf<LexerToken>(LexerToken(0, 2, WIKI_LINK_OPEN, -1), LexerToken(2, 3, WIKI_LINK_REF_ANCHOR_MARKER, -1), LexerToken(3, 9, WIKI_LINK_REF_ANCHOR, -1), LexerToken(9, 11, WIKI_LINK_CLOSE, -1))) /*  2 */,
                /*  3 */arrayOf<Any?>(3, arrayOf<LexerToken>(LexerToken(0, 2, WIKI_LINK_OPEN, -1), LexerToken(2, 13, WIKI_LINK_REF, -1), LexerToken(13, 15, WIKI_LINK_CLOSE, -1))) /*  3 */,
                /*  4 */arrayOf<Any?>(4, arrayOf<LexerToken>(LexerToken(0, 2, WIKI_LINK_OPEN, -1), LexerToken(2, 13, WIKI_LINK_REF, -1), LexerToken(13, 14, WIKI_LINK_REF_ANCHOR_MARKER, -1), LexerToken(14, 20, WIKI_LINK_REF_ANCHOR, -1), LexerToken(20, 22, WIKI_LINK_CLOSE, -1))) /*  4 */,
                /*  5 */arrayOf<Any?>(5, arrayOf<LexerToken>(LexerToken(0, 2, WIKI_LINK_OPEN, -1), LexerToken(2, 11, WIKI_LINK_TEXT, -1), LexerToken(11, 12, WIKI_LINK_SEPARATOR, -1), LexerToken(12, 24, WIKI_LINK_REF, -1), LexerToken(24, 26, WIKI_LINK_CLOSE, -1))) /*  5 */,
                /*  6 */arrayOf<Any?>(6, arrayOf<LexerToken>(LexerToken(0, 2, WIKI_LINK_OPEN, -1), LexerToken(2, 11, WIKI_LINK_TEXT, -1), LexerToken(11, 12, WIKI_LINK_SEPARATOR, -1), LexerToken(12, 24, WIKI_LINK_REF, -1), LexerToken(24, 25, WIKI_LINK_REF_ANCHOR_MARKER, -1), LexerToken(25, 31, WIKI_LINK_REF_ANCHOR, -1), LexerToken(31, 33, WIKI_LINK_CLOSE, -1))) /*  6 */,
                /*  7 */arrayOf<Any?>(7, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_CLOSE, -1))) /*  7 */,
                /*  8 */arrayOf<Any?>(8, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(5, 6, LINK_REF_CLOSE, -1))) /*  8 */,
                /*  9 */arrayOf<Any?>(9, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_ANCHOR_MARKER, -1), LexerToken(5, 6, LINK_REF_CLOSE, -1))) /*  9 */,
                /* 10 */arrayOf<Any?>(10, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_ANCHOR_MARKER, -1), LexerToken(5, 11, LINK_REF_ANCHOR, -1), LexerToken(11, 12, LINK_REF_CLOSE, -1))) /* 10 */,
                /* 11 */arrayOf<Any?>(11, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_CLOSE, -1))) /* 11 */,
                /* 12 */arrayOf<Any?>(12, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_ANCHOR_MARKER, -1), LexerToken(17, 18, LINK_REF_CLOSE, -1))) /* 12 */,
                /* 13 */arrayOf<Any?>(13, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_ANCHOR_MARKER, -1), LexerToken(17, 23, LINK_REF_ANCHOR, -1), LexerToken(23, 24, LINK_REF_CLOSE, -1))) /* 13 */,
                /* 14 */arrayOf<Any?>(14, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_CLOSE, -1))) /* 14 */,
                /* 15 */arrayOf<Any?>(15, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_ANCHOR_MARKER, -1), LexerToken(25, 26, LINK_REF_CLOSE, -1))) /* 15 */,
                /* 16 */arrayOf<Any?>(16, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_ANCHOR_MARKER, -1), LexerToken(25, 31, LINK_REF_ANCHOR, -1), LexerToken(31, 32, LINK_REF_CLOSE, -1))) /* 16 */,
                /* 17 */arrayOf<Any?>(17, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_CLOSE, -1))) /* 17 */,
                /* 18 */arrayOf<Any?>(18, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_ANCHOR_MARKER, -1), LexerToken(29, 30, LINK_REF_CLOSE, -1))) /* 18 */,
                /* 19 */arrayOf<Any?>(19, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_ANCHOR_MARKER, -1), LexerToken(29, 35, LINK_REF_ANCHOR, -1), LexerToken(35, 36, LINK_REF_CLOSE, -1))) /* 19 */,
                /* 20 */arrayOf<Any?>(20, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_CLOSE, -1), LexerToken(5, 6, TEXT, -1), LexerToken(7, 8, TEXT, -1))) /* 20 */,
                /* 21 */arrayOf<Any?>(21, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(5, 6, LINK_REF_CLOSE, -1), LexerToken(6, 7, TEXT, -1), LexerToken(8, 9, TEXT, -1))) /* 21 */,
                /* 22 */arrayOf<Any?>(22, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_ANCHOR_MARKER, -1), LexerToken(5, 6, LINK_REF_CLOSE, -1), LexerToken(6, 7, TEXT, -1), LexerToken(8, 9, TEXT, -1))) /* 22 */,
                /* 23 */arrayOf<Any?>(23, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_ANCHOR_MARKER, -1), LexerToken(5, 11, LINK_REF_ANCHOR, -1), LexerToken(11, 12, LINK_REF_CLOSE, -1), LexerToken(12, 13, TEXT, -1), LexerToken(14, 15, TEXT, -1))) /* 23 */,
                /* 24 */arrayOf<Any?>(24, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_CLOSE, -1), LexerToken(17, 18, TEXT, -1), LexerToken(19, 20, TEXT, -1))) /* 24 */,
                /* 25 */arrayOf<Any?>(25, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_ANCHOR_MARKER, -1), LexerToken(17, 18, LINK_REF_CLOSE, -1), LexerToken(18, 19, TEXT, -1), LexerToken(20, 21, TEXT, -1))) /* 25 */,
                /* 26 */arrayOf<Any?>(26, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_ANCHOR_MARKER, -1), LexerToken(17, 23, LINK_REF_ANCHOR, -1), LexerToken(23, 24, LINK_REF_CLOSE, -1), LexerToken(24, 25, TEXT, -1), LexerToken(26, 27, TEXT, -1))) /* 26 */,
                /* 27 */arrayOf<Any?>(27, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_CLOSE, -1), LexerToken(25, 26, TEXT, -1), LexerToken(27, 28, TEXT, -1))) /* 27 */,
                /* 28 */arrayOf<Any?>(28, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_ANCHOR_MARKER, -1), LexerToken(25, 26, LINK_REF_CLOSE, -1), LexerToken(26, 27, TEXT, -1), LexerToken(28, 29, TEXT, -1))) /* 28 */,
                /* 29 */arrayOf<Any?>(29, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_ANCHOR_MARKER, -1), LexerToken(25, 31, LINK_REF_ANCHOR, -1), LexerToken(31, 32, LINK_REF_CLOSE, -1), LexerToken(32, 33, TEXT, -1), LexerToken(34, 35, TEXT, -1))) /* 29 */,
                /* 30 */arrayOf<Any?>(30, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_CLOSE, -1), LexerToken(29, 30, TEXT, -1), LexerToken(31, 32, TEXT, -1))) /* 30 */,
                /* 31 */arrayOf<Any?>(31, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_ANCHOR_MARKER, -1), LexerToken(29, 30, LINK_REF_CLOSE, -1), LexerToken(30, 31, TEXT, -1), LexerToken(32, 33, TEXT, -1))) /* 31 */,
                /* 32 */arrayOf<Any?>(32, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_ANCHOR_MARKER, -1), LexerToken(29, 35, LINK_REF_ANCHOR, -1), LexerToken(35, 36, LINK_REF_CLOSE, -1), LexerToken(36, 37, TEXT, -1), LexerToken(38, 39, TEXT, -1))) /* 32 */,
                /* 33 */arrayOf<Any?>(33, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_CLOSE, -1), LexerToken(5, 6, TEXT, -1), LexerToken(6, 13, QUOTE, -1))) /* 33 */,
                /* 34 */arrayOf<Any?>(34, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(5, 6, LINK_REF_CLOSE, -1), LexerToken(6, 7, TEXT, -1), LexerToken(7, 14, QUOTE, -1))) /* 34 */,
                /* 35 */arrayOf<Any?>(35, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_ANCHOR_MARKER, -1), LexerToken(5, 6, LINK_REF_CLOSE, -1), LexerToken(6, 7, TEXT, -1), LexerToken(7, 14, QUOTE, -1))) /* 35 */,
                /* 36 */arrayOf<Any?>(36, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 5, LINK_REF_ANCHOR_MARKER, -1), LexerToken(5, 11, LINK_REF_ANCHOR, -1), LexerToken(11, 12, LINK_REF_CLOSE, -1), LexerToken(12, 13, TEXT, -1), LexerToken(13, 20, QUOTE, -1))) /* 36 */,
                /* 37 */arrayOf<Any?>(37, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_CLOSE, -1), LexerToken(17, 18, TEXT, -1), LexerToken(18, 25, QUOTE, -1))) /* 37 */,
                /* 38 */arrayOf<Any?>(38, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_ANCHOR_MARKER, -1), LexerToken(17, 18, LINK_REF_CLOSE, -1), LexerToken(18, 19, TEXT, -1), LexerToken(19, 26, QUOTE, -1))) /* 38 */,
                /* 39 */arrayOf<Any?>(39, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 2, TEXT, -1), LexerToken(2, 3, LINK_REF_TEXT_CLOSE, -1), LexerToken(3, 4, LINK_REF_OPEN, -1), LexerToken(4, 16, LINK_REF, -1), LexerToken(16, 17, LINK_REF_ANCHOR_MARKER, -1), LexerToken(17, 23, LINK_REF_ANCHOR, -1), LexerToken(23, 24, LINK_REF_CLOSE, -1), LexerToken(24, 25, TEXT, -1), LexerToken(25, 32, QUOTE, -1))) /* 39 */,
                /* 40 */arrayOf<Any?>(40, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_CLOSE, -1), LexerToken(25, 26, TEXT, -1), LexerToken(26, 33, QUOTE, -1))) /* 40 */,
                /* 41 */arrayOf<Any?>(41, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_ANCHOR_MARKER, -1), LexerToken(25, 26, LINK_REF_CLOSE, -1), LexerToken(26, 27, TEXT, -1), LexerToken(27, 34, QUOTE, -1))) /* 41 */,
                /* 42 */arrayOf<Any?>(42, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 10, TEXT, -1), LexerToken(10, 11, LINK_REF_TEXT_CLOSE, -1), LexerToken(11, 12, LINK_REF_OPEN, -1), LexerToken(12, 24, LINK_REF, -1), LexerToken(24, 25, LINK_REF_ANCHOR_MARKER, -1), LexerToken(25, 31, LINK_REF_ANCHOR, -1), LexerToken(31, 32, LINK_REF_CLOSE, -1), LexerToken(32, 33, TEXT, -1), LexerToken(33, 40, QUOTE, -1))) /* 42 */,
                /* 43 */arrayOf<Any?>(43, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_CLOSE, -1), LexerToken(29, 30, TEXT, -1), LexerToken(30, 37, QUOTE, -1))) /* 43 */,
                /* 44 */arrayOf<Any?>(44, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_ANCHOR_MARKER, -1), LexerToken(29, 30, LINK_REF_CLOSE, -1), LexerToken(30, 31, TEXT, -1), LexerToken(31, 38, QUOTE, -1))) /* 44 */,
                /* 45 */arrayOf<Any?>(45, arrayOf<LexerToken>(LexerToken(0, 1, LINK_REF_TEXT_OPEN, -1), LexerToken(1, 3, BOLD_MARKER, -1), LexerToken(3, 12, BOLD, -1), LexerToken(12, 14, BOLD_MARKER, -1), LexerToken(14, 15, LINK_REF_TEXT_CLOSE, -1), LexerToken(15, 16, LINK_REF_OPEN, -1), LexerToken(16, 28, LINK_REF, -1), LexerToken(28, 29, LINK_REF_ANCHOR_MARKER, -1), LexerToken(29, 35, LINK_REF_ANCHOR, -1), LexerToken(35, 36, LINK_REF_CLOSE, -1), LexerToken(36, 37, TEXT, -1), LexerToken(37, 44, QUOTE, -1))) /* 45 */,
                /* 46 */arrayOf<Any?>(46, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 6, IMAGE_LINK_REF_CLOSE, -1))) /* 46 */,
                /* 47 */arrayOf<Any?>(47, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(6, 7, IMAGE_LINK_REF_CLOSE, -1))) /* 47 */,
                /* 48 */arrayOf<Any?>(48, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 6, IMAGE_LINK_REF, -1), LexerToken(6, 7, IMAGE_LINK_REF_CLOSE, -1))) /* 48 */,
                /* 49 */arrayOf<Any?>(49, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 12, IMAGE_LINK_REF, -1), LexerToken(12, 13, IMAGE_LINK_REF_CLOSE, -1))) /* 49 */,
                /* 50 */arrayOf<Any?>(50, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 17, IMAGE_LINK_REF, -1), LexerToken(17, 18, IMAGE_LINK_REF_CLOSE, -1))) /* 50 */,
                /* 51 */arrayOf<Any?>(51, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 18, IMAGE_LINK_REF, -1), LexerToken(18, 19, IMAGE_LINK_REF_CLOSE, -1))) /* 51 */,
                /* 52 */arrayOf<Any?>(52, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 24, IMAGE_LINK_REF, -1), LexerToken(24, 25, IMAGE_LINK_REF_CLOSE, -1))) /* 52 */,
                /* 53 */arrayOf<Any?>(53, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 25, IMAGE_LINK_REF, -1), LexerToken(25, 26, IMAGE_LINK_REF_CLOSE, -1))) /* 53 */,
                /* 54 */arrayOf<Any?>(54, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 26, IMAGE_LINK_REF, -1), LexerToken(26, 27, IMAGE_LINK_REF_CLOSE, -1))) /* 54 */,
                /* 55 */arrayOf<Any?>(55, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 32, IMAGE_LINK_REF, -1), LexerToken(32, 33, IMAGE_LINK_REF_CLOSE, -1))) /* 55 */,
                /* 56 */arrayOf<Any?>(56, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 29, IMAGE_LINK_REF, -1), LexerToken(29, 30, IMAGE_LINK_REF_CLOSE, -1))) /* 56 */,
                /* 57 */arrayOf<Any?>(57, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 30, IMAGE_LINK_REF, -1), LexerToken(30, 31, IMAGE_LINK_REF_CLOSE, -1))) /* 57 */,
                /* 58 */arrayOf<Any?>(58, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 36, IMAGE_LINK_REF, -1), LexerToken(36, 37, IMAGE_LINK_REF_CLOSE, -1))) /* 58 */,
                /* 59 */arrayOf<Any?>(59, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 6, IMAGE_LINK_REF_CLOSE, -1), LexerToken(6, 7, TEXT, -1), LexerToken(8, 9, TEXT, -1))) /* 59 */,
                /* 60 */arrayOf<Any?>(60, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(6, 7, IMAGE_LINK_REF_CLOSE, -1), LexerToken(7, 8, TEXT, -1), LexerToken(9, 10, TEXT, -1))) /* 60 */,
                /* 61 */arrayOf<Any?>(61, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 6, IMAGE_LINK_REF, -1), LexerToken(6, 7, IMAGE_LINK_REF_CLOSE, -1), LexerToken(7, 8, TEXT, -1), LexerToken(9, 10, TEXT, -1))) /* 61 */,
                /* 62 */arrayOf<Any?>(62, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 12, IMAGE_LINK_REF, -1), LexerToken(12, 13, IMAGE_LINK_REF_CLOSE, -1), LexerToken(13, 14, TEXT, -1), LexerToken(15, 16, TEXT, -1))) /* 62 */,
                /* 63 */arrayOf<Any?>(63, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 17, IMAGE_LINK_REF, -1), LexerToken(17, 18, IMAGE_LINK_REF_CLOSE, -1), LexerToken(18, 19, TEXT, -1), LexerToken(20, 21, TEXT, -1))) /* 63 */,
                /* 64 */arrayOf<Any?>(64, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 18, IMAGE_LINK_REF, -1), LexerToken(18, 19, IMAGE_LINK_REF_CLOSE, -1), LexerToken(19, 20, TEXT, -1), LexerToken(21, 22, TEXT, -1))) /* 64 */,
                /* 65 */arrayOf<Any?>(65, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 24, IMAGE_LINK_REF, -1), LexerToken(24, 25, IMAGE_LINK_REF_CLOSE, -1), LexerToken(25, 26, TEXT, -1), LexerToken(27, 28, TEXT, -1))) /* 65 */,
                /* 66 */arrayOf<Any?>(66, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 25, IMAGE_LINK_REF, -1), LexerToken(25, 26, IMAGE_LINK_REF_CLOSE, -1), LexerToken(26, 27, TEXT, -1), LexerToken(28, 29, TEXT, -1))) /* 66 */,
                /* 67 */arrayOf<Any?>(67, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 26, IMAGE_LINK_REF, -1), LexerToken(26, 27, IMAGE_LINK_REF_CLOSE, -1), LexerToken(27, 28, TEXT, -1), LexerToken(29, 30, TEXT, -1))) /* 67 */,
                /* 68 */arrayOf<Any?>(68, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 32, IMAGE_LINK_REF, -1), LexerToken(32, 33, IMAGE_LINK_REF_CLOSE, -1), LexerToken(33, 34, TEXT, -1), LexerToken(35, 36, TEXT, -1))) /* 68 */,
                /* 69 */arrayOf<Any?>(69, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 29, IMAGE_LINK_REF, -1), LexerToken(29, 30, IMAGE_LINK_REF_CLOSE, -1), LexerToken(30, 31, TEXT, -1), LexerToken(32, 33, TEXT, -1))) /* 69 */,
                /* 70 */arrayOf<Any?>(70, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 30, IMAGE_LINK_REF, -1), LexerToken(30, 31, IMAGE_LINK_REF_CLOSE, -1), LexerToken(31, 32, TEXT, -1), LexerToken(33, 34, TEXT, -1))) /* 70 */,
                /* 71 */arrayOf<Any?>(71, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 36, IMAGE_LINK_REF, -1), LexerToken(36, 37, IMAGE_LINK_REF_CLOSE, -1), LexerToken(37, 38, TEXT, -1), LexerToken(39, 40, TEXT, -1))) /* 71 */,
                /* 72 */arrayOf<Any?>(72, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 6, IMAGE_LINK_REF_CLOSE, -1), LexerToken(6, 7, TEXT, -1), LexerToken(7, 14, QUOTE, -1))) /* 72 */,
                /* 73 */arrayOf<Any?>(73, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(6, 7, IMAGE_LINK_REF_CLOSE, -1), LexerToken(7, 8, TEXT, -1), LexerToken(8, 15, QUOTE, -1))) /* 73 */,
                /* 74 */arrayOf<Any?>(74, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 6, IMAGE_LINK_REF, -1), LexerToken(6, 7, IMAGE_LINK_REF_CLOSE, -1), LexerToken(7, 8, TEXT, -1), LexerToken(8, 15, QUOTE, -1))) /* 74 */,
                /* 75 */arrayOf<Any?>(75, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 12, IMAGE_LINK_REF, -1), LexerToken(12, 13, IMAGE_LINK_REF_CLOSE, -1), LexerToken(13, 14, TEXT, -1), LexerToken(14, 21, QUOTE, -1))) /* 75 */,
                /* 76 */arrayOf<Any?>(76, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 17, IMAGE_LINK_REF, -1), LexerToken(17, 18, IMAGE_LINK_REF_CLOSE, -1), LexerToken(18, 19, TEXT, -1), LexerToken(19, 26, QUOTE, -1))) /* 76 */,
                /* 77 */arrayOf<Any?>(77, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 18, IMAGE_LINK_REF, -1), LexerToken(18, 19, IMAGE_LINK_REF_CLOSE, -1), LexerToken(19, 20, TEXT, -1), LexerToken(20, 27, QUOTE, -1))) /* 77 */,
                /* 78 */arrayOf<Any?>(78, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 3, TEXT, -1), LexerToken(3, 4, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(4, 5, IMAGE_LINK_REF_OPEN, -1), LexerToken(5, 24, IMAGE_LINK_REF, -1), LexerToken(24, 25, IMAGE_LINK_REF_CLOSE, -1), LexerToken(25, 26, TEXT, -1), LexerToken(26, 33, QUOTE, -1))) /* 78 */,
                /* 79 */arrayOf<Any?>(79, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 25, IMAGE_LINK_REF, -1), LexerToken(25, 26, IMAGE_LINK_REF_CLOSE, -1), LexerToken(26, 27, TEXT, -1), LexerToken(27, 34, QUOTE, -1))) /* 79 */,
                /* 80 */arrayOf<Any?>(80, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 26, IMAGE_LINK_REF, -1), LexerToken(26, 27, IMAGE_LINK_REF_CLOSE, -1), LexerToken(27, 28, TEXT, -1), LexerToken(28, 35, QUOTE, -1))) /* 80 */,
                /* 81 */arrayOf<Any?>(81, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 11, TEXT, -1), LexerToken(11, 12, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(12, 13, IMAGE_LINK_REF_OPEN, -1), LexerToken(13, 32, IMAGE_LINK_REF, -1), LexerToken(32, 33, IMAGE_LINK_REF_CLOSE, -1), LexerToken(33, 34, TEXT, -1), LexerToken(34, 41, QUOTE, -1))) /* 81 */,
                /* 82 */arrayOf<Any?>(82, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 29, IMAGE_LINK_REF, -1), LexerToken(29, 30, IMAGE_LINK_REF_CLOSE, -1), LexerToken(30, 31, TEXT, -1), LexerToken(31, 38, QUOTE, -1))) /* 82 */,
                /* 83 */arrayOf<Any?>(83, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 30, IMAGE_LINK_REF, -1), LexerToken(30, 31, IMAGE_LINK_REF_CLOSE, -1), LexerToken(31, 32, TEXT, -1), LexerToken(32, 39, QUOTE, -1))) /* 83 */,
                /* 84 */arrayOf<Any?>(84, arrayOf<LexerToken>(LexerToken(0, 2, IMAGE_LINK_REF_TEXT_OPEN, -1), LexerToken(2, 4, BOLD_MARKER, -1), LexerToken(4, 13, BOLD, -1), LexerToken(13, 15, BOLD_MARKER, -1), LexerToken(15, 16, IMAGE_LINK_REF_TEXT_CLOSE, -1), LexerToken(16, 17, IMAGE_LINK_REF_OPEN, -1), LexerToken(17, 36, IMAGE_LINK_REF, -1), LexerToken(36, 37, IMAGE_LINK_REF_CLOSE, -1), LexerToken(37, 38, TEXT, -1), LexerToken(38, 45, QUOTE, -1))) /* 84 */
        )
    }
}
