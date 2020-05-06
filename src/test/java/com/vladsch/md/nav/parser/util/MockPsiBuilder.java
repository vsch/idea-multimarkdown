/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.md.nav.parser.util;

import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.psi.util.MdTypes;

import java.util.ArrayList;

public class MockPsiBuilder {
    final private BasedSequence myChars;
    final private ArrayList<Lexeme> myLexemes;
    final private int myEndOffset;
    final private Marker myRootMarker;

    private int myOffset;
    private Marker myCurrentMarker;

    public MockPsiBuilder(BasedSequence chars, ArrayList<Lexeme> lexemes) {
        myChars = chars;
        myLexemes = lexemes;
        myOffset = 0;

        int endOffset = 0;
        if (myLexemes.size() > 0) {
            endOffset = myLexemes.get(myLexemes.size() - 1).getEnd();
        }

        if (endOffset != myChars.length()) {
            throw new IllegalStateException("Last lexeme end offset and chars length do not match");
        }

        myEndOffset = endOffset;
        myRootMarker = new Marker(this, null, 0);
        myCurrentMarker = myRootMarker;
    }

    public IElementType getTokenType() {
        return myOffset < myLexemes.size() ? myLexemes.get(myOffset).myType : null;
    }

    public Marker mark() {
        myCurrentMarker = new Marker(this, myCurrentMarker, myOffset);
        return myCurrentMarker;
    }

    void markerDone(Marker marker) {
        myCurrentMarker = marker.getParentMarker();
    }

    public int getCurrentOffset() {
        return myOffset < myLexemes.size() ? myLexemes.get(myOffset).myStart : myEndOffset;
    }

    public boolean eof() {
        return myOffset >= myLexemes.size();
    }

    public void advanceLexer() {
        if (myOffset < myLexemes.size()) {
            myOffset++;
        }
    }

    public int getOffset() {
        return myOffset;
    }

    public LexParserNode buildTree(BasedSequence source) {
        LexParserNode rootNode = new LexParserNode(source, "");

        myRootMarker.done(MdTypes.FILE);
        buildMarker(myRootMarker, rootNode);
        return (LexParserNode) rootNode.getFirstChild().getFirstChild();
    }

    private void buildMarker(Marker marker, LexParserNode parent) {
        int start = marker.getStartOffset();
        int end = marker.getEndOffset();
        int startOffset = myLexemes.get(start).getStart();
        int endOffset = start < end ? myLexemes.get(end - 1).getEnd() : myLexemes.get(start).getStart();
        LexParserNode node = new LexParserNode(myChars.subSequence(startOffset, endOffset), marker.getType().toString());
        parent.appendChild(node);

        int nextChild = 0;
        ArrayList<Marker> children = marker.getChildren();

        while (start < end) {
            int childStart = children != null && nextChild < children.size() ? children.get(nextChild).getStartOffset() : Integer.MAX_VALUE;

            if (start < childStart || children == null) {
                // not claimed by child, we add it as PsiLeaf
                Lexeme lexeme = myLexemes.get(start);
                LexParserNode childNode = new LexParserNode(myChars.subSequence(lexeme.getStart(), lexeme.getEnd()), "Leaf:" + lexeme.getType().toString());
                node.appendChild(childNode);
                start++;
            } else {
                Marker childMarker = children.get(nextChild);
                buildMarker(childMarker, node);
                start = childMarker.getEndOffset();
                nextChild++;
            }
        }

        node.setCharsFromContent();
    }

    public static class Marker {
        final private MockPsiBuilder myBuilder;
        final private Marker myParentMarker;
        final private int myStartOffset;
        private int myEndOffset;
        private ArrayList<Marker> myChildren;
        private IElementType myType;

        public Marker(MockPsiBuilder builder, Marker parentMarker, int startOffset) {
            myBuilder = builder;
            myParentMarker = parentMarker;
            myStartOffset = startOffset;
            myChildren = null;
            myType = null;
        }

        public void addChild(Marker marker) {
            if (myChildren == null) myChildren = new ArrayList<>();
            myChildren.add(marker);
        }

        public void done(IElementType type) {
            myEndOffset = myBuilder.getOffset();
            myType = type;
            if (myParentMarker != null) myParentMarker.addChild(this);
            myBuilder.markerDone(this);
        }

        public Marker getParentMarker() {
            return myParentMarker;
        }

        public int getStartOffset() {
            return myStartOffset;
        }

        public int getEndOffset() {
            return myEndOffset;
        }

        public ArrayList<Marker> getChildren() {
            return myChildren;
        }

        public IElementType getType() {
            return myType;
        }
    }
}
