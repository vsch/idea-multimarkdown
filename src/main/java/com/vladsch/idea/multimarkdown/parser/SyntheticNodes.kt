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
package com.vladsch.idea.multimarkdown.parser

import com.intellij.psi.tree.IElementType
import com.intellij.util.text.CharArrayCharSequence
import com.intellij.util.text.CharArrayUtil
import org.apache.log4j.Logger
import org.pegdown.ast.Node
import org.pegdown.ast.SuperNode
import java.util.*

class SyntheticNodes {
    val node: Node
    protected val fullText: CharArray
    val nodes = ArrayList<SyntheticNode>()
    protected var current: Int = 0
    protected var nodeStart: Int = 0
    protected var nodeEnd: Int = 0
    protected var nodeRange: Int = 0
    protected var nodeType: IElementType

    override fun toString(): String {
        val nodeText = CharArrayCharSequence(fullText, nodeStart, nodeEnd)
        return nodeType.toString() + "[" + nodeStart + "," + nodeEnd + ") " + nodeText
    }

    constructor(fullText: CharArray, node: Node, type: IElementType) {
        this.node = node
        this.fullText = fullText
        this.nodeType = type
        nodeStart = node.startIndex
        nodeEnd = node.endIndex
        if (nodeStart > fullText.size) nodeStart = fullText.size
        if (nodeEnd > fullText.size) nodeEnd = fullText.size
        nodeRange = nodeEnd - nodeStart

        nodes.add(SyntheticNode(nodeStart, nodeEnd, type))
        current = 0
    }

    constructor(fullText: CharArray, node: Node, nodeType: IElementType, type: IElementType) {
        this.node = node
        this.fullText = fullText
        this.nodeType = nodeType
        nodeStart = node.startIndex
        nodeEnd = node.endIndex
        if (nodeStart > fullText.size) nodeStart = fullText.size
        if (nodeEnd > fullText.size) nodeEnd = fullText.size
        nodeRange = nodeEnd - nodeStart

        nodes.add(SyntheticNode(nodeStart, nodeEnd, type))
        current = 0
    }

    constructor(fullText: CharArray, node: Node, nodeType: IElementType, type: IElementType, len: Int, openType: IElementType, closeType: IElementType) : this(fullText, node, nodeType, type, len, openType, len, closeType) {
    }

    constructor(fullText: CharArray, node: Node, nodeType: IElementType, type: IElementType, lenOpen: Int, openType: IElementType, lenClose: Int, closeType: IElementType) {
        this.node = node
        this.fullText = fullText
        this.nodeType = nodeType
        nodeStart = node.startIndex
        nodeEnd = node.endIndex
        if (nodeStart > fullText.size) nodeStart = fullText.size
        if (nodeEnd > fullText.size) nodeEnd = fullText.size
        nodeRange = nodeEnd - nodeStart

        nodes.add(SyntheticNode(nodeStart, nodeStart + lenOpen, openType))
        nodes.add(SyntheticNode(nodeStart + lenOpen, nodeEnd - lenClose, type))
        nodes.add(SyntheticNode(nodeEnd - lenClose, nodeEnd, closeType))
        current = 1
    }

    fun validateAllContiguous() {
        var start = nodeStart

        for (node in nodes) {
            assert(node.startIndex == start) { "synthetic node should start " + start + ", instead " + node.startIndex }
            logger.info("validating synth node " + node)
            start = node.endIndex
        }

        assert(start == nodeEnd) { "last synthetic node ended $start instead of $nodeEnd" }
    }

