// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex

import com.vladsch.flexmark.util.collection.OrderedMap
import com.vladsch.flexmark.util.sequence.BasedSequence
import java.util.*

class Template {
    var openMarker = "options("
    var closeMarker = ")"
    var commonMarker: String? = null
    val replaceMap = OrderedMap<String, String>()
    val optionSet = HashSet<String>()

    constructor()

    constructor(openMarker: String, closeMarker: String, commonMarker: String?) {
        this.openMarker = openMarker
        this.closeMarker = closeMarker
        this.commonMarker = commonMarker
    }

    constructor(openMarker: String, closeMarker: String) {
        this.openMarker = openMarker
        this.closeMarker = closeMarker
    }

    fun haveSomeOptions(options: Collection<String>): Boolean {
        for (option in options) {
            if (optionSet.contains(option)) return true
        }
        return false
    }

    fun replaceText(text: String): String {
        if (commonMarker != null && !text.contains(commonMarker!!, ignoreCase = true)) return text
        var result = text
        for (entry in replaceMap.entries) {
            result = result.replace(entry.key, entry.value)
        }

        return result
    }

    // filter out lines whose options are not in the set
    fun filterLines(text: String): String {
        val useText = BasedSequence.of(text)
        var pos = useText.indexOf(openMarker)
        if (pos != -1) {
            val out = StringBuilder()
            var lastPos = 0
            do {
                val lineStart = useText.lastIndexOf('\n', pos) + 1
                if (lastPos < lineStart) {
                    // output up to line start
                    out.append(useText.substring(lastPos, lineStart))
                }

                var lineEnd = useText.indexOf('\n', pos)
                var lineEOLPos = lineEnd
                if (lineEnd == -1) {
                    lineEnd = useText.length
                    lineEOLPos = lineEnd
                } else lineEnd++

                lastPos = lineEnd

                val endPos = useText.indexOf(closeMarker, pos + openMarker.length)
                if (endPos == -1 || endPos > lineEOLPos) {
                    out.append(useText.substring(lineStart, lineEnd))
                } else {
                    // get the options
                    var commentPrefixPos = useText.lastIndexOf("//", pos)
                    if (commentPrefixPos == -1) commentPrefixPos = pos

                    val commentPrefix = useText.substring(commentPrefixPos, pos)
                    val lineIndent = useText.substring(lineStart, lineStart + useText.countLeadingSpaceTab(lineStart))
                    val linePrefix = useText.substring(lineStart + lineIndent.length, commentPrefixPos)
                    val lineSuffix = useText.substring(endPos + closeMarker.length, lineEOLPos)
                    val lineEOL = useText.substring(lineEOLPos, lineEnd)

                    val options = useText.substring(pos + openMarker.length, endPos).trim().split(',').map { it.trim() }.filter { !it.isEmpty() }

                    var remove = false
                    var commentOut = false
                    if (!options.isEmpty() && !haveSomeOptions(options)) {
                        if (options.contains("REMOVE")) {
                            // remove the line
                            remove = true
                        } else {
                            commentOut = true
                        }
                    }

                    if (!remove) {
                        if (!commentOut) {
                            if (!linePrefix.isBlank() || !lineSuffix.isBlank()) {
                                out.append(lineIndent)
                                out.append(linePrefix)
                                if (!lineSuffix.isBlank()) {
                                    out.append(commentPrefix)
                                    out.append(lineSuffix)
                                }
                                out.append(lineEOL)
                            }
                        } else {
                            if (!linePrefix.isBlank()) {
                                if (!lineSuffix.isBlank()) {
                                    out.append(lineIndent)
                                    out.append("// ")
                                    out.append(linePrefix)
                                    out.append(commentPrefix)
                                    out.append(lineSuffix)
                                    out.append(lineEOL)
                                } else {
                                    out.append(lineIndent)
                                    out.append("// ")
                                    out.append(linePrefix)
                                    out.append(lineEOL)
                                }
                            } else if (!lineSuffix.isBlank()) {
                                out.append(lineIndent)
                                out.append("// ")
                                out.append(lineSuffix)
                                out.append(lineEOL)
                            }
                        }
                    }
                }

                // find the next one
                pos = useText.indexOf(openMarker, lineEnd)
            } while (pos >= 0 && pos < useText.length)

            if (lastPos < useText.length) {
                out.append(useText.substring(lastPos, useText.length))
            }
            return out.toString()
        }
        return text
    }
}
