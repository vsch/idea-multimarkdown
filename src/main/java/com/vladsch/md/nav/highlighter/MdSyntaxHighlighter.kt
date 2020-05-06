// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.impl.EditorColorsManagerImpl
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileTypes.PlainTextSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.vladsch.md.nav.parser.MdLexer
import com.vladsch.md.nav.parser.MdPlainTextLexer
import com.vladsch.md.nav.psi.util.MdTokenSets.*
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.SyntaxHighlightingType
import java.awt.Color
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap

class MdSyntaxHighlighter constructor(val renderingProfile: MdRenderingProfile, val forSampleDoc: Boolean, val forAnnotator: Boolean) : SyntaxHighlighterBase() {

    constructor() : this(MdRenderingProfile.DEFAULT, false, false)

    override fun getHighlightingLexer(): Lexer {
        val noSyntax = MdApplicationSettings.instance.documentSettings.syntaxHighlighting == SyntaxHighlightingType.NONE.intValue
        val renderingProfile = this.renderingProfile
        return when {
            forAnnotator -> MdPlainTextLexer()
            forSampleDoc -> MdLexer(MdRenderingProfile.FOR_SAMPLE_DOC)
            noSyntax -> PlainTextSyntaxHighlighterFactory.createPlainTextLexer()
            else ->
                MdLexer(renderingProfile)
        }
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        val textAttributesKey = ATTRIBUTES[tokenType]
//        if (textAttributesKey == null) {
//            val tmp = 0
//        }
        return SyntaxHighlighterBase.pack(textAttributesKey)
    }