    inner class SyntheticNode(startOffs: Int, endOffs: Int, type: IElementType) {
        var startIndex: Int = 0
            set
        var endIndex: Int = 0
            set
        var type: IElementType
            set

        init {
            assert(startOffs >= nodeStart && startOffs <= nodeEnd)
            assert(endOffs >= nodeStart && endOffs <= nodeEnd)
            assert(startOffs <= endOffs)

            this.type = type
            this.startIndex = startOffs
            this.endIndex = endOffs
        }

        fun chopLead(offs: Int, type: IElementType): SyntheticNode {
            assert(offs >= startIndex && offs <= endIndex)

            val start = startIndex
            startIndex = offs
            return SyntheticNode(start, startIndex, type)
        }

        fun chopTail(offs: Int, type: IElementType): SyntheticNode {
            assert(offs >= startIndex && offs <= endIndex)

            val end = endIndex
            endIndex = offs

            return SyntheticNode(offs, end, type)
        }

        fun indexOf(pattern: Char): Int {
            if (startIndex == endIndex) return -1
            val pos = CharArrayUtil.indexOf(fullText, pattern, startIndex, endIndex)
            if (pos < 0) return -1
            return pos - startIndex
        }

        fun lastIndexOf(pattern: Char): Int {
            if (startIndex == endIndex) return -1
            val pos = CharArrayUtil.lastIndexOf(fullText, pattern, startIndex, endIndex)
            if (pos < 0) return -1
            return pos - startIndex
        }

        fun indexOf(pattern: String): Int {
            if (startIndex == endIndex) return -1
            if (!pattern.isEmpty()) {
                val patternChars = pattern.toCharArray()
                val searchEnd = endIndex - patternChars.size + 1
                var lastPos = startIndex

                outer@ while (lastPos < searchEnd) {
                    val pos = CharArrayUtil.indexOf(fullText, patternChars[0], lastPos, searchEnd)
                    if (pos < 0) return -1

                    if (patternChars.size > 1) {
                        lastPos = pos + 1
                        var i = 0
                        while (++i < patternChars.size) {
                            if (fullText[pos + i] != patternChars[i]) continue@outer
                        }
                    }
                    return pos - startIndex
                }
                return -1
            }
            return 0
        }

        fun lastIndexOf(pattern: String): Int {
            if (startIndex == endIndex) return -1
            if (!pattern.isEmpty()) {
                val patternChars = pattern.toCharArray()
                val searchEnd = endIndex - patternChars.size + 1
                var lastPos = searchEnd

                outer@ while (lastPos > startIndex) {
                    val pos = CharArrayUtil.lastIndexOf(fullText, patternChars[0], startIndex, lastPos)
                    if (pos < 0) return -1

                    if (patternChars.size > 1) {
                        lastPos = pos
                        var i = 0
                        while (++i < patternChars.size) {
                            if (fullText[pos + i] != patternChars[i]) continue@outer
                        }
                    }
                    return pos - startIndex
                }
                return -1
            }
            return endIndex
        }

        override fun toString(): String {
            val nodeText = CharArrayCharSequence(fullText, startIndex, endIndex)
            return type.toString() + "[" + startIndex + "," + endIndex + ") " + nodeText
        }

        fun trimLead(startOffs: Int, vararg pattern: Char): Int {
            var pattern = pattern
            assert(startOffs >= startIndex && startOffs <= endIndex)
            if (pattern.size == 0) pattern = WHITE_SPACE

            var pos = startOffs
            while (pos < endIndex && inPattern(fullText[pos], *pattern)) pos++
            return pos
        }

        fun trimTail(endOffs: Int, vararg pattern: Char): Int {
            var pattern = pattern
            assert(endOffs >= startIndex && endOffs <= endIndex)
            if (pattern.size == 0) pattern = WHITE_SPACE

            var pos = endOffs
            while (pos > startIndex && inPattern(fullText[pos - 1], *pattern)) pos--
            return pos
        }

        fun dropLead(vararg pattern: Char) {
            var pattern = pattern
            if (pattern.size == 0) pattern = WHITE_SPACE

            var pos = startIndex
            while (pos < endIndex && inPattern(fullText[pos], *pattern)) pos++
            startIndex = pos
        }

        fun dropTail(vararg pattern: Char) {
            var pattern = pattern
            if (pattern.size == 0) pattern = WHITE_SPACE

            var pos = endIndex
            while (pos > startIndex && inPattern(fullText[pos - 1], *pattern)) pos--
            endIndex = pos
        }

        fun trimLead(vararg pattern: Char): SyntheticNode? {
            var pattern = pattern
            if (pattern.size == 0) pattern = WHITE_SPACE

            var pos = startIndex
            while (pos < endIndex && inPattern(fullText[pos], *pattern)) pos++

            if (pos > startIndex) {
                if (pos == endIndex) {
                    // change node
                    type = nodeType
                } else {
                    val newNode = SyntheticNode(startIndex, pos, nodeType)
                    startIndex = pos
                    return newNode
                }
            }
            return null
        }

        fun trimTail(vararg pattern: Char): SyntheticNode? {
            var pattern = pattern
            if (pattern.size == 0) pattern = WHITE_SPACE

            var pos = endIndex
            while (pos > startIndex && inPattern(fullText[pos - 1], *pattern)) pos--

            if (pos < endIndex) {
                if (pos == startIndex) {
                    // change node
                    type = nodeType
                } else {
                    val newNode = SyntheticNode(pos, endIndex, nodeType)
                    endIndex = pos
                    return newNode
                }
            }
            return null
        }

        fun getText(startOffs: Int, endOffs: Int): String {
            assert(startOffs >= startIndex && startOffs <= endIndex)
            assert(endOffs >= startIndex && endOffs <= endIndex)
            assert(startOffs <= endOffs)

            val chars = CharArray(endOffs - startOffs)
            System.arraycopy(fullText, startOffs, chars, 0, endOffs - startOffs)
            return String(chars)
        }

        fun getChar(startOffs: Int): Char {
            assert(startOffs >= startIndex && startOffs <= endIndex)
            return fullText[startOffs]
        }
    }

