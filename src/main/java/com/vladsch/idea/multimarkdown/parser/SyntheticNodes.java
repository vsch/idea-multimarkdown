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
import org.pegdown.ast.TextNode;

import java.util.ArrayList;

public class SyntheticNodes {
    final protected TextNode node;
    final protected ArrayList<SyntheticNode> syntheticNodes = new ArrayList<SyntheticNode>();
    protected int current;
    protected int textStart;
    protected int textEnd;
    protected int nodeRange;

    public ArrayList<SyntheticNode> getNodes() {
        return syntheticNodes;
    }

    public SyntheticNodes(TextNode node, IElementType type) {
        this.node = node;
        nodeRange = node.getEndIndex() - node.getStartIndex();
        textStart = 0;
        textEnd = nodeRange;

        syntheticNodes.add(new SyntheticNode(0, nodeRange, type));
        current = 0;
    }

    public SyntheticNodes(TextNode node, IElementType type, int len, IElementType openType, IElementType closeType) {
        this(node, type, len, openType, len, closeType);
    }

    public SyntheticNodes(TextNode node, IElementType type, int lenOpen, IElementType openType, int lenClose, IElementType closeType) {
        this.node = node;
        nodeRange = node.getEndIndex() - node.getStartIndex();
        textStart = lenOpen;
        textEnd = nodeRange - lenClose;

        syntheticNodes.add(new SyntheticNode(0, lenOpen, openType));
        syntheticNodes.add(new SyntheticNode(lenOpen, textEnd, type));
        syntheticNodes.add(new SyntheticNode(textEnd, nodeRange, closeType));
        current = 1;
    }

    public class SyntheticNode {
        int startIndex;
        int endIndex;
        IElementType type;

        public int getStartIndex() {
            return node.getStartIndex() + startIndex;
        }

        public int getEndIndex() {
            return node.getStartIndex() + endIndex;
        }

        public IElementType getType() {
            return type;
        }

        public SyntheticNode(int startOffs, int endOffs, IElementType type) {
            assert startOffs >= 0 && startOffs <= nodeRange;
            assert endOffs >= 0 && endOffs <= nodeRange;
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

        protected int indexOf(String pattern) {
            assert startIndex >= textStart && startIndex <= textEnd;

            int pos = node.getText().indexOf(pattern, startIndex);
            return pos < 0 || pos + textStart > endIndex - pattern.length() ? -1 : pos + textStart - startIndex;
        }

        protected int lastIndexOf(String pattern) {
            assert startIndex >= textStart && startIndex <= textEnd;

            int pos = node.getText().lastIndexOf(pattern, endIndex);
            return pos < 0 || pos + textStart < startIndex ? -1 : pos + textStart - startIndex;
        }
    }

    void next() {
        assert current < syntheticNodes.size() - 1;
        current++;
    }

    void prev() {
        assert current >= 1;
        current--;
    }

    void current(int current) {
        assert current >= 0 && current < syntheticNodes.size();
        this.current = current;
    }

    int current() {
        return current;
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

    boolean chopLeadNotEmpty(String pattern, IElementType type) {
        SyntheticNode oldNode = syntheticNodes.get(current);
        int pos = oldNode.indexOf(pattern);
        return pos >= 0 && chopLeadNotEmpty(pos, type);
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
}
