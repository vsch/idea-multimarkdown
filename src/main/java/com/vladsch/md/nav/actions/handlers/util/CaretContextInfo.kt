// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.hint.HintUtil
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.ui.LightweightHint
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.actions.api.MdCaretContextInfoHandler
import com.vladsch.md.nav.parser.LexParserState
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.format.FormatControlProcessor
import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.minLimit
import com.vladsch.plugin.util.nullIf
import com.vladsch.plugin.util.psi.isTypeOf
import com.vladsch.plugin.util.rangeLimit
import java.util.*
import java.util.function.Consumer
import javax.swing.event.HyperlinkEvent

open class CaretContextInfo private constructor(
    file: PsiFile,
    editor: Editor,
    offset: Int = editor.caretModel.currentCaret.offset,
    char: Char?,
    val isDeleted: Boolean
) : PsiEditAdjustment(offset, file, editor.document.text, char, editor) {

    val document: Document = editor.document
    val caretLine: Int = document.getLineNumber(caretOffset)
    val caretLineStart: Int = document.getLineStartOffset(caretLine)
    val caretLineEnd: Int = document.getLineEndOffset(caretLine)

    val beforeCaretChars: BasedSequence = charSequence.subSequence(document.getLineStartOffset(caretLine), caretOffset)
    val afterCaretChars: BasedSequence = charSequence.subSequence(caretOffset, document.getLineEndOffset(caretLine))
    val caretLineChars: BasedSequence = lineChars(caretLine)!!
    val previousLineChars: BasedSequence? = lineChars(caretLine - 1)
    val nextLineChars: BasedSequence? = lineChars(caretLine + 1)
    val afterCaretChar: Char = if (afterCaretChars.isNotEmpty) afterCaretChars[0] else '\u0000'
    val beforeCaretChar: Char = if (beforeCaretChars.isNotEmpty) beforeCaretChars[beforeCaretChars.lastIndex] else '\u0000'
    val extensionData: MutableDataSet = MutableDataSet()

    private var myIsPreHandler = false
    private val myClosingHandlers = ArrayList<() -> Unit>()
    private var myPositionAdjuster: PositionAdjuster? = null

    override fun getEditor(): Editor = super.getEditor()!!

    val lexParserState: LexParserState.State by lazy { LexParserState.getInstance().state }
    val TYPOGRAPHIC_MARKER_SET: TokenSet get() = lexParserState.TYPOGRAPHIC_MARKER_SET
    val TEXT_SET: TokenSet get() = lexParserState.TEXT_SET
    val TEXT_TOKEN_TYPE_MAP: Map<IElementType, IElementType> get() = lexParserState.TEXT_TOKEN_TYPE_MAP
    val NON_MERGE_TOKEN_SET: TokenSet get() = lexParserState.NON_MERGE_TOKEN_SET
    val COMBINATION_SPLITS: Map<IElementType, Map<IElementType, IElementType>> get() = lexParserState.COMBINATION_SPLITS
    val INLINE_NON_PLAIN_TEXT: List<IElementType> get() = lexParserState.INLINE_NON_PLAIN_TEXT
    val INLINE_SPECIAL_TEXT: List<IElementType> get() = lexParserState.INLINE_SPECIAL_TEXT
    val INLINE_PLAIN_TEXT: List<IElementType> get() = lexParserState.INLINE_PLAIN_TEXT

    var selectionSize: Int = 0
        private set

    var pastEndOfLine: Int = 0
        private set

    // not set anywhere
    var hadAutoTypeDelete: Boolean = false
        private set

    val wrappingContext: WrappingContext? by lazy {
        wrappingContext(preEditOffset(caretOffset))
    }

    val formatterControlProcessor: FormatControlProcessor by lazy {
        FormatControlProcessor(this.file)
    }

    fun isFormatRegion(offset: Int): Boolean {
        return formatterControlProcessor.isFormattingRegion(offset)
    }

    fun wrappingContext(offset: Int): WrappingContext? {
        val useOffset = offset - if (charSequence.isBaseCharAt(offset, CharPredicate.ANY_EOL_NUL)) 1 else 0
        val offsetLineEnd = offsetLineEnd(useOffset) ?: return null
        val offsetLineStart = offsetLineStart(useOffset) ?: return null
        var startElement: PsiElement?

        // NOTE: need offset from line end of 2 if caret is at the end of line to find the right EOL
        var delta = if (useOffset >= offsetLineEnd - 1) 2 else 1
        while (true) {
            startElement = findElementAt((offsetLineEnd - delta).minLimit(offsetLineStart))
            if (offsetLineEnd - delta <= offsetLineStart ||
                (startElement != null
                    && startElement.node.textRange.endOffset > offsetLineStart
                    && startElement.node.textRange.startOffset <= offsetLineEnd
                    && startElement.node.elementType != MdTypes.EOL
                    && startElement.node.elementType != MdTypes.WHITESPACE
                    && (startElement.node.elementType != MdTypes.BLANK_LINE || delta > 1))) break
            delta++
        }

        startElement ?: return null

        startElement = MdPsiImplUtil.getBlockElement(startElement)
        if (startElement == null || !startElement.isTypeOf(MdTokenSets.WRAPPING_BLOCK_ELEMENTS)) return null

        val formatElement = MdPsiImplUtil.findChildTextBlock(startElement)
        val startOffset: Int
        val endOffset: Int
        val prefixStartOffset: Int
        val prefixEndOffset: Int
        var endLineOffset = 0

        if (formatElement == null) {
            // this has an empty body, we take everything between the marker and EOL if any
            val postEditNodeStart = postEditNodeStart(startElement.node)
            startOffset = offsetLineEnd(postEditNodeStart) ?: startElement.node.startOffset
            endOffset = startOffset
            prefixStartOffset = offsetLineStart(postEditNodeStart)?.rangeLimit(0, charSequence.length) ?: 0
            prefixEndOffset = startOffset
        } else {
            startOffset = postEditNodeStart(formatElement.node, PostEditAdjust.IF_INDENT_CHAR).rangeLimit(0, charSequence.length)
            endOffset = postEditNodeEnd(formatElement.node).rangeLimit(startOffset, charSequence.length)
            prefixStartOffset = offsetLineStart(startOffset) ?: startOffset
            prefixEndOffset = startOffset

            if (endOffset > 0 && endOffset == charSequence.length && charSequence[endOffset - 1] != '\n') endLineOffset = 1
        }

        return WrappingContext(
            charSequence,
            startElement,
            formatElement,
            startOffset,
            endOffset,
            prefixStartOffset,
            prefixEndOffset,
            offsetLineNumber(startOffset) ?: 0,
            offsetLineNumber(endOffset) ?: document.lineCount,
            offsetLineNumber(endOffset)?.plus(endLineOffset) ?: document.lineCount
        )
    }

    fun showTooltip(message: String, hyperlinkListener: ((link: String) -> Unit)?) {
        if (myIsPreHandler) {
            // save it, it won't show in pre action handlers
            editor.putUserData(DELAYED_TOOLTIP_KEY, message)
        } else {
            showEditorTooltip(editor, message, hyperlinkListener)
        }
    }

    val isCaretInFrontMatter: Boolean get() = caretOffset < frontMatterOffset

    private fun setRangeHighlighter(rangeHighlighter: RangeHighlighter?, key: Key<RangeHighlighter>) {
        // remove range highlighter
        val oldRangeHighlighter = editor.getUserData(key)
        if (oldRangeHighlighter != null) {
            val markupModel = DocumentMarkupModel.forDocument(document, file.project, false) as MarkupModelEx?
            markupModel?.removeHighlighter(oldRangeHighlighter)
        }

        if (rangeHighlighter != null) {
            editor.putUserData(key, rangeHighlighter)
        }
    }

    val mirroredCount: Int by lazy {
        val beforeChar = if (isInsertOp && char != null) char else beforeCaretChar
        if (isMirrorChar(beforeChar) || beforeChar in " \t") {
            val endIndex = if (isInsertOp && beforeCaretChars.isNotEmpty) beforeCaretChars.lastIndex else beforeCaretChars.length
            val startIndex = 0
            val beforeInsertCaretChars = beforeCaretChars.subSequence(startIndex, endIndex)
            val mirrored = afterCaretChars.isMirrored(beforeInsertCaretChars, enabledMirrorChars(), maxMirrored(beforeChar)) ?: -1
            mirrored.maxLimit(maxMirrored(beforeChar))
        } else {
            -1
        }
    }

    fun enabledMirrorChars(): String {
        var chars = ""
        for (char in SMART_AUTO_MIRROR_CHARS) {
            if (isMirrorChar(char)) chars += char
        }
        return chars
    }

    fun maxMirrored(char: Char?): Int = if (char == '~') 2 else if (char == '`') 6 else 3

    fun firstNonBlank(offset: Int): Int {
        return when {
            offset >= charSequence.length -> charSequence.length
            else -> {
                val pos = charSequence.indexOfAnyNot(CharPredicate.SPACE_TAB, offset)
                if (pos < 0) charSequence.length else pos
            }
        }
    }

    fun addSubContext(caretInfo: CaretContextInfo) {
        addDoneHandler { caretInfo.runDoneHandlers() }
    }

    private fun addDoneHandler(doneHandler: () -> Unit) {
        myClosingHandlers.add(doneHandler)
    }

    private fun addDoneHandler(doneHandler: Runnable) {
        myClosingHandlers.add { doneHandler.run() }
    }

    private fun runDoneHandlers() {
        for (handler in myClosingHandlers.reversed()) {
            try {
                handler()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    /**
     * opening and closing handlers
     */
    fun beforeCharTypedHandler(): CaretContextInfo? {
        // store information about the size of the selection, if we will handle it
        var hadSelection = false
        var hadPastEol = false
        if (editor.caretModel.caretCount == 1) {
            if (editor.caretModel.primaryCaret.hasSelection()) {
                editor.putUserData(SELECTION_SIZE_KEY, editor.caretModel.primaryCaret.selectionEnd - editor.caretModel.primaryCaret.selectionStart)
                hadSelection = true
            } else {
                // if the caret is after the end of line then we need to add the virtual spaces that will be added
                val log = editor.caretModel.primaryCaret.logicalPosition
                val offsetCol = caretOffset - caretLineStart
                val caretPast = log.column - offsetCol
                editor.putUserData(PAST_EOL_SIZE_KEY, caretPast)
                pastEndOfLine = caretPast
                hadPastEol = true
            }
        }
        if (!hadSelection) editor.putUserData(SELECTION_SIZE_KEY, null)
        if (!hadPastEol) editor.putUserData(PAST_EOL_SIZE_KEY, null)
        editor.putUserData(DELAYED_TOOLTIP_KEY, null)
        myIsPreHandler = true
        caretContextInfoHandlers.forEach { it.beforeCharTypedHandler(this, this@CaretContextInfo::addDoneHandler) }
        return this
    }

    fun adjustExpectedEditOpDelta(delta: Int) {
        editor.putUserData(EDIT_OP_DELTA_ADJUSTMENT_KEY, delta.nullIf(0))
    }

    // return null if no formatting is to be done because the tree is out of sync with the file
    // and we cannot compensate
    fun charTypedHandler(): CaretContextInfo? {
        // take information stored about selection and clear it
        selectionSize = editor.getUserData(SELECTION_SIZE_KEY) ?: 0
        editor.putUserData(SELECTION_SIZE_KEY, null)

        pastEndOfLine = editor.getUserData(PAST_EOL_SIZE_KEY) ?: 0
        editor.putUserData(PAST_EOL_SIZE_KEY, null)

        val adjustEditOpDelta = editor.getUserData(EDIT_OP_DELTA_ADJUSTMENT_KEY) ?: 0
        editor.putUserData(EDIT_OP_DELTA_ADJUSTMENT_KEY, null)

        editOpDelta = charSequence.length - file.textLength
        val multiByteAdjustment = if (char == null || editOpDelta >= 0) 0 else editOpDelta - adjustEditOpDelta

        return if (editOpDelta >= adjustEditOpDelta + multiByteAdjustment && editOpDelta <= 10 + adjustEditOpDelta) {
            addDoneHandler { typedHandlerDone() }
            caretContextInfoHandlers.forEach { it.charTypedHandler(this, this@CaretContextInfo::addDoneHandler) }
            this
        } else {
            null
        }
    }

    fun beforeBackspaceHandler(): CaretContextInfo? {
        editor.putUserData(DELAYED_TOOLTIP_KEY, null)
        myIsPreHandler = true
        caretContextInfoHandlers.forEach { it.beforeBackspaceHandler(this, this@CaretContextInfo::addDoneHandler) }
        return this
    }

    fun backspaceHandler(): CaretContextInfo? {
        editOpDelta = charSequence.length - file.textLength
        addDoneHandler { typedHandlerDone() }
        caretContextInfoHandlers.forEach { it.backspaceHandler(this, this@CaretContextInfo::addDoneHandler) }
        return this
    }

    fun beforeEnterHandler(): CaretContextInfo? {
        caretContextInfoHandlers.forEach { it.beforeEnterHandler(this, this@CaretContextInfo::addDoneHandler) }
        return this
    }

    fun enterHandler(): CaretContextInfo? {
        caretContextInfoHandlers.forEach { it.enterHandler(this, this@CaretContextInfo::addDoneHandler) }
        return this
    }

    private fun handlerDone() {
        runDoneHandlers()
    }

    private fun typedHandlerDone() {
        val message = editor.getUserData(DELAYED_TOOLTIP_KEY)
        editor.putUserData(DELAYED_TOOLTIP_KEY, null)
        if (message != null) showTooltip(message, null)
    }

    fun adjustedDocumentPosition(offset: Int): AdjustingDocumentPosition {
        val positionAdjuster = myPositionAdjuster
        if (positionAdjuster == null) {
            // first one, create listener
            val newPositionAdjuster = PositionAdjuster()
            document.addDocumentListener(newPositionAdjuster)
            myPositionAdjuster = newPositionAdjuster

            addDoneHandler {
                document.removeDocumentListener(newPositionAdjuster)
            }
            return newPositionAdjuster.adjustDocumentPosition(offset)
        }
        return positionAdjuster.adjustDocumentPosition(offset)
    }

    fun lineChars(line: Int): BasedSequence? {
        return if (line >= 0 && line < document.lineCount) charSequence.subSequence(document.getLineStartOffset(line), document.getLineEndOffset(line)) else null
    }

    fun lineStart(line: Int): Int? {
        return if (line >= 0 && line < document.lineCount) document.getLineStartOffset(line) else null
    }

    fun lineEnd(line: Int): Int? {
        return if (line >= 0 && line < document.lineCount) document.getLineEndOffset(line) else null
    }

    fun offsetLineStart(offset: Int): Int? {
        return if (offset >= 0 && offset <= document.textLength) document.getLineStartOffset(document.getLineNumber(offset)) else null
    }

    fun offsetLineEnd(offset: Int): Int? {
        return if (offset >= 0 && offset <= document.textLength) document.getLineEndOffset(document.getLineNumber(offset)) else null
    }

    fun offsetLineNumber(offset: Int): Int? {
        return if (offset >= 0 && offset <= document.textLength) document.getLineNumber(offset) else null
    }

    private class PositionAdjuster : DocumentListener {
        private val myAdjustingPositions = ArrayList<AdjustingDocumentPosition>()

        fun adjustDocumentPosition(offset: Int): AdjustingDocumentPosition {
            val pos = AdjustingDocumentPosition(offset)
            myAdjustingPositions.add(pos)
            return pos
        }

        override fun documentChanged(event: DocumentEvent) {
            val editOpDelta = event.newLength - event.oldLength
            if (editOpDelta < 0) {
                // text deleted
                for (pos in myAdjustingPositions) {
                    if (pos.adjustedOffset >= event.offset) {
                        if (pos.adjustedOffset < event.offset - editOpDelta) {
                            // within the deleted region
                            pos.adjustedOffset = event.offset
                            pos.wasDeleted = true
                        } else {
                            pos.adjustedOffset = (pos.adjustedOffset + editOpDelta).rangeLimit(0, event.document.textLength)
                        }
                    }
                }
            } else if (editOpDelta > 0) {
                // text inserted
                for (pos in myAdjustingPositions) {
                    if (pos.adjustedOffset >= event.offset) {
                        pos.adjustedOffset = (pos.adjustedOffset + editOpDelta).rangeLimit(0, event.document.textLength)
                    }
                }
            }
        }

        override fun beforeDocumentChange(event: DocumentEvent) {
        }
    }

    // finally, let extensions initialize context
    init {
        caretContextInfoHandlers.forEach { it.initializeContext(this, this@CaretContextInfo::addDoneHandler) }
    }

    companion object {
        private val contextLogger = Logger.getInstance("com.vladsch.md.nav.editor.caret-context")

        val caretContextInfoHandlers: Array<MdCaretContextInfoHandler> by lazy { MdCaretContextInfoHandler.EP_NAME.extensions }

        private val SELECTION_SIZE_KEY = Key.create<Int>("MARKDOWN_NAVIGATOR.SELECTION_SIZE")
        private val PAST_EOL_SIZE_KEY = Key.create<Int>("MARKDOWN_NAVIGATOR.PAST_EOL_SIZE_KEY")
        private val EDIT_OP_DELTA_ADJUSTMENT_KEY = Key.create<Int>("MARKDOWN_NAVIGATOR.EXPECTED_EDIT_OP_ADJUST")
        private val DELAYED_TOOLTIP_KEY = Key.create<String>("MARKDOWN_NAVIGATOR.DELAYED_TOOLTIP")

        const val SMART_AUTO_MIRROR_CHARS: String = "*~_`"

        @JvmStatic
        fun isMirrorChar(char: Char?): Boolean {
            val documentSettings = MdApplicationSettings.instance.documentSettings
            return when (char) {
                '*' -> documentSettings.smartEditAsterisks
                '_' -> documentSettings.smartEditUnderscore
                '~' -> documentSettings.smartEditTildes
                '`' -> documentSettings.smartEditBackTicks
                else -> false
            }
        }

        @JvmStatic
        fun showEditorTooltip(editor: Editor, message: String, hyperlinkListener: ((link: String) -> Unit)?) {
            // NOTE: do not display hints during tests
            if (ApplicationManager.getApplication().isUnitTestMode) return

            val hintManager = HintManager.getInstance() as HintManagerImpl
            val flags = HintManager.HIDE_BY_ANY_KEY or HintManager.HIDE_BY_SCROLLING or HintManager.HIDE_BY_ESCAPE
            val timeout = 15000 // default?
            val html = HintUtil.prepareHintText(message, HintUtil.getInformationHint())
            var hint: LightweightHint? = null

            val label = if (hyperlinkListener == null) HintUtil.createInformationLabel(html)
            else HintUtil.createInformationLabel(html, { hyperlinkEvent ->
                if (hyperlinkEvent.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    hint?.hide()
                    if (hyperlinkEvent.url == null) {
                        val link = hyperlinkEvent.description
                        hyperlinkListener.invoke(link)
                    } else {
                        BrowserUtil.browse(hyperlinkEvent.url.toString())
                    }
                }
            }, null, null)

            hint = LightweightHint(label)
            val p = hintManager.getHintPosition(hint, editor, HintManager.ABOVE)
            hintManager.showEditorHint(hint, editor, p, flags, timeout, false)
        }

        fun withContext(file: PsiFile, editor: Editor, char: Char?, isDeleted: Boolean, caretOffset: Int? = null, runnable: Consumer<CaretContextInfo>) {
            withContextOr(file, editor, char, isDeleted, false, caretOffset) {
                runnable.accept(it)
                true
            }
        }

        fun withContextOrFalse(file: PsiFile, editor: Editor, char: Char?, isDeleted: Boolean, caretOffset: Int? = null, runnable: Consumer<CaretContextInfo>): Boolean {
            return withContextOr(file, editor, char, isDeleted, false, caretOffset) {
                runnable.accept(it)
                true
            }
        }

        fun withContext(file: PsiFile, editor: Editor, char: Char?, isDeleted: Boolean, caretOffset: Int? = null, runnable: (caretContext: CaretContextInfo) -> Unit) {
            withContextOr(file, editor, char, isDeleted, false, caretOffset) {
                runnable.invoke(it)
                true
            }
        }

        fun withContextOrFalse(file: PsiFile, editor: Editor, char: Char?, isDeleted: Boolean, caretOffset: Int? = null, runnable: (caretContext: CaretContextInfo) -> Unit): Boolean {
            return withContextOr(file, editor, char, isDeleted, false, caretOffset) {
                runnable.invoke(it)
                true
            }
        }

        fun <T : Any?> withContextOr(file: PsiFile, editor: Editor, char: Char?, isDeleted: Boolean, noContextValue: T, caretOffset: Int? = null, runnable: (caretContext: CaretContextInfo) -> T): T {
            return withContextOrNull(file, editor, char, isDeleted, caretOffset) {
                return@withContextOrNull if (it == null) noContextValue else runnable.invoke(it)
            }
        }

        fun <T : Any?> withContextOrNull(file: PsiFile,
            editor: Editor,
            char: Char?,
            isDeleted: Boolean,
            caretOffset: Int? = null,
            runnable: (caretContext: CaretContextInfo?) -> T): T {
            if (editor.document.textLength != 0) {
                val contextInfo = CaretContextInfo(file, editor, caretOffset ?: editor.caretModel.currentCaret.offset, char, isDeleted)
                try {
                    return if (char != null) {
//                        TimeIt.logTimedValue(contextLogger, "withContext c=$char, del=$isDeleted $runnable(contextInfo)") {
                        runnable.invoke(contextInfo)
//                        }
                    } else {
                        runnable.invoke(contextInfo)
                    }
                } finally {
                    contextInfo.handlerDone()
                }
            }
            return if (char != null) {
//                TimeIt.logTimedValue(contextLogger, "withContextOrNull c=$char, del=$isDeleted $runnable(null)") {
                runnable.invoke(null)
//                }
            } else {
                runnable.invoke(null)
            }
        }

        fun subContext(context: CaretContextInfo, caretOffset: Int): CaretContextInfo {
            val caretInfo = CaretContextInfo(context.file, context.editor, caretOffset.maxLimit(context.editor.document.textLength), context.char, context.isDeleted)
            caretInfo.selectionSize = context.selectionSize
            caretInfo.pastEndOfLine = context.pastEndOfLine
            caretInfo.editOpDelta = context.editOpDelta
            caretInfo.hadAutoTypeDelete = context.hadAutoTypeDelete
            caretInfo.isForceDelete = context.isForceDelete

            // close this one when the parent closes
            context.addSubContext(caretInfo)
            return caretInfo
        }
    }
}

fun getDiffInfo(orig: CharSequence, wrap: CharSequence, force: Boolean): DiffInfo {
    val wrapLen = wrap.length
    if (force) return DiffInfo(isEqual = false, lastCharDiff = false, firstDiff = 0, lastDiff = wrap.length, lastLineDiffOnly = false)

    var pos = 0
    val origLen = orig.length
    val endPos = origLen.maxLimit(wrapLen)

    while (pos < endPos) {
        if (orig[pos] != wrap[pos]) break
        pos++
    }

    if (pos < endPos || (origLen - wrapLen) > 1 || (origLen - wrapLen) < -1) {
        // see if this is a last line diff only
        val lastEOL = orig.lastIndexOf('\n', origLen)
        var lastPos = 0
        val firstPos = endPos - pos

        while (lastPos < firstPos) {
            if (orig[origLen - lastPos - 1] != wrap[wrapLen - lastPos - 1]) {
                break
            }
            lastPos++
        }

        return DiffInfo(isEqual = false, lastCharDiff = false, firstDiff = pos, lastDiff = lastPos, lastLineDiffOnly = lastEOL < pos)
    }

    return DiffInfo(pos == endPos, lastCharDiff = true, firstDiff = 0, lastDiff = 0, lastLineDiffOnly = false)
}