    operator fun next() {
        assert(current + 1 < nodes.size)
        current++
    }

    fun next(next: Int) {
        assert(current + next >= 0)
        assert(current + next < nodes.size)
        current += next
    }

    fun prev() {
        assert(current - 1 >= 0)
        current--
    }

    fun prev(prev: Int) {
        assert(current - prev >= 0)
        assert(current - prev < nodes.size)
        current -= prev
    }

    fun current(current: Int) {
        assert(current >= 0 && current < nodes.size)
        this.current = current
    }

    fun current(): Int {
        return current
    }

    fun currentNode(): SyntheticNode {
        return nodes[current]
    }

    // return true if new node inserted
    fun chopLeadNotEmpty(offs: Int, type: IElementType): Boolean {
        var offs = offs
        val oldNode = nodes[current]
        if (offs < 0)
            offs += oldNode.endIndex
        else
            offs += oldNode.startIndex

        if (offs == oldNode.endIndex) {
            oldNode.type = type
            return false
        } else if (offs > oldNode.startIndex) {
            val newPart = oldNode.chopLead(offs, type)
            nodes.add(current++, newPart)
            return true
        }
        return false
    }

    fun chopLead(offs: Int, type: IElementType) {
        var offs = offs
        val oldNode = nodes[current]
        if (offs < 0)
            offs += oldNode.endIndex
        else
            offs += oldNode.startIndex

        val newPart = oldNode.chopLead(offs, type)
        nodes.add(current++, newPart)
    }

    fun trimLead(type: IElementType, vararg pattern: Char) {
        val oldNode = nodes[current]
        val offs = oldNode.trimLead(oldNode.startIndex, *pattern)

        if (offs > oldNode.startIndex) {
            val newPart = oldNode.chopLead(offs, type)
            nodes.add(current++, newPart)
        }
    }

    // return true if new node inserted
    fun chopTailNotEmpty(offs: Int, type: IElementType): Boolean {
        var offs = offs
        val oldNode = nodes[current]
        if (offs < 0)
            offs += oldNode.endIndex
        else
            offs += oldNode.startIndex

        if (offs == oldNode.startIndex) {
            oldNode.type = type
            return false
        } else if (offs < oldNode.endIndex) {
            val newPart = oldNode.chopTail(offs, type)
            nodes.add(current + 1, newPart)
            return true
        }
        return false
    }

    fun chopTail(offs: Int, type: IElementType) {
        var offs = offs
        val oldNode = nodes[current]
        if (offs < 0)
            offs += oldNode.endIndex
        else
            offs += oldNode.startIndex

        val newPart = oldNode.chopTail(offs, type)
        nodes.add(current + 1, newPart)
    }

    fun trimTail(type: IElementType, vararg pattern: Char) {
        val oldNode = nodes[current]
        val offs = oldNode.trimTail(oldNode.endIndex, *pattern)

        if (offs < oldNode.endIndex) {
            val newPart = oldNode.chopTail(offs, type)
            nodes.add(current + 1, newPart)
        }
    }

    fun chopLeadNotEmpty(pattern: String, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        return pos >= 0 && chopLeadNotEmpty(pos, type)
    }

