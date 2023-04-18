// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.vladsch.md.nav.highlighter.MdHighlighterColors
import com.vladsch.md.nav.highlighter.MdSyntaxHighlighter
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTokenType
import com.vladsch.md.nav.psi.util.MdTypes.*
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.plugin.util.max
import java.util.*

@Suppress("UNUSED_VARIABLE")
class LexParserState {

    companion object {
        private val DEFAULT: LexParserState by lazy { LexParserState() }
        private val LOG: Logger = Logger.getInstance("com.vladsch.md.nav.parser")

        @JvmStatic
        fun getInstance(): LexParserState {
            return if (ApplicationManager.getApplication() == null) DEFAULT
            else ServiceManager.getService(LexParserState::class.java)
        }
    }

    private val childrenNotExcludingParentsMap = HashMap<IElementType, HashSet<IElementType>>()
    private val parentsNotExcludingChildrenMap = HashMap<IElementType, HashSet<IElementType>>()

    fun isExcludedByChild(parent: IElementType, child: IElementType): Boolean {
        val childNotExcludingParents = childrenNotExcludingParentsMap[child] ?: return true
        return !childNotExcludingParents.contains(parent)
    }

    fun isExcludedByParent(parent: IElementType, child: IElementType): Boolean {
        val parentNotExcludingChildren = parentsNotExcludingChildrenMap[parent] ?: return true
        return !parentNotExcludingChildren.contains(child)
    }

    private val COMBINATION_TYPES = HashMap<String, IElementType>()

    val state: State by lazy {
        initialize()
    }

    data class State(
        @JvmField val TYPOGRAPHIC_MARKER_SET: TokenSet,
        @JvmField val TEXT_SET: TokenSet,
        @JvmField val TEXT_TOKEN_TYPE_MAP: Map<IElementType, IElementType>,
        @JvmField val NON_MERGE_TOKEN_SET: TokenSet,
        @JvmField val COMBINATION_SPLITS: Map<IElementType, Map<IElementType, IElementType>>,
        @JvmField val INLINE_NON_PLAIN_TEXT: List<IElementType>,
        @JvmField val INLINE_SPECIAL_TEXT: List<IElementType>,
        @JvmField val INLINE_PLAIN_TEXT: List<IElementType>
    )

    // @formatter:off
    private var TYPOGRAPHIC_MARKER_SET: TokenSet = TokenSet.create()
    private var TEXT_SET: TokenSet = TokenSet.create()
    private val TEXT_TOKEN_TYPE_MAP = HashMap<IElementType, IElementType>()

    /* NOTE: must add all markers that can be consecutive, otherwise they are combined into a single leaf
        and it is hell to debug why formatting, wrap on typing and other features fail.
    */
    // these will not be combined into a single lexer token even if adjacent, others will combine consecutive tokens
    private val NON_MERGE_TOKEN_SET = TokenSet.create(
        IMAGE_LINK_REF_TITLE_MARKER,
        LINK_REF_TITLE_MARKER,
        REFERENCE_TITLE_MARKER,
        ASIDE_BLOCK_MARKER,
        BLOCK_QUOTE_MARKER,
        BULLET_LIST_ITEM_MARKER,
        ORDERED_LIST_ITEM_MARKER,
        DEFINITION_TERM,
        FLEXMARK_EXAMPLE_SEPARATOR,
        EOL,
        BLANK_LINE
    )

    private val COMBINATION_SPLITS: MutableMap<IElementType, HashMap<IElementType, IElementType>> = HashMap()

    private val INLINE_NON_PLAIN_TEXT: ArrayList<IElementType> = arrayListOf(
        COMMENT_OPEN,
        COMMENT_CLOSE,
        BLOCK_COMMENT_OPEN,
        BLOCK_COMMENT_CLOSE,
        HTML_ENTITY,
        EMOJI_MARKER,
        EMOJI_ID
    )

    private val INLINE_SPECIAL_TEXT: ArrayList<IElementType> = arrayListOf(
        SPECIAL_TEXT
    )

