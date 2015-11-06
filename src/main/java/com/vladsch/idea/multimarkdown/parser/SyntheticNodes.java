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
package com.vladsch.idea.multimarkdown.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.util.text.CharArrayCharSequence;
import com.intellij.util.text.CharArrayUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.pegdown.ast.Node;

import java.util.ArrayList;

public class SyntheticNodes {
    private static final Logger logger = Logger.getLogger(SyntheticNodes.class);

    private static final char[] WHITE_SPACE = new char[] { ' ', '\t' };
    final protected Node node;
    final protected char[] fullText;
    final protected ArrayList<SyntheticNode> syntheticNodes = new ArrayList<SyntheticNode>();
    protected int current;
    protected int nodeStart;
    protected int nodeEnd;
    protected int nodeRange;
    protected IElementType nodeType;

    public ArrayList<SyntheticNode> getNodes() {
        return syntheticNodes;
    }

    public Node getNode() {
        return node;
    }

    public SyntheticNodes(char[] fullText, Node node, IElementType type) {
        this.node = node;
        this.fullText = fullText;
        this.nodeType = type;
        nodeStart = node.getStartIndex();
        nodeEnd = node.getEndIndex();
        if (nodeStart > fullText.length) nodeStart = fullText.length;
        if (nodeEnd > fullText.length) nodeEnd = fullText.length;
        nodeRange = nodeEnd - nodeStart;

        syntheticNodes.add(new SyntheticNode(nodeStart, nodeEnd, type));
        current = 0;
    }

    public SyntheticNodes(char[] fullText, Node node, IElementType nodeType, IElementType type) {
        this.node = node;
        this.fullText = fullText;
        this.nodeType = nodeType;
        nodeStart = node.getStartIndex();
        nodeEnd = node.getEndIndex();
        if (nodeStart > fullText.length) nodeStart = fullText.length;
        if (nodeEnd > fullText.length) nodeEnd = fullText.length;
        nodeRange = nodeEnd - nodeStart;

        syntheticNodes.add(new SyntheticNode(nodeStart, nodeEnd, type));
        current = 0;
    }

    public SyntheticNodes(char[] fullText, Node node, IElementType nodeType, IElementType type, int len, IElementType openType, IElementType closeType) {
        this(fullText, node, nodeType, type, len, openType, len, closeType);
    }

    public SyntheticNodes(char[] fullText, Node node, IElementType nodeType, IElementType type, int lenOpen, IElementType openType, int lenClose, IElementType closeType) {
        this.node = node;
        this.fullText = fullText;
        this.nodeType = nodeType;
        nodeStart = node.getStartIndex();
        nodeEnd = node.getEndIndex();
        if (nodeStart > fullText.length) nodeStart = fullText.length;
        if (nodeEnd > fullText.length) nodeEnd = fullText.length;
        nodeRange = nodeEnd - nodeStart;

        syntheticNodes.add(new SyntheticNode(nodeStart, nodeStart + lenOpen, openType));
        syntheticNodes.add(new SyntheticNode(nodeStart + lenOpen, nodeEnd - lenClose, type));
        syntheticNodes.add(new SyntheticNode(nodeEnd - lenClose, nodeEnd, closeType));
        current = 1;
    }

    public void validateAllContiguous() {
        int start = nodeStart;

        for (SyntheticNode node : syntheticNodes) {
            assert node.startIndex == start : "synthetic node should start " + start +", instead " + node.startIndex;
            logger.info("validating synth node " + node);
            start = node.endIndex;
        }

        assert start == nodeEnd : "last synthetic node ended " + start + " instead of " + nodeEnd;
    }

    public class SyntheticNode {
        int startIndex;
        int endIndex;
        IElementType type;

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public IElementType getType() {
            return type;
        }

        public SyntheticNode(int startOffs, int endOffs, IElementType type) {
            assert startOffs >= nodeStart && startOffs <= nodeEnd;
            assert endOffs >= nodeStart && endOffs <= nodeEnd;
            assert startOffs <= endOffs;

            this.type = type;
            this.startIndex = startOffs;
            this.endIndex = endOffs;
        }