    fun chopStart(pattern: String, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos == 0) {
            chopLead(pos + pattern.length, type)
            return true
        }
        return false
    }

    fun chopStart(pattern: Char, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos == 0) {
            chopLead(pos + 1, type)
            return true
        }
        return false
    }

    fun chopLead(pattern: String, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopLead(pos, type)
            return true
        }
        return false
    }

    fun chopLead(pattern: Char, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopLead(pos, type)
            return true
        }
        return false
    }

    fun chopLastLead(pattern: String, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.lastIndexOf(pattern)
        if (pos >= 0) {
            chopLead(pos, type)
            return true
        }
        return false
    }

    fun chopLastLead(pattern: Char, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.lastIndexOf(pattern)
        if (pos >= 0) {
            chopLead(pos, type)
            return true
        }
        return false
    }

    fun chopLeadNotEmpty(pattern: String, type: IElementType, patternType: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopLeadNotEmpty(pos, type)
            chopLeadNotEmpty(pattern.length, patternType)
            return true
        }
        return false
    }

    fun chopLead(pattern: String, type: IElementType, patternType: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopLead(pos, type)
            chopLead(pattern.length, patternType)
            return true
        }
        return false
    }

    fun chopLead(pattern: Char, type: IElementType, patternType: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopLead(pos, type)
            chopLead(1, patternType)
            return true
        }
        return false
    }

    enum class ChildLocationType {
        ANY, LEAD, TAIL
    }

    fun chopChildren(locationType: ChildLocationType, type: IElementType): Boolean {
        var children = node.children
        if (children.size == 0) return false

        if (children[0] is SuperNode) {
            children = children[0].children
            if (children.size == 0) return false
        }

        val oldNode = nodes[current]
        val endIndex = children[children.size - 1].endIndex
        val startIndex = children[0].startIndex

        if ((locationType == ChildLocationType.ANY || locationType == ChildLocationType.LEAD) && startIndex == oldNode.startIndex && endIndex <= oldNode.endIndex) {
            val newPart = oldNode.chopTail(endIndex, type)
            nodes.add(current + 1, newPart)
            return true
        } else if ((locationType == ChildLocationType.ANY || locationType == ChildLocationType.TAIL) && startIndex >= oldNode.startIndex && endIndex == oldNode.endIndex) {
            val newPart = oldNode.chopLead(startIndex, type)
            nodes.add(current, newPart)
            current++
            return true
        }

        return false
    }

    fun chopLastLead(pattern: String, type: IElementType, patternType: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.lastIndexOf(pattern)
        if (pos >= 0) {
            chopLead(pos, type)
            chopLead(pattern.length, patternType)
            return true
        }
        return false
    }

    fun chopLastLead(pattern: Char, type: IElementType, patternType: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.lastIndexOf(pattern)
        if (pos >= 0) {
            chopLead(pos, type)
            chopLead(1, patternType)
            return true
        }
        return false
    }

    fun chopTailNotEmpty(pattern: String, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        return pos >= 0 && chopTailNotEmpty(pos, type)
    }

    fun chopTail(pattern: String, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopTail(pos, type)
            return true
        }
        return false
    }

    fun chopTail(pattern: Char, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopTail(pos, type)
            return true
        }
        return false
    }

    fun lastIndexOf(pattern: String): Int {
        val oldNode = nodes[current]
        return oldNode.lastIndexOf(pattern)
    }

    fun lastIndexOf(pattern: Char): Int {
        val oldNode = nodes[current]
        return oldNode.lastIndexOf(pattern)
    }

    fun indexOf(pattern: String): Int {
        val oldNode = nodes[current]
        return oldNode.indexOf(pattern)
    }

    fun indexOf(pattern: Char): Int {
        val oldNode = nodes[current]
        return oldNode.indexOf(pattern)
    }

    fun nodeText(startOffset: Int, endOffset: Int): String {
        var startOffset = startOffset
        var endOffset = endOffset
        val oldNode = nodes[current]
        if (startOffset < 0)
            startOffset += oldNode.endIndex
        else
            startOffset += oldNode.startIndex
        if (endOffset < 0)
            endOffset += oldNode.endIndex
        else
            endOffset += oldNode.startIndex
        return oldNode.getText(startOffset, endOffset)
    }

    fun getChar(startOffset: Int): Char {
        var startOffset = startOffset
        val oldNode = nodes[current]
        if (startOffset < 0)
            startOffset += oldNode.endIndex
        else
            startOffset += oldNode.startIndex
        return oldNode.getChar(startOffset)
    }

    fun chopLastTail(pattern: String, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.lastIndexOf(pattern)
        if (pos >= 0) {
            chopTail(pos, type)
            return true
        }
        return false
    }

    fun chopLastTail(pattern: Char, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.lastIndexOf(pattern)
        if (pos >= 0) {
            chopTail(pos, type)
            return true
        }
        return false
    }

    fun chopEnd(pattern: String, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos == oldNode.endIndex - oldNode.startIndex - pattern.length) {
            chopTail(pos, type)
            return true
        }
        return false
    }

    fun chopEnd(pattern: Char, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos == oldNode.endIndex - oldNode.startIndex - 1) {
            chopTail(pos, type)
            return true
        }
        return false
    }

    fun chopTail(pattern: String, patternType: IElementType, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopTail(pos, patternType)
            val index = current++
            chopTail(pattern.length, type)
            current = index
            return true
        }
        return false
    }

    fun chopTail(pattern: Char, patternType: IElementType, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            chopTail(pos, patternType)
            val index = current++
            chopTail(1, type)
            current = index
            return true
        }
        return false
    }

    fun chopLastTail(pattern: String, patternType: IElementType, type: IElementType): Boolean {
        if (!pattern.isEmpty()) {
            val oldNode = nodes[current]
            val pos = oldNode.lastIndexOf(pattern)
            if (pos >= 0) {
                chopTail(pos, patternType)
                val index = current++
                chopTail(pattern.length, type)
                current = index
                return true
            }
        }
        return false
    }

    fun chopLastTail(pattern: Char, patternType: IElementType, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.lastIndexOf(pattern)
        if (pos >= 0) {
            chopTail(pos, patternType)
            val index = current++
            chopTail(1, type)
            current = index
            return true
        }
        return false
    }

    fun chopTailNotEmpty(pattern: String, patternType: IElementType, type: IElementType): Boolean {
        val oldNode = nodes[current]
        val pos = oldNode.indexOf(pattern)
        if (pos >= 0) {
            val index = current
            if (chopTailNotEmpty(pos, patternType)) current++
            chopTailNotEmpty(pattern.length, type)
            current = index
            return true
        }
        return false
    }

    fun chopMarkers(openLen: Int, openType: IElementType, closeLen: Int, closeType: IElementType) {
        chopLead(openLen, openType)
        chopTail(-closeLen, closeType)
    }

    fun chopMarkers(len: Int, openType: IElementType, closeType: IElementType) {
        chopLead(len, openType)
        chopTail(-len, closeType)
    }

    fun chopMiddle(lenOpen: Int, lenClose: Int, middleType: IElementType) {
        val type = nodes[current].type
        chopTail(lenOpen, middleType)
        next()
        chopTail(-lenClose, type)
    }

    fun chopMiddle(lenOpen: Int, closePattern: String, middleType: IElementType) {
        val type = nodes[current].type
        chopTail(lenOpen, middleType)
        next()
        chopLastTail(closePattern, type)
    }

    fun chopMiddle(lenOpen: Int, closePattern: Char, middleType: IElementType) {
        val type = nodes[current].type
        chopTail(lenOpen, middleType)
        next()
        chopLastTail(closePattern, type)
    }

    fun chopMiddle(openPattern: String, closePattern: String, middleType: IElementType) {
        val oldNode = nodes[current]
        val type = oldNode.type
        val pos = oldNode.indexOf(openPattern)
        if (pos >= 0) {
            chopTail(pos + openPattern.length, middleType)
            next()
        }
        chopLastTail(closePattern, type)
    }

    fun chopMiddle(openPattern: Char, closePattern: Char, middleType: IElementType) {
        val oldNode = nodes[current]
        val type = oldNode.type
        val pos = oldNode.indexOf(openPattern)
        if (pos >= 0) {
            chopTail(pos + 1, middleType)
            next()
        }
        chopLastTail(closePattern, type)
    }

    fun trim(type: IElementType, vararg pattern: Char) {
        trimLead(type, *pattern)
        trimTail(type, *pattern)
    }

    fun dropWhiteSpace(vararg types: IElementType) {
        for (node in nodes) {
            if (inTypes(node.type, types)) {
                node.dropLead()
                node.dropTail()
            }
        }
    }

    fun trimToNodeType(vararg types: IElementType) {
        var i = 0
        while (i < nodes.size) {
            val node = nodes[i]

            if (inTypes(node.type, types)) {
                var newNode = node.trimLead()
                if (newNode != null) {
                    nodes.add(current, newNode)
                    i++
                }

                newNode = node.trimTail()
                if (newNode != null) {
                    nodes.add(current + 1, newNode)
                    i++
                }
            }
            i++
        }
    }

    companion object {
        private val logger = Logger.getLogger(SyntheticNodes::class.java)

        private val WHITE_SPACE = charArrayOf(' ', '\t')

        fun inPattern(c: Char, vararg pattern: Char): Boolean {
            for (p in pattern) {
                if (p == c) return true
            }
            return false
        }

        fun inTypes(type: IElementType, types: Array<out IElementType>): Boolean {
            for (inType in types) {
                if (inType === type) return true
            }
            return false
        }
    }
}