    private val INLINE_PLAIN_TEXT = arrayListOf<IElementType>(
//                COMMENT_TEXT,
//                BLOCK_COMMENT_TEXT,
    )

    internal fun addChildDoesNotExcludeParent(parent: IElementType, child: IElementType, addParentDoesNotExcludesChild: Boolean = false) {
        val childExclusions = childrenNotExcludingParentsMap.computeIfAbsent(child ){ HashSet() }
        childExclusions.add(parent)

        if (addParentDoesNotExcludesChild) {
            addParentDoesNotExcludesChild(parent, child)
        }
    }

    private fun addParentDoesNotExcludesChild(parent: IElementType, child: IElementType) {
        val parentExclusions = parentsNotExcludingChildrenMap.computeIfAbsent(parent ){ HashSet() }
        parentExclusions.add(child)
    }

    private fun addInlineExclusions(parent: IElementType, addNonPlainText: Boolean = true) {
        addChildDoesNotExcludeParent(parent, TEXT)

        if (addNonPlainText) {
            for (inline in INLINE_NON_PLAIN_TEXT) {
                addChildDoesNotExcludeParent(parent, inline)
            }
        }
    }
    // @formatter:on

    private fun <T : Any> List<T>.union(other: Collection<T>): List<T> {
        val setList = this.toSet()
        val list = ArrayList<T>()
        list.addAll(this)

        for (item in other) {
            if (!setList.contains(item)) list.add(item)
        }
        return list
    }