    companion object {
        private val MERGED_ATTRIBUTES = HashMap<TextAttributesKey, List<TextAttributesKey>>()

        private val ATTRIBUTES: HashMap<IElementType, TextAttributesKey> by lazy {
            val map = HashMap<IElementType, TextAttributesKey>()
            val highlighterColors = MdHighlighterColors.getInstance()


            fillMap(map, TEXT_FOR_SYNTAX_SET, highlighterColors.TEXT_ATTR_KEY)
            fillMap(map, ABBREVIATION_SET, highlighterColors.ABBREVIATION_ATTR_KEY)
            fillMap(map, ABBREVIATION_SHORT_TEXT_SET, highlighterColors.ABBREVIATION_SHORT_TEXT_ATTR_KEY)
            fillMap(map, ABBREVIATION_EXPANDED_TEXT_SET, highlighterColors.ABBREVIATION_EXPANDED_TEXT_ATTR_KEY)
            fillMap(map, ABBREVIATED_TEXT_SET, highlighterColors.ABBREVIATED_TEXT_ATTR_KEY)
            fillMap(map, AUTO_LINK_SET, highlighterColors.AUTO_LINK_ATTR_KEY)
            fillMap(map, ANCHOR_SET, highlighterColors.ANCHOR_ATTR_KEY)
            fillMap(map, ANCHOR_ID_SET, highlighterColors.ANCHOR_ID_ATTR_KEY)
            fillMap(map, EMOJI_MARKER_SET, highlighterColors.EMOJI_MARKER_ATTR_KEY)
            fillMap(map, EMOJI_ID_SET, highlighterColors.EMOJI_ID_ATTR_KEY)
            fillMap(map, ASIDE_BLOCK_SET, highlighterColors.ASIDE_BLOCK_ATTR_KEY)
            fillMap(map, BLOCK_QUOTE_SET, highlighterColors.BLOCK_QUOTE_ATTR_KEY)
            fillMap(map, BOLD_MARKER_SET, highlighterColors.BOLD_MARKER_ATTR_KEY)
            fillMap(map, BOLD_SET, highlighterColors.BOLD_ATTR_KEY)
            fillMap(map, UNDERLINE_MARKER_SET, highlighterColors.UNDERLINE_MARKER_ATTR_KEY)
            fillMap(map, UNDERLINE_SET, highlighterColors.UNDERLINE_ATTR_KEY)
            fillMap(map, SUPERSCRIPT_MARKER_SET, highlighterColors.SUPERSCRIPT_MARKER_ATTR_KEY)
            fillMap(map, SUPERSCRIPT_SET, highlighterColors.SUPERSCRIPT_ATTR_KEY)
            fillMap(map, SUBSCRIPT_MARKER_SET, highlighterColors.SUBSCRIPT_MARKER_ATTR_KEY)
            fillMap(map, SUBSCRIPT_SET, highlighterColors.SUBSCRIPT_ATTR_KEY)
            fillMap(map, BULLET_LIST_SET, highlighterColors.BULLET_LIST_ATTR_KEY)
            fillMap(map, COMMENT_SET, highlighterColors.COMMENT_ATTR_KEY)
            fillMap(map, BLOCK_COMMENT_SET, highlighterColors.BLOCK_COMMENT_ATTR_KEY)
            fillMap(map, CODE_SET, highlighterColors.CODE_ATTR_KEY)
            fillMap(map, CODE_MARKER_SET, highlighterColors.CODE_MARKER_ATTR_KEY)
            fillMap(map, GITLAB_MATH_SET, highlighterColors.GITLAB_MATH_ATTR_KEY)
            fillMap(map, GITLAB_MATH_MARKER_SET, highlighterColors.GITLAB_MATH_MARKER_ATTR_KEY)
            fillMap(map, DEFINITION_MARKER_SET, highlighterColors.DEFINITION_MARKER_ATTR_KEY)
            fillMap(map, DEFINITION_TERM_SET, highlighterColors.DEFINITION_TERM_ATTR_KEY)
            fillMap(map, EXPLICIT_LINK_SET, highlighterColors.EXPLICIT_LINK_ATTR_KEY)
            fillMap(map, FOOTNOTE_SET, highlighterColors.FOOTNOTE_ATTR_KEY)
            fillMap(map, FOOTNOTE_REF_SET, highlighterColors.FOOTNOTE_REF_ATTR_KEY)
            fillMap(map, FOOTNOTE_ID_SET, highlighterColors.FOOTNOTE_ID_ATTR_KEY)
            fillMap(map, ATX_HEADER_SET, highlighterColors.ATX_HEADER_ATTR_KEY)
            fillMap(map, SETEXT_HEADER_SET, highlighterColors.SETEXT_HEADER_ATTR_KEY)
            fillMap(map, HEADER_TEXT_SET, highlighterColors.HEADER_TEXT_ATTR_KEY)
            fillMap(map, HEADER_ATX_MARKER_SET, highlighterColors.HEADER_ATX_MARKER_ATTR_KEY)
            fillMap(map, HEADER_SETEXT_MARKER_SET, highlighterColors.HEADER_SETEXT_MARKER_ATTR_KEY)
            fillMap(map, HRULE_SET, highlighterColors.HRULE_ATTR_KEY)
            fillMap(map, HTML_BLOCK_SET, highlighterColors.HTML_BLOCK_ATTR_KEY)
            fillMap(map, JEKYLL_FRONT_MATTER_BLOCK_SET, highlighterColors.JEKYLL_FRONT_MATTER_BLOCK_ATTR_KEY)
            fillMap(map, JEKYLL_FRONT_MATTER_MARKER_SET, highlighterColors.JEKYLL_FRONT_MATTER_MARKER_ATTR_KEY)
            fillMap(map, JEKYLL_TAG_MARKER_SET, highlighterColors.JEKYLL_TAG_MARKER_ATTR_KEY)
            fillMap(map, JEKYLL_TAG_NAME_SET, highlighterColors.JEKYLL_TAG_NAME_ATTR_KEY)
            fillMap(map, JEKYLL_TAG_PARAMETERS_SET, highlighterColors.JEKYLL_TAG_PARAMETERS_ATTR_KEY)

            fillMap(map, MACRO_SET, highlighterColors.MACRO_ATTR_KEY)
            fillMap(map, MACRO_REF_SET, highlighterColors.MACRO_REF_ATTR_KEY)
            fillMap(map, MACRO_ID_SET, highlighterColors.MACRO_ID_ATTR_KEY)

            fillMap(map, ATTRIBUTES_MARKER_SET, highlighterColors.ATTRIBUTES_MARKER_ATTR_KEY)
            fillMap(map, ATTRIBUTE_NAME_SET, highlighterColors.ATTRIBUTE_NAME_ATTR_KEY)
            fillMap(map, ATTRIBUTE_VALUE_SEP_SET, highlighterColors.ATTRIBUTE_VALUE_SEP_ATTR_KEY)
            fillMap(map, ATTRIBUTE_VALUE_MARKER_SET, highlighterColors.ATTRIBUTE_VALUE_MARKER_ATTR_KEY)
            fillMap(map, ENUM_REF_FORMAT_SET, highlighterColors.ENUM_REF_FORMAT_ATTR_KEY)
            fillMap(map, ENUM_REF_LINK_SET, highlighterColors.ENUM_REF_LINK_ATTR_KEY)
            fillMap(map, ENUM_REF_TEXT_SET, highlighterColors.ENUM_REF_TEXT_ATTR_KEY)
            fillMap(map, ENUM_REF_ID_SET, highlighterColors.ENUM_REF_ID_ATTR_KEY)

            fillMap(map, ADMONITION_MARKER_SET, highlighterColors.ADMONITION_MARKER_ATTR_KEY)
            fillMap(map, ADMONITION_INFO_SET, highlighterColors.ADMONITION_INFO_ATTR_KEY)
            fillMap(map, ADMONITION_TITLE_SET, highlighterColors.ADMONITION_TITLE_ATTR_KEY)

            fillMap(map, FLEXMARK_MARKER_SET, highlighterColors.FLEXMARK_MARKER_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_SECTION_SET, highlighterColors.FLEXMARK_EXAMPLE_SECTION_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_SECTION_MARKER_SET, highlighterColors.FLEXMARK_EXAMPLE_SECTION_MARKER_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_NUMBER_SET, highlighterColors.FLEXMARK_EXAMPLE_NUMBER_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD_SET, highlighterColors.FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTIONS_KEYWORD_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTIONS_KEYWORD_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTIONS_MARKER_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTIONS_MARKER_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTION_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTION_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTION_PARAM_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTION_PARAM_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTION_PARAM_MARKER_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTION_PARAM_MARKER_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTION_BUILT_IN_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTION_BUILT_IN_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTION_IGNORE_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTION_IGNORE_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTION_FAIL_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTION_FAIL_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME_SET, highlighterColors.FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME_ATTR_KEY)
            fillMap(map, FLEXMARK_EXAMPLE_SEPARATOR_SET, highlighterColors.FLEXMARK_EXAMPLE_SEPARATOR_ATTR_KEY)

            fillMap(map, IMAGE_LINK_REF_SET, highlighterColors.IMAGE_LINK_REF_ATTR_KEY)
            fillMap(map, IMAGE_URL_CONTENT_SET, highlighterColors.IMAGE_URL_CONTENT_ATTR_KEY)
            fillMap(map, IMAGE_LINK_REF_TITLE_SET, highlighterColors.IMAGE_LINK_REF_TITLE_ATTR_KEY)
            fillMap(map, IMAGE_ALT_TEXT_SET, highlighterColors.IMAGE_ALT_TEXT_ATTR_KEY)
            fillMap(map, IMAGE_SET, highlighterColors.IMAGE_ATTR_KEY)
            fillMap(map, INLINE_HTML_SET, highlighterColors.INLINE_HTML_ATTR_KEY)
            fillMap(map, HTML_ENTITY_SET, highlighterColors.HTML_ENTITY_ATTR_KEY)
            fillMap(map, ITALIC_MARKER_SET, highlighterColors.ITALIC_MARKER_ATTR_KEY)
            fillMap(map, ITALIC_SET, highlighterColors.ITALIC_ATTR_KEY)
            fillMap(map, LINK_REF_SET, highlighterColors.LINK_REF_ATTR_KEY)
            fillMap(map, LINK_REF_TEXT_SET, highlighterColors.LINK_REF_TEXT_ATTR_KEY)
            fillMap(map, LINK_REF_TITLE_SET, highlighterColors.LINK_REF_TITLE_ATTR_KEY)
            fillMap(map, LINK_REF_ANCHOR_SET, highlighterColors.LINK_REF_ANCHOR_ATTR_KEY)
            fillMap(map, LINK_REF_ANCHOR_MARKER_SET, highlighterColors.LINK_REF_ANCHOR_MARKER_ATTR_KEY)
            fillMap(map, MAIL_LINK_SET, highlighterColors.MAIL_LINK_ATTR_KEY)
            fillMap(map, ORDERED_LIST_SET, highlighterColors.ORDERED_LIST_ATTR_KEY)
            fillMap(map, QUOTE_SET, highlighterColors.QUOTE_ATTR_KEY)
            fillMap(map, QUOTED_TEXT_SET, highlighterColors.QUOTED_TEXT_ATTR_KEY)
            fillMap(map, REFERENCE_IMAGE_SET, highlighterColors.REFERENCE_IMAGE_ATTR_KEY)
            fillMap(map, REFERENCE_IMAGE_REFERENCE_SET, highlighterColors.REFERENCE_IMAGE_REFERENCE_ATTR_KEY)
            fillMap(map, REFERENCE_IMAGE_TEXT_SET, highlighterColors.REFERENCE_IMAGE_TEXT_ATTR_KEY)
            fillMap(map, REFERENCE_LINK_SET, highlighterColors.REFERENCE_LINK_ATTR_KEY)
            fillMap(map, REFERENCE_LINK_REFERENCE_SET, highlighterColors.REFERENCE_LINK_REFERENCE_ATTR_KEY)
            fillMap(map, REFERENCE_LINK_TEXT_SET, highlighterColors.REFERENCE_LINK_TEXT_ATTR_KEY)
            fillMap(map, REFERENCE_SET, highlighterColors.REFERENCE_ATTR_KEY)
            fillMap(map, REFERENCE_LINK_REF_SET, highlighterColors.REFERENCE_LINK_REF_ATTR_KEY)
            fillMap(map, REFERENCE_TITLE_SET, highlighterColors.REFERENCE_TITLE_ATTR_KEY)
            fillMap(map, REFERENCE_TEXT_SET, highlighterColors.REFERENCE_TEXT_ATTR_KEY)
            fillMap(map, REFERENCE_ANCHOR_SET, highlighterColors.REFERENCE_ANCHOR_ATTR_KEY)
            fillMap(map, REFERENCE_ANCHOR_MARKER_SET, highlighterColors.REFERENCE_ANCHOR_MARKER_ATTR_KEY)
            fillMap(map, SMARTS_SET, highlighterColors.SMARTS_ATTR_KEY)
            fillMap(map, SPECIAL_TEXT_SET, highlighterColors.SPECIAL_TEXT_ATTR_KEY)
            fillMap(map, SPECIAL_TEXT_MARKER_SET, highlighterColors.SPECIAL_TEXT_MARKER_ATTR_KEY)
            fillMap(map, LINE_BREAK_SPACES_SET, highlighterColors.LINE_BREAK_SPACES_ATTR_KEY)
            fillMap(map, STRIKETHROUGH_MARKER_SET, highlighterColors.STRIKETHROUGH_MARKER_ATTR_KEY)
            fillMap(map, STRIKETHROUGH_SET, highlighterColors.STRIKETHROUGH_ATTR_KEY)
            fillMap(map, TABLE_CAPTION_SET, highlighterColors.TABLE_CAPTION_ATTR_KEY)
            fillMap(map, TABLE_CAPTION_MARKER_SET, highlighterColors.TABLE_CAPTION_MARKER_ATTR_KEY)
            fillMap(map, TABLE_CELL_REVEN_CEVEN_SET, highlighterColors.TABLE_CELL_REVEN_CEVEN_ATTR_KEY)
            fillMap(map, TABLE_CELL_REVEN_CODD_SET, highlighterColors.TABLE_CELL_REVEN_CODD_ATTR_KEY)
            fillMap(map, TABLE_CELL_RODD_CEVEN_SET, highlighterColors.TABLE_CELL_RODD_CEVEN_ATTR_KEY)
            fillMap(map, TABLE_CELL_RODD_CODD_SET, highlighterColors.TABLE_CELL_RODD_CODD_ATTR_KEY)
            fillMap(map, TABLE_ROW_EVEN_SET, highlighterColors.TABLE_ROW_EVEN_ATTR_KEY)
            fillMap(map, TABLE_ROW_ODD_SET, highlighterColors.TABLE_ROW_ODD_ATTR_KEY)
            fillMap(map, TABLE_HDR_CELL_REVEN_CEVEN_SET, highlighterColors.TABLE_HDR_CELL_REVEN_CEVEN_ATTR_KEY)
            fillMap(map, TABLE_HDR_CELL_REVEN_CODD_SET, highlighterColors.TABLE_HDR_CELL_REVEN_CODD_ATTR_KEY)
            fillMap(map, TABLE_HDR_CELL_RODD_CEVEN_SET, highlighterColors.TABLE_HDR_CELL_RODD_CEVEN_ATTR_KEY)
            fillMap(map, TABLE_HDR_CELL_RODD_CODD_SET, highlighterColors.TABLE_HDR_CELL_RODD_CODD_ATTR_KEY)
            fillMap(map, TABLE_HDR_ROW_EVEN_SET, highlighterColors.TABLE_HDR_ROW_EVEN_ATTR_KEY)
            fillMap(map, TABLE_HDR_ROW_ODD_SET, highlighterColors.TABLE_HDR_ROW_ODD_ATTR_KEY)
            fillMap(map, TABLE_SEP_COLUMN_ODD_SET, highlighterColors.TABLE_SEP_COLUMN_ODD_ATTR_KEY)
            fillMap(map, TABLE_SEP_COLUMN_EVEN_SET, highlighterColors.TABLE_SEP_COLUMN_EVEN_ATTR_KEY)
            fillMap(map, TABLE_SEPARATOR_ROW_SET, highlighterColors.TABLE_SEPARATOR_ATTR_KEY)
            fillMap(map, TABLE_SET, highlighterColors.TABLE_ATTR_KEY)
            fillMap(map, TEXT_SET, highlighterColors.TEXT_ATTR_KEY)
            fillMap(map, TOC_SET, highlighterColors.TOC_ATTR_KEY)
            fillMap(map, GEN_CONTENT_SET, highlighterColors.GEN_CONTENT_ATTR_KEY)
            fillMap(map, TOC_MARKER_SET, highlighterColors.TOC_MARKER_ATTR_KEY)
            fillMap(map, TOC_OPTION_SET, highlighterColors.TOC_OPTION_ATTR_KEY)
            fillMap(map, SIM_TOC_TITLE_SET, highlighterColors.SIM_TOC_TITLE_ATTR_KEY)
            fillMap(map, TASK_ITEM_MARKER_SET, highlighterColors.TASK_ITEM_MARKER_ATTR_KEY)
            fillMap(map, TASK_DONE_MARKER_ITEM_SET, highlighterColors.TASK_DONE_ITEM_MARKER_ATTR_KEY)
            fillMap(map, VERBATIM_SET, highlighterColors.VERBATIM_ATTR_KEY)
            fillMap(map, VERBATIM_MARKER_SET, highlighterColors.VERBATIM_MARKER_ATTR_KEY)
            fillMap(map, VERBATIM_CONTENT_SET, highlighterColors.VERBATIM_CONTENT_ATTR_KEY)
            fillMap(map, VERBATIM_LANG_SET, highlighterColors.VERBATIM_LANG_ATTR_KEY)
            fillMap(map, WIKI_LINK_SET, highlighterColors.WIKI_LINK_ATTR_KEY)
            fillMap(map, WIKI_LINK_SEPARATOR_SET, highlighterColors.WIKI_LINK_SEPARATOR_ATTR_KEY)
            fillMap(map, WIKI_LINK_REF_SET, highlighterColors.WIKI_LINK_REF_ATTR_KEY)
            fillMap(map, WIKI_LINK_REF_ANCHOR_SET, highlighterColors.WIKI_LINK_REF_ANCHOR_ATTR_KEY)
            fillMap(map, WIKI_LINK_REF_ANCHOR_MARKER_SET, highlighterColors.WIKI_LINK_REF_ANCHOR_MARKER_ATTR_KEY)
            fillMap(map, WIKI_LINK_TEXT_SET, highlighterColors.WIKI_LINK_TEXT_ATTR_KEY)

            map
        }

        private fun fillMap(map: HashMap<IElementType, TextAttributesKey>, keys: TokenSet, value: TextAttributesKey) {
            fillMap(map, value, *keys.types)
        }

        @JvmStatic
        fun fillMap(keys: TokenSet, value: TextAttributesKey) {
            fillMap(ATTRIBUTES, value, *keys.types)
        }

        private fun fillMap(map: HashMap<IElementType, TextAttributesKey>, value: TextAttributesKey, vararg types: IElementType) {
            for (type in types) {
                map[type] = value
            }
        }

        @JvmStatic
        fun getAttributes(): Map<IElementType, TextAttributesKey> {
            return ATTRIBUTES
        }

        @JvmStatic
        fun getMergedKeys(): Collection<TextAttributesKey> {
            return MERGED_ATTRIBUTES.keys
        }

        @JvmStatic
        fun addMergedKey(combinedKey: TextAttributesKey, baseKey: TextAttributesKey, overlayKey: TextAttributesKey) {
            val list = ArrayList<TextAttributesKey>(2)
            list.add(baseKey)
            list.add(overlayKey)
            MERGED_ATTRIBUTES.put(combinedKey, list)
        }

        private var inMergeAttributes = AtomicBoolean(false)

        @JvmStatic
        fun computeMergedAttributes(initEditors: Boolean) {
            if (!inMergeAttributes.getAndSet(true)) {
                try {
                    val scheme = EditorColorsManager.getInstance().globalScheme
                    val keys = getMergedKeys()
                    for (key in keys) {
                        val list = MERGED_ATTRIBUTES[key] ?: continue
                        var combinedAttr: TextAttributes? = null

                        for (attrKey in list) {
                            val attr = scheme.getAttributes(attrKey)

                            if (combinedAttr == null) {
                                combinedAttr = attr.clone()
                            } else {
                                combinedAttr.foregroundColor = combineColors(combinedAttr.foregroundColor, attr.foregroundColor)
                                combinedAttr.backgroundColor = combineColors(combinedAttr.backgroundColor, attr.backgroundColor)
                                combinedAttr.effectType = if (combinedAttr.effectColor == null) attr.effectType else combinedAttr.effectType
                                combinedAttr.effectColor = combineColors(combinedAttr.effectColor, attr.effectColor)
                                combinedAttr.errorStripeColor = combineColors(combinedAttr.errorStripeColor, attr.errorStripeColor)
                                combinedAttr.fontType = combineFontType(combinedAttr.fontType, attr.fontType)
                            }
                        }

                        scheme.setAttributes(key, combinedAttr)
                    }

                    if (initEditors) {
                        val manager = EditorColorsManager.getInstance() as EditorColorsManagerImpl
                        manager.schemeChangedOrSwitched(manager.globalScheme)
                    }
                } finally {
                    inMergeAttributes.set(false)
                }
            }
        }

        private fun combineFontType(baseFontType: Int, overlayFontType: Int): Int {
            return baseFontType or overlayFontType
        }

        @JvmStatic
        fun combineColors(baseColor: Color?, overlayColor: Color?): Color? {
            if (overlayColor == null) return baseColor
            if (baseColor == null) return overlayColor

            val colors = baseColor.getRGBComponents(null)
            val overlays = overlayColor.getRGBComponents(null)
            val overlayAlpha = 0.5f
            val baseAlpha = (1.0f - overlayAlpha)
            val averageAlpha = 0.10f
            val multiplyAlpha = (1.0f - averageAlpha)

            val results = colors.clone()
            for (i in 0 .. 2) {
                if (colors[i] * overlays[i] < 0.25) {
                    results[i] = 1.0f - (((1.0f - colors[i]) * baseAlpha + (1.0f - overlays[i]) * overlayAlpha) * (1.0f - averageAlpha) + (1.0f - colors[i]) * (1.0f - overlays[i]) * (1.0f - multiplyAlpha))
                } else {
                    results[i] = (colors[i] * baseAlpha + overlays[i] * overlayAlpha) * averageAlpha + colors[i] * overlays[i] * multiplyAlpha
                }
            }

            return Color(results[0], results[1], results[2], colors[3])
        }
    }
}
