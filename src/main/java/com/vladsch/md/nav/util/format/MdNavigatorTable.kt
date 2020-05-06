// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

import com.intellij.openapi.editor.LogicalPosition
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.format.*
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.LineAppendable

@Suppress("MemberVisibilityCanBePrivate")
class MdNavigatorTable(val markdownTable: MarkdownTable) {

    private var _offset: Int = 0
    private var _cellOffsetInfo: TableCellOffsetInfo? = null

    var offset: Int
        get() = _offset
        set(value: Int) {
            if (_offset != value) {
                _offset = value
                _cellOffsetInfo = null
            }
        }

    // after setting offset can get these
    val exactColumn: Int? get() = cellOffsetInfo.insideColumn
    val inColumnOffset: Int? get() = cellOffsetInfo.insideOffset
    val offsetColumn: Int get() = cellOffsetInfo.column
    val offsetRow: Int get() = cellOffsetInfo.row
    val offsetTableCell: TableCell? get() = cellOffsetInfo.tableCell
    val offsetTableRow: TableRow? get() = cellOffsetInfo.tableRow

    val header: TableSection = markdownTable.header
    val separator: TableSection = markdownTable.separator
    val body: TableSection = markdownTable.body

    var caption: BasedSequence
        get() = markdownTable.captionCell.text
        set(caption: BasedSequence) = markdownTable.setCaption(caption)

    val captionOpen: BasedSequence get() = markdownTable.captionCell.openMarker
    val captionClose: BasedSequence get() = markdownTable.captionCell.closeMarker
    val headingRowCount: Int get() = markdownTable.headingRowCount
    val bodyRowCount: Int get() = markdownTable.bodyRowCount
    val captionRowCount: Int get() = markdownTable.captionRowCount
    val maxHeadingColumns: Int get() = markdownTable.maxHeadingColumns
    val maxSeparatorColumns: Int get() = markdownTable.maxSeparatorColumns
    val maxBodyColumns: Int get() = markdownTable.maxBodyColumns
    val minColumns: Int get() = markdownTable.minColumns
    val maxColumns: Int get() = markdownTable.maxColumns
    val trackedOffsets: List<TrackedOffset> get() = markdownTable.trackedOffsets
    val allRows: List<TableRow> get() = markdownTable.allRows as List<TableRow>
    val allContentRows: List<TableRow> get() = markdownTable.allContentRows as List<TableRow>
    val allRowsCount: Int get() = markdownTable.allRowsCount
    val allContentRowsCount: Int get() = markdownTable.allContentRowsCount

    val separatorRow: Int get() = markdownTable.header.rows.size

    var options: DataHolder
        get() = MutableDataSet().setFrom(markdownTable.options)
        set(value) {
            markdownTable.options = TableFormatOptions(options.toMutable().setAll(value))
        }

    val cellOffsetInfo: TableCellOffsetInfo
        get() {
            if (_cellOffsetInfo == null) {
                _cellOffsetInfo = markdownTable.getCellOffsetInfo(offset)
            }
            return _cellOffsetInfo!!
        }

    fun cellOffsetInfo(offset: Int): TableCellOffsetInfo {
        return markdownTable.getCellOffsetInfo(offset)
    }

    fun fillMissingColumns() {
        markdownTable.fillMissingColumns()
    }

    fun fillMissingColumns(minColumn: Int?) {
        markdownTable.fillMissingColumns(minColumn)
    }

    @Suppress("UNUSED_PARAMETER")
    fun logicalPositionFromColumnOffset(tableStartColumn: Int, row: Int, column: Int, firstRowOffset: Int, inColumnOffset: Int): LogicalPosition? {
        // RELEASE: QUERY: check why it always returns null
//        var logicalRowPosition: LogicalPosition? = null
//        if (row < this.allRowsCount) {
//            val tableRow = this.allRows[row]
//            val indexSpanOffset = tableRow.indexOf(column)
//            logicalRowPosition = logicalRowPosition(tableStartColumn, row + firstRowOffset, indexSpanOffset.index, inColumnOffset, tableRow)
//        }
//        return logicalRowPosition
        return null
    }

    @Suppress("UNUSED_PARAMETER")
    fun logicalPosition(tableStartColumn: Int, row: Int, column: Int, firstRowOffset: Int, inColumnOffset: Int): LogicalPosition? {
        // RELEASE: QUERY: check why it always returns null
//        var logicalRowPosition: LogicalPosition? = null
//        if (row < this.allRowsCount) {
//            logicalRowPosition = logicalRowPosition(tableStartColumn, row + firstRowOffset, column, inColumnOffset, this.allRows[row])
//        }
//        return logicalRowPosition
        return null
    }

    fun logicalRowPosition(tableStartColumn: Int, row: Int, index: Int, inColumnOffset: Int, tableRow: TableRow): LogicalPosition? {
        if (index < tableRow.cells.size) {
            var col = 0
            val endColIndex = if (inColumnOffset < 0) index else index - 1
            for (i in 0 .. endColIndex) {
                col += tableRow.cells[i].getCellLength(if (i > 0) tableRow.cells[i - 1] else null)
            }
            return LogicalPosition(row, tableStartColumn + col + inColumnOffset)
        }
        return null
    }

    fun addTrackedOffset(trackedOffset: TrackedOffset): Boolean {
        return markdownTable.addTrackedOffset(trackedOffset)
    }

    fun deleteRows(rowIndex: Int, count: Int) {
        markdownTable.deleteRows(rowIndex, count)
    }

    fun insertRows(rowIndex: Int, count: Int) {
        markdownTable.insertRows(rowIndex, count)
    }

    fun insertColumns(column: Int, count: Int) {
        markdownTable.insertColumns(column, count)
    }

    fun deleteColumns(column: Int, count: Int) {
        markdownTable.deleteColumns(column, count)
    }

    fun moveColumn(fromColumn: Int, toColumn: Int) {
        markdownTable.moveColumn(fromColumn, toColumn)
    }

    fun isAllRowsSeparator(index: Int): Boolean {
        return markdownTable.isAllRowsSeparator(index)
    }

    fun isEmptyColumn(column: Int): Boolean {
        return markdownTable.isEmptyColumn(column)
    }

    fun isEmptyRow(row: Int): Boolean {
        return markdownTable.isAllRowsEmptyAt(row)
    }

    fun normalize() {
        markdownTable.normalize()
    }

    fun finalizeTable() {
        markdownTable.finalizeTable()
    }

    fun appendTable(out: LineAppendable) {
        markdownTable.appendTable(out)
    }

    fun appendFormattedCaption(out: LineAppendable, caption: BasedSequence, options: TableFormatOptions) {
        MarkdownTable.appendFormattedCaption(out, caption, options)
    }

    fun formattedCaption(caption: BasedSequence, options: TableFormatOptions): String {
        return MarkdownTable.formattedCaption(caption, options)
    }
}