    private fun initialize(): State {
        // create combination types so that overlay highlighting cab be done in lexer
        // do it first so that the non-text inline set is updated
        val inlineEmphasis: List<IElementType>
        val inlineEmphasisMarkers: List<IElementType>
        val tableCellsInline: List<IElementType>
        val fullHighlightCombinations = MdApplicationSettings.instance.documentSettings.fullHighlightCombinations

        LOG.info("Initializing LexParserState: fullHighlights = $fullHighlightCombinations")

        inlineEmphasis = if (fullHighlightCombinations)
            listOf(
                STRIKETHROUGH_TEXT,
                BOLD_TEXT,
                UNDERLINE_TEXT,
                SUPERSCRIPT_TEXT,
                SUBSCRIPT_TEXT,
                ITALIC_TEXT
            )
        else
            listOf(
                STRIKETHROUGH_TEXT,
                BOLD_TEXT,
                ITALIC_TEXT
            )

        inlineEmphasisMarkers = if (fullHighlightCombinations)
            listOf(
                BOLD_MARKER,
                UNDERLINE_MARKER,
                SUPERSCRIPT_MARKER,
                SUBSCRIPT_MARKER,
                ITALIC_MARKER,
                STRIKETHROUGH_MARKER
            )
        else
            listOf(
                BOLD_MARKER,
                ITALIC_MARKER,
                STRIKETHROUGH_MARKER
            )

        tableCellsInline = listOf(
            TABLE_CELL_REVEN_CEVEN,
            TABLE_CELL_REVEN_CODD,
            TABLE_CELL_RODD_CEVEN,
            TABLE_CELL_RODD_CODD,
            TABLE_HDR_CELL_REVEN_CEVEN,
            TABLE_HDR_CELL_REVEN_CODD,
            TABLE_HDR_CELL_RODD_CEVEN,
            TABLE_HDR_CELL_RODD_CODD
        )

        INLINE_PLAIN_TEXT.add(TEXT)
        INLINE_PLAIN_TEXT.addAll(inlineEmphasis)
        INLINE_NON_PLAIN_TEXT.addAll(inlineEmphasisMarkers)

        if (fullHighlightCombinations) {
            INLINE_PLAIN_TEXT.addAll(listOf(CODE_TEXT, GITLAB_MATH_TEXT, QUOTED_TEXT, ABBREVIATED_TEXT))
        } else {
            INLINE_PLAIN_TEXT.addAll(listOf(CODE_TEXT, ABBREVIATED_TEXT))
        }

        val combinator = Combinator(this)
        val combinationInlineEmphasis = combinator.combinationSplits(inlineEmphasis, inlineEmphasis)
        val combinationInlineEmphasisQuotes = if (!fullHighlightCombinations) listOf() else combinator.combinationSplits(inlineEmphasis.union(setOf(QUOTED_TEXT)), inlineEmphasis.union(setOf(QUOTED_TEXT)))
        //            val combinationInlineEmphasisAbbrQuotes = combinator.crossSplits(combinationInlineEmphasisQuotes, listOf(ABBREVIATED_TEXT))
        //            val combinationInlineEmphasisAbbrQuotes = combinationInlineEmphasisQuotes
        val combinationInlineEmphasisCode = combinator.combinationSplits(inlineEmphasis, listOf(CODE_TEXT))
        //            val combinationInlineEmphasisEntity = combinator.combinationSplits(inlineEmphasis.union(setOf(QUOTED_TEXT)), listOf(HTML_ENTITY))
        //            val combinationInlineEmphasisSpecial = combinator.combinationSplits(inlineEmphasis.union(setOf(QUOTED_TEXT)), listOf(SPECIAL_TEXT)).union(combinator.combinationSplits(listOf(QUOTED_TEXT), listOf(SPECIAL_TEXT_MARKER)))
        //            val combinationInlineEmphasisSmarts = inlineEmphasis.union(setOf(QUOTED_TEXT, SMARTS))
        if (fullHighlightCombinations) {
            val combinationInlineEmphasisSmarts = combinator.combinationSplits(inlineEmphasis.union(setOf(QUOTED_TEXT)), listOf(SMARTS))
            val tableCellInlineCombo = combinator.crossSplits(tableCellsInline, INLINE_PLAIN_TEXT.union(INLINE_NON_PLAIN_TEXT).union(INLINE_SPECIAL_TEXT))
        }
        val tableCellInlineComboEmphasis = combinator.crossSplits(tableCellsInline, combinationInlineEmphasis)
        //            val tableCellInlineComboAbbr = combinator.crossSplits(tableCellsInline, combinationInlineEmphasisAbbrQuotes)
        //            val tableCellInlineComboEmphasisMarkers = combinator.crossSplits(tableCellsInline, inlineEmphasisMarkers.union(setOf(QUOTE_MARKER, CODE_MARKER, SPECIAL_TEXT_MARKER)))
        //            val tableCellInlineComboEmphasisMarkers = combinator.crossSplits(tableCellsInline, inlineEmphasisMarkers.union(setOf(CODE_MARKER)))
        if (fullHighlightCombinations) {
            val tableCellInlineComboNotIssueMarkers = combinator.crossSplits(tableCellsInline, MdTokenSets.NON_TEXT_INLINE_ELEMENTS.types.toList())
        }

        // add combinations for emphasis, quotes and code to header text overlays
        val overlays = listOf<IElementType>()
            .union(combinationInlineEmphasisQuotes)
            //                    .union(combinationInlineEmphasisAbbrQuotes)
            .union(combinationInlineEmphasisCode)
            //                    .union(combinationInlineEmphasisEntity)
            //                    .union(combinationInlineEmphasisSpecial)
            //                    .union(combinationInlineEmphasisSmarts)
            //                    .union(combinationInlineEmphasis)
            //                    .union(inlineEmphasisMarkers)
            .union(setOf(QUOTE_MARKER, CODE_MARKER, SPECIAL_TEXT_MARKER))
        //                    .union(setOf(CODE_MARKER, SPECIAL_TEXT_MARKER))
        //                    .union(INLINE_NON_PLAIN_TEXT.filter { !it.toString().startsWith("TABLE_") && !it.toString().startsWith("DEFINITION_") })
        //                    .union(issueMarkerEmphasis).union(setOf(ISSUE_MARKER))

        if (fullHighlightCombinations) {
            val headerCombos = combinator.crossSplits(listOf(HEADER_TEXT), overlays)
            val headerEmphasis = combinator.crossSplits(listOf(HEADER_TEXT), combinationInlineEmphasis)
            //            val headerEmphasisIssueMarker = combinator.crossSplits(headerEmphasis, setOf(ISSUE_MARKER))
            val headerEmphasisMarkers = combinator.crossSplits(listOf(HEADER_TEXT), inlineEmphasisMarkers
                .union(setOf(QUOTE_MARKER, CODE_MARKER, SPECIAL_TEXT_MARKER))
                .union(INLINE_NON_PLAIN_TEXT.filter { !it.toString().startsWith("TABLE_") && !it.toString().startsWith("DEFINITION_") })
            )
        }

        // construct TEXT to ABBREVIATED_TEXT map
        //            val combinationInlineAbbrText = combinator.crossSplits(tableCellInlineComboAbbr.union(headerCombos).union(headerEmphasis), listOf(ABBREVIATED_TEXT))

        val abbrMap = COMBINATION_SPLITS[ABBREVIATED_TEXT]
        if (abbrMap != null) TEXT_TOKEN_TYPE_MAP.putAll(abbrMap)

        // now add these so the rest of the code knows what an issue marker is
        val issueMarkers = mutableListOf<IElementType>()
        val invalidIssueMarkers = mutableListOf<IElementType>()
        val textSet = mutableListOf<IElementType>()
        val invalidTextSet = mutableListOf<IElementType>()
        val typographicMarkers = mutableListOf<IElementType>()

        for (entry in COMBINATION_SPLITS.entries) {
            issueMarkers.addAll(entry.value.values.filter { it.toString().endsWith("_ISSUE_MARKER") })
            invalidIssueMarkers.addAll(entry.value.values.filter { it.toString().contains("_ISSUE_MARKER_") })
            textSet.addAll(entry.value.values.filter { it.toString().endsWith("_CODE_TEXT") })
            invalidIssueMarkers.addAll(entry.value.values.filter { it.toString().contains("_CODE_TEXT_") })
            typographicMarkers.addAll(entry.value.values.filter { it.toString().endsWith("_SMARTS") || it.toString().endsWith("_QUOTE_MARKER") })
        }

        TYPOGRAPHIC_MARKER_SET = TokenSet.create(QUOTE_MARKER, SMARTS, *typographicMarkers.toTypedArray())
        TEXT_SET = TokenSet.create(TEXT, CODE_TEXT, *textSet.toTypedArray(), *combinationInlineEmphasisCode.toTypedArray())

        INLINE_PLAIN_TEXT.addAll(tableCellsInline)

        // Text does not punch through other inline elements
        for (inline in INLINE_PLAIN_TEXT.union(INLINE_NON_PLAIN_TEXT)) {
            addChildDoesNotExcludeParent(inline, TEXT)
        }

        // nodes not to be punched out by text or emphasis
        //            addInlineExclusions(ABBREVIATION)
        addInlineExclusions(ANCHOR_LINK)
        //            addInlineExclusions(AUTO_LINK)
        addInlineExclusions(DEFINITION_TERM)
        //            addInlineExclusions(HEADER_ATX_MARKER)
        //            addInlineExclusions(HEADER_SETEXT_MARKER)
        //            addInlineExclusions(IMAGE_LINK_REF_TEXT)
        //            addInlineExclusions(IMAGE_LINK_REF_TITLE)
        //            addInlineExclusions(LINK_REF_TITLE)
        //            addInlineExclusions(QUOTE)
        //            addInlineExclusions(REFERENCE)
        //            addInlineExclusions(REFERENCE_IMAGE_REFERENCE)
        //            addInlineExclusions(REFERENCE_IMAGE_TEXT)
        //            addInlineExclusions(REFERENCE_LINK_REFERENCE)
        //            addInlineExclusions(REFERENCE_LINK_TEXT)
        //            addInlineExclusions(REFERENCE_TEXT)
        //            addInlineExclusions(REFERENCE_TITLE)
        //            addInlineExclusions(TASK_DONE_ITEM_MARKER)
        //            addInlineExclusions(TASK_ITEM_MARKER)
        //            addInlineExclusions(WIKI_LINK_REF)
        //            addInlineExclusions(WIKI_LINK_TEXT)

        // after all are updated, fire off the merger just in case
        val combinedTypes = COMBINATION_TYPES.size
        val splitCombos = MdSyntaxHighlighter.getMergedKeys().size
        val s = "Combined highlighters: $splitCombos, $combinedTypes"
        LOG.info(s)
        System.out.println(s)

        MdSyntaxHighlighter.computeMergedAttributes(false)

        return State(
            TYPOGRAPHIC_MARKER_SET,
            TEXT_SET,
            TEXT_TOKEN_TYPE_MAP.toMap(),
            NON_MERGE_TOKEN_SET,
            COMBINATION_SPLITS.toMap(),
            INLINE_NON_PLAIN_TEXT.toList(),
            INLINE_SPECIAL_TEXT.toList(),
            INLINE_PLAIN_TEXT.toList()
        )
    }