        SyntheticNode chopLead(int offs, IElementType type) {
            assert offs >= startIndex && offs <= endIndex;

            int start = startIndex;
            startIndex = offs;
            return new SyntheticNode(start, startIndex, type);
        }

        SyntheticNode chopTail(int offs, IElementType type) {
            assert offs >= startIndex && offs <= endIndex;

            int end = endIndex;
            endIndex = offs;

            return new SyntheticNode(offs, end, type);
        }

        protected int indexOf(char pattern) {
            if (startIndex == endIndex) return -1;
            int pos = CharArrayUtil.indexOf(fullText, pattern, startIndex, endIndex);
            if (pos < 0) return -1;
            return pos - startIndex;
        }

        protected int lastIndexOf(char pattern) {
            if (startIndex == endIndex) return -1;
            int pos = CharArrayUtil.lastIndexOf(fullText, pattern, startIndex, endIndex);
            if (pos < 0) return -1;
            return pos - startIndex;
        }

        protected int indexOf(String pattern) {
            if (startIndex == endIndex) return -1;
            if (!pattern.isEmpty()) {
                char[] patternChars = pattern.toCharArray();
                int searchEnd = endIndex - patternChars.length + 1;
                int lastPos = startIndex;

                outer:
                while (lastPos < searchEnd) {
                    int pos = CharArrayUtil.indexOf(fullText, patternChars[0], lastPos, searchEnd);
                    if (pos < 0) return -1;

                    if (patternChars.length > 1) {
                        lastPos = pos + 1;
                        int i = 0;
                        while (++i < patternChars.length) {
                            if (fullText[pos + i] != patternChars[i]) continue outer;
                        }
                    }
                    return pos - startIndex;
                }
                return -1;
            }
            return 0;
        }

        protected int lastIndexOf(String pattern) {
            if (startIndex == endIndex) return -1;
            if (!pattern.isEmpty()) {
                char[] patternChars = pattern.toCharArray();
                int searchEnd = endIndex - patternChars.length + 1;
                int lastPos = searchEnd;

                outer:
                while (lastPos > startIndex) {
                    int pos = CharArrayUtil.lastIndexOf(fullText, patternChars[0], startIndex, lastPos);
                    if (pos < 0) return -1;

                    if (patternChars.length > 1) {
                        lastPos = pos;
                        int i = 0;
                        while (++i < patternChars.length) {
                            if (fullText[pos + i] != patternChars[i]) continue outer;
                        }
                    }
                    return pos - startIndex;
                }
                return -1;
            }
            return endIndex;
        }

        @Override
        public String toString() {
            CharArrayCharSequence nodeText = new CharArrayCharSequence(fullText, startIndex, endIndex);
            return type + "[" + startIndex + "," + endIndex + ") " + nodeText;
        }

        public int trimLead(int startOffs, char... pattern) {
            assert startOffs >= startIndex && startOffs <= endIndex;
            if (pattern.length == 0) pattern = WHITE_SPACE;

            int pos = startOffs;
            while (pos < endIndex && inPattern(fullText[pos], pattern)) pos++;
            return pos;
        }

        public int trimTail(int endOffs, char... pattern) {
            assert endOffs >= startIndex && endOffs <= endIndex;
            if (pattern.length == 0) pattern = WHITE_SPACE;

            int pos = endOffs;
            while (pos > startIndex && inPattern(fullText[pos - 1], pattern)) pos--;
            return pos;
        }

        public void dropLead(char... pattern) {
            if (pattern.length == 0) pattern = WHITE_SPACE;

            int pos = startIndex;
            while (pos < endIndex && inPattern(fullText[pos], pattern)) pos++;
            startIndex = pos;
        }

        public void dropTail(char... pattern) {
            if (pattern.length == 0) pattern = WHITE_SPACE;

            int pos = endIndex;
            while (pos > startIndex && inPattern(fullText[pos - 1], pattern)) pos--;
            endIndex = pos;
        }