    private class Combinator(val state: LexParserState) {
        private val highlighterColors = MdHighlighterColors.getInstance()

        /**
         * Create all combinations of elements in both the bases and overlays lists
         * overlays list gives elements that are always contained by base elements or their combinations
         *
         * eg. bases(a,b) overlays (c,e) will produce the following calls to combinationSplit():
         *       ; 1x1 => 2
         *       c(a,b) => ab             0001 0010 0011
         *       c(a,c) => ac             0001 0100 0101
         *       c(a,e) => ae             0001 1000 1001
         *       c(b,a) => ab             0010 0001 0011
         *       c(b,c) => bc             0010 0100 0110
         *       c(b,e) => be             0010 1000 1010
         *       c(c,a) => *
         *       c(c,b) => *
         *       c(c,e) => ce             0100 1000 1100
         *       c(e,a) => *
         *       c(e,b) => *
         *       c(e,c) => ce             1000 0100 1100
         *
         *       ; 1x2 => 3
         *       c(a,bc) => abc          0001 0110 0111
         *       c(a,be) => abe          0001 1010 1011
         *       c(b,ce) => bce          0010 1100 1110
         *
         *       ; 2x1 => 3
         *       c(ab,c) => abc          0011 0100 0111
         *       c(ae,c) => ace          1001 0100 1101
         *       c(ab,e) => abe          0011 1000 1011
         *       c(ac,e) => ace          0101 1000 1101
         *       c(bc,e) => bce          0110 1000 1110
         *
         *       ; 1x3 => 4
         *       c(a,bce) => abce        0001 1110 1111
         *       c(b,ace) => abce        0010 1101 1111
         *       c(abe,c) => abce        1011 0100 1111
         *       c(abc,e) => abce        0111 1000 1111
         *
         *       ; 2x2 => 4
         *       c(ab,ce) => abce        0011 1100 1111
         *       c(ac,be) => abce        0101 1010 1111
         *       c(ae,bc) => abce        1001 0110 1111
         */
        fun combinationSplits(bases: List<IElementType>, overlays: List<IElementType>): List<IElementType> {
            val bitMap = HashMap<IElementType, Int>()
            val bitMaskNameMap = HashMap<Int, String>()
            val bitList = ArrayList<IElementType>(bases.size.max(overlays.size))

            var bitMask = 1
            for (item in bases) {
                if (bitMap[item] == null) {
                    bitMap.put(item, bitMask)
                    bitMaskNameMap.put(bitMask, item.toString())
                    bitList.add(item)
                    bitMask = bitMask.shl(1)
                    state.addChildDoesNotExcludeParent(item, TEXT, false)
                }
            }

            val maxBaseMask = bitMask
            val basesBitMask = maxBaseMask - 1
            for (item in overlays) {
                if (bitMap[item] == null) {
                    bitMap.put(item, bitMask)
                    bitMaskNameMap.put(bitMask, item.toString())
                    bitList.add(item)
                    bitMask = bitMask.shl(1)
                    state.addChildDoesNotExcludeParent(item, TEXT, false)
                }
            }

            // start with all singles
            var combinationCount: Int

            val combinationList = ArrayList<IElementType>(bases.size * overlays.size)
            combinationList.addAll(bitList)

            val bitItems = bitList.toSet()

            do {
                combinationCount = combinationList.size
                val newCombinations = ArrayList<IElementType>()
                for (item in combinationList) {
                    val itemBitMask = bitMap[item]!!
                    for (combo in combinationList) {
                        val comboBitMask = bitMap[combo]!!

                        if (comboBitMask and itemBitMask == 0) {
                            // this combination can be a new one
                            val base: IElementType
                            val overlay: IElementType

                            if (itemBitMask < maxBaseMask) {
                                base = item
                                overlay = combo
                            } else {
                                // only allow overlay items with combinations or other overlay items
                                if (bitItems.contains(combo) && comboBitMask < maxBaseMask) {
                                    continue
                                }
                                base = combo
                                overlay = item
                            }

                            val comboName = bitMaskNameMap.computeIfAbsent(itemBitMask or comboBitMask) { base.toString() + "_" + overlay.toString() }
                            val element = createSplitCombo(comboName, base, overlay)

                            if (!bitMap.contains(element)) {
                                bitMap.put(element, itemBitMask or comboBitMask)
                                newCombinations.add(element)
                            }
                        }
                    }
                }

                combinationList.addAll(newCombinations)
            } while (combinationCount < combinationList.size)

            return combinationList
        }

        fun crossSplits(bases: Collection<IElementType>, overlays: Collection<IElementType>): Set<IElementType> {
            val listSet = HashSet<IElementType>(bases.size * overlays.size)

            for (base in bases) {
                if (!listSet.contains(base)) {
                    listSet.add(base)

                    for (overlay in overlays) {
                        if (!listSet.contains(overlay)) {
                            listSet.add(overlay)
                        }

                        val element = createSplitCombo(base.toString() + "_" + overlay.toString(), base, overlay)
                        if (!listSet.contains(element)) {
                            listSet.add(element)
                        }
                    }
                    state.addChildDoesNotExcludeParent(base, TEXT, false)
                }
            }

            return listSet
        }

        fun createSplitCombo(comboName: String, base: IElementType, overlay: IElementType): IElementType {
            var tableCellType = state.COMBINATION_TYPES[comboName]

            if (tableCellType != null) {
                state.addCombinationSplit(tableCellType, base, overlay)
                return tableCellType
            }

            tableCellType = MdTokenType(comboName)
            state.COMBINATION_TYPES[comboName] = tableCellType

            state.addCombinationSplit(tableCellType, base, overlay)

            // TEXT does not exclude the combination
            state.addChildDoesNotExcludeParent(tableCellType, TEXT, false)

            val tableCellSet = TokenSet.create(tableCellType)
            val tableCellKey = highlighterColors.createKey(comboName)
            MdSyntaxHighlighter.fillMap(tableCellSet, tableCellKey)

            val baseKey = MdSyntaxHighlighter.getAttributes()[base]
            val overlayKey = MdSyntaxHighlighter.getAttributes()[overlay]
            if (baseKey != null && overlayKey != null) MdSyntaxHighlighter.addMergedKey(tableCellKey, baseKey, overlayKey)

            // if base and overlay are inline then the combination is an inline
            if (state.INLINE_NON_PLAIN_TEXT.contains(base) || state.INLINE_NON_PLAIN_TEXT.contains(overlay)) {
                state.INLINE_NON_PLAIN_TEXT.add(tableCellType)
            } else if (state.INLINE_SPECIAL_TEXT.contains(base) || state.INLINE_SPECIAL_TEXT.contains(overlay)) {
                state.INLINE_SPECIAL_TEXT.add(tableCellType)
            } else if (state.INLINE_PLAIN_TEXT.contains(base) || state.INLINE_PLAIN_TEXT.contains(overlay)) {
                state.INLINE_PLAIN_TEXT.add(tableCellType)
            }
            return tableCellType
        }
    }

    internal fun addCombinationSplit(resultingType: IElementType, parentType: IElementType, childType: IElementType) {
        COMBINATION_SPLITS.computeIfAbsent(childType) { HashMap(2) }.computeIfAbsent(parentType) { resultingType }
    }
}