        @Nullable
        public SyntheticNode trimLead(char... pattern) {
            if (pattern.length == 0) pattern = WHITE_SPACE;

            int pos = startIndex;
            while (pos < endIndex && inPattern(fullText[pos], pattern)) pos++;

            if (pos > startIndex) {
                if (pos == endIndex) {
                    // change node
                    type = nodeType;
                } else {
                    SyntheticNode newNode = new SyntheticNode(startIndex, pos, nodeType);
                    startIndex = pos;
                    return newNode;
                }
            }
            return null;
        }

        @Nullable
        public SyntheticNode trimTail(char... pattern) {
            if (pattern.length == 0) pattern = WHITE_SPACE;

            int pos = endIndex;
            while (pos > startIndex && inPattern(fullText[pos - 1], pattern)) pos--;

            if (pos < endIndex) {
                if (pos == startIndex) {
                    // change node
                    type = nodeType;
                } else {
                    SyntheticNode newNode = new SyntheticNode(pos, endIndex, nodeType);
                    endIndex = pos;
                    return newNode;
                }
            }
            return null;
        }

        public String getText(int startOffs, int endOffs) {
            assert startOffs >= startIndex && startOffs <= endIndex;
            assert endOffs >= startIndex && endOffs <= endIndex;
            assert startOffs <= endOffs;

            char[] chars = new char[endOffs - startOffs];
            System.arraycopy(fullText, startOffs, chars, 0, endOffs - startOffs);
            return new String(chars);
        }
        public char getChar(int startOffs) {
            assert startOffs >= startIndex && startOffs <= endIndex;
            return fullText[startOffs];
        }
    }

    public static boolean inPattern(char c, char... pattern) {
        for (char p : pattern) {
            if (p == c) return true;
        }
        return false;
    }

    void next() {
        assert current + 1 < syntheticNodes.size();
        current++;
    }

    void next(int next) {
        assert current + next >= 0;
        assert current + next < syntheticNodes.size();
        current += next;
    }

    void prev() {
        assert current - 1 >= 0;
        current--;
    }

    void prev(int prev) {
        assert current - prev >= 0;
        assert current - prev < syntheticNodes.size();
        current -= prev;
    }

    void current(int current) {
        assert current >= 0 && current < syntheticNodes.size();
        this.current = current;
    }

    int current() {
        return current;
    }

    public SyntheticNode currentNode() {
        return syntheticNodes.get(current);
    }

    // return true if new node inserted
    boolean chopLeadNotEmpty(int offs, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        if (offs < 0) offs += oldNode.endIndex;
        else offs += oldNode.startIndex;

        if (offs == oldNode.endIndex) {
            oldNode.type = type;
            return false;
        } else if (offs > oldNode.startIndex) {
            SyntheticNode newPart = oldNode.chopLead(offs, type);
            syntheticNodes.add(current++, newPart);
            return true;
        }
        return false;
    }

    void chopLead(int offs, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        if (offs < 0) offs += oldNode.endIndex;
        else offs += oldNode.startIndex;

        SyntheticNode newPart = oldNode.chopLead(offs, type);
        syntheticNodes.add(current++, newPart);
    }

    public void trimLead(IElementType type, char... pattern) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int offs = oldNode.trimLead(oldNode.startIndex, pattern);

        if (offs > oldNode.startIndex) {
            SyntheticNode newPart = oldNode.chopLead(offs, type);
            syntheticNodes.add(current++, newPart);
        }
    }

    // return true if new node inserted
    boolean chopTailNotEmpty(int offs, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        if (offs < 0) offs += oldNode.endIndex;
        else offs += oldNode.startIndex;

        if (offs == oldNode.startIndex) {
            oldNode.type = type;
            return false;
        } else if (offs < oldNode.endIndex) {
            SyntheticNode newPart = oldNode.chopTail(offs, type);
            syntheticNodes.add(current + 1, newPart);
            return true;
        }
        return false;
    }

    void chopTail(int offs, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        if (offs < 0) offs += oldNode.endIndex;
        else offs += oldNode.startIndex;

        SyntheticNode newPart = oldNode.chopTail(offs, type);
        syntheticNodes.add(current + 1, newPart);
    }

    public void trimTail(IElementType type, char... pattern) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int offs = oldNode.trimTail(oldNode.endIndex, pattern);

        if (offs < oldNode.endIndex) {
            SyntheticNode newPart = oldNode.chopTail(offs, type);
            syntheticNodes.add(current + 1, newPart);
        }
    }

    boolean chopLeadNotEmpty(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        return pos >= 0 && chopLeadNotEmpty(pos, type);
    }

    boolean chopStart(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos == 0) {
            chopLead(pos + pattern.length(), type);
            return true;
        }
        return false;
    }

    boolean chopStart(char pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos == 0) {
            chopLead(pos + 1, type);
            return true;
        }
        return false;
    }

    boolean chopLead(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopLead(pos, type);
            return true;
        }
        return false;
    }

    boolean chopLead(char pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopLead(pos, type);
            return true;
        }
        return false;
    }

    boolean chopLastLead(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.lastIndexOf(pattern);
        if (pos >= 0) {
            chopLead(pos, type);
            return true;
        }
        return false;
    }

    boolean chopLastLead(char pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.lastIndexOf(pattern);
        if (pos >= 0) {
            chopLead(pos, type);
            return true;
        }
        return false;
    }

    boolean chopLeadNotEmpty(String pattern, IElementType type, IElementType patternType) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopLeadNotEmpty(pos, type);
            chopLeadNotEmpty(pattern.length(), patternType);
            return true;
        }
        return false;
    }

    boolean chopLead(String pattern, IElementType type, IElementType patternType) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopLead(pos, type);
            chopLead(pattern.length(), patternType);
            return true;
        }
        return false;
    }

    boolean chopLead(char pattern, IElementType type, IElementType patternType) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopLead(pos, type);
            chopLead(1, patternType);
            return true;
        }
        return false;
    }

    boolean chopLastLead(String pattern, IElementType type, IElementType patternType) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.lastIndexOf(pattern);
        if (pos >= 0) {
            chopLead(pos, type);
            chopLead(pattern.length(), patternType);
            return true;
        }
        return false;
    }

    boolean chopLastLead(char pattern, IElementType type, IElementType patternType) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.lastIndexOf(pattern);
        if (pos >= 0) {
            chopLead(pos, type);
            chopLead(1, patternType);
            return true;
        }
        return false;
    }

    boolean chopTailNotEmpty(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        return pos >= 0 && chopTailNotEmpty(pos, type);
    }

    boolean chopTail(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopTail(pos, type);
            return true;
        }
        return false;
    }

    boolean chopTail(char pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopTail(pos, type);
            return true;
        }
        return false;
    }

    int lastIndexOf(String pattern) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        return oldNode.lastIndexOf(pattern);
    }

    int lastIndexOf(char pattern) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        return oldNode.lastIndexOf(pattern);
    }

    int indexOf(String pattern) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        return oldNode.indexOf(pattern);
    }

    int indexOf(char pattern) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        return oldNode.indexOf(pattern);
    }

    String nodeText(int startOffset, int endOffset) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        if (startOffset < 0) startOffset += oldNode.endIndex;
        else startOffset += oldNode.startIndex;
        if (endOffset < 0) endOffset += oldNode.endIndex;
        else endOffset += oldNode.startIndex;
        return oldNode.getText(startOffset, endOffset);
    }

    public char getChar(int startOffset) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        if (startOffset < 0) startOffset += oldNode.endIndex;
        else startOffset += oldNode.startIndex;
        return oldNode.getChar(startOffset);
    }

    boolean chopLastTail(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.lastIndexOf(pattern);
        if (pos >= 0) {
            chopTail(pos, type);
            return true;
        }
        return false;
    }

    boolean chopLastTail(char pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.lastIndexOf(pattern);
        if (pos >= 0) {
            chopTail(pos, type);
            return true;
        }
        return false;
    }

    boolean chopEnd(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos == oldNode.endIndex - oldNode.startIndex - pattern.length()) {
            chopTail(pos, type);
            return true;
        }
        return false;
    }

    boolean chopEnd(char pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos == oldNode.endIndex - oldNode.startIndex - 1) {
            chopTail(pos, type);
            return true;
        }
        return false;
    }

    boolean chopTail(String pattern, IElementType patternType, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopTail(pos, patternType);
            int index = current++;
            chopTail(pattern.length(), type);
            current = index;
            return true;
        }
        return false;
    }

    boolean chopTail(char pattern, IElementType patternType, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            chopTail(pos, patternType);
            int index = current++;
            chopTail(1, type);
            current = index;
            return true;
        }
        return false;
    }

    boolean chopLastTail(String pattern, IElementType patternType, IElementType type) {
        if (!pattern.isEmpty()) {
            SyntheticNode oldNode = syntheticNodes.get(current);
            int pos = oldNode.lastIndexOf(pattern);
            if (pos >= 0) {
                chopTail(pos, patternType);
                int index = current++;
                chopTail(pattern.length(), type);
                current = index;
                return true;
            }
        }
        return false;
    }

    boolean chopLastTail(char pattern, IElementType patternType, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.lastIndexOf(pattern);
        if (pos >= 0) {
            chopTail(pos, patternType);
            int index = current++;
            chopTail(1, type);
            current = index;
            return true;
        }
        return false;
    }

    boolean chopTailNotEmpty(String pattern, IElementType patternType, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        if (pos >= 0) {
            int index = current;
            if (chopTailNotEmpty(pos, patternType)) current++;
            chopTailNotEmpty(pattern.length(), type);
            current = index;
            return true;
        }
        return false;
    }

    public void chopMarkers(int openLen, IElementType openType, int closeLen, IElementType closeType) {
        chopLead(openLen, openType);
        chopTail(-closeLen, closeType);
    }

    public void chopMarkers(int len, IElementType openType, IElementType closeType) {
        chopLead(len, openType);
        chopTail(-len, closeType);
    }

    public void chopMiddle(int lenOpen, int lenClose, IElementType middleType) {
        IElementType type = syntheticNodes.get(current).type;
        chopTail(lenOpen, middleType);
        next();
        chopTail(-lenClose, type);
    }

    public void chopMiddle(int lenOpen, String closePattern, IElementType middleType) {
        IElementType type = syntheticNodes.get(current).type;
        chopTail(lenOpen, middleType);
        next();
        chopLastTail(closePattern, type);
    }

    public void chopMiddle(int lenOpen, char closePattern, IElementType middleType) {
        IElementType type = syntheticNodes.get(current).type;
        chopTail(lenOpen, middleType);
        next();
        chopLastTail(closePattern, type);
    }

    public void chopMiddle(String openPattern, String closePattern, IElementType middleType) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        IElementType type = oldNode.type;
        int pos = oldNode.indexOf(openPattern);
        if (pos >= 0) {
            chopTail(pos + openPattern.length(), middleType);
            next();
        }
        chopLastTail(closePattern, type);
    }

    public void chopMiddle(char openPattern, char closePattern, IElementType middleType) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        IElementType type = oldNode.type;
        int pos = oldNode.indexOf(openPattern);
        if (pos >= 0) {
            chopTail(pos + 1, middleType);
            next();
        }
        chopLastTail(closePattern, type);
    }

    public void trim(IElementType type, char... pattern) {
        trimLead(type, pattern);
        trimTail(type, pattern);
    }

    public static boolean inTypes(IElementType type, IElementType[] types) {
        for (IElementType inType : types) {
            if (inType == type) return true;
        }
        return false;
    }

    public void dropWhiteSpace(IElementType... types) {
        for (SyntheticNode node : syntheticNodes) {
            if (inTypes(node.type, types)) {
                node.dropLead();
                node.dropTail();
            }
        }
    }

    public void trimToNodeType(IElementType... types) {
        for (int i = 0; i < syntheticNodes.size(); i++) {
            SyntheticNode node = syntheticNodes.get(i);

            if (inTypes(node.type, types)) {
                SyntheticNode newNode = node.trimLead();
                if (newNode != null) {
                    syntheticNodes.add(current, newNode);
                    i++;
                }

                newNode = node.trimTail();
                if (newNode != null) {
                    syntheticNodes.add(current+1, newNode);
                    i++;
                }
            }
        }
    }
}
